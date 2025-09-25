/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 *
 * This Example Content is intended to demonstrate usage of Eclipse technology. It is
 * provided to you under the terms and conditions of the Eclipse Distribution License
 * v1.0 which is available at http://www.eclipse.org/org/documents/edl-v10.php.
 */
package org.eclipse.imagen.demo;

import java.awt.event.*;
import java.awt.image.renderable.ParameterBlock;
import java.util.Vector;
import javax.swing.*;
import org.eclipse.imagen.*;
import org.eclipse.imagen.operator.*;

public class JAIMedianPanel extends JAIDemoPanel implements ItemListener {

    JComboBox box;
    int type = -1;
    String[] labels = {
        "Original Image",
        "3x3 Square",
        "3x3 Separable square",
        "3x3 Plus",
        "3x3 X",
        "5x5 Square",
        "5x5 Separable square",
        "5x5 Plus",
        "5x5 X",
        "7x7 Square",
        "7x7 Separable square",
        "7x7 Plus",
        "7x7 X",
        "9x9 Square",
        "9x9 Separable square",
        "9x9 Plus",
        "9x9 X",
    };

    public JAIMedianPanel(Vector sourceVec) {
        super(sourceVec);
        masterSetup();
    }

    public String getDemoName() {
        return "Median";
    }

    public void makeControls(JPanel controls) {
        box = new JComboBox();
        for (int i = 0; i < labels.length; i++) {
            box.addItem(labels[i]);
        }

        box.addItemListener(this);
        controls.add(box);
    }

    private static MedianFilterShape[] medianShapes = {
        MedianFilterDescriptor.MEDIAN_MASK_SQUARE,
        MedianFilterDescriptor.MEDIAN_MASK_SQUARE_SEPARABLE,
        MedianFilterDescriptor.MEDIAN_MASK_PLUS,
        MedianFilterDescriptor.MEDIAN_MASK_X
    };

    public boolean supportsAutomatic() {
        return true;
    }

    public PlanarImage process() {
        PlanarImage im = getSource(0);

        if (type == -1) {
            return im;
        } else {
            int size = 2 * (type / 4) + 3;
            MedianFilterShape shape = medianShapes[type % 4];

            // Median operation
            ParameterBlock pb = new ParameterBlock();
            pb.addSource(im);
            pb.add(shape);
            pb.add(size);
            return ImageN.create("medianfilter", pb, renderHints);
        }
    }

    public void startAnimation() {}

    public void animate() {
        try {
            int current = box.getSelectedIndex() + 1;

            if (current >= labels.length) {
                current = 0;
            }

            box.setSelectedIndex(current);
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
    }

    public void reset() {
        box.setSelectedIndex(0);
        type = -1;
    }

    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.DESELECTED) {
            return;
        }

        for (int i = 0; i < labels.length; i++) {
            if (e.getItem().equals(labels[i])) {
                type = i - 1;
                break;
            }
        }

        repaint();
    }
}
