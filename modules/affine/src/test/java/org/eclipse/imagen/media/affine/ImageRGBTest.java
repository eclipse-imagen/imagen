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
package org.eclipse.imagen.media.affine;

import static org.eclipse.imagen.media.testclasses.TestBase.InterpolationType.BICUBIC_INTERP;
import static org.eclipse.imagen.media.testclasses.TestBase.InterpolationType.BILINEAR_INTERP;
import static org.eclipse.imagen.media.testclasses.TestBase.InterpolationType.NEAREST_INTERP;
import static org.eclipse.imagen.media.testclasses.TestBase.ScaleType.MAGNIFY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.eclipse.imagen.BorderExtender;
import org.eclipse.imagen.ImageN;
import org.eclipse.imagen.Interpolation;
import org.eclipse.imagen.PlanarImage;
import org.eclipse.imagen.ROI;
import org.eclipse.imagen.ROIShape;
import org.eclipse.imagen.RenderedOp;
import org.eclipse.imagen.media.testclasses.TestData;
import org.eclipse.imagen.media.viewer.RenderedImageBrowser;
import org.junit.Test;

/**
 * This class extends the TestAffine class and tests the Affine operation on a RGB image. If the user want to see the
 * result, must set the ImageN.Ext.Interactive parameter to true, the ImageN.Ext.TestSelector from 0 to 2 (the 3
 * interpolation types) ,ImageN.Ext.TransformationSelector from 0 to 3 (one of the possible transformations) and
 * ImageN.Ext.InverseScale to 0 or 1 (Magnification/reduction) to the Console. The ROI is created by the roiCreation()
 * method and its height and width are half of the RGB image height and width. The 3 tests with the different
 * interpolation types are executed by calling 3 times the testImage() method and changing each time the selected
 * interpolation. The transformation performed is a combination of rotation, translation and scaling.
 */
public class ImageRGBTest extends TestAffine {
    /** RGB image width */
    private int imageWidth;

    /** RGB image height */
    private int imageHeigth;

    /** Logger for catch any expression */
    private Logger logger = Logger.getLogger(ImageRGBTest.class.getName());

