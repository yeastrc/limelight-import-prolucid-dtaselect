package org.yeastrc.limelight.xml.prolucid.reader;

import info.psidev.psi.pi.mzidentml._1.*;
import org.yeastrc.limelight.xml.prolucid.objects.ProLuCIDPSM;
import org.yeastrc.limelight.xml.prolucid.objects.ProLuCIDProtein;
import org.yeastrc.limelight.xml.prolucid.objects.ProLuCIDReportedPeptide;
import org.yeastrc.limelight.xml.prolucid.objects.ProLuCIDResults;
import org.yeastrc.limelight.xml.prolucid.utils.ReportedPeptideUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProLuCIDResultsReader {

    public static ProLuCIDResults getResults(File mzidFile) throws Exception {

        MzIdentMLType mzIdentML = getMzIdentML(mzidFile);

        String prolucidVersion = mzIdentML.getAnalysisSoftwareList().getAnalysisSoftware().get(0).getVersion();
        System.err.println("\tProLuCID version: " + prolucidVersion);

        String dtaselectVersion = mzIdentML.getAnalysisSoftwareList().getAnalysisSoftware().get(1).getVersion();
        System.err.println("\tDTASelect version: " + dtaselectVersion);

        Map<String, BigDecimal> staticMods = getStaticMods(mzIdentML);
        System.err.println("\tFound " + staticMods.size() + " static mods.");

        // A map of peptides parsed from the mzIdentML, keyed by Peptide.id in that file
        Map<String, ProLuCIDReportedPeptide> reportedPeptideMap = getPeptides(mzIdentML, staticMods);
        System.err.println("\tFound " + reportedPeptideMap.size() + " distinct peptide ids.");

        System.err.print("\tReading PSMs... ");
        Map<ProLuCIDReportedPeptide, Collection<ProLuCIDPSM>> psmPeptideMap = getPSMPeptideMap(mzIdentML, reportedPeptideMap);
        System.err.println("Done.");

        System.err.println("Done reading .mzid file.");

        ProLuCIDResults results = new ProLuCIDResults();
        results.setPeptidePSMMap( psmPeptideMap );
        results.setProteinIdProteinMap( getProteinIdProteinMap(mzIdentML) );
        results.setStaticMods( staticMods );
        results.setProlucidVersion( prolucidVersion );
        results.setDtaselectVersion( dtaselectVersion );
        results.setSearchDatabase(getSearchDatabase(mzIdentML));

        return results;
    }

    private static String getSearchDatabase(MzIdentMLType mzIdentML) {

        String searchDatabase = "Unknown";

        try {

            searchDatabase = (new File(mzIdentML.getDataCollection().getInputs().getSearchDatabase().get(0).getLocation()).getName());

        } catch(Throwable t) {
            ;
        }

        return searchDatabase;
    }

    private static Map<ProLuCIDReportedPeptide, Collection<ProLuCIDPSM>> getPSMPeptideMap(MzIdentMLType mzIdentML,
                                                                                          Map<String, ProLuCIDReportedPeptide> reportedPeptideMap) throws Exception {

        Map<ProLuCIDReportedPeptide, Collection<ProLuCIDPSM>> psmPeptideMap = new HashMap<>();
        SpectrumIdentificationListType spectrumIdentificationList = getSpectrumIdentificationList(mzIdentML);

        for(SpectrumIdentificationResultType result : spectrumIdentificationList.getSpectrumIdentificationResult()) {
            int scanNumber = getScanNumberFromSpectrumID(result.getSpectrumID());

            for(SpectrumIdentificationItemType item : result.getSpectrumIdentificationItem()) {

                ProLuCIDReportedPeptide reportedPeptide = reportedPeptideMap.get(item.getPeptideRef());

                // this PSM matches a peptide that didn't map to a target--it's a decoy
                if(reportedPeptide == null) {
                    continue;
                }

                int charge = item.getChargeState();
                BigDecimal obsMZ = BigDecimal.valueOf(item.getExperimentalMassToCharge());
                BigDecimal massDiff = BigDecimal.valueOf(getMassDiff(item.getExperimentalMassToCharge(), item.getCalculatedMassToCharge(), charge)).setScale(4, RoundingMode.HALF_UP);

                BigDecimal xcorr = null;
                BigDecimal deltacn = null;

                for( AbstractParamType cv : item.getParamGroup()) {
                    String name = cv.getName();
                    if(name.equals( "ProLuCID:xcorr" ) ) {
                        xcorr = new BigDecimal(cv.getValue());
                    } else if(name.equals( "ProLuCID:deltacn" ) ) {
                        deltacn = new BigDecimal(cv.getValue());
                    }
                }

                if(xcorr == null) {
                    throw new Exception("Could not find xcorr for ProLuCID PSM " + item.getId());
                }
                if(deltacn == null) {
                    throw new Exception("Could not find deltacn for ProLuCID PSM " + item.getId());
                }

                ProLuCIDPSM psm = new ProLuCIDPSM();
                psm.setCharge(charge);
                psm.setDecoy(false);
                psm.setMassDiff(massDiff);
                psm.setPeptideSequence(reportedPeptide.getNakedPeptide());
                psm.setObservedMoverZ(obsMZ);
                psm.setScanNumber(scanNumber);
                psm.setXcorr(xcorr);
                psm.setDeltaCn(deltacn);

                if(!psmPeptideMap.containsKey(reportedPeptide)) {
                    psmPeptideMap.put(reportedPeptide, new HashSet<>());
                }

                psmPeptideMap.get(reportedPeptide).add(psm);
            }
        }


        return psmPeptideMap;
    }

    private static double getMassDiff(double observedMz, double expectedMz, int charge) {

        double neutralObservedMass = observedMz * charge;
        double neutralExpectedMass = expectedMz * charge;

        return neutralObservedMass - neutralExpectedMass;
    }

    private static int getScanNumberFromSpectrumID(String spectrumID) throws Exception {
        Pattern p = Pattern.compile("^.*scan=(\\d+)$");
        Matcher m = p.matcher(spectrumID);

        if(!m.matches()) {
            throw new Exception("Could not parse scan number from " + spectrumID);
        }

        return Integer.parseInt(m.group(1));
    }

    private static SpectrumIdentificationListType getSpectrumIdentificationList(MzIdentMLType mzIdentML) throws Exception {

        DataCollectionType dataCollection = mzIdentML.getDataCollection();
        if(dataCollection == null) {
            throw new Exception("Could not find DataCollection element.");
        }

        AnalysisDataType analysisData = dataCollection.getAnalysisData();
        if(analysisData == null) {
            throw new Exception("Could not find AnalysisData element.");
        }

        SpectrumIdentificationListType spectrumIdentificationList = analysisData.getSpectrumIdentificationList().get(0);    // assume only one spectrum identification list
        if(spectrumIdentificationList == null) {
            throw new Exception("Could not find SpectrumIdentificationList element.");
        }

        return spectrumIdentificationList;
    }

    private static Map<String, ProLuCIDReportedPeptide> getPeptides(MzIdentMLType mzIdentML, Map<String, BigDecimal> staticMods) throws Exception {
        Map<String, ProLuCIDReportedPeptide> peptideMap = new HashMap<>();
        Map<String, Collection<String>> pepEvidenceMap = getPeptideEvidenceMap(mzIdentML);

        SequenceCollectionType sequenceCollection = getSequenceCollection(mzIdentML);
        for(PeptideType peptide : sequenceCollection.getPeptide()) {

            // this peptide didn't map to any non decoy proteins, skip it
            if(!pepEvidenceMap.containsKey(peptide.getId())) {
                continue;
            }

            ProLuCIDReportedPeptide proLuCIDReportedPeptide = getReportedPeptide(peptide, pepEvidenceMap, staticMods);
            peptideMap.put(peptide.getId(), proLuCIDReportedPeptide);
        }

        return peptideMap;
    }

    private static ProLuCIDReportedPeptide getReportedPeptide(PeptideType peptide, Map<String, Collection<String>> pepEvidenceMap, Map<String, BigDecimal> staticMods) {
        ProLuCIDReportedPeptide reportedPeptide = new ProLuCIDReportedPeptide();
        Map<Integer, BigDecimal> mods = getDynamicMods(peptide, staticMods);

        reportedPeptide.setNakedPeptide(peptide.getPeptideSequence());
        reportedPeptide.setMods(mods);
        reportedPeptide.setReportedPeptideString(ReportedPeptideUtils.getReportedPeptideString(peptide.getPeptideSequence(), mods));
        reportedPeptide.setProteinMatches(pepEvidenceMap.get(peptide.getId()));

        return reportedPeptide;
    }

    private static Map<Integer, BigDecimal> getDynamicMods(PeptideType peptide, Map<String, BigDecimal> staticMods) {
        Map<Integer, BigDecimal> mods = new HashMap<>();

        for(ModificationType mod : peptide.getModification()) {

            String peptideSequence = peptide.getPeptideSequence();
            int position = mod.getLocation();
            String moddedResidue = peptideSequence.substring(position - 1, position);
            BigDecimal moddedMass = BigDecimal.valueOf(mod.getMonoisotopicMassDelta());

            if(!staticMods.containsKey(moddedResidue) || !bigDecimalsAreEqual(staticMods.get(moddedResidue), moddedMass, 3)) {
                mods.put(mod.getLocation(), BigDecimal.valueOf(mod.getMonoisotopicMassDelta()));
            }
        }

        return mods;
    }

    private static boolean bigDecimalsAreEqual(BigDecimal bd1, BigDecimal bd2, int scale) {
        return bd1.setScale(scale, RoundingMode.HALF_UP).equals(bd2.setScale(scale, RoundingMode.HALF_UP));
    }


    private static Map<String, Collection<String>> getPeptideEvidenceMap(MzIdentMLType mzIdentML) throws Exception {
        Map<String, Collection<String>> pepEvidenceMap = new HashMap<>();

        SequenceCollectionType sequenceCollection = getSequenceCollection(mzIdentML);
        for(PeptideEvidenceType peptideEvidence : sequenceCollection.getPeptideEvidence()) {
            // do not include decoys
            if(peptideEvidence.isIsDecoy()) {
                continue;
            }

            String pepRef = peptideEvidence.getPeptideRef();
            String protRef = peptideEvidence.getDBSequenceRef();

            if(!pepEvidenceMap.containsKey(pepRef)) {
                pepEvidenceMap.put(pepRef, new HashSet<>());
            }

            pepEvidenceMap.get(pepRef).add(protRef);
        }

        return pepEvidenceMap;
    }

    private static SequenceCollectionType getSequenceCollection(MzIdentMLType mzIdentML) throws Exception {
        SequenceCollectionType sequenceCollection = mzIdentML.getSequenceCollection();
        if( sequenceCollection == null ) {
            throw new Exception("Did not find SequenceCollection in .mzid file.");
        }

        return sequenceCollection;
    }


    private static Map<String, ProLuCIDProtein> getProteinIdProteinMap(MzIdentMLType mzIdentML) throws Exception {
        Map<String, ProLuCIDProtein> proteinMap = new HashMap<>();
        Map<String, Collection<String>> pepEvidenceMap = getPeptideEvidenceMap(mzIdentML);

        // create a set of non-decoy protein ids that were matched by a peptide
        Collection<String> targetProteins = new HashSet<>();
        for(Collection<String> proteins : pepEvidenceMap.values()) {
            targetProteins.addAll(proteins);
        }

        SequenceCollectionType sequenceCollection = getSequenceCollection(mzIdentML);

        for(DBSequenceType dbSequence : sequenceCollection.getDBSequence()) {
            String name = dbSequence.getName();
            String accession = dbSequence.getAccession();
            String proteinId = dbSequence.getId();

            // skip decoy proteins
            if(!(targetProteins.contains(proteinId))) {
                continue;
            }

            ProLuCIDProtein.Annotation anno = new ProLuCIDProtein.Annotation();
            anno.setDescription( name );
            anno.setName( accession );

            if(!proteinMap.containsKey(proteinId)) {
                proteinMap.put(proteinId, new ProLuCIDProtein(proteinId));
            }

            ProLuCIDProtein metaProtein = proteinMap.get(proteinId);
            metaProtein.getAnnotations().add(anno);

        }

        return proteinMap;
    }

    private static Map<String, BigDecimal> getStaticMods(MzIdentMLType mzIdentML) {
        Map<String, BigDecimal> staticMods = new HashMap<>();

        AnalysisProtocolCollectionType analysisProtocol = mzIdentML.getAnalysisProtocolCollection();
        if(analysisProtocol != null) {
            for( SpectrumIdentificationProtocolType sipt : analysisProtocol.getSpectrumIdentificationProtocol() ) {
                ModificationParamsType modificationParams = sipt.getModificationParams();
                if(modificationParams != null) {
                    for(SearchModificationType searchModification : modificationParams.getSearchModification()) {
                        if(searchModification.isFixedMod()) {
                            BigDecimal massShift = BigDecimal.valueOf( searchModification.getMassDelta() );

                            for(String residue : searchModification.getResidues()) {
                                staticMods.put(residue, massShift);
                            }
                        }
                    }
                }
            }
        }

        return staticMods;
    }

    private static MzIdentMLType getMzIdentML(File mzidFile) throws JAXBException {

        MzIdentMLType mzIdentML = null;

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(MzIdentMLType.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            mzIdentML = (MzIdentMLType)jaxbUnmarshaller.unmarshal( mzidFile );
        } catch (JAXBException e) {
            System.err.println("Error processing mzIdentML file: " + mzidFile.getAbsolutePath());
            throw e;
        }


        return mzIdentML;
    }
}
