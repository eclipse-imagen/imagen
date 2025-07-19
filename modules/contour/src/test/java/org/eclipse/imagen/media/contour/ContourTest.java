/* Copyright 2010-2015 Michael Bedward, and others
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
 *  Copyright (c) 2010-2015, Michael Bedward. All rights reserved.
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

package org.eclipse.imagen.media.contour;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.Point;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BandedSampleModel;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.imagen.DataBufferDouble;
import org.eclipse.imagen.FloatDoubleColorModel;
import org.eclipse.imagen.PlanarImage;
import org.eclipse.imagen.TiledImage;
import org.eclipse.imagen.media.range.RangeDouble;
import org.eclipse.imagen.media.range.RangeFactory;
import org.eclipse.imagen.media.utilities.ImageUtilities;
import org.junit.Before;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

/**
 * Unit tests for the "Contour" operation.
 *
 * @author Michael Bedward
 * @since 1.1
 * @version $Id$
 */
public class ContourTest extends TestBase {

    private Map<String, Object> args;

    @Before
    public void setup() {
        args = new HashMap<String, Object>();
    }

    /** Test that omitting both the levels and interval parameters provokes an IllegalArgumentException */
    @Test(expected = IllegalArgumentException.class)
    public void missingLevelsParameter() {
        TiledImage src = ImageUtilities.createConstantImage(IMAGE_WIDTH, IMAGE_WIDTH, 0);
        doOp(src, args);
    }

    /**
     * Test for graceful response to a source image with no values in the request contour range. Expected result is an
     * empty {@code Collection}.
     */
    @Test
    public void noContours() {
        TiledImage src = ImageUtilities.createConstantImage(IMAGE_WIDTH, IMAGE_WIDTH, 0);
        List<Integer> levels = Arrays.asList(new Integer[] {-10, -5, 5, 10});
        args.put("levels", levels);
        Collection<LineString> contours = doOp(src, args);

        assertNotNull(contours);
        assertEquals(0, contours.size());
    }

    /** Check using interval works (issue 81) */
    @Test
    public void intervalsVerticalGradient() {
        TiledImage src = createGradientImage(Gradient.VERTICAL);

        args.put("interval", 10);
        Collection<LineString> contours = doOp(src, args);
        assertEquals(9, contours.size());

        // test in a way that makes no assumptions about the collection contents order (so that
        // implementation is free to be changed and eventually result in a different order)
        boolean[] found = new boolean[10];
        for (LineString contour : contours) {
            assertSingleSegment(contour);
            double level = (Double) contour.getUserData();

            // check the level is multiple of 10 and within the expected limits
            assertEquals(0d, level % 10, 0d);
            assertTrue(level > 0 && level < 100);

            // check it's the first time we see this level
            assertTrue(!found[(int) level / 10]);
            found[(int) level / 10] = true;

            // check the contour geometry is consistent
            assertContour(contour, 0, level, IMAGE_WIDTH - 1, level);
        }
    }

    /**
     * Trace a single contour in a source image with a vertical gradient of values. Contour simplification is on
     * (default).
     */
    @Test
    public void singleContourVerticalGradient() {
        TiledImage src = createGradientImage(Gradient.VERTICAL);

        args.put("levels", Collections.singleton(IMAGE_WIDTH / 2));
        Collection<LineString> contours = doOp(src, args);
        assertEquals(1, contours.size());

        LineString contour = contours.iterator().next();
        assertSingleSegment(contour);
        assertContour(contour, 0, IMAGE_WIDTH / 2, IMAGE_WIDTH - 1, IMAGE_WIDTH / 2);
    }

    private void assertSingleSegment(LineString contour) {
        assertEquals(2, contour.getNumPoints());
    }

    /**
     * Same as test singleContourVerticalGradient but contour simplification is turned off so we should get one
     * coordinate per pixel.
     */
    @Test
    public void doNotSimplify() {
        TiledImage src = createGradientImage(Gradient.VERTICAL);

        args.put("levels", Collections.singleton(IMAGE_WIDTH / 2));
        args.put("simplify", false);

        Collection<LineString> contours = doOp(src, args);
        assertEquals(1, contours.size());

        LineString contour = contours.iterator().next();
        assertEquals(IMAGE_WIDTH, contour.getCoordinates().length);
        assertContour(contour, 0, IMAGE_WIDTH / 2, IMAGE_WIDTH - 1, IMAGE_WIDTH / 2);
    }

