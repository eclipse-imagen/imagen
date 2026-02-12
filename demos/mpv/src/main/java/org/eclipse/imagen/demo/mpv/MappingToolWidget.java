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

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.accessibility.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.colorchooser.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;
import javax.swing.text.*;

/**
 * This widget generates a piecewise linear "mapping" from a source to destination. The widget is not application
 * specific although it was written as a generalized window level interface for imaging applications.
 *
 * <p>The user must implement PropertyChangeListener to receive notification of any changes on the mapping values.
 *
 * @version 1.0 5/15/01
 */
public class MappingToolWidget extends JFrame {

    public static final int DEFAULT_HORIZONTAL_MIN = 0;
    public static final int DEFAULT_HORIZONTAL_MAX = 255;
    public static final int DEFAULT_VERTICAL_MIN = 0;
    public static final int DEFAULT_VERTICAL_MAX = 255;
    public static final String DEFAULT_TITLE = new String("MappingToolWidget");
    public static final String DEFAULT_HELP_STRING = new String("Use top and bottom sliders to adjust bright\n"
            + "and dark cutoff values.  Use the side slider\n" + "to adjust the center variable contrast point.\n");
    private static final int PT_DIAMETER = 8;
    private static final int PT_RADIUS = PT_DIAMETER / 2;

    private String title;
    private String helpString;

    private int topSliderValue;
    private int sideSliderValue;
    private int bottomSliderValue;

    private JButton resetBtn;
    private JButton invertBtn;
    private JButton helpBtn;
    private JButton hideBtn;

    private JSlider topSldr;
    private JSlider sideSldr;
    private JSlider bottomSldr;
    private DrawingArea drawingArea;

    private JPanel basePanel;

    private int xMin;
    private int xMax;
    private int yMin;
    private int yMax;

    private int oldTop;
    private int oldSide;
    private int oldBottom;

    public PropertyChangeListener changeEventListener = null;
    public String propertyId;
    private Vector mtwPropertyValues = new Vector(3);

    // Main method allowing a standalone run
    public static void main(String[] args) {
        MappingToolWidget mtw = new MappingToolWidget();
        mtw.show();
    }

    /** Constructor for a JFrame with some widgets (most general case) */
    public MappingToolWidget(
            String userTitle,
            int horizontalMin,
            int horizontalMax,
            int verticalMin,
            int verticalMax,
            PropertyChangeListener listener,
            String userPropertyId) {
        super(userTitle);

        setFields(userTitle, horizontalMin, horizontalMax, verticalMin, verticalMax, listener, userPropertyId);
    } // MappingToolWidget

    /** Constructor for a JFrame with some widgets (useful for mapping to bytes) */
    public MappingToolWidget(
            String userTitle,
            int horizontalMin,
            int horizontalMax,
            PropertyChangeListener listener,
            String userPropertyId) {
        super(userTitle);

        setFields(
                userTitle,
                horizontalMin,
                horizontalMax,
                DEFAULT_VERTICAL_MIN,
                DEFAULT_VERTICAL_MAX,
                listener,
                userPropertyId);
    } // MappingToolWidget

    /** Constructor for a JFrame with some widgets (toy/test mode) */
    public MappingToolWidget() {
        super(DEFAULT_TITLE);

        setFields(
                DEFAULT_TITLE,
                DEFAULT_HORIZONTAL_MIN,
                DEFAULT_HORIZONTAL_MAX,
                DEFAULT_VERTICAL_MIN,
                DEFAULT_VERTICAL_MAX,
                (PropertyChangeListener) null,
                (String) null);

        // No listener, so this is just running in "toy mode"
        System.out.println("Mapping tool widget running in toy mode!");
    }

    /** Constructor helper */
    private void setFields(
            String userTitle,
            int horizontalMin,
            int horizontalMax,
            int verticalMin,
            int verticalMax,
            PropertyChangeListener listener,
            String userPropertyId) {

        title = userTitle;

        // Set the size for this widget
        setBounds(100, 100, 270, 330);

        // Set up the base panel
        basePanel = new JPanel();
        basePanel.setLayout(new BoxLayout(basePanel, BoxLayout.Y_AXIS));
        getContentPane().add(basePanel);

        // Load the map values from the user
        xMin = horizontalMin;
        xMax = horizontalMax;
        yMin = verticalMin;
        yMax = verticalMax;

        // Place the items in the gui onto base panel
        createSlidersButtonsAndDrawingArea();

        // Store values to avoid repeating duplicates
        oldTop = topSliderValue;
        oldSide = sideSliderValue;
        oldBottom = bottomSliderValue;

        // Get the user's propertyId to pass this widget's properties
        if (userPropertyId != null) {
            this.changeEventListener = listener;
            addPropertyChangeListener(userPropertyId, listener);
            this.propertyId = userPropertyId;
        }

        helpString = DEFAULT_HELP_STRING;

        this.pack();
    } // setFields

