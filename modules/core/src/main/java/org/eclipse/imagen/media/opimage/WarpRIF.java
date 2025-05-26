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

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import org.eclipse.imagen.BorderExtender;
import org.eclipse.imagen.ImageLayout;
import org.eclipse.imagen.Interpolation;
import org.eclipse.imagen.InterpolationBilinear;
import org.eclipse.imagen.InterpolationNearest;
import org.eclipse.imagen.Warp;

/**
 * A <code>RIF</code> supporting the "Warp" operation in the rendered image layer.
 *
 * @since EA2
 * @see org.eclipse.imagen.operator.WarpDescriptor
 * @see GeneralWarpOpImage
 */
public class WarpRIF implements RenderedImageFactory {

    /** Constructor. */
    public WarpRIF() {}

    /**
     * Creates a new instance of warp operator according to the warp object and interpolation method.
     *
     * @param paramBlock The warp and interpolation objects.
     */
    public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints) {
        // Get ImageLayout from renderHints if any.
        ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);

        // Get BorderExtender from renderHints if any.
        BorderExtender extender = RIFUtil.getBorderExtenderHint(renderHints);

        RenderedImage source = paramBlock.getRenderedSource(0);
        Warp warp = (Warp) paramBlock.getObjectParameter(0);
        Interpolation interp = (Interpolation) paramBlock.getObjectParameter(1);

        double[] backgroundValues = (double[]) paramBlock.getObjectParameter(2);

        if (interp instanceof InterpolationNearest) {
            return new WarpNearestOpImage(source, renderHints, layout, warp, interp, backgroundValues);
        } else if (interp instanceof InterpolationBilinear) {
            return new WarpBilinearOpImage(source, extender, renderHints, layout, warp, interp, backgroundValues);
        } else {
            return new WarpGeneralOpImage(source, extender, renderHints, layout, warp, interp, backgroundValues);
        }
    }
}
