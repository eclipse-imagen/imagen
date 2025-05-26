#
# Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
# 
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
# 
#   http://www.apache.org/licenses/LICENSE-2.0
# 
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

.SUFFIXES: .java .class

JARPATH=${JAI}/jai_core.jar:${JAI}/jai_codec.jar:${JAI}/mlibwrapper_jai.jar

.java.class:
	javac -classpath $(JARPATH):. $<

SRCS = \
	JAINetworkDemo.java \
        JAIDemoPanel.java \
        JAIAddConstPanel.java \
        JAIBlurSharpPanel.java \
        JAIConvolvePanel.java \
        JAIGradientPanel.java \
        JAIMedianPanel.java \
        JAIRotatePanel.java \
        JAIScalePanel.java \
        JAITransposePanel.java \
        JAIDyadicPanel.java             \
        JAIDyadicAddPanel.java \
        JAIDyadicDividePanel.java \
        JAIDyadicMultiplyPanel.java \
        JAIDyadicSubtractPanel.java \
        JAIDyadicSource0Panel.java \
        JAIDyadicSource1Panel.java \
        JAISourceAdjPanel.java\
        JAIImageReader.java \
        IconJAI.java

CLASS = $(SRCS:%.java=%.class)

all: JAINetworkDemo

JAINetworkDemo: $(CLASS)

clean:
	/bin/rm -f *.class
