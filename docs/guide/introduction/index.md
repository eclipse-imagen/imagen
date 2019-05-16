---
layout: default
title: Introduction
parent: Programming Guide
nav_order: 2
---


  ----------------------------------------
    C H A P T E R![](shared/sm-space.gif)1
  ----------------------------------------


+----------------------------------------------------------------------:+
| -------------------------------------------------------------------   |
|                                                                       |
| Introduction to Java Advanced Imaging                                 |
+-----------------------------------------------------------------------+

\
\
\

**T**HE Java programming language has continued to grow both in
popularity and scope since its initial release. Java in its current
form is the culmination of several years work, dating back to 1991
when it was conceived as a modular and extensible programming
language.

Java is based on the C and C++ programming languages, but differs from
these languages is some important ways. The main difference between
C/C++ and Java is that in Java all development is done with objects
and classes. This main difference provides distinct advantages for
programs written in Java, such as multiple threads of control and
dynamic loading.

Another advantage to Java is its extensibility. Since the original
release of Java, several extensions have been added to the core code,
providing greater flexibility and power to applications. These
extensions add objects and classes that improve the Java programmer\'s
ability to use such features as:

-   Java Swing - a component set to create grapical user interfaces
    with a cross-platform look and feel


-   Java Sound - for high-quality 32-channel audio rendering and
    MIDI-controlled sound synthesis


-   Java 3D - for advanced geometry and 3D spatial sound


-   Java Media Framework - for components to play and control
    time-based media such as audio and video


-   Java Telephony (JTAPI) - for computer-telephony applications


-   Java Speech - for including speech technology into Java applets
    and applications


1.1 ![](shared/space.gif)The Evolution of Imaging in Java
---------------------------------------------------------

Early versions of the Java AWT provided a simple rendering package
suitable for rendering common HTML pages, but without the features
necessary for complex imaging. The early AWT allowed the generation of
simple images by drawing lines and shapes. A very limited number of
image files, such as GIF and JPEG, could be read in through the use of
a `Toolkit` object. Once read in, the image could be displayed, but
there were essentially no image processing operators.

The Java 2D API extended the early AWT by adding support for more
general graphics and rendering operations. Java 2D added special
graphics classes for the definition of geometric primitives, text
layout and font definition, color spaces, and image rendering. The new
classes supported a limited set of image processing operators for
blurring, geometric transformation, sharpening, contrast enhancement,
and thresholding. The Java 2D extensions were added to the core Java
AWT beginning with the Java Platform 1.2 release.

The Java Advanced Imaging (JAI) API further extends the Java platform
(including the Java 2D API) by allowing sophisticated,
high-performance image processing to be incorporated into Java applets
and applications. JAI is a set of classes providing imaging
functionality beyond that of Java 2D and the Java Foundation classes,
though it is compatible with those APIs.

JAI implements a set of core image processing capabilities including
image tiling, regions of interest, and deferred execution. JAI also
offers a set of core image processing operators including many common
point, area, and frequency-domain operators.

JAI is intended to meet the needs of all imaging applications. The API
is highly extensible, allowing new image processing operations to be
added in such a way as to appear to be a native part of it. Thus, JAI
benefits virtually all Java developers who want to incorporate imaging
into their applets and applications.


1.2 ![](shared/space.gif)Why Another Imaging API?
-------------------------------------------------

Several imaging APIs have been developed - a few have even been
marketed and been fairly successful. However, none of these APIs have
been universally accepted because they failed to address specific
segments of the imaging market or they lacked the power to meet
specific needs. As a consequence, many companies have had to \"roll
their own\" in an attempt to meet their specific requirements.

Writing a custom imaging API is a very expensive and time-consuming
task and the customized API often has to be rewritten whenever a new
CPU or operating system comes along, creating a maintenance nightmare.
How much simpler it would be to have an imaging API that meets
everyone\'s needs.

Previous industry and academic experience in the design of image
processing libraries, the usefulness of these libraries across a wide
variety of application domains, and the feedback from the users of
these libraries have been incorporated into the design of JAI.

