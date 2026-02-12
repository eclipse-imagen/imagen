/* JAI-Ext - OpenSource Java Advanced Image Extensions Library
 *    http://www.geo-solutions.it/
 *    Copyright 2014 GeoSolutions
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 
 * http://www.apache.org/licenses/LICENSE-2.0
 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.eclipse.imagen.media.orderdither;

import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.util.Arrays;
import org.eclipse.imagen.ColorCube;
import org.eclipse.imagen.KernelImageN;
import org.eclipse.imagen.PlanarImage;
import org.eclipse.imagen.ROI;
import org.eclipse.imagen.media.range.Range;
import org.eclipse.imagen.media.testclasses.ComparisonTestBase;
import org.junit.BeforeClass;
import org.junit.Test;

public class ComparisonTest extends ComparisonTestBase {
    private static final int NUM_BANDS = 3;

    /** ColorMap used for testing */
    private static ColorCube colorMap;

    /** Dithering mask used for testing */
    private static KernelImageN[] ditherMask;

    @BeforeClass
    public static void init() {

        // Definition of the colorcube
        colorMap = ColorCube.BYTE_496;
        // Definition of the Kernels
        // Getting the dithering mask
        ditherMask = new KernelImageN[NUM_BANDS];
        int width = 64;
        int height = 64;
        float[] data = new float[width * height];
        Arrays.fill(data, 0.5f);
        ditherMask[0] = new KernelImageN(width, height, data);
        ditherMask[1] = new KernelImageN(width, height, data);
        ditherMask[2] = new KernelImageN(width, height, data);
    }

    @Test
    public void testDataTypesWithRoi() {
        testAllTypes(TestSelection.ROI_ONLY_DATA);
    }

    @Test
    public void testDataTypesWithNoData() {
        testAllTypes(TestSelection.NO_ROI_NO_DATA);
    }

    @Test
    public void testDataTypesWithBoth() {
        testAllTypes(TestSelection.ROI_NO_DATA);
    }

    @Override
    protected boolean supportDataType(int dataType) {
        // The previous test was only working with byte dataType
        return dataType == DataBuffer.TYPE_BYTE;
    }

    @Override
    public void testOperation(int dataType, TestSelection testType) {
        Range range = getTestRange(dataType, testType);
        ROI roi = getTestRoi(testType);
        RenderedImage testImage = createDefaultTestImage(dataType, NUM_BANDS, true);

        PlanarImage image = OrderedDitherDescriptor.create(testImage, colorMap, ditherMask, null, roi, range, 100d);
        finalizeTest(getSuffix(testType, null), dataType, image);
    }
}
