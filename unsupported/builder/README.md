# ImageN Builder

Experimental module exploring use of fluent builder API to improve library usability.

Use as a builder to create RenderedImage:

```
RenderedImage source = new FileLoad("0E_60N.tif");
RenderedImage image = new Affine(source).scale(3,9).rotate(1,2).rotate(2,4).build();
```

Use as a type safe lazy parameter block:
```
RenderedImage source = new FileLoad("0E_60N.tif");
OperationBuilder builder = new Affine( source ).scale(3,9);
RenderedImage image = ImageN.create(builder);
```

OperationBuilder may also be used directly:

```
RenderedImage image = new OperationBuilder("Affine").source(source).parameter("affine", affine)
        .hint(Interpolation.BILINEAR).build();
```

For more details and an example see [ImageN Builder API Design](design.md).