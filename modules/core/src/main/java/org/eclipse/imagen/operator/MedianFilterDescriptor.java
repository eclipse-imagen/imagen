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
import org.eclipse.imagen.PropertyGenerator;
import org.eclipse.imagen.RenderedOp;
import org.eclipse.imagen.media.util.AreaOpPropertyGenerator;
import org.eclipse.imagen.registry.RenderedRegistryMode;

/**
 * An <code>OperationDescriptor</code> describing the "MedianFilter" operation.
 *
 * <p>The "MedianFilter" operation is a non-linear filter which is useful for removing isolated lines or pixels while
 * preserving the overall appearance of an image. The filter is implemented by moving a mask over the image. For each
 * position of the mask, the center pixel is replaced by the median of the pixel values covered by the mask.
 *
 * <p>There are several shapes possible for the mask. The MedianFilter operation supports three shapes, as follows:
 *
 * <p>Square Mask:
 *
 * <pre>
 *                       x x x
 *                       x x x
 *                       x x x
 * </pre>
 *
 * <p>Plus Mask:
 *
 * <pre>
 *                         x
 *                       x x x
 *                         x
 * </pre>
 *
 * <p>X Mask:
 *
 * <pre>
 *                       x   x
 *                         x
 *                       x   x
 * </pre>
 *
 * <p>The Median operation may also be used to compute the "separable median" of a 3x3 or 5x5 region of pixels. The
 * separable median is defined as the median of the medians of each row. For example, if the pixel values in a 3x3
 * window are equal to:
 *
 * <pre>
 * [ 1 2 3 ]
 * [ 5 6 7 ]
 * [ 4 8 9 ]
 * </pre>
 *
 * then the overall (non-separable) median value is 5, while the separable median is equal to the median of the three
 * row medians: median(1, 2, 3) = 2, median(5, 6, 7) = 6, and median(4, 8, 9) = 8, yielding an overall median of 6. The
 * separable median may be obtained by specifying a mask of type MEDIAN_MASK_SQUARE_SEPARABLE.
 *
 * <p>It should be noted that this operation automatically adds a value of <code>Boolean.TRUE</code> for the <code>
 * JAI.KEY_REPLACE_INDEX_COLOR_MODEL</code> to the given <code>configuration</code> so that the operation is performed
 * on the pixel values instead of being performed on the indices into the color map if the source(s) have an <code>
 * IndexColorModel</code>. This addition will take place only if a value for the <code>JAI.KEY_REPLACE_INDEX_COLOR_MODEL
 * </code> has not already been provided by the user. Note that the <code>configuration</code> Map is cloned before the
 * new hint is added to it. The operation can be smart about the value of the <code>JAI.KEY_REPLACE_INDEX_COLOR_MODEL
 * </code> <code>RenderingHints</code>, i.e. while the default value for the <code>JAI.KEY_REPLACE_INDEX_COLOR_MODEL
 * </code> is <code>Boolean.TRUE</code>, in some cases the operator could set the default.
 *
 * <p>
 *
 * <table align=center border=1>
 * <caption>Resource List</caption>
 * <tr><th>Name</th>        <th>Value</th></tr>
 * <tr><td>GlobalName</td>  <td>MedianFilter</td></tr>
 * <tr><td>LocallName</td>  <td>MedianFilter</td></tr>
 * <tr><td>Vendor</td>      <td>org.eclipse.imagen.media</td></tr>
 * <tr><td>Description</td> <td>Performs median filtering on an image.</td></tr>
 * <tr><td>DocURL</td>      <td>http://java.sun.com/products/java-media/jai/forDevelopers/jaiapi/org.eclipse.imagen.operator.MedianFilterDescriptor.html</td></tr>
 * <tr><td>Version</td>     <td>1.0</td></tr>
 * <tr><td>arg0Desc</td>    <td>The shape of the mask to be used for Median Filtering.</td></tr>
 * <tr><td>arg1Desc</td>    <td>The size (width/height) of the mask to be used in Median Filtering.</td></tr>
 * </table>
 *
 * <p>
 *
 * <table align=center border=1>
 * <caption>Parameter List</caption>
 * <tr><th>Name</th>   <th>Class Type</th>
 *                     <th>Default Value</th></tr>
 * <tr><td>maskShape</td> <td>org.eclipse.imagen.operator.MedianFilterShape</td>
 *                     <td>MEDIAN_MASK_SQUARE</td>
 * <tr><td>maskSize</td> <td>java.lang.Integer</td>
 *                     <td>3</td>
 * </table>
 *
 * @see org.eclipse.imagen.OperationDescriptor
 * @see MedianFilterShape
 */
