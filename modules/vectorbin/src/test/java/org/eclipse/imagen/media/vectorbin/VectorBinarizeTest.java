/* JAI-Ext - OpenSource Java Advanced Image Extensions Library
*    http://www.geo-solutions.it/
*    Copyright 2014 GeoSolutions


* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at

* http://www.apache.org/licenses/LICENSE-2.0

* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.eclipse.imagen.media.vectorbin;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.awt.Dimension;
import java.awt.image.Raster;
import org.eclipse.imagen.JAI;
import org.eclipse.imagen.ParameterBlockJAI;
import org.eclipse.imagen.ParameterListDescriptor;
import org.eclipse.imagen.RegistryElementDescriptor;
import org.eclipse.imagen.RenderedOp;
import org.eclipse.imagen.media.jts.CoordinateSequence2D;
import org.eclipse.imagen.media.testclasses.TestBase;
import org.junit.Before;
import org.junit.Test;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.WKTReader;

/**
 * Unit tests for the VectorBinarize operation.
 *
 * @author Michael Bedward
 * @author Andrea Aime
 */
public class VectorBinarizeTest extends TestBase {

    private static final GeometryFactory gf = new GeometryFactory();

    private static final int TILE_WIDTH = 8;

    WKTReader reader = new WKTReader(gf);

    @Before
    public void setupTileSize() {
        JAI.setDefaultTileSize(new Dimension(TILE_WIDTH, TILE_WIDTH));
    }

    @Test
    public void rectanglePolyAcrossTiles() throws Exception {
        final int margin = 3;
        final int Ntiles = 3;

        int minx = margin;
        int miny = minx;
        int maxx = TILE_WIDTH * Ntiles - 2 * margin;
        int maxy = maxx;

        String wkt = String.format(
                "POLYGON((%d %d, %d %d, %d %d, %d %d, %d %d))",
                minx, miny, minx, maxy, maxx, maxy, maxx, miny, minx, miny);

        Polygon poly = (Polygon) reader.read(wkt);

        ParameterBlockJAI pb = new ParameterBlockJAI("VectorBinarize");
        pb.setParameter("width", Ntiles * TILE_WIDTH);
        pb.setParameter("height", Ntiles * TILE_WIDTH);
        pb.setParameter("geometry", poly);

        RenderedOp dest = JAI.create("VectorBinarize", pb);

        CoordinateSequence2D testPointCS = new CoordinateSequence2D(1);
        Point testPoint = gf.createPoint(testPointCS);

        for (int ytile = 0; ytile < Ntiles; ytile++) {
            for (int xtile = 0; xtile < Ntiles; xtile++) {
                Raster tile = dest.getTile(xtile, ytile);
                for (int y = tile.getMinY(), iy = 0; iy < tile.getHeight(); y++, iy++) {
                    testPointCS.setY(0, y + 0.5);
                    for (int x = tile.getMinX(), ix = 0; ix < tile.getWidth(); x++, ix++) {
                        testPointCS.setX(0, x + 0.5);
                        testPoint.geometryChanged();
                        int expected = poly.intersects(testPoint) ? 1 : 0;
                        assertEquals(
                                "Failed test at position " + x + ", " + y + ", " + "expected " + expected + " but got "
                                        + tile.getSample(x, y, 0),
                                expected,
                                tile.getSample(x, y, 0));
                    }
                }
            }
        }
    }

    @Test
    public void testRegistration() {
        RegistryElementDescriptor descriptor =
                JAI.getDefaultInstance().getOperationRegistry().getDescriptor("rendered", "VectorBinarize");
        assertNotNull(descriptor);
        assertEquals("VectorBinarize", descriptor.getName());
        ParameterListDescriptor parameters = descriptor.getParameterListDescriptor("rendered");
        assertArrayEquals(
                new String[] {"minx", "miny", "width", "height", "geometry", "antiAliasing"},
                parameters.getParamNames());
    }
}
