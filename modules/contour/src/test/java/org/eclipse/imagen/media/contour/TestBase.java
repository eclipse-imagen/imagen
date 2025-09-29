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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.eclipse.imagen.*;
import org.eclipse.imagen.media.utilities.ImageUtilities;
import org.eclipse.imagen.util.ImagingListener;
import org.junit.BeforeClass;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;

/**
 * Base class for unit test of the Contour operator.
 *
 * @author Michael Bedward
 * @since 1.1
 * @version $Id$
 */
public abstract class TestBase {

    protected static final double TOL = 1.0e-6d;

    protected enum Gradient {
        VERTICAL,
        HORIZONTAL,
        RADIAL
    };

    protected static final int IMAGE_WIDTH = 100;

    @BeforeClass
    public static void quiet() {
        ImageN imageN = ImageN.getDefaultInstance();
        final ImagingListener imagingListener = imageN.getImagingListener();
        if (imagingListener == null || imagingListener.getClass().getName().contains("ImagingListenerImpl")) {
            imageN.setImagingListener(new ImagingListener() {
                @Override
                public boolean errorOccurred(String message, Throwable thrown, Object where, boolean isRetryable)
                        throws RuntimeException {
                    if (message.contains("Continuing in pure Java mode")) {
                        return false;
                    }
                    return imagingListener.errorOccurred(message, thrown, where, isRetryable);
                }
            });
        }
    }
    /**
     * Runs the operation and retrieves the contours.
     *
     * @param src the source image
     * @return the contours
     */
    protected Collection<LineString> doOp(PlanarImage src, Map<String, Object> args) {
        ParameterBlockJAI pb = new ParameterBlockJAI("Contour");
        pb.setSource("source0", src);

        for (String paramName : pb.getParameterListDescriptor().getParamNames()) {
            Object obj = args.get(paramName);
            if (obj != null) {
                pb.setParameter(paramName, obj);
            }
        }

        RenderedOp dest = ImageN.create("Contour", pb);
        Object prop = dest.getProperty(ContourDescriptor.CONTOUR_PROPERTY_NAME);
        assertNotNull(prop);
        assertTrue(prop instanceof Collection);

        return (Collection<LineString>) prop;
    }

    /** Check a single contour's end-points and number of coordinates. */
    protected void assertContour(LineString contour, double x0, double y0, double x1, double y1) {
        contour.normalize();
        Coordinate[] coords = contour.getCoordinates();
        final int N = coords.length;

        assertEquals(coords[0].x, x0, TOL);
        assertEquals(coords[0].y, y0, TOL);
        assertEquals(coords[N - 1].x, x1, TOL);
        assertEquals(coords[N - 1].y, y1, TOL);
    }

    /**
     * Asserts that a collection of contours matches those expected.
     *
     * @param contours collection returned by the operator
     * @param expected expected contours
     */
    protected void assertContoursMatch(Collection<LineString> contours, LineString... expected) {
        assertEquals(expected.length, contours.size());

        // copy into a new collection just in case the caller needs the
        //  input collection afterwards
        List<LineString> list = new ArrayList<>();
        list.addAll(contours);

        for (LineString contour : list) {
            contour.normalize();
        }

        for (LineString exp : expected) {
            boolean found = false;
            exp.normalize();
            // might be JTS pre 1.12 so do equality test explicitly
            for (LineString contour : list) {
                if (exp.equalsExact(contour, TOL)) {
                    list.remove(contour);
                    found = true;
                    break;
                }
            }

            assertTrue("Expected contour not found", found);
        }

        assertEquals("result collection had additional contours", 0, list.size());
    }

    /**
     * Creates an image with a linear gradient of values.
     *
     * @param gradient direction of the gradient
     * @return the image
     */
    protected TiledImage createGradientImage(Gradient gradient) {
        TiledImage src = ImageUtilities.createConstantImage(IMAGE_WIDTH, IMAGE_WIDTH, Double.valueOf(0));

        switch (gradient) {
            case HORIZONTAL:
                for (int y = 0; y < IMAGE_WIDTH; y++) {
                    for (int x = 0; x < IMAGE_WIDTH; x++) {
                        src.setSample(x, y, 0, x);
                    }
                }
                break;

            case RADIAL:
                double mid = IMAGE_WIDTH / 2;
                for (int y = 0; y < IMAGE_WIDTH; y++) {
                    double yd2 = (y - mid) * (y - mid);
                    for (int x = 0; x < IMAGE_WIDTH; x++) {
                        double xd2 = (x - mid) * (x - mid);
                        src.setSample(x, y, 0, Math.sqrt(yd2 + xd2));
                    }
                }
                break;

            case VERTICAL:
                for (int y = 0; y < IMAGE_WIDTH; y++) {
                    for (int x = 0; x < IMAGE_WIDTH; x++) {
                        src.setSample(x, y, 0, y);
                    }
                }
                break;
        }

        return src;
    }

    /**
     * Creates a binary image with a horizontal, vertical or radial split
     *
     * @param gradient direction of the split
     * @return the image
     */
    protected TiledImage createBinaryImage(Gradient gradient) {
        TiledImage src = ImageUtilities.createConstantImage(IMAGE_WIDTH, IMAGE_WIDTH, Double.valueOf(0));

        final double mid = IMAGE_WIDTH / 2;
        switch (gradient) {
            case HORIZONTAL:
                for (int y = 0; y < IMAGE_WIDTH; y++) {
                    for (int x = 0; x < IMAGE_WIDTH; x++) {
                        src.setSample(x, y, 0, x > mid ? 1 : 0);
                    }
                }
                break;

            case RADIAL:
                for (int y = 0; y < IMAGE_WIDTH; y++) {
                    double yd2 = (y - mid) * (y - mid);
                    for (int x = 0; x < IMAGE_WIDTH; x++) {
                        double xd2 = (x - mid) * (x - mid);
                        src.setSample(x, y, 0, Math.sqrt(yd2 + xd2) > (mid - 2) ? 1 : 0);
                    }
                }
                break;

            case VERTICAL:
                for (int y = 0; y < IMAGE_WIDTH; y++) {
                    for (int x = 0; x < IMAGE_WIDTH; x++) {
                        src.setSample(x, y, 0, y > mid ? 1 : 0);
                    }
                }
                break;
        }

        return src;
    }
}
