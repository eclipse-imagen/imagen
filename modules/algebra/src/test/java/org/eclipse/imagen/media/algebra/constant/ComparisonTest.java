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
package org.eclipse.imagen.media.algebra.constant;

import static org.eclipse.imagen.media.algebra.AlgebraDescriptor.Operator.AND;
import static org.eclipse.imagen.media.algebra.AlgebraDescriptor.Operator.DIVIDE;
import static org.eclipse.imagen.media.algebra.AlgebraDescriptor.Operator.MULTIPLY;
import static org.eclipse.imagen.media.algebra.AlgebraDescriptor.Operator.OR;
import static org.eclipse.imagen.media.algebra.AlgebraDescriptor.Operator.SUBTRACT;
import static org.eclipse.imagen.media.algebra.AlgebraDescriptor.Operator.SUM;
import static org.eclipse.imagen.media.algebra.AlgebraDescriptor.Operator.XOR;

import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import org.eclipse.imagen.PlanarImage;
import org.eclipse.imagen.ROI;
import org.eclipse.imagen.media.algebra.AlgebraDescriptor.Operator;
import org.eclipse.imagen.media.range.Range;
import org.eclipse.imagen.media.testclasses.ComparisonTestBase;
import org.junit.BeforeClass;
import org.junit.Test;

public class ComparisonTest extends ComparisonTestBase {

    /** Output value for No Data */
    private static double destNoData;

    private static double[] constsD;
    private static int[] constsI;

    private static Operator OPERATIONS[] = {SUM, SUBTRACT, MULTIPLY, DIVIDE, OR, XOR, AND};

    @BeforeClass
    public static void initialSetup() {

        // Constants
        constsD = new double[] {5};
        constsI = new int[] {5};

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
        Range range = getTestRange(dataType, testType);
        ROI roi = getTestRoi(testType);
        RenderedImage testImage = createDefaultTestImage(dataType, 1, true);
        Operator op;
        for (int o = 0; o < OPERATIONS.length; o++) {
            op = OPERATIONS[o];

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
                case AND:
                    operation = "And";
                    break;
                default:
                    break;
            }
            if ("Or".equals(operation) || "Xor".equals(operation) || "And".equals(operation)) {
                if (dataType == DataBuffer.TYPE_FLOAT || dataType == DataBuffer.TYPE_DOUBLE) {
                    // logical operations are not supported on float and double
                    continue;
                }
            }
            PlanarImage image = OperationConstDescriptor.create(testImage, constsD, op, roi, range, destNoData, null);
            finalizeTest(getSuffix(testType, operation), dataType, image);
        }
    }
}
