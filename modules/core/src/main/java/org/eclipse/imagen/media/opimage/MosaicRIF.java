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

package org.eclipse.imagen.media.opimage;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import org.eclipse.imagen.PlanarImage;
import org.eclipse.imagen.ROI;
import org.eclipse.imagen.operator.MosaicType;

/**
 * A <code>RIF</code> supporting the "Mosaic" operation in the rendered image layer.
 *
 * @since JAI 1.1.2
 * @see org.eclipse.imagen.operator.MosaicDescriptor
 */
public class MosaicRIF implements RenderedImageFactory {

    /** Constructor. */
    public MosaicRIF() {}

    /** Renders a "Mosaic" operation node. */
    public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints) {
        return new MosaicOpImage(
                paramBlock.getSources(),
                RIFUtil.getImageLayoutHint(renderHints),
                renderHints,
                (MosaicType) paramBlock.getObjectParameter(0),
                (PlanarImage[]) paramBlock.getObjectParameter(1),
                (ROI[]) paramBlock.getObjectParameter(2),
                (double[][]) paramBlock.getObjectParameter(3),
                (double[]) paramBlock.getObjectParameter(4));
    }
}
