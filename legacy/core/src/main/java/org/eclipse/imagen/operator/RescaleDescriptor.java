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
import org.eclipse.imagen.ParameterBlockImageN;
import org.eclipse.imagen.RenderableOp;
import org.eclipse.imagen.RenderedOp;
import org.eclipse.imagen.registry.RenderableRegistryMode;
import org.eclipse.imagen.registry.RenderedRegistryMode;

/**
 * An <code>OperationDescriptor</code> describing the "Rescale" operation.
 *
 * <p>The "Rescale" operation takes a rendered or renderable source image and maps the pixel values of an image from one
 * range to another range by multiplying each pixel value by one of a set of constants and then adding another constant
 * to the result of the multiplication. If the number of constants supplied is less than the number of bands of the
 * destination, then the constant from entry 0 is applied to all the bands. Otherwise, a constant from a different entry
 * is applied to each band. There must be at least one entry in each of the contants and offsets arrays.
 *
 * <p>The destination pixel values are defined by the pseudocode:
 *
 * <pre>
 * constant = (constants.length < dstNumBands) ?
 *            constants[0] : constants[b];
 * offset = (offsets.length < dstNumBands) ?
 *          offsets[0] : offsets[b];
 *
 * dst[x][y][b] = src[x][y][b]*constant + offset;
 * </pre>
 *
 * <p>The pixel arithmetic is performed using the data type of the destination image. By default, the destination will
 * have the same data type as the source image unless an <code>ImageLayout</code> containing a <code>SampleModel</code>
 * with a different data type is supplied as a rendering hint.
 *
 * <p>
 *
 * <table border=1>
 * <caption>Resource List</caption>
 * <tr><th>Name</th>        <th>Value</th></tr>
 * <tr><td>GlobalName</td>  <td>Rescale</td></tr>
 * <tr><td>LocalName</td>   <td>Rescale</td></tr>
 * <tr><td>Vendor</td>      <td>org.eclipse.imagen.media</td></tr>
 * <tr><td>Description</td> <td>Maps the pixels values of an image from
 *                              one range to another range.</td></tr>
 * <tr><td>DocURL</td>      <td>http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/RescaleDescriptor.html</td></tr>
 * <tr><td>Version</td>     <td>1.0</td></tr>
 * <tr><td>arg0Desc</td>    <td>The per-band constants to multiply by.</td></tr>
 * <tr><td>arg1Desc</td>    <td>The per-band offsets to be added.</td></tr>
 * </table>
 *
 * <p>
 *
 * <table border=1>
 * <caption>Parameter List</caption>
 * <tr><th>Name</th>   <th>Class Type</th>
 *                     <th>Default Value</th></tr>
 * <tr><td>constants</td> <td>double[]</td>
 *                     <td>{1.0}</td>
 * <tr><td>offsets</td> <td>double[]</td>
 *                     <td>{0.0}</td>
 * </table>
 *
 * @see org.eclipse.imagen.OperationDescriptor
 */
public class RescaleDescriptor extends OperationDescriptorImpl {

    /**
     * The resource strings that provide the general documentation and specify the parameter list for this operation.
     */
    private static final String[][] resources = {
        {"GlobalName", "Rescale"},
        {"LocalName", "Rescale"},
        {"Vendor", "org.eclipse.imagen.media"},
        {"Description", JaiI18N.getString("RescaleDescriptor0")},
        {
            "DocURL",
            "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/RescaleDescriptor.html"
        },
        {"Version", JaiI18N.getString("DescriptorVersion")},
        {"arg0Desc", JaiI18N.getString("RescaleDescriptor1")},
        {"arg1Desc", JaiI18N.getString("RescaleDescriptor2")}
    };

    /** The parameter class list for this operation. */
    private static final Class[] paramClasses = {double[].class, double[].class};

    /** The parameter name list for this operation. */
    private static final String[] paramNames = {"constants", "offsets"};

    /** The parameter default value list for this operation. */
    private static final Object[] paramDefaults = {new double[] {1.0}, new double[] {0.0}};

    /** Constructor. */
    public RescaleDescriptor() {
        super(resources, 1, paramClasses, paramNames, paramDefaults);
    }

    /** Returns <code>true</code> since renderable operation is supported. */
    public boolean isRenderableSupported() {
        return true;
    }

    /**
     * Validates the input parameters.
     *
     * <p>In addition to the standard checks performed by the superclass method, this method checks that the length of
     * the "constants" and "offsets" arrays are each at least 1.
     */
    protected boolean validateParameters(ParameterBlock args, StringBuffer msg) {
        if (!super.validateParameters(args, msg)) {
            return false;
        }

        int constantsLength = ((double[]) args.getObjectParameter(0)).length;
        int offsetsLength = ((double[]) args.getObjectParameter(1)).length;

        if (constantsLength < 1) {
            msg.append(getName() + " " + JaiI18N.getString("RescaleDescriptor3"));
            return false;
        }

        if (offsetsLength < 1) {
            msg.append(getName() + ": " + JaiI18N.getString("RescaleDescriptor4"));
            return false;
        }

        return true;
    }

    /**
     * Maps the pixels values of an image from one range to another range.
     *
     * <p>Creates a <code>ParameterBlockImageN</code> from all supplied arguments except <code>hints</code> and invokes
     * {@link ImageN#create(String,ParameterBlock,RenderingHints)}.
     *
     * @see ImageN
     * @see ParameterBlockImageN
     * @see RenderedOp
     * @param source0 <code>RenderedImage</code> source 0.
     * @param constants The per-band constants to multiply by. May be <code>null</code>.
     * @param offsets The per-band offsets to be added. May be <code>null</code>.
     * @param hints The <code>RenderingHints</code> to use. May be <code>null</code>.
     * @return The <code>RenderedOp</code> destination.
     * @throws IllegalArgumentException if <code>source0</code> is <code>null</code>.
     */
    public static RenderedOp create(RenderedImage source0, double[] constants, double[] offsets, RenderingHints hints) {
        ParameterBlockImageN pb = new ParameterBlockImageN("Rescale", RenderedRegistryMode.MODE_NAME);

        pb.setSource("source0", source0);

        pb.setParameter("constants", constants);
        pb.setParameter("offsets", offsets);

        return ImageN.create("Rescale", pb, hints);
    }

    /**
     * Maps the pixels values of an image from one range to another range.
     *
     * <p>Creates a <code>ParameterBlockImageN</code> from all supplied arguments except <code>hints</code> and invokes
     * {@link ImageN#createRenderable(String,ParameterBlock,RenderingHints)}.
     *
     * @see ImageN
     * @see ParameterBlockImageN
     * @see RenderableOp
     * @param source0 <code>RenderableImage</code> source 0.
     * @param constants The per-band constants to multiply by. May be <code>null</code>.
     * @param offsets The per-band offsets to be added. May be <code>null</code>.
     * @param hints The <code>RenderingHints</code> to use. May be <code>null</code>.
     * @return The <code>RenderableOp</code> destination.
     * @throws IllegalArgumentException if <code>source0</code> is <code>null</code>.
     */
    public static RenderableOp createRenderable(
            RenderableImage source0, double[] constants, double[] offsets, RenderingHints hints) {
        ParameterBlockImageN pb = new ParameterBlockImageN("Rescale", RenderableRegistryMode.MODE_NAME);

        pb.setSource("source0", source0);

        pb.setParameter("constants", constants);
        pb.setParameter("offsets", offsets);

        return ImageN.createRenderable("Rescale", pb, hints);
    }
}
