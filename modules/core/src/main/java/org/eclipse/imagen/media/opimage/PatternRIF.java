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
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import org.eclipse.imagen.ImageLayout;

/**
 * A <code>RIF</code> supporting the "Pattern" operation in the rendered image layer.
 *
 * @see org.eclipse.imagen.operator.PatternDescriptor
 * @see PatternOpImage
 */
public class PatternRIF implements RenderedImageFactory {

    /** Constructor. */
    public PatternRIF() {}

    /**
     * Creates a new instance of PatternOpImage in the rendered layer. This method satisfies the implementation of RIF.
     */
    public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints) {
        // Get ImageLayout from renderHints if any.
        ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);

        int minX = 0;
        int minY = 0;

        if (layout != null) {
            if (layout.isValid(ImageLayout.MIN_X_MASK)) {
                minX = layout.getMinX(null);
            }
            if (layout.isValid(ImageLayout.MIN_Y_MASK)) {
                minY = layout.getMinY(null);
            }
        }

        RenderedImage source = (RenderedImage) paramBlock.getSource(0);
        Raster pattern = source.getData();
        ColorModel colorModel = source.getColorModel();

        // Get image width and height from the parameter block
        int width = paramBlock.getIntParameter(0);
        int height = paramBlock.getIntParameter(1);

        return new PatternOpImage(pattern, colorModel, minX, minY, width, height);
    }
}
