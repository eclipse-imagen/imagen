/* Copyright 2011 Michael Bedward, and others
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 *  Copyright (c) 2010-2011, Michael Bedward. All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification,
 *  are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.jaitools.media.jai.vectorize;

import java.awt.Rectangle;
import java.awt.image.RenderedImage;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.eclipse.imagen.*;
import org.eclipse.imagen.media.utilities.ImageUtilities;
import org.eclipse.imagen.registry.RenderedRegistryMode;
import org.junit.Before;
import org.junit.Test;
import org.locationtech.jts.geom.Polygon;

/**
 * General unit tests for the Vectorize operator.
 *
 * @author Michael Bedward
 * @since 1.1
 * @version $Id$
 */
public class VectorizeTest extends TestBase {

    private Map<String, Object> args;

    @Before
    public void setup() {
        args = new HashMap<String, Object>();
    }

    /**
     * Test vectorizing a small image where all pixels have equal value. We leave the outsideValues arg at its null
     * default so all image pixels are treated as inside.
     *
     * <p>Expected result is a single polygon with coordinates equal to those of the source image bounding rectangle.
     */
    @Test
    public void imageBoundary() throws Exception {
        final int IMAGE_WIDTH = 10;

        TiledImage src = ImageUtilities.createConstantImage(IMAGE_WIDTH, IMAGE_WIDTH, Integer.valueOf(0));

        RenderedOp dest = doOp(src, null);
        List<Polygon> polys = getPolygons(dest, 1);

        ExpectedPoly[] expected = {new ExpectedPoly("POLYGON((0 0, 0 10, 10 10, 10 0, 0 0))", 0)};

        assertPolygons(expected, polys);
    }

    /** Tests for graceful handling of an image with no inside values. */
    @Test
    public void noInsideValues() throws Exception {
        final int IMAGE_WIDTH = 10;

        TiledImage src = ImageUtilities.createConstantImage(IMAGE_WIDTH, IMAGE_WIDTH, Integer.valueOf(0));

        Random rr = new Random();
        final int MAX = 5;

        for (int y = 0; y < IMAGE_WIDTH; y++) {
            for (int x = 0; x < IMAGE_WIDTH; x++) {
                src.setSample(x, y, 0, rr.nextInt(MAX));
            }
        }

        Set<Integer> outsideValues = new HashSet<Integer>();
        for (int i = 0; i < MAX; i++) {
            outsideValues.add(i);
        }

        args.put("outsideValues", outsideValues);
        RenderedOp dest = doOp(src, args);
        getPolygons(dest, 0);
    }

    /**
     * Vectorize a test image that has a single block of inside pixels surrounded by a margin of outside pixels.
     *
     * <p>Expected result is a single polygon surrounding the inside pixels.
     */
    @Test
    public void outsideMargin() throws Exception {
        final int IMAGE_WIDTH = 10;
        final int MARGIN_WIDTH = 2;

        final int OUTSIDE = 0;
        final int INSIDE = 1;

        TiledImage src = ImageUtilities.createConstantImage(IMAGE_WIDTH, IMAGE_WIDTH, Integer.valueOf(0));

        for (int y = MARGIN_WIDTH; y < IMAGE_WIDTH - MARGIN_WIDTH; y++) {
            for (int x = MARGIN_WIDTH; x < IMAGE_WIDTH - MARGIN_WIDTH; x++) {
                src.setSample(x, y, 0, 1);
            }
        }

        args.put("outsideValues", Collections.singleton(OUTSIDE));
        RenderedOp dest = doOp(src, args);
        List<Polygon> polys = getPolygons(dest, 1);

        ExpectedPoly[] expected = {new ExpectedPoly("POLYGON((2 2, 2 8, 8 8, 8 2, 2 2))", 1)};

        assertPolygons(expected, polys);
    }

