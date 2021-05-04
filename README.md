# ProLuCID + DTASelect to limelight XML Converter


Use this program to convert the results of a ProLuCID + DTASelect
search into Limelight XML suitable for import into Limelight.

## How To Run
There are two ways to run this converter. You can download it and run it using Java or you can run it using Docker.

This program uses the mzIdentML (.mzID) file format for ProLuCID + DTASelect results. The FASTA file for
the search that generated the .mzID file is also required.

### Run as Java program

1. Download the [latest release](https://github.com/yeastrc/limelight-import-prolucid-dtaselect/releases).
2. Run the program ``java -jar prolucidToLimelightXML.jar`` with no arguments to see the possible parameters. Requires Java 8.

#### Command Line Instructions

```
java -jar prolucidToLimelightXML.jar [-hvV] -f=<fastaFile> -m=<mzidFile>
                                     -o=<outFile>

Description:

Convert the results of a ProLuCID + DTASelect analysis to a Limelight XML file
suitable for import into Limelight.

More info at: https://github.com/yeastrc/limelight-import-prolucid-dtaselect

Options:
  -m, --mzid=<mzidFile>      Full path to the location of the mzIdentML file (.mzid).
  -f, --fasta=<fastaFile>    Full path to the location of the FASTA file.
  -o, --out-file=<outFile>   Full path to use for the Limelight XML output file
                               (including file name).
  -v, --verbose              If this parameter is present, error messages will
                               include a full stacktrace. Helpful for debugging.
  -h, --help                 Show this help message and exit.
  -V, --version              Print version information and exit.
```

#### For example:

```
java -jar prolucidToLimelightXML.jar -m .\my_yeast_data.mzid -f C:\fasta_files\yeast.fasta -o .\yeast_results.limelight.xml -v
```

### Run using Docker
To run this program using Docker (which doesn't require installing Java), you must first have Docker
installed. Please see our [Docker Installation Guide](https://limelight-ms.readthedocs.io/en/latest/tutorials/install-docker.html).

Once Docker is installed, this converter can be run on the Linux command line (including on Windows using WSL2) using:

```
sudo docker run --rm -it --user $(id -u):$(id -g) -v `pwd`:`pwd` -w `pwd` mriffle/limelight-import-prolucid-dtaselect \
 -m /path/to/my_yeast_data.mzid -f /path/to/yeast.fasta -o /path/to/yeast_results.limelight.xml -v
```

Substitute `/path/to/my_yeast_data.mzid`, `/path/to/yeast.fasta`, and `/path/to/yeast_results.limelight.xml` to your
actual directories. This will create a file at `/path/to/yeast_results.limelight.xml` with your results formatted
as Limelight XML.
