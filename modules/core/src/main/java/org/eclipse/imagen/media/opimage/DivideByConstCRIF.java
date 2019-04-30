/*
 * Copyright (c) [2019,] 2019, Oracle and/or its affiliates. All rights reserved.
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
import org.eclipse.imagen.CRIFImpl;
import org.eclipse.imagen.ImageLayout;
import java.util.Map;

/**
 * A <code>CRIF</code> supporting the "DivideByConst" operation in the rendered
 * and renderable image layers.
 *
 * @see org.eclipse.imagen.operator.DivideByConstDescriptor
 * @see MultiplyConstOpImage
 *
 *
 * @since EA2
 */
public class DivideByConstCRIF extends CRIFImpl {

    /** Constructor. */
    public DivideByConstCRIF() {
        super("dividebyconst");
    }

    /**
     * Creates a new instance of <code>DivideByConstOpImage</code> in the
     * rendered layer.
     *
     * @param args   The source image and the constants.
     * @param hints  Optionally contains destination image layout.
     */
    public RenderedImage create(ParameterBlock args,
                                RenderingHints renderHints) {
        // Get ImageLayout from renderHints if any.
        ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);
        

        /* Invert the constants. */
        double[] constants = (double[])args.getObjectParameter(0);
        int length = constants.length;

        double[] invConstants = new double[length];

        for (int i = 0; i < length; i++) {
            invConstants[i] = 1.0 / constants[i];
        }

        return new MultiplyConstOpImage(args.getRenderedSource(0),
                                        renderHints,
					layout,
					invConstants);
    }
}
