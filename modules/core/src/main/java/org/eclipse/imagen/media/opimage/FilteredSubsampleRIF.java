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
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import java.util.Map;
import org.eclipse.imagen.BorderExtender;
import org.eclipse.imagen.ImageLayout;
import org.eclipse.imagen.ImageN;
import org.eclipse.imagen.Interpolation;
import org.eclipse.imagen.InterpolationBicubic;
import org.eclipse.imagen.InterpolationBicubic2;
import org.eclipse.imagen.InterpolationBilinear;
import org.eclipse.imagen.InterpolationNearest;

/**
 * Class implementing the RIF interface for the FilteredSubsample operator. An instance of this class should be
 * registered with the OperationRegistry with operation name "FilteredSubsample."
 */
public class FilteredSubsampleRIF implements RenderedImageFactory {

    /** Default constructor (there is no input). */
    public FilteredSubsampleRIF() {}

    /**
     * Creates a new instance of SubsampleOpImage in the rendered layer. This method satisfies the implementation of
     * RIF.
     *
     * @param paramBlock The source image, the X and Y scale factors.
     * @param renderHints RenderingHints.
     */
    public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints) {
        RenderedImage source = paramBlock.getRenderedSource(0);

        BorderExtender extender =
                renderHints == null ? null : (BorderExtender) renderHints.get(ImageN.KEY_BORDER_EXTENDER);
        ImageLayout layout = renderHints == null ? null : (ImageLayout) renderHints.get(ImageN.KEY_IMAGE_LAYOUT);

        int scaleX = paramBlock.getIntParameter(0);
        int scaleY = paramBlock.getIntParameter(1);
        float[] qsFilter = (float[]) paramBlock.getObjectParameter(2);
        Interpolation interp = (Interpolation) paramBlock.getObjectParameter(3);

        // check if binary and interpolation type allowed
        SampleModel sm = source.getSampleModel();
        int dataType = sm.getDataType();

        // Determine the interpolation type, if not supported throw exception
        boolean validInterp = (interp instanceof InterpolationNearest)
                || (interp instanceof InterpolationBilinear)
                || (interp instanceof InterpolationBicubic)
                || (interp instanceof InterpolationBicubic2);

        if (!validInterp) throw new IllegalArgumentException(JaiI18N.getString("FilteredSubsample3"));

        return new FilteredSubsampleOpImage(
                source, extender, (Map) renderHints, layout, scaleX, scaleY, qsFilter, interp);
    } // create
} // FilteredSubsampleRIF
