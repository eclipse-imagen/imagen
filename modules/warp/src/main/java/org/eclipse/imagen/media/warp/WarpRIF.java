/* JAI-Ext - OpenSource Java Advanced Image Extensions Library
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
package org.eclipse.imagen.media.warp;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import org.eclipse.imagen.BorderExtender;
import org.eclipse.imagen.ImageLayout;
import org.eclipse.imagen.Interpolation;
import org.eclipse.imagen.PlanarImage;
import org.eclipse.imagen.ROI;
import org.eclipse.imagen.Warp;
import org.eclipse.imagen.media.interpolators.InterpolationBicubic;
import org.eclipse.imagen.media.interpolators.InterpolationBilinear;
import org.eclipse.imagen.media.interpolators.InterpolationNearest;
import org.eclipse.imagen.media.opimage.RIFUtil;
import org.eclipse.imagen.media.range.Range;
import org.eclipse.imagen.media.range.RangeFactory;

/**
 * A <code>RIF</code> supporting the "Warp" operation in the rendered image layer.
 *
 * @since EA2
 * @see org.eclipse.imagen.operator.WarpDescriptor
 * @see GeneralWarpOpImage
 */
public class WarpRIF implements RenderedImageFactory {

    /** Constructor. */
    public WarpRIF() {}

    /**
     * Creates a new instance of warp operator according to the warp object and interpolation method.
     *
     * @param paramBlock The warp and interpolation objects.
     */
    public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints) {
        // Get ImageLayout from renderHints if any.
        ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);

        // Get BorderExtender from renderHints if any.
        BorderExtender extender = RIFUtil.getBorderExtenderHint(renderHints);

        RenderedImage source = paramBlock.getRenderedSource(0);
        Warp warp = (Warp) paramBlock.getObjectParameter(0);
        Interpolation interp = (Interpolation) paramBlock.getObjectParameter(1);
        double[] backgroundValues = (double[]) paramBlock.getObjectParameter(2);

        ROI roi = null;
        Object roi_ = paramBlock.getObjectParameter(3);
        if (roi_ instanceof ROI) {
            roi = (ROI) roi_;
            PlanarImage temp = PlanarImage.wrapRenderedImage(source);
            temp.setProperty("ROI", roi);
            source = temp;
        }
        Range noData = (Range) paramBlock.getObjectParameter(4);
        noData = RangeFactory.convert(noData, source.getSampleModel().getDataType());
        if (interp instanceof InterpolationNearest || interp instanceof org.eclipse.imagen.InterpolationNearest) {
            return new WarpNearestOpImage(source, renderHints, layout, warp, interp, roi, noData, backgroundValues);
        } else if (interp instanceof InterpolationBilinear
                || interp instanceof org.eclipse.imagen.InterpolationBilinear) {
            return new WarpBilinearOpImage(
                    source, extender, renderHints, layout, warp, interp, roi, noData, backgroundValues);
        } else if (interp instanceof InterpolationBicubic
                || interp instanceof org.eclipse.imagen.InterpolationBicubic
                || interp instanceof org.eclipse.imagen.InterpolationBicubic2) {
            return new WarpBicubicOpImage(
                    source, extender, renderHints, layout, warp, interp, roi, noData, backgroundValues);
        } else {
            return new WarpGeneralOpImage(
                    source, extender, renderHints, layout, warp, interp, backgroundValues, roi, noData);
        }
    }
}
