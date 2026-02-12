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

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import org.eclipse.imagen.ImageN;
import org.eclipse.imagen.OperationDescriptorImpl;
import org.eclipse.imagen.ParameterBlockImageN;
import org.eclipse.imagen.RenderedOp;
import org.eclipse.imagen.registry.RenderedRegistryMode;

/**
 * An <code>OperationDescriptor</code> describing the "Pattern" operation.
 *
 * <p>The "Pattern" operation defines a tiled image consisting of a repeated pattern. The width and height of the
 * destination image must be specified. The tileWidth and tileHeight are equal to pattern's width and height. Each tile
 * of the destination image will be defined by a reference to a shared instance of the pattern.
 *
 * <p>
 *
 * <table border=1>
 * <caption>Resource List</caption>
 * <tr><th>Name</th>        <th>Value</th></tr>
 * <tr><td>GlobalName</td>  <td>pattern</td></tr>
 * <tr><td>LocalName</td>   <td>pattern</td></tr>
 * <tr><td>Vendor</td>      <td>org.eclipse.imagen.media</td></tr>
 * <tr><td>Description</td> <td>Defines an image with a repeated
 *                              pattern.</td></tr>
 * <tr><td>DocURL</td>      <td>http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/PatternDescriptor.html</td></tr>
 * <tr><td>Version</td>     <td>1.0</td></tr>
 * <tr><td>arg0Desc</td>    <td>The width of the image in pixels.</td></tr>
 * <tr><td>arg1Desc</td>    <td>The height of the image in pixels.</td></tr>
 * </table>
 *
 * <p>
 *
 * <table border=1>
 * <caption>Parameter List</caption>
 * <tr><th>Name</th>    <th>Class Type</th>
 *                      <th>Default Value</th></tr>
 * <tr><td>width</td>   <td>java.lang.Integer</td>
 *                      <td>NO_PARAMETER_DEFAULT</td>
 * <tr><td>height</td>  <td>java.lang.Integer</td>
 *                      <td>NO_PARAMETER_DEFAULT</td>
 * </table>
 *
 * @see org.eclipse.imagen.OperationDescriptor
 */
public class PatternDescriptor extends OperationDescriptorImpl {

    /** The resource strings that provide the general documentation for the "Pattern" operation. */
    private static final String[][] resources = {
        {"GlobalName", "Pattern"},
        {"LocalName", "Pattern"},
        {"Vendor", "org.eclipse.imagen.media"},
        {"Description", JaiI18N.getString("PatternDescriptor0")},
        {
            "DocURL",
            "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/PatternDescriptor.html"
        },
        {"Version", JaiI18N.getString("DescriptorVersion")},
        {"arg0Desc", JaiI18N.getString("PatternDescriptor1")},
        {"arg1Desc", JaiI18N.getString("PatternDescriptor2")}
    };

    /** The parameter class list for this operation. */
    private static final Class[] paramClasses = {
        java.lang.Integer.class, java.lang.Integer.class,
    };

    /** The parameter name list for this operation. */
    private static final String[] paramNames = {"width", "height"};

    /** The parameter default value list for this operation. */
    private static final Object[] paramDefaults = {NO_PARAMETER_DEFAULT, NO_PARAMETER_DEFAULT};

    /** Constructor. */
    public PatternDescriptor() {
        super(resources, 1, paramClasses, paramNames, paramDefaults);
    }

    public Number getParamMinValue(int index) {
        if (index == 0 || index == 1) {
            return new Integer(1);
        } else {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    /**
     * Defines an image with a repeated pattern.
     *
     * <p>Creates a <code>ParameterBlockImageN</code> from all supplied arguments except <code>hints</code> and invokes
     * {@link ImageN#create(String,ParameterBlock,RenderingHints)}.
     *
     * @see ImageN
     * @see ParameterBlockImageN
     * @see RenderedOp
     * @param source0 <code>RenderedImage</code> source 0.
     * @param width The width of the image in pixels.
     * @param height The height of the image in pixels.
     * @param hints The <code>RenderingHints</code> to use. May be <code>null</code>.
     * @return The <code>RenderedOp</code> destination.
     * @throws IllegalArgumentException if <code>source0</code> is <code>null</code>.
     * @throws IllegalArgumentException if <code>width</code> is <code>null</code>.
     * @throws IllegalArgumentException if <code>height</code> is <code>null</code>.
     */
    public static RenderedOp create(RenderedImage source0, Integer width, Integer height, RenderingHints hints) {
        ParameterBlockImageN pb = new ParameterBlockImageN("Pattern", RenderedRegistryMode.MODE_NAME);

        pb.setSource("source0", source0);

        pb.setParameter("width", width);
        pb.setParameter("height", height);

        return ImageN.create("Pattern", pb, hints);
    }
}
