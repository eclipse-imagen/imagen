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
package org.eclipse.imagen.media.warp;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.io.IOException;
import org.eclipse.imagen.BorderExtender;
import org.eclipse.imagen.ImageLayout;
import org.eclipse.imagen.Interpolation;
import org.eclipse.imagen.JAI;
import org.eclipse.imagen.ParameterListDescriptor;
import org.eclipse.imagen.PlanarImage;
import org.eclipse.imagen.ROI;
import org.eclipse.imagen.ROIShape;
import org.eclipse.imagen.RegistryElementDescriptor;
import org.eclipse.imagen.RenderedOp;
import org.eclipse.imagen.TiledImage;
import org.eclipse.imagen.Warp;
import org.eclipse.imagen.WarpAffine;
import org.eclipse.imagen.media.range.Range;
import org.eclipse.imagen.media.range.RangeFactory;
import org.eclipse.imagen.media.stats.Statistics;
import org.eclipse.imagen.media.stats.Statistics.StatsType;
import org.eclipse.imagen.media.stats.StatisticsDescriptor;
import org.eclipse.imagen.media.testclasses.TestBase;
import org.junit.Test;

/**
 * Test class which extends the jt-utilities TestBase class and provide utility methods for testing the various classes.
 * The tests can be made with nearest-neighbor, bilinear, bicubic and a general type of interpolation. All the tests can
 * be executed with and without ROI and No Data.
 *
 * <p>It is possible to see the result of each operation by adding the following parameters:
 *
 * <ul>
 *   <li>-DJAI.Ext.Interactive=true(false default)
 *   <li>-DJAI.Ext.TestSelection=0(No ROI No NODATA,default), 2(ROI No NODATA), 4(NODATA No ROI), 5(NODATA ROI)
 *   <li>-DJAI.Ext.InterpolationType=0(Nearest,default), 1(Bilinear), 2(Bicubic), 3(General interpolation, in this case
 *       Bicubic)
 * </ul>
 */
public class TestWarp extends TestBase {
    /** Angle rotation for the warp transformation */
    protected static final double ANGLE_ROTATION = 45d;

    /** Warp object used in tests */
    protected static Warp warpObj;

    /** Default number of images to test */
    protected static final int NUM_IMAGES = 6;

    /** Images to test */
    protected static RenderedImage[] images;

    /** No data value for Byte images */
    protected static byte noDataValueB;

    /** Interpolation to test */
    protected static InterpolationType interpType;

    /** No data value for UShort images */
    protected static short noDataValueU;

    /** No data value for Short images */
    protected static short noDataValueS;

    /** No data value for Integer images */
    protected static int noDataValueI;

    /** No data value for Float images */
    protected static float noDataValueF;

    /** No data value for Double images */
    protected static double noDataValueD;

