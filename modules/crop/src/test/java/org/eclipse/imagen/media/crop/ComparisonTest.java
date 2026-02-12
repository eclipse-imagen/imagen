/* JAI-Ext - OpenSource Java Advanced Image Extensions Library
 *    http://www.geo-solutions.it/
 *    Copyright 2014 GeoSolutions
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
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.eclipse.imagen.media.crop;

import java.awt.image.RenderedImage;
import org.eclipse.imagen.PlanarImage;
import org.eclipse.imagen.ROI;
import org.eclipse.imagen.media.range.Range;
import org.eclipse.imagen.media.testclasses.ComparisonTestBase;
import org.junit.BeforeClass;
import org.junit.Test;

public class ComparisonTest extends ComparisonTestBase {

    /** X origin parameter */
    private static float cropX;

    /** Y origin parameter */
    private static float cropY;

    /** Width parameter */
    private static float cropWidth;

    /** Height parameter */
    private static float cropHeight;

    @BeforeClass
    public static void initialSetup() {
        // Crop dimensions setting
        cropX = 10;
        cropY = 10;
        cropWidth = 20;
        cropHeight = 20;
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
    public void testOperation(int dataType, TestSelection testType) {
        Range range = getTestRange(dataType, testType);
        ROI roi = getTestRoi(testType);
        RenderedImage testImage = createDefaultTestImage(dataType, 1, true);

        // Image
        PlanarImage image =
                CropDescriptor.create(testImage, cropX, cropY, cropWidth, cropHeight, roi, range, null, null);
        finalizeTest(getSuffix(testType, null), dataType, image);
    }
}
