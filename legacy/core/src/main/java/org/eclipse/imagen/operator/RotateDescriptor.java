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
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderableImage;
import org.eclipse.imagen.*;
import org.eclipse.imagen.media.util.PropertyGeneratorImpl;
import org.eclipse.imagen.registry.RenderableRegistryMode;
import org.eclipse.imagen.registry.RenderedRegistryMode;

/** This property generator computes the properties for the operation "Rotate" dynamically. */
class RotatePropertyGenerator extends PropertyGeneratorImpl {

    /** Constructor. */
    public RotatePropertyGenerator() {
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

            // Retrieve the Interpolation object.
            Interpolation interp = (Interpolation) pb.getObjectParameter(3);

            // Determine the effective source bounds.
            Rectangle srcBounds = null;
            PlanarImage dst = op.getRendering();
            if (dst instanceof GeometricOpImage && ((GeometricOpImage) dst).getBorderExtender() == null) {
                srcBounds = new Rectangle(
                        src.getMinX() + interp.getLeftPadding(),
                        src.getMinY() + interp.getTopPadding(),
                        src.getWidth() - interp.getWidth() + 1,
                        src.getHeight() - interp.getHeight() + 1);
            } else {
                srcBounds = new Rectangle(src.getMinX(), src.getMinY(), src.getWidth(), src.getHeight());
            }

            // If necessary, clip the ROI to the effective source bounds.
            if (!srcBounds.contains(srcROI.getBounds())) {
                srcROI = srcROI.intersect(new ROIShape(srcBounds));
            }

            // Retrieve the translation and rotation angle.
            double xorig = (double) pb.getFloatParameter(0);
            double yorig = (double) pb.getFloatParameter(1);
            double angle = (double) pb.getFloatParameter(2);

            // Create an transform representing the rotation.
            AffineTransform transform = AffineTransform.getRotateInstance(angle, xorig, yorig);

            // Create the rotated/translated ROI.
            ROI dstROI = srcROI.transform(transform);

            // Retrieve the destination bounds.
            Rectangle dstBounds = op.getBounds();

            // If necessary, clip the rotated ROI to the destination bounds.
            if (!dstBounds.contains(dstROI.getBounds())) {
                dstROI = dstROI.intersect(new ROIShape(dstBounds));
            }

            // Return the rotated and possibly clipped ROI.
            return dstROI;
        }

        return java.awt.Image.UndefinedProperty;
    }
}

/**
 * An <code>OperationDescriptor</code> describing the "Rotate" operation.
 *
 * <p>The "Rotate" operation rotates an image about a given point by a given angle, specified in radians. The origin
 * defaults to (0, 0).
 *
 * <p>The parameter, "backgroundValues", is defined to fill the background with the user-specified background values.
 * These background values will be translated into background colors by the <code>ColorModel</code> when the image is
 * displayed. With the default value, <code>{0.0}</code>, of this parameter, the background pixels are filled with 0s.
 * If the provided array length is smaller than the number of bands, the first element of the provided array is used for
 * all the bands. If the provided values are out of the data range of the destination image, they will be clamped into
 * the proper range.
 *
 * <p>It may be noted that the minX, minY, width and height hints as specified through the <code>JAI.KEY_IMAGE_LAYOUT
 * </code> hint in the <code>RenderingHints</code> object are not honored, as this operator calculates the destination
 * image bounds itself. The other <code>ImageLayout</code> hints, like tileWidth and tileHeight, however are honored.
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
 * <p>"Rotate" defines a PropertyGenerator that performs an identical transformation on the "ROI" property of the source
 * image, which can be retrieved by calling the <code>getProperty</code> method with "ROI" as the property name.
 *
 * <p>
 *
 * <table border=1>
 * <caption>Resource List</caption>
 * <tr><th>Name</th>        <th>Value</th></tr>
 * <tr><td>GlobalName</td>  <td>Rotate</td></tr>
 * <tr><td>LocalName</td>   <td>Rotate</td></tr>
 * <tr><td>Vendor</td>      <td>org.eclipse.imagen.media</td></tr>
 * <tr><td>Description</td> <td>Rotate an image.</td></tr>
 * <tr><td>DocURL</td>      <td>http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/RotateDescriptor.html</td></tr>
 * <tr><td>Version</td>     <td>1.0</td></tr>
 * <tr><td>arg0Desc</td>    <td>The X origin to rotate about.</td></tr>
 * <tr><td>arg1Desc</td>    <td>The Y origin to rotate about.</td></tr>
 * <tr><td>arg2Desc</td>    <td>The rotation angle in radians.</td></tr>
 * <tr><td>arg3Desc</td>    <td>The interpolation method.</td></tr>
 * </table>
 *
 * <p>
 *
 * <table border=1>
 * <caption>Parameter List</caption>
 * <tr><th>Name</th>          <th>Class Type</th>
 *                            <th>Default Value</th></tr>
 * <tr><td>xOrigin</td>       <td>java.lang.Float</td>
 *                            <td>0.0F</td>
 * <tr><td>yOrigin</td>       <td>java.lang.Float</td>
 *                            <td>0.0F</td>
 * <tr><td>angle</td>         <td>java.lang.Float</td>
 *                            <td>0.0F</td>
 * <tr><td>interpolation</td> <td>org.eclipse.imagen.Interpolation</td>
 *                            <td>InterpolationNearest</td>
 * <tr><td>backgroundValues</td> <td>double[]</td>
 *                            <td>{0.0}</td>
 * </table>
 *
 * @see Interpolation
 * @see org.eclipse.imagen.OperationDescriptor
 */
