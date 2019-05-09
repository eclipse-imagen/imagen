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
package org.eclipse.imagen.tutorial.network;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.renderable.ParameterBlock;
import java.util.Hashtable;
import java.util.Vector;
import org.eclipse.imagen.*;
import org.eclipse.imagen.remote.*;
import javax.swing.*;
import javax.swing.event.*;

public class JAIRotatePanel extends JAIDemoPanel
    implements ChangeListener, ItemListener {

    float theta = 0.0F;
    Interpolation interp;

    JSlider slider;
    JRadioButton nearest;
    JRadioButton linear;
    JRadioButton cubic;

    public JAIRotatePanel(Vector sourceVec, RemoteJAI pClient) {
        super(sourceVec, pClient);
        masterSetup();
    }

    public String getDemoName() {
        return "Rotate";
    }

    public void makeControls(JPanel controls) {
        slider = new JSlider(JSlider.HORIZONTAL, -180, 180, 0);

        Hashtable labels = new Hashtable();
        slider.setMajorTickSpacing(45);
        slider.setMinorTickSpacing(5);
        slider.setSnapToTicks(false);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        
        slider.addChangeListener(this);

        JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.X_AXIS));
        sliderPanel.add(new JLabel("Angle"));
        sliderPanel.add(slider);

        nearest = new JRadioButton("Nearest Neighbor", true);
        linear = new JRadioButton("Bilinear", false);
        cubic = new JRadioButton("Bicubic", false);
        ButtonGroup bgroup = new ButtonGroup();
        bgroup.add(nearest);
        bgroup.add(linear);
        bgroup.add(cubic);

        nearest.addItemListener(this);
        linear.addItemListener(this);
        cubic.addItemListener(this);

        JPanel interpPanel = new JPanel();
        interpPanel.setLayout(new BoxLayout(interpPanel, BoxLayout.Y_AXIS));
        interpPanel.add(nearest);
        interpPanel.add(linear);
        interpPanel.add(cubic);

        controls.setLayout(new BorderLayout());
        controls.add("Center", sliderPanel);
        controls.add("East", interpPanel);
    }

    public boolean supportsAutomatic() {
        return true;
    }

    public PlanarImage process() {
        PlanarImage im = getSource(0);

        ParameterBlock pb = new ParameterBlock();
        pb.addSource(im);
        pb.add(0.0F);
        pb.add(0.0F);
        pb.add(theta);
        if (interp == null) {
            interp = Interpolation.getInstance(Interpolation.INTERP_NEAREST);
        }
        pb.add(interp);
        return client.create("Rotate", pb, renderHints);
    }

    public void startAnimation() {
    }

    int sliderDelta = 5;

    public void animate() {
        int value = slider.getValue();
        int newValue = value + sliderDelta;

        if (newValue < slider.getMinimum() ||
            newValue > slider.getMaximum()) {
            sliderDelta = -sliderDelta;
        }
        slider.setValue(value + sliderDelta);
    }

    public void reset() {
        theta = 0.0F;
        nearest.setSelected(true);
        interp = Interpolation.getInstance(Interpolation.INTERP_NEAREST);
        slider.setValue(0);
    }

    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider)e.getSource();
        if (source.getValueIsAdjusting()) {
            return;
        }
        int value = slider.getValue();

        theta = (float)(value*(Math.PI/180.0F));
        repaint();
    }

    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.DESELECTED) {
            return;
        }

        if (e.getSource() == nearest) {
            interp =
                Interpolation.getInstance(Interpolation.INTERP_NEAREST);
        } else if (e.getSource() == linear) {
            interp =
                Interpolation.getInstance(Interpolation.INTERP_BILINEAR);
        } else {
            interp =
                Interpolation.getInstance(Interpolation.INTERP_BICUBIC);
        }
        repaint();
    }
}
