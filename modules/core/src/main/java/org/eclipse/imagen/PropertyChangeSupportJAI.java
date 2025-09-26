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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Extension of the beans utility class <code>PropertyChangeSupport</code> which adds an accessor for the parameter
 * passed to the constructor. All events fired by the <code>firePropertyChange()</code> methods of this class are
 * instances of <code>PropertyChangeEventJAI</code>; consequently all property names are forced to lower case for
 * recognition purposes. The property name-specific <code>PropertyChangeListener</code> registration and unregistration
 * methods defined in this class also force the supplied property name to lower case.
 *
 * @see PropertyChangeSupport
 * @since ImageN 1.1
 */
public final class PropertyChangeSupportJAI extends PropertyChangeSupport {
    /** The <code>PropertyChangeEvent</code> source. */
    protected Object propertyChangeEventSource;

    /**
     * Constructs a <code>PropertyChangeSupportJAI</code> object. The parameter is cached for later use and retrieval.
     *
     * @param propertyChangeEventSource The property change event source.
     * @throws If <code>propertyChangeEventSource</code> is <code>null</code> then a <code>NullPointerException</code>
     *     will be thrown in the superclass.
     */
    public PropertyChangeSupportJAI(Object propertyChangeEventSource) {
        // if propertyChangeEventSource is null, a NullPointerException
        // is thrown in the superclass.
        super(propertyChangeEventSource);

        this.propertyChangeEventSource = propertyChangeEventSource;
    }

    /**
     * Retrieve the parameter passed to the constructor.
     *
     * @return The property change event source.
     */
    public Object getPropertyChangeEventSource() {
        return propertyChangeEventSource;
    }

    /**
     * Add a <code>PropertyChangeListener</code> for a specific property. The <code>propertyName</code> is forced to
     * lower case.
     *
     * @exception IllegalArgumentException if <code>propertyName</code> is <code>null</code>.
     */
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {

        if (propertyName == null) {
            throw new IllegalArgumentException(ImageNI18N.getString("Generic0"));
        }

        super.addPropertyChangeListener(propertyName.toLowerCase(), listener);
    }

    /**
     * Remove a <code>PropertyChangeListener</code> for a specific property. The <code>propertyName</code> is forced to
     * lower case.
     *
     * @exception IllegalArgumentException if <code>propertyName</code> is <code>null</code>.
     */
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {

        if (propertyName == null) {
            throw new IllegalArgumentException(ImageNI18N.getString("Generic0"));
        }

        super.removePropertyChangeListener(propertyName.toLowerCase(), listener);
    }

    /**
     * Report a bound property update to any registered listeners. If the supplied object is not a <code>
     * PropertyChangeEventJAI</code> then a <code>PropertyChangeEventJAI</code> is constructed from the event object's
     * accessors and fired instead.
     *
     * @param evt The <code>PropertyChangeEvent</code> object.
     */
    public void firePropertyChange(PropertyChangeEvent evt) {
        if (!(evt instanceof PropertyChangeEventJAI)) {
            evt = new PropertyChangeEventJAI(
                    evt.getSource(), evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
        }
        super.firePropertyChange(evt);
    }

    /**
     * Report a bound property update to any registered listeners. A <code>PropertyChangeEventJAI</code> is created from
     * the cached property event source and the supplied parameters and fired using the superclass <code>
     * firePropertyChange(PropertyChangeEvent)</code> method.
     *
     * @param propertyName The name of the changed property.
     * @param oldValue The old value of the property.
     * @param newValue The new value of the property.
     */
    public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        PropertyChangeEventJAI evt =
                new PropertyChangeEventJAI(propertyChangeEventSource, propertyName, oldValue, newValue);
        super.firePropertyChange(evt);
    }

    /**
     * Check whether there are any listeners for a specific property. The <code>propertyName</code> is forced to lower
     * case.
     *
     * @param propertyName The name of the property.
     * @return <code>true</code> if there are one or more listeners for the given property
     */
    public synchronized boolean hasListeners(String propertyName) {
        return super.hasListeners(propertyName.toLowerCase());
    }
}
