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
package org.eclipse.imagen;

import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ContextualRenderedImageFactory;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderContext;
import java.awt.image.renderable.RenderableImage;
import org.eclipse.imagen.media.util.ImageUtil;
import org.eclipse.imagen.registry.RenderedRegistryMode;
import org.eclipse.imagen.util.ImagingListener;

/**
 * A utility class to minimize in most cases the effort required to implement the <code>ContextualRenderedImageFactory
 * </code> (CRIF) of an operation. An extender of this class is required to implement only the method <code>
 * RenderedImage create(ParameterBlock, RenderingHints)</code> defined in the <code>RenderedImageFactory</code>
 * interface. The remaining methods may be overridden insofar as this is necessary to obtain behavior different from
 * that provided by default.
 *
 * @see java.awt.image.renderable.ContextualRenderedImageFactory
 * @see java.awt.image.renderable.RenderedImageFactory
 */
// This class was actually added in ImageN EA2 but was then in
// org.eclipse.imagen.media.opimage. It was moved to the public API in ImageN 0.4.0.
public abstract class CRIFImpl implements ContextualRenderedImageFactory {

    /**
     * If non-<code>null</code>, this name will be used as a parameter to <code>ImageN.create()</code> in <code>
     * create(RenderContext,ParameterBlock)</code>; otherwise the RIF <code>create(ParameterBlock,RenderingHints)</code>
     * method implemented in the extending class will be invoked.
     */
    protected String operationName = null;

    /** Default constructor. The operation name is set to <code>null</code>. */
    public CRIFImpl() {
        this.operationName = null;
    }

    /** Constructor. The operation name is set to the specified value which may be <code>null</code>. */
    public CRIFImpl(String operationName) {
        this.operationName = operationName;
    }

