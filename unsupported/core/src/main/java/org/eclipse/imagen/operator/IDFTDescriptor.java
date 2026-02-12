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
import org.eclipse.imagen.EnumeratedParameter;
import org.eclipse.imagen.ImageN;
import org.eclipse.imagen.OperationDescriptorImpl;
import org.eclipse.imagen.ParameterBlockImageN;
import org.eclipse.imagen.PropertyGenerator;
import org.eclipse.imagen.RenderableOp;
import org.eclipse.imagen.RenderedOp;
import org.eclipse.imagen.media.util.PropertyGeneratorImpl;
import org.eclipse.imagen.registry.RenderableRegistryMode;
import org.eclipse.imagen.registry.RenderedRegistryMode;

/** This property generator computes the properties for the operation "IDFT" dynamically. */
class IDFTPropertyGenerator extends PropertyGeneratorImpl {

    /** Constructor. */
    public IDFTPropertyGenerator() {
        super(new String[] {"COMPLEX"}, new Class[] {Boolean.class}, new Class[] {RenderedOp.class, RenderableOp.class
        });
    }

    /**
     * Returns the specified property.
     *
     * @param name Property name.
     * @param opNode Operation node.
     */
    public Object getProperty(String name, Object opNode) {
        validate(name, opNode);

        if (name.equalsIgnoreCase("complex")) {
            if (opNode instanceof RenderedOp) {
                RenderedOp op = (RenderedOp) opNode;
                ParameterBlock pb = op.getParameterBlock();
                DFTDataNature dataNature = (DFTDataNature) pb.getObjectParameter(1);
                return dataNature.equals(IDFTDescriptor.COMPLEX_TO_REAL) ? Boolean.FALSE : Boolean.TRUE;
            } else if (opNode instanceof RenderableOp) {
                RenderableOp op = (RenderableOp) opNode;
                ParameterBlock pb = op.getParameterBlock();
                DFTDataNature dataNature = (DFTDataNature) pb.getObjectParameter(1);
                return dataNature.equals(IDFTDescriptor.COMPLEX_TO_REAL) ? Boolean.FALSE : Boolean.TRUE;
            }
        }

        return java.awt.Image.UndefinedProperty;
    }
}

/**
 * An <code>OperationDescriptor</code> describing the "IDFT" operation.
 *
 * <p>The "IDFT" operation computes the inverse discrete Fourier transform of an image. A positive exponential is used
 * as the basis function for the transform. The operation supports real-to-complex, complex-to-complex, and
 * complex-to-real transforms. A complex image must have an even number of bands, with the even bands (0, 2, ...)
 * representing the real parts and the odd bands (1, 3, ...) the imaginary parts of each complex pixel.
 *
 * <p>The nature of the source and destination data is specified by the "dataNature" operation parameter. If the source
 * data are complex then the number of bands in the source image must be a multiple of 2. The number of bands in the
 * destination must match that which would be expected given the number of bands in the source image and the specified
 * nature of the source and destination data. If the source image is real then the number of bands in the destination
 * will be twice that in the source. If the destination image is real than the number of bands in the destination will
 * be half that in the source. Otherwise the number of bands in the source and destination must be equal.
 *
 * <p>If an underlying fast Fourier transform (FFT) implementation is used which requires that the image dimensions be
 * powers of 2, then the width and height may each be increased to the power of 2 greater than or equal to the original
 * width and height, respectively.
 *
 * <p>"IDFT" defines a PropertyGenerator that sets the "COMPLEX" property of the image to <code>java.lang.Boolean.FALSE
 * </code> if the "dataNature" operation parameter is equal to IDFTDescriptor.COMPLEX_TO_REAL and to <code>
 * java.lang.Boolean.TRUE</code> otherwise. The value of this property may be retrieved by calling the <code>
 * getProperty()</code> method with "COMPLEX" as the property name.
 *
 * <p>
 *
 * <table border=1>
 * <caption>Resource List</caption>
 * <tr><th>Name</th>        <th>Value</th></tr>
 * <tr><td>GlobalName</td>  <td>IDFT</td></tr>
 * <tr><td>LocalName</td>   <td>IDFT</td></tr>
 * <tr><td>Vendor</td>      <td>org.eclipse.imagen.media</td></tr>
 * <tr><td>Description</td> <td>Computes the discrete Fourier transform of
 *                              an image.</td></tr>
 * <tr><td>DocURL</td>      <td>http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/IDFTDescriptor.html</td></tr>
 * <tr><td>Version</td>     <td>1.0</td></tr>
 * <tr><td>arg0Desc</td>    <td>The type of scaling to be used.</td></tr>
 * <tr><td>arg1Desc</td>    <td>The nature of the data.</td></tr>
 * </table>
 *
 * <p>
 *
 * <table border=1>
 * <caption>Parameter List</caption>
 * <tr><th>Name</th>  <th>Class Type</th>
 *                    <th>Default Value</th></tr>
 * <tr><td>scalingType</td> <td>org.eclipse.imagen.operator.DFTScalingType</td>
 *                    <td>IDFTDescriptor.SCALING_DIMENSIONS</td>
 * <tr><td>dataNature</td> <td>org.eclipse.imagen.operator.DFTDataNature</td>
 *                    <td>IDFTDescriptor.COMPLEX_TO_REAL</td>
 * </table>
 *
 * @see org.eclipse.imagen.OperationDescriptor
 * @see DFTDescriptor
 * @see DFTDataNature
 * @see DFTScalingType
 */