    /**
     * Vectorize a test image that has a single block of data pixels surrounded by a margin of pixels with a different
     * value
     *
     * <p>Expected result is two polygons, one within the other. The inner polygon should be simple. The outer polygon
     * should have a hole corresponding to the inner one.
     */
    @Test
    public void dataMargin() throws Exception {
        final int IMAGE_WIDTH = 10;
        final int MARGIN_WIDTH = 2;

        final int VAL1 = -1;
        final int VAL2 = 1;

        TiledImage src = ImageUtilities.createConstantImage(IMAGE_WIDTH, IMAGE_WIDTH, Integer.valueOf(VAL1));

        for (int y = MARGIN_WIDTH; y < IMAGE_WIDTH - MARGIN_WIDTH; y++) {
            for (int x = MARGIN_WIDTH; x < IMAGE_WIDTH - MARGIN_WIDTH; x++) {
                src.setSample(x, y, 0, VAL2);
            }
        }

        RenderedOp dest = doOp(src, args);
        List<Polygon> polys = getPolygons(dest, 2);

        ExpectedPoly[] expected = {
            new ExpectedPoly("POLYGON((2 2, 2 8, 8 8, 8 2, 2 2))", VAL2),
            new ExpectedPoly("POLYGON((0 0, 0 10, 10 10, 10 0, 0 0), (2 2, 2 8, 8 8, 8 2, 2 2))", VAL1)
        };

        assertPolygons(expected, polys);
    }

    /** Vectorize a test image that has horizontal stripes of uniform data. */
    @Test
    public void horizontalStripes() throws Exception {
        final int IMAGE_WIDTH = 100;
        final int BLOCK_HEIGHT = 25;
        final int NUM_BLOCKS = 4;

        int val = -1;
        TiledImage src = ImageUtilities.createConstantImage(IMAGE_WIDTH, IMAGE_WIDTH, Integer.valueOf(val));

        for (int y = 0; y < IMAGE_WIDTH; y++) {
            val = y / BLOCK_HEIGHT;
            for (int x = 0; x < IMAGE_WIDTH; x++) {
                src.setSample(x, y, 0, val);
            }
        }

        RenderedOp dest = doOp(src, null);
        List<Polygon> polys = getPolygons(dest, NUM_BLOCKS);

        ExpectedPoly[] expected = new ExpectedPoly[NUM_BLOCKS];
        for (int i = 0; i < NUM_BLOCKS; i++) {
            int ymin = i * BLOCK_HEIGHT;
            int ymax = ymin + BLOCK_HEIGHT;

            String wkt = String.format(
                    "POLYGON((%d %d, %d %d, %d %d, %d %d, %d %d))",
                    0, ymin, 0, ymax, IMAGE_WIDTH, ymax, IMAGE_WIDTH, ymin, 0, ymin);
            expected[i] = new ExpectedPoly(wkt, i);
        }

        assertPolygons(expected, polys);
    }

    /** Vectorize a test image that has vertical stripes of uniform data. */
    @Test
    public void verticalStripes() throws Exception {
        final int IMAGE_WIDTH = 100;
        final int BLOCK_WIDTH = 25;
        final int NUM_BLOCKS = 4;

        int val = -1;
        TiledImage src = ImageUtilities.createConstantImage(IMAGE_WIDTH, IMAGE_WIDTH, Integer.valueOf(val));

        for (int y = 0; y < IMAGE_WIDTH; y++) {
            for (int x = 0; x < IMAGE_WIDTH; x++) {
                src.setSample(x, y, 0, x / BLOCK_WIDTH);
            }
        }

        RenderedOp dest = doOp(src, null);
        List<Polygon> polys = getPolygons(dest, NUM_BLOCKS);

        ExpectedPoly[] expected = new ExpectedPoly[NUM_BLOCKS];
        for (int i = 0; i < NUM_BLOCKS; i++) {
            int xmin = i * BLOCK_WIDTH;
            int xmax = xmin + BLOCK_WIDTH;

            String wkt = String.format(
                    "POLYGON((%d %d, %d %d, %d %d, %d %d, %d %d))",
                    xmin, 0, xmin, IMAGE_WIDTH, xmax, IMAGE_WIDTH, xmax, 0, xmin, 0);

            expected[i] = new ExpectedPoly(wkt, i);
        }

        assertPolygons(expected, polys);
    }

