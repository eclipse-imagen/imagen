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
import org.eclipse.imagen.InterpolationBicubic;
import org.eclipse.imagen.InterpolationBicubic2;
import org.eclipse.imagen.InterpolationBilinear;
import org.eclipse.imagen.InterpolationNearest;
import org.eclipse.imagen.TileCache;

/**
 * This image factory supports image operator <code>TranslateOpImage</code> in the rendered and renderable image layers.
 *
 * @see TranslateOpImage
 */
public class TranslateCRIF extends CRIFImpl {

    private static final float TOLERANCE = 0.01F;

    /** Constructor. */
    public TranslateCRIF() {
        super("translate");
    }

    /**
     * Creates a new instance of <code>TranslateOpImage</code> in the rendered layer. This method satisfies the
     * implementation of RIF.
     */
    public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints) {
        RenderedImage source = paramBlock.getRenderedSource(0);
        float xTrans = paramBlock.getFloatParameter(0);
        float yTrans = paramBlock.getFloatParameter(1);
        Interpolation interp = (Interpolation) paramBlock.getObjectParameter(2);

        // Get ImageLayout from renderHints if any.
        ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);

        // If there is a layout hint, TranslateIntOpImage can't deal with it
        if ((Math.abs(xTrans - (int) xTrans) < TOLERANCE)
                && (Math.abs(yTrans - (int) yTrans) < TOLERANCE)
                && layout == null) {
            return new TranslateIntOpImage(source, renderHints, (int) xTrans, (int) yTrans);
        } else {

            // Get TileCache from renderHints if any.
            TileCache cache = RIFUtil.getTileCacheHint(renderHints);

            // Get BorderExtender from renderHints if any.
            BorderExtender extender = RIFUtil.getBorderExtenderHint(renderHints);

            //
            // Call the Scale operation, since it encapsulates Translate
            // and is better optimized than Affine.
            //
            float xScale = 1.0F;
            float yScale = 1.0F;
            SampleModel sm = source.getSampleModel();
            boolean isBinary = (sm instanceof MultiPixelPackedSampleModel)
                    && (sm.getSampleSize(0) == 1)
                    && (sm.getDataType() == DataBuffer.TYPE_BYTE
                            || sm.getDataType() == DataBuffer.TYPE_USHORT
                            || sm.getDataType() == DataBuffer.TYPE_INT);

            if (interp instanceof InterpolationNearest) {
                if (isBinary) {
                    return new ScaleNearestBinaryOpImage(
                            source, extender, renderHints, layout, xScale, yScale, xTrans, yTrans, interp);
                } else {
                    return new ScaleNearestOpImage(
                            source, extender, renderHints, layout, xScale, yScale, xTrans, yTrans, interp);
                }
            } else if (interp instanceof InterpolationBilinear) {
                if (isBinary) {
                    return new ScaleBilinearBinaryOpImage(
                            source, extender, renderHints, layout, xScale, yScale, xTrans, yTrans, interp);
                } else {
                    return new ScaleBilinearOpImage(
                            source, extender, renderHints, layout, xScale, yScale, xTrans, yTrans, interp);
                }
            } else if ((interp instanceof InterpolationBicubic) || (interp instanceof InterpolationBicubic2)) {
                return new ScaleBicubicOpImage(
                        source, extender, renderHints, layout, xScale, yScale, xTrans, yTrans, interp);
            } else {
                return new ScaleGeneralOpImage(
                        source, extender, renderHints, layout, xScale, yScale, xTrans, yTrans, interp);
            }
        }
    }

    /**
     * Creates a new instance of <code>TranslateOpImage</code> in the renderable layer. This method satisfies the
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
        AffineTransform translate = new AffineTransform();
        translate.setToTranslation(paramBlock.getFloatParameter(0), paramBlock.getFloatParameter(1));

        RenderContext RC = (RenderContext) renderContext.clone();
        AffineTransform usr2dev = RC.getTransform();
        usr2dev.concatenate(translate);
        RC.setTransform(usr2dev);
        return RC;
    }

    /**
     * Gets the bounding box for output of <code>TranslateOpImage</code>. This method satisfies the implementation of
     * CRIF.
     */
    public Rectangle2D getBounds2D(ParameterBlock paramBlock) {
        RenderableImage source = paramBlock.getRenderableSource(0);
        float xTrans = paramBlock.getFloatParameter(0);
        float yTrans = paramBlock.getFloatParameter(1);

        return new Rectangle2D.Float(
                source.getMinX() + xTrans, source.getMinY() + yTrans, source.getWidth(), source.getHeight());
    }
}
