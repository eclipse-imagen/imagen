---
layout: default
title: Java AWT Imaging
parent: Programming Guide
nav_order: 3
---

\


  ----------------------------------------
    C H A P T E R![](shared/sm-space.gif)2
  ----------------------------------------


+----------------------------------------------------------------------:+
| -------------------------------------------------------------------   |
|                                                                       |
| Java AWT Imaging                                                      |
+-----------------------------------------------------------------------+

\
\
\

**D**IGITAL imaging in Java has been supported since its first
release, through the **java.awt** and **java.awt.image** class
packages. The image-oriented part of these class packages is referred
to as *AWT Imaging* throughout this guide.


2.1 ![](shared/space.gif)Introduction
-------------------------------------

The Java Advanced Imaging (JAI) API supports three imaging models:

-   The producer/consumer (push) model - the basic AWT imaging model


-   The immediate mode model - an advanced AWT imaging model


-   The pipeline (pull) model - The JAI model

[Table 2-1](J2D-concepts.doc.html#52516) lists the interfaces and
classes for each of the three models.

  --------------------------------------------------------------------------------------------------
  [AWT Push Model]{#52522}   [Java 2D Immediate Mode Model]{#52524}   [Pull Model]{#52526}
  -------------------------- ---------------------------------------- ------------------------------
  [Image]{#52528}\           [BufferedImage]{#52533}\                 [RenderableImage]{#52538}\
  [ImageProducer]{#52529}\   [Raster]{#52534}\                        [RenderableImageOp]{#52539}\
  [ImageConsumer]{#52530}\   [BufferedImageOp]{#52535}\               [RenderedOp]{#52540}\
  [ImageObserver]{#52531}\   [RasterOp]{#52536}\                      [RenderableOp]{#52541}\
                                                                      [TiledImage]{#52542}\

  --------------------------------------------------------------------------------------------------

  :  **[*Table 2-1* ![](shared/sm-blank.gif) Imaging Model Interfaces
  and Classes]{#52516}**


### 2.1.1 ![](shared/space.gif)The AWT Push Model

The AWT push model, supported through the `java.awt` class package, is
a simple filter model of image producers and consumers for image
processing. An `Image` object is an abstraction that is not
manipulated directly; rather it is used to obtain a reference to
another object that implements the `ImageProducer` interface. Objects
that implement this interface are in turn attached to objects that
implement the ImageConsumer interface. Filter objects implement both
the producer and consumer interfaces and can thus serve as both a
source and sink of image data. Image data has associated with it a
ColorModel that describes the pixel layout within the image and the
interpretation of the data.

To process images in the push model, an Image object is obtained from
some source (for example, through the `Applet.getImage()` method). The
`Image.getSource()` method can then be used to get the `ImageProducer`
for that `Image`. A series of FilteredImageSource objects can then be
attached to the ImageProducer, with each filter being an ImageConsumer
of the previous image source. AWT Imaging defines a few simple filters
for image cropping and color channel manipulation.

The ultimate destination for a filtered image is an AWT `Image`
object, created by a call to, for example, `Component.createImage()`.
Once this consumer image has been created, it can by drawn upon the
screen by calling `Image.getGraphics()` to obtain a `Graphics` object
(such as a screen device), followed by `Graphics.drawImage()`.

AWT Imaging was largely designed to facilitate the display of images
in a browser environment. In this context, an image resides somewhere
on the network. There is no guarantee that the image will be available
when required, so the AWT model does not force image filtering or
display to completion. The model is entirely a *push* model. An
ImageConsumer can never ask for data; it must wait for the
ImageProducer to \"push\" the data to it. Similarly, an ImageConsumer
has no guarantee about when the data will be completely delivered; it
must wait for a call to its `ImageComplete()` method to know that it
has the complete image. An application can also instantiate an
ImageObserver object if it wishes to be notified about completion of
imaging operations.

AWT Imaging does not incorporate the idea of an image that is backed
by a persistent image store. While methods are provided to convert an
input memory array into an ImageProducer, or capture an output memory
array from an ImageProducer, there is no notion of a persistent image
object that can be reused. When data is wanted from an Image, the
programmer must retrieve a handle to the Image\'s ImageProducer to
obtain it.

The AWT imaging model is not amenable to the development of
high-performance image processing code. The push model, the lack of a
persistent image data object, the restricted model of an image filter,
and the relative paucity of image data formats are all severe
constraints. AWT Imaging also lacks a number of common concepts that
are often used in image processing, such as operations performed on a
region of interest in an image.


### 2.1.2 ![](shared/space.gif)AWT Push Model Interfaces and Classes

The following are the Java interfaces and classes associated with the
AWT push model of imaging.

  -------------------------------------------------------------------------------------------------
  [Interface]{#52559}   [Description]{#52561}
  --------------------- ---------------------------------------------------------------------------
  [Image]{#52582}\      [Extends: Object]{#52584}\
                        [The superclass of all classes that represent graphical images.]{#52585}\

  -------------------------------------------------------------------------------------------------

  :  **[*Table 2-2* ![](shared/sm-blank.gif) Push Model Imaging
  Interfaces]{#52555}**

  ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
  [Class]{#52598}                  [Description]{#52600}
  -------------------------------- ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
  [ColorModel]{#52680}\            [An abstract class that encapsulates the methods for translating a pixel value to color components (e.g., red, green, blue) and an alpha component.]{#52684}\

  [FilteredImageSource]{#52695}\   [An implementation of the ImageProducer interface which takes an existing image and a filter object and uses them to produce image data for a new filtered version of the original image.]{#52699}\

  [ImageProducer]{#52602}\         [The interface for objects that can produce the image data for Images. Each image contains an ImageProducer that is used to reconstruct the image whenever it is needed, for example, when a new size of the Image is scaled, or when the width or height of the Image is being requested.]{#52621}\

  [ImageConsumer]{#52606}\         [The interface for objects expressing interest in image data through the ImageProducer interfaces. When a consumer is added to an image producer, the producer delivers all of the data about the image using the method calls defined in this interface.]{#52641}\

  [ImageObserver]{#52610}\         [An asynchronous update interface for receiving notifications about Image information as the Image is constructed.]{#52653}\
  ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

  :  **[*Table 2-3* ![](shared/sm-blank.gif) Push Model Imaging
  Classes]{#52594}**


2.2 ![](shared/space.gif)The Immediate Mode Model
-------------------------------------------------

To alleviate some of the restrictions of the original AWT imaging
model and to provide a higher level of abstraction, a new
specification called the *Java 2D* API was developed. This new API
extends AWT\'s capabilities for both two-dimensional graphics and
imaging. In practice, the Java 2D package is now merged into the AWT
specification and is a part of the Java Core (and thus available in
all Java implementations). However, for purposes of discussion, the
distinction between Java 2D and the AWT is preserved in this chapter.

The Java 2D API specifies a set of classes that extend the Java AWT
classes to provide extensive support for both two-dimensional graphics
and imaging. The support for 2D graphics is fairly complete, but will
not be discussed further here.

For digital imaging, the Java 2D API retains to some extent the AWT
producer/consumer model but adds the concept of a memory-backed
persistent image data object, an extensible set of 2D image filters, a
wide variety of image data formats and color models, and a more
sophisticated representation of output devices. The Java 2D API also
introduces the notion of resolution-independent image rendering by the
introduction of the *Renderable* and *Rendered* interfaces, allowing
images to be pulled through a chain of filter operations, with the
image resolution selected through a rendering context.

The concepts of rendered and renderable images contained in the Java
2D API are essential to JAI. The next few sections explain these
concepts; complete information about the classes discussed can be
found in *The Java 2D API Specification* and the *Java 2D API White
Paper*.


### 2.2.1 ![](shared/space.gif)Rendering Independence

Rendering independence for images is a poorly understood topic because
it is poorly named. The more general problem is \"resolution
independence,\" the ability to describe an image as you want it to
appear, but independent of any specific instance of it. Resolution is
but one feature of any such rendering. Others are the physical size,
output device type, color quality, tonal quality, and rendering speed.
A rendering-independent description is concerned with none of these.

In this document, the term *rendering-independent* is for the more
general concept instead of *resolution-independent*. The latter term
is used to specifically refer to independence from final display
resolution.

For a rendering-independent description of an image, two fundamental
elements are needed:

-   An unrendered source (sometimes called a *resolution-independent
    source*). For a still image, this is, conceptually, the viewfinder
    of an idealized camera trained on a real scene. It has no logical
    \"size.\" Rather, one knows what it looks like and can imagine
    projecting it onto any surface. Furthermore, the ideal camera has
    an ideal lens that is capable of infinite zooming. The
    characteristics of this image are that it is dimensional, has a
    native aspect ratio (that of the capture device), and may have
    properties that could be queried.


-   Operators for describing how to change the character of the image,
    independent of its final destination. It can be useful to think of
    this as a pipe of operations.

Together, the unrendered source and the operators specify the visual
character that the image should have when it is rendered. This
specification can then be associated with any device, display size, or
rendering quality. The primary power of rendering independence is that
the same visual description can be routed to any display context with
an optimal result.


### 2.2.2 ![](shared/space.gif)Rendering-independent Imaging in Java AWT

The Java AWT API architecture integrates a model of rendering
independence with a parallel, device-dependent (rendered) model. The
rendering-independent portion of the architecture is a superset of,
rather than a replacement for, the traditional model of
device-dependent imaging.

The Java AWT API architecture supports context-dependent adaptation,
which is superior to full image production and processing.
Context-dependent adaptation is inherently more efficient and thus
also suited to network sources. Beyond efficiency, it is the mechanism
by which optimal image quality can be assured in any context.

The Java AWT API architecture is essentially synchronous is nature.
This has several advantages, such as a simplified programming model
and explicit controls on the type and order of results. However, the
synchronous nature of Java AWT has one distinct disadvantage in that
it is not well suited to notions of progressive rendering or network
resources. These issues are addressed in JAI.


### 2.2.3 ![](shared/space.gif)The Renderable Layer vs. the Rendered Layer

The Java AWT API architecture provides for two integrated imaging
layers: renderable and rendered.


#### 2.2.3.1 ![](shared/space.gif)Renderable Layer

The renderable layer is a rendering-independent layer. All the
interfaces and classes in the Java AWT API have `renderable` in their
names.

The renderable layer provides image sources that can be optimally
reused multiple times in different contexts, such as screen display or
printing. The renderable layer also provides imaging operators that
take rendering-independent parameters. These operators can be linked
to form *chains*. The layer is essentially synchronous in the sense
that it \"pulls\" the image through the chain whenever a rendering
(such as to a display or a file) is requested. That is, a request is
made at the sink end of the chain that is passed up the chain to the
source. Such requests are context-specific (such as device specific),
and the chain adapts to the context. Only the data required for the
context is produced.


#### 2.2.3.2 ![](shared/space.gif)Rendered Layer

Image sources and operators in the parallel *Rendered layer* (the
interfaces and classes have `rendered` in their names) are
context-specific. A `RenderedImage` is an image that has been rendered
to fulfill the needs of the context. Rendered layer operators can also
be linked together to form chains. They take context-dependent
parameters. Like the Renderable layer, the Rendered layer implements a
synchronous \"pull\" model.


#### 2.2.3.3 ![](shared/space.gif)Using the Layers

Structurally, the Renderable layer is lightweight. It does not
directly handle pixel processing. Rather, it makes use of operator
objects from the Rendered layer. This is possible because the operator
classes from the Rendered layer can implement an interface (the
`ContextualRenderedImageFactory` interface) that allows them to adapt
to different contexts.

Since the Rendered layer operators implement this interface, they
house specific operations in their entirety. That is, all the
intelligence required to function in both the Rendered and Renderable
layers is housed in a single class. This simplifies the task of
writing new operators and makes extension of the architecture
manageable.

[Figure 2-1](J2D-concepts.doc.html#51378) shows a renderable chain.
The chain has a sink attached (a Graphics2D object), but no pixels
flow through the chain yet.


------------------------------------------------------------------------

![](J2D-concepts.doc.anc.gif)

------------------------------------------------------------------------


***Figure 2-1* ![](shared/sm-blank.gif) A Renderable Chain**

You may use either the Renderable or Rendered layer to construct an
application. Many programmers will directly employ the Rendered layer,
but the Renderable layer provides advantages that greatly simplify
imaging tasks. For example, a chain of Renderable operators remains
editable. Parameters used to construct the chain can be modified
repeatedly. Doing so does not cause pixel value computation to occur.
Instead, the pixels are computed only when they are needed by a
specific rendition obtained from a `RenderableImage` by passing it
defined *render contexts*.


### 2.2.4 ![](shared/space.gif)The Render Context

The renderable layer allows for the construction of a chain of
operators (`RenderableImageOps`) connected to a `RenderableImage`
source. The end of this chain represents a new `RenderableImage`
source. The implication of this is that `RenderableImageOps` must
implement the same interface as sources: `RenderableImageOp`
implements `RenderableImage`.

Such a source can be asked to provide various specific
`RenderedImage`s corresponding to a specific context. The required
size of the `RenderedImage` in the device space (the size in pixels)
must be specified. This information is provided in the form of an
affine transformation from the user space of the Renderable source to
the desired device space.

Other information can also be provided to the source (or chain) to
help it perform optimally for a specific context. A preference for
speed over image quality is an example. Such information is provided
in the form of an extensible hints table. It may also be useful to
provide a means to limit the request to a specific area of the image.

The architecture refers to these parameters collectively as a *render
context*. The parameters are housed in a `RenderContext` class. Render
contexts form a fundamental link between the Renderable and Rendered
layers. A `RenderableImage` source is given a `RenderContext` and, as
a result, produces a specific rendering, or `RenderedImage`. This is
accomplished by the Renderable chain instantiating a chain of Render
layer objects. That is, a chain of `RenderedImage`s corresponding to
the specific context, the `RenderedImage` object at the end of the
chain being returned to the user.


2.3 ![](shared/space.gif)Renderable and Rendered Classes
--------------------------------------------------------

Many users will be able to employ the Renderable layer, with the
advantages of its rendering-independent properties for most imaging
purposes. Doing so eliminates the need to deal directly with pixels,
greatly simplifying image manipulation. However, in many cases it is
either necessary or desirable to work with pixels and the Rendered
layer is used for this purpose.

The architecture of the provided classes is discussed in this section.
Extending the model by writing new operators or algorithms in the Java
2D API is discussed. Details of how the Rendered layer functions
internally within the Renderable layer are also covered.


### 2.3.1 ![](shared/space.gif)The Renderable Layer

The renderable layer is primarily defined by the `RenderableImage`
interface. Any class implementing this interface is a renderable image
source, and is expected to adapt to `RenderContext`s.
`RenderableImage`s are referenced through a user-defined coordinate
system. One of the primary functions of the `RenderContext` is to
define the mapping between this user space and the specific device
space for the desired rendering.

A chain in this layer is a chain of `RenderableImage`s. Specifically,
it is a chain of `RenderableImageOp`s (a class that implements
`RenderableImage`), ultimately sourced by a `RenderableImage`.

There is only one `RenderableImageOp` class. It is a lightweight,
general purpose class that takes on the functionality of a specific
operation through a parameter provided at instantiation time. That
parameter is the name of a class that implements a
`ContextualRenderedImageFactory` (known as a CRIF, for short). Each
instantiation of `RenderableImageOp` derives its specific
functionality from the named class. In this way, the Renderable layer
is heavily dependent on the Rendered layer.

**[*Table 2-4* ![](shared/sm-blank.gif) The Renderable Layer
Interfaces and Classes]{#53782}**

[Type]{#53788}

[Name]{#53790}

[Description]{#53792}

[Interface]{#53794}\

[RenderableImage]{#53797}\

[A common interface for rendering-independent images (a notion that
subsumes resolution independence).]{#53799}\

[ContextualRenderedImage-Factory]{#53804}\

[Extends: RenderedImageFactory]{#53806}\
[Provides an interface for the functionality that may differ between
instances of RenderableImageOp.]{#53807}\

[Class]{#53809}\

[ParameterBlock]{#53811}\

[Extends: Object]{#53813}\
[Implements: Cloneable, Serializable]{#53814}\
[Encapsulates all the information about sources and parameters
(expressed as base types or Objects) required by a RenderableImageOp
and other future classes that manipulate chains of imaging
operators.]{#53815}\

[RenderableImageOp]{#53819}\

[Extends: Object]{#53821}\
[Implements: RenderableImage]{#53822}\
[Handles the renderable aspects of an operation with help from its
associated instance of a ContextualRenderedImageFactory.]{#53823}\

[RenderableImageProducer]{#53827}\

[Extends: Object]{#53829}\
[Implements: ImageProducer, Runnable]{#53830}\
[An adapter class that implements ImageProducer to allow the
asynchronous production of a RenderableImage.]{#53831}\

[RenderContext]{#53835}\

[Extends: Object]{#53837}\
[Implements: Cloneable]{#53838}\
[Encapsulates the information needed to produce a specific rendering
from a RenderableImage.]{#53839}\

The other block involved in the construction of `RenderableImageOp` is
a `ParameterBlock`. The `ParameterBlock` houses the source(s) for the
operation, plus parameters or other objects that the operator may
require. The parameters are rendering-independent versions of the
parameters that control the (Rendered) operator.

A Renderable chain is constructed by instantiating each successive
`RenderableImageOp`, passing in the last `RenderableImage` as the
source in the `ParameterBlock`. This chain can then be requested to
provide a number of renderings to specific device spaces through the
`getImage` method.

This chain, once constructed, remains editable. Both the parameters
for the specific operations in the chain and the very structure of the
chain can be changed. This is accomplished by the `setParameterBlock`
method, setting new controlling parameters and/or new sources. These
edits only affect future `RenderedImage`s derived from points in the
chain below the edits. `RenderedImage`s that were previously obtained
from the Renderable chain are immutable and completely independent
from the chain from which they were derived.


### 2.3.2 ![](shared/space.gif)The Rendered Layer

The Rendered layer is designed to work in concert with the Renderable
layer. The Rendered layer is comprised of sources and operations for
device-specific representations of images or renderings. The Rendered
layer is primarily defined by the `RenderedImage` interface. Sources
such as `BufferedImage` implement this interface.

Operators in this layer are simply `RenderedImage`s that take other
`RenderedImage`s as sources. Chains, therefore, can be constructed in
much the same manner as those of the Renderable layer. A sequence of
`RenderedImage`s is instantiated, each taking the last `RenderedImage`
as a source.

In [Figure 2-2](J2D-concepts.doc.html#51937), when the user calls
`Graphics2D.drawImage()`, a render context is constructed and used to
call the `getImage()` method of the renderable operator. A rendered
operator to actually do the pixel processing is constructed and
attached to the source and sink of the renderable operator and is
passed a clone of the renderable operator\'s parameter block. Pixels
actually flow through the rendered operator to the Graphics2D. The
renderable operator chain remains available to produce more renderings
whenever its `getImage()` method is called.


------------------------------------------------------------------------

![](J2D-concepts.doc.anc1.gif)

------------------------------------------------------------------------


***Figure 2-2* ![](shared/sm-blank.gif) Deriving a Rendering from a
Renderable Chain**

**[*Table 2-5* ![](shared/sm-blank.gif) The Rendered Layer Interfaces
and Classes]{#52034}**

[Type]{#52082}

[Name]{#52084}

[Description]{#52086}

[Interface]{#52046}\

[RenderedImage]{#52048}\

[A common interface for objects that contain or can produce image data
in the form of Rasters.]{#52050}\

[Class]{#52052}\

[BufferedImage]{#52054}\

[Extends: `Image`]{#52056}\
[Implements: WritableRenderedImage]{#52114}\
[A subclass that describes an Image with an accessible buffer of image
data.]{#52115}\

[WritableRenderedImage]{#52060}\

[Extends: RenderedImage]{#52062}\
[A common interface for objects that contain or can produce image data
that can be modified and/or written over.]{#52134}\

A rendered image represents a virtual image with a coordinate system
that maps directly to pixels. A Rendered image does not have to have
image data associated with it, only that it be able to produce image
data when requested. The `BufferedImage` class, which is the Java 2D
API\'s implementation of `RenderedImage`, however, maintains a full
page buffer that can be accessed and written to. Data can be accessed
in a variety of ways, each with different properties.


2.4 ![](shared/space.gif)Java Image Data Representation
-------------------------------------------------------

In the Java AWT API, a sample is the most basic unit of image data.
Each pixel is composed of a set of samples. For an RGB pixel, there
are three samples; one each for red, green, and blue. All samples of
the same kind across all pixels in an image constitute a *band*. For
example, in an RGB image, all the red samples together make up a band.
Therefore, an RGB image contains three bands.

A three-color subtractive image contains three bands; one each for
cyan, magenta, and yellow (CMY). A four-color subtractive image
contains four bands; one each for cyan, magenta, yellow, and black
(CMYK).

**[*Table 2-6* ![](shared/sm-blank.gif) Java 2D Image Data
Classes]{#52193}**

[Type]{#52241}

[Name]{#52243}

[Description]{#52245}

[Class]{#52205}\

[DataBuffer]{#52207}\

[Extends: Object]{#52289}\
[Wraps one or more data arrays. Each data array in the DataBuffer is
referred to as a bank.]{#52209}\

[Raster]{#52265}\

[Extends: Object]{#52267}\
[Represents a rectanglular array of pixels and provides methods for
retrieving image data.]{#52287}\

[SampleModel]{#53562}\

[Extends: Object]{#53566}\
[Extracts samples of pixels in images.]{#53564}\

[WriteableRaster]{#52219}\

[Extends: Raster]{#52221}\
[Provides methods for storing image data and inherits methods for
retrieving image data from it\'s parent class Raster.]{#52277}\

The basic unit of image data storage is the `DataBuffer`. The
`DataBuffer` is a kind of raw storage that contains all of the samples
for the image data but does not maintain a notion of how those samples
can be put together as pixels. The information about how the samples
are put together as pixels is contained in a `SampleModel`. The
`SampleModel` class contains methods for deriving pixel data from a
`DataBuffer`. Together, a `DataBuffer` and a `SampleModel` constitute
a meaningful multi-pixel image storage unit called a `Raster`.

A `Raster` has methods that directly return pixel data for the image
data it contains. There are two basic types of `Raster`s:

-   `Raster` - a read-only object that has only accessors


-   `WritableRaster` - A writable object that has a variety of
    mutators

There are separate interfaces for dealing with each raster type. The
`RenderedImage` interface assumes that the data is read-only and does
not contain methods for writing a `Raster`. The
`WritableRenderedImage` interface assumes that the image data is
writeable and can be modified.

Data from a *tile* is returned in a `Raster` object. A tile is not a
class in the architecture; it is a concept. A tile is one of a set of
regular rectangular regions that span the image on a regular grid. In
the `RenderedImage` interface, there are several methods that relate
to tiles and a tile grid. These methods are used by the JAI API,
rather than the Java 2D API. In the Java 2D API, the implementation of
the `WritableRenderedImage` (`BufferedImage`) is defined to have a
single tile. This, the `getWritableTile` method will return all the
image data. Other methods that relate to tiling will return the
correct degenerative results.

`RenderedImage`s do not necessarily maintain a `Raster` internally.
Rather, they can return requested rectangles of image data in the form
of a (`Writable`)`Raster` (through the `getData`, `getRect`, and
`get`(`Writable`)`Tile` methods). This distinction allows
`RenderedImages` to be virtual images, producing data only when
needed. `RenderedImage`s do, however, have an associated
`SampleModel`, implying that data returned in `Raster`s from the same
image will always be written to the associated `DataBuffer` in the
same way.

The Java 2D `BufferedImage` also adds an associated `ColorModel`,
which is different from the `SampleModel`. The `ColorModel` determines
how the bands are interpreted in a colorimetric sense.


2.5 ![](shared/space.gif)Introducing the Java Advanced Imaging API
------------------------------------------------------------------

The JAI API builds on the foundation of the Java 2D API to allow more
powerful and general imaging applications. The JAI API adds the
following concepts:

-   Multi-tiled images


-   Deferred execution


-   Networked images


-   Image property management


-   Image operators with multiple sources


-   Three-dimensional image data

The combination of tiling and deferred execution allows for
considerable run-time optimization while maintaining a simple imaging
model for programmers. New operators may be added and the new
operators may participate as first-class objects in the deferred
execution model.

The JAI API also provides for a considerable degree of compatibility
with the Java AWT and Java 2D imaging models. JAI\'s operators can
work directly on Java 2D `BufferedImage` objects or any other image
objects that implement the `RenderedImage` interface. JAI supports the
same rendering-independent model as the Java 2D API. using
device-independent coordinates. JAI also supports Java 2D-style
drawing on both Rendered and Renderable images using the `Graphics`
interface.

The JAI API does not make use of the image producer/consumer
interfaces introduced in Java AWT and carried forward into the Java 2D
API. Instead, the JAI API requires that image sources participate in
the \"pull\" imaging model by responding to requests for arbitrary
areas, thus making it impossible to instantiate an `ImageProducer`
directly as a source. It is, however, possible to instantiate an
`ImageProducer` that makes the JAI API image data available to older
AWT applications.


### 2.5.1 ![](shared/space.gif)Similarities with the Java 2D API

The JAI API is heavily dependent on the abstractions defined in the
Java 2D API. In general, the entire mechanism for handling Renderable
and Rendered images, pixel samples, and data storage is carried over
into JAI. Here are some of the major points of congruity between Java
2D and JAI:

-   The `RenderableImage` and `RenderedImage` interfaces defined in
    the Java 2D API are used as a basis for higher-level abstractions.
    Further, JAI allows you to create and manipulate directed acyclic
    graphs of objects implementing these interfaces.


-   The primary data object, the `TiledImage`, implements the
    `WritableRenderedImage` interface and can contain a regular tile
    grid of `Raster` objects. However, unlike the `BufferedImage` of
    the Java 2D API, `TiledImage` does not require that a `ColorModel`
    for photometric interpretation of its image data be present.


-   The JAI operator objects are considerably more sophisticated than
    in the Java 2D API. The `OpImage`, the fundamental operator
    object, provides considerable support for extensibility to new
    operators beyone that in the Java 2D API. JAI has a registry
    mechanism that automates the selection of operations on
    `RenderedImages`.


-   The Java 2D API `SampleModel`, `DataBuffer`, and `Raster` objects
    are carried over into JAI without change, except that `double`s
    and `float`s are allows to be used as the fundamental data types
    of a `DataBuffer` in addition to the `byte`, `short`, and `int`
    data types.


### 2.5.2 ![](shared/space.gif)JAI Data Classes

JAI introduces two new data classes, which extend the Java 2D
`DataBuffer` image data class.

**[*Table 2-7* ![](shared/sm-blank.gif) JAI Data Classes]{#52723}**

[Type]{#52747}

[Name]{#52749}

[Description]{#52751}

[Class]{#52735}\

[DataBufferFloat]{#52737}\

[Extends: DataBuffer]{#52745}\
[Stores data internally in float form.]{#52787}\

[DataBufferDouble]{#52804}\

[Extends: DataBuffer]{#52806}\
[Stores data internally in double form.]{#52807}\


#### 2.5.2.1 ![](shared/space.gif)The DataBufferFloat Class

**API:** 
|                                   | `javax.media.jai.DataBufferFloat` |

    DataBufferFloat(int size)

:   constructs a float-based DataBuffer with a specified size.
      --------------- -------- ---------------------------------------------
      *Parameters*:   `size`   The number of elements in the `DataBuffer`.
      --------------- -------- ---------------------------------------------

      : 


    DataBufferFloat(int size, int numBanks)

:   constructs a float-based DataBuffer with a specified number of
    banks, all of which are of a specified size.
    *Parameters*:
    `size`
    The number of elements in each bank of the `DataBuffer`.
    `numBanks`
    The number of banks in the `DataBuffer`.


    DataBufferFloat(float[] dataArray, int size)

:   constructs a float-based `DataBuffer` with the specified data
    array. Only the first size elements are available for use by this
    data buffer. The array must be large enough to hold `size`
    elements.
    *Parameters*:
    `dataArray`
    An array of floats to be used as the first and only bank of this
    `DataBuffer`.
    `size`
    The number of elements of the array to be used.


    DataBufferFloat(float[] dataArray, int size, int offset)

:   constructs a float-based `DataBuffer` with the specified data
    array. Only the elements between `offset` and (`offset` + `size` -
    1) are available for use by this `DataBuffer`. The array must be
    large enough to hold (`offset` + `size`) elements.
    *Parameters*:
    `dataArray`
    An array of floats to be used as the first and only bank of this
    `DataBuffer`.
    `size`
    The number of elements of the array to be used.
    `offset`
    The offset of the first element of the array that will be used.


    DataBufferFloat(float[][] dataArray, int size)

:   constructs a float-based `DataBuffer` with the specified data
    arrays. Only the first size elements of each array are available
    for use by this `DataBuffer`. The number of banks will be equal to
    `dataArray.length`.
    *Parameters*:
    `dataArray`
    An array of floats to be used as banks of this `DataBuffer`.
    `size`
    The number of elements of each array to be used.


    DataBufferFloat(float[][] dataArray, int size, int[] offsets)

:   constructs a float-based `DataBuffer` with the specified data
    arrays, size, and per-bank offsets. The number of banks is equal
    to `dataArray.length`. Each array must be at least as large as
    `size` + the corresponding `offset`. There must be an entry in the
    `offsets` array for each data array.
    *Parameters*:
    `dataArray`
    An array of arrays of floats to be used as the banks of this
    `DataBuffer`.
    `size`
    The number of elements of each array to be used.
    `offset`
    An array of integer offsets, one for each bank.


#### 2.5.2.2 ![](shared/space.gif)The DataBufferDouble Class

**API:** 
|                                   | `javax.media.jai.DataBufferDouble |
|                                   | `                                 |

    DataBufferDouble(int size)

:   constructs a double-based `DataBuffer` with a specified size.
      --------------- -------- ---------------------------------------------
      *Parameters*:   `size`   The number of elements in the `DataBuffer`.
      --------------- -------- ---------------------------------------------

      : 


    DataBufferDouble(int size, int numBanks)

:   constructs a double-based `DataBuffer` with a specified number of
    banks, all of which are of a specified size.
    *Parameters*:
    `size`
    The number of elements in each bank of the `DataBuffer`.
    `numBanks`
    The number of banks in the `DataBuffer`.


    DataBufferDouble(double[] dataArray, int size)

:   constructs a double-based `DataBuffer` with the specified data
    array. Only the first `size` elements are available for use by
    this databuffer. The array must be large enough to hold `size`
    elements.
    *Parameters*:
    `dataArray`
    An array of doubles to be used as the first and only bank of this
    `DataBuffer`.
    `size`
    The number of elements of the array to be used.


    DataBufferDouble(double[] dataArray, int size, int offset)

:   constructs a double-based `DataBuffer` with the specified data
    array. Only the elements between `offset` and (`offset` + `size` -
    1) are available for use by this data buffer. The array must be
    large enough to hold (`offset` + `size`) elements.
    *Parameters*:
    `dataArray`
    An array of doubles to be used as the first and only bank of this
    `DataBuffer`.
    `size`
    The number of elements of the array to be used.
    `offset`
    The offset of the first element of the array that will be used.


    DataBufferDouble(double[][] dataArray, int size)

:   constructs a double-based `DataBuffer` with the specified data
    arrays. Only the first size elements of each array are available
    for use by this `DataBuffer`. The number of banks will be equal to
    `dataArray.length`.
    *Parameters*:
    `dataArray`
    An array of doubles to be used as banks of this `DataBuffer`.
    `size`
    The number of elements of each array to be used.


    DataBufferDouble(double[][] dataArray, int size, int[] offsets)

:   constructs a double-based `DataBuffer` with the specified data
    arrays, size, and per-bank offsets. The number of banks is equal
    to `dataArray.length`. Each array must be at least as large as
    `size` + the corresponding `offset`. There must be an entry in the
    offsets array for each data array.
    *Parameters*:
    `dataArray`
    An array of arrays of doubles to be used as the banks of this
    `DataBuffer`.
    `size`
    The number of elements of each array to be used.
    `offset`
    An array of integer offsets, one for each bank.

------------------------------------------------------------------------

\




\

##### [Copyright](copyright.html) © 1999, Sun Microsystems, Inc. All rights reserved.
