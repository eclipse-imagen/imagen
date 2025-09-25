/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 *
 * This Example Content is intended to demonstrate usage of Eclipse technology. It is
 * provided to you under the terms and conditions of the Eclipse Distribution License
 * v1.0 which is available at http://www.eclipse.org/org/documents/edl-v10.php.
 */
package org.eclipse.imagen.demo;

import java.awt.image.IndexColorModel;
import java.awt.image.RenderedImage;
import org.eclipse.imagen.ImageN;
import org.eclipse.imagen.LookupTableJAI;
import org.eclipse.imagen.PlanarImage;
import org.eclipse.imagen.media.codec.FileSeekableStream;
import org.eclipse.imagen.media.codec.ImageCodec;
import org.eclipse.imagen.media.codec.ImageDecoder;
import org.eclipse.imagen.media.codec.SeekableStream;

public class JAIImageReader {

    public static PlanarImage readImage(String filename) {
        PlanarImage image = null;

        // Use the ImageN API unless JAI_IMAGE_READER_USE_CODECS is set
        if (System.getProperty("JAI_IMAGE_READER_USE_CODECS") == null) {
            image = ImageN.create("fileload", filename);
        } else {
            try {
                // Use the ImageCodec APIs
                SeekableStream stream = new FileSeekableStream(filename);
                String[] names = ImageCodec.getDecoderNames(stream);
                ImageDecoder dec = ImageCodec.createImageDecoder(names[0], stream, null);
                RenderedImage im = dec.decodeAsRenderedImage();
                image = PlanarImage.wrapRenderedImage(im);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        // If the source image is colormapped, convert it to 3-band RGB.
        if (image.getColorModel() instanceof IndexColorModel) {
            // Retrieve the IndexColorModel
            IndexColorModel icm = (IndexColorModel) image.getColorModel();

            // Cache the number of elements in each band of the colormap.
            int mapSize = icm.getMapSize();

            // Allocate an array for the lookup table data.
            byte[][] lutData = new byte[3][mapSize];

            // Load the lookup table data from the IndexColorModel.
            icm.getReds(lutData[0]);
            icm.getGreens(lutData[1]);
            icm.getBlues(lutData[2]);

            // Create the lookup table object.
            LookupTableJAI lut = new LookupTableJAI(lutData);

            // Replace the original image with the 3-band RGB image.
            image = ImageN.create("lookup", image, lut);
        }

        return image;
    }
}
