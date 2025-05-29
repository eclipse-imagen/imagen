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
# descriptor	org.eclipse.imagen.operator.AffineDescriptor
descriptor	org.eclipse.imagen.operator.AndConstDescriptor
descriptor	org.eclipse.imagen.operator.AndDescriptor
#descriptor	org.eclipse.imagen.operator.AWTImageDescriptor
# descriptor	org.eclipse.imagen.operator.BandCombineDescriptor
# descriptor	org.eclipse.imagen.operator.BandMergeDescriptor
# descriptor	org.eclipse.imagen.operator.BandSelectDescriptor
# descriptor	org.eclipse.imagen.operator.BinarizeDescriptor
#descriptor	org.eclipse.imagen.operator.BMPDescriptor
# descriptor	org.eclipse.imagen.operator.BorderDescriptor
descriptor	org.eclipse.imagen.operator.BoxFilterDescriptor
# descriptor	org.eclipse.imagen.operator.ClampDescriptor
# descriptor	org.eclipse.imagen.operator.ColorConvertDescriptor
# descriptor	org.eclipse.imagen.operator.ColorQuantizerDescriptor
descriptor	org.eclipse.imagen.operator.ConstantDescriptor
#descriptor	org.eclipse.imagen.operator.CompositeDescriptor
#descriptor	org.eclipse.imagen.operator.ConjugateDescriptor
descriptor	org.eclipse.imagen.operator.ConvolveDescriptor
#descriptor	org.eclipse.imagen.operator.CropDescriptor
#descriptor	org.eclipse.imagen.operator.DCTDescriptor
#descriptor	org.eclipse.imagen.operator.DFTDescriptor
#descriptor	org.eclipse.imagen.operator.DilateDescriptor
descriptor	org.eclipse.imagen.operator.DivideDescriptor
#descriptor	org.eclipse.imagen.operator.DivideComplexDescriptor
descriptor	org.eclipse.imagen.operator.DivideByConstDescriptor
descriptor	org.eclipse.imagen.operator.DivideIntoConstDescriptor
#descriptor	org.eclipse.imagen.operator.ErodeDescriptor
# descriptor	org.eclipse.imagen.operator.ErrorDiffusionDescriptor
#descriptor	org.eclipse.imagen.operator.EncodeDescriptor
descriptor	org.eclipse.imagen.operator.ExpDescriptor
descriptor	org.eclipse.imagen.operator.ExtremaDescriptor
#descriptor	org.eclipse.imagen.operator.FileLoadDescriptor
#descriptor	org.eclipse.imagen.operator.FileStoreDescriptor
descriptor	org.eclipse.imagen.operator.FilteredSubsampleDescriptor
# descriptor	org.eclipse.imagen.operator.FormatDescriptor
#descriptor	org.eclipse.imagen.operator.FPXDescriptor
#descriptor	org.eclipse.imagen.operator.GIFDescriptor
#descriptor	org.eclipse.imagen.operator.GradientMagnitudeDescriptor
descriptor	org.eclipse.imagen.operator.HistogramDescriptor
#descriptor	org.eclipse.imagen.operator.IDCTDescriptor
#descriptor	org.eclipse.imagen.operator.IDFTDescriptor
#descriptor	org.eclipse.imagen.operator.IIPDescriptor
#descriptor	org.eclipse.imagen.operator.IIPResolutionDescriptor
#descriptor	org.eclipse.imagen.operator.ImageFunctionDescriptor
descriptor	org.eclipse.imagen.operator.InvertDescriptor
#descriptor	org.eclipse.imagen.operator.JPEGDescriptor
descriptor	org.eclipse.imagen.operator.LogDescriptor
# descriptor	org.eclipse.imagen.operator.LookupDescriptor
#descriptor	org.eclipse.imagen.operator.MagnitudeDescriptor
#descriptor	org.eclipse.imagen.operator.MagnitudeSquaredDescriptor
descriptor	org.eclipse.imagen.operator.MaxDescriptor
descriptor	org.eclipse.imagen.operator.MaxFilterDescriptor
#descriptor	org.eclipse.imagen.operator.MatchCDFDescriptor
descriptor	org.eclipse.imagen.operator.MeanDescriptor
descriptor	org.eclipse.imagen.operator.MedianFilterDescriptor
descriptor	org.eclipse.imagen.operator.MinDescriptor
descriptor	org.eclipse.imagen.operator.MinFilterDescriptor
# descriptor	org.eclipse.imagen.operator.MosaicDescriptor
descriptor	org.eclipse.imagen.operator.MultiplyConstDescriptor
#descriptor	org.eclipse.imagen.operator.MultiplyComplexDescriptor
descriptor	org.eclipse.imagen.operator.MultiplyDescriptor
descriptor	org.eclipse.imagen.operator.NotDescriptor
# descriptor	org.eclipse.imagen.operator.NullDescriptor
descriptor	org.eclipse.imagen.operator.OrConstDescriptor
descriptor	org.eclipse.imagen.operator.OrDescriptor
#descriptor	org.eclipse.imagen.operator.OrderedDitherDescriptor
#descriptor	org.eclipse.imagen.operator.OverlayDescriptor
descriptor	org.eclipse.imagen.operator.PatternDescriptor
#descriptor	org.eclipse.imagen.operator.PeriodicShiftDescriptor
#descriptor	org.eclipse.imagen.operator.PhaseDescriptor
descriptor	org.eclipse.imagen.operator.PiecewiseDescriptor
#descriptor	org.eclipse.imagen.operator.PNGDescriptor
#descriptor	org.eclipse.imagen.operator.PNMDescriptor
#descriptor	org.eclipse.imagen.operator.PolarToComplexDescriptor
descriptor	org.eclipse.imagen.operator.RenderableDescriptor
# descriptor	org.eclipse.imagen.operator.RescaleDescriptor
descriptor	org.eclipse.imagen.operator.RotateDescriptor
# descriptor	org.eclipse.imagen.operator.ScaleDescriptor
descriptor	org.eclipse.imagen.operator.ShearDescriptor
#descriptor	org.eclipse.imagen.operator.StreamDescriptor
#descriptor	org.eclipse.imagen.operator.SubsampleAverageDescriptor
#descriptor	org.eclipse.imagen.operator.SubsampleBinaryToGrayDescriptor
descriptor	org.eclipse.imagen.operator.SubtractDescriptor
descriptor	org.eclipse.imagen.operator.SubtractConstDescriptor
descriptor	org.eclipse.imagen.operator.SubtractFromConstDescriptor
#descriptor	org.eclipse.imagen.operator.TIFFDescriptor
# descriptor	org.eclipse.imagen.operator.ThresholdDescriptor
descriptor	org.eclipse.imagen.operator.TranslateDescriptor
descriptor	org.eclipse.imagen.operator.TransposeDescriptor
descriptor	org.eclipse.imagen.operator.UnsharpMaskDescriptor
#descriptor	org.eclipse.imagen.operator.URLDescriptor
descriptor	org.eclipse.imagen.operator.WarpDescriptor
descriptor	org.eclipse.imagen.operator.XorConstDescriptor
descriptor	org.eclipse.imagen.operator.XorDescriptor

