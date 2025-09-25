/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 *
 * This Example Content is intended to demonstrate usage of Eclipse technology. It is
 * provided to you under the terms and conditions of the Eclipse Distribution License
 * v1.0 which is available at http://www.eclipse.org/org/documents/edl-v10.php.
 */
package org.eclipse.imagen.tutorial;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.text.NumberFormat;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.*;
import org.eclipse.imagen.*;

interface DemoListener {

    void notifyNumPoints(int points, int needed);

    void notifyPolynomial(String xpoly, String ypoly);
}

class WarpPanel extends JPanel implements MouseListener, MouseMotionListener {

    Color grey;
    Color yellow;
    Color cyan;
    Color blue;

    int numPoints = 0;
    float[] srcCoords = new float[200];
    float[] dstCoords = new float[200];

    float mag = 4.0F;

    int dragIndex = -1;
    int dragSrcDst = -1;

    AffineTransform identityTransform = new AffineTransform();
    WarpPolynomial warp;
    float[] coeffs = {1.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F};

    int degree = 1;
    int pointsNeeded = (degree + 1) * (degree + 2) / 2;

    RenderedImage srcImage;
    RenderedImage dstImage;
    int width, height;

    Vector listeners = new Vector();

    boolean showSourcePositions = true;
    boolean showDestPositions = true;
    boolean showWarpedImage = true;
    boolean magnifyDisplacements = false;

    public WarpPanel(RenderedImage srcImage) {
        this.srcImage = srcImage;
        this.width = srcImage.getWidth();
        this.height = srcImage.getHeight();
        updateWarp();

        grey = new Color(170, 170, 170);
        cyan = new Color(0, 255, 255);
        yellow = new Color(255, 255, 0);
        blue = new Color(0, 0, 255);

        addMouseListener(this);
        addMouseMotionListener(this);
    }

    public Dimension getPreferredSize() {
        return new Dimension(srcImage.getWidth(), srcImage.getHeight());
    }

    public void addDemoListener(DemoListener l) {
        listeners.add(l);
    }

    public void removeDemoListener(DemoListener l) {
        listeners.remove(l);
    }

    public void paint(Graphics g) {
        g.setColor(grey);
        g.fillRect(0, 0, 512, 512);

        if (showWarpedImage) {
            ((Graphics2D) g).drawRenderedImage(dstImage, identityTransform);
        } else {
            ((Graphics2D) g).drawRenderedImage(srcImage, identityTransform);
        }

        if (showSourcePositions && showDestPositions) {
            g.setColor(blue);
            for (int i = 0; i < numPoints; i++) {
                g.drawLine(getIntXCoord(0, i), getIntYCoord(0, i), getIntXCoord(1, i), getIntYCoord(1, i));
            }
        }

        if (showSourcePositions) {
            g.setColor(cyan);
            for (int i = 0; i < numPoints; i++) {
                g.fillRect(getIntXCoord(0, i) - 2, getIntYCoord(0, i) - 2, 4, 4);
            }
        }

        if (showDestPositions) {
            g.setColor(yellow);
            for (int i = 0; i < numPoints; i++) {
                g.fillRect(getIntXCoord(1, i) - 2, getIntYCoord(1, i) - 2, 4, 4);
            }
        }
    }

    public void deleteAllPoints() {
        numPoints = 0;
        notifyNumPoints();
        updateWarp();
        repaint();
    }

    private void notifyNumPoints() {
        Enumeration e = listeners.elements();
        while (e.hasMoreElements()) {
            DemoListener l = (DemoListener) e.nextElement();
            l.notifyNumPoints(numPoints, pointsNeeded);
        }
    }

    private String getPolyAsString(float[] coeffs, int offset, int degree) {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(2);

        String s = new String();
        boolean firstTime = true;

        for (int i = 0; i <= degree; i++) {
            for (int j = 0; j <= i; j++) {
                if (!firstTime) {
                    s += "+";
                }
                firstTime = false;

                s += nf.format(coeffs[offset++]);
                if (i - j == 1) {
                    s += "*x";
                } else if (i - j > 1) {
                    s += "*x^" + (i - j);
                }

                if (j == 1) {
                    s += "*y";
                } else if (j > 1) {
                    s += "*y^" + j;
                }
            }
        }

        return s;
    }

    private void notifyPolynomial() {
        String xpoly = "x'=" + getPolyAsString(coeffs, 0, degree);
        String ypoly = "y'=" + getPolyAsString(coeffs, pointsNeeded, degree);

        Enumeration e = listeners.elements();
        while (e.hasMoreElements()) {
            DemoListener l = (DemoListener) e.nextElement();
            l.notifyPolynomial(xpoly, ypoly);
        }
    }

    public void setDegree(int degree) {
        this.degree = degree;
        this.pointsNeeded = (degree + 1) * (degree + 2) / 2;
        notifyNumPoints();

        updateWarp();
        repaint();
    }

    public void setShowSource(boolean show) {
        showSourcePositions = show;
        repaint();
    }

