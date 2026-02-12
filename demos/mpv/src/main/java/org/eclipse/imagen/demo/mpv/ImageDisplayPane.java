/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 *
 * This Example Content is intended to demonstrate usage of Eclipse technology. It is
 * provided to you under the terms and conditions of the Eclipse Distribution License
 * v1.0 which is available at http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */
package org.eclipse.imagen.demo.mpv;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;

/**
 * This class generates a <code>JPanel</code> to contain a <code>RenderedImage</code> for display. The <code>
 * RenderedImage</code> is wrapped as a <code>WindowOpImage</code>. When the image moves in this panel, the tiles cached
 * in this WindowOpImage will be re-used. This strategy should improve display update performance.
 *
 * <p>The <code>JPanel</code> contains a labeled border and a graphical "+" at its center. Methods for changing the
 * label and disabling graphics are also provided.
 */
public class ImageDisplayPane extends JPanel {

    /**
     * The baseWindow contains a slightly larger region of the image than is displayed (renderedImage). The
     * renderedImage pulls data from the baseWindow rather than all the way from the source data.
     */
    protected WindowOpImage baseWindow = null;

    protected RenderedImage renderedImage = null;

    /** The image name */
    protected String imageName;

    /** image fixed point relative to the policy */
    protected int referenceX;

    protected int referenceY;

    protected Point origin;
    protected boolean drawCenterWanted = true;
    protected boolean drawBorderWanted = true;
    protected String borderTitle = null;

    /**
     * the resizing policy for this pane.
     *
     * @see ResizePolicy
     */
    protected ResizePolicy resizePolicy = new ResizePolicy(ResizePolicy.POLICY_CENTER);

    /**
     * default constructor
     *
     * @param imageName the image name to appear on the <code>borderTitle</code>.
     */
    public ImageDisplayPane(String imageName) {
        super();
        this.imageName = imageName;
        setLayout(null);
        borderTitle = imageName;
        drawBorder(); // we don't want an extra repaint (setBorderTitle)
    }

    private void initializeReference(RenderedImage image) {
        Point reference = resizePolicy.computeReference(image.getWidth(), image.getHeight(), getWidth(), getHeight());
        referenceX = reference.x;
        referenceY = reference.y;
        origin = resizePolicy.computeOrigin(referenceX, referenceY, getWidth(), getHeight());
    }

    /**
     * constructor with given image
     *
     * @param image the RenderedImage for display in this pane.
     * @param imageName the image name to appear on the <code>borderTitle</code>.
     */
    public ImageDisplayPane(RenderedImage image, String imageName) {
        this(imageName);

        initializeReference(image);
        Rectangle window = getBounds();
        window.translate(origin.x, origin.y);
        renderedImage = image;
        baseWindow = new WindowOpImage(image, window);
    } // constructor

    /**
     * constructor with given image and policy
     *
     * @param image the RenderedImage for display in this pane.
     * @param policy the policy to move the image when this pane is resized.
     * @param imageName the image name to appear on the <code>borderTitle</code>.
     */
    public ImageDisplayPane(RenderedImage image, ResizePolicy policy, String imageName) {
        this(image, imageName);
        resizePolicy = policy;
    } // constructor

    /**
     * Set a new resizing policy to this <code>ImageDisplayPane</code>.
     *
     * @param policy the new resizing policy for the display.
     */
    public void setResizePolicy(ResizePolicy policy) {
        resizePolicy = policy;
        initializeReference(baseWindow);
    }

    /**
     * Move image within it's container.
     *
     * @param x the x coordinate of the new reference in the image coordinate system.
     * @param y the y coordinate of the new reference in the image coordinate system.
     */
    public void setReference(int x, int y) {
        referenceX = x;
        referenceY = y;
        revalidate();
        repaint();
    }

    /**
     * Get the image origin.
     *
     * @return return the coordinate of the center pixel in the display window in the image coordinate system.
     */
    public Point getReference() {
        return new Point(referenceX, referenceY);
    }

    /**
     * Display a new image
     *
     * @param im the new image for display.
     */
    public void set(RenderedImage im) {

        this.borderTitle = this.imageName;
        drawBorder();
        initializeReference(im);
        Rectangle window = getBounds();
        window.translate(origin.x, origin.y);
        renderedImage = im;
        baseWindow = new WindowOpImage(im, window);

        // Swing geometry
        int w = baseWindow.getWidth();
        int h = baseWindow.getHeight();
        Insets insets = getInsets();
        Dimension dim = new Dimension(w + insets.left + insets.right, h + insets.top + insets.bottom);

        setPreferredSize(dim);
        revalidate();
        repaint();
    }

