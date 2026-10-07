/* Copyright (c) 2026 Jody Garnett and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution and is available at
 * http://www.opensource.org/licenses/apache2.0.php.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.eclipse.imagen.media;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;
import org.eclipse.imagen.ImageN;
import org.eclipse.imagen.OperationDescriptor;
import org.eclipse.imagen.OperationDescriptorImpl;
import org.eclipse.imagen.OperationRegistry;
import org.eclipse.imagen.ParameterBlockImageN;
import org.eclipse.imagen.RenderedOp;
import org.eclipse.imagen.operator.TranslateDescriptor;
import org.eclipse.imagen.registry.RenderableRegistryMode;
import org.eclipse.imagen.registry.RenderedRegistryMode;
import org.junit.Test;

public class LegacyCoreSpiTest {

    private static final String[] OPERATIONS = {
        "Add",
        "Affine",
        "And",
        "BandCombine",
        "BandMerge",
        "BandSelect",
        "Binarize",
        "Border",
        "Clamp",
        "ColorConvert",
        "Convolve",
        "Crop",
        "ErrorDiffusion",
        "Format",
        "ImageFunction",
        "Mosaic",
        "Null",
        "OrderedDither",
        "Rescale",
        "Scale",
        "Subtract",
        "Threshold",
        "Translate",
        "Warp",
        "Xor"
    };

    @Test
    public void testRegisteredWhenAbsent() {
        OperationRegistry registry = ImageN.getDefaultInstance().getOperationRegistry();
        for (String name : OPERATIONS) {
            OperationDescriptor descriptor =
                    (OperationDescriptor) registry.getDescriptor(OperationDescriptor.class, name);
            assertNotNull(name, descriptor);
            assertEquals(
                    name,
                    "org.eclipse.imagen.operator",
                    descriptor.getClass().getPackage().getName());
        }
        assertTrue(registry.getFactoryIterator(RenderableRegistryMode.MODE_NAME, "Translate")
                .hasNext());
    }

    @Test
    public void testSkippedWhenPresent() {
        OperationRegistry registry = new OperationRegistry();
        OperationDescriptor existing = new OperationDescriptorImpl(
                new String[][] {
                    {"GlobalName", "Add"},
                    {"LocalName", "Add"},
                    {"Vendor", "test"},
                    {"Description", "test"},
                    {"DocURL", "test"},
                    {"Version", "1.0"}
                },
                2) {};
        registry.registerDescriptor(existing);

        new LegacyCoreSpi().updateRegistry(registry);

        assertSame(existing, registry.getDescriptor(OperationDescriptor.class, "Add"));
        assertNull(registry.getFactoryIterator(RenderedRegistryMode.MODE_NAME, "Add"));
        assertTrue(registry.getDescriptor(OperationDescriptor.class, "Translate") instanceof TranslateDescriptor);
    }

    @Test
    public void testTranslate() {
        BufferedImage image = new BufferedImage(4, 2, BufferedImage.TYPE_BYTE_GRAY);
        RenderedOp rendered = ImageN.create(
                "Translate",
                new ParameterBlockImageN("Translate").addSource(image).set(3f, "xTrans"));
        assertEquals(3, rendered.getMinX());
    }
}
