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
import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.TreeSet;
import org.eclipse.imagen.ImageN;
import org.eclipse.imagen.OperationRegistry;
import org.eclipse.imagen.ParameterBlockImageN;
import org.eclipse.imagen.RegistryMode;
import org.eclipse.imagen.RenderedOp;
import org.junit.Test;

public class BoxFilterTest {

    @Test
    public void testDescriptorsRegistered() throws Exception {
        Set<String> expected = new TreeSet<>();
        try (InputStream in = getClass().getResourceAsStream("/META-INF/registryFile.imagen");
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] keys = line.trim().split("\\s+");
                if (keys.length > 1 && keys[0].equals("descriptor")) {
                    expected.add(keys[1]);
                }
            }
        }
        OperationRegistry registry = ImageN.getDefaultInstance().getOperationRegistry();
        Set<String> registered = new TreeSet<>();
        for (Object descriptorClass : RegistryMode.getDescriptorClasses()) {
            for (Object descriptor : registry.getDescriptors((Class) descriptorClass)) {
                registered.add(descriptor.getClass().getName());
            }
        }
        expected.removeAll(registered);
        assertTrue("Not registered: " + expected, expected.isEmpty());
    }

    @Test
    public void testBoxFilter() {
        BufferedImage image = new BufferedImage(3, 3, BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster raster = image.getRaster();
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                raster.setSample(x, y, 0, 10 * (x + y * 3));
            }
        }

        ParameterBlockImageN pb =
                new ParameterBlockImageN("BoxFilter").addSource(image).set(3, "width");
        RenderedOp op = ImageN.create("BoxFilter", pb);

        Raster data = op.getData();
        assertEquals(40, data.getSample(1, 1, 0));
    }
}
