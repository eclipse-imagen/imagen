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
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import org.eclipse.imagen.ColorCube;
import org.eclipse.imagen.ImageLayout;
import org.eclipse.imagen.KernelImageN;

/**
 * A <code>RIF</code> supporting the "OrderedDither" operation in the rendered image layer.
 *
 * @since EA3
 * @see org.eclipse.imagen.operator.OrderedDitherDescriptor
 */
public class OrderedDitherRIF implements RenderedImageFactory {

    /** Constructor. */
    public OrderedDitherRIF() {}

    /**
     * Creates a new instance of an ordered dither operator according to the color map and dither mask kernel array.
     *
     * @param paramBlock The color map and dither mask kernel array objects.
     */
    public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints) {
        // Get ImageLayout from renderHints if any.
        ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);

        RenderedImage source = paramBlock.getRenderedSource(0);
        ColorCube colorMap = (ColorCube) paramBlock.getObjectParameter(0);
        KernelImageN[] ditherMask = (KernelImageN[]) paramBlock.getObjectParameter(1);

        return new OrderedDitherOpImage(source, renderHints, layout, colorMap, ditherMask);
    }
}
