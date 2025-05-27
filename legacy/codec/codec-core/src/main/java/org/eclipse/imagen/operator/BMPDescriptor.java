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
import org.eclipse.imagen.JAI;
import org.eclipse.imagen.OperationDescriptorImpl;
import org.eclipse.imagen.ParameterBlockJAI;
import org.eclipse.imagen.RenderedOp;
import org.eclipse.imagen.media.codec.SeekableStream;
import org.eclipse.imagen.registry.RenderedRegistryMode;

/**
 * An <code>OperationDescriptor</code> describing the "BMP" operation.
 *
 * <p>The "BMP" operation reads a standard BMP input stream. The "BMP" operation currently reads Version2, Version3 and
 * some of the Version 4 images, as defined in the Microsoft Windows BMP file format.
 *
 * <p>Version 4 of the BMP format allows for the specification of alpha values, gamma values and CIE colorspaces. These
 * are not currently handled, but the relevant properties are emitted, if they are available from the BMP image file.
 *
 * <p><b> The classes in the <code>org.eclipse.imagen.media.codec</code> package are not a committed part of the JAI
 * API. Future releases of JAI will make use of new classes in their place. This class will change accordingly.</b>
 *
 * <p>
 *
 * <table border=1>
 * <caption>Resource List</caption>
 * <tr><th>Name</th>        <th>Value</th></tr>
 * <tr><td>GlobalName</td>  <td>BMP</td></tr>
 * <tr><td>LocalName</td>   <td>BMP</td></tr>
 * <tr><td>Vendor</td>      <td>org.eclipse.imagen.media</td></tr>
 * <tr><td>Description</td> <td>Reads an image from a BMP stream.</td></tr>
 * <tr><td>DocURL</td>      <td>http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/BMPDescriptor.html</td></tr>
 * <tr><td>Version</td>     <td>1.0</td></tr>
 * <tr><td>arg0Desc</td>    <td>The SeekableStream to read from.</td></tr>
 * </table>
 *
 * <p>
 *
 * <table border=1>
 * <caption>Parameter List</caption>
 * <tr><th>Name</th>    <th>Class Type</th>
 *                      <th>Default Value</th></tr>
 * <tr><td>stream</td>  <td>org.eclipse.imagen.media.codec.SeekableStream</td>
 *                      <td>NO_PARAMETER_DEFAULT</td>
 * </table>
 *
 * @see org.eclipse.imagen.media.codec.SeekableStream
 * @see org.eclipse.imagen.OperationDescriptor
 */
public class BMPDescriptor extends OperationDescriptorImpl {

    /**
     * The resource strings that provide the general documentation and specify the parameter list for the "BMP"
     * operation.
     */
    private static final String[][] resources = {
        {"GlobalName", "BMP"},
        {"LocalName", "BMP"},
        {"Vendor", "org.eclipse.imagen.media"},
        {"Description", JaiI18N.getString("BMPDescriptor0")},
        {
            "DocURL",
            "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/BMPDescriptor.html"
        },
        {"Version", JaiI18N.getString("DescriptorVersion")},
        {"arg0Desc", JaiI18N.getString("BMPDescriptor1")},
    };

    /** The parameter names for the "BMP" operation. */
    private static final String[] paramNames = {"stream"};

    /** The parameter class types for the "BMP" operation. */
    private static final Class[] paramClasses = {org.eclipse.imagen.media.codec.SeekableStream.class};

    /** The parameter default values for the "BMP" operation. */
    private static final Object[] paramDefaults = {NO_PARAMETER_DEFAULT};

    /** Constructor. */
    public BMPDescriptor() {
        super(resources, 0, paramClasses, paramNames, paramDefaults);
    }

    /**
     * Reads an image from a BMP stream.
     *
     * <p>Creates a <code>ParameterBlockJAI</code> from all supplied arguments except <code>hints</code> and invokes
     * {@link JAI#create(String,ParameterBlock,RenderingHints)}.
     *
     * @see JAI
     * @see ParameterBlockJAI
     * @see RenderedOp
     * @param stream The SeekableStream to read from.
     * @param hints The <code>RenderingHints</code> to use. May be <code>null</code>.
     * @return The <code>RenderedOp</code> destination.
     * @throws IllegalArgumentException if <code>stream</code> is <code>null</code>.
     */
    public static RenderedOp create(SeekableStream stream, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("BMP", RenderedRegistryMode.MODE_NAME);

        pb.setParameter("stream", stream);

        return JAI.create("BMP", pb, hints);
    }
}
