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
import org.eclipse.imagen.ImageN;
import org.eclipse.imagen.KernelJAI;
import org.eclipse.imagen.OperationDescriptorImpl;
import org.eclipse.imagen.ParameterBlockImageN;
import org.eclipse.imagen.PropertyGenerator;
import org.eclipse.imagen.RenderedOp;
import org.eclipse.imagen.media.util.AreaOpPropertyGenerator;
import org.eclipse.imagen.registry.RenderedRegistryMode;

/**
 * An <code>OperationDescriptor</code> describing the "GradientMagnitude" operation.
 *
 * <p>The "GradientMagnitude" operation is an edge detector which computes the magnitude of the image gradient vector in
 * two orthogonal directions.
 *
 * <p>The result of the "GradientMagnitude" operation may be defined as:
 *
 * <pre>
 * dst[x][y][b] = ((SH(x,y,b))^2 + (SV(x,y,b))^2 )^0.5
 * </pre>
 *
 * where SH(x,y,b) and SV(x,y,b) are the horizontal and vertical gradient images generated from band <i>b</i> of the
 * source image by correlating it with the supplied orthogonal (horizontal and vertical) gradient masks.
 *
 * <p>Origins set on the kernels will be ignored. The origins are assumed to be width/2 & height/2.
 *
 * <p>It should be noted that this operation automatically adds a value of <code>Boolean.TRUE</code> for the <code>
 * ImageN.KEY_REPLACE_INDEX_COLOR_MODEL</code> to the given <code>configuration</code> so that the operation is
 * performed on the pixel values instead of being performed on the indices into the color map if the source(s) have an
 * <code>
 * IndexColorModel</code>. This addition will take place only if a value for the <code>
 * ImageN.KEY_REPLACE_INDEX_COLOR_MODEL
 * </code> has not already been provided by the user. Note that the <code>configuration</code> Map is cloned before the
 * new hint is added to it. The operation can be smart about the value of the <code>ImageN.KEY_REPLACE_INDEX_COLOR_MODEL
 * </code> <code>RenderingHints</code>, i.e. while the default value for the <code>ImageN.KEY_REPLACE_INDEX_COLOR_MODEL
 * </code> is <code>Boolean.TRUE</code>, in some cases the operator could set the default.
 *
 * <p>
 *
 * <table align=center border=1>
 * <caption>Resource List</caption>
 * <tr><th>Name</th>        <th>Value</th></tr>
 * <tr><td>GlobalName</td>  <td>GradientMagnitude</td></tr>
 * <tr><td>LocallName</td>  <td>GradientMagnitude</td></tr>
 * <tr><td>Vendor</td>      <td>org.eclipse.imagen.media</td></tr>
 * <tr><td>Description</td> <td>Performs gradient magnitude edge detection
 *                              on an image.</td></tr>
 * <tr><td>DocURL</td>      <td>http://java.sun.com/products/java-media/jai/forDevelopers/jaiapi/org.eclipse.imagen.operator.GradientMagnitudeDescriptor.html</td></tr>
 * <tr><td>Version</td>     <td>1.0</td></tr>
 * <tr><td>arg0Desc</td>    <td>A gradient mask</td></tr>
 * <tr><td>arg1Desc</td>    <td>A gradient mask orthogonal to the first one.</td></tr>
 * </table>
 *
 * <p>
 *
 * <table border=1>
 * <caption>Parameter List</caption>
 * <tr><th>Name</th>   <th>Class Type</th>
 *                     <th>Default Value</th></tr>
 * <tr><td>mask1</td>  <td>org.eclipse.imagen.KernelJAI</td>
 *                     <td>KernalJAI.GRADIENT_MASK_SOBEL_HORIZONTAL</td>
 * <tr><td>mask2</td>  <td>org.eclipse.imagen.KernelJAI</td>
 *                     <td>KernalJAI.GRADIENT_MASK_SOBEL_VERTICAL</td>
 * </table>
 *
 * @see org.eclipse.imagen.OperationDescriptor
 * @see org.eclipse.imagen.KernelJAI
 */
public class GradientMagnitudeDescriptor extends OperationDescriptorImpl {

