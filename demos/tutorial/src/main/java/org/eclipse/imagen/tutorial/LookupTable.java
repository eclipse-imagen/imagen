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

public class LookupTable extends JPanel implements ActionListener {

    private PlanarImage image;
    private ImageDisplay display;
    private Panner panner;
    private JButton[] btns;
    private Colorbar cbar;
    private byte lut[][];
    private byte newlut[][];
    private int brightness = 0;
    private final int num_buttons = 6;
    private final String[] labels = {"Pseudo", "InverseGray", "Red", "Brighter", "Darker", "Reset"};

    public LookupTable(String filename) {
        super(true);
        setOpaque(true);
        setBackground(Color.white);
        setLayout(new BorderLayout());

        cbar = new Colorbar(SwingConstants.HORIZONTAL);
        cbar.setLayout(new FlowLayout(FlowLayout.LEFT));
        cbar.setBackground(Color.black);
        cbar.setPreferredSize(new Dimension(256, 25));
        cbar.setBorder(new LineBorder(Color.blue, 2));
        add(cbar, BorderLayout.NORTH);

        JLabel clabel = new JLabel("Lookup Table");
        clabel.setForeground(Color.red);
        cbar.add(clabel);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(2, 2, 20, 20));
        controlPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        add(controlPanel, BorderLayout.SOUTH);

        btns = new JButton[num_buttons];

        for (int i = 0; i < num_buttons; i++) {
            btns[i] = new JButton(labels[i]);
            btns[i].addActionListener(this);
            controlPanel.add(btns[i]);
        }

        image = ReadImage.getImage(filename);
        display = new ImageDisplay(image);
        display.setLayout(new FlowLayout(FlowLayout.RIGHT, 2, 2));

        panner = new Panner(display, image, 128);
        panner.setBackground(Color.red);
        panner.setBorder(new EtchedBorder());
        display.add(panner);

        add(display, BorderLayout.CENTER);

        lut = new byte[3][256];
        newlut = new byte[3][256];

        // initialize lookup table
        for (int i = 0; i < 256; i++) {
            lut[0][i] = (byte) i;
            lut[1][i] = (byte) i;
            lut[2][i] = (byte) i;
        }
    }

    public void colorize(byte[][] lt) {
        LookupTableJAI lookup = new LookupTableJAI(lt);

        ParameterBlock pb = new ParameterBlock();
        pb.addSource(image);
        pb.add(lookup);

        PlanarImage dst = JAI.create("lookup", pb, null);
        display.set(dst);
    }

    public void actionPerformed(ActionEvent e) {
        int i;
        JButton b = (JButton) e.getSource();

        if (b == btns[0]) {
            for (i = 0; i < 256; i++) {
                lut[0][i] = (byte) (255 - i);
                lut[1][i] = (byte) i;
                lut[2][i] = (byte) (255 - i);
            }
        } else if (b == btns[1]) {
            for (i = 0; i < 256; i++) {
                lut[0][i] = (byte) (255 - i);
                lut[1][i] = (byte) (255 - i);
                lut[2][i] = (byte) (255 - i);
            }
        } else if (b == btns[2]) {
            for (i = 0; i < 256; i++) {
                lut[0][i] = (byte) i;
                lut[1][i] = (byte) 0;
                lut[2][i] = (byte) 0;
            }
        } else if (b == btns[3]) {
            brightness += 15;
            if (brightness > 255) brightness = 255;
        } else if (b == btns[4]) {
            brightness -= 15;
            if (brightness < -255) brightness = -255;
        } else if (b == btns[5]) {
            brightness = 0;

            for (i = 0; i < 256; i++) {
                lut[0][i] = (byte) i;
                lut[1][i] = (byte) i;
                lut[2][i] = (byte) i;
            }
        }

        for (i = 0; i < 256; i++) {
            int red = (int) lut[0][i] & 0xFF;
            int green = (int) lut[1][i] & 0xFF;
            int blue = (int) lut[2][i] & 0xFF;
            newlut[0][i] = clamp(red + brightness);
            newlut[1][i] = clamp(green + brightness);
            newlut[2][i] = clamp(blue + brightness);
        }

        cbar.setLut(newlut);
        colorize(newlut);
    }

    private final byte clamp(int v) {
        if (v > 255) {
            return (byte) 255;
        } else if (v < 0) {
            return (byte) 0;
        } else {
            return (byte) v;
        }
    }
}
