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

/**
 * A <code>RIF</code> supporting the "border" operation in the rendered image layer.
 *
 * @see java.awt.image.renderable.RenderedImageFactory
 * @see org.eclipse.imagen.operator.BorderDescriptor
 * @see BorderOpImage
 */
public class BorderRIF implements RenderedImageFactory {

    /** Constructor. */
    public BorderRIF() {}

    /**
     * Creates a new instance of <code>BorderOpImage</code> in the rendered layer.
     *
     * @param args The source image and the border information
     * @param hints Optionally contains destination image layout.
     */
    public RenderedImage create(ParameterBlock args, RenderingHints renderHints) {
        // Get ImageLayout from renderHints if any.
        ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);

        RenderedImage source = args.getRenderedSource(0);
        int leftPad = args.getIntParameter(0);
        int rightPad = args.getIntParameter(1);
        int topPad = args.getIntParameter(2);
        int bottomPad = args.getIntParameter(3);
        BorderExtender type = (BorderExtender) args.getObjectParameter(4);

        if (type == BorderExtender.createInstance(BorderExtender.BORDER_WRAP)) {
            int minX = source.getMinX() - leftPad;
            int minY = source.getMinY() - topPad;
            int width = source.getWidth() + leftPad + rightPad;
            int height = source.getHeight() + topPad + bottomPad;

            return new PatternOpImage(source.getData(), source.getColorModel(), minX, minY, width, height);
        } else {
            return new BorderOpImage(source, renderHints, layout, leftPad, rightPad, topPad, bottomPad, type);
        }
    }
}
