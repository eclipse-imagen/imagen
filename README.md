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
   * [JAI Migration](https://eclipse-imagen.github.io/imagen/migration/) - including JPMS allow-list configuration
   
* [ImageN Project](https://projects.eclipse.org/projects/technology.imagen) - Eclipse Project Page
* [Replace JAI](https://github.com/geotools/geotools/wiki/Replace-JAI) - GeoTools Wiki

## Maven Build

Use maven to build on the command line:

    mvn install

The build uses the `javac` compiler argument `-XDignore.symbol.file` to reference JDK codecs directly. This functionality is only available from the `javac` command line and requires maven (or your IDE) to fork each call to `javac`.

Maven build QA modules (both are applied transparently during the normal build, use manually if needed):

    mvn sortpom:sort
    mvn spotless:apply

Building with Jacoco aggregate code coverage:

    mvn clean install -Pjacoco
    <your_browser> modules/all/target/site/jacoco-aggregate/index.html

## Supported Java Environment

The *ImageN* codebase has been migrated from the original Java Plugin to a jar compatible with Java jigsaw module system. It no longer uses the namespace `javax` and is able to be used as a normal Java library.

| module       | OpenJDK 17 | OpenJDK 21 | OpenJDK 25 |
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

## Release

Prep:

1. Locate [Release Milestone](https://github.com/eclipse-imagen/imagen/milestones) for the release
2. Apply this milestones to Issues and PRs included in the release.
3. Prepare Release Notes:
   
   * Record significant changes
   * Enter date of release

3. For minor release we are okay to proceed without formal review.

  * Email imagen-dev@eclipse.org that release is stated
  * Use text from release notes to describe the release

3. For major release start eclipse release process
   
   Example [JTS 1.17.0-release-review](https://projects.eclipse.org/projects/locationtech.jts/reviews/1.17.0-release-review) page.
   
   * Use text from release notes to describe the release
   * Email review page to imagen-dev@eclipse.org for aproval
   * Email review page emo@eclipse.org when ready, to save time link to the imagen-dev email approval thread
   * EMO opens a [bug ticket like this](https://bugs.eclipse.org/bugs/show_bug.cgi?id=564358) to track progress
    
   This takes about 2 weeks, schuedled for 1st and 15th each month.

Release artifacts are published to two repositories:

* **repo.osgeo.org** - deployed by a release manager from their own machine (see [Deploy to OSGeo](#deploy-to-osgeo)).
* **Maven Central** - built, signed and published by the [Publish release to Maven Central](.github/workflows/central.yml)
  workflow from a release tag, using the Eclipse Foundation managed signing key and Central credentials
  (see [Publish to Maven Central](#publish-to-maven-central)). This cannot be done locally.

### Update Version

1. Check the Maven build executes with no errors using JDK 17:

   ```
   sdk use java 17.0.17-tem
   mvn clean install
   ```

2. On a release branch update version number in Maven POMs:

   ```
   git fetch upstream
   git checkout -b release-0.9.3 upstream/main
   mvn versions:set -DgenerateBackupPoms=false -DnewVersion=0.9.3
   ```

3. Commit this change and open a pull request, merge once the build passes.

4. Tag the merged commit on `main`, and push the tag to `eclipse-imagen/imagen`:

   ```
   git fetch upstream
   git tag -a 0.9.3 -m "Release version 0.9.3" upstream/main
   git push upstream 0.9.3
   ```

   The tag must match the POM version exactly, the Maven Central workflow checks this before publishing.

### Deploy to OSGeo

1. Checkout the tag:

   ```
   git checkout 0.9.3
   ```

2. Deploy to repo.osgeo.org, using your OSGeo credentials in `~/.m2/settings.xml`:

   ```
   <server>
     <id>nexus</id>
     <username>osgeo_user</username>
     <password>osgeo_password</password>
   </server>
   ```

   Then deploy using one of:

   * Signed release, with sources, javadocs and signatures using your own `gpg` key (matches what is published to Maven Central):

     ```
     mvn clean deploy -Drelease -DskipTests
     ```

     Reference: [Working with PGP Signatures](https://central.sonatype.org/publish/requirements/gpg/)

   * Unsigned, with sources but no javadocs (no `gpg` setup required):

     ```
     mvn clean deploy -DskipTests
     ```

### Publish to Maven Central

1. Create a [GitHub release](https://github.com/eclipse-imagen/imagen/releases):

   1. Use "Draft a new release" and choose your tag.
   2. Copy the release notes (example: [0.9.3](https://github.com/eclipse-imagen/imagen/releases/tag/0.9.3),
      you may also wish to hit "Generate release notes".
   3. Add release artifacts (from the `target` folders of the OSGeo deploy above):

      * `modules/all/target/imagen-all-0.9.3.jar`
      * `legacy/all/target/imagen-legacy-all-0.9.3.jar`

2. Publish the release, this starts the
   [Publish release to Maven Central](https://github.com/eclipse-imagen/imagen/actions/workflows/central.yml) workflow.

   To keep the GitHub release as a draft (while an Eclipse release review completes), run the workflow manually
   with "Run workflow" and enter the tag instead.

3. Check the workflow completes, and the artifacts appear on
   [Maven Central](https://central.sonatype.com/namespace/org.eclipse.imagen) (this may take a while).
   If Maven Central rejects the deployment, the validation errors are shown in the workflow log.

References:

* [Eclipse CBI: Publishing to Maven Central with GitHub Actions](https://eclipse.dev/cbi/best-practices/github-actions/central-portal/) - Eclipse Foundation guidance followed by the workflow
* [Eclipse Project Handbook: Releases](https://www.eclipse.org/projects/handbook/#release) - release reviews and process
* [Eclipse Foundation Helpdesk](https://gitlab.eclipse.org/eclipsefdn/helpdesk) - ask for help with Maven Central credentials or signing keys

### Post release

Update main to the next release version:

1. On a branch update version number in Maven POMs:

   ```
   git checkout -b version-0.9.4-SNAPSHOT upstream/main
   mvn versions:set -DgenerateBackupPoms=false -DnewVersion=0.9.4-SNAPSHOT
   ```

2. Update version number in `docs/_config.yml`:

   ```
   imagen_version: "0.9.4-SNAPSHOT"
   ```

3. Compile to test, commit this change and open a pull request:

   ```
   mvn clean install
   ```

### Announcing

* Message to [imagen-dev@eclipse.org](https://accounts.eclipse.org/mailing-list/imagen-dev)
* Comment on [Matrix channel](https://matrix.to/#/#technology.imagen-dev:matrix.eclipse.org)
* Social media?
* Others?

   
  
