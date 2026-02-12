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

import java.awt.image.RenderedImage;
import java.util.Map;
import org.eclipse.imagen.BorderExtender;
import org.eclipse.imagen.ImageLayout;
import org.eclipse.imagen.RasterAccessor;
// import org.eclipse.imagen.media.test.OpImageTester;
import org.eclipse.imagen.operator.MedianFilterDescriptor;

/** An OpImage class to perform median filtering on a source image. */
final class MedianFilterPlusOpImage extends MedianFilterOpImage {

    /**
     * Creates a MedianFilterPlusOpImage with the given source and maskSize. The image dimensions are derived from the
     * source image. The tile grid layout, SampleModel, and ColorModel may optionally be specified by an ImageLayout
     * object.
     *
     * @param source a RenderedImage.
     * @param extender a BorderExtender, or null.
     * @param layout an ImageLayout optionally containing the tile grid layout, SampleModel, and ColorModel, or null.
     * @param maskSize the mask size.
     */
    public MedianFilterPlusOpImage(
            RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, int maskSize) {
        super(source, extender, config, layout, MedianFilterDescriptor.MEDIAN_MASK_PLUS, maskSize);
    }

    protected void byteLoop(RasterAccessor src, RasterAccessor dst, int filterSize) {
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int dnumBands = dst.getNumBands();

        byte dstDataArrays[][] = dst.getByteDataArrays();
        int dstBandOffsets[] = dst.getBandOffsets();
        int dstPixelStride = dst.getPixelStride();
        int dstScanlineStride = dst.getScanlineStride();

        byte srcDataArrays[][] = src.getByteDataArrays();
        int srcBandOffsets[] = src.getBandOffsets();
        int srcPixelStride = src.getPixelStride();
        int srcScanlineStride = src.getScanlineStride();

        int values[] = new int[filterSize * 2 - 1];
        int wp = filterSize;
        int offset = filterSize / 2;

        for (int k = 0; k < dnumBands; k++) {
            byte dstData[] = dstDataArrays[k];
            byte srcData[] = srcDataArrays[k];
            int srcScanlineOffset = srcBandOffsets[k];
            int dstScanlineOffset = dstBandOffsets[k];
            for (int j = 0; j < dheight; j++) {
                int srcPixelOffset = srcScanlineOffset;
                int dstPixelOffset = dstScanlineOffset;

                for (int i = 0; i < dwidth; i++) {
                    int valueCount = 0;

                    // figure out where the top of the plus starts
                    int imageOffset = srcPixelOffset + srcPixelStride * offset;
                    for (int u = 0; u < wp; u++) {
                        values[valueCount++] = (int) (srcData[imageOffset] & 0xff);
                        imageOffset += srcScanlineStride;
                    }

                    // remove the center element so it doesn't get counted
                    // twice when we do the horizontal piece
                    valueCount--;
                    values[offset] = values[valueCount];

                    // figure out where the left side of plus starts
                    imageOffset = srcPixelOffset + srcScanlineStride * offset;

                    for (int v = 0; v < wp; v++) {
                        values[valueCount++] = (int) (srcData[imageOffset] & 0xff);
                        imageOffset += srcPixelStride;
                    }
                    int val = medianFilter(values);

                    dstData[dstPixelOffset] = (byte) val;
                    srcPixelOffset += srcPixelStride;
                    dstPixelOffset += dstPixelStride;
                }
                srcScanlineOffset += srcScanlineStride;
                dstScanlineOffset += dstScanlineStride;
            }
        }
    }

    protected void shortLoop(RasterAccessor src, RasterAccessor dst, int filterSize) {
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int dnumBands = dst.getNumBands();

        short dstDataArrays[][] = dst.getShortDataArrays();
        int dstBandOffsets[] = dst.getBandOffsets();
        int dstPixelStride = dst.getPixelStride();
        int dstScanlineStride = dst.getScanlineStride();

        short srcDataArrays[][] = src.getShortDataArrays();
        int srcBandOffsets[] = src.getBandOffsets();
        int srcPixelStride = src.getPixelStride();
        int srcScanlineStride = src.getScanlineStride();

        int values[] = new int[filterSize * 2 - 1];
        int wp = filterSize;
        int offset = filterSize / 2;

        for (int k = 0; k < dnumBands; k++) {
            short dstData[] = dstDataArrays[k];
            short srcData[] = srcDataArrays[k];
            int srcScanlineOffset = srcBandOffsets[k];
            int dstScanlineOffset = dstBandOffsets[k];
            for (int j = 0; j < dheight; j++) {
                int srcPixelOffset = srcScanlineOffset;
                int dstPixelOffset = dstScanlineOffset;

                for (int i = 0; i < dwidth; i++) {
                    int valueCount = 0;

                    // figure out where the top of the plus starts
                    int imageOffset = srcPixelOffset + srcPixelStride * offset;
                    for (int u = 0; u < wp; u++) {
                        values[valueCount++] = (int) (srcData[imageOffset]);
                        imageOffset += srcScanlineStride;
                    }

                    // remove the center element so it doesn't get counted
                    // twice when we do the horizontal piece
                    valueCount--;
                    values[offset] = values[valueCount];

                    // figure out where the left side of plus starts
                    imageOffset = srcPixelOffset + srcScanlineStride * offset;

                    for (int v = 0; v < wp; v++) {
                        values[valueCount++] = srcData[imageOffset];
                        imageOffset += srcPixelStride;
                    }

                    int val = medianFilter(values);

                    dstData[dstPixelOffset] = (short) val;
                    srcPixelOffset += srcPixelStride;
                    dstPixelOffset += dstPixelStride;
                }
                srcScanlineOffset += srcScanlineStride;
                dstScanlineOffset += dstScanlineStride;
            }
        }
    }

