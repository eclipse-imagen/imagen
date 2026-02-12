/* Copyright (c) 2025 Daniele Romagnoli and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution and is available at
 * http://www.opensource.org/licenses/apache2.0.php.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.eclipse.imagen.media.algebra;

import java.awt.*;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import org.eclipse.imagen.ImageN;
import org.eclipse.imagen.OperationDescriptorImpl;
import org.eclipse.imagen.ParameterBlockImageN;
import org.eclipse.imagen.RenderableOp;
import org.eclipse.imagen.RenderedOp;
import org.eclipse.imagen.registry.RenderableRegistryMode;
import org.eclipse.imagen.registry.RenderedRegistryMode;

public class SubtractDescriptor extends OperationDescriptorImpl {

    /**
     * The resource strings that provide the general documentation and specify the parameter list for this operation.
     */
    private static final String[][] resources = {
        {"GlobalName", "subtract"},
        {"LocalName", "subtract"},
        {"Vendor", "org.eclipse.imagen.media"},
        {"Description", "This class executes the subtract operation on each pixel of the source images"},
        {"DocURL", "Not Defined"},
        {"Version", "1.0"},
        {"arg0Desc", "ROI object used"},
        {"arg1Desc", "No Data Range used"},
        {"arg2Desc", "Output value for No Data"}
    };

    /** Input Parameter name */
    private static final String[] paramNames = {"roi", "noData", "destinationNoData"};

    /** Input Parameter class */
    private static final Class[] paramClasses = {
        org.eclipse.imagen.ROI.class, org.eclipse.imagen.media.range.Range.class, Double.class
    };

    /** Input Parameter default values */
    private static final Object[] paramDefaults = {null, null, Double.NaN};

    /** Constructor. */
    public SubtractDescriptor() {
        super(resources, 2, paramClasses, paramNames, paramDefaults);
    }

    /** Returns true since renderable operation is supported. */
    public boolean isRenderableSupported() {
        return true;
    }

    /**
     * subtracts two images.
     *
     * @param source0 RenderedImage source 0.
     * @param source1 RenderedImage source 1.
     * @param hints The RenderingHints to use. May be null.
     * @return The RenderedOp destination.
     * @throws IllegalArgumentException if source0 or source1 is null.
     */
    public static RenderedOp create(RenderedImage source0, RenderedImage source1, RenderingHints hints) {
        ParameterBlockImageN pb = new ParameterBlockImageN("subtract", RenderedRegistryMode.MODE_NAME);

        pb.setSource("source0", source0);
        pb.setSource("source1", source1);

        return ImageN.create("subtract", pb, hints);
    }

    /**
     * subtracts two images.
     *
     * @param source0 RenderableImage source 0.
     * @param source1 RenderableImage source 1.
     * @param hints The RenderingHints to use. May be null.
     * @return The RenderableOp destination.
     * @throws IllegalArgumentException if source0 or source1 is null.
     */
    public static RenderableOp createRenderable(
            RenderableImage source0, RenderableImage source1, RenderingHints hints) {
        ParameterBlockImageN pb = new ParameterBlockImageN("subtract", RenderableRegistryMode.MODE_NAME);

        pb.setSource("source0", source0);
        pb.setSource("source1", source1);

        return ImageN.createRenderable("subtract", pb, hints);
    }
}
