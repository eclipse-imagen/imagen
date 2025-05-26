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

import static org.eclipse.imagen.media.testclasses.TestBase.InterpolationType.BICUBIC_INTERP;
import static org.eclipse.imagen.media.testclasses.TestBase.InterpolationType.BILINEAR_INTERP;
import static org.eclipse.imagen.media.testclasses.TestBase.InterpolationType.NEAREST_INTERP;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.awt.*;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.eclipse.imagen.BorderExtender;
import org.eclipse.imagen.Interpolation;
import org.eclipse.imagen.JAI;
import org.eclipse.imagen.PlanarImage;
import org.eclipse.imagen.ROIShape;
import org.eclipse.imagen.RenderedOp;
import org.eclipse.imagen.media.testclasses.TestData;
import org.eclipse.imagen.media.viewer.RenderedImageBrowser;
import org.junit.Test;

/**
 * This class extends the TestScale class and tests the Scale operation on a RGB image. If the user want to see the
 * result, must set the JAI.Ext.Interactive parameter to true, JAI.Ext.TestSelector from 0 to 2 (the 3 interpolation
 * types) and JAI.Ext.InverseScale to 0 or 1 (Magnification/reduction) to the Console. The ROI is created by the
 * roiCreation() method and its height and width are half of the RGB image height and width. The 3 tests with the
 * different interpolation types are executed by calling 3 times the testImage() method and changing each time the
 * selected interpolation.
 */
public class ImageRGBTest extends TestScale2 {
    /** RGB image width */
    private int imageWidth;

    /** RGB image height */
    private int imageHeigth;

    /** Destination No Data value */
    protected double destinationNoData = 255;

    @Test
    public void testInterpolationScale() throws Throwable {

        boolean bicubic2Disabled = true;
        boolean useROIAccessor = true;
        boolean roiUsed = true;

        File inputFile = TestData.file(this, "testImageLittle.tif");
        RenderedImage image = ImageIO.read(inputFile);

        imageWidth = image.getWidth();
        imageHeigth = image.getHeight();

        int dataType = image.getSampleModel().getDataType();

        testImage(image, useROIAccessor, roiUsed, bicubic2Disabled, ScaleType.MAGNIFY, dataType, NEAREST_INTERP);
        testImage(image, useROIAccessor, roiUsed, bicubic2Disabled, ScaleType.MAGNIFY, dataType, BILINEAR_INTERP);
        testImage(image, useROIAccessor, roiUsed, bicubic2Disabled, ScaleType.MAGNIFY, dataType, BICUBIC_INTERP);
        testImage(image, useROIAccessor, roiUsed, bicubic2Disabled, ScaleType.REDUCTION, dataType, NEAREST_INTERP);
        testImage(image, useROIAccessor, roiUsed, bicubic2Disabled, ScaleType.REDUCTION, dataType, BILINEAR_INTERP);
        testImage(image, useROIAccessor, roiUsed, bicubic2Disabled, ScaleType.REDUCTION, dataType, BICUBIC_INTERP);
    }

    protected ROIShape roiCreation() {

        int roiHeight = imageHeigth / 2;
        int roiWidth = imageWidth / 2;

        Rectangle roiBound = new Rectangle(0, 0, roiWidth, roiHeight);

        ROIShape roi = new ROIShape(roiBound);
        return roi;
    }

