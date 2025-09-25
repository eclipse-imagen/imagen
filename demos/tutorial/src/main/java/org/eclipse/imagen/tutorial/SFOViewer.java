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
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import org.eclipse.imagen.*;
import org.eclipse.imagen.widgets.*;

public class SFOViewer extends JPanel implements ChangeListener, ActionListener {

    private PlanarImage src = null;
    private ImageDisplay dsp = null;
    private JFileChooser fileChooser;
    private JSlider brightness_slider;
    private Panner panner;
    private Font defaultFont = new Font("SansSerif", Font.BOLD, 14);
    private Font smallFont = new Font("SansSerif", Font.PLAIN, 10);
    private JButton b1, b2, b3, b4, b5, b6;

    public SFOViewer(String file, String thumbnail) {
        super(true);
        File f = new File(file);

        if (f.exists() && f.canRead()) {
            src = ImageN.create("fileload", file, null);
            createGUI(thumbnail);
        } else {
            System.out.println("File " + file + " not found.");
            System.exit(0);
        }
    }

    public void createGUI(String thumbnail) {
        // toplevel app window
        setLayout(new BorderLayout());

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BorderLayout());
        controlPanel.setOpaque(true);

        // build the display
        add(new JLabel("San Francisco"), BorderLayout.NORTH);
        dsp = new ImageDisplay(src);
        dsp.setOpaque(true);
        add(dsp, BorderLayout.CENTER);

        // build the panner controller
        PlanarImage thumb = ImageN.create("fileload", thumbnail);
        panner = new Panner(dsp, src, thumb);

        panner.setBorder(new CompoundBorder(new EtchedBorder(3), new LineBorder(Color.gray, 3)));

        // holds the hot buttons for auto-positioning
        JPanel hotPanel = new JPanel();
        hotPanel.setLayout(new GridLayout(3, 2, 10, 5));
        hotPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        hotPanel.setOpaque(true);
        b1 = new JButton("Transamerica");
        b2 = new JButton("Pier 39");
        b3 = new JButton("Fort Mason");
        b4 = new JButton("Coit Tower");
        b5 = new JButton("Civic Center");
        b6 = new JButton("Bay Bridge");
        b1.setFont(smallFont);
        b2.setFont(smallFont);
        b3.setFont(smallFont);
        b4.setFont(smallFont);
        b5.setFont(smallFont);
        b6.setFont(smallFont);
        hotPanel.add(b1);
        hotPanel.add(b2);
        hotPanel.add(b3);
        hotPanel.add(b4);
        hotPanel.add(b5);
        hotPanel.add(b6);

        b1.addActionListener(this);
        b2.addActionListener(this);
        b3.addActionListener(this);
        b4.addActionListener(this);
        b5.addActionListener(this);
        b6.addActionListener(this);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());

        brightness_slider = new JSlider(JSlider.HORIZONTAL, -255, 255, 0);
        Hashtable labels = new Hashtable();
        labels.put(new Integer(-255), new JLabel("-255"));
        labels.put(new Integer(0), new JLabel("0"));
        labels.put(new Integer(255), new JLabel("255"));
        brightness_slider.setLabelTable(labels);
        brightness_slider.setPaintLabels(true);
        brightness_slider.addChangeListener(this);
        brightness_slider.setEnabled(true);

        JPanel borderedPanel = new JPanel();
        borderedPanel.setLayout(new BorderLayout());
        borderedPanel.setBorder(BorderFactory.createTitledBorder("Brightness"));
        borderedPanel.add(brightness_slider, BorderLayout.NORTH);

        centerPanel.add(borderedPanel, BorderLayout.NORTH);
        centerPanel.add(hotPanel, BorderLayout.SOUTH);
        centerPanel.add(panner.getOdometer(), BorderLayout.CENTER);

        controlPanel.add(panner, BorderLayout.WEST);
        controlPanel.add(centerPanel, BorderLayout.CENTER);

        add(controlPanel, BorderLayout.SOUTH);

        // center the view
        panner.setSliderLocation(panner.getWidth() / 2, panner.getHeight() / 2);

        panner.setSliderBorderColor(Color.red);
    }

    // coordinates are from the panner object space
    public void actionPerformed(ActionEvent e) {
        JButton bt = (JButton) e.getSource();

        if (bt == b1) {
            panner.setSliderLocation(113, 71); // Transamerica
        } else if (bt == b2) {
            panner.setSliderLocation(91, 18); // Pier 39
        } else if (bt == b3) {
            panner.setSliderLocation(39, 29); // Fort Mason
        } else if (bt == b4) {
            panner.setSliderLocation(103, 45); // Coit Tower
        } else if (bt == b5) {
            panner.setSliderLocation(68, 133); // Civic Center
        } else if (bt == b6) {
            panner.setSliderLocation(162, 92); // Bay Bridge
        }
    }

    public final void stateChanged(ChangeEvent e) {
        JSlider slider = (JSlider) e.getSource();
        int brightness = slider.getValue();
        dsp.setBrightness(brightness);
    }
}
