/* JAI-Ext - OpenSource Java Advanced Image Extensions Library
*    http://www.geo-solutions.it/
*    Copyright 2016 GeoSolutions


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
package org.eclipse.imagen.media.threshold;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.util.logging.Logger;
import org.eclipse.imagen.JAI;
import org.eclipse.imagen.OperationDescriptorImpl;
import org.eclipse.imagen.ParameterBlockJAI;
import org.eclipse.imagen.ROI;
import org.eclipse.imagen.RenderedOp;
import org.eclipse.imagen.media.range.Range;
import org.eclipse.imagen.registry.RenderedRegistryMode;

/*
 * The Threshold operation takes one rendered image,
 * and maps all the pixels of this image whose value
 * falls within a specified range to a specified constant.
 * The range is specified by a low value and a high value.
 *
 * the number of elements supplied via the "high", "low",
 * and "constants" arrays must be 1 (the element at entry 0
 * is applied to all the bands) or
 * equal to the number of bands of the source image.
 */

public class ThresholdDescriptor extends OperationDescriptorImpl {

    static final Logger LOGGER = Logger.getLogger(ThresholdDescriptor.class.getName());

    /**
     * The resource strings that provide the general documentation and specify the parameter list for this operation.
     */
    private static final String[][] resources = {
        {"GlobalName", "Threshold"},
        {"LocalName", "Threshold"},
        {"Vendor", "org.eclipse.imagen.media"},
        {
            "Description",
            "Operation used for sets all the pixels whose value is below a low value to that low value and all the pixels whose value is above a high value to that high value"
        },
        {"DocURL", "Not Defined"},
        {"Version", "1.0"},
        {"arg0Desc", "noData values"},
        {"arg1Desc", "Destination No Data value"},
        {"arg2Desc", "ROI object to use"},
        {"arg3Desc", "The lower boundary for each band"},
        {"arg4Desc", "The upper boundary for each band"},
        {"arg5Desc", "The constant value for each band"}
    };

    /** Input Parameter name */
    private static final String[] paramNames = {"noData", "destinationNoData", "roi", "low", "high", "constant"};

    /** Input Parameter class */
    private static final Class[] paramClasses = {
        org.eclipse.imagen.media.range.Range.class,
        Double.class,
        org.eclipse.imagen.ROI.class,
        double[].class,
        double[].class,
        double[].class
    };

    /** Input Parameter default values */
    private static final Object[] paramDefaults = {
        null, 0d, null, new double[] {0.0}, new double[] {255.0}, NO_PARAMETER_DEFAULT
    };

    /** Constructor. */
    public ThresholdDescriptor() {
        super(resources, 1, paramClasses, paramNames, paramDefaults);
    }

    protected boolean validateParameters(ParameterBlock args, StringBuffer msg) {
        if (!super.validateParameters(args, msg)) {
            return false;
        }

        double[] low = (double[]) args.getObjectParameter(3);
        double[] high = (double[]) args.getObjectParameter(4);
        double[] constant = (double[]) args.getObjectParameter(5);

        if (low.length < 1 || high.length < 1 || constant.length < 1) {
            msg.append(getName() + " wrong parameters number");
            return false;
        }

        // each "low" value must be less than or equal to the corresponding "high" value
        int length = Math.min(low.length, high.length);
        for (int i = 0; i < length; i++) {
            if (low[i] > high[i]) {
                msg.append(getName() + " wrong parameters");
                return false;
            }
        }

        // all arrays have the same number of elements that matches the number of bands of the source image
        // or all arrays contain 1 element
        int numBands = ((RenderedImage) args.getSource(0)).getSampleModel().getNumBands();
        if (!((numBands == low.length && numBands == high.length && numBands == constant.length)
                || (low.length == 1 && high.length == 1 && constant.length == 1))) {
            msg.append(getName() + " wrong parameters number");
            return false;
        }
        return true;
    }

    /**
     * Creates a <code>ParameterBlockJAI</code> from all supplied arguments except <code>hints</code> and invokes
     * {@link JAI#create(String,ParameterBlock,RenderingHints)}.
     *
     * @see JAI
     * @see ParameterBlockJAI
     * @see RenderedOp
     * @param noData Array of input No Data Ranges.
     * @param destinationNoData value used by the RenderedOp for setting the output no data value.
     * @param roi
     * @param low array of low values
     * @param high array of high values
     * @param constant array of constant values
     * @param hints The <code>RenderingHints</code> to use. May be <code>null</code>.
     * @param sources Array of source <code>RenderedImage</code>.
     * @return The <code>RenderedOp</code> destination.
     * @throws IllegalArgumentException if <code>sources</code> is <code>null</code>.
     * @throws IllegalArgumentException if a <code>source</code> is <code>null</code>.
     */
    public static RenderedOp create(
            Range noData,
            double destinationNoData,
            ROI roi,
            double[] low,
            double[] high,
            double[] constant,
            RenderingHints hints,
            RenderedImage sources) {
        // register();

        ParameterBlockJAI pb = new ParameterBlockJAI("Threshold", RenderedRegistryMode.MODE_NAME);
        if (sources == null) throw new IllegalArgumentException("This resource is null");

        // Setting of sources
        pb.setSource(sources, 0);

        // Setting of the parameters
        pb.setParameter("high", high);
        pb.setParameter("noData", noData);
        pb.setParameter("destinationNoData", destinationNoData);
        pb.setParameter("roi", roi);
        pb.setParameter("low", low);
        pb.setParameter("high", high);
        pb.setParameter("constant", constant);

        // Creation of the RenderedOp
        return JAI.create("Threshold", pb, hints);
    }
}
