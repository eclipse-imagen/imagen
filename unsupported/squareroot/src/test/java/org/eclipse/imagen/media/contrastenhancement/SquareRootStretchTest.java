/* Copyright (c) 2026 Jody Garnett and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution and is available at
 * http://www.opensource.org/licenses/apache2.0.php.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.eclipse.imagen.media.contrastenhancement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.TreeSet;
import org.eclipse.imagen.ImageN;
import org.eclipse.imagen.OperationDescriptor;
import org.eclipse.imagen.RenderedOp;
import org.eclipse.imagen.registry.RenderedRegistryMode;
import org.junit.Test;

public class SquareRootStretchTest {

    private static BufferedImage grayImage(int type, int... values) {
        BufferedImage image = new BufferedImage(values.length, 1, type);
        WritableRaster raster = image.getRaster();
        for (int x = 0; x < values.length; x++) {
            raster.setSample(x, 0, 0, values[x]);
        }
        return image;
    }

    @Test
    public void testRegistered() {
        assertNotNull(ImageN.getDefaultInstance()
                .getOperationRegistry()
                .getDescriptor(OperationDescriptor.class, "SquareRootStretch"));
        assertNotNull(ImageN.getDefaultInstance()
                .getOperationRegistry()
                .getFactory(RenderedRegistryMode.MODE_NAME, "SquareRootStretch"));
    }

    @Test
    public void testAllowListMatchesRegistryFile() throws Exception {
        Set<String> registered = new TreeSet<>();
        try (InputStream in = SquareRootStretchRegistryAllowListProvider.class.getResourceAsStream(
                        "/META-INF/registryFile.imagen");
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] keys = line.trim().split("\\s+");
                if (keys.length > 1 && keys[0].matches("descriptor|rendered|renderable|collection")) {
                    registered.add(keys[1]);
                }
            }
        }
        Set<String> allowed =
                new TreeSet<>(new SquareRootStretchRegistryAllowListProvider().getAllowedRegistryClasses());
        assertEquals(registered, allowed);
    }

    @Test
    public void testByte() {
        BufferedImage source = grayImage(BufferedImage.TYPE_BYTE_GRAY, 0, 16, 64, 144, 255);
        RenderedOp op = SquareRootStretchDescriptor.create(
                source, new int[] {0}, new int[] {256}, new int[] {0}, new int[] {256}, null);
        Raster data = op.getData();
        assertEquals(DataBuffer.TYPE_BYTE, data.getDataBuffer().getDataType());
        assertEquals(0, data.getSample(0, 0, 0));
        assertEquals(64, data.getSample(1, 0, 0));
        assertEquals(128, data.getSample(2, 0, 0), 1);
        assertEquals(192, data.getSample(3, 0, 0));
        assertEquals(255, data.getSample(4, 0, 0));
    }

    @Test
    public void testByteRange() {
        BufferedImage source = grayImage(BufferedImage.TYPE_BYTE_GRAY, 0, 10, 35, 110, 200);
        RenderedOp op = SquareRootStretchDescriptor.create(
                source, new int[] {10}, new int[] {110}, new int[] {50}, new int[] {150}, null);
        Raster data = op.getData();
        assertEquals(50, data.getSample(0, 0, 0));
        assertEquals(50, data.getSample(1, 0, 0));
        assertEquals(100, data.getSample(2, 0, 0));
        assertEquals(150, data.getSample(3, 0, 0));
        assertEquals(150, data.getSample(4, 0, 0));
    }

    @Test
    public void testUShort() {
        BufferedImage source = grayImage(BufferedImage.TYPE_USHORT_GRAY, 0, 16384, 40000, 65535);
        RenderedOp op = SquareRootStretchDescriptor.create(
                source, new int[] {0}, new int[] {65536}, new int[] {0}, new int[] {256}, null);
        Raster data = op.getData();
        assertEquals(DataBuffer.TYPE_USHORT, data.getDataBuffer().getDataType());
        assertEquals(0, data.getSample(0, 0, 0));
        assertEquals(128, data.getSample(1, 0, 0));
        assertEquals(200, data.getSample(2, 0, 0));
        assertEquals(256, data.getSample(3, 0, 0));
    }
}