    /**
     * Test vectorizing a small L-shaped pattern which caused failures in the GeoTools code from which the Vectorize
     * operator was ported.
     */
    @Test
    public void testLShape() throws Exception {
        final int IMAGE_WIDTH = 5;
        final int IMAGE_HEIGHT = 4;
        RenderedImage src = ImageUtilities.createImageFromArray(
                new Integer[] {
                    0, 0, 0, 0, 0,
                    0, 1, 0, 0, 0,
                    0, 1, 1, 1, 0,
                    0, 0, 0, 0, 0
                },
                IMAGE_WIDTH,
                IMAGE_HEIGHT);

        args.put("outsideValues", Collections.singleton(Integer.valueOf(0)));
        RenderedOp dest = doOp(src, args);
        List<Polygon> polys = getPolygons(dest, 1);

        ExpectedPoly[] expected = {new ExpectedPoly("POLYGON ((1 3, 1 1, 2 1, 2 2, 4 2, 4 3, 1 3))", Integer.valueOf(1))
        };

        assertPolygons(expected, polys);
    }

    /** Test image has alternating square blocks of pixels with value of 0 or 1 like a chessboard pattern. */
    @Test
    public void chessboard() throws Exception {
        final int IMAGE_WIDTH = 100;
        final int SQUARE_WIDTH = 25;
        final int NUM_SQUARES_ACROSS = 4;
        final int NUM_SQUARES = 16;

        Valuer valuer = new Valuer() {
            public int getValue(int areaX, int areaY) {
                return (areaX % 2 == areaY % 2 ? 1 : 0);
            }
        };

        RenderedImage src = createChessboardImage(IMAGE_WIDTH, SQUARE_WIDTH, valuer);
        RenderedOp dest = doOp(src, null);
        List<Polygon> polys = getPolygons(dest, NUM_SQUARES);

        ExpectedPoly[] expected = new ExpectedPoly[NUM_SQUARES];
        int n = 0;
        for (int i = 0; i < NUM_SQUARES_ACROSS; i++) {
            int ymin = i * SQUARE_WIDTH;
            int ymax = ymin + SQUARE_WIDTH;
            for (int j = 0; j < NUM_SQUARES_ACROSS; j++) {
                int xmin = j * SQUARE_WIDTH;
                int xmax = xmin + SQUARE_WIDTH;

                String wkt = String.format(
                        "POLYGON((%d %d, %d %d, %d %d, %d %d, %d %d))",
                        xmin, ymin, xmin, ymax, xmax, ymax, xmax, ymin, xmin, ymin);

                expected[n++] = new ExpectedPoly(wkt, valuer.getValue(j, i));
            }
        }

        assertPolygons(expected, polys);
    }

    /**
     * Test image has alternating square blocks of pixels with value of 0 or 1 like a chessboard pattern. Here we set
     * the insideEdges arg to false. Expected result is a single polygon for the image boundary.
     */
    @Test
    public void chessboardNoInsideEdges() throws Exception {
        final int IMAGE_WIDTH = 100;
        final int SQUARE_WIDTH = 25;

        Valuer valuer = new Valuer() {
            public int getValue(int areaX, int areaY) {
                return (areaX % 2 == areaY % 2 ? 1 : 0);
            }
        };

        RenderedImage src = createChessboardImage(IMAGE_WIDTH, SQUARE_WIDTH, valuer);
        args.put("insideEdges", Boolean.FALSE);
        RenderedOp dest = doOp(src, args);
        getPolygons(dest, 1);
    }

    /**
     * Test image has alternating square blocks of pixels with value of 0 or 1 like a chessboard pattern. When
     * vectorizing, 0 is set to be an outside value.
     */
    @Test
    public void chessboardInsideOutside() throws Exception {
        final int IMAGE_WIDTH = 100;
        final int SQUARE_WIDTH = 25;
        final int NUM_SQUARES_ACROSS = 4;
        final int NUM_SQUARES = 16;

        Valuer valuer = new Valuer() {
            public int getValue(int areaX, int areaY) {
                return (areaX % 2 == areaY % 2 ? 1 : 0);
            }
        };

        RenderedImage src = createChessboardImage(IMAGE_WIDTH, SQUARE_WIDTH, valuer);

        args.put("outsideValues", Collections.singleton(0));
        RenderedOp dest = doOp(src, args);
        List<Polygon> polys = getPolygons(dest, NUM_SQUARES / 2);

        ExpectedPoly[] expected = new ExpectedPoly[NUM_SQUARES / 2];
        int n = 0;
        for (int i = 0; i < NUM_SQUARES_ACROSS; i++) {
            int ymin = i * SQUARE_WIDTH;
            int ymax = ymin + SQUARE_WIDTH;
            for (int j = 0; j < NUM_SQUARES_ACROSS; j++) {
                int value = valuer.getValue(j, i);
                if (value != 0) {
                    int xmin = j * SQUARE_WIDTH;
                    int xmax = xmin + SQUARE_WIDTH;

                    String wkt = String.format(
                            "POLYGON((%d %d, %d %d, %d %d, %d %d, %d %d))",
                            xmin, ymin, xmin, ymax, xmax, ymax, xmax, ymin, xmin, ymin);

                    expected[n++] = new ExpectedPoly(wkt, value);
                }
            }
        }

        assertPolygons(expected, polys);
    }

