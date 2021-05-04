/*
 * Original author: Michael Riffle <mriffle .at. uw.edu>
 *                  
 * Copyright 2018 University of Washington - Seattle, WA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.yeastrc.limelight.xml.prolucid.main;

import org.yeastrc.limelight.xml.prolucid.builder.XMLBuilder;
import org.yeastrc.limelight.xml.prolucid.objects.ConversionParameters;
import org.yeastrc.limelight.xml.prolucid.objects.ProLuCIDResults;
import org.yeastrc.limelight.xml.prolucid.reader.FASTASequenceLoader;
import org.yeastrc.limelight.xml.prolucid.reader.ProLuCIDResultsReader;

import java.io.File;
import java.util.Map;

public class ConverterRunner {

	// conveniently get a new instance of this class
	public static ConverterRunner createInstance() { return new ConverterRunner(); }
	
	
	public void convertToLimelightXML(ConversionParameters conversionParameters ) throws Throwable {

		System.err.println( "\nLoading ProLuCID results into memory...");
		ProLuCIDResults results = ProLuCIDResultsReader.getResults( conversionParameters.getMzidFile() );

		System.err.print( "\nProcessing FASTA file, matching protein IDs to sequences..." );
		FASTASequenceLoader.getProteinSequenceUniqueIdMap(results, conversionParameters);
		System.err.println( " Done." );

		System.err.print( "\nWriting out XML..." );
		(new XMLBuilder()).buildAndSaveXML(conversionParameters, results);
		System.err.println( " Done." );

		System.err.print( "Validating Limelight XML..." );
		LimelightXMLValidator.validateLimelightXML(new File(conversionParameters.getOutputFilePath()));
		System.err.println( " Done." );

	}
}
