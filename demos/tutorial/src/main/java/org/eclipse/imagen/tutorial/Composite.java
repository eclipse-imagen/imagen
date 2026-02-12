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
import java.awt.image.renderable.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import org.eclipse.imagen.*;
import org.eclipse.imagen.operator.*;
import org.eclipse.imagen.widgets.*;

public class Composite extends JPanel implements ChangeListener {

    JSlider slider1;
    JSlider slider2;
    PlanarImage src1 = null;
    PlanarImage src2 = null;
    ImageDisplay ic1 = null;
    ImageDisplay ic2 = null;
    ImageDisplay ic3 = null;
    ImageDisplay ic4 = null;
    ImageDisplay ic5 = null;

    public Composite(String im1, String im2) {
        super(true);
        setLayout(new BorderLayout());

        JPanel imagePanel = new JPanel();
        imagePanel.setLayout(new GridLayout(3, 2, 1, 30));

        JPanel alpha1Panel = new JPanel();
        JPanel alpha2Panel = new JPanel();
        alpha1Panel.setLayout(new BorderLayout());
        alpha2Panel.setLayout(new BorderLayout());

        // slider used to adjust alpha1
        slider1 = new JSlider(JSlider.HORIZONTAL, 0, 255, 0);
        Hashtable labels = new Hashtable();
        labels.put(new Integer(0), new JLabel("0"));
        labels.put(new Integer(127), new JLabel("127"));
        labels.put(new Integer(255), new JLabel("255"));
        slider1.setLabelTable(labels);
        slider1.setPaintLabels(true);
        slider1.addChangeListener(this);

        JPanel borderedPanel1 = new JPanel();
        borderedPanel1.setLayout(new BorderLayout());

        borderedPanel1.setBorder(BorderFactory.createTitledBorder("Alpha1 Image"));

        borderedPanel1.add(slider1, BorderLayout.NORTH);

        // slider used to adjust alpha2
        slider2 = new JSlider(JSlider.HORIZONTAL, 0, 255, 255);
        slider2.setLabelTable(labels);
        slider2.setPaintLabels(true);
        slider2.addChangeListener(this);

        JPanel borderedPanel2 = new JPanel();
        borderedPanel2.setLayout(new BorderLayout());

        borderedPanel2.setBorder(BorderFactory.createTitledBorder("Alpha2 Image"));

        borderedPanel2.add(slider2, BorderLayout.NORTH);

        // load files
        src1 = ReadImage.getImage(im1);
        src2 = ReadImage.getImage(im2);

        Byte[] bandValues = new Byte[3];
        Byte alpha1 = new Byte((byte) 0);
        Byte alpha2 = new Byte((byte) 0);
        Byte alpha3 = new Byte((byte) 0);

        bandValues[0] = alpha1;
        bandValues[1] = alpha2;
        bandValues[2] = alpha3;
        ParameterBlock pb = new ParameterBlock();
        pb.add((float) src1.getWidth());
        pb.add((float) src1.getHeight());
        pb.add(bandValues);
        PlanarImage afa1 = ImageN.create("constant", pb, null);

        bandValues = new Byte[3];
        alpha1 = new Byte((byte) 255);
        alpha2 = new Byte((byte) 255);
        alpha3 = new Byte((byte) 255);

        bandValues[0] = alpha1;
        bandValues[1] = alpha2;
        bandValues[2] = alpha3;
        pb = new ParameterBlock();
        pb.add((float) src2.getWidth());
        pb.add((float) src2.getHeight());
        pb.add(bandValues);
        PlanarImage afa2 = ImageN.create("constant", pb, null);

        ic1 = new ImageDisplay(afa1);
        ic2 = new ImageDisplay(afa2);
        ic3 = new ImageDisplay(src1);
        ic4 = new ImageDisplay(src2);

        ic1.setBorder(new LineBorder(Color.blue, 1));
        ic2.setBorder(new LineBorder(Color.blue, 1));

        alpha1Panel.add(ic1, BorderLayout.CENTER);
        alpha1Panel.add(borderedPanel1, BorderLayout.SOUTH);
        alpha2Panel.add(ic2, BorderLayout.CENTER);
        alpha2Panel.add(borderedPanel2, BorderLayout.SOUTH);

        imagePanel.add(ic3);
        imagePanel.add(ic4);
        imagePanel.add(alpha1Panel);
        imagePanel.add(alpha2Panel);

        pb = new ParameterBlock();
        pb.addSource(src1);
        pb.addSource(src2);
        pb.add(afa1);
        pb.add(afa2);
        pb.add(new Boolean(false));
        pb.add(CompositeDescriptor.NO_DESTINATION_ALPHA);

        RenderedOp tmp = ImageN.create("composite", pb, null);

        /* if DESTINATION_ALPHA_LAST, convert to 3 band */
        /*
                int[] bandIndices = new int[3];
                bandIndices[0] = 0;
                bandIndices[1] = 1;
                bandIndices[2] = 2;
                pb = new ParameterBlock();
                pb.addSource(tmp);
                pb.add(bandIndices);

                RenderedOp dst = ImageN.create("bandselect", pb, null);

                ic5 = new ImageDisplay(dst);
        */
        ic5 = new ImageDisplay(tmp);
        ic5.setBackground(new Color(255, 0, 0));

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(new JLabel("Composite Image"), BorderLayout.SOUTH);
        panel.add(ic5, BorderLayout.CENTER);

        imagePanel.add(panel);

        add(new JLabel("Source Images for Compositing"), BorderLayout.NORTH);
        add(imagePanel, BorderLayout.CENTER);
    }

