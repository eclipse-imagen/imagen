/* Copyright (c) 2025 Andrea Aime and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution and is available at
 * http://www.opensource.org/licenses/apache2.0.php.
 */
package org.eclipse.imagen.media.convolve;

import static org.junit.Assert.assertEquals;

import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import org.eclipse.imagen.KernelImageN;
import org.eclipse.imagen.RenderedOp;
import org.eclipse.imagen.media.kernel.KernelFactory;
import org.eclipse.imagen.media.testclasses.TestBase;
import org.junit.Test;

public class ConvolveTest extends TestBase {

    public static final int IMAGE_SIZE = 10;

    @Test
    public void testIdentityFloat() {
        RenderedImage source = createTestImage(DataBuffer.TYPE_FLOAT, IMAGE_SIZE, IMAGE_SIZE, null, false, 1);
        KernelImageN kernel = KernelFactory.createRectangle(1, 1);
        RenderedOp convolve = ConvolveDescriptor.create(source, kernel, null, null, 0, false, null);

        float[] sourcePixel = new float[1];
        float[] resultPixel = new float[1];
        Raster sourceData = source.getData();
        Raster resultData = convolve.getData();
        for (int i = 0; i < IMAGE_SIZE; i++) {
            for (int j = 0; j < IMAGE_SIZE; j++) {
                sourceData.getPixel(i, j, sourcePixel);
                resultData.getPixel(i, j, resultPixel);
                assertEquals(
                        "The pixel at " + i + "," + j + " should be identical", sourcePixel[0], resultPixel[0], 0f);
            }
        }
    }

    @Test
    public void testIdentityDouble() {
        RenderedImage source = createTestImage(DataBuffer.TYPE_DOUBLE, IMAGE_SIZE, IMAGE_SIZE, null, false, 1);
        KernelImageN kernel = KernelFactory.createRectangle(1, 1);
        RenderedOp convolve = ConvolveDescriptor.create(source, kernel, null, null, 0, false, null);

        double[] sourcePixel = new double[1];
        double[] resultPixel = new double[1];
        Raster sourceData = source.getData();
        Raster resultData = convolve.getData();
        for (int i = 0; i < IMAGE_SIZE; i++) {
            for (int j = 0; j < IMAGE_SIZE; j++) {
                sourceData.getPixel(i, j, sourcePixel);
                resultData.getPixel(i, j, resultPixel);
                assertEquals(
                        "The pixel at " + i + "," + j + " should be identical", sourcePixel[0], resultPixel[0], 0d);
            }
        }
    }
}
