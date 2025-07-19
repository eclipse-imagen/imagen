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
 *  Copyright (c) 2011, Michael Bedward. All rights reserved.
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

import static org.junit.Assert.*;

import java.awt.image.RenderedImage;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.imagen.RenderedOp;
import org.eclipse.imagen.media.utilities.ImageUtilities;
import org.junit.Before;
import org.junit.Test;
import org.locationtech.jts.geom.Polygon;

/**
 * Unit tests for filtering small polygons.
 *
 * @author Michael Bedward
 * @since 1.1
 * @version $Id$
 */
public class SmallPolysTest extends TestBase {

    private static final Integer[] DATA = {
        1, 1, 1, 1, 1, 1,
        1, 1, 1, 1, 1, 1,
        1, 1, 1, 3, 1, 1,
        2, 2, 3, 4, 3, 2,
        2, 2, 2, 3, 2, 2,
        2, 2, 2, 2, 2, 2
    };

    private static final int WIDTH = 6;
    private static final int NUM_SMALL = 5;

    private Map<String, Object> args;

    @Before
    public void setup() {
        args = new HashMap<>();
    }

    @Test
    public void removeSmallPolys() throws Exception {
        // System.out.println("   remove small polygons");
        RenderedImage img = ImageUtilities.createImageFromArray(DATA, WIDTH, WIDTH);

        args.put("filterThreshold", 1.1);
        args.put("filterMethod", VectorizeDescriptor.FILTER_DELETE);
        RenderedOp dest = doOp(img, args);
        getPolygons(dest, 2);
    }

    @Test
    public void mergeSmallPolysIntoLargestNbr() throws Exception {
        // System.out.println("   merge small polygons into largest neighbour");
        RenderedImage img = ImageUtilities.createImageFromArray(DATA, WIDTH, WIDTH);

        args.put("filterThreshold", 1.1);
        args.put("filterMethod", VectorizeDescriptor.FILTER_MERGE_LARGEST);
        RenderedOp dest = doOp(img, args);
        List<Polygon> polygons = getPolygons(dest, 2);

        int[] ids = new int[2];
        int[] areas = new int[2];
        int k = 0;
        for (Polygon poly : polygons) {
            ids[k] = ((Number) poly.getUserData()).intValue();
            areas[k] = (int) poly.getArea();
            k++;
        }

        /*
         * Depending on the order in which small polys were
         * processed, poly 1 area could have increased by
         * 4 or 5 while poly 2 area could have increased by
         * 1 or remained unchanged
         */
        Map<Integer, Integer> startAreas = getStartAreas();

        int[] diffs = {areas[0] - startAreas.get(ids[0]), areas[1] - startAreas.get(ids[1])};

        assertTrue(diffs[0] >= 0 && diffs[1] >= 0);
        assertEquals(NUM_SMALL, diffs[0] + diffs[1]);
    }

    @Test
    public void smallIslandPoly() throws Exception {
        // System.out.println("   small polygon surrounded by nodata");

        // Set 3 to nodata so that 4 will be an island poly
        args.put("outsideValues", Collections.singleton(3d));
        args.put("filterThreshold", 1.1);
        args.put("filterMethod", VectorizeDescriptor.FILTER_MERGE_RANDOM);

        RenderedImage img = ImageUtilities.createImageFromArray(DATA, WIDTH, WIDTH);
        RenderedOp dest = doOp(img, args);

        // There should only be 2 polys because the island has no
        // shared boundary segments
        getPolygons(dest, 2);
    }

    @Test
    public void thresholdBelowMinPolySize() throws Exception {
        // System.out.println("   filter threshold below min poly size");

        args.put("filterThreshold", 0.5);
        args.put("filterMethod", VectorizeDescriptor.FILTER_DELETE);

        RenderedImage img = ImageUtilities.createImageFromArray(DATA, WIDTH, WIDTH);
        RenderedOp dest = doOp(img, args);

        // There should be 7 polygons because none will have been filtered
        getPolygons(dest, 7);
    }

    private Map<Integer, Integer> getStartAreas() {
        Map<Integer, Integer> areas = new HashMap<>();

        int k = 0;
        Integer id, area;
        for (int y = 0; y < WIDTH; y++) {
            for (int x = 0; x < WIDTH; x++) {
                id = DATA[k++];
                area = areas.get(id);
                if (area == null) area = 0;
                areas.put(id, area + 1);
            }
        }

        return areas;
    }
}