    /** Test image with uniform pixel values. ROI covering part of the image. */
    @Test
    public void imageBoundaryWithROI() throws Exception {
        final int IMAGE_WIDTH = 100;

        TiledImage src = ImageUtilities.createConstantImage(IMAGE_WIDTH, IMAGE_WIDTH, Integer.valueOf(0));

        final int ROIW = IMAGE_WIDTH / 2;
        ROI roi = new ROIShape(new Rectangle(0, 0, ROIW, ROIW));

        args.put("roi", roi);
        RenderedOp dest = doOp(src, args);

        String wkt = String.format("POLYGON((0 0, 0 %d, %d %d, %d 0, 0 0))", ROIW, ROIW, ROIW, ROIW);

        ExpectedPoly[] expected = {new ExpectedPoly(wkt, 0)};
        assertPolygons(expected, getPolygons(dest, 1));
    }

    /**
     * An ROI created from an image such that pixels alternate between included and excluded. A very small source image
     * is used to avoid a slow test.
     */
    @Test
    public void smallSquaresROI() throws Exception {
        final int IMAGE_WIDTH = 10;
        final int SQUARE_WIDTH = 1;

        RenderedImage src = ImageUtilities.createConstantImage(IMAGE_WIDTH, IMAGE_WIDTH, Integer.valueOf(0));

        RenderedImage roiImg = createChessboardImage(IMAGE_WIDTH, SQUARE_WIDTH, new Valuer() {

            public int getValue(int areaX, int areaY) {
                return (areaX % 2 == areaY % 2 ? 1 : 0);
            }
        });

        ROI roi = new ROI(roiImg, 1);

        args.put("roi", roi);
        RenderedOp dest = doOp(src, args);
        getPolygons(dest, 50);
    }

    /**
     * Interface used with the {@link #createChessboardImage} method. Provides a single function that assigns values to
     * squares.
     */
    private interface Valuer {
        int getValue(int areaX, int areaY);
    }

    /**
     * Helper method to create with square blocks in a chessboard-like pattern.
     *
     * @param imgW image width
     * @param squareW square width
     * @param valuer instance of {@link VectorizeTest.Valuer} to assign values to squares
     * @return the created image
     */
    private RenderedImage createChessboardImage(final int imgW, final int squareW, Valuer valuer) {
        TiledImage src = ImageUtilities.createConstantImage(imgW, imgW, Integer.valueOf(0));

        int areaX, areaY;
        for (int y = 0; y < imgW; y++) {
            areaY = y / squareW;

            for (int x = 0; x < imgW; x++) {
                areaX = x / squareW;
                src.setSample(x, y, 0, valuer.getValue(areaX, areaY));
            }
        }
        return src;
    }

    /** Register the operator with JAI if it is not already registered */
    private static void ensureRegistered() {
        OperationRegistry reg = JAI.getDefaultInstance().getOperationRegistry();
        String[] names = reg.getDescriptorNames(RenderedRegistryMode.MODE_NAME);
        VectorizeDescriptor desc = new VectorizeDescriptor();
        String descName = desc.getName();
        for (String name : names) {
            if (descName.equalsIgnoreCase(name)) {
                return;
            }
        }

        VectorizeSpi spi = new VectorizeSpi();
        spi.updateRegistry(reg);
    }
}
