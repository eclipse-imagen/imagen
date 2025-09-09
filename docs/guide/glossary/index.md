---
layout: default
title: Glossary
parent: Programming Guide
nav_order: 18
---

# Glossary 

This glossary contains descriptions of significant terms that appear in this book.

**affine transformation**  
:   Geometric image transformation, such as translation, scaling, or
    rotation.
    
**band**  
:   The set of all samples of one type in an image, such as all red
    samples or all green samples.
    
**box filter**  
:   A low-pass spatial filter composed of uniformly-weighted
    convolution coefficients.
    
**bicubic interpolation**  
:   Two-dimensional cubic interpolation of pixel values based on the
    16 pixels in a 4 x 4 pixel neighborhood. See also [*bilinear
    interpolation*](../glossary), [*nearest-neighbor
    interpolation*](../glossary).
    
**bilinear interpolation**  
:   Two-dimensional linear interpolation of pixel values based on the
    four pixels in a 2 x 2 pixel neighborhood. See also [*bicubic
    interpolation*](../glossary), [*nearest-neighbor
    interpolation*](../glossary).
    
**binary image**  
:   An image that consists of only two brightness levels: black and
    white.
    
**chain code**  
:   A pixel-by-pixel direction code that defines boundaries of objects
    within an image. The chain code records an object boundary as a
    series of direction codes.
    
**cobble**  
:   To assemble multiple tile regions into a single contiguous region.
    
**color space conversion**  
:   The conversion of a color using one space to another color space,
    such as RGB to CMYK.
    
**components**  
:   Values of samples independent of color interpretation.
    
**compression ratio**  
:   In image compression, the ratio of an uncompressed image data file
    size to its compressed counterpart.
    
**data element**  
:   Primitive types used as units of storage of image data. Data
    elements are individual members of a `DataBuffer` array.
    
**directed graph (digraph)**  
:   A graph with one-way edges. See also [*directed acyclic graph
    (DAG)*](../glossary).
    
**directed acyclic graph (DAG)**  
:   A directed graph containing no cycles. This means that if there is
    a route from node A to node B then there is no way back.
    
**first-order interpolation**  
:   See [*bilinear interpolation*](../glossary).
    
**high-pass filter**  
:   A spatial filter that accentuates an image\'s high-frequency
    detail or attenuates the low-frequency detail. Contrast with
    [*low-pass filter*](../glossary), [*median
    filter*](../glossary).
    
**histogram**  
:   A measure of the amplitude distribution of pixels within an image.
    
**Lempel-Ziv-Welch (LZW) compression**  
:   A lossless image coding method that scans the image for repeating
    patterns of blocks of pixels and codes the patterns into a code
    list.
    
**low-pass filter**  
:   A spatial filter that attenuates an image\'s high-frequency detail
    or accentuates the low-frequency detail. Contrast with [*high-pass
    filter*](../glossary), [*median
    filter*](../glossary).
    
**median filter**  
:   A non-linear spatial filter used to remove noise spikes from an
    image.
    
**nearest-neighbor interpolation**  
:   Two-dimensional interpolation of pixel values in which the
    amplitude of the interpolated sample is the amplitude of its
    nearest neighbor. See also [*bicubic
    interpolation*](../glossary), [*bilinear
    interpolation*](../glossary).
    
**perspective warp**  
:   An image distortion in which objects appear trapezoidal due to
    foreshortening.
    
**projective warp**  
:   See [*perspective warp*](../glossary).
    
**quantization**  
:   The conversion of discrete image samples to digital quantities.
    
**ROI**  
:   Abbreviation for *region of interest*. An area or pixel group
    within an image that has been selected for processing.
    
**run-length coding**  
:   A type of lossless image data compression that scans for sequences
    of pixels with the same brightness level and codes them into a
    reduced description.
    
**Sobel edge detection**  
:   A spatial edge detection filter that detects edges by finding the
    gradient of an image.
    
**square pixel**  
:   A pixel with equal height and width.
    
**thresholding**  
:   A point operation that maps all the pixel values of an image that
    fall within a given range to one of a set of per-band constants.
    
**transform coding**  
:   A form of lossy block coding that transforms blocks of an image
    from the spatial domain to the frequency domain.
    
**trapping**  
:   An image manipulation technique used in printing that uses
    dilation and erosion to compensation for misregistration of
    colors.
    
**unsharp masking**  
:   An image enhancement technique using a high-frequency accentuating
    filter.
    
**zero-order interpolation**  
:   See *nearest-neighbor interpolation*.

