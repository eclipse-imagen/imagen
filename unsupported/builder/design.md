# ImageN Builder API Design

ImageN offers a [search and replace migration](https://eclipse.github.io/imagen/migration/) for those migrating from Java Advanced Imaging API classes to the ImageN library.

ImageN is experimenting with a builder approach which offers a "fluent" API for improved ease of use and discoverability taking avantage of IDE code completion.

## Simple ImageN Parameter Block Example

Here is "A Simple ImageN Program" from [Programming Guide](https://eclipse.github.io/imagen/guide/introduction/):

```java
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
Frame window = new Frame("ImageN Sample Program");
window.add(panel);
window.pack();
window.show();
```

## Simple ImageN Builder Example

ImageN offers a facade class providing high level access to library functionality. Use of literate programming reduces the need for untyped API contracts (such as ParameterBlock above).

Here is the sample illustrating this approach:

```java
/*
* Create an input stream from the specified file name
* to be used with the file decoding operator.
*/
FileSeekableStream fileSeekableStream = new FileSeekableStream(args[0]);

/* Create an operation to decode the image file. */
Operation image1 = ImageRead.stream(fileSeekableStream);

/* Create operation to scale image1, with interpolation hint. */
Operation image2 = Affine.source(image1)
                         .scale(2.0F,2.0F)
                         .interpolation(Interpolation.BILINEAR).create();

/* Get the width and height of image2. */
int width = image2.getWidth();
int height = image2.getHeight();

/* Attach image2 to a scrolling panel to be displayed. */
ScrollingImagePanel panel = new ScrollingImagePanel(image2, width, height);

/* Create a frame to contain the panel. */
Frame window = new Frame(“ImageN Builder Sample Program”);
window.add(panel);
window.pack();
window.show();
```
Usage Notes:

- parameter are required to be matched during operation lookup, hints are not required
- interpolation is always a hint and never a parameter
- example: scale, warp and a translate.

## Design Background

Three reference designs were considered:

* Option 1 "Parameter Blocks": Use of a late binding approach, as favoured by JAI Parameter Blocks. This solution could be improved on by using explicit Parameter Block subclasses providing methods, keys and java docs for programmer ease of use.

```java
ParameterBlock paramBlock = new ParameterBlock();
paramBlock.setSource(image1, 0);
paramBlock.add(2.0F);
paramBlock.add(2.0F);
paramBlock.add(0.0F);
paramBlock.add(0.0F);
paramBlock.add(interpolation)         
image2 = JAI.create("Scale", paramBlock);
```

  While some facade methods have been added for ease of use the result is not extendable:

```java
AffineTransform transform = AffineTransform.getScaleInstance(2.0F,2.0F);
Interpolation interpolation = new InterpolationNearest();
image2 = JAI.create("affine", image1, transform, interpolation);
```

  The design of GeoTools ImageWorker was referenced, which offers a methods with documented parameters for the construction of a parameter block and subsequent operation lookup. This approach helped, but was not extendible if additional parameters are required at a future date and had no ability to skip unused parameters (such as the rotate 0.0F and 0.0F arguments shown below).

```java
ImageWorker imageWorker = new ImageWorker(image1);
imageWorker.setRenderingHints(hints);
imageWorker.scale(2.0F,2.0F,0.0F,0.0F,Interpolation.BILINEAR);
RenderedImage image3 = imageWorker.getRenderedImage();
```

* Option 2 "Descriptors": Use of early binding approach, defining a descriptor with a create method (responsible for assembling a parameter block and performing implementation lookup).
  
  We have run into the limitations of this approach during the JAI-EXT project where adding an optional parameter for "region of interest" could not be supported.
  
* Option 3 "SLD Builder Approach": The GeoTools SLD Builder provides a self documenting literate api by returning a data structure specific builder at each level of the tree.
  
  This approach, while promising, could not easily account for multiple source operations and multiple output operations in an operation chain. Attempts to address this by providing "pop" methods to adjust the builder stack looked too complicated as code examples.

Design notes:

- Affine.source is a static method, returning an Affine OperationBuilder which uses a literate API to assemble parameters and hints in a programmer friendly fashion with methods and javadocs. Affine.create() looks up the appropriate implementation.

        Operation image2 = Affine.source(image1).scale(2.0F,2.0F).create();
  
- The set of parameters is open ended and additional parameter are expected to be added over time - similar to how a "region of interest" parameter was added for the JAI-EXT project to denote no-data areas.

        Operation image2 = Affine.source(image1).scale(2.0F,2.0F).roi( roi ).create();

- The set of hints is open ended and additional hints are expected to be added over time.     
  
        image2 = Affine.source(image1).scale(2.0F,2.0F).hint("layout", param ).create();
  
- As a consequence the Affine class is "syntactic sugar" for its parent "OperationBuilder" class which provides parameter and hint methods.
  
  An identical operation lookup can be performed using:
       
```java       
AffineTransform affine = new AffineTransform();
affine.setToScale(2.0F,2.0F);
Operation image2 = new OperationBuilder().source(image1).
                                        .parameter( "affine", affine )
                                        .hint(Interpolation.KEY,Interpolation.BILINEAR)
                                        .create();
```

- We are using distinct OperationBuilder subclasses, such as Affine, rather than a single facade class to improve readability.
  
  Incorrect:
```java
image2 = REP.scale(2.0F,2.0F).interpolation(Interpolation.BILINEAR).create();
```

  Correct:
```java
image2 = Affine.scale(2.0F,2.0F).interpolation(Interpolation.BILINEAR).create();
```

- We considered using lazy lookup of a delegate operation, allowing an operation to collect parameters and look up the implementation to use on first use. The result had too many possibility for concurrency issues to justify.

  Incorrect:
  
```java
image2 = REP.scale(2.0F,2.0F).interpolation(Interpolation.BILINEAR).create();
```

Open design questions:

- The use of strongly typed keys for hints and parameters is useful as an additional safety, providing opportunities dynamically document available settings and validate hint and parameter settings.
  
  The impact on ease of use in comparison to Strings and the use of String constants is still under consideration.

- It is tempting to make the API easier to use for enumerated values as Interpolation by assuming the hint key.
  
  Example:
  
```java
image2 = Affine.scale(2.0F,2.0F).hint(Interpolation.BILINEAR).create();
```
  
  Assumes the hint Enumeration.KEY (determined using reflection) as shown below:
```java  
Operation image2 = new OperationBuilder().source(image1).
                                        .parameter( "affine", affine )
                                        .hint(Interpolation.KEY,Interpolation.BILINEAR)
                                        .create();
```