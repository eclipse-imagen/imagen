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
import java.io.InputStream;
import java.net.URL;
import org.eclipse.imagen.ImageN;
import org.eclipse.imagen.OpImage;
import org.eclipse.imagen.OperationRegistry;
import org.eclipse.imagen.media.codec.ImageDecodeParam;
import org.eclipse.imagen.media.codec.SeekableStream;
import org.eclipse.imagen.media.util.ImageUtil;
import org.eclipse.imagen.registry.RIFRegistry;
import org.eclipse.imagen.util.ImagingListener;

/**
 * @see org.eclipse.imagen.operator.URLDescriptor
 * @since EA4
 */
public class URLRIF implements RenderedImageFactory {

    /** Constructor. */
    public URLRIF() {}

    /** Creates an image from a URL. */
    public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints) {
        try {
            // Create a SeekableStream from the URL (first parameter).
            URL url = (URL) paramBlock.getObjectParameter(0);
            InputStream stream = url.openStream();
            SeekableStream src = SeekableStream.wrapInputStream(stream, true);

            ImageDecodeParam param = null;
            if (paramBlock.getNumParameters() > 1) {
                param = (ImageDecodeParam) paramBlock.getObjectParameter(1);
            }

            ParameterBlock newParamBlock = new ParameterBlock();
            newParamBlock.add(src);
            newParamBlock.add(param);

            RenderingHints.Key key = ImageN.KEY_OPERATION_BOUND;
            int bound = OpImage.OP_NETWORK_BOUND;
            if (renderHints == null) {
                renderHints = new RenderingHints(key, new Integer(bound));
            } else if (!renderHints.containsKey(key)) {
                renderHints.put(key, new Integer(bound));
            }

            // Get the registry from the hints, if any.
            // Don't check for null hints as it cannot be null here.
            OperationRegistry registry = (OperationRegistry) renderHints.get(ImageN.KEY_OPERATION_REGISTRY);

            // Create the image using the most preferred RIF for "stream".
            RenderedImage image = RIFRegistry.create(registry, "stream", newParamBlock, renderHints);

            // NB: StreamImage is defined in FileLoadRIF.java.
            return image == null ? null : new StreamImage(image, src);
        } catch (IOException e) {
            ImagingListener listener = ImageUtil.getImagingListener(renderHints);
            String message = JaiI18N.getString("URLRIF0");
            listener.errorOccurred(message, e, this, false);
            //            e.printStackTrace();
            return null;
        }
    }
}
