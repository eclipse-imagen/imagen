/*
 * $RCSfile: ImageReadCRIF.java,v $
 *
 *
 * Copyright (c) 2005 Sun Microsystems, Inc. All  Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * - Redistribution of source code must retain the above copyright
 *   notice, this  list of conditions and the following disclaimer.
 *
 * - Redistribution in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in
 *   the documentation and/or other materials provided with the
 *   distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of
 * contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. SUN MIDROSYSTEMS, INC. ("SUN") AND ITS LICENSORS SHALL
 * NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF
 * USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR
 * ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL,
 * CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND
 * REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR
 * INABILITY TO USE THIS SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that this software is not designed or intended for
 * use in the design, construction, operation or maintenance of any
 * nuclear facility.
 *
 * $Revision: 1.2 $
 * $Date: 2005/12/01 00:39:04 $
 * $State: Exp $
 */
package org.eclipse.imagen.media.imageread;

import java.awt.*;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.net.URL;
import java.util.EventListener;
import java.util.Iterator;
import java.util.Locale;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.event.IIOReadProgressListener;
import javax.imageio.event.IIOReadUpdateListener;
import javax.imageio.event.IIOReadWarningListener;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import org.eclipse.imagen.CRIFImpl;
import org.eclipse.imagen.ImageLayout;
import org.eclipse.imagen.ImageN;
import org.eclipse.imagen.PropertySource;
import org.eclipse.imagen.WritablePropertySource;

public final class ImageReadCRIF extends CRIFImpl {
    public ImageReadCRIF() {
        super(); // Pass up the name?
    }

