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
package org.eclipse.imagen.demo;

import java.awt.image.IndexColorModel;
import java.awt.image.RenderedImage;
import org.eclipse.imagen.JAI;
import org.eclipse.imagen.LookupTableJAI;
import org.eclipse.imagen.PlanarImage;
import org.eclipse.imagen.media.codec.ImageCodec;
import org.eclipse.imagen.media.codec.ImageDecoder;
import org.eclipse.imagen.media.codec.FileSeekableStream;
import org.eclipse.imagen.media.codec.SeekableStream;

public class JAIImageReader {

    public static PlanarImage readImage(String filename) {
        PlanarImage image = null;

        // Use the JAI API unless JAI_IMAGE_READER_USE_CODECS is set
        if (System.getProperty("JAI_IMAGE_READER_USE_CODECS") == null) {
            image = JAI.create("fileload", filename);
        } else {
            try {
                // Use the ImageCodec APIs
                SeekableStream stream = new FileSeekableStream(filename);
                String[] names = ImageCodec.getDecoderNames(stream);
                ImageDecoder dec =
                    ImageCodec.createImageDecoder(names[0], stream, null);
                RenderedImage im = dec.decodeAsRenderedImage();
                image = PlanarImage.wrapRenderedImage(im);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        // If the source image is colormapped, convert it to 3-band RGB.
        if(image.getColorModel() instanceof IndexColorModel) {
            // Retrieve the IndexColorModel
            IndexColorModel icm = (IndexColorModel)image.getColorModel();

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
            image = JAI.create("lookup", image, lut);
        }

        return image;
    }
}
