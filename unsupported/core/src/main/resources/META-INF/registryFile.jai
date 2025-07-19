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
descriptor	org.eclipse.imagen.operator.AWTImageDescriptor
descriptor	org.eclipse.imagen.operator.PeriodicShiftDescriptor
descriptor	org.eclipse.imagen.operator.PhaseDescriptor
descriptor	org.eclipse.imagen.operator.MagnitudeDescriptor
descriptor	org.eclipse.imagen.operator.MagnitudeSquaredDescriptor
descriptor	org.eclipse.imagen.operator.AWTImageDescriptor
descriptor	org.eclipse.imagen.operator.ColorQuantizerDescriptor
descriptor	org.eclipse.imagen.operator.CompositeDescriptor
descriptor	org.eclipse.imagen.operator.ConjugateDescriptor
descriptor	org.eclipse.imagen.operator.DCTDescriptor
descriptor	org.eclipse.imagen.operator.DFTDescriptor
descriptor	org.eclipse.imagen.operator.DilateDescriptor
descriptor	org.eclipse.imagen.operator.DivideComplexDescriptor
descriptor	org.eclipse.imagen.operator.ErodeDescriptor
descriptor	org.eclipse.imagen.operator.GradientMagnitudeDescriptor
descriptor	org.eclipse.imagen.operator.IDCTDescriptor
descriptor	org.eclipse.imagen.operator.IDFTDescriptor
descriptor	org.eclipse.imagen.operator.IIPDescriptor
descriptor	org.eclipse.imagen.operator.IIPResolutionDescriptor
descriptor	org.eclipse.imagen.operator.MatchCDFDescriptor
descriptor	org.eclipse.imagen.operator.MultiplyComplexDescriptor
descriptor	org.eclipse.imagen.operator.PeriodicShiftDescriptor
descriptor	org.eclipse.imagen.operator.PolarToComplexDescriptor
descriptor	org.eclipse.imagen.operator.SubsampleAverageDescriptor
descriptor	org.eclipse.imagen.operator.SubsampleBinaryToGrayDescriptor

#
# tile codec descriptor
#


#
# remote descriptor
#


#
# "rendered" factory objects
#
rendered    org.eclipse.imagen.media.opimage.AWTImageRIF		org.eclipse.imagen.media	awtimage		sunawtimagerif
rendered    org.eclipse.imagen.media.opimage.PhaseCRIF			org.eclipse.imagen.media	phase			sunphaserif
rendered    org.eclipse.imagen.media.opimage.MagnitudeCRIF		org.eclipse.imagen.media	magnitude		sunmagnituderif
rendered    org.eclipse.imagen.media.opimage.MagnitudeSquaredCRIF	org.eclipse.imagen.media	magnitudesquared	sunmagnitudesquaredrif
rendered    org.eclipse.imagen.media.opimage.AWTImageRIF		org.eclipse.imagen.media	awtimage		sunawtimagerif
rendered    org.eclipse.imagen.media.opimage.ColorQuantizerRIF		org.eclipse.imagen.media	colorquantizer		suncolorquantizerrif
rendered    org.eclipse.imagen.media.opimage.ConjugateCRIF		org.eclipse.imagen.media	conjugate		sunconjugaterif
rendered    org.eclipse.imagen.media.opimage.DCTCRIF			org.eclipse.imagen.media	dct			sundctrif
rendered    org.eclipse.imagen.media.opimage.DFTCRIF			org.eclipse.imagen.media	dft			sundftrif
rendered    org.eclipse.imagen.media.opimage.DilateRIF			org.eclipse.imagen.media	dilate			sundilaterif
rendered    org.eclipse.imagen.media.opimage.DivideComplexCRIF		org.eclipse.imagen.media	dividecomplex		sundividecomplexrif
rendered    org.eclipse.imagen.media.opimage.ErodeRIF			org.eclipse.imagen.media	erode			suneroderif
rendered    org.eclipse.imagen.media.opimage.GradientRIF		org.eclipse.imagen.media	gradientmagnitude	sungradientrif
rendered    org.eclipse.imagen.media.opimage.IDCTCRIF			org.eclipse.imagen.media	idct			sunidctrif
rendered    org.eclipse.imagen.media.opimage.IDFTCRIF			org.eclipse.imagen.media	idft			sunidftrif
rendered    org.eclipse.imagen.media.opimage.MatchCDFCRIF		org.eclipse.imagen.media	matchcdf		sunmatchcdfrif
rendered    org.eclipse.imagen.media.opimage.MultiplyComplexCRIF	org.eclipse.imagen.media	multiplycomplex		sunmultiplycomplexrif
rendered    org.eclipse.imagen.media.opimage.OverlayCRIF		org.eclipse.imagen.media	overlay			sunoverlayrif
rendered    org.eclipse.imagen.media.opimage.PeriodicShiftCRIF		org.eclipse.imagen.media	periodicshift		sunperiodicshiftrif
rendered    org.eclipse.imagen.media.opimage.PolarToComplexCRIF	org.eclipse.imagen.media	polartocomplex		sunpolartocomplexrif
rendered    org.eclipse.imagen.media.opimage.SubsampleBinaryToGrayCRIF	org.eclipse.imagen.media	subsamplebinarytogray	sunsubsamplebinarytograyrif
rendered    org.eclipse.imagen.media.opimage.SubsampleAverageCRIF	org.eclipse.imagen.media	subsampleaverage	sunsubsampleaveragerif

#
# "renderable" factory objects
#


renderable	org.eclipse.imagen.media.opimage.PhaseCRIF		phase
renderable	org.eclipse.imagen.media.opimage.MagnitudeCRIF		magnitude
renderable	org.eclipse.imagen.media.opimage.MagnitudeSquaredCRIF	magnitudesquared
renderable	org.eclipse.imagen.media.opimage.CompositeCRIF		composite
renderable	org.eclipse.imagen.media.opimage.ConjugateCRIF		conjugate
renderable	org.eclipse.imagen.media.opimage.DCTCRIF		dct
renderable	org.eclipse.imagen.media.opimage.DFTCRIF		dft
renderable	org.eclipse.imagen.media.opimage.DivideComplexCRIF	dividecomplex
renderable	org.eclipse.imagen.media.opimage.IDCTCRIF		idct
renderable	org.eclipse.imagen.media.opimage.IDFTCRIF		idft
renderable	org.eclipse.imagen.media.opimage.MatchCDFCRIF		matchcdf
renderable	org.eclipse.imagen.media.opimage.MultiplyComplexCRIF	multiplycomplex
renderable	org.eclipse.imagen.media.opimage.OverlayCRIF		overlay
renderable	org.eclipse.imagen.media.opimage.PeriodicShiftCRIF	periodicshift
renderable	org.eclipse.imagen.media.opimage.PolarToComplexCRIF	polartocomplex
renderable	org.eclipse.imagen.media.opimage.SubsampleBinaryToGrayCRIF	subsamplebinarytogray
renderable	org.eclipse.imagen.media.opimage.SubsampleAverageCRIF	subsampleaverage

#
# "collection" factory objects
#

#
# tile decoder factory objects
#

#
# tile encoder factory objects
#

#
# remote rendered factory objects
#

#
# remote renderable factory objects
#
