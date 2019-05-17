/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 *
 * This Example Content is intended to demonstrate usage of Eclipse technology. It is
 * provided to you under the terms and conditions of the Eclipse Distribution License
 * v1.0 which is available at http://www.eclipse.org/org/documents/edl-v10.php.
 */
package org.eclipse.imagen.tutorial;

import java.io.File;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Color;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.border.*;
import org.eclipse.imagen.JAI;
import org.eclipse.imagen.PlanarImage;
import org.eclipse.imagen.widgets.*;

public class About extends JPanel {

    private PlanarImage source = null;

    public About(String filename) {
        File f = new File(filename);

        if ( f.exists() && f.canRead() ) {
            source = JAI.create("fileload", filename);
        } else {
            return;
        }

        ImageDisplay canvas = new ImageDisplay(source);
        canvas.setBackground(Color.blue);

        canvas.setBorder(new CompoundBorder(
                            new EtchedBorder(),
                            new LineBorder(Color.gray, 20)
                        ) );

        Font font = new Font("SansSerif", Font.BOLD, 12);
        JLabel title = new JLabel(" Use the mouse to position magnifier.");
        title.setFont(font);
        title.setLocation(0, 32);

        setOpaque(true);
        setLayout(new BorderLayout());
        setBackground(Color.white);

        add(title,  BorderLayout.NORTH);
        add(canvas, BorderLayout.CENTER);

        JLabel label = new JLabel(" Magnifier");
        label.setForeground(Color.white);

        Magnifier mag = new Magnifier();
        mag.setSource(canvas);
        mag.setMagnification(3.0F);
        mag.setSize(128, 128);
        mag.setLocation(150, 150);
        mag.setBorder(new LineBorder(Color.white,1));
        mag.setLayout(new BorderLayout());

        mag.add(label, BorderLayout.NORTH);
        canvas.add(mag);
    }
}
