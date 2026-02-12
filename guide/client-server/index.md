---
layout: default
title: Client-Server Imaging
parent: Programming Guide
nav_order: 14
---

# Client-Server Imaging                                                 
{:.no_toc}

This chapter describes ImageN\'s client-server imaging system

* Contents
{:toc}

This functionality is deprecated and provided for applications migrating from JAI,
and is available using the `imagen-legacy-network-core` dependency:

```xml
    <dependency>
      <groupId>org.eclipse.imagen</groupId>
      <artifactId>imagen-legacy-network-core</artifactId>
    </dependency>
```
# 12.1 Introduction

Client-server imaging provides the ability to distribute computation
between a set of processing nodes. For example, it is possible to set
up a large, powerful server that provides image processing services to
several thin clients. With JAI, it is possible for a client to set up
a complex imaging chain on a server, including references to source
images on other network hosts, and to request rendered output from the
server.

ImageN Java Remote Method Invocation (RMI) to implement
client-server imaging. To communicate using Remote Method Invocation,
both the client and server must be running Java. A *stub* object is
instantiated on the client. The stub object forwards its method calls
to a corresponding server object. Method call arguments and return
values are transmitted between the client and server by means of the
Java Development Environment\'s *serialization* capability.

The hostname and port depend on the local setup. The host must be
running an RMI registry process and have a `RemoteImageServer`
listening at the desired port.

This call will result in the creation of a server-side `RMIImageImpl`
object and a client-side stub object. The client stub serializes its
method arguments and transfers them to the server over a socket; the
server serializes its return values and returns them in the same
manner.

# 12.2 Server Name and Port Number

The `RemoteImage` constructor requires a `serverName` parameter that
consists of a host name and port number, in the following format:

         host:port

For example:

         camus.eng.sun.com:1099

