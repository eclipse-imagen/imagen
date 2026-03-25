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

import org.eclipse.imagen.OperationDescriptorImpl;

public class BlockedRegistryDescriptor extends OperationDescriptorImpl {
    public BlockedRegistryDescriptor() {
        super(
                new String[][] {
                    {"GlobalName", "BlockedRegistry"},
                    {"LocalName", "BlockedRegistry"},
                    {"Vendor", "org.eclipse.imagen.test"},
                    {"Description", "Blocked registry descriptor"},
                    {"DocURL", ""},
                    {"Version", "1.0"}
                },
                0);
    }
}
