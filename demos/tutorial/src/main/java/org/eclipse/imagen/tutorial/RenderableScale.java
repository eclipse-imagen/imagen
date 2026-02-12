/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 *
 * This Example Content is intended to demonstrate usage of Eclipse technology. It is
 * provided to you under the terms and conditions of the Eclipse Distribution License
 * v1.0 which is available at http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */
package org.eclipse.imagen.tutorial;

import java.awt.*;
import java.awt.image.*;
import java.awt.image.renderable.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import org.eclipse.imagen.*;
import org.eclipse.imagen.widgets.*;

public class RenderableScale extends JPanel implements ChangeListener {

    private RenderableDisplay canvas = null;
    private final int width = 185;
    private final int height = 173;

    public RenderableScale(String base) {
        /* TODO MultiResolutionRenderableImage has been moved to unsupported.
        Vector sources = new Vector();

        for (int i = 3; i >= 0; i--) {
            int ext = i + 1;
            String file = base + ext + ".jpg";
            RenderedImage im = ImageN.create("fileload", file);
            sources.addElement(im);
        }

        RenderableImage r = new MultiResolutionRenderableImage(sources, 0.0F, 0.0F, 1.0F);

        setLayout(new BorderLayout());

        JLabel label = new JLabel("Scalable Image");
        add(label, BorderLayout.NORTH);

        canvas = new RenderableDisplay(r);
        add(canvas, BorderLayout.CENTER);
        setVisible(true);

        JSlider slider = new JSlider(JSlider.HORIZONTAL, 1, 30, 30);
        slider.addChangeListener(this);

        JPanel borderedPanel = new JPanel();
        borderedPanel.setLayout(new BorderLayout());
        borderedPanel.setBorder(BorderFactory.createTitledBorder("Zoom"));
        borderedPanel.add(slider, BorderLayout.NORTH);

        add(borderedPanel, BorderLayout.SOUTH);

        canvas.setSize(new Dimension(18, 17));
         */
    }

    public final void stateChanged(ChangeEvent e) {
        JSlider slider = (JSlider) e.getSource();
        int scale = slider.getValue();

        int nw = (scale * width) / 10;
        int nh = (scale * height) / 10;

        canvas.setSize(new Dimension(nw, nh));
    }
}
