/* Copyright (c) 2025 Daniele Romagnoli and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution and is available at
 * http://www.opensource.org/licenses/apache2.0.php.
 */
package org.eclipse.imagen.media.testclasses;

import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageOutputStream;
import org.eclipse.imagen.PlanarImage;

/**
 * Utility class to dump images during tests, mainly for debugging/comparison purposes.
 *
 * <p>Currently supports saving images as Deflate-compressed TIFF files.
 */
public class TestImageDumper {

    private TestImageDumper() {}

    public static void saveAsDeflateTiff(Path path, RenderedImage image) {

        try {

            // Pick a TIFF writer
            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("tiff");
            if (!writers.hasNext()) {
                throw new IllegalStateException(
                        "No TIFF ImageWriter found. Add a TIFF plugin (e.g., jai-imageio or TwelveMonkeys).");
            }
            ImageWriter writer = writers.next();

            try (ImageOutputStream ios = ImageIO.createImageOutputStream(path.toFile())) {
                writer.setOutput(ios);
                ImageWriteParam param = writer.getDefaultWriteParam();
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                // Normalize the image, to minX=0, minY=0
                // Not doing that resulted into some byte images being corrupted when deflate compressed
                RenderedImage ri = normalizeImage(image);
                String deflate = findCompressionTypeIgnoreCase(param, "Deflate", "ZLib", "ZIP");
                if (deflate != null) {
                    param.setCompressionType(deflate);
                } else {
                    param.setCompressionMode(ImageWriteParam.MODE_COPY_FROM_METADATA);
                }
                IIOMetadata metadata =
                        writer.getDefaultImageMetadata(ImageTypeSpecifier.createFromRenderedImage(ri), param);
                writer.write(null, new IIOImage(ri, null, metadata), param);
            } finally {
                writer.dispose();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save image: " + e.getMessage(), e);
        }
        if (image instanceof PlanarImage) {
            ((PlanarImage) image).dispose();
        }
    }

    private static RenderedImage normalizeImage(RenderedImage src) {
        if (src.getMinX() == 0 && src.getMinY() == 0 && src.getColorModel() != null) return src;
        int width = src.getWidth();
        int height = src.getHeight();

        // Create a compatible ColorModel and sample raster
        ColorModel cm = src.getColorModel();
        SampleModel sm = src.getSampleModel();
        if (cm == null) {
            // For short datatypes the colorModel may be missing
            cm = setupColorModel(sm);
        }
        Raster srcRaster = src.getData(); // has minX/minY
        WritableRaster dst = srcRaster.createCompatibleWritableRaster(0, 0, width, height);
        Raster srcAtZero = srcRaster.createTranslatedChild(0, 0);
        dst.setRect(srcAtZero);

        // Wrap in a BufferedImage (keeps CM, origin at 0,0)
        return new BufferedImage(cm, dst, cm.isAlphaPremultiplied(), null);
    }

    private static ColorModel setupColorModel(SampleModel sm) {
        int bands = sm.getNumBands();
        int type = sm.getDataType();

        boolean hasAlpha = (bands == 2 || bands == 4); // heuristic for interleaved data
        int colorBands = hasAlpha ? bands - 1 : bands;

        ColorSpace cs = (colorBands == 1)
                ? ColorSpace.getInstance(ColorSpace.CS_GRAY)
                : ColorSpace.getInstance(ColorSpace.CS_sRGB);

        int bitsPerSample = (type == DataBuffer.TYPE_BYTE)
                ? 8
                : (type == DataBuffer.TYPE_USHORT)
                        ? 16
                        : (type == DataBuffer.TYPE_SHORT)
                                ? 16
                                : (type == DataBuffer.TYPE_INT)
                                        ? 32
                                        : (type == DataBuffer.TYPE_FLOAT)
                                                ? 32
                                                : (type == DataBuffer.TYPE_DOUBLE) ? 64 : 8;

        int[] bits = new int[bands];
        Arrays.fill(bits, bitsPerSample);
        int transparency = hasAlpha ? Transparency.TRANSLUCENT : Transparency.OPAQUE;
        return new ComponentColorModel(cs, bits, hasAlpha, false, transparency, type);
    }

    private static String findCompressionTypeIgnoreCase(ImageWriteParam p, String... wanted) {
        String[] types = p.getCompressionTypes();
        if (types == null) return null;
        for (String w : wanted) {
            for (String t : types) {
                if (t.equalsIgnoreCase(w)) return t;
            }
        }
        return null;
    }
}
