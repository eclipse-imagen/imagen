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
 * SPDX-License-Identifier: Apache-2.0
 */
package org.eclipse.imagen.media.opimage;

import java.awt.Rectangle;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import org.eclipse.imagen.ImageLayout;
import org.eclipse.imagen.PointOpImage;
import org.eclipse.imagen.RasterAccessor;
import org.eclipse.imagen.RasterFormatTag;
import org.eclipse.imagen.media.util.ImageUtil;

/**
 * An <code>OpImage</code> implementing the "AddCollection" operation.
 *
 * @see org.eclipse.imagen.operator.AddCollectionDescriptor
 * @see AddCollectionCRIF
 * @since EA3
 */
final class AddCollectionOpImage extends PointOpImage {

    private byte[][] byteTable = null;

    private synchronized void initByteTable() {

        if (byteTable != null) {
            return;
        }

        /* Initialize byteTable. */
        byteTable = new byte[0x100][0x100];
        for (int j = 0; j < 0x100; j++) {
            byte[] t = byteTable[j];
            for (int i = 0; i < 0x100; i++) {
                t[i] = ImageUtil.clampBytePositive(j + i);
            }
        }
    }

    /**
     * Constructor.
     *
     * @param sources A collection of rendered images to be added.
     * @param layout The destination image layout.
     */
    public AddCollectionOpImage(Collection sources, Map config, ImageLayout layout) {
        super(vectorize(sources), layout, config, true);
    }

    /** Put the rendered images in a vector. */
    private static Vector vectorize(Collection sources) {
        if (sources instanceof Vector) {
            return (Vector) sources;
        } else {
            Vector v = new Vector(sources.size());
            Iterator iter = sources.iterator();
            while (iter.hasNext()) {
                v.add(iter.next());
            }
            return v;
        }
    }

    /**
     * Adds the pixel values of the source images within a specified rectangle.
     *
     * @param sources Cobbled sources, guaranteed to provide all the source data necessary for computing the rectangle.
     * @param dest The tile containing the rectangle to be computed.
     * @param destRect The rectangle within the tile to be computed.
     */
    protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        // Retrieve format tags.
        RasterFormatTag[] formatTags = getFormatTags();

        int numSrcs = getNumSources();

        RasterAccessor dst = new RasterAccessor(dest, destRect, formatTags[numSrcs], getColorModel());

        RasterAccessor[] srcs = new RasterAccessor[numSrcs];
        for (int i = 0; i < numSrcs; i++) {
            Rectangle srcRect = mapDestRect(destRect, i);
            srcs[i] = new RasterAccessor(
                    sources[i], srcRect, formatTags[i], getSourceImage(i).getColorModel());
        }

        switch (dst.getDataType()) {
            case DataBuffer.TYPE_BYTE:
                computeRectByte(srcs, dst);
                break;
            case DataBuffer.TYPE_USHORT:
                computeRectUShort(srcs, dst);
                break;
            case DataBuffer.TYPE_SHORT:
                computeRectShort(srcs, dst);
                break;
            case DataBuffer.TYPE_INT:
                computeRectInt(srcs, dst);
                break;
            case DataBuffer.TYPE_FLOAT:
                computeRectFloat(srcs, dst);
                break;
            case DataBuffer.TYPE_DOUBLE:
                computeRectDouble(srcs, dst);
                break;
        }

