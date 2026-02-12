/* JAI-Ext - OpenSource Java Advanced Image Extensions Library
 *    http://www.geo-solutions.it/
 *    Copyright 2014 GeoSolutions
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.eclipse.imagen.media.colorconvert;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.imagen.ColorSpaceImageN;
import org.eclipse.imagen.IHSColorSpace;
import org.eclipse.imagen.ImageLayout;
import org.eclipse.imagen.ImageN;
import org.eclipse.imagen.ParameterBlockImageN;
import org.eclipse.imagen.ParameterListDescriptor;
import org.eclipse.imagen.ROI;
import org.eclipse.imagen.ROIShape;
import org.eclipse.imagen.RasterFactory;
import org.eclipse.imagen.RegistryElementDescriptor;
import org.eclipse.imagen.RenderedOp;
import org.eclipse.imagen.TiledImage;
import org.eclipse.imagen.media.range.Range;
import org.eclipse.imagen.media.range.RangeFactory;
import org.eclipse.imagen.media.testclasses.TestBase;
import org.eclipse.imagen.media.testclasses.TestData;
import org.eclipse.imagen.media.utilities.ImageLayout2;
import org.eclipse.imagen.media.viewer.RenderedImageBrowser;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test class for the ColorConvert operation
 *
 * @author Nicola Lagomarsini, GeoSolutions
 * @source $URL$
 */
public class TestColorConvert extends TestBase {

    /**
     * Synthetic with Short Sample Model
     *
     * @throws IOException
     */
    @Test
    public void testSyntheticShort() throws IOException {

        // /////////////////////////////////////////////////////////////////////
        //
        // This test uses a Ushort Image datatype.
        //
        // /////////////////////////////////////////////////////////////////////
        final BufferedImage image = getSyntheticShortImage();

        ParameterBlockImageN pbj = new ParameterBlockImageN("ColorConvert");
        pbj.addSource(image);
        pbj.setParameter("colorModel", getJAICm());
        RenderedOp finalimage = ImageN.create("ColorConvert", pbj);

        if (INTERACTIVE) RenderedImageBrowser.showChain(finalimage, false, false, null);
        else finalimage.getTiles();
        finalimage.dispose();

        // ROI creation
        ROI roi = new ROIShape(
                new Rectangle(image.getMinX() + 5, image.getMinY() + 5, image.getWidth() / 4, image.getHeight() / 4));

        Range nodata = RangeFactory.create((short) 5, (short) 5);

        // ROI
        pbj = new ParameterBlockImageN("ColorConvert");
        pbj.addSource(image);
        pbj.setParameter("colorModel", getJAICm());
        pbj.setParameter("roi", roi);
        finalimage = ImageN.create("ColorConvert", pbj);

        if (INTERACTIVE) RenderedImageBrowser.showChain(finalimage, false, false, null);
        else finalimage.getTiles();
        finalimage.dispose();

        // NODATA
        pbj = new ParameterBlockImageN("ColorConvert");
        pbj.addSource(image);
        pbj.setParameter("colorModel", getJAICm());
        pbj.setParameter("nodata", nodata);
        finalimage = ImageN.create("ColorConvert", pbj);

        if (INTERACTIVE) RenderedImageBrowser.showChain(finalimage, false, false, null);
        else finalimage.getTiles();
        finalimage.dispose();

        // NODATA AND ROI
        pbj = new ParameterBlockImageN("ColorConvert");
        pbj.addSource(image);
        pbj.setParameter("colorModel", getJAICm());
        pbj.setParameter("roi", roi);
        pbj.setParameter("nodata", nodata);
        finalimage = ImageN.create("ColorConvert", pbj);

        if (INTERACTIVE) RenderedImageBrowser.showChain(finalimage, false, false, null);
        else finalimage.getTiles();
        finalimage.dispose();
    }

