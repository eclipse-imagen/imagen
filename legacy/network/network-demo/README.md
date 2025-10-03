# ImageN Network Imaging Demo

The Network Imaging demo has two components: the client and the
server(s). Both the components use a simple GUI to display the results
of imaging operations. In this release, the JAINetworkDemo (client) has
panels for the Scale, Rotate, Convolve, AddConst, Transpose, Gradient,
Blur/Sharpen and Median operators, as well as a set of Dyadic (two
source) operators.

To build using maven:

    mvn clean install

Before release we intend to package up a fat jar for the client and server used by this example.

## ImageN Network Client

*The following details describe the initial code code contribution and need to be updated to reflect maven build process*.

1. The Network Imaging demo is a simple GUI front-end for experimenting
   with Java Advanced Imaging operators in a networked environment. 

   The Network demo client forwards requests for all the imaging operations
   requested of it to the imaging server (JAIRMIServerWrapper) that is the
   second component of this demo. Thus all the image processing is
   performed on the imaging server but the results are displayed on the
   client. In order to facilitate understanding of this networked
   processing, the simple GUI front end for the server displays the tile
   that it has most recently processed, the name of the operation that
   generated that tile as well as  the position (x, y corrdinates) of the
   tile in the tile grid of the resultant image. Thus the top of the server
   GUI displays tiles as they are computed, a label just beneath displays a
   String of the form 

   <OperationName>:tile(<tileX>, <tileY>). 

   Note that the client display requests tiles from the server, at which
   point the server computes the tiles, displays them in its simple GUI and
   then returns the requested tile to the client. The client then displays
   the tile in its display. The simple GUI for the server also includes a
   text area that displays text messages about the major operations that
   are taking place on the server as operations are being specified and
   tiles are being requested by the client.

   Please refer to the README in the server subdirectory for a more
   detailed explanation of the two servers used by this demo application.

   JAINetworkDemo may be run using the runNetworkDemo.sh script on Solaris,
   or runNetworkDemo.bat on Windows 95/98/NT. For detailed instructions on
   running the demo, please refer to Section 4.
   
2. Setting PATH, CLASSPATH and LD_LIBRARY_PATH settings

   If JAI is installed as part of the Java 2 SDK's Java Runtime
   Environment, no further settings are required. When you encounter
   problems building or running JAI applications, it is most likely that
   you are not running the correct version of java (jdk). Check with the
   command "java -version" on Solaris or at Dos prompt. If needed, set the
   PATH correctly to use the correct version of JDK or JRE as follows :

   For Solaris: 

   % setenv PATH $JDK1_3/bin:$PATH;    or
   % setenv PATH $JRE1_3/bin:$PATH;  

   For Windows: 

   To set PATH environment variable, you may need to edit autoexec.bat on
   Windows 98/95. For other Windows systems, try click
   Start-Settings-ControlPanel, then double click on System. Choose
   Environment. Under System Variables, click on Path, and update its value
   to put 

      c:\Program Files\JavaSoft\jre\1.3\bin;     or
      c:\jdk1.3\jre\bin;     or 
      c:\jdk1.3\bin;

   ahead of others. Note that ";" is used to separate from other values. To
   double check, open a DOS command prompt and use "set" or "set path" to
   view the PATH environment variable. 

   If you have used the CLASSPATH install into arbitrary location either for
   Solaris or Windows, then you will need to set an environment variable
   named JAI to point to the directory where the jai jar files are
   installed. Additionally on Solaris, the LD_LIBRARY_PATH variable also
   needs to be set.

   On Solaris:

   % setenv JAI $JAIDIR/jai-1_1_1/lib
   % setenv LD_LIBRARY_PATH .:$JAI:$LD_LIBRARY_PATH

   On Windows;

     set JAI c:\jai-1_1_1\lib

3. Building the sample demo

   The two components of the demo, the server and the client have to be
   built separately. To build the server, change directory to the server
   subdirectory and follow the instructions outlined in the README in that
   directory.  In order to build the client, ensure PATH and CLASSPATH
   settings are made as outlined above and simply run make.

## ImageN Network Server

*The following details describe the initial code code contribution and need to be updated to reflect maven build process*.

1. This directory contains the sample source code for the server that is
   used in the JAINetworkDemo network imaging demo application. 

   It may be noted that the server provided (JAIRMIServerWrapper) is simply
   a wrapper around the ImageNRMIImageServer class that is provided in the JAI
   distribution as the server that implements the "jairmi" protocol. The
   JAIRMIServerWrapper class simply intercepts all calls in order to
   display images and text to the demo server GUI before forwarding the
   call to the actual ImageNRMIImageServer server instance that performs the
   image processing.

   The DirectoryListerServerImpl class is a server that provides the
   listing of the all the files present in an directory. It is used in
   conjunction with the ImageFilenameFilterImpl class to send the names of
   all the image files present in a specified directory to the client so
   that these names can be displayed in the client application GUI.

