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
 * SPDX-License-Identifier: Apache-2.0
 */
package org.eclipse.imagen.operator;

import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderableImage;
import org.eclipse.imagen.ImageN;
import org.eclipse.imagen.OperationDescriptorImpl;
import org.eclipse.imagen.ParameterBlockImageN;
import org.eclipse.imagen.PixelAccessor;
import org.eclipse.imagen.PropertyGenerator;
import org.eclipse.imagen.ROI;
import org.eclipse.imagen.ROIShape;
import org.eclipse.imagen.RenderableOp;
import org.eclipse.imagen.RenderedOp;
import org.eclipse.imagen.media.util.PropertyGeneratorImpl;
import org.eclipse.imagen.registry.RenderableRegistryMode;
import org.eclipse.imagen.registry.RenderedRegistryMode;

/** This property generator computes the properties for the operation "SubsampleBinaryToGray" dynamically. */
class SubsampleBinaryToGrayPropertyGenerator extends PropertyGeneratorImpl {

    /** Constructor. */
    public SubsampleBinaryToGrayPropertyGenerator() {
        super(new String[] {"ROI"}, new Class[] {ROI.class}, new Class[] {RenderedOp.class});
    }

    /**
     * Returns the specified property.
     *
     * @param name Property name.
     * @param opNode Operation node.
     */
    public Object getProperty(String name, Object opNode) {
        validate(name, opNode);

        if (opNode instanceof RenderedOp && name.equalsIgnoreCase("roi")) {
            RenderedOp op = (RenderedOp) opNode;

            ParameterBlock pb = op.getParameterBlock();

            // Retrieve the rendered source image and its ROI.
            RenderedImage src = pb.getRenderedSource(0);

            Object property = src.getProperty("ROI");
            if (property == null || property.equals(java.awt.Image.UndefinedProperty) || !(property instanceof ROI)) {
                return java.awt.Image.UndefinedProperty;
            }
            ROI srcROI = (ROI) property;

            // Determine the effective source bounds.
            Rectangle srcBounds = new Rectangle(src.getMinX(), src.getMinY(), src.getWidth(), src.getHeight());

            // If necessary, clip the ROI to the effective source bounds.
            if (!srcBounds.contains(srcROI.getBounds())) {
                srcROI = srcROI.intersect(new ROIShape(srcBounds));
            }

            // Retrieve the scale factors and translation values.
            float sx = pb.getFloatParameter(0);
            float sy = pb.getFloatParameter(1);

            // Create an equivalent transform.
            AffineTransform transform = new AffineTransform(sx, 0.0, 0.0, sy, 0.0, 0.0);

            // Create the scaled ROI.
            ROI dstROI = srcROI.transform(transform);

            // Retrieve the destination bounds.
            Rectangle dstBounds = op.getBounds();

            // If necessary, clip the warped ROI to the destination bounds.
            if (!dstBounds.contains(dstROI.getBounds())) {
                dstROI = dstROI.intersect(new ROIShape(dstBounds));
            }

            // Return the warped and possibly clipped ROI.
            return dstROI;
        }

        return java.awt.Image.UndefinedProperty;
    }
}

