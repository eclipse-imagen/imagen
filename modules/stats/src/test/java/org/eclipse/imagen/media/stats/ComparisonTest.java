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
package org.eclipse.imagen.media.stats;

import java.awt.image.RenderedImage;
import org.eclipse.imagen.PlanarImage;
import org.eclipse.imagen.ROI;
import org.eclipse.imagen.media.range.Range;
import org.eclipse.imagen.media.stats.Statistics.StatsType;
import org.eclipse.imagen.media.testclasses.ComparisonTestBase;
import org.junit.BeforeClass;
import org.junit.Test;

public class ComparisonTest extends ComparisonTestBase {

    /** Horizontal subsampling parameter */
    private static int xPeriod;

    /** Vertical subsampling parameter */
    private static int yPeriod;

    /** Array with band indexes */
    private static int[] bands;

    /** Array indicating the number of bins for each band */
    private static int[] numBins;

    /** Array indicating the maximum bounds for each band */
    private static double[] maxBounds;

    /** Array indicating the minimum bounds for each band */
    private static double[] minBounds;

    // Initial static method for preparing all the test data
    @BeforeClass
    public static void initialSetup() {
        // Band definition
        bands = new int[] {0, 1, 2};

        // Period Definitions
        xPeriod = 1;
        yPeriod = 1;

        // Histogram variables definition
        numBins = new int[] {5};
        maxBounds = new double[] {3};
        minBounds = new double[] {-3};
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
        RenderedImage testImage = createDefaultTestImage(dataType, 3, true);
        int[] numBinsTest = null;
        double[] maxBoundsTest = null;
        double[] minBoundsTest = null;
        StatsType[] arrayStats = null;

        String stat = "";
        for (int statistic = 0; statistic < 3; statistic++) {
            if (statistic == 0) {
                stat = "Mean";
                arrayStats = new StatsType[] {StatsType.MEAN};
            } else if (statistic == 1) {
                stat = "Extrema";
                arrayStats = new StatsType[] {StatsType.EXTREMA};
            } else if (statistic == 2) {
                stat = "Histogram";
                arrayStats = new StatsType[] {StatsType.HISTOGRAM};
                numBinsTest = numBins;
                maxBoundsTest = maxBounds;
                minBoundsTest = minBounds;
            }

            // Image dataType
            PlanarImage image = StatisticsDescriptor.create(
                    testImage,
                    xPeriod,
                    yPeriod,
                    roi,
                    range,
                    false,
                    bands,
                    arrayStats,
                    minBoundsTest,
                    maxBoundsTest,
                    numBinsTest,
                    null);

            finalizeTest(getSuffix(testType, stat), dataType, image);
        }
    }
}
