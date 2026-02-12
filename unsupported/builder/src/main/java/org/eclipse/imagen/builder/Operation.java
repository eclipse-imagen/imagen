/* Copyright (c) 2018 Jody Garnett and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution and is available at
 * http://www.opensource.org/licenses/apache2.0.php.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.eclipse.imagen.builder;

import java.awt.image.RenderedImage;

public abstract class Operation {
    private final String name;

    protected Operation(String name) {
        this.name = name;
    }

    /**
     * Name of operation implemented by factory.
     *
     * @return name of operation
     */
    public String getName() {
        return name;
    }

    /**
     * Create RenderedImage using named operation
     *
     * @param builder OperationBuilder, must match factory name
     * @return RenderedImage
     */
    public abstract RenderedImage create(OperationBuilder builder);
}
