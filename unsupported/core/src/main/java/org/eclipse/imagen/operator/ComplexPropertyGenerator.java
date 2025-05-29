/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.eclipse.imagen.operator;

import org.eclipse.imagen.RenderableOp;
import org.eclipse.imagen.RenderedOp;
import org.eclipse.imagen.media.util.PropertyGeneratorImpl;

/**
 * This property generator returns <code>Boolean.TRUE</code> for the "COMPLEX" property for the rendered and renderable
 * modes.
 */
class ComplexPropertyGenerator extends PropertyGeneratorImpl {

    /** Constructor. */
    public ComplexPropertyGenerator() {
        super(new String[] {"COMPLEX"}, new Class[] {Boolean.class}, new Class[] {RenderedOp.class, RenderableOp.class
        });
    }

    /**
     * Returns the specified property.
     *
     * @param name Property name.
     * @param op Operation node.
     */
    public Object getProperty(String name, Object op) {
        validate(name, op);

        return name.equalsIgnoreCase("complex") ? Boolean.TRUE : java.awt.Image.UndefinedProperty;
    }
}
