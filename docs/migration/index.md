---
layout: default
title: JAI Migration
nav_order: 5
---

# {{ page.title }}

Eclipse ImageN offers a migration path for developers migrating from the Java Advanced Imaging Framework:

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
  as individual module dependencies.
  
  Unsupported functionality is provided "as is", and requires test case coverage to be fully supported.

## Automatic Update

1. Download ant migration scripts:

    * [pom-update.xml](https://github.com/eclipse-imagen/imagen/blob/main/docs/migration/pom-update.xml)
    * [code-update.xml](https://github.com/eclipse-imagen/imagen/blob/main/docs/migration/code-update.xml)

2. Ant refactoring script for ``pom.xml``:

   ```bash
   ant -f pom-update.xml -Dproject.dir=(absolute path to your project directory)
   ```
   
   This is a best-effort script recognizing ``jai_core``, ``jai_codec`` dependencies used with ``jai.version``.
   
3. And refactoring script for ``java`` files.
   
   ```bash
   ant -f code-update.xml -Dproject.dir=(absolute path to your project directory)
   ```
   
   The default ``update`` target runs each step in order:
   
   * ``update1``: JAI and JAI-Ext imports to ImageN 0.4.0
   * ``update2``: JAI class names and constants to ImageN 0.9.0
   * ``update3``: scale2 package changes in ImageN 0.9.3.3
   
   ImageN projects can run only the steps needed for their upgrade.
   To update a project using ImageN 0.9.2 to ImageN 0.9.3:
   
   ```bash
   ant -f code-update.xml -Dproject.dir=/home/user/dev/myproject update3
   ```
   
4. This is a simple search and replace refactoring script to fix:
   
   * imports and class references
   * class name changes made during transition to ImageN library
   * ParameterBlock constants, like "ImageN.ImageReadParam".

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
     <groupId>javax.media</groupId>
     <artifactId>jai_codec</artifactId>
     <version>${jai.version}</version>
   </dependency>
   ```

2. Replace with Eclipse ImageN dependency:
   
   Replacing:
   
   ```xml
   <properties>
      <imagen.version>{{site.imagen_version}}</imagen.version>
   </properties>
   ...
   <dependency>
     <groupId>org.eclipse.imagen</groupId>
     <artifactId>imagen-legacy-all</artifactId>
     <version>${imagen.version}</version>
   </dependency>
   ```
   
   Note `imagen-legacy-all` includes the original legacy operators, and unsupported functionality
   such as the `jai_codec` for which better replacements are available. 

3. Source code imports and references to JAI in class names and constants:
   
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

4. Can be directly replaced (in order):
   
   * Replace `javax.media.jai` with package `org.eclipse.imagen`
   * Replace `com.sun.media.jai` with package `org.eclipse.imagen.media`
   * Replace `JAI` with `ImageN` classe
   * Replace classes containing the JAI with their ImageN equivalent,
     such as `ParameterBlockJAI` with `ParameterBlockImageN`.
     Consult the table in the next section as a reference.
   * Replace parameter block constants,
     such as `"JAI.ImageReadParam"` with `"ImageN.ImageReadParam"`.
     A complete table is provided in the next section.

   ```java
   import java.awt.Frame;
   import java.awt.image.renderable.ParameterBlock;
   import java.io.IOException;
   import org.eclipse.imagen.Interpolation;
   import org.eclipse.imagen.ImageN;
   import org.eclipse.imagen.RenderedOp;
   import org.eclipse.imagen.media.codec.FileSeekableStream;
   import org.eclipse.imagen.widget.ScrollingImagePanel;

   public class ImageNSampleProgram {
      ...
   }
   ``` 

6. Recommended: Once your application compiles change to `org.eclipse.imagen:imagen-all` dependency
   (for core library and supported operators) and add additional unsupported or legacy dependencies as needed.
   
   * Legacy functionality has been identified for removal and will not be available in future releases of ImageN.
   
   * Unsupported functionality is provided "as is", and requires test case coverage to be fully supported.

# ImageN Name Changes

The following classes have been renamed:

| JAI or JAI-Ext Class          | ImageN Class                    |
|-------------------------------|---------------------------------|
| `ColorSpaceJAI`               | `ColorSpaceImageN`              | 
| `ColorSpaceJAIExt`            | `ColorSpaceImageNExt`           |
| `ColorSpaceJAIExtWrapper`     | `ColorSpaceImageNExtWrapper`    |
| `IHSColorSpaceJAIExt`         | `IHSColorSpaceImageNExt`        |
| `JAI`                         | `ImageN`                        |
| `JAIRMIImageServer`           | `ImageNRMIImageServer`          |
| `JAIServerConfigurationSpi`   | `ImageNServerConfigurationSpi`  |
| `ParameterBlockJAI`           | `ParameterBlockImageN`          |
| `PropertyChangeEventJAI`      | `PropertyChangeEventImageN`     | 
| `LookupTableJAI`              | `LookupTableImageN`             |
| `ComponentSampleModelJAI`     | `ComponentSampleModelImageN`    |
| `PropertyChangeSupportJAI`    | `PropertyChangeSupportImageN`   |
| `KernelJAI`                   | `KernelImageN`                  |
| `ImageJAI`                    | `ImageImageN`                   | 
| `ImageFunctionJAIEXT`         | `ImageFunctionExt`              |
| `ImageFunctionJAIEXTWrapper`  | `ImageFunctionExtWrapper`       |

The following `ImageReadDescriptors` have been renamed:

| JAI Descriptor        | ImageN Descriptor        |
|-----------------------|--------------------------|
| `JAI.ImageReadParam`  | `ImageN.ImageReadParam`  |
| `JAI.ImageReader`     | `ImageN.ImageReader`     |
| `JAI.ImageMetadata`   | `ImageN.ImageMetadata`   |
| `JAI.StreamMetadata`  | `ImageN.StreamMetadata`  |
| `JAI.Thumbnails`      | `ImageN.Thumbnails`      |
| `JAI.RenderableInput` | `ImageN.RenderableInput` |

The registry files have been renamed

| JAI or JAI-Ext              | ImageN                         |
|-----------------------------|--------------------------------|
| `META-INF/registryFile.jai` | `META-INF/registryFile.imagen` | 

Scale2 has been moved to a new package in ImageN 0.9.3:

| ImageN 0.9.2                              | ImageN 0.9.3                                   |
|------------------------------------------------|-------------------------------------------------|
| `org.eclipse.imagen.media.scale.Scale2*`       | `org.eclipse.imagen.media.scale2.Scale2*`       |

# Java Image Formats

Both the Java platform and ImageN include encoding/decoding codecs for image formats:

| Format   | Java 8 ImageIO  | ImageN Codec | Java 11 ImageIO |
|----------|-----------------|--------------|-----------------| 
| BMP      | read/write      | read/write   | read/write      |
| FlashPix |                 |              |                 |
| GIF      | read/write      | read         | read/write      |
| JPEG     | read/write      |              | read/write      |
| PNG      | read/write      | read/write   | read/write      |
| PNM      |                 | read/write   |                 |
| TIFF     |                 |              | read/write      | 
|  WBMP    | read/write      | read         | read/write      |


The ImageN codec module no longer provides FlashPix, JPEG or TIFF support. ImageN requires Java 17, so use `ImageIO`, or the `ImageRead` operation, for JPEG and TIFF.

# Finalize() removed

Finalizers have been deprecated in Java 18, as they are unpredictable, slow, error-prone, and pose security and resource management risks, making them fundamentally unsafe for modern applications. A number of legacy classes have been updated to no longer implement the `finalize` method. In some cases, it has been replaced with a more appropriate cleanup method; in others, a suitable method already existed. The table below summarizes these changes:

| Class                         | Removed Method | Replaced by New Method  | Existing Method |
|-------------------------------|----------------|-------------------------|-----------------|
| `RMIServerProxy`              | finalize       | dispose                 |                 |
| `RemoteImage`                 | finalize       | dispose                 |                 |
| `PlanarImageServerProxy`      | finalize       | dispose                 |                 |
| `FlieLoadRIF`                 | finalize       | close                   |                 |
| `SeekableStream`              | finalize       |                         | close           |
| `SerializableRenderableImage` | finalize       |                         | dispose         |
| `SerializableRenderedImage`   | finalize       |                         | dispose         |


If your code relies on the classes mentioned above, consider updating it to ensure proper resource cleanup is performed.

The following core classes have been updated:

1. `TileScheduler`: it now extends `AutoCloseable`
2. `SunTileScheduler`: it now implements `close` replacing the `finalize` method.
3. `PlanarImage`: the `finalize` method has been removed. `dispose` method already exists to clean up resources.
4. `JAI`: it now implements `AutoCloseable`, implementing a `close` method that will clean up the tileScheduler.

If your code relies on the classes mentioned above, consider updating it to ensure proper resource cleanup is performed.
 

# JPMS Allowances

ImageN runs on the Java Platform Module System. Each jar is an automatic module, for example `org.eclipse.imagen.core` or `org.eclipse.imagen.legacy.core`. The following improvements allow operations to load on both the classpath and the module path.

## JaiI18N and Message properties removed

The package-private `JaiI18N` helper classes have been removed, and  ImageN no longer reads exception and descriptor text from `org.eclipse.imagen/*.properties` resource bundles.

 *  Operations following the JAI pattern of a `JaiI18N` class should replace their own lookups with string literals.
 
    ```java
    private static final String[][] resources = {
        {"GlobalName", "Rotate"},
        {"Description", "Rotates an image."},
        ...
    };
    ```
 
 *  `PropertyUtil.getString(String, String)` is no longer used and is deprecated, however it still reads `org.eclipse.imagen/<package>.properties` allowing operations that ship their own bundle keep working.
 *  Legacy codec-core `org.eclipse.imagen.media.codecimpl.util.PropertyUtil` is deprecated. Use imagen-core `org.eclipse.imagen.media.util.PropertyUtil`.

## Allow-list configuration

ImageN only creates classes it trusts when loading by reflection:

* `RegistryFileParser` creates the descriptor and factory classes named in `META-INF/registryFile.imagen`. Other entries are skipped with the warning `Class ... is not in the allow-list`.
* `Service` creates the `OperationRegistrySpi` providers listed in `META-INF/services`.

Operation libraries contribute trusted classes by implementing `RegistryAllowListProvider` and `ServiceAllowListProvider`:

```java
public class ExampleRegistryAllowListProvider implements RegistryAllowListProvider {
    @Override
    public Set<String> getAllowedRegistryClasses() {
        return Set.of("org.example.media.ExampleDescriptor", "org.example.media.ExampleCRIF");
    }
}
```

Applications can add additional classes with a System Property or Environment Variable, as comma-separated fully qualified class names:

| Allow-list | System property | Environment variable |
|---|---|---|
| Registry classes | `org.eclipse.imagen.allowedRegistryClasses` | `ORG_ECLIPSE_IMAGEN_ALLOWED_REGISTRY_CLASSES` |
| Service providers | `org.eclipse.imagen.allowedServiceProviderClasses` | `ORG_ECLIPSE_IMAGEN_ALLOWED_SERVICE_PROVIDER_CLASSES` |

Each allow-list is read once per JVM, on first use.

For example, GeoTools palette operations without a contributed provider requires:

```
-Dorg.eclipse.imagen.allowedRegistryClasses=\
org.geotools.image.palette.ColorReductionDescriptor,\
org.geotools.image.palette.ColorInversionDescriptor,\
org.geotools.image.palette.ColorReductionCRIF,\
org.geotools.image.palette.ColorInversionCRIF
```

## Registering operations with OperationRegistrySpi

Operations are normally registered in `META-INF/registryFile.imagen`. Use an `OperationRegistrySpi` only to register an operation conditionally. Providers run after every `registryFile.imagen` has been read, so they can check what is already registered.

To avoid being dependent on classpath order `imagen-legacy-core` uses this approach: allowing any `imagen-*` module operation with the same name to take precedence. From `LegacyCoreSpi`:

```java
public class LegacyCoreSpi implements OperationRegistrySpi {

    @Override
    public void updateRegistry(OperationRegistry registry) {
        register(registry, new AddDescriptor(), PRODUCT, new AddCRIF(), true);
        ...
    }

    private static void register(
            OperationRegistry registry,
            OperationDescriptor descriptor,
            String product,
            RenderedImageFactory factory,
            boolean renderable) {
        String name = descriptor.getName();
        if (registry.getDescriptor(OperationDescriptor.class, name) != null) {
            LOGGER.log(Level.FINE, "Operation {0} already registered, skipping {1}", new Object[] {
                name, descriptor.getClass().getName()
            });
            return;
        }
        registry.registerDescriptor(descriptor);
        if (factory != null) {
            registry.registerFactory(RenderedRegistryMode.MODE_NAME, name, product, factory);
            if (renderable) {
                registry.registerFactory(
                        RenderableRegistryMode.MODE_NAME, name, product, (ContextualRenderedImageFactory) factory);
            }
        }
    }
}
```

`OperationRegistrySpi` providers must be listed in `META-INF/services/org.eclipse.imagen.OperationRegistrySpi`, and be on the service allow-list, see [Allow-list configuration](#allow-list-configuration).

## Tips for operation implementors

* List each provider in `META-INF/services`. ImageN reads `OperationRegistrySpi` providers from these files on both the classpath and the module path.
* In a named module, also declare the allow-list providers with `provides`, as they are discovered by `java.util.ServiceLoader`:

  ```java
  module org.example.media {
      requires org.eclipse.imagen.core;
      provides org.eclipse.imagen.spi.RegistryAllowListProvider with org.example.media.ExampleRegistryAllowListProvider;
      provides org.eclipse.imagen.spi.ServiceAllowListProvider with org.example.media.ExampleServiceAllowListProvider;
  }
  ```

* Use a unique operation name; when two jars register the same name, the first `registryFile.imagen` read wins.
* When shading jars, append `META-INF/registryFile.imagen` with an `AppendingTransformer` and merge `META-INF/services` with the `ServicesResourceTransformer`.
