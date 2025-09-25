/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 *
 * This Example Content is intended to demonstrate usage of Eclipse technology. It is
 * provided to you under the terms and conditions of the Eclipse Distribution License
 * v1.0 which is available at http://www.eclipse.org/org/documents/edl-v10.php.
 */
package org.eclipse.imagen.widgets;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.awt.image.renderable.*;
import javax.swing.*;
import org.eclipse.imagen.*;

/**
 * An output widget for a RenderableImage. RenderableDisplay subclasses javax.swing.JComponent, and can be used in any
 * context that calls for a JComponent. It monitors resize and update events and automatically requests tiles from its
 * source on demand.
 *
 * <p>Due to the limitations of BufferedImage, only TYPE_BYTE of band 1, 2, 3, 4, and TYPE_USHORT of band 1, 2, 3 images
 * can be displayed using this widget.
 *
 * @author Daniel Rice
 * @author Dennis Sigel
 */
public class RenderableDisplay extends JComponent {

    RenderableImage source;
    PlanarImage rendering;
    float aspect;

    int tileWidth;
    int tileHeight;
    int tileGridXOffset;
    int tileGridYOffset;
    int minTileX;
    int maxTileX;
    int minTileY;
    int maxTileY;
    SampleModel sampleModel;
    ColorModel colorModel;

    int componentWidth;
    int componentHeight;

    public RenderableDisplay(RenderableImage im) {
        source = im;
        this.aspect = source.getWidth() / source.getHeight();
    }

    public Dimension getPreferredSize() {
        return new Dimension((int) (100 * aspect + 0.5), 100);
    }

    public void setBounds(int x, int y, int width, int height) {

        if ((float) width / height > aspect) {
            width = (int) (height * aspect);
        } else {
            height = (int) (width / aspect);
        }

        scale(width, height, true);
        super.setBounds(x, y, width, height);

        componentWidth = width;
        componentHeight = height;

        tileWidth = rendering.getTileWidth();
        tileHeight = rendering.getTileHeight();

        tileGridXOffset = rendering.getTileGridXOffset();
        tileGridYOffset = rendering.getTileGridYOffset();

        minTileX = rendering.getMinTileX();
        maxTileX = minTileX + rendering.getNumXTiles() - 1;
        minTileY = rendering.getMinTileY();
        maxTileY = minTileY + rendering.getNumYTiles() - 1;

        sampleModel = rendering.getSampleModel();
        colorModel = rendering.getColorModel();
    }

    public void scale(int width, int height, boolean scaleOnly) {

        if (scaleOnly) {
            RenderingHints hints = new RenderingHints(
                    ImageN.KEY_INTERPOLATION, Interpolation.getInstance(Interpolation.INTERP_BILINEAR));
            rendering = (PlanarImage) source.createScaledRendering(width, height, hints);
        } else {
            AffineTransform transform = new AffineTransform();
            transform.translate(-source.getMinX(), -source.getMinY());
            transform.scale((double) width / source.getWidth(), (double) height / source.getHeight());

            RenderContext renderContext = new RenderContext(transform);
            rendering = (PlanarImage) source.createRendering(renderContext);
        }
    }

    private int XtoTileX(int x) {
        return (int) Math.floor((double) (x - tileGridXOffset) / tileWidth);
    }

    private int YtoTileY(int y) {
        return (int) Math.floor((double) (y - tileGridYOffset) / tileHeight);
    }

    private int TileXtoX(int tx) {
        return tx * tileWidth + tileGridXOffset;
    }

    private int TileYtoY(int ty) {
        return ty * tileHeight + tileGridYOffset;
    }

    public void paintComponent(Graphics g) {

        Graphics2D g2D = (Graphics2D) g;

        if (rendering == null) {
            g2D.setColor(getBackground());
            g2D.fillRect(0, 0, componentWidth, componentHeight);
            return;
        }

        // Get the clipping rectangle and translate it into image coordinates.
        Rectangle clipBounds = g.getClipBounds();
        if (clipBounds == null) {
            clipBounds = new Rectangle(0, 0, componentWidth, componentHeight);
        }

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

        // Loop over tiles within the clipping region
        for (tj = tymin; tj <= tymax; tj++) {
            for (ti = txmin; ti <= txmax; ti++) {
                int tx = TileXtoX(ti);
                int ty = TileYtoY(tj);

                Raster tile = rendering.getTile(ti, tj);
                if (tile != null) {
                    DataBuffer dataBuffer = tile.getDataBuffer();

                    WritableRaster wr = tile.createWritableRaster(sampleModel, dataBuffer, null);

                    BufferedImage bi = new BufferedImage(colorModel, wr, false, null);

                    AffineTransform transform = AffineTransform.getTranslateInstance(tx, ty);
                    g2D.drawRenderedImage(bi, transform);
                }
            }
        }
    }
}
