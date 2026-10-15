/* Copyright (c) 2026 Jody Garnett and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution and is available at
 * http://www.opensource.org/licenses/apache2.0.php.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.eclipse.imagen;

import static org.junit.Assert.*;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import org.junit.Test;

/** Unit tests for the Histogram class, expected values verified against JAI 1.1.3. */
public class HistogramTest {

    private static final double DELTA = 1E-9;

    private static final int[] DATA_TYPES = {
        DataBuffer.TYPE_BYTE,
        DataBuffer.TYPE_USHORT,
        DataBuffer.TYPE_SHORT,
        DataBuffer.TYPE_INT,
        DataBuffer.TYPE_FLOAT,
        DataBuffer.TYPE_DOUBLE
    };

    /** Two bands of four unit width bins: {1, 2, 3, 4} and {4, 3, 2, 1}. */
    private static Histogram tiny() {
        Histogram h = new Histogram(new int[] {4}, new double[] {0}, new double[] {4}, 2);
        int[][] bins = h.getBins();
        bins[0] = new int[] {1, 2, 3, 4};
        bins[1] = new int[] {4, 3, 2, 1};
        return h;
    }

    /** Two gaussian peaks centred on 50 and 200. */
    private static Histogram bimodal() {
        Histogram h = new Histogram(256, 0, 256, 1);
        int[] bins = h.getBins(0);
        for (int i = 0; i < 256; i++) {
            double a = (i - 50) / 10.0;
            double c = (i - 200) / 15.0;
            bins[i] = (int) Math.round(1000 * Math.exp(-a * a / 2) + 600 * Math.exp(-c * c / 2));
        }
        return h;
    }

    private static Histogram spike() {
        Histogram h = new Histogram(21, 0, 21, 1);
        h.getBins(0)[10] = 900;
        return h;
    }

    private static Histogram flat() {
        Histogram h = new Histogram(21, 0, 21, 1);
        Arrays.fill(h.getBins(0), 10);
        return h;
    }

