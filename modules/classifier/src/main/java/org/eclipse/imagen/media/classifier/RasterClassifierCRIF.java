/* ImageN-Ext - OpenSource Java Advanced Image Extensions Library
*    http://www.geo-solutions.it/
*    Copyright 2014 GeoSolutions


* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at

* http://www.apache.org/licenses/LICENSE-2.0

* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.eclipse.imagen.media.classifier;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import org.eclipse.imagen.CRIFImpl;
import org.eclipse.imagen.ImageLayout;
import org.eclipse.imagen.ROI;
import org.eclipse.imagen.media.opimage.RIFUtil;
import org.eclipse.imagen.media.range.Range;

/**
 * {@link RenderedImageFactory} used for creating a new {@link RasterClassifierOpImage} instance.
 *
 * @author Nicola Lagomarsini geosolutions
 */
public class RasterClassifierCRIF extends CRIFImpl {

    /**
     * Creates a {@link RenderedImage} representing the results of an imaging operation for a given
     * {@link ParameterBlock} and {@link RenderingHints}.
     */
    public RenderedImage create(final ParameterBlock param, final RenderingHints hints) {
        // Getting Source
        final RenderedImage image = (RenderedImage) param.getSource(0);
        // Getting imageLayout
        ImageLayout layout = RIFUtil.getImageLayoutHint(hints);
        // Getting parameters
        final ColorMapTransform<ColorMapTransformElement> lic =
                (ColorMapTransform<ColorMapTransformElement>) param.getObjectParameter(0);
        final int bandIndex = param.getIntParameter(1);
        ROI roi = (ROI) param.getObjectParameter(2);
        Range nodata = (Range) param.getObjectParameter(3);
        // Creating the RasterClassifierOpImage
        return new RasterClassifierOpImage(image, lic, layout, bandIndex, roi, nodata, hints);
    }
}
