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
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.IndexColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Map;
import org.eclipse.imagen.BorderExtender;
import org.eclipse.imagen.ImageLayout;
import org.eclipse.imagen.Interpolation;
import org.eclipse.imagen.PlanarImage;
import org.eclipse.imagen.RasterAccessor;
import org.eclipse.imagen.RasterFormatTag;
import org.eclipse.imagen.Warp;
import org.eclipse.imagen.WarpOpImage;
import org.eclipse.imagen.iterator.RandomIter;
import org.eclipse.imagen.iterator.RandomIterFactory;
import org.eclipse.imagen.media.util.ImageUtil;

/**
 * An <code>OpImage</code> implementing the general "Warp" operation as described in <code>
 * org.eclipse.imagen.operator.WarpDescriptor</code>. It supports all interpolation cases.
 *
 * @since EA2
 * @see org.eclipse.imagen.Warp
 * @see org.eclipse.imagen.WarpOpImage
 * @see org.eclipse.imagen.operator.WarpDescriptor
 * @see WarpRIF
 */
final class WarpGeneralOpImage extends WarpOpImage {

    /** Color table representing source's IndexColorModel. */
    private byte[][] ctable = null;

    /**
     * Constructs a WarpGeneralOpImage.
     *
     * @param source The source image.
     * @param extender A BorderExtender, or null.
     * @param layout The destination image layout.
     * @param warp An object defining the warp algorithm.
     * @param interp An object describing the interpolation method.
     */
    public WarpGeneralOpImage(
            RenderedImage source,
            BorderExtender extender,
            Map config,
            ImageLayout layout,
            Warp warp,
            Interpolation interp,
            double[] backgroundValues) {
        super(source, layout, config, false, extender, interp, warp, backgroundValues);

        /*
         * If the source has IndexColorModel, get the RGB color table.
         * Note, in this case, the source should have an integral data type.
         * And dest always has data type byte.
         */
        ColorModel srcColorModel = source.getColorModel();
        if (srcColorModel instanceof IndexColorModel) {
            IndexColorModel icm = (IndexColorModel) srcColorModel;
            ctable = new byte[3][icm.getMapSize()];
            icm.getReds(ctable[0]);
            icm.getGreens(ctable[1]);
            icm.getBlues(ctable[2]);
        }
    }

    /** Warps a rectangle. */
    protected void computeRect(PlanarImage[] sources, WritableRaster dest, Rectangle destRect) {
        // Retrieve format tags.
        RasterFormatTag[] formatTags = getFormatTags();

        RasterAccessor d = new RasterAccessor(dest, destRect, formatTags[1], getColorModel());

        switch (d.getDataType()) {
            case DataBuffer.TYPE_BYTE:
                computeRectByte(sources[0], d);
                break;
            case DataBuffer.TYPE_USHORT:
                computeRectUShort(sources[0], d);
                break;
            case DataBuffer.TYPE_SHORT:
                computeRectShort(sources[0], d);
                break;
            case DataBuffer.TYPE_INT:
                computeRectInt(sources[0], d);
                break;
            case DataBuffer.TYPE_FLOAT:
                computeRectFloat(sources[0], d);
                break;
            case DataBuffer.TYPE_DOUBLE:
                computeRectDouble(sources[0], d);
                break;
        }

        if (d.isDataCopy()) {
            d.clampDataArrays();
            d.copyDataToRaster();
        }
    }

