/* JAI-Ext - OpenSource Java Advanced Image Extensions Library
 *    http://www.geo-solutions.it/
 *    Copyright 2018 GeoSolutions
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
package org.eclipse.imagen.media.scale;

import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.DataBuffer;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderContext;
import java.awt.image.renderable.RenderableImage;
import org.eclipse.imagen.BorderExtender;
import org.eclipse.imagen.CRIFImpl;
import org.eclipse.imagen.ImageLayout;
import org.eclipse.imagen.Interpolation;
import org.eclipse.imagen.PlanarImage;
import org.eclipse.imagen.ROI;
import org.eclipse.imagen.media.interpolators.InterpolationBicubic;
import org.eclipse.imagen.media.interpolators.InterpolationBilinear;
import org.eclipse.imagen.media.interpolators.InterpolationNearest;
import org.eclipse.imagen.media.opimage.CopyOpImage;
import org.eclipse.imagen.media.opimage.RIFUtil;
import org.eclipse.imagen.media.range.Range;
import org.eclipse.imagen.media.range.RangeFactory;
import org.eclipse.imagen.media.translate.TranslateIntOpImage;

public class Scale2CRIF extends CRIFImpl {

    static final float TOLERANCE = 0.01F;

    /** Constructor. */
    public Scale2CRIF() {
        super("Scale2");
    }

    /**
     * Creates a new instance of ScaleOpImage in the rendered layer. This method satisfies the implementation of RIF.
     *
     * @param paramBlock The source image, the X and Y scale factor, and the interpolation method for resampling.
     */
    public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints) {

        // Get ImageLayout from renderHints if any.
        ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);

        // Get BorderExtender from renderHints if any.
        BorderExtender extender = RIFUtil.getBorderExtenderHint(renderHints);
        RenderedImage source = paramBlock.getRenderedSource(0);

        double xScale = paramBlock.getDoubleParameter(0);
        double yScale = paramBlock.getDoubleParameter(1);
        double xTrans = paramBlock.getDoubleParameter(2);
        double yTrans = paramBlock.getDoubleParameter(3);

        Interpolation interp = (Interpolation) paramBlock.getObjectParameter(4);
        // Get the Nodata Range
        Range nodata = (Range) paramBlock.getObjectParameter(7);
        nodata = RangeFactory.convert(nodata, source.getSampleModel().getDataType());

        // Get the backgroundValues
        double[] backgroundValues = (double[]) paramBlock.getObjectParameter(8);
        // SG make sure we use the ROI
        Object property = paramBlock.getObjectParameter(5);
        ROI roi = null;
        boolean useRoiAccessor = false;
        if (property instanceof ROI) {
            roi = (ROI) property;

            PlanarImage temp = PlanarImage.wrapRenderedImage(source);
            temp.setProperty("ROI", roi);
            source = temp;
            useRoiAccessor = (Boolean) paramBlock.getObjectParameter(6);
        }

        Rectangle sourceBounds =
                new Rectangle(source.getMinX(), source.getMinY(), source.getWidth(), source.getHeight());

        // Check and see if we are scaling by 1.0 in both x and y and no
        // translations. If so call the copy operation. This operation can
        // be executed only if ROI data are not defined or contain all the
        // image or are empty

        if (xScale == 1.0D
                && yScale == 1.0D
                && xTrans == 0.0D
                && yTrans == 0.0D
                && (roi == null || (roi.getBounds().isEmpty() || roi.contains(sourceBounds)))) {
            return new CopyOpImage(source, renderHints, layout);
        }

        // Check to see whether the operation specified is a pure
        // integer translation. If so call translate
        // If the hints contain an ImageLayout hint, then we can't use
        // TranslateIntOpImage since that can't deal with the ImageLayout hint.
        // This operation can be executed only if ROI data are not defined or
        // contain all the image or are empty
        if (xScale == 1.0D
                && yScale == 1.0D
                && (Math.abs(xTrans - (int) xTrans) < TOLERANCE)
                && (Math.abs(yTrans - (int) yTrans) < TOLERANCE)
                && layout == null
                && (roi == null || (roi.getBounds().isEmpty() || roi.contains(sourceBounds)))) {
            // It's an integer translate.
            return new TranslateIntOpImage(source, renderHints, (int) xTrans, (int) yTrans);
        }

        SampleModel sm = source.getSampleModel();

        boolean isBinary = (sm instanceof MultiPixelPackedSampleModel)
                && (sm.getSampleSize(0) == 1)
                && (sm.getDataType() == DataBuffer.TYPE_BYTE
                        || sm.getDataType() == DataBuffer.TYPE_USHORT
                        || sm.getDataType() == DataBuffer.TYPE_INT);

        // Check which kind of interpolation we are using
        boolean nearestInterp =
                interp instanceof InterpolationNearest || interp instanceof org.eclipse.imagen.InterpolationNearest;
        boolean bilinearInterp =
                interp instanceof InterpolationBilinear || interp instanceof org.eclipse.imagen.InterpolationBilinear;
        boolean bicubicInterp = interp instanceof InterpolationBicubic
                || interp instanceof org.eclipse.imagen.InterpolationBicubic
                || interp instanceof org.eclipse.imagen.InterpolationBicubic2;

        // Transformation of the interpolators ImageN-->ImageN-EXT
        int dataType = source.getSampleModel().getDataType();
        double destinationNoData = (backgroundValues != null && backgroundValues.length > 0)
                ? backgroundValues[0]
                : nodata != null ? nodata.getMin().doubleValue() : 0;
        if (interp instanceof org.eclipse.imagen.InterpolationNearest) {
            interp = new InterpolationNearest(nodata, useRoiAccessor, destinationNoData, dataType);
        } else if (interp instanceof org.eclipse.imagen.InterpolationBilinear) {
            interp = new InterpolationBilinear(
                    interp.getSubsampleBitsH(), nodata, useRoiAccessor, destinationNoData, dataType);
        } else if (interp instanceof org.eclipse.imagen.InterpolationBicubic) {
            org.eclipse.imagen.InterpolationBicubic bic = (org.eclipse.imagen.InterpolationBicubic) interp;
            interp = new InterpolationBicubic(
                    bic.getSubsampleBitsH(),
                    nodata,
                    useRoiAccessor,
                    destinationNoData,
                    dataType,
                    true,
                    bic.getPrecisionBits());
        } else if (interp instanceof org.eclipse.imagen.InterpolationBicubic2) {
            org.eclipse.imagen.InterpolationBicubic2 bic = (org.eclipse.imagen.InterpolationBicubic2) interp;
            interp = new InterpolationBicubic(
                    bic.getSubsampleBitsH(),
                    nodata,
                    useRoiAccessor,
                    destinationNoData,
                    dataType,
                    false,
                    bic.getPrecisionBits());
        }

        if (nearestInterp && isBinary) {
            return new Scale2GeneralOpImage(
                    source,
                    layout,
                    renderHints,
                    extender,
                    interp,
                    xScale,
                    yScale,
                    xTrans,
                    yTrans,
                    useRoiAccessor,
                    nodata,
                    backgroundValues);
        } else if (nearestInterp && !isBinary) {
            return new Scale2NearestOpImage(
                    source,
                    layout,
                    renderHints,
                    extender,
                    interp,
                    xScale,
                    yScale,
                    xTrans,
                    yTrans,
                    useRoiAccessor,
                    nodata,
                    backgroundValues);
        } else if (bilinearInterp && !isBinary) {
            return new Scale2BilinearOpImage(
                    source,
                    layout,
                    renderHints,
                    extender,
                    interp,
                    xScale,
                    yScale,
                    xTrans,
                    yTrans,
                    useRoiAccessor,
                    nodata,
                    backgroundValues);
        } else if (bilinearInterp && isBinary) {
            return new Scale2GeneralOpImage(
                    source,
                    layout,
                    renderHints,
                    extender,
                    interp,
                    xScale,
                    yScale,
                    xTrans,
                    yTrans,
                    useRoiAccessor,
                    nodata,
                    backgroundValues);
        } else if (bicubicInterp && !isBinary) {
            return new Scale2BicubicOpImage(
                    source,
                    layout,
                    renderHints,
                    extender,
                    interp,
                    xScale,
                    yScale,
                    xTrans,
                    yTrans,
                    useRoiAccessor,
                    nodata,
                    backgroundValues);
        } else if (bicubicInterp && isBinary) {
            return new Scale2GeneralOpImage(
                    source,
                    layout,
                    renderHints,
                    extender,
                    interp,
                    xScale,
                    yScale,
                    xTrans,
                    yTrans,
                    useRoiAccessor,
                    nodata,
                    backgroundValues);
        } else {
            return new Scale2GeneralOpImage(
                    source,
                    layout,
                    renderHints,
                    extender,
                    interp,
                    xScale,
                    yScale,
                    xTrans,
                    yTrans,
                    useRoiAccessor,
                    nodata,
                    backgroundValues);
        }
    }

    /**
     * Creates a new instance of <code>AffineOpImage</code> in the renderable layer. This method satisfies the
     * implementation of CRIF.
     */
    public RenderedImage create(RenderContext renderContext, ParameterBlock paramBlock) {
        return paramBlock.getRenderedSource(0);
    }

    /**
     * Maps the output RenderContext into the RenderContext for the ith source. This method satisfies the implementation
     * of CRIF.
     *
     * @param i The index of the source image.
     * @param renderContext The renderContext being applied to the operation.
     * @param paramBlock The ParameterBlock containing the sources and the translation factors.
     * @param image The RenderableImageOp from which this method was called.
     */
    public RenderContext mapRenderContext(
            int i, RenderContext renderContext, ParameterBlock paramBlock, RenderableImage image) {

        float scale_x = paramBlock.getFloatParameter(0);
        float scale_y = paramBlock.getFloatParameter(1);
        float trans_x = paramBlock.getFloatParameter(2);
        float trans_y = paramBlock.getFloatParameter(3);

        AffineTransform scale = new AffineTransform(scale_x, 0.0, 0.0, scale_y, trans_x, trans_y);

        RenderContext rc = (RenderContext) renderContext.clone();
        AffineTransform usr2dev = rc.getTransform();
        usr2dev.concatenate(scale);
        rc.setTransform(usr2dev);
        return rc;
    }

    /**
     * Gets the bounding box for the output of <code>ScaleOpImage</code>. This method satisfies the implementation of
     * CRIF.
     */
    public Rectangle2D getBounds2D(ParameterBlock paramBlock) {

        RenderableImage source = paramBlock.getRenderableSource(0);

        float scale_x = paramBlock.getFloatParameter(0);
        float scale_y = paramBlock.getFloatParameter(1);
        float trans_x = paramBlock.getFloatParameter(2);
        float trans_y = paramBlock.getFloatParameter(3);

        // Get the source dimensions
        float x0 = source.getMinX();
        float y0 = source.getMinY();
        float w = source.getWidth();
        float h = source.getHeight();

        // Forward map the source using x0, y0, w and h
        float d_x0 = x0 * scale_x + trans_x;
        float d_y0 = y0 * scale_y + trans_y;
        float d_w = w * scale_x;
        float d_h = h * scale_y;

        return new Rectangle2D.Float(d_x0, d_y0, d_w, d_h);
    }
}
