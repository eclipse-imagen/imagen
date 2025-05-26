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
import org.eclipse.imagen.AreaOpImage;
import org.eclipse.imagen.BorderExtender;
import org.eclipse.imagen.ImageLayout;
import org.eclipse.imagen.RasterAccessor;
import org.eclipse.imagen.RasterFormatTag;
import org.eclipse.imagen.operator.MedianFilterShape;
// import org.eclipse.imagen.media.test.OpImageTester;

/** An abstract OpImage class that subclasses will use to perform MedianFiltering with specific masks. */
abstract class MedianFilterOpImage extends AreaOpImage {

    protected MedianFilterShape maskType;
    protected int maskSize;

    /**
     * Creates a MedianFilterOpImage given an image source, an optional BorderExtender, a maskType and maskSize. The
     * image dimensions are derived the source image. The tile grid layout, SampleModel, and ColorModel may optionally
     * be specified by an ImageLayout object.
     *
     * @param source a RenderedImage.
     * @param extender a BorderExtender, or null.
     * @param layout an ImageLayout optionally containing the tile grid layout, SampleModel, and ColorModel, or null.
     * @param maskType the filter mask type.
     * @param maskSize the filter mask size.
     */
    public MedianFilterOpImage(
            RenderedImage source,
            BorderExtender extender,
            Map config,
            ImageLayout layout,
            MedianFilterShape maskType,
            int maskSize) {
        super(
                source,
                layout,
                config,
                true,
                extender,
                (maskSize - 1) / 2,
                (maskSize - 1) / 2,
                (maskSize / 2),
                (maskSize / 2));
        this.maskType = maskType;
        this.maskSize = maskSize;
    }

    /**
     * Performs median filtering on a specified rectangle. The sources are cobbled.
     *
     * @param sources an array of source Rasters, guaranteed to provide all necessary source data for computing the
     *     output.
     * @param dest a WritableRaster tile containing the area to be computed.
     * @param destRect the rectangle within dest to be processed.
     */
    protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        // Retrieve format tags.
        RasterFormatTag[] formatTags = getFormatTags();

        Raster source = sources[0];
        Rectangle srcRect = mapDestRect(destRect, 0);

        RasterAccessor srcAccessor =
                new RasterAccessor(source, srcRect, formatTags[0], getSource(0).getColorModel());
        RasterAccessor dstAccessor = new RasterAccessor(dest, destRect, formatTags[1], getColorModel());

        switch (dstAccessor.getDataType()) {
            case DataBuffer.TYPE_BYTE:
                byteLoop(srcAccessor, dstAccessor, maskSize);
                break;
            case DataBuffer.TYPE_SHORT:
                shortLoop(srcAccessor, dstAccessor, maskSize);
                break;
            case DataBuffer.TYPE_USHORT:
                ushortLoop(srcAccessor, dstAccessor, maskSize);
                break;
            case DataBuffer.TYPE_INT:
                intLoop(srcAccessor, dstAccessor, maskSize);
                break;
            case DataBuffer.TYPE_FLOAT:
                floatLoop(srcAccessor, dstAccessor, maskSize);
                break;
            case DataBuffer.TYPE_DOUBLE:
                doubleLoop(srcAccessor, dstAccessor, maskSize);
                break;
        }