    private void computeRectByte(PlanarImage src, RasterAccessor dst) {
        int lpad, rpad, tpad, bpad;
        if (interp != null) {
            lpad = interp.getLeftPadding();
            rpad = interp.getRightPadding();
            tpad = interp.getTopPadding();
            bpad = interp.getBottomPadding();
        } else {
            lpad = rpad = tpad = bpad = 0;
        }

        int minX, maxX, minY, maxY;
        RandomIter iter;
        if (extender != null) {
            minX = src.getMinX();
            maxX = src.getMaxX();
            minY = src.getMinY();
            maxY = src.getMaxY();
            Rectangle bounds = new Rectangle(
                    src.getMinX() - lpad,
                    src.getMinY() - tpad,
                    src.getWidth() + lpad + rpad,
                    src.getHeight() + tpad + bpad);
            iter = RandomIterFactory.create(src.getExtendedData(bounds, extender), bounds);
        } else {
            minX = src.getMinX() + lpad;
            maxX = src.getMaxX() - rpad;
            minY = src.getMinY() + tpad;
            maxY = src.getMaxY() - bpad;
            iter = RandomIterFactory.create(src, src.getBounds());
        }

        int kwidth = interp.getWidth();
        int kheight = interp.getHeight();

        int dstWidth = dst.getWidth();
        int dstHeight = dst.getHeight();
        int dstBands = dst.getNumBands();

        int lineStride = dst.getScanlineStride();
        int pixelStride = dst.getPixelStride();
        int[] bandOffsets = dst.getBandOffsets();
        byte[][] data = dst.getByteDataArrays();

        int precH = 1 << interp.getSubsampleBitsH();
        int precV = 1 << interp.getSubsampleBitsV();

        float[] warpData = new float[2 * dstWidth];

        int[][] samples = new int[kheight][kwidth];

        int lineOffset = 0;

        byte[] backgroundByte = new byte[dstBands];
        for (int i = 0; i < dstBands; i++) backgroundByte[i] = (byte) backgroundValues[i];

        if (ctable == null) { // source does not have IndexColorModel
            for (int h = 0; h < dstHeight; h++) {
                int pixelOffset = lineOffset;
                lineOffset += lineStride;

                warp.warpRect(dst.getX(), dst.getY() + h, dstWidth, 1, warpData);

                int count = 0;
                for (int w = 0; w < dstWidth; w++) {
                    float sx = warpData[count++];
                    float sy = warpData[count++];

                    int xint = floor(sx);
                    int yint = floor(sy);
                    int xfrac = (int) ((sx - xint) * precH);
                    int yfrac = (int) ((sy - yint) * precV);

                    if (xint < minX || xint >= maxX || yint < minY || yint >= maxY) {
                        /* Fill with a background color. */
                        if (setBackground) {
                            for (int b = 0; b < dstBands; b++) {
                                data[b][pixelOffset + bandOffsets[b]] = backgroundByte[b];
                            }
                        }
                    } else {
                        xint -= lpad;
                        yint -= tpad;

                        for (int b = 0; b < dstBands; b++) {
                            for (int j = 0; j < kheight; j++) {
                                for (int i = 0; i < kwidth; i++) {
                                    samples[j][i] = iter.getSample(xint + i, yint + j, b) & 0xFF;
                                }
                            }

                            data[b][pixelOffset + bandOffsets[b]] =
                                    ImageUtil.clampByte(interp.interpolate(samples, xfrac, yfrac));
                        }
                    }

                    pixelOffset += pixelStride;
                }
            }
        } else { // source has IndexColorModel
            for (int h = 0; h < dstHeight; h++) {
                int pixelOffset = lineOffset;
                lineOffset += lineStride;

                warp.warpRect(dst.getX(), dst.getY() + h, dstWidth, 1, warpData);

                int count = 0;
                for (int w = 0; w < dstWidth; w++) {
                    float sx = warpData[count++];
                    float sy = warpData[count++];

                    int xint = floor(sx);
                    int yint = floor(sy);
                    int xfrac = (int) ((sx - xint) * precH);
                    int yfrac = (int) ((sy - yint) * precV);

                    if (xint < minX || xint >= maxX || yint < minY || yint >= maxY) {
                        /* Fill with a background color. */
                        if (setBackground) {
                            for (int b = 0; b < dstBands; b++) {
                                data[b][pixelOffset + bandOffsets[b]] = backgroundByte[b];
                            }
                        }
                    } else {
                        xint -= lpad;
                        yint -= tpad;

                        for (int b = 0; b < dstBands; b++) {
                            byte[] t = ctable[b];

                            for (int j = 0; j < kheight; j++) {
                                for (int i = 0; i < kwidth; i++) {
                                    samples[j][i] = t[iter.getSample(xint + i, yint + j, 0) & 0xFF] & 0xFF;
                                }
                            }

                            data[b][pixelOffset + bandOffsets[b]] =
                                    ImageUtil.clampByte(interp.interpolate(samples, xfrac, yfrac));
                        }
                    }

                    pixelOffset += pixelStride;
                }
            }
        }
    }

