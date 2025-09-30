/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 *
 * This Example Content is intended to demonstrate usage of Eclipse technology. It is
 * provided to you under the terms and conditions of the Eclipse Distribution License
 * v1.0 which is available at http://www.eclipse.org/org/documents/edl-v10.php.
 */
package org.eclipse.imagen.widgets;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.image.renderable.*;
import javax.swing.*;
import javax.swing.border.*;
import org.eclipse.imagen.*;

/**
 * An output widget used for 2D sliders. Contrast subclasses javax.swing.JComponent, and can be used in any context that
 * calls for a * JComponent. It monitors resize and update events.
 *
 * <p>This method rescales the data contents of a rendered images and returns a 3band byte image suitable for display.
 * The operation cannot be reversed, so a copy of the original image is required to reset the contrast.
 *
 * <p>This method can emulate window-level, contrast stretching and low-high thresholding. Bands cannot be independently
 * altered. Data is rescaled, thresholded and reformatted into the range 0-255.
 *
 * @author Dennis Sigel
 */
public final class Contrast extends JComponent implements MouseListener, MouseMotionListener {

    /** The display target */
    ImageDisplay display;

    /** The original unmodified image */
    private PlanarImage source;

    /** The size parameters. */
    private int componentWidth;

    private int componentHeight;

    /** The slider box properties. */
    private JLabel slider;

    private boolean sliderOpaque;
    private Color sliderBorderColor;
    private Color sliderColor;

    /** The x,y center of the slider box */
    private int sliderX;

    private int sliderY;

    /** The dimensions of the slider box */
    private int sliderWidth;

    private int sliderHeight;

    /** Slider current values and limits */
    private double hValue = 0.0;

    private double vValue = 0.0;

    /** range (limits) of 2D slider */
    private double hmin = 0.0;

    private double hmax = 256.0;
    private double vmin = 0.0;
    private double vmax = 512.0;

    /** mapping between box size and limits */
    private double hslope = 1.0;

    private double hy_int = 0.0;
    private double vslope = 1.0;
    private double vy_int = 0.0;

    /** X,Y position tracker */
    private JLabel odometer;

    /** Default constructor */
    public Contrast() {
        super();
        setLayout(null);
        componentWidth = 64;
        componentHeight = 64;
        setPreferredSize(new Dimension(64, 64));
        createSliderBox(4, 4);
    }

    /** Constructs a Contrast object of a given size with no image set. */
    public Contrast(int width, int height) {
        super();
        setLayout(null);
        componentWidth = width;
        componentHeight = height;
        setPreferredSize(new Dimension(64, 64));
        createSliderBox(4, 4);
    }

    /** Constructs a Contrast object of a given size */
    public Contrast(PlanarImage d, int width, int height) {
        super();
        setLayout(null);
        source = d;
        componentWidth = width;
        componentHeight = height;
        setPreferredSize(new Dimension(width, height));
        createSliderBox(componentWidth / 16, componentHeight / 16);
        map();
    }

    public void setSource(PlanarImage d) {
        source = d;
    }

    public final JLabel getOdometer() {
        if (odometer == null) {
            odometer = new JLabel();
            odometer.setVerticalAlignment(SwingConstants.CENTER);
            odometer.setHorizontalAlignment(SwingConstants.LEFT);
            odometer.setText(" ");
            addMouseListener(this);
            addMouseMotionListener(this);
        }

        return odometer;
    }

    /* relation between box size and limits */
    private final void map() {
        Insets insets = super.getInsets();
        double nw = (double) (componentWidth - insets.left - insets.right);
        double nh = (double) (componentHeight - insets.top - insets.bottom);
        hslope = (hmax - hmin) / nw;
        hy_int = hmax - nw * hslope;
        vslope = (vmax - vmin) / nh;
        vy_int = vmax - nh * vslope;
    }

    /** Provides panning (moves slider box center location) */
    public final void setSliderLocation(int x, int y) {
        moveit(x, y);
    }

    public final Point getSliderLocation() {
        return new Point(sliderX, sliderY);
    }

    public final void setSliderOpaque(boolean v) {
        sliderOpaque = v;
        slider.setOpaque(v);
    }

    public final void setSliderColor(Color color) {
        slider.setBackground(color);
    }

    public void setSliderBorderColor(Color color) {
        slider.setBorder(new CompoundBorder(LineBorder.createBlackLineBorder(), new LineBorder(color, 1)));
    }

    public Dimension getMinimumSize() {
        return new Dimension(componentWidth, componentHeight);
    }

    public Dimension getPreferredSize() {
        return getMinimumSize();
    }

    public Dimension getMaximumSize() {
        return getMinimumSize();
    }

    public final int getWidth() {
        return componentWidth;
    }

    public final int getHeight() {
        return componentHeight;
    }

    // use for window-level
    public void setSliderLimits(double hmin, double hmax, double vmin, double vmax) {
        this.hmin = hmin;
        this.hmax = hmax;
        this.vmin = vmin;
        this.vmax = vmax;

        map();
    }

