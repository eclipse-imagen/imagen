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
package org.eclipse.imagen.media.imagefunction;

import org.eclipse.imagen.ImageFunction;
import org.eclipse.imagen.PlanarImage;
import org.eclipse.imagen.ROI;
import org.eclipse.imagen.media.range.Range;
import org.eclipse.imagen.media.testclasses.ComparisonTestBase;
import org.junit.BeforeClass;
import org.junit.Test;

public class ComparisonTest extends ComparisonTestBase {

    /** {@link ImageFunction} used in test */
    private static ImageFunctionExt function;

    /** Output image width */
    private static int width;

    /** Output image height */
    private static int height;

    /** X translation of input pixels */
    private static float xTrans;

    /** Y translation of input pixels */
    private static float yTrans;

    /** X scale of input pixels */
    private static float xScale;

    /** Y scale of input pixels */
    private static float yScale;

    @BeforeClass
    public static void init() {

        // ImageFunction
        function = new ImageFunctionTest.DummyFunction();

        // size and other parameters
        width = 256;
        height = 256;
        xTrans = 2f;
        yTrans = 2f;
        xScale = 3f;
        yScale = 3f;
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

        PlanarImage image = ImageFunctionDescriptor.create(
                function, width, height, xScale, yScale, xTrans, yTrans, roi, range, 0f, null);
        finalizeTest(getSuffix(testType, null), dataType, image);
    }
}
