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
import java.awt.image.renderable.ParameterBlock;
import org.junit.Ignore;
import org.junit.Test;

public class SubsampleAverageOpImageTest {

    // Create a 4x4 test image with values 1..16
    private static BufferedImage createImage(int dataType) {
        int w = 4, h = 4;
        BufferedImage img = TestSupport.createImage(dataType, w, h);
        WritableRaster raster = img.getRaster();
        int value = 1;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                raster.setSample(x, y, 0, value++);
            }
        }
        return img;
    }

    private RenderedImage applySubsampleAverage(BufferedImage img, double scaleX, double scaleY) {
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(img);
        pb.add(scaleX);
        pb.add(scaleY);
        RenderedOp op = ImageN.create("SubsampleAverage", pb);
        assertNotNull("RenderedOp should not be null", op);
        return op;
    }

    @Test
    @Ignore
    /** TODO: double check the operations code and revisit the expected values */
    public void testSubsampleAverage_AllDataTypes() {
        for (int type : TestSupport.DATA_TYPES) {
            BufferedImage img = createImage(type);
            RenderedImage result = applySubsampleAverage(img, 0.5, 0.5); // 4x4 -> 2x2
            Raster out = result.getData();
            // Expected block averages:
            double[][] expected = {
                {3.5, 5.5},
                {11.5, 13.5}
            };

            for (int y = 0; y < 2; y++) {
                for (int x = 0; x < 2; x++) {
                    double actual = out.getSampleDouble(x, y, 0);
                    double exp = expected[y][x];

                    if (type == DataBuffer.TYPE_FLOAT || type == DataBuffer.TYPE_DOUBLE) {
                        assertEquals("type " + type + " at (" + x + "," + y + ")", exp, actual, 0.0001);
                    } else {
                        // integer types get rounded
                        long rounded = Math.round(exp);
                        assertEquals("type " + type + " at (" + x + "," + y + ")", rounded, (long) actual);
                    }
                }
            }
        }
    }
}
