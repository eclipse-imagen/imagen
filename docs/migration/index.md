---
layout: default
title: JAI Migration
nav_order: 5
---
# JAI Migration

Eclipse ImageN offers a migration path for developers using the Java Advanced Imaging Framework.

To upgrade:

1. To migrate from a project depending on JAI 1.1.3:
   
   ```XML
   <properties>
      <jai.version>1.1.3</jai.version>
   </properties>
   ...
   <dependency>
     <groupId>javax.media</groupId>
     <artifactId>jai_core</artifactId>
     <version>1.1.3</version>
   </dependency>
   ```

2. Replace with Eclipse ImageN dependency:
   
   Replacing:
   
   ```XML
   <properties>
      <jai.version>0.4-SNAPSHOT</jai.version>
   </properties>
   ...
   <dependency>
     <groupId>org.eclipse.imagen</groupId>
     <artifactId>imagen_core</artifactId>
     <version>${jai.version}</version>
   </dependency>
   <dependency>
     <groupId>org.eclipse.imagen</groupId>
     <artifactId>jai_codec</artifactId>
     <version>${jai.version}</version>
   </dependency>
   ```

3. Source code imports:
   
   ```Java
     import java.awt.Frame;
     import java.awt.image.renderable.ParameterBlock;
     import java.io.IOException;
     import javax.media.jai.Interpolation;
     import javax.media.jai.JAI;
     import javax.media.jai.RenderedOp;
     import com.sun.media.jai.codec.FileSeekableStream;
     import javax.media.jai.widget.ScrollingImagePanel;
     
     public class JAISampleProgram {
        ...
     }
   ```

3. Can be directly replaced:
   
   * Replace `javax.jai` with package `org.eclipse.imagen`
   * Replace `com.sun.media.jai` with package `org.eclipse.imagen.media`
   
   ```Java
   import java.awt.Frame;
   import java.awt.image.renderable.ParameterBlock;
   import java.io.IOException;
   import org.eclipse.imagen.Interpolation;
   import org.eclipse.imagen.JAI;
   import org.eclipse.imagen.RenderedOp;
   import org.eclipse.imagen.media.codec.FileSeekableStream;
   import org.eclipse.imagen.widget.ScrollingImagePanel;

   public class ImageNSampleProgram {
      ...
   }
   ```

# Java 8 Image Formats

Java 8 is a Long Term Support release, and includes the internal `com.sun.image.codec.jpeg` packages used by `imagen_codec`. These packages are not available in Java 11.

When running in Java 8 `imagen_codec` supports:

----------

Format         | [Java 8 ImageIO](https://docs.oracle.com/javase/8/docs/api/javax/imageio/package-summary.html) | imagen_codec   | [Java 11 ImageIO](https://docs.oracle.com/en/java/javase/11/docs/api/java.desktop/javax/imageio/package-summary.html)
-------------- | -------------- | -------------- | -------------- 
BMP            | read/write     | read/write     | read/write
FlashPix       |                | read           | 
GIF            | read/write     | read           | read/write
JPEG           | read/write     | read/write     | read/write
PNG            | read/write     | read/write     | read/write
PNM            |                | read/write     | 
TIFF           |                | read/write     | read/write
WBMP           | read/write     | read           | read/write

----------

The key format missing from Java 8 is TIFF, which is included in `ImageIO` from Java 9 onward. You may wish to continue to use `imagen_codec` to provide TIFF support when operating in a Java 8 environment:

```XML
<profiles>
 <profile>
   <id>java8</id>
   <activation>
     <jdk>1.8</jdk>
   </activation>
   <dependencies>
     <dependency>
       <groupId>org.eclipse.imagen</groupId>
       <artifactId>jai_codec</artifactId>
       <version>${jai.version}</version>
     </dependency>
   </dependencies>
 </profile>
</profiles>
```

# MediaLib (Unsupported)

This work is provided to aid those migrating to Eclipse ImageN and is not supported or intended for production use. Recent experience shows that improvements in JIT compiler technology have eroded the performance benefits of these native operations. While the MediaLib library was released as part of OpenSolaris the specific builds distributed with JAI remain closed source and cannot be maintained.

The operations taking advantage of `mlibwrapper_jai` have been factored out into a seperate `imagen_mlib` dependency:

```XML
<dependency>
    <groupId>javax.media</groupId>
    <artifactId>mlibwrapper_jai</artifactId>
    <version>1.1.3</version>
</dependency>
<dependency>
  <groupId>org.eclipse.imagen</groupId>
  <artifactId>imagen_mlib</artifactId>
  <version>${jai.version}</version>
</dependency>
```
