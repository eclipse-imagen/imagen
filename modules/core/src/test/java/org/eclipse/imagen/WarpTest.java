/* Copyright (c) 2025 Daniele Romagnoli and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution and is available at
 * http://www.opensource.org/licenses/apache2.0.php.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.eclipse.imagen;

import static org.junit.Assert.*;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import org.junit.Test;

/** Unit tests for the Warp class. */
public class WarpTest {

    private static final float DELTA = 1E-6f;
    private static final float HALF_PIXEL = 0.5f;

    /** Build a 1×1 identity grid: points at (0,0), (1,0), (0,1), (1,1). */
    private static WarpGrid getIdentityGrid() {
        // xStart=0,yStart=0,xStep=1,yStep=1,xNumCells=1,yNumCells=1
        // warpPositions are (x,y) pairs for the 4 grid points in scanline order.
        float[] pos = new float[] {
            0f, 0f, // (0,0)
            1f, 0f, // (1,0)
            0f, 1f, // (0,1)
            1f, 1f // (1,1)
        };
        return getTranslatedGrid(pos);
    }

    private static WarpGrid getTranslatedGrid(float[] translated) {
        return new WarpGrid(0, 1, 1, 0, 1, 1, translated);
    }

    private static WarpAffine getIdentityWarp() {
        return new WarpAffine(new AffineTransform(1, 0, 0, 1, 0, 0));
    }

    private static WarpAffine getScale2Warp() {
        return new WarpAffine(AffineTransform.getScaleInstance(2.0, 2.0));
    }

    @Test
    /** Test basic point and rectangle mapping with an identity warp. */
    public void testWarpIdentityMapping() {
        WarpAffine warp = getIdentityWarp();

        // Points
        Point2D src = new Point2D.Float(12.5f, 7.25f);
        Point2D dst = warp.mapDestPoint(src); // identity -> same
        assertEquals((float) src.getX(), (float) dst.getX(), DELTA);
        assertEquals((float) src.getY(), (float) dst.getY(), DELTA);

        Point2D back = warp.mapSourcePoint(dst); // identity -> same
        assertEquals((float) src.getX(), (float) back.getX(), DELTA);
        assertEquals((float) src.getY(), (float) back.getY(), DELTA);

        // Rectangles
        Rectangle r = new Rectangle(3, 4, 5, 6);
        Rectangle rToSrc = warp.mapSourceRect(r);
        Rectangle rToDst = warp.mapDestRect(r);
        assertEquals(r, rToSrc);
        assertEquals(r, rToDst);
    }

    @Test
    /** Test basic rectangle warping with an identity warp. */
    public void testWarpRect() {
        WarpAffine warp = getIdentityWarp();

        int x = 5, y = 10, w = 3, h = 2;
        float[] coords = new float[w * h * 2];
        warp.warpRect(x, y, w, h, coords);

        assertEquals(w * h * 2, coords.length);

        // coordinates are written in scanline order: (x+i, y+j)
        // first pixel (x,y)
        assertEquals(x, coords[0], DELTA);
        assertEquals(y, coords[1], DELTA);

        // last pixel in first row (x+w-1, y)
        int idxLastFirstRow = (w - 1) * 2;
        assertEquals(x + w - 1, coords[idxLastFirstRow], DELTA);
        assertEquals(y, coords[idxLastFirstRow + 1], DELTA);

        // first pixel in second row (x, y+1)
        int idxFirstSecondRow = w * 2;
        assertEquals(x, coords[idxFirstSecondRow], DELTA);
        assertEquals(y + 1, coords[idxFirstSecondRow + 1], DELTA);

        // last pixel overall (x+w-1, y+h-1)
        int idxLast = (w * h - 1) * 2;
        assertEquals(x + w - 1, coords[idxLast], DELTA);
        assertEquals(y + h - 1, coords[idxLast + 1], DELTA);
    }

    @Test
    /** Test sparse rectangle warping with an identity warp. */
    public void testBasicWarpSparseRect() {
        WarpAffine warp = getIdentityWarp();

        int x = 10, y = 20, w = 4, h = 4;
        int periodX = 2, periodY = 2; // sample every other pixel
        // expected sample grid: (x, y), (x+2, y), (x, y+2), (x+2, y+2) -> 4 samples
        float[] coords = new float[(w / periodX) * (h / periodY) * 2];
        warp.warpSparseRect(x, y, w, h, periodX, periodY, coords);
        assertEquals(8, coords.length);

        // (x,y)
        assertEquals(x, coords[0], DELTA);
        assertEquals(y, coords[1], DELTA);
        // (x+2,y)
        assertEquals(x + 2, coords[2], DELTA);
        assertEquals(y, coords[3], DELTA);
        // (x, y+2)
        assertEquals(x, coords[4], DELTA);
        assertEquals(y + 2, coords[5], DELTA);
        // (x+2, y+2)
        assertEquals(x + 2, coords[6], DELTA);
        assertEquals(y + 2, coords[7], DELTA);
    }

