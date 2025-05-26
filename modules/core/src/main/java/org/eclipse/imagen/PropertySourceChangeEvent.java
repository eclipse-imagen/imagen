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

package org.eclipse.imagen;

/**
 * A class instances of which represent JAI properties as emitted for example by a <code>PropertySource</code> but in
 * the guise of an event as defined for Java Beans. This class definition adds no functionality to that provided by the
 * superclass per se. The significance of the derivation is that instances of this event by definition refer to
 * properties in the JAI sense of the term. Otherwise put, this class provides an extra level of indirection.
 *
 * @see PropertyChangeEventJAI
 * @see PropertySource
 * @since JAI 1.1
 */
public class PropertySourceChangeEvent extends PropertyChangeEventJAI {
    /**
     * Constructs a <code>PropertySourceChangeEvent</code>. <code>propertyName</code> is forced to lower case; all other
     * parameters are passed unmodified to the superclass constructor. If <code>oldValue</code> or <code>newValue</code>
     * is to indicate a property for which no value is defined, then the object <code>java.awt.Image.UndefinedProperty
     * </code> should be passed.
     *
     * @exception NullPointerException if <code>propertyName</code> is <code>null</code>.
     * @exception IllegalArgumentException if <code>source</code>, <code>oldValue</code> or <code>newValue</code> is
     *     <code>null</code>.
     */
    public PropertySourceChangeEvent(Object source, String propertyName, Object oldValue, Object newValue) {
        super(source, propertyName, oldValue, newValue);

        // Note: source and propertyName are checked for null in superclass.

        if (oldValue == null) {
            throw new IllegalArgumentException(JaiI18N.getString("PropertySourceChangeEvent0"));
        } else if (newValue == null) {
            throw new IllegalArgumentException(JaiI18N.getString("PropertySourceChangeEvent1"));
        }
    }
}
