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
package org.eclipse.imagen.spi;

import java.util.Set;

/**
 * Contributes exact service-provider class names that ImageN may instantiate through its SPI loading path.
 *
 * <p>Implementations are discovered through Java {@code ServiceLoader}. Returned values must be exact fully qualified
 * class names. Blank entries are ignored by ImageN.
 */
public interface ServiceProviderAllowListProvider {

    /**
     * Returns exact service-provider class names that should be trusted in addition to ImageN built-in defaults.
     *
     * @return exact fully qualified service-provider class names; never {@code null}, use an empty set to contribute
     *     nothing
     */
    Set<String> getAllowedServiceProviderClasses();
}
