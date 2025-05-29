/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.eclipse.imagen.media.opimage;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Map;
import org.eclipse.imagen.ImageLayout;
import org.eclipse.imagen.RasterAccessor;
import org.eclipse.imagen.RasterFactory;
import org.eclipse.imagen.RasterFormatTag;
import org.eclipse.imagen.UntiledOpImage;
import org.eclipse.imagen.media.util.JDKWorkarounds;
import org.eclipse.imagen.media.util.MathJAI;

/**
 * An <code>OpImage</code> implementing the forward and inverse even discrete cosine transform (DCT) operations as
 * described in <code>org.eclipse.imagen.operator.DCTDescriptor</code> and <code>
 * org.eclipse.imagen.operator.IDCTDescriptor</code>.
 *
 * <p>The DCT operation is implemented using a one-dimensional fast cosine transform (FCT) which is applied successively
 * to the rows and the columns of the image. All image dimensions are enlarged to the next positive power of 2 greater
 * than or equal to the respective dimension unless the dimension is unity in which case it is not modified. Source
 * image values are padded with zeros when the dimension is smaller than the output power-of-2 dimension.
 *
 * @since EA3
 * @see org.eclipse.imagen.UntiledOpImage
 * @see org.eclipse.imagen.operator.DCTDescriptor
 * @see org.eclipse.imagen.operator.IDCTDescriptor
 */
public class DCTOpImage extends UntiledOpImage {
    /** The Fast Cosine Transform object. */
    private FCT fct;

    /**
     * Override the dimension specification for the destination such that it has width and height which are equal to
     * non-negative powers of 2.
     */
    private static ImageLayout layoutHelper(ImageLayout layout, RenderedImage source) {
        // Create an ImageLayout or clone the one passed in.
        ImageLayout il = layout == null ? new ImageLayout() : (ImageLayout) layout.clone();

        // Force the origin to coincide with that of the source.
        il.setMinX(source.getMinX());
        il.setMinY(source.getMinY());

        // Recalculate the non-unity dimensions to be a positive power of 2.
        // XXX This calculation should not be effected if an implementation
        // of the FCT which supports arbitrary dimensions is used.
        boolean createNewSampleModel = false;
        int w = il.getWidth(source);
        if (w > 1) {
            int newWidth = MathJAI.nextPositivePowerOf2(w);
            if (newWidth != w) {
                il.setWidth(w = newWidth);
                createNewSampleModel = true;
            }
        }
        int h = il.getHeight(source);
        if (h > 1) {
            int newHeight = MathJAI.nextPositivePowerOf2(h);
            if (newHeight != h) {
                il.setHeight(h = newHeight);
                createNewSampleModel = true;
            }
        }

        // Force the image to contain floating point data.
        SampleModel sm = il.getSampleModel(source);
        int dataType = sm.getTransferType();
        if (dataType != DataBuffer.TYPE_FLOAT && dataType != DataBuffer.TYPE_DOUBLE) {
            dataType = DataBuffer.TYPE_FLOAT;
            createNewSampleModel = true;
        }

        // Create a new SampleModel for the destination.
        if (createNewSampleModel) {
            sm = RasterFactory.createComponentSampleModel(sm, dataType, w, h, sm.getNumBands());
            il.setSampleModel(sm);

            // Clear the ColorModel mask if needed.
            ColorModel cm = il.getColorModel(null);
            if (cm != null && !JDKWorkarounds.areCompatibleDataModels(sm, cm)) {
                // Clear the mask bit if incompatible.
                il.unsetValid(ImageLayout.COLOR_MODEL_MASK);
            }
        }

        return il;
    }

    /**
     * Constructs a <code>DCTOpImage</code> object.
     *
     * <p>The image dimensions are the respective next positive powers of 2 greater than or equal to the dimensions of
     * the source image. The tile grid layout, SampleModel, and ColorModel may optionally be specified by an ImageLayout
     * object.
     *
     * @param source A RenderedImage.
     * @param layout An ImageLayout optionally containing the tile grid layout, SampleModel, and ColorModel, or null.
     * @param fct The Fast Cosine Transform object.
     */
    public DCTOpImage(RenderedImage source, Map config, ImageLayout layout, FCT fct) {
        super(source, config, layoutHelper(layout, source));

        // Cache the FCT object.
        this.fct = fct;
    }

    /**
     * Computes the source point corresponding to the supplied point.
     *
     * @param destPt the position in destination image coordinates to map to source image coordinates.
     * @return <code>null</code>.
     * @throws IllegalArgumentException if <code>destPt</code> is <code>null</code>.
     * @since JAI 1.1.2
     */
    public Point2D mapDestPoint(Point2D destPt) {
        if (destPt == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }

        return null;
    }