    public final void setBlend(int b1, int b2) {
        Byte[] bandValues = new Byte[3];
        Byte alpha1 = new Byte((byte) b1);
        Byte alpha2 = new Byte((byte) b1);
        Byte alpha3 = new Byte((byte) b1);

        // first alpha channel
        bandValues[0] = alpha1;
        bandValues[1] = alpha2;
        bandValues[2] = alpha3;
        ParameterBlock pb = new ParameterBlock();
        pb.add((float) src1.getWidth());
        pb.add((float) src1.getHeight());
        pb.add(bandValues);
        PlanarImage afa1 = (PlanarImage) ImageN.create("constant", pb, null);
        ic1.set(afa1);

        // second alpha channel
        bandValues = new Byte[3];
        alpha1 = new Byte((byte) b2);
        alpha2 = new Byte((byte) b2);
        alpha3 = new Byte((byte) b2);

        bandValues[0] = alpha1;
        bandValues[1] = alpha2;
        bandValues[2] = alpha3;
        pb = new ParameterBlock();
        pb.add((float) src2.getWidth());
        pb.add((float) src2.getHeight());
        pb.add(bandValues);
        PlanarImage afa2 = (PlanarImage) ImageN.create("constant", pb, null);
        ic2.set(afa2);

        pb = new ParameterBlock();
        pb.addSource(src1);
        pb.addSource(src2);
        pb.add(afa1);
        pb.add(afa2);
        pb.add(new Boolean(false));
        pb.add(CompositeDescriptor.NO_DESTINATION_ALPHA);

        RenderedOp tmp = ImageN.create("composite", pb, null);

        /* if DESTINATION_ALPHA_LAST, convert to 3band */
        /*
                int[] bandIndices = new int[3];
                bandIndices[0] = 0;
                bandIndices[1] = 1;
                bandIndices[2] = 2;
                pb = new ParameterBlock();
                pb.addSource(tmp);
                pb.add(bandIndices);

                RenderedOp op  = ImageN.create("bandselect", pb, null);

                ic5.set(op);
        */
        ic5.set(tmp);
    }

    public final void stateChanged(ChangeEvent e) {
        // JSlider slider = (JSlider)e.getSource();

        int blend1 = slider1.getValue();
        int blend2 = slider2.getValue();

        setBlend(blend1, blend2);
    }

    private final byte clampByte(int v) {
        if (v > 255) {
            return (byte) 255;
        } else if (v < 0) {
            return (byte) 0;
        } else {
            return (byte) v;
        }
    }
}
