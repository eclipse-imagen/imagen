/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.eclipse.imagen.widgets;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.awt.geom.*;
import org.eclipse.imagen.*;
import javax.swing.*;

/**
 * An output widget for a PlanarImage.  ImageDisplay subclasses
 * javax.swing.JComponent, and can be used in any context that calls for a
 * JComponent.  It monitors resize and update events and automatically
 * requests tiles from its source on demand.
 *
 * <p> Due to the limitations of BufferedImage, only TYPE_BYTE of band
 * 1, 2, 3, 4, and TYPE_USHORT of band 1, 2, 3 images can be displayed
 * using this widget.
 *
 * @author Daniel Rice
 * @author Dennis Sigel
 */

public class ImageDisplay extends JComponent
                          implements MouseListener, MouseMotionListener {

    /** The source PlanarImage. */
    protected PlanarImage source;
    /** The image's SampleModel. */
    protected SampleModel sampleModel;
    /** The image's ColorModel or one we supply. */
    protected ColorModel colorModel;

    /** The image's min X tile. */
    protected int minTileX;
    /** The image's max X tile. */
    protected int maxTileX;
    /** The image's min Y tile. */
    protected int minTileY;
    /** The image's max Y tile. */
    protected int maxTileY;
    /** The image's tile width. */
    protected int tileWidth;
    /** The image's tile height. */
    protected int tileHeight;
    /** The image's tile grid X offset. */
    protected int tileGridXOffset;
    /** The image's tile grid Y offset. */
    protected int tileGridYOffset;

    protected int originX = 0;
    protected int originY = 0;

    protected int shift_x = 0;
    protected int shift_y = 0;

    protected JLabel odometer = null;
    protected int componentWidth;
    protected int componentHeight;

    /** Brightness control */
    protected BufferedImageOp biop = null;
    protected boolean brightnessEnabled = false;
    protected int brightness = 0;
    protected byte[] lutData;

    /** Initializes the ImageDisplay. */
    private synchronized void initialize() {
        if ( source == null ) return;

        componentWidth  = source.getWidth();
        componentHeight = source.getHeight();

        setPreferredSize(new Dimension(componentWidth, componentHeight));

        this.sampleModel = source.getSampleModel();

        // First check whether the opimage has already set a suitable ColorModel
        this.colorModel = source.getColorModel();
        if (this.colorModel == null) {
            // If not, then create one.
            this.colorModel = PlanarImage.createColorModel(this.sampleModel);
            if (this.colorModel == null) {
                throw new IllegalArgumentException("no color model");
            }
        }

        minTileX = source.getMinTileX();
        maxTileX = source.getMinTileX() + source.getNumXTiles() - 1;
        minTileY = source.getMinTileY();
        maxTileY = source.getMinTileY() + source.getNumYTiles() - 1;
        tileWidth = source.getTileWidth();
        tileHeight = source.getTileHeight();
        tileGridXOffset = source.getTileGridXOffset();
        tileGridYOffset = source.getTileGridYOffset();
    }

    /**
     * Default constructor
     */
    public ImageDisplay() {
        super();
        source = null;

        lutData = new byte[256];

        for ( int i = 0; i < 256; i++ ) {
            lutData[i] = (byte)i;
        }

        componentWidth  = 64;
        componentHeight = 64;
        setPreferredSize(new Dimension(componentWidth, componentHeight));
        setOrigin(0, 0);
        setBrightnessEnabled(true);
    }

    /** 
     * Constructs an ImageDisplay to display a PlanarImage.
     *
     * @param source a PlanarImage to be displayed.
     */
    public ImageDisplay(PlanarImage im) {
        super();
        source = im;
        initialize();

        lutData = new byte[256];

        for ( int i = 0; i < 256; i++ ) {
            lutData[i] = (byte)i;
        }

        setOrigin(0, 0);
        setBrightnessEnabled(true);
    }

    /**
     * Constructs an ImageDisplay of fixed size (no image)
     *
     * @param width - display width
     * @param height - display height
     */
    public ImageDisplay(int width, int height) {
        super();
        source = null;

        lutData = new byte[256];

        for ( int i = 0; i < 256; i++ ) {
            lutData[i] = (byte)i;
        }

        componentWidth  = width;
        componentHeight = height;
        setPreferredSize(new Dimension(componentWidth, componentHeight));
        setOrigin(0, 0);
        setBrightnessEnabled(true);
    }

    /** Changes the source image to a new PlanarImage. */
    public void set(PlanarImage im) {
        source = im;
        initialize();
        repaint();
    }

    public void set(PlanarImage im, int x, int y) {
        source = im;
        initialize();
        setOrigin(x, y);
    }

    public PlanarImage getImage() {
        return source;
    }

    public final JLabel getOdometer() {
        if ( odometer == null ) {
            odometer = new JLabel();
            odometer.setVerticalAlignment(SwingConstants.CENTER);
            odometer.setHorizontalAlignment(SwingConstants.LEFT);
            odometer.setText(" ");
            addMouseListener(this);
            addMouseMotionListener(this);
        }

        return odometer;
    }

    /** Provides panning */
    public final void setOrigin(int x, int y) {
        // shift to box origin
        originX = -x;
        originY = -y;
        repaint();
    }

    public int getXOrigin() {
        return originX;
    }

    public int getYOrigin() {
        return originY;
    }

    /** Records a new size.  Called by the AWT. */
    public void setBounds(int x, int y, int width, int height) {
        Insets insets = getInsets();
        int w;
        int h;

        if ( source == null ) {
            w = width;
            h = height;
        } else {
            w = source.getWidth();
            h = source.getHeight();

            if ( width < w ) {
                w = width;
            }

            if ( height < h ) {
                h = height;
            }
        }

        componentWidth  = w + insets.left + insets.right;
        componentHeight = h + insets.top  + insets.bottom;

        super.setBounds(x+shift_x, y+shift_y, componentWidth, componentHeight);
    }

    public void setLocation(int x, int y) {
        shift_x = x;
        shift_y = y;
        super.setLocation(x, y);
    }

    private final int XtoTileX(int x) {
        return (int) Math.floor((double) (x - tileGridXOffset)/tileWidth);
    }

    private final int YtoTileY(int y) {
        return (int) Math.floor((double) (y - tileGridYOffset)/tileHeight);
    }

    private final int TileXtoX(int tx) {
        return tx*tileWidth + tileGridXOffset;
    }

    private final int TileYtoY(int ty) {
        return ty*tileHeight + tileGridYOffset;
    }

    private static final void debug(String msg) {
        System.out.println(msg);
    }

    private final byte clampByte(int v) {
        if ( v > 255 ) {
            return (byte)255;
        } else if ( v < 0 ) {
            return (byte)0;
        } else {
            return (byte)v;
        }
    }

    private final void setBrightnessEnabled(boolean v) {
        brightnessEnabled = v;

        if ( brightnessEnabled == true ) {
            biop = new AffineTransformOp(new AffineTransform(),
                                         AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        } else {
            biop = null;
        }
    }

    public final boolean getBrightnessEnabled() {
        return brightnessEnabled;
    }

    public final void setBrightness(int b) {
        if ( b != brightness && brightnessEnabled == true ) {
            for ( int i = 0; i < 256; i++ ) {
                lutData[i] = clampByte(i+b);
            }

            repaint();
        }
    }

    /**
     * Paint the image onto a Graphics object.  The painting is
     * performed tile-by-tile, and includes a grey region covering the
     * unused portion of image tiles as well as the general
     * background.  At this point the image must be byte data.
     */
    public synchronized void paintComponent(Graphics g) {

        Graphics2D g2D = null;
        if (g instanceof Graphics2D) {
            g2D = (Graphics2D)g;
        } else {
            return;
        }

        // if source is null, it's just a component
        if ( source == null ) {
            g2D.setColor(getBackground());
            g2D.fillRect(0, 0, componentWidth, componentHeight);
            return;
        }

        int transX = -originX;
        int transY = -originY;

        // Get the clipping rectangle and translate it into image coordinates.
        Rectangle clipBounds = g.getClipBounds();

        if (clipBounds == null) {
            clipBounds = new Rectangle(0, 0, componentWidth, componentHeight);
        }

        // clear the background (clip it) [minimal optimization here]
        if ( transX > 0 ||
             transY > 0 ||
             transX < (componentWidth-source.getWidth()) ||
             transY < (componentHeight-source.getHeight())) {
            g2D.setColor(getBackground());
            g2D.fillRect(0, 0, componentWidth, componentHeight);
        }

        clipBounds.translate(-transX, -transY);

        // Determine the extent of the clipping region in tile coordinates.
        int txmin, txmax, tymin, tymax;
        int ti, tj;

        txmin = XtoTileX(clipBounds.x);
        txmin = Math.max(txmin, minTileX);
        txmin = Math.min(txmin, maxTileX);

        txmax = XtoTileX(clipBounds.x + clipBounds.width - 1);
        txmax = Math.max(txmax, minTileX);
        txmax = Math.min(txmax, maxTileX);

        tymin = YtoTileY(clipBounds.y);
        tymin = Math.max(tymin, minTileY);
        tymin = Math.min(tymin, maxTileY);

        tymax = YtoTileY(clipBounds.y + clipBounds.height - 1);
        tymax = Math.max(tymax, minTileY);
        tymax = Math.min(tymax, maxTileY);
        Insets insets = getInsets();

        // Loop over tiles within the clipping region
        for (tj = tymin; tj <= tymax; tj++) {
            for (ti = txmin; ti <= txmax; ti++) {
                int tx = TileXtoX(ti);
                int ty = TileYtoY(tj);

                Raster tile = source.getTile(ti, tj);
                if ( tile != null ) {
                    DataBuffer dataBuffer = tile.getDataBuffer();

                    WritableRaster wr = tile.createWritableRaster(sampleModel,
                                                                  dataBuffer,
                                                                  null);

                    BufferedImage bi = new BufferedImage(colorModel,
                                                         wr,
                                                         colorModel.isAlphaPremultiplied(),
                                                         null);

                    // correctly handles band offsets
                    if ( brightnessEnabled == true ) {
                        SampleModel sm = sampleModel.createCompatibleSampleModel(tile.getWidth(),
                                                                                 tile.getHeight());

                        WritableRaster raster = RasterFactory.createWritableRaster(sm, null);

                        BufferedImage bimg = new BufferedImage(colorModel,
                                                               raster,
                                                               colorModel.isAlphaPremultiplied(),
                                                               null);

                        // don't move this code
                        ByteLookupTable lutTable = new ByteLookupTable(0, lutData);
                        LookupOp lookup = new LookupOp(lutTable, null);
                        lookup.filter(bi, bimg);

                        g2D.drawImage(bimg, biop, tx+transX+insets.left, ty+transY+insets.top);
                    } else {
                        AffineTransform transform;

                        transform = AffineTransform.getTranslateInstance(tx + transX + insets.left,
                                                                         ty + transY + insets.top);

                        g2D.drawRenderedImage(bi, transform);
                    }
                }
            }
        }
    }

    // mouse interface
    public final void mouseEntered(MouseEvent e) {
    }

    public final void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        Point p = e.getPoint();
        int mods = e.getModifiers();

        if ( odometer != null ) {
             String output = " (" + p.x + ", " + p.y + ")";
             odometer.setText(output);
        }
    }

    public final void mouseReleased(MouseEvent e) {
        Point p = e.getPoint();

        if ( odometer != null ) {
             String output = " (" + p.x + ", " + p.y + ")";
             odometer.setText(output);
        }
    }

    public final void mouseClicked(MouseEvent e) {
    }

    public final void mouseMoved(MouseEvent e) {
        Point p = e.getPoint();

        if ( odometer != null ) {
             String output = " (" + p.x + ", " + p.y + ")";
             odometer.setText(output);
        }
    }

    public final void mouseDragged(MouseEvent e) {
        mousePressed(e);
    }
}
