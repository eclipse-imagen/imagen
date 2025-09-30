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
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;
import org.eclipse.imagen.ColorCube;
import org.eclipse.imagen.ImageN;
import org.eclipse.imagen.KernelJAI;
import org.eclipse.imagen.OperationDescriptorImpl;
import org.eclipse.imagen.ParameterBlockImageN;
import org.eclipse.imagen.RenderedOp;
import org.eclipse.imagen.registry.RenderedRegistryMode;

/**
 * An <code>OperationDescriptor</code> describing the "OrderedDither" operation.
 *
 * <p>The "OrderedDither" operation performs color quantization by finding the nearest color to each pixel in a supplied
 * color cube and "shifting" the resulting index value by a pseudo-random amount determined by the values of a supplied
 * dither mask.
 *
 * <p>The dither mask is supplied as an array of <code>KernelJAI</code> objects the length of which must equal the
 * number of bands in the image. Each element of the array is a <code>KernelJAI</code> object which represents the
 * dither mask matrix for the corresponding band. All <code>KernelJAI</code> objects in the array must have the same
 * dimensions and contain floating point values greater than or equal to 0.0 and less than or equal to 1.0.
 *
 * <p>For all integral data types, the source image samples are presumed to occupy the full range of the respective
 * types. For floating point data types it is assumed that the data samples have been scaled to the range [0.0, 1.0].
 *
 * <p>
 *
 * <table border=1>
 * <caption>Resource List</caption>
 * <tr><th>Name</th>        <th>Value</th></tr>
 * <tr><td>GlobalName</td>  <td>OrderedDither</td></tr>
 * <tr><td>LocalName</td>   <td>OrderedDither</td></tr>
 * <tr><td>Vendor</td>      <td>org.eclipse.imagen.media</td></tr>
 * <tr><td>Description</td> <td>Performs ordered dither color quantization
 *                              using a specified color cube and
 *                              dither mask.</td></tr>
 * <tr><td>DocURL</td>      <td>http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/OrderedDitherDescriptor.html</td></tr>
 * <tr><td>Version</td>     <td>1.0</td></tr>
 * <tr><td>arg0Desc</td>    <td>The color cube.</td></tr>
 * <tr><td>arg1Desc</td>    <td>The dither mask.</td></tr>
 * </table>
 *
 * <p>
 *
 * <table border=1>
 * <caption>Parameter List</caption>
 * <tr><th>Name</th>          <th>Class Type</th>
 *                            <th>Default Value</th></tr>
 * <tr><td>colorMap</td>      <td>org.eclipse.imagen.ColorCube</td>
 *                            <td>ColorCube.BYTE_496</td>
 * <tr><td>ditherMask</td>   <td>org.eclipse.imagen.KernelJAI[]</td>
 *                            <td>KernelJAI.DITHER_MASK_443</td>
 * </table>
 *
 * @see org.eclipse.imagen.KernelJAI
 * @see org.eclipse.imagen.ColorCube
 * @see org.eclipse.imagen.OperationDescriptor
 */
public class OrderedDitherDescriptor extends OperationDescriptorImpl {

    /**
     * The resource strings that provide the general documentation and specify the parameter list for this operation.
     */
    private static final String[][] resources = {
        {"GlobalName", "OrderedDither"},
        {"LocalName", "OrderedDither"},
        {"Vendor", "org.eclipse.imagen.media"},
        {"Description", JaiI18N.getString("OrderedDitherDescriptor0")},
        {
            "DocURL",
            "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/OrderedDitherDescriptor.html"
        },
        {"Version", JaiI18N.getString("DescriptorVersion")},
        {"arg0Desc", JaiI18N.getString("OrderedDitherDescriptor1")},
        {"arg1Desc", JaiI18N.getString("OrderedDitherDescriptor2")}
    };

    /** The parameter names for the "OrderedDither" operation. */
    private static final String[] paramNames = {"colorMap", "ditherMask"};

    /** The parameter class types for the "OrderedDither" operation. */
    private static final Class[] paramClasses = {
        org.eclipse.imagen.ColorCube.class, org.eclipse.imagen.KernelJAI[].class
    };

    /** The parameter default values for the "OrderedDither" operation. */
    private static final Object[] paramDefaults = {ColorCube.BYTE_496, KernelJAI.DITHER_MASK_443};

    private static final String[] supportedModes = {"rendered"};

