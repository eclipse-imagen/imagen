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

package org.eclipse.imagen.operator;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderableImage;
import org.eclipse.imagen.JAI;
import org.eclipse.imagen.OperationDescriptorImpl;
import org.eclipse.imagen.ParameterBlockJAI;
import org.eclipse.imagen.PropertyGenerator;
import org.eclipse.imagen.RenderableOp;
import org.eclipse.imagen.RenderedOp;
import org.eclipse.imagen.registry.RenderableRegistryMode;
import org.eclipse.imagen.registry.RenderedRegistryMode;

/**
 * An <code>OperationDescriptor</code> describing the "MagnitudeSquared" operation.
 *
 * <p>The "MagnitudeSquared" operation computes the squared magnitude or of each pixel of a complex image. The source
 * image must have an even number of bands, with the even bands (0, 2, ...) representing the real parts and the odd
 * bands (1, 3, ...) the imaginary parts of each complex pixel. The destination image has at most half the number of
 * bands of the source image with each sample in a pixel representing the magnitude of the corresponding complex source
 * sample. The magnitude squared values of the destination image are defined for a given sample by the pseudocode:
 *
 * <pre>dstPixel[x][y][b] = src[x][y][2*b]^2 + src[x][y][2*b + 1]^2</pre>
 *
 * where the number of bands <i>b</i> varies from zero to one less than the number of bands in the destination image.
 *
 * <p>For integral image datatypes, the result will be rounded and clamped as needed.
 *
 * <p>"MagnitudeSquared" defines a PropertyGenerator that sets the "COMPLEX" property of the image to <code>
 * java.lang.Boolean.FALSE</code>, which may be retrieved by calling the <code>getProperty()</code> method with
 * "COMPLEX" as the property name.
 *
 * <p>
 *
 * <table border=1>
 * <caption>Resource List</caption>
 * <tr><th>Name</th>        <th>Value</th></tr>
 * <tr><td>GlobalName</td>  <td>MagnitudeSquared</td></tr>
 * <tr><td>LocalName</td>   <td>MagnitudeSquared</td></tr>
 * <tr><td>Vendor</td>      <td>org.eclipse.imagen.media</td></tr>
 * <tr><td>Description</td> <td>Computes the squared magnitude of each
 *                              pixel of a complex image.</td></tr>
 * <tr><td>DocURL</td>      <td>http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/MagnitudeSquaredDescriptor.html</td></tr>
 * <tr><td>Version</td>     <td>1.0</td></tr>
 * </table>
 *
 * <p>No parameters are needed for the "MagnitudeSquared" operation.
 *
 * @see org.eclipse.imagen.OperationDescriptor
 */
public class MagnitudeSquaredDescriptor extends OperationDescriptorImpl {

    /**
     * The resource strings that provide the general documentation and specify the parameter list for this operation.
     */
    private static final String[][] resources = {
        {"GlobalName", "MagnitudeSquared"},
        {"LocalName", "MagnitudeSquared"},
        {"Vendor", "org.eclipse.imagen.media"},
        {"Description", JaiI18N.getString("MagnitudeSquaredDescriptor0")},
        {
            "DocURL",
            "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/MagnitudeSquaredDescriptor.html"
        },
        {"Version", JaiI18N.getString("DescriptorVersion")}
    };

    private static final String[] supportedModes = {"rendered", "renderable"};

    /** Constructor. */
    public MagnitudeSquaredDescriptor() {
        super(resources, supportedModes, 1, null, null, null, null);
    }

    /**
     * Returns an array of <code>PropertyGenerators</code> implementing property inheritance for the "MagnitudeSquared"
     * operation.
     *
     * @return An array of property generators.
     */
    public PropertyGenerator[] getPropertyGenerators(String modeName) {
        PropertyGenerator[] pg = new PropertyGenerator[1];
        pg[0] = new ComplexPropertyGenerator();
        return pg;
    }

    /**
     * Validates the input source.
     *
     * <p>In addition to the standard checks performed by the superclass method, this method checks that the source
     * image has an even number of bands.
     */
    protected boolean validateSources(String modeName, ParameterBlock args, StringBuffer msg) {
        if (!super.validateSources(modeName, args, msg)) {
            return false;
        }

        if (!modeName.equalsIgnoreCase("rendered")) return true;

        RenderedImage src = args.getRenderedSource(0);

        int bands = src.getSampleModel().getNumBands();

        if (bands % 2 != 0) {
            msg.append(getName() + " " + JaiI18N.getString("MagnitudeSquaredDescriptor1"));
            return false;
        }

        return true;
    }

    /**
     * Computes the squared magnitude of each pixel of a complex image.
     *
     * <p>Creates a <code>ParameterBlockJAI</code> from all supplied arguments except <code>hints</code> and invokes
     * {@link JAI#create(String,ParameterBlock,RenderingHints)}.
     *
     * @see JAI
     * @see ParameterBlockJAI
     * @see RenderedOp
     * @param source0 <code>RenderedImage</code> source 0.
     * @param hints The <code>RenderingHints</code> to use. May be <code>null</code>.
     * @return The <code>RenderedOp</code> destination.
     * @throws IllegalArgumentException if <code>source0</code> is <code>null</code>.
     */
    public static RenderedOp create(RenderedImage source0, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("MagnitudeSquared", RenderedRegistryMode.MODE_NAME);

        pb.setSource("source0", source0);

        return JAI.create("MagnitudeSquared", pb, hints);
    }

    /**
     * Computes the squared magnitude of each pixel of a complex image.
     *
     * <p>Creates a <code>ParameterBlockJAI</code> from all supplied arguments except <code>hints</code> and invokes
     * {@link JAI#createRenderable(String,ParameterBlock,RenderingHints)}.
     *
     * @see JAI
     * @see ParameterBlockJAI
     * @see RenderableOp
     * @param source0 <code>RenderableImage</code> source 0.
     * @param hints The <code>RenderingHints</code> to use. May be <code>null</code>.
     * @return The <code>RenderableOp</code> destination.
     * @throws IllegalArgumentException if <code>source0</code> is <code>null</code>.
     */
    public static RenderableOp createRenderable(RenderableImage source0, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("MagnitudeSquared", RenderableRegistryMode.MODE_NAME);

        pb.setSource("source0", source0);

        return JAI.createRenderable("MagnitudeSquared", pb, hints);
    }
}