    /**
     * Trace a single contour in a source image with a horizontal gradient of values. Contour simplification is on
     * (default).
     */
    @Test
    public void singleContourHorizontalGradient() {
        TiledImage src = createGradientImage(Gradient.HORIZONTAL);

        args.put("levels", Collections.singleton(IMAGE_WIDTH / 2));
        Collection<LineString> contours = doOp(src, args);
        assertEquals(1, contours.size());

        LineString contour = contours.iterator().next();
        assertSingleSegment(contour);
        assertContour(contour, IMAGE_WIDTH / 2, 0, IMAGE_WIDTH / 2, IMAGE_WIDTH - 1);
    }

    /**
     * Trace a single ring contour from a source image with a radial value gradient and check that each vertex of the
     * contour is within an acceptable distance from the image centre.
     */
    @Test
    public void singleContourRadialGradient() {
        TiledImage src = createGradientImage(Gradient.RADIAL);

        final double value = IMAGE_WIDTH / 3.0d;
        args.put("levels", Collections.singleton(value));
        Collection<LineString> contours = doOp(src, args);

        assertEquals(1, contours.size());

        LineString contour = contours.iterator().next();
        Coordinate mid = new Coordinate(IMAGE_WIDTH / 2, IMAGE_WIDTH / 2);
        final double tol = value / 100.0;
        for (Coordinate c : contour.getCoordinates()) {
            assertEquals(value, c.distance(mid), tol);
        }
    }

    /**
     * Trace a single ring contour from a binary image split in two and ensure there is a single line made of just two
     * points
     *
     * @throws ParseException
     */
    @Test
    public void singleContourVerticalBinaryGradient() throws ParseException {
        TiledImage src = createBinaryImage(Gradient.VERTICAL);

        args.put("levels", Collections.singleton(1));
        Collection<LineString> contours = doOp(src, args);

        assertEquals(1, contours.size());

        LineString contour = contours.iterator().next();
        assertTrue(contour.equalsTopo(new WKTReader().read("LINESTRING(0 51, 99 51)")));
        assertEquals(1d, contour.getUserData());
    }

    /**
     * Trace a single ring contour from a binary image split in two and ensure there is a single line made of just two
     * points
     *
     * @throws ParseException
     */
    @Test
    public void singleContourHorizontalBinaryGradient() throws ParseException {
        TiledImage src = createBinaryImage(Gradient.HORIZONTAL);

        args.put("levels", Collections.singleton(1));
        Collection<LineString> contours = doOp(src, args);

        assertEquals(1, contours.size());

        LineString contour = contours.iterator().next();
        assertTrue(contour.equalsTopo(new WKTReader().read("LINESTRING(51 0, 51 99)")));
        assertEquals(1d, contour.getUserData());
    }

    /**
     * Trace a single ring contour from a binary image split in two and ensure there is a single line
     *
     * @throws ParseException
     */
    @Test
    public void singleContourRadialBinaryGradient() throws ParseException {
        TiledImage src = createBinaryImage(Gradient.RADIAL);

        args.put("levels", Collections.singleton(1));
        Collection<LineString> contours = doOp(src, args);

        assertEquals(1, contours.size());

        LineString contour = contours.iterator().next();
        assertEquals(1d, contour.getUserData());
    }

    @Test
    public void demTest() {
        double[] matrix = new double[] { //
            1493.0, 1496.0, 1500.0, //
            1487.0, 1493.0, 1500.0, //
            1494.0, 1500.0, 1506.0
        };
        DataBuffer data = new DataBufferDouble(matrix, 3);
        BandedSampleModel sm = new BandedSampleModel(DataBuffer.TYPE_DOUBLE, 3, 3, 1);
        WritableRaster raster = Raster.createWritableRaster(sm, data, new Point(0, 0));
        FloatDoubleColorModel colorModel = new FloatDoubleColorModel(
                ColorSpace.getInstance(ColorSpace.CS_GRAY), false, false, Transparency.OPAQUE, DataBuffer.TYPE_DOUBLE);
        BufferedImage buffered = new BufferedImage(colorModel, raster, false, null);
        PlanarImage src = PlanarImage.wrapRenderedImage(buffered);

        final double value = 1500d;
        args.put("levels", Collections.singleton(value));
        Collection<LineString> contours = doOp(src, args);

        assertEquals(1, contours.size());
    }

