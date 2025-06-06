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
import org.eclipse.imagen.JAI;
import org.eclipse.imagen.OperationDescriptorImpl;
import org.eclipse.imagen.ParameterBlockJAI;
import org.eclipse.imagen.ROI;
import org.eclipse.imagen.RenderedOp;
import org.eclipse.imagen.registry.RenderedRegistryMode;

/**
 * An <code>OperationDescriptor</code> describing the "Mean" operation.
 *
 * <p>The Mean operation scans a specific region of a rendered image and computes the mean pixel value for each band
 * within that region of the image. The image data pass through this operation unchanged.
 *
 * <p>The region-wise mean pixel value for each band may be retrieved by calling the <code>getProperty</code> method on
 * this operation with "mean" as the property name. The return value has type <code>double[#bands]</code>.
 *
 * <p>The region of interest (ROI) does not have to be a rectangle. It may be <code>null</code>, in which case the
 * entire image is scanned to find the image-wise mean pixel value for each band.
 *
 * <p>The set of pixels scanned may be further reduced by specifying the "xPeriod" and "yPeriod" parameters that
 * represent the sampling rate along each axis. These variables may not be less than 1. However, they may be <code>null
 * </code>, in which case the sampling rate is set to 1; that is, every pixel in the ROI is processed.
 *
 * <p>
 *
 * <table border=1>
 * <caption>Resource List</caption>
 * <tr><th>Name</th>        <th>Value</th></tr>
 * <tr><td>GlobalName</td>  <td>Mean</td></tr>
 * <tr><td>LocalName</td>   <td>Mean</td></tr>
 * <tr><td>Vendor</td>      <td>org.eclipse.imagen.media</td></tr>
 * <tr><td>Description</td> <td>Calculates the region-wise mean pixel value
 *                              for each band of an image.</td></tr>
 * <tr><td>DocURL</td>      <td>http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/MeanDescriptor.html</td></tr>
 * <tr><td>Version</td>     <td>1.0</td></tr>
 * <tr><td>arg0Desc</td>    <td>The region of the image to scan.</td></tr>
 * <tr><td>arg1Desc</td>    <td>The horizontal sampling rate,
 *                              may not be less than 1.</td></tr>
 * <tr><td>arg2Desc</td>    <td>The vertical sampling rate,
 *                              may not be less than 1.</td></tr>
 * </table>
 *
 * <p>
 *
 * <table border=1>
 * <caption>Parameter List</caption>
 * <tr><th>Name</th>          <th>Class Type</th>
 *                            <th>Default Value</th></tr>
 * <tr><td>roi</td>           <td>org.eclipse.imagen.ROI</td>
 *                            <td>null</td>
 * <tr><td>xPeriod</td>       <td>java.lang.Integer</td>
 *                            <td>1</td>
 * <tr><td>yPeriod</td>       <td>java.lang.Integer</td>
 *                            <td>1</td>
 * </table>
 *
 * @see org.eclipse.imagen.ROI
 * @see org.eclipse.imagen.OperationDescriptor
 */
public class MeanDescriptor extends OperationDescriptorImpl {

    /**
     * The resource strings that provide the general documentation and specify the parameter list for this operation.
     */
    private static final String[][] resources = {
        {"GlobalName", "Mean"},
        {"LocalName", "Mean"},
        {"Vendor", "org.eclipse.imagen.media"},
        {"Description", JaiI18N.getString("MeanDescriptor0")},
        {
            "DocURL",
            "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/MeanDescriptor.html"
        },
        {"Version", JaiI18N.getString("DescriptorVersion")},
        {"arg0Desc", JaiI18N.getString("MeanDescriptor1")},
        {"arg1Desc", JaiI18N.getString("MeanDescriptor2")},
        {"arg2Desc", JaiI18N.getString("MeanDescriptor3")}
    };

    /** The parameter name list for this operation. */
    private static final String[] paramNames = {"roi", "xPeriod", "yPeriod"};

    /** The parameter class list for this operation. */
    private static final Class[] paramClasses = {
        org.eclipse.imagen.ROI.class, java.lang.Integer.class, java.lang.Integer.class
    };

    /** The parameter default value list for this operation. */
    private static final Object[] paramDefaults = {null, new Integer(1), new Integer(1)};

    /** Constructor. */
    public MeanDescriptor() {
        super(resources, 1, paramClasses, paramNames, paramDefaults);
    }

    /** Returns the minimum legal value of a specified numeric parameter for this operation. */
    public Number getParamMinValue(int index) {
        if (index == 0) {
            return null;
        } else if (index == 1 || index == 2) {
            return new Integer(1);
        } else {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    /**
     * Calculates the image-wise mean pixel value for each band of an image.
     *
     * <p>Creates a <code>ParameterBlockJAI</code> from all supplied arguments except <code>hints</code> and invokes
     * {@link JAI#create(String,ParameterBlock,RenderingHints)}.
     *
     * @see JAI
     * @see ParameterBlockJAI
     * @see RenderedOp
     * @param source0 <code>RenderedImage</code> source 0.
     * @param roi The region of the image to scan. May be <code>null</code>.
     * @param xPeriod The horizontal sampling rate, may not be less than 1. May be <code>null</code>.
     * @param yPeriod The vertical sampling rate, may not be less than 1. May be <code>null</code>.
     * @param hints The <code>RenderingHints</code> to use. May be <code>null</code>.
     * @return The <code>RenderedOp</code> destination.
     * @throws IllegalArgumentException if <code>source0</code> is <code>null</code>.
     */
    public static RenderedOp create(
            RenderedImage source0, ROI roi, Integer xPeriod, Integer yPeriod, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("Mean", RenderedRegistryMode.MODE_NAME);

        pb.setSource("source0", source0);

        pb.setParameter("roi", roi);
        pb.setParameter("xPeriod", xPeriod);
        pb.setParameter("yPeriod", yPeriod);

        return JAI.create("Mean", pb, hints);
    }
}
