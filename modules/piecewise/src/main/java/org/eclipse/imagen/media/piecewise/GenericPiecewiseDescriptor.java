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
package org.eclipse.imagen.media.piecewise;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import org.eclipse.imagen.JAI;
import org.eclipse.imagen.OperationDescriptorImpl;
import org.eclipse.imagen.ParameterBlockJAI;
import org.eclipse.imagen.ROI;
import org.eclipse.imagen.RenderedOp;
import org.eclipse.imagen.media.range.Range;
import org.eclipse.imagen.registry.RenderedRegistryMode;

/**
 * {@link OperationDescriptorImpl} for the GenericPiecewise operation.
 *
 * @author Nicola Lagomarsini geosolutions
 */
public class GenericPiecewiseDescriptor extends OperationDescriptorImpl {
    /** UID */
    private static final long serialVersionUID = 7954257625240335874L;

    /** Construct the descriptor. */
    public GenericPiecewiseDescriptor() {
        super(
                new String[][] {
                    {"GlobalName", GenericPiecewiseOpImage.OPERATION_NAME},
                    {"LocalName", GenericPiecewiseOpImage.OPERATION_NAME},
                    {"Vendor", "org.eclipse.imagen.media"},
                    {"Description", "Generic Piecewise Transformation"},
                    {"DocURL", ""},
                    {"Version", "1.0"}
                },
                new String[] {RenderedRegistryMode.MODE_NAME},
                1,
                new String[] {"Domain1D", "bandIndex", "roi", "nodata"}, // Argument
                // names
                new Class[] {
                    PiecewiseTransform1D.class,
                    Integer.class,
                    org.eclipse.imagen.ROI.class,
                    org.eclipse.imagen.media.range.Range.class
                }, // Argument
                // classes
                new Object[] {NO_PARAMETER_DEFAULT, new Integer(-1), null, null}, // Default values for parameters,
                null // No restriction on valid parameter values.
                );
    }

    /**
     * Static create method returning a new image passed by the GenericPiecewise operation
     *
     * @param source0 input image to process
     * @param domain1D input piecewise transformation
     * @param bandIndex index defining on which band calculation must be done
     * @param roi {@link ROI} object used for reducing computation area
     * @param nodata NoData {@link Range} defining NoData values
     * @param hints Configuration Hints
     * @return
     */
    public RenderedOp create(
            RenderedImage source0,
            PiecewiseTransform1D domain1D,
            Integer bandIndex,
            ROI roi,
            Range nodata,
            RenderingHints hints) {
        ParameterBlockJAI pb =
                new ParameterBlockJAI(GenericPiecewiseOpImage.OPERATION_NAME, RenderedRegistryMode.MODE_NAME);
        // Setting source
        pb.setSource(source0, 0);
        // Setting parameters
        pb.setParameter("Domain1D", domain1D);
        pb.setParameter("bandIndex", bandIndex);
        pb.setParameter("roi", roi);
        pb.setParameter("nodata", nodata);

        return JAI.create(GenericPiecewiseOpImage.OPERATION_NAME, pb, hints);
    }

    /**
     * Returns {@code true} if the parameters are valids. This implementation check that the number of bands in the
     * source image is equals to the number of supplied sample dimensions, and that all sample dimensions has piecewise.
     *
     * @param modeName The mode name (usually "Rendered").
     * @param param The parameter block for the operation to performs.
     * @param message A buffer for formatting an error message if any.
     */
    @SuppressWarnings("unchecked")
    protected boolean validateParameters(
            final String modeName, final ParameterBlock param, final StringBuffer message) {
        if (!super.validateParameters(modeName, param, message)) {
            return false;
        }
        final RenderedImage source = (RenderedImage) param.getSource(0);
        final PiecewiseTransform1D lic = (PiecewiseTransform1D) param.getObjectParameter(0);
        if (lic == null) return false;
        final int numBands = source.getSampleModel().getNumBands();
        final int bandIndex = param.getIntParameter(1);
        if (bandIndex == -1) return true;
        if (bandIndex < 0 || bandIndex >= numBands) {
            return false;
        }
        return true;
    }
}
