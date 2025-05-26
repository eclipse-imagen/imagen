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

package org.eclipse.imagen.media.util;

import java.awt.Rectangle;
import java.awt.image.renderable.ParameterBlock;
import org.eclipse.imagen.AreaOpImage;
import org.eclipse.imagen.PlanarImage;
import org.eclipse.imagen.ROI;
import org.eclipse.imagen.ROIShape;
import org.eclipse.imagen.RenderedOp;

public class AreaOpPropertyGenerator extends PropertyGeneratorImpl {
    /** Constructor. */
    public AreaOpPropertyGenerator() {
        super(new String[] {"ROI"}, new Class[] {ROI.class}, new Class[] {RenderedOp.class});
    }

    /**
     * Returns the specified property in the rendered layer.
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
            PlanarImage src = (PlanarImage) pb.getRenderedSource(0);
            Object roiProperty = src.getProperty("ROI");
            if (roiProperty == null
                    || roiProperty == java.awt.Image.UndefinedProperty
                    || !(roiProperty instanceof ROI)) {
                return java.awt.Image.UndefinedProperty;
            }
            ROI roi = (ROI) roiProperty;

            // Determine the effective destination bounds.
            Rectangle dstBounds = null;
            PlanarImage dst = op.getRendering();
            if (dst instanceof AreaOpImage && ((AreaOpImage) dst).getBorderExtender() == null) {
                AreaOpImage aoi = (AreaOpImage) dst;
                dstBounds = new Rectangle(
                        aoi.getMinX() + aoi.getLeftPadding(),
                        aoi.getMinY() + aoi.getTopPadding(),
                        aoi.getWidth() - aoi.getLeftPadding() - aoi.getRightPadding(),
                        aoi.getHeight() - aoi.getTopPadding() - aoi.getBottomPadding());
            } else {
                dstBounds = dst.getBounds();
            }

            // If necessary, clip the ROI to the destination bounds.
            // XXX Is this desirable?
            if (!dstBounds.contains(roi.getBounds())) {
                roi = roi.intersect(new ROIShape(dstBounds));
            }

            return roi;
        }

        return java.awt.Image.UndefinedProperty;
    }
}
