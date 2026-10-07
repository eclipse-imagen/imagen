/* Copyright (c) 2026 Jody Garnett and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution and is available at
 * http://www.opensource.org/licenses/apache2.0.php.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.eclipse.imagen.media.crop;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import org.eclipse.imagen.OperationDescriptor;
import org.eclipse.imagen.OperationRegistry;
import org.eclipse.imagen.registry.RenderedRegistryMode;
import org.junit.Test;

public class CropSpiTest {

    @Test
    public void testRegistersOnce() {
        OperationRegistry registry = new OperationRegistry();
        CropSpi spi = new CropSpi();

        spi.updateRegistry(registry);
        spi.updateRegistry(registry);

        assertTrue(registry.getDescriptor(OperationDescriptor.class, "crop") instanceof CropDescriptor);
        List factories =
                registry.getOrderedFactoryList(RenderedRegistryMode.MODE_NAME, "Crop", "org.eclipse.imagen.media");
        assertEquals(1, factories.size());
        assertTrue(factories.get(0) instanceof CropCRIF);
    }
}