        // If the RasterAccessor object set up a temporary buffer for the
        // op to write to, tell the RasterAccessor to write that data
        // to the raster no that we're done with it.
        if (dstAccessor.isDataCopy()) {
            dstAccessor.clampDataArrays();
            dstAccessor.copyDataToRaster();
        }
    }

    /** Performs median filtering using the subclass's mask on byte data */
    protected abstract void byteLoop(RasterAccessor src, RasterAccessor dst, int filterSize);

    /** Performs median filtering using the subclass's mask on short data */
    protected abstract void shortLoop(RasterAccessor src, RasterAccessor dst, int filterSize);

    /** Performs median filtering using the subclass's mask on ushort data */
    protected abstract void ushortLoop(RasterAccessor src, RasterAccessor dst, int filterSize);

    /** Performs median filtering using the subclass's mask on int data */
    protected abstract void intLoop(RasterAccessor src, RasterAccessor dst, int filterSize);

    /** Performs median filtering using the subclass's mask on float data */
    protected abstract void floatLoop(RasterAccessor src, RasterAccessor dst, int filterSize);

    /** Performs median filtering using the subclass's mask on double data */
    protected abstract void doubleLoop(RasterAccessor src, RasterAccessor dst, int filterSize);

    /** Returns the median of the input integer array */
    protected int medianFilter(int data[]) {
        if (data.length == 3) {
            int a = data[0];
            int b = data[1];
            int c = data[2];
            if (a < b) {
                if (b < c) {
                    return b;
                } else {
                    if (c > a) {
                        return c;
                    } else {
                        return a;
                    }
                }
            } else {
                if (a < c) {
                    return a;
                } else {
                    if (b < c) {
                        return c;
                    } else {
                        return b;
                    }
                }
            }
        }
        int left = 0;
        int right = data.length - 1;
        int target = data.length / 2;

        while (true) {
            int oleft = left;
            int oright = right;
            int mid = data[(left + right) / 2];
            do {
                while (data[left] < mid) {
                    left++;
                }
                while (mid < data[right]) {
                    right--;
                }
                if (left <= right) {
                    int tmp = data[left];
                    data[left] = data[right];
                    data[right] = tmp;
                    left++;
                    right--;
                }
            } while (left <= right);
            if (oleft < right && right >= target) {
                left = oleft;
            } else if (left < oright && left <= target) {
                right = oright;
            } else {
                return data[target];
            }
        }
    }

    /** Returns the median of the input float array */
    protected float medianFilterFloat(float data[]) {
        if (data.length == 3) {
            float a = data[0];
            float b = data[1];
            float c = data[2];
            if (a < b) {
                if (b < c) {
                    return b;
                } else {
                    if (c > a) {
                        return c;
                    } else {
                        return a;
                    }
                }
            } else {
                if (a < c) {
                    return a;
                } else {
                    if (b < c) {
                        return c;
                    } else {
                        return b;
                    }
                }
            }
        }
        int left = 0;
        int right = data.length - 1;
        int target = data.length / 2;

        while (true) {
            int oleft = left;
            int oright = right;
            float mid = data[(left + right) / 2];
            do {
                while (data[left] < mid) {
                    left++;
                }
                while (mid < data[right]) {
                    right--;
                }
                if (left <= right) {
                    float tmp = data[left];
                    data[left] = data[right];
                    data[right] = tmp;
                    left++;
                    right--;
                }
            } while (left <= right);
            if (oleft < right && right >= target) {
                left = oleft;
            } else if (left < oright && left <= target) {
                right = oright;
            } else {
                return data[target];
            }
        }
    }

    /** Returns the median of the input double array */
    protected double medianFilterDouble(double data[]) {
        if (data.length == 3) {
            double a = data[0];
            double b = data[1];
            double c = data[2];
            if (a < b) {
                if (b < c) {
                    return b;
                } else {
                    if (c > a) {
                        return c;
                    } else {
                        return a;
                    }
                }
            } else {
                if (a < c) {
                    return a;
                } else {
                    if (b < c) {
                        return c;
                    } else {
                        return b;
                    }
                }
            }
        }
        int left = 0;
        int right = data.length - 1;
        int target = data.length / 2;

        while (true) {
            int oleft = left;
            int oright = right;
            double mid = data[(left + right) / 2];
            do {
                while (data[left] < mid) {
                    left++;
                }
                while (mid < data[right]) {
                    right--;
                }
                if (left <= right) {
                    double tmp = data[left];
                    data[left] = data[right];
                    data[right] = tmp;
                    left++;
                    right--;
                }
            } while (left <= right);
            if (oleft < right && right >= target) {
                left = oleft;
            } else if (left < oright && left <= target) {
                right = oright;
            } else {
                return data[target];
            }
        }
    }

    //     // Calls a method on OpImage that uses introspection, to make this
    //     // class, discover it's createTestImage() call, call it and then
    //     // benchmark the performance of the created OpImage chain.
    //     public static void main(String args[]) {
    //         String classname =
    //                "org.eclipse.imagen.media.opimage.MedianFilterSquareOpImage";
    //         OpImageTester.performDiagnostics(classname,args);
    //         classname =
    //                "org.eclipse.imagen.media.opimage.MedianFilterXOpImage";
    //         OpImageTester.performDiagnostics(classname,args);
    //         classname =
    //                "org.eclipse.imagen.media.opimage.MedianFilterPlusOpImage";
    //         OpImageTester.performDiagnostics(classname,args);
    //         classname =
    //                "org.eclipse.imagen.media.opimage.MedianFilterSeparableOpImage";
    //         OpImageTester.performDiagnostics(classname,args);
    //     }
}
