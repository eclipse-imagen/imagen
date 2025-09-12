/* Copyright (c) 2025 Daniele Romagnoli and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution and is available at
 * http://www.opensource.org/licenses/apache2.0.php.
 */
package org.eclipse.imagen;

import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferShort;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;

/** A set of utility methods and constants supporting the test cases. */
public class TestSupport {

    /** An array of data types to be tested. */
    public static final int[] DATA_TYPES = {
        DataBuffer.TYPE_BYTE,
        DataBuffer.TYPE_USHORT,
        DataBuffer.TYPE_SHORT,
        DataBuffer.TYPE_INT,
        DataBuffer.TYPE_FLOAT,
        DataBuffer.TYPE_DOUBLE
    };

    /**
     * Creates a BufferedImage of the specified data type and dimensions. For integral data types, a grayscale image is
     * created. For floating point data types, a single band image with a suitable ColorModel is created.
     *
     * @param dataType The data type of the image to be created.
     * @param w The width of the image to be created.
     * @param h The height of the image to be created.
     * @return A BufferedImage of the specified data type and dimensions.
     * @throws IllegalArgumentException if <code>dataType</code> is not one of the supported data types.
     */
    public static BufferedImage createImage(int dataType, int w, int h) {
        BufferedImage img;
        switch (dataType) {
            case DataBuffer.TYPE_BYTE:
                img = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
                break;
            case DataBuffer.TYPE_USHORT:
                img = new BufferedImage(w, h, BufferedImage.TYPE_USHORT_GRAY);
                break;
            case DataBuffer.TYPE_INT:
                img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                break;
            case DataBuffer.TYPE_FLOAT:
                SampleModel smFloat = RasterFactory.createPixelInterleavedSampleModel(DataBuffer.TYPE_FLOAT, w, h, 1);
                DataBuffer dbFloat = new DataBufferFloat(w * h);
                WritableRaster wrFloat = RasterFactory.createWritableRaster(smFloat, dbFloat, null);
                img = new BufferedImage(getGrayComponentColorModel(DataBuffer.TYPE_FLOAT), wrFloat, false, null);
                break;
            case DataBuffer.TYPE_DOUBLE:
                SampleModel smDouble = RasterFactory.createPixelInterleavedSampleModel(DataBuffer.TYPE_DOUBLE, w, h, 1);
                DataBuffer dbDouble = new DataBufferDouble(w * h);
                WritableRaster wrDouble = RasterFactory.createWritableRaster(smDouble, dbDouble, null);
                img = new BufferedImage(getGrayComponentColorModel(DataBuffer.TYPE_DOUBLE), wrDouble, false, null);
                break;
            case DataBuffer.TYPE_SHORT:
                SampleModel smShort = RasterFactory.createPixelInterleavedSampleModel(DataBuffer.TYPE_SHORT, w, h, 1);
                DataBuffer dbShort = new DataBufferShort(w * h);
                WritableRaster wrShort = RasterFactory.createWritableRaster(smShort, dbShort, null);
                img = new BufferedImage(getGrayComponentColorModel(DataBuffer.TYPE_SHORT), wrShort, false, null);
                break;
            default:
                throw new IllegalArgumentException("Unsupported type " + dataType);
        }
        return img;
    }

    private static ComponentColorModel getGrayComponentColorModel(int typeFloat) {
        return new ComponentColorModel(
                ColorSpace.getInstance(ColorSpace.CS_GRAY), false, false, Transparency.OPAQUE, typeFloat);
    }

    public static FloatDoubleColorModel getFloatColorModel(boolean hasAlpha, boolean premultiplied) {
        return new FloatDoubleColorModel(
                ColorSpace.getInstance(ColorSpace.CS_sRGB),
                hasAlpha,
                premultiplied,
                hasAlpha ? Transparency.TRANSLUCENT : Transparency.OPAQUE,
                DataBuffer.TYPE_FLOAT);
    }

    public static FloatDoubleColorModel getDoubleColorModel(boolean hasAlpha, boolean premultiplied) {
        return new FloatDoubleColorModel(
                ColorSpace.getInstance(ColorSpace.CS_sRGB),
                hasAlpha,
                premultiplied,
                hasAlpha ? Transparency.TRANSLUCENT : Transparency.OPAQUE,
                DataBuffer.TYPE_DOUBLE);
    }
}
