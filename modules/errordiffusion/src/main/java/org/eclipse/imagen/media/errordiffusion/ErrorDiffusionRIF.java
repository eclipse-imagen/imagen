/* JAI-Ext - OpenSource Java Advanced Image Extensions Library
 *    http://www.geo-solutions.it/
 *    Copyright 2014 GeoSolutions
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 
 * http://www.apache.org/licenses/LICENSE-2.0
 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.eclipse.imagen.media.errordiffusion;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import org.eclipse.imagen.ImageLayout;
import org.eclipse.imagen.KernelImageN;
import org.eclipse.imagen.LookupTableImageN;
import org.eclipse.imagen.ROI;
import org.eclipse.imagen.media.opimage.RIFUtil;
import org.eclipse.imagen.media.range.Range;

/**
 * {@link RenderedImageFactory} implementation for the Error diffusion operation
 *
 * @author Nicola Lagomarsini geosolutions
 */
public class ErrorDiffusionRIF implements RenderedImageFactory {

    /** Constructor. */
    public ErrorDiffusionRIF() {}

    public RenderedImage create(ParameterBlock paramBlock, RenderingHints hints) {
        // Get ImageLayout from renderHints if any.
        ImageLayout layout = RIFUtil.getImageLayoutHint(hints);
        // Get the source image
        RenderedImage source = paramBlock.getRenderedSource(0);
        // Get the parameters
        LookupTableImageN lookupTable = (LookupTableImageN) paramBlock.getObjectParameter(0);
        KernelImageN kernel = (KernelImageN) paramBlock.getObjectParameter(1);
        ROI roi = (ROI) paramBlock.getObjectParameter(2);
        Range nodata = (Range) paramBlock.getObjectParameter(3);
        int destNoData = paramBlock.getIntParameter(4);

        return new ErrorDiffusionOpImage(source, hints, layout, lookupTable, kernel, roi, nodata, destNoData);
    }
}
