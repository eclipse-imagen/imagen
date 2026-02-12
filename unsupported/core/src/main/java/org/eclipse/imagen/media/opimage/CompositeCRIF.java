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
import java.awt.image.renderable.RenderContext;
import java.awt.image.renderable.RenderableImage;
import org.eclipse.imagen.CRIFImpl;
import org.eclipse.imagen.EnumeratedParameter;
import org.eclipse.imagen.ImageLayout;
import org.eclipse.imagen.ImageN;
import org.eclipse.imagen.operator.CompositeDescriptor;

/**
 * A <code>CRIF</code> supporting the "Composite" operation in the rendered and renderable image modes.
 *
 * @see org.eclipse.imagen.operator.CompositeDescriptor
 * @see CompositeOpImage
 * @see CompositeNoAlphaOpImage
 */
public class CompositeCRIF extends CRIFImpl {

    /** Constructor. */
    public CompositeCRIF() {
        super("composite");
    }

    /**
     * Creates a new instance of <code>CompositeOpImage</code> in the rendered layer. This method satisfies the
     * implementation of RIF.
     */
    public RenderedImage create(ParameterBlock args, RenderingHints hints) {
        /* Get ImageLayout from RenderingHints if any. */
        ImageLayout layout = RIFUtil.getImageLayoutHint(hints);

        RenderedImage source1 = args.getRenderedSource(0);
        RenderedImage source2 = args.getRenderedSource(1);

        RenderedImage alpha1 = (RenderedImage) args.getObjectParameter(0);
        RenderedImage alpha2 = null;
        if (args.getObjectParameter(1) != null) {
            alpha2 = (RenderedImage) args.getObjectParameter(1);
        }

        boolean premultiplied = ((Boolean) args.getObjectParameter(2)).booleanValue();
        EnumeratedParameter destAlpha = (EnumeratedParameter) args.getObjectParameter(3);

        if (destAlpha.equals(CompositeDescriptor.NO_DESTINATION_ALPHA)) {
            return new CompositeNoDestAlphaOpImage(source1, source2, hints, layout, alpha1, alpha2, premultiplied);
        } else {
            return new CompositeOpImage(
                    source1,
                    source2,
                    hints,
                    layout,
                    alpha1,
                    alpha2,
                    premultiplied,
                    destAlpha.equals(CompositeDescriptor.DESTINATION_ALPHA_FIRST));
        }
    }

    /**
     * Creates a <Code>RenderedImage</Code> from the renderable layer.
     *
     * @param renderContext The rendering information associated with this rendering.
     * @param paramBlock The parameters used to create the image.
     * @return A <code>RenderedImage</code>.
     */
    public RenderedImage create(RenderContext renderContext, ParameterBlock paramBlock) {

        // Get the two renderable alpha images from the parameter block
        RenderableImage alphaImage1 = (RenderableImage) paramBlock.getObjectParameter(0);
        RenderableImage alphaImage2 = (RenderableImage) paramBlock.getObjectParameter(1);

        // Cause the two renderable alpha images to be rendered
        RenderedImage rAlphaImage1 = alphaImage1.createRendering(renderContext);
        RenderedImage rAlphaImage2 = alphaImage2.createRendering(renderContext);

        ParameterBlock newPB = (ParameterBlock) paramBlock.clone();

        // Replace the renderable alpha images in the ParameterBlock with
        // their renderings
        newPB.set(rAlphaImage1, 0);
        newPB.set(rAlphaImage2, 1);

        // Return ImageN.create("composite")
        return ImageN.create("composite", newPB, renderContext.getRenderingHints());
    }
}
