/* Copyright (c) 2026 Fernando Mino and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution and is available at
 * http://www.opensource.org/licenses/apache2.0.php.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.eclipse.imagen.media.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Set;
import org.eclipse.imagen.OperationRegistrySpi;
import org.junit.After;
import org.junit.Test;

public class ServiceAllowListTest {

    @After
    public void clearProperties() {
        System.clearProperty(AllowedServiceProviderClasses.PROPERTY_NAME);
        AllowedServiceProviderClasses.clearCacheForTests();
    }

    @Test
    public void defaultsContainKnownImageNBuiltIns() {
        Set<String> defaults = AllowedServiceProviderClasses.defaultClasses();

        assertTrue(defaults.contains("org.eclipse.imagen.media.crop.CropSpi"));
        assertTrue(defaults.contains("org.eclipse.imagen.media.jiffleop.JiffleSpi"));
    }

    @Test
    public void loadIncludesSpiContributedServiceProviderClass() {
        assertTrue(AllowedServiceProviderClasses.allowedClasses().contains(CustomOperationRegistrySpi.class.getName()));
    }

    @Test
    public void rejectsBlockedServiceProviderClass() throws Exception {
        try (ProviderLookup providerLookup = providersFor(BlockedOperationRegistrySpi.class.getName())) {
            try {
                providerLookup.getProviders().next();
                fail("Expected ServiceConfigurationError");
            } catch (ServiceConfigurationError e) {
                assertTrue(e.getMessage().contains("allow-list"));
                assertTrue(e.getMessage().contains(BlockedOperationRegistrySpi.class.getName()));
            }
        }
    }

    @Test
    public void allowsSpiContributedServiceProviderClass() throws Exception {
        try (ProviderLookup providerLookup = providersFor(CustomOperationRegistrySpi.class.getName())) {
            Object provider = providerLookup.getProviders().next();
            assertEquals(
                    CustomOperationRegistrySpi.class.getName(),
                    provider.getClass().getName());
        }
    }

    @Test
    public void allowsConfiguredServiceProviderClass() throws Exception {
        System.setProperty(AllowedServiceProviderClasses.PROPERTY_NAME, BlockedOperationRegistrySpi.class.getName());
        AllowedServiceProviderClasses.clearCacheForTests();
        try (ProviderLookup providerLookup = providersFor(BlockedOperationRegistrySpi.class.getName())) {
            Object provider = providerLookup.getProviders().next();
            assertEquals(
                    BlockedOperationRegistrySpi.class.getName(),
                    provider.getClass().getName());
        }
    }

    @Test
    public void cachedAllowListDoesNotRefreshAfterPropertyChange() {
        System.setProperty(AllowedServiceProviderClasses.PROPERTY_NAME, BlockedOperationRegistrySpi.class.getName());
        AllowedServiceProviderClasses.clearCacheForTests();

        Set<String> initial = AllowedServiceProviderClasses.allowedClasses();
        assertTrue(initial.contains(BlockedOperationRegistrySpi.class.getName()));

        System.setProperty(AllowedServiceProviderClasses.PROPERTY_NAME, "org.example.UnusedProvider");

        Set<String> cached = AllowedServiceProviderClasses.allowedClasses();
        assertTrue(cached.contains(BlockedOperationRegistrySpi.class.getName()));
        assertTrue(!cached.contains("org.example.UnusedProvider"));
    }

    private ProviderLookup providersFor(String providerClassName) throws IOException {
        Path dir = Files.createTempDirectory("service-allowlist-");
        Path servicesDir = dir.resolve("META-INF/services");
        Files.createDirectories(servicesDir);
        Files.write(
                servicesDir.resolve(OperationRegistrySpi.class.getName()),
                (providerClassName + System.lineSeparator()).getBytes(StandardCharsets.UTF_8));
        URL[] urls = {dir.toUri().toURL()};
        URLClassLoader loader = new URLClassLoader(urls, getClass().getClassLoader());
        return new ProviderLookup(loader, Service.providers(OperationRegistrySpi.class, loader));
    }

    private static final class ProviderLookup implements AutoCloseable {

        private final URLClassLoader loader;
        private final Iterator providers;

        private ProviderLookup(URLClassLoader loader, Iterator providers) {
            this.loader = loader;
            this.providers = providers;
        }

        private Iterator getProviders() {
            return providers;
        }

        @Override
        public void close() throws IOException {
            loader.close();
        }
    }
}
