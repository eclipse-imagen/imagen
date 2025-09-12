/* Copyright (c) 2025 Daniele Romagnoli and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution and is available at
 * http://www.opensource.org/licenses/apache2.0.php.
 */
package org.eclipse.imagen;

import static org.junit.Assert.*;

import java.awt.image.*;
import org.junit.Test;

/** Tests for org.eclipse.imagen.FloatDoubleColorModel */
public class FloatDoubleColorModelTest {

    public static final int DELTA_INT = 1;
    public static final double DELTA = 1e-6;
    public static final float ZERO_DELTA = 0f;

    private static WritableRaster makeFloatRasterRGBA(int w, int h) {
        SampleModel sm = new PixelInterleavedSampleModel(DataBuffer.TYPE_FLOAT, w, h, 4, w * 4, new int[] {0, 1, 2, 3});
        DataBuffer db = new DataBufferFloat(w * h * 4);
        return RasterFactory.createWritableRaster(sm, db, null);
    }

    private static WritableRaster makeDoubleRasterRGBA(int w, int h) {
        SampleModel sm =
                new PixelInterleavedSampleModel(DataBuffer.TYPE_DOUBLE, w, h, 4, w * 4, new int[] {0, 1, 2, 3});
        DataBuffer db = new DataBufferDouble(w * h * 4);
        return RasterFactory.createWritableRaster(sm, db, null);
    }

    @Test
    public void testArgbToFloat() {
        FloatDoubleColorModel cm = TestSupport.getFloatColorModel(true, false);

        int argb = 0x80402010; // A=0x80, R=0x40, G=0x20, B=0x10
        float[] elems = (float[]) cm.getDataElements(argb, null);
        assertEquals(4, elems.length);

        int r = cm.getRed(elems);
        int g = cm.getGreen(elems);
        int b = cm.getBlue(elems);
        int a = cm.getAlpha(elems);

        // Allow ±1 for float to int rounding
        assertEquals((argb >>> 16) & 0xFF, r, DELTA_INT);
        assertEquals((argb >>> 8) & 0xFF, g, DELTA_INT);
        assertEquals(argb & 0xFF, b, DELTA_INT);
        assertEquals((argb >>> 24) & 0xFF, a, DELTA_INT);

        int rgbFromElems = cm.getRGB(elems);
        int composed = ((a & 0xFF) << 24) | (rgbFromElems & 0x00FFFFFF);
        // Now compare to original (RGB may have ±1 drift due to float rounding and back)
        assertEquals((argb >>> 24) & 0xFF, (composed >>> 24) & 0xFF);
        assertEquals((argb >>> 16) & 0xFF, (composed >>> 16) & 0xFF, DELTA_INT);
        assertEquals((argb >>> 8) & 0xFF, (composed >>> 8) & 0xFF, DELTA_INT);
        assertEquals(argb & 0xFF, composed & 0xFF, DELTA_INT);
    }

    @Test
    public void testCoerceDataToPremultipliedAlpha() {
        FloatDoubleColorModel cm = TestSupport.getFloatColorModel(true, false);
        WritableRaster wr = makeFloatRasterRGBA(1, 1);

        wr.setSample(0, 0, 0, 0.5f); // R
        wr.setSample(0, 0, 1, 0.25f); // G
        wr.setSample(0, 0, 2, 0.75f); // B
        wr.setSample(0, 0, 3, 0.5f); // A

        ColorModel premul = cm.coerceData(wr, true);
        assertTrue(premul.isAlphaPremultiplied());
        assertEquals(0.25f, wr.getSampleFloat(0, 0, 0), DELTA);
        assertEquals(0.125f, wr.getSampleFloat(0, 0, 1), DELTA);
        assertEquals(0.375f, wr.getSampleFloat(0, 0, 2), DELTA);
        assertEquals(0.5f, wr.getSampleFloat(0, 0, 3), DELTA);

        ColorModel unpremul = premul.coerceData(wr, false);
        assertFalse(unpremul.isAlphaPremultiplied());
        assertEquals(0.5f, wr.getSampleFloat(0, 0, 0), DELTA);
        assertEquals(0.25f, wr.getSampleFloat(0, 0, 1), DELTA);
        assertEquals(0.75f, wr.getSampleFloat(0, 0, 2), DELTA);
        assertEquals(0.5f, wr.getSampleFloat(0, 0, 3), DELTA);
    }

    @Test
    public void testCoerceDataWithZeroAlpha() {
        FloatDoubleColorModel cm = TestSupport.getFloatColorModel(true, false);
        WritableRaster wr = makeFloatRasterRGBA(1, 1);

        wr.setSample(0, 0, 0, 1f);
        wr.setSample(0, 0, 1, 1f);
        wr.setSample(0, 0, 2, 1f);
        wr.setSample(0, 0, 3, 0f); // A=0

        ColorModel cm2 = cm.coerceData(wr, true);

        // Alpha should remain 0
        assertEquals(0f, wr.getSampleFloat(0, 0, 3), ZERO_DELTA);
        // RGB should be unchanged (not forced to 0)
        assertEquals(1f, wr.getSampleFloat(0, 0, 0), ZERO_DELTA);
        assertEquals(1f, wr.getSampleFloat(0, 0, 1), ZERO_DELTA);
        assertEquals(1f, wr.getSampleFloat(0, 0, 2), ZERO_DELTA);
        assertTrue(cm2.isAlphaPremultiplied());
    }

    @Test
    public void testCompatibleChecks() {
        FloatDoubleColorModel cm = TestSupport.getFloatColorModel(true, false);

        SampleModel sm = cm.createCompatibleSampleModel(2, 3);
        assertTrue(cm.isCompatibleSampleModel(sm));

        WritableRaster wr = cm.createCompatibleWritableRaster(2, 3);
        assertTrue(cm.isCompatibleRaster(wr));

        WritableRaster wrong = makeDoubleRasterRGBA(2, 3);
        assertFalse(cm.isCompatibleRaster(wrong));
    }

    @Test
    public void testRgbFromDouble() {
        FloatDoubleColorModel cm = TestSupport.getDoubleColorModel(true, false);

        double[] elems = new double[] {0.25, 0.5, 0.75, 1.0};
        int rgb = cm.getRGB(elems);

        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;

        assertTrue(r > 0 && g > 0 && b > 0);
        assertNotNull(cm.toString());
        assertTrue(cm.toString().contains("FloatDoubleColorModel"));
    }

    @Test
    public void testFloatData() {
        FloatDoubleColorModel cm = TestSupport.getFloatColorModel(true, false);
        int argb = 0xFF336699; // R=0x33, G=0x66, B=0x99

        float[] elems = (float[]) cm.getDataElements(argb, null);
        assertEquals((argb >>> 16) & 0xFF, cm.getRed(elems), 1);
        assertEquals((argb >>> 8) & 0xFF, cm.getGreen(elems), 1);
        assertEquals(argb & 0xFF, cm.getBlue(elems), 1);
        assertEquals((argb >>> 24) & 0xFF, cm.getAlpha(elems));
    }
}