    /**
     * Computes the destination point corresponding to the supplied point.
     *
     * @return <code>null</code>.
     * @throws IllegalArgumentException if <code>sourcePt</code> is <code>null</code>.
     * @since JAI 1.1.2
     */
    public Point2D mapSourcePoint(Point2D sourcePt) {
        if (sourcePt == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }

        return null;
    }

    /*
     * Calculate the discrete cosine transform of the source image.
     *
     * @param source The source Raster; should be the whole image.
     * @param dest The destination WritableRaster; should be the whole image.
     * @param destRect The destination Rectangle; should be the image bounds.
     */
    protected void computeImage(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        Raster source = sources[0];

        // Degenerate case.
        if (destRect.width == 1 && destRect.height == 1) {
            double[] pixel = source.getPixel(destRect.x, destRect.y, (double[]) null);
            dest.setPixel(destRect.x, destRect.y, pixel);
            return;
        }

        // Initialize to first non-unity length to be encountered.
        fct.setLength(destRect.width > 1 ? getWidth() : getHeight());

        // Get some information about the source image.
        int srcWidth = source.getWidth();
        int srcHeight = source.getHeight();
        int srcX = source.getMinX();
        int srcY = source.getMinY();

        // Retrieve format tags.
        RasterFormatTag[] formatTags = getFormatTags();

        RasterAccessor srcAccessor = new RasterAccessor(
                source,
                new Rectangle(
                        srcX, srcY,
                        srcWidth, srcHeight),
                formatTags[0],
                getSourceImage(0).getColorModel());
        RasterAccessor dstAccessor = new RasterAccessor(dest, destRect, formatTags[1], getColorModel());

        // Set data type flags.
        int srcDataType = srcAccessor.getDataType();
        int dstDataType = dstAccessor.getDataType();

        // Set pixel and line strides.
        int srcPixelStride = srcAccessor.getPixelStride();
        int srcScanlineStride = srcAccessor.getScanlineStride();
        int dstPixelStride = dstAccessor.getPixelStride();
        int dstScanlineStride = dstAccessor.getScanlineStride();

        // Loop over the bands.
        int numBands = sampleModel.getNumBands();
        for (int band = 0; band < numBands; band++) {
            // Get the source and destination arrays for this band.
            Object srcData = srcAccessor.getDataArray(band);
            Object dstData = dstAccessor.getDataArray(band);

            if (destRect.width > 1) {
                // Set the FCT length.
                fct.setLength(getWidth());

                // Initialize the data offsets for this band.
                int srcOffset = srcAccessor.getBandOffset(band);
                int dstOffset = dstAccessor.getBandOffset(band);

                // Perform the row transforms.
                for (int row = 0; row < srcHeight; row++) {
                    // Set the input data of the FCT.
                    fct.setData(srcDataType, srcData, srcOffset, srcPixelStride, srcWidth);

                    // Calculate the DFT of the row.
                    fct.transform();

                    // Get the output data of the FCT.
                    fct.getData(
                            dstDataType, dstData,
                            dstOffset, dstPixelStride);

                    // Increment the data offsets.
                    srcOffset += srcScanlineStride;
                    dstOffset += dstScanlineStride;
                }
            }

            if (destRect.width == 1) { // destRect.height > 1
                // Initialize the data offsets for this band.
                int srcOffset = srcAccessor.getBandOffset(band);
                int dstOffset = dstAccessor.getBandOffset(band);

                // Set the input data of the FCT.
                fct.setData(srcDataType, srcData, srcOffset, srcScanlineStride, srcHeight);

                // Calculate the DFT of the row.
                fct.transform();

                // Get the output data of the FCT.
                fct.getData(
                        dstDataType, dstData,
                        dstOffset, dstScanlineStride);
            } else if (destRect.height > 1) { // destRect.width > 1
                // Reset the FCT length.
                fct.setLength(getHeight());

                // Initialize destination offset.
                int dstOffset = dstAccessor.getBandOffset(band);

                // Perform the column transforms.
                for (int col = 0; col < destRect.width; col++) {
                    // Set the input data of the FCT.
                    fct.setData(dstDataType, dstData, dstOffset, dstScanlineStride, destRect.height);

                    // Calculate the DFT of the column.
                    fct.transform();

                    // Get the output data of the FCT.
                    fct.getData(
                            dstDataType, dstData,
                            dstOffset, dstScanlineStride);

                    // Increment the data offset.
                    dstOffset += dstPixelStride;
                }
            }
        }

        if (dstAccessor.needsClamping()) {
            dstAccessor.clampDataArrays();
        }

        // Make sure that the output data is copied to the destination.
        dstAccessor.copyDataToRaster();
    }
}
