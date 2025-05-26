/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 *
 * This Example Content is intended to demonstrate usage of Eclipse technology. It is
 * provided to you under the terms and conditions of the Eclipse Distribution License
 * v1.0 which is available at http://www.eclipse.org/org/documents/edl-v10.php.
 */
package org.eclipse.imagen.demo.medical;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;

/**
 * This class defines a pane to display a <code>RenderedImage</code>. A three-dimensional border is created to form the
 * frame for the displayed image. When this pane/image is focused, the three-dimensional border is shown as the image is
 * pressed.
 */
public class ImagePane extends JPanel implements Focusable, MedicalAppConstants {
    /** The image to be displayed */
    protected RenderedImage source = null;

    /** The image name may be displayed as the title of this pane when the border is a titled border. */
    protected String imageName;

    /**
     * The reference point coordinates in the image coodinate system. The position of this reference point relative to
     * the pane is vary with the resizing policy.
     *
     * @see ResizePolicy
     */
    protected int referenceX;

    protected int referenceY;

    /**
     * The translation between the display coordinate system and the image coordinate system. To be used to draw the
     * image and transform the coordinates back to the image coordinate system.
     */
    protected int shiftX;

    protected int shiftY;

    /** The origin of the pane in the image coordinate system. */
    protected Point origin;

    /** Two compound border to imitate a three-dimensional effect when this pane or image gains/losses focus. */
    protected Border compound;

    protected Border compound1;

    /** Indicates this pane is focused or not. */
    protected boolean isFocused = false;

    /** Indicates to display a red cross at the center of the pane or not. */
    protected boolean displayCenter = false;

    /**
     * The resizing policy for this pane. The default policy is
     *
     * @see ResizePolicy
     */
    protected ResizePolicy resizePolicy = new ResizePolicy(ResizePolicy.POLICY_CENTER);

    /**
     * The default constructor. It creates the three-dimensional border. Set the layout of this pane to NoLayout.
     *
     * @param image The RenderedImage to be displayed in this pane.
     */
    public ImagePane(String imageName) {
        super();
        this.imageName = imageName;
        Border raisedbevel = BorderFactory.createRaisedBevelBorder();
        Border loweredbevel = BorderFactory.createLoweredBevelBorder();
        compound = BorderFactory.createCompoundBorder(raisedbevel, loweredbevel);
        compound1 = BorderFactory.createCompoundBorder(loweredbevel, raisedbevel);
        setLayout(null);
    }

    /**
     * Constructor.
     *
     * <p>Call the default constructor to define the basic geometry of this pane. Define the original position of the
     * image to be displayed, the preferred size of the pane and the position of the reference point based on the <code>
     * ResizePolicy</code>, the image size and the pane size.
     *
     * @param image The RenderedImage to be displayed in this pane.
     * @param imageName The name of the provided image.
     */
    public ImagePane(RenderedImage image, String imageName) {
        this(imageName);
        source = image;
        int w = source.getWidth();
        int h = source.getHeight();
        Insets insets = compound.getBorderInsets(this);
        Dimension dim = new Dimension(w + insets.left + insets.right, h + insets.top + insets.bottom);
        setPreferredSize(dim);
        initializeReference();
    }

    /**
     * Constructor accepts a <code>ResizePolicy</code>.
     *
     * @param image The <code>RenderedImage</code> for display in this pane.
     * @param policy The policy to move the image when this pane is resized.
     * @param imageName The name of the provided image.
     */
    public ImagePane(RenderedImage image, ResizePolicy policy, String imageName) {
        this(image, imageName);
        initializeReference();
    }

    /**
     * Set a new resizing policy to this <code>ImagePane</code>.
     *
     * @param policy The new resizing policy for the display.
     */
    public void setResizePolicy(ResizePolicy policy) {
        resizePolicy = policy;
        initializeReference();
    }

    /**
     * Change the position of the reference point.
     *
     * @param x The x coordinate of the new reference in the image coordinate system.
     * @param y The y coordinate of the new reference in the image coordinate system.
     */
    public void setReference(int x, int y) {
        referenceX = x;
        referenceY = y;
        revalidate();
        repaint();
    }

    /**
     * Return the position of the reference point.
     *
     * @return Return the coordinate of the reference point in the image coordinate system.
     */
    public Point getReference() {
        return new Point(referenceX, referenceY);
    }

    /**
     * Replace the displayed image.
     *
     * @param im The new image for display.
     */
    public void set(RenderedImage im) {
        source = im;
        initializeReference();
        // Swing geometry
        int w = source.getWidth();
        int h = source.getHeight();
        Insets insets = getInsets();
        Dimension dim = new Dimension(w, h);

        setPreferredSize(dim);
        revalidate();
        repaint();
    }

    /**
     * Replace the displayed image and the name of this image.
     *
     * @param im The new image for display.
     * @param imageName The image name.
     */
    public void set(RenderedImage im, String imageName) {
        this.imageName = imageName;
        set(im);
    }

    /** Display a new image at the new reference position. */
    public void set(RenderedImage im, String imageName, int x, int y) {
        set(im, imageName);
        setReference(x, y);
    }

    /** Return the displayed image. */
    public RenderedImage getImage() {
        return source;
    }

    /**
     * Set the focus flag and process the related events. Override the method defined in the interface <code>Focusable
     * </code>.
     *
     * @see Focusable
     */
    public void setFocused(boolean b) {
        if (b == isFocused) return;
        if (source instanceof Focusable) ((Focusable) source).setFocused(b);
        isFocused = b;
        revalidate();
        repaint();
    }

    /**
     * Return the focus status. Override the method defined in the interface <code>Focusable</code> .
     *
     * @see Focusable
     */
    public boolean isFocused() {
        return isFocused;
    }

    /** Enable/disable to display the red cross at the center of this pane. */
    public void setDisplayCenter(boolean b) {
        displayCenter = b;
    }

    /**
     * The paint procedure: paint the border based on the focus status, draw the image, and draw the red cross if the
     * flag is set.
     */
    public synchronized void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        if (isFocused) setBorder(compound1);
        else setBorder(compound);

        Insets insets = getBorder().getBorderInsets(this);
        g2d.setColor(Color.black);
        g2d.fillRect(insets.left, insets.top, getWidth() - insets.right, getHeight() - insets.bottom);

        // empty component (no image)
        if (source == null) {
            g2d.setColor(getBackground());
            g2d.fillRect(0, 0, getWidth(), getHeight());
            return;
        }
        origin = resizePolicy.computeOrigin(referenceX, referenceY, getWidth(), getHeight());

        // account for borders
        shiftX = insets.left - origin.x - source.getMinX();
        shiftY = insets.top - origin.y - source.getMinY();

        AffineTransform transform = new AffineTransform();
        transform.translate(shiftX, shiftY);
        /** Translation moves the entire image within the container */
        try {
            g2d.drawRenderedImage(source, transform);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }

        if (displayCenter) {
            // draw a cross at the center
            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;

            g.setColor(Color.red);
            g.drawLine(centerX - 5, centerY, centerX + 5, centerY);
            g.drawLine(centerX, centerY - 5, centerX, centerY + 5);
        }
    }

    /** Define the reference point based on the image and panel size. */
    private void initializeReference() {
        Point reference = resizePolicy.computeReference(
                source.getWidth(), source.getHeight(),
                getWidth(), getHeight());
        referenceX = reference.x;
        referenceY = reference.y;

        origin = resizePolicy.computeOrigin(referenceX, referenceY, getWidth(), getHeight());
    }
}
