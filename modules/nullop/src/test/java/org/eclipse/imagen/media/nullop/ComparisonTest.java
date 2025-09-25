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
package org.eclipse.imagen.media.nullop;

import java.awt.image.RenderedImage;
import org.eclipse.imagen.PlanarImage;
import org.eclipse.imagen.media.testclasses.ComparisonTestBase;
import org.junit.Test;

public class ComparisonTest extends ComparisonTestBase {

    @Test
    @Override
    public void testBase() {
        // nullop doesn't need to test all data types
    }

    @Override
    public void testOperation(int dataType, TestSelection testType) {
        RenderedImage testImage = createDefaultTestImage(dataType, 1, false);
        PlanarImage image = NullDescriptor.create(testImage, null);
        finalizeTest(getSuffix(testType, null), dataType, image);
    }
}
