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

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.eclipse.imagen.spi.RegistryAllowListProvider;

/** Allows the operations registered by {@code imagen-square-root} in {@code META-INF/registryFile.imagen}. */
public class SquareRootStretchRegistryAllowListProvider implements RegistryAllowListProvider {

    private static final Set<String> CLASSES = Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList(
            "org.eclipse.imagen.media.contrastenhancement.SquareRootStretchDescriptor",
            "org.eclipse.imagen.media.contrastenhancement.SquareRootStretchCRIF")));

    @Override
    public Set<String> getAllowedRegistryClasses() {
        return CLASSES;
    }
}
