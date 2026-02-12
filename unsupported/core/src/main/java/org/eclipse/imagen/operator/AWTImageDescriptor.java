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
 * SPDX-License-Identifier: Apache-2.0
 */
package org.eclipse.imagen.operator;

import java.awt.Image;
import java.awt.RenderingHints;
import org.eclipse.imagen.ImageN;
import org.eclipse.imagen.OperationDescriptorImpl;
import org.eclipse.imagen.ParameterBlockImageN;
import org.eclipse.imagen.RenderedOp;
import org.eclipse.imagen.registry.RenderedRegistryMode;

/**
 * An <code>OperationDescriptor</code> describing the "AWTImage" operation.
 *
 * <p>The AWTImage operation converts a standard <code>java.awt.Image</code> into a rendered image. By default, the
 * width and height of the image are the same as the original AWT image. The sample model and color model are set
 * according to the AWT image data.
 *
 * <p>
 *
 * <table border=1>
 * <caption>Resource List</caption>
 * <tr><th>Name</th>        <th>Value</th></tr>
 * <tr><td>GlobalName</td>  <td>AWTImage</td></tr>
 * <tr><td>LocalName</td>   <td>AWTImage</td></tr>
 * <tr><td>Vendor</td>      <td>org.eclipse.imagen.media</td></tr>
 * <tr><td>Description</td> <td>Converts a <code>java.awt.Image</code>
 *                              into a rendered image.</td></tr>
 * <tr><td>DocURL</td>      <td>http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/AWTImageDescriptor.html</td></tr>
 * <tr><td>Version</td>     <td>1.0</td></tr>
 * <tr><td>arg0Desc</td>    <td>The AWT image to be converted.</td></tr>
 * </table>
 *
 * <p>
 *
 * <table border=1>
 * <caption>Parameter List</caption>
 * <tr><th>Name</th>     <th>Class Type</th>
 *                       <th>Default Value</th></tr>
 * <tr><td>awtImage</td> <td>java.awt.Image</td>
 *                       <td>NO_PARAMETER_DEFAULT</td>
 * </table>
 *
 * @see java.awt.Image
 * @see org.eclipse.imagen.OperationDescriptor
 */
public class AWTImageDescriptor extends OperationDescriptorImpl {

    /**
     * The resource strings that provide the general documentation and specify the parameter list for this operation.
     */
    private static final String[][] resources = {
        {"GlobalName", "AWTImage"},
        {"LocalName", "AWTImage"},
        {"Vendor", "org.eclipse.imagen.media"},
        {"Description", JaiI18N.getString("AWTImageDescriptor0")},
        {
            "DocURL",
            "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/AWTImageDescriptor.html"
        },
        {"Version", JaiI18N.getString("DescriptorVersion")},
        {"arg0Desc", JaiI18N.getString("AWTImageDescriptor1")}
    };

    /** The parameter class list for this operation. */
    private static final Class[] paramClasses = {java.awt.Image.class};

    /** The parameter name list for this operation. */
    private static final String[] paramNames = {"awtImage"};

    /** The parameter default value list for this operation. */
    private static final Object[] paramDefaults = {NO_PARAMETER_DEFAULT};

    /** Constructor. */
    public AWTImageDescriptor() {
        super(resources, 0, paramClasses, paramNames, paramDefaults);
    }

    /**
     * Converts a java.awt.Image into a rendered image.
     *
     * <p>Creates a <code>ParameterBlockImageN</code> from all supplied arguments except <code>hints</code> and invokes
     * {@link ImageN#create(String,ParameterBlock,RenderingHints)}.
     *
     * @see ImageN
     * @see ParameterBlockImageN
     * @see RenderedOp
     * @param awtImage The AWT image to be converted.
     * @param hints The <code>RenderingHints</code> to use. May be <code>null</code>.
     * @return The <code>RenderedOp</code> destination.
     * @throws IllegalArgumentException if <code>awtImage</code> is <code>null</code>.
     */
    public static RenderedOp create(Image awtImage, RenderingHints hints) {
        ParameterBlockImageN pb = new ParameterBlockImageN("AWTImage", RenderedRegistryMode.MODE_NAME);

        pb.setParameter("awtImage", awtImage);

        return ImageN.create("AWTImage", pb, hints);
    }
}
