package org.yeastrc.limelight.xml.prolucid.builder;

import org.yeastrc.limelight.limelight_import.api.xml_dto.*;
import org.yeastrc.limelight.limelight_import.api.xml_dto.SearchProgram.PsmAnnotationTypes;
import org.yeastrc.limelight.limelight_import.create_import_file_from_java_objects.main.CreateImportFileFromJavaObjectsMain;
import org.yeastrc.limelight.xml.prolucid.annotation.PSMAnnotationTypeSortOrder;
import org.yeastrc.limelight.xml.prolucid.annotation.PSMAnnotationTypes;
import org.yeastrc.limelight.xml.prolucid.annotation.PSMDefaultVisibleAnnotationTypes;
import org.yeastrc.limelight.xml.prolucid.constants.Constants;
import org.yeastrc.limelight.xml.prolucid.objects.*;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

public class XMLBuilder {

	public void buildAndSaveXML(ConversionParameters conversionParameters,
								ProLuCIDResults results)
    throws Exception {

		LimelightInput limelightInputRoot = new LimelightInput();

		limelightInputRoot.setFastaFilename( results.getSearchDatabase() );

		// add in the conversion program (this program) information
		ConversionProgramBuilder.createInstance().buildConversionProgramSection( limelightInputRoot, conversionParameters);

		SearchProgramInfo searchProgramInfo = new SearchProgramInfo();
		limelightInputRoot.setSearchProgramInfo( searchProgramInfo );

		SearchPrograms searchPrograms = new SearchPrograms();
		searchProgramInfo.setSearchPrograms( searchPrograms );

		{
			SearchProgram searchProgram = new SearchProgram();
			searchPrograms.getSearchProgram().add( searchProgram );

			searchProgram.setName( Constants.PROGRAM_NAME_PROLUCID);
			searchProgram.setDisplayName( Constants.PROGRAM_NAME_PROLUCID);
			searchProgram.setVersion(results.getProlucidVersion());


			//
			// Define the annotation types present in tide data
			//
			PsmAnnotationTypes psmAnnotationTypes = new PsmAnnotationTypes();
			searchProgram.setPsmAnnotationTypes( psmAnnotationTypes );

			FilterablePsmAnnotationTypes filterablePsmAnnotationTypes = new FilterablePsmAnnotationTypes();
			psmAnnotationTypes.setFilterablePsmAnnotationTypes( filterablePsmAnnotationTypes );

			for( FilterablePsmAnnotationType annoType : PSMAnnotationTypes.getFilterablePsmAnnotationTypes() ) {
				filterablePsmAnnotationTypes.getFilterablePsmAnnotationType().add( annoType );
			}
		}

		{
			SearchProgram searchProgram = new SearchProgram();
			searchPrograms.getSearchProgram().add( searchProgram );

			searchProgram.setName( Constants.PROGRAM_NAME_DTASELECT);
			searchProgram.setDisplayName( Constants.PROGRAM_NAME_DTASELECT);
			searchProgram.setVersion(results.getDtaselectVersion());
		}


		//
		// Define which annotation types are visible by default
		//
		DefaultVisibleAnnotations xmlDefaultVisibleAnnotations = new DefaultVisibleAnnotations();
		searchProgramInfo.setDefaultVisibleAnnotations( xmlDefaultVisibleAnnotations );

		VisiblePsmAnnotations xmlVisiblePsmAnnotations = new VisiblePsmAnnotations();
		xmlDefaultVisibleAnnotations.setVisiblePsmAnnotations( xmlVisiblePsmAnnotations );

		for( SearchAnnotation sa : PSMDefaultVisibleAnnotationTypes.getDefaultVisibleAnnotationTypes() ) {
			xmlVisiblePsmAnnotations.getSearchAnnotation().add( sa );
		}

		//
		// Define the default display order in proxl
		//
		AnnotationSortOrder xmlAnnotationSortOrder = new AnnotationSortOrder();
		searchProgramInfo.setAnnotationSortOrder( xmlAnnotationSortOrder );

		PsmAnnotationSortOrder xmlPsmAnnotationSortOrder = new PsmAnnotationSortOrder();
		xmlAnnotationSortOrder.setPsmAnnotationSortOrder( xmlPsmAnnotationSortOrder );

		for( SearchAnnotation xmlSearchAnnotation : PSMAnnotationTypeSortOrder.getPSMAnnotationTypeSortOrder() ) {
			xmlPsmAnnotationSortOrder.getSearchAnnotation().add( xmlSearchAnnotation );
		}

		//
		// Define the static mods
		//
		Map<String, BigDecimal> staticMods = results.getStaticMods();
		if(staticMods.size() > 0) {

			StaticModifications smods = new StaticModifications();
			limelightInputRoot.setStaticModifications( smods );

			for( String residue : staticMods.keySet() ) {

				StaticModification xmlSmod = new StaticModification();
				xmlSmod.setAminoAcid( residue );
				xmlSmod.setMassChange( staticMods.get(residue) );

				smods.getStaticModification().add( xmlSmod );
			}
		}

		//
		// Build MatchedProteins section and get map of protein names to MatchedProtein ids
		//
		MatchedProteinsBuilder.getInstance().buildMatchedProteins(
				limelightInputRoot,
				results
		);


		//
		// Define the peptide and PSM data
		//
		ReportedPeptides reportedPeptides = new ReportedPeptides();
		limelightInputRoot.setReportedPeptides( reportedPeptides );

		// iterate over each distinct reported peptide
		for( ProLuCIDReportedPeptide proLuCIDReportedPeptide : results.getPeptidePSMMap().keySet() ) {

			// skip this if it only contains decoys
			if(!peptideHasProteins(proLuCIDReportedPeptide, results)) {
				continue;
			}

			String reportedPeptideString = proLuCIDReportedPeptide.getReportedPeptideString();

			ReportedPeptide xmlReportedPeptide = new ReportedPeptide();
			reportedPeptides.getReportedPeptide().add( xmlReportedPeptide );

			xmlReportedPeptide.setReportedPeptideString( reportedPeptideString );
			xmlReportedPeptide.setSequence( proLuCIDReportedPeptide.getNakedPeptide() );

			MatchedProteinsForPeptide xProteinsForPeptide = new MatchedProteinsForPeptide();
			xmlReportedPeptide.setMatchedProteinsForPeptide( xProteinsForPeptide );

			// add in protein inference info
			for( Integer sequenceCountId : this.getSequenceCountIdsForReportedPeptide(results, proLuCIDReportedPeptide) ) {

					MatchedProteinForPeptide xProteinForPeptide = new MatchedProteinForPeptide();
					xProteinsForPeptide.getMatchedProteinForPeptide().add(xProteinForPeptide);

					xProteinForPeptide.setId(BigInteger.valueOf(sequenceCountId));
			}

			// add in the mods for this peptide
			if( proLuCIDReportedPeptide.getMods() != null && proLuCIDReportedPeptide.getMods().keySet().size() > 0 ) {

				PeptideModifications xmlModifications = new PeptideModifications();
				xmlReportedPeptide.setPeptideModifications( xmlModifications );

				for( int position : proLuCIDReportedPeptide.getMods().keySet() ) {

					PeptideModification xmlModification = new PeptideModification();
					xmlModifications.getPeptideModification().add( xmlModification );

					xmlModification.setMass( proLuCIDReportedPeptide.getMods().get( position ) );

					if( position == 0) {

						xmlModification.setIsNTerminal( true );

					} else if( position == proLuCIDReportedPeptide.getNakedPeptide().length() + 1 ) {

						xmlModification.setIsCTerminal( true );

					} else {
						xmlModification.setPosition( BigInteger.valueOf( position ) );
					}
				}
			}


			// add in the PSMs and annotations
			Psms xmlPsms = new Psms();
			xmlReportedPeptide.setPsms( xmlPsms );

			// iterate over all PSMs for this reported peptide

			for( ProLuCIDPSM psm : results.getPeptidePSMMap().get(proLuCIDReportedPeptide) ) {

				Psm xmlPsm = new Psm();
				xmlPsms.getPsm().add( xmlPsm );

				xmlPsm.setScanNumber( new BigInteger( String.valueOf( psm.getScanNumber() ) ) );
				xmlPsm.setPrecursorCharge( new BigInteger( String.valueOf( psm.getCharge() ) ) );
				xmlPsm.setPrecursorMZ(psm.getObservedMoverZ());

				// add in the filterable PSM annotations (e.g., score)
				FilterablePsmAnnotations xmlFilterablePsmAnnotations = new FilterablePsmAnnotations();
				xmlPsm.setFilterablePsmAnnotations( xmlFilterablePsmAnnotations );

				// handle psm scores

				{
					FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
					xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add( xmlFilterablePsmAnnotation );

					xmlFilterablePsmAnnotation.setAnnotationName( PSMAnnotationTypes.ANNOTATION_TYPE_XCORR );
					xmlFilterablePsmAnnotation.setSearchProgram( Constants.PROGRAM_NAME_PROLUCID);
					xmlFilterablePsmAnnotation.setValue( psm.getXcorr().setScale(4, RoundingMode.HALF_UP));
				}

				{
					FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
					xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add( xmlFilterablePsmAnnotation );

					xmlFilterablePsmAnnotation.setAnnotationName( PSMAnnotationTypes.ANNOTATION_TYPE_DELTACN );
					xmlFilterablePsmAnnotation.setSearchProgram( Constants.PROGRAM_NAME_PROLUCID);
					xmlFilterablePsmAnnotation.setValue( psm.getDeltaCn().setScale(4, RoundingMode.HALF_UP));
				}

				{
					FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
					xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add( xmlFilterablePsmAnnotation );

					xmlFilterablePsmAnnotation.setAnnotationName( PSMAnnotationTypes.ANNOTATION_TYPE_MASSDIFF );
					xmlFilterablePsmAnnotation.setSearchProgram( Constants.PROGRAM_NAME_PROLUCID);
					xmlFilterablePsmAnnotation.setValue( psm.getMassDiff());
				}

			}// end iterating over psms for a reported peptide

		}//end iterating over reported peptides


		//make the xml file
		CreateImportFileFromJavaObjectsMain.getInstance().createImportFileFromJavaObjectsMain( new File(conversionParameters.getOutputFilePath() ), limelightInputRoot);

	}

	private Collection<Integer> getSequenceCountIdsForReportedPeptide(ProLuCIDResults proLuCIDResults, ProLuCIDReportedPeptide proLuCIDReportedPeptide) throws Exception {

		Collection<Integer> sequenceCountIds = new HashSet<>();

		for( String proteinId : proLuCIDReportedPeptide.getProteinMatches() ) {
			if(!proLuCIDResults.getProteinUniqueIdSequenceCountIdMap().containsKey(proteinId)) {
				throw new Exception("Could not find a sequence count id for protein: " + proteinId);
			}

			sequenceCountIds.add(proLuCIDResults.getProteinUniqueIdSequenceCountIdMap().get(proteinId));
		}

		if(sequenceCountIds.size() < 1) {
			throw new Exception("Could not find a protein sequence for peptide: " + proLuCIDReportedPeptide);
		}

		return sequenceCountIds;
	}

	private boolean peptideHasProteins(ProLuCIDReportedPeptide proLuCIDReportedPeptide, ProLuCIDResults results) {

		for( String proteinId : proLuCIDReportedPeptide.getProteinMatches() ) {
			if(results.getProteinIdProteinMap().containsKey( proteinId ) ) {
				return true;
			}
		}

		return false;
	}
	
}
