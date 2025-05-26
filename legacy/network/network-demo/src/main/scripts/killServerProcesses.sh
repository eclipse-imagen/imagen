#!/bin/sh

cat /tmp/server_pids |
while read line
do
	echo $line
	kill -9 $line
done
rm /tmp/server_pids
