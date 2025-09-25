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
import java.awt.image.renderable.RenderableImage;
import org.eclipse.imagen.ImageN;
import org.eclipse.imagen.OperationDescriptorImpl;
import org.eclipse.imagen.ParameterBlockJAI;
import org.eclipse.imagen.RenderableOp;
import org.eclipse.imagen.RenderedOp;
import org.eclipse.imagen.registry.RenderableRegistryMode;
import org.eclipse.imagen.registry.RenderedRegistryMode;

/**
 * An <code>OperationDescriptor</code> describing the "Add" operation.
 *
 * <p>The Add operation takes two rendered or renderable source images, and adds every pair of pixels, one from each
 * source image of the corresponding position and band. No additional parameters are required.
 *
 * <p>The two source images may have different numbers of bands and data types. By default, the destination image bounds
 * are the intersection of the two source image bounds. If the sources don't intersect, the destination will have a
 * width and height of 0.
 *
 * <p>The default number of bands of the destination image is equal to the smallest number of bands of the sources, and
 * the data type is the smallest data type with sufficient range to cover the range of both source data types (not
 * necessarily the range of their sums).
 *
 * <p>As a special case, if one of the source images has N bands (N > 1), the other source has 1 band, and an <code>
 * ImageLayout</code> hint is provided containing a destination <code>SampleModel</code> with K bands (1 < K <= N), then
 * the single band of the 1-banded source is added to each of the first K bands of the N-band source.
 *
 * <p>If the result of the operation underflows/overflows the minimum/maximum value supported by the destination data
 * type, then it will be clamped to the minimum/maximum value respectively.
 *
 * <p>The destination pixel values are defined by the pseudocode:
 *
 * <pre>
 * dst[x][y][dstBand] = clamp(srcs[0][x][y][src0Band] +
 *                            srcs[1][x][y][src1Band]);
 * </pre>
 *
 * <p>
 *
 * <table border=1>
 * <caption>Resource List</caption>
 * <tr><th>Name</th>        <th>Value</th></tr>
 * <tr><td>GlobalName</td>  <td>Add</td></tr>
 * <tr><td>LocalName</td>   <td>Add</td></tr>
 * <tr><td>Vendor</td>      <td>org.eclipse.imagen.media</td></tr>
 * <tr><td>Description</td> <td>Adds two images.</td></tr>
 * <tr><td>DocURL</td>      <td>http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/AddDescriptor.html</td></tr>
 * <tr><td>Version</td>     <td>1.0</td></tr>
 * </table>
 *
 * <p>No parameters are needed for this operation.
 *
 * @see org.eclipse.imagen.OperationDescriptor
 */
public class AddDescriptor extends OperationDescriptorImpl {

    /**
     * The resource strings that provide the general documentation and specify the parameter list for this operation.
     */
    private static final String[][] resources = {
        {"GlobalName", "Add"},
        {"LocalName", "Add"},
        {"Vendor", "org.eclipse.imagen.media"},
        {"Description", JaiI18N.getString("AddDescriptor0")},
        {
            "DocURL",
            "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/AddDescriptor.html"
        },
        {"Version", JaiI18N.getString("DescriptorVersion")}
    };

    /** Constructor. */
    public AddDescriptor() {
        super(resources, 2, null, null, null);
    }

    /** Returns <code>true</code> since renderable operation is supported. */
    public boolean isRenderableSupported() {
        return true;
    }

    /**
     * Adds two images.
     *
     * <p>Creates a <code>ParameterBlockJAI</code> from all supplied arguments except <code>hints</code> and invokes
     * {@link ImageN#create(String,ParameterBlock,RenderingHints)}.
     *
     * @see ImageN
     * @see ParameterBlockJAI
     * @see RenderedOp
     * @param source0 <code>RenderedImage</code> source 0.
     * @param source1 <code>RenderedImage</code> source 1.
     * @param hints The <code>RenderingHints</code> to use. May be <code>null</code>.
     * @return The <code>RenderedOp</code> destination.
     * @throws IllegalArgumentException if <code>source0</code> is <code>null</code>.
     * @throws IllegalArgumentException if <code>source1</code> is <code>null</code>.
     */
    public static RenderedOp create(RenderedImage source0, RenderedImage source1, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("Add", RenderedRegistryMode.MODE_NAME);

        pb.setSource("source0", source0);
        pb.setSource("source1", source1);

        return ImageN.create("Add", pb, hints);
    }

    /**
     * Adds two images.
     *
     * <p>Creates a <code>ParameterBlockJAI</code> from all supplied arguments except <code>hints</code> and invokes
     * {@link ImageN#createRenderable(String,ParameterBlock,RenderingHints)}.
     *
     * @see ImageN
     * @see ParameterBlockJAI
     * @see RenderableOp
     * @param source0 <code>RenderableImage</code> source 0.
     * @param source1 <code>RenderableImage</code> source 1.
     * @param hints The <code>RenderingHints</code> to use. May be <code>null</code>.
     * @return The <code>RenderableOp</code> destination.
     * @throws IllegalArgumentException if <code>source0</code> is <code>null</code>.
     * @throws IllegalArgumentException if <code>source1</code> is <code>null</code>.
     */
    public static RenderableOp createRenderable(
            RenderableImage source0, RenderableImage source1, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("Add", RenderableRegistryMode.MODE_NAME);

        pb.setSource("source0", source0);
        pb.setSource("source1", source1);

        return ImageN.createRenderable("Add", pb, hints);
    }
}
