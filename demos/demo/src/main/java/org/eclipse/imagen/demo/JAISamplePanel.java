/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 *
 * This Example Content is intended to demonstrate usage of Eclipse technology. It is
 * provided to you under the terms and conditions of the Eclipse Distribution License
 * v1.0 which is available at http://www.eclipse.org/org/documents/edl-v10.php.
 */
package org.eclipse.imagen.demo;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.*;
import org.eclipse.imagen.*;
import org.eclipse.imagen.registry.RIFRegistry;

public class JAISamplePanel extends JAIDemoPanel implements ChangeListener, ItemListener {

    int param1 = 0;
    int param2 = 255;
    int sliderDelta = 4;

    JSlider p1Slider;
    JSlider p2Slider;

    public JAISamplePanel(Vector sourceVec) {
        super(sourceVec);

        SampleDescriptor sampleDescriptor = new SampleDescriptor();
        OperationDescriptor odesc = sampleDescriptor;
        RenderedImageFactory rif = sampleDescriptor;

        String operationName = "sample";
        String productName = "com.mycompany";
        OperationRegistry or = ImageN.getDefaultInstance().getOperationRegistry();
        or.registerDescriptor(odesc);
        RIFRegistry.register(or, operationName, productName, rif);
        masterSetup();
    }

    public String getDemoName() {
        return "Sample";
    }

    public void makeControls(JPanel controls) {
        p1Slider = new JSlider(JSlider.HORIZONTAL, 0, 255, 15);
        p2Slider = new JSlider(JSlider.HORIZONTAL, 0, 255, 240);

        Hashtable labels = new Hashtable();
        labels.put(new Integer(0), new JLabel("0"));
        labels.put(new Integer(128), new JLabel("128"));
        labels.put(new Integer(255), new JLabel("255"));
        p1Slider.setLabelTable(labels);
        p2Slider.setLabelTable(labels);
        p1Slider.setPaintLabels(true);
        p2Slider.setPaintLabels(true);

        p1Slider.addChangeListener(this);
        p2Slider.addChangeListener(this);

        JPanel p1SliderPanel = new JPanel();
        p1SliderPanel.setLayout(new BoxLayout(p1SliderPanel, BoxLayout.X_AXIS));
        JLabel p1Label = new JLabel("Lower bound");
        p1SliderPanel.add(p1Label);
        p1SliderPanel.add(p1Slider);

        JPanel p2SliderPanel = new JPanel();
        p2SliderPanel.setLayout(new BoxLayout(p2SliderPanel, BoxLayout.X_AXIS));
        JLabel p2Label = new JLabel("Upper bound");
        p2Label.setPreferredSize(p1Label.getPreferredSize());
        p2SliderPanel.add(p2Label);
        p2SliderPanel.add(p2Slider);

        JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.Y_AXIS));
        sliderPanel.add(p1SliderPanel);
        sliderPanel.add(p2SliderPanel);

        controls.setLayout(new BorderLayout());
        controls.add("Center", sliderPanel);
    }

    public boolean supportsAutomatic() {
        return true;
    }

    public PlanarImage process() {
        PlanarImage im = getSource(0);

        ParameterBlock pb = new ParameterBlock();
        pb.addSource(im);
        pb.add(param1);
        pb.add(param2);
        return ImageN.create("sample", pb, renderHints);
    }

    public void startAnimation() {}

    public void animate() {
        int value = p1Slider.getValue();
        int temp = p2Slider.getValue();
        int newValue = value + sliderDelta;

        if (newValue < p1Slider.getMinimum() || newValue >= temp) {
            sliderDelta = -sliderDelta;
        }

        p1Slider.setValue(value + sliderDelta);
    }

    public void reset() {
        param1 = 0;
        param2 = 255;
        p1Slider.setValue(param1);
        p2Slider.setValue(param2);
    }

    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();
        int value = source.getValue();

        if (source == p1Slider) {
            param1 = value;
            if (value > p2Slider.getValue()) {
                p2Slider.setValue(value);
            }
        } else {
            param2 = value;
            if (value < p1Slider.getValue()) {
                p1Slider.setValue(value);
            }
        }
        repaint();
    }

    public void itemStateChanged(ItemEvent e) {}
}
