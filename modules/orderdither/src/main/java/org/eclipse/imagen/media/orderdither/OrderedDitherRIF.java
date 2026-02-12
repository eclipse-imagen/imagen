/* JAI-Ext - OpenSource Java Advanced Image Extensions Library
 *    http://www.geo-solutions.it/
 *    Copyright 2014 GeoSolutions
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.eclipse.imagen.media.orderdither;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import org.eclipse.imagen.ColorCube;
import org.eclipse.imagen.ImageLayout;
import org.eclipse.imagen.KernelImageN;
import org.eclipse.imagen.ROI;
import org.eclipse.imagen.media.opimage.RIFUtil;
import org.eclipse.imagen.media.range.Range;

/**
 * {@link RenderedImageFactory} implementation generating a new {@link OrderedDitherOpImage} instance.
 *
 * @author Nicola Lagomarsini GeoSolutions
 */
public class OrderedDitherRIF implements RenderedImageFactory {

    public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints) {
        // Get ImageLayout from renderHints if any.
        ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);
        // Getting source
        RenderedImage source = paramBlock.getRenderedSource(0);
        // Getting the parameters
        ColorCube colorMap = (ColorCube) paramBlock.getObjectParameter(0);
        KernelImageN[] ditherMask = (KernelImageN[]) paramBlock.getObjectParameter(1);
        ROI roi = (ROI) paramBlock.getObjectParameter(2);
        Range nodata = (Range) paramBlock.getObjectParameter(3);
        double destNoData = paramBlock.getDoubleParameter(4);

        return new OrderedDitherOpImage(source, renderHints, layout, colorMap, ditherMask, roi, nodata, destNoData);
    }
}