public class IDFTDescriptor extends OperationDescriptorImpl {
    /** A flag indicating that the transform is not to be scaled. */
    public static final DFTScalingType SCALING_NONE = DFTDescriptor.SCALING_NONE;

    /** A flag indicating that the transform is to be scaled by the square root of the product of its dimensions. */
    public static final DFTScalingType SCALING_UNITARY = DFTDescriptor.SCALING_UNITARY;

    /** A flag indicating that the transform is to be scaled by the product of its dimensions. */
    public static final DFTScalingType SCALING_DIMENSIONS = DFTDescriptor.SCALING_DIMENSIONS;

    /** A flag indicating that the source data are real and the destination data complex. */
    public static final DFTDataNature REAL_TO_COMPLEX = DFTDescriptor.REAL_TO_COMPLEX;

    /** A flag indicating that the source and destination data are both complex. */
    public static final DFTDataNature COMPLEX_TO_COMPLEX = DFTDescriptor.COMPLEX_TO_COMPLEX;

    /** A flag indicating that the source data are complex and the destination data real. */
    public static final DFTDataNature COMPLEX_TO_REAL = DFTDescriptor.COMPLEX_TO_REAL;

    /**
     * The resource strings that provide the general documentation and specify the parameter list for this operation.
     */
    private static final String[][] resources = {
        {"GlobalName", "IDFT"},
        {"LocalName", "IDFT"},
        {"Vendor", "org.eclipse.imagen.media"},
        {"Description", JaiI18N.getString("IDFTDescriptor0")},
        {
            "DocURL",
            "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/IDFTDescriptor.html"
        },
        {"Version", JaiI18N.getString("DescriptorVersion2")},
        {"arg0Desc", JaiI18N.getString("IDFTDescriptor1")},
        {"arg1Desc", JaiI18N.getString("IDFTDescriptor2")}
    };

    /** The parameter class list for this operation. */
    private static final Class[] paramClasses = {DFTScalingType.class, DFTDataNature.class};

    /** The parameter name list for this operation. */
    private static final String[] paramNames = {"scalingType", "dataNature"};

    /** The parameter default value list for this operation. */
    private static final Object[] paramDefaults = {IDFTDescriptor.SCALING_DIMENSIONS, IDFTDescriptor.COMPLEX_TO_REAL};

    private static final String[] supportedModes = {"rendered", "renderable"};

    /** Constructor. */
    public IDFTDescriptor() {
        super(resources, supportedModes, 1, paramNames, paramClasses, paramDefaults, null);
    }

