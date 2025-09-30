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

import java.beans.PropertyChangeListener;

/**
 * A class which emits <code>PropertyChangeEvent</code>s. This abstraction permits objects of disparate types to be
 * recognized as sources of <code>PropertyChangeEvent</code>s. <code>PropertyChangeEvent</code>s emitted by ImageN
 * objects will be <code>PropertyChangeEventJAI</code> instances.
 *
 * <p>Note that the case of property names used in this context is significant.
 *
 * @see PropertyChangeEventJAI
 * @since ImageN 0.4.0
 */
public interface PropertyChangeEmitter {

    /** Add a PropertyChangeListener to the listener list. The listener is registered for all properties. */
    void addPropertyChangeListener(PropertyChangeListener listener);

    /**
     * Add a PropertyChangeListener for a specific property. The listener will be invoked only when a call on
     * firePropertyChange names that specific property.
     *
     * @throws IllegalArgumentException for null <code>propertyName</code>.
     */
    void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);

    /**
     * Remove a PropertyChangeListener from the listener list. This removes a PropertyChangeListener that was registered
     * for all properties.
     */
    void removePropertyChangeListener(PropertyChangeListener listener);

    /**
     * Remove a PropertyChangeListener for a specific property.
     *
     * @throws IllegalArgumentException for null <code>propertyName</code>.
     */
    void removePropertyChangeListener(String propertyName, PropertyChangeListener listener);
}
