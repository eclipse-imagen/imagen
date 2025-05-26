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
import java.awt.image.renderable.ParameterBlock;
import org.eclipse.imagen.JAI;
import org.eclipse.imagen.OperationDescriptorImpl;
import org.eclipse.imagen.ParameterBlockJAI;
import org.eclipse.imagen.RenderableOp;
import org.eclipse.imagen.RenderedOp;
import org.eclipse.imagen.registry.RenderableRegistryMode;
import org.eclipse.imagen.registry.RenderedRegistryMode;
import org.eclipse.imagen.util.Range;

/**
 * An <code>OperationDescriptor</code> describing the "Constant" operation.
 *
 * <p>The Constant operation creates a multi-banded, tiled rendered image, where all the pixels from the same band have
 * a constant value. The width and height of the image must be specified and greater than 0. At least one constant must
 * be supplied. The number of bands of the image is determined by the number of constant pixel values supplied in the
 * "bandValues" parameter. The data type is determined by the type of the constants; this means all elements of the
 * <code>bandValues</code> array must be of the same type.
 *
 * <p>If the <code>bandValues</code> array is a <code>Short</code> array, then <code>TYPE_USHORT</code> is used if all
 * values are non-negative; otherwise <code>TYPE_SHORT</code> is used.
 *
 * <p>
 *
 * <table border=1>
 * <caption>Resource List</caption>
 * <tr><th>Name</th>        <th>Value</th></tr>
 * <tr><td>GlobalName</td>  <td>Constant</td></tr>
 * <tr><td>LocalName</td>   <td>Constant</td></tr>
 * <tr><td>Vendor</td>      <td>org.eclipse.imagen.media</td></tr>
 * <tr><td>Description</td> <td>Creates an image with
 *                              constant pixel values.</td></tr>
 * <tr><td>DocURL</td>      <td>http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/ConstantDescriptor.html</td></tr>
 * <tr><td>Version</td>     <td>1.0</td></tr>
 * <tr><td>arg0Desc</td>    <td>Image width in pixels.</td></tr>
 * <tr><td>arg1Desc</td>    <td>Image height in pixels.</td></tr>
 * <tr><td>arg2Desc</td>    <td>The constant pixel band values.</td></tr>
 * </table>
 *
 * <p>
 *
 * <table border=1>
 * <caption>Parameter List</caption>
 * <tr><th>Name</th>       <th>Class Type</th>
 *                         <th>Default Value</th></tr>
 * <tr><td>width</td>      <td>java.lang.Float</td>
 *                         <td>NO_PARAMETER_DEFAULT</td>
 * <tr><td>height</td>     <td>java.lang.Float</td>
 *                         <td>NO_PARAMETER_DEFAULT</td>
 * <tr><td>bandValues</td> <td>java.lang.Number[]</td>
 *                         <td>NO_PARAMETER_DEFAULT</td>
 * </table>
 *
 * @see org.eclipse.imagen.OperationDescriptor
 */
public class ConstantDescriptor extends OperationDescriptorImpl {

    /**
     * The resource strings that provide the general documentation and specify the parameter list for this operation.
     */
    private static final String[][] resources = {
        {"GlobalName", "Constant"},
        {"LocalName", "Constant"},
        {"Vendor", "org.eclipse.imagen.media"},
        {"Description", JaiI18N.getString("ConstantDescriptor0")},
        {
            "DocURL",
            "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/ConstantDescriptor.html"
        },
        {"Version", JaiI18N.getString("DescriptorVersion")},
        {"arg0Desc", JaiI18N.getString("ConstantDescriptor1")},
        {"arg1Desc", JaiI18N.getString("ConstantDescriptor2")},
        {"arg2Desc", JaiI18N.getString("ConstantDescriptor3")}
    };

    /** The parameter class list for this operation. */
    private static final Class[] paramClasses = {java.lang.Float.class, java.lang.Float.class, java.lang.Number[].class
    };

    /** The parameter name list for this operation. */
    private static final String[] paramNames = {"width", "height", "bandValues"};

    /** The parameter default value list for this operation. */
    private static final Object[] paramDefaults = {NO_PARAMETER_DEFAULT, NO_PARAMETER_DEFAULT, NO_PARAMETER_DEFAULT};

