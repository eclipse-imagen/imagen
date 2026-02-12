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
import java.awt.image.renderable.RenderableImage;
import org.eclipse.imagen.ImageN;
import org.eclipse.imagen.Interpolation;
import org.eclipse.imagen.OperationDescriptorImpl;
import org.eclipse.imagen.ParameterBlockImageN;
import org.eclipse.imagen.PlanarImage;
import org.eclipse.imagen.PropertyGenerator;
import org.eclipse.imagen.ROI;
import org.eclipse.imagen.RenderableOp;
import org.eclipse.imagen.RenderedOp;
import org.eclipse.imagen.media.util.PropertyGeneratorImpl;
import org.eclipse.imagen.registry.RenderableRegistryMode;
import org.eclipse.imagen.registry.RenderedRegistryMode;

/** This property generator computes the properties for the operation "Transpose" dynamically. */
class TransposePropertyGenerator extends PropertyGeneratorImpl {

    /** Constructor. */
    public TransposePropertyGenerator() {
        super(new String[] {"ROI"}, new Class[] {ROI.class}, new Class[] {RenderedOp.class});
    }

    /**
     * Returns the specified property.
     *
     * @param name Property name.
     * @param opNode Operation node.
     */
    public Object getProperty(String name, Object opNode) {
        validate(name, opNode);

        if (opNode instanceof RenderedOp && name.equalsIgnoreCase("roi")) {
            RenderedOp op = (RenderedOp) opNode;

            ParameterBlock pb = op.getParameterBlock();

            // Retrieve the rendered source image and its ROI.
            PlanarImage src = (PlanarImage) pb.getRenderedSource(0);
            Object property = src.getProperty("ROI");
            if (property == null || property.equals(java.awt.Image.UndefinedProperty) || !(property instanceof ROI)) {
                return java.awt.Image.UndefinedProperty;
            }

            // Return undefined also if source ROI is empty.
            ROI srcROI = (ROI) property;
            if (srcROI.getBounds().isEmpty()) {
                return java.awt.Image.UndefinedProperty;
            }

            /// This should really create a proper AffineTransform
            /// and transform the ROI with it to avoid forcing
            /// ROI.getAsImage to be called.

            // Retrieve the transpose type and create a nearest neighbor
            // Interpolation object.
            TransposeType transposeType = (TransposeType) pb.getObjectParameter(0);
            Interpolation interp = Interpolation.getInstance(Interpolation.INTERP_NEAREST);

            // Return the transposed ROI.
            return new ROI(ImageN.create("transpose", srcROI.getAsImage(), transposeType));
        }

        return java.awt.Image.UndefinedProperty;
    }
}

/**
 * An <code>OperationDescriptor</code> describing the "Transpose" operation.
 *
 * <p>The "Transpose" operation performs the following operations:
 *
 * <ul>
 *   <li>Flip an image across an imaginary horizontal line that runs through the center of the image (FLIP_VERTICAL).
 *   <li>Flip an image across an imaginary vertical line that runs through the center of the image (FLIP_HORIZONTAL).
 *   <li>Flip an image across its main diagonal that runs from the upper left to the lower right corner (FLIP_DIAGONAL).
 *   <li>Flip an image across its main antidiagonal that runs from the upper right to the lower left
 *       corner(FLIP_ANTIDIAGONAL).
 *   <li>Rotate an image clockwise by 90, 180, or 270 degrees (ROTATE_90, ROTATE_180, ROTATE_270).
 * </ul>
 *
 * <p>In all cases, the resulting image will have the same origin (as defined by the return values of its <code>
 * getMinX()</code> and <code>getMinY()</code> methods) as the source image.
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
 * <p>"Transpose" defines a PropertyGenerator that performs an identical transformation on the "ROI" property of the
 * source image, which can be retrieved by calling the <code>getProperty</code> method with "ROI" as the property name.
 *
 * <p>
 *
 * <table border=1>
 * <caption>Resource List</caption>
 * <tr><th>Name</th>        <th>Value</th></tr>
 * <tr><td>GlobalName</td>  <td>transpose</td></tr>
 * <tr><td>LocalName</td>   <td>transpose</td></tr>
 * <tr><td>Vendor</td>      <td>org.eclipse.imagen.media</td></tr>
 * <tr><td>Description</td> <td>Reflects an image in a specified direction
 *                              or rotates an image in multiples of 90
 *                              degrees.</td></tr>
 * <tr><td>DocURL</td>      <td>http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/TransposeDescriptor.html</td></tr>
 * <tr><td>Version</td>     <td>1.0</td></tr>
 * <tr><td>arg0Desc</td>    <td>The type of flip operation
 *                              to be performed.</td></tr>
 * </table>
 *
 * <p>
 *
 * <table border=1>
 * <caption>Parameter List</caption>
 * <tr><th>Name</th> <th>Class Type</th>
 *                   <th>Default Value</th></tr>
 * <tr><td>type</td> <td>org.eclipse.imagen.operator.TransposeType</td>
 *                   <td>NO_PARAMETER_DEFAULT</td>
 * </table>
 *
 * @see org.eclipse.imagen.OperationDescriptor
 * @see TransposeType
 */
