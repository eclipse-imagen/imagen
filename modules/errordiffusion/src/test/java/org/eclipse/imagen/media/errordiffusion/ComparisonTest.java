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
package org.eclipse.imagen.media.errordiffusion;

import java.awt.image.RenderedImage;
import org.eclipse.imagen.KernelJAI;
import org.eclipse.imagen.LookupTableJAI;
import org.eclipse.imagen.PlanarImage;
import org.eclipse.imagen.ROI;
import org.eclipse.imagen.media.range.Range;
import org.eclipse.imagen.media.testclasses.ComparisonTestBase;
import org.junit.BeforeClass;
import org.junit.Test;

public class ComparisonTest extends ComparisonTestBase {

    /** Source band number */
    private static final int NUM_BANDS = 1;

    /** Test LookupTable */
    private static LookupTableJAI lut;

    /** Test kernel */
    private static KernelJAI kernel;

    @BeforeClass
    public static void init() {

        // Diffusion Kernel
        kernel = KernelJAI.GRADIENT_MASK_SOBEL_VERTICAL;
        // Create simple lookuptable
        float[] data = new float[256];
        for (int i = 0; i < 256; i++) {
            data[i] = i;
        }
        // LUT
        lut = new LookupTableJAI(data);
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
        RenderedImage testImage = createDefaultTestImage(dataType, NUM_BANDS, true);
        PlanarImage image = ErrorDiffusionDescriptor.create(testImage, lut, kernel, roi, range, null, null);
        finalizeTest(getSuffix(testType, null), dataType, image);
    }
}