    public void setShowDest(boolean show) {
        showDestPositions = show;
        repaint();
    }

    public void setShowWarped(boolean show) {
        showWarpedImage = show;
        repaint();
    }

    public void setMagnifyDisplacements(boolean magnify) {
        magnifyDisplacements = magnify;
        repaint();
    }

    private void updateWarp() {
        if (numPoints >= pointsNeeded) {
            warp = WarpPolynomial.createWarp(
                    srcCoords,
                    0,
                    dstCoords,
                    0,
                    2 * numPoints,
                    1.0F / width,
                    1.0F / height,
                    (float) width,
                    (float) height,
                    degree);
            float[][] tcoeffs = warp.getCoeffs();
            int length = tcoeffs[0].length;
            coeffs = new float[2 * length];
            for (int i = 0; i < length; i++) {
                coeffs[i] = tcoeffs[0][i];
                coeffs[i + length] = tcoeffs[1][i];
            }
            notifyPolynomial();

            ParameterBlock pb = new ParameterBlock();
            pb.addSource(srcImage);
            pb.add(warp);
            pb.add(new InterpolationNearest());
            dstImage = ImageN.create("warp", pb);
        } else {
            coeffs = new float[2 * pointsNeeded];
            coeffs[1] = 1.0F;
            coeffs[pointsNeeded + 2] = 1.0F;
            notifyPolynomial();

            dstImage = srcImage;
        }
    }

    private int locatePoint(int x, int y, int srcDst) {
        for (int i = 0; i < numPoints; i++) {
            int dx = getIntXCoord(srcDst, i) - x;
            int dy = getIntYCoord(srcDst, i) - y;
            if (dx * dx + dy * dy < 36) {
                return i;
            }
        }

        return -1;
    }

    private void setCoords(int srcDst, int index, int x, int y) {
        if (srcDst == 0) {
            srcCoords[2 * index] = (float) x;
            srcCoords[2 * index + 1] = (float) y;
        } else {
            if (magnifyDisplacements) {
                float dx = x - srcCoords[2 * index];
                float dy = y - srcCoords[2 * index + 1];

                dstCoords[2 * index] = srcCoords[2 * index] + dx / mag;
                dstCoords[2 * index + 1] = srcCoords[2 * index + 1] + dy / mag;
            } else {
                dstCoords[2 * index] = (float) x;
                dstCoords[2 * index + 1] = (float) y;
            }
        }
    }

    private int getIntXCoord(int srcDst, int index) {
        if (srcDst == 0) {
            return (int) Math.round(srcCoords[2 * index]);
        } else {
            if (magnifyDisplacements) {
                float x = srcCoords[2 * index];
                float dx = dstCoords[2 * index] - x;
                return (int) Math.round(x + mag * dx);
            } else {
                return (int) Math.round(dstCoords[2 * index]);
            }
        }
    }

    private int getIntYCoord(int srcDst, int index) {
        if (srcDst == 0) {
            return (int) Math.round(srcCoords[2 * index + 1]);
        } else {
            if (magnifyDisplacements) {
                float y = srcCoords[2 * index + 1];
                float dy = dstCoords[2 * index + 1] - y;
                return (int) Math.round(y + mag * dy);
            } else {
                return (int) Math.round(dstCoords[2 * index + 1]);
            }
        }
    }

    private void addPoint(int x, int y) {
        setCoords(0, numPoints, x, y);
        setCoords(1, numPoints, x, y);
        ++numPoints;

        notifyNumPoints();
    }

    private void removePoint(int index) {
        if (numPoints > 1) {
            srcCoords[2 * index] = srcCoords[2 * (numPoints - 1)];
            srcCoords[2 * index + 1] = srcCoords[2 * (numPoints - 1) + 1];
            dstCoords[2 * index] = dstCoords[2 * (numPoints - 1)];
            dstCoords[2 * index + 1] = dstCoords[2 * (numPoints - 1) + 1];
        }
        --numPoints;

        notifyNumPoints();
    }

    public void mouseClicked(MouseEvent e) {
        // System.out.println("mouseClicked");
    }

    public void mouseEntered(MouseEvent e) {
        // System.out.println("mouseEntered");
    }

    public void mouseExited(MouseEvent e) {
        // System.out.println("mouseExited");
    }

    public void mousePressed(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        int index;

        index = locatePoint(x, y, 1);
        dragSrcDst = 1;
        if (index == -1) {
            index = locatePoint(x, y, 0);
            dragSrcDst = 0;
        }

        if ((e.getModifiers() & InputEvent.BUTTON1_MASK) != 0) {
            if (index == -1) {
                addPoint(x, y);
                repaint();
                dragIndex = numPoints - 1;
                dragSrcDst = 1;
            } else {
                dragIndex = index;
            }
        } else if ((e.getModifiers() & InputEvent.BUTTON3_MASK) != 0) {
            if (index != -1) {
                removePoint(index);
                updateWarp();
                repaint();
            }
        }
    }

    public void mouseReleased(MouseEvent e) {
        if (dragIndex != -1) {
            updateWarp();
            repaint();
        }
        dragIndex = -1;
    }