The port number is optional and need be supplied only if the host name
is supplied. If the `serverName` parameter is null, the default is to
search for the RMIImage service on the local host at the default
*rmiregistry* port (1099.

**API:** `org.eclipse.imagen.RemoteImage`

* `RemoteImage(String serverName, RenderedImage source)`
* `RemoteImage(String serverName, RenderedOp source)`
* `RemoteImage(String serverName, RenderableOp source, RenderContext renderContext)`

# 12.3 Setting the Timeout Period and Number of Retries

A network error or a delay caused by the server failing to respond to
the request for an image is dealt with through retries. If, on the
first attempt, the server fails to respond, the program will wait a
specified amount of time and then make another request for the image.
When the limit of retries is exceeded, a null Raster may be returned.

The amount of time to wait between retries defaults to 1 second (1000
milliseconds). The `getTimeout` method is used to get the amount of
time between retries, in milliseconds. The `setTimeout` method is used
to set the amount of time between retries.

The number of times the program will attempt to read the remote image
may be read with the `getNumRetries` method. The `setNumRetries`
method is used to set the maximum number of retries.

**API:** `org.eclipse.imagen.RemoteImage`

* `void setTimeout(int timeout)`
* `int getTimeout()`
* `void setNumRetries(int numRetries)`

# 12.4 Remote Imaging Test Example

This section contains two examples of remote imaging programs.

### 12.4.1 Simple Remote Imaging Example

[Listing 12-1](#listing-12-1) shows a complete code
example of a `RemoteImaging` test. This example displays a 2 x 2 grid
of `ScrollingImagePanel`s, with each window displaying the sum of two
byte images that were rescaled to the range \[0,127\] prior to
addition. The panels display the following specific results:

-   upper left: local rendering
-   upper right: result of remote rendering of a RenderedOp graph
-   lower left: result of remote loading of a RenderedImage
-   lower right: result of remote rendering of a RenderableOp graph

The lower right image is a dithered version of the sum image passed
through a color cube lookup table and may appear slightly different
from the other three images, which should be identical.


***Listing 12-1*  Remote Imaging Example Program (Sheet 1 of 4)** <a name="listing-12-1"></a>

```java
{% include_relative RemoteImagingTest.java %}
```

### 12.4.2 RemoteImaging Example Across Two Nodes

[Listing 12-2](#listing-12-2) shows an example of a
RemoteImaging chain spread across two remote nodes, and displays the
results locally.

***Listing 12-2*  RemoteImaging Example Program Using Two Nodes (Sheet 1 of 2)** <a name="listing-12-2"></a>

```java
{% include_relative MultiNodeTest.java %}
```

**API:** `org.eclipse.imagen.RemoteImage`

* int getWidth()
* int getHeight()
* Raster getData()
* Raster getData(Rectangle rect)
* WritableRaster copyData(WritableRaster raster)
* Raster getTile(int x, int y)

# 12.5 Running Remote Imaging

To run remote imaging in ImageN, you have to do the following:

These four steps are explained in more detail in the following
sections.

### 12.5.1 Step 1: Create a Security Policy File

The default RMI security policy implementation is specified within one
or more policy configuration files. These configuration files specify
what permissions are allowed for code from various sources. There is a
default system-wide policy file and a single user policy file. For
more information on policy files and permissions, see:

         http://java.sun.com/products/jdk/1.2/docs/guide/security/
    PolicyFiles.html
         http://java.sun.com/products/jdk/1.2/docs/guide/security/
    permissions.html

The policy file is located in the base directory where Java Advanced
Imaging is installed. If `$JAI` is the base directory where Java
Advanced Imaging is installed, use any simple text editor to create a
text file named `$JAI/policy` containing the following:

         grant {
         // Allow everything for now
            permission java.security.AllPermission;
         };

Note that this policy file is for testing purposes only.

### 12.5.2 Step 2: Start the RMI Registry

The RMI registry is a simple server-side name server that allows
remote clients to get a reference to a remote object. Typically, the
registry is used only to locate the first remote object an application
needs to talk to. Then that object in turn provides
application-specific support for finding other objects.

------------------------------------------------------------------------

**Note:** Before starting the rmiregistry, make sure that the shell or
window in which you will run the registry either has no `CLASSPATH`
set or has a `CLASSPATH` that does not include the path to any classes
you want downloaded to your client, including the stubs for your
remote object implementation classes.

------------------------------------------------------------------------

To start the registry on the server, log in to the remote system where
the image server will be running and execute the `rmiregistry`
command.

For example, in the **Solaris** operating environment using a
Bourne-compatible shell (e.g., /bin/sh):

         $ unset CLASSPATH
         $ rmiregistry &

Note that the `CLASSPATH` environment variable is deliberately not
set.

For example, on **Windows 95** or **Windows NT**:

         start rmiregistry

If the `start` command is not available, use `javaw`.


### 12.5.3 Step 3: Start the Remote Image Server

While still logged in to the remote server system, set the `CLASSPATH`
and `LD_LIBRARY_PATH` environment variable (see the `INSTALL` file) and
start the remote imaging server. For example:

         $ CLASSPATH=$JAI/lib/jai.jar
         $ export CLASSPATH
         $ LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$JAI/lib
         $ export LD_LIBRARY_PATH
         $ java \
         -Djava.rmi.server.codebase=\
              file:$JAI/lib/jai.jar \
         -Djava.rmi.server.useCodebaseOnly=false \
         -Djava.security.policy=file:$JAI/policy \
               org.eclipse.imagen.media.rmi.RMIImageImpl

For example, when the above steps are executed on a machine with IP
address 123.456.78.90 the following is printed:

         Server: using host 123.456.78.90 port 1099
         Registering image server as
           "rmi://123.456.78.90:1099/RemoteImageServer".
         Server: Bound RemoteImageServer into
           the registry.


### 12.5.4 Step 4: Run the Local Application

After completing steps 1 through 3, you are ready to run the local
application. When running the local application, make sure that the
`serverName` parameter of any RemoteImage constructors corresponds to
the machine on which the remote image server is running. For example,
if the machine with IP address 123.456.78.90 above is named
`myserver`, the `serverName` parameter of any `RemoteImage()`
constructors should be `"myserver"`.

# 12.6 Internet Imaging Protocol (IIP)

There are two operations that support Internet Imaging Protocol
(IIP) operations. Two separate operations provide client-side support
of the Internet Imaging Protocol. These operations, `IIP` and
`IIPResolution`, request an image from an IIP server then create
either a RenderedImage or a RenderableImage.

### 12.6.1 IIP Operation

The `IIP` operation provides client-side support of the Internet
Imaging Protocol (IIP) in both the rendered and renderable modes. It
creates a `RenderedImage` or a `RenderableImage` based on the data
received from the IIP server, and optionally applies a sequence of
operations to the created image.

The operations that may be applied and the order in which they are
applied are defined in section 2.2.1.1 of the *Internet Imaging
Protocol Specification* version 1.0.5. Some or all of the requested
operations may be executed on the IIP server if it is determined that
the server supports such operations. Any of the requested operations
not supported by the server will be executed on the host on which the
operation chain is rendered.

The processing sequence for the supplied operations is as follows:

-   Filtering (blur or sharpen)


-   Tone and color correction (\"color twist\")


-   Contrast adjustment


-   Selection of source rectangle of interest


-   Spatial orientation (rendering-independent affine transformation)


-   Selection of destination rectangle of interest


-   Rendering transformation (renderable mode only)


-   Transposition (rotation and/or mirroring).

As indicated, the rendering transformation is performed only in
renderable mode processing. This transformation is derived from the
AffineTransform supplied in the RenderContext when rendering actually
occurs. Rendered mode processing creates a RenderedImage which is the
default rendering of the RenderableImage created in renderable mode
processing.

The `IIP` operation takes 14 parameters.

| Parameter | Type | Description |
| --------- | ---- | ----------- |
| URL | String | The URL of the IIP image |
| subImages | int\[\] | The sub-images to be used by the server for images at each resolution level |
| filter | Float | The filtering value |
| colorTwist | float\[\] | The color twist matrix |
| contrast | Float | The contrast value
| sourceROI | Rectangle2D.Float | The source rectangle of interest in rendering-independent coordinates |
| transform | AffineTransform | The rendering-independent spatial orientation transform |
| aspectRatio | Float | The aspect ratio of the destination image |
| destROI | Rectangle2D.Float | The destination rectangle of interest in rendering-independent coordinates |
| rotation | Integer Z| The counterclockwise rotation angle to be applied to the destination |
| mirrorAxis | String | The mirror axis |
| ICCProfile | color.ICC\_Profile | The ICC profile used to represent the color space of the source image |
| JPEGQuality | Integer | The JPEG quality factor |
| JPEGTable | Integer | The JPEG compression group index number |

The `URL` parameter specifies the URL of the IIP image as a
`java.lang.String`. It must represent a valid URL and include any
required FIF or SDS commands. It cannot be null.

The `subImages` parameter optionally indicates the sub-images to be
used by the server to get the images at each resolution level. The
values in this `int` array cannot be negative. If this parameter is
not specified, or if the array is too short (length is 0), or if a
negative value is specified, this operation will use the zeroth
sub-image of the resolution level actually processed.

The `filter` parameter specifies a blur or sharpen operation; a
positive value indicates sharpen and a negative value blur. A unit
step should produce a perceptible change in the image. The default
value is 0 which signifies that no filtering will occur.

The `colorTwist` parameter represents a 4 x 4 matrix stored in
row-major order and should have an array length of at least 16. If an
array of length greater than 16 is specified, all elements from index
16 and beyond are ignored. Elements 12, 13, and 14 must be 0. This
matrix will be applied to the (possibly padded) data in an
intermediate normalized PhotoYCC color space with a premultiplied
alpha channel. This operation will force an alpha channel to be added
to the image if the last column of the last row of the color twist
matrix is not 1.0F. Also, if the image originally has a grayscale
color space it will be cast up to RGB if casting the data back to
grayscale after applying the color twist matrix would result in any
loss of data. The default value is null.

The `contrast` parameter specifies a contrast enhancement operation
with increasing contrast for larger value. It must be greater than or
equal to 1.0F. A value of 1.0F indicates no contrast adjustment. The
default value is 1.0F.

The `sourceROI` parameter specifies the rectangle of interest in the
source image in rendering-independent coordinates. The intersection of
this rectangle with the rendering-independent bounds of the source
image must equal itself. The rendering-independent bounds of the
source image are defined to be (0.0F, 0.0F, r, 1.0F) where r is the
aspect ratio (width/height) of the source image. Note that the source
image will not in fact be cropped to these limits but values outside
of this rectangle will be suppressed.

The `transform` parameter represents an affine backward mapping to be
applied in rendering-independent coordinates. Note that the direction
of transformation is opposite to that of the AffineTransform supplied
in the RenderContext which is a forward mapping. The default value of
this transform is the identity mapping. The supplied AffineTransform
must be invertible.

The `aspectRatio` parameter specifies the rendering-independent width
of the destination image and must be positive. The
rendering-independent bounds of the destination image are (0.0F, 0.0F,
aspectRatio, 1.0F). If this parameter is not provided, the destination
aspect ratio defaults to that of the source.

The `destROI` parameter specifies the rectangle of interest in the
destination image in rendering-independent coordinates. This rectangle
must have a non-empty intersection with the rendering-independent
bounds of the destination image but is not constrained to the
destination image bounds.

The `rotation` parameter specifies a counter-clockwise rotation angle
of the destination image. The rotation angle is limited to 0, 90, 180,
or 270 degrees. By default, the destination image is not rotated.

The `mirrorAxis` parameter may be null, in which case no flipping is
applied, or a String of `x`, `X`, `y`, or `Y`.

The `ICCProfile` parameter may only be used with client-side
processing or with server-side processing if the connection protocol
supports the ability to transfer a profile.

The `JPEGQuality` and `JPEGTable` parameters are only used with
server-side processing. If provided, `JPEGQuality` must be in the
range \[0,100\] and `JPEGTable` in \[1,255\].

There is no source image associated with this operation.

[Listing 12-3](#listing-12-3) shows a code sample for an `IIP` operation.

***Listing 12-3*  IIP Operation Example** <a name="listing-12-3"></a>

```java
public static final String SERVER = "http://istserver:8087/";
public static final String DEFAULT_IMAGE = "cat.fpx";
public static final int DEFAULT_HEIGHT = 512;

public static void main(String[] args) {
    String imagePath = DEFAULT_IMAGE;

   for(int i = 0; i < args.length; i++) {
       if(args[i].equalsIgnoreCase("-image")) {
           imagePath = args[++i];
           if(!(imagePath.toLowerCase().endsWith(".fpx"))) {
                imagePath += ".fpx";
           }
       }
   }

   String url = SERVER + "FIF=" + imagePath;

   new IIPTest(url);
}

// Define the parameter block.
ParameterBlock pb = (new ParameterBlock()).add(url);

// Default sub-image array
pb.set(-10.0F, 2); // filter
float[] colorTwist = new float[]
    {1.0F, 0.0F, 0.0F, 0.0F,
     0.0F, 0.0F, 1.0F, 0.0F,
     0.0F, 1.0F, 0.0F, 0.0F,
     0.0F, 0.0F, 0.0F, 1.0F};
pb.set(colorTwist, 3); //color-twist
pb.set(2.0F, 4); // contrast
pb.set(new Rectangle2D.Float(0.10F, 0.10F,
                             0.80F*aspectRatioSource, 0.80F),
       5); // srcROI

AffineTransform afn = AffineTransform.getShearInstance(0.2,

                   0.1);
pb.set(afn, 6); // transform
Rectangle2D destBounds = null;

try {
     Rectangle2D sourceRect =
           new Rectangle2D.Float(0.0F, 0.0F, aspectRatioSource,

                              1.0F);
     Shape shape =
         afn.createInverse().createTransformedShape(sourceRect);
     destBounds = shape.getBounds2D();
} catch(Exception e) {
}

float aspectRatio = (float)destBounds.getHeight();
pb.set(aspectRatio, 7); // destination aspect ratio
pb.set(new Rectangle2D.Float(0.0F, 0.0F,
                         0.75F*aspectRatio, 0.75F), 8); // dstROI
pb.set(90, 9); // rotation angle
pb.set("x", 10); // mirror axis

// Default ICC profile
// Default JPEG quality
// Default JPEG table index

int height = DEFAULT_HEIGHT;
AffineTransform at =
    AffineTransform.getScaleInstance(height*aspectRatioSource,

                 height);
RenderContext rc = new RenderContext(at);

// Create a RenderableImage.
RenderableImage renderable = ImageN.createRenderable("iip", pb);
```

### 12.6.2 IIPResolution Operation

The `IIPResolution` operation provides client-side support of the
Internet Imaging Protocol (IIP) in the rendered mode. It is
resolution-specific. It requests from the IIP server an image at a
particular resolution level, and creates a `RenderedImage` based on
the data received from the server. Once the `RenderedImage` is
created, the resolution level cannot be changed.

The layout of the created RenderedImage is set as follows:

-   minX, minY, tileGridXOffset, and tileGridYOffset are set to 0


-   width and height are determined based on the specified resolution
    level


-   tileWidth and tileHeight are set to 64


-   sampleModel is of the type `PixelInterleavedSampleModel` with byte
    data type and the appropriate number of bands


-   colorModel is of the type `java.awt.image.ComponentColorModel`,
    with the ColorSpace set to sRGB, PhotoYCC, or Grayscale, depending
    on the color space of the remote image; if an alpha channel is
    present, it will be premultiplied

The `IIPResolution` operation takes three parameters.

| Parameter  | Type    | Description |
| ---------- | ------- | ----------- |
| URL        | String  | The URL of the IIP image |
| resolution | Integer | The resolution level to request |
| subImage   | Integer | The sub-image to be used by the server |

The `URL` parameter specifies the URL of the IIP image as a
`java.lang.String`. It must represent a valid URL, and include any
required FIF or SDS commands. It cannot be null.

The `resolution` parameter specifies the resolution level of the
requested IIP image from the server. The lowest resolution level is 0,
with larger integers representing higher resolution levels. If the
requested resolution level does not exist, the nearest resolution
level is used. If this parameter is not specified, it is set to the
default value `IIPResolutionDescriptor.MAX_RESOLUTION`, which
indicates the highest resolution level.

The `subImage` parameter indicates the sub-image to be used by the
server to get the image at the specified resolution level. This
parameter cannot be negative. If this parameter is not specified, it
is set to the default value 0.

There is no source image associated with this operation.

If available from the IIP server certain properties may be set on the
RenderedImage. The names of properties and the class types of their
associated values are listed in the following table. See the IIP
specification for information on each of these properties.

| Property | Type |
| -------- | ---- |
| affine-transform   | java.awt.geom.AffineTransform |
| app-name           | java.lang.String |
| aspect-ratio       | java.lang.Float |
| author             | java.lang.String |
| colorspace         | int\[\] |
| color-twist        | float\[16\] |
| comment            | java.lang.String |
| contrast-adjust    | java.lang.Float |
| copyright          | java.lang.String |
| create-dtm         | java.lang.String |
| edit-time          | java.lang.String |
| filtering-value    | java.lang.Float |
| iip                | java.lang.String |
| iip-server         | java.lang.String |
| keywords           | java.lang.String |
| last-author        | java.lang.String |
| last-printed       | java.lang.String |
| last-save-dtm      | java.lang.String |
| max-size           | int\[2\] |
| resolution-number  | java.lang.Integer |
| rev-number         | java.lang.String |
| roi-iip            | java.awt.geom.Rectangle2D.Float |
| subject            | java.lang.String |
| title              | java.lang.String |

[Listing 12-4](#listing-12-4) shows a code sample for
an `IIPResolution` operation.

***Listing 12-4*  IIPResolution Operation Example**  <a name="listing-12-4"></a>

```java
public static final String SERVER = "http://istserver:8087/";
public static final String DEFAULT_IMAGE = "cat.fpx";
public static final int DEFAULT_RESOLUTION = 3;

public static void main(String[] args) {
    String imagePath = DEFAULT_IMAGE;
    int resolution = DEFAULT_RESOLUTION;

    for(int i = 0; i < args.length; i++) {
        if(args[i].equalsIgnoreCase("-image")) {
            imagePath = args[++i];
            if(!(imagePath.toLowerCase().endsWith(".fpx"))) {
                imagePath += ".fpx";
            }
        } else if(args[i].equalsIgnoreCase("-res")) {
            resolution = Integer.valueOf(args[++i]).intValue();
        }
    }

    String url = SERVER + "FIF=" + imagePath;

    new IIPResolutionTest(url, resolution);
}

ParameterBlock pb = new ParameterBlock();
pb.add(url).add(resolution);
PlanarImage pi = ImageN.create("iipresolution", pb);
```