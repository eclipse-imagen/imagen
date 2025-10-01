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
import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import org.eclipse.imagen.BorderExtender;
import org.eclipse.imagen.ImageLayout;
import org.eclipse.imagen.KernelImageN;

/** @see ConvolveOpImage */
public class ConvolveRIF implements RenderedImageFactory {

    /** Constructor. */
    public ConvolveRIF() {}

    /**
     * Create a new instance of ConvolveOpImage in the rendered layer. This method satisfies the implementation of RIF.
     *
     * @param paramBlock The source image and the convolution kernel.
     */
    public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints) {
        // Get ImageLayout from renderHints if any.
        ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);

        // Get BorderExtender from renderHints if any.
        BorderExtender extender = RIFUtil.getBorderExtenderHint(renderHints);

        KernelImageN unRotatedKernel = (KernelImageN) paramBlock.getObjectParameter(0);
        KernelImageN kJAI = unRotatedKernel.getRotatedKernel();

        int dataType = paramBlock.getRenderedSource(0).getSampleModel().getDataType();
        boolean dataTypeOk = (dataType == DataBuffer.TYPE_BYTE
                || dataType == DataBuffer.TYPE_SHORT
                || dataType == DataBuffer.TYPE_INT);

        if (kJAI.getWidth() == 3
                && kJAI.getHeight() == 3
                && kJAI.getXOrigin() == 1
                && kJAI.getYOrigin() == 1
                && dataTypeOk) {
            return new Convolve3x3OpImage(paramBlock.getRenderedSource(0), extender, renderHints, layout, kJAI);
        } else if (kJAI.isSeparable()) {
            return new SeparableConvolveOpImage(paramBlock.getRenderedSource(0), extender, renderHints, layout, kJAI);

        } else {
            return new ConvolveOpImage(paramBlock.getRenderedSource(0), extender, renderHints, layout, kJAI);
        }
    }
}
