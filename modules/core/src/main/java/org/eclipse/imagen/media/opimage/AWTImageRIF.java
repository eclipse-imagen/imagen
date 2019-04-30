/*
 * Copyright (c) [2019,] 2019, Oracle and/or its affiliates. All rights reserved.
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
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import org.eclipse.imagen.ImageLayout;
import java.util.Map;

/**
 * A <code>RIF</code> supporting the "AWTImage" operation in the rendered
 * layer. It's used by </code>OperationRegistry</code> to create an
 * <code>AWTImageOpImage</code>.
 *
 * @see org.eclipse.imagen.operator.AWTImageDescriptor
 * @see AWTImageOpImage
 *
 */
public class AWTImageRIF implements RenderedImageFactory {

    /** Constructor. */
    public AWTImageRIF() {}

    /**
     * Creates a new instance of <code>AWTImageOpImage</code>
     * in the rendered layer.
     * This method satisfies the implementation of RIF.
     *
     * @param paramBlock  The AWT image.
     */
    public RenderedImage create(ParameterBlock paramBlock,
                                RenderingHints renderHints) {
        // Get ImageLayout from renderHints if any.
        ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);

        // Extract the source AWT Image and cast it.
        Image awtImage = (Image)paramBlock.getObjectParameter(0);

        // If it's already a RenderedImage (as for a BufferedImage) just cast.
        if(awtImage instanceof RenderedImage) {
            return (RenderedImage)awtImage;
        }

        // Create a RenderedImage from the data.
        return new AWTImageOpImage(renderHints, layout, awtImage);
    }
}
