/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 *
 * This Example Content is intended to demonstrate usage of Eclipse technology. It is
 * provided to you under the terms and conditions of the Eclipse Distribution License
 * v1.0 which is available at http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */
package org.eclipse.imagen.demo;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.RenderedImage;
import java.util.Vector;
import javax.swing.*;
import org.eclipse.imagen.*;

class AutoThread extends Thread {

    private final JAIDemoPanel panel;
    private volatile boolean suspended = false;
    private volatile boolean stopped = false;

    public AutoThread(JAIDemoPanel panel) {
        this.panel = panel;
    }

    public void run() {
        while (!stopped) {
            if (!suspended) {
                panel.animate();
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void mySuspend() {
        suspended = true;
    }

    public void myResume() {
        suspended = false;
    }

    public void myStop() {
        suspended = true;
        stopped = true;
    }
}

public abstract class JAIDemoPanel extends JPanel implements ActionListener {

    Vector sourceVec = null;
    Icon imageIcon = null;
    JLabel imageLabel = null;

    boolean autoMode = false;
    AutoThread autoThread = null;

    /** Rendering hints to be used by subclasses. */
    protected RenderingHints renderHints = null;

    public JAIDemoPanel(Vector sourceVec) {
        this.sourceVec = (Vector) sourceVec.clone();
    }

    /**
     * Performs the standard setup. Subclasses should call this method immediately after calling super() in their
     * constructors. The main image pane is constructed and placed in a JScrollPane. A control panel with a reset button
     * is also constructed, and the makeControls method of the subclass is called to fill in the custom portion of the
     * panel.
     */
    public void masterSetup() {
        imageIcon = new IconJAI(process());
        imageLabel = new JLabel(imageIcon);
        JScrollPane scrollPane = new JScrollPane(
                imageLabel,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        setLayout(new BorderLayout());
        add("Center", scrollPane);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BorderLayout());

        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(this);

        JToggleButton autoButton = new JToggleButton("Automatic");
        autoButton.addActionListener(this);
        if (!supportsAutomatic()) {
            autoButton.setEnabled(false);
        }

        JPanel controlButtonPanel = new JPanel();
        controlButtonPanel.setLayout(new GridLayout(1, 2));
        controlButtonPanel.add(resetButton);
        controlButtonPanel.add(autoButton);

        controlPanel.add("West", controlButtonPanel);

        JPanel customPanel = new JPanel();
        controlPanel.add("Center", customPanel);
        makeControls(customPanel);

        add("South", controlPanel);
    }

    /** Returns the current source image. */
    public PlanarImage getSource(int index) {
        return (PlanarImage) sourceVec.elementAt(index);
    }

    /** Sets the source and performs processing. */
    public void setSource(int sourceNum, PlanarImage source) {
        sourceVec.setElementAt(source, sourceNum);
        repaint();
    }

    public void setRenderingHints(RenderingHints renderHints) {
        this.renderHints = renderHints;
        repaint();
    }

    /**
     * Repaints the image panel. The process() method is called to generate a new output image, which is then wrapped by
     * an ImageIcon and set as the icon of the JLabel imageLabel.
     */
    public void repaint() {
        // Beware of a race condition when Swing tries to repaint
        // before we've finished initialization.
        if (sourceVec == null) {
            return;
        }

        if (imageIcon instanceof IconJAI) {
            RenderedImage previousImage = ((IconJAI) imageIcon).getImage();
            if (previousImage instanceof PlanarImage) {
                ((PlanarImage) previousImage).dispose();
            }
        }

        imageIcon = new IconJAI(process());
        imageLabel.setIcon(imageIcon);
        Dimension iconSize = new Dimension(imageIcon.getIconWidth(), imageIcon.getIconHeight());
        imageLabel.setPreferredSize(iconSize);
        imageLabel.revalidate();
    }

    /** Creates a control panel that will affect the operation parameters. */
    public void makeControls(JPanel controls) {}

    /** Default method when any action is performed. */
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals("Reset")) {
            reset();
            repaint();
        } else if (command.equals("Automatic")) {
            autoMode = !autoMode;
            if (autoMode) {
                autoThread = new AutoThread(this);
                autoThread.start();
            } else {
                autoThread.interrupt();
                autoThread.myStop();
                autoThread = null;
            }
        }
    }

    /** Returns the name of the sub-demo. */
    public abstract String getDemoName();

    /** Returns true if the sub-demo supports auto mode. */
    public boolean supportsAutomatic() {
        return false;
    }

    public void startAnimation() {}

    public void animate() {}

    public void activate() {
        if (autoMode) {
            autoThread = new AutoThread(this);
            autoThread.start();
        }
    }

    public void deactivate() {
        if (autoMode) {
            autoThread.interrupt();
            autoThread.myStop();
            autoThread = null;
        }
    }

    /** Returns the result of processing the source image. */
    public abstract PlanarImage process();

    /** Called when the reset button is pressed. */
    public void reset() {}
}
