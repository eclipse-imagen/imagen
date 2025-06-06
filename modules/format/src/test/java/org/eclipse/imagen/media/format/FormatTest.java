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
package org.eclipse.imagen.media.format;

import static org.junit.Assert.*;

import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import org.eclipse.imagen.JAI;
import org.eclipse.imagen.ParameterListDescriptor;
import org.eclipse.imagen.RegistryElementDescriptor;
import org.eclipse.imagen.RenderedOp;
import org.eclipse.imagen.media.testclasses.TestBase;
import org.junit.Test;

/**
 * Simple test class ensuring that the {@link FormatDescriptor} and {@link FormatCRIF} classes behaves correctly.
 *
 * @author Nicola Lagomarsini GeoSolutions
 */
public class FormatTest extends TestBase {

    @Test
    public void testImages() {
        // Create test image
        RenderedImage image = createTestImage(DataBuffer.TYPE_INT, DEFAULT_WIDTH, DEFAULT_HEIGHT, 13, false);
        // Convert the image to all the dataTypes and ensure that the conversion is correct
        for (int i = 0; i < 6; i++) {
            testFormat(image, i);
        }
    }

    /**
     * Simple method for checking if an image has been converted to the new format
     *
     * @param image
     * @param dataType
     */
    private void testFormat(RenderedImage image, int dataType) {
        RenderedOp test = FormatDescriptor.create(image, dataType, null);
        assertEquals(test.getSampleModel().getDataType(), dataType);
    }

    @Test
    public void testRegistration() {
        RegistryElementDescriptor descriptor =
                JAI.getDefaultInstance().getOperationRegistry().getDescriptor("rendered", "Format");
        assertNotNull(descriptor);
        assertEquals("Format", descriptor.getName());
        ParameterListDescriptor parameters = descriptor.getParameterListDescriptor("rendered");
        assertArrayEquals(new String[] {"dataType"}, parameters.getParamNames());
    }
}
