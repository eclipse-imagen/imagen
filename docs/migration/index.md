---
layout: default
title: JAI Migration
nav_order: 5
---

# {{ page.title }}

Eclipse ImageN offers a migration path for developers using the Java Advanced Imaging Framework:

* ``org.eclipse.imagen:imagen-all``: includes the core library, bundled with supported operators for which test cases
  have been provided.
  
  The supported operators have been battle tested by the Java Geospatial community and include functionality
  such as NO_DATA (allowing the operators to skip over areas of an image  that are masked out).
  
* ``org.eclipse.imagen:imagen-unsupported-all``: includes the core library bundled with supported operators, and
  unsupported functionality for which no tests are available.
  
  Developers are welcome to pick up any unsupported functionality and write tests.

* ``org.eclipse.imagen:imagen-legacy-all``: includes the core library, bundled with legacy operators only, and
  unsupported functionality for which no tests are available.
  
  Legacy functionality has been identified for removal and will not be avaialble in future releases of ImageN.
  As an example ``codec`` support which has long been superseded by Java and ImageIO.

* In addition to these combined jars, maven projects wishing greater control can depend on only the functionality used
  as individual modules.
  
  Unsupported functionality is provided "as is", and requires test case coverage to be fully supported.

## Automatic Update

1. Download ant migration scripts:
   
   * [pom-updates.xml](https://raw.githubusercontent.com/eclipse-imagen/imagen/refs/heads/master/docs/migration/pom-update.xml)
   * [code-updates.xml](https://raw.githubusercontent.com/eclipse-imagen/imagen/refs/heads/master/docs/migration/code-update.xml)

2. Ant refactoring script for ``pom.xml``:

   ```bash
   ant -f pom-updates.xml -Dproject.dir=(absolute path to your project directory)
   ```
   
   This is a best-effort script recognizing ``jai_core``, ``jai_codec`` dependencies used with ``jai.version``.
   
3. And refactoring script for ``java`` files.
   
   ```
   ant -f code-updates.xml -Dproject.dir=(absolute path to your project directory)
   ```
   
   This is a simple refactoring script to fix imports and class references, and adjust to any
   class name changes made during transition to ImageN library.
   
## Manual Update

To upgrade:

1. To migrate from a project depending on JAI 1.1.3:
   
   ```xml
   <properties>
      <jai.version>1.1.3</jai.version>
   </properties>
   ...
   <dependency>
     <groupId>javax.media</groupId>
     <artifactId>jai_core</artifactId>
     <version>1.1.3</version>
   </dependency>
   <dependency>
     <groupId>org.eclipse.imagen</groupId>
     <artifactId>jai_codec</artifactId>
     <version>${jai.version}</version>
   </dependency>
   ```

2. Replace with Eclipse ImageN dependency:
   
   Replacing:
   
   ```xml
   <properties>
      <imagen.version>0.4-SNAPSHOT</imagen.version>
   </properties>
   ...
   <dependency>
     <groupId>org.eclipse.imagen</groupId>
     <artifactId>imagen-legacy-all</artifactId>
     <version>${imagen.version}</version>
   </dependency>
   ```
   
   Note `imagen-legacy-all` includes the origional legacy operators, and unsupported functionality
   such as the `jai_codec` for which better replacements are avaialble. 

3. Source code imports:
   
   ```java
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

4. Can be directly replaced:
   
   * Replace `javax.media.jai` with package `org.eclipse.imagen`
   * Replace `com.sun.media.jai` with package `org.eclipse.imagen.media`
   
   ```java
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

4. Once your application compiles change to ``org.eclipse.imagen:imagen-all` dependency
   (for core library and supported operators) and add additional unsupported or legacy dependencies as needed.
   
   * Legacy functionality has been identified for removal and will not be avaialble in future releases of ImageN.
   
   * Unsupported functionality is provided "as is", and requires test case coverage to be fully supported.

# Java Image Formats

Both the Java platform and ImageN include encoding/decoding codecs for image formats:

Format         | [Java 8 ImageIO](https://docs.oracle.com/javase/8/docs/api/javax/imageio/package-summary.html) | ImageN Codec   | [Java 11 ImageIO](https://docs.oracle.com/en/java/javase/11/docs/api/java.desktop/javax/imageio/package-summary.html)
-------------- | -------------- | -------------- | -------------- 
BMP            | read/write     | read/write     | read/write
FlashPix       |                | read           | 
GIF            | read/write     | read           | read/write
JPEG           | read/write     | read/write     | read/write
PNG            | read/write     | read/write     | read/write
PNM            |                | read/write     | 
TIFF           |                | read/write     | read/write
WBMP           | read/write     | read           | read/write

Oracle JDK 8 includes the internal `com.sun.image.codec.jpeg` packages used by `imagen-codec` JPEG read/write support listed above. These packages are not available in OpenJDK 8 or Java 11.

The key format missing from Java 8 is TIFF, which is included in `ImageIO` from Java 9 onward. You may wish to continue to use `imagen-codec` to provide TIFF support when operating in a Java 8 environment:

```xml
<profiles>
 <profile>
   <id>java8</id>
   <activation>
     <jdk>1.8</jdk>
   </activation>
   <dependencies>
     <dependency>
       <groupId>org.eclipse.imagen</groupId>
       <artifactId>jai-codec</artifactId>
       <version>${jai.version}</version>
     </dependency>
   </dependencies>
 </profile>
</profiles>
```

# Finalize() removed
Finalizers are deprecated and will soon be removed from Java, as they  are unpredictable, slow, error-prone, and pose security and resource management risks, making them fundamentally unsafe for modern applications. A number of legacy classes have been updated to no longer implement the `finalize` method. In some cases, it has been replaced with a more appropriate cleanup method; in others, a suitable method already existed. The table below summarizes these changes:

Class                       | Removed Method | Replaced by New Method  | Existing Method
----------------------------|----------------|-------------------------|----------------
RMIServerProxy              | finalize       | dispose                 |
RemoteImage                 | finalize       | dispose                 |
PlanarImageServerProxy      | finalize       | dispose                 |
FlieLoadRIF                 | finalize       | close                   |
SeekableStream              | finalize       |                         | close
SerializableRenderableImage | finalize       |                         | dispose
SerializableRenderedImage   | finalize       |                         | dispose


If your code relies on the classes mentioned above, consider updating it to ensure proper resource cleanup is performed.

The following core classes have been updated:

1. `TileScheduler`: it now extends `AutoCloseable`
2. `SunTileScheduler`: it now implements `close` replacing the `finalize` method.
3. `PlanarImage`: the `finalize` method has been removed. `dispose` method already exists to cleanup resources.
4. `JAI`: it now implements `AutoCloseable`, implementing a `close` method that will cleanup the tileScheduler.

If your code relies on the classes mentioned above, consider updating it to ensure proper resource cleanup is performed.
 