    /**
     * Display a new image
     *
     * @param im the new image for display.
     * @param imageName the image name to appear on the <code>borderTitle</code>.
     */
    public void set(RenderedImage im, String imageName) {
        this.imageName = imageName;
        set(im);
    }

    /**
     * This method is used to display a new image. The display reference coordinates are used to set the scroll
     * position.
     *
     * @param im the new image for display.
     * @param imageName the image name to appear on the <code>borderTitle</code>.
     * @param x the x coordinate of the new reference in the image coordinate system.
     * @param y the y coordinate of the new reference in the image coordinate system.
     */
    public void set(RenderedImage im, String imageName, int x, int y) {
        set(im, imageName);
        setReference(x, y);
    }

    public synchronized void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // empty component (no image)
        if (baseWindow == null) {
            g2d.setColor(getBackground());
            g2d.fillRect(0, 0, getWidth(), getHeight());
            return;
        }

        // account for borders
        Insets insets = getInsets();

        // Fetch tiles that the current window overlaps (this method is
        // defined in this class but eventually calls the baseWindow's
        // "setWindow(Rectangle)" method).
        setWindow();
        int tx = insets.left - origin.x;
        int ty = insets.top - origin.y;

        // Clear damaged component area
        Rectangle clipBounds = g2d.getClipBounds();
        g2d.setColor(getBackground());
        g2d.fillRect(clipBounds.x, clipBounds.y, clipBounds.width, clipBounds.height);

        AffineTransform transform = new AffineTransform();
        transform.translate(tx, ty);

        /** Translation moves the entire image within the container */
        try {
            g2d.drawRenderedImage(renderedImage, transform);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }

        // draw a cross at the center and the border (if enabled)
        drawCenterPlus(getWidth() / 2, getHeight() / 2, g);
    }

    protected void drawCenterPlus(int ctrX, int ctrY, Graphics g) {

        if (drawCenterWanted) {
            g.setColor(Color.red);
            g.drawLine(ctrX - 5, ctrY, ctrX + 5, ctrY);
            g.drawLine(ctrX, ctrY - 5, ctrX, ctrY + 5);
        }
    }

    protected void setWindow() {
        Rectangle window = getBounds();
        origin = resizePolicy.computeOrigin(referenceX, referenceY, getWidth(), getHeight());

        window.translate(origin.x, origin.y);
        baseWindow.setWindow(window);
    }

    /** Puts a titled border around an image (no repaint). User can set border with setBorderTitle() method. */
    protected void drawBorder() {

        if (!drawBorderWanted) {
            setBorder(BorderFactory.createEmptyBorder());
        } else {
            Border border = BorderFactory.createLineBorder(Color.red, 2);
            setBorder(BorderFactory.createTitledBorder(border, borderTitle));
        }
    }

    /**
     * Sets the border title with the user's string. If the user passes null, this is taken to mean that the user
     * doesn't want a border.
     *
     * @param title label on a border that surrounds the image display area
     */
    public void setBorderTitle(String title) {
        borderTitle = title;
        drawBorderWanted = true;
        if (borderTitle == null) drawBorderWanted = false;
        drawBorder();
        repaint();
    }

    /** Returns String borderTitle (see setBorderTitle to change the border title around an image. */
    public String getBorderTitle() {
        return borderTitle;
    }

    /**
     * Returns a boolean indicating whether the user wants to have a + drawn at the center of a display (true implies +
     * is drawn).
     *
     * @param drawCenterWanted indicates whether or not the center "+" is desired.
     */
    public boolean getCenterPlus() {
        return drawCenterWanted;
    }

    /**
     * Enable or disable a + to be drawn at the center of the displayed image (true implies + is drawn, false it is not
     * drawn).
     */
    public void setCenterPlus(boolean value) {
        drawCenterWanted = value;
        repaint();
    }

    /**
     * Toggle the value enabling a + to be drawn at the center of a displayed image (see setCenterPlus for definitions).
     */
    public void toggleCenterPlus() {
        drawCenterWanted = !drawCenterWanted;
        repaint();
    }

    /**
     * Toggle the value enabling a titled border to be drawn around a displayed image (see setBorderTitle for
     * definitions).
     */
    public void toggleBorder() {
        drawBorderWanted = !drawBorderWanted;
        drawBorder();
        repaint();
    }

    /**
     * A convenience method to toggle both the Border and + drawn on a displayed image. This is more efficient than
     * calling both toggleCenterPlus() and toggleBorder().
     */
    public void toggleBorderAndPlus() {
        drawCenterWanted = !drawCenterWanted;
        drawBorderWanted = !drawBorderWanted;
        drawBorder();
        repaint();
    }
}
