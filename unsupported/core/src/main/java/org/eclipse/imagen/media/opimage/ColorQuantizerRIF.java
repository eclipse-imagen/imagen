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
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import java.util.Map;
import org.eclipse.imagen.ImageLayout;
import org.eclipse.imagen.ImageN;
import org.eclipse.imagen.ROI;
import org.eclipse.imagen.operator.ColorQuantizerDescriptor;
import org.eclipse.imagen.operator.ColorQuantizerType;

/**
 * Class implementing the RIF interface for the ColorQuantizer operator. An instance of this class should be registered
 * with the OperationRegistry with operation name "ColorQuantizer."
 */
public class ColorQuantizerRIF implements RenderedImageFactory {

    /** Default constructor (there is no input). */
    public ColorQuantizerRIF() {}

    /**
     * Creates a new instance of ColorQuantizerOpImage in the rendered layer. This method satisfies the implementation
     * of RIF.
     *
     * @param paramBlock The source image, the color quantization algorithm name, the maximum number of colors, the
     *     parameter for training (the histogram size for median-cut, the cycle for neuquant, and maximum tree size for
     *     oct-tree), and the ROI.
     * @param renderHints RenderingHints.
     */
    public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints) {
        RenderedImage source = paramBlock.getRenderedSource(0);

        ImageLayout layout = renderHints == null ? null : (ImageLayout) renderHints.get(ImageN.KEY_IMAGE_LAYOUT);

        ColorQuantizerType algorithm = (ColorQuantizerType) paramBlock.getObjectParameter(0);
        int maxColorNum = paramBlock.getIntParameter(1);
        int upperBound = paramBlock.getIntParameter(2);
        ROI roi = (ROI) paramBlock.getObjectParameter(3);
        int xPeriod = paramBlock.getIntParameter(4);
        int yPeriod = paramBlock.getIntParameter(5);

        // check if 3-band byte-type image
        SampleModel sm = source.getSampleModel();
        if (sm.getNumBands() != 3 && sm.getDataType() == DataBuffer.TYPE_BYTE)
            throw new IllegalArgumentException("ColorQuantizerRIF0");

        if (algorithm.equals(ColorQuantizerDescriptor.NEUQUANT))
            return new NeuQuantOpImage(
                    source, (Map) renderHints, layout, maxColorNum, upperBound, roi, xPeriod, yPeriod);

        if (algorithm.equals(ColorQuantizerDescriptor.OCTTREE))
            return new OctTreeOpImage(
                    source, (Map) renderHints, layout, maxColorNum, upperBound, roi, xPeriod, yPeriod);
        else
            return new MedianCutOpImage(
                    source, (Map) renderHints, layout, maxColorNum, upperBound, roi, xPeriod, yPeriod);
    } // create
} // ColorQuantizerRIF
