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
import org.eclipse.imagen.operator.MinFilterDescriptor;

/** An OpImage class to perform min filtering on a source image. */
final class MinFilterPlusOpImage extends MinFilterOpImage {

    /**
     * Creates a MinFilterPlusOpImage with the given source and maskSize. The image dimensions are derived from the
     * source image. The tile grid layout, SampleModel, and ColorModel may optionally be specified by an ImageLayout
     * object.
     *
     * @param source a RenderedImage.
     * @param extender a BorderExtender, or null.
     * @param layout an ImageLayout optionally containing the tile grid layout, SampleModel, and ColorModel, or null.
     * @param maskSize the mask size.
     */
    public MinFilterPlusOpImage(
            RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, int maskSize) {
        super(source, extender, config, layout, MinFilterDescriptor.MIN_MASK_PLUS, maskSize);
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

        int minval, val;
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

                    // figure out where the top of the plus starts
                    int imageOffset = srcPixelOffset + srcPixelStride * offset;

                    minval = Integer.MAX_VALUE;

                    for (int u = 0; u < wp; u++) {
                        val = (int) (srcData[imageOffset] & 0xff);
                        imageOffset += srcScanlineStride;
                        minval = (val < minval) ? val : minval;
                    }

                    // figure out where the left side of plus starts
                    imageOffset = srcPixelOffset + srcScanlineStride * offset;

                    for (int v = 0; v < wp; v++) {
                        val = (int) (srcData[imageOffset] & 0xff);
                        imageOffset += srcPixelStride;
                        minval = (val < minval) ? val : minval;
                    }

                    dstData[dstPixelOffset] = (byte) minval;
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

        int minval, val;
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

                    // figure out where the top of the plus starts
                    int imageOffset = srcPixelOffset + srcPixelStride * offset;

                    minval = Integer.MAX_VALUE;

                    for (int u = 0; u < wp; u++) {
                        val = (int) (srcData[imageOffset]);
                        imageOffset += srcScanlineStride;
                        minval = (val < minval) ? val : minval;
                    }

                    // figure out where the left side of plus starts
                    imageOffset = srcPixelOffset + srcScanlineStride * offset;

                    for (int v = 0; v < wp; v++) {
                        val = srcData[imageOffset];
                        imageOffset += srcPixelStride;
                        minval = (val < minval) ? val : minval;
                    }

                    dstData[dstPixelOffset] = (short) minval;
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

        int minval, val;
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

                    // figure out where the top of the plus starts
                    int imageOffset = srcPixelOffset + srcPixelStride * offset;

                    minval = Integer.MAX_VALUE;

                    for (int u = 0; u < wp; u++) {
                        val = (int) (srcData[imageOffset] & 0xffff);
                        imageOffset += srcScanlineStride;
                        minval = (val < minval) ? val : minval;
                    }

                    // figure out where the left side of plus starts
                    imageOffset = srcPixelOffset + srcScanlineStride * offset;

                    for (int v = 0; v < wp; v++) {
                        val = (int) (srcData[imageOffset] & 0xffff);
                        imageOffset += srcPixelStride;
                        minval = (val < minval) ? val : minval;
                    }

                    dstData[dstPixelOffset] = (short) minval;
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

        int minval, val;
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

                    // figure out where the top of the plus starts
                    int imageOffset = srcPixelOffset + srcPixelStride * offset;

                    minval = Integer.MAX_VALUE;

                    for (int u = 0; u < wp; u++) {
                        val = srcData[imageOffset];
                        imageOffset += srcScanlineStride;
                        minval = (val < minval) ? val : minval;
                    }

                    // figure out where the left side of plus starts
                    imageOffset = srcPixelOffset + srcScanlineStride * offset;

                    for (int v = 0; v < wp; v++) {
                        val = srcData[imageOffset];
                        imageOffset += srcPixelStride;
                        minval = (val < minval) ? val : minval;
                    }

                    dstData[dstPixelOffset] = minval;
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

        float minval, val;
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

                    // figure out where the top of the plus starts
                    int imageOffset = srcPixelOffset + srcPixelStride * offset;

                    minval = Float.MAX_VALUE;

                    for (int u = 0; u < wp; u++) {
                        val = srcData[imageOffset];
                        imageOffset += srcScanlineStride;
                        minval = (val < minval) ? val : minval;
                    }

                    // figure out where the left side of plus starts
                    imageOffset = srcPixelOffset + srcScanlineStride * offset;

                    for (int v = 0; v < wp; v++) {
                        val = srcData[imageOffset];
                        imageOffset += srcPixelStride;
                        minval = (val < minval) ? val : minval;
                    }

                    dstData[dstPixelOffset] = minval;
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

        double minval, val;
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

                    // figure out where the top of the plus starts
                    int imageOffset = srcPixelOffset + srcPixelStride * offset;

                    minval = Double.MAX_VALUE;

                    for (int u = 0; u < wp; u++) {
                        val = srcData[imageOffset];
                        imageOffset += srcScanlineStride;
                        minval = (val < minval) ? val : minval;
                    }

                    // figure out where the left side of plus starts
                    imageOffset = srcPixelOffset + srcScanlineStride * offset;

                    for (int v = 0; v < wp; v++) {
                        val = srcData[imageOffset];
                        imageOffset += srcPixelStride;
                        minval = (val < minval) ? val : minval;
                    }

                    dstData[dstPixelOffset] = minval;
                    srcPixelOffset += srcPixelStride;
                    dstPixelOffset += dstPixelStride;
                }
                srcScanlineOffset += srcScanlineStride;
                dstScanlineOffset += dstScanlineStride;
            }
        }
    }

    //     public static OpImage createTestImage(OpImageTester oit) {
    //         return new MinFilterPlusOpImage(oit.getSource(), null, null,
    //                                            new ImageLayout(oit.getSource()),
    //                                            3);
    //     }
}
