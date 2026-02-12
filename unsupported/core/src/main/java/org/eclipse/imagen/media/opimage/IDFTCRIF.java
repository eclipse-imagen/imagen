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
import org.eclipse.imagen.CRIFImpl;
import org.eclipse.imagen.EnumeratedParameter;
import org.eclipse.imagen.ImageLayout;

/**
 * A <code>CRIF</code> supporting the "IDFT" operation in the rendered image layer.
 *
 * @since Beta
 * @see org.eclipse.imagen.operator.DFTDescriptor
 * @see org.eclipse.imagen.operator.IDFTDescriptor
 */
public class IDFTCRIF extends CRIFImpl {

    /** Constructor. */
    public IDFTCRIF() {
        super("idft");
    }

    /**
     * Creates a new instance of an IDFT operator according to the scaling type.
     *
     * @param paramBlock The scaling type.
     */
    public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints) {
        // Get ImageLayout from renderHints if any.
        ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);

        RenderedImage source = paramBlock.getRenderedSource(0);
        EnumeratedParameter scalingType = (EnumeratedParameter) paramBlock.getObjectParameter(0);
        EnumeratedParameter dataNature = (EnumeratedParameter) paramBlock.getObjectParameter(1);

        FFT fft = new FFT(false, new Integer(scalingType.getValue()), 2);

        return new DFTOpImage(source, renderHints, layout, dataNature, fft);
    }
}
