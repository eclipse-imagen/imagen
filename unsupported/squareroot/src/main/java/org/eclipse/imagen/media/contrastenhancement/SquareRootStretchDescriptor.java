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
package org.eclipse.imagen.media.contrastenhancement;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderableImage;
import org.eclipse.imagen.ImageN;
import org.eclipse.imagen.OperationDescriptorImpl;
import org.eclipse.imagen.ParameterBlockImageN;
import org.eclipse.imagen.RenderableOp;
import org.eclipse.imagen.RenderedOp;
import org.eclipse.imagen.registry.RenderableRegistryMode;
import org.eclipse.imagen.registry.RenderedRegistryMode;

public class SquareRootStretchDescriptor extends OperationDescriptorImpl {

    /**
     * The resource strings that provide the general documentation and specify the parameter list for this operation.
     */
    private static final String[][] resources = {
        {"GlobalName", "SquareRootStretch"},
        {"LocalName", "SquareRootStretch"},
        {"Vendor", "org.eclipse.imagen.media"},
        {"Description", JaiI18N.getString("SquareRootStretchDescriptor0")},
        {"DocURL", ""},
        {"Version", JaiI18N.getString("DescriptorVersion")},
        {"arg0Desc", JaiI18N.getString("SquareRootStretchDescriptor1")},
        {"arg1Desc", JaiI18N.getString("SquareRootStretchDescriptor2")},
        {"arg2Desc", JaiI18N.getString("SquareRootStretchDescriptor3")},
        {"arg3Desc", JaiI18N.getString("SquareRootStretchDescriptor4")}
    };

    /**
     * The parameter class list for this operation. The number of constants provided should be either 1, in which case
     * this same constant is applied to all the source bands; or the same number as the source bands, in which case one
     * contant is applied to each band.
     */
    private static final Class[] paramClasses = {int[].class, int[].class, int[].class, int[].class};

    /** The parameter name list for this operation. */
    private static final String[] paramNames = {"inputMin", "inputMax", "outputMin", "outputMax"};

    /** The parameter default value list for this operation. */
    private static final Object[] paramDefaults = {new int[] {0}, new int[] {65536}, new int[] {0}, new int[] {255}};

    /**
     * Compute the SquareRoot stretch
     *
     * <p>Creates a <code>ParameterBlockImageN</code> from all supplied arguments except <code>hints</code> and invokes
     * {@link ImageN#create(String,ParameterBlock,RenderingHints)}.
     *
     * @see ImageN
     * @see ParameterBlockImageN
     * @see RenderedOp
     * @param source0 <code>RenderedImage</code> source 0.
     * @param hints The <code>RenderingHints</code> to use. May be <code>null</code>.
     * @return The <code>RenderedOp</code> destination.
     * @throws IllegalArgumentException if <code>source0</code> is <code>null</code>.
     */
    public static RenderedOp create(
            RenderedImage source0,
            int[] inputMin,
            int[] inputMax,
            int[] outputMin,
            int[] outputMax,
            RenderingHints hints) {
        ParameterBlockImageN pb = new ParameterBlockImageN("SquareRootStretch", RenderedRegistryMode.MODE_NAME);

        pb.setSource("source0", source0);

        pb.setParameter("inputMin", inputMin);
        pb.setParameter("inputMax", inputMax);
        pb.setParameter("outputMin", outputMin);
        pb.setParameter("outputMax", outputMax);

        return ImageN.create("SquareRootStretch", pb, hints);
    }

    /**
     * Compute the SquareRoot stretch
     *
     * <p>Creates a <code>ParameterBlockImageN</code> from all supplied arguments except <code>hints</code> and invokes
     * {@link ImageN#createRenderable(String,ParameterBlock,RenderingHints)}.
     *
     * @see ImageN
     * @see ParameterBlockImageN
     * @see RenderableOp
     * @param source0 <code>RenderableImage</code> source 0.
     * @param hints The <code>RenderingHints</code> to use. May be <code>null</code>.
     * @return The <code>RenderableOp</code> destination.
     * @throws IllegalArgumentException if <code>source0</code> is <code>null</code>.
     */
    public static RenderableOp createRenderable(
            RenderableImage source0,
            int[] inputMin,
            int[] inputMax,
            int[] outputMin,
            int[] outputMax,
            RenderingHints hints) {
        ParameterBlockImageN pb = new ParameterBlockImageN("SquareRootStretch", RenderableRegistryMode.MODE_NAME);

        pb.setSource("source0", source0);

        pb.setParameter("inputMin", inputMin);
        pb.setParameter("inputMax", inputMax);
        pb.setParameter("outputMin", outputMin);
        pb.setParameter("outputMax", outputMax);

        return ImageN.createRenderable("SquareRootStretch", pb, hints);
    }

    /** Constructor. */
    public SquareRootStretchDescriptor() {
        super(resources, 1, paramClasses, paramNames, paramDefaults);
    }

    /** Returns <code>true</code> since renderable operation is supported. */
    public boolean isRenderableSupported() {
        return true;
    }

    /**
     * Validates the input parameter.
     *
     * <p>In addition to the standard checks performed by the superclass method, this method checks that the length of
     * the provided parameters array is at least 1.
     */
    protected boolean validateParameters(ParameterBlock args, StringBuffer message) {
        if (!super.validateParameters(args, message)) {
            return false;
        }

        int length = ((int[]) args.getObjectParameter(0)).length;
        if (length < 1) {
            message.append(getName() + " " + "SquareRootStretchDescriptor5");

            return false;
        }

        length = ((int[]) args.getObjectParameter(1)).length;
        if (length < 1) {
            message.append(getName() + " " + "SquareRootStretchDescriptor5");

            return false;
        }

        length = ((int[]) args.getObjectParameter(2)).length;
        if (length < 1) {
            message.append(getName() + " " + "SquareRootStretchDescriptor5");

            return false;
        }

        length = ((int[]) args.getObjectParameter(3)).length;
        if (length < 1) {
            message.append(getName() + " " + "SquareRootStretchDescriptor5");

            return false;
        }

        return true;
    }
}
