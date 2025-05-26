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
import java.io.IOException;
import org.eclipse.imagen.ImageLayout;
import org.eclipse.imagen.JAI;
import org.eclipse.imagen.OpImage;
import org.eclipse.imagen.OperationRegistry;
import org.eclipse.imagen.TileCache;
import org.eclipse.imagen.media.codec.ImageCodec;
import org.eclipse.imagen.media.codec.ImageDecodeParam;
import org.eclipse.imagen.media.codec.ImageDecoder;
import org.eclipse.imagen.media.codec.SeekableStream;
import org.eclipse.imagen.media.util.DisposableNullOpImage;
import org.eclipse.imagen.media.util.ImageUtil;
import org.eclipse.imagen.registry.RIFRegistry;
import org.eclipse.imagen.util.ImagingException;
import org.eclipse.imagen.util.ImagingListener;

/**
 * @see org.eclipse.imagen.operator.StreamDescriptor
 * @since EA2
 */
public class StreamRIF implements RenderedImageFactory {

    /** Constructor. */
    public StreamRIF() {}

    /** Creates an image from a SeekableStream. */
    public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints) {
        ImagingListener listener = ImageUtil.getImagingListener(renderHints);
        SeekableStream src = (SeekableStream) paramBlock.getObjectParameter(0);
        try {
            src.seek(0L);
        } catch (IOException e) {
            listener.errorOccurred(JaiI18N.getString("StreamRIF0"), e, this, false);
            //            e.printStackTrace();
            return null;
        }

        ImageDecodeParam param = null;
        if (paramBlock.getNumParameters() > 1) {
            param = (ImageDecodeParam) paramBlock.getObjectParameter(1);
        }

        String[] names = ImageCodec.getDecoderNames(src);

        OperationRegistry registry = JAI.getDefaultInstance().getOperationRegistry();
        int bound = OpImage.OP_IO_BOUND;
        ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);

        if (renderHints != null) {
            RenderingHints.Key key;

            key = JAI.KEY_OPERATION_REGISTRY;
            if (renderHints.containsKey(key)) {
                registry = (OperationRegistry) renderHints.get(key);
            }

            key = JAI.KEY_OPERATION_BOUND;
            if (renderHints.containsKey(key)) {
                bound = ((Integer) renderHints.get(key)).intValue();
            }
        }

        // Try to create a JAI operation with the given name
        for (int i = 0; i < names.length; i++) {
            RenderedImageFactory rif = null;
            try {
                rif = RIFRegistry.get(registry, names[i]);
            } catch (IllegalArgumentException iae) {
                // ignore IAE.
            }
            if (rif != null) {
                RenderedImage im = RIFRegistry.create(registry, names[i], paramBlock, renderHints);
                if (im != null) {
                    return im;
                }
            }
        }

        // Set flag indicating that a recovery may be attempted if
        // an OutOfMemoryError occurs during the decodeAsRenderedImage()
        // call - which is only possible if the stream can seek backwards.
        boolean canAttemptRecovery = src.canSeekBackwards();

        // Save the stream position prior to decodeAsRenderedImage().
        long streamPosition = Long.MIN_VALUE;
        if (canAttemptRecovery) {
            try {
                streamPosition = src.getFilePointer();
            } catch (IOException ioe) {
                listener.errorOccurred(JaiI18N.getString("StreamRIF1"), ioe, this, false);
                // Unset the recovery attempt flag but otherwise
                // ignore the exception.
                canAttemptRecovery = false;
            }
        }

        // Try to create an ImageDecoder directly
        for (int i = 0; i < names.length; i++) {
            ImageDecoder dec = ImageCodec.createImageDecoder(names[i], src, param);
            RenderedImage im = null;
            try {
                im = dec.decodeAsRenderedImage();
            } catch (OutOfMemoryError memoryError) {
                // Ran out of memory - may be due to the decoder being
                // obliged to read the entire image when it creates the
                // RenderedImage it returns.
                if (canAttemptRecovery) {
                    // First flush the cache if one is defined.
                    TileCache cache = RIFUtil.getTileCacheHint(renderHints);
                    if (cache != null) {
                        cache.flush();
                    }

                    // Force garbage collection.
                    System.gc(); // slow

                    try {
                        // Reposition the stream before the previous decoding.
                        src.seek(streamPosition);

                        // Retry image decoding.
                        im = dec.decodeAsRenderedImage();
                    } catch (IOException ioe) {
                        listener.errorOccurred(JaiI18N.getString("StreamRIF2"), ioe, this, false);
                        im = null;
                    }
                } else {
                    String message = JaiI18N.getString("CodecRIFUtil0");
                    listener.errorOccurred(message, new ImagingException(message, memoryError), this, false);
                    // Re-throw the error.
                    //                    throw memoryError;
                }
            } catch (IOException e) {
                listener.errorOccurred(JaiI18N.getString("StreamRIF2"), e, this, false);
                im = null;
            }

            // If decoding succeeded, wrap the result in an OpImage.
            if (im != null) {
                return new DisposableNullOpImage(
                        im, layout,
                        renderHints, bound);
            }
        }

        return null;
    }
}
