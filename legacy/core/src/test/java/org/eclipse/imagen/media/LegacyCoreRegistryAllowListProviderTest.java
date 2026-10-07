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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import org.eclipse.imagen.ImageN;
import org.eclipse.imagen.OperationDescriptor;
import org.junit.Test;

public class LegacyCoreRegistryAllowListProviderTest {

    private static Set<String> registryFileClasses(String types) throws Exception {
        Set<String> classes = new TreeSet<>();
        try (InputStream in =
                        LegacyCoreRegistryAllowListProvider.class.getResourceAsStream("/META-INF/registryFile.imagen");
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] keys = line.trim().split("\\s+");
                if (keys.length > 1 && keys[0].matches(types)) {
                    classes.add(keys[1]);
                }
            }
        }
        return classes;
    }

    @Test
    public void testAllowListMatchesRegistryFile() throws Exception {
        Set<String> allowed = new TreeSet<>(new LegacyCoreRegistryAllowListProvider().getAllowedRegistryClasses());
        assertEquals(registryFileClasses("descriptor|rendered|renderable|collection"), allowed);
    }

    @Test
    public void testDescriptorsRegistered() throws Exception {
        Set<String> registered = new TreeSet<>();
        for (Object descriptor :
                ImageN.getDefaultInstance().getOperationRegistry().getDescriptors(OperationDescriptor.class)) {
            registered.add(descriptor.getClass().getName());
        }
        Set<String> missing = registryFileClasses("descriptor");
        missing.removeAll(registered);
        assertEquals(Collections.emptySet(), missing);
    }
}