public class RotateDescriptor extends OperationDescriptorImpl {

    /**
     * The resource strings that provide the general documentation and specify the parameter list for the "Rotate"
     * operation.
     */
    private static final String[][] resources = {
        {"GlobalName", "Rotate"},
        {"LocalName", "Rotate"},
        {"Vendor", "org.eclipse.imagen.media"},
        {"Description", JaiI18N.getString("RotateDescriptor0")},
        {
            "DocURL",
            "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/RotateDescriptor.html"
        },
        {"Version", JaiI18N.getString("DescriptorVersion")},
        {"arg0Desc", JaiI18N.getString("RotateDescriptor1")},
        {"arg1Desc", JaiI18N.getString("RotateDescriptor2")},
        {"arg2Desc", JaiI18N.getString("RotateDescriptor3")},
        {"arg3Desc", JaiI18N.getString("RotateDescriptor4")},
        {"arg4Desc", JaiI18N.getString("RotateDescriptor5")}
    };

    /** The parameter names for the "Rotate" operation. */
    private static final String[] paramNames = {"xOrigin", "yOrigin", "angle", "interpolation", "backgroundValues"};

    /** The parameter class types for the "Rotate" operation. */
    private static final Class[] paramClasses = {
        Float.class, Float.class, Float.class, Interpolation.class, double[].class
    };

    /** The parameter default values for the "Rotate" operation. */
    private static final Object[] paramDefaults = {
        new Float(0.0F),
        new Float(0.0F),
        new Float(0.0F),
        Interpolation.getInstance(Interpolation.INTERP_NEAREST),
        new double[] {0.0}
    };

    /** Constructor. */
    public RotateDescriptor() {
        super(resources, 1, paramClasses, paramNames, paramDefaults);
    }

    /** Returns <code>true</code> since renderable operation is supported. */
    public boolean isRenderableSupported() {
        return true;
    }

    /**
     * Returns an array of <code>PropertyGenerators</code> implementing property inheritance for the "Rotate" operation.
     *
     * @return An array of property generators.
     */
    public PropertyGenerator[] getPropertyGenerators() {
        PropertyGenerator[] pg = new PropertyGenerator[1];
        pg[0] = new RotatePropertyGenerator();
        return pg;
    }

    /**
     * Rotates an image.
     *
     * <p>Creates a <code>ParameterBlockJAI</code> from all supplied arguments except <code>hints</code> and invokes
     * {@link JAI#create(String,ParameterBlock,RenderingHints)}.
     *
     * @see JAI
     * @see ParameterBlockJAI
     * @see RenderedOp
     * @param source0 <code>RenderedImage</code> source 0.
     * @param xOrigin The X origin to rotate about. May be <code>null</code>.
     * @param yOrigin The Y origin to rotate about. May be <code>null</code>.
     * @param angle The rotation angle in radians. May be <code>null</code>.
     * @param interpolation The interpolation method. May be <code>null</code>.
     * @param backgroundValues The user-specified background values. May be <code>null</code>.
     * @param hints The <code>RenderingHints</code> to use. May be <code>null</code>.
     * @return The <code>RenderedOp</code> destination.
     * @throws IllegalArgumentException if <code>source0</code> is <code>null</code>.
     */
    public static RenderedOp create(
            RenderedImage source0,
            Float xOrigin,
            Float yOrigin,
            Float angle,
            Interpolation interpolation,
            double[] backgroundValues,
            RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("Rotate", RenderedRegistryMode.MODE_NAME);

        pb.setSource("source0", source0);

        pb.setParameter("xOrigin", xOrigin);
        pb.setParameter("yOrigin", yOrigin);
        pb.setParameter("angle", angle);
        pb.setParameter("interpolation", interpolation);
        pb.setParameter("backgroundValues", backgroundValues);

        return JAI.create("Rotate", pb, hints);
    }

    /**
     * Rotates an image.
     *
     * <p>Creates a <code>ParameterBlockJAI</code> from all supplied arguments except <code>hints</code> and invokes
     * {@link JAI#createRenderable(String,ParameterBlock,RenderingHints)}.
     *
     * @see JAI
     * @see ParameterBlockJAI
     * @see RenderableOp
     * @param source0 <code>RenderableImage</code> source 0.
     * @param xOrigin The X origin to rotate about. May be <code>null</code>.
     * @param yOrigin The Y origin to rotate about. May be <code>null</code>.
     * @param angle The rotation angle in radians. May be <code>null</code>.
     * @param interpolation The interpolation method. May be <code>null</code>.
     * @param backgroundValues The user-specified background values. May be <code>null</code>.
     * @param hints The <code>RenderingHints</code> to use. May be <code>null</code>.
     * @return The <code>RenderableOp</code> destination.
     * @throws IllegalArgumentException if <code>source0</code> is <code>null</code>.
     */
    public static RenderableOp createRenderable(
            RenderableImage source0,
            Float xOrigin,
            Float yOrigin,
            Float angle,
            Interpolation interpolation,
            double[] backgroundValues,
            RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("Rotate", RenderableRegistryMode.MODE_NAME);

        pb.setSource("source0", source0);

        pb.setParameter("xOrigin", xOrigin);
        pb.setParameter("yOrigin", yOrigin);
        pb.setParameter("angle", angle);
        pb.setParameter("interpolation", interpolation);
        pb.setParameter("backgroundValues", backgroundValues);

        return JAI.createRenderable("Rotate", pb, hints);
    }
}
