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
import javax.swing.*;
import javax.swing.border.*;
import org.eclipse.imagen.*;
import org.eclipse.imagen.widgets.*;

public class FrontPage extends JPanel implements ActionListener {

    private PlanarImage src_color = null;
    private PlanarImage src_gray = null;
    private ImageDisplay canvas = null;
    private JLabel label;
    private String flasher = null;
    private double[] constants = {10.0, 10.0, 10.0};

    public FrontPage(String file, String button_icon, String flash_icon) {

        setBackground(new Color(200, 200, 255));
        setLayout(new BorderLayout());
        setOpaque(true);

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(new Color(200, 200, 255));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton button = new JButton("Smile");
        label = new JLabel(new ImageIcon(button_icon));
        flasher = new String(flash_icon);

        src_color = JAI.create("fileload", file);
        src_gray = TutorUtils.convertColorToGray(src_color, 40);
        canvas = new ImageDisplay(src_gray);
        canvas.setBackground(Color.yellow);

        canvas.setBorder(new CompoundBorder(new EtchedBorder(), new LineBorder(new Color(100, 100, 180), 20)));

        canvas.setPreferredSize(new Dimension(src_gray.getWidth(), src_gray.getHeight()));

        // brighten the color image a bit
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(src_color);
        pb.add(constants);
        src_color = JAI.create("addconst", pb, null);

        button.addActionListener(this);
        panel.add(button);
        panel.add(label);
        add(panel, BorderLayout.NORTH);
        add(canvas, BorderLayout.CENTER);

        // center the image (quick and dirty)
        canvas.setLocation(30, 50);
    }

    public void actionPerformed(ActionEvent e) {
        JButton b = (JButton) e.getSource();
        SampleModel sm = src_gray.getSampleModel();
        double[] pix = new double[sm.getNumBands()];
        Raster srcras = src_color.getData();
        int width = src_color.getWidth();
        int height = src_color.getHeight();
        Graphics g = getGraphics();
        Point origin = canvas.getLocation();
        Insets insets = canvas.getInsets();

        label.setIcon(new ImageIcon(flasher));

        // intentionally slow
        // red rose
        /*
                for ( int i = 0; i < height; i++ ) {
                    for ( int j = 0; j < width; j++ ) {
                        srcras.getPixel(j, height-i-1, pix);

                        g.setColor(new Color((int)pix[0],
                                             (int)pix[1],
                                             (int)pix[2]));

                        g.drawLine(j+origin.x+insets.left,
                                   origin.y+height-i-1+insets.top,
                                   j+origin.x+insets.left,
                                   origin.y+height-i-1+insets.top);
                    }
                }
        */

        // synchronize graphics and image
        canvas.set(src_color);
        b.setEnabled(false);
        b.setBackground(Color.gray);
    }
}
