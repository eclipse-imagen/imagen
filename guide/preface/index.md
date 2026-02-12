---
layout: default
title: Preface
parent: Programming Guide
nav_order: 1
---

# Preface

This document introduces the Eclipse ImageN API and how to
program in it. This document is intended for serious programmers who
want to use ImageN for real projects. To best
understand this document and the examples, you need a solid background
in the Java programming language and some experience with imaging. In
addition, you will need a working knowledge of other Java APIs,
depending on your intended application:

- *Java ImageIO* for image encoding and decoding

- *Java 2D* for simple graphics, text, and fundamental image manipulation

This library is an Apache License v 2.0 release of the former *Java Advanced Imaging API*
codebase. Method compatibility has been maintained using new packages and library name.

Disclaimer
----------

This version of is based on release {{site.imagen_version}} of the Eclipse ImageN API:

> If any discrepancies between this guide and the javadocs are noted, always
consider the javadocs to be the most accurate, since they are
generated directly from the source files and are always the most up to
date.

About This Book
---------------

**[Chapter 1, \"Introduction to Eclipse ImageN\"](../introduction),** gives an overview of the Eclipse ImageN API, how it evolved from the original Java Advanced Windowing Toolkit (AWT), previous Java Advanced Imaging API (JAI), some of its features, and introduces the imaging operations.

**[Chapter 2, \"Java AWT Imaging\"](../j2d-concepts),"** reviews the imaging portions of the Java AWT and examines the imaging features of the Java 2D API.

**[Chapter 3, \"Programming Environment\"](../programming-environ),** describes how to get started programming with the Eclipse ImageN.

**[Chapter 4, \"Image Acquisition and
Display\"](../acquisition),** describes the image data types and the API constructors and methods for image acquisition and display.

**[Chapter 5, \"Color Space\"](../color),** describes the color space, transparency, and the color conversion operators.

**[Chapter 6, \"Image
Manipulation\"](../image-manipulation),** describes the basics of manipulating images to prepare them for processing and display.

**[Chapter 7, \"Image Enhancement\"](../image-enhance),** describes the basics of improving the visual appearance of images through enhancement techniques.

**[Chapter 8, \"Geometric Image
Manipulation\"](../geom-image-manip),** describes the basics of geometric image manipulation functions.

**[Chapter 9, \"Image Analysis\"](../analysis),** describes the image analysis operators.

**[Chapter 10, \"Graphics Rendering\"](../graphics),** describes the presentation of shapes and text.

**[Chapter 11, \"Image Properties\"](../properties),** describes the tools that allow a programmer to add a simple database of arbitrary data that can be attached to images.

**[Chapter 12, \"Client-Server
Imaging\"](../client-server),** describes client-server imaging system.

**[Chapter 13, \"Writing Image Files\"](../encode),** describes Eclipse ImageN\'s codec system for encoding image data files.

**[Chapter 14, \"Extending the API\"](../extension),** describes Eclipse ImageN API is extended.

**[Appendix A, \"Program Examples\"](../Examples),** contains fully-operational Eclipse ImageN program examples.

**[Appendix B, \"API
Summary\"](../api-summary),** summarizes the imaging interfaces, and classes, including the `java.awt`, `java.awt.Image`, and `org.eclipse.imagen` classes.

The **[Glossary](../glossary)** contains descriptions of significant terms that appear in this book.

Related Documentation <a name="related"></a>
---------------------

For more information on digital imaging, we suggest you refer to the
following books:

-   *Fundamentals of Digital Image Processing*, by Anil K. Jain

-   *Digital Image Processing: Principles and Applications*, by
    Gregory A. Baxes

-   *Digital Image Processing*, by Kenneth R. Castleman

-   *Digital Image Processing*, 2nd. ed., by William K. Pratt

Additional Information
----------------------

Since Eclipse ImageN continues to evolve and periodically add
new operators, it is always a good idea to occasionally check the
project page for the latest information.

* `https://projects.eclipse.org/projects/technology.imagen`

The web site contains links to the latest version of ImageN, email
list to take part in the community, and tutorial and demos 
including examples of the use of many ImageN operators.

Style Conventions
-----------------

The following style conventions are used in this document:

-   `Monspace` is used to represent computer code and the names of
    files and directories.

-   **Bold** is used for API declarations.

-   *Italic type* is used for emphasis and for equations.

Throughout the book, we introduce many API calls with the following format:

**API:** `org.eclipse.imagen.TiledImage`

When introducing an API call for the first time, we add a short
list of the methods, tagged with the API heading.

Code Examples
-------------

Notice:

> Example content is intended to demonstrate usage of Eclipse technology. It is provided to you under the terms and conditions of the Eclipse Distribution License v1.0 which is available at http://www.eclipse.org/org/documents/edl-v10.php
