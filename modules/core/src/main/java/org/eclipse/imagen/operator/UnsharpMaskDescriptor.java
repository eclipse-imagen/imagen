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
import org.eclipse.imagen.JAI;
import org.eclipse.imagen.KernelJAI;
import org.eclipse.imagen.OperationDescriptorImpl;
import org.eclipse.imagen.ParameterBlockJAI;
import org.eclipse.imagen.PropertyGenerator;
import org.eclipse.imagen.RenderedOp;
import org.eclipse.imagen.media.util.AreaOpPropertyGenerator;
import org.eclipse.imagen.registry.RenderedRegistryMode;

/**
 * An <code>OperationDescriptor</code> describing the "UnsharpMask" operation.
 *
 * <p>Unsharp masking is derived from a photographic technique for improving the sharpness of images. In its digital
 * form it is implemented using convolution to create a low-pass filtered version of a source image. The low-pass image
 * is then subtracted from the original image, creating a high-pass image. The high pass image is then added back to the
 * original image, creating enhanced edge contrast. By adjusting a scaling factor, the degree of high pass add-back can
 * be controlled.
 *
 * <p>The operation is implemented algorithmically as follows. At each original pixel location x,y:
 *
 * <pre>
 *    result = original + (original - lowpass) * gainFactor
 *
 *     where
 *      original = value at position x,y of source image
 *      lowpass  = result of convolution with lowpass filter
 *                 centered at pixel x,y
 *      gain     = controlling parameter for degree of sharpness
 *                   gain = 0 : no effect
 *                   gain > 0 : sharpening
 *                   -1 < gain < 0 : smoothing
 * </pre>
 *
 * <p>In general gain factors should be restricted to a range of [-1, 2], as higher magnitude values are likely to cause
 * overflows or underflows which must be clamped to the image data type's range.
 *
 * <p>The default gain factor is set to <code>1,0F</code>.
 *
 * <p>This operation is widely applied to scanned image enhancement. The typical gain factor for scanned images takes
 * values in the range of [1/4, 2] (page 278 in Digital Image Processing by William K. Pratt, 3rd).
 *
 * <p>
 *
 * <table border=1>
 * <caption>Resource List</caption>
 * <tr><th>Name</th>        <th>Value</th></tr>
 * <tr><td>GlobalName</td>  <td>UnsharpMask</td></tr>
 * <tr><td>LocalName</td>   <td>UnsharpMask</td></tr>
 * <tr><td>Vendor</td>      <td>org.eclipse.imagen.media</td></tr>
 * <tr><td>Description</td> <td>Performs unsharp masking to sharpen or smooth
 *                              an image.</td></tr>
 * <tr><td>DocURL</td>      <td>http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/UnsharpMaskDescriptor.html</td></tr>
 * <tr><td>Version</td>     <td>1.1</td></tr>
 * <tr><td>arg0Desc</td>    <td>The convolution kernel.</td></tr>
 * <tr><td>arg1Desc</td>    <td>The gain factor.</td></tr>
 * </table>
 *
 * <p>
 *
 * <table border=1>
 * <caption>Parameter List</caption>
 * <tr><th>Name</th>   <th>Class Type</th>
 *                     <th>Default Value</th></tr>
 * <tr><td>kernel</td> <td>org.eclipse.imagen.KernelJAI</td>
 *                     <td>3 X 3 average</td>
 * <tr><td>gain</td> <td>java.lang.Float</td>
 *                     <td>1.0F</td>
 * </table>
 *
 * @see org.eclipse.imagen.OperationDescriptor
 * @see org.eclipse.imagen.KernelJAI
 * @see org.eclipse.imagen.operator.ConvolveDescriptor
 * @since JAI 1.1
 */
public class UnsharpMaskDescriptor extends OperationDescriptorImpl {

    /**
     * The resource strings that provide the general documentation and specify the parameter list for a UnsharpMask
     * operation.
     */
    private static final String[][] resources = {
        {"GlobalName", "UnsharpMask"},
        {"LocalName", "UnsharpMask"},
        {"Vendor", "org.eclipse.imagen.media"},
        {"Description", JaiI18N.getString("UnsharpMaskDescriptor0")},
        {
            "DocURL",
            "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/UnsharpMaskDescriptor.html"
        },
        {"Version", JaiI18N.getString("DescriptorVersion")},
        {"arg0Desc", JaiI18N.getString("UnsharpMaskDescriptor1")},
        {"arg1Desc", JaiI18N.getString("UnsharpMaskDescriptor2")}
    };

    /** The parameter names for the UnsharpMask operation. */
    private static final String[] paramNames = {"kernel", "gain"};

    /** The parameter class types for the UnsharpMask operation. */
    private static final Class[] paramClasses = {org.eclipse.imagen.KernelJAI.class, java.lang.Float.class};

    /** The parameter default values for the UnsharpMask operation. */
    private static final Object[] paramDefaults = {
        new KernelJAI(3, 3, 1, 1, new float[] {1 / 9F, 1 / 9F, 1 / 9F, 1 / 9F, 1 / 9F, 1 / 9F, 1 / 9F, 1 / 9F, 1 / 9F}),
        new Float(1.0F)
    };

    /** Constructor. */
    public UnsharpMaskDescriptor() {
        super(resources, 1, paramClasses, paramNames, paramDefaults);
    }

    /**
     * Returns an array of <code>PropertyGenerators</code> implementing property inheritance for the "UnsharpMask"
     * operation.
     *
     * @return An array of property generators.
     */
    public PropertyGenerator[] getPropertyGenerators() {
        PropertyGenerator[] pg = new PropertyGenerator[1];
        pg[0] = new AreaOpPropertyGenerator();
        return pg;
    }

    /**
     * Performs UnsharpMask operation on the image.
     *
     * <p>Creates a <code>ParameterBlockJAI</code> from all supplied arguments except <code>hints</code> and invokes
     * {@link JAI#create(String,ParameterBlock,RenderingHints)}.
     *
     * @see JAI
     * @see ParameterBlockJAI
     * @see RenderedOp
     * @param source0 <code>RenderedImage</code> source 0.
     * @param kernel The low-pass convolution kernel. May be <code>null</code>.
     * @param gain The sharpening value. May be <code>null</code>.
     * @param hints The <code>RenderingHints</code> to use. May be <code>null</code>.
     * @return The <code>RenderedOp</code> destination.
     * @throws IllegalArgumentException if <code>source0</code> is <code>null</code>.
     */
    public static RenderedOp create(RenderedImage source0, KernelJAI kernel, Float gain, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("UnsharpMask", RenderedRegistryMode.MODE_NAME);

        pb.setSource("source0", source0);

        pb.setParameter("kernel", kernel);
        pb.setParameter("gain", gain);

        return JAI.create("UnsharpMask", pb, hints);
    }
}
