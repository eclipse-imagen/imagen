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
import org.eclipse.imagen.ImageN;
import org.eclipse.imagen.OperationDescriptorImpl;
import org.eclipse.imagen.ParameterBlockJAI;
import org.eclipse.imagen.RenderableOp;
import org.eclipse.imagen.RenderedOp;
import org.eclipse.imagen.registry.RenderableRegistryMode;
import org.eclipse.imagen.registry.RenderedRegistryMode;

/**
 * An <code>OperationDescriptor</code> describing the "Binarize" operation.
 *
 * <p>The "Binarize" operation takes one rendered or renderable single-banded source image and a threshold value and
 * applies a thresholding operation to the produce a bilevel image.
 *
 * <p>By default the destination image bounds are equal to those of the source image. The <code>SampleModel</code> of
 * the destination image is an instance of <code>MultiPixelPackedSampleModel</code>.
 *
 * <p>The pseudocode for "Binarize" is as follows:
 *
 * <pre>
 *      dst(x, y) = src(x, y) >= threshold ? 1 : 0;
 * </pre>
 *
 * <p>
 *
 * <table border=1>
 * <caption>Resource List</caption>
 * <tr><th>Name</th>        <th>Value</th></tr>
 * <tr><td>GlobalName</td>  <td>Binarize</td></tr>
 * <tr><td>LocalName</td>   <td>Binarize</td></tr>
 * <tr><td>Vendor</td>      <td>org.eclipse.imagen.media</td></tr>
 * <tr><td>Description</td> <td>Thresholds an image into a bilevel image.<td></tr>
 * <tr><td>DocURL</td>      <td>http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/BinarizeDescriptor.html</td></tr>
 * <tr><td>Version</td>     <td>1.1</td></tr>
 * <tr><td>arg0Desc</td>    <td>The threshold value.</td></tr>
 * </table>
 *
 * <p>
 *
 * <table border=1>
 * <caption>Parameter List</caption>
 * <tr><th>Name</th>      <th>Class Type</th>
 *                        <th>Default Value</th></tr>
 * <tr><td>threshold</td> <td>java.lang.Double</td>
 *                        <td>NO_PARAMETER_DEFAULT</td>
 * </table>
 *
 * @see org.eclipse.imagen.OperationDescriptor
 * @since ImageN 1.1
 */
public class BinarizeDescriptor extends OperationDescriptorImpl {

    /**
     * The resource strings that provide the general documentation and specify the parameter list for this operation.
     */
    private static final String[][] resources = {
        {"GlobalName", "Binarize"},
        {"LocalName", "Binarize"},
        {"Vendor", "org.eclipse.imagen.media"},
        {"Description", JaiI18N.getString("BinarizeDescriptor0")},
        {
            "DocURL",
            "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/BinarizeDescriptor.html"
        },
        {"Version", JaiI18N.getString("DescriptorVersion")},
        {"arg0Desc", JaiI18N.getString("BinarizeDescriptor1")}
    };

    /** The parameter name list for this operation. */
    private static final String[] paramNames = {"threshold"};

    /** The parameter class list for this operation. The number of threshold value provided should be 1. */
    private static final Class[] paramClasses = {java.lang.Double.class};

    /** The parameter default value list for this operation. */
    private static final Object[] paramDefaults = {NO_PARAMETER_DEFAULT};

    private static final String[] supportedModes = {"rendered", "renderable"};

    /** Constructor. */
    public BinarizeDescriptor() {
        super(resources, supportedModes, 1, paramNames, paramClasses, paramDefaults, null);
    }

    /**
     * Validates the input source.
     *
     * <p>In addition to the standard checks performed by the superclass method, this method checks that the source
     * image is single-banded.
     */
    protected boolean validateSources(String modeName, ParameterBlock args, StringBuffer msg) {
        if (!super.validateSources(modeName, args, msg)) {
            return false;
        }

        if (!modeName.equalsIgnoreCase("rendered")) return true;

        RenderedImage source = (RenderedImage) (args.getSource(0));
        int numBands = source.getSampleModel().getNumBands();
        if (numBands != 1) {
            msg.append(getName() + " " + JaiI18N.getString("BinarizeDescriptor2"));
            return false;
        }

        return true;
    }

    /**
     * Binarize an image from a threshold value.
     *
     * <p>Creates a <code>ParameterBlockJAI</code> from all supplied arguments except <code>hints</code> and invokes
     * {@link ImageN#create(String,ParameterBlock,RenderingHints)}.
     *
     * @see ImageN
     * @see ParameterBlockJAI
     * @see RenderedOp
     * @param source0 <code>RenderedImage</code> source 0.
     * @param threshold Argment must be of type java.lang.Double.
     * @param hints The <code>RenderingHints</code> to use. May be <code>null</code>.
     * @return The <code>RenderedOp</code> destination.
     * @throws IllegalArgumentException if <code>source0</code> is <code>null</code>.
     * @throws IllegalArgumentException if <code>threshold</code> is <code>null</code>.
     */
    public static RenderedOp create(RenderedImage source0, Double threshold, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("Binarize", RenderedRegistryMode.MODE_NAME);

        pb.setSource("source0", source0);

        pb.setParameter("threshold", threshold);

        return ImageN.create("Binarize", pb, hints);
    }

    /**
     * Binarize an image from a threshold value.
     *
     * <p>Creates a <code>ParameterBlockJAI</code> from all supplied arguments except <code>hints</code> and invokes
     * {@link ImageN#createRenderable(String,ParameterBlock,RenderingHints)}.
     *
     * @see ImageN
     * @see ParameterBlockJAI
     * @see RenderableOp
     * @param source0 <code>RenderableImage</code> source 0.
     * @param threshold Argment must be of type java.lang.Double.
     * @param hints The <code>RenderingHints</code> to use. May be <code>null</code>.
     * @return The <code>RenderableOp</code> destination.
     * @throws IllegalArgumentException if <code>source0</code> is <code>null</code>.
     * @throws IllegalArgumentException if <code>threshold</code> is <code>null</code>.
     */
    public static RenderableOp createRenderable(RenderableImage source0, Double threshold, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("Binarize", RenderableRegistryMode.MODE_NAME);

        pb.setSource("source0", source0);

        pb.setParameter("threshold", threshold);

        return ImageN.createRenderable("Binarize", pb, hints);
    }
}