    private void testImage(
            RenderedImage sourceImage,
            boolean useROIAccessor,
            boolean roiUsed,
            boolean bicubic2Disabled,
            ScaleType scaleValue,
            int dataType,
            InterpolationType interpType) {

        if (scaleValue == ScaleType.REDUCTION) {
            scaleXd = 0.5d;
            scaleYd = 0.5d;
        } else {
            scaleXd = 1.5d;
            scaleYd = 1.5d;
        }
        // Hints are used only with roiAccessor
        RenderingHints hints = null;
        // ROI creation
        ROIShape roi = null;
        if (roiUsed) {
            if (useROIAccessor) {
                hints = new RenderingHints(
                        JAI.KEY_BORDER_EXTENDER, BorderExtender.createInstance(BorderExtender.BORDER_ZERO));
            }
            roi = roiCreation();
        } else {
            useROIAccessor = false;
        }

        // Interpolator initialization
        Interpolation interp = null;

        // Interpolators
        switch (interpType) {
            case NEAREST_INTERP:
                // Nearest-Neighbor
                interp = new org.eclipse.imagen.InterpolationNearest();
                break;
            case BILINEAR_INTERP:
                // Bilinear
                interp = new org.eclipse.imagen.InterpolationBilinear(DEFAULT_SUBSAMPLE_BITS);
                if (hints != null) {
                    hints.add(new RenderingHints(
                            JAI.KEY_BORDER_EXTENDER, BorderExtender.createInstance(BorderExtender.BORDER_COPY)));
                } else {
                    hints = new RenderingHints(
                            JAI.KEY_BORDER_EXTENDER, BorderExtender.createInstance(BorderExtender.BORDER_COPY));
                }

                break;
            case BICUBIC_INTERP:
                // Bicubic
                interp = new org.eclipse.imagen.InterpolationBicubic(DEFAULT_SUBSAMPLE_BITS);
                if (hints != null) {
                    hints.add(new RenderingHints(
                            JAI.KEY_BORDER_EXTENDER, BorderExtender.createInstance(BorderExtender.BORDER_COPY)));
                } else {
                    hints = new RenderingHints(
                            JAI.KEY_BORDER_EXTENDER, BorderExtender.createInstance(BorderExtender.BORDER_COPY));
                }
                break;
            default:
                throw new IllegalArgumentException("...");
        }

        // Scale operation
        RenderedImage destinationIMG = Scale2Descriptor.create(
                sourceImage, scaleXd, scaleYd, transXd, transYd, interp, roi, useROIAccessor, null, null, hints);

        if (INTERACTIVE && TEST_SELECTOR == interpType.getType() && INVERSE_SCALE == scaleValue.getType()) {
            RenderedImageBrowser.showChain(destinationIMG, false, roiUsed);
            try {
                System.in.read();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            // Forcing to retrieve an array of all the image tiles
            // image tile calculation for searching possible errors
            ((PlanarImage) destinationIMG).getTiles();
        }
        // Check minimum and maximum value for a tile
        Raster simpleTile = destinationIMG.getTile(destinationIMG.getMinTileX(), destinationIMG.getMinTileY());

        int tileWidth = simpleTile.getWidth();
        int tileHeight = simpleTile.getHeight();

        int minValue = Integer.MAX_VALUE;
        int maxValue = Integer.MIN_VALUE;

        for (int i = 0; i < tileHeight; i++) {
            for (int j = 0; j < tileWidth; j++) {
                int value = simpleTile.getSample(j, i, 0);
                if (value > maxValue) {
                    maxValue = value;
                }

                if (value < minValue) {
                    minValue = value;
                }
            }
        }
        // Check if the values are not max and minimum value
        assertFalse(minValue == maxValue);
        assertFalse(minValue == Integer.MAX_VALUE);
        assertFalse(maxValue == Integer.MIN_VALUE);

        // Control if the ROI has been expanded
        PlanarImage planarIMG = (PlanarImage) destinationIMG;
        int imgWidthROI = destinationIMG.getWidth() / 2 - 1;
        int imgHeightROI = destinationIMG.getHeight() / 2 - 1;

        int tileInROIx = planarIMG.XToTileX(imgWidthROI);
        int tileInROIy = planarIMG.YToTileY(imgHeightROI);

        Raster testTile = destinationIMG.getTile(tileInROIx, tileInROIy);

        int value = testTile.getSample(testTile.getMinX() + 2, testTile.getMinY() + 2, 0);
        assertFalse(value == (int) destinationNoData);

        // Control if the scale operation has been correctly performed
        // width
        assertEquals((int) (imageWidth * scaleXd), destinationIMG.getWidth());
        // height
        assertEquals((int) (imageHeigth * scaleYd), destinationIMG.getHeight());

        // Final Image disposal
        if (destinationIMG instanceof RenderedOp) {
            ((RenderedOp) destinationIMG).dispose();
        }
    }
}
