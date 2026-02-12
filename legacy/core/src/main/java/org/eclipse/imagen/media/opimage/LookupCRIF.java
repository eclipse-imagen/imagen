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
package org.eclipse.imagen.media.opimage;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import org.eclipse.imagen.CRIFImpl;
import org.eclipse.imagen.ImageLayout;
import org.eclipse.imagen.LookupTableImageN;

/**
 * A <code>CRIF</code> supporting the "Lookup" operation in the rendered and renderable image layers.
 *
 * <p>Although Lookup is supported in the renderable layer, it is necessary to understand that in some situations the
 * operator may not produce smooth results. This is due to an affine transform being performed on the source image,
 * which combined with certain types of table data will produce random/unexpected destination values. In addition, a
 * lookup operation with the same input source and table in the renderable chain will yield to different destination
 * from different rendering.
 *
 * @see org.eclipse.imagen.operator.LookupDescriptor
 * @see LookupOpImage
 */
public class LookupCRIF extends CRIFImpl {

    /** Constructor. */
    public LookupCRIF() {
        super("lookup");
    }

    /**
     * Creates a new instance of <code>LookupOpImage</code> in the rendered layer.
     *
     * @param args The source image and the lookup table.
     * @param hints Optionally contains destination image layout.
     */
    public RenderedImage create(ParameterBlock args, RenderingHints renderHints) {
        // Get ImageLayout from renderHints if any.
        ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);

        return new LookupOpImage(
                args.getRenderedSource(0), renderHints, layout, (LookupTableImageN) args.getObjectParameter(0));
    }
}
