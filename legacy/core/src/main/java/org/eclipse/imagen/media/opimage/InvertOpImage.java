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
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Map;
import org.eclipse.imagen.ColormapOpImage;
import org.eclipse.imagen.ImageLayout;
import org.eclipse.imagen.RasterAccessor;
import org.eclipse.imagen.RasterFormatTag;

/**
 * An <code>OpImage</code> implementing the "Invert" operation as described in <code>
 * org.eclipse.imagen.operator.InvertDescriptor</code>.
 *
 * <p>This <code>OpImage</code> negates the pixel values of the source image on a per-band basis by subtracting the
 * pixel values from the maximum value of the respective data type. Please note that data type byte is treated as
 * unsigned with a maximum value of 0xFF. The value of the pixel (x, y) in the destination image is defined as:
 *
 * <pre>
 * for (b = 0; b < dst.numBands; b++) {
 *     dst[y][x][b] = maximumValue - src[y][x][b];
 * }
 * </pre>
 *
 * @see org.eclipse.imagen.operator.InvertDescriptor
 * @see InvertRIF
 */
final class InvertOpImage extends ColormapOpImage {

    /**
     * Constructs an <code>InvertOpImage</code>.
     *
     * <p>The <code>layout</code> parameter may optionally contains the tile grid layout, sample model, and/or color
     * model. The image dimension is set to the same values as that of the source image.
     *
     * <p>The image layout of the source image is used as the fall-back for the image layout of the destination image.
     * Any layout parameters not specified in the <code>layout</code> argument are set to the same value as that of the
     * source.
     *
     * @param source The source image.
     * @param layout The destination image layout.
     */
    public InvertOpImage(RenderedImage source, Map config, ImageLayout layout) {
        super(source, layout, config, true);

        // Set flag to permit in-place operation.
        permitInPlaceOperation();

        // Initialize the colormap if necessary.
        initializeColormapOperation();
    }

    /** Transform the colormap according to the rescaling parameters. */
    protected void transformColormap(byte[][] colormap) {

        for (int b = 0; b < 3; b++) {
            byte[] map = colormap[b];
            int mapSize = map.length;

            for (int i = 0; i < mapSize; i++) {
                map[i] = (byte) (255 - (map[i] & 0xFF));
            }
        }
    }

    /**
     * Inverts the pixel values within a specified rectangle.
     *
     * @param sources Cobbled sources, guaranteed to provide all the source data necessary for computing the rectangle.
     * @param dest The tile containing the rectangle to be computed.
     * @param destRect The rectangle within the tile to be computed.
     */
    protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        // Retrieve format tags.
        RasterFormatTag[] formatTags = getFormatTags();

        /* For ColormapOpImage, srcRect = destRect. */
        RasterAccessor s = new RasterAccessor(
                sources[0], destRect, formatTags[0], getSourceImage(0).getColorModel());
        RasterAccessor d = new RasterAccessor(dest, destRect, formatTags[1], getColorModel());

