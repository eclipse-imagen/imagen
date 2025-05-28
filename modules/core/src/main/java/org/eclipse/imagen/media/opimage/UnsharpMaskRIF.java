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
import org.eclipse.imagen.KernelJAI;
import org.eclipse.imagen.media.convolve.Convolve3x3OpImage;
import org.eclipse.imagen.media.convolve.ConvolveGeneralOpImage;
import org.eclipse.imagen.media.convolve.ConvolveOpImage;
import org.eclipse.imagen.media.util.ImageUtil;

/** @see UnsharpMaskOpImage */
public class UnsharpMaskRIF implements RenderedImageFactory {

    /** Constructor. */
    public UnsharpMaskRIF() {}

    /**
     * Create a new instance of UnsharpMaskOpImage in the rendered layer. This method satisfies the implementation of
     * RIF.
     *
     * @param paramBlock The source image, the unsharp mask kernel and the gain factor.
     */
    public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints) {
        // Get ImageLayout from renderHints if any.
        ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);

        // Get BorderExtender from renderHints if any.
        BorderExtender extender = RIFUtil.getBorderExtenderHint(renderHints);

        // map the input kernel + gain factor to an equivalent
        // convolution kernel and then do a normal convolve.
        KernelJAI unRotatedKernel = ImageUtil.getUnsharpMaskEquivalentKernel(
                (KernelJAI) paramBlock.getObjectParameter(0), paramBlock.getFloatParameter(1));

        KernelJAI kJAI = unRotatedKernel.getRotatedKernel();

        RenderedImage source = paramBlock.getRenderedSource(0);
        int dataType = source.getSampleModel().getDataType();

        boolean dataTypeOk = (dataType == DataBuffer.TYPE_BYTE
                || dataType == DataBuffer.TYPE_SHORT
                || dataType == DataBuffer.TYPE_INT);

        if ((kJAI.getWidth() == 3)
                && (kJAI.getHeight() == 3)
                && (kJAI.getXOrigin() == 1)
                && (kJAI.getYOrigin() == 1)
                && dataTypeOk) {
            //TODO: Convolve3x3OpImage was moved to legacy
            //      constructor has changed.  Veryify this is correct.
            return new Convolve3x3OpImage(source, extender, renderHints, layout, kJAI, null, null,0, false);
        } else if (kJAI.isSeparable()) {
            return new SeparableConvolveOpImage(source, extender, renderHints, layout, kJAI);

        } else {
            //TODO: ConvolveOpImage In jai this was a final class, in the new implementation, this is an abstract class
            //TODO: I moved this to ConvolveGeneralOpImage - not sure if this is correct.
            return new ConvolveGeneralOpImage(source, extender, renderHints, layout, kJAI, null, null, 0, false);
        }
    }
}
