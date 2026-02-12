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
package org.eclipse.imagen.media.scale;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.awt.Rectangle;
import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import org.eclipse.imagen.Interpolation;
import org.eclipse.imagen.PlanarImage;
import org.eclipse.imagen.ROI;
import org.eclipse.imagen.ROIShape;
import org.eclipse.imagen.RenderedOp;
import org.eclipse.imagen.media.interpolators.InterpolationBicubic;
import org.eclipse.imagen.media.interpolators.InterpolationBilinear;
import org.eclipse.imagen.media.interpolators.InterpolationNearest;
import org.eclipse.imagen.media.nullop.NullDescriptor;
import org.eclipse.imagen.media.range.Range;
import org.eclipse.imagen.media.translate.TranslateIntOpImage;
import org.junit.Test;

/**
 * This test-class extends the TestScale class and is used for extending the code-coverage of the project. In this
 * test-class are checked the getProperty() method of the ScaleDescriptor class and the capability of the
 * ScaleCRIF.create() method to call the TranslateIntOpImage class or the CopyOpImage class when the requested operation
 * is simply a translation or a copy of the source image without ROI object.
 */
public class CoverageClassTest extends TestScale2 {

    // this test-case is used for testing the getProperty() method of the ScaleDescriptor class
    @Test
    public void testROIProperty() {
        Scale2Descriptor descriptor = new Scale2Descriptor();
        Scale2PropertyGenerator propertyGenerator =
                (Scale2PropertyGenerator) descriptor.getPropertyGenerators()[0];

        boolean useROIAccessor = false;
        boolean bicubic2Disabled = false;
        int dataType = DataBuffer.TYPE_BYTE;
        Range noDataRange = null;

        // Interpolators initialization
        // Nearest-Neighbor
        Interpolation interpNear = new org.eclipse.imagen.InterpolationNearest();
        // Bilinear
        Interpolation interpBil = new org.eclipse.imagen.InterpolationBilinear(DEFAULT_SUBSAMPLE_BITS);
        // Bicubic
        Interpolation interpBic = new org.eclipse.imagen.InterpolationBicubic(DEFAULT_SUBSAMPLE_BITS);

        byte imageValue = 127;

        // Test image creation
        RenderedImage testImg = createTestImage(DataBuffer.TYPE_BYTE, DEFAULT_WIDTH, DEFAULT_HEIGHT, imageValue, false);

        // ROI creation
        ROIShape roi = new ROIShape(
                new Rectangle(testImg.getMinX(), testImg.getMinY(), testImg.getWidth(), testImg.getHeight()));

        RenderedOp testIMG = NullDescriptor.create(testImg, null);

        // Scaled images
        RenderedImage scaleImgNear = Scale2Descriptor.create(
                testIMG, scaleXd, scaleYd, transXd, transYd, interpNear, roi, useROIAccessor, null, null, null);

        RenderedImage scaleImgBil = Scale2Descriptor.create(
                testIMG, scaleXd, scaleYd, transXd, transYd, interpBil, roi, useROIAccessor, null, null, null);

        RenderedImage scaleImgBic = Scale2Descriptor.create(
                testIMG, scaleXd, scaleYd, transXd, transYd, interpBic, roi, useROIAccessor, null, null, null);

        scaleImgNear.getTile(0, 0);
        scaleImgBil.getTile(0, 0);
        scaleImgBic.getTile(0, 0);

        // Scale operation on ROI
        ROI roiNear = (ROI) propertyGenerator.getProperty("roi", scaleImgNear);
        checkROIConsistentWithImage(roiNear, scaleImgNear);

        ROI roiBil = (ROI) propertyGenerator.getProperty("roi", scaleImgBil);
        checkROIConsistentWithImage(roiBil, scaleImgBil);

        ROI roiBic = (ROI) propertyGenerator.getProperty("roi", scaleImgBic);
        checkROIConsistentWithImage(roiBic, scaleImgBic);

        // Final Images disposal
        if (scaleImgNear instanceof RenderedOp) {
            ((RenderedOp) scaleImgNear).dispose();
        }
        if (scaleImgBil instanceof RenderedOp) {
            ((RenderedOp) scaleImgBil).dispose();
        }
        if (scaleImgBic instanceof RenderedOp) {
            ((RenderedOp) scaleImgBic).dispose();
        }
    }

    private void checkROIConsistentWithImage(ROI roi, RenderedImage image) {
        assertEquals(
                roi.getBounds(), new Rectangle(image.getMinX(), image.getMinY(), image.getWidth(), image.getHeight()));
        PlanarImage roiImage = roi.getAsImage();
        assertEquals(image.getTileWidth(), roiImage.getTileWidth());
        assertEquals(image.getTileHeight(), roiImage.getTileHeight());
    }