/**
 * An <code>OperationDescriptor</code> describing the "SubsampleBinaryToGray" operation.
 *
 * <p>The "SubsampleBinaryToGray" operation collects a Binary image pixels into a gray scale image. Roughly speaking,
 * each pixel (x, y) of the destination, is the sum of the source pixel values of the source pixel matrix of size
 * 1/xScale by 1/yScale. In the noninteger case, the next bigger inter is used. Thus when xScale = yScale = 1/2.2, a 3 x
 * 3 pixel matrix into a single destination pixel, resulting in 10 levels [0..9]. The resulting gray values are then
 * normalized to have gray values in [0..255].
 *
 * <p>The source is a Binary image and the result of the operation is a grayscale image. The scale factors <code> xScale
 * </code> and <code> yScale</code> must be between (0, 1] and strictly bigger than 0.
 *
 * <p>The destination image is a byte image whose dimensions are: <code><pre>
 *       dstWidth  = floor(srcWidth * xScale)
 *       dstHeight = floor(srcHeight * yScale)
 * </pre></code>
 *
 * <p>It may be noted that the minX, minY, width and height hints as specified through the <code>ImageN.KEY_IMAGE_LAYOUT
 * </code> hint in the <code>RenderingHints</code> object are not honored, as this operator calculates the destination
 * image bounds itself. The other <code>ImageLayout</code> hints, like tileWidth and tileHeight, however, are honored.
 *
 * <p>It should be noted that this operation automatically adds a value of <code>Boolean.FALSE</code> for the <code>
 * ImageN.KEY_REPLACE_INDEX_COLOR_MODEL</code> to the given <code>configuration</code> since this operation is capable
 * of dealing correctly with a source that has an <code>IndexColorModel</code>, without having to expand the <code>
 * IndexColorModel</code>. This addition will take place only if a value for the <code>
 * ImageN.KEY_REPLACE_INDEX_COLOR_MODEL
 * </code> has not already been provided by the user. Note that the <code>configuration</code> Map is cloned before the
 * new hint is added to it.
 *
 * <p>Specifying a scale factor of greater than 1 or less than 0 will cause an exception. To scale image sizes up or
 * down, see <code>Scale</code> operator.
 *
 * <p>
 *
 * <table border=1>
 * <caption>Resource List</caption>
 * <tr><th>Name</th>        <th>Value</th></tr>
 * <tr><td>GlobalName</td>  <td>SubsampleBinaryToGray</td></tr>
 * <tr><td>LocalName</td>   <td>SubsampleBinaryToGray</td></tr>
 * <tr><td>Vendor</td>      <td>org.eclipse.imagen.media</td></tr>
 * <tr><td>Description</td> <td>Subsamples a binary image into a gray scale image.</td></tr>
 * <tr><td>DocURL</td>      <td>http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/SubsampleBinaryToGrayDescriptor.html</td></tr>
 * <tr><td>Version</td>     <td>1.0</td></tr>
 * <tr><td>arg0Desc</td>    <td>The X scale factor.</td></tr>
 * <tr><td>arg1Desc</td>    <td>The Y scale factor.</td></tr>
 * </table>
 *
 * <p>
 *
 * <table border=1>
 * <caption>Parameter List</caption>
 * <tr><th>Name</th>          <th>Class Type</th>
 *                            <th>Default Value</th></tr>
 * <tr><td>xScale</td>        <td>java.lang.Float</td>
 *                            <td>1.0F</td>
 * <tr><td>yScale</td>        <td>java.lang.Float</td>
 *                            <td>1.0F</td>
 * </table>
 *
 * @see org.eclipse.imagen.OperationDescriptor
 */
public class SubsampleBinaryToGrayDescriptor extends OperationDescriptorImpl {

    /**
     * The resource strings that provide the general documentation and specify the parameter list for this operation.
     */
    private static final String[][] resources = {
        {"GlobalName", "SubsampleBinaryToGray"},
        {"LocalName", "SubsampleBinaryToGray"},
        {"Vendor", "org.eclipse.imagen.media"},
        {"Description", JaiI18N.getString("SubsampleBinaryToGray0")},
        {
            "DocURL",
            "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/SubsampleBinaryToGrayDescriptor.html"
        },
        {"Version", JaiI18N.getString("DescriptorVersion")},
        {"arg0Desc", JaiI18N.getString("SubsampleBinaryToGray1")},
        {"arg1Desc", JaiI18N.getString("SubsampleBinaryToGray2")}
    };

    /** The parameter class list for this operation. */
    private static final Class[] paramClasses = {java.lang.Float.class, java.lang.Float.class};

    /** The parameter name list for this operation. */
    private static final String[] paramNames = {"xScale", "yScale"};

    /** The parameter default value list for this operation. */
    private static final Object[] paramDefaults = {new Float(1.0F), new Float(1.0F)};

    /** Constructor. */
    public SubsampleBinaryToGrayDescriptor() {
        super(resources, 1, paramClasses, paramNames, paramDefaults);
    }

    /** Returns <code>true</code> since renderable operation is supported. */
    public boolean isRenderableSupported() {
        return true;
    }

    /**
     * Returns an array of <code>PropertyGenerators</code> implementing property inheritance for the
     * "SubsampleBinaryToGray" operation.
     *
     * @return An array of property generators.
     */
    public PropertyGenerator[] getPropertyGenerators() {
        PropertyGenerator[] pg = new PropertyGenerator[1];
        pg[0] = new SubsampleBinaryToGrayPropertyGenerator();
        return pg;
    }