public class MedianFilterDescriptor extends OperationDescriptorImpl {

    /** Default 3x3 Windows */

    /** Square shaped mask. */
    public static final MedianFilterShape MEDIAN_MASK_SQUARE = new MedianFilterShape("MEDIAN_MASK_SQUARE", 1);

    /** Plus shaped mask. */
    public static final MedianFilterShape MEDIAN_MASK_PLUS = new MedianFilterShape("MEDIAN_MASK_PLUS", 2);

    /** X shaped mask. */
    public static final MedianFilterShape MEDIAN_MASK_X = new MedianFilterShape("MEDIAN_MASK_X", 3);

    /** Separable square mask. */
    public static final MedianFilterShape MEDIAN_MASK_SQUARE_SEPARABLE =
            new MedianFilterShape("MEDIAN_MASK_SQUARE_SEPARABLE", 4);

    /**
     * The resource strings that provide the general documentation and specify the parameter list for this operation.
     */
    private static final String[][] resources = {
        {"GlobalName", "MedianFilter"},
        {"LocalName", "MedianFilter"},
        {"Vendor", "org.eclipse.imagen.media"},
        {"Description", JaiI18N.getString("MedianFilterDescriptor0")},
        {
            "DocURL",
            "http://java.sun.com/products/java-media/jai/forDevelopers/jaiapi/org.eclipse.imagen.operator.MedianFilterDescriptor.html"
        },
        {"Version", JaiI18N.getString("DescriptorVersion2")},
        {"arg0Desc", JaiI18N.getString("MedianFilterDescriptor1")},
        {"arg1Desc", JaiI18N.getString("MedianFilterDescriptor2")}
    };

    /** The parameter class list for this operation. */
    private static final Class[] paramClasses = {MedianFilterShape.class, java.lang.Integer.class};

    /** The parameter name list for this operation. */
    private static final String[] paramNames = {"maskShape", "maskSize"};

    /** The parameter default value list for this operation. */
    private static final Object[] paramDefaults = {MEDIAN_MASK_SQUARE, new Integer(3)};

    /** Constructor for the MedianFilterDescriptor. */
    public MedianFilterDescriptor() {
        super(resources, 1, paramClasses, paramNames, paramDefaults);
    }

    /** Returns the minimum legal value of a specified numeric parameter for this operation. */
    public Number getParamMinValue(int index) {
        if (index == 0) {
            return null;
        } else if (index == 1) {
            return new Integer(1);
        } else {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    /** Returns the maximum legal value of a specified numeric parameter for this operation. */
    public Number getParamMaxValue(int index) {
        if (index == 0) {
            return null;
        } else if (index == 1) {
            return new Integer(Integer.MAX_VALUE);
        } else {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    /**
     * Returns an array of <code>PropertyGenerators</code> implementing property inheritance for the "MedianFilter"
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
     * Performs median filtering on an image.
     *
     * <p>Creates a <code>ParameterBlockJAI</code> from all supplied arguments except <code>hints</code> and invokes
     * {@link JAI#create(String,ParameterBlock,RenderingHints)}.
     *
     * @see JAI
     * @see ParameterBlockJAI
     * @see RenderedOp
     * @param source0 <code>RenderedImage</code> source 0.
     * @param maskShape The mask shape to be used for Median Filtering. May be <code>null</code>.
     * @param maskSize The mask size to be used for Median Filtering. May be <code>null</code>.
     * @param hints The <code>RenderingHints</code> to use. May be <code>null</code>.
     * @return The <code>RenderedOp</code> destination.
     * @throws IllegalArgumentException if <code>source0</code> is <code>null</code>.
     */
    public static RenderedOp create(
            RenderedImage source0, MedianFilterShape maskShape, Integer maskSize, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("MedianFilter", RenderedRegistryMode.MODE_NAME);

        pb.setSource("source0", source0);

        pb.setParameter("maskShape", maskShape);
        pb.setParameter("maskSize", maskSize);

        return JAI.create("MedianFilter", pb, hints);
    }
}