    // typically level
    public double getCurrentHValue() {
        return 0.0;
    }

    // typically window
    public double getCurrentVValue() {
        return 0.0;
    }

    /** force a fixed size. Called by the AWT. */
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, componentWidth, componentHeight);
    }

    private final void createSliderBox(int width, int height) {
        // create a custom navigator box
        slider = new JLabel();
        slider.setBorder(new CompoundBorder(LineBorder.createBlackLineBorder(), new LineBorder(Color.white, 1)));

        sliderWidth = width + 2;
        sliderHeight = height + 2;
        slider.setBounds(0, 0, sliderWidth, sliderHeight);
        slider.setOpaque(false);
        add(slider);

        // add event handlers
        addMouseListener(new MouseClickHandler());
        addMouseMotionListener(new MouseMotionHandler());

        setOpaque(true);
    }

    // moves the slider box
    class MouseClickHandler extends MouseAdapter {
        public void mousePressed(MouseEvent e) {
            int mods = e.getModifiers();
            Point p = e.getPoint();

            if ((mods & InputEvent.BUTTON1_MASK) != 0) {
                moveit(p.x, p.y);
            } else if ((mods & InputEvent.BUTTON2_MASK) != 0) {
                reset();
            }
        }

        public void mouseReleased(MouseEvent e) {}
    }

    class MouseMotionHandler extends MouseMotionAdapter {
        public void mouseDragged(MouseEvent e) {
            Point p = e.getPoint();
            int mods = e.getModifiers();

            if ((mods & InputEvent.BUTTON1_MASK) != 0) {
                moveit(p.x, p.y);
            }
        }
    }

    public final void reset() {
        moveit(componentWidth / 2, componentHeight / 2);
    }

    public final void setDisplay(ImageDisplay d) {
        display = d;
    }

    /** px and py are true mouse positions x and y is position of slider center */
    private final void moveit(int px, int py) {
        int x;
        int y;
        Insets insets = super.getInsets();

        // containment of slider box
        if (px < insets.left) {
            x = insets.left;
        } else if (px >= (componentWidth - insets.right)) {
            x = componentWidth - insets.right;
        } else {
            x = px;
        }

        if (py < insets.top) {
            y = insets.top;
        } else if (py >= (componentHeight - insets.bottom)) {
            y = componentHeight - insets.bottom;
        } else {
            y = py;
        }

        // slider center
        sliderX = x;
        sliderY = y;

        // slider origin
        slider.setLocation(x - sliderWidth / 2, y - sliderHeight / 2);

        // calculate contrast from slider position
        map();
        double nx = (double) x - insets.left;
        double ny = (double) y - insets.top;
        double window = vslope * ny + vy_int;
        double level = hslope * nx + hy_int;

        if (source != null) {
            PlanarImage dst = getWindowLevelImage(source, window, level);
            display.set(dst);
        }
    }

    public synchronized void paintComponent(Graphics g) {

        Graphics2D g2D = null;
        if (g instanceof Graphics2D) {
            g2D = (Graphics2D) g;
        } else {
            System.err.println("not a Graphic2D");
            return;
        }

        g2D.setColor(getBackground());
        g2D.fillRect(0, 0, super.getWidth(), super.getHeight());
    }

    // mouse interface
    public final void mouseEntered(MouseEvent e) {}

    public final void mouseExited(MouseEvent e) {}

    public void mousePressed(MouseEvent e) {
        Point p = e.getPoint();
        int mods = e.getModifiers();
        Insets inset = super.getInsets();

        if (odometer != null) {
            String output = " (" + (p.x - inset.left) + ", " + (p.y - inset.top) + ")";
            odometer.setText(output);
        }
    }

    public final void mouseReleased(MouseEvent e) {
        Point p = e.getPoint();

        if (odometer != null) {
            String output = " (" + p.x + ", " + p.y + ")";
            odometer.setText(output);
        }
    }

    public final void mouseClicked(MouseEvent e) {}

    public final void mouseMoved(MouseEvent e) {
        Point p = e.getPoint();

        if (odometer != null) {
            String output = " (" + p.x + ", " + p.y + ")";
            odometer.setText(output);
        }
    }

    public final void mouseDragged(MouseEvent e) {
        mousePressed(e);
    }

    /** auto contrast mapping (returns a byte image) */
    public static final PlanarImage getAutoRescaledImage(PlanarImage image) {
        ParameterBlock pb = null;
        PlanarImage dst = null;
        int bands = image.getSampleModel().getNumBands();
        int dtype = image.getSampleModel().getDataType();
        double rmin;
        double rmax;

        if (dtype == DataBuffer.TYPE_BYTE) {
            rmin = 255.0;
            rmax = 0.0;

            pb = new ParameterBlock();
            pb = new ParameterBlock();
            pb.addSource(image);
            pb.add(null);
            pb.add(1);
            pb.add(1);
            dst = ImageN.create("extrema", pb, null);
            double[][] extrema = (double[][]) dst.getProperty("extrema");
            int numBands = dst.getSampleModel().getNumBands();

            // find the overall min, max (all bands)
            for (int i = 0; i < numBands; i++) {
                if (extrema[0][i] < rmin) rmin = extrema[0][i];
                if (extrema[1][i] > rmax) rmax = extrema[1][i];
            }

            double[] slope = new double[numBands];
            double[] y_int = new double[numBands];

            for (int i = 0; i < numBands; i++) {
                slope[i] = (255.0D - 0.0D) / (rmax - rmin);
                y_int[i] = 255.0D - slope[i] * rmax;
            }

            // rescale from xxx to byte range
            pb = new ParameterBlock();
            pb.addSource(dst);
            pb.add(slope);
            pb.add(y_int);
            dst = ImageN.create("rescale", pb, null);

            // produce a byte image
            pb = new ParameterBlock();
            pb.addSource(dst);
            pb.add(DataBuffer.TYPE_BYTE);
            dst = ImageN.create("format", pb, null);
        } else if (dtype == DataBuffer.TYPE_SHORT) {
        } else if (dtype == DataBuffer.TYPE_USHORT) {
        } else if (dtype == DataBuffer.TYPE_INT) {
        } else if (dtype == DataBuffer.TYPE_FLOAT) {
        } else if (dtype == DataBuffer.TYPE_DOUBLE) {
        }

        return dst;
    }

    /** returns a 3band byte image */
    public static final PlanarImage getRescaledImage(PlanarImage image, double low, double high) {

        if (image == null) {
            return null;
        }

        ParameterBlock pb = null;
        PlanarImage dst = null;
        int bands = image.getSampleModel().getNumBands();
        int dtype = image.getSampleModel().getDataType();
        double rmin;
        double rmax;
        double slope;
        double y_int;

        if (dtype == DataBuffer.TYPE_BYTE) {
            // use a lookup table for rescaling
            if (high != low) {
                slope = 256.0 / (high - low);
                y_int = 256.0 - slope * high;
            } else {
                slope = 0.0;
                y_int = 0.0;
            }

            byte lut[][] = new byte[bands][256];

            for (int i = 0; i < 256; i++) {
                for (int j = 0; j < bands; j++) {
                    int value = (int) (slope * i + y_int);

                    if (value < (int) low) {
                        value = 0;
                    } else if (value > (int) high) {
                        value = 255;
                    } else {
                        value &= 0xFF;
                    }

                    lut[j][i] = (byte) value;
                }
            }

            LookupTableImageN lookup = new LookupTableImageN(lut);

            pb = new ParameterBlock();
            pb.addSource(image);
            pb.add(lookup);
            dst = ImageN.create("lookup", pb, null);
        } else if (dtype == DataBuffer.TYPE_SHORT || dtype == DataBuffer.TYPE_USHORT) {
            // use a lookup table for rescaling
            if (high != low) {
                slope = 256.0 / (high - low);
                y_int = 256.0 - slope * high;
            } else {
                slope = 0.0;
                y_int = 0.0;
            }

            byte lut[][] = new byte[bands][65536];

            for (int i = 0; i < 65535; i++) {
                for (int j = 0; j < bands; j++) {
                    int value = (int) (slope * i + y_int);

                    if (dtype == DataBuffer.TYPE_USHORT) {
                        value &= 0xFFFF;
                    }

                    if (value < (int) low) {
                        value = 0;
                    } else if (value > (int) high) {
                        value = 255;
                    } else {
                        value &= 0xFF;
                    }

                    lut[j][i] = (byte) value;
                }
            }

            LookupTableImageN lookup = new LookupTableImageN(lut);

            pb = new ParameterBlock();
            pb.addSource(image);
            pb.add(lookup);
            dst = ImageN.create("lookup", pb, null);
        } else if (dtype == DataBuffer.TYPE_INT || dtype == DataBuffer.TYPE_FLOAT || dtype == DataBuffer.TYPE_DOUBLE) {

            // use the rescale and format ops
            if (high != low) {
                slope = 256.0 / (high - low);
                y_int = 256.0 - slope * high;
            } else {
                slope = 0.0;
                y_int = 0.0;
            }

            pb = new ParameterBlock();
            pb.addSource(image);
            pb.add(slope);
            pb.add(y_int);
            dst = ImageN.create("rescale", pb, null);

            // produce a byte image
            pb = new ParameterBlock();
            pb.addSource(dst);
            pb.add(DataBuffer.TYPE_BYTE);
            dst = ImageN.create("format", pb, null);
        }

        return dst;
    }

    public static final PlanarImage getWindowLevelImage(PlanarImage image, double window, double level) {

        double low = level - window / 2.0;
        double high = level + window / 2.0;

        return getRescaledImage(image, low, high);
    }
}
