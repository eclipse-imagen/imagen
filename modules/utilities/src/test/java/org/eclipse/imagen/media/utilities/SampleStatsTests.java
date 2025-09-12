/* Copyright (c) 2025 Daniele Romagnoli and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution and is available at
 * http://www.opensource.org/licenses/apache2.0.php.
 */
package org.eclipse.imagen.media.utilities;

import static org.junit.Assert.*;

import org.eclipse.imagen.media.numeric.SampleStats;
import org.junit.Test;

/** Unit tests for the SampleStats utility class. */
public class SampleStatsTests {

    public static final double DELTA = 1e-9;

    @Test
    public void testMinMax() {
        Double[] a = {-2d, 7d, 0d, 3d};
        assertEquals(-2.0, SampleStats.min(a, true), DELTA);
        assertEquals(7.0, SampleStats.max(a, true), DELTA);
    }

    @Test
    public void testMean() {
        Double[] a = {1d, 2d, 3d, 4d};
        assertEquals(2.5, SampleStats.mean(a, true), DELTA);
        Double[] b = {-2d, -1d, 0d, 3d};
        assertEquals(0.0, SampleStats.mean(b, true), DELTA);
    }

    @Test
    public void testSum() {
        Double[] a = {1d, 2d, 3d, 4d};
        assertEquals(10.0, SampleStats.sum(a, true), DELTA);
    }

    @Test
    public void testRange() {
        Double[] a = {-2d, 7d, 0d, 3d};
        assertEquals(9.0, SampleStats.range(a, true), DELTA);
    }

    @Test
    public void testMedian() {
        Double[] odd = {4d, 1d, 3d};
        assertEquals(3.0, SampleStats.median(odd, true), DELTA);
        Double[] even = {1d, 2d, 9d, 4d};
        assertEquals(3.0, SampleStats.median(even, true), DELTA);
    }

    @Test
    public void testMode() {
        Double[] a = {1d, 2d, 2d, 3d, 4d, 2d};
        assertEquals(2.0, SampleStats.mode(a, true), DELTA);
        Double[] b = {1d, 1d, 2d, 2d, 3d};
        double mode = SampleStats.mode(b, true);
        assertTrue(mode == 1.0 || mode == 2.0);
    }

    @Test
    public void testVarianceAndSdev() {
        Double[] a = {2d, 4d, 4d, 4d, 5d, 5d, 7d, 9d};
        assertEquals(4.5714, SampleStats.variance(a, true), 1E-4);
        assertEquals(2.1381, SampleStats.sdev(a, true), 1E-4);
    }

    @Test
    public void testIgnoringNaN() {
        Double[] a = {1d, Double.NaN, Double.NaN, 2d, 3d};
        assertEquals(2.0, SampleStats.mean(a, true), DELTA);
    }
}
