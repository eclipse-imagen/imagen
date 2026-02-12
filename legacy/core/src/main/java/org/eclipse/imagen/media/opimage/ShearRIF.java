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

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.DataBuffer;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import org.eclipse.imagen.*;
import org.eclipse.imagen.operator.ShearDescriptor;

/** @see AffineOpimage */
public class ShearRIF implements RenderedImageFactory {

    /** Constructor. */
    public ShearRIF() {}

    /** Creates an shear operation as an instance of AffineOpImage. */
    public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints) {
        // Get ImageLayout from renderHints if any.
        ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);

        // Get BorderExtender from renderHints if any.
        BorderExtender extender = RIFUtil.getBorderExtenderHint(renderHints);

        RenderedImage source = paramBlock.getRenderedSource(0);

        float shear_amt = paramBlock.getFloatParameter(0);
        EnumeratedParameter shear_dir = (EnumeratedParameter) paramBlock.getObjectParameter(1);

        float xTrans = paramBlock.getFloatParameter(2);
        float yTrans = paramBlock.getFloatParameter(3);

        Object arg1 = paramBlock.getObjectParameter(4);
        Interpolation interp = (Interpolation) arg1;

        double[] backgroundValues = (double[]) paramBlock.getObjectParameter(5);

        // Create the affine transform
        AffineTransform tr = new AffineTransform();

        if (shear_dir.equals(ShearDescriptor.SHEAR_HORIZONTAL)) {
            // SHEAR_HORIZONTAL
            tr.setTransform(1.0, 0.0, shear_amt, 1.0, xTrans, 0.0);
        } else {
            // SHEAR_VERTICAL
            tr.setTransform(1.0, shear_amt, 0.0, 1.0, 0.0, yTrans);
        }

        // Do Affine
        if (interp instanceof InterpolationNearest) {
            SampleModel sm = source.getSampleModel();
            boolean isBinary = (sm instanceof MultiPixelPackedSampleModel)
                    && (sm.getSampleSize(0) == 1)
                    && (sm.getDataType() == DataBuffer.TYPE_BYTE
                            || sm.getDataType() == DataBuffer.TYPE_USHORT
                            || sm.getDataType() == DataBuffer.TYPE_INT);
            if (isBinary) {
                return new AffineNearestBinaryOpImage(
                        source, extender, renderHints, layout, tr, interp, backgroundValues);
            } else {
                return new AffineNearestOpImage(source, extender, renderHints, layout, tr, interp, backgroundValues);
            }
        } else if (interp instanceof InterpolationBilinear) {
            return new AffineBilinearOpImage(source, extender, renderHints, layout, tr, interp, backgroundValues);
        } else if (interp instanceof InterpolationBicubic) {
            return new AffineBicubicOpImage(source, extender, renderHints, layout, tr, interp, backgroundValues);
        } else if (interp instanceof InterpolationBicubic2) {
            return new AffineBicubic2OpImage(source, extender, renderHints, layout, tr, interp, backgroundValues);
        } else {
            return new AffineGeneralOpImage(source, extender, renderHints, layout, tr, interp, backgroundValues);
        }
    }
}
