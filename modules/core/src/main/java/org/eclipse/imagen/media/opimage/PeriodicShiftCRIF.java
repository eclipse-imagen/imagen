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
import org.eclipse.imagen.CRIFImpl;
import org.eclipse.imagen.ImageLayout;

/**
 * This image factory supports image operator <code>PeriodicShiftOpImage</code> in the rendered and renderable image
 * layers.
 *
 * @see PeriodicShiftOpImage
 */
public class PeriodicShiftCRIF extends CRIFImpl {

    /** Constructor. */
    public PeriodicShiftCRIF() {
        super("periodicshift");
    }

    /**
     * Creates a new instance of <code>PeriodicShiftOpImage</code> in the rendered layer. This method satisfies the
     * implementation of RIF.
     */
    public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints) {
        // Get ImageLayout from renderHints if any.
        ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);

        // Get the source image.
        RenderedImage source = paramBlock.getRenderedSource(0);

        // Get the translation parameters.
        int shiftX = paramBlock.getIntParameter(0);
        int shiftY = paramBlock.getIntParameter(1);

        // Return the OpImage.
        return new PeriodicShiftOpImage(source, renderHints, layout, shiftX, shiftY);
    }
}