2. Setting PATH, CLASSPATH and LD_LIBRARY_PATH settings

   If JAI is installed as part of the Java 2 SDK's Java Runtime
   Environment, no further settings are required. When you encounter
   problems building JAI applications, it is most likely that you are not
   running the correct version of java (jdk). Check with the command "java
   -version" on Solaris or at Dos prompt. If needed, set the PATH correctly
   to use the correct version of JDK or JRE as follows :

   For Solaris: 

   % setenv PATH $JDK1_3/bin:$PATH;    or
   % setenv PATH $JRE1_3/bin:$PATH;  

   For Windows: 

   To set PATH environment variable, you may need to edit autoexec.bat on
   Windows 98/95. For other Windows systems, try click
   Start-Settings-ControlPanel, then double click on System. Choose
   Environment. Under System Variables, click on Path, and update its value
   to put 

      c:\Program Files\JavaSoft\jre\1.3\bin;     or
      c:\jdk1.3\jre\bin;     or 
      c:\jdk1.3\bin;

   ahead of others. Note that ";" is used to separate from other values. To
   double check, open a DOS command prompt and use "set" or "set path" to
   view the PATH environment variable. 

   If you have used the CLASSPATH install into arbitrary location either for
   Solaris or Windows, then you will need to set an environment variable
   named JAI to point to the directory where the jai jar files are
   installed. Additionally on Solaris, the LD_LIBRARY_PATH variable also
   needs to be set.

   On Solaris:

   % setenv JAI $JAIDIR/lib
   % setenv LD_LIBRARY_PATH .:$JAI:$CLASSPATH

   On Windows;

     set JAI c:\jai-1_1_1\lib

3. Building the sample server code

   Once the above settings have been made, the sample source code can be
   built simply by running "make".

## Running the demo

*The following details describe the initial code code contribution and need to be updated to reflect maven build process*.

In order to run the JAINetworkDemo, the server(s) have to be started
first, following which the client can be run.

1. Starting the server:

   Once the settings specified in section 2 above have been made, the
   two servers JAIRMIServerWrapper and DirectoryListerServerImpl can
   both be run by running the "runServers.sh" script on Solaris and the 
   "runServers.bat" script on Windows. Note that both these scripts
   exist in the NetworkDemo directory one level up from the server
   directory in which the build is performed.

   By default, these scripts run the rmiregistry at port 1099. If it
   intended to run the rmiregistry at a different port, the Solaris
   script can be instructed to use a different port by using the
   "-port" option. The Windows script should be edited to set the port
   to the desired port number.

   It may be noted that the above scripts create three separate
   processes: the rmiregistry process, the JAIRMIServerWrapper server
   and the DirectoryListerServerImpl server. Here is an explanation of
   the steps the script performs on Solaris in order to start the
   servers (Refer to

   http://java.sun.com/j2se/1.3/docs/guide/rmi/getstart.doc.html#7445 

   for an detailed explanation of the steps to be followed).

   . A check is made to see whether the -port option was used to
   specify which port the rmiregistry should be run on. If so, this
   value is assigned to the PORT variable.

   . Before starting the rmiregistry, the script ensures that there is
   no CLASSPATH set. 

   . The rmiregistry is then started at the specified port, or at the
   default 1099 port if none was specified 

       rmiregistry 1099 &

   . The CLASSPATH is then set to the following value:

       CLASSPATH=$JAI/jai_core.jar:$JAI/jai_codec.jar
       CLASSPATH=$CLASSPATH:server/

   Note that when JAI is installed into the JDK or JRE, the JAI
   environment variable does not need to be set and therefore the
   CLASSPATH setting becomes simply:

       CLASSPATH=server/

   . The LD_LIBRARY_PATH variable is then set 

       LD_LIBRARY_PATH=$JAI:$LD_LIBRARY_PATH

   . The JAIRMIServerWrapper server is then run using the following
   command 

       java -Djava.rmi.server.codebase="file:$JAI/jai_core.jar \
       file:$JAI/jai_codec.jar \
       file:`pwd`/server/" -Djava.rmi.server.useCodebaseOnly=false \
       -Djava.security.policy=file:`pwd`/policy  \ 
       JAIRMIServerWrapper -port $PORT &
   
   . The DirectoryLister server is next run using the following command 
  
       java -classpath .:server/ \
       -Djava.rmi.server.codebase="file:`pwd`/server/" \
       -Djava.rmi.server.useCodebaseOnly=false \
       -Djava.security.policy=file:`pwd`/policy \
       DirectoryListerServerImpl -port $PORT &

2. Starting the client

   Once the settings specified in section 2 above have been made, the
   client can be run using the "runNetworkDemo.sh" script on Solaris
   and "runNetworkDemo.bat" script on Windows.

   The two scripts take the "-h", "-server" and "-imagePath"
   options. The "-h" option provides an explanation of the options
   accepted by the script. The "-server" option should be used to
   specify the server on which the "runServers.sh" or "runServers.bat"
   script was run. The "-imagePath" option should specify the absolute
   path to the images on the server. Note that this path should be the
   absolute path to the images on the server, not the client. These
   images will then be used in the demo.

   In order to exit the demo, the "killServerProcesses.sh" script can
   be used on Solaris to kill the various server processes. 
