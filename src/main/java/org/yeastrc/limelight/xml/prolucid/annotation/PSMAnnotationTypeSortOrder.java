package org.yeastrc.limelight.xml.prolucid.annotation;

import org.yeastrc.limelight.limelight_import.api.xml_dto.SearchAnnotation;
import org.yeastrc.limelight.xml.prolucid.constants.Constants;

import java.util.ArrayList;
import java.util.List;

public class PSMAnnotationTypeSortOrder {

	public static List<SearchAnnotation> getPSMAnnotationTypeSortOrder() {
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
