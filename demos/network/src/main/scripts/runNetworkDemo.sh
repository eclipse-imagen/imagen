#!/bin/sh

java -classpath .:$JAI/jai_core.jar:$JAI/jai_codec.jar -Djava.security.policy=file:`pwd`/policy -Djava.security.manager -Xmx48m JAINetworkDemo $*
