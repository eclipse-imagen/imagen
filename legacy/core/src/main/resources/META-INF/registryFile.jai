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
descriptor	org.eclipse.imagen.operator.AbsoluteDescriptor
descriptor	org.eclipse.imagen.operator.AddCollectionDescriptor
descriptor	org.eclipse.imagen.operator.AddConstDescriptor
descriptor	org.eclipse.imagen.operator.AddConstToCollectionDescriptor
descriptor	org.eclipse.imagen.operator.AddDescriptor
descriptor	org.eclipse.imagen.operator.AffineDescriptor
descriptor	org.eclipse.imagen.operator.AndConstDescriptor
descriptor	org.eclipse.imagen.operator.AndDescriptor
descriptor	org.eclipse.imagen.operator.BandCombineDescriptor
descriptor	org.eclipse.imagen.operator.BandMergeDescriptor
descriptor	org.eclipse.imagen.operator.BandSelectDescriptor
descriptor	org.eclipse.imagen.operator.BinarizeDescriptor
descriptor	org.eclipse.imagen.operator.BorderDescriptor
descriptor	org.eclipse.imagen.operator.RotateDescriptor
descriptor	org.eclipse.imagen.operator.ShearDescriptor
descriptor	org.eclipse.imagen.operator.SubtractConstDescriptor
descriptor	org.eclipse.imagen.operator.ClampDescriptor
descriptor	org.eclipse.imagen.operator.ColorConvertDescriptor
descriptor	org.eclipse.imagen.operator.ConvolveDescriptor
descriptor	org.eclipse.imagen.operator.RescaleDescriptor
descriptor	org.eclipse.imagen.operator.ScaleDescriptor
descriptor	org.eclipse.imagen.operator.TranslateDescriptor
descriptor	org.eclipse.imagen.operator.SubtractDescriptor
descriptor	org.eclipse.imagen.operator.SubtractConstDescriptor
descriptor	org.eclipse.imagen.operator.ThresholdDescriptor






#
# tile codec descriptor
#

#
# remote descriptor
#

#
# "rendered" factory objects
#
rendered    org.eclipse.imagen.media.opimage.AbsoluteCRIF		org.eclipse.imagen.media	absolute		sunabsoluterif
rendered    org.eclipse.imagen.media.opimage.AddCollectionCRIF		org.eclipse.imagen.media	addcollection		sunaddcollectionrif
rendered    org.eclipse.imagen.media.opimage.AddConstCRIF		org.eclipse.imagen.media	addconst		sunaddconstrif
rendered    org.eclipse.imagen.media.opimage.AddCRIF			org.eclipse.imagen.media	add			sunaddrif
rendered    org.eclipse.imagen.media.opimage.AffineCRIF		org.eclipse.imagen.media	affine			sunaffinerif
rendered    org.eclipse.imagen.media.opimage.AndConstCRIF		org.eclipse.imagen.media	andconst		sunandconstrif
rendered    org.eclipse.imagen.media.opimage.AndCRIF			org.eclipse.imagen.media	and			sunandrif
rendered    org.eclipse.imagen.media.opimage.BandCombineCRIF		org.eclipse.imagen.media	bandcombine		sunbandcombinerif
rendered    org.eclipse.imagen.media.opimage.BandMergeCRIF		org.eclipse.imagen.media	bandmerge		sunbandmergerif
rendered    org.eclipse.imagen.media.opimage.BandSelectCRIF		org.eclipse.imagen.media	bandselect		sunbandselectrif
rendered    org.eclipse.imagen.media.opimage.BinarizeCRIF		org.eclipse.imagen.media	binarize		sunbinarizerif
rendered    org.eclipse.imagen.media.opimage.BorderRIF			org.eclipse.imagen.media	border			sunborderrif
rendered    org.eclipse.imagen.media.opimage.RotateCRIF		org.eclipse.imagen.media	rotate			sunrotaterif
rendered    org.eclipse.imagen.media.opimage.ShearRIF			org.eclipse.imagen.media	shear			sunshearrif
rendered    org.eclipse.imagen.media.opimage.SubtractConstCRIF		org.eclipse.imagen.media	subtractconst		sunsubtractconstrif
rendered    org.eclipse.imagen.media.opimage.ClampCRIF			org.eclipse.imagen.media	clamp			sunclamprif
rendered    org.eclipse.imagen.media.opimage.ColorConvertCRIF		org.eclipse.imagen.media	colorconvert		suncolorconvertrif
rendered    org.eclipse.imagen.media.opimage.ConvolveRIF		org.eclipse.imagen.media	convolve		sunconvolverif
rendered    org.eclipse.imagen.media.opimage.RescaleCRIF		org.eclipse.imagen.media	rescale			sunrescalerif
rendered    org.eclipse.imagen.media.opimage.ScaleCRIF			org.eclipse.imagen.media	scale			sunscalerif
rendered    org.eclipse.imagen.media.opimage.TranslateCRIF		org.eclipse.imagen.media	translate		suntransrif
rendered    org.eclipse.imagen.media.opimage.SubtractCRIF		org.eclipse.imagen.media	subtract		sunsubtractrif
rendered    org.eclipse.imagen.media.opimage.SubtractFromConstCRIF	org.eclipse.imagen.media	subtractfromconst	sunsubtractfromconstrif
rendered    org.eclipse.imagen.media.opimage.ThresholdCRIF		org.eclipse.imagen.media	threshold		sunthresholdrif





#
# "renderable" factory objects
#
renderable	org.eclipse.imagen.media.opimage.AbsoluteCRIF		absolute
renderable	org.eclipse.imagen.media.opimage.AddCollectionCRIF	addcollection
renderable	org.eclipse.imagen.media.opimage.AddConstCRIF		addconst
renderable	org.eclipse.imagen.media.opimage.AddCRIF		add
renderable	org.eclipse.imagen.media.opimage.AffineCRIF		affine
renderable	org.eclipse.imagen.media.opimage.AndConstCRIF		andconst
renderable	org.eclipse.imagen.media.opimage.AndCRIF		and
renderable	org.eclipse.imagen.media.opimage.BandCombineCRIF	bandcombine
renderable	org.eclipse.imagen.media.opimage.BandMergeCRIF         bandmerge
renderable	org.eclipse.imagen.media.opimage.BandSelectCRIF	bandselect
renderable	org.eclipse.imagen.media.opimage.BinarizeCRIF		binarize
renderable	org.eclipse.imagen.media.opimage.RotateCRIF		rotate
renderable	org.eclipse.imagen.media.opimage.ScaleCRIF		scale
renderable	org.eclipse.imagen.media.opimage.SubtractConstCRIF	subtractconst
renderable	org.eclipse.imagen.media.opimage.ClampCRIF		clamp
renderable	org.eclipse.imagen.media.opimage.ColorConvertCRIF	colorconvert
renderable	org.eclipse.imagen.media.opimage.RescaleCRIF		rescale
renderable	org.eclipse.imagen.media.opimage.ScaleCRIF		scale
renderable	org.eclipse.imagen.media.opimage.TranslateCRIF		translate
renderable	org.eclipse.imagen.media.opimage.SubtractCRIF		subtract
renderable	org.eclipse.imagen.media.opimage.SubtractFromConstCRIF	subtractfromconst
renderable	org.eclipse.imagen.media.opimage.ThresholdCRIF		threshold


#
# "collection" factory objects
#
collection	org.eclipse.imagen.media.opimage.AddConstToCollectionCIF	org.eclipse.imagen.media	addconsttocollection	sunaddconsttocollectioncif


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

