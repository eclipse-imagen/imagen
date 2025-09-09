---
layout: default
title: Program Examples
parent: Programming Guide
nav_order: 18
---

# Program Examples                                                      

Appendix containing fully-operational ImageN program examples.

The examples in this appendix are provided to demonstrate how to
create simple programs using JAI. Although these examples can be
compiled and run, they are not intended to be used that way since they
are pretty simple and would not be particularly interesting, visually.


A.1 Lookup Operation Example
-------------------------------------------------

[Listing A-1](#listing-a-1) shows an example of the
`Lookup` operation. This example program decodes a TIFF image file
into a `RenderedImage`. If the TIFF image is an unsigned short type
image, the program performs a `Lookup` operation to convert the image
into a byte type image. Finally, the program displays the byte image.

***Listing A-1* Example Lookup Program** <a name="listing-a-1"></a>

```java
{% include_relative LookupSampleProgram.java %}
```


A.2 Adding an OperationDescriptor Example
--------------------------------------------------------------

[Chapter 14, \"Extending the API](../extension),\"
describes how to extend the API by writing custom
OperationDescriptors. [Listing A-2](../Examples) shows the
construction of an `OperationDescriptor`, called `SampleDescriptor`,
that is both an `OperationDescriptor` and a `RenderedImageFactory`.
The operation created here is called `Sample` and takes two parameters
for the operation.

***Listing A-2* Example OperationDescriptor** <a name="listing-a-2"></a>

```java
{% include_relative SampleDescriptor.java %}
```
