/*
 * Copyright (c) 2026 Eclipse ImageN contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution and is available at
 * http://www.opensource.org/licenses/apache2.0.php.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.eclipse.imagen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import org.junit.Test;

public class ParameterBlockImageNTest {

    private static final BufferedImage IMAGE = TestSupport.createImage(DataBuffer.TYPE_BYTE, 4, 4);

    @Test
    public void testSetParameterChain() {
        RenderedOp op = ImageN.create(
                "SubsampleAverage",
                new ParameterBlockImageN("SubsampleAverage")
                        .setSource("source0", IMAGE)
                        .setParameter("scaleX", 0.5)
                        .setParameter("scaleY", 0.5));

        assertEquals(2, op.getWidth());
        assertEquals(2, op.getHeight());
    }

    @Test
    public void testParameterBlockChain() {
        ParameterBlockImageN pb = new ParameterBlockImageN("SubsampleAverage")
                .addSource(IMAGE)
                .set(0.5, 0)
                .set(0.25, 1);

        assertSame(IMAGE, pb.getSource(0));
        assertEquals(0.5, pb.getDoubleParameter("scaleX"), 0.0);
        assertEquals(0.25, pb.getDoubleParameter("scaleY"), 0.0);
    }

    @Test
    public void testClone() {
        ParameterBlockImageN pb = new ParameterBlockImageN("SubsampleAverage")
                .setSource("source0", IMAGE)
                .setParameter("scaleX", 0.5);

        ParameterBlockImageN copy = pb.clone().setParameter("scaleX", 0.25);

        assertNotSame(pb, copy);
        assertEquals(0.5, pb.getDoubleParameter("scaleX"), 0.0);
        assertEquals(0.25, copy.getDoubleParameter("scaleX"), 0.0);
    }

    @Test
    public void testParameterListImplChain() {
        ParameterListImpl list = new ParameterListImpl(
                        new ParameterBlockImageN("SubsampleAverage").getParameterListDescriptor())
                .setParameter("scaleX", 0.5)
                .setParameter("scaleY", 0.25);

        assertEquals(0.5, list.getDoubleParameter("scaleX"), 0.0);
        assertEquals(0.25, list.getDoubleParameter("scaleY"), 0.0);
    }

    @Test(expected = IllegalStateException.class)
    @SuppressWarnings("deprecation")
    public void testAddUnsupported() {
        new ParameterBlockImageN("SubsampleAverage").add(0.5);
    }

    @Test(expected = IllegalStateException.class)
    @SuppressWarnings("deprecation")
    public void testAddObjectUnsupported() {
        new ParameterBlockImageN("SubsampleAverage").add(Double.valueOf(0.5));
    }
}
