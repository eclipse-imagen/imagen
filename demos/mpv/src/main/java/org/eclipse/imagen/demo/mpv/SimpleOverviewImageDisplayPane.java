/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 *
 * This Example Content is intended to demonstrate usage of Eclipse technology. It is
 * provided to you under the terms and conditions of the Eclipse Distribution License
 * v1.0 which is available at http://www.eclipse.org/org/documents/edl-v10.php.
 */
package org.eclipse.imagen.demo.mpv;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;
import org.eclipse.imagen.ImageLayout;
import org.eclipse.imagen.JAI;
import org.eclipse.imagen.LookupTableJAI;

/**
 * A pane to contain an overview image for display. An overview image is a higher level image on an pyramid pyramid. It
 * is associated with an rectangle ROI. This content in this ROI is displayed in a PrimaryImageDisplayPane.
 *
 * <p>This class generates a pane to contain an overview for display. The super class defines the pane for containing
 * the image, manage the resizing policy and handle the image display.
 *
 * <p>This class handles mouse events which relate to overview image manipulations.
 */
public class SimpleOverviewImageDisplayPane extends ImageDisplayPane
        implements MouseListener, MouseMotionListener, PropertyChangeListener {
    public static final int MOUSEDRAG_NONE = 0;
    public static final int MOUSEDRAG_RESIZE = 1;
    public static final int MOUSEDRAG_ROTATE = 2;
    public static final int MOUSEDRAG_SHIFT = 3;

    private static final int MOUSECLICK_NONE = 0;
    private static final int MOUSECLICK_SHIFT = 1;

    public static final int TILE_SIZE = 128;

    public static final String BOX_MOVE_ID = "boxMoveId";

    private Point oldMousePosition;
    private Point dragPosition;

    private int dragPolicy = MOUSEDRAG_NONE;
    private int clickPolicy = MOUSECLICK_SHIFT;

    private boolean dragging = false;
    protected MappingToolWidget mtw;
    protected PropertyChangeListener boxMoverListener = null;
    public String boxMoverId;

    private static final int TABLE_MAX = 8000;
    private static final int X_TOP_MAX = 1024;

    private SimpleOverviewROI roi;

    private boolean roiChanged = false;
    private boolean displayPopup = false;

    /**
     * constructor with given image
     *
     * @param image the RenderedImage for display in this pane.
     */
    public SimpleOverviewImageDisplayPane(RenderedImage image, String imageName, SimpleOverviewROI roi) {
        super(image, new ResizePolicy(ResizePolicy.POLICY_CENTER), imageName);

        Dimension dim = new Dimension(baseWindow.getWidth(), baseWindow.getHeight());
        setPreferredSize(dim);

        this.roi = roi;
        roi.setPosition(new Point2D.Float(getWidth() / 2.0f, getHeight() / 2.0f));
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        setCenterPlus(false);
        toggleBorder();
        this.mtw = new MappingToolWidget(imageName, 0, X_TOP_MAX, 0, 255, this, "MTW_OVW");
        renderedImage = grayscaleMapOperator(baseWindow, 0, X_TOP_MAX, 127);
        revalidate();
        repaint();
    }

    /** paint routine */
    public synchronized void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!roiChanged) roi.setPosition(new Point2D.Float(getWidth() / 2.0f, getHeight() / 2.0f));
        roi.draw(g);
    }

    public void mouseDragged(MouseEvent e) {
        dragging = true;
        Point position = e.getPoint();
        roi.adjust(position);
        roiChanged = true;
        revalidate();
        repaint();
    }

    public void addBoxMoverListener(String propertyId, PropertyChangeListener pcl) {
        boxMoverListener = pcl;
        boxMoverId = propertyId;
        addPropertyChangeListener(boxMoverId, boxMoverListener);
    }

    public void mouseMoved(MouseEvent e) {}

    public void mouseClicked(MouseEvent e) {
        if (clickPolicy == MOUSECLICK_SHIFT) {
            roi.setPosition(e.getPoint());
        }
        revalidate();
        repaint();
    }

    public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger()) {
            displayPopup = true;
        } else {
            requestFocus();
            oldMousePosition = e.getPoint();
            roi.getDragPolicy(e.getPoint());
        }
        return;
    }

    public void mouseReleased(MouseEvent e) {
        dragging = false;
        if (displayPopup) {
            displayPopup = false;
            return;
        }
        roiChanged = true;
        int width = (int) roi.getSize().getWidth();
        int height = (int) roi.getSize().getHeight();
        roi.setPosition(e.getPoint());
        Point position = new Point(
                (int) (roi.getPosition().getX() + 0.5) - width / 2,
                (int) (roi.getPosition().getY() + 0.5) - height / 2);
        Dimension size = new Dimension(width, height);
        Rectangle box = new Rectangle(position, size);
        firePropertyChange(boxMoverId, null, (Object) box);

        revalidate();
        repaint();
    }

    public boolean isFocusTranversable() {
        return true;
    }

    public void mouseEntered(MouseEvent e) {}

    public void mouseExited(MouseEvent e) {}

    private final RenderedImage grayscaleMapOperator(WindowOpImage source, int xBot, int xTop, int ySide) {

        if (source == null) {
            return null;
        }

        ParameterBlock pb = null;
        RenderedImage dst = null;
        SampleModel sampleModel = source.getSampleModel();
        int bands = sampleModel.getNumBands();
        int datatype = sampleModel.getDataType();

        int tableLength = X_TOP_MAX;
        int scaledTopX = (xTop * 255 + X_TOP_MAX / 2) / X_TOP_MAX;
        int scaledBotX = (xBot * 255 + X_TOP_MAX / 2) / X_TOP_MAX;
        if (datatype == DataBuffer.TYPE_SHORT || datatype == DataBuffer.TYPE_USHORT) {
            scaledTopX = xTop;
            scaledBotX = xBot;
            tableLength = TABLE_MAX;
        }

        ImageLayout il = new ImageLayout();
        il.setTileWidth(TILE_SIZE);
        il.setTileHeight(TILE_SIZE);
        il.setTileGridXOffset(0);
        il.setTileGridYOffset(0);
        RenderingHints hints = new RenderingHints(JAI.KEY_IMAGE_LAYOUT, il);

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
            LookupTableJAI lookup = new LookupTableJAI(lut);
            pb = new ParameterBlock();
            pb.addSource(source);
            pb.add(lookup);
            dst = JAI.create("lookup", pb, hints);
        } else if (datatype == DataBuffer.TYPE_INT
                || datatype == DataBuffer.TYPE_FLOAT
                || datatype == DataBuffer.TYPE_DOUBLE) {
            pb = new ParameterBlock();
            pb.addSource(source);
            double slope = (xTop != xBot) ? (256D / (xTop - xBot)) : 0D;
            double yInt = (xTop != xBot) ? (256D - slope * xTop) : 0D;
            pb.add(slope);
            pb.add(yInt);
            dst = JAI.create("rescale", pb, null);

            // produce a byte image
            pb = new ParameterBlock();
            pb.addSource(dst);
            pb.add(DataBuffer.TYPE_BYTE);
            dst = JAI.create("format", pb, null);
        }
        return dst;
    } // grayscaleMapOperator

    // computeByteMap
    //
    // Computes a piecewise linear map lookup table that corresponds to
    // the functionality of the MappingToolWidget.
    //
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

    // Pick up events from the window leveling widget
    // or box mover listener partner
    public void propertyChange(PropertyChangeEvent event) {

        // Window leveler event
        if (event.getSource() == mtw) {

            // Unpack event values
            if (event.getNewValue() instanceof Vector) {
                Vector v = (Vector) (event.getNewValue());
                int xTop = ((Integer) (v.elementAt(0))).intValue();
                int ySide = ((Integer) (v.elementAt(1))).intValue();
                int xBot = ((Integer) (v.elementAt(2))).intValue();
                renderedImage = grayscaleMapOperator(baseWindow, xBot, xTop, ySide);
                revalidate();
                repaint();
            }
        }

        // Box mover event
        if (event.getSource() == boxMoverListener) {

            // Unpack event values
            if (event.getNewValue() instanceof Rectangle) {
                roiChanged = true;
                Rectangle rect = (Rectangle) event.getNewValue();
                Point position = new Point(rect.x + rect.width / 2, rect.y + rect.height / 2);
                roi.setSize(new Dimension(rect.width, rect.height));
                roi.setPosition(position);
                revalidate();
                repaint();
            }
        }
    } // propertyChanged
}