        if (d.isBinary()) {
            byte[] srcBits = s.getBinaryDataArray();
            byte[] dstBits = d.getBinaryDataArray();
            int length = dstBits.length;
            for (int i = 0; i < length; i++) {
                dstBits[i] = (byte) (~(srcBits[i]));
            }
            d.copyBinaryDataToRaster();
        } else {
            switch (d.getDataType()) {
                case DataBuffer.TYPE_BYTE:
                    computeRectByte(s, d);
                    break;
                case DataBuffer.TYPE_USHORT:
                    computeRectUShort(s, d);
                    break;
                case DataBuffer.TYPE_SHORT:
                    computeRectShort(s, d);
                    break;
                case DataBuffer.TYPE_INT:
                    computeRectInt(s, d);
                    break;
                case DataBuffer.TYPE_FLOAT:
                case DataBuffer.TYPE_DOUBLE:
                    throw new RuntimeException(JaiI18N.getString("InvertOpImage0"));
            }

            d.copyDataToRaster();
        }
    }

    private void computeRectByte(RasterAccessor src, RasterAccessor dst) {
        int sLineStride = src.getScanlineStride();
        int sPixelStride = src.getPixelStride();
        int[] sBandOffsets = src.getBandOffsets();
        byte[][] sData = src.getByteDataArrays();

        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int bands = dst.getNumBands();
        int dLineStride = dst.getScanlineStride();
        int dPixelStride = dst.getPixelStride();
        int[] dBandOffsets = dst.getBandOffsets();
        byte[][] dData = dst.getByteDataArrays();

        for (int b = 0; b < bands; b++) {
            byte[] s = sData[b];
            byte[] d = dData[b];

            int sLineOffset = sBandOffsets[b];
            int dLineOffset = dBandOffsets[b];

            for (int h = 0; h < dheight; h++) {
                int sPixelOffset = sLineOffset;
                int dPixelOffset = dLineOffset;

                sLineOffset += sLineStride;
                dLineOffset += dLineStride;

                int dstEnd = dPixelOffset + dwidth * dPixelStride;
                while (dPixelOffset < dstEnd) {
                    d[dPixelOffset] = (byte) (255 - (s[sPixelOffset] & 0xFF));
                    sPixelOffset += sPixelStride;
                    dPixelOffset += dPixelStride;
                }
            }
        }
    }

    private void computeRectUShort(RasterAccessor src, RasterAccessor dst) {
        int sLineStride = src.getScanlineStride();
        int sPixelStride = src.getPixelStride();
        int[] sBandOffsets = src.getBandOffsets();
        short[][] sData = src.getShortDataArrays();

        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int bands = dst.getNumBands();
        int dLineStride = dst.getScanlineStride();
        int dPixelStride = dst.getPixelStride();
        int[] dBandOffsets = dst.getBandOffsets();
        short[][] dData = dst.getShortDataArrays();

        for (int b = 0; b < bands; b++) {
            short[] s = sData[b];
            short[] d = dData[b];

            int sLineOffset = sBandOffsets[b];
            int dLineOffset = dBandOffsets[b];

            for (int h = 0; h < dheight; h++) {
                int sPixelOffset = sLineOffset;
                int dPixelOffset = dLineOffset;

                sLineOffset += sLineStride;
                dLineOffset += dLineStride;

                int dstEnd = dPixelOffset + dwidth * dPixelStride;
                while (dPixelOffset < dstEnd) {
                    d[dPixelOffset] = (short) (65535 - (s[sPixelOffset] & 0xFFFF));
                    sPixelOffset += sPixelStride;
                    dPixelOffset += dPixelStride;
                }
            }
        }
    }

    private void computeRectShort(RasterAccessor src, RasterAccessor dst) {
        int sLineStride = src.getScanlineStride();
        int sPixelStride = src.getPixelStride();
        int[] sBandOffsets = src.getBandOffsets();
        short[][] sData = src.getShortDataArrays();

        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int bands = dst.getNumBands();
        int dLineStride = dst.getScanlineStride();
        int dPixelStride = dst.getPixelStride();
        int[] dBandOffsets = dst.getBandOffsets();
        short[][] dData = dst.getShortDataArrays();

        for (int b = 0; b < bands; b++) {
            short[] s = sData[b];
            short[] d = dData[b];

            int sLineOffset = sBandOffsets[b];
            int dLineOffset = dBandOffsets[b];

            for (int h = 0; h < dheight; h++) {
                int sPixelOffset = sLineOffset;
                int dPixelOffset = dLineOffset;

                sLineOffset += sLineStride;
                dLineOffset += dLineStride;

                int dstEnd = dPixelOffset + dwidth * dPixelStride;
                while (dPixelOffset < dstEnd) {
                    d[dPixelOffset] = (short) (Short.MAX_VALUE - s[sPixelOffset]);

                    sPixelOffset += sPixelStride;
                    dPixelOffset += dPixelStride;
                }
            }
        }
    }

    private void computeRectInt(RasterAccessor src, RasterAccessor dst) {
        int sLineStride = src.getScanlineStride();
        int sPixelStride = src.getPixelStride();
        int[] sBandOffsets = src.getBandOffsets();
        int[][] sData = src.getIntDataArrays();

        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int bands = dst.getNumBands();
        int dLineStride = dst.getScanlineStride();
        int dPixelStride = dst.getPixelStride();
        int[] dBandOffsets = dst.getBandOffsets();
        int[][] dData = dst.getIntDataArrays();

        /*
         * For the TAG_INT_COPIED case, the destination data type may
         * be any of the integral data types. The "clamp" function must
         * clamp to the appropriate range for that data type.
         */
        int[] s = sData[0];
        int[] d = dData[0];
        int pixels = d.length;

        /*
         * The pixel data array is actually retrieved using getPixels
         * so there's no need to worry about scanline stride, pixel
         * stride, band offset, data offset, etc.
         */
        switch (sampleModel.getTransferType()) {
            case DataBuffer.TYPE_BYTE:
                for (int i = 0; i < pixels; i++) {
                    d[i] = (~s[i]) & 0xFF;
                }
                break;

            case DataBuffer.TYPE_USHORT:
                for (int i = 0; i < pixels; i++) {
                    d[i] = (~s[i]) & 0xFFFF;
                }
                break;

            case DataBuffer.TYPE_SHORT:
                for (int i = 0; i < pixels; i++) {
                    d[i] = Short.MAX_VALUE - s[i];
                }
                break;

            case DataBuffer.TYPE_INT:
                for (int b = 0; b < bands; b++) {
                    s = sData[b];
                    d = dData[b];

                    int sLineOffset = sBandOffsets[b];
                    int dLineOffset = dBandOffsets[b];

                    for (int h = 0; h < dheight; h++) {
                        int sPixelOffset = sLineOffset;
                        int dPixelOffset = dLineOffset;

                        sLineOffset += sLineStride;
                        dLineOffset += dLineStride;

                        int dstEnd = dPixelOffset + dwidth * dPixelStride;
                        while (dPixelOffset < dstEnd) {
                            d[dPixelOffset] = Integer.MAX_VALUE - s[sPixelOffset];

                            sPixelOffset += sPixelStride;
                            dPixelOffset += dPixelStride;
                        }
                    }
                }
                break;
        }
    }
}