    /**
     * Synthetic with Float Sample Model!
     *
     * @return {@linkplain BufferedImage}
     */
    private BufferedImage getSyntheticShortImage() {
        final int width = 500;
        final int height = 500;
        final WritableRaster raster = RasterFactory.createBandedRaster(DataBuffer.TYPE_USHORT, width, height, 3, null);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                raster.setSample(x, y, 0, (x + y));
            }
        }
        final ColorModel cm = new ComponentColorModel(
                ColorSpace.getInstance(ColorSpace.CS_LINEAR_RGB),
                false,
                false,
                Transparency.OPAQUE,
                DataBuffer.TYPE_USHORT);
        final BufferedImage image = new BufferedImage(cm, raster, false, null);
        return image;
    }

    /**
     * Building a synthetic image upon a Byte sample-model.
     *
     * @return {@linkplain BufferedImage}
     * @throws IOException
     */
    @Test
    public void testSyntheticByte() throws IOException {

        // /////////////////////////////////////////////////////////////////////
        //
        // This test uses a Byte Image datatype.
        //
        // /////////////////////////////////////////////////////////////////////

        BufferedImage image = getSyntheticByteImage();

        ParameterBlockImageN pbj = new ParameterBlockImageN("ColorConvert");
        pbj.addSource(image);
        pbj.setParameter("colorModel", getJAIEXTCm());
        RenderedOp finalimage = ImageN.create("ColorConvert", pbj);

        if (INTERACTIVE) RenderedImageBrowser.showChain(finalimage, false, false, null);
        else finalimage.getTiles();
        finalimage.dispose();

        // ROI creation
        ROI roi = new ROIShape(
                new Rectangle(image.getMinX() + 5, image.getMinY() + 5, image.getWidth() / 4, image.getHeight() / 4));

        Range nodata = RangeFactory.create((byte) 5, (byte) 5);

        // ROI
        pbj = new ParameterBlockImageN("ColorConvert");
        pbj.addSource(image);
        pbj.setParameter("colorModel", getJAIEXTCm());
        pbj.setParameter("roi", roi);
        finalimage = ImageN.create("ColorConvert", pbj);

        if (INTERACTIVE) RenderedImageBrowser.showChain(finalimage, false, false, null);
        else finalimage.getTiles();
        finalimage.dispose();

        // NODATA
        pbj = new ParameterBlockImageN("ColorConvert");
        pbj.addSource(image);
        pbj.setParameter("colorModel", getJAIEXTCm());
        pbj.setParameter("nodata", nodata);
        finalimage = ImageN.create("ColorConvert", pbj);

        if (INTERACTIVE) RenderedImageBrowser.showChain(finalimage, false, false, null);
        else finalimage.getTiles();
        finalimage.dispose();

        // NODATA AND ROI
        pbj = new ParameterBlockImageN("ColorConvert");
        pbj.addSource(image);
        pbj.setParameter("colorModel", getJAIEXTCm());
        pbj.setParameter("roi", roi);
        pbj.setParameter("nodata", nodata);
        finalimage = ImageN.create("ColorConvert", pbj);

        if (INTERACTIVE) RenderedImageBrowser.showChain(finalimage, false, false, null);
        else finalimage.getTiles();
        finalimage.dispose();
    }

    /**
     * Building a synthetic image upon a byte sample-model.
     *
     * @return {@linkplain BufferedImage}
     * @throws IOException
     */
    @Test
    public void testSyntheticByteIHSColorSpace() throws IOException {

        // /////////////////////////////////////////////////////////////////////
        //
        // This test uses a Byte Image datatype. Input ColorSpace is IHS.
        //
        // /////////////////////////////////////////////////////////////////////

        final BufferedImage image = getSyntheticByteImageIHSColorSpace();

        ParameterBlockImageN pbj = new ParameterBlockImageN("ColorConvert");
        pbj.addSource(image);
        pbj.setParameter("colorModel", getNotJAICm());
        RenderedOp finalimage = ImageN.create("ColorConvert", pbj);

        if (INTERACTIVE) RenderedImageBrowser.showChain(finalimage, false, false, null);
        else finalimage.getTiles();
        finalimage.dispose();

        // ROI creation
        ROI roi = new ROIShape(
                new Rectangle(image.getMinX() + 5, image.getMinY() + 5, image.getWidth() / 4, image.getHeight() / 4));

        Range nodata = RangeFactory.create((byte) 5, (byte) 5);

        // ROI
        pbj = new ParameterBlockImageN("ColorConvert");
        pbj.addSource(image);
        pbj.setParameter("colorModel", getNotJAICm());
        pbj.setParameter("roi", roi);
        finalimage = ImageN.create("ColorConvert", pbj);

        if (INTERACTIVE) RenderedImageBrowser.showChain(finalimage, false, false, null);
        else finalimage.getTiles();
        finalimage.dispose();

        // NODATA
        pbj = new ParameterBlockImageN("ColorConvert");
        pbj.addSource(image);
        pbj.setParameter("colorModel", getNotJAICm());
        pbj.setParameter("nodata", nodata);
        finalimage = ImageN.create("ColorConvert", pbj);

        if (INTERACTIVE) RenderedImageBrowser.showChain(finalimage, false, false, null);
        else finalimage.getTiles();
        finalimage.dispose();

        // NODATA AND ROI
        pbj = new ParameterBlockImageN("ColorConvert");
        pbj.addSource(image);
        pbj.setParameter("colorModel", getNotJAICm());
        pbj.setParameter("roi", roi);
        pbj.setParameter("nodata", nodata);
        finalimage = ImageN.create("ColorConvert", pbj);

        if (INTERACTIVE) RenderedImageBrowser.showChain(finalimage, false, false, null);
        else finalimage.getTiles();
        finalimage.dispose();
    }

    /**
     * Building a synthetic image upon a FLOAT sample-model.
     *
     * @return {@linkplain BufferedImage}
     */
    private BufferedImage getSyntheticByteImage() {
        final int width = 500;
        final int height = 500;
        final WritableRaster raster = RasterFactory.createBandedRaster(DataBuffer.TYPE_BYTE, width, height, 3, null);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                raster.setSample(x, y, 0, (x + y));
            }
        }
        final ColorModel cm = new ComponentColorModel(
                ColorSpace.getInstance(ColorSpace.CS_LINEAR_RGB),
                false,
                false,
                Transparency.OPAQUE,
                DataBuffer.TYPE_BYTE);
        final BufferedImage image = new BufferedImage(cm, raster, false, null);
        return image;
    }

    /**
     * Building a synthetic image upon a FLOAT sample-model.
     *
     * @return {@linkplain BufferedImage}
     */
    private BufferedImage getSyntheticByteImageIHSColorSpace() {
        final int width = 500;
        final int height = 500;
        final WritableRaster raster = RasterFactory.createBandedRaster(DataBuffer.TYPE_BYTE, width, height, 3, null);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                raster.setSample(x, y, 0, (x + y));
            }
        }
        final ColorModel cm = new ComponentColorModel(
                new IHSColorSpaceImageNExt(), false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
        final BufferedImage image = new BufferedImage(cm, raster, false, null);
        return image;
    }

    /**
     * Tiff image test-case.
     *
     * @throws IOException
     */
    @Test
    public void testTiff() throws IOException {

        final RenderedImage image = getTestTiff();
        ParameterBlockImageN pbj = new ParameterBlockImageN("ColorConvert");
        pbj.addSource(image);
        pbj.setParameter("colorModel", getNotJAICm());
        RenderedOp finalimage = ImageN.create("ColorConvert", pbj);

        if (INTERACTIVE) RenderedImageBrowser.showChain(finalimage, false, false, null);
        else finalimage.getTiles();
        finalimage.dispose();

        // ROI creation
        ROI roi = new ROIShape(
                new Rectangle(image.getMinX() + 5, image.getMinY() + 5, image.getWidth() / 4, image.getHeight() / 4));

        Range nodata = RangeFactory.create((byte) 5, (byte) 5);

        // ROI
        pbj = new ParameterBlockImageN("ColorConvert");
        pbj.addSource(image);
        pbj.setParameter("colorModel", getNotJAICm());
        pbj.setParameter("roi", roi);
        finalimage = ImageN.create("ColorConvert", pbj);

        if (INTERACTIVE) RenderedImageBrowser.showChain(finalimage, false, false, null);
        else finalimage.getTiles();
        finalimage.dispose();

        // NODATA
        pbj = new ParameterBlockImageN("ColorConvert");
        pbj.addSource(image);
        pbj.setParameter("colorModel", getNotJAICm());
        pbj.setParameter("nodata", nodata);
        finalimage = ImageN.create("ColorConvert", pbj);

        if (INTERACTIVE) RenderedImageBrowser.showChain(finalimage, false, false, null);
        else finalimage.getTiles();
        finalimage.dispose();

        // NODATA AND ROI
        pbj = new ParameterBlockImageN("ColorConvert");
        pbj.addSource(image);
        pbj.setParameter("colorModel", getNotJAICm());
        pbj.setParameter("roi", roi);
        pbj.setParameter("nodata", nodata);
        finalimage = ImageN.create("ColorConvert", pbj);

        if (INTERACTIVE) RenderedImageBrowser.showChain(finalimage, false, false, null);
        else finalimage.getTiles();
        finalimage.dispose();
    }

    @Test
    @Ignore // TODO: fix and re-enable
    public void testExpandGrayCaseC() {
        // create gray indexed image
        BufferedImage bi = new BufferedImage(10, 10, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D gr = bi.createGraphics();
        gr.setColor(Color.GRAY);
        gr.fillRect(0, 0, 10, 10);
        gr.dispose();

        // create a RGB color model
        final ColorModel cm = new ComponentColorModel(
                ColorSpace.getInstance(ColorSpace.CS_sRGB),
                false,
                false,
                Transparency.OPAQUE,
                bi.getSampleModel().getDataType());

        // set the destination image layout
        final ImageLayout2 il = new ImageLayout2(bi);
        il.setColorModel(cm);
        il.setSampleModel(cm.createCompatibleSampleModel(bi.getWidth(), bi.getHeight()));
        RenderingHints ri = new RenderingHints(ImageN.KEY_IMAGE_LAYOUT, il);

        // perform color expansion
        ParameterBlockImageN pbj = new ParameterBlockImageN("ColorConvert");
        pbj.addSource(bi);
        pbj.setParameter("colorModel", cm);
        pbj.setParameter("noData", RangeFactory.create(-1, -1));
        RenderedOp finalimage = ImageN.create("ColorConvert", pbj, ri);

        int[] pixel = new int[3];
        finalimage.getData().getPixel(0, 0, pixel);
        assertEquals(128, pixel[0]);
        assertEquals(128, pixel[1]);
        assertEquals(128, pixel[2]);
    }

    @Test
    /**
     * This test covers some corner-cases found in mosaicking/scaling scenarios where the requested image has an
     * extra-line or column with respect to the source image. In particular, it was found that when using IHSColorSpace
     * color conversion the computation of such tiles was throwing ArrayIndexOutOfBoundsExceptions due to the code not
     * considering the bounds of the Destination Rectangle.
     */
    public void testCornerCase() {
        ColorSpace ihsCS = new IHSColorSpaceImageNExt();
        int[] nBits = {8, 8, 8};
        int[] dataTypes = {DataBuffer.TYPE_BYTE, DataBuffer.TYPE_INT, DataBuffer.TYPE_FLOAT};
        ROI[] rois = {null, new ROIShape(new Rectangle(10, 10, 10, 10))};
        Range[] nodatas = {null, RangeFactory.create(244d, 244d)};
        for (int dataType : dataTypes) {
            for (ROI roi : rois) {
                for (Range nodata : nodatas) {
                    ComponentColorModel colorModel =
                            new ComponentColorModel(ihsCS, nBits, false, false, Transparency.OPAQUE, dataType);

                    int tileW = 25;
                    int tileH = 25;
                    int pixelStride = 3;
                    int scanlineStride = tileW * pixelStride;
                    int[] bandOffsets = {0, 1, 2};

                    SampleModel sampleModel = new PixelInterleavedSampleModel(
                            dataType, tileW, tileH, pixelStride, scanlineStride, bandOffsets);

                    // minX = -1, minY = 1, width = 26, height = 24
                    // tileGridXOffset = 0, tileGridYOffset = 0, tile size = 25x25
                    TiledImage src = new TiledImage(-1, 1, 26, 24, 0, 0, sampleModel, colorModel);

                    WritableRaster raster = Raster.createWritableRaster(sampleModel, new Point(-1, 1));
                    src.setData(raster);

                    ImageLayout layout = new ImageLayout();
                    layout.setMinX(-1);
                    layout.setMinY(1);
                    layout.setWidth(26);
                    layout.setHeight(24);
                    layout.setTileGridXOffset(0);
                    layout.setTileGridYOffset(0);
                    layout.setTileWidth(tileW);
                    layout.setTileHeight(tileH);
                    layout.setSampleModel(sampleModel);
                    layout.setColorModel(colorModel);

                    double[] destNoData = new double[] {0d, 0d, 0d};
                    Map<Object, Object> config = new HashMap<>();
                    config.put(ImageN.KEY_IMAGE_LAYOUT, layout);

                    ColorConvertOpImage op =
                            new ColorConvertOpImage(src, config, layout, colorModel, nodata, roi, destNoData);

                    // Before the fix the tile computation was throwing an ArrayIndexOutOfBoundsException,
                    // trying to get pixels outside the data arrays.
                    Raster tile = op.computeTile(0, 0);
                    assertNotNull(tile);

                    int[] pixel = new int[3];
                    tile.getPixel(10, 10, pixel);
                    assertNotNull(pixel);
                }
            }
        }
    }

    /**
     * Reading an image based on Spearfish data.
     *
     * @return {@linkplain BufferedImage}
     * @throws IOException
     * @throws FileNotFoundException
     */
    private RenderedImage getTestTiff() throws IOException, FileNotFoundException {
        File spearfish = TestData.file(this, "test.tif");
        RenderedOp image = ImageN.create("ImageRead", spearfish);
        return image;
    }

    /** @return a ColorModel which contains an instance of {@link ColorSpaceImageNExt} */
    private ColorModel getJAIEXTCm() {
        ColorSpace cs = new IHSColorSpaceImageNExt();

        final ColorModel cm = new ComponentColorModel(cs, false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);

        return cm;
    }

    /** @return a ColorModel which contains an instance of {@link ColorSpaceImageN} */
    private ColorModel getJAICm() {
        ColorSpace cs = IHSColorSpace.getInstance();

        final ColorModel cm = new ComponentColorModel(cs, false, false, Transparency.OPAQUE, DataBuffer.TYPE_USHORT);

        return cm;
    }

    /**
     * @return a ColorModel which contains an instance of {@link ColorSpace} different from {@link ColorSpaceImageNExt}
     *     and {@link ColorSpaceImageN}
     */
    private ColorModel getNotJAICm() {
        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_LINEAR_RGB);

        final ColorModel cm = new ComponentColorModel(cs, false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
        return cm;
    }

    @Test
    public void testRegistration() {
        RegistryElementDescriptor descriptor =
                ImageN.getDefaultInstance().getOperationRegistry().getDescriptor("rendered", "ColorConvert");
        assertNotNull(descriptor);
        assertEquals("ColorConvert", descriptor.getName());
        ParameterListDescriptor parameters = descriptor.getParameterListDescriptor("rendered");
        assertArrayEquals(new String[] {"colorModel", "roi", "nodata", "destNoData"}, parameters.getParamNames());
    }
}
