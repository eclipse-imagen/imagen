/* Copyright (c) 2025 Daniele Romagnoli and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution and is available at
 * http://www.opensource.org/licenses/apache2.0.php.
 */
package org.eclipse.imagen;

import static org.junit.Assert.*;

import java.awt.RenderingHints;
import java.awt.image.*;
import java.awt.image.renderable.ParameterBlock;
import org.junit.Ignore;
import org.junit.Test;

public class FilteredSubsampleOpImageTest {

    private static BufferedImage createByteGrayImage() {
        BufferedImage img = TestSupport.createImage(DataBuffer.TYPE_BYTE, 4, 4);
        WritableRaster r = img.getRaster();
        int v = 1;
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                r.setSample(x, y, 0, v++);
            }
        }
        return img;
    }

    private static BufferedImage createFloatGrayImage() {
        BufferedImage img = TestSupport.createImage(DataBuffer.TYPE_FLOAT, 4, 4);
        WritableRaster wr = img.getRaster();
        int v = 1;
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                wr.setSample(x, y, 0, (float) v++);
            }
        }
        return img;
    }

    private static float[] filter() {
        return new float[] {0.5F, 0.5F};
    }

    private static Raster filteredSubsample(RenderedImage src, int sx, int sy, float[] filter) {
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(src);
        pb.add(sx);
        pb.add(sy);
        pb.add(filter);
        pb.add(Interpolation.getInstance(Interpolation.INTERP_NEAREST));
        RenderingHints hints =
                new RenderingHints(JAI.KEY_BORDER_EXTENDER, BorderExtender.createInstance(BorderExtender.BORDER_ZERO));
        RenderedOp op = JAI.create("FilteredSubsample", pb, hints);
        assertNotNull(op);
        return op.getData();
    }

    @Test
    @Ignore
    /** TODO: double check the operations code and revisit the expected values */
    public void testFilteredSubsampleByte() {
        BufferedImage src = createByteGrayImage();
        Raster out = filteredSubsample(src, 2, 2, filter());

        assertEquals(4, out.getSample(0, 0, 0));
        assertEquals(6, out.getSample(1, 0, 0));
        assertEquals(12, out.getSample(0, 1, 0));
        assertEquals(14, out.getSample(1, 1, 0));
    }

    @Test
    @Ignore
    /** TODO: double check the operations code and revisit the expected values */
    public void testFilteredSubsampleFloat() {
        BufferedImage src = createFloatGrayImage();
        Raster out = filteredSubsample(src, 2, 2, filter());

        // FLOAT destination → fractional averages should be preserved
        assertEquals(3.5, out.getSampleDouble(0, 0, 0), 1e-6);
        assertEquals(5.5, out.getSampleDouble(1, 0, 0), 1e-6);
        assertEquals(11.5, out.getSampleDouble(0, 1, 0), 1e-6);
        assertEquals(13.5, out.getSampleDouble(1, 1, 0), 1e-6);
    }
}
