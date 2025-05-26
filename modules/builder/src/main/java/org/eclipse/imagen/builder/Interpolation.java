/* Copyright (c) 2018 Jody Garnett and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution and is available at
 * http://www.opensource.org/licenses/apache2.0.php.
 */
package org.eclipse.imagen.builder;

public enum Interpolation implements KeyedHint {
    NEAREST("NearestNeighbor"),
    BILINEAR("Bilinear"),
    BICUBIC("Bicubic");

    public static String KEY = "Interpoliation";

    private final String value;

    Interpolation(String value) {
        this.value = value;
    }

    public String key() {
        return Interpolation.KEY;
    }

    public String value() {
        return this.value;
    }
}
