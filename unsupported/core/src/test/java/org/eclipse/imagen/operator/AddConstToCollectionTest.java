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

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.eclipse.imagen.ImageN;
import org.eclipse.imagen.ParameterBlockImageN;
import org.eclipse.imagen.registry.CollectionRegistryMode;
import org.junit.Test;

public class AddConstToCollectionTest {

    @Test
    public void testSingleConstant() {
        List<RenderedImage> sources = new ArrayList<>();
        sources.add(gray(10));
        sources.add(gray(250));

        List<Raster> results = addConst(sources, new double[] {5});

        assertEquals(2, results.size());
        assertEquals(15, results.get(0).getSample(0, 0, 0));
        assertEquals(255, results.get(1).getSample(0, 0, 0));
    }

    @Test
    public void testConstantPerBand() {
        BufferedImage rgb = new BufferedImage(1, 1, BufferedImage.TYPE_3BYTE_BGR);
        rgb.getRaster().setPixel(0, 0, new int[] {10, 20, 30});
        List<RenderedImage> sources = new ArrayList<>();
        sources.add(rgb);

        Raster result = addConst(sources, new double[] {1, 2, 3}).get(0);

        assertEquals(11, result.getSample(0, 0, 0));
        assertEquals(22, result.getSample(0, 0, 1));
        assertEquals(33, result.getSample(0, 0, 2));
    }

    private static BufferedImage gray(int value) {
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_GRAY);
        image.getRaster().setSample(0, 0, 0, value);
        return image;
    }

    private static List<Raster> addConst(Collection<RenderedImage> sources, double[] constants) {
        ParameterBlockImageN pb = new ParameterBlockImageN("AddConstToCollection", CollectionRegistryMode.MODE_NAME)
                .addSource(sources)
                .set(constants, "constants");
        Collection<?> collection = ImageN.createCollection("AddConstToCollection", pb, null);
        List<Raster> rasters = new ArrayList<>();
        for (Iterator<?> it = collection.iterator(); it.hasNext(); ) {
            rasters.add(((RenderedImage) it.next()).getData());
        }
        return rasters;
    }
}
