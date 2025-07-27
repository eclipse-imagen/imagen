#
# $RCSfile: registryFile.jai,v $
#
# Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#  http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
# $Revision: 1.2 $
# $Date: 2005-08-31 22:35:25 $
# $State: Exp $
#
# The master JAI registry initialization file
#
############################################################################
#
# Each line can be in one of the formats described below. Space or tab
# characters separate keywords in each line. The comment character is
# '#'; on each line all characters following the first comment character
# are ignored. The file must be encoded in UTF-8.
#
# 1. To register descriptors :
#
#	descriptor <descriptor-class-name>
#	odesc	   <descriptor-class-name>  <descriptor-name>
#
#    The second version above is deprecated and is retained for backward
#    compatibility with JAI 1.0.2. Descriptors are always registered
#    against <descriptor-class>.getName(). The <descriptor-name> in the
#    second version is always ignored.
#
# 2. To register factory objects under a product against a specific mode :
#
#	<registry-mode-name>	<factory-class-name>	<product-name>	<descriptor-name>   <local-name>
#	<registry-mode-name>	<factory-class-name>	<descriptor-name>
#
#    The first version above is used to register factory objects against
#    modes that support preferences. The second version is used for those
#    that do not support preferences. <local-name> is an arbitrary name that
#    is unique for a given mode. This is (only) used later on in this file
#    to set preferences between factory objects.
#
# 3. To set preferences between products for a descriptor under a
#    specific mode :
#
#	prefProduct <modeName>	<descriptor-name>   <preferred-product-name> <other-product-name>
#	pref	    product	<descriptor-name>   <preferred-product-name> <other-product-name>
#
#    The second version above is deprecated and is retained for backward
#    compatibility with JAI 1.0.2. This version is assumed to set
#    product preferences for the "rendered" mode.
#
# 4. To set preferences between factory objects for descriptor under a
#    a specific product and registry mode :
#
#	pref	<modeName>  <descriptor-name>	<product-name>	<preferred-factory-local-name>	<other-factory-local-name>
#
############################################################################
#
# Image operation descriptors :
#

descriptor	org.eclipse.imagen.operator.BMPDescriptor
descriptor	org.eclipse.imagen.operator.EncodeDescriptor
descriptor	org.eclipse.imagen.operator.FileLoadDescriptor
descriptor	org.eclipse.imagen.operator.FileStoreDescriptor
descriptor	org.eclipse.imagen.operator.FPXDescriptor
descriptor	org.eclipse.imagen.operator.GIFDescriptor
descriptor	org.eclipse.imagen.operator.JPEGDescriptor
descriptor	org.eclipse.imagen.operator.PNGDescriptor
descriptor	org.eclipse.imagen.operator.PNMDescriptor
descriptor	org.eclipse.imagen.operator.StreamDescriptor
descriptor	org.eclipse.imagen.operator.TIFFDescriptor
descriptor	org.eclipse.imagen.operator.URLDescriptor

#
# tile codec descriptor
#
descriptor	org.eclipse.imagen.tilecodec.GZIPTileCodecDescriptor
descriptor	org.eclipse.imagen.tilecodec.JPEGTileCodecDescriptor
descriptor	org.eclipse.imagen.tilecodec.RawTileCodecDescriptor


#
# "rendered" factory objects
#
rendered    org.eclipse.imagen.media.opimage.BMPRIF			org.eclipse.imagen.media	bmp			sunbmprif
rendered    org.eclipse.imagen.media.opimage.EncodeRIF			org.eclipse.imagen.media	encode   		sunencoderif
rendered    org.eclipse.imagen.media.opimage.FileLoadRIF		org.eclipse.imagen.media	fileload		sunfileloadrif
rendered    org.eclipse.imagen.media.opimage.FileStoreRIF		org.eclipse.imagen.media	filestore		sunfilestorerif
rendered    org.eclipse.imagen.media.opimage.FPXRIF		        org.eclipse.imagen.media	fpx			sunfpxrif
rendered    org.eclipse.imagen.media.opimage.GIFRIF		        org.eclipse.imagen.media	gif			sungifrif
rendered    org.eclipse.imagen.media.opimage.JPEGRIF		        org.eclipse.imagen.media	jpeg			sunjpegrif
rendered    org.eclipse.imagen.media.opimage.PNGRIF			org.eclipse.imagen.media	png			sunpngrif
rendered    org.eclipse.imagen.media.opimage.PNMRIF			org.eclipse.imagen.media	pnm			sunpnmrif
rendered    org.eclipse.imagen.media.opimage.StreamRIF			org.eclipse.imagen.media	stream			sunstreamrif
rendered    org.eclipse.imagen.media.opimage.TIFFRIF			org.eclipse.imagen.media	tiff			suntiffrif
rendered    org.eclipse.imagen.media.opimage.URLRIF			org.eclipse.imagen.media	url			sunurlrif

#
# tile decoder factory objects
#
tileDecoder org.eclipse.imagen.media.tilecodec.GZIPTileDecoderFactory	org.eclipse.imagen.media	gzip			sungziptiledecoderfactory
tileDecoder org.eclipse.imagen.media.tilecodec.RawTileDecoderFactory	org.eclipse.imagen.media	raw			sunrawtiledecoderfactory

#
# tile encoder factory objects
#
tileEncoder org.eclipse.imagen.media.tilecodec.GZIPTileEncoderFactory	org.eclipse.imagen.media	gzip			sungziptileencoderfactory
tileEncoder org.eclipse.imagen.media.tilecodec.RawTileEncoderFactory	org.eclipse.imagen.media	raw			sunrawtileencoderfactory