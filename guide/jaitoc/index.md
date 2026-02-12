![](shared/SunLogo.gif)



Release 1.0.1, November 1999

\


+----------------------------------------------------------------------:+
| Contents                                                              |
|                                                                       |
| -------------------------------------------------------------------   

[](../Preface)

### Preface

:   Disclaimer [](../Preface)

:   About This Book [](../Preface)

:   Related Documentation [](../Preface)

:   Additional Information [](../Preface)

:   Style Conventions

[](../Introduction)

### 1 Introduction to Java Advanced Imaging

:   1.1 The Evolution of Imaging in Java
    [](../Introduction)

:   1.2 Why Another Imaging API? [](../Introduction)

:   1.3 Features

    :   1.3.1 Cross-platform Imaging [](../Introduction)

    :   1.3.2 Distributed Imaging [](../Introduction)

    :   1.3.3 Object-oriented API [](../Introduction)

    :   1.3.4 Flexible and Extensible [](../Introduction)

    :   1.3.5 Device Independent [](../Introduction)

    :   1.3.6 Powerful [](../Introduction)

    :   1.3.7 High Performance [](../Introduction)

    :   1.3.8 Interoperable

    [](../Introduction)

:   1.4 A Simple JAI Program

[](../j2d-concepts)

### 2 Java AWT Imaging

:   2.1 Introduction

    :   2.1.1 The AWT Push Model [](../j2d-concepts)

    :   2.1.2 AWT Push Model Interfaces and Classes

    [](../j2d-concepts)

:   2.2 The Immediate Mode Model

    :   2.2.1 Rendering Independence [](../j2d-concepts)

    :   2.2.2 Rendering-independent Imaging in Java AWT
        [](../j2d-concepts)

    :   2.2.3 The Renderable Layer vs. the Rendered Layer
        [](../j2d-concepts)

    :   2.2.4 The Render Context

    [](../j2d-concepts)

:   2.3 Renderable and Rendered Classes

    :   2.3.1 The Renderable Layer [](../j2d-concepts)

    :   2.3.2 The Rendered Layer

    [](../j2d-concepts)

:   2.4 Java Image Data Representation [](../j2d-concepts)

:   2.5 Introducing the Java Advanced Imaging API

    :   2.5.1 Similarities with the Java 2D API
        [](../j2d-concepts)

    :   2.5.2 JAI Data Classes

[](../programming-environ)

### 3 Programming in Java Advanced Imaging

:   3.1 Introduction [](../programming-environ)

:   3.2 An Overview of Graphs [](../programming-environ)

:   3.3 Processing Graphs

    :   3.3.1 Rendered Graphs [](../programming-environ)

    :   3.3.2 Renderable Graphs [](../programming-environ)

    :   3.3.3 Reusing Graphs

    [](../programming-environ)

:   3.4 Remote Execution [](../programming-environ)

:   3.5 Basic JAI API Classes

    :   3.5.1 The JAI Class [](../programming-environ)

    :   3.5.2 The PlanarImage Class
        [](../programming-environ)

    :   3.5.3 The CollectionImage Class
        [](../programming-environ)

    :   3.5.4 The TiledImage Class
        [](../programming-environ)

    :   3.5.5 The OpImage Class [](../programming-environ)

    :   3.5.6 The RenderableOp Class
        [](../programming-environ)

    :   3.5.7 The RenderedOp Class

    [](../programming-environ)

:   3.6 JAI API Operators

    :   3.6.1 Point Operators [](../programming-environ)

    :   3.6.2 Area Operators [](../programming-environ)

    :   3.6.3 Geometric Operators
        [](../programming-environ)

    :   3.6.4 Color Quantization Operators
        [](../programming-environ)

    :   3.6.5 File Operators [](../programming-environ)

    :   3.6.6 Frequency Operators
        [](../programming-environ)

    :   3.6.7 Statistical Operators
        [](../programming-environ)

    :   3.6.8 Edge Extraction Operators
        [](../programming-environ)

    :   3.6.9 Miscellaneous Operators

    [](../programming-environ)

