/* JAI-Ext - OpenSource Java Advanced Image Extensions Library
 *    http://www.geo-solutions.it/
 *    Copyright 2018 GeoSolutions
 *
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

package org.eclipse.imagen.media.jiffleop;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.util.Arrays;
import org.eclipse.imagen.ImageN;
import org.eclipse.imagen.ParameterListDescriptor;
import org.eclipse.imagen.RegistryElementDescriptor;
import org.eclipse.imagen.RenderedOp;
import org.eclipse.imagen.iterator.RandomIter;
import org.eclipse.imagen.iterator.RandomIterFactory;
import org.eclipse.imagen.media.jiffle.runtime.BandTransform;
import org.eclipse.imagen.media.range.Range;
import org.eclipse.imagen.media.range.RangeFactory;
import org.eclipse.imagen.media.testclasses.TestBase;
import org.eclipse.imagen.media.utilities.ImageUtilities;
import org.junit.Test;

public class JiffleOpTest extends TestBase {

    @Test
    public void testCopyDefaults() {
        RenderedImage src = buildTestImage(10, 10);
        RenderedOp op = JiffleDescriptor.create(
                new RenderedImage[] {src}, null, null, "dest = src;", null, null, null, null, null);
        assertCopy(src, op, DataBuffer.TYPE_DOUBLE);
    }

    @Test
    public void testCopyRemappedDefaults() {
        RenderedImage src = buildTestImage(10, 10);
        BandTransform transform = (x, y, b) -> 0;
        RenderedOp op = JiffleDescriptor.create(
                new RenderedImage[] {src},
                null,
                null,
                "dest = src[10];",
                null,
                null,
                null,
                new BandTransform[] {transform},
                null);
        assertCopy(src, op, DataBuffer.TYPE_DOUBLE);
    }

    @Test
    public void testCopyNonDefaults() {
        RenderedImage src = buildTestImage(10, 10);
        RenderedOp op = JiffleDescriptor.create(
                new RenderedImage[] {src},
                new String[] {"a"},
                "b",
                "b = a;",
                null,
                DataBuffer.TYPE_BYTE,
                null,
                null,
                null);
        assertCopy(src, op, DataBuffer.TYPE_BYTE);
    }

    @Test
    public void testSum() {
        RenderedImage src1 = buildTestImage(10, 10);
        RenderedImage src2 = buildTestImage(10, 10);
        RenderedOp op = JiffleDescriptor.create(
                new RenderedImage[] {src1, src2},
                new String[] {"a", "b"},
                "res",
                "res = a + b;",
                null,
                DataBuffer.TYPE_INT,
                null,
                null,
                null);

        // check same size and expected
        assertEquals(src1.getMinX(), op.getMinX());
        assertEquals(src1.getWidth(), op.getWidth());
        assertEquals(src1.getMinY(), op.getMinY());
        assertEquals(src1.getHeight(), op.getHeight());
        assertEquals(DataBuffer.TYPE_INT, op.getSampleModel().getDataType());

        RandomIter srcIter = RandomIterFactory.create(src1, null);
        RandomIter opIter = RandomIterFactory.create(op, null);
        for (int y = src1.getMinY(); y < src1.getMinY() + src1.getHeight(); y++) {
            for (int x = src1.getMinX(); x < src1.getMinX() + src1.getWidth(); x++) {
                double expected = srcIter.getSampleDouble(x, y, 0) * 2;
                double actual = opIter.getSampleDouble(x, y, 0);
                assertEquals(expected, actual, 0d);
            }
        }
    }

    @Test
    public void testSumNoData() {
        RenderedImage src1 = buildTestImage(10, 10);
        RenderedImage src2 = buildTestImage(10, 10);
        Range nodata = RangeFactory.create((byte) 5, (byte) 5);
        RenderedOp op = JiffleDescriptor.create(
                new RenderedImage[] {src1, src2},
                new String[] {"a", "b"},
                "res",
                "res = a + b;",
                null,
                DataBuffer.TYPE_DOUBLE,
                null,
                null,
                null,
                new Range[] {nodata, nodata},
                null);

        // check same size and expected
        assertEquals(src1.getMinX(), op.getMinX());
        assertEquals(src1.getWidth(), op.getWidth());
        assertEquals(src1.getMinY(), op.getMinY());
        assertEquals(src1.getHeight(), op.getHeight());
        assertEquals(DataBuffer.TYPE_DOUBLE, op.getSampleModel().getDataType());

        RandomIter srcIter = RandomIterFactory.create(src1, null);
        RandomIter opIter = RandomIterFactory.create(op, null);
        for (int y = src1.getMinY(); y < src1.getMinY() + src1.getHeight(); y++) {
            for (int x = src1.getMinX(); x < src1.getMinX() + src1.getWidth(); x++) {
                double value = srcIter.getSampleDouble(x, y, 0);
                double expected = value == 5 ? Double.NaN : value * 2;
                double actual = opIter.getSampleDouble(x, y, 0);
                assertEquals(expected, actual, 0d);
            }
        }
    }

    private void assertCopy(RenderedImage src, RenderedOp op, int dataType) {
        // check it's a copy with the expected values
        assertEquals(src.getMinX(), op.getMinX());
        assertEquals(src.getWidth(), op.getWidth());
        assertEquals(src.getMinY(), op.getMinY());
        assertEquals(src.getHeight(), op.getHeight());
        assertEquals(dataType, op.getSampleModel().getDataType());

        RandomIter srcIter = RandomIterFactory.create(src, null);
        RandomIter opIter = RandomIterFactory.create(op, null);
        for (int y = src.getMinY(); y < src.getMinY() + src.getHeight(); y++) {
            for (int x = src.getMinX(); x < src.getMinX() + src.getWidth(); x++) {
                double expected = srcIter.getSampleDouble(x, y, 0);
                double actual = opIter.getSampleDouble(x, y, 0);
                assertEquals(expected, actual, 0d);
            }
        }
    }

    private RenderedImage buildTestImage(int width, int height) {
        Number[] values = new Number[width * height];
        for (int i = 0; i < values.length; i++) {
            values[i] = Byte.valueOf((byte) i);
        }
        return ImageUtilities.createImageFromArray(values, width, height);
    }

    @Test
    public void testRegistration() {
        RegistryElementDescriptor descriptor =
                ImageN.getDefaultInstance().getOperationRegistry().getDescriptor("rendered", "Jiffle");
        assertNotNull(descriptor);
        assertEquals("Jiffle", descriptor.getName());
        ParameterListDescriptor parameters = descriptor.getParameterListDescriptor("rendered");
        System.out.println(Arrays.toString(parameters.getParamNames()));
        assertArrayEquals(
                new String[] {
                    "sourceNames",
                    "destName",
                    "script",
                    "destBounds",
                    "destType",
                    "srcCoordinateTransforms",
                    "srcBandTransforms",
                    "destBands",
                    "noData"
                },
                parameters.getParamNames());
    }
}
