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
import org.eclipse.imagen.ROI;

/**
 * A <code>RIF</code> supporting the "Mean" operation in the rendered image layer.
 *
 * @since EA2
 * @see org.eclipse.imagen.operator.MeanDescriptor
 */
public class MeanRIF implements RenderedImageFactory {

    /** Constructor. */
    public MeanRIF() {}

    /**
     * Creates a new instance of <code>MeanOpImage</code> in the rendered layer. Any image layout information in <code>
     * RenderingHints</code> is ignored. This method satisfies the implementation of RIF.
     */
    public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints) {
        RenderedImage src = paramBlock.getRenderedSource(0);

        int xStart = src.getMinX(); // default values
        int yStart = src.getMinY();

        int maxWidth = src.getWidth();
        int maxHeight = src.getHeight();

        return new MeanOpImage(
                src,
                (ROI) paramBlock.getObjectParameter(0),
                xStart,
                yStart,
                paramBlock.getIntParameter(1),
                paramBlock.getIntParameter(2));
    }
}
