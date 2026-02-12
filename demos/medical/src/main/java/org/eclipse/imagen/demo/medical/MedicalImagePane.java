/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 *
 * This Example Content is intended to demonstrate usage of Eclipse technology. It is
 * provided to you under the terms and conditions of the Eclipse Distribution License
 * v1.0 which is available at http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */
package org.eclipse.imagen.demo.medical;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.RenderedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JComponent;
import javax.swing.JSplitPane;

/**
 * A pane to contian an RenderedImagei for display.
 *
 * <p>This class generates a pne to contain a RenderedImage for display. The RenderedImage is wrapped as a
 * WindowOpImage. So when the image moves in this pane, the tiles cached in this WindowOpImage will be re-used. This
 * strategy will make the display much faster.
 */
public class MedicalImagePane extends ImagePane
        implements MouseListener, MouseMotionListener, KeyListener, Observer, PropertyChangeListener {
    /** The mouse drag policies. */
    public static final int MOUSEDRAG_NONE = 0;

    public static final int MOUSEDRAG_SCALE = 1;
    public static final int MOUSEDRAG_MOVE = 2;

    /** The mouse click policies. */
    public static final int MOUSECLICK_NONE = 0;

    public static final int MOUSECLICK_RECENTER = 1;
    public static final int MOUSECLICK_REGISTER = 2;

    /** positions. */
    private Point oldMousePosition;

    private Point dragPosition;
    private Point currentMousePosition;

    /** policies. */
    private int dragPolicy = MOUSEDRAG_MOVE;

    private int clickPolicy = MOUSECLICK_NONE;

    /** indicate the drag is on/off. */
    private boolean draging = false;

    /** annotation parameters. */
    private boolean annotation = false;

    private String currentAnnotation = "_";
    private ArrayList annotationList = new ArrayList();

    /** measurement parameters. */
    private boolean measurement = false;

    private Point startPoint;
    private Point endPoint;

    /** indicates statistics is on/off. */
    private boolean statistics = false;

    /** indicates histogram is on/off. */
    private boolean histogram = false;

    /** default constructor */
    public MedicalImagePane(String imageName) {
        super(imageName);
        addListeners();
    }

    /**
     * constructor with given image
     *
     * @param image the RenderedImage for display in this pane.
     */
    public MedicalImagePane(RenderedImage image, String imageName) {
        super(image, imageName);
        addListeners();
        if (image instanceof Observable) ((Observable) image).addObserver(this);
    }

    /**
     * constructor with given image and policy
     *
     * @param image the RenderedImage for display in this pane.
     * @param policy the policy to move the image when this pane is resized.
     */
    public MedicalImagePane(RenderedImage image, ResizePolicy policy, String imageName) {
        super(image, policy, imageName);
        addListeners();
        if (image instanceof Observable) ((Observable) image).addObserver(this);
    }

    /** convenient method to add the proper listeners. */
    private void addListeners() {
        this.addKeyListener(this);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }

    /** set the image for display. Clear and register the correct observers. */
    public synchronized void set(RenderedImage im) {
        if (source instanceof Observable) ((Observable) source).deleteObservers();
        if (im instanceof Observable) ((Observable) im).addObserver(this);

        super.set(im);
    }

    /** get the current mouse drag policy. */
    public int getMouseDragPolicy() {
        return dragPolicy;
    }

    /** set the mouse drag policy. */
    public void setMouseDragPolicy(int i) {
        dragPolicy = i;
    }

    /** get the current mouse click policy. */
    public int getMouseClickPolicy() {
        return clickPolicy;
    }

    /** set the mouse click policy. */
    public void setMouseClickPolicy(int i) {
        clickPolicy = i;
    }

    /** set the annotation flag. */
    public void setAnnotation(boolean b) {
        if (annotation == b) return;

        annotation = b;
        if (!annotation) {
            annotationList.clear();
            revalidate();
            repaint();
        } else {
            setMeasurement(false);
            setStatistics(false);
            setHistogram(false);
        }
    }

    /** set the measurement flag. */
    public void setMeasurement(boolean b) {
        if (measurement == b) return;

        measurement = b;
        if (measurement) {
            setAnnotation(false);
            setStatistics(false);
            setHistogram(false);
        }
    }

    /** set the statistics flag. */
    public void setStatistics(boolean b) {
        if (statistics == b) return;
        statistics = b;
        if (statistics) {
            setAnnotation(false);
            setMeasurement(false);
            setHistogram(false);
        }
    }

    /** set the histogram flag. */
    public void setHistogram(boolean b) {
        if (histogram == b) return;
        histogram = b;

        if (histogram) {
            setAnnotation(false);
            setMeasurement(false);
            setStatistics(false);
        }
    }

    /** paint routine */
    public synchronized void paintComponent(Graphics g) {
        super.paintComponent(g);

        // if drag the mouse to define a window for scaling
        if (dragPolicy == MOUSEDRAG_SCALE && draging) {
            g.setColor(Color.green);
            // g.setXORMode(getBackground());
            g.drawRect(
                    Math.min(oldMousePosition.x, dragPosition.x),
                    Math.min(oldMousePosition.y, dragPosition.y),
                    Math.abs(oldMousePosition.x - dragPosition.x),
                    Math.abs(oldMousePosition.y - dragPosition.y));
        }

        if (annotation) {
            g.setColor(Color.white);
            if (currentAnnotation != null && currentAnnotation.length() > 0 && currentMousePosition != null)
                g.drawString(currentAnnotation, currentMousePosition.x, currentMousePosition.y);
            for (int i = 0; i < annotationList.size(); i += 2) {
                Point pos = (Point) annotationList.get(i);
                String s = (String) annotationList.get(i + 1);
                g.drawString(s, pos.x, pos.y);
            }
        }

        // draw a line from the start point to the end point
        if (measurement) {
            if (startPoint != null && endPoint != null && !startPoint.equals(endPoint)) {
                g.setColor(Color.green);
                g.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y);
                double distance = ((MedicalAppOpImage) source).computeDistance(startPoint, endPoint);
                g.drawString(new String("" + distance + " mm"), endPoint.x, endPoint.y);
            }
        }

        // draw the ROI
        if (statistics || histogram) {
            if (draging) {
                g.setColor(Color.green);
                g.drawRect(
                        Math.min(oldMousePosition.x, dragPosition.x),
                        Math.min(oldMousePosition.y, dragPosition.y),
                        Math.abs(oldMousePosition.x - dragPosition.x) + 1,
                        Math.abs(oldMousePosition.y - dragPosition.y) + 1);
            }
        }
    }

    /** process mouse drag event. */
    public void mouseDragged(MouseEvent e) {
        draging = true;

        if (measurement) {
            endPoint = e.getPoint();
        } else if (statistics || histogram) {
            dragPosition = e.getPoint();
        } else if (dragPolicy == MOUSEDRAG_MOVE) {
            referenceX -= e.getX() - oldMousePosition.x;
            referenceY -= e.getY() - oldMousePosition.y;
            oldMousePosition = e.getPoint();
        }

        currentMousePosition = e.getPoint();
        revalidate();
        repaint();
    }

    /** process mouse move event. */
    public void mouseMoved(MouseEvent e) {}

    /** process the mouse click event. */
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
        currentMousePosition = e.getPoint();

        revalidate();
        repaint();
    }

    /** process the mouse pressed event. */
    public void mousePressed(MouseEvent e) {
        requestFocus();
        oldMousePosition = e.getPoint();
        currentMousePosition = e.getPoint();
        if (measurement) startPoint = oldMousePosition;
    }

    /** process the mouse released event. */
    public void mouseReleased(MouseEvent e) {
        currentMousePosition = e.getPoint();
        if (measurement) {
            startPoint = endPoint = null;
        } else if (draging && (statistics || histogram)) {
            Rectangle rect = new Rectangle(
                    Math.min(oldMousePosition.x, dragPosition.x),
                    Math.min(oldMousePosition.y, dragPosition.y),
                    Math.abs(oldMousePosition.x - dragPosition.x),
                    Math.abs(oldMousePosition.y - dragPosition.y));
            rect.translate(-shiftX, -shiftY);
            ((MedicalAppOpImage) source).displayStatistics(rect, histogram);
        }
        draging = false;

        revalidate();
        repaint();
    }

    /** process mouse entered event. */
    public void mouseEntered(MouseEvent e) {}

    /** process mouse-exited event. */
    public void mouseExited(MouseEvent e) {}

    /** Set focus transversable. */
    public boolean isFocusTranversable() {
        return true;
    }

    /** process key pressed event. */
    public void keyPressed(KeyEvent e) {}

    /** process key-released event. */
    public void keyReleased(KeyEvent e) {}

    /** process key-typed event. */
    public void keyTyped(KeyEvent e) {
        if (!annotation) return;

        char c = e.getKeyChar();
        if (c != KeyEvent.CHAR_UNDEFINED) {
            if (c == KeyEvent.VK_BACK_SPACE) {
                int len = currentAnnotation.length() - 2;
                if (len < 0) len = 0;

                currentAnnotation = currentAnnotation.substring(0, len) + "_";
            } else if (c == KeyEvent.VK_ENTER) {
                int len = currentAnnotation.length() - 1;
                if (len < 0) len = 0;

                currentAnnotation = currentAnnotation.substring(0, len);

                annotationList.add(currentMousePosition);
                annotationList.add(currentAnnotation);
                currentAnnotation = "_";
            } else {
                int len = currentAnnotation.length() - 1;
                if (len < 0) len = 0;

                currentAnnotation = currentAnnotation.substring(0, len) + c + "_";
            }
            revalidate();
            repaint();
        }
    }

    /** update if observe that the image is changed. */
    public synchronized void update(Observable o, Object rectangle) {
        if (o.equals(source)) {
            super.set(source);
            revalidate();
            repaint();
        }
    }

    /** process the property change event when annotation,..., histogram status is changed. */
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(annotationCommand)) {
            setAnnotation(((Boolean) evt.getNewValue()).booleanValue());
        } else if (evt.getPropertyName().equals(measurementCommand)) {
            setMeasurement(((Boolean) evt.getNewValue()).booleanValue());
        } else if (evt.getPropertyName().equals(statisticsCommand)) {
            setStatistics(((Boolean) evt.getNewValue()).booleanValue());
        } else if (evt.getPropertyName().equals(histogramCommand)) {
            setHistogram(((Boolean) evt.getNewValue()).booleanValue());
        }
    }

    private int getQuadNum() {
        JComponent root = getRootPane();
        int[] code = {-1, -1};
        getQuadNum(root, code);
        return code[0] + code[1];
    }

    // this method is used when the display pane is splited into
    // 2 x 2 layout
    private boolean getQuadNum(Component container, int[] code) {

        if (container == null) return false;

        if (container.equals(this)) {
            return true;
        }

        int pos = 0;
        if (container instanceof JSplitPane) {
            while (pos < code.length && code[pos] != -1) pos++;
            JSplitPane pane = (JSplitPane) container;

            int factor = 1;
            if (pane.getOrientation() == JSplitPane.VERTICAL_SPLIT) factor = 2;

            code[pos] = 0;
            if (getQuadNum(pane.getLeftComponent(), code)) return true;

            code[pos] = factor;
            if (getQuadNum(pane.getRightComponent(), code)) return true;
            code[pos] = -1;
            return false;
        }

        if (!(container instanceof Container)) return false;

        Component[] components = ((Container) container).getComponents();

        for (int i = 0; i < components.length; i++) if (getQuadNum(components[i], code)) return true;

        return false;
    }
}