    @Test
    /** Test warping of single points with a scaling 2 warp. */
    public void testWarpPoint() {
        WarpAffine warp = getScale2Warp();

        int x = 3, y = 4;
        float[] xy = new float[2];
        warp.warpPoint(x, y, xy);

        // Taking into account half-pixel center convention
        float expectedX = 2f * x + HALF_PIXEL;
        float expectedY = 2f * y + HALF_PIXEL;
        assertEquals(expectedX, xy[0], DELTA);
        assertEquals(expectedY, xy[1], DELTA);

        x = 7;
        y = 9;
        float[] fxy = new float[2];
        warp.warpPoint(x, y, fxy);

        int[] ixy = new int[2];
        warp.warpPoint(x, y, 0, 0, ixy);
        assertEquals((int) fxy[0], ixy[0]);
        assertEquals((int) fxy[1], ixy[1]);
    }

    @Test
    /** Test sparse rectangle warping with a scaling 2 warp. */
    public void testSparseRect() {
        WarpAffine warp = getScale2Warp();

        int x = 1, y = 2, w = 2, h = 2, px = 1, py = 1;
        float[] coords = new float[w * h * 2];
        warp.warpSparseRect(x, y, w, h, px, py, coords);

        int idx = 0;
        int[] tmp = new int[2];
        for (int j = 0; j < h; j += py) {
            for (int i = 0; i < w; i += px) {
                int sx = x + i;
                int sy = y + j;
                warp.warpPoint(sx, sy, 0, 0, tmp);
                assertEquals(tmp[0] + HALF_PIXEL, coords[idx++], DELTA);
                assertEquals(tmp[1] + HALF_PIXEL, coords[idx++], DELTA);
            }
        }
    }

    @Test
    /** Test mapping of a rectangle. */
    public void testMapDestRect() {
        WarpAffine warp = getScale2Warp();

        Rectangle r = new Rectangle(1, 2, 3, 4);
        Rectangle mapped = warp.mapDestRect(r);

        // With pixel-center convention under 2x scale:
        // x' = floor(2*x + 0.5), y' = floor(2*y + 0.5)
        // w' = 2*w + 1,         h' = 2*h + 1
        Rectangle expected = new Rectangle(2, 4, 2 * r.width + 1, 2 * r.height + 1);
        assertEquals(expected, mapped);
    }

    @Test
    /** Test mapping of a single point. */
    public void testMapDestPoint() {
        WarpAffine warp = getScale2Warp();

        Point2D p = new Point2D.Float(5f, 7f);
        Point2D mapped = warp.mapDestPoint(p);

        assertEquals(2f * (float) p.getX() + HALF_PIXEL, (float) mapped.getX(), DELTA);
        assertEquals(2f * (float) p.getY() + HALF_PIXEL, (float) mapped.getY(), DELTA);
    }

    @Test
    /** Basic tests on a WarpGrid. */
    public void testWarpGridBase() {
        WarpGrid grid = getIdentityGrid();

        assertEquals(0, grid.getXStart());
        assertEquals(0, grid.getYStart());
        assertEquals(1, grid.getXStep());
        assertEquals(1, grid.getYStep());
        assertEquals(1, grid.getXNumCells());
        assertEquals(1, grid.getYNumCells());

        assertNotNull(grid.getXWarpPos());
        assertNotNull(grid.getYWarpPos());
        assertEquals(4, grid.getXWarpPos().length);
        assertEquals(4, grid.getYWarpPos().length);
    }

    @Test
    /** Test a simple translated WarpGrid. */
    public void testTranslateWarpGrid() {
        float[] translated = {
            1f, 2f, // (0,0)
            1f, 3f, // (0,1)
            2f, 2f, // (1,0)
            2f, 3f // (1,1)
        };

        WarpGrid grid = getTranslatedGrid(translated);
        assertEquals(0, grid.getXStart());
        assertEquals(0, grid.getYStart());
        assertEquals(1, grid.getXStep());
        assertEquals(1, grid.getYStep());
        assertEquals(1, grid.getXNumCells());
        assertEquals(1, grid.getYNumCells());

        assertMapEqualsWarpPoint(grid, 0, 0);
        assertMapEqualsWarpPoint(grid, 1, 0);
        assertMapEqualsWarpPoint(grid, 0, 1);
        assertMapEqualsWarpPoint(grid, 1, 1);
    }

    private static void assertMapEqualsWarpPoint(Warp grid, int x, int y) {
        Point2D mp = grid.mapDestPoint(new Point2D.Float(x, y));
        int[] tmp = new int[2];
        grid.warpPoint(x, y, 0, 0, tmp);
        assertEquals(tmp[0], (int) Math.round(mp.getX()));
        assertEquals(tmp[1], (int) Math.round(mp.getY()));
    }

    @Test
    /** Test warping a rectangle with a WarpGrid. */
    public void testWarpGridSparseRect() {
        float[] translated = new float[] {
            1f, 2f, // (0,0) -> (1,2)
            2f, 2f, // (1,0) -> (2,2)
            1f, 3f, // (0,1) -> (1,3)
            2f, 3f // (1,1) -> (2,3)
        };

        WarpGrid grid = getTranslatedGrid(translated);

        int x = 0, y = 0, w = 2, h = 2, px = 1, py = 1;
        float[] coords = new float[w * h * 2];
        grid.warpSparseRect(x, y, w, h, px, py, coords);

        int k = 0;
        int[] tmp = new int[2];
        for (int j = 0; j < h; j += py) {
            for (int i = 0; i < w; i += px) {
                grid.warpPoint(x + i, y + j, 0, 0, tmp);
                assertEquals(tmp[0], coords[k++], DELTA);
                assertEquals(tmp[1], coords[k++], DELTA);
            }
        }
    }
}
