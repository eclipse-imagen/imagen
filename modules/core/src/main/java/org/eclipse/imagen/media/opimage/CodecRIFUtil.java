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
import java.io.IOException;
import org.eclipse.imagen.ImageLayout;
import org.eclipse.imagen.JAI;
import org.eclipse.imagen.OpImage;
import org.eclipse.imagen.TileCache;
import org.eclipse.imagen.media.codec.ImageCodec;
import org.eclipse.imagen.media.codec.ImageDecodeParam;
import org.eclipse.imagen.media.codec.ImageDecoder;
import org.eclipse.imagen.media.codec.SeekableStream;
import org.eclipse.imagen.media.util.DisposableNullOpImage;
import org.eclipse.imagen.media.util.ImageUtil;
import org.eclipse.imagen.util.ImagingException;
import org.eclipse.imagen.util.ImagingListener;

public class CodecRIFUtil {

    private CodecRIFUtil() {}

    public static RenderedImage create(String type, ParameterBlock paramBlock, RenderingHints renderHints) {
        ImagingListener listener = ImageUtil.getImagingListener(renderHints);

        SeekableStream source = (SeekableStream) paramBlock.getObjectParameter(0);

        ImageDecodeParam param = null;
        if (paramBlock.getNumParameters() > 1) {
            param = (ImageDecodeParam) paramBlock.getObjectParameter(1);
        }
        int page = 0;
        if (paramBlock.getNumParameters() > 2) {
            page = paramBlock.getIntParameter(2);
        }

        ImageDecoder dec = ImageCodec.createImageDecoder(type, source, param);
        try {
            int bound = OpImage.OP_IO_BOUND;
            ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);

            if (renderHints != null) {
                RenderingHints.Key key;

                key = JAI.KEY_OPERATION_BOUND;
                if (renderHints.containsKey(key)) {
                    bound = ((Integer) renderHints.get(key)).intValue();
                }
            }

            // Set flag indicating that a recovery may be attempted if
            // an OutOfMemoryError occurs during the decodeAsRenderedImage()
            // call - which is only possible if the stream can seek backwards.
            boolean canAttemptRecovery = source.canSeekBackwards();

            // Save the stream position prior to decodeAsRenderedImage().
            long streamPosition = Long.MIN_VALUE;
            if (canAttemptRecovery) {
                try {
                    streamPosition = source.getFilePointer();
                } catch (IOException ioe) {
                    listener.errorOccurred(JaiI18N.getString("StreamRIF1"), ioe, CodecRIFUtil.class, false);
                    // Unset the recovery attempt flag but otherwise
                    // ignore the exception.
                    canAttemptRecovery = false;
                }
            }

            OpImage image = null;
            try {
                // Attempt to create an OpImage from the decoder image.
                image = new DisposableNullOpImage(dec.decodeAsRenderedImage(page), layout, renderHints, bound);
            } catch (OutOfMemoryError memoryError) {
                // Ran out of memory - may be due to the decoder being
                // obliged to read the entire image when it creates the
                // RenderedImage it returns.
                if (canAttemptRecovery) {
                    // First flush the cache if one is defined.
                    TileCache cache = image != null ? image.getTileCache() : RIFUtil.getTileCacheHint(renderHints);
                    if (cache != null) {
                        cache.flush();
                    }

                    // Force garbage collection.
                    System.gc(); // slow

                    // Reposition the stream before the previous decoding.
                    source.seek(streamPosition);

                    // Retry image decoding.
                    image = new DisposableNullOpImage(dec.decodeAsRenderedImage(page), layout, renderHints, bound);
                } else {
                    // Re-throw the error.
                    String message = JaiI18N.getString("CodecRIFUtil0");
                    listener.errorOccurred(
                            message, new ImagingException(message, memoryError), CodecRIFUtil.class, false);
                    //                    throw memoryError;
                }
            }

            return image;
        } catch (Exception e) {
            listener.errorOccurred(JaiI18N.getString("CodecRIFUtil1"), e, CodecRIFUtil.class, false);
            //            e.printStackTrace();
            return null;
        }
    }
}