    private void computeRectUShort(PlanarImage src, RasterAccessor dst) {
        int lpad, rpad, tpad, bpad;
        if (interp != null) {
            lpad = interp.getLeftPadding();
            rpad = interp.getRightPadding();
            tpad = interp.getTopPadding();
            bpad = interp.getBottomPadding();
        } else {
            lpad = rpad = tpad = bpad = 0;
        }

        int minX, maxX, minY, maxY;
        RandomIter iter;
        if (extender != null) {
            minX = src.getMinX();
            maxX = src.getMaxX();
            minY = src.getMinY();
            maxY = src.getMaxY();
            Rectangle bounds = new Rectangle(
                    src.getMinX() - lpad,
                    src.getMinY() - tpad,
                    src.getWidth() + lpad + rpad,
                    src.getHeight() + tpad + bpad);
            iter = RandomIterFactory.create(src.getExtendedData(bounds, extender), bounds);
        } else {
            minX = src.getMinX() + lpad;
            maxX = src.getMaxX() - rpad;
            minY = src.getMinY() + tpad;
            maxY = src.getMaxY() - bpad;
            iter = RandomIterFactory.create(src, src.getBounds());
        }

        int kwidth = interp.getWidth();
        int kheight = interp.getHeight();

        int dstWidth = dst.getWidth();
        int dstHeight = dst.getHeight();
        int dstBands = dst.getNumBands();

        int lineStride = dst.getScanlineStride();
        int pixelStride = dst.getPixelStride();
        int[] bandOffsets = dst.getBandOffsets();
        short[][] data = dst.getShortDataArrays();

        int precH = 1 << interp.getSubsampleBitsH();
        int precV = 1 << interp.getSubsampleBitsV();

        float[] warpData = new float[2 * dstWidth];

        int[][] samples = new int[kheight][kwidth];

        int lineOffset = 0;

        short[] backgroundUShort = new short[dstBands];
        for (int i = 0; i < dstBands; i++) backgroundUShort[i] = (short) backgroundValues[i];

        for (int h = 0; h < dstHeight; h++) {
            int pixelOffset = lineOffset;
            lineOffset += lineStride;

            warp.warpRect(dst.getX(), dst.getY() + h, dstWidth, 1, warpData);

            int count = 0;
            for (int w = 0; w < dstWidth; w++) {
                float sx = warpData[count++];
                float sy = warpData[count++];

                int xint = floor(sx);
                int yint = floor(sy);
                int xfrac = (int) ((sx - xint) * precH);
                int yfrac = (int) ((sy - yint) * precV);

                if (xint < minX || xint >= maxX || yint < minY || yint >= maxY) {
                    /* Fill with a background color. */
                    if (setBackground) {
                        for (int b = 0; b < dstBands; b++) {
                            data[b][pixelOffset + bandOffsets[b]] = backgroundUShort[b];
                        }
                    }
                } else {
                    xint -= lpad;
                    yint -= tpad;

                    for (int b = 0; b < dstBands; b++) {
                        for (int j = 0; j < kheight; j++) {
                            for (int i = 0; i < kwidth; i++) {
                                samples[j][i] = iter.getSample(xint + i, yint + j, b) & 0xFFFF;
                            }
                        }

                        data[b][pixelOffset + bandOffsets[b]] =
                                ImageUtil.clampUShort(interp.interpolate(samples, xfrac, yfrac));
                    }
                }

                pixelOffset += pixelStride;
            }
        }
    }

