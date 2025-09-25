/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 *
 * This Example Content is intended to demonstrate usage of Eclipse technology. It is
 * provided to you under the terms and conditions of the Eclipse Distribution License
 * v1.0 which is available at http://www.eclipse.org/org/documents/edl-v10.php.
 */
package org.eclipse.imagen.tutorial;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.renderable.*;
import javax.swing.*;
import javax.swing.border.*;
import org.eclipse.imagen.*;
import org.eclipse.imagen.widgets.*;

public class Arithmetic extends JPanel implements ActionListener {

    private PlanarImage src1 = null;
    private PlanarImage src2 = null;
    private ImageDisplay ic1 = null;
    private ImageDisplay ic2 = null;
    private ImageDisplay ic3 = null;
    private JButton b1;
    private JButton b2;
    private JButton b3;
    private JButton b4;

    public Arithmetic(String im1, String im2) {
        super(true);
        setLayout(new BorderLayout());

        // load files
        src1 = ReadImage.getImage(im1);
        src2 = ReadImage.getImage(im2);

        // alter mask image a bit
        Byte[] bandValues = new Byte[3];
        bandValues[0] = new Byte((byte) 2);
        bandValues[1] = new Byte((byte) 2);
        bandValues[2] = new Byte((byte) 2);

        ParameterBlock pb = new ParameterBlock();
        pb.add((float) src2.getWidth());
        pb.add((float) src2.getHeight());
        pb.add(bandValues);
        PlanarImage temp = ImageN.create("constant", pb, null);

        pb = new ParameterBlock();
        pb.addSource(src2);
        pb.addSource(temp);

        src2 = ImageN.create("add", pb, null);

        ic1 = new ImageDisplay(src1);
        ic2 = new ImageDisplay(src2);
        ic3 = new ImageDisplay(src1.getWidth(), src1.getHeight());

        JPanel imagePanel = new JPanel();
        imagePanel.setLayout(new GridLayout(3, 1, 0, 0));
        imagePanel.add(ic1);
        imagePanel.add(ic2);
        imagePanel.add(ic3);
        add(imagePanel, BorderLayout.CENTER);

        // control panel
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 4, 4, 4));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        b1 = new JButton("Add");
        b2 = new JButton("Subtract");
        b3 = new JButton("Multiply");
        b4 = new JButton("Divide");

        b1.addActionListener(this);
        b2.addActionListener(this);
        b3.addActionListener(this);
        b4.addActionListener(this);

        panel.add(b1);
        panel.add(b2);
        panel.add(b3);
        panel.add(b4);
        add(panel, BorderLayout.SOUTH);

        // GUI spacing
        add(new JLabel("          "), BorderLayout.WEST);
        add(new JLabel("          "), BorderLayout.EAST);
    }

    public void actionPerformed(ActionEvent e) {
        PlanarImage target = null;
        JButton b = (JButton) e.getSource();
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(src1);
        pb.addSource(src2);

        if (b == b1) {
            target = ImageN.create("add", pb, null);
        } else if (b == b2) {
            target = ImageN.create("subtract", pb, null);
        } else if (b == b3) {
            target = ImageN.create("multiply", pb, null);
        } else if (b == b4) {
            target = ImageN.create("divide", pb, null);
        }

        ic3.set(target);
    }
}
