package org.yeastrc.limelight.xml.prolucid.builder;

import org.yeastrc.limelight.limelight_import.api.xml_dto.LimelightInput;
import org.yeastrc.limelight.limelight_import.api.xml_dto.MatchedProtein;
import org.yeastrc.limelight.limelight_import.api.xml_dto.MatchedProteinLabel;
import org.yeastrc.limelight.limelight_import.api.xml_dto.MatchedProteins;
import org.yeastrc.limelight.xml.prolucid.objects.ProLuCIDProtein;
import org.yeastrc.limelight.xml.prolucid.objects.ProLuCIDResults;

import java.math.BigInteger;
import java.util.Collection;

/**
 * Build the MatchedProteins section of the limelight XML docs. This is done by finding all proteins in the FASTA
 * file that contains any of the peptide sequences found in the experiment. 
 * 
 * This is generalized enough to be usable by any pipeline
 * 
 * @author mriffle
 *
 */
public class MatchedProteinsBuilder {

	public static MatchedProteinsBuilder getInstance() { return new MatchedProteinsBuilder(); }


	public void buildMatchedProteins(LimelightInput limelightInputRoot, ProLuCIDResults results) throws Exception {

		MatchedProteins xmlMatchedProteins = new MatchedProteins();
		limelightInputRoot.setMatchedProteins( xmlMatchedProteins );

		for( String proteinSequence : results.getProteinSequenceCountIdMap().keySet() ) {

			MatchedProtein xmlProtein = new MatchedProtein();
			xmlMatchedProteins.getMatchedProtein().add( xmlProtein );

			int proteinSequenceCountId = results.getProteinSequenceCountIdMap().get(proteinSequence);

			xmlProtein.setId( BigInteger.valueOf(proteinSequenceCountId));
			xmlProtein.setSequence(proteinSequence);

			Collection<String> uniqueProteinIds = results.getProteinSequenceCountIdUniqueProteinIdMap().get(proteinSequenceCountId);

			for(String proteinId : uniqueProteinIds) {
				ProLuCIDProtein protein = results.getProteinIdProteinMap().get(proteinId);

				for (ProLuCIDProtein.Annotation anno : protein.getAnnotations()) {
					MatchedProteinLabel xmlMatchedProteinLabel = new MatchedProteinLabel();
					xmlProtein.getMatchedProteinLabel().add(xmlMatchedProteinLabel);

					xmlMatchedProteinLabel.setName(anno.getName());

					if (anno.getDescription() != null)
						xmlMatchedProteinLabel.setDescription(anno.getDescription());
				}
			}
		}
	}
	
}
