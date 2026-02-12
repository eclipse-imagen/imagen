/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 *
 * This Example Content is intended to demonstrate usage of Eclipse technology. It is
 * provided to you under the terms and conditions of the Eclipse Distribution License
 * v1.0 which is available at http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */
package org.eclipse.imagen.widgets;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.border.*;
import org.eclipse.imagen.*;

/**
 * An output widget used for panning. Panner subclasses javax.swing.JComponent, and can be used in any context that
 * calls for a * JComponent. It monitors resize and update events and automatically requests tiles from its source on
 * demand.
 *
 * <p>Due to the limitations of BufferedImage, only TYPE_BYTE of band 1, 2, 3, 4, and TYPE_USHORT of band 1, 2, 3 images
 * can be displayed using this widget.
 *
 * @author Dennis Sigel
 */
public final class Panner extends JComponent implements MouseListener, MouseMotionListener {

    /** The TiledImage used for background. */
    private TiledImage pannerImage;

    private SampleModel sampleModel;
    private ColorModel colorModel;

    /** The image's tile parameters. */
    private int minTileX;

    private int maxTileX;
    private int minTileY;
    private int maxTileY;
    private int tileWidth;
    private int tileHeight;
    private int tileGridXOffset;
    private int tileGridYOffset;

    /** The panner's parameters. */
    private int pannerWidth;

    private int pannerHeight;

    /** The slider box properties. */
    private JLabel slider;

    private boolean sliderOpaque;
    private Color sliderBorderColor;

    /** The x,y center of the slider box */
    private int sliderX;

    private int sliderY;

    /** The dimensions of the slider box */
    private int sliderWidth;

    private int sliderHeight;

    /** scale from panner to scroll object */
    private float scale;

    /** Range of slider mapped values */
    private float pannerMinH;

    private float pannerMaxH;
    private float pannerMinV;
    private float pannerMaxV;

    /** Slider current values */
    private float hValue;

    private float vValue;

    /** The object to control */
    private JComponent scrollObject = null;

    /** X,Y position tracker */
    private JLabel odometer;

    /** enable or disabled scaling */
    private boolean enableScale = false;

    /** Initializes the Panner. */
    private synchronized void initialize(JComponent item, PlanarImage image, PlanarImage thumb, int maxSize) {
        scrollObject = item;
        PlanarImage temp = null;

        if (image != null) {
            Rectangle rect = item.getBounds();

            float scale_x;
            float scale_y;
            float imageWidth = (float) image.getWidth();
            float imageHeight = (float) image.getHeight();

            if (imageWidth >= (float) rect.height) {
                scale_x = (float) maxSize / imageWidth;
                scale_y = scale_x;
                pannerWidth = maxSize;
                pannerHeight = (int) (imageHeight * scale_y);
            } else {
                scale_y = (float) maxSize / imageHeight;
                scale_x = scale_y;
                pannerHeight = maxSize;
                pannerWidth = (int) (imageWidth * scale_x);
            }

            scale = 1.0F / scale_x;

            // this must be put into a tiled image or scale gets
            // called on every repaint.
            if (enableScale == true) {
                InterpolationBilinear interp = new InterpolationBilinear();
                temp = ImageN.create("scale", image, scale_x, scale_y, 0.0F, 0.0F, interp);
            } else {
                temp = thumb;
            }

            sampleModel = temp.getSampleModel();

            // First check whether the opimage has already set a suitable ColorModel
            colorModel = temp.getColorModel();
            if (colorModel == null) {
                // If not, then create one.
                colorModel = PlanarImage.createColorModel(sampleModel);
                if (colorModel == null) {
                    throw new IllegalArgumentException("no color model");
                }
            }

            pannerImage = new TiledImage(0, 0, pannerWidth, pannerHeight, 0, 0, sampleModel, colorModel);

            pannerImage.set(temp);

            minTileX = pannerImage.getMinTileX();
            maxTileX = pannerImage.getMinTileX() + pannerImage.getNumXTiles() - 1;
            minTileY = pannerImage.getMinTileY();
            maxTileY = pannerImage.getMinTileY() + pannerImage.getNumYTiles() - 1;
            tileWidth = pannerImage.getTileWidth();
            tileHeight = pannerImage.getTileHeight();
            tileGridXOffset = pannerImage.getTileGridXOffset();
            tileGridYOffset = pannerImage.getTileGridYOffset();

            int tw = (int) ((float) rect.width * scale_x);
            int th = (int) ((float) rect.height * scale_y);
            createSliderBox(tw, th);
        } else {
            createSliderBox(32, 32);
        }

        sliderX = 0;
        sliderY = 0;
        odometer = null;

        repaint();
    }