#
# tile codec descriptor
#
#descriptor	org.eclipse.imagen.tilecodec.GZIPTileCodecDescriptor
#descriptor	org.eclipse.imagen.tilecodec.JPEGTileCodecDescriptor
#descriptor	org.eclipse.imagen.tilecodec.RawTileCodecDescriptor

#
# remote descriptor
#
#descriptor    org.eclipse.imagen.remote.JAIRMIDescriptor

#
# "rendered" factory objects
#
rendered    org.eclipse.imagen.media.opimage.AbsoluteCRIF		org.eclipse.imagen.media	absolute		sunabsoluterif
rendered    org.eclipse.imagen.media.opimage.AddCollectionCRIF		org.eclipse.imagen.media	addcollection		sunaddcollectionrif
rendered    org.eclipse.imagen.media.opimage.AddConstCRIF		org.eclipse.imagen.media	addconst		sunaddconstrif
rendered    org.eclipse.imagen.media.opimage.AddCRIF			org.eclipse.imagen.media	add			sunaddrif
# rendered    org.eclipse.imagen.media.opimage.AffineCRIF		org.eclipse.imagen.media	affine			sunaffinerif
rendered    org.eclipse.imagen.media.opimage.AndConstCRIF		org.eclipse.imagen.media	andconst		sunandconstrif
rendered    org.eclipse.imagen.media.opimage.AndCRIF			org.eclipse.imagen.media	and			sunandrif
#rendered    org.eclipse.imagen.media.opimage.AWTImageRIF		org.eclipse.imagen.media	awtimage		sunawtimagerif
# rendered    org.eclipse.imagen.media.opimage.BandCombineCRIF		org.eclipse.imagen.media	bandcombine		sunbandcombinerif
# rendered    org.eclipse.imagen.media.opimage.BandMergeCRIF		org.eclipse.imagen.media	bandmerge		sunbandmergerif
# rendered    org.eclipse.imagen.media.opimage.BandSelectCRIF		org.eclipse.imagen.media	bandselect		sunbandselectrif
# rendered    org.eclipse.imagen.media.opimage.BinarizeCRIF		org.eclipse.imagen.media	binarize		sunbinarizerif
#rendered    org.eclipse.imagen.media.opimage.BMPRIF			org.eclipse.imagen.media	bmp			sunbmprif
# rendered    org.eclipse.imagen.media.opimage.BorderRIF			org.eclipse.imagen.media	border			sunborderrif
rendered    org.eclipse.imagen.media.opimage.BoxFilterRIF		org.eclipse.imagen.media	boxfilter		sunboxfilterrif
# rendered    org.eclipse.imagen.media.opimage.ClampCRIF			org.eclipse.imagen.media	clamp			sunclamprif
#rendered    org.eclipse.imagen.media.opimage.ColorConvertCRIF		org.eclipse.imagen.media	colorconvert		suncolorconvertrif
#rendered    org.eclipse.imagen.media.opimage.ColorQuantizerRIF		org.eclipse.imagen.media	colorquantizer		suncolorquantizerrif
#rendered    org.eclipse.imagen.media.opimage.CompositeCRIF		org.eclipse.imagen.media	composite		suncompositerif
rendered    org.eclipse.imagen.media.opimage.ConstantCRIF		org.eclipse.imagen.media	constant		sunconstrif1
#rendered    org.eclipse.imagen.media.opimage.ConjugateCRIF		org.eclipse.imagen.media	conjugate		sunconjugaterif
rendered    org.eclipse.imagen.media.opimage.ConvolveRIF		org.eclipse.imagen.media	convolve		sunconvolverif
#rendered    org.eclipse.imagen.media.opimage.CropCRIF			org.eclipse.imagen.media	crop			suncroprif
#rendered    org.eclipse.imagen.media.opimage.DCTCRIF			org.eclipse.imagen.media	dct			sundctrif
#rendered    org.eclipse.imagen.media.opimage.DFTCRIF			org.eclipse.imagen.media	dft			sundftrif
#rendered    org.eclipse.imagen.media.opimage.DilateRIF			org.eclipse.imagen.media	dilate			sundilaterif
rendered    org.eclipse.imagen.media.opimage.DivideCRIF		org.eclipse.imagen.media	divide			sundividerif
#rendered    org.eclipse.imagen.media.opimage.DivideComplexCRIF		org.eclipse.imagen.media	dividecomplex		sundividecomplexrif
rendered    org.eclipse.imagen.media.opimage.DivideByConstCRIF		org.eclipse.imagen.media	dividebyconst		sundividebyconstrif
rendered    org.eclipse.imagen.media.opimage.DivideIntoConstCRIF	org.eclipse.imagen.media	divideintoconst		sundivideintoconstrif
#rendered    org.eclipse.imagen.media.opimage.EncodeRIF			org.eclipse.imagen.media	encode   		sunencoderif
#rendered    org.eclipse.imagen.media.opimage.ErodeRIF			org.eclipse.imagen.media	erode			suneroderif
# rendered    org.eclipse.imagen.media.opimage.ErrorDiffusionRIF		org.eclipse.imagen.media	errordiffusion		sunerrordiffusionrif
rendered    org.eclipse.imagen.media.opimage.ExpCRIF		        org.eclipse.imagen.media	exp			sunexprif
rendered    org.eclipse.imagen.media.opimage.ExtremaRIF		org.eclipse.imagen.media	extrema			sunextremarif
#rendered    org.eclipse.imagen.media.opimage.FileLoadRIF		org.eclipse.imagen.media	fileload		sunfileloadrif
#rendered    org.eclipse.imagen.media.opimage.FileStoreRIF		org.eclipse.imagen.media	filestore		sunfilestorerif
rendered    org.eclipse.imagen.media.opimage.FilteredSubsampleRIF      org.eclipse.imagen.media       filteredsubsample       sunfilteredsubsamplerif
# rendered    org.eclipse.imagen.media.opimage.FormatCRIF		org.eclipse.imagen.media	format			sunformatrif
#rendered    org.eclipse.imagen.media.opimage.FPXRIF		        org.eclipse.imagen.media	fpx			sunfpxrif
#rendered    org.eclipse.imagen.media.opimage.GIFRIF		        org.eclipse.imagen.media	gif			sungifrif
#rendered    org.eclipse.imagen.media.opimage.GradientRIF		org.eclipse.imagen.media	gradientmagnitude	sungradientrif
rendered    org.eclipse.imagen.media.opimage.HistogramRIF		org.eclipse.imagen.media	histogram		sunhistogramrif
#rendered    org.eclipse.imagen.media.opimage.IDCTCRIF			org.eclipse.imagen.media	idct			sunidctrif
#rendered    org.eclipse.imagen.media.opimage.IDFTCRIF			org.eclipse.imagen.media	idft			sunidftrif
#rendered    org.eclipse.imagen.media.opimage.ImageFunctionRIF		org.eclipse.imagen.media	imagefunction		sunimagefunctionrif
rendered    org.eclipse.imagen.media.opimage.InvertCRIF		org.eclipse.imagen.media	invert			suninvertrif
#rendered    org.eclipse.imagen.media.opimage.JPEGRIF		        org.eclipse.imagen.media	jpeg			sunjpegrif
rendered    org.eclipse.imagen.media.opimage.LogCRIF		        org.eclipse.imagen.media	log			sunlogrif
# rendered    org.eclipse.imagen.media.opimage.LookupCRIF		org.eclipse.imagen.media	lookup			sunlookuprif
#rendered    org.eclipse.imagen.media.opimage.MagnitudeCRIF		org.eclipse.imagen.media	magnitude		sunmagnituderif
#rendered    org.eclipse.imagen.media.opimage.MagnitudeSquaredCRIF	org.eclipse.imagen.media	magnitudesquared	sunmagnitudesquaredrif
#rendered    org.eclipse.imagen.media.opimage.MatchCDFCRIF		org.eclipse.imagen.media	matchcdf		sunmatchcdfrif
rendered    org.eclipse.imagen.media.opimage.MaxCRIF		        org.eclipse.imagen.media	max			sunmaxrif
rendered    org.eclipse.imagen.media.opimage.MaxFilterRIF		org.eclipse.imagen.media	maxfilter		sunmaxfilterrif
rendered    org.eclipse.imagen.media.opimage.MeanRIF		        org.eclipse.imagen.media	mean			sunmeanrif
rendered    org.eclipse.imagen.media.opimage.MedianFilterRIF		org.eclipse.imagen.media	medianfilter		sunmedianfilterrif
rendered    org.eclipse.imagen.media.opimage.MinCRIF		        org.eclipse.imagen.media	min			sunminrif
rendered    org.eclipse.imagen.media.opimage.MinFilterRIF		org.eclipse.imagen.media	minfilter		sunminfilterrif
# rendered    org.eclipse.imagen.media.opimage.MosaicRIF		org.eclipse.imagen.media	mosaic		sunmosaicrif
rendered    org.eclipse.imagen.media.opimage.MultiplyCRIF		org.eclipse.imagen.media	multiply		sunmultiplyrif
#rendered    org.eclipse.imagen.media.opimage.MultiplyComplexCRIF	org.eclipse.imagen.media	multiplycomplex		sunmultiplycomplexrif
rendered    org.eclipse.imagen.media.opimage.MultiplyConstCRIF		org.eclipse.imagen.media	multiplyconst		sunmultiplyconstrif
rendered    org.eclipse.imagen.media.opimage.NotCRIF			org.eclipse.imagen.media	not			sunnotrif
# rendered    org.eclipse.imagen.NullCRIF			   	org.eclipse.imagen		null			sunullrif
rendered    org.eclipse.imagen.media.opimage.OrConstCRIF		org.eclipse.imagen.media	orconst			sunorconstrif
rendered    org.eclipse.imagen.media.opimage.OrCRIF			org.eclipse.imagen.media	or			sunorrif
#rendered    org.eclipse.imagen.media.opimage.OrderedDitherRIF		org.eclipse.imagen.media	ordereddither		sunorderedditherrif
#rendered    org.eclipse.imagen.media.opimage.OverlayCRIF		org.eclipse.imagen.media	overlay			sunoverlayrif
rendered    org.eclipse.imagen.media.opimage.PatternRIF		org.eclipse.imagen.media	pattern			sunpatternrif
#rendered    org.eclipse.imagen.media.opimage.PhaseCRIF			org.eclipse.imagen.media	phase			sunphaserif
#rendered    org.eclipse.imagen.media.opimage.PeriodicShiftCRIF		org.eclipse.imagen.media	periodicshift		sunperiodicshiftrif
rendered    org.eclipse.imagen.media.opimage.PiecewiseCRIF		org.eclipse.imagen.media	piecewise		sunpiecewiserif
#rendered    org.eclipse.imagen.media.opimage.PNGRIF			org.eclipse.imagen.media	png			sunpngrif
#rendered    org.eclipse.imagen.media.opimage.PNMRIF			org.eclipse.imagen.media	pnm			sunpnmrif
#rendered    org.eclipse.imagen.media.opimage.PolarToComplexCRIF	org.eclipse.imagen.media	polartocomplex		sunpolartocomplexrif
#rendered    org.eclipse.imagen.media.opimage.RescaleCRIF		org.eclipse.imagen.media	rescale			sunrescalerif
rendered    org.eclipse.imagen.media.opimage.RotateCRIF		org.eclipse.imagen.media	rotate			sunrotaterif
# rendered    org.eclipse.imagen.media.opimage.ScaleCRIF			org.eclipse.imagen.media	scale			sunscalerif
rendered    org.eclipse.imagen.media.opimage.ShearRIF			org.eclipse.imagen.media	shear			sunshearrif
#rendered    org.eclipse.imagen.media.opimage.StreamRIF			org.eclipse.imagen.media	stream			sunstreamrif
#rendered    org.eclipse.imagen.media.opimage.SubsampleBinaryToGrayCRIF	org.eclipse.imagen.media	subsamplebinarytogray	sunsubsamplebinarytograyrif
#rendered    org.eclipse.imagen.media.opimage.SubsampleAverageCRIF	org.eclipse.imagen.media	subsampleaverage	sunsubsampleaveragerif
rendered    org.eclipse.imagen.media.opimage.SubtractCRIF		org.eclipse.imagen.media	subtract		sunsubtractrif
rendered    org.eclipse.imagen.media.opimage.SubtractConstCRIF		org.eclipse.imagen.media	subtractconst		sunsubtractconstrif
rendered    org.eclipse.imagen.media.opimage.SubtractFromConstCRIF	org.eclipse.imagen.media	subtractfromconst	sunsubtractfromconstrif
#rendered    org.eclipse.imagen.media.opimage.TIFFRIF			org.eclipse.imagen.media	tiff			suntiffrif
# rendered    org.eclipse.imagen.media.opimage.ThresholdCRIF		org.eclipse.imagen.media	threshold		sunthresholdrif
rendered    org.eclipse.imagen.media.opimage.TranslateCRIF		org.eclipse.imagen.media	translate		suntransrif
rendered    org.eclipse.imagen.media.opimage.TransposeCRIF		org.eclipse.imagen.media	transpose		suntransposerif
rendered    org.eclipse.imagen.media.opimage.UnsharpMaskRIF		org.eclipse.imagen.media	unsharpmask		sununsharpmaskrif
#rendered    org.eclipse.imagen.media.opimage.URLRIF			org.eclipse.imagen.media	url			sunurlrif
rendered    org.eclipse.imagen.media.opimage.WarpRIF			org.eclipse.imagen.media	warp			sunwarprif
rendered    org.eclipse.imagen.media.opimage.XorConstCRIF		org.eclipse.imagen.media	xorconst		sunxorconstrif
rendered    org.eclipse.imagen.media.opimage.XorCRIF			org.eclipse.imagen.media	xor			sunxorrif


