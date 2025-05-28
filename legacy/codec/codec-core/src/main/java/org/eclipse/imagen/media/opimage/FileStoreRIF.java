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
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import org.eclipse.imagen.JAI;
import org.eclipse.imagen.OperationRegistry;
import org.eclipse.imagen.PlanarImage;
import org.eclipse.imagen.RenderedImageAdapter;
import org.eclipse.imagen.media.codec.ImageEncodeParam;
import org.eclipse.imagen.media.codec.SeekableOutputStream;
import org.eclipse.imagen.media.util.ImageUtil;
import org.eclipse.imagen.registry.RIFRegistry;
import org.eclipse.imagen.util.ImagingListener;

/**
 * @see org.eclipse.imagen.operator.FileDescriptor
 * @since EA4
 */
public class FileStoreRIF implements RenderedImageFactory {
    /** The default file format. */
    private static String DEFAULT_FORMAT = "tiff";

    /** Constructor. */
    public FileStoreRIF() {}

    /*
     * Private class which merely adds a dispose() method to close
     * the associated stream.
     */
    private class FileStoreImage extends RenderedImageAdapter {
        private OutputStream stream;

        /*
         * Create the object and cache the stream.
         */
        public FileStoreImage(RenderedImage image, OutputStream stream) {
            super(image);
            this.stream = stream;
        }

        /*
         * Close the stream.
         */
        public void dispose() {
            try {
                stream.close();
            } catch (IOException e) {
                // Ignore it ...
            }
            super.dispose();
        }
    }

    /** Stores an image to a file. */
    public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints) {
        ImagingListener listener = ImageUtil.getImagingListener(renderHints);

        // Retrieve the file path.
        String fileName = (String) paramBlock.getObjectParameter(0);

        // Retrieve the file format preference.
        String format = (String) paramBlock.getObjectParameter(1);

        // TODO: If format is null get format name from file extension.

        // If the format is still null use the default format.
        if (format == null) {
            format = DEFAULT_FORMAT;
        }

        // Retrieve the ImageEncodeParam (which may be null).
        ImageEncodeParam param = null;
        if (paramBlock.getNumParameters() > 2) {
            param = (ImageEncodeParam) paramBlock.getObjectParameter(2);
        }

        // Create a FileOutputStream from the file name.
        OutputStream stream = null;
        try {
            if (param == null) {
                // Use a BufferedOutputStream for greater efficiency
                // since no compression is occurring.
                stream = new BufferedOutputStream(new FileOutputStream(fileName));
            } else {
                // Use SeekableOutputStream to avoid temp cache file
                // in case of compression.
                stream = new SeekableOutputStream(new RandomAccessFile(fileName, "rw"));
            }
        } catch (FileNotFoundException e) {
            String message = JaiI18N.getString("FileLoadRIF0") + fileName;
            listener.errorOccurred(message, e, this, false);
            //            e.printStackTrace();
            return null;
        } catch (SecurityException e) {
            String message = JaiI18N.getString("FileStoreRIF0");
            listener.errorOccurred(message, e, this, false);
            //            e.printStackTrace();
            return null;
        }

        // Add the operation to the DAG.
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(paramBlock.getSource(0));
        pb.add(stream).add(format).add(param);

        // Get the default registry.
        OperationRegistry registry =
                (renderHints == null) ? null : (OperationRegistry) renderHints.get(JAI.KEY_OPERATION_REGISTRY);

        PlanarImage im = new FileStoreImage(RIFRegistry.create(registry, "encode", pb, renderHints), stream);

        return im;
    }
}
