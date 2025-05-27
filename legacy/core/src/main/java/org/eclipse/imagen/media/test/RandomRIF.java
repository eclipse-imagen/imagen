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

package org.eclipse.imagen.media.test;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import org.eclipse.imagen.ImageLayout;
import org.eclipse.imagen.JAI;

/** @see RandomOpImage */
public class RandomRIF implements RenderedImageFactory {

    /** Constructor. */
    public RandomRIF() {}

    /**
     * Creates a new instance of RandomOpImage in the rendered layer. This method satisfies the implementation of RIF.
     */
    public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints) {
        // Get ImageLayout from renderHints if any.
        ImageLayout layout = (renderHints == null) ? null : (ImageLayout) renderHints.get(JAI.KEY_IMAGE_LAYOUT);

        return OpImageTester.createRandomOpImage(renderHints, layout);
    }
}
