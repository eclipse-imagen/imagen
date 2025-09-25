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
import java.util.Collection;
import java.util.Iterator;
import org.eclipse.imagen.ImageN;
import org.eclipse.imagen.OperationDescriptorImpl;
import org.eclipse.imagen.ParameterBlockJAI;
import org.eclipse.imagen.ParameterListDescriptor;
import org.eclipse.imagen.RenderableOp;
import org.eclipse.imagen.RenderedOp;
import org.eclipse.imagen.registry.RenderableRegistryMode;
import org.eclipse.imagen.registry.RenderedRegistryMode;

/**
 * An <code>OperationDescriptor</code> describing the "AddCollection" operation.
 *
 * <p>The AddCollection operation takes a collection of rendered or renderable source images, and adds every set of
 * pixels, one from each source image of the corresponding position and band. No additional parameters are required.
 *
 * <p>There is no restriction on the actual class type used to represent the source collection, but all elements of the
 * collection must be instances of <code>RenderedImage</code> or <code>RenderableImage</code> depending on the mode. The
 * number of images in the collection may vary from 2 to n. The source images may have different numbers of bands and
 * data types.
 *
 * <p>By default, the destination image bounds are the intersection of all of the source image bounds. If any of the two
 * sources are completely disjoint, the destination will have a width and a height of 0. The number of bands of the
 * destination image is equal to the minimum number of bands of all the sources, and the data type is the biggest data
 * type of all the sources. If the result of the operation underflows/overflows the minimum/maximum value supported by
 * the destination data type, then it will be clamped to the minimum/maximum value respectively.
 *
 * <p>The destination pixel values are defined by the pseudocode:
 *
 * <pre>
 * dst[x][y][b] = 0;
 * for (int i = 0; i < numSources; i++) {
 *     dst[x][y][b] += srcs[i][x][y][b];
 * }
 * </pre>
 *
 * <p>
 *
 * <table border=1>
 * <caption>Resource List</caption>
 * <tr><th>Name</th>        <th>Value</th></tr>
 * <tr><td>GlobalName</td>  <td>AddCollection</td></tr>
 * <tr><td>LocalName</td>   <td>AddCollection</td></tr>
 * <tr><td>Vendor</td>      <td>org.eclipse.imagen.media</td></tr>
 * <tr><td>Description</td> <td>Adds a collection of rendered images.</td></tr>
 * <tr><td>DocURL</td>      <td>http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/AddCollectionDescriptor.html</td></tr>
 * <tr><td>Version</td>     <td>1.0</td></tr>
 * </table>
 *
 * <p>No parameters are needed for this operation.
 *
 * @see java.awt.image.RenderedImage
 * @see java.awt.image.renderable.RenderableImage
 * @see java.util.Collection
 * @see org.eclipse.imagen.OperationDescriptor
 */
public class AddCollectionDescriptor extends OperationDescriptorImpl {

    /**
     * The resource strings that provide the general documentation and specify the parameter list for this operation.
     */
    private static final String[][] resources = {
        {"GlobalName", "AddCollection"},
        {"LocalName", "AddCollection"},
        {"Vendor", "org.eclipse.imagen.media"},
        {"Description", JaiI18N.getString("AddCollectionDescriptor0")},
        {
            "DocURL",
            "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/AddCollectionDescriptor.html"
        },
        {"Version", JaiI18N.getString("DescriptorVersion")}
    };

    /** The source class list for this operation. */
    private static final Class[][] sourceClasses = {{java.util.Collection.class}, {java.util.Collection.class}};

    private static final String[] supportedModes = {"rendered", "renderable"};

    /** Constructor. */
    public AddCollectionDescriptor() {
        super(resources, supportedModes, null, sourceClasses, (ParameterListDescriptor) null);
    }

    /** Validates input source collection. */
    protected boolean validateSources(String modeName, ParameterBlock args, StringBuffer msg) {
        if (!super.validateSources(modeName, args, msg)) {
            return false;
        }

        Collection col = (Collection) args.getSource(0);

        if (col.size() < 2) {
            msg.append(getName() + " " + JaiI18N.getString("AddCollectionDescriptor1"));
            return false;
        }

        Iterator iter = col.iterator();
        if (modeName.equalsIgnoreCase(RenderedRegistryMode.MODE_NAME)) {
            while (iter.hasNext()) {
                Object o = iter.next();
                if (!(o instanceof RenderedImage)) {
                    msg.append(getName() + " " + JaiI18N.getString("AddCollectionDescriptor2"));
                    return false;
                }
            }
        } else if (modeName.equalsIgnoreCase(RenderableRegistryMode.MODE_NAME)) {
            while (iter.hasNext()) {
                Object o = iter.next();
                if (!(o instanceof RenderableImage)) {
                    msg.append(getName() + " " + JaiI18N.getString("AddCollectionDescriptor3"));
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Adds a collection of images.
     *
     * <p>Creates a <code>ParameterBlockJAI</code> from all supplied arguments except <code>hints</code> and invokes
     * {@link ImageN#create(String,ParameterBlock,RenderingHints)}.
     *
     * @see ImageN
     * @see ParameterBlockJAI
     * @see RenderedOp
     * @param source0 <code>Collection</code> source 0.
     * @param hints The <code>RenderingHints</code> to use. May be <code>null</code>.
     * @return The <code>RenderedOp</code> destination.
     * @throws IllegalArgumentException if <code>source0</code> is <code>null</code>.
     */
    public static RenderedOp create(Collection source0, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("AddCollection", RenderedRegistryMode.MODE_NAME);

        pb.setSource("source0", source0);

        return ImageN.create("AddCollection", pb, hints);
    }

    /**
     * Adds a collection of images.
     *
     * <p>Creates a <code>ParameterBlockJAI</code> from all supplied arguments except <code>hints</code> and invokes
     * {@link ImageN#createRenderable(String,ParameterBlock,RenderingHints)}.
     *
     * @see ImageN
     * @see ParameterBlockJAI
     * @see RenderableOp
     * @param source0 <code>Collection</code> source 0.
     * @param hints The <code>RenderingHints</code> to use. May be <code>null</code>.
     * @return The <code>RenderableOp</code> destination.
     * @throws IllegalArgumentException if <code>source0</code> is <code>null</code>.
     */
    public static RenderableOp createRenderable(Collection source0, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("AddCollection", RenderableRegistryMode.MODE_NAME);

        pb.setSource("source0", source0);

        return ImageN.createRenderable("AddCollection", pb, hints);
    }
}