public class TransposeDescriptor extends OperationDescriptorImpl {

    public static final TransposeType FLIP_VERTICAL = new TransposeType("FLIP_VERTICAL", 0);
    public static final TransposeType FLIP_HORIZONTAL = new TransposeType("FLIP_HORIZONTAL", 1);
    public static final TransposeType FLIP_DIAGONAL = new TransposeType("FLIP_DIAGONAL", 2);
    public static final TransposeType FLIP_ANTIDIAGONAL = new TransposeType("FLIP_ANTIDIAGONAL", 3);
    public static final TransposeType ROTATE_90 = new TransposeType("ROTATE_90", 4);
    public static final TransposeType ROTATE_180 = new TransposeType("ROTATE_180", 5);
    public static final TransposeType ROTATE_270 = new TransposeType("ROTATE_270", 6);

    /**
     * The resource strings that provide the general documentation and specify the parameter list for this operation.
     */
    private static final String[][] resources = {
        {"GlobalName", "Transpose"},
        {"LocalName", "Transpose"},
        {"Vendor", "org.eclipse.imagen.media"},
        {"Description", JaiI18N.getString("TransposeDescriptor0")},
        {
            "DocURL",
            "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/TransposeDescriptor.html"
        },
        {"Version", JaiI18N.getString("DescriptorVersion2")},
        {"arg0Desc", JaiI18N.getString("TransposeDescriptor1")}
    };

    /** The parameter class list for this operation. */
    private static final Class[] paramClasses = {TransposeType.class};

    /** The parameter name list for this operation. */
    private static final String[] paramNames = {"type"};

    /** The parameter default value list for this operation. */
    private static final Object[] paramDefaults = {NO_PARAMETER_DEFAULT};

    /** Constructor. */
    public TransposeDescriptor() {
        super(resources, 1, paramClasses, paramNames, paramDefaults);
    }

    /** Returns <code>true</code> since renderable operation is supported. */
    public boolean isRenderableSupported() {
        return true;
    }

    /**
     * Returns an array of <code>PropertyGenerators</code> implementing property inheritance for the "Transpose"
     * operation.
     *
     * @return An array of property generators.
     */
    public PropertyGenerator[] getPropertyGenerators() {
        PropertyGenerator[] pg = new PropertyGenerator[1];
        pg[0] = new TransposePropertyGenerator();
        return pg;
    }

    /**
     * Reflects an image in a specified direction or rotates an image in multiples of 90 degrees.
     *
     * <p>Creates a <code>ParameterBlockImageN</code> from all supplied arguments except <code>hints</code> and invokes
     * {@link ImageN#create(String,ParameterBlock,RenderingHints)}.
     *
     * @see ImageN
     * @see ParameterBlockImageN
     * @see RenderedOp
     * @param source0 <code>RenderedImage</code> source 0.
     * @param type The The type of flip operation to be performed.
     * @param hints The <code>RenderingHints</code> to use. May be <code>null</code>.
     * @return The <code>RenderedOp</code> destination.
     * @throws IllegalArgumentException if <code>source0</code> is <code>null</code>.
     * @throws IllegalArgumentException if <code>type</code> is <code>null</code>.
     */
    public static RenderedOp create(RenderedImage source0, TransposeType type, RenderingHints hints) {
        ParameterBlockImageN pb = new ParameterBlockImageN("Transpose", RenderedRegistryMode.MODE_NAME);

        pb.setSource("source0", source0);

        pb.setParameter("type", type);

        return ImageN.create("Transpose", pb, hints);
    }

    /**
     * Reflects an image in a specified direction or rotates an image in multiples of 90 degrees.
     *
     * <p>Creates a <code>ParameterBlockImageN</code> from all supplied arguments except <code>hints</code> and invokes
     * {@link ImageN#createRenderable(String,ParameterBlock,RenderingHints)}.
     *
     * @see ImageN
     * @see ParameterBlockImageN
     * @see RenderableOp
     * @param source0 <code>RenderableImage</code> source 0.
     * @param type The The type of flip operation to be performed.
     * @param hints The <code>RenderingHints</code> to use. May be <code>null</code>.
     * @return The <code>RenderableOp</code> destination.
     * @throws IllegalArgumentException if <code>source0</code> is <code>null</code>.
     * @throws IllegalArgumentException if <code>type</code> is <code>null</code>.
     */
    public static RenderableOp createRenderable(RenderableImage source0, TransposeType type, RenderingHints hints) {
        ParameterBlockImageN pb = new ParameterBlockImageN("Transpose", RenderableRegistryMode.MODE_NAME);

        pb.setSource("source0", source0);

        pb.setParameter("type", type);

        return ImageN.createRenderable("Transpose", pb, hints);
    }
}
