#!/bin/sh

#
# Set the port number
#
if [ "$1" ]; then
    PORT=$1
else
    PORT=1099
fi
echo "Using port $PORT for rmiregistry and RemoteImageServer ..."

# Save the current CLASSPATH settings and unset it so rmiregistry 
# doesn't pick them up
PREVCPATH=$CLASSPATH
CLASSPATH=

# Start rmiregistry
rmiregistry $PORT &
echo $! > /tmp/server_pids

# Set CPATH to the required CLASSPATH setting
CPATH=$JAI/jai_core.jar:$JAI/jai_codec.jar:server/:$PREVCPATH
LD_LIBRARY_PATH=$JAI:$LD_LIBRARY_PATH
export LD_LIBRARY_PATH

#
# Run the network imaging server wrapper.
#
java -classpath $CPATH -Djava.rmi.server.codebase="file:$JAI/jai_core.jar file:$JAI/jai_codec.jar file:`pwd`/server/" -Djava.rmi.server.useCodebaseOnly=false -Djava.security.policy=file:`pwd`/policy JAIRMIServerWrapper -port $PORT &
echo $! >> /tmp/server_pids

#
# Run the DirectoryListerServer.
#
java -classpath .:server/ -Djava.rmi.server.codebase="file:`pwd`/server/" -Djava.rmi.server.useCodebaseOnly=false -Djava.security.policy=file:`pwd`/policy DirectoryListerServerImpl -port $PORT &
echo $! >> /tmp/server_pids
