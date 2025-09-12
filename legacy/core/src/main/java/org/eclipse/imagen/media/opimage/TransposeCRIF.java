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
import java.awt.image.DataBuffer;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;
import org.eclipse.imagen.CRIFImpl;
import org.eclipse.imagen.EnumeratedParameter;
import org.eclipse.imagen.ImageLayout;

/**
 * @see TransposeOpImage
 * @see TransposeBinaryOpImage
 */
public class TransposeCRIF extends CRIFImpl {

    /** Constructor. */
    public TransposeCRIF() {
        super("transpose");
    }

    /** Creates a Tranpose operation. */
    public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints) {
        // Get ImageLayout from renderHints if any.
        ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);

        RenderedImage source = paramBlock.getRenderedSource(0);

        EnumeratedParameter type = (EnumeratedParameter) paramBlock.getObjectParameter(0);

        SampleModel sm = source.getSampleModel();
        if ((sm instanceof MultiPixelPackedSampleModel)
                && (sm.getSampleSize(0) == 1)
                && (sm.getDataType() == DataBuffer.TYPE_BYTE
                        || sm.getDataType() == DataBuffer.TYPE_USHORT
                        || sm.getDataType() == DataBuffer.TYPE_INT)) {
            return new TransposeBinaryOpImage(source, renderHints, layout, type.getValue());
        } else {
            return new TransposeOpImage(source, renderHints, layout, type.getValue());
        }
    }
}