    @Test
    public void testTranslation() {

        boolean useROIAccessor = false;
        int dataType = DataBuffer.TYPE_BYTE;
        Range noDataRange = null;

        double xScale = 1.0f;
        double yScale = 1.0f;
        double xTrans = 3f;
        double yTrans = 3f;

        byte imageValue = 127;

        // nearest
        InterpolationNearest interpNear =
                new InterpolationNearest(noDataRange, useROIAccessor, destinationNoData, dataType);
        testTranslation(useROIAccessor, dataType, xScale, yScale, xTrans, yTrans, imageValue, interpNear);

        // bilinear
        InterpolationBilinear interpBilinear = new InterpolationBilinear(
                DEFAULT_SUBSAMPLE_BITS, noDataRange, useROIAccessor, destinationNoData, dataType);
        testTranslation(useROIAccessor, dataType, xScale, yScale, xTrans, yTrans, imageValue, interpBilinear);

        // bicubic
        InterpolationBicubic interpBicubic = new InterpolationBicubic(
                DEFAULT_SUBSAMPLE_BITS, noDataRange, useROIAccessor, destinationNoData, dataType, true, 8);
        testTranslation(useROIAccessor, dataType, xScale, yScale, xTrans, yTrans, imageValue, interpBicubic);
    }

    public void testTranslation(
            boolean useROIAccessor,
            int dataType,
            double xScale,
            double yScale,
            double xTrans,
            double yTrans,
            byte imageValue,
            Interpolation interpolation) {
        RenderedImage testIMG = createTestImage(dataType, DEFAULT_WIDTH, DEFAULT_HEIGHT, imageValue, false);
        PlanarImage testImgWithROI = PlanarImage.wrapRenderedImage(testIMG);
        testImgWithROI.setProperty("roi", new ROIShape(new Rectangle(0, 0, DEFAULT_WIDTH / 2, DEFAULT_HEIGHT / 2)));

        // Scaled images
        RenderedOp scaleImgNear = Scale2Descriptor.create(
                testImgWithROI, xScale, yScale, xTrans, yTrans, interpolation, null, useROIAccessor, null, null, null);
        scaleImgNear.getTiles();

        // verify the translate int optimization, and that
        assertThat(scaleImgNear.getRendering(), instanceOf(TranslateIntOpImage.class));
        Object roi = scaleImgNear.getProperty("roi");
        assertThat(roi, instanceOf(ROI.class));
        assertThat(((ROI) roi).getAsImage(), instanceOf(TranslateIntOpImage.class));

        double actualX = scaleImgNear.getMinX();
        double actualY = scaleImgNear.getMinY();

        double expectedX = testIMG.getMinX() + xTrans;
        double expectedY = testIMG.getMinY() + yTrans;

        double tolerance = 0.1f;

        assertEquals(expectedX, actualX, tolerance);
        assertEquals(expectedY, actualY, tolerance);

        // Final Image disposal
        if (scaleImgNear instanceof RenderedOp) {
            ((RenderedOp) scaleImgNear).dispose();
        }
    }

    @Test
    public void testCopy() {

        boolean useROIAccessor = false;
        int dataType = DataBuffer.TYPE_BYTE;
        Range noDataRange = null;

        double xScale = 1.0f;
        double yScale = 1.0f;
        double xTrans = 0.0f;
        double yTrans = 0.0f;

        byte imageValue = 127;

        // Nearest-Neighbor
        InterpolationNearest interpNear =
                new InterpolationNearest(noDataRange, useROIAccessor, destinationNoData, dataType);

        RenderedImage testIMG = createTestImage(dataType, DEFAULT_WIDTH, DEFAULT_HEIGHT, imageValue, false);

        // Scaled images
        PlanarImage scaleImgNear = Scale2Descriptor.create(
                testIMG, xScale, yScale, xTrans, yTrans, interpNear, null, useROIAccessor, null, null, null);
        scaleImgNear.getTiles();

        double actualX = scaleImgNear.getMinX();
        double actualY = scaleImgNear.getMinY();

        double expectedX = testIMG.getMinX();
        double expectedY = testIMG.getMinY();

        double tolerance = 0.1f;

        assertEquals(expectedX, actualX, tolerance);
        assertEquals(expectedY, actualY, tolerance);

        // Final Image disposal
        if (scaleImgNear instanceof RenderedOp) {
            ((RenderedOp) scaleImgNear).dispose();
        }
    }
}