    protected void ushortLoop(RasterAccessor src, RasterAccessor dst, int filterSize) {
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int dnumBands = dst.getNumBands();

        short dstDataArrays[][] = dst.getShortDataArrays();
        int dstBandOffsets[] = dst.getBandOffsets();
        int dstPixelStride = dst.getPixelStride();
        int dstScanlineStride = dst.getScanlineStride();

        short srcDataArrays[][] = src.getShortDataArrays();
        int srcBandOffsets[] = src.getBandOffsets();
        int srcPixelStride = src.getPixelStride();
        int srcScanlineStride = src.getScanlineStride();

        int values[] = new int[filterSize * 2 - 1];
        int wp = filterSize;
        int offset = filterSize / 2;

        for (int k = 0; k < dnumBands; k++) {
            short dstData[] = dstDataArrays[k];
            short srcData[] = srcDataArrays[k];
            int srcScanlineOffset = srcBandOffsets[k];
            int dstScanlineOffset = dstBandOffsets[k];
            for (int j = 0; j < dheight; j++) {
                int srcPixelOffset = srcScanlineOffset;
                int dstPixelOffset = dstScanlineOffset;

                for (int i = 0; i < dwidth; i++) {
                    int valueCount = 0;

                    // figure out where the top of the plus starts
                    int imageOffset = srcPixelOffset + srcPixelStride * offset;
                    for (int u = 0; u < wp; u++) {
                        values[valueCount++] = (int) (srcData[imageOffset] & 0xffff);
                        imageOffset += srcScanlineStride;
                    }

                    // remove the center element so it doesn't get counted
                    // twice when we do the horizontal piece
                    valueCount--;
                    values[offset] = values[valueCount];

                    // figure out where the left side of plus starts
                    imageOffset = srcPixelOffset + srcScanlineStride * offset;

                    for (int v = 0; v < wp; v++) {
                        values[valueCount++] = (int) (srcData[imageOffset] & 0xffff);
                        imageOffset += srcPixelStride;
                    }

                    int val = medianFilter(values);

                    dstData[dstPixelOffset] = (short) val;
                    srcPixelOffset += srcPixelStride;
                    dstPixelOffset += dstPixelStride;
                }
                srcScanlineOffset += srcScanlineStride;
                dstScanlineOffset += dstScanlineStride;
            }
        }
    }

    protected void intLoop(RasterAccessor src, RasterAccessor dst, int filterSize) {
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int dnumBands = dst.getNumBands();

        int dstDataArrays[][] = dst.getIntDataArrays();
        int dstBandOffsets[] = dst.getBandOffsets();
        int dstPixelStride = dst.getPixelStride();
        int dstScanlineStride = dst.getScanlineStride();

        int srcDataArrays[][] = src.getIntDataArrays();
        int srcBandOffsets[] = src.getBandOffsets();
        int srcPixelStride = src.getPixelStride();
        int srcScanlineStride = src.getScanlineStride();

        int values[] = new int[filterSize * 2 - 1];
        int wp = filterSize;
        int offset = filterSize / 2;

        for (int k = 0; k < dnumBands; k++) {
            int dstData[] = dstDataArrays[k];
            int srcData[] = srcDataArrays[k];
            int srcScanlineOffset = srcBandOffsets[k];
            int dstScanlineOffset = dstBandOffsets[k];
            for (int j = 0; j < dheight; j++) {
                int srcPixelOffset = srcScanlineOffset;
                int dstPixelOffset = dstScanlineOffset;

                for (int i = 0; i < dwidth; i++) {
                    int valueCount = 0;

                    // figure out where the top of the plus starts
                    int imageOffset = srcPixelOffset + srcPixelStride * offset;
                    for (int u = 0; u < wp; u++) {
                        values[valueCount++] = srcData[imageOffset];
                        imageOffset += srcScanlineStride;
                    }

                    // remove the center element so it doesn't get counted
                    // twice when we do the horizontal piece
                    valueCount--;
                    values[offset] = values[valueCount];

                    // figure out where the left side of plus starts
                    imageOffset = srcPixelOffset + srcScanlineStride * offset;

                    for (int v = 0; v < wp; v++) {
                        values[valueCount++] = srcData[imageOffset];
                        imageOffset += srcPixelStride;
                    }
                    int val = medianFilter(values);

                    dstData[dstPixelOffset] = val;
                    srcPixelOffset += srcPixelStride;
                    dstPixelOffset += dstPixelStride;
                }
                srcScanlineOffset += srcScanlineStride;
                dstScanlineOffset += dstScanlineStride;
            }
        }
    }