:   3.7 Creating Operations

    :   3.7.1 Operation Name [](../programming-environ)

    :   3.7.2 Parameter Blocks [](../programming-environ)

    :   3.7.3 Rendering Hints

[](../acquisition)

### 4 Image Acquisition and Display

:   4.1 Introduction

    :   4.1.1 Image Data [](../acquisition)

    :   4.1.2 Basic Storage Types

    [](../acquisition)

:   4.2 JAI Image Types

    :   4.2.1 Planar Image [](../acquisition)

    :   4.2.2 Tiled Image [](../acquisition)

    :   4.2.3 Snapshot Image [](../acquisition)

    :   4.2.4 Remote Image [](../acquisition)

    :   4.2.5 Collection Image [](../acquisition)

    :   4.2.6 Image Sequence [](../acquisition)

    :   4.2.7 Image Stack [](../acquisition)

    :   4.2.8 Image MIP Map [](../acquisition)

    :   4.2.9 Image Pyramid [](../acquisition)

    :   4.2.10 Multi-resolution Renderable Images

    [](../acquisition)

:   4.3 Streams [](../acquisition)

:   4.4 Reading Image Files

    :   4.4.1 Standard File Readers for Most Data Types
        [](../acquisition)

    :   4.4.2 Reading TIFF Images [](../acquisition)

    :   4.4.3 Reading FlashPix Images [](../acquisition)

    :   4.4.4 Reading JPEG Images [](../acquisition)

    :   4.4.5 Reading GIF Images [](../acquisition)

    :   4.4.6 Reading BMP Images [](../acquisition)

    :   4.4.7 Reading PNG Images [](../acquisition)

    :   4.4.8 Reading PNM Images [](../acquisition)

    :   4.4.9 Reading Standard AWT Images
        [](../acquisition)

    :   4.4.10 Reading URL Images

    [](../acquisition)

:   4.5 Reformatting an Image [](../acquisition)

:   4.6 Converting a Rendered Image to Renderable
    [](../acquisition)

:   4.7 Creating a Constant Image [](../acquisition)

:   4.8 Image Display

    :   4.8.1 Positioning the Image in the Panel
        [](../acquisition)

    :   4.8.2 The ImageCanvas Class [](../acquisition)

    :   4.8.3 Image Origin

[](../color)

### 5 Color Space

:   5.1 Introduction [](../color)

:   5.2 Color Management

    :   5.2.1 Color Models [](../color)

    :   5.2.2 Color Space [](../color)

    :   5.2.3 ICC Profile and ICC Color Space

    [](../color)

:   5.3 Transparency [](../color)

:   5.4 Color Conversion [](../color)

:   5.5 Non-standard Linear Color Conversion (BandCombine)

[](../image-manipulation)

### 6 Image Manipulation

:   6.1 Introduction [](../image-manipulation)

:   6.2 Region of Interest Control

    :   6.2.1 The ROI Class [](../image-manipulation)

    :   6.2.2 The ROIShape Class

    [](../image-manipulation)

:   6.3 Relational Operators

    :   6.3.1 Finding the Maximum Values of Two Images
        [](../image-manipulation)

    :   6.3.2 Finding the Minimum Values of Two Images

    [](../image-manipulation)

:   6.4 Logical Operators

    :   6.4.1 ANDing Two Images [](../image-manipulation)

    :   6.4.2 ANDing an Image with a Constant
        [](../image-manipulation)

    :   6.4.3 ORing Two Images [](../image-manipulation)

    :   6.4.4 ORing an Image with a Constant
        [](../image-manipulation)

    :   6.4.5 XORing Two Images [](../image-manipulation)

    :   6.4.6 XORing an Image with a Constant
        [](../image-manipulation)

    :   6.4.7 Taking the Bitwise NOT of an Image

    [](../image-manipulation)