    public void mouseDragged(MouseEvent e) {
        if (dragIndex != -1) {
            int x = e.getX();
            int y = e.getY();

            setCoords(dragSrcDst, dragIndex, x, y);
            repaint();
        }
    }

    public void mouseMoved(MouseEvent e) {
        // System.out.println("mouseMoved");
    }
}

public class JAIWarpDemo extends JPanel implements ActionListener, DemoListener {

    WarpPanel warpPanel;

    boolean showSourcePositions = true;
    boolean showDestPositions = true;
    boolean showWarpedImage = true;
    boolean magnifyDisplacements = false;

    JComboBox combo;
    JLabel pointsLabel;
    JLabel xPolyLabel, yPolyLabel;

    public static final String DELETE_ALL_STRING = "Reset and Delete all points";

    public static final String SHOW_SRC_STRING = "Show src positions";
    public static final String SHOW_DST_STRING = "Show dst positions";
    public static final String SHOW_WARP_STRING = "Show warped image";

    public static final String MAGNIFY_STRING = "Magnify displacements";

    public static final String DEGREE_1_STRING = "Degree 1 (Affine)";
    public static final String DEGREE_2_STRING = "Degree 2 (Quadratic)";
    public static final String DEGREE_3_STRING = "Degree 3 (Cubic)";

    public JAIWarpDemo(String arg) {
        ParameterBlock pb = new ParameterBlock().add(arg);
        RenderedImage im = ImageN.create("fileload", pb, null);

        warpPanel = new WarpPanel(im);
        warpPanel.addDemoListener(this);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(7, 1));

        JButton bu;
        bu = new JButton(DELETE_ALL_STRING);
        bu.addActionListener(this);
        buttonPanel.add(bu);

        JCheckBox cb;
        cb = new JCheckBox(SHOW_SRC_STRING, true);
        cb.addActionListener(this);
        buttonPanel.add(cb);

        cb = new JCheckBox(SHOW_DST_STRING, true);
        cb.addActionListener(this);
        buttonPanel.add(cb);

        cb = new JCheckBox(SHOW_WARP_STRING, true);
        cb.addActionListener(this);
        buttonPanel.add(cb);

        cb = new JCheckBox(MAGNIFY_STRING, false);
        cb.addActionListener(this);
        buttonPanel.add(cb);

        combo = new JComboBox();
        combo.addItem(DEGREE_1_STRING);
        combo.addItem(DEGREE_2_STRING);
        combo.addItem(DEGREE_3_STRING);
        combo.addActionListener(this);
        buttonPanel.add(combo);

        pointsLabel = new JLabel("Got 0 of 3 points or pairs");
        buttonPanel.add(pointsLabel);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BorderLayout());
        controlPanel.add(buttonPanel, "North");

        JPanel polyPanel = new JPanel();
        polyPanel.setLayout(new GridLayout(2, 1));

        xPolyLabel = new JLabel("x' = 0.0 + 1.0*x + 0.0*y");
        yPolyLabel = new JLabel("y' = 0.0 + 0.0*x + 1.0*y");

        polyPanel.add(xPolyLabel);
        polyPanel.add(yPolyLabel);

        JPanel masterPanel = new JPanel();
        masterPanel.setLayout(new BorderLayout());
        masterPanel.add(polyPanel, "North");
        masterPanel.add(warpPanel, "Center");
        masterPanel.add(controlPanel, "South");

        setOpaque(true);
        add(masterPanel, BorderLayout.CENTER);
        add(new JLabel("Click and/or drag mouse to enter points."), BorderLayout.SOUTH);
    }

    public void notifyNumPoints(int points, int needed) {
        pointsLabel.setText("Got " + points + " of " + needed + " points");
    }

    public void notifyPolynomial(String xpoly, String ypoly) {
        xPolyLabel.setText(xpoly);
        yPolyLabel.setText(ypoly);
    }

    public void actionPerformed(ActionEvent e) {
        String s = e.getActionCommand();

        if (e.getSource() == combo) {
            int index = combo.getSelectedIndex();
            warpPanel.setDegree(index + 1);
            return;
        }

        if (DELETE_ALL_STRING.equals(s)) {
            warpPanel.deleteAllPoints();
            return;
        } else if (SHOW_SRC_STRING.equals(s)) {
            showSourcePositions = !showSourcePositions;
            warpPanel.setShowSource(showSourcePositions);
            return;
        } else if (SHOW_DST_STRING.equals(s)) {
            showDestPositions = !showDestPositions;
            warpPanel.setShowDest(showDestPositions);
            return;
        } else if (SHOW_WARP_STRING.equals(s)) {
            showWarpedImage = !showWarpedImage;
            warpPanel.setShowWarped(showWarpedImage);
            return;
        } else if (MAGNIFY_STRING.equals(s)) {
            magnifyDisplacements = !magnifyDisplacements;
            warpPanel.setMagnifyDisplacements(magnifyDisplacements);
            return;
        }
    }
}