    private static final String[] supportedModes = {"rendered", "renderable"};

    private static final Object[] validParamValues = {
        new Range(Float.class, new Float(0.0F), false, null, false),
        new Range(Float.class, new Float(0.0F), false, null, false),
        null
    };

    /** Constructor. */
    public ConstantDescriptor() {
        super(resources, supportedModes, 0, paramNames, paramClasses, paramDefaults, validParamValues);
    }

    /**
     * Validates the input parameters.
     *
     * <p>In addition to the standard checks performed by the superclass method, this method checks that "width" and
     * "height" are greater than 0 (for rendered mode) and that "bandValues" has length at least 1.
     */
    protected boolean validateParameters(String modeName, ParameterBlock args, StringBuffer message) {
        if (!super.validateParameters(modeName, args, message)) {
            return false;
        }

        int length = ((Number[]) args.getObjectParameter(2)).length;
        if (length < 1) {
            message.append(getName() + " " + JaiI18N.getString("ConstantDescriptor4"));
            return false;
        }

        if (modeName.equalsIgnoreCase("rendered")) {
            int width = Math.round(args.getFloatParameter(0));
            int height = Math.round(args.getFloatParameter(1));

            if ((width < 1) || (height < 1)) {
                message.append(getName() + " " + JaiI18N.getString("ConstantDescriptor5"));
                return false;
            }
        } else if (modeName.equalsIgnoreCase("renderable")) {
            float width = args.getFloatParameter(0);
            float height = args.getFloatParameter(1);

            if ((width <= 0.0F) || (height <= 0.0F)) {
                message.append(getName() + " " + JaiI18N.getString("ConstantDescriptor6"));
                return false;
            }
        }

        return true;
    }

    /**
     * Creates an image with constant pixel values.
     *
     * <p>Creates a <code>ParameterBlockJAI</code> from all supplied arguments except <code>hints</code> and invokes
     * {@link JAI#create(String,ParameterBlock,RenderingHints)}.
     *
     * @see JAI
     * @see ParameterBlockJAI
     * @see RenderedOp
     * @param width Image width in pixels.
     * @param height Image height in pixels.
     * @param bandValues The constant pixel band values.
     * @param hints The <code>RenderingHints</code> to use. May be <code>null</code>.
     * @return The <code>RenderedOp</code> destination.
     * @throws IllegalArgumentException if <code>width</code> is <code>null</code>.
     * @throws IllegalArgumentException if <code>height</code> is <code>null</code>.
     * @throws IllegalArgumentException if <code>bandValues</code> is <code>null</code>.
     */
    public static RenderedOp create(Float width, Float height, Number[] bandValues, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("Constant", RenderedRegistryMode.MODE_NAME);

        pb.setParameter("width", width);
        pb.setParameter("height", height);
        pb.setParameter("bandValues", bandValues);

        return JAI.create("Constant", pb, hints);
    }

    /**
     * Creates an image with constant pixel values.
     *
     * <p>Creates a <code>ParameterBlockJAI</code> from all supplied arguments except <code>hints</code> and invokes
     * {@link JAI#createRenderable(String,ParameterBlock,RenderingHints)}.
     *
     * @see JAI
     * @see ParameterBlockJAI
     * @see RenderableOp
     * @param width Image width in pixels.
     * @param height Image height in pixels.
     * @param bandValues The constant pixel band values.
     * @param hints The <code>RenderingHints</code> to use. May be <code>null</code>.
     * @return The <code>RenderableOp</code> destination.
     * @throws IllegalArgumentException if <code>width</code> is <code>null</code>.
     * @throws IllegalArgumentException if <code>height</code> is <code>null</code>.
     * @throws IllegalArgumentException if <code>bandValues</code> is <code>null</code>.
     */
    public static RenderableOp createRenderable(Float width, Float height, Number[] bandValues, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("Constant", RenderableRegistryMode.MODE_NAME);

        pb.setParameter("width", width);
        pb.setParameter("height", height);
        pb.setParameter("bandValues", bandValues);

        return JAI.createRenderable("Constant", pb, hints);
    }
}
