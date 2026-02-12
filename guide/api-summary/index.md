---
layout: default
title: API Summary
parent: Programming Guide
nav_order: 18
---

# Eclipse ImageN API Summary
{: .no_toc }

This appendix summarizes the imaging interfaces and classes for Java AWT, Java 2D,
and Eclipse ImageN.

* Contents
{:toc}

## B.1 Java AWT Imaging

[Table B-1](#table-B-1) lists and describes the `java.awt` imaging classes.

**Table B-1 `java.awt` Imaging Classes** <a name="table-B-1"></a>

| Class | Description |
| ----- | ----------- |
| Image | The superclass of all classes that represent graphical images. |

## B.2 Java 2D Imaging

The Java 2D API is a set of classes for advanced 2D graphics and
imaging. It encompasses line art, text, and images in a single
comprehensive model. The API provides extensive support for image
compositing and alpha channel images, a set of classes to provide
accurate color space definition and conversion, and a rich set of
display-oriented imaging operators.

The Java 2D classes are provided as additions to the `java.awt` and
`java.awt.image` packages (rather than as a separate package).

### B.2.1 Java 2D Imaging Interfaces

[Table B-2](#table-B-2) lists and briefly describes
the imaging interfaces defined in the `java.awt.image `(Java 2D) API.

**Table B-2 `java.awt.image` Interfaces** <a name="table-B-2"></a>

| Interface | Description |
| --------- | ------------|
| BufferedImageOp | Describes single-input/single-output operations performed on BufferedImage objects. This is implemented by such classes as `AffineTransformOp`, `ConvolveOp`, `BandCombineOp`, and `LookupOp`. |
| ImageConsumer | Used for objects expressing interest in image data through the ImageProducer interfaces. |
| ImageObserver | Receives notifications about Image information as the Image is constructed. |
| ImageProducer | Used for objects that can produce the image data for Images. Each image contains an ImageProducer that is used to reconstruct the image whenever it is needed, for example, when a new size of the Image is scaled, or when the width or height of the Image is being requested. |
| ImagingLib | Provides a hook to access platform-specific imaging code. |
| RasterImageConsumer | Extends: ImageConsumer <br/> The interface for objects expressing interest in image data through the `ImageProducer` interfaces. When a consumer is added to an image producer, the producer delivers all of the data about the image using the method calls defined in this interface. |
| RasterOp | Describes single-input/single-output operations performed on `Raster` objects. This is implemented by such classes as `AffineTransformOp`, `ConvolveOp`, and `LookupOp`. |
| RenderedImage | A common interface for objects that contain or can produce image data in the form of Rasters. |
| TileChangeListener | An interface for objects that wish to be informed when tiles of a `WritableRenderedImage` become modifiable by some writer via a call to getWritableTile, and when they become unmodifiable via the last call to releaseWritableTile. |
| WritableRenderedImage | Extends: RenderedImage <br/> A common interface for objects that contain or can produce image data that can be modified and/or written over. |
### B.2.2 Java 2D Imaging Classes

[Table B-3](#table-B-3) lists and briefly describes
the imaging classes defined in the `java.awt.image `(Java 2D) API.

**Table B-3 `java.awt.image` Classes** <a name="table-B-3"></a>

| Class | Description |
| ----- | ------------|
| AffineTransformOp | Implements: `BufferedImageOp`, `RasterOp` <br/> An abstract class that uses an affine transform to perform a linear mapping from 2D coordinates in the source image or Raster to 2D coordinates in the destination image or Raster. |
| AreaAveragingScaleFilter | Extends: `ReplicateScaleFilter` <br/>  An ImageFilter class for scaling images using a simple area averaging algorithm that produces smoother results than the nearest-neighbor algorithm. |
| BandCombineOp | Implements: `RasterOp` <br/> Performs an arbitrary linear combination of bands in a Raster, using a specified matrix. |
| BandedSampleModel | Extends: `SampleModel` <br/>  Provides more efficent implementations for accessing image data than are provided in SampleModel. Used when working with images that store sample data for each band in a different bank of the DataBuffer. |
| BilinearAffineTransformOp | Extends: `AffineTransformOp`  <br/> Uses an affine transformation with bilinear interpolation to transform an image or Raster. |
| BufferedImage | Extends: `Image` <br/> Implements: `WritableRenderedImage` <br/>  Describes an Image with an accessible buffer of image data. |
| BufferedImageFilter | Extends: `ImageFilter` <br/> Implements: RasterImageConsumer, Cloneable Provides a simple means of using a single-source/single-destination image operator (BufferedImageOp) to filter a BufferedImage or Raster in the Image Producer/Consumer/Observer paradigm. |
| ByteLookupTable | Extends: `LookupTable` <br/> Defines a lookup table object. The lookup table contains byte data for one or more tile channels or image components (for example, separate arrays for R, G, and B), and it contains an offset that will be subtracted from the input value before indexing the array. |
| ColorConvertOp | Implements: `BufferedImageOp`, `RasterOp` <br/> Performs a pixel-by-pixel color conversion of the data in the source image. The resulting color values are scaled to the precision of the destination image data type. |
| ColorModel | Implements: `Transparency` <br/> An abstract class that encapsulates the methods for translating from pixel values to color components (e.g., red, green, blue) for an image. |
| ComponentColorModel | Extends: `ColorModel` A ColorModel class that can handle an arbitrary ColorSpace and an array of color components to match the ColorSpace. |
| ComponentSampleModel | Extends: `SampleModel` <br/> Stores the N samples that make up a pixel in N separate data array elements all of which are in the same bank of a dataBuffer. |
| ConvolveOp | Implements: `BufferedImageOp`, `RasterOp` <br/> Implements a convolution from the source to the destination. Convolution using a convolution kernel is a spatial operation that computes the output pixel from an input pixel by multiplying the kernel with the surround of the input pixel. |
| CropImageFilter | Extends: `ImageFilter` <br/>An ImageFilter class for cropping images. |
| DataBuffer | Wraps one or more data arrays. Each data array in the DataBuffer is referred to as a bank. Accessor methods for getting and setting elements of the DataBuffer\'s banks exist with and without a bank specifier. |
| DataBufferByte | Extends: `DataBuffer` <br/> Stores data internally as bytes. |
| DataBufferInt | Extends: `DataBuffer` <br/> Stores data internally as ints. |
| DataBufferShort | Extends: `DataBuffer` <br/> Stores data internally as shorts. |
| DirectColorModel | Extends: `PackedColorModel` <br/> Represents pixel values that have RGB color components embedded directly in the bits of the pixel itself. |
| FilteredImageSource | Implements: `ImageProducer` <br/> An implementation of the ImageProducer interface which takes an existing image and a filter object and uses them to produce image data for a new filtered version of the original image. |
| ImageFilter | Implements: `ImageConsumer`, `Cloneable` <br/> Implements a filter for the set of interface methods that are used to deliver data from an ImageProducer to an ImageConsumer. |
| IndexColorModel | Extends: `ColorModel` <br/> Represents pixel values that are indices into a fixed colormap in the ColorModel\'s color space. |
| Kernel | Defines a Kernel object - a matrix describing how a given pixel and its surrounding pixels affect the value of the given pixel in a filtering operation. |
| LookupOp | Implements: `BufferedImageOp`, `RasterOp` <br/> Implements a lookup operation from the source to the destination. |
| LookupTable | Defines a lookup table object. The subclasses are ByteLookupTable and ShortLookupTable, which contain byte and short data, respectively. |
| MemoryImageSource | Implements: `ImageProducer` <br/> An implementation of the ImageProducer interface, which uses an array to produce pixel values for an Image. |
| MultiPixelPackedSampleModel | Extends: `SampleModel` <br/> Stores one-banded images, but can pack multiple one-sample pixels into one data element. |
| NearestNeighborAffine-TransformOp | Extends: `AffineTransformOp` <br/> Uses an affine transformation with nearest neighbor interpolation to transform an image or Raster. |
| PackedColorModel | Extends: `ColorModel` <br/> An abstract ColorModel class that represents pixel values that have the color components embedded directly in the bits of an integer pixel. |
| PixelGrabber | Implements: `ImageConsumer` <br/> Implements an ImageConsumer which can be attached to an Image or ImageProducer object to retrieve a subset of the pixels in that image. |
| RGBImageFilter | Extends: `ImageFilter` <br/> Provides an easy way to create an ImageFilter that modifies the pixels of an image in the default RGB ColorModel. It is meant to be used in conjunction with a FilteredImageSource object to produce filtered versions of existing images. |
| Raster | Represents a rectanglular array of pixels and provides methods for retrieving image data. It contains a DataBuffer object that holds a buffer of image data in some format, a SampleModel that describes the format is capable of storing and retrieving Samples from the DataBuffer, and a Rect that defines the coordinate space of the raster (upper left corner, width and height). |
| ReplicateScaleFilter | Extends: `ImageFilter` <br/> Scales images using the simplest algorithm. |
| RescaleOp | Implements: `BufferedImageOp`, `RasterOp` <br/> Performs a pixel-by-pixel rescaling of the data in the source image by multiplying each pixel value by a scale factor and then adding an offset. |
| SampleModel | Defines an interface for extracting samples of an image without knowing how the underlying data is stored in a DataBuffer. |
| ShortLookupTable | Extends: `LookupTable` <br/>  Defines a lookup table object. The lookup table contains short data for one or more tile channels or image components (for example, separate arrays for R, G, and B), and it contains an offset that will be subtracted from the input value before indexing the array. |
| SinglePixelPackedSample-Model | Extends: `SampleModel` <br/> Stores (packs) the N samples that make up a single pixel in one data array element. All data array elements reside in the first bank of a DataBuffer. |
| ThresholdOp | Implements: `BufferedImageOp`, `RasterOp` <br/> Performs thresholding on the source image by mapping the value of each image component (for BufferedImages) or channel element (for Rasters) that falls between a low and a high value, to a constant. |
| TileChangeMulticaster | A convenience class that takes care of the details of implementing the TileChangeListener interface. |
| WritableRaster | Extends: `Raster` <br/> Provides methods for storing image data and inherits methods for retrieving image data from it\'s parent class Raster. |

B.3 Eclipse ImageN
------------------

The Eclipse ImagenN API consists of the following packages:

-   `org.eclipse.imagen` - contains the \"core\" ImageN interfaces and
    classes

-   `org.eclipse.imagen.iterator` - contains special iterator interfaces
    and classes, which are useful for writing extension operations

-   `org.eclipse.imagen.operator` - contains classes that describe all of
    the image operators

-   `org.eclipse.imagen.widget` - contains interfaces and classes for
    creating simple image canvases and scrolling windows for image
    display


### B.3.1 ImageN Interfaces

[Table B-4](#table-B-4) lists and briefly describes the interfaces defined in the ImageN API (`org.eclipse.imagen`).

**Table B-4 Summary of ImageN Interfaces** <a name="table-B-4"></a>

| Interface                | Description |
| ------------------------ | ------------|
| CollectionImageFactory   | Abbreviated CIF, this interface is intended to be implemented by classes that wish to act as factories to produce different collection image operators. |
| ImageFunction            | A common interface for vector-valued functions that are to be evaluated at positions in the X-Y coordinate system |
| ImageImageN              | The top-level ImageN image type, implemented by all ImageN image classes |
| OperationDescriptor      | Describes a family of implementations of a high-level operation (RIF) that are to be added to an OperationRegistry |
| PropertyGenerator        | An interface through which properties may be computed dynamically with respect to an environment of pre-existing properties |
| PropertySource           | Encapsulates the set of operations involved in identifying and reading properties |
| TileCache                | Implements a caching mechanism for image tiles. The TileCache is a central place for OpImages to cache tiles they have computed. The tile cache is created with a given capacity, measured in tiles |
| TileScheduler            | Implements a mechanism for scheduling tile calculation. |

### B.3.2 ImageN Classes

[Table B-5](#table-B-5) lists and briefly describes
the classes defined in the Eclipse ImageN API
(`org.eclipse.imagen`).

**Table B-5 Summary of ImageN Classes** <a name="table-B-5"></a>

| Class                          | Description |
|--------------------------------|-------------|
| AreaOpImage                    | Extends: `OpImage` <br/> An abstract base class for image operators that require only a fixed rectangular source region around a source pixel in order to compute each each destination pixel. |
| BorderExtender                 | An abstract superclass for classes that extend a WritableRaster with additional pixel data taken from a PlanarImage. |
| BorderExtenderConstant         | Extends: `BorderExtender` <br/>  Implements border extension by filling all pixels outside of the image bounds with constant values. |
| BorderExtenderCopy             | Extends: `BorderExtender` <br/> Implements border extension by filling all pixels outside of the image bounds with copies of the edge pixels. |
| BorderExtenderReflect          | Extends: `BorderExtender` <br/> BorderExtenderReflect Implements border extension by filling all pixels outside of the image bounds with copies of the whole image. |
| BorderExtenderWrap             | Extends: `BorderExtender` <br/> Implements border extension by filling all pixels outside of the image bounds with copies of the whole image. |
| BorderExtenderZero             | Extends: `BorderExtender` <br/> Implements border extension by filling all pixels outside of the image bounds with zeros. |
| CanvasJAI                      | Extends: `java.awt.Canvas` <br/> Automatically returns an instance of GraphicsJAI from its getGraphics method |
| CollectionImage                | Extends: `ImageImageN` <br/>  Implements: java.util.Collection <br/>  An abstract superclass for classes representing a collection of objects. |
| CollectionOp                   | Extends: `CollectionImage` <br/> A node in a rendered imaging chain representing a CollectionImage. |
| ColorCube                      | Extends: `LookupTableImageN`<br/>  Represents a color cube lookup table that provides a fixed, invertible mapping between tables indices and sample values. |
| ComponentSampleModelImageN     | Extends: `ComponentSampleModel`<br/>  Represents image data that is stored such that each sample of a pixel occupies one data element of the DataBuffer. |
| CoordinateImage                | Extends: `java.lang.Object` <br/> Represents an image that is associated with a coordinate. This class is used with ImageStack |
| DataBufferDouble               | Extends: `java.awt.image.DataBuffer` <br/> Stores DataBuffer data internally in double form |
| DataBufferFloat                | Extends: `java.awt.image.DataBuffer` <br/> Stores DataBuffer data internally in float form |
| DisplayOpImage                 | Extends: `OpImage` <br/> A placeholder for display functionality |
| FloatDoubleColorModel          | Extends: `ComponentColorModel` <br/> A ColorModel class that works with pixel values that represent color and alpha information as separate samples, using float or double elements, and that store each sample in a separate data element |
| GraphicsJAI                    | Extends: `java.awt.Graphics2D` <br/> An extension of java.awt.Graphics and java.awt.Graphics2D that will support new drawing operations |
| Histogram                      | Extends: `java.lang.Object` <br/> Accumulates histogram information on an image. A histogram counts the number of image samples whose values lie within a given range of values, or *bin* |
| ImageLayout                    | Extends: `java.lang.Object` <br/> Implements: `java.lang.Clonable` <br/> Describes the desired layout of an OpImage |
| ImageMIPMap                    | Extends: `ImageCollection` <br/> Represents a stack of images with a fixed operational relationship between adjacent slices |
| ImagePyramid                   | Extends: `ImageCollection` <br/> Represents a stack of images with a fixed operational relationship between adjacent slices |
| ImageSequence                  | Extends: `ImageCollection` <br/> Represents a sequence of images with associated timestamps and camera positions that can be used to represent video or time-lapse photography |
| ImageStack                     | Extends: `ImageCollection` <br/> Represents a group of images, each with a defined spatial orientation in a common coordinate system, such as CT scans or seismic volumes |
| IntegerSequence                | Extends: `java.lang.Object` <br/> Represents an image that is associated with a coordinate. This class is used with ImageStack |
| Interpolation                  | Extends: `java.lang.Object` <br/> Encapsulates a particualr algorithm for performing sampling on a regular grid of pixels using a local neighborhood. It is intended to be used by operations that resample their sources, including affine mapping and warping |
| InterpolationBicubic           | Extends: `InterpolationTable` <br/> Performs bicubic interpolation |
| InterpolationBicubic2          | Extends: `InterpolationTable` <br/> Performs bicubic interpolation using a different polynomial than InterpolationBicubic |
| InterpolationBilinear          | Extends: `Interpolation` <br/> Represents bilinear interpolation |
| InterpolationNearest           | Extends: `Interpolation` <br/> Represents nearest-neighbor interpolation |
| InterpolationTable             | Extends: `Interpolation` <br/> Represents nearest-neighbor interpolation |
| ImageN                         | Extends: `java.lang.Object` <br/> A convenience class for instantiating operations |
| KernelImageN                   | Extends: `java.lang.Object` <br/> A convolution kernel, used by the Convolve operation |
| LookupTableImageN              | Extends: `java.awt.image.renderable`, `ParameterBlock` <br/> A convenience subclass of ParameterBlock that allows the use of default parameter values and getting/setting parameters by name |
| PerspectiveTransform           | Extends: `java.lang.Object` <br/> Implements: `java.lang.Cloneable`, `java.io.Serializable` <br/> A 2D perspective (or projective) transform, used by various OpImages |
| PlanarImage                    | Extends: `java.awt.Image` <br/> Implements: `java.awt.image.RenderedImage` <br/> A fundamental base class representing two-dimensional images |
| PointOpImage                   | Extends: `OpImage` <br/> An abstract base class for image operators that require only a single source pixel to compute each destination pixel |
| PropertyGeneratorImpl          | Extends: `java.lang.Object` <br/> A utility class to simplify the writing of property generators |
| RasterAccessor                 | Extends: `java.lang.Object` <br/> An adapter class for presenting image data in a ComponentSampleModel format, even if the data is not stored that way |
| RasterFactory                  | A convenience class for the construction of various types of WritableRaster and SampleModel objects |
| RasterFormatTag                | Encapsulates some of the information needed for RasterAccessor to understand how a Raster is laid out |
| RemoteImage                    | Extends: `PlanarImage` <br/> An implementation of RenderedImage that uses a RMIImage as its source |
| RenderableGraphics             | Extends: `Graphics2D` <br/> Implements: `RenderableImage`, `Serializable` <br/> An implementation of Graphics2D with RenderableImage semantics |
| RenderableImageAdapter         | Extends: `java.lang.Object` <br/> Implements: `java.awt.image.renderable.RenderableImage`, `PropertySource` <br/> An adapter class for externally-generated RenderableImages |
| RenderableOp                   | Extends: `java.awt.image.renderable.RenderableImageOp` <br/> Implements: `PropertySource` <br/> A ImageN version of RenderableImageOp |
| RenderedImageAdapter           | Extends: `PlanarImage` <br/> A PlanarImage wrapper for a non-writable RenderedImage |
| RenderedOp                     | Extends: `PlanarImage` <br/> A node in a rendered imaging chain |
| ROI                            | Extends: `java.lang.Object` <br/> Represents a region of interest of an image |
| ROIShape                       | Extends: `ROI` <br/> Represents a region of interest within an image as a Shape |
| ScaleOpImage                   | Extends: `WarpOpImage` <br/> Used by further extension classes that perform scale-like operations and thus require rectilinear backwards mapping and padding by the resampling filter dimensions |
| SequentialImage                | Extends: `java.lang.Object` <br/> Represents an image that is associated with a time stamp and a camera position. Used with ImageSequence |
| SnapshotImage                  | Extends: `PlanarImage`: <br/> Implements: `java.awt.image.TileObserver` <br/> Provides an arbitrary number of synchronous views of a possibly changing WritableRenderedImage |
| SourcelessOpImage              | Extends: `OpImage` <br/> An abstract base class for image operators that have no image sources |
| StatisticsOpImage              | Extends: `OpImage` <br/> An abstract base class for image operators that compute statistics on a given region of an image and with a given sampling rate |
| TiledImage                     | Extends: `PlanarImage` <br/> Implements: `java.awt.image.WritableRenderedImage` <br/> A concrete implementation of WritableRenderedImage |
| UntiledOpImage                 | Extends: `OpImage` <br/> A general class for single-source operations in which the values of all pixels in the source image contribute to the value of each pixel in the destination image |
| Warp                           | Extends: `java.lang.Object` <br/> A description of an image warp |
| WarpAffine                     | Extends: `WarpPolynomial` <br/> A description of an Affine warp |
| WarpCubic                      | Extends: `WarpPolynomial` <br/> A cubic-based description of an image warp |
| WarpGeneralPolynomial          | Extends: `WarpPolynomial` <br/> A general polynomial-based description of an image warp |
| WarpGrid                       | Extends: `Warp` <br/> A regular grid-based description of an image warp |
| WarpOpImage                    | Extends: `OpImage` <br/> A general implementation of image warping, and a superclass for other geometric image operations |
| WarpPerspective                | Extends: `Warp` <br/> A description of a perspective (projective) warp |
| WarpPolynomial                 | Extends: `Warp` <br/> A polynomial-based description of an image warp |
| WarpQuadratic                  | Extends: `WarpPolynomial` <br/> A quadratic-based description of an image warp |
| WritableRenderedImage-Adapter  | Extends: `RenderedImageAdapter` <br/> Implements: `java.awt.image.WritableRenderedImage` <br/> A PlanarImage wrapper for a WritableRenderedImage. |

### B.3.3 ImageN Iterator Interfaces

[Table B-6](api-summary/index.html) lists the ImageN iterator classes
(`org.eclipse.imagen.iterator`).

**Table B-6 ImageN Iterator Interfaces** <a name="table-B-6"></a>

| Interface          | Description |
| ------------------ | ------------|
| RandomIter         | An iterator that allows random read-only access to any sample within its bounding rectangle. |
| RectIter           | An iterator for traversing a read-only image in top-to-bottom, left-to-right order. |
| RookIter           | An iterator for traversing a read-only image using arbitrary up-down and left-right moves. |
| WritableRandomIter | Extends: `RandomIter` <br/> An iterator that allows random read/write access to any sample within its bounding rectangle |
|  WritableRectIter  | Extends: `RectIter` <br/> An iterator for traversing a read/write image in top-to-bottom, left-to-right order |
| WritableRookIter   | Extends: `RookIter`, `WritableRectIter` <br/> An iterator for traversing a read/write image using arbitrary up-down and left-right moves. |
### B.3.4 Iterator Classes

[Table B-7](api-summary/index.html) lists the ImageN iterator classes
(`org.eclipse.imagen.iterator`).

**Table B-7 ImageN Iterator Classes** <a name="table-B-7"></a>

| Class              | Description |
| ------------------ | ------------|
| RandomIterFactory  | Extends: `java.lang.Object` <br/> A factory class to instantiate instances of the RandomIter and WritableRandomIter interfaces on sources of type Raster, RenderedImage, and WritableRenderedImage |
| RectIterFactory    | Extends: `java.lang.Object` <br/> A factory class to instantiate instances of the RectIter and WritableRectIter interfaces on sources of type Raster, RenderedImage, and WritableRenderedImage |
| RookIterFactory    | Extends: `java.lang.Object` <br/> A factory class to instantiate instances of the RookIter and WritableRookIter interfaces on sources of type Raster, RenderedImage, and WritableRenderedImage. |
### B.3.5 Operator Classes

[Table B-8](api-summary/index.html) lists the operator classes
(`org.eclipse.imagen.operator`). These classes extend the ImageN
OperationDescriptor class.

**Table B-8 ImageN Operator Classes** <a name="table-B-8"></a>
  
| Class              | Description |
| ------------------ | ------------|
| AbsoluteDescriptor                | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the Absolute operation, which gives the mathematical absolute value of the pixel values of a source image |
| AddCollectionDescriptor           | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the AddCollection operation, which takes a collection of rendered images, and adds every set of pixels, one from each source image of the corresponding position and band |
| AddConstDescriptor                | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the AddConst operation, which adds one of a set of constant values to the pixel values of a source image on a per-band basis |
| AddConstToCollection-Descriptor   | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the AddConstToCollection operation, which adds constants to a collection of rendered images |
| AddDescriptor                     | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the Add operation, which adds the pixel values of two source images on a per-band basis |
| AffineDescriptor                  | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the Affine operation, which performs an affine mapping between a source and a destination image |
| AndConstDescriptor                | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the AndConst operation, which performs a bitwise logical AND between the pixel values of a source image with one of a set of per-band constants |
| AndDescriptor                     | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the And operation, which performs a bitwise logical AND between the pixel values of the two source images on a per-band basis |
| AWTImageDescriptor                | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the AWTImage operation, which imports a standard AWT image into ImageN |
| BandCombineDescriptor             | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the BandCombine operation, which computes an arbitrary linear combination of the bands of a source image for each band of a destination image, using a specified matrix |
| BandSelectDescriptor              | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the BandSelect operation, which copies the pixel data from a specified number of bands in a source image to a destination image in a specified order |
| BMPDescriptor                     | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the BMP operation, which reads BMP image data file from an input stream |
| BorderDecriptor                   | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the Border operation, which adds a border around an image |
| BoxFilterDescriptor               | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the BoxFilter operation, which determines the intensity of a pixel in an image by averaging the source pixels within a rectangular area around the pixel |
| ClampDescriptor                   | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the Clamp operation, which sets all the pixel values below a \"low\" value to that low value, and sets all the pixel values above a \"high\" value to that high value |
| ColorConvertDescriptor            | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the ColorConvert operation, which performs a pixel-by-pixel color conversion of the data in a source image |
| CompositeDescriptor               | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the Composite operation, which combines two images based on their alpha values at each pixel |
| ConjugateDescriptor               | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the Conjugate operation, which negates the imaginary components of pixel values of an image containing complex data |
| ConstantDescriptor                | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the Constant operation, which defines a multi-banded, tiled rendered image with constant pixel values |
| ConvolveDescriptor                | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the Convolve operation, which computes each output sample by multiplying elements of a kernel with the samples surrounding a particular source sample |
| CropDescriptor                    | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the Crop operation, which crops a rendered or renderable image to a specified rectangular area |
| DCTDescriptor                     | Extends: `OperationDescriptorImpl` <br/> An operation descriptor for the DCT operation, which computes the even discrete cosine transform of an image |
| DFTDescriptor                     | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the DFT operation, which computes the discrete Fourier transform of an image |
| DivideByConstDescriptor           | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the DivideByConst operation, which divides the pixel values of a source image by a constant |
| DivideComplexDescriptor           | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the DivideComplex operation, which divides two images representing complex data |
| DivideDescriptor                  | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the Divide operation, which divides the pixel values of one first source image by the pixel values of another source image on a per-band basis |
| DivideIntoConstDescriptor         | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the DivideIntoConst operation, which divides a constant by the pixel values of a source image |
| EncodeDescriptor                  | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the Encode operation, which stores an image to an OutputStream |
| ErrorDiffusionDescriptor          | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the ErrorDiffusion operation, which performs color quantization by finding the nearest color to each pixel in a supplied color map |
| ExpDescriptor                     | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the Exp operation, which takes the exponential of the pixel values of an image |
| ExtremaDescriptor                 | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the Extrema operation, which scans an image and finds the image-wise maximum and minimum pixel values for each band |
| FileLoadDescriptor                | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the FileLoad operation, which reads an image from a file |
| FileStoreDescriptor               | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the FileStore operation, which stores an image to a file |
| FormatDescriptor                  | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the Format operation, which reformats an image |
| FPXDescriptor                     | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the FPX operation, which reads FlashPix data from an input stream |
| GIFDescriptor                     | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the GIF operation, which reads GIF data from an input stream |
| GradientMagnitudeDescriptor       | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the Gradient operation, which is an edge detector that computes the magnitude of the image gradient vector in two orthogonal directions |
| HistogramDecriptor                | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the Histogram operation, which scans a specified region of an image and generates a histogram based on the pixel values within that region of the image |
| IDCTDescriptor                    | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the IDCT operation, which computes the inverse discrete cosine transform of an image |
| IDFTDescriptor                    | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the IDFT operation, which computes the inverse discrete Fourier transform of an image |
| IIPDescriptor                     | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the IIP operation, which reads an image from an IIP server and creates a RenderedImage or a RenderableImage based on data from the server |
| IIPResolutionDescriptor           | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the IIPResolution operation, which reads an image at a particular resolution from an IIP server and creates a RenderedImage based on the data from the server |
| ImageFunctionDescriptor           | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the ImageFunction operation, which generates an image on the basis of a functional description provided by an object that is an instance of a class that implements the ImageFunction interface |
| InvertDescriptor                  | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the Invert operation, which inverts the pixel values of an image |
| JPEGDescriptor                    | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the JPEG operation, which reads a standard JPEG (JFIF) file |
| LogDescriptor                     | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the Log operation, which takes the logarithm of the pixel values of an image |
| LookupDescriptor                  | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the Lookup operation, which performs general table lookup on an image |
| MagnitudeDescriptor               | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the Magnitude operation, which computes the magnitude of each pixel of an image |
| MagnitudeSquaredDescriptor        | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the MagnitudeSquared operation, which computes the squared magnitude of each pixel of a complex image |
| MatchCDFDescriptor                | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the MatchCDF operation, which matches pixel values to a supplied cumulative distribution function (CDF). |
| MaxDescriptor                     | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the Max operation, which computes the pixelwise maximum value of two images. |
| MeanDescriptor                    | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the Mean operation, which scans a specified region of an image and computes the image-wise mean pixel value for each band within the region. |
| MedianFilterDescriptor            | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the MedianFilter operation, which is useful for removing isolated lines or pixels while preserving the overall appearance of an image. |
| MinDescriptor                     | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the Min operation, which computes the pixelwise minimum value of two images. |
| MultiplyComplexDescriptor         | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the MultiplyComplex operation, which multiplies two images representing complex data. |
| MultiplyConstDescriptor           | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the MultiplyConst operation, which multiplies the pixel values of a source image with a constant on a per-band basis. |
| MultiplyDescriptor                | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the Multiply operation, which multiplies the pixel values of two source images on a per-band basis. |
| NotDescriptor                     | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the Multiply operation, which performs a bitwise logical NOT operation on each pixel of a source image on a per-band basis. |
| OrConstDescriptor                 | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the OrConst operation, which performs a bitwise logical OR between the pixel values of a source image with a constant on a per-band basis. |
| OrderedDitherDescriptor           | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the OrderedDither operation, which performs color quantization by finding the nearest color to each pixel in a supplied color cube and \"shifting\" the resulting index value by a pseudo-random amount determined by the values of a supplied dither mask. |
| OrDescriptor                      | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the Or operation, which performs a bitwise logical OR between the pixel values of the two source images on a per-band basis. |
| OverlayDescriptor                 | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the Overlay operation, which overlays one image on top of another image. |
| PatternDescriptor                 | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the Pattern operation, which defines a tiled image consisting of a repeated pattern. |
| PeriodicShiftDescriptor           | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the PeriodicShift operation, which computes the periodic translation of an image. |
| PhaseDescriptor                   | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the Phase operation, which computes the phase angle of each pixel of an image. |
| PiecewiseDescriptor               | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the Piecewise operation, which applies a piecewise pixel value mapping to an image. |
| PNGDescriptor                     | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the PNG operation, which reads a PNG input stream. |
| PNMDescriptor                     | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the PNM operation, which reads a standard PNM file, including PBM, PGM, and PPM images of both ASCII and raw formats. |
| PolarToComplexDescriptor          | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the PolarToComplex operation, which computes a complex image from a magnitude and a phase image. |
| RenderableDescriptor              | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the Renderable operation, which produces a RenderableImage from a RenderedImage. |
| RescaleDescriptor                 | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the Rescale operation, which maps the pixel values of an image from one range to another range. |
| RotateDescriptor                  | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the Rotate operation, which rotates an image about a given point by a given angle. |
| ScaleDescriptor                   | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the Scale operation, which translates and resizes an image. |
| ShearDescriptor                   | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the Shear operation, which shears an image horizontally or vertically. |
| StreamDescriptor                  | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the Stream operation, which reads java.io.InputStream files. |
| SubtractConstDescriptor           | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the SubtractConst operation, which subtracts one of a set of constant values from the pixel values of a source image on a per-band basis. |
| SubtractDescriptor                | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the Subtract operation, which subtracts the pixel values of the second source image from the first source image on a per-band basis. |
| SubtractFromConstDescriptor       | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the SubtractFromConst operation, which subtracts the pixel values of a source image from one of a set of constant values on a per-band basis. |
| ThresholdDescriptor               | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the Threshold operation, which maps all the pixel values of an image that fall within a given range to one of a set of per-band constants. |
| TIFFDescriptor                    | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the TIFF operation, which reads TIFF 6.0 data from an input stream. |
| TranslateDescriptor               | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the Translate operation, which copies an image to a new location in the plane. |
| TransposeDescriptor               | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the Transpose operation, which flips or rotates an image. |
| URLDescriptor                     | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the URL operation, which reads an image from a file, via a URL path. |
| WarpDescriptor                    | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the Warp operation, which performs general warping on an image. |
| XorConstDescriptor                | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the XorConst operation, which performs a bitwise logical XOR between the pixel values of a source image with a constant. |
| XorDescriptor                     | Extends: `OperationDescriptorImpl` <br/> An OperationDescriptor for the Xor operation, which performs a bitwise logical XOR between the pixel values of two source images on a per-band basis. |

### B.3.6 Widget Interfaces

[Table B-9](api-summary/index.html) lists the ImageN widget
interfaces (`org.eclipse.imagen.widget`).

**Table B-9 ImageN Widget Interfaces** <a name="table-B-9"></a>

| Interface          | Description |
| ------------------ | ------------|
| ViewportListener   | Used by the ScrollingImagePanel class to inform listeners of the current viewable area of the image. |

### B.3.7 Widget Classes

[Table B-10](#table-B-10) lists the ImageN widget classes
(`org.eclipse.imagen.widget`).

**Table B-10 ImageN Widget Classes** <a name="table-B-10"></a>

| Class              | Description |
| ------------------ | ------------|
| ImageCanvas |Extends: `java.awt.Canvas` <br/> A simple output widget for a RenderedImage. This class can be used in any context that calls for a Canvas. |
| ScrollingImagePanel |Extends: `java.awt.Panel` <br/> Implements: `java.awt.event.AdjustmentListener`, `java.awt.event.MouseListener`, `java.awt.event.MouseMotionListener` <br/> An extension of java.awt.Panel that contains an ImageCanvas and vertical and horizontal scrollbars. |
