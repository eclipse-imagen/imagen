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
package org.eclipse.imagen.media.scale;

import java.awt.RenderingHints;
import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import org.eclipse.imagen.BorderExtender;
import org.eclipse.imagen.Interpolation;
import org.eclipse.imagen.JAI;
import org.eclipse.imagen.PlanarImage;
import org.eclipse.imagen.media.range.Range;
import org.eclipse.imagen.media.testclasses.ComparisonTestBase;
import org.junit.BeforeClass;

public class ComparisonTest extends ComparisonTestBase {

    /** Value indicating No Data for the destination image */
    private static double destNoData = 0;

    /** Translation parameter on the X axis */
    private float xTrans = 0;

    /** Translation parameter on the Y axis */
    private float yTrans = 0;

    /** Scale parameter on the X axis */
    private float xScale = 1.5f;

    /** Scale parameter on the Y axis */
    private float yScale = 1.5f;

    /** RenderingHints used for selecting the borderExtender */
    private static RenderingHints hints;

    @BeforeClass
    public static void initialSetup() {
        hints = new RenderingHints(JAI.KEY_BORDER_EXTENDER, BorderExtender.createInstance(BorderExtender.BORDER_COPY));
    }

    @Override
    protected boolean supportDataType(int dataType) {
        return dataType == DataBuffer.TYPE_BYTE;
    }

    @Override
    public void testOperation(int dataType, TestSelection testType) {
        RenderedImage testImage = createDefaultTestImage(dataType, 1, true);
        Interpolation interpolation;
        String suffix = "";
        Range range = getTestRange(dataType, testType);
        for (int is = 0; is <= 2; is++) {
            interpolation = getInterpolation(dataType, is, range, destNoData);
            suffix = getInterpolationSuffix(is);
            float scaleX = xScale;
            float scaleY = yScale;
            PlanarImage image = ScaleDescriptor.create(
                    testImage, scaleX, scaleY, xTrans, yTrans, interpolation, null, false, null, null, hints);
            finalizeTest(getSuffix(testType, suffix), dataType, image);
        }
    }
}