:   6.5 Arithmetic Operators

    :   6.5.1 Adding Two Source Images
        [](../image-manipulation)

    :   6.5.2 Adding a Constant Value to an Image
        [](../image-manipulation)

    :   6.5.3 Adding a Collection of Images
        [](../image-manipulation)

    :   6.5.4 Adding Constants to a Collection of Rendered Images
        [](../image-manipulation)

    :   6.5.5 Subtracting Two Source Images
        [](../image-manipulation)

    :   6.5.6 Subtracting a Constant from an Image
        [](../image-manipulation)

    :   6.5.7 Subtracting an Image from a Constant
        [](../image-manipulation)

    :   6.5.8 Dividing One Image by Another Image
        [](../image-manipulation)

    :   6.5.9 Dividing an Image by a Constant
        [](../image-manipulation)

    :   6.5.10 Dividing an Image into a Constant
        [](../image-manipulation)

    :   6.5.11 Dividing Complex Images
        [](../image-manipulation)

    :   6.5.12 Multiplying Two Images
        [](../image-manipulation)

    :   6.5.13 Multiplying an Image by a Constant
        [](../image-manipulation)

    :   6.5.14 Multiplying Two Complex Images
        [](../image-manipulation)

    :   6.5.15 Finding the Absolute Value of Pixels
        [](../image-manipulation)

    :   6.5.16 Taking the Exponent of an Image

    [](../image-manipulation)

:   6.6 Dithering an Image

    :   6.6.1 Ordered Dither [](../image-manipulation)

    :   6.6.2 Error-diffusion Dither

    [](../image-manipulation)

:   6.7 Clamping Pixel Values [](../image-manipulation)

:   6.8 Band Copying [](../image-manipulation)

:   6.9 Constructing a Kernel

[](../image-enhance)

### 7 Image Enhancement

:   7.1 Introduction [](../image-enhance)

:   7.2 Adding Borders to Images

    :   7.2.1 The Border Operation [](../image-enhance)

    :   7.2.2 Extending the Edge of an Image

    [](../image-enhance)

:   7.3 Cropping an Image [](../image-enhance)

:   7.4 Amplitude Rescaling [](../image-enhance)

:   7.5 Histogram Equalization

    :   7.5.1 Piecewise Linear Mapping
        [](../image-enhance)

    :   7.5.2 Histogram Matching

    [](../image-enhance)

:   7.6 Lookup Table Modification

    :   7.6.1 Creating the Lookup Table
        [](../image-enhance)

    :   7.6.2 Performing the Lookup [](../image-enhance)

    :   7.6.3 Other Lookup Table Operations

    [](../image-enhance)

:   7.7 Convolution Filtering

    :   7.7.1 Performing the Convolve Operation
        [](../image-enhance)

    :   7.7.2 Box Filter

    [](../image-enhance)

:   7.8 Median Filtering [](../image-enhance)

:   7.9 Frequency Domain Processing

    :   7.9.1 Fourier Transform [](../image-enhance)

    :   7.9.2 Cosine Transform [](../image-enhance)

    :   7.9.3 Magnitude Enhancement [](../image-enhance)

    :   7.9.4 Magnitude-squared Enhancement
        [](../image-enhance)

    :   7.9.5 Phase Enhancement [](../image-enhance)

    :   7.9.6 Complex Conjugate [](../image-enhance)

    :   7.9.7 Periodic Shift [](../image-enhance)

    :   7.9.8 Polar to Complex [](../image-enhance)

    :   7.9.9 Images Based on a Functional Description

    [](../image-enhance)

:   7.10 Single-image Pixel Point Processing

    :   7.10.1 Pixel Inverting [](../image-enhance)

    :   7.10.2 Logarithmic Enhancement

    [](../image-enhance)

:   7.11 Dual Image Pixel Point Processing

    :   7.11.1 Overlay Images [](../image-enhance)

    :   7.11.2 Image Compositing

    [](../image-enhance)

:   7.12 Thresholding

[](../geom-image-manip)

### 8 Geometric Image Manipulation

