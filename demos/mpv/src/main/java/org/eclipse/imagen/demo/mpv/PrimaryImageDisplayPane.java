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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;
import javax.swing.JSplitPane;
import org.eclipse.imagen.ImageLayout;
import org.eclipse.imagen.ImageN;
import org.eclipse.imagen.KernelImageN;
import org.eclipse.imagen.LookupTableImageN;
import org.eclipse.imagen.ROIShape;
import org.eclipse.imagen.RenderedOp;

/**
 * This class generates an ImageDisplayPane to contain a RenderedImage for display. The RenderedImage is wrapped as a
 * WindowOpImage. When the image in this pane is scrolled, the tiles cached in the WindowOpImage will be re-used. This
 * strategy improves display update performance.
 *
 * <p>This class also contains facilities (widgets + callbacks) to change image intensities and sharpen image displays.
 */
public class PrimaryImageDisplayPane extends ImageDisplayPane
        implements MouseListener, MouseMotionListener, PropertyChangeListener {
    public static final int MOUSEDRAG_NONE = 0;
    public static final int MOUSEDRAG_SCALE = 1;
    public static final int MOUSEDRAG_MOVE = 2;
    public static final int DRAG_THRESHOLD = 10;

    public static final int MOUSECLICK_NONE = 0;
    public static final int MOUSECLICK_RECENTER = 1;
    public static final int MOUSECLICK_REGISTER = 2;

    protected SharpenToolWidget sharpWidget = null;
    protected String sharpId;
    private static final int NO_SHARP = -10000;
    protected int sharpIndex = NO_SHARP;
    public static final int SHARP_MIN = 0;
    public static final int SHARP_MAX = 300;
    public static final int SHARP_DEFAULT = 20;
    public static final float SHARP_A0 = 0.0F;
    public static final float SHARP_A1 = 0.05F;

    public static final int TILE_SIZE = 128;

    private Point oldMousePosition;
    private Point dragPosition;

    private int dragPolicy = MOUSEDRAG_NONE;
    private int clickPolicy = MOUSECLICK_RECENTER;

    private boolean dragging = false;
    protected MappingToolWidget mtw;
    private static final int NO_WIN_LEVEL = -10000;
    private int xTop = NO_WIN_LEVEL;
    private int xBot = NO_WIN_LEVEL;
    private int ySide = NO_WIN_LEVEL;

    private int savedWidth = -1;
    private int savedHeight = -1;
    private Point savedOrigin = new Point();

    private int maxIntens = NO_WIN_LEVEL;

    protected PropertyChangeListener boxMoverListener = null;
    private double boxScale;
    public String boxMoverId;

    /** constructor */
    public PrimaryImageDisplayPane(String imageName) {
        super(imageName);
        setValues(imageName);
    } // PrimaryImageDisplayPane constructor

    /**
     * constructor with given image
     *
     * @param image the RenderedImage for display in this pane.
     */
    public PrimaryImageDisplayPane(RenderedImage image, String imageName) {
        super(image, imageName);
        setValues(imageName);
    } // PrimaryImageDisplayPane constructor

    /**
     * constructor with given image and policy
     *
     * @param image the RenderedImage for display in this pane.
     * @param policy the policy to move the image when this pane is resized.
     */
    public PrimaryImageDisplayPane(RenderedImage image, ResizePolicy policy, String imageName) {
        super(image, policy, imageName);
        setValues(imageName);
    } // PrimaryImageDisplayPane constructor

    /** Perform the common steps done by all the constructor variations. */
    private void setValues(String imageName) {
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.sharpWidget = new SharpenToolWidget(imageName, SHARP_MIN, SHARP_MAX, SHARP_DEFAULT, this, "STW");

        maxIntens = maxIntensity();

        // Build the win level widget for the general case
        xTop = maxIntens;
        xBot = 0;
        ySide = 127;
        this.mtw = new MappingToolWidget(imageName, 0, maxIntens, 0, 255, this, "MTW");

        renderedImage = grayscaleMapOperator(baseWindow);
        if (renderedImage != null) {
            revalidate();
            repaint();
        }
    }

    // Compute the max intensity in a range (default to 255 for byte images)
    private int maxIntensity() {

        int maxIntensity = 0;

        // Determine how many bits/pixel there are in the image
        int[] numBits = baseWindow.getSampleModel().getSampleSize();
        if (numBits[0] == 8) {
            maxIntensity = 255;
        } else { // If it's not a byte image, get a dynamic range estimate

            // Compute extrema for the current image being displayed
            RenderedOp extrema = ImageN.create(
                    "extrema",
                    new ParameterBlock()
                            .addSource(baseWindow)
                            .add(new ROIShape(baseWindow.getLoadedRectangle()))
                            .add(2) // These numbers are subsample step
                            .add(2), // sizes in pixels
                    null);

            double[] max = (double[]) extrema.getProperty("maximum");
            for (int j = 0; j < max.length; j++) if ((int) max[j] > maxIntensity) maxIntensity = (int) max[j] + 1;
        }
        return maxIntensity;
    }

    /**
     * Add a listener to get messages for window translation coordination with a possible overview window.
     *
     * @param boxScaleFactor the ratio of an overview image size to a primary view size (to maintain a panner box)
     * @param propertyId the keyword used to send <code>PropertyChangeEvent</code>s
     * @param pcl a change listener (most likely an overview window)
     */
    public void addBoxMoverListener(double boxScaleFactor, String propertyId, PropertyChangeListener pcl) {
        boxScale = boxScaleFactor;
        boxMoverListener = pcl;
        boxMoverId = propertyId;
        addPropertyChangeListener(boxMoverId, boxMoverListener);
    } // addBoxMoverListener

    /** Return the current <code>dragPolicy</code> */
    public int getMouseDragPolicy() {
        return dragPolicy;
    } // getMouseDragPolicy

    /**
     * Set the current <code>dragPolicy</code>
     *
     * @param i new drag policy value
     */
    public void setMouseDragPolicy(int i) {
        dragPolicy = i;
    } // setMouseDragPolicy

    /** Return the current <code>clickPolicy</code> */
    public int getMouseClickPolicy() {
        return clickPolicy;
    } // getMouseClickPolicy

    /**
     * Set the current <code>clickPolicy</code>
     *
     * @param i new mouse click policy value
     */
    public void setMouseClickPolicy(int i) {
        clickPolicy = i;
    } // setMouseClickPolicy

    /** Fast rounding */
    private static final int ROUND(double f) {
        return (f > 0D) ? (int) (f + 0.5D) : (int) (f - 0.5D);
    } // ROUND

    /** paint routine */
    public synchronized void paintComponent(Graphics g) {
        super.paintComponent(g);

        int newWidth = getWidth();
        int newHeight = getHeight();

        if ((savedWidth != newWidth)
                || (savedHeight != newHeight)
                || (savedOrigin.x != origin.x)
                || (savedOrigin.y != origin.y)) {

            int scaledWidth = ROUND(boxScale * newWidth);
            int scaledHeight = ROUND(boxScale * newHeight);

            int scaledX = ROUND(boxScale * origin.x);
            int scaledY = ROUND(boxScale * origin.y);

            // Fire a message to a boxMover listener
            Rectangle box = new Rectangle(new Point(scaledX, scaledY), new Dimension(scaledWidth, scaledHeight));
            firePropertyChange(boxMoverId, null, (Object) box);

            savedWidth = newWidth;
            savedHeight = newHeight;
            savedOrigin.x = origin.x;
            savedOrigin.y = origin.y;
        }

        // if drag the mouse to define a window for possible (add-on feature) scaling
        if (dragPolicy == MOUSEDRAG_SCALE && dragging) {
            g.setColor(Color.green);
            g.drawRect(
                    Math.min(oldMousePosition.x, dragPosition.x),
                    Math.min(oldMousePosition.y, dragPosition.y),
                    Math.abs(oldMousePosition.x - dragPosition.x),
                    Math.abs(oldMousePosition.y - dragPosition.y));
        }
    } // paintComponent

    public void mouseDragged(MouseEvent e) {
        dragging = true;

        if (dragPolicy == MOUSEDRAG_MOVE) {
            referenceX -= e.getX() - oldMousePosition.x;
            referenceY -= e.getY() - oldMousePosition.y;
            oldMousePosition = e.getPoint();
        } else {
            dragPosition = e.getPoint();
        }

        revalidate();
        repaint();
    } // mouseDragged

    public void mouseMoved(MouseEvent e) {}

    public void mouseClicked(MouseEvent e) {
        if (clickPolicy == MOUSECLICK_RECENTER) {
            referenceX += e.getX() - getWidth() / 2;
            referenceY += e.getY() - getHeight() / 2;
        } else if (clickPolicy == MOUSECLICK_REGISTER) {
            int quad = getQuadNum();
            if (quad == 0) {
                referenceX -= getWidth() - e.getX();
                referenceY -= getHeight() - e.getY();
            } else if (quad == 1) {
                referenceX += e.getX();
                referenceY -= getHeight() - e.getY();
            } else if (quad == 2) {
                referenceX -= getWidth() - e.getX();
                referenceY += e.getY();
            } else if (quad == 3) {
                referenceX += e.getX();
                referenceY += e.getY();
            }
        }

        revalidate();
        repaint();
    } // mouseClicked

    public void mousePressed(MouseEvent e) {
        oldMousePosition = e.getPoint();
    } // mousePressed

    public void mouseReleased(MouseEvent e) {
        if (dragging && dragPolicy == MOUSEDRAG_SCALE) {
            int mouseDx = Math.abs(oldMousePosition.x - e.getX());
            int mouseDy = Math.abs(oldMousePosition.y - e.getY());
            if ((mouseDx < DRAG_THRESHOLD) || (mouseDy < DRAG_THRESHOLD)) {
                dragging = false;
                return;
            }
            Rectangle bound = getBounds();
            Point newPosition = e.getPoint();

            Point shift = resizePolicy.shiftReference(
                    Math.min(oldMousePosition.x, newPosition.x),
                    Math.min(oldMousePosition.y, newPosition.y),
                    Math.max(oldMousePosition.x, newPosition.x),
                    Math.max(oldMousePosition.y, newPosition.y),
                    bound.width,
                    bound.height);
            referenceX += shift.x;
            referenceY += shift.y;

            oldMousePosition = newPosition;
        }
        dragging = false;

        revalidate();
        repaint();
    } // mouseReleased

    public void mouseEntered(MouseEvent e) {}

    public void mouseExited(MouseEvent e) {}

    /**
     * Compute the quadtree index of a window. For side by side this will enumerate 0, 1 (left, right), for four in a
     * quadrant arrangement this will enumerate 0, 1, 2, 3 (upper left, upper right, and lower left, and lower right).
     * Other configurations will be enumerated analagously.
     *
     * <p>Note that since this routine works bottom-up bits are set in least significant to most significant order.
     */
    private int getQuadNum() {
        int dq = 1;
        int quad = 0;
        Component child = (Component) this;
        Component parent = child.getParent();
        while (parent != null) {
            if (parent instanceof JSplitPane) {
                JSplitPane pane = (JSplitPane) parent;

                // Note that getRightComponent returns the "bottom" if splitOrientation
                // is HORIZONTAL_SPLIT
                if (pane.getRightComponent().equals(child)) {
                    quad += dq;
                }
                dq = 2 * dq;
            }
            child = parent;
            parent = parent.getParent();
        }
        return quad;
    } // getQuadNum

    /**
     * Add a convolution step to an image chain.
     *
     * @param source the input to be sharpened
     */
    private final RenderedImage sharpOperator(RenderedImage source) {

        if (source == null) {
            return null;
        }

        ImageLayout il = new ImageLayout();
        il.setTileWidth(TILE_SIZE);
        il.setTileHeight(TILE_SIZE);
        il.setTileGridXOffset(0);
        il.setTileGridYOffset(0);
        RenderingHints hints = new RenderingHints(ImageN.KEY_IMAGE_LAYOUT, il);

        // Scale the sharpIndex into a meaningful range
        float mVal = SHARP_A0 + SHARP_A1 * sharpIndex;
        float cVal = (1.0F - mVal) / 12.0F;
        float sVal = 2.0F * cVal;

        float[] kernelData = {
            cVal, sVal, cVal,
            sVal, mVal, sVal,
            cVal, sVal, cVal
        };
        KernelImageN kernel = new KernelImageN(3, 3, 1, 1, kernelData);

        ParameterBlock pb = new ParameterBlock();
        pb.addSource(source);
        pb.add(kernel);

        return ImageN.create("convolve", pb, hints);
    } // sharpOperator

    /**
     * Add an image look-up step to an image chain.
     *
     * @param source the input to be grayscale mapped
     */
    private final RenderedImage grayscaleMapOperator(WindowOpImage source) {

        if (source == null) {
            return null;
        }
        ParameterBlock pb = null;
        RenderedImage dst = null;
        SampleModel sampleModel = source.getSampleModel();
        int bands = sampleModel.getNumBands();
        int datatype = sampleModel.getDataType();

        int tableLength = 256;
        int scaledTopX = (xTop * 255 + maxIntens / 2) / maxIntens;
        int scaledBotX = (xBot * 255 + maxIntens / 2) / maxIntens;
        if (datatype == DataBuffer.TYPE_SHORT || datatype == DataBuffer.TYPE_USHORT) {
            scaledTopX = xTop;
            scaledBotX = xBot;
            tableLength = maxIntens;
        }

        ImageLayout il = new ImageLayout();
        il.setTileWidth(TILE_SIZE);
        il.setTileHeight(TILE_SIZE);
        il.setTileGridXOffset(0);
        il.setTileGridYOffset(0);
        RenderingHints hints = new RenderingHints(ImageN.KEY_IMAGE_LAYOUT, il);

        if (datatype >= DataBuffer.TYPE_BYTE && datatype < DataBuffer.TYPE_INT) {
            int[] map = computeByteMap(scaledBotX, scaledTopX, ySide, tableLength);

            byte[] lut = new byte[tableLength];
            byte bValue;
            for (int i = 0; i < tableLength; i++) {
                int value = map[i];
                if (datatype == DataBuffer.TYPE_USHORT) {
                    value &= 0xFF;
                }
                bValue = (byte) value;
                for (int j = 0; j < bands; j++) {
                    lut[i] = bValue;
                }
            }
            LookupTableImageN lookup = new LookupTableImageN(lut);
            pb = new ParameterBlock();
            pb.addSource(source);
            pb.add(lookup);
            dst = ImageN.create("lookup", pb, hints);
        } else if (datatype == DataBuffer.TYPE_INT
                || datatype == DataBuffer.TYPE_FLOAT
                || datatype == DataBuffer.TYPE_DOUBLE) {
            pb = new ParameterBlock();
            pb.addSource(source);
            double slope = (xTop != xBot) ? (256D / (xTop - xBot)) : 0D;
            double yInt = (xTop != xBot) ? (256D - slope * xTop) : 0D;
            pb.add(slope);
            pb.add(yInt);
            dst = ImageN.create("rescale", pb, null);

            // produce a byte image
            pb = new ParameterBlock();
            pb.addSource(dst);
            pb.add(DataBuffer.TYPE_BYTE);
            dst = ImageN.create("format", pb, null);
        }
        return dst;
    } // grayscaleMapOperator

    /**
     * computeByteMap
     *
     * <p>Computes a piecewise linear map lookup table that corresponds to the functionality of the MappingToolWidget.
     *
     * @param xBot slider location at the bottom of the widget
     * @param xTop slider location at the top of the widget
     * @param ySide slider location at the side of the widget
     * @param xMax the maximum mapped value
     */
    private int[] computeByteMap(int xBot, int xTop, int ySide, int xMax) {
        int v; // Mapping value index
        int yMin = 0;
        int yMax = 255;

        if (xMax < 0) return null;
        int[] map = new int[xMax];

        if (xTop >= xBot) { // Normal case

            int width = xTop - xBot;
            int ht = yMax - yMin;
            int hY1 = ySide - yMin;
            int hY2 = yMax - ySide;
            int xInt = (width * hY2 + ht / 2) / ht;
            int hX1 = xInt - xBot;
            int hX2 = xTop - xInt;
            for (v = 0; v < xBot; v++) map[v] = yMin;
            for (v = xBot; v < xInt; v++) map[v] = ((v - xBot) * hY1 + hX1 / 2) / hX1 + yMin;
            for (v = xInt; v < xTop; v++) map[v] = ((v - xInt) * hY2 + hX2 / 2) / hX2 + ySide;
            for (v = xTop; v < xMax; v++) map[v] = yMax;

        } else { // Inverted case

            int width = xBot - xTop;
            int ht = yMax - yMin;
            int hY1 = yMax - ySide;
            int hY2 = ySide - yMin;
            int xInt = (width * hY2 + ht / 2) / ht + xTop;
            int hX1 = xInt - xTop;
            int hX2 = xBot - xInt;
            for (v = 0; v < xTop; v++) map[v] = yMax;
            for (v = xTop; v < xInt; v++) map[v] = yMax - ((v - xTop) * hY1 + hX1 / 2) / hX1;
            for (v = xInt; v < xBot; v++) map[v] = ySide - ((v - xInt) * hY2 + hX2 / 2) / hX2;
            for (v = xBot; v < xMax; v++) map[v] = yMin;
        }

        return map;
    } // computeByteMap

    /**
     * Compute a rendered image that contains -- null, if there is no source image -- both a grayscale mapping and image
     * sharpening convolution if the user has selected a window leveling operation and a sharpening operation -- one of
     * a grayscale mapping or an image sharpening convolution if the user has selected either operation -- otherwise,
     * the renderedImage is returned.
     *
     * @param source the <code>WindowOpImage</code> that contains a reservoir of tiles.
     */
    private RenderedImage sharpWinlevelOperator(WindowOpImage source) {

        if (source == null) return null;

        if ((xTop == NO_WIN_LEVEL) && (sharpIndex == NO_SHARP)) {
            return renderedImage;
        }
        if (xTop == NO_WIN_LEVEL) {
            return sharpOperator((RenderedImage) source);
        }
        if (sharpIndex == NO_SHARP) {
            return grayscaleMapOperator(source);
        } else {
            return sharpOperator(grayscaleMapOperator(source));
        }
    } // sharpWinLevel()

    /**
     * Set a new image in a display pane
     *
     * @param ri a new source image
     * @param imagePath the system path to an image
     * @see ImageDisplayPane
     */
    public void setNewImage(RenderedImage ri, String imagePath) {
        super.set(ri, imagePath);

        maxIntens = maxIntensity();

        // Build the win level widget for the new case
        xTop = maxIntens;
        xBot = 0;
        ySide = 127;
        this.mtw = new MappingToolWidget(imagePath, 0, maxIntens, 0, 255, this, "MTW");

        renderedImage = grayscaleMapOperator(baseWindow);
        if (renderedImage != null) {
            revalidate();
            repaint();
        }
    }

    /**
     * Pick up property events from either the window leveling widget (mtw), box movement (on the overview), or
     * sharpness widget
     *
     * @param event <code>PropertyChangeEvent</code>s from widgets registered to this class.
     */
    public void propertyChange(PropertyChangeEvent event) {
        if (event.getSource() == mtw) {
            if (event.getNewValue() instanceof Vector) {
                Vector v = (Vector) (event.getNewValue());
                xTop = ((Integer) (v.elementAt(0))).intValue();
                ySide = ((Integer) (v.elementAt(1))).intValue();
                xBot = ((Integer) (v.elementAt(2))).intValue();
                renderedImage = sharpWinlevelOperator(baseWindow);
                revalidate();
                repaint();
            }
        }

        // Box mover event
        if (event.getSource() == boxMoverListener) {

            // Unpack event values
            if (event.getNewValue() instanceof Rectangle) {
                Rectangle rect = (Rectangle) event.getNewValue();
                int x = (int) ((double) rect.x / boxScale + 0.5);
                int y = (int) ((double) rect.y / boxScale + 0.5);
                Point reference = resizePolicy.computeReferenceFromOrigin(
                        (int) (x + 0.5), (int) (y + 0.5), getWidth(), getHeight());
                referenceX = reference.x;
                referenceY = reference.y;

                revalidate();
                repaint();
            }
        }

        // sharpening event
        if (event.getSource() == sharpWidget) {

            // Unpack event values
            if (event.getNewValue() instanceof Integer) {
                sharpIndex = ((Integer) event.getNewValue()).intValue();
                renderedImage = sharpWinlevelOperator(baseWindow);
                revalidate();
                repaint();
            }
        }
    } // propertyChanged
} // PrimaryImageDisplayPane