    /**
     * The resource strings that provide the general documentation and specify the parameter list for the
     * GradientMagnitude operation.
     */
    private static final String[][] resources = {
        {"GlobalName", "GradientMagnitude"},
        {"LocalName", "GradientMagnitude"},
        {"Vendor", "org.eclipse.imagen.media"},
        {"Description", JaiI18N.getString("GradientMagnitudeDescriptor0")},
        {
            "DocURL",
            "http://java.sun.com/products/java-media/jai/forDevelopers/jaiapi/org.eclipse.imagen.operator.GradientMagnitudeDescriptor.html"
        },
        {"Version", JaiI18N.getString("DescriptorVersion")},
        {"arg0Desc", "A gradient mask."},
        {"arg1Desc", "A gradient mask orthogonal to the first one."}
    };

    /** The parameter names for the GradientMagnitude operation. */
    private static final String[] paramNames = {"mask1", "mask2"};

    /** The parameter class types for the GradientMagnitude operation. */
    private static final Class[] paramClasses = {org.eclipse.imagen.KernelJAI.class, org.eclipse.imagen.KernelJAI.class
    };

    /** The parameter default values for the GradientMagnitude operation. */
    private static final Object[] paramDefaults = {
        KernelJAI.GRADIENT_MASK_SOBEL_HORIZONTAL, KernelJAI.GRADIENT_MASK_SOBEL_VERTICAL
    };

    /** Constructor for the GradientMagnitudeDescriptor. */
    public GradientMagnitudeDescriptor() {
        super(resources, 1, paramClasses, paramNames, paramDefaults);
    }

    /**
     * Validates the input parameters.
     *
     * <p>In addition to the standard checks performed by the superclass method, this method checks that "mask1" and
     * "mask2" have the same dimensions.
     */
    protected boolean validateParameters(ParameterBlock args, StringBuffer msg) {
        if (!super.validateParameters(args, msg)) {
            return false;
        }

        KernelJAI h_kernel = (KernelJAI) args.getObjectParameter(0);
        KernelJAI v_kernel = (KernelJAI) args.getObjectParameter(1);

        /* Check if both kernels are equivalent in terms of dimensions. */
        if ((h_kernel.getWidth() != v_kernel.getWidth()) || (h_kernel.getHeight() != v_kernel.getHeight())) {
            msg.append(getName() + " " + JaiI18N.getString("GradientMagnitudeDescriptor1"));
            return false;
        }

        return true;
    }

    /**
     * Returns an array of <code>PropertyGenerators</code> implementing property inheritance for the "GradientMagnitude"
     * operation.
     *
     * @return An array of property generators.
     */
    public PropertyGenerator[] getPropertyGenerators() {
        PropertyGenerator[] pg = new PropertyGenerator[1];
        pg[0] = new AreaOpPropertyGenerator();
        return pg;
    }

    /**
     * Computes the gradient of an image
     *
     * <p>Creates a <code>ParameterBlockImageN</code> from all supplied arguments except <code>hints</code> and invokes
     * {@link ImageN#create(String,ParameterBlock,RenderingHints)}.
     *
     * @see ImageN
     * @see ParameterBlockImageN
     * @see RenderedOp
     * @param source0 <code>RenderedImage</code> source 0.
     * @param mask1 A gradient mask. May be <code>null</code>.
     * @param mask2 A gradient mask orthogonal to the first one. May be <code>null</code>.
     * @param hints The <code>RenderingHints</code> to use. May be <code>null</code>.
     * @return The <code>RenderedOp</code> destination.
     * @throws IllegalArgumentException if <code>source0</code> is <code>null</code>.
     */
    public static RenderedOp create(RenderedImage source0, KernelJAI mask1, KernelJAI mask2, RenderingHints hints) {
        ParameterBlockImageN pb = new ParameterBlockImageN("GradientMagnitude", RenderedRegistryMode.MODE_NAME);

        pb.setSource("source0", source0);

        pb.setParameter("mask1", mask1);
        pb.setParameter("mask2", mask2);

        return ImageN.create("GradientMagnitude", pb, hints);
    }
}
