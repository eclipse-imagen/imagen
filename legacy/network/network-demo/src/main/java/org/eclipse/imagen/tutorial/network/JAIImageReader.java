/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 *
 * This Example Content is intended to demonstrate usage of Eclipse technology. It is
 * provided to you under the terms and conditions of the Eclipse Distribution License
 * v1.0 which is available at http://www.eclipse.org/org/documents/edl-v10.php.
 */
package org.eclipse.imagen.tutorial.network;

import java.awt.image.IndexColorModel;
import java.awt.image.renderable.ParameterBlock;
import org.eclipse.imagen.JAI;
import org.eclipse.imagen.LookupTableJAI;
import org.eclipse.imagen.PlanarImage;
import org.eclipse.imagen.RenderedOp;
import org.eclipse.imagen.remote.RemoteJAI;

public class JAIImageReader {

    public static PlanarImage readImage(String filename, RemoteJAI client) {

        ParameterBlock pb = new ParameterBlock();
        pb.add(filename);
        pb.add(null);
        pb.add(Boolean.FALSE);
        RenderedOp image = client.create("fileload", pb, null);

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
            image = JAI.create("lookup", image, lut);
        }

        return image;
    }
}
