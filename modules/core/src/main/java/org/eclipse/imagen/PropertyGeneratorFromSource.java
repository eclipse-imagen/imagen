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
 * SPDX-License-Identifier: Apache-2.0
 */
package org.eclipse.imagen;

import java.util.Vector;
import org.eclipse.imagen.media.util.PropertyGeneratorImpl;

/**
 * A class that implements the <code>PropertyGenerator</code> interface. This class is used when a property is to be
 * calculated from a particular source. All properties except the named one are ignored. If the given source index out
 * of range the property will be undefined, in particular no exception will be thrown.
 */
class PropertyGeneratorFromSource extends PropertyGeneratorImpl {

    int sourceIndex;
    String propertyName;

    PropertyGeneratorFromSource(int sourceIndex, String propertyName) {
        super(
                new String[] {propertyName},
                new Class[] {Object.class}, // could be anything
                new Class[] {OperationNode.class});

        if (propertyName == null) {
            throw new IllegalArgumentException(ImageNI18N.getString("Generic0"));
        }

        this.sourceIndex = sourceIndex;
        this.propertyName = propertyName;
    }

    public Object getProperty(String name, Object opNode) {
        if (name == null || opNode == null) {
            throw new IllegalArgumentException(ImageNI18N.getString("Generic0"));
        }

        if (sourceIndex >= 0 && opNode instanceof OperationNode && propertyName.equalsIgnoreCase(name)) {
            OperationNode op = (OperationNode) opNode;
            Vector sources = op.getParameterBlock().getSources();
            if (sources != null && sourceIndex < sources.size()) {
                Object src = sources.elementAt(sourceIndex);
                if (src instanceof PropertySource) {
                    return ((PropertySource) src).getProperty(name);
                }
            }
        }

        return java.awt.Image.UndefinedProperty;
    }
}
