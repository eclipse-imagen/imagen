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
import org.eclipse.imagen.LookupTableJAI;
import org.eclipse.imagen.OperationDescriptorImpl;
import org.eclipse.imagen.ParameterBlockImageN;
import org.eclipse.imagen.RenderedOp;
import org.eclipse.imagen.registry.RenderedRegistryMode;

/**
 * An <code>OperationDescriptor</code> describing the "ErrorDiffusion" operation.
 *
 * <p>The "ErrorDiffusion" operation performs color quantization by finding the nearest color to each pixel in a
 * supplied color map and "diffusing" the color quantization error below and to the right of the pixel.
 *
 * <p>
 *
 * <table border=1>
 * <caption>Resource List</caption>
 * <tr><th>Name</th>        <th>Value</th></tr>
 * <tr><td>GlobalName</td>  <td>ErrorDiffusion</td></tr>
 * <tr><td>LocalName</td>   <td>ErrorDiffusion</td></tr>
 * <tr><td>Vendor</td>      <td>org.eclipse.imagen.media</td></tr>
 * <tr><td>Description</td> <td>Performs error diffusion color quantization
 *                              using a specified color map and
 *                              error filter.</td></tr>
 * <tr><td>DocURL</td>      <td>http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/ErrorDiffusionDescriptor.html</td></tr>
 * <tr><td>Version</td>     <td>1.0</td></tr>
 * <tr><td>arg0Desc</td>    <td>The color map.</td></tr>
 * <tr><td>arg1Desc</td>    <td>The error filter kernel.</td></tr>
 * </table>
 *
 * <p>
 *
 * <table border=1>
 * <caption>Parameter List</caption>
 * <tr><th>Name</th>          <th>Class Type</th>
 *                            <th>Default Value</th></tr>
 * <tr><td>colorMap</td>          <td>org.eclipse.imagen.LookupTableJAI</td>
 *                            <td>NO_PARAMETER_DEFAULT</td>
 * <tr><td>errorKernel</td>   <td>org.eclipse.imagen.KernelJAI</td>
 *                            <td>org.eclipse.imagen.KernelJAI.ERROR_FILTER_FLOYD_STEINBERG</td>
 * </table>
 *
 * @see org.eclipse.imagen.LookupTableJAI
 * @see org.eclipse.imagen.KernelJAI
 * @see org.eclipse.imagen.ColorCube
 * @see org.eclipse.imagen.OperationDescriptor
 */
public class ErrorDiffusionDescriptor extends OperationDescriptorImpl {

    /**
     * The resource strings that provide the general documentation and specify the parameter list for the
     * "ErrorDiffusion" operation.
     */
    private static final String[][] resources = {
        {"GlobalName", "ErrorDiffusion"},
        {"LocalName", "ErrorDiffusion"},
        {"Vendor", "org.eclipse.imagen.media"},
        {"Description", JaiI18N.getString("ErrorDiffusionDescriptor0")},
        {
            "DocURL",
            "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/ErrorDiffusionDescriptor.html"
        },
        {"Version", JaiI18N.getString("DescriptorVersion")},
        {"arg0Desc", JaiI18N.getString("ErrorDiffusionDescriptor1")},
        {"arg1Desc", JaiI18N.getString("ErrorDiffusionDescriptor2")}
    };

    /** The parameter names for the "ErrorDiffusion" operation. */
    private static final String[] paramNames = {"colorMap", "errorKernel"};

    /** The parameter class types for the "ErrorDiffusion" operation. */
    private static final Class[] paramClasses = {
        org.eclipse.imagen.LookupTableJAI.class, org.eclipse.imagen.KernelJAI.class
    };

    /** The parameter default values for the "ErrorDiffusion" operation. */
    private static final Object[] paramDefaults = {
        NO_PARAMETER_DEFAULT,
        // Default error filter to Floyd-Steinberg.
        KernelJAI.ERROR_FILTER_FLOYD_STEINBERG
    };

    /** Constructor. */
    public ErrorDiffusionDescriptor() {
        super(resources, 1, paramClasses, paramNames, paramDefaults);
    }

    /**
     * Performs error diffusion color quantization using a specified color map and error filter.
     *
     * <p>Creates a <code>ParameterBlockImageN</code> from all supplied arguments except <code>hints</code> and invokes
     * {@link ImageN#create(String,ParameterBlock,RenderingHints)}.
     *
     * @see ImageN
     * @see ParameterBlockImageN
     * @see RenderedOp
     * @param source0 <code>RenderedImage</code> source 0.
     * @param colorMap The color map.
     * @param errorKernel The error filter kernel. May be <code>null</code>.
     * @param hints The <code>RenderingHints</code> to use. May be <code>null</code>.
     * @return The <code>RenderedOp</code> destination.
     * @throws IllegalArgumentException if <code>source0</code> is <code>null</code>.
     * @throws IllegalArgumentException if <code>colorMap</code> is <code>null</code>.
     */
    public static RenderedOp create(
            RenderedImage source0, LookupTableJAI colorMap, KernelJAI errorKernel, RenderingHints hints) {
        ParameterBlockImageN pb = new ParameterBlockImageN("ErrorDiffusion", RenderedRegistryMode.MODE_NAME);

        pb.setSource("source0", source0);

        pb.setParameter("colorMap", colorMap);
        pb.setParameter("errorKernel", errorKernel);

        return ImageN.create("ErrorDiffusion", pb, hints);
    }
}