    /** Default constructor */
    public Panner() {
        super();
        setLayout(null);
        setPreferredSize(new Dimension(64, 64));
        pannerWidth = 64;
        pannerHeight = 64;
        createSliderBox(4, 4);
    }

    /**
     * Constructs a Panner to display a scaled PlanarImage.
     *
     * @param Swing object to be controlled.
     * @param image a PlanarImage to be displayed.
     * @param Max width or height for scaled image.
     */
    public Panner(JComponent item, PlanarImage image, int maxSize) {
        super();
        setLayout(null);

        if (item == null) {
            return;
        }

        enableScale = true;
        initialize(item, image, null, maxSize);
    }

    /** Constructs a Panner with no image */
    public Panner(int panner_width, int panner_height) {
        super();
        setLayout(null);
        setPreferredSize(new Dimension(panner_width, panner_height));
        pannerWidth = panner_width;
        pannerHeight = panner_height;
        createSliderBox(pannerWidth / 16, pannerHeight / 16);
    }

    /** Construct a Panner with a pre-scaled image */
    public Panner(JComponent item, PlanarImage image, PlanarImage thumb) {
        super();
        setLayout(null);

        if (item == null) {
            return;
        }

        int maxSize = thumb.getWidth();

        enableScale = false;
        initialize(item, image, thumb, maxSize);
    }

