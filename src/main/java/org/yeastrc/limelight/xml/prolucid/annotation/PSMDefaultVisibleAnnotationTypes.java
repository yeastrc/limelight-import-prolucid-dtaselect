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

package org.yeastrc.limelight.xml.prolucid.annotation;

import org.yeastrc.limelight.limelight_import.api.xml_dto.SearchAnnotation;
import org.yeastrc.limelight.xml.prolucid.constants.Constants;

import java.util.ArrayList;
import java.util.List;

public class PSMDefaultVisibleAnnotationTypes {

	/**
	 * Get the default visible annotation types for Magnum data
	 * @return
	 */
	public static List<SearchAnnotation> getDefaultVisibleAnnotationTypes() {
		List<SearchAnnotation> annotations = new ArrayList<>();

		{
			SearchAnnotation annotation = new SearchAnnotation();
			annotation.setAnnotationName( PSMAnnotationTypes.ANNOTATION_TYPE_XCORR );
			annotation.setSearchProgram( Constants.PROGRAM_NAME_PROLUCID);
			annotations.add( annotation );
		}

		{
			SearchAnnotation annotation = new SearchAnnotation();
			annotation.setAnnotationName( PSMAnnotationTypes.ANNOTATION_TYPE_DELTACN );
			annotation.setSearchProgram( Constants.PROGRAM_NAME_PROLUCID);
			annotations.add( annotation );
		}


		return annotations;
	}
	
}
