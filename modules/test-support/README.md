# Test Support

Provides test support classes and is intended for use as a test scope dependency:

```xml
    <dependency>
      <groupId>org.eclipse.imagen</groupId>
      <artifactId>test-support</artifactId>
      <version>${project.version}</version>
      <scope>test</scope>
    </dependency>
```

This module forms a build integration point, making use of a number of modules and
responsible for their testing (to avoid circular maven dependencies).

* ``org.eclipse.imagen:binarize``
* ``org.eclipse.imagen:border``

This module is not included in the combined ``imagen-all`` jar and is considered part of the build support.