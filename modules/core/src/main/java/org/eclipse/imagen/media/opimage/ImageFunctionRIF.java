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
import java.awt.image.renderable.RenderedImageFactory;
import java.awt.image.renderable.ParameterBlock;
import org.eclipse.imagen.ImageFunction;
import org.eclipse.imagen.ImageLayout;
import java.util.Map;

/**
 * A <code>RIF</code> supporting the "ImageFunction" operation in the rendered
 * image layer.
 *
 * @see org.eclipse.imagen.operator.ImageFunctionDescriptor
 * @see org.eclipse.imagen.ImageFunction
 * @since EA4
 */
public class ImageFunctionRIF implements RenderedImageFactory {

    /** Constructor. */
    public ImageFunctionRIF() {}

    /**
     * Creates a new instance of ImageFunctionOpImage in the rendered layer.
     * This method satisfies the implementation of RIF.
     *
     * @param paramBlock  The source image, the X and Y scale factor,
     *                    and the interpolation method for resampling.
     */
    public RenderedImage create(ParameterBlock paramBlock,
                                RenderingHints renderHints) {
        // Get ImageLayout from renderHints if any.
        ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);
        

        ImageFunction function =
            (ImageFunction)paramBlock.getObjectParameter(0);

        // Ascertain that a supplied SampleModel has the requisite
        // number of bands vis-a-vis the ImageFunction.
        int numBandsRequired = function.isComplex() ?
            function.getNumElements() * 2 : function.getNumElements();
        if(layout != null &&
           layout.isValid(ImageLayout.SAMPLE_MODEL_MASK) &&
           layout.getSampleModel(null).getNumBands() != numBandsRequired ) {
            throw new RuntimeException(JaiI18N.getString("ImageFunctionRIF0"));
        }

        int minX = 0;
        int minY = 0;
        if (layout != null) {
            if (layout.isValid(ImageLayout.MIN_X_MASK)) {
                minX = layout.getMinX(null);
            }
            if (layout.isValid(ImageLayout.MIN_Y_MASK)) {
                minY = layout.getMinX(null);
            }
        }

        int width = paramBlock.getIntParameter(1);
        int height = paramBlock.getIntParameter(2);
        float xScale = paramBlock.getFloatParameter(3);
        float yScale = paramBlock.getFloatParameter(4);
        float xTrans = paramBlock.getFloatParameter(5);
        float yTrans = paramBlock.getFloatParameter(6);

        return new ImageFunctionOpImage(function,
                                        minX, minY,
                                        width, height,
                                        xScale, yScale,
                                        xTrans, yTrans,
                                        renderHints, layout);
    }
}
