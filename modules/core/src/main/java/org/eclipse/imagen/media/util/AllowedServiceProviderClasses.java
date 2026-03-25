/*
 * Copyright (c) 2026 Fernando Mino and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution and is available at
 * http://www.opensource.org/licenses/apache2.0.php.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.eclipse.imagen.media.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.imagen.spi.ServiceProviderAllowListProvider;
import org.eclipse.imagen.util.ExactClassAllowList;
import org.eclipse.imagen.util.ExactClassAllowList.Snapshot;

/**
 * Loads the service-provider allow-list used by {@link Service} before reflective provider instantiation.
 *
 * <p>Built-in defaults track the {@code OperationRegistrySpi} implementations shipped by ImageN modules. Runtime
 * configuration extends, but never replaces, those defaults. Additional trusted classes may be contributed by
 * downstream libraries through {@link ServiceProviderAllowListProvider}. The effective allow-list is resolved once per
 * JVM and cached as an immutable snapshot, so runtime configuration changes are not observed after the first access.
 */
final class AllowedServiceProviderClasses {

    static final String PROPERTY_NAME = "org.eclipse.imagen.allowedServiceProviderClasses";
    static final String ENV_NAME = "ORG_ECLIPSE_IMAGEN_ALLOWED_SERVICE_PROVIDER_CLASSES";

    private static final Logger LOGGER = Logger.getLogger(AllowedServiceProviderClasses.class.getName());
    private static volatile Set<String> cachedAllowedClasses;

    private static final Set<String> DEFAULTS = Collections.unmodifiableSet(new LinkedHashSet<String>(Arrays.asList(
            "org.eclipse.imagen.media.contour.ContourSpi",
            "org.eclipse.imagen.media.crop.CropSpi",
            "org.eclipse.imagen.media.jiffleop.JiffleSpi",
            "org.jaitools.media.jai.vectorize.VectorizeSpi")));

    /** Utility class; do not instantiate. */
    private AllowedServiceProviderClasses() {}

    /**
     * Returns the cached immutable service-provider allow-list for the current JVM.
     *
     * <p>The allow-list is initialized lazily on first access and then reused for the lifetime of the JVM. This keeps
     * SPI discovery and property parsing off the reflective hot path.
     *
     * @return immutable set of exact class names allowed for SPI instantiation
     */
    static Set<String> allowedClasses() {
        Set<String> allowedClasses = cachedAllowedClasses;
        if (allowedClasses == null) {
            synchronized (AllowedServiceProviderClasses.class) {
                allowedClasses = cachedAllowedClasses;
                if (allowedClasses == null) {
                    allowedClasses = load();
                    cachedAllowedClasses = allowedClasses;
                }
            }
        }
        return allowedClasses;
    }

    /**
     * Builds a fresh immutable service-provider allow-list from built-ins, SPI contributions, and startup
     * configuration.
     *
     * <p>Production enforcement should call {@link #allowedClasses()} so the effective set is resolved once per JVM.
     * This method remains separate to support test setup and cache initialization.
     *
     * @return immutable set of exact class names allowed for SPI instantiation
     */
    static Set<String> load() {
        Set<String> contributedDefaults = loadContributedDefaults();
        Snapshot snapshot = ExactClassAllowList.load(PROPERTY_NAME, ENV_NAME, DEFAULTS, contributedDefaults);
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(
                    Level.FINE,
                    "Loaded service-provider allow-list from {0} with {1} entries ({2} SPI-contributed)",
                    new Object[] {
                        snapshot.getSource(),
                        Integer.valueOf(snapshot.getAllowedClasses().size()),
                        Integer.valueOf(contributedDefaults.size())
                    });
        }
        return snapshot.getAllowedClasses();
    }

    /**
     * Returns the built-in ImageN defaults without SPI or runtime configuration applied.
     *
     * @return immutable built-in service-provider defaults
     */
    static Set<String> defaultClasses() {
        return DEFAULTS;
    }

    /**
     * Clears the cached allow-list snapshot.
     *
     * <p>This hook exists only for test isolation and must not be used as a runtime refresh mechanism.
     */
    static void clearCacheForTests() {
        cachedAllowedClasses = null;
    }

    private static Set<String> loadContributedDefaults() {
        LinkedHashSet<String> contributed = new LinkedHashSet<String>();
        ServiceLoader<ServiceProviderAllowListProvider> loader =
                ServiceLoader.load(ServiceProviderAllowListProvider.class);
        Iterator<ServiceProviderAllowListProvider> iterator = loader.iterator();

        while (true) {
            ServiceProviderAllowListProvider provider;
            try {
                if (!iterator.hasNext()) {
                    break;
                }
                provider = iterator.next();
            } catch (ServiceConfigurationError e) {
                LOGGER.log(Level.WARNING, "Failed to load service-provider allow-list contributor", e);
                continue;
            }

            try {
                int before = contributed.size();
                addContributedClasses(contributed, provider.getAllowedServiceProviderClasses());
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.log(
                            Level.FINE,
                            "Loaded {0} service-provider allow-list entries from contributor {1}",
                            new Object[] {
                                Integer.valueOf(contributed.size() - before),
                                provider.getClass().getName()
                            });
                }
            } catch (RuntimeException e) {
                LOGGER.log(
                        Level.WARNING,
                        e,
                        () -> "Ignoring service-provider allow-list contributor "
                                + provider.getClass().getName());
            }
        }

        return Collections.unmodifiableSet(contributed);
    }

    private static void addContributedClasses(Set<String> target, Set<String> contributedClasses) {
        if (contributedClasses == null) {
            return;
        }

        for (String className : contributedClasses) {
            if (className == null) {
                continue;
            }
            String trimmed = className.trim();
            if (!trimmed.isEmpty()) {
                target.add(trimmed);
            }
        }
    }
}
