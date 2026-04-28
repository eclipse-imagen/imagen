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
package org.eclipse.imagen.media.warp;

import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import org.eclipse.imagen.Interpolation;
import org.eclipse.imagen.PlanarImage;
import org.eclipse.imagen.ROI;
import org.eclipse.imagen.Warp;
import org.eclipse.imagen.WarpAffine;
import org.eclipse.imagen.media.range.Range;
import org.eclipse.imagen.media.testclasses.ComparisonTestBase;
import org.junit.BeforeClass;
import org.junit.Test;

public class ComparisonTest extends ComparisonTestBase {

    /** Warp Object */
    private static Warp warpObj;

    /** Background values to use */
    private static double[] backgroundValues;

    @BeforeClass
    public static void initialSetup() {
        // Definition of the Warp Object
        AffineTransform transform = AffineTransform.getRotateInstance(Math.toRadians(45d));
        transform.concatenate(AffineTransform.getTranslateInstance(0, -DEFAULT_HEIGHT));
        warpObj = new WarpAffine(transform);

        // Background Values
        backgroundValues = new double[] {0};
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
        RenderedImage testImage = createDefaultTestImage(dataType, 1, false);

        // Definition of the interpolation
        Interpolation interpolation;
        String suffix;
        PlanarImage image;
        for (int is = 0; is <= 2; is++) {
            interpolation = getInterpolation(dataType, is, range, destinationNoData);
            suffix = getInterpolationSuffix(is);

            // creation of the image
            image = WarpDescriptor.create(testImage, warpObj, interpolation, backgroundValues, roi, range, null);
            finalizeTest(getSuffix(testType, suffix), dataType, image);
        }
    }
}