    /** 4x4 two band raster at (10, 20), band 0 holds 0..15 in scanline order and band 1 holds 15..0. */
    private static Raster raster(int dataType) {
        WritableRaster raster = RasterFactory.createBandedRaster(dataType, 4, 4, 2, new Point(10, 20));
        double fraction = dataType == DataBuffer.TYPE_FLOAT || dataType == DataBuffer.TYPE_DOUBLE ? 0.25 : 0;
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                int v = x + 4 * y;
                raster.setSample(10 + x, 20 + y, 0, v + fraction);
                raster.setSample(10 + x, 20 + y, 1, 15 - v);
            }
        }
        return raster;
    }

    private static int[] bins(int... setBins) {
        int[] bins = new int[16];
        for (int bin : setBins) {
            bins[bin]++;
        }
        return bins;
    }

    @Test
    public void testConstructor() {
        Histogram h = new Histogram(new int[] {4, 8}, new double[] {0, 10}, new double[] {4, 50});
        assertEquals(2, h.getNumBands());
        assertArrayEquals(new int[] {4, 8}, h.getNumBins());
        assertEquals(8, h.getNumBins(1));
        assertArrayEquals(new double[] {0, 10}, h.getLowValue(), DELTA);
        assertEquals(10, h.getLowValue(1), DELTA);
        assertArrayEquals(new double[] {4, 50}, h.getHighValue(), DELTA);
        assertEquals(50, h.getHighValue(1), DELTA);
        assertEquals(15, h.getBinLowValue(1, 1), DELTA);
        assertEquals(0, h.getBinSize(1, 7));
        assertEquals(8, h.getBins(1).length);
    }

    @Test
    public void testConstructorFill() {
        Histogram h = new Histogram(new int[] {4, 8}, new double[] {0}, new double[] {16}, 3);
        assertEquals(3, h.getNumBands());
        assertArrayEquals(new int[] {4, 8, 4}, h.getNumBins());
        assertArrayEquals(new double[] {0, 0, 0}, h.getLowValue(), DELTA);
        assertArrayEquals(new double[] {16, 16, 16}, h.getHighValue(), DELTA);
        assertEquals(4, h.getBinLowValue(2, 1), DELTA);
        assertEquals(2, h.getBinLowValue(1, 1), DELTA);
    }

    @Test
    public void testConstructorSameForAllBands() {
        Histogram h = new Histogram(10, -5, 5, 3);
        assertEquals(3, h.getNumBands());
        assertArrayEquals(new int[] {10, 10, 10}, h.getNumBins());
        assertEquals(-4, h.getBinLowValue(2, 1), DELTA);
    }

    @Test
    public void testConstructorInvalid() {
        double[] low = {0};
        double[] high = {1};
        assertThrows(IllegalArgumentException.class, () -> new Histogram(null, low, high));
        assertThrows(IllegalArgumentException.class, () -> new Histogram(new int[] {1}, null, high));
        assertThrows(IllegalArgumentException.class, () -> new Histogram(new int[] {1}, low, null));
        assertThrows(IllegalArgumentException.class, () -> new Histogram(new int[] {1, 1}, low, high));
        assertThrows(IllegalArgumentException.class, () -> new Histogram(new int[0], new double[0], new double[0]));
        assertThrows(IllegalArgumentException.class, () -> new Histogram(new int[] {0}, low, high));
        assertThrows(IllegalArgumentException.class, () -> new Histogram(new int[] {1}, high, low));
        assertThrows(IllegalArgumentException.class, () -> new Histogram(new int[0], low, high, 1));
        assertThrows(IllegalArgumentException.class, () -> new Histogram(new int[] {1}, new double[0], high, 1));
        assertThrows(IllegalArgumentException.class, () -> new Histogram(new int[] {1}, low, high, 0));
        assertThrows(IllegalArgumentException.class, () -> new Histogram(1, 0, 1, 0));
        assertThrows(IllegalArgumentException.class, () -> new Histogram(0, 0, 1, 1));
        assertThrows(IllegalArgumentException.class, () -> new Histogram(1, 1, 1, 1));
    }

    @Test
    public void testStatistics() {
        Histogram h = tiny();
        assertArrayEquals(new int[] {10, 10}, h.getTotals());
        assertArrayEquals(new double[] {2.0, 1.0}, h.getMean(), DELTA);
        assertArrayEquals(new double[] {1.0, 1.0}, h.getStandardDeviation(), DELTA);
        assertArrayEquals(new double[] {1.8464393446710154, 1.8464393446710154}, h.getEntropy(), DELTA);
    }

    @Test
    public void testSubTotal() {
        Histogram h = tiny();
        assertEquals(5, h.getSubTotal(0, 1, 2));
        assertEquals(10, h.getSubTotal(1, 0, 3));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> h.getSubTotal(0, -1, 2));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> h.getSubTotal(0, 0, 4));
        assertThrows(IllegalArgumentException.class, () -> h.getSubTotal(0, 2, 1));
    }

    @Test
    public void testMoment() {
        Histogram h = tiny();
        assertArrayEquals(new double[] {2.0, 1.0}, h.getMoment(1, false, false), DELTA);
        assertArrayEquals(new double[] {2.0, 1.0}, h.getMoment(1, true, false), DELTA);
        assertArrayEquals(new double[] {0.0, 0.0}, h.getMoment(1, false, true), DELTA);
        assertArrayEquals(new double[] {0.0, 0.0}, h.getMoment(1, true, true), DELTA);
        assertArrayEquals(new double[] {5.0, 2.0}, h.getMoment(2, false, false), DELTA);
        assertArrayEquals(new double[] {5.0, 2.0}, h.getMoment(2, true, false), DELTA);
        assertArrayEquals(new double[] {1.0, 1.0}, h.getMoment(2, false, true), DELTA);
        assertArrayEquals(new double[] {1.0, 1.0}, h.getMoment(2, true, true), DELTA);
        assertArrayEquals(new double[] {13.4, 4.6}, h.getMoment(3, false, false), DELTA);
        assertArrayEquals(new double[] {13.4, 4.6}, h.getMoment(3, true, false), DELTA);
        assertArrayEquals(new double[] {1.4, 1.4}, h.getMoment(3, false, true), DELTA);
        assertArrayEquals(new double[] {1.4, 1.4}, h.getMoment(3, true, true), DELTA);
        assertThrows(IllegalArgumentException.class, () -> h.getMoment(0, false, false));
    }

    @Test
    public void testThresholds() {
        Histogram h = bimodal();
        assertArrayEquals(new int[] {47621}, h.getTotals());
        assertArrayEquals(new double[] {121.05676067281242}, h.getMean(), DELTA);
        assertArrayEquals(new double[] {49.0}, h.getPTileThreshold(0.25), DELTA);
        assertArrayEquals(new double[] {66.0}, h.getPTileThreshold(0.5), DELTA);
        assertArrayEquals(new double[] {212.0}, h.getPTileThreshold(0.9), DELTA);
        assertArrayEquals(new double[] {126.0}, h.getModeThreshold(1), DELTA);
        assertArrayEquals(new double[] {127.0}, h.getModeThreshold(2), DELTA);
        assertArrayEquals(new double[] {124.99875881023095}, h.getIterativeThreshold(), DELTA);
        assertArrayEquals(new double[] {116.0}, h.getMaxVarianceThreshold(), DELTA);
        assertArrayEquals(new double[] {73.5}, h.getMaxEntropyThreshold(), DELTA);
        assertArrayEquals(new double[] {116.0}, h.getMinErrorThreshold(), DELTA);
        assertArrayEquals(new double[] {87.5}, h.getMinFuzzinessThreshold(), DELTA);
    }

    @Test
    public void testThresholdsTiny() {
        Histogram h = tiny();
        assertArrayEquals(new double[] {2.1666666666666665, 1.380952380952381}, h.getIterativeThreshold(), DELTA);
        assertArrayEquals(new double[] {2.0, 1.0}, h.getMinErrorThreshold(), DELTA);
        assertArrayEquals(new double[] {2.0}, new Histogram(4, 0, 4, 1).getIterativeThreshold(), DELTA);
    }

    @Test
    public void testPTileThresholdInvalid() {
        Histogram h = tiny();
        assertThrows(IllegalArgumentException.class, () -> h.getPTileThreshold(0.0));
        assertThrows(IllegalArgumentException.class, () -> h.getPTileThreshold(1.0));
    }

    @Test
    public void testSmoothed() {
        assertArrayEquals(
                new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 225, 225, 225, 225, 0, 0, 0, 0, 0, 0, 0, 0},
                spike().getSmoothed(false, 2).getBins(0));
        assertArrayEquals(
                new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 225, 338, 225, 113, 0, 0, 0, 0, 0, 0, 0, 0},
                spike().getSmoothed(true, 2).getBins(0));
        assertArrayEquals(
                new int[] {9, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10},
                flat().getSmoothed(false, 2).getBins(0));
        int[] ten = new int[21];
        Arrays.fill(ten, 10);
        assertArrayEquals(ten, flat().getSmoothed(true, 2).getBins(0));

        Histogram h = spike();
        assertSame(h, h.getSmoothed(false, 0));
        assertThrows(IllegalArgumentException.class, () -> h.getSmoothed(false, -1));
    }

    @Test
    public void testGaussianSmoothed() {
        assertArrayEquals(
                new int[] {0, 0, 0, 0, 0, 0, 0, 33, 99, 194, 242, 194, 99, 33, 7, 0, 0, 0, 0, 0, 0},
                spike().getGaussianSmoothed(1.5).getBins(0));
        int[] ten = new int[21];
        Arrays.fill(ten, 10);
        assertArrayEquals(ten, flat().getGaussianSmoothed(1.5).getBins(0));

        Histogram h = spike();
        assertSame(h, h.getGaussianSmoothed(0.0));
        assertThrows(IllegalArgumentException.class, () -> h.getGaussianSmoothed(-1.0));
    }

    @Test
    public void testCountPixels() {
        for (int dataType : DATA_TYPES) {
            Histogram h = new Histogram(16, 0, 16, 2);
            h.countPixels(raster(dataType), null, 10, 20, 1, 1);
            int[] ones = new int[16];
            Arrays.fill(ones, 1);
            assertArrayEquals("type " + dataType, ones, h.getBins(0));
            assertArrayEquals("type " + dataType, ones, h.getBins(1));
            assertArrayEquals(new int[] {16, 16}, h.getTotals());
        }
    }

    @Test
    public void testCountPixelsPeriod() {
        for (int dataType : DATA_TYPES) {
            Histogram h = new Histogram(16, 0, 16, 2);
            h.countPixels(raster(dataType), null, 10, 20, 2, 2);
            assertArrayEquals("type " + dataType, bins(0, 2, 8, 10), h.getBins(0));
            assertArrayEquals("type " + dataType, bins(15, 13, 7, 5), h.getBins(1));

            h = new Histogram(16, 0, 16, 2);
            h.countPixels(raster(dataType), null, 9, 19, 2, 2);
            assertArrayEquals("type " + dataType, bins(5, 7, 13, 15), h.getBins(0));
            assertArrayEquals("type " + dataType, bins(10, 8, 2, 0), h.getBins(1));
        }
    }

    @Test
    public void testCountPixelsROI() {
        for (int dataType : DATA_TYPES) {
            Histogram h = new Histogram(16, 0, 16, 2);
            h.countPixels(raster(dataType), new ROIShape(new Rectangle(10, 20, 2, 2)), 10, 20, 1, 1);
            assertArrayEquals("type " + dataType, bins(0, 1, 4, 5), h.getBins(0));
            assertArrayEquals("type " + dataType, bins(15, 14, 11, 10), h.getBins(1));

            h.countPixels(raster(dataType), new ROIShape(new Rectangle(0, 0, 2, 2)), 10, 20, 1, 1);
            assertArrayEquals("type " + dataType, bins(0, 1, 4, 5), h.getBins(0));
        }
    }

    @Test
    public void testCountPixelsRange() {
        Histogram h = new Histogram(4, 4, 12, 2);
        h.countPixels(raster(DataBuffer.TYPE_BYTE), null, 10, 20, 1, 1);
        assertArrayEquals(new int[] {2, 2, 2, 2}, h.getBins(0));
        assertArrayEquals(new int[] {8, 8}, h.getTotals());
    }

    @Test
    public void testCountPixelsBinary() {
        WritableRaster bit = Raster.createPackedRaster(DataBuffer.TYPE_BYTE, 8, 1, 1, 1, null);
        for (int x = 0; x < 8; x++) {
            bit.setSample(x, 0, 0, x % 3 == 0 ? 1 : 0);
        }
        Histogram h = new Histogram(2, 0, 2, 1);
        h.countPixels(bit, null, 0, 0, 1, 1);
        assertArrayEquals(new int[] {5, 3}, h.getBins(0));
    }

    @Test
    public void testCountPixelsAccumulateAndClear() {
        Histogram h = new Histogram(16, 0, 16, 2);
        Raster raster = raster(DataBuffer.TYPE_INT);
        h.countPixels(raster, null, 10, 20, 1, 1);
        h.countPixels(raster, null, 10, 20, 1, 1);
        assertEquals(2, h.getBinSize(0, 7));

        h.clearHistogram();
        assertArrayEquals(new int[16], h.getBins(0));
        assertArrayEquals(new int[16], h.getBins(1));

        new Histogram(4, 0, 4, 1).clearHistogram();
    }

    @Test
    public void testCountPixelsInvalid() {
        Histogram h = new Histogram(16, 0, 16, 1);
        assertThrows(IllegalArgumentException.class, () -> h.countPixels(null, null, 0, 0, 1, 1));
        assertThrows(
                IllegalArgumentException.class, () -> h.countPixels(raster(DataBuffer.TYPE_BYTE), null, 0, 0, 1, 1));
    }

    @Test
    public void testSerialization() throws Exception {
        Histogram h = tiny();
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
            out.writeObject(h);
        }
        Histogram copy;
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            copy = (Histogram) in.readObject();
        }
        assertArrayEquals(h.getNumBins(), copy.getNumBins());
        assertArrayEquals(h.getLowValue(), copy.getLowValue(), DELTA);
        assertArrayEquals(h.getHighValue(), copy.getHighValue(), DELTA);
        assertArrayEquals(h.getBins(0), copy.getBins(0));
        assertArrayEquals(h.getBins(1), copy.getBins(1));
        assertArrayEquals(h.getMean(), copy.getMean(), DELTA);
    }
}
