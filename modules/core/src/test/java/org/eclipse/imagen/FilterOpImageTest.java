/* Copyright (c) 2025 Daniele Romagnoli and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution and is available at
 * http://www.opensource.org/licenses/apache2.0.php.
 */
package org.eclipse.imagen;

import static org.eclipse.imagen.TestSupport.DATA_TYPES;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.ParameterBlock;
import org.eclipse.imagen.operator.MaxFilterDescriptor;
import org.eclipse.imagen.operator.MedianFilterDescriptor;
import org.eclipse.imagen.operator.MinFilterDescriptor;
import org.junit.Test;

/**
 * Test cases for FilterOpImage implementations.
 *
 * <p>The tests create a 3x3 image with known pixel values and apply various filter operations (Max, Min, Median) with
 * different mask shapes and sizes. The output is then verified against expected results.
 */
public class FilterOpImageTest {

    public static final double DELTA = 0.0001;

    private final int[][] PIXELS = {
        {10, 20, 30},
        {40, 50, 60},
        {70, 80, 90}
    };

    private BufferedImage createImage(int dataType) {
        int w = 3, h = 3;
        BufferedImage img = TestSupport.createImage(dataType, w, h);
        WritableRaster raster = img.getRaster();
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                raster.setSample(x, y, 0, PIXELS[y][x]);
            }
        }
        return img;
    }

    private Raster applyFilter(RenderedImage testImage, String opName, EnumeratedParameter shape, int maskSize) {
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(testImage);
        pb.add(shape);
        pb.add(maskSize);
        RenderedOp op = JAI.create(opName, pb);
        assertNotNull("RenderedOp should not be null", op);
        return op.getData();
    }

    @Test
    public void testMaxFilterSeparableOpImage() {
        for (int type : DATA_TYPES) {
            BufferedImage img = createImage(type);
            Raster out = applyFilter(img, "MaxFilter", MaxFilterDescriptor.MAX_MASK_SQUARE_SEPARABLE, 3);
            assertEquals("Failed for type " + type, 90, out.getSample(1, 1, 0), DELTA);
        }
    }

    @Test
    public void testMinFilterSeparableOpImage() {
        for (int type : DATA_TYPES) {
            BufferedImage img = createImage(type);
            Raster out = applyFilter(img, "MinFilter", MinFilterDescriptor.MIN_MASK_SQUARE_SEPARABLE, 3);
            assertEquals("Failed for type " + type, 10, out.getSample(1, 1, 0), DELTA);
        }
    }

    @Test
    public void testMedianFilterSeparableOpImage() {
        for (int type : DATA_TYPES) {
            BufferedImage img = createImage(type);
            Raster out = applyFilter(img, "MedianFilter", MedianFilterDescriptor.MEDIAN_MASK_SQUARE_SEPARABLE, 3);
            assertEquals("Failed for type " + type, 50, out.getSample(1, 1, 0), DELTA);
        }
    }

    @Test
    public void testMaxFilterSquareOpImage() {
        for (int type : DATA_TYPES) {
            BufferedImage img = createImage(type);
            Raster out = applyFilter(img, "MaxFilter", MaxFilterDescriptor.MAX_MASK_SQUARE, 3);
            assertEquals("Failed for type " + type, 90, out.getSample(1, 1, 0), DELTA);
        }
    }

    @Test
    public void testMinFilterSquareOpImage() {
        for (int type : DATA_TYPES) {
            BufferedImage img = createImage(type);
            Raster out = applyFilter(img, "MinFilter", MinFilterDescriptor.MIN_MASK_SQUARE, 3);
            assertEquals("Failed for type " + type, 10, out.getSample(1, 1, 0), DELTA);
        }
    }

    @Test
    public void testMedianFilterSquareOpImage() {
        for (int type : DATA_TYPES) {
            BufferedImage img = createImage(type);
            Raster out = applyFilter(img, "MedianFilter", MedianFilterDescriptor.MEDIAN_MASK_SQUARE, 3);
            assertEquals("Failed for type " + type, 50, out.getSample(1, 1, 0), DELTA);
        }
    }

    @Test
    public void testMaxFilterXOpImage() {
        for (int type : DATA_TYPES) {
            BufferedImage img = createImage(type);
            Raster out = applyFilter(img, "MaxFilter", MaxFilterDescriptor.MAX_MASK_X, 3);
            assertEquals("Failed for type " + type, 90, out.getSample(1, 1, 0), DELTA);
        }
    }

    @Test
    public void testMinFilterXOpImage() {
        for (int type : DATA_TYPES) {
            BufferedImage img = createImage(type);
            Raster out = applyFilter(img, "MinFilter", MinFilterDescriptor.MIN_MASK_X, 3);
            assertEquals("Failed for type " + type, 10, out.getSample(1, 1, 0), DELTA);
        }
    }

    @Test
    public void testMedianFilterXOpImage() {
        for (int type : DATA_TYPES) {
            BufferedImage img = createImage(type);
            Raster out = applyFilter(img, "MedianFilter", MedianFilterDescriptor.MEDIAN_MASK_X, 3);
            assertEquals("Failed for type " + type, 50, out.getSample(1, 1, 0), DELTA);
        }
    }

    @Test
    public void testMaxFilterPlusOpImage() {
        for (int type : DATA_TYPES) {
            BufferedImage img = createImage(type);
            Raster out = applyFilter(img, "MaxFilter", MaxFilterDescriptor.MAX_MASK_PLUS, 3);
            assertEquals("Failed for type " + type, 80, out.getSample(1, 1, 0), DELTA);
        }
    }

    @Test
    public void testMinFilterPlusOpImage() {
        for (int type : DATA_TYPES) {
            BufferedImage img = createImage(type);
            Raster out = applyFilter(img, "MinFilter", MinFilterDescriptor.MIN_MASK_PLUS, 3);
            assertEquals("Failed for type " + type, 20, out.getSample(1, 1, 0), DELTA);
        }
    }

    @Test
    public void testMedianFilterPlusOpImage() {
        for (int type : DATA_TYPES) {
            BufferedImage img = createImage(type);
            Raster out = applyFilter(img, "MedianFilter", MedianFilterDescriptor.MEDIAN_MASK_PLUS, 3);
            assertEquals("Failed for type " + type, 50, out.getSample(1, 1, 0), DELTA);
        }
    }
}
