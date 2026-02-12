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
import org.eclipse.imagen.OperationDescriptorImpl;
import org.eclipse.imagen.ParameterBlockImageN;
import org.eclipse.imagen.RenderableOp;
import org.eclipse.imagen.RenderedOp;
import org.eclipse.imagen.registry.RenderableRegistryMode;
import org.eclipse.imagen.registry.RenderedRegistryMode;

/**
 * An <code>OperationDescriptor</code> describing the "Threshold" operation.
 *
 * <p>The Threshold operation takes one rendered image, and maps all the pixels of this image whose value falls within a
 * specified range to a specified constant. The range is specified by a low value and a high value.
 *
 * <p>If the number of elements supplied via the "high", "low", and "constants" arrays are less than the number of bands
 * of the source image, then the element from entry 0 is applied to all the bands. Otherwise, the element from a
 * different entry is applied to its corresponding band.
 *
 * <p>The destination pixel values are defined by the pseudocode:
 *
 * <pre>
 * lowVal = (low.length < dstNumBands) ?
 *          low[0] : low[b];
 * highVal = (high.length < dstNumBands) ?
 *           high[0] : high[b];
 * const = (constants.length < dstNumBands) ?
 *           constants[0] : constants[b];
 *
 * if (src[x][y][b] >= lowVal && src[x][y][b] <= highVal) {
 *     dst[x][y][b] = const;
 * } else {
 *     dst[x][y][b] = src[x][y][b];
 * }
 * </pre>
 *
 * <p>
 *
 * <table border=1>
 * <caption>Resource List</caption>
 * <tr><th>Name</th>        <th>Value</th></tr>
 * <tr><td>GlobalName</td>  <td>Threshold</td></tr>
 * <tr><td>LocalName</td>   <td>Threshold</td></tr>
 * <tr><td>Vendor</td>      <td>org.eclipse.imagen.media</td></tr>
 * <tr><td>Description</td> <td>Maps the pixels whose value falls between a low
 *                              value and a high value to a constant.</td></tr>
 * <tr><td>DocURL</td>      <td>http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/ThresholdDescriptor.html</td></tr>
 * <tr><td>Version</td>     <td>1.0</td></tr>
 * <tr><td>arg0Desc</td>    <td>The low value.</td></tr>
 * <tr><td>arg1Desc</td>    <td>The high value.</td></tr>
 * <tr><td>arg2Desc</td>    <td>The constant the pixels are mapped to.</td></tr>
 * </table>
 *
 * <p>
 *
 * <table border=1>
 * <caption>Parameter List</caption>
 * <tr><th>Name</th>      <th>Class Type</th>
 *                        <th>Default Value</th></tr>
 * <tr><td>low</td>       <td>double[]</td>
 *                        <td>NO_PARAMETER_DEFAULT</td>
 * <tr><td>high</td>      <td>double[]</td>
 *                        <td>NO_PARAMETER_DEFAULT</td>
 * <tr><td>constants</td> <td>double[]</td>
 *                        <td>NO_PARAMETER_DEFAULT</td>
 * </table>
 *
 * @see org.eclipse.imagen.OperationDescriptor
 */
public class ThresholdDescriptor extends OperationDescriptorImpl {

    /**
     * The resource strings that provide the general documentation and specify the parameter list for this operation.
     */
    private static final String[][] resources = {
        {"GlobalName", "Threshold"},
        {"LocalName", "Threshold"},
        {"Vendor", "org.eclipse.imagen.media"},
        {"Description", JaiI18N.getString("ThresholdDescriptor0")},
        {
            "DocURL",
            "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/ThresholdDescriptor.html"
        },
        {"Version", JaiI18N.getString("DescriptorVersion")},
        {"arg0Desc", JaiI18N.getString("ThresholdDescriptor1")},
        {"arg1Desc", JaiI18N.getString("ThresholdDescriptor2")},
        {"arg2Desc", JaiI18N.getString("ThresholdDescriptor3")}
    };

    /** The parameter name list for this operation. */
    private static final String[] paramNames = {"low", "high", "constants"};

    /** The parameter class list for this operation. */
    private static final Class[] paramClasses = {double[].class, double[].class, double[].class};

