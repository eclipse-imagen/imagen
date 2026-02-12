/* Copyright (c) 2025 Daniele Romagnoli and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution and is available at
 * http://www.opensource.org/licenses/apache2.0.php.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.eclipse.imagen.util;

import static org.junit.Assert.*;

import org.junit.Test;

/** Unit tests for the Range class. */
public class RangeTest {

    private static Range intRange(Integer min, boolean minInc, Integer max, boolean maxInc) {
        return new Range(Integer.class, min, minInc, max, maxInc);
    }

    @Test
    public void testBase() {
        Range r = intRange(10, true, 20, false); // [10,20)
        assertEquals(Integer.class, r.getElementClass());
        assertEquals(10, r.getMinValue());
        assertEquals(20, r.getMaxValue());
        assertTrue(r.isMinIncluded());
        assertFalse(r.isMaxIncluded());
        assertFalse(r.isEmpty());
        assertTrue(r.contains(10)); // min included
        assertTrue(r.contains(15));
        assertFalse(r.contains(20)); // max excluded
        assertFalse(r.contains(9));
        assertFalse(r.contains(21));

        r = intRange(5, false, 5, false); // (5,5) is empty since min=max excluded
        assertTrue(r.isEmpty());
        assertFalse(r.contains(5));
    }

    @Test
    public void testRangesContainment() {
        Range outer = intRange(10, true, 20, true); // [10,20]
        Range inner = intRange(12, true, 18, false); // [12,18)
        Range touchingLeft = intRange(10, true, 12, true); // [10,12]
        Range touchingRight = intRange(18, true, 20, true); // [18,20]
        Range outside = intRange(5, true, 9, true); // [5,9]

        assertTrue(outer.contains(inner));
        assertTrue(outer.contains(touchingLeft));
        assertTrue(outer.contains(touchingRight));
        assertFalse(outer.contains(outside));
    }

    @Test
    public void testRangesIntersection() {
        Range a = intRange(10, true, 20, false); // [10,20)
        Range b = intRange(20, true, 30, true); // [20,30] -> only touch at 20 (excluded by a)
        Range c = intRange(19, true, 25, true); // [19,25] -> overlap
        Range d = intRange(0, true, 5, true); // [0,5]   -> disjoint

        assertFalse(a.intersects(b)); // 20 not included in a
        assertTrue(a.intersects(c));
        assertFalse(a.intersects(d));

        a = intRange(10, true, 20, true); // [10,20]
        b = intRange(15, false, 25, true); // (15,25]
        Range i = a.intersect(b); // (15,20]

        assertNotNull(i);
        assertEquals(15, i.getMinValue());
        assertFalse(i.isMinIncluded());
        assertEquals(20, i.getMaxValue());
        assertTrue(i.isMaxIncluded());
    }

    @Test
    public void testRangesUnion() {
        Range a = intRange(10, true, 20, false); // [10,20)
        Range b = intRange(20, true, 30, true); // [20,30]
        Range u = a.union(b); // should be [10,30] with minInc=true, maxInc=true from b

        assertEquals(10, u.getMinValue());
        assertTrue(u.isMinIncluded());
        assertEquals(30, u.getMaxValue());
        assertTrue(u.isMaxIncluded());
        assertTrue(u.contains(10));
        assertTrue(u.contains(30));
    }

    @Test
    public void testRangesSubtraction() {
        Range a = intRange(10, true, 20, true); // [10,20]
        Range middle = intRange(13, true, 17, false); // [13,17)
        Range[] res = a.subtract(middle);

        // Expect two remainders: [10,13) and [17,20]
        assertEquals(2, res.length);

        Range left = res[0];
        assertEquals(10, left.getMinValue());
        assertTrue(left.isMinIncluded());
        assertEquals(13, left.getMaxValue());
        assertFalse(left.isMaxIncluded());

        Range right = res[1];
        assertEquals(17, right.getMinValue());
        assertTrue(right.isMinIncluded());
        assertEquals(20, right.getMaxValue());
        assertTrue(right.isMaxIncluded());

        left = intRange(5, true, 12, false); // [5,12)
        res = a.subtract(left);

        assertEquals(1, res.length);
        Range tail = res[0];
        assertEquals(12, tail.getMinValue());
        assertTrue(tail.isMinIncluded()); // 12 is not included in subtrahend → remains included
        assertEquals(20, tail.getMaxValue());
        assertTrue(tail.isMaxIncluded());
    }

    @Test
    public void testRangesEquality() {
        Range r1 = intRange(1, true, 5, false);
        Range r2 = intRange(1, true, 5, false);
        Range r3 = intRange(1, false, 5, false);

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertNotEquals(r1, r3);
        assertNotEquals(r2, r3);
    }
}