    /** Changes the pannerImage image to a new PlanarImage. */
    public synchronized void set(PlanarImage im, int maxSize, int vWidth, int vHeight) {
        /*
                initialize(im, maxSize, vWidth, vHeight);
        */
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

    public final void setScrollObject(JComponent o) {
        scrollObject = o;
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

    public void setSliderBorderColor(Color color) {
        slider.setBorder(new CompoundBorder(LineBorder.createBlackLineBorder(), new LineBorder(color, 1)));
    }

    public Dimension getMinimumSize() {
        return new Dimension(pannerWidth, pannerHeight);
    }

    public Dimension getPreferredSize() {
        return getMinimumSize();
    }

    public Dimension getMaximumSize() {
        return getMinimumSize();
    }

    public final int getWidth() {
        return pannerWidth;
    }

    public final int getHeight() {
        return pannerHeight;
    }

    /** force a fixed size. Called by the AWT. */
    public void setBounds(int x, int y, int width, int height) {
        if (pannerImage != null) {
            pannerWidth = pannerImage.getWidth();
            pannerHeight = pannerImage.getHeight();
            super.setBounds(x, y, pannerWidth, pannerHeight);
        } else {
            super.setBounds(x, y, pannerWidth, pannerHeight);
        }
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

    private final void moveit(int px, int py) {
        Insets inset = super.getInsets();

        int pw = sliderWidth / 2;
        int ph = sliderHeight / 2;
        int x = px - pw;
        int y = py - ph;

        if (px < inset.left) x = -pw + inset.left;
        if (py < inset.top) y = -ph + inset.top;
        if (px >= (pannerWidth - inset.right)) x = pannerWidth - pw - inset.right;
        if (py >= (pannerHeight - inset.bottom)) y = pannerHeight - ph - inset.bottom;

        // slider center
        sliderX = px;
        sliderY = py;

        // slider origin
        slider.setLocation(x, y);

        if (scrollObject != null) {
            int sx;
            int sy;

            sx = (int) ((float) x * scale + .5F);
            sy = (int) ((float) y * scale + .5F);

            if (scrollObject instanceof ImageDisplay) {
                ((ImageDisplay) scrollObject).setOrigin(-sx, -sy);
            } else {
                scrollObject.setLocation(-sx, -sy);
            }
        }
    }

    private final int XtoTileX(int x) {
        return (int) Math.floor((double) (x - tileGridXOffset) / tileWidth);
    }

    private final int YtoTileY(int y) {
        return (int) Math.floor((double) (y - tileGridYOffset) / tileHeight);
    }

    private final int TileXtoX(int tx) {
        return tx * tileWidth + tileGridXOffset;
    }

    private final int TileYtoY(int ty) {
        return ty * tileHeight + tileGridYOffset;
    }

    private static final void debug(String msg) {
        System.out.println(msg);
    }

    /**
     * Paint the image onto a Graphics object. The painting is performed tile-by-tile, and includes a grey region
     * covering the unused portion of image tiles as well as the general background.
     */
    public synchronized void paintComponent(Graphics g) {

        Graphics2D g2D = null;
        if (g instanceof Graphics2D) {
            g2D = (Graphics2D) g;
        } else {
            System.err.println("not a Graphic2D");
            return;
        }

        // if pannerImage is null, it's just a component
        if (pannerImage == null) {
            g2D.setColor(getBackground());
            g2D.fillRect(0, 0, super.getWidth(), super.getHeight());
            return;
        }

        // sigel - optimize later
        if (slider != null && scrollObject != null) {
            Rectangle rect = scrollObject.getBounds();
            sliderWidth = (int) ((float) rect.width / scale + .5);
            sliderHeight = (int) ((float) rect.height / scale + .5);
            slider.setSize(new Dimension(sliderWidth, sliderHeight));
        }

        // Get the clipping rectangle and translate it into image coordinates.
        Rectangle clipBounds = g.getClipBounds();

        // Determine the extent of the clipping region in tile coordinates.
        int txmin, txmax, tymin, tymax;

        txmin = XtoTileX(clipBounds.x);
        txmin = maxInt(txmin, minTileX);
        txmin = minInt(txmin, maxTileX);

        txmax = XtoTileX(clipBounds.x + clipBounds.width - 1);
        txmax = maxInt(txmax, minTileX);
        txmax = minInt(txmax, maxTileX);

        tymin = YtoTileY(clipBounds.y);
        tymin = maxInt(tymin, minTileY);
        tymin = minInt(tymin, maxTileY);

        tymax = YtoTileY(clipBounds.y + clipBounds.height - 1);
        tymax = maxInt(tymax, minTileY);
        tymax = minInt(tymax, maxTileY);

        int xmin = pannerImage.getMinX();
        int xmax = pannerImage.getMinX() + pannerImage.getWidth();
        int ymin = pannerImage.getMinY();
        int ymax = pannerImage.getMinY() + pannerImage.getHeight();

        // Loop over tiles within the clipping region
        for (int tj = tymin; tj <= tymax; tj++) {
            for (int ti = txmin; ti <= txmax; ti++) {
                int tx = TileXtoX(ti);
                int ty = TileYtoY(tj);

                Raster tile = pannerImage.getTile(ti, tj);
                if (tile != null) {
                    DataBuffer dataBuffer = tile.getDataBuffer();

                    WritableRaster wr = tile.createWritableRaster(sampleModel, dataBuffer, null);

                    BufferedImage bi = new BufferedImage(colorModel, wr, colorModel.isAlphaPremultiplied(), null);

                    AffineTransform transform = AffineTransform.getTranslateInstance(tx, ty);

                    g2D.drawRenderedImage(bi, transform);
                }
            }
        }
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

    // speed up math min and max by inlining
    private final int maxInt(int a, int b) {
        return a > b ? a : b;
    }

    private final int minInt(int a, int b) {
        return (a <= b) ? a : b;
    }
}