    /** The parameter default value list for this operation. */
    private static final Object[] paramDefaults = {NO_PARAMETER_DEFAULT, NO_PARAMETER_DEFAULT, NO_PARAMETER_DEFAULT};

    /** Constructor. */
    public ThresholdDescriptor() {
        super(resources, 1, paramClasses, paramNames, paramDefaults);
    }

    /** Returns <code>true</code> since renderable operation is supported. */
    public boolean isRenderableSupported() {
        return true;
    }

    /** Validates input parameters. */
    protected boolean validateParameters(ParameterBlock args, StringBuffer msg) {
        int numParams = args.getNumParameters();
        if (numParams < 3) {
            msg.append(getName() + " " + JaiI18N.getString("ThresholdDescriptor4"));
            return false;
        }

        for (int i = 0; i < 3; i++) {
            Object p = args.getObjectParameter(i);

            if (p == null) {
                msg.append(getName() + " " + JaiI18N.getString("ThresholdDescriptor5"));
                return false;
            }

            if (!(p instanceof double[])) {
                msg.append(getName() + " " + JaiI18N.getString("ThresholdDescriptor6"));
                return false;
            }

            if (((double[]) p).length < 1) {
                msg.append(getName() + " " + JaiI18N.getString("ThresholdDescriptor7"));
                return false;
            }
        }

        return true;
    }

    /**
     * Maps the pixels whose value falls between a low value and a high value to a constant.
     *
     * <p>Creates a <code>ParameterBlockImageN</code> from all supplied arguments except <code>hints</code> and invokes
     * {@link ImageN#create(String,ParameterBlock,RenderingHints)}.
     *
     * @see ImageN
     * @see ParameterBlockImageN
     * @see RenderedOp
     * @param source0 <code>RenderedImage</code> source 0.
     * @param low The low value.
     * @param high The high value.
     * @param constants The constant the pixels are mapped to.
     * @param hints The <code>RenderingHints</code> to use. May be <code>null</code>.
     * @return The <code>RenderedOp</code> destination.
     * @throws IllegalArgumentException if <code>source0</code> is <code>null</code>.
     * @throws IllegalArgumentException if <code>low</code> is <code>null</code>.
     * @throws IllegalArgumentException if <code>high</code> is <code>null</code>.
     * @throws IllegalArgumentException if <code>constants</code> is <code>null</code>.
     */
    public static RenderedOp create(
            RenderedImage source0, double[] low, double[] high, double[] constants, RenderingHints hints) {
        ParameterBlockImageN pb = new ParameterBlockImageN("Threshold", RenderedRegistryMode.MODE_NAME);

        pb.setSource("source0", source0);

        pb.setParameter("low", low);
        pb.setParameter("high", high);
        pb.setParameter("constants", constants);

        return ImageN.create("Threshold", pb, hints);
    }

    /**
     * Maps the pixels whose value falls between a low value and a high value to a constant.
     *
     * <p>Creates a <code>ParameterBlockImageN</code> from all supplied arguments except <code>hints</code> and invokes
     * {@link ImageN#createRenderable(String,ParameterBlock,RenderingHints)}.
     *
     * @see ImageN
     * @see ParameterBlockImageN
     * @see RenderableOp
     * @param source0 <code>RenderableImage</code> source 0.
     * @param low The low value.
     * @param high The high value.
     * @param constants The constant the pixels are mapped to.
     * @param hints The <code>RenderingHints</code> to use. May be <code>null</code>.
     * @return The <code>RenderableOp</code> destination.
     * @throws IllegalArgumentException if <code>source0</code> is <code>null</code>.
     * @throws IllegalArgumentException if <code>low</code> is <code>null</code>.
     * @throws IllegalArgumentException if <code>high</code> is <code>null</code>.
     * @throws IllegalArgumentException if <code>constants</code> is <code>null</code>.
     */
    public static RenderableOp createRenderable(
            RenderableImage source0, double[] low, double[] high, double[] constants, RenderingHints hints) {
        ParameterBlockImageN pb = new ParameterBlockImageN("Threshold", RenderableRegistryMode.MODE_NAME);

        pb.setSource("source0", source0);

        pb.setParameter("low", low);
        pb.setParameter("high", high);
        pb.setParameter("constants", constants);

        return ImageN.createRenderable("Threshold", pb, hints);
    }
}
