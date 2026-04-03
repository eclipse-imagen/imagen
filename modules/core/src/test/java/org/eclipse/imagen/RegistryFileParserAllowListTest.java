/* Copyright (c) 2026 Fernando Mino and others
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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.eclipse.imagen.operator.ConstantDescriptor;
import org.eclipse.imagen.test.BlockedRegistryDescriptor;
import org.eclipse.imagen.test.CustomRegistryDescriptor;
import org.junit.After;
import org.junit.Test;

public class RegistryFileParserAllowListTest {

    @After
    public void clearProperties() {
        System.clearProperty(AllowedRegistryClasses.PROPERTY_NAME);
        AllowedRegistryClasses.clearCacheForTests();
    }

    @Test
    public void defaultsContainKnownImageNBuiltIns() {
        Set<String> defaults = AllowedRegistryClasses.defaultClasses();

        assertTrue(defaults.contains(ConstantDescriptor.class.getName()));
        assertTrue(defaults.contains("org.eclipse.imagen.media.jiffleop.JiffleDescriptor"));
    }

    @Test
    public void loadIncludesSpiContributedRegistryClass() {
        assertTrue(AllowedRegistryClasses.allowedClasses().contains(CustomRegistryDescriptor.class.getName()));
    }

    @Test
    public void allowsBuiltInRegistryDescriptorClass() throws Exception {
        OperationRegistry registry = new OperationRegistry();

        loadRegistry(registry, "descriptor " + ConstantDescriptor.class.getName() + "\n");

        assertNotNull(registry.getDescriptor("rendered", new ConstantDescriptor().getName()));
    }

    @Test
    public void allowsSpiContributedRegistryDescriptorClass() throws Exception {
        OperationRegistry registry = new OperationRegistry();

        loadRegistry(registry, "descriptor " + CustomRegistryDescriptor.class.getName() + "\n");

        assertNotNull(registry.getDescriptor("rendered", "CustomRegistry"));
    }

    @Test
    public void rejectsBlockedRegistryDescriptorClass() throws Exception {
        OperationRegistry registry = new OperationRegistry();
        Logger logger = Logger.getLogger(RegistryFileParser.class.getName());
        List<LogRecord> records = new ArrayList<LogRecord>();
        Handler handler = new Handler() {
            @Override
            public void publish(LogRecord record) {
                records.add(record);
            }

            @Override
            public void flush() {}

            @Override
            public void close() {}
        };
        handler.setLevel(Level.ALL);
        Level previousLevel = logger.getLevel();
        boolean previousUseParentHandlers = logger.getUseParentHandlers();
        logger.addHandler(handler);
        logger.setLevel(Level.ALL);
        logger.setUseParentHandlers(false);
        try {
            loadRegistry(registry, "descriptor " + BlockedRegistryDescriptor.class.getName() + "\n");
        } finally {
            logger.removeHandler(handler);
            logger.setLevel(previousLevel);
            logger.setUseParentHandlers(previousUseParentHandlers);
        }

        assertNull(registry.getDescriptor("rendered", "BlockedRegistry"));
        assertTrue(records.stream()
                .anyMatch(record -> record.getLevel().intValue() >= Level.WARNING.intValue()
                        && record.getMessage().contains("Rejected registry class")
                        && record.getParameters() != null
                        && record.getParameters().length > 0
                        && BlockedRegistryDescriptor.class.getName().equals(record.getParameters()[0])));
    }

    @Test
    public void allowsConfiguredRegistryDescriptorClass() throws Exception {
        System.setProperty(AllowedRegistryClasses.PROPERTY_NAME, BlockedRegistryDescriptor.class.getName());
        AllowedRegistryClasses.clearCacheForTests();
        OperationRegistry registry = new OperationRegistry();

        loadRegistry(registry, "descriptor " + BlockedRegistryDescriptor.class.getName() + "\n");

        assertNotNull(registry.getDescriptor("rendered", "BlockedRegistry"));
    }

    @Test
    public void cachedAllowListDoesNotRefreshAfterPropertyChange() {
        System.setProperty(AllowedRegistryClasses.PROPERTY_NAME, BlockedRegistryDescriptor.class.getName());
        AllowedRegistryClasses.clearCacheForTests();

        Set<String> initial = AllowedRegistryClasses.allowedClasses();
        assertTrue(initial.contains(BlockedRegistryDescriptor.class.getName()));

        System.setProperty(AllowedRegistryClasses.PROPERTY_NAME, "org.example.UnusedRegistryClass");

        Set<String> cached = AllowedRegistryClasses.allowedClasses();
        assertTrue(cached.contains(BlockedRegistryDescriptor.class.getName()));
        assertTrue(!cached.contains("org.example.UnusedRegistryClass"));
    }

    private void loadRegistry(OperationRegistry registry, String content) throws Exception {
        try (InputStream stream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8))) {
            RegistryFileParser.loadOperationRegistry(registry, getClass().getClassLoader(), stream);
        }
    }
}