    /**
     * The <code>RenderedImageFactory</code> <code>create()</code> method which must be implemented by concrete
     * subclasses.
     */
    public abstract RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints);

    /**
     * Creates a <code>RenderedImage</code> from the renderable layer.
     *
     * <p>If <code>operationName</code> is non-<code>null</code>, <code>ImageN.create()</code> will be invoked using the
     * supplied <code>ParameterBlock</code> and the <code>RenderingHints</code> contained in the <code>RenderContext
     * </code>. If <code>operationName</code> is <code>null</code>, or <code>ImageN.create()</code> returns <code>null
     * </code>, the <code>create(ParameterBlock,RenderingHints)</code> method defined in the extending class will be
     * invoked.
     *
     * @param renderContext The rendering information associated with this rendering.
     * @param paramBlock The parameters used to create the image.
     * @return A <code>RenderedImage</code>.
     */
    public RenderedImage create(RenderContext renderContext, ParameterBlock paramBlock) {
        RenderingHints renderHints = renderContext.getRenderingHints();
        if (operationName != null) {
            OperationRegistry registry =
                    renderHints == null ? null : (OperationRegistry) renderHints.get(ImageN.KEY_OPERATION_REGISTRY);

            RenderedImage rendering;

            if (registry == null) {
                // Call the ImageN.create method to get the best implementation
                rendering = ImageN.create(operationName, paramBlock, renderHints);
            } else { // registry != null
                // NB: This section is lifted pretty much verbatim from
                // ImageN.createNS().

                // Get the OperationDescriptor registered under the
                // specified name.
                OperationDescriptor odesc =
                        (OperationDescriptor) registry.getDescriptor(OperationDescriptor.class, operationName);
                if (odesc == null) {
                    throw new IllegalArgumentException(operationName + ": " + ImageNI18N.getString("JAI0"));
                }

                // Does this operation support rendered mode?
                if (!odesc.isModeSupported(RenderedRegistryMode.MODE_NAME)) {
                    throw new IllegalArgumentException(operationName + ": " + ImageNI18N.getString("JAI1"));
                }

                // Check the destination image type.
                if (!RenderedImage.class.isAssignableFrom(odesc.getDestClass(RenderedRegistryMode.MODE_NAME))) {
                    throw new IllegalArgumentException(operationName + ": " + ImageNI18N.getString("JAI2"));
                }

                // Validate input arguments. The ParameterBlock is cloned here
                // because OperationDescriptor.validateArguments() may change
                // its content.
                StringBuffer msg = new StringBuffer();
                paramBlock = (ParameterBlock) paramBlock.clone();
                if (!odesc.validateArguments(RenderedRegistryMode.MODE_NAME, paramBlock, msg)) {
                    throw new IllegalArgumentException(msg.toString());
                }

                // Create the rendered operation node.
                rendering = new RenderedOp(
                        registry, operationName,
                        paramBlock, renderHints);
            }

            // Return the rendering if possible.
            if (rendering != null) {
                // If the rendering is a rendered chain, replace it by its
                // rendering which will likely be an OpImage chain.
                if (rendering instanceof RenderedOp) {
                    try {
                        rendering = ((RenderedOp) rendering).getRendering();
                    } catch (Exception e) {
                        ImagingListener listener = ImageUtil.getImagingListener(renderHints);
                        String message = ImageNI18N.getString("CRIFImpl0") + operationName;
                        listener.errorOccurred(message, e, this, false);
                        //                        e.printStackTrace();
                    }
                }
                return rendering;
            }
        }

        // Call the RIF create method of the extending class
        return create(paramBlock, renderHints);
    }

    /**
     * Maps the destination <code>RenderContext</code> into a <code>RenderContext</code> for each source. The
     * implementation in this class simply returns the <code>RenderContext</code> passed in by the caller.
     *
     * @param i The index of the source image.
     * @param renderContext The <code>RenderContext</code> being applied to the operation.
     * @param paramBlock A <code>ParameterBlock</code> containing the sources and parameters of the operation.
     * @param image The <code>RenderableImage</code> being rendered.
     * @return The <code>RenderContext</code> to be used to render the given source.
     */
    public RenderContext mapRenderContext(
            int i, RenderContext renderContext, ParameterBlock paramBlock, RenderableImage image) {
        return renderContext;
    }

    /**
     * Returns the bounding box for the output of the operation. The implementation in this class computes the bounding
     * box as the intersection the bounding boxes of all the (renderable sources).
     *
     * @param paramBlock A <code>ParameterBlock</code> containing the sources and parameters of the operation.
     * @return A <code>Rectangle2D</code> specifying the bounding box.
     */
    public Rectangle2D getBounds2D(ParameterBlock paramBlock) {
        int numSources = paramBlock.getNumSources();

        if (numSources == 0) {
            return null;
        }

        RenderableImage src = paramBlock.getRenderableSource(0);
        Rectangle2D.Float box1 = new Rectangle2D.Float(src.getMinX(), src.getMinY(), src.getWidth(), src.getHeight());

        for (int i = 1; i < numSources; i++) {
            src = paramBlock.getRenderableSource(i);
            Rectangle2D.Float box2 = new Rectangle2D.Float(
                    src.getMinX(), src.getMinY(),
                    src.getWidth(), src.getHeight());
            box1 = (Rectangle2D.Float) box1.createIntersection(box2);
            if (box1.isEmpty()) {
                break;
            }
        }

        return box1;
    }

    /**
     * Returns the appropriate instance of the property with the indicated name.
     *
     * <p>The implementation in this class always returns <code>java.awt.Image.UndefinedProperty</code> since no
     * properties are defined by default.
     *
     * @param paramBlock A <code>ParameterBlock</code> containing the sources and parameters of the operation.
     * @param name A <code>String</code> containing the desired property name.
     * @return the value <code>java.awt.Image.UndefinedProperty</code> indicating that the property is undefined.
     */
    public Object getProperty(ParameterBlock paramBlock, String name) {
        return java.awt.Image.UndefinedProperty;
    }

    /**
     * Returns the valid property names for the operation. The implementation in this class always returns <code>null
     * </code> since no properties are associated with the operation by default.
     *
     * @return <code>null</code> indicating that no properties are defined.
     */
    public String[] getPropertyNames() {
        return null;
    }

    /**
     * Returns <code>true</code> if successive renderings with the same arguments may produce different results. The
     * implementation in this class always returns <code>false</code> so as to enable caching of renderings by default.
     * CRIFs that do implement dynamic rendering behavior must override this method.
     *
     * @return <code>false</code> indicating that the rendering is static.
     */
    public boolean isDynamic() {
        return false;
    }
}
