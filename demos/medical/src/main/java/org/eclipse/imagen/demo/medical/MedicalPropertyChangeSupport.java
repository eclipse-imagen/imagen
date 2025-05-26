/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 *
 * This Example Content is intended to demonstrate usage of Eclipse technology. It is
 * provided to you under the terms and conditions of the Eclipse Distribution License
 * v1.0 which is available at http://www.eclipse.org/org/documents/edl-v10.php.
 */
package org.eclipse.imagen.demo.medical;

import java.awt.image.RenderedImage;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import javax.swing.event.SwingPropertyChangeSupport;

/**
 * This is a delegation class for the property change support. It wraps a <code>SwingPropertyChangeSupport</code>
 * object. A new method is defined to remove all the listeners.
 */
public final class MedicalPropertyChangeSupport implements MedicalAppConstants {

    /** The wrapped instance of SwingPropertyChangeSupport. */
    SwingPropertyChangeSupport changeSupport;

    /**
     * Constructs a <code>MedicalPropertyChangeSupport</code> object.
     *
     * @param sourceBean The bean to be given as the source for any events.
     */
    public MedicalPropertyChangeSupport(Object sourceBean) {
        changeSupport = new SwingPropertyChangeSupport(sourceBean);
    }

    /**
     * Add a PropertyChangeListener to the listener list. The listener is registered for all properties.
     *
     * @param listener The PropertyChangeListener to be added
     */
    public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
        if (listeners == null) {
            listeners = new java.util.HashSet(3);
        }
        listeners.add(listener);
        changeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Add a PropertyChangeListener for a specific property. The listener will be invoked only when a call on
     * firePropertyChange names that specific property.
     *
     * @param propertyName The name of the property to listen on.
     * @param listener The PropertyChangeListener to be added
     */
    public synchronized void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        if (listeners == null) {
            listeners = new java.util.HashSet(3);
        }
        changeSupport.addPropertyChangeListener(propertyName, listener);
        listeners.add(listener);
    }

    /**
     * Remove a PropertyChangeListener for a specific property.
     *
     * @param propertyName The name of the property that was listened on.
     * @param listener The PropertyChangeListener to be removed
     */
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(propertyName, listener);
    }

    /** Remove all the PropertyChangeListener. */
    public synchronized void removePropertyChangeListeners() {
        if (listeners == null) return;

        int number = 0;
        Iterator iterator = listeners.iterator();
        while (iterator.hasNext()) {
            PropertyChangeListener listener = (PropertyChangeListener) iterator.next();
            if (listener instanceof MedicalImagePane) {
                removePropertyChangeListener(annotationCommand, listener);
                removePropertyChangeListener(measurementCommand, listener);
                removePropertyChangeListener(statisticsCommand, listener);
                removePropertyChangeListener(histogramCommand, listener);
            } else if (listener instanceof RenderedImage) {
                removePropertyChangeListener(windowCommand, listener);
                removePropertyChangeListener(levelCommand, listener);
                removePropertyChangeListener(rotationCommand, listener);
                removePropertyChangeListener(zoomCommand, listener);
            }

            // changeSupport.removePropertyChangeListener(listener);
            iterator.remove();
            number++;
        }
    }

    /**
     * Report a bound property update to any registered listeners. No event is fired if old and new are equal and
     * non-null.
     *
     * @param propertyName The programmatic name of the property that was changed.
     * @param oldValue The old value of the property.
     * @param newValue The new value of the property.
     */
    public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        changeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    /**
     * Check if there are any listeners for a specific property.
     *
     * @param propertyName The property name.
     * @return true if there are ore or more listeners for the given property
     */
    public synchronized boolean hasListeners(String propertyName) {
        return changeSupport.hasListeners(propertyName);
    }

    // "listeners" lists all the generic listeners.
    private transient java.util.HashSet listeners;
}
