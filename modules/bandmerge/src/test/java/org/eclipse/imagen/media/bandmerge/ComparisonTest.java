/* ImageN-Ext - OpenSource Java Advanced Image Extensions Library
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
package org.eclipse.imagen.media.bandmerge;

import java.awt.image.RenderedImage;
import java.util.Vector;
import org.eclipse.imagen.ParameterBlockJAI;
import org.eclipse.imagen.PlanarImage;
import org.eclipse.imagen.TiledImage;
import org.eclipse.imagen.media.range.Range;
import org.eclipse.imagen.media.testclasses.ComparisonTestBase;
import org.junit.BeforeClass;
import org.junit.Test;

public class ComparisonTest extends ComparisonTestBase {

    /** Final image band number */
    private static final int BAND_NUMBER = 3;

    /** Destination No Data value */
    private static double destNoData;

    // Initial static method for preparing all the test data
    @BeforeClass
    public static void initialSetup() {
        // Destination No Data
        destNoData = 50d;
    }

    @Test
    public void testDataTypesWithNoData() {
        testAllTypes(TestSelection.NO_ROI_NO_DATA);
    }

    @Override
    public void testOperation(int dataType, TestSelection testType) {
        Range[] range = new Range[] {getTestRange(dataType, testType)};
        RenderedImage[] testImage = new RenderedImage[BAND_NUMBER];
        for (int i = 0; i < BAND_NUMBER; i++) {
            testImage[i] = createDefaultTestImage(dataType, 1, true);
        }

        // Vector of sources (used for Old BandMerge operation)
        Vector vec = new Vector(testImage.length);

        for (RenderedImage img : testImage) {
            vec.add(img);
        }

        ParameterBlockJAI pbj = new ParameterBlockJAI("bandmerge");
        pbj.setSources(vec);

        // New descriptor calculations
        PlanarImage image = BandMergeDescriptor.create(range, destNoData, false, null, testImage);

        finalizeTest(getSuffix(testType, null), dataType, image);

        for (int band = 0; band < BAND_NUMBER; band++) {
            ((TiledImage) testImage[band]).dispose();
        }
    }
}
