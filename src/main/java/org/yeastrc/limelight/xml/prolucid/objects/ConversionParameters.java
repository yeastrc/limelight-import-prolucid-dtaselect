package org.yeastrc.limelight.xml.prolucid.objects;

import java.io.File;

public class ConversionParameters {


    public File getMzidFile() {
        return mzidFile;
    }

    public String getOutputFilePath() {
        return outputFilePath;
    }

    public ConversionProgramInfo getConversionProgramInfo() {
        return conversionProgramInfo;
    }

    public File getFastaFile() {
        return fastaFile;
    }

    @Override
    public String toString() {
        return "ConversionParameters{" +
                "mzidFile=" + mzidFile +
                ", fastaFile=" + fastaFile +
                ", outputFilePath='" + outputFilePath + '\'' +
                ", conversionProgramInfo=" + conversionProgramInfo +
                '}';
    }

    public ConversionParameters(File mzidFile, File fastaFile, String outputFilePath, ConversionProgramInfo conversionProgramInfo) {
        this.mzidFile = mzidFile;
        this.fastaFile = fastaFile;
        this.outputFilePath = outputFilePath;
        this.conversionProgramInfo = conversionProgramInfo;
    }


    private File mzidFile;
    private File fastaFile;
    private String outputFilePath;
    private ConversionProgramInfo conversionProgramInfo;

}