    /**
     * Attempt to create an {@link ImageInputStream} for the supplied input. The following sequence is effected:
     *
     * <ol>
     *   <li>
     *       <ul>
     *         <li>If <code>input</code> is an <code>ImageInputStream</code> it is cast and returned.
     *         <li>If <code>input</code> is a <code>String</code> it is converted to a read-only <code>RandomAccessFile
     *             </code>.
     *         <li>If conversion to a <code>RandomAccessFile</code> fails, the <code>String</code> <code>input</code> is
     *             converted to an <code>InputStream</code> by accessing it as a resource bundled in a JAR file.
     *         <li>If <code>input</code> is a <code>URL</code> it is converted to an <code>InputStream</code>.
     *         <li>If <code>input</code> is a <code>Socket</code> it is converted to an <code>InputStream</code>.
     *       </ul>
     *   <li><code>ImageIO.createImageInputStream()</code> is invoked with parameter set to the (possibly converted)
     *       input and the value it returns (which could be <code>null</code>) is returned to the caller.
     * </ol>
     *
     * @param input An <code>Object</code> to be used as the source, such as a <code>String</code>, <code>URL</code>,
     *     <code>File</code>, readable <code>RandomAccessFile</code>, <code>InputStream</code>, readable <code>Socket
     *     </code>, or readable <code>Channel</code>.
     * @return An <code>ImageInputStream</code> or <code>null</code>.
     */
    private static ImageInputStream getImageInputStream(Object input) {
        // The value to be returned.
        ImageInputStream stream = null;

        // If already an ImageInputStream cast and return.
        if (input instanceof ImageInputStream) {
            return (ImageInputStream) input;
        } else {
            // If the input is a String replace it with a RandomAccessFile.
            if (input instanceof String) {
                try {
                    // 'input' is conditionally checked for readability
                    // in the OperationDescriptor.
                    input = new RandomAccessFile((String) input, "r");
                } catch (Exception e) {
                    // Try to get the file as an InputStream resource. This
                    // would happen when the application and image file are
                    // packaged in a JAR file
                    input = ImageReadCRIF.class.getClassLoader().getResourceAsStream((String) input);
                    if (input == null) throw new RuntimeException(I18N.getString("ImageReadCRIF0") + " " + input);
                }
            } else if (input instanceof URL) {
                // If the input is a URL replace it with an InputStream.
                try {
                    input = ((URL) input).openStream();
                } catch (Exception e) {
                    throw new RuntimeException(I18N.getString("ImageReadCRIF1") + " " + input);
                }
            } else if (input instanceof Socket) {
                // If output is a Socket replace it with an InputStream.
                try {
                    Socket socket = (Socket) input;
                    // XXX check binding, connection, closed, shutdown
                    // as these could have changed.
                    input = socket.getInputStream();
                } catch (Exception e) {
                    throw new RuntimeException(I18N.getString("ImageReadCRIF2") + " " + input);
                }
            }
        }

        // Create the ImageInputStream.
        try {
            stream = ImageIO.createImageInputStream(input);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return stream;
    }

    /**
     * Get the <code>ImageReader</code> and set its input and metadata flag. The input set on the reader might not be
     * the same object as the input passed in if the latter was replaced by getImageInputStream().
     */
    static ImageReader getImageReader(ParameterBlock pb) {
        // Get the input.
        Object input = pb.getObjectParameter(0);

        // Get the reader parameter.
        ImageReader reader = (ImageReader) pb.getObjectParameter(8);

        // Attempt to create an ImageInputStream from the input.
        ImageInputStream stream = getImageInputStream(input);

        // If no reader passed in, try to find one.
        if (reader == null) {
            // Get all compatible readers.
            Iterator readers = ImageIO.getImageReaders(stream != null ? stream : input);

            // If any readers, take the first one whose originating
            // service provider indicates that it can decode the input.
            if (readers != null && readers.hasNext()) {
                do {
                    ImageReader tmpReader = (ImageReader) readers.next();
                    ImageReaderSpi readerSpi = tmpReader.getOriginatingProvider();
                    try {
                        if (readerSpi.canDecodeInput(stream != null ? stream : input)) {
                            reader = tmpReader;
                        }
                    } catch (IOException ioe) {
                        // XXX Ignore it?
                    }
                } while (reader == null && readers.hasNext());
            }
        }

        // If reader found, set its input and metadata flag.
        if (reader != null) {
            // Get the locale parameter and set on the reader.
            Locale locale = (Locale) pb.getObjectParameter(6);
            if (locale != null) {
                reader.setLocale(locale);
            }

            // Get the listeners parameter and set on the reader.
            EventListener[] listeners = (EventListener[]) pb.getObjectParameter(5);
            if (listeners != null) {
                for (int i = 0; i < listeners.length; i++) {
                    EventListener listener = listeners[i];
                    if (listener instanceof IIOReadProgressListener) {
                        reader.addIIOReadProgressListener((IIOReadProgressListener) listener);
                    }
                    if (listener instanceof IIOReadUpdateListener) {
                        reader.addIIOReadUpdateListener((IIOReadUpdateListener) listener);
                    }
                    if (listener instanceof IIOReadWarningListener) {
                        reader.addIIOReadWarningListener((IIOReadWarningListener) listener);
                    }
                }
            }

            // Get the metadata reading flag.
            boolean readMetadata = ((Boolean) pb.getObjectParameter(2)).booleanValue();

            // Set the input and indicate metadata reading state.
            reader.setInput(
                    stream != null ? stream : input,
                    false, // seekForwardOnly
                    !readMetadata); // ignoreMetadata
        }

        return reader;
    }

    static void copyProperty(PropertySource ps, WritablePropertySource wps, String propertyName) {
        Object propertyValue = ps.getProperty(propertyName);

        if (propertyValue != null && !propertyValue.equals(java.awt.Image.UndefinedProperty)) {
            wps.setProperty(propertyName, propertyValue);
        }
    }

    public RenderedImage create(ParameterBlock pb, RenderingHints rh) {

        // Value to be returned.
        RenderedImage image = null;

        // Get the reader.
        ImageReader reader = getImageReader(pb);
        // Proceed if a compatible reader was found.
        if (reader != null) {
            // If the reader was not externally provided, it will have to be disposed on close
            boolean closeReader = !(pb.getObjectParameter(0) instanceof ImageReader);

            // Get the remaining parameters required.
            int imageIndex = pb.getIntParameter(1);
            ImageReadParam param = (ImageReadParam) pb.getObjectParameter(7);
            boolean readThumbnails = ((Boolean) pb.getObjectParameter(3)).booleanValue();

            // Initialize the layout.
            ImageLayout layout = (rh != null && rh.containsKey(ImageN.KEY_IMAGE_LAYOUT))
                    ? (ImageLayout) rh.get(ImageN.KEY_IMAGE_LAYOUT)
                    : new ImageLayout();

            try {
                // Get the parameter input.
                Object paramInput = pb.getObjectParameter(0);

                // Get the reader input.
                Object readerInput = reader.getInput();

                // Set the stream to close when the OpImage is disposed.
                ImageInputStream streamToClose = null;
                if (readerInput != paramInput && readerInput instanceof ImageInputStream) {
                    streamToClose = (ImageInputStream) readerInput;
                }

                // Create the rendering.
                image = new ImageReadOpImage(
                        layout, rh, param, reader, imageIndex, readThumbnails, streamToClose, closeReader);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return image;
    }
}
