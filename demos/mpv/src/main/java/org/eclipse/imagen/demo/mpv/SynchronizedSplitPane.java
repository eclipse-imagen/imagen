/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 *
 * This Example Content is intended to demonstrate usage of Eclipse technology. It is
 * provided to you under the terms and conditions of the Eclipse Distribution License
 * v1.0 which is available at http://www.eclipse.org/org/documents/edl-v10.php.
 */
package org.eclipse.imagen.demo.mpv;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JButton;
import javax.swing.JSplitPane;

/** The class to create synchronized split pane. */
public class SynchronizedSplitPane extends JSplitPane implements PropertyChangeListener {

    private int synchronizedDividerSize = 4;

    public SynchronizedSplitPane(int newOrientation) {
        super(newOrientation, true, new JButton("Left button"), new JButton("Right button"));
        setDividerSize(synchronizedDividerSize);
    }

    public SynchronizedSplitPane(int newOrientation, Component newLeftComponent, Component newRightComponent) {
        super(newOrientation, true, newLeftComponent, newRightComponent);
        setDividerSize(synchronizedDividerSize);
    }

    public void addSynchronized(SynchronizedSplitPane c) {
        c.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, this);
        this.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, c);
    }

    public void propertyChange(PropertyChangeEvent event) {
        Object source = event.getSource();

        if (source instanceof JSplitPane) {
            JSplitPane other = (JSplitPane) source;
            if (other.getOrientation() != this.getOrientation())
                throw new IllegalArgumentException("Should have same orientation");

            if (event.getPropertyName().equals(DIVIDER_LOCATION_PROPERTY)) {
                Object newValue = event.getNewValue();
                if (newValue instanceof Double) {
                    double newLocation = ((Double) newValue).doubleValue();
                    synthronizeDividerLocation(newLocation);
                } else if (newValue instanceof Integer) {
                    int newLocation = ((Integer) newValue).intValue();
                    synthronizeDividerLocation(newLocation);
                }
            }
        }
    }

    private void synthronizeDividerLocation(double proportionalLocation) {
        if (proportionalLocation < 0.0 || proportionalLocation > 1.0) {
            throw new IllegalArgumentException("proportional location must " + "be between 0.0 and 1.0.");
        }

        if (getOrientation() == VERTICAL_SPLIT) {
            synthronizeDividerLocation((int) ((double) (getHeight() - getDividerSize()) * proportionalLocation));
        } else {
            synthronizeDividerLocation((int) ((double) (getWidth() - getDividerSize()) * proportionalLocation));
        }
    }

    private void synthronizeDividerLocation(int location) {
        if (getLastDividerLocation() == location) return;
        setDividerLocation(location);
    }
}
