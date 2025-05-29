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
import java.util.Collection;
import java.util.Iterator;
import org.eclipse.imagen.JAI;
import org.eclipse.imagen.OperationDescriptorImpl;
import org.eclipse.imagen.ParameterBlockJAI;
import org.eclipse.imagen.registry.CollectionRegistryMode;

/**
 * An <code>OperationDescriptor</code> describing the "AddConstToCollection" operation.
 *
 * <p>The AddConstToCollection operation takes a collection of rendered images and an array of double constants, and for
 * each rendered image in the collection adds a constant to every pixel of its corresponding band. If the number of
 * constants supplied is less than the number of bands of a source image then the same constant from entry 0 is applied
 * to all the bands. Otherwise, a constant from a different entry is applied to each band.
 *
 * <p>The operation will attempt to store the result images in the same collection class as that of the source images.
 * If a new instance of the source collection class can not be created, then the operation will store the result images
 * in a java.util.Vector. There will be the same number of images in the output collection as in the source collection.
 *
 * <p>
 *
 * <table border=1>
 * <caption>Resource List</caption>
 * <tr><th>Name</th>        <th>Value</th></tr>
 * <tr><td>GlobalName</td>  <td>AddConstToCollection</td></tr>
 * <tr><td>LocalName</td>   <td>AddConstToCollection</td></tr>
 * <tr><td>Vendor</td>      <td>org.eclipse.imagen.media</td></tr>
 * <tr><td>Description</td> <td>Adds constants to a collection
 *                              of rendered images.<td></tr>
 * <tr><td>DocURL</td>      <td>http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/AddConstToCollectionDescriptor.html</td></tr>
 * <tr><td>Version</td>     <td>1.0</td></tr>
 * <tr><td>arg0Desc</td>    <td>The constants to be added.</td></tr>
 * </table>
 *
 * <p>
 *
 * <table border=1>
 * <caption>Parameter List</caption>
 * <tr><th>Name</th>      <th>Class Type</th>
 *                        <th>Default Value</th></tr>
 * <tr><td>constants</td> <td>double[]</td>
 *                        <td>{0.0}</td>
 * </table>
 *
 * @see org.eclipse.imagen.CollectionImage
 * @see java.util.Collection
 * @see org.eclipse.imagen.OperationDescriptor
 */
public class AddConstToCollectionDescriptor extends OperationDescriptorImpl {

    /**
     * The resource strings that provide the general documentation and specify the parameter list for this operation.
     */
    private static final String[][] resources = {
        {"GlobalName", "AddConstToCollection"},
        {"LocalName", "AddConstToCollection"},
        {"Vendor", "org.eclipse.imagen.media"},
        {"Description", org.eclipse.imagen.operator.JaiI18N.getString("AddConstToCollectionDescriptor0")},
        {
            "DocURL",
            "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/AddConstToCollectionDescriptor.html"
        },
        {"Version", org.eclipse.imagen.operator.JaiI18N.getString("DescriptorVersion")},
        {"arg0Desc", org.eclipse.imagen.operator.JaiI18N.getString("AddConstToCollectionDescriptor1")}
    };

    /** The parameter name list for this operation. */
    private static final String[] paramNames = {"constants"};

    /** The parameter class list for this operation. */
    private static final Class[] paramClasses = {double[].class};

    /** The parameter default value list for this operation. */
    private static final Object[] paramDefaults = {new double[] {0.0}};

    private static final String[] supportedModes = {"collection"};

    /** Constructor. */
    public AddConstToCollectionDescriptor() {
        super(resources, supportedModes, 1, paramNames, paramClasses, paramDefaults, null);
    }

    /** Validates input source and parameter. */
    public boolean validateArguments(String modeName, ParameterBlock args, StringBuffer msg) {
        if (!super.validateArguments(modeName, args, msg)) {
            return false;
        }

        Collection col = (Collection) args.getSource(0);

        if (col.size() < 1) {
            msg.append(
                    getName() + " " + org.eclipse.imagen.operator.JaiI18N.getString("AddConstToCollectionDescriptor2"));
            return false;
        }

        Iterator iter = col.iterator();
        while (iter.hasNext()) {
            Object o = iter.next();
            if (!(o instanceof RenderedImage)) {
                msg.append(getName() + " "
                        + org.eclipse.imagen.operator.JaiI18N.getString("AddConstToCollectionDescriptor3"));
                return false;
            }
        }

        int length = ((double[]) args.getObjectParameter(0)).length;
        if (length < 1) {
            msg.append(
                    getName() + " " + org.eclipse.imagen.operator.JaiI18N.getString("AddConstToCollectionDescriptor4"));
            return false;
        }

        return true;
    }

    /**
     * Adds constants to a collection of rendered images.
     *
     * <p>Creates a <code>ParameterBlockJAI</code> from all supplied arguments except <code>hints</code> and invokes
     * {@link JAI#createCollection(String,ParameterBlock,RenderingHints)}.
     *
     * @see JAI
     * @see ParameterBlockJAI
     * @see Collection
     * @param source0 <code>Collection</code> source 0.
     * @param constants The constants to be added. May be <code>null</code>.
     * @param hints The <code>RenderingHints</code> to use. May be <code>null</code>.
     * @return The <code>Collection</code> destination.
     * @throws IllegalArgumentException if <code>source0</code> is <code>null</code>.
     */
    public static Collection createCollection(Collection source0, double[] constants, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("AddConstToCollection", CollectionRegistryMode.MODE_NAME);

        pb.setSource("source0", source0);

        pb.setParameter("constants", constants);

        return JAI.createCollection("AddConstToCollection", pb, hints);
    }
}
