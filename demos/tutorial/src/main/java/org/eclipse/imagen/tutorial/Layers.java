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
import java.awt.image.*;
import java.awt.image.renderable.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import org.eclipse.imagen.*;
import org.eclipse.imagen.widgets.*;

public class Layers extends JPanel implements ActionListener, ChangeListener {

    private Collection collection;
    private PlanarImage result = null;
    private ImageDisplay src_display = null;
    private ImageDisplay dst_display = null;
    private JButton[] btns;
    private JSlider[] sliders;
    private Vector sources; // PlanarImages (readonly)
    private Vector targets; // scaled by sliders
    private Vector rasters; // editable
    private double[][] constants;
    private int active_layer;
    private int[] rgb;

    public Layers(String filename1, String filename2, String filename3, String filename4) {

        ParameterBlock pb;

        setLayout(new GridLayout(2, 1, 5, 5));
        setBorder(new EmptyBorder(2, 2, 2, 2));

        String[] file = new String[4];

        file[0] = new String(filename1);
        file[1] = new String(filename2);
        file[2] = new String(filename3);
        file[3] = new String(filename4);

        JPanel layers = new JPanel();
        layers.setLayout(new BorderLayout());

        JPanel top = new JPanel();
        top.setLayout(new FlowLayout(FlowLayout.LEFT));

        top.add(new JLabel("Layers:"));

        sliders = new JSlider[4];

        btns = new JButton[4];
        sources = new Vector(4);
        targets = new Vector(4);
        rasters = new Vector(4);

        // percent contribution to composite
        constants = new double[4][3];

        for (int i = 0; i < 4; i++) {
            btns[i] = new JButton("" + i);
            btns[i].addActionListener(this);
            top.add(btns[i]);

            PlanarImage im = JAI.create("fileload", file[i]);
            sources.addElement(im);

            rasters.add(((PlanarImage) im).getAsBufferedImage().getRaster());

            if (i == 0) {
                src_display = new ImageDisplay(im);
                src_display.addMouseListener(new paintClicker());
                src_display.addMouseMotionListener(new paintMotion());
            }

            // targets are initially empty
            constants[i][0] = 0.0;
            constants[i][1] = 0.0;
            constants[i][2] = 0.0;

            pb = new ParameterBlock();
            pb.addSource(im);
            pb.add(constants[i]);
            targets.addElement(JAI.create("multiplyconst", pb, null));

            sliders[i] = new JSlider(JSlider.HORIZONTAL, 0, 10, 0);
            sliders[i].setPreferredSize(new Dimension(70, 20));
            sliders[i].addChangeListener(this);
        }

        pb = new ParameterBlock();
        pb.addSource(targets);

        result = JAI.create("addcollection", pb, null);

        JPanel dest = new JPanel();
        dest.setLayout(new BorderLayout());
        dst_display = new ImageDisplay(result);

        JPanel control = new JPanel();
        control.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 8));
        control.add(new JLabel("%:"));
        control.add(sliders[0]);
        control.add(sliders[1]);
        control.add(sliders[2]);
        control.add(sliders[3]);

        dest.add(control, BorderLayout.NORTH);
        dest.add(dst_display, BorderLayout.CENTER);
        dest.add(new JLabel("Composite result."), BorderLayout.SOUTH);

        layers.add(top, BorderLayout.NORTH);
        layers.add(src_display, BorderLayout.CENTER);
        add(layers);
        add(dest);

        // white rectangle for eraser's brush
        rgb = new int[3 * 20 * 20];
        for (int i = 0; i < 3 * 20 * 20; i++) {
            rgb[i] = 255;
        }
    }

    // handles layers (layer button callback)
    public final void actionPerformed(ActionEvent e) {
        JButton b = (JButton) e.getSource();
        active_layer = Integer.parseInt(b.getText());

        ColorModel colorModel = ((PlanarImage) sources.get(active_layer)).getColorModel();
        WritableRaster raster = (WritableRaster) rasters.get(active_layer);

        BufferedImage bi = new BufferedImage(colorModel, raster, colorModel.isAlphaPremultiplied(), null);

        src_display.set(PlanarImage.wrapRenderedImage(bi));
    }

    // slider handler
    public final void stateChanged(ChangeEvent e) {
        composite();
    }

    // (not so) simple erasing method
    public final void eraser(Point p) {
        int x;
        int y;

        if (p.x - 10 < 0) {
            x = 10;
        } else if (p.x + 10 >= src_display.getWidth()) {
            x = src_display.getWidth() - 10;
        } else {
            x = p.x;
        }

        if (p.y - 10 < 0) {
            y = 10;
        } else if (p.y + 10 >= src_display.getHeight()) {
            y = src_display.getHeight() - 10;
        } else {
            y = p.y;
        }

        ColorModel colorModel = ((PlanarImage) sources.get(active_layer)).getColorModel();
        WritableRaster raster = (WritableRaster) rasters.get(active_layer);
        raster.setPixels(x - 10, y - 10, 20, 20, rgb);

        BufferedImage bi = new BufferedImage(colorModel, raster, colorModel.isAlphaPremultiplied(), null);

        src_display.set(PlanarImage.wrapRenderedImage(bi));
    }

    class paintClicker extends MouseAdapter {
        public void mousePressed(MouseEvent e) {
            int mods = e.getModifiers();
            Point p = e.getPoint();

            if ((mods & InputEvent.BUTTON1_MASK) != 0) {
                eraser(p);
            }
        }

        public void mouseReleased(MouseEvent e) {
            int mods = e.getModifiers();

            if ((mods & InputEvent.BUTTON1_MASK) != 0) {
                composite();
            }
        }
    }

    class paintMotion extends MouseMotionAdapter {
        public void mouseDragged(MouseEvent e) {
            Point p = e.getPoint();
            int mods = e.getModifiers();

            if ((mods & InputEvent.BUTTON1_MASK) != 0) {
                eraser(p);
            }
        }
    }

    public final void composite() {
        ParameterBlock pb;
        int value;
        double fac;

        fac = (double) (sliders[0].getValue() + sliders[1].getValue() + sliders[2].getValue() + sliders[3].getValue());

        if (fac <= 10.0) fac = 10.0;

        // scale each image
        for (int i = 0; i < 4; i++) {
            // same for r,g,b bands
            value = sliders[i].getValue();
            constants[i][0] = (double) value / fac;
            constants[i][1] = (double) value / fac;
            constants[i][2] = (double) value / fac;

            ColorModel colorModel = ((PlanarImage) sources.get(i)).getColorModel();
            WritableRaster raster = (WritableRaster) rasters.get(i);

            BufferedImage bi = new BufferedImage(colorModel, raster, colorModel.isAlphaPremultiplied(), null);

            pb = new ParameterBlock();
            pb.addSource(bi);
            pb.add(constants[i]);
            PlanarImage tmp = JAI.create("multiplyconst", pb, null);
            targets.set(i, tmp);
        }

        pb = new ParameterBlock();
        pb.addSource(targets);

        result = JAI.create("addcollection", pb, null);
        dst_display.set(result);
    }
}
