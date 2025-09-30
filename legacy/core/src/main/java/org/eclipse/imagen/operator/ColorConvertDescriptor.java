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

import java.awt.*;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import org.eclipse.imagen.*;
import org.eclipse.imagen.registry.RenderableRegistryMode;
import org.eclipse.imagen.registry.RenderedRegistryMode;

/**
 * An <code>OperationDescriptor</code> describing the "ColorConvert" operation.
 *
 * <p>The "ColorConvert" operation performs a pixel-by-pixel color conversion of the data in a rendered or renderable
 * source image.
 *
 * <p>The data are treated as having no alpha channel, i.e., all bands are color bands. The color space of the source
 * image is specified by the <code>ColorSpace</code> object of the source image <code>ColorModel</code> which must not
 * be <code>null</code>. The color space of the destination image is specified by the <code>ColorSpace</code> of the
 * "colorModel" parameter which must be a <code>ColorModel</code>. If a <code>ColorModel</code> is suggested via the
 * <code>RenderingHints</code> it is ignored.
 *
 * <p>The calculation pathway is selected to optimize performance and accuracy based on which <code>ColorSpace</code>
 * subclasses are used to represent the source and destination color spaces. The subclass categories are <code>
 * ICC_ColorSpace</code>, <code>ColorSpaceJAI</code>, and generic <code>ColorSpace</code>, i.e., one which is not an
 * instance of either the two aforementioned subclasses. Note that in the Sun Microsystems implementation, an <code>
 * ICC_ColorSpace</code> instance is what is returned by <code>ColorSpace.getInstance()</code>.
 *
 * <p>Integral data are assumed to occupy the full range of the respective data type; floating point data are assumed to
 * be normalized to the range [0.0,1.0].
 *
 * <p>By default, the destination image bounds, data type, and number of bands are the same as those of the source
 * image.
 *
 * <p>
 *
 * <table border=1>
 * <caption>Resource List</caption>
 * <tr><th>Name</th>        <th>Value</th></tr>
 * <tr><td>GlobalName</td>  <td>ColorConvert</td></tr>
 * <tr><td>LocalName</td>   <td>ColorConvert</td></tr>
 * <tr><td>Vendor</td>      <td>org.eclipse.imagen.media</td></tr>
 * <tr><td>Description</td> <td>Convert the color space of an image.<td></tr>
 * <tr><td>DocURL</td>      <td>http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/ColorConvertDescriptor.html</td></tr>
 * <tr><td>Version</td>     <td>1.0</td></tr>
 * <tr><td>arg0Desc</td>    <td>The destination <code>ColorModel</code>.</td></tr>
 * </table>
 *
 * <p>
 *
 * <table border=1>
 * <caption>Parameter List</caption>
 * <tr><th>Name</th>      <th>Class Type</th>
 *                        <th>Default Value</th></tr>
 * <tr><td>colorModel</td> <td>java.awt.image.ColorModel</td>
 *                        <td>NO_PARAMETER_DEFAULT</td>
 * </table>
 *
 * @see org.eclipse.imagen.OperationDescriptor
 * @see java.awt.color.ColorSpace
 * @see java.awt.color.ICC_ColorSpace
 * @see ColorModel
 * @see org.eclipse.imagen.ColorSpaceJAI
 * @see org.eclipse.imagen.IHSColorSpace
 */
public class ColorConvertDescriptor extends OperationDescriptorImpl {

    /**
     * The resource strings that provide the general documentation and specify the parameter list for this operation.
     */
    private static final String[][] resources = {
        {"GlobalName", "ColorConvert"},
        {"LocalName", "ColorConvert"},
        {"Vendor", "org.eclipse.imagen.media"},
        {"Description", JaiI18N.getString("ColorConvertDescriptor0")},
        {
            "DocURL",
            "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/ColorConvertDescriptor.html"
        },
        {"Version", JaiI18N.getString("DescriptorVersion2")},
        {"arg0Desc", JaiI18N.getString("ColorConvertDescriptor1")}
    };

    /** The parameter class list for this operation. */
    private static final Class[] paramClasses = {ColorModel.class};

    /** The parameter name list for this operation. */
    private static final String[] paramNames = {"colorModel"};

    /** The parameter default value list for this operation. */
    private static final Object[] paramDefaults = {NO_PARAMETER_DEFAULT};

    /** Constructor. */
    public ColorConvertDescriptor() {
        super(resources, 1, paramClasses, paramNames, paramDefaults);
    }

    /** Returns <code>true</code> since renderable operation is supported. */
    public boolean isRenderableSupported() {
        return true;
    }

    /**
     * Convert the color space of an image.
     *
     * <p>Creates a <code>ParameterBlockImageN</code> from all supplied arguments except <code>hints</code> and invokes
     * {@link ImageN#create(String,ParameterBlock,RenderingHints)}.
     *
     * @see ImageN
     * @see ParameterBlockImageN
     * @see RenderedOp
     * @param source0 <code>RenderedImage</code> source 0.
     * @param colorModel The destination color space.
     * @param hints The <code>RenderingHints</code> to use. May be <code>null</code>.
     * @return The <code>RenderedOp</code> destination.
     * @throws IllegalArgumentException if <code>source0</code> is <code>null</code>.
     * @throws IllegalArgumentException if <code>colorModel</code> is <code>null</code>.
     */
    public static RenderedOp create(RenderedImage source0, ColorModel colorModel, RenderingHints hints) {
        ParameterBlockImageN pb = new ParameterBlockImageN("ColorConvert", RenderedRegistryMode.MODE_NAME);

        pb.setSource("source0", source0);

        pb.setParameter("colorModel", colorModel);

        return ImageN.create("ColorConvert", pb, hints);
    }

    /**
     * Convert the color space of an image.
     *
     * <p>Creates a <code>ParameterBlockImageN</code> from all supplied arguments except <code>hints</code> and invokes
     * {@link ImageN#createRenderable(String,ParameterBlock,RenderingHints)}.
     *
     * @see ImageN
     * @see ParameterBlockImageN
     * @see RenderableOp
     * @param source0 <code>RenderableImage</code> source 0.
     * @param colorModel The destination color space.
     * @param hints The <code>RenderingHints</code> to use. May be <code>null</code>.
     * @return The <code>RenderableOp</code> destination.
     * @throws IllegalArgumentException if <code>source0</code> is <code>null</code>.
     * @throws IllegalArgumentException if <code>colorModel</code> is <code>null</code>.
     */
    public static RenderableOp createRenderable(RenderableImage source0, ColorModel colorModel, RenderingHints hints) {
        ParameterBlockImageN pb = new ParameterBlockImageN("ColorConvert", RenderableRegistryMode.MODE_NAME);

        pb.setSource("source0", source0);

        pb.setParameter("colorModel", colorModel);

        return ImageN.createRenderable("ColorConvert", pb, hints);
    }
}
