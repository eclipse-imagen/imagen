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
 * Sub-interface of <code>PropertySource</code> which permits setting
 * the values of JAI properties in addition to obtaining their names
 * and values.  As the values of properties managed by classes which
 * implement this interface may change, this is also a sub-interface
 * of <code>PropertyChangeEmitter</code>.  This permits other objects
 * to register as listeners of particular JAI properties.
 *
 * <p> The case of the names of properties added via this interface
 * should be retained although the case will be ignored in queries via
 * <code>getProperty()</code> and will be forced to lower case in
 * emitted <code>PropertySourceChangeEvent<code>.
 *
 * @see PropertySource
 * @see PropertyChangeEmitter
 *
 * @since JAI 1.1
 */
public interface WritablePropertySource extends PropertySource, PropertyChangeEmitter {
    /**
     * Adds the property value associated with the supplied name to the <code>WritablePropertySource</code>. Properties
     * set by this means will supersede any properties of the same name which might otherwise be derived dynamically.
     *
     * <p>Implementing classes which should fire a <code>PropertySourceChangeEvent</code> with a name set to that of the
     * set property (retaining case), source set to the <code>WritablePropertySource</code>, and old and new values set
     * to the previous and current values of the property, respectively. Neither the old nor the new value may <code>
     * null</code>: undefined properties must as usual be indicated by an the constant value <code>
     * java.awt.Image.UndefinedProperty</code>. It is however legal for either but not both of the old and new property
     * values to equal <code>java.awt.Image.UndefinedProperty</code>.
     *
     * @param propertyName the name of the property, as a <code>String</code>.
     * @param propertyValue the property, as a general <code>Object</code>.
     * @exception IllegalArgumentException if <code>propertyName</code> or <code>propertyValue</code> is <code>null
     *     </code>.
     */
    void setProperty(String propertyName, Object propertyValue);

    /**
     * Removes the named property from the <code>WritablePropertySource</code>. This method will clear any locally
     * cached (static) properties but may have no effect on properties which would be derived dynamically.
     *
     * @param propertyName the name of the property, as a <code>String</code>.
     * @param propertyValue the property, as a general <code>Object</code>.
     * @exception IllegalArgumentException if <code>propertyName</code> is <code>null</code>.
     */
    void removeProperty(String propertyName);
}
