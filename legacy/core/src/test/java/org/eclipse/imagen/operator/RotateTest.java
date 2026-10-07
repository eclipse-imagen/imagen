/* Copyright (c) 2026 Jody Garnett and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution and is available at
 * http://www.opensource.org/licenses/apache2.0.php.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.eclipse.imagen.operator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import org.eclipse.imagen.ImageN;
import org.eclipse.imagen.Interpolation;
import org.eclipse.imagen.OperationDescriptor;
import org.eclipse.imagen.ParameterBlockImageN;
import org.eclipse.imagen.RenderedOp;
import org.junit.Test;

public class RotateTest {

    @Test
    public void testDescriptorRegistered() {
        OperationDescriptor descriptor = (OperationDescriptor)
                ImageN.getDefaultInstance().getOperationRegistry().getDescriptor(OperationDescriptor.class, "Rotate");
        assertNotNull(descriptor);
        assertTrue(descriptor instanceof RotateDescriptor);
    }

    @Test
    public void testRotate() {
        BufferedImage image = new BufferedImage(4, 2, BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster raster = image.getRaster();
        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 4; x++) {
                raster.setSample(x, y, 0, 1 + x + y * 4);
            }
        }

        ParameterBlockImageN pb = new ParameterBlockImageN("Rotate")
                .addSource(image)
                .set(0f, "xOrigin")
                .set(0f, "yOrigin")
                .set((float) (Math.PI / 2), "angle")
                .set(Interpolation.getInstance(Interpolation.INTERP_NEAREST), "interpolation");
        RenderedOp op = ImageN.create("Rotate", pb);

        assertEquals(2, op.getWidth());
        assertEquals(4, op.getHeight());
        assertEquals(-2, op.getMinX());
        assertEquals(0, op.getMinY());

        Raster data = op.getData();
        assertEquals(1, data.getSample(-1, 0, 0));
        assertEquals(5, data.getSample(-2, 0, 0));
        assertEquals(4, data.getSample(-1, 3, 0));
        assertEquals(8, data.getSample(-2, 3, 0));
    }
}
