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

Maven build QA modules (both are applied transparently during the normal build, use manually if needed):

    mvn sortpom:sort
    mvn spotless:apply

Building with Jacoco aggregate code coverage:

    mvn clean install -Pjacoco
    <your_browser> modules/all/target/site/jacoco-aggregate/index.html

## Supported Java Environment

The *ImageN* codebase has been migrated from the original Java Plugin to a jar compatible with Java "jigsaw" module system. It no longer uses the namespace `javax` and is able to be used as a normal Java library.

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

Update artifacts:

### Update Artifacts

On main:

1. Before you start check that the Maven build executes with no errors using JDK 11:

   ```
   sdk use java 17.0.17-tem
   ```

2. Update version number in Maven POMs (run the Maven versions plugin at project root):

   ```
   mvn versions:set -DgenerateBackupPoms=false -DnewVersion=0.9.1
   ```

3. Commit this change.

   ```
   git add .
   git commit -m "Release version 0.9.1"
   git push
   ```

4. Tag this commit, and push the tag to GitHub.

   ```
   git tag -a 0.4.0 -m "Release version 0.9.1"
   git push --tags
   ```

   This is the commit that will form the GitHub release below.

### Create Release Artifacts

1. Before you start double check that you have `gpg` installed and configured, with your public key distributed.

   References: [Working with PGP Signatures](https://central.sonatype.org/pages/working-with-pgp-signatures.html)

2. The `gpg-agent` will remember a passphrase for a short duration.

   To interact with the agent (so it asks you the passphrase):

   ```
   gpg --use-agent --armor --detach-sign --output - pom.xml
   ```

   Reference: [Configuring GPG/PGP for Maven Releases to Sonatype on Mac OS X](https://nblair.github.io/2015/10/29/maven-gpg-sonatype/)

2. Execute the final Maven release build which will sign jars:

   ```
   mvn clean install -Drelease
   ```

### Deploy the Release

1. Deploy to Maven Central, using credentials in your `~/.m2/settings.xml`:

   ```
   <server>
      <id>central</id>
      <username>generated_user</username>
      <password>generated_password</password>
   </server>
   ```

   Reference: [Publishing By Using the Maven Plugin](https://central.sonatype.org/publish/publish-portal-maven/)

2. Deploy to repo.osgeo.org:

   ```
   mvn deploy -DskipTests -DskipTests
   ```
   
   Outdated: Deploy to Maven Central with the release property and profile

   ```
   mvn deploy -Drelease -DskipTests
   ```
   
   A successful deploy will verify, and then wait for you to publish:
   
   ```
   Deployment 9590fb21-a026-4451-9722-a7216b258f4d has been validated. To finish publishing visit https://central.sonatype.com/publishing/deployments
   ```
   
   Check the artifacts work as expected before manually publishing.
 
4. Create a [GitHub release](https://github.com/eclipse-imagen/imagen/releases)

  1. Navigate to https://github.com/eclipse-imagen/imagen/releases and use "Draft new Release"
     based on your tag.

  2. Copy the release notes:

     Example: [0.9.1](tps://github.com/eclipse-imagen/imagen/releases/tag/0.9.1]
     
     You may also wish to hit "generate release notes".

  3. Add release artifacts (from the `target` folders):

    * modules/all/target/imagen-all-0.9.1.jar
    * legacy/all/target/imagen-legacy-all-0.9.1.jar

  4. Tip: Mark as a draft release (until Eclipse review process completes)

### Post release

Update main to the next release version:

1. Update version number in Maven POMs (run the Maven release plugin at project root):

   ```
   mvn versions:set -DgenerateBackupPoms=false -DnewVersion=0.9.2-SNAPSHOT
   ```

2. Compile to test, and commit this change.

   ```
   mvn clean install
   git add .
   git commit -m "Version 0.9.2-SNAPSHOT"
   git push
   ```  

### Announcing

* Message to [imagen-dev@eclipse.org](https://accounts.eclipse.org/mailing-list/imagen-dev)
* Comment on [Matrix channel](https://matrix.to/#/#technology.imagen-dev:matrix.eclipse.org)
* Social media?
* Others?

   
  