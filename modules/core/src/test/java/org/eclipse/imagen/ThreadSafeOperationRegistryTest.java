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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import org.eclipse.imagen.registry.RenderedRegistryMode;
import org.eclipse.imagen.util.ImagingException;
import org.junit.Test;

public class ThreadSafeOperationRegistryTest {

    static class TestDescriptor extends OperationDescriptorImpl {
        TestDescriptor(String name) {
            super(
                    new String[][] {
                        {"GlobalName", name},
                        {"LocalName", name},
                        {"Vendor", "org.eclipse.imagen.test"},
                        {"Description", name},
                        {"DocURL", ""},
                        {"Version", "1.0"}
                    },
                    0);
        }
    }

    static class LazyRegisteringFactory implements RenderedImageFactory {
        private final OperationRegistry registry;

        LazyRegisteringFactory(OperationRegistry registry) {
            this.registry = registry;
        }

        @Override
        public RenderedImage create(ParameterBlock pb, RenderingHints hints) {
            registry.registerDescriptor(new TestDescriptor("Helper"));
            return null;
        }
    }

    @Test(timeout = 10000)
    public void testWriteUnderReadLockFailsFast() {
        OperationRegistry registry = OperationRegistry.getThreadSafeOperationRegistry();
        registry.registerDescriptor(new TestDescriptor("Lazy"));
        registry.registerFactory(
                RenderedRegistryMode.MODE_NAME,
                "Lazy",
                "org.eclipse.imagen.test",
                new LazyRegisteringFactory(registry));

        ImagingException e = assertThrows(
                ImagingException.class,
                () -> registry.invokeFactory(
                        RenderedRegistryMode.MODE_NAME, "Lazy", new Object[] {new ParameterBlock(), null}));
        assertTrue(e.getRootCause() instanceof IllegalStateException);

        registry.registerDescriptor(new TestDescriptor("Helper"));
        assertNotNull(registry.getDescriptor(RenderedRegistryMode.MODE_NAME, "Helper"));
    }
}
