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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.eclipse.imagen.ImageN;
import org.eclipse.imagen.PlanarImage;
import org.eclipse.imagen.widgets.*;

public class HelloWorld extends JPanel {

    private PlanarImage source = null;

    public HelloWorld(String filename) {
        File f = new File(filename);

        if (f.exists() && f.canRead()) {
            source = ImageN.create("fileload", filename);
        } else {
            return;
        }

        ImageDisplay canvas = new ImageDisplay(source);

        Font font = new Font("SansSerif", Font.BOLD, 24);
        JLabel title = new JLabel(" Hello World");
        title.setFont(font);
        title.setLocation(0, 32);

        setOpaque(true);
        setLayout(new BorderLayout());
        setBackground(Color.white);

        add(title, BorderLayout.NORTH);
        add(canvas, BorderLayout.CENTER);
        canvas.setLocation(0, 42);
    }
}