:   8.1 Introduction [](../geom-image-manip)

:   8.2 Interpolation

    :   8.2.1 Nearest-neighbor Interpolation
        [](../geom-image-manip)

    :   8.2.2 Bilinear Interpolation
        [](../geom-image-manip)

    :   8.2.3 Bicubic Interpolation
        [](../geom-image-manip)

    :   8.2.4 Bicubic2 Interpolation
        [](../geom-image-manip)

    :   8.2.5 Table Interpolation

    [](../geom-image-manip)

:   8.3 Geometric Transformation

    :   8.3.1 Translation Transformation
        [](../geom-image-manip)

    :   8.3.2 Scaling Transformation
        [](../geom-image-manip)

    :   8.3.3 Rotation Transformation
        [](../geom-image-manip)

    :   8.3.4 Affine Transformation

    [](../geom-image-manip)

:   8.4 Perspective Transformation

    :   8.4.1 Performing the Transform
        [](../geom-image-manip)

    :   8.4.2 Mapping a Quadrilateral
        [](../geom-image-manip)

    :   8.4.3 Mapping Triangles [](../geom-image-manip)

    :   8.4.4 Inverse Perspective Transform
        [](../geom-image-manip)

    :   8.4.5 Creating the Adjoint of the Current Transform

    [](../geom-image-manip)

:   8.5 Transposing [](../geom-image-manip)

:   8.6 Shearing [](../geom-image-manip)

:   8.7 Warping

    :   8.7.1 Performing a Warp Operation
        [](../geom-image-manip)

    :   8.7.2 Polynomial Warp [](../geom-image-manip)

    :   8.7.3 General Polynomial Warp
        [](../geom-image-manip)

    :   8.7.4 Grid Warp [](../geom-image-manip)

    :   8.7.5 Quadratic Warp [](../geom-image-manip)

    :   8.7.6 Cubic Warp [](../geom-image-manip)

    :   8.7.7 Perspective Warp [](../geom-image-manip)

    :   8.7.8 Affine Warp

[](../analysis)

### 9 Image Analysis

:   9.1 Introduction [](../analysis)

:   9.2 Finding the Mean Value of an Image Region
    [](../analysis)

:   9.3 Finding the Extrema of an Image [](../analysis)

:   9.4 Histogram Generation

    :   9.4.1 Specifying the Histogram [](../analysis)

    :   9.4.2 Performing the Histogram Operation
        [](../analysis)

    :   9.4.3 Reading the Histogram Data [](../analysis)

    :   9.4.4 Histogram Operation Example

    [](../analysis)

:   9.5 Edge Detection [](../analysis)

:   9.6 Statistical Operations

[](../graphics)

### 10 Graphics Rendering

:   10.1 Introduction

    :   10.1.1 Simple 2D Graphics [](../graphics)

    :   10.1.2 Renderable Graphics

    [](../graphics)

:   10.2 A Review of Graphics Rendering

    :   10.2.1 Overview of the Rendering Process
        [](../graphics)

    :   10.2.2 Stroke Attributes [](../graphics)

    :   10.2.3 Rendering Graphics Primitives

    [](../graphics)

:   10.3 Graphics2D Example [](../graphics)

:   10.4 Adding Graphics and Text to an Image

[](../properties)

### 11 Image Properties

:   11.1 Introduction

    :   11.1.1 The PropertySource Interface
        [](../properties)

    :   11.1.2 The PropertyGenerator Interface

    [](../properties)

:   11.2 Synthetic Properties [](../properties)

:   11.3 Regions of Interest [](../properties)

:   11.4 Complex Data

[](../client-server)

### 12 Client-Server Imaging

:   12.1 Introduction [](../client-server)

:   12.2 Server Name and Port Number [](../client-server)

:   12.3 Setting the Timeout Period and Number of Retries
    [](../client-server)

:   12.4 Remote Imaging Test Example

    :   12.4.1 Simple Remote Imaging Example
        [](../client-server)

    :   12.4.2 RemoteImaging Example Across Two Nodes

    [](../client-server)

