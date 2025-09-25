/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 *
 * This Example Content is intended to demonstrate usage of Eclipse technology. It is
 * provided to you under the terms and conditions of the Eclipse Distribution License
 * v1.0 which is available at http://www.eclipse.org/org/documents/edl-v10.php.
 */
package org.eclipse.imagen.widgets;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import javax.swing.*;
import org.eclipse.imagen.*;

/**
 * A class to create icons from Planar Images
 *
 * @author Dennis Sigel
 */
public class Iconizer implements Icon {

    protected int width = 64;
    protected int height = 64;
    protected BufferedImage icon = null;

    /** Default constructor */
    public Iconizer() {}

    /**
     * @param source a PlanarImage to be displayed.
     * @param width is the icon width
     * @param height is the icon height
     */
    public Iconizer(PlanarImage image, int width, int height) {
        this.width = width;
        this.height = height;

        icon = iconify(image);
    }

    public int getIconWidth() {
        return width;
    }

    public int getIconHeight() {
        return height;
    }

    /** Paint the icon */
    public synchronized void paintIcon(Component c, Graphics g, int x, int y) {

        Graphics2D g2D = null;
        if (g instanceof Graphics2D) {
            g2D = (Graphics2D) g;
        } else {
            return;
        }

        AffineTransform transform = AffineTransform.getTranslateInstance(0, 0);
        g2D.drawRenderedImage(icon, transform);
    }

    private BufferedImage iconify(PlanarImage image) {
        float scale = 1.0F;

        float s1 = (float) width / (float) image.getWidth();
        float s2 = (float) height / (float) image.getHeight();

        if (s1 > s2) {
            scale = s1;
        } else {
            scale = s2;
        }

        InterpolationBilinear interp = new InterpolationBilinear();

        PlanarImage temp = ImageN.create("scale", image, scale, scale, 0.0F, 0.0F, interp);

        return temp.getAsBufferedImage();
    }

    public void save(String filename, String format) {
        ImageN.create("filestore", icon, filename, format, null);
    }
}
