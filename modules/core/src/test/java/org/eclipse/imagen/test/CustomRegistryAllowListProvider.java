/* Copyright (c) 2026 Fernando Mino and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution and is available at
 * http://www.opensource.org/licenses/apache2.0.php.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.eclipse.imagen.test;

import java.util.Collections;
import java.util.Set;
import org.eclipse.imagen.spi.RegistryAllowListProvider;

public class CustomRegistryAllowListProvider implements RegistryAllowListProvider {
    @Override
    public Set<String> getAllowedRegistryClasses() {
        return Collections.singleton(CustomRegistryDescriptor.class.getName());
    }
}