    /**
     * Set a new string for help message
     *
     * @param helpMessage new help message to be displayed when user presses "help" button
     */
    public void setHelpString(String helpMessage) {
        helpString = helpMessage;
    } // setHelpString

    /** Send changed values to listeners and repaint the drawing area */
    private void processUpdate() {

        drawingArea.repaint();
        if ((topSliderValue == oldTop) && (sideSliderValue == oldSide) && (bottomSliderValue == oldBottom)) return;

        oldTop = topSliderValue;
        oldSide = sideSliderValue;
        oldBottom = bottomSliderValue;
        mtwPropertyValues.setSize(3);
        mtwPropertyValues.setElementAt(new Integer(topSliderValue), 0);
        mtwPropertyValues.setElementAt(new Integer(sideSliderValue), 1);
        mtwPropertyValues.setElementAt(new Integer(bottomSliderValue), 2);
        firePropertyChange(propertyId, null, (Object) mtwPropertyValues);
    } // processUpdate()

    /** Create panels to hold sliders (top, side, bottom), buttons (bottom) and a drawing area. */
    private void createSlidersButtonsAndDrawingArea() {

        // Create panels to hold the layout:
        //
        //   ---------------------------
        //   | <---slider--------++--> |
        //   ---------------------------
        //   | --------------------| ^ |
        //   | |                   | s |
        //   | |                   | l |
        //   | |                   | i |
        //   | |                   | d |
        //   | |     Drawing       | e |
        //   | |       Area        | r |
        //   | |                   | | |
        //   | |                   | | |
        //   | |                   | + |
        //   | |                   | + |
        //   | |                   | | |
        //   | |                   | | |
        //   | |                   | | |
        //   | --------------------| v |
        //   ---------------------------
        //   | <--+++--------slider--> |
        //   ---------------------------
        //   |<btn1><btn2><btn3><btn4> |
        //   ---------------------------
        //
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        JPanel midPanel = new JPanel();
        midPanel.setLayout(new BoxLayout(midPanel, BoxLayout.X_AXIS));
        JPanel lowerPanel = new JPanel();
        lowerPanel.setLayout(new BoxLayout(lowerPanel, BoxLayout.Y_AXIS));
        SliderListener sListener = new SliderListener();

        // Set default parameters
        topSliderValue = xMax;
        sideSliderValue = (yMin + yMax) / 2;
        bottomSliderValue = xMin;

        // Top slider
        topSldr = new JSlider(xMin, xMax, topSliderValue);
        topSldr.setPaintTicks(false);
        topSldr.setPaintLabels(false);
        topSldr.addChangeListener(sListener);
        topSldr.setOrientation(javax.swing.SwingConstants.HORIZONTAL);
        topPanel.add(topSldr);

        // Drawing area and side slider
        drawingArea = new DrawingArea();

        // Side slider
        sideSldr = new JSlider(yMin, yMax, sideSliderValue);
        sideSldr.setPaintTicks(false);
        sideSldr.setPaintLabels(false);
        sideSldr.addChangeListener(sListener);
        sideSldr.setOrientation(javax.swing.SwingConstants.VERTICAL);
        midPanel.add(drawingArea);
        midPanel.add(sideSldr);

        // Bottom slider
        bottomSldr = new JSlider(xMin, xMax, bottomSliderValue);
        bottomSldr.setPaintTicks(false);
        bottomSldr.setPaintLabels(false);
        bottomSldr.addChangeListener(sListener);
        bottomSldr.setOrientation(javax.swing.SwingConstants.HORIZONTAL);
        lowerPanel.add(bottomSldr);

        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.X_AXIS));

        ButtonListener bListener = new ButtonListener();

        // Construct invert, reset, help, and hide buttons
        invertBtn = new JButton("Invert");
        invertBtn.addActionListener(bListener);
        btnPanel.add(invertBtn);
        resetBtn = new JButton("Reset");
        resetBtn.addActionListener(bListener);
        btnPanel.add(resetBtn);
        helpBtn = new JButton("Help");
        helpBtn.addActionListener(bListener);
        btnPanel.add(helpBtn);
        hideBtn = new JButton("Hide");
        hideBtn.addActionListener(bListener);
        btnPanel.add(hideBtn);

        // Attach everything to the base panel
        basePanel.add(topPanel);
        basePanel.add(midPanel);
        basePanel.add(lowerPanel);
        basePanel.add(btnPanel);
    } // void createSlidersAndDrawingArea

    /** Reset the widget to input parameters */
    private void resetWidget() {

        // Set default parameters
        topSliderValue = xMax;
        sideSliderValue = (yMin + yMax) / 2;
        bottomSliderValue = xMin;
        topSldr.setValue(topSliderValue);
        sideSldr.setValue(sideSliderValue);
        bottomSldr.setValue(bottomSliderValue);
        processUpdate();
    }

    /** Reflect the segmented curve about the line y = x */
    private void invertWidget() {

        // Switch top and bottom parameters
        int temp = topSliderValue;
        topSliderValue = bottomSliderValue;
        sideSliderValue = yMin + yMax - sideSliderValue;
        bottomSliderValue = temp;
        topSldr.setValue(topSliderValue);
        sideSldr.setValue(sideSliderValue);
        bottomSldr.setValue(bottomSliderValue);
        processUpdate();
    }

    /** Display a helpful message (user can change <code>helpString</code> using <code>setHelpString</code> method). */
    private void showHelp() {
        JFrame frame = new JFrame("Graycale Tool Help");
        JOptionPane.showMessageDialog(frame, helpString, "User Help", JOptionPane.INFORMATION_MESSAGE);
    } // void showHelp

    /** An inner class to handle button presses */
    class ButtonListener implements ActionListener {
        public ButtonListener() {}

        public void actionPerformed(ActionEvent e) {
            JButton b = (JButton) e.getSource();
            if (b.equals(resetBtn)) {

                // Reset the widget to the nominal setting
                resetWidget();

            } else if (b.equals(invertBtn)) {

                // Invert the input/output roles on the mapping
                invertWidget();

            } else if (b.equals(helpBtn)) {

                // Display an information dialog
                showHelp();

            } else if (b.equals(hideBtn)) {

                // hide this widget from view
                setVisible(false);
            }
        }
    } // class ButtonListener

    /* An inner class to handle slider changes */

    class SliderListener implements ChangeListener {

        public SliderListener() {}

        public void stateChanged(ChangeEvent e) {
            JSlider s = (JSlider) e.getSource();

            if (s.equals(topSldr)) {
                topSliderValue = s.getValue();
            } else if (s.equals(sideSldr)) {
                sideSliderValue = s.getValue();
            } else if (s.equals(bottomSldr)) {
                bottomSliderValue = s.getValue();
            }

            // If the user is dragging, wait until they
            // let go before sending updates to client.
            if (s.getValueIsAdjusting()) {
                drawingArea.repaint();
            } else {
                processUpdate();
            }
        }
    } // class SliderListener

    /** Fast version of ROUND operation */
    private static final int ROUND(double x) {
        return (x > 0) ? (int) (x + 0.5) : (int) (x - 0.5);
    }

    /** An inner class for the drawing area holding the widget's graphics */
    private class DrawingArea extends JPanel {

        /** Constructor */
        public DrawingArea() {
            super();
        }

        /**
         * Build graphic display as determined by top, bottom, and side slider positions and their respective units.
         *
         * @param g system supplied graphics context
         */
        public void paint(Graphics g) {
            int daWidth = drawingArea.getWidth();
            int daHeight = drawingArea.getHeight();
            Color lineColor = Color.black;
            Color daColor = new Color(0, 200, 200);
            int sliderWidth = xMax - xMin + 1;
            int sliderHeight = yMax - yMin + 1;

            /* Clear the drawing area */

            g.setColor(daColor);
            g.fillRect(0, 0, daWidth, daHeight);

            /* Set coordinates inside the drawing area */

            int xOff = PT_RADIUS;
            int yOff = PT_RADIUS;
            int width = daWidth - PT_DIAMETER;
            int height = daHeight - PT_DIAMETER;

            /* Compute display coords for points */

            int xTop = xOff + ((topSliderValue - xMin) * width + sliderWidth / 2) / sliderWidth;
            int xBot = xOff + ((bottomSliderValue - xMin) * width + sliderWidth / 2) / sliderWidth;
            int yTop = yOff;
            int yBot = yOff + height - 1;

            /* The interior point varies along the complementary diagonal to top
            and bottom points */

            double t = (double) (sideSliderValue - yMin) / (yMax - yMin);
            int xInt = ROUND(t * xBot + (1 - t) * xTop);
            int yInt = ROUND(t * yTop + (1 - t) * yBot);

            /* render top and bottom lines */

            g.setColor(lineColor);
            if (topSliderValue >= bottomSliderValue) {
                g.drawLine(xTop, yTop, daWidth - 1, yTop);
                g.drawLine(xBot, yBot, 0, yBot);
            } else {
                g.drawLine(xTop, yTop, 0, yTop);
                g.drawLine(xBot, yBot, daWidth - 1, yBot);
            }

            /* render middle lines */

            g.drawLine(xTop, yTop, xInt, yInt);
            g.drawLine(xBot, yBot, xInt, yInt);

            /* draw the points (filled circles) in green */

            g.setColor(Color.green);
            g.fillArc(xTop - PT_RADIUS, yTop - PT_RADIUS, PT_DIAMETER, PT_DIAMETER, 0, 360);
            g.setColor(Color.blue);
            g.fillArc(xBot - PT_RADIUS, yBot - PT_RADIUS, PT_DIAMETER, PT_DIAMETER, 0, 360);
            g.setColor(Color.red);
            g.fillArc(xInt - PT_RADIUS, yInt - PT_RADIUS, PT_DIAMETER, PT_DIAMETER, 0, 360);

            return; /* done */
        } // paint()
    } // DrawingArea
}
