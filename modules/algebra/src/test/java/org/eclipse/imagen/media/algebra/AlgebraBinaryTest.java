/* Copyright (c) 2025 Andrea Aime and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution and is available at
 * http://www.opensource.org/licenses/apache2.0.php.
 */
package org.eclipse.imagen.media.algebra;

import static org.eclipse.imagen.media.algebra.AlgebraDescriptor.Operator.AND;
import static org.eclipse.imagen.media.algebra.AlgebraDescriptor.Operator.MAX;
import static org.eclipse.imagen.media.algebra.AlgebraDescriptor.Operator.MIN;
import static org.eclipse.imagen.media.algebra.AlgebraDescriptor.Operator.MULTIPLY;
import static org.eclipse.imagen.media.algebra.AlgebraDescriptor.Operator.OR;
import static org.eclipse.imagen.media.algebra.AlgebraDescriptor.Operator.SUBTRACT;
import static org.eclipse.imagen.media.algebra.AlgebraDescriptor.Operator.SUM;
import static org.eclipse.imagen.media.algebra.AlgebraDescriptor.Operator.XOR;
import static org.junit.Assert.assertEquals;

import java.awt.image.DataBuffer;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.SampleModel;
import java.io.IOException;
import org.eclipse.imagen.PlanarImage;
import org.eclipse.imagen.RenderedOp;
import org.eclipse.imagen.TiledImage;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the binary optimized implementations of the Algebra operations, for binary images that have no ROI Nor NoData,
 * working over groups of bits (one byte at a time) rather than single pixels.
 */
public class AlgebraBinaryTest {

    public static final int SIZE = 4;
    public static final int TILE_SIZE = SIZE / 2;
    TiledImage first;
    TiledImage second;

    @Before
    public void createTestImages() {
        SampleModel sm = new MultiPixelPackedSampleModel(DataBuffer.TYPE_BYTE, TILE_SIZE, TILE_SIZE, 1);
        // 1st      2nd
        // 0000     0011
        // 0000     0011
        // 1111     0011
        // 1111     0011
        first = new TiledImage(0, 0, SIZE, SIZE, 0, 0, sm, PlanarImage.createColorModel(sm));
        second = new TiledImage(0, 0, SIZE, SIZE, 0, 0, sm, PlanarImage.createColorModel(sm));
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (j >= TILE_SIZE) first.setSample(i, j, 0, 1);
                else first.setSample(i, j, 0, 0);
                if (i >= TILE_SIZE) second.setSample(i, j, 0, 1);
                else second.setSample(i, j, 0, 0);
            }
        }
    }

    @Test
    public void testSubtractBinary() throws IOException {
        RenderedOp subtract = algebraOperation(SUBTRACT);
        assertEquals(0, getPixel(subtract, 0, 0));
        assertEquals(0, getPixel(subtract, 2, 0));
        assertEquals(1, getPixel(subtract, 0, 2));
        assertEquals(0, getPixel(subtract, 2, 2));
    }

    @Test
    public void testAndBinary() throws IOException {
        testAndLike(AND);
    }

    @Test
    public void testMinBinary() throws IOException {
        testAndLike(MIN);
    }

    @Test
    public void testMultiplyBinary() throws IOException {
        testAndLike(MULTIPLY);
    }

    private void testAndLike(AlgebraDescriptor.Operator operator) {
        RenderedOp and = algebraOperation(operator);
        assertEquals(0, getPixel(and, 0, 0));
        assertEquals(0, getPixel(and, 2, 0));
        assertEquals(0, getPixel(and, 0, 2));
        assertEquals(1, getPixel(and, 2, 2));
    }

    @Test
    public void testOrBinary() throws IOException {
        testOrLike(OR);
    }

    @Test
    public void testMaxBinary() throws IOException {
        testOrLike(MAX);
    }

    @Test
    public void testSumBinary() throws IOException {
        testOrLike(SUM);
    }

    private void testOrLike(AlgebraDescriptor.Operator operator) {
        RenderedOp or = algebraOperation(operator);
        assertEquals(0, getPixel(or, 0, 0));
        assertEquals(1, getPixel(or, 2, 0));
        assertEquals(1, getPixel(or, 0, 2));
        assertEquals(1, getPixel(or, 2, 2));
    }

    @Test
    public void testXorBinary() throws IOException {
        RenderedOp xor = algebraOperation(XOR);
        assertEquals(0, getPixel(xor, 0, 0));
        assertEquals(1, getPixel(xor, 2, 0));
        assertEquals(1, getPixel(xor, 0, 2));
        assertEquals(0, getPixel(xor, 2, 2));
    }

    private static int getPixel(RenderedOp subtract, int x, int y) {
        int[] pixel = new int[1];
        subtract.getData().getPixel(x, y, pixel);
        return pixel[0];
    }

    private RenderedOp algebraOperation(AlgebraDescriptor.Operator operator) {
        return AlgebraDescriptor.create(operator, null, null, 0, null, first, second);
    }
}
