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
package org.eclipse.imagen.media.border;

import java.awt.image.RenderedImage;
import org.eclipse.imagen.BorderExtender;
import org.eclipse.imagen.PlanarImage;
import org.eclipse.imagen.media.range.Range;
import org.eclipse.imagen.media.testclasses.ComparisonTestBase;
import org.junit.BeforeClass;

public class ComparisonTest extends ComparisonTestBase {

    /** Left padding parameter */
    private static int leftPad;

    /** Right padding parameter */
    private static int rightPad;

    /** Top padding parameter */
    private static int topPad;

    /** Bottom padding parameter */
    private static int bottomPad;

    /** Output value for No Data */
    private static double destNoData;

    @BeforeClass
    public static void initialSetup() {

        // Border dimensions setting
        leftPad = 2;
        rightPad = 2;
        topPad = 2;
        bottomPad = 2;

        // destination No Data
        destNoData = 100d;
    }

    public void testOperation(int dataType, TestSelection testType) {
        for (int borderType = 0; borderType < 4; borderType++) {
            RenderedImage testImage = createDefaultTestImage(dataType, 1, true);
            Range range = getTestRange(dataType, testType);
            BorderExtender extender = BorderExtender.createInstance(borderType);
            String suffix = extender.getClass().getSimpleName().replaceFirst("^BorderExtender", "");
            suffix = suffix.substring(0, 1).toUpperCase() + suffix.substring(1);

            // Image
            PlanarImage image = BorderDescriptor.create(
                    testImage, leftPad, rightPad, topPad, bottomPad, extender, range, destNoData, null);
            finalizeTest(getSuffix(testType, suffix), dataType, image);
        }
    }
}
