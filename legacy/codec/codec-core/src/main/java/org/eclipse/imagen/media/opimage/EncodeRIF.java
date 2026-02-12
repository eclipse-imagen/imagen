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
import java.awt.image.renderable.RenderedImageFactory;
import java.io.IOException;
import java.io.OutputStream;
import org.eclipse.imagen.media.codec.ImageCodec;
import org.eclipse.imagen.media.codec.ImageEncodeParam;
import org.eclipse.imagen.media.codec.ImageEncoder;
import org.eclipse.imagen.media.util.ImageUtil;
import org.eclipse.imagen.util.ImagingListener;

/**
 * @see org.eclipse.imagen.operator.FileDescriptor
 * @since EA4
 */
public class EncodeRIF implements RenderedImageFactory {

    /** Constructor. */
    public EncodeRIF() {}

    /** Stores an image to a stream. */
    public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints) {

        ImagingListener listener = ImageUtil.getImagingListener(renderHints);

        // Retrieve the OutputStream.
        OutputStream stream = (OutputStream) paramBlock.getObjectParameter(0);

        // Retrieve the format.
        String format = (String) paramBlock.getObjectParameter(1);

        // Retrieve the ImageEncodeParam (which may be null).
        ImageEncodeParam param = null;
        if (paramBlock.getNumParameters() > 2) {
            param = (ImageEncodeParam) paramBlock.getObjectParameter(2);
        }

        // Create an ImageEncoder.
        ImageEncoder encoder = ImageCodec.createImageEncoder(format, stream, param);

        // Check the ImageEncoder.
        if (encoder == null) {
            throw new RuntimeException(JaiI18N.getString("EncodeRIF0"));
        }

        // Store the data.
        RenderedImage im = (RenderedImage) paramBlock.getSource(0);
        try {
            encoder.encode(im);
            stream.flush();
            // Fix 4665208: EncodeRIF closed the stream after flush
            // User may put more into the stream
            // stream.close();
        } catch (IOException e) {
            String message = JaiI18N.getString("EncodeRIF1") + " " + format;
            listener.errorOccurred(message, e, this, false);
            //            e.printStackTrace();
            return null;
        }

        return im;
    }
}