:   12.5 Running Remote Imaging

    :   12.5.1 Step 1: Create a Security Policy File
        [](../client-server)

    :   12.5.2 Step 2: Start the RMI Registry
        [](../client-server)

    :   12.5.3 Step 3: Start the Remote Image Server
        [](../client-server)

    :   12.5.4 Step 4: Run the Local Application

    [](../client-server)

:   12.6 Internet Imaging Protocol (IIP)

    :   12.6.1 IIP Operation [](../client-server)

    :   12.6.2 IIPResolution Operation

[](../encode)

### 13 Writing Image Files

:   13.1 Introduction [](../encode)

:   13.2 Writing to a File [](../encode)

:   13.3 Writing to an Output Stream [](../encode)

:   13.4 Writing BMP Image Files

    :   13.4.1 BMP Version [](../encode)

    :   13.4.2 BMP Data Layout [](../encode)

    :   13.4.3 Example Code

    [](../encode)

:   13.5 Writing JPEG Image Files

    :   13.5.1 JFIF Header [](../encode)

    :   13.5.2 JPEG DCT Compression Parameters
        [](../encode)

    :   13.5.3 Quantization Table [](../encode)

    :   13.5.4 Horizontal and Vertical Subsampling
        [](../encode)

    :   13.5.5 Compression Quality [](../encode)

    :   13.5.6 Restart Interval [](../encode)

    :   13.5.7 Writing an Abbreviated JPEG Stream
        [](../encode)

    :   13.5.8 Example Code

    [](../encode)

:   13.6 Writing PNG Image Files

    :   13.6.1 PNG Image Layout [](../encode)

    :   13.6.2 PNG Filtering [](../encode)

    :   13.6.3 Bit Depth [](../encode)

    :   13.6.4 Interlaced Data Order [](../encode)

    :   13.6.5 PLTE Chunk for Palette Images [](../encode)

    :   13.6.6 Ancillary Chunk Specifications

    [](../encode)

:   13.7 Writing PNM Image Files [](../encode)

:   13.8 Writing TIFF Image Files

    :   13.8.1 TIFF Compression [](../encode)

    :   13.8.2 TIFF Tiled Images

[](../extension)

### 14 Extending the API

:   14.1 Introduction [](../extension)

:   14.2 Package Naming Convention [](../extension)

:   14.3 Writing New Operators

    :   14.3.1 Extending the OpImage Class
        [](../extension)

    :   14.3.2 Extending the OperationDescriptor Interface

    [](../extension)

:   14.4 Iterators

    :   14.4.1 RectIter [](../extension)

    :   14.4.2 RookIter [](../extension)

    :   14.4.3 RandomIter [](../extension)

    :   14.4.4 Example RectIter

    [](../extension)

:   14.5 Writing New Image Decoders and Encoders

    :   14.5.1 Image Codecs

[](../Examples)

### A Program Examples

:   A.1 Lookup Operation Example [](../Examples)

:   A.2 Adding an OperationDescriptor Example

[](../api-summary)

### B Java Advanced Imaging API Summary

:   B.1 Java AWT Imaging [](../api-summary)

:   B.2 Java 2D Imaging

    :   B.2.1 Java 2D Imaging Interfaces
        [](../api-summary)

    :   B.2.2 Java 2D Imaging Classes

    [](../api-summary)

:   B.3 Java Advanced Imaging

    :   B.3.1 JAI Interfaces [](../api-summary)

    :   B.3.2 JAI Classes [](../api-summary)

    :   B.3.3 JAI Iterator Interfaces [](../api-summary)

    :   B.3.4 JAI Iterator Classes [](../api-summary)

    :   B.3.5 JAI Operator Classes [](../api-summary)

    :   B.3.6 JAI Widget Interfaces [](../api-summary)

    :   B.3.7 JAI Widget Classes

[](../glossary)

### Glossary

------------------------------------------------------------------------

\


