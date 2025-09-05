/* Copyright (c) 2025 Daniele Romagnoli and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution and is available at
 * http://www.opensource.org/licenses/apache2.0.php.
 */
package org.eclipse.imagen.media.testclasses;

import static org.junit.Assert.assertTrue;

import java.awt.Rectangle;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.util.Objects;

/**
 * Utility class for comparing two RenderedImages.
 *
 * <p>The comparison is done in strips to limit memory consumption.
 *
 * <p>The images must have the same bounds, number of bands and data type.
 *
 * <p>The comparison can be exact (no diffs allowed) or with tolerance, both in terms of number of pixels and numeric
 * tolerance per sample.
 *
 * <p>Supported data types are: BYTE, USHORT, SHORT, INT, FLOAT, DOUBLE.
 *
 * <p>NaN values are considered equal. For floating point types, infinities are considered equal only if they have the
 * same sign.
 */
public final class ImageComparator {

    private static final double DELTA = 1E-6;

    private static final boolean DEBUG = Boolean.getBoolean("imagen.test.debug");

    private ImageComparator() {}

    /** Exact image comparison, no diffs allowed. */
    public static void assertEquals(RenderedImage a, RenderedImage b) {
        // exact, no diffs allowed
        assertEquals(a, b, false, 0, 0.0);
    }

    /**
     * Image comparison with tolerance.
     *
     * @param strictBounds means that minX and minY should match too. Saved images on disk always start from 0,0 so this
     *     is false by default
     * @param maxDifferentPixels how many pixels may differ (default 0)
     * @param delta numeric tolerance per sample (0.0 = exact). Applied to FLOAT/DOUBLE; integers are exact compare.
     */
    public static void assertEquals(
            RenderedImage a, RenderedImage b, boolean strictBounds, int maxDifferentPixels, double delta) {
        Objects.requireNonNull(a, "left image is null");
        Objects.requireNonNull(b, "right image is null");

        // 1) bounds
        Rectangle ra = new Rectangle(a.getMinX(), a.getMinY(), a.getWidth(), a.getHeight());
        Rectangle rb = new Rectangle(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight());
        if (strictBounds) {
            org.junit.Assert.assertEquals("Image bounds should be equal", ra, rb);
        } else {
            org.junit.Assert.assertEquals("Image width should be equal", ra.getWidth(), rb.getWidth(), DELTA);
            org.junit.Assert.assertEquals("Image height should be equal", ra.getHeight(), rb.getHeight(), DELTA);
        }

        // 2) bands + datatype
        SampleModel sma = a.getSampleModel();
        SampleModel smb = b.getSampleModel();
        org.junit.Assert.assertEquals(sma.getNumBands(), smb.getNumBands());

        final int dataType = sma.getDataType();
        final int bands = sma.getNumBands();
        final int width = ra.width;
        final int height = ra.height;
        final int minX = ra.x;
        final int minY = ra.y;

        final int stripHeight = Math.max(1, Math.min(512, height));

        int differingPixels = 0;

        for (int y = 0; y < height; y += stripHeight) {
            int h = Math.min(stripHeight, height - y);
            Rectangle stripA = new Rectangle(minX, minY + y, width, h);
            Rectangle stripB = new Rectangle(b.getMinX(), b.getMinY() + y, width, h);
            if (strictBounds) {
                stripB = stripA;
            }
            Raster rA = a.getData(stripA);
            Raster rB = b.getData(stripB);

            switch (dataType) {
                case DataBuffer.TYPE_BYTE:
                case DataBuffer.TYPE_USHORT:
                case DataBuffer.TYPE_SHORT:
                case DataBuffer.TYPE_INT: {
                    int[] pA = rA.getPixels(stripA.x, stripA.y, stripA.width, stripA.height, (int[]) null);
                    int[] pB = rB.getPixels(stripB.x, stripB.y, stripB.width, stripB.height, (int[]) null);
                    differingPixels += countDifferentPixelsInt(pA, pB, bands);
                    break;
                }
                case DataBuffer.TYPE_FLOAT: {
                    float[] pA = rA.getPixels(stripA.x, stripA.y, stripA.width, stripA.height, (float[]) null);
                    float[] pB = rB.getPixels(stripB.x, stripB.y, stripB.width, stripB.height, (float[]) null);
                    differingPixels += countDifferentPixelsFloat(pA, pB, bands, (float) delta);
                    break;
                }
                case DataBuffer.TYPE_DOUBLE: {
                    double[] pA = rA.getPixels(stripA.x, stripA.y, stripA.width, stripA.height, (double[]) null);
                    double[] pB = rB.getPixels(stripB.x, stripB.y, stripB.width, stripB.height, (double[]) null);
                    differingPixels += countDifferentPixelsDouble(pA, pB, bands, delta);
                    break;
                }
                default:
                    throw new IllegalArgumentException("Unsupported DataBuffer type: " + dataType);
            }
            assertTrue(
                    "Too many differing pixels: " + differingPixels + " > " + maxDifferentPixels,
                    differingPixels <= maxDifferentPixels);
        }
        assertTrue(
                "Too many differing pixels: " + differingPixels + " > " + maxDifferentPixels,
                differingPixels <= maxDifferentPixels);
    }

