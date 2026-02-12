/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 *
 * This Example Content is intended to demonstrate usage of Eclipse technology. It is
 * provided to you under the terms and conditions of the Eclipse Distribution License
 * v1.0 which is available at http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */
package org.eclipse.imagen.demo;

import java.awt.event.*;
import java.awt.image.renderable.ParameterBlock;
import java.util.Vector;
import javax.swing.*;
import org.eclipse.imagen.*;
import org.eclipse.imagen.operator.TransposeDescriptor;

public class JAITransposePanel extends JAIDemoPanel implements ItemListener {

    JComboBox box;
    EnumeratedParameter type = null;
    String[] labels = {
        "Original Image",
        "FLIP_VERTICAL",
        "FLIP_HORIZONTAL",
        "FLIP_DIAGONAL",
        "FLIP_ANTIDIAGONAL",
        "ROTATE_90",
        "ROTATE_180",
        "ROTATE_270"
    };
    EnumeratedParameter[] transposeTypes = {
        null,
        TransposeDescriptor.FLIP_VERTICAL,
        TransposeDescriptor.FLIP_HORIZONTAL,
        TransposeDescriptor.FLIP_DIAGONAL,
        TransposeDescriptor.FLIP_ANTIDIAGONAL,
        TransposeDescriptor.ROTATE_90,
        TransposeDescriptor.ROTATE_180,
        TransposeDescriptor.ROTATE_270
    };

    public JAITransposePanel(Vector sourceVec) {
        super(sourceVec);
        masterSetup();
    }

    public String getDemoName() {
        return "Transpose";
    }

    public void makeControls(JPanel controls) {
        box = new JComboBox();
        for (int i = 0; i < labels.length; i++) {
            box.addItem(labels[i]);
        }

        box.addItemListener(this);
        controls.add(box);
    }

    public boolean supportsAutomatic() {
        return true;
    }

    public PlanarImage process() {
        PlanarImage im = getSource(0);

        if (type == null) {
            // Original image
            return im;
        } else {
            // Transpose operation
            ParameterBlock pb = new ParameterBlock();
            pb.addSource(im);
            pb.add(type);
            return ImageN.create("transpose", pb, renderHints);
        }
    }

    public void startAnimation() {}

    public void animate() {
        int current = box.getSelectedIndex() + 1;

        if (current >= labels.length) {
            current = 0;
        }

        box.setSelectedIndex(current);
    }

    public void reset() {
        box.setSelectedIndex(0);
        type = null;
    }

    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.DESELECTED) {
            return;
        }

        for (int i = 0; i < labels.length; i++) {
            if (e.getItem().equals(labels[i])) {
                type = transposeTypes[i];
                break;
            }
        }

        repaint();
    }
}
