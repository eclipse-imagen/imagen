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
import java.awt.image.DataBuffer;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import org.eclipse.imagen.BorderExtender;
import org.eclipse.imagen.ImageLayout;
import org.eclipse.imagen.KernelImageN;

/** @see ErodeOpImage */
public class ErodeRIF implements RenderedImageFactory {

    /** Constructor. */
    public ErodeRIF() {}

    /**
     * Create a new instance of ErodeOpImage in the rendered layer. This method satisfies the implementation of RIF.
     *
     * @param paramBlock The source image and the erosion kernel.
     */
    public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints) {
        // Get ImageLayout from renderHints if any.
        ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);

        // Get BorderExtender from renderHints if any.
        BorderExtender extender = RIFUtil.getBorderExtenderHint(renderHints);

        KernelImageN unRotatedKernel = (KernelImageN) paramBlock.getObjectParameter(0);
        KernelImageN kJAI = unRotatedKernel.getRotatedKernel();

        RenderedImage source = paramBlock.getRenderedSource(0);
        SampleModel sm = source.getSampleModel();

        // check dataType and binary
        int dataType = sm.getDataType();

        boolean isBinary = (sm instanceof MultiPixelPackedSampleModel)
                && (sm.getSampleSize(0) == 1)
                && (dataType == DataBuffer.TYPE_BYTE
                        || dataType == DataBuffer.TYPE_USHORT
                        || dataType == DataBuffer.TYPE_INT);

        // possible speed up later: 3x3 with table lookup
        if (isBinary) {

            return new ErodeBinaryOpImage(source, extender, renderHints, layout, kJAI);
        } else {
            return new ErodeOpImage(source, extender, renderHints, layout, kJAI);
        }
    }
}
