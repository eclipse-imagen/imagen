#!/bin/bash

# error out if any statements fail
set -e

# move to modules
pushd ../modules

# build aggregate javadocs
mvn javadoc:aggregate -Pjavadoc

# publish to docs folder
cd target/reports
rm -rf ../../../docs/javadocs

mv apidocs ../../../docs/javadocs