    private static int countDifferentPixelsInt(int[] a, int[] b, int bands) {
        if (a == b) return 0;
        if (a == null || b == null || a.length != b.length) return Integer.MAX_VALUE;

        int diffs = 0;
        for (int i = 0; i < a.length; ) {
            boolean pixelDiff = false;
            for (int bnd = 0; bnd < bands; bnd++, i++) {
                if (a[i] != b[i]) pixelDiff = true;
            }
            if (pixelDiff) diffs++;
        }
        return diffs;
    }

    private static int countDifferentPixelsFloat(float[] a, float[] b, int bands, float eps) {
        if (a == b) return 0;
        if (a == null || b == null || a.length != b.length) return Integer.MAX_VALUE;

        final boolean exact = (eps == 0f);
        int diffs = 0;

        for (int i = 0; i < a.length; ) {
            boolean pixelDiff = false;
            for (int bnd = 0; bnd < bands; bnd++, i++) {
                float x = a[i], y = b[i];
                boolean same;
                if (exact) {
                    same = Float.floatToIntBits(x) == Float.floatToIntBits(y);
                } else {
                    if (Float.isNaN(x) && Float.isNaN(y)) {
                        same = true;
                    } else if (Float.isInfinite(x) || Float.isInfinite(y)) {
                        same = (x == y);
                    } else {
                        same = Math.abs(x - y) <= eps;
                    }
                }
                if (!same) pixelDiff = true;
            }
            if (pixelDiff) diffs++;
        }
        return diffs;
    }

    private static int countDifferentPixelsDouble(double[] a, double[] b, int bands, double eps) {
        if (a == b) return 0;
        if (a == null || b == null || a.length != b.length) return Integer.MAX_VALUE;

        final boolean exact = (eps == 0.0);
        int diffs = 0;

        for (int i = 0; i < a.length; ) {
            boolean pixelDiff = false;
            for (int bnd = 0; bnd < bands; bnd++, i++) {
                double x = a[i], y = b[i];
                boolean same;
                if (exact) {
                    // exact bit identity
                    same = Double.doubleToLongBits(x) == Double.doubleToLongBits(y);
                } else {
                    if (Double.isNaN(x) && Double.isNaN(y)) {
                        same = true;
                    } else if (Double.isInfinite(x) || Double.isInfinite(y)) {
                        same = (x == y);
                    } else {
                        same = Math.abs(x - y) <= eps;
                    }
                }
                if (!same) pixelDiff = true;
            }
            if (pixelDiff) diffs++;
        }
        return diffs;
    }
}
