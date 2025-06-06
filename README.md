# ImageN

The Eclipse ImageN project provides an extensible, on-demand image processing library with no artificial
restrictions on raster size or number of bands.

![](logo/imagen-horizontal-logo-small.png)

ImageN provides:

* High performance Pure Java Image Processing
* Clear image processing operations, allowing installations to use native libs to accelerate processing if available
* On demand processing of large raster content staging tiles in memory for parallel processing
* No artificial limitation on raster size or number of bands to support multi-spectral imagery

Long term continuation of JAI and JAI-Ext:

* Migration refactoring planned
* Modernize Java API planned

This is a [Eclipse Foundation](https://www.eclipse.org) open source project using the [Apache License v 2.0](LICENSE.md).

For more information:

* [ImageN](https://eclipse-imagen.github.io/imagen/) - website generated from [docs](docs) GitHub pages folder
  
   * [Eclipse ImageN Programming Guide](https://eclipse-imagen.github.io/imagen/guide/)
   
* [ImageN Project](https://projects.eclipse.org/projects/technology.imagen) - Eclipse Project Page
* [Replace JAI](https://github.com/geotools/geotools/wiki/Replace-JAI) - GeoTools Wiki

## Maven Build

Use maven to build on the command line:

    mvn install

The build uses the `javac` compiler argument `-XDignore.symbol.file` to reference JDK codecs directly. This functionality is only available from the `javac` command line and requires maven (or your IDE) to fork each call to `javac`.

Maven build QA modules:

    mvn sortpom:sort
    mvn spotless:apply

## Supported Java Environment

The *ImageN* codebase is in the process of being migrated from a Java Extension to a jar compatible with Java "jigsaw" module system.

| module       | OpenJDK 11 | OpenJDK 17 | OpenJDK 21 |
|--------------|------------|------------|------------|
| modules      | compiles   | compiles   | compiles   | 
| unsupported  | compiles   | compiles   | compiles   | 
| legacy       | compiles   | compiles   | compiles   | 
| legacy/codec | compiles   | compiles   | compiles   | 

If using an unsupported environment:

```
COMPILATION ERROR : 
TIFFImage.java:[59,31] error: package com.sun.image.codec.jpeg does not exist
```