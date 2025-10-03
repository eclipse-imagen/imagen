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
package org.eclipse.imagen.media.translate;

import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import org.eclipse.imagen.ImageN;
import org.eclipse.imagen.PlanarImage;
import org.eclipse.imagen.media.testclasses.ComparisonTestBase;
import org.junit.Test;

public class ComparisonTest extends ComparisonTestBase {

    /** X translation parameter */
    private static float transX;

    /** Y translation parameter */
    private static float transY;

    @Test
    @Override
    public void testBase() {
        // Translate doesn't need to test all data types
    }

    @Test
    public void testTranslation() {
        RenderedImage testImage = getSyntheticImage((byte) 100);
        PlanarImage image = TranslateDescriptor.create(testImage, transX, transY, null, null);
        finalizeTest(null, DataBuffer.TYPE_BYTE, image);
    }

    public static RenderedImage getSyntheticImage(byte value) {
        final float width = 256;
        final float height = 256;
        ParameterBlock pb = new ParameterBlock();
        Byte[] array = new Byte[] {value, (byte) (value + 1), (byte) (value + 2)};
        pb.add(width);
        pb.add(height);
        pb.add(array);
        // Create the constant operation.
        return ImageN.create("constant", pb);
    }
}
