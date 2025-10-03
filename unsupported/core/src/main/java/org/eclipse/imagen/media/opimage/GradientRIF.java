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
import org.eclipse.imagen.KernelImageN;

/** @see GradientOpImage */
public class GradientRIF implements RenderedImageFactory {

    /** Constructor. */
    public GradientRIF() {}

    /**
     * Create a new instance of GradientOpImage in the rendered layer. This method satisfies the implementation of RIF.
     *
     * @param paramBlock The source image and the gradient's horizontal kernel & vertical kernel.
     */
    public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints) {
        // Get ImageLayout from renderHints if any.
        ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);

        // Get BorderExtender from renderHints if any.
        BorderExtender extender = RIFUtil.getBorderExtenderHint(renderHints);

        RenderedImage source = paramBlock.getRenderedSource(0);

        // Get the Horizontal & Vertical kernels
        KernelImageN kern_h = (KernelImageN) paramBlock.getObjectParameter(0);
        KernelImageN kern_v = (KernelImageN) paramBlock.getObjectParameter(1);

        return new GradientOpImage(source, extender, renderHints, layout, kern_h, kern_v);
    }
}