    /**
     * Method to check the validity of the color map parameter. The supplied color cube must have the same data type and
     * number of bands as the source image.
     *
     * @param sourceImage The source image of the operation.
     * @param colorMap The color cube.
     * @param msg The buffer to which messages should be appended.
     * @return Whether the color map is valid.
     */
    private static boolean isValidColorMap(RenderedImage sourceImage, ColorCube colorMap, StringBuffer msg) {
        SampleModel srcSampleModel = sourceImage.getSampleModel();

        if (colorMap.getDataType() != srcSampleModel.getTransferType()) {
            msg.append(JaiI18N.getString("OrderedDitherDescriptor3"));
            return false;
        } else if (colorMap.getNumBands() != srcSampleModel.getNumBands()) {
            msg.append(JaiI18N.getString("OrderedDitherDescriptor4"));
            return false;
        }

        return true;
    }

    /**
     * Method to check the validity of the dither mask parameter. The dither mask is an array of <code>KernelJAI</code>
     * objects wherein the number of elements in the array must equal the number of bands in the source image.
     * Furthermore all kernels in the array must have the same width and height. Finally all data elements of all
     * kernels must be greater than or equal to zero and less than or equal to unity.
     *
     * @param sourceImage The source image of the operation.
     * @param ditherMask The dither mask.
     * @param msg The buffer to which messages should be appended.
     * @return Whether the dither mask is valid.
     */
    private static boolean isValidDitherMask(RenderedImage sourceImage, KernelJAI[] ditherMask, StringBuffer msg) {
        if (ditherMask.length != sourceImage.getSampleModel().getNumBands()) {
            msg.append(JaiI18N.getString("OrderedDitherDescriptor5"));
            return false;
        }

        int maskWidth = ditherMask[0].getWidth();
        int maskHeight = ditherMask[0].getHeight();
        for (int band = 0; band < ditherMask.length; band++) {
            if (ditherMask[band].getWidth() != maskWidth || ditherMask[band].getHeight() != maskHeight) {
                msg.append(JaiI18N.getString("OrderedDitherDescriptor6"));
                return false;
            }
            float[] kernelData = ditherMask[band].getKernelData();
            for (int i = 0; i < kernelData.length; i++) {
                if (kernelData[i] < 0.0F || kernelData[i] > 1.0) {
                    msg.append(JaiI18N.getString("OrderedDitherDescriptor7"));
                    return false;
                }
            }
        }

        return true;
    }

    /** Constructor. */
    public OrderedDitherDescriptor() {
        super(resources, supportedModes, 1, paramNames, paramClasses, paramDefaults, null);
    }

    /**
     * Validates the input source and parameters.
     *
     * <p>In addition to the standard checks performed by the superclass method, this method checks that "colorMap" and
     * "ditherMask" are valid for the given source image.
     */
    public boolean validateArguments(String modeName, ParameterBlock args, StringBuffer msg) {
        if (!super.validateArguments(modeName, args, msg)) {
            return false;
        }

        if (!modeName.equalsIgnoreCase("rendered")) return true;

        // Retrieve the operation source and parameters.
        RenderedImage src = args.getRenderedSource(0);
        ColorCube colorMap = (ColorCube) args.getObjectParameter(0);
        KernelJAI[] ditherMask = (KernelJAI[]) args.getObjectParameter(1);

        // Check color map validity.
        if (!isValidColorMap(src, colorMap, msg)) {
            return false;
        }

        // Check dither mask validity.
        if (!isValidDitherMask(src, ditherMask, msg)) {
            return false;
        }

        return true;
    }

    /**
     * Performs ordered dither color quantization using a specified color cube and dither mask.
     *
     * <p>Creates a <code>ParameterBlockImageN</code> from all supplied arguments except <code>hints</code> and invokes
     * {@link ImageN#create(String,ParameterBlock,RenderingHints)}.
     *
     * @see ImageN
     * @see ParameterBlockImageN
     * @see RenderedOp
     * @param source0 <code>RenderedImage</code> source 0.
     * @param colorMap The color cube. May be <code>null</code>.
     * @param ditherMask The dither mask. May be <code>null</code>.
     * @param hints The <code>RenderingHints</code> to use. May be <code>null</code>.
     * @return The <code>RenderedOp</code> destination.
     * @throws IllegalArgumentException if <code>source0</code> is <code>null</code>.
     */
    public static RenderedOp create(
            RenderedImage source0, ColorCube colorMap, KernelJAI[] ditherMask, RenderingHints hints) {
        ParameterBlockImageN pb = new ParameterBlockImageN("OrderedDither", RenderedRegistryMode.MODE_NAME);

        pb.setSource("source0", source0);

        pb.setParameter("colorMap", colorMap);
        pb.setParameter("ditherMask", ditherMask);

        return ImageN.create("OrderedDither", pb, hints);
    }
}