#
# "renderable" factory objects
#
renderable	org.eclipse.imagen.media.opimage.AbsoluteCRIF		absolute
renderable	org.eclipse.imagen.media.opimage.AddCollectionCRIF	addcollection
renderable	org.eclipse.imagen.media.opimage.AddConstCRIF		addconst
renderable	org.eclipse.imagen.media.opimage.AddCRIF		add
# renderable	org.eclipse.imagen.media.opimage.AffineCRIF		affine
renderable	org.eclipse.imagen.media.opimage.AndConstCRIF		andconst
renderable	org.eclipse.imagen.media.opimage.AndCRIF		and
# renderable	org.eclipse.imagen.media.opimage.BandCombineCRIF	bandcombine
# renderable	org.eclipse.imagen.media.opimage.BandMergeCRIF         bandmerge
# renderable	org.eclipse.imagen.media.opimage.BandSelectCRIF	bandselect
# renderable	org.eclipse.imagen.media.opimage.BinarizeCRIF		binarize
# renderable	org.eclipse.imagen.media.opimage.ClampCRIF		clamp
#renderable	org.eclipse.imagen.media.opimage.ColorConvertCRIF	colorconvert
#renderable	org.eclipse.imagen.media.opimage.CompositeCRIF		composite
#renderable	org.eclipse.imagen.media.opimage.ConjugateCRIF		conjugate
renderable	org.eclipse.imagen.media.opimage.ConstantCRIF		constant
#renderable	org.eclipse.imagen.media.opimage.CropCRIF		crop
#renderable	org.eclipse.imagen.media.opimage.DCTCRIF		dct
#renderable	org.eclipse.imagen.media.opimage.DFTCRIF		dft
renderable	org.eclipse.imagen.media.opimage.DivideCRIF		divide
#renderable	org.eclipse.imagen.media.opimage.DivideComplexCRIF	dividecomplex
renderable	org.eclipse.imagen.media.opimage.DivideByConstCRIF	dividebyconst
renderable	org.eclipse.imagen.media.opimage.DivideIntoConstCRIF	divideintoconst
renderable	org.eclipse.imagen.media.opimage.ExpCRIF		exp
#renderable	org.eclipse.imagen.media.opimage.FormatCRIF		format
#renderable	org.eclipse.imagen.media.opimage.IDCTCRIF		idct
#renderable	org.eclipse.imagen.media.opimage.IDFTCRIF		idft
renderable	org.eclipse.imagen.media.opimage.InvertCRIF		invert
renderable	org.eclipse.imagen.media.opimage.LogCRIF		log
# renderable	org.eclipse.imagen.media.opimage.LookupCRIF		lookup
#renderable	org.eclipse.imagen.media.opimage.MagnitudeCRIF		magnitude
#renderable	org.eclipse.imagen.media.opimage.MagnitudeSquaredCRIF	magnitudesquared
#renderable	org.eclipse.imagen.media.opimage.MatchCDFCRIF		matchcdf
renderable	org.eclipse.imagen.media.opimage.MaxCRIF		max
renderable	org.eclipse.imagen.media.opimage.MinCRIF		min
renderable	org.eclipse.imagen.media.opimage.MultiplyCRIF		multiply
#renderable	org.eclipse.imagen.media.opimage.MultiplyComplexCRIF	multiplycomplex
renderable	org.eclipse.imagen.media.opimage.MultiplyConstCRIF	multiplyconst
renderable	org.eclipse.imagen.media.opimage.NotCRIF		not
# renderable	org.eclipse.imagen.NullCRIF			null
renderable	org.eclipse.imagen.media.opimage.OrConstCRIF		orconst
renderable	org.eclipse.imagen.media.opimage.OrCRIF		or
#renderable	org.eclipse.imagen.media.opimage.OverlayCRIF		overlay
#renderable	org.eclipse.imagen.media.opimage.PeriodicShiftCRIF	periodicshift
#renderable	org.eclipse.imagen.media.opimage.PhaseCRIF		phase
renderable	org.eclipse.imagen.media.opimage.PiecewiseCRIF		piecewise
#renderable	org.eclipse.imagen.media.opimage.PolarToComplexCRIF	polartocomplex
#renderable	org.eclipse.imagen.media.opimage.RenderableCRIF	renderable
# renderable	org.eclipse.imagen.media.opimage.RescaleCRIF		rescale
renderable	org.eclipse.imagen.media.opimage.RotateCRIF		rotate
#renderable	org.eclipse.imagen.media.opimage.ScaleCRIF		scale
#renderable	org.eclipse.imagen.media.opimage.SubsampleBinaryToGrayCRIF	subsamplebinarytogray
#renderable	org.eclipse.imagen.media.opimage.SubsampleAverageCRIF	subsampleaverage
renderable	org.eclipse.imagen.media.opimage.SubtractCRIF		subtract
renderable	org.eclipse.imagen.media.opimage.SubtractConstCRIF	subtractconst
renderable	org.eclipse.imagen.media.opimage.SubtractFromConstCRIF	subtractfromconst
# renderable	org.eclipse.imagen.media.opimage.ThresholdCRIF		threshold
renderable	org.eclipse.imagen.media.opimage.TranslateCRIF		translate
renderable	org.eclipse.imagen.media.opimage.TransposeCRIF		transpose
renderable	org.eclipse.imagen.media.opimage.XorConstCRIF		xorconst
renderable	org.eclipse.imagen.media.opimage.XorCRIF		xor

#
# "collection" factory objects
#
collection	org.eclipse.imagen.media.opimage.AddConstToCollectionCIF	org.eclipse.imagen.media	addconsttocollection	sunaddconsttocollectioncif

#
# tile decoder factory objects
#
#tileDecoder org.eclipse.imagen.media.tilecodec.GZIPTileDecoderFactory	org.eclipse.imagen.media	gzip			sungziptiledecoderfactory
#tileDecoder org.eclipse.imagen.media.tilecodec.RawTileDecoderFactory	org.eclipse.imagen.media	raw			sunrawtiledecoderfactory

#
# tile encoder factory objects
#
#tileEncoder org.eclipse.imagen.media.tilecodec.GZIPTileEncoderFactory	org.eclipse.imagen.media	gzip			sungziptileencoderfactory
#tileEncoder org.eclipse.imagen.media.tilecodec.RawTileEncoderFactory	org.eclipse.imagen.media	raw			sunrawtileencoderfactory

#
# remote rendered factory objects
#
#remoterendered      org.eclipse.imagen.media.rmi.JAIRMICRIF             jairmi

#
# remote renderable factory objects
#
#remoterenderable    org.eclipse.imagen.media.rmi.JAIRMICRIF             jairmi

