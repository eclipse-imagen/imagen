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
package org.eclipse.imagen.util;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Utility for loading immutable exact-class allow-lists from built-in defaults plus optional runtime configuration.
 *
 * <p>The resulting allow-list always contains the supplied defaults and may be extended through a system property or,
 * if the property is blank, an environment variable. Instances of {@link Snapshot} are immutable and therefore safe to
 * share between threads.
 */
public final class ExactClassAllowList {

    /** Immutable view of a loaded exact-class allow-list and the configuration source used to build it. */
    public static final class Snapshot {

        private final Set<String> allowedClasses;
        private final String source;

        private Snapshot(Set<String> allowedClasses, String source) {
            this.allowedClasses = allowedClasses;
            this.source = source;
        }

        /**
         * Returns the immutable set of allowed class names.
         *
         * @return immutable allow-list values
         */
        public Set<String> getAllowedClasses() {
            return allowedClasses;
        }

        /**
         * Returns the configuration source that contributed custom values, or {@code defaults} when none were set.
         *
         * @return the resolved configuration source name
         */
        public String getSource() {
            return source;
        }
    }

    /** Utility class; do not instantiate. */
    private ExactClassAllowList() {}

    /**
     * Loads an immutable allow-list snapshot.
     *
     * <p>System property values take precedence over environment variable values. Blank values are ignored. Runtime
     * configuration extends the supplied defaults rather than replacing them.
     *
     * @param propertyName system property containing a comma-separated list of exact FQNs
     * @param envName environment variable containing a comma-separated list of exact FQNs
     * @param defaults built-in defaults that are always allowed; must not be {@code null}
     * @return immutable snapshot of the resolved allow-list and its source
     * @throws NullPointerException if {@code defaults} is {@code null}
     */
    public static Snapshot load(String propertyName, String envName, Set<String> defaults) {
        return load(propertyName, envName, defaults, Collections.<String>emptySet());
    }

    /**
     * Loads an immutable allow-list snapshot including SPI-contributed defaults.
     *
     * <p>System property values take precedence over environment variable values. Blank values are ignored. Runtime
     * configuration extends the supplied defaults and SPI-contributed values rather than replacing them.
     *
     * @param propertyName system property containing a comma-separated list of exact FQNs
     * @param envName environment variable containing a comma-separated list of exact FQNs
     * @param defaults built-in defaults that are always allowed; must not be {@code null}
     * @param contributedDefaults SPI-contributed defaults discovered at runtime; must not be {@code null}
     * @return immutable snapshot of the resolved allow-list and its source
     * @throws NullPointerException if {@code defaults} or {@code contributedDefaults} is {@code null}
     */
    public static Snapshot load(
            String propertyName, String envName, Set<String> defaults, Set<String> contributedDefaults) {
        Objects.requireNonNull(defaults, "defaults must not be null");
        Objects.requireNonNull(contributedDefaults, "contributedDefaults must not be null");
        LinkedHashSet<String> allowed = new LinkedHashSet<String>(defaults);
        allowed.addAll(contributedDefaults);
        String configured = System.getProperty(propertyName);
        String source = "defaults";
        if (configured == null || configured.trim().isEmpty()) {
            configured = System.getenv(envName);
            if (configured != null && !configured.trim().isEmpty()) {
                source = "environment variable";
            }
        } else {
            source = "system property";
        }
        if (configured != null) {
            for (String token : configured.split(",")) {
                String trimmed = token.trim();
                if (!trimmed.isEmpty()) {
                    allowed.add(trimmed);
                }
            }
        }
        return new Snapshot(Collections.unmodifiableSet(allowed), source);
    }
}