    /** Generate contours at values determined by the interval parameter. */
    @Test
    public void intervalContoursVerticalGradient() {
        TiledImage src = createGradientImage(Gradient.VERTICAL);

        int interval = 10;
        args.put("interval", IMAGE_WIDTH / interval);
        Collection<LineString> contours = doOp(src, args);

        List<Integer> levels = new ArrayList<Integer>();
        for (int level = interval; level < IMAGE_WIDTH; level += interval) {
            levels.add(level);
        }

        for (LineString contour : contours) {
            assertSingleSegment(contour);
            int z = ((Number) contour.getUserData()).intValue();
            assertTrue(levels.contains(z));
            levels.remove(Integer.valueOf(z)); // remove by value, not index !

            assertContour(contour, 0, z, IMAGE_WIDTH - 1, z);
        }
    }

    /** Test that the levels parameter overrides the interval parameter when both are supplied. */
    @Test
    public void levelsParamOverridesIntervalParam() {
        TiledImage src = createGradientImage(Gradient.VERTICAL);

        final int LEVEL = 42;
        args.put("levels", Collections.singleton(LEVEL));
        args.put("interval", IMAGE_WIDTH / 3);
        Collection<LineString> contours = doOp(src, args);

        assertEquals(1, contours.size());

        int val = ((Number) contours.iterator().next().getUserData()).intValue();
        assertEquals(42, val);
    }

    /** Trace a contour in a source image with a horizontal gradient of values and NaN values across the middle. */
    @Test
    public void strictNODATA_NaN() throws Exception {
        nodataValueTest(Double.NaN);
    }

    /**
     * Trace a contour in a source image with a horizontal gradient of values and user-defined NODATA values across the
     * middle.
     */
    @Test
    public void strictNODATA_UserValue() throws Exception {
        args.put("nodata", Collections.singleton(-1.0d));
        nodataValueTest(-1.0);
    }

    private void nodataValueTest(double nodataValue) throws Exception {
        TiledImage src = createGradientImage(Gradient.HORIZONTAL);

        int minNoDataY = IMAGE_WIDTH / 4;
        int maxNoDataY = 3 * IMAGE_WIDTH / 4;
        for (int y = minNoDataY; y <= maxNoDataY; y++) {
            for (int x = 0; x < IMAGE_WIDTH; x++) {
                src.setSample(x, y, 0, nodataValue);
            }
        }

        args.put("levels", Collections.singleton(IMAGE_WIDTH / 2));
        Collection<LineString> contours = doOp(src, args);

        // expected contours
        WKTReader reader = new WKTReader();
        LineString c1 = (LineString) reader.read(String.format("LINESTRING (50 0, 50 %d)", IMAGE_WIDTH / 4 - 1));

        LineString c2 = (LineString)
                reader.read(String.format("LINESTRING (50 %d, 50 %d)", 3 * IMAGE_WIDTH / 4 + 1, IMAGE_WIDTH - 1));

        assertContoursMatch(contours, c1, c2);
    }

    /** Tests using a Range to define NODATA values. */
    @Test
    public void strictNODATA_Range() throws Exception {
        TiledImage src = createGradientImage(Gradient.HORIZONTAL);

        double minNODATA = IMAGE_WIDTH / 2 - 5;
        double maxNODATA = IMAGE_WIDTH / 2 + 5;
        RangeDouble r = RangeFactory.create(minNODATA, true, maxNODATA, true);
        args.put("nodata", Collections.singleton(r));

        args.put("interval", IMAGE_WIDTH / 4);
        Collection<LineString> contours = doOp(src, args);

        // expected contours
        WKTReader reader = new WKTReader();
        int z = IMAGE_WIDTH / 4;
        LineString c1 = (LineString) reader.read(String.format("LINESTRING (%d 0, %d 99)", z, z));

        LineString c2 = (LineString) reader.read(String.format("LINESTRING (%d 0, %d 99)", 3 * z, 3 * z));

        assertContoursMatch(contours, c1, c2);
    }

    @Test
    public void smoothingPreservesContourLevels() throws Exception {
        TiledImage src = createGradientImage(Gradient.VERTICAL);

        args.put("interval", 10);
        args.put("smooth", Boolean.TRUE);
        Collection<LineString> contours = doOp(src, args);

        for (LineString contour : contours) {
            // confirm that level attribute is present
            Double level = (Double) contour.getUserData();
            assertNotNull(level);

            // check the level is valid
            assertEquals(0d, level % 10, 0d);
            assertTrue(level > 0 && level < 100);
        }
    }
}