    /**
     * Validates the input parameters.
     *
     * <p>In addition to the standard checks performed by the superclass method, this method checks that "xScale" and
     * "yScale" are both greater than 0 and less than or equal to 1.
     *
     * <p>The src image must be a binary, a one band, one bit per pixel image with a <code> MultiPixelPackedSampleModel
     * </code>.
     */
    protected boolean validateParameters(ParameterBlock args, StringBuffer msg) {
        if (!super.validateParameters(args, msg)) {
            return false;
        }

        // Checks for source to be Binary and Dest Grayscale
        // to be in the RIF

        RenderedImage src = (RenderedImage) args.getSource(0);

        PixelAccessor srcPA = new PixelAccessor(src);
        if (!srcPA.isPacked || !srcPA.isMultiPixelPackedSM) {
            msg.append(getName() + " " + JaiI18N.getString("SubsampleBinaryToGray3"));
            return false;
        }

        float xScale = args.getFloatParameter(0);
        float yScale = args.getFloatParameter(1);
        if (xScale <= 0.0F || yScale <= 0.0F || xScale > 1.0F || yScale > 1.0F) {
            msg.append(getName() + " " + JaiI18N.getString("SubsampleBinaryToGray1") + " or "
                    + JaiI18N.getString("SubsampleBinaryToGray2"));
            return false;
        }

        return true;
    }

    /**
     * Returns the minimum legal value of a specified numeric parameter for this operation.
     *
     * <p>For the minimum value of "xScale" and "yScale", this method returns 0. However, the scale factors must be a
     * positive floating number and can not be 0.
     */
    public Number getParamMinValue(int index) {
        if (index == 0 || index == 1) {
            return new Float(0.0F);
        } else {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    /**
     * To subsamples binary image to gray; reverse of dithering.
     *
     * <p>Creates a <code>ParameterBlockImageN</code> from all supplied arguments except <code>hints</code> and invokes
     * {@link ImageN#create(String,ParameterBlock,RenderingHints)}.
     *
     * @see ImageN
     * @see ParameterBlockImageN
     * @see RenderedOp
     * @param source0 <code>RenderedImage</code> source 0.
     * @param xScale scaleX must be between 0 and 1, excluding 0. May be <code>null</code>.
     * @param yScale scaleY must be between 0 and 1, excluding 0. May be <code>null</code>.
     * @param hints The <code>RenderingHints</code> to use. May be <code>null</code>.
     * @return The <code>RenderedOp</code> destination.
     * @throws IllegalArgumentException if <code>source0</code> is <code>null</code>.
     */
    public static RenderedOp create(RenderedImage source0, Float xScale, Float yScale, RenderingHints hints) {
        ParameterBlockImageN pb = new ParameterBlockImageN("SubsampleBinaryToGray", RenderedRegistryMode.MODE_NAME);

        pb.setSource("source0", source0);

        pb.setParameter("xScale", xScale);
        pb.setParameter("yScale", yScale);

        return ImageN.create("SubsampleBinaryToGray", pb, hints);
    }

    /**
     * To subsamples binary image to gray; reverse of dithering.
     *
     * <p>Creates a <code>ParameterBlockImageN</code> from all supplied arguments except <code>hints</code> and invokes
     * {@link ImageN#createRenderable(String,ParameterBlock,RenderingHints)}.
     *
     * @see ImageN
     * @see ParameterBlockImageN
     * @see RenderableOp
     * @param source0 <code>RenderableImage</code> source 0.
     * @param xScale scaleX must be between 0 and 1, excluding 0. May be <code>null</code>.
     * @param yScale scaleY must be between 0 and 1, excluding 0. May be <code>null</code>.
     * @param hints The <code>RenderingHints</code> to use. May be <code>null</code>.
     * @return The <code>RenderableOp</code> destination.
     * @throws IllegalArgumentException if <code>source0</code> is <code>null</code>.
     */
    public static RenderableOp createRenderable(
            RenderableImage source0, Float xScale, Float yScale, RenderingHints hints) {
        ParameterBlockImageN pb = new ParameterBlockImageN("SubsampleBinaryToGray", RenderableRegistryMode.MODE_NAME);

        pb.setSource("source0", source0);

        pb.setParameter("xScale", xScale);
        pb.setParameter("yScale", yScale);

        return ImageN.createRenderable("SubsampleBinaryToGray", pb, hints);
    }
}