    @Test
    public void testInterpolation() throws IOException {

        boolean bicubic2Disabled = true;
        boolean useROIAccessor = true;
        boolean roiUsed = true;
        boolean setDestinationNoData = true;

        File inputFile = TestData.file(this, "testImageLittle.tif");
        RenderedImage image = ImageIO.read(inputFile);

        imageWidth = image.getWidth();
        imageHeigth = image.getHeight();

        int dataType = image.getSampleModel().getDataType();

        testImage(
                image,
                useROIAccessor,
                roiUsed,
                bicubic2Disabled,
                setDestinationNoData,
                dataType,
                NEAREST_INTERP,
                MAGNIFY);

        testImage(
                image,
                useROIAccessor,
                roiUsed,
                bicubic2Disabled,
                setDestinationNoData,
                dataType,
                BILINEAR_INTERP,
                MAGNIFY);

        testImage(
                image,
                useROIAccessor,
                roiUsed,
                bicubic2Disabled,
                setDestinationNoData,
                dataType,
                BICUBIC_INTERP,
                MAGNIFY);

        testImage(
                image,
                useROIAccessor,
                roiUsed,
                bicubic2Disabled,
                setDestinationNoData,
                dataType,
                NEAREST_INTERP,
                ScaleType.REDUCTION);

        testImage(
                image,
                useROIAccessor,
                roiUsed,
                bicubic2Disabled,
                setDestinationNoData,
                dataType,
                BILINEAR_INTERP,
                ScaleType.REDUCTION);

        testImage(
                image,
                useROIAccessor,
                roiUsed,
                bicubic2Disabled,
                setDestinationNoData,
                dataType,
                BICUBIC_INTERP,
                ScaleType.REDUCTION);
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
            boolean setDestinationNoData,
            int dataType,
            InterpolationType interpType,
            ScaleType scaleValue) {

        if (scaleValue == ScaleType.REDUCTION) {
            scaleX = 0.5f;
            scaleY = 0.5f;
        } else {
            scaleX = 1.5f;
            scaleY = 1.5f;
        }

        // Hints are used only with roiAccessor
        RenderingHints hints = null;
        // ROI creation
        ROIShape roi = null;
        if (roiUsed) {
            if (useROIAccessor) {
                hints = new RenderingHints(
                        ImageN.KEY_BORDER_EXTENDER, BorderExtender.createInstance(BorderExtender.BORDER_ZERO));
            }
            roi = roiCreation();
        } else {
            useROIAccessor = false;
        }

        // Setting of the destination No Data
        destinationNoData = 255;

        // Transformation
        // Rotation
        AffineTransform transform = AffineTransform.getQuadrantRotateInstance(numquadrants, anchorX, anchorY);
        // + Scale (X and Y doubled)
        transform.concatenate(AffineTransform.getScaleInstance(scaleX, scaleY));
        // + Translation (translation towards the center of the image)
        transform.concatenate(AffineTransform.getTranslateInstance(transX, transY));

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
                interp = new org.eclipse.imagen.InterpolationBilinear();

                if (hints != null) {
                    hints.add(new RenderingHints(
                            ImageN.KEY_BORDER_EXTENDER, BorderExtender.createInstance(BorderExtender.BORDER_COPY)));
                } else {
                    hints = new RenderingHints(
                            ImageN.KEY_BORDER_EXTENDER, BorderExtender.createInstance(BorderExtender.BORDER_COPY));
                }

                break;
            case BICUBIC_INTERP:
                // Bicubic
                interp = new org.eclipse.imagen.InterpolationBicubic(DEFAULT_SUBSAMPLE_BITS);

                if (hints != null) {
                    hints.add(new RenderingHints(
                            ImageN.KEY_BORDER_EXTENDER, BorderExtender.createInstance(BorderExtender.BORDER_COPY)));
                } else {
                    hints = new RenderingHints(
                            ImageN.KEY_BORDER_EXTENDER, BorderExtender.createInstance(BorderExtender.BORDER_COPY));
                }

                break;
            default:
                break;
        }
        // Affine operation
        RenderedImage destinationIMG = AffineDescriptor.create(
                sourceImage, transform, interp, null, (ROI) roi, useROIAccessor, setDestinationNoData, null, hints);

        if (INTERACTIVE && INVERSE_SCALE == scaleValue.getType()) {
            RenderedImageBrowser.showChain(destinationIMG, false, roiUsed);
            try {
                System.in.read();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Forcing to retrieve an array of all the image tiles
            ((PlanarImage) destinationIMG).getTiles();
        }

        // Check minimum and maximum value for a tile
        int minTileX = destinationIMG.getMinTileX();
        int tileNumX = destinationIMG.getNumXTiles();
        int minTileY = destinationIMG.getMinTileY();

        Raster simpleTile = destinationIMG.getTile(minTileX + tileNumX - 1, minTileY + 1);

        int tileMinX = simpleTile.getMinX();
        int tileMinY = simpleTile.getMinY();

        int tileWidth = simpleTile.getWidth();
        int tileHeight = simpleTile.getHeight();

        int minValue = Integer.MAX_VALUE;
        int maxValue = Integer.MIN_VALUE;

        for (int i = tileMinY; i < tileMinY + tileHeight; i++) {
            for (int j = tileMinX; j < tileMinX + tileWidth; j++) {
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

        int value = simpleTile.getSample(
                simpleTile.getMinX() + simpleTile.getWidth() / 2, simpleTile.getMinY() + simpleTile.getHeight() / 4, 0);
        assertFalse(value == (int) destinationNoData);

        // Control if the affine operation has been correctly performed
        // width
        assertEquals((int) (imageWidth * scaleX), destinationIMG.getHeight());
        // height
        assertEquals((int) (imageHeigth * scaleY), destinationIMG.getWidth());

        // Final Image disposal
        if (destinationIMG instanceof RenderedOp) {
            ((RenderedOp) destinationIMG).dispose();
        }
    }
}