    /**
     * Validates the input source and parameters.
     *
     * <p>In addition to the standard checks performed by the superclass method, this method checks that "scalingType"
     * is one of <code>SCALING_NONE</code>, <code>SCALING_UNITARY</code>, or <code>SCALING_DIMENSIONS</code>, and that
     * "dataNature" is one of <code>REAL_TO_COMPLEX</code>, <code>COMPLEX_TO_COMPLEX</code>, or <code>COMPLEX_TO_REAL
     * </code>. Also, if "dataNature" is <code>COMPLEX_TO_COMPLEX</code> or <code>COMPLEX_TO_REAL</code> the number of
     * source bands must be even.
     */
    public boolean validateArguments(String modeName, ParameterBlock args, StringBuffer msg) {
        if (!super.validateArguments(modeName, args, msg)) {
            return false;
        }

        if (!modeName.equalsIgnoreCase("rendered")) return true;

        // Check source band count: must be even for a complex source.
        EnumeratedParameter dataNature = (EnumeratedParameter) args.getObjectParameter(1);

        if (!dataNature.equals(IDFTDescriptor.REAL_TO_COMPLEX)) {
            RenderedImage src = args.getRenderedSource(0);

            if (src.getSampleModel().getNumBands() % 2 != 0) {
                msg.append(getName() + " " + JaiI18N.getString("IDFTDescriptor5"));
                return false;
            }
        }

        return true;
    }

    /**
     * Returns an array of <code>PropertyGenerators</code> implementing property inheritance for the "IDFT" operation.
     *
     * @return An array of property generators.
     */
    public PropertyGenerator[] getPropertyGenerators(String modeName) {
        PropertyGenerator[] pg = new PropertyGenerator[1];
        pg[0] = new IDFTPropertyGenerator();
        return pg;
    }

    /**
     * Computes the inverse discrete Fourier transform of an image.
     *
     * <p>Creates a <code>ParameterBlockImageN</code> from all supplied arguments except <code>hints</code> and invokes
     * {@link ImageN#create(String,ParameterBlock,RenderingHints)}.
     *
     * @see ImageN
     * @see ParameterBlockImageN
     * @see RenderedOp
     * @param source0 <code>RenderedImage</code> source 0.
     * @param scalingType The type of scaling to perform. May be <code>null</code>.
     * @param dataNature The nature of the data. May be <code>null</code>.
     * @param hints The <code>RenderingHints</code> to use. May be <code>null</code>.
     * @return The <code>RenderedOp</code> destination.
     * @throws IllegalArgumentException if <code>source0</code> is <code>null</code>.
     */
    public static RenderedOp create(
            RenderedImage source0, DFTScalingType scalingType, DFTDataNature dataNature, RenderingHints hints) {
        ParameterBlockImageN pb = new ParameterBlockImageN("IDFT", RenderedRegistryMode.MODE_NAME);

        pb.setSource("source0", source0);

        pb.setParameter("scalingType", scalingType);
        pb.setParameter("dataNature", dataNature);

        return ImageN.create("IDFT", pb, hints);
    }

    /**
     * Computes the inverse discrete Fourier transform of an image.
     *
     * <p>Creates a <code>ParameterBlockImageN</code> from all supplied arguments except <code>hints</code> and invokes
     * {@link ImageN#createRenderable(String,ParameterBlock,RenderingHints)}.
     *
     * @see ImageN
     * @see ParameterBlockImageN
     * @see RenderableOp
     * @param source0 <code>RenderableImage</code> source 0.
     * @param scalingType The type of scaling to perform. May be <code>null</code>.
     * @param dataNature The nature of the data. May be <code>null</code>.
     * @param hints The <code>RenderingHints</code> to use. May be <code>null</code>.
     * @return The <code>RenderableOp</code> destination.
     * @throws IllegalArgumentException if <code>source0</code> is <code>null</code>.
     */
    public static RenderableOp createRenderable(
            RenderableImage source0, DFTScalingType scalingType, DFTDataNature dataNature, RenderingHints hints) {
        ParameterBlockImageN pb = new ParameterBlockImageN("IDFT", RenderableRegistryMode.MODE_NAME);

        pb.setSource("source0", source0);

        pb.setParameter("scalingType", scalingType);
        pb.setParameter("dataNature", dataNature);

        return ImageN.createRenderable("IDFT", pb, hints);
    }
}