        if (dst.needsClamping()) {
            /* Further clamp down to underlying raster data type. */
            dst.clampDataArrays();
        }
        dst.copyDataToRaster();
    }

    private void computeRectByte(RasterAccessor[] srcs, RasterAccessor dst) {
        initByteTable();

        int dstWidth = dst.getWidth();
        int dstHeight = dst.getHeight();
        int dstBands = dst.getNumBands();

        int dstLineStride = dst.getScanlineStride();
        int dstPixelStride = dst.getPixelStride();
        int[] dstBandOffsets = dst.getBandOffsets();
        byte[][] dstData = dst.getByteDataArrays();

        int numSrcs = getNumSources();

        for (int i = 0; i < numSrcs; i++) {
            RasterAccessor src = srcs[i];
            int srcLineStride = src.getScanlineStride();
            int srcPixelStride = src.getPixelStride();
            int[] srcBandOffsets = src.getBandOffsets();
            byte[][] srcData = src.getByteDataArrays();

            for (int b = 0; b < dstBands; b++) {
                int dstLineOffset = dstBandOffsets[b];
                int srcLineOffset = srcBandOffsets[b];

                byte[] d = dstData[b];
                byte[] s = srcData[b];

                for (int h = 0; h < dstHeight; h++) {
                    int dstPixelOffset = dstLineOffset;
                    int srcPixelOffset = srcLineOffset;

                    dstLineOffset += dstLineStride;
                    srcLineOffset += srcLineStride;

                    for (int w = 0; w < dstWidth; w++) {
                        d[dstPixelOffset] = byteTable[d[dstPixelOffset] & 0xff][s[srcPixelOffset] & 0xff];

                        dstPixelOffset += dstPixelStride;
                        srcPixelOffset += srcPixelStride;
                    }
                }
            }
        }
    }

    private void computeRectUShort(RasterAccessor[] srcs, RasterAccessor dst) {
        int dstWidth = dst.getWidth();
        int dstHeight = dst.getHeight();
        int dstBands = dst.getNumBands();

        int dstLineStride = dst.getScanlineStride();
        int dstPixelStride = dst.getPixelStride();
        int[] dstBandOffsets = dst.getBandOffsets();
        short[][] dstData = dst.getShortDataArrays();

        int numSrcs = getNumSources();

        for (int i = 0; i < numSrcs; i++) {
            RasterAccessor src = srcs[i];
            int srcLineStride = src.getScanlineStride();
            int srcPixelStride = src.getPixelStride();
            int[] srcBandOffsets = src.getBandOffsets();
            short[][] srcData = src.getShortDataArrays();

            for (int b = 0; b < dstBands; b++) {
                int dstLineOffset = dstBandOffsets[b];
                int srcLineOffset = srcBandOffsets[b];

                short[] d = dstData[b];
                short[] s = srcData[b];

                for (int h = 0; h < dstHeight; h++) {
                    int dstPixelOffset = dstLineOffset;
                    int srcPixelOffset = srcLineOffset;

                    dstLineOffset += dstLineStride;
                    srcLineOffset += srcLineStride;

                    for (int w = 0; w < dstWidth; w++) {
                        d[dstPixelOffset] = ImageUtil.clampUShortPositive(
                                (d[dstPixelOffset] & 0xffff) + (s[srcPixelOffset] & 0xffff));

                        dstPixelOffset += dstPixelStride;
                        srcPixelOffset += srcPixelStride;
                    }
                }
            }
        }
    }

    private void computeRectShort(RasterAccessor[] srcs, RasterAccessor dst) {
        int dstWidth = dst.getWidth();
        int dstHeight = dst.getHeight();
        int dstBands = dst.getNumBands();

        int dstLineStride = dst.getScanlineStride();
        int dstPixelStride = dst.getPixelStride();
        int[] dstBandOffsets = dst.getBandOffsets();
        short[][] dstData = dst.getShortDataArrays();

        int numSrcs = getNumSources();

        for (int i = 0; i < numSrcs; i++) {
            RasterAccessor src = srcs[i];
            int srcLineStride = src.getScanlineStride();
            int srcPixelStride = src.getPixelStride();
            int[] srcBandOffsets = src.getBandOffsets();
            short[][] srcData = src.getShortDataArrays();

            for (int b = 0; b < dstBands; b++) {
                int dstLineOffset = dstBandOffsets[b];
                int srcLineOffset = srcBandOffsets[b];

                short[] d = dstData[b];
                short[] s = srcData[b];

                for (int h = 0; h < dstHeight; h++) {
                    int dstPixelOffset = dstLineOffset;
                    int srcPixelOffset = srcLineOffset;

                    dstLineOffset += dstLineStride;
                    srcLineOffset += srcLineStride;

                    for (int w = 0; w < dstWidth; w++) {
                        d[dstPixelOffset] = ImageUtil.clampShort(d[dstPixelOffset] + s[srcPixelOffset]);

                        dstPixelOffset += dstPixelStride;
                        srcPixelOffset += srcPixelStride;
                    }
                }
            }
        }
    }

    private void computeRectInt(RasterAccessor[] srcs, RasterAccessor dst) {
        int dstWidth = dst.getWidth();
        int dstHeight = dst.getHeight();
        int dstBands = dst.getNumBands();

        int dstLineStride = dst.getScanlineStride();
        int dstPixelStride = dst.getPixelStride();
        int[] dstBandOffsets = dst.getBandOffsets();
        int[][] dstData = dst.getIntDataArrays();

        int numSrcs = getNumSources();

        for (int i = 0; i < numSrcs; i++) {
            RasterAccessor src = srcs[i];
            int srcLineStride = src.getScanlineStride();
            int srcPixelStride = src.getPixelStride();
            int[] srcBandOffsets = src.getBandOffsets();
            int[][] srcData = src.getIntDataArrays();

            for (int b = 0; b < dstBands; b++) {
                int dstLineOffset = dstBandOffsets[b];
                int srcLineOffset = srcBandOffsets[b];

                int[] d = dstData[b];
                int[] s = srcData[b];

                for (int h = 0; h < dstHeight; h++) {
                    int dstPixelOffset = dstLineOffset;
                    int srcPixelOffset = srcLineOffset;

                    dstLineOffset += dstLineStride;
                    srcLineOffset += srcLineStride;

                    for (int w = 0; w < dstWidth; w++) {
                        d[dstPixelOffset] = ImageUtil.clampInt((long) d[dstPixelOffset] + (long) s[srcPixelOffset]);

                        dstPixelOffset += dstPixelStride;
                        srcPixelOffset += srcPixelStride;
                    }
                }
            }
        }
    }

    private void computeRectFloat(RasterAccessor[] srcs, RasterAccessor dst) {
        int dstWidth = dst.getWidth();
        int dstHeight = dst.getHeight();
        int dstBands = dst.getNumBands();

        int dstLineStride = dst.getScanlineStride();
        int dstPixelStride = dst.getPixelStride();
        int[] dstBandOffsets = dst.getBandOffsets();
        float[][] dstData = dst.getFloatDataArrays();

        int numSrcs = getNumSources();

        for (int i = 0; i < numSrcs; i++) {
            RasterAccessor src = srcs[i];
            int srcLineStride = src.getScanlineStride();
            int srcPixelStride = src.getPixelStride();
            int[] srcBandOffsets = src.getBandOffsets();
            float[][] srcData = src.getFloatDataArrays();

            for (int b = 0; b < dstBands; b++) {
                int dstLineOffset = dstBandOffsets[b];
                int srcLineOffset = srcBandOffsets[b];

                float[] d = dstData[b];
                float[] s = srcData[b];

                for (int h = 0; h < dstHeight; h++) {
                    int dstPixelOffset = dstLineOffset;
                    int srcPixelOffset = srcLineOffset;

                    dstLineOffset += dstLineStride;
                    srcLineOffset += srcLineStride;

                    for (int w = 0; w < dstWidth; w++) {
                        d[dstPixelOffset] =
                                ImageUtil.clampFloat((double) d[dstPixelOffset] + (double) s[srcPixelOffset]);

                        dstPixelOffset += dstPixelStride;
                        srcPixelOffset += srcPixelStride;
                    }
                }
            }
        }
    }

    private void computeRectDouble(RasterAccessor[] srcs, RasterAccessor dst) {
        int dstWidth = dst.getWidth();
        int dstHeight = dst.getHeight();
        int dstBands = dst.getNumBands();

        int dstLineStride = dst.getScanlineStride();
        int dstPixelStride = dst.getPixelStride();
        int[] dstBandOffsets = dst.getBandOffsets();
        double[][] dstData = dst.getDoubleDataArrays();

        int numSrcs = getNumSources();

        for (int i = 0; i < numSrcs; i++) {
            RasterAccessor src = srcs[i];
            int srcLineStride = src.getScanlineStride();
            int srcPixelStride = src.getPixelStride();
            int[] srcBandOffsets = src.getBandOffsets();
            double[][] srcData = src.getDoubleDataArrays();

            for (int b = 0; b < dstBands; b++) {
                int dstLineOffset = dstBandOffsets[b];
                int srcLineOffset = srcBandOffsets[b];

                double[] d = dstData[b];
                double[] s = srcData[b];

                for (int h = 0; h < dstHeight; h++) {
                    int dstPixelOffset = dstLineOffset;
                    int srcPixelOffset = srcLineOffset;

                    dstLineOffset += dstLineStride;
                    srcLineOffset += srcLineStride;

                    for (int w = 0; w < dstWidth; w++) {
                        d[dstPixelOffset] = d[dstPixelOffset] + s[srcPixelOffset];

                        dstPixelOffset += dstPixelStride;
                        srcPixelOffset += srcPixelStride;
                    }
                }
            }
        }
    }
}
