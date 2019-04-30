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
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import java.util.Arrays;
import org.eclipse.imagen.BorderExtender;
import org.eclipse.imagen.ImageLayout;
import org.eclipse.imagen.KernelJAI;
import java.util.Map;

/**
 * A <code>RIF</code> supporting the "BoxFilter" operation in the rendered
 * image layer.
 *
 * @see org.eclipse.imagen.operator.BoxFilterDescriptor
 * @see org.eclipse.imagen.media.opimage.SeparableConvolveOpImage
 *
 * @since EA4
 *
 */
public class BoxFilterRIF implements RenderedImageFactory {

    /** Constructor. */
    public BoxFilterRIF() {}

    /**
     * Create a new instance of SeparableConvolveOpImage in the rendered layer.
     * This method satisfies the implementation of RIF.
     *
     * @param paramBlock  The source image and the convolution kernel.
     */
    public RenderedImage create(ParameterBlock paramBlock,
                                RenderingHints renderHints) {
        // Get ImageLayout from renderHints if any.
        ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);
        

        // Get BorderExtender from renderHints if any.
        BorderExtender extender = RIFUtil.getBorderExtenderHint(renderHints);

        // Get the operation parameters.
        int width = paramBlock.getIntParameter(0);
        int height = paramBlock.getIntParameter(1);
        int xOrigin = paramBlock.getIntParameter(2);
        int yOrigin = paramBlock.getIntParameter(3);

        // Allocate and initialize arrays.
        float[] dataH = new float[width];
        Arrays.fill(dataH, 1.0F/(float)width);
        float[] dataV = null;
        if(height == width) {
            dataV = dataH;
        } else {
            dataV = new float[height];
            Arrays.fill(dataV, 1.0F/(float)height);
        }

        // Construct a separable kernel.
        KernelJAI kernel = new KernelJAI(width, height, xOrigin, yOrigin,
                                         dataH, dataV);

        // Construct and return the OpImage.
        return new SeparableConvolveOpImage(paramBlock.getRenderedSource(0),
                                            extender,
                                            renderHints,
                                            layout,
                                            kernel);
    }
}
