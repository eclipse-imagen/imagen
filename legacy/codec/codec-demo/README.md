# ImageN Media Format Demo

The source files and run scripts for FormatDemo are in the codec
subdirectory of the sample directory.

FormatDemo tests a given input file to determine its format.  The
formats recognized include the standard JAI formats as well as the
"samplepnm" format whose implementation is provided in the same
directory.  The "samplepnm" format is identical to PNM and is renamed
to distinguish it from the standard implementation embedded into JAI.

The source files SamplePNMCodec.java, SamplePNMImageEncoder.java,
SamplePNMImageEncoder.java, and SimpleRenderedImage.java together
implement the "samplepnm" format.  They are intended to demonstrate
how to write a file format codec.  They are based on, but not
identical to, the PNM sources in the JAI implementation.  The code in
these files may be used freely.

FormatDemo may be run from within the codec subdirectory using the
runjai.sh script on Solaris, or runjai.bat on Windows 95/98/NT.