    private void computeRectShort(PlanarImage src, RasterAccessor dst) {
        int lpad, rpad, tpad, bpad;
        if (interp != null) {
            lpad = interp.getLeftPadding();
            rpad = interp.getRightPadding();
            tpad = interp.getTopPadding();
            bpad = interp.getBottomPadding();
        } else {
            lpad = rpad = tpad = bpad = 0;
        }

        int minX, maxX, minY, maxY;
        RandomIter iter;
        if (extender != null) {
            minX = src.getMinX();
            maxX = src.getMaxX();
            minY = src.getMinY();
            maxY = src.getMaxY();
            Rectangle bounds = new Rectangle(
                    src.getMinX() - lpad,
                    src.getMinY() - tpad,
                    src.getWidth() + lpad + rpad,
                    src.getHeight() + tpad + bpad);
            iter = RandomIterFactory.create(src.getExtendedData(bounds, extender), bounds);
        } else {
            minX = src.getMinX() + lpad;
            maxX = src.getMaxX() - rpad;
            minY = src.getMinY() + tpad;
            maxY = src.getMaxY() - bpad;
            iter = RandomIterFactory.create(src, src.getBounds());
        }

        int kwidth = interp.getWidth();
        int kheight = interp.getHeight();

        int dstWidth = dst.getWidth();
        int dstHeight = dst.getHeight();
        int dstBands = dst.getNumBands();

        int lineStride = dst.getScanlineStride();
        int pixelStride = dst.getPixelStride();
        int[] bandOffsets = dst.getBandOffsets();
        short[][] data = dst.getShortDataArrays();

        int precH = 1 << interp.getSubsampleBitsH();
        int precV = 1 << interp.getSubsampleBitsV();

        float[] warpData = new float[2 * dstWidth];

        int[][] samples = new int[kheight][kwidth];

        int lineOffset = 0;

        short[] backgroundShort = new short[dstBands];
        for (int i = 0; i < dstBands; i++) backgroundShort[i] = (short) backgroundValues[i];

        for (int h = 0; h < dstHeight; h++) {
            int pixelOffset = lineOffset;
            lineOffset += lineStride;

            warp.warpRect(dst.getX(), dst.getY() + h, dstWidth, 1, warpData);

            int count = 0;
            for (int w = 0; w < dstWidth; w++) {
                float sx = warpData[count++];
                float sy = warpData[count++];

                int xint = floor(sx);
                int yint = floor(sy);
                int xfrac = (int) ((sx - xint) * precH);
                int yfrac = (int) ((sy - yint) * precV);

                if (xint < minX || xint >= maxX || yint < minY || yint >= maxY) {
                    /* Fill with a background color. */
                    if (setBackground) {
                        for (int b = 0; b < dstBands; b++) {
                            data[b][pixelOffset + bandOffsets[b]] = backgroundShort[b];
                        }
                    }
                } else {
                    xint -= lpad;
                    yint -= tpad;

                    for (int b = 0; b < dstBands; b++) {
                        for (int j = 0; j < kheight; j++) {
                            for (int i = 0; i < kwidth; i++) {
                                samples[j][i] = iter.getSample(xint + i, yint + j, b);
                            }
                        }

                        data[b][pixelOffset + bandOffsets[b]] =
                                ImageUtil.clampShort(interp.interpolate(samples, xfrac, yfrac));
                    }
                }

                pixelOffset += pixelStride;
            }
        }
    }

    private void computeRectInt(PlanarImage src, RasterAccessor dst) {
        int lpad, rpad, tpad, bpad;
        if (interp != null) {
            lpad = interp.getLeftPadding();
            rpad = interp.getRightPadding();
            tpad = interp.getTopPadding();
            bpad = interp.getBottomPadding();
        } else {
            lpad = rpad = tpad = bpad = 0;
        }

        int minX, maxX, minY, maxY;
        RandomIter iter;
        if (extender != null) {
            minX = src.getMinX();
            maxX = src.getMaxX();
            minY = src.getMinY();
            maxY = src.getMaxY();
            Rectangle bounds = new Rectangle(
                    src.getMinX() - lpad,
                    src.getMinY() - tpad,
                    src.getWidth() + lpad + rpad,
                    src.getHeight() + tpad + bpad);
            iter = RandomIterFactory.create(src.getExtendedData(bounds, extender), bounds);
        } else {
            minX = src.getMinX() + lpad;
            maxX = src.getMaxX() - rpad;
            minY = src.getMinY() + tpad;
            maxY = src.getMaxY() - bpad;
            iter = RandomIterFactory.create(src, src.getBounds());
        }

        int kwidth = interp.getWidth();
        int kheight = interp.getHeight();

        int dstWidth = dst.getWidth();
        int dstHeight = dst.getHeight();
        int dstBands = dst.getNumBands();

        int lineStride = dst.getScanlineStride();
        int pixelStride = dst.getPixelStride();
        int[] bandOffsets = dst.getBandOffsets();
        int[][] data = dst.getIntDataArrays();

        int precH = 1 << interp.getSubsampleBitsH();
        int precV = 1 << interp.getSubsampleBitsV();

        float[] warpData = new float[2 * dstWidth];

        int[][] samples = new int[kheight][kwidth];

        int lineOffset = 0;

        int[] backgroundInt = new int[dstBands];
        for (int i = 0; i < dstBands; i++) backgroundInt[i] = (int) backgroundValues[i];

        for (int h = 0; h < dstHeight; h++) {
            int pixelOffset = lineOffset;
            lineOffset += lineStride;

            warp.warpRect(dst.getX(), dst.getY() + h, dstWidth, 1, warpData);

            int count = 0;
            for (int w = 0; w < dstWidth; w++) {
                float sx = warpData[count++];
                float sy = warpData[count++];

                int xint = floor(sx);
                int yint = floor(sy);
                int xfrac = (int) ((sx - xint) * precH);
                int yfrac = (int) ((sy - yint) * precV);

                if (xint < minX || xint >= maxX || yint < minY || yint >= maxY) {
                    /* Fill with a background color. */
                    if (setBackground) {
                        for (int b = 0; b < dstBands; b++) {
                            data[b][pixelOffset + bandOffsets[b]] = backgroundInt[b];
                        }
                    }
                } else {
                    xint -= lpad;
                    yint -= tpad;

                    for (int b = 0; b < dstBands; b++) {
                        for (int j = 0; j < kheight; j++) {
                            for (int i = 0; i < kwidth; i++) {
                                samples[j][i] = iter.getSample(xint + i, yint + j, b);
                            }
                        }

                        data[b][pixelOffset + bandOffsets[b]] = interp.interpolate(samples, xfrac, yfrac);
                    }
                }

                pixelOffset += pixelStride;
            }
        }
    }

    private void computeRectFloat(PlanarImage src, RasterAccessor dst) {
        int lpad, rpad, tpad, bpad;
        if (interp != null) {
            lpad = interp.getLeftPadding();
            rpad = interp.getRightPadding();
            tpad = interp.getTopPadding();
            bpad = interp.getBottomPadding();
        } else {
            lpad = rpad = tpad = bpad = 0;
        }

        int minX, maxX, minY, maxY;
        RandomIter iter;
        if (extender != null) {
            minX = src.getMinX();
            maxX = src.getMaxX();
            minY = src.getMinY();
            maxY = src.getMaxY();
            Rectangle bounds = new Rectangle(
                    src.getMinX() - lpad,
                    src.getMinY() - tpad,
                    src.getWidth() + lpad + rpad,
                    src.getHeight() + tpad + bpad);
            iter = RandomIterFactory.create(src.getExtendedData(bounds, extender), bounds);
        } else {
            minX = src.getMinX() + lpad;
            maxX = src.getMaxX() - rpad;
            minY = src.getMinY() + tpad;
            maxY = src.getMaxY() - bpad;
            iter = RandomIterFactory.create(src, src.getBounds());
        }

        int kwidth = interp.getWidth();
        int kheight = interp.getHeight();

        int dstWidth = dst.getWidth();
        int dstHeight = dst.getHeight();
        int dstBands = dst.getNumBands();

        int lineStride = dst.getScanlineStride();
        int pixelStride = dst.getPixelStride();
        int[] bandOffsets = dst.getBandOffsets();
        float[][] data = dst.getFloatDataArrays();

        float[] warpData = new float[2 * dstWidth];

        float[][] samples = new float[kheight][kwidth];

        int lineOffset = 0;

        float[] backgroundFloat = new float[dstBands];
        for (int i = 0; i < dstBands; i++) backgroundFloat[i] = (float) backgroundValues[i];

        for (int h = 0; h < dstHeight; h++) {
            int pixelOffset = lineOffset;
            lineOffset += lineStride;

            warp.warpRect(dst.getX(), dst.getY() + h, dstWidth, 1, warpData);

            int count = 0;
            for (int w = 0; w < dstWidth; w++) {
                float sx = warpData[count++];
                float sy = warpData[count++];

                int xint = floor(sx);
                int yint = floor(sy);
                float xfrac = sx - xint;
                float yfrac = sy - yint;

                if (xint < minX || xint >= maxX || yint < minY || yint >= maxY) {
                    /* Fill with a background color. */
                    if (setBackground) {
                        for (int b = 0; b < dstBands; b++) {
                            data[b][pixelOffset + bandOffsets[b]] = backgroundFloat[b];
                        }
                    }
                } else {
                    xint -= lpad;
                    yint -= tpad;

                    for (int b = 0; b < dstBands; b++) {
                        for (int j = 0; j < kheight; j++) {
                            for (int i = 0; i < kwidth; i++) {
                                samples[j][i] = iter.getSampleFloat(xint + i, yint + j, b);
                            }
                        }

                        data[b][pixelOffset + bandOffsets[b]] = interp.interpolate(samples, xfrac, yfrac);
                    }
                }

                pixelOffset += pixelStride;
            }
        }
    }

    private void computeRectDouble(PlanarImage src, RasterAccessor dst) {
        int lpad, rpad, tpad, bpad;
        if (interp != null) {
            lpad = interp.getLeftPadding();
            rpad = interp.getRightPadding();
            tpad = interp.getTopPadding();
            bpad = interp.getBottomPadding();
        } else {
            lpad = rpad = tpad = bpad = 0;
        }

        int minX, maxX, minY, maxY;
        RandomIter iter;
        if (extender != null) {
            minX = src.getMinX();
            maxX = src.getMaxX();
            minY = src.getMinY();
            maxY = src.getMaxY();
            Rectangle bounds = new Rectangle(
                    src.getMinX() - lpad,
                    src.getMinY() - tpad,
                    src.getWidth() + lpad + rpad,
                    src.getHeight() + tpad + bpad);
            iter = RandomIterFactory.create(src.getExtendedData(bounds, extender), bounds);
        } else {
            minX = src.getMinX() + lpad;
            maxX = src.getMaxX() - rpad;
            minY = src.getMinY() + tpad;
            maxY = src.getMaxY() - bpad;
            iter = RandomIterFactory.create(src, src.getBounds());
        }

        int kwidth = interp.getWidth();
        int kheight = interp.getHeight();

        int dstWidth = dst.getWidth();
        int dstHeight = dst.getHeight();
        int dstBands = dst.getNumBands();

        int lineStride = dst.getScanlineStride();
        int pixelStride = dst.getPixelStride();
        int[] bandOffsets = dst.getBandOffsets();
        double[][] data = dst.getDoubleDataArrays();

        float[] warpData = new float[2 * dstWidth];

        double[][] samples = new double[kheight][kwidth];

        int lineOffset = 0;

        for (int h = 0; h < dstHeight; h++) {
            int pixelOffset = lineOffset;
            lineOffset += lineStride;

            warp.warpRect(dst.getX(), dst.getY() + h, dstWidth, 1, warpData);

            int count = 0;
            for (int w = 0; w < dstWidth; w++) {
                float sx = warpData[count++];
                float sy = warpData[count++];

                int xint = floor(sx);
                int yint = floor(sy);
                float xfrac = sx - xint;
                float yfrac = sy - yint;

                if (xint < minX || xint >= maxX || yint < minY || yint >= maxY) {
                    /* Fill with a background color. */
                    if (setBackground) {
                        for (int b = 0; b < dstBands; b++) {
                            data[b][pixelOffset + bandOffsets[b]] = backgroundValues[b];
                        }
                    }
                } else {
                    xint -= lpad;
                    yint -= tpad;

                    for (int b = 0; b < dstBands; b++) {
                        for (int j = 0; j < kheight; j++) {
                            for (int i = 0; i < kwidth; i++) {
                                samples[j][i] = iter.getSampleDouble(xint + i, yint + j, b);
                            }
                        }

                        data[b][pixelOffset + bandOffsets[b]] = interp.interpolate(samples, xfrac, yfrac);
                    }
                }

                pixelOffset += pixelStride;
            }
        }
    }

    /** Returns the "floor" value of a float. */
    private static final int floor(float f) {
        return f >= 0 ? (int) f : (int) f - 1;
    }
}