JAI is intended to support image processing using the Java programming
language as generally as possible so that few, if any, image
processing applications are beyond its reach. At the same time, JAI
presents a simple programming model that can be readily used in
applications without a tremendous mechanical programming overhead or a
requirement that the programmer be expert in all phases of the API\'s
design.

JAI encapsulates image data formats and remote method invocations
within a re-usable image data object, allowing an image file, a
network image object, or a real-time data stream to be processed
identically. Thus, JAI represents a simple programming model while
concealing the complexity of the internal mechanisms.


1.3 ![](shared/space.gif)JAI Features
-------------------------------------

JAI is intended to meet the requirements of all of the different
imaging markets, and more. JAI offers several advantages for
applications developers compared to other imaging solutions. Some of
these advantages are described in the following paragraphs.


### 1.3.1 ![](shared/space.gif)Cross-platform Imaging

Whereas most imaging APIs are designed for one specific operating
system, JAI follows the Java run time library model, providing
platform independence. Implementations of JAI applications will run on
any computer where there is a Java Virtual Machine. This makes JAI a
true cross-platform imaging API, providing a standard interface to the
imaging capabilities of a platform. This means that you write your
application once and it will run anywhere.


### 1.3.2 ![](shared/space.gif)Distributed Imaging

JAI is also well suited for client-server imaging by way of the Java
platform\'s networking architecture and remote execution technologies.
Remote execution is based on Java RMI (remote method invocation). Java
RMI allows Java code on a client to invoke method calls on objects
that reside on another computer without having to move those objects
to the client.


### 1.3.3 ![](shared/space.gif)Object-oriented API

Like Java itself, JAI is totally object-oriented. In JAI, images and
image processing operations are defined as objects. JAI unifies the
notions of image and operator by making both subclasses of a common
parent.

An operator object is instantiated with one or more image sources and
other parameters. This operator object may then become an image source
for the next operator object. The connections between the objects
define the flow of processed data. The resulting editable graphs of
image processing operations may be defined and instantiated as needed.


### 1.3.4 ![](shared/space.gif)Flexible and Extensible

Any imaging API must support certain basic imaging technologies, such
as image acquisition and display, basic manipulation, enhancement,
geometric manipulation, and analysis. JAI provides a core set of the
operators required to support the basic imaging technologies. These
operators support many of the functions required of an imaging
application. However, some applications require special image
processing operations that are seldom, if ever, required by other
applications. For these specialized applications, JAI provides an
extensible framework that allows customized solutions to be added to
the core API.

JAI also provides a standard set of image compression and
decompression methods. The core set is based on international
standards for the most common compressed file types. As with special
image processing functions, some applications also require certain
types of compressed image files. It is beyond the scope of any API to
support the hundreds of known compression algorithms, so JAI also
supports the addition of customized coders and decoders (codecs),
which can be added to the core API.


### 1.3.5 ![](shared/space.gif)Device Independent

The processing of images can be specified in device-independent
coordinates, with the ultimate translation to pixels being specified
as needed at run time. JAI\'s \"renderable\" mode treats all image
sources as rendering-independent. You can set up a graph (or chain) of
renderable operations without any concern for the source image
resolution or size; JAI takes care of the details of the operations.

To make it possible to develop platform-independent applications, JAI
makes no assumptions about output device resolution, color space, or
color model. Nor does the API assume a particular file format. Image
files may be acquired and manipulated without the programmer having
any knowledge of the file format being acquired.


### 1.3.6 ![](shared/space.gif)Powerful

JAI supports complex image formats, including images of up to three
dimensions and an arbitrary number of bands. Many classes of imaging
algorithms are supported directly, others may be added as needed.

