#!/bin/sh
#
# A script file to run the demo.
#

if [ $# = 0 ]
then
IMAGES="../images/*.*"
else
IMAGES=$*
fi

LD_LIBRARY_PATH=../../lib:$LD_LIBRARY_PATH
export LD_LIBRARY_PATH
JARPATH=../../lib/jai_core.jar:../../lib/jai_codec.jar

java -Xmx48m -classpath $JARPATH:. FormatDemo $IMAGES
