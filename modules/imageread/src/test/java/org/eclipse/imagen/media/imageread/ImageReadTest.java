/* Copyright (c) 2025 Andrea Aime and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution and is available at
 * http://www.opensource.org/licenses/apache2.0.php.
 */
package org.eclipse.imagen.media.imageread;

import static org.junit.Assert.*;

import java.awt.*;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.io.File;
import org.eclipse.imagen.JAI;
import org.eclipse.imagen.ParameterListDescriptor;
import org.eclipse.imagen.RegistryElementDescriptor;
import org.eclipse.imagen.RenderedOp;
import org.junit.Test;

public class ImageReadTest {

    @Test
    public void testRegistration() {
        RegistryElementDescriptor descriptor =
                JAI.getDefaultInstance().getOperationRegistry().getDescriptor("rendered", "ImageRead");
        assertNotNull(descriptor);
        assertEquals("ImageRead", descriptor.getName());
        ParameterListDescriptor parameters = descriptor.getParameterListDescriptor("rendered");
        assertEquals(
                new String[] {
                    "Input",
                    "ImageChoice",
                    "ReadMetadata",
                    "ReadThumbnails",
                    "VerifyInput",
                    "Listeners",
                    "Locale",
                    "ReadParam",
                    "Reader"
                },
                parameters.getParamNames());
    }

    @Test
    public void testRead() throws Exception {
        RenderedOp image = JAI.create(
                "ImageRead", new File("src/test/resources/sample.tiff").toURI().toURL());
        assertNotNull(image);
        try {
            // confirm all basic properties (this already triggers a read)
            assertEquals(120, image.getTileWidth());
            assertEquals(15, image.getTileHeight());
            SampleModel sampleModel = image.getSampleModel();
            assertEquals(1, sampleModel.getNumBands());
            assertEquals(DataBuffer.TYPE_BYTE, sampleModel.getDataType());
            assertTrue(sampleModel instanceof PixelInterleavedSampleModel);
            ColorModel colorModel = image.getColorModel();
            assertEquals(1, colorModel.getNumComponents());
            assertTrue(colorModel instanceof ComponentColorModel);

            // grab a tile to double-check the image is readable
            Raster tile = image.getTile(0, 0);
            assertEquals(120, tile.getWidth());
            assertEquals(15, tile.getHeight());
            assertEquals(sampleModel, tile.getSampleModel());
            assertEquals(new Rectangle(0, 0, 120, 15), tile.getBounds());
        } finally {
            image.dispose();
        }
    }
}
