/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 *
 * This Example Content is intended to demonstrate usage of Eclipse technology. It is
 * provided to you under the terms and conditions of the Eclipse Distribution License
 * v1.0 which is available at http://www.eclipse.org/org/documents/edl-v10.php.
 */
package org.eclipse.imagen.tutorial.network;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.renderable.ParameterBlock;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.*;
import org.eclipse.imagen.*;
import org.eclipse.imagen.remote.RemoteJAI;

public class JAIBlurSharpPanel extends JAIDemoPanel implements ChangeListener, ItemListener {

    int param1 = 150;
    int sliderDelta = 4;

    JSlider p1Slider;

    public JAIBlurSharpPanel(Vector sourceVec, RemoteJAI pClient) {
        super(sourceVec, pClient);
        masterSetup();
    }

    public String getDemoName() {
        return "BlurSharp";
    }

    public void makeControls(JPanel controls) {
        p1Slider = new JSlider(JSlider.HORIZONTAL, 0, 300, 150);

        Hashtable labels = new Hashtable();
        labels.put(new Integer(0), new JLabel("Blurrier"));
        labels.put(new Integer(150), new JLabel("Normal"));
        labels.put(new Integer(300), new JLabel("Sharper"));
        p1Slider.setLabelTable(labels);
        p1Slider.setPaintLabels(true);

        p1Slider.addChangeListener(this);

        JPanel p1SliderPanel = new JPanel();
        p1SliderPanel.setLayout(new BoxLayout(p1SliderPanel, BoxLayout.X_AXIS));
        JLabel p1Label = new JLabel("Sharpness");
        p1SliderPanel.add(p1Label);
        p1SliderPanel.add(p1Slider);

        JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.Y_AXIS));
        sliderPanel.add(p1SliderPanel);

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
        float kData[] = new float[9];

        float alpha;
        if (param1 > 150) {
            alpha = (param1 - 125.0f) / 25.0f;
        } else {
            alpha = param1 / 150.0f;
        }
        float beta = (1.0f - alpha) / 8.0f;
        for (int i = 0; i < 9; i++) {
            kData[i] = beta;
        }
        kData[4] = alpha;

        KernelImageN k = new KernelImageN(3, 3, 1, 1, kData);
        pb.add(k);
        return client.create("convolve", pb, renderHints);
    }

    public void startAnimation() {}

    public void animate() {
        int value = p1Slider.getValue();
        int newValue = value + sliderDelta;

        if (newValue < p1Slider.getMinimum() || newValue > p1Slider.getMaximum()) {
            sliderDelta = -sliderDelta;
        }
        p1Slider.setValue(value + sliderDelta);
    }

    public void reset() {
        param1 = 150;
        p1Slider.setValue(param1);
    }

    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();
        int value = source.getValue();

        if (source == p1Slider) {
            param1 = value;
        }
        repaint();
    }

    public void itemStateChanged(ItemEvent e) {}
}
