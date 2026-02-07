/* Copyright (c) 2018 Jody Garnett and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution and is available at
 * http://www.opensource.org/licenses/apache2.0.php.
 */
package org.eclipse.imagen.builder;

/** Parameter used for RPE OperationDispatch, can be optional */
public class Parameter {
    final String name;
    final Object value;
    final boolean optional;

    public Parameter(String name, Object value, boolean optional) {
        this.name = name;
        this.value = value;
        this.optional = optional;
    }

    public Parameter(String name, Object value) {
        this.name = name;
        this.value = value;
        this.optional = false;
    }
}