    /**
     * General method for testing a RenderedImage with the selected interpolation, warp object and with or without ROI
     * and No Data.
     */
    public void testWarp(
            RenderedImage source,
            boolean noDataUsed,
            boolean roiUsed,
            Warp warpObj,
            Number noDataValue,
            InterpolationType interpType,
            TestSelection testSelect) {

        // Image data type
        int dataType = source.getSampleModel().getDataType();

        // ROI
        ROI roi = null;

        if (roiUsed) {
            roi = roiCreation();
        }

        // Hints used for image expansion
        RenderingHints hints =
                new RenderingHints(JAI.KEY_BORDER_EXTENDER, BorderExtender.createInstance(BorderExtender.BORDER_COPY));

        RenderedOp destinationIMG = null;

        // Interpolator variable
        Interpolation interp;

        // Interpolators
        switch (interpType) {
            case NEAREST_INTERP:
                // Nearest-Neighbor
                interp = new org.eclipse.imagen.InterpolationNearest();
                break;
            case BILINEAR_INTERP:
                // Bilinear
                interp = new org.eclipse.imagen.InterpolationBilinear(DEFAULT_SUBSAMPLE_BITS);

                break;
            case BICUBIC_INTERP:
                // Bicubic
                interp = new org.eclipse.imagen.InterpolationBicubic(DEFAULT_SUBSAMPLE_BITS);

                break;
            case GENERAL_INTERP:
                // Bicubic
                interp = new org.eclipse.imagen.InterpolationBicubic(DEFAULT_SUBSAMPLE_BITS);

                break;
            default:
                throw new IllegalArgumentException("Wrong interpolation type");
        }

        // Warp operation
        double[] background = new double[] {destinationNoData};
        destinationIMG = WarpDescriptor.create(source, warpObj, interp, background, roi, hints);

        if (INTERACTIVE && dataType == DataBuffer.TYPE_BYTE && TEST_SELECTOR == testSelect.getType()) {
            // TODO: removed this due to circular dependencies
            //            RenderedImageBrowser.showChain(destinationIMG, false, roiUsed);
            try {
                System.in.read();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Forcing to retrieve an array of all the image tiles
            destinationIMG.getTiles();
        }

        // Control if the operation has been correctly performed.

        // Check if the final image dimensions are more than 0
        assertTrue(destinationIMG.getHeight() > 0);
        assertTrue(destinationIMG.getWidth() > 0);

        // Check if the finalImage dimensions are correct
        Rectangle inputRect = new Rectangle(source.getMinX(), source.getMinY(), source.getWidth(), source.getHeight());

        Rectangle outputRect = warpObj.mapSourceRect(inputRect);

        assertEquals(destinationIMG.getMinX(), outputRect.x);
        assertEquals(destinationIMG.getMinY(), outputRect.y);
        assertEquals(destinationIMG.getHeight(), outputRect.width);
        assertEquals(destinationIMG.getWidth(), outputRect.height);

        // check the destination image ROI
        if (roiUsed) {
            Object roiProperty = destinationIMG.getProperty("ROI");
            assertThat(roiProperty, instanceOf(ROI.class));
            ROI destRoi = (ROI) roiProperty;
            // we have warped the ROI
            RenderedImage roiImage = destRoi.getAsImage();
            assertThat(roiImage, instanceOf(RenderedOp.class));
            RenderedOp roiOp = (RenderedOp) roiImage;

            switch (interpType) {
                case NEAREST_INTERP:
                    assertThat(getWarpOperation(roiOp), instanceOf(WarpNearestOpImage.class));
                    break;
                case BILINEAR_INTERP:
                    assertThat(getWarpOperation(roiOp), instanceOf(WarpBilinearOpImage.class));
                    break;
                case BICUBIC_INTERP:
                case GENERAL_INTERP:
                    assertThat(getWarpOperation(roiOp), instanceOf(WarpBicubicOpImage.class));
                    break;
                default:
                    throw new IllegalArgumentException("Wrong interpolation type");
            }
        }

        // Control if the final image has some data
        destinationNoData = 10;
        StatsType[] stats = new StatsType[] {StatsType.MEAN};
        Range noData;
        switch (dataType) {
            case DataBuffer.TYPE_BYTE:
                noData = RangeFactory.create((byte) destinationNoData, true, (byte) destinationNoData, true);
                break;
            case DataBuffer.TYPE_USHORT:
            case DataBuffer.TYPE_SHORT:
                noData = RangeFactory.create((short) destinationNoData, true, (short) destinationNoData, true);
                break;
            case DataBuffer.TYPE_INT:
                noData = RangeFactory.create((int) destinationNoData, true, (int) destinationNoData, true);
                break;
            case DataBuffer.TYPE_FLOAT:
                noData = RangeFactory.create((float) destinationNoData, true, (float) destinationNoData, true, true);
                break;
            case DataBuffer.TYPE_DOUBLE:
                noData = RangeFactory.create(destinationNoData, true, destinationNoData, true, true);
                break;
            default:
                throw new IllegalArgumentException("Wrong data type");
        }
        RenderedOp statistics = StatisticsDescriptor.create(
                destinationIMG, 1, 1, roi, noData, false, new int[] {0}, stats, null, null, null, hints);

        Statistics[][] result = (Statistics[][]) statistics.getProperty(Statistics.STATS_PROPERTY);
        // Image disposal
        destinationIMG.dispose();
        double mean = (Double) result[0][0].getResult();
        long num = (long) result[0][0].getNumSamples();

        assertTrue(num > 0);
        assertTrue(mean != destinationNoData);

        // Image disposal
        statistics.dispose();
    }

    private RenderedImage getWarpOperation(RenderedOp roiOp) {
        String name = roiOp.getOperationName();
        if ("binarize".equals(name)) {
            return roiOp.getSourceImage(0);
        } else {
            return roiOp.getRendering();
        }
    }

    /** Test without ROI and without NoData */
    public void testImage(InterpolationType interpType) {
        boolean roiUsed = false;
        boolean noDataUsed = false;
        TestSelection testSelect = TestSelection.NO_ROI_ONLY_DATA;

        for (int i = 0; i < images.length; i++) {
            testWarp(images[0], noDataUsed, roiUsed, warpObj, noDataValueB, interpType, testSelect);
        }
    }

    /** Test with ROI and without NoData */
    public void testImageROI(InterpolationType interpType) {
        boolean roiUsed = true;
        boolean noDataUsed = false;
        TestSelection testSelect = TestSelection.ROI_ONLY_DATA;

        for (int i = 0; i < images.length; i++) {
            testWarp(images[0], noDataUsed, roiUsed, warpObj, noDataValueB, interpType, testSelect);
        }
    }

    /** Test without ROI and with NoData */
    public void testImageNoData(InterpolationType interpType) {
        boolean roiUsed = false;
        boolean noDataUsed = true;
        TestSelection testSelect = TestSelection.NO_ROI_NO_DATA;

        for (int i = 0; i < images.length; i++) {
            testWarp(images[0], noDataUsed, roiUsed, warpObj, noDataValueB, interpType, testSelect);
        }
    }

    /** Test with ROI and with NoData */
    public void testImageNoDataROI(InterpolationType interpType) {
        boolean roiUsed = true;
        boolean noDataUsed = true;
        TestSelection testSelect = TestSelection.ROI_NO_DATA;

        for (int i = 0; i < images.length; i++) {
            testWarp(images[0], noDataUsed, roiUsed, warpObj, noDataValueB, interpType, testSelect);
        }
    }

    /** Final method for closing the input images */
    public static void finalStuff() {
        // Creation of the images
        if (images != null) {
            for (int i = 0; i < images.length; i++) {
                RenderedImage image = images[i];
                if (image instanceof TiledImage) {
                    ((TiledImage) image).dispose();
                } else if (image instanceof BufferedImage) {
                    ((BufferedImage) image).flush();
                }
            }
        }
    }

    protected void testROILayout(int interpolation) {
        testROILayout(DataBuffer.TYPE_BYTE, interpolation);
        testROILayout(DataBuffer.TYPE_USHORT, interpolation);
        testROILayout(DataBuffer.TYPE_SHORT, interpolation);
        testROILayout(DataBuffer.TYPE_INT, interpolation);
        testROILayout(DataBuffer.TYPE_FLOAT, interpolation);
        testROILayout(DataBuffer.TYPE_DOUBLE, interpolation);
    }

    protected void testROILayout(int dataType, int interpolationType) {
        RenderedImage testIMG = createTestImage(dataType, 1, 1, null, false);
        PlanarImage testImgWithROI = PlanarImage.wrapRenderedImage(testIMG);
        ROI roi = new ROIShape(new Rectangle(0, 0, 1, 1));
        testImgWithROI.setProperty("roi", roi);

        ImageLayout targetLayout = new ImageLayout();
        targetLayout.setTileWidth(512);
        targetLayout.setTileHeight(512);
        RenderingHints hints = new RenderingHints(JAI.KEY_IMAGE_LAYOUT, targetLayout);
        RenderedOp warped = WarpDescriptor.create(
                testIMG,
                new WarpAffine(new AffineTransform(1, -1, 1, 1, 0, 0)),
                Interpolation.getInstance(interpolationType),
                null,
                roi,
                hints);
        ROI warpedROI = (ROI) warped.getProperty("roi");

        // ROI is aligned withe the image and has the expected tile size
        assertEquals(warped.getBounds(), warpedROI.getBounds());
        PlanarImage scaleRoiImage = warpedROI.getAsImage();
        assertEquals(warped.getTileHeight(), scaleRoiImage.getTileHeight());
        assertEquals(warped.getTileWidth(), scaleRoiImage.getTileWidth());
        assertEquals(512, scaleRoiImage.getTileWidth());
        assertEquals(512, scaleRoiImage.getTileHeight());
    }

    @Test
    public void testRegistration() {
        RegistryElementDescriptor descriptor =
                JAI.getDefaultInstance().getOperationRegistry().getDescriptor("rendered", "Warp");
        assertNotNull(descriptor);
        assertEquals("Warp", descriptor.getName());
        ParameterListDescriptor parameters = descriptor.getParameterListDescriptor("rendered");
        assertArrayEquals(
                new String[] {"warp", "interpolation", "backgroundValues", "roi", "nodata"},
                parameters.getParamNames());
    }
}