    protected void floatLoop(RasterAccessor src, RasterAccessor dst, int filterSize) {
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int dnumBands = dst.getNumBands();

        float dstDataArrays[][] = dst.getFloatDataArrays();
        int dstBandOffsets[] = dst.getBandOffsets();
        int dstPixelStride = dst.getPixelStride();
        int dstScanlineStride = dst.getScanlineStride();

        float srcDataArrays[][] = src.getFloatDataArrays();
        int srcBandOffsets[] = src.getBandOffsets();
        int srcPixelStride = src.getPixelStride();
        int srcScanlineStride = src.getScanlineStride();

        float values[] = new float[filterSize * 2 - 1];
        int wp = filterSize;
        int offset = filterSize / 2;

        for (int k = 0; k < dnumBands; k++) {
            float dstData[] = dstDataArrays[k];
            float srcData[] = srcDataArrays[k];
            int srcScanlineOffset = srcBandOffsets[k];
            int dstScanlineOffset = dstBandOffsets[k];
            for (int j = 0; j < dheight; j++) {
                int srcPixelOffset = srcScanlineOffset;
                int dstPixelOffset = dstScanlineOffset;

                for (int i = 0; i < dwidth; i++) {
                    int valueCount = 0;

                    // figure out where the top of the plus starts
                    int imageOffset = srcPixelOffset + srcPixelStride * offset;
                    for (int u = 0; u < wp; u++) {
                        values[valueCount++] = srcData[imageOffset];
                        imageOffset += srcScanlineStride;
                    }

                    // remove the center element so it doesn't get counted
                    // twice when we do the horizontal piece
                    valueCount--;
                    values[offset] = values[valueCount];

                    // figure out where the left side of plus starts
                    imageOffset = srcPixelOffset + srcScanlineStride * offset;

                    for (int v = 0; v < wp; v++) {
                        values[valueCount++] = srcData[imageOffset];
                        imageOffset += srcPixelStride;
                    }

                    float val = medianFilterFloat(values);

                    dstData[dstPixelOffset] = val;
                    srcPixelOffset += srcPixelStride;
                    dstPixelOffset += dstPixelStride;
                }
                srcScanlineOffset += srcScanlineStride;
                dstScanlineOffset += dstScanlineStride;
            }
        }
    }

    protected void doubleLoop(RasterAccessor src, RasterAccessor dst, int filterSize) {
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int dnumBands = dst.getNumBands();

        double dstDataArrays[][] = dst.getDoubleDataArrays();
        int dstBandOffsets[] = dst.getBandOffsets();
        int dstPixelStride = dst.getPixelStride();
        int dstScanlineStride = dst.getScanlineStride();

        double srcDataArrays[][] = src.getDoubleDataArrays();
        int srcBandOffsets[] = src.getBandOffsets();
        int srcPixelStride = src.getPixelStride();
        int srcScanlineStride = src.getScanlineStride();

        double values[] = new double[filterSize * 2 - 1];
        int wp = filterSize;
        int offset = filterSize / 2;

        for (int k = 0; k < dnumBands; k++) {
            double dstData[] = dstDataArrays[k];
            double srcData[] = srcDataArrays[k];
            int srcScanlineOffset = srcBandOffsets[k];
            int dstScanlineOffset = dstBandOffsets[k];
            for (int j = 0; j < dheight; j++) {
                int srcPixelOffset = srcScanlineOffset;
                int dstPixelOffset = dstScanlineOffset;

                for (int i = 0; i < dwidth; i++) {
                    int valueCount = 0;

                    // figure out where the top of the plus starts
                    int imageOffset = srcPixelOffset + srcPixelStride * offset;
                    for (int u = 0; u < wp; u++) {
                        values[valueCount++] = srcData[imageOffset];
                        imageOffset += srcScanlineStride;
                    }

                    // remove the center element so it doesn't get counted
                    // twice when we do the horizontal piece
                    valueCount--;
                    values[offset] = values[valueCount];

                    // figure out where the left side of plus starts
                    imageOffset = srcPixelOffset + srcScanlineStride * offset;

                    for (int v = 0; v < wp; v++) {
                        values[valueCount++] = srcData[imageOffset];
                        imageOffset += srcPixelStride;
                    }

                    double val = medianFilterDouble(values);

                    dstData[dstPixelOffset] = val;
                    srcPixelOffset += srcPixelStride;
                    dstPixelOffset += dstPixelStride;
                }
                srcScanlineOffset += srcScanlineStride;
                dstScanlineOffset += dstScanlineStride;
            }
        }
    }

    //     public static OpImage createTestImage(OpImageTester oit) {
    //         return new MedianFilterPlusOpImage(oit.getSource(), null, null,
    //                                            new ImageLayout(oit.getSource()),
    //                                            3);
    //     }
}
