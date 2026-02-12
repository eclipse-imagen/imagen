# ImageN Documentation

This folder provides documentation for the ImageN project.

## Jekyell

[Jekyll](https://jekyllrb.com) is written in Ruby and managed as a gem, requiring [installation](https://jekyllrb.com/docs/installation/) of the following environment:

```
ruby -v
gem -v
gcc -v
g++ -v
make -v
```

Use `gem` to install `bundler`:

```
gem install bundler
```

Use bundle to download `jekyll` (and other gems listed in [Gemfile](Gemfile):

```
bundle
```

To build and run using the [Gemfile](Gemfile) environment:

```
bundle exec jekyll serve
```

Viewing the result in your browser: [http://localhost:4000/](http://localhost:4000/):

```
open http://localhost:4000/
```

## Site Theme

The site uses the [Just The Docs](https://pmarsceill.github.io/just-the-docs/) theme (MIT License).

The [default.html](_layouts/default.html) layout has been modified to include [copyright.html](_includes/copyright.html).

## Site Structure

Definition:

* [_data](_data)
* [_includes](_includes) - reusable page snippets
* [_includes/src](_includes/src) - source code examples 
* [_layouts](_layouts) - maps page variables and content to html output
* [_sass](_sass) - reusable css snippets
* [assets/css](assets/css)
* [assets/images](assets/images)
* [assets/js](assets/js)
* [_config.yaml](_config.yaml) Jekyell configuration including global variables
* [Gemfile](Gemfile) ruby environment definition for  [bundler](https://rubygems.org/gems/bundler)
* README.md - readme page for display in GitHub repository

Content:

* [index.md](index.md) - main page
* [guide](guide) - Programming Guide

Generated:

* [javadocs](javadocs) - refresh using `bin/apidocs.sh`

## Pages

Each page of content starts with a YAML front matter snippet located between two ``---`` lines.

```Markdown
---
layout: default
title: Welcome
nav_order: 5
---

# {{ page.title }}

Visit [Eclipse ImageN](https://projects.eclipse.org/projects/technology.imagen) project
page for more information.
```

The front matter snippet defines variables used for our page layout, and is followed by Markdown used to define page `{{ content }}`.

The `nav_order` is used by the site theme, for details see [navigation structure](https://pmarsceill.github.io/just-the-docs/docs/navigation-structure/).

## Code Examples

Markdown highlighting is used for code snippets:

````
```java
   // Retrieve and report the mean pixel value.
   double[] mean = (double[])meanImage.getProperty("mean");
   System.out.println("Band 0 mean = " + mean[0]);
```
````

Complete code examples are placed in [_includes/src](_includes/src) folder:

````
```java
{% include src/ImageNSampleProgram.java %}
```
````

