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
package org.eclipse.imagen.media.algebra;

import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import org.eclipse.imagen.PlanarImage;
import org.eclipse.imagen.ROI;
import org.eclipse.imagen.ROIShape;
import org.eclipse.imagen.media.algebra.AlgebraDescriptor.Operator;
import org.eclipse.imagen.media.range.Range;
import org.eclipse.imagen.media.testclasses.ComparisonTestBase;
import org.junit.BeforeClass;
import org.junit.Test;

public class ComparisonTest extends ComparisonTestBase {

    /** Number associated with the type of BorderExtender to use */
    private static final int NUM_IMAGES = Integer.getInteger("ImageN.Ext.NumImages", 2);

    /** Number associated with the type of BorderExtender to use */
    private static final int NUM_BANDS = 1;

    /** Output value for No Data */
    private static double destNoData;

    private static ROIShape roi;

    @BeforeClass
    public static void initialSetup() {

        // destination No Data
        destNoData = 100d;
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
        RenderedImage testImages[] = new RenderedImage[NUM_IMAGES];

        for (int i = 0; i < NUM_IMAGES; i++) {
            testImages[i] = createDefaultTestImage(dataType, NUM_BANDS, true);
        }
        Range range = getTestRange(dataType, testType);
        ROI roi = getTestRoi(testType);
        Operator op;
        Operator[] operations = Operator.values();
        for (int o = 0; o < operations.length; o++) {
            op = operations[o];

            // Descriptor string definition
            String operation = "";
            switch (op) {
                case SUM:
                    operation = "Add";
                    break;
                case SUBTRACT:
                    operation = "Subtract";
                    break;
                case MULTIPLY:
                    operation = "Multiply";
                    break;
                case DIVIDE:
                    operation = "Divide";
                    break;
                case OR:
                    operation = "Or";
                    break;
                case XOR:
                    operation = "Xor";
                    break;
                case ABSOLUTE:
                    operation = "Absolute";
                    break;
                case AND:
                    operation = "And";
                    break;
                case EXP:
                    operation = "Exp";
                    break;
                case INVERT:
                    operation = "Invert";
                    break;
                case LOG:
                    operation = "Log";
                    break;
                case NOT:
                    operation = "Not";
                    break;
                case DIVIDE_INTO:
                    operation = "DivideInto";
                    break;
                case SUBTRACT_FROM:
                    operation = "SubtractFrom";
                    break;
                case MAX:
                    operation = "Max";
                    break;
                case MIN:
                    operation = "Min";
                    break;
            }
            if ("Or".equals(operation)
                    || "Xor".equals(operation)
                    || "And".equals(operation)
                    || "Not".equals(operation)
                    || "Invert".equals(operation)) {
                if (dataType == DataBuffer.TYPE_FLOAT || dataType == DataBuffer.TYPE_DOUBLE) {
                    // logical operations are not supported on float and double
                    continue;
                }
            }
            PlanarImage image = AlgebraDescriptor.create(op, roi, range, destNoData, null, testImages);
            finalizeTest(getSuffix(testType, operation), dataType, image);
        }
    }
}
