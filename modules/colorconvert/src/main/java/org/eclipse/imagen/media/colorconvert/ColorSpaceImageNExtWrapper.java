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
package org.eclipse.imagen.media.colorconvert;

import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import org.eclipse.imagen.ColorSpaceImageN;
import org.eclipse.imagen.ROI;
import org.eclipse.imagen.media.range.Range;

/**
 * This class is used for wrapping {@link ColorSpaceImageN} instances into {@link ColorSpaceImageNExt} ones.
 *
 * @author Nicola Lagomarsini geosolutions
 */
public class ColorSpaceImageNExtWrapper extends ColorSpaceImageNExt {

    /** Input ColorSpace Provided */
    private ColorSpaceImageN csImageN;

    /** Input Colorspace ColorSpaceImageNExt used if the input colorspace is ColorSpaceImageNExt */
    private ColorSpaceImageNExt csImageNExt;

    /** Boolean used for checking if the input ColorSpace is a {@link ColorSpaceImageNExt} instance */
    boolean isJAIExt = false;

    protected ColorSpaceImageNExtWrapper(ColorSpaceImageN cs) {
        super(cs.getType(), cs.getNumComponents(), cs.isRGBPreferredIntermediary());
        this.csImageN = cs;
        if (cs instanceof ColorSpaceImageNExt) {
            isJAIExt = true;
            csImageNExt = (ColorSpaceImageNExt) cs;
        }
    }

    @Override
    public WritableRaster fromCIEXYZ(
            Raster src,
            int[] srcComponentSize,
            WritableRaster dest,
            int[] dstComponentSize,
            ROI roi,
            Range nodata,
            float[] destNodata) {
        if (isJAIExt) {
            return csImageNExt.fromCIEXYZ(src, srcComponentSize, dest, dstComponentSize, roi, nodata, destNodata);
        }
        return csImageN.fromCIEXYZ(src, srcComponentSize, dest, dstComponentSize);
    }

    @Override
    public WritableRaster fromRGB(
            Raster src,
            int[] srcComponentSize,
            WritableRaster dest,
            Rectangle destRect,
            int[] dstComponentSize,
            ROI roi,
            Range nodata,
            float[] destNodata) {
        if (isJAIExt) {
            return csImageNExt.fromRGB(
                    src, srcComponentSize, dest, destRect, dstComponentSize, roi, nodata, destNodata);
        }
        return csImageN.fromRGB(src, srcComponentSize, dest, dstComponentSize);
    }

    @Override
    public WritableRaster toCIEXYZ(
            Raster src,
            int[] srcComponentSize,
            WritableRaster dest,
            int[] dstComponentSize,
            ROI roi,
            Range nodata,
            float[] destNodata) {
        if (isJAIExt) {
            return csImageNExt.toCIEXYZ(src, srcComponentSize, dest, dstComponentSize, roi, nodata, destNodata);
        }
        return csImageN.toCIEXYZ(src, srcComponentSize, dest, dstComponentSize);
    }

    @Override
    public WritableRaster toRGB(
            Raster src,
            int[] srcComponentSize,
            WritableRaster dest,
            Rectangle destRect,
            int[] dstComponentSize,
            ROI roi,
            Range nodata,
            float[] destNodata) {
        if (isJAIExt) {
            return csImageNExt.toRGB(src, srcComponentSize, dest, destRect, dstComponentSize, roi, nodata, destNodata);
        }
        return csImageN.toRGB(src, srcComponentSize, dest, dstComponentSize);
    }

    @Override
    public float[] fromCIEXYZ(float[] src) {
        return csImageN.fromCIEXYZ(src);
    }

    @Override
    public float[] fromRGB(float[] src) {
        return csImageN.fromRGB(src);
    }

    @Override
    public float[] toCIEXYZ(float[] src) {
        return csImageN.toCIEXYZ(src);
    }

    @Override
    public float[] toRGB(float[] src) {
        return csImageN.toRGB(src);
    }
}
