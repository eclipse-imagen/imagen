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
import java.net.URL;
import org.eclipse.imagen.ImageN;
import org.eclipse.imagen.OperationDescriptorImpl;
import org.eclipse.imagen.ParameterBlockJAI;
import org.eclipse.imagen.RenderedOp;
import org.eclipse.imagen.media.codec.ImageDecodeParam;
import org.eclipse.imagen.registry.RenderedRegistryMode;

/**
 * An <code>OperationDescriptor</code> describing the "URL" operation.
 *
 * <p>The URL operation creates an output image whose source is specified by a Uniform Resource Locator (URL).
 *
 * <p>The allowable formats are those registered with the <code>org.eclipse.imagen.media.codec.ImageCodec</code> class.
 *
 * <p>The second parameter contains an instance of <code>ImageDecodeParam</code> to be used during the decoding. It may
 * be set to <code>null</code> in order to perform default decoding, or equivalently may be omitted.
 *
 * <p><b> The classes in the <code>org.eclipse.imagen.media.codec</code> package are not a committed part of the ImageN
 * API. Future releases of ImageN will make use of new classes in their place. This class will change accordingly.</b>
 *
 * <p>
 *
 * <table border=1>
 * <caption>Resource List</caption>
 * <tr><th>Name</th>        <th>Value</th></tr>
 * <tr><td>GlobalName</td>  <td>fileload</td></tr>
 * <tr><td>LocalName</td>   <td>fileload</td></tr>
 * <tr><td>Vendor</td>      <td>org.eclipse.imagen.media</td></tr>
 * <tr><td>Description</td> <td>Reads an image from a file.</td></tr>
 * <tr><td>DocURL</td>      <td>http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/URLDescriptor.html</td></tr>
 * <tr><td>Version</td>     <td>1.0</td></tr>
 * <tr><td>arg0Desc</td>    <td>The path of the file to read from.</td></tr>
 * <tr><td>arg1Desc</td>    <td>The ImageDecodeParam to use.</td></tr>
 * </table>
 *
 * <p>
 *
 * <table border=1>
 * <caption>Parameter List</caption>
 * <tr><th>Name</th>          <th>Class Type</th>
 *                            <th>Default Value</th></tr>
 * <tr><td>URL</td>           <td>java.net.URL</td>
 *                            <td>NO_PARAMETER_DEFAULT</td>
 * <tr><td>param</td>         <td>org.eclipse.imagen.media.codec.ImageDecodeParam</td>
 *                            <td>null</td>
 * </table>
 *
 * @see org.eclipse.imagen.OperationDescriptor
 */
public class URLDescriptor extends OperationDescriptorImpl {

    /**
     * The resource strings that provide the general documentation and specify the parameter list for the "URL"
     * operation.
     */
    private static final String[][] resources = {
        {"GlobalName", "URL"},
        {"LocalName", "URL"},
        {"Vendor", "org.eclipse.imagen.media"},
        {"Description", JaiI18N.getString("URLDescriptor0")},
        {
            "DocURL",
            "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/URLDescriptor.html"
        },
        {"Version", JaiI18N.getString("DescriptorVersion")},
        {"arg0Desc", JaiI18N.getString("URLDescriptor1")},
        {"arg1Desc", JaiI18N.getString("URLDescriptor2")}
    };

    /** The parameter names for the "URL" operation. */
    private static final String[] paramNames = {"URL", "param"};

    /** The parameter class types for the "URL" operation. */
    private static final Class[] paramClasses = {
        java.net.URL.class, org.eclipse.imagen.media.codec.ImageDecodeParam.class
    };

    /** The parameter default values for the "URL" operation. */
    private static final Object[] paramDefaults = {NO_PARAMETER_DEFAULT, null};

    /** Constructor. */
    public URLDescriptor() {
        super(resources, 0, paramClasses, paramNames, paramDefaults);
    }

    /**
     * Reads an image from a URL.
     *
     * <p>Creates a <code>ParameterBlockJAI</code> from all supplied arguments except <code>hints</code> and invokes
     * {@link ImageN#create(String,ParameterBlock,RenderingHints)}.
     *
     * @see ImageN
     * @see ParameterBlockJAI
     * @see RenderedOp
     * @param URL The URL to read from.
     * @param param The ImageDecodeParam to use. May be <code>null</code>.
     * @param hints The <code>RenderingHints</code> to use. May be <code>null</code>.
     * @return The <code>RenderedOp</code> destination.
     * @throws IllegalArgumentException if <code>URL</code> is <code>null</code>.
     */
    public static RenderedOp create(URL URL, ImageDecodeParam param, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("URL", RenderedRegistryMode.MODE_NAME);

        pb.setParameter("URL", URL);
        pb.setParameter("param", param);

        return ImageN.create("URL", pb, hints);
    }
}