JAI implements a set of core image processing capabilities, including
image tiling, regions of interest, and deferred execution. The API
also implements a set of core image processing operators, including
many common point, area, and frequency-domain operations. For a list
of the available operators, see [Section 3.6, \"JAI API
Operators](Programming-environ.doc.html#55112).\"


### 1.3.7 ![](shared/space.gif)High Performance

A variety of implementations are possible, including highly-optimized
implementations that can take advantage of hardware acceleration and
the media capabilities of the platform, such as MMX on Intel
processors and VIS on UltraSparc.


### 1.3.8 ![](shared/space.gif)Interoperable

JAI is integrated with the rest of the Java Media APIs, enabling
media-rich applications to be deployed on the Java platform. JAI works
well with other Java APIs, such as Java 3D and Java component
technologies. This allows sophisticated imaging to be a part of every
Java technology programmer\'s tool box.

JAI is a Java Media API. It is classified as a Standard Extension to
the Java platform. JAI provides imaging functionality beyond that of
the Java Foundation Classes, although it is compatible with those
classes in most cases.


1.4 ![](shared/space.gif)A Simple JAI Program
---------------------------------------------

Before proceeding any further, let\'s take a look at an example JAI
program to get an idea of what it looks like. [Listing
1-1](Introduction.doc.html#55064) shows a simple example of a complete
JAI program. This example reads an image, passed to the program as a
command line argument, scales the image by 2x with bilinear
interpolation, then displays the result.

**[]{#55064}**

***Listing 1-1* ![](shared/sm-blank.gif) **Simple Example JAI
Program****

------------------------------------------------------------------------

         import java.awt.Frame;
         import java.awt.image.renderable.ParameterBlock;
         import java.io.IOException;
         import javax.media.jai.Interpolation;
         import javax.media.jai.JAI;
         import javax.media.jai.RenderedOp;
         import com.sun.media.jai.codec.FileSeekableStream;
         import javax.media.jai.widget.ScrollingImagePanel;

         /**
          * This program decodes an image file of any JAI supported
          * formats, such as GIF, JPEG, TIFF, BMP, PNM, PNG, into a
          * RenderedImage, scales the image by 2X with bilinear
          * interpolation, and then displays the result of the scale
          * operation.
          */
         public class JAISampleProgram {

             /** The main method. */
             public static void main(String[] args) {
                 /* Validate input. */
                 if (args.length != 1) {
                     System.out.println("Usage: java JAISampleProgram " +
                                        "input_image_filename");
                     System.exit(-1);
                 }

                 /*
                  * Create an input stream from the specified file name
                  * to be used with the file decoding operator.
                  */
                 FileSeekableStream stream = null;
                 try {
                     stream = new FileSeekableStream(args[0]);
                 } catch (IOException e) {
                     e.printStackTrace();
                     System.exit(0);
                 }

                 /* Create an operator to decode the image file. */
                 RenderedOp image1 = JAI.create("stream", stream);
                 /*
                  * Create a standard bilinear interpolation object to be
                  * used with the "scale" operator.
                  */
                 Interpolation interp = Interpolation.getInstance(
                                            Interpolation.INTERP_BILINEAR);

                 /**
                  * Stores the required input source and parameters in a
                  * ParameterBlock to be sent to the operation registry,
                  * and eventually to the "scale" operator.
                  */
                 ParameterBlock params = new ParameterBlock();
                 params.addSource(image1);
                 params.add(2.0F);         // x scale factor
                 params.add(2.0F);         // y scale factor
                 params.add(0.0F);         // x translate
                 params.add(0.0F);         // y translate
                 params.add(interp);       // interpolation method

                 /* Create an operator to scale image1. */
                 RenderedOp image2 = JAI.create("scale", params);

                 /* Get the width and height of image2. */
                 int width = image2.getWidth();
                 int height = image2.getHeight();

                 /* Attach image2 to a scrolling panel to be displayed. */
                 ScrollingImagePanel panel = new ScrollingImagePanel(
                                                 image2, width, height);

                 /* Create a frame to contain the panel. */
                 Frame window = new Frame("JAI Sample Program");
                 window.add(panel);
                 window.pack();
                 window.show();
             }
         }

------------------------------------------------------------------------

------------------------------------------------------------------------

\




\

##### [Copyright](copyright.html) © 1999, Sun Microsystems, Inc. All rights reserved.
