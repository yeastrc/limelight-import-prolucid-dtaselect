package org.yeastrc.limelight.xml.prolucid.utils;

public class DecoyUtils {

    public static boolean isProteinNameDecoy(String proteinName) {
        proteinName = proteinName.toLowerCase();

        return proteinName.startsWith("reverse") || proteinName.startsWith("shuffle") || proteinName.startsWith("random") || proteinName.startsWith("decoy");
    }

}
