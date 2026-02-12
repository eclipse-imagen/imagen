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
package org.eclipse.imagen.media.affine;

import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.eclipse.imagen.BorderExtender;
import org.eclipse.imagen.ImageN;
import org.eclipse.imagen.Interpolation;
import org.eclipse.imagen.PlanarImage;
import org.eclipse.imagen.ROI;
import org.eclipse.imagen.media.range.Range;
import org.eclipse.imagen.media.testclasses.ComparisonTestBase;
import org.junit.BeforeClass;
import org.junit.Test;

/** Test class used for comparing the ImageN operation with the previous ImageN-EXT one */
public class ComparisonTest extends ComparisonTestBase {

    /** Value indicating No Data for the destination image */
    private static double destinationNoData = 0;

    /** Rotation used */
    private static AffineTransform rotateTransform;

    /** Translation used */
    private static AffineTransform translateTransform;

    /** Scale used */
    private static AffineTransform scaleTransform;

    /** RenderingHints used for selecting the borderExtender */
    private static RenderingHints hints;

    private static int[] weight;

    @BeforeClass
    public static void initialSetup() throws FileNotFoundException, IOException {
        // Selection of the RGB image

        hints = new RenderingHints(
                ImageN.KEY_BORDER_EXTENDER, BorderExtender.createInstance(BorderExtender.BORDER_COPY));

        // 45� degrees rotation
        double theta = Math.PI / 4;
        rotateTransform = AffineTransform.getRotateInstance(theta);
        // 100 px translation
        translateTransform = AffineTransform.getTranslateInstance(100, 0);
        // 2 x scale
        scaleTransform = AffineTransform.getScaleInstance(1.5f, 1.5f);

        weight = new int[4];

        double rnd = Math.random();

        if (rnd >= 0 & rnd < 0.25d) {
            weight[0] = 0;
            weight[1] = 1;
            weight[2] = 0;
            weight[3] = 0;
        } else if (rnd >= 0.25d & rnd < 0.5d) {
            weight[0] = 0;
            weight[1] = 1;
            weight[2] = 0;
            weight[3] = 1;
        } else if (rnd >= 0.25d & rnd < 0.5d) {
            weight[0] = 1;
            weight[1] = 1;
            weight[2] = 0;
            weight[3] = 0;
        } else {
            weight[0] = 1;
            weight[1] = 1;
            weight[2] = 1;
            weight[3] = 1;
        }
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
        RenderedImage testImage = createDefaultTestImage(dataType, 1, true);
        Interpolation interpolation;
        String suffix = "";
        PlanarImage image = null;
        Range range = getTestRange(dataType, testType);
        ROI roi = getTestRoi(testType);
        for (int is = 0; is <= 2; is++) {
            interpolation = getInterpolation(dataType, is, range, destinationNoData);
            suffix = getInterpolationSuffix(is);
            AffineTransform transform = new AffineTransform();
            for (int itx = 0; itx < 3; itx++) {
                switch (itx) {
                    case 0:
                        transform.concatenate(rotateTransform);
                        break;
                    case 1:
                        transform.concatenate(scaleTransform);
                        break;
                    case 2:
                        transform.concatenate(rotateTransform);
                        transform.concatenate(scaleTransform);
                        transform.concatenate(translateTransform);
                        break;
                    default:
                        throw new IllegalArgumentException("Wrong transformation value");
                }

                // Destination no data used by the affine operation with the classic
                // bilinear interpolator
                double[] destinationNoDataArray = {destinationNoData, destinationNoData, destinationNoData};
                image = AffineDescriptor.create(
                        testImage, transform, interpolation, destinationNoDataArray, roi, false, false, range, hints);
            }
            finalizeTest(getSuffix(testType, suffix), dataType, image, false);
        }
    }
}
