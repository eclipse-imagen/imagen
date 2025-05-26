/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 *
 * This Example Content is intended to demonstrate usage of Eclipse technology. It is
 * provided to you under the terms and conditions of the Eclipse Distribution License
 * v1.0 which is available at http://www.eclipse.org/org/documents/edl-v10.php.
 */
package org.eclipse.imagen.tutorial;

import java.awt.*;
import java.awt.image.renderable.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import org.eclipse.imagen.*;
import org.eclipse.imagen.widgets.*;

public class Threshold extends JPanel implements ChangeListener {

    private PlanarImage source = null;
    private PlanarImage target = null;
    private ImageDisplay display = null;
    private double low[];
    private double high[];
    private double map[];

    public Threshold(String filename) {

        setLayout(new BorderLayout());

        add(new JLabel("CT Scan"), BorderLayout.NORTH);

        source = JAI.create("fileload", filename);

        display = new ImageDisplay(source);
        add(display, BorderLayout.CENTER);

        low = new double[1];
        high = new double[1];
        map = new double[1];

        low[0] = 0.0F;
        high[0] = 0.0F;
        map[0] = 0.0F;

        JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 255, 0);
        Hashtable labels = new Hashtable();
        labels.put(new Integer(0), new JLabel("0"));
        labels.put(new Integer(255), new JLabel("255"));
        slider.setLabelTable(labels);
        slider.setPaintLabels(true);
        slider.addChangeListener(this);
        slider.setEnabled(true);

        JPanel borderedPane = new JPanel();
        borderedPane.setLayout(new BorderLayout());
        borderedPane.setBorder(BorderFactory.createTitledBorder("Threshold"));
        borderedPane.add(slider, BorderLayout.NORTH);

        add(borderedPane, BorderLayout.SOUTH);
    }

    public final void stateChanged(ChangeEvent e) {
        JSlider slider = (JSlider) e.getSource();
        high[0] = (double) slider.getValue();

        ParameterBlock pb = new ParameterBlock();
        pb.addSource(source);
        pb.add(low);
        pb.add(high);
        pb.add(map);
        target = JAI.create("threshold", pb, null);
        display.set(target);
    }
}
