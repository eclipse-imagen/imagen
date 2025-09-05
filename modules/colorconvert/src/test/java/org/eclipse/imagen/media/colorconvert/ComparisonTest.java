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
package org.eclipse.imagen.media.colorconvert;

import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import org.eclipse.imagen.PlanarImage;
import org.eclipse.imagen.ROI;
import org.eclipse.imagen.media.range.Range;
import org.eclipse.imagen.media.testclasses.ComparisonTestBase;

public class ComparisonTest extends ComparisonTestBase {

    @Override
    protected boolean supportDataType(int dataType) {
        return (dataType != DataBuffer.TYPE_SHORT);
    }

    @Override
    public void testOperation(int dataType, TestSelection testType) {
        Range range = getTestRange(dataType, testType);
        ROI roi = getTestRoi(testType);
        RenderedImage testImage = createDefaultTestImage(dataType, 1, true);
        // ColorModel
        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);

        ComponentColorModel colorModel = new ComponentColorModel(cs, false, false, Transparency.OPAQUE, dataType);
        // Image
        PlanarImage image = ColorConvertDescriptor.create(testImage, colorModel, roi, range, null, null);
        finalizeTest(getSuffix(testType, null), dataType, image);
    }
}
