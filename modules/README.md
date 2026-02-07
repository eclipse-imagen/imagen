ImageN Modules

## Aggregate javadocs

Javadocs are managed with the `javadoc.skip` property. To build aggregate javadocs use the `javadoc` profile
to change the `javadoc.skip` to `false.`

```bash
mvn compile javadoc:aggregate -Pjavadoc
```

The generated javadocs are located in: `target/reports/apidocs`