package org.yeastrc.limelight.xml.prolucid.reader;

import org.yeastrc.limelight.xml.prolucid.objects.ConversionParameters;
import org.yeastrc.limelight.xml.prolucid.objects.ProLuCIDProtein;
import org.yeastrc.limelight.xml.prolucid.objects.ProLuCIDResults;
import org.yeastrc.limelight.xml.prolucid.utils.DecoyUtils;
import org.yeastrc.proteomics.fasta.FASTAEntry;
import org.yeastrc.proteomics.fasta.FASTAFileParser;
import org.yeastrc.proteomics.fasta.FASTAFileParserFactory;
import org.yeastrc.proteomics.fasta.FASTAHeader;

import java.util.*;

public class FASTASequenceLoader {


    public static void getProteinSequenceUniqueIdMap(ProLuCIDResults results, ConversionParameters params) throws Throwable {

        // main data structure's we're building
        Map<String, Integer> proteinSequenceCountIdMap = new HashMap<>();
        Map<Integer, Collection<String>> proteinSequenceCountIdUniqueProteinIdMap = new HashMap<>();
        Map<String, Integer> proteinUniqueIdSequenceCountIdMap= new HashMap<>();

        // build a collection of unique ids we need sequences for
        Map<String, String> fastaNameUniqueIdMap = new HashMap<>();
        for( String uniqueId : results.getProteinIdProteinMap().keySet()) {
            for(ProLuCIDProtein.Annotation anno : results.getProteinIdProteinMap().get(uniqueId).getAnnotations()) {
                fastaNameUniqueIdMap.put(anno.getName(), uniqueId);
            }
        }

        int proteinSequenceCountId = 0;
        try ( FASTAFileParser parser = FASTAFileParserFactory.getInstance().getFASTAFileParser(  params.getFastaFile() ) ) {

            for (FASTAEntry entry = parser.getNextEntry(); entry != null; entry = parser.getNextEntry() ) {

                for(FASTAHeader header : entry.getHeaders()) {

                    final String name = header.getName();
                    if(DecoyUtils.isProteinNameDecoy(name)) {
                        continue;
                    }

                    proteinSequenceCountId++;

                    final List<String> mzIDNamesMatchingFASTAName = FASTASequenceLoader.findmzIDNamesMatchingFASTAName(name, fastaNameUniqueIdMap.keySet());

                    if(mzIDNamesMatchingFASTAName.size() > 1) {
                        throw new Exception("Found more than one distinct protein entry in mzID file that matched name: " + name);
                    }

                    if(mzIDNamesMatchingFASTAName.size() == 1) {

                        final String sequence = entry.getSequence();
                        final String uniqueId = fastaNameUniqueIdMap.get(mzIDNamesMatchingFASTAName.get(0));

                        if(!(proteinSequenceCountIdMap.containsKey(sequence))) {
                            proteinSequenceCountIdMap.put(sequence, proteinSequenceCountId);
                        }

                        int uniqueSequenceId = proteinSequenceCountIdMap.get(sequence);

                        if(!(proteinSequenceCountIdUniqueProteinIdMap.containsKey(uniqueSequenceId))) {
                            proteinSequenceCountIdUniqueProteinIdMap.put(uniqueSequenceId, new HashSet<>());
                        }

                        proteinSequenceCountIdUniqueProteinIdMap.get(uniqueSequenceId).add(uniqueId);

                        if(proteinUniqueIdSequenceCountIdMap.containsKey(uniqueId)) {
                            throw new Exception("Found more than one sequence for same protein id: " + uniqueId);
                        }
                        proteinUniqueIdSequenceCountIdMap.put(uniqueId, uniqueSequenceId);

                    }
                }

            }
        }

        results.setProteinSequenceCountIdMap(proteinSequenceCountIdMap);
        results.setProteinSequenceCountIdUniqueProteinIdMap(proteinSequenceCountIdUniqueProteinIdMap);
        results.setProteinUniqueIdSequenceCountIdMap(proteinUniqueIdSequenceCountIdMap);
    }

    private static List<String> findmzIDNamesMatchingFASTAName(String nameFromFASTAFile, Set<String> fastaIDsFrommzID) {

        List<String> names = new ArrayList<>();

        for( String mzIdName : fastaIDsFrommzID ) {
            if(nameFromFASTAFile.contains(mzIdName)) {
                names.add(mzIdName);
            }
        }

        return names;
    }

}
