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

package org.eclipse.imagen.media.test;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Map;
import org.eclipse.imagen.ImageLayout;
import org.eclipse.imagen.RasterFactory;
import org.eclipse.imagen.SourcelessOpImage;

/** Defines a checkerboard image for testing purpose. */
final class CheckerboardOpImage extends SourcelessOpImage {

    private int increment;

    private int checkerSize;

    private int numColors;

    /** Defines a checkerboard image of several grey shades. */
    public CheckerboardOpImage(
            int minX,
            int minY,
            int width,
            int height,
            SampleModel sampleModel,
            Map configuration,
            ImageLayout layout,
            int checkerSize,
            int numColors) {
        super(layout, configuration, sampleModel, minX, minY, width, height);

        if (numColors < 2) {
            numColors = 2;
        }
        this.checkerSize = checkerSize;
        this.numColors = numColors;

        switch (sampleModel.getTransferType()) {
            case DataBuffer.TYPE_BYTE:
                increment = 255 / (numColors - 1);
                break;
            case DataBuffer.TYPE_USHORT:
                increment = 65535 / (numColors - 1);
                break;
            case DataBuffer.TYPE_SHORT:
                increment = Short.MAX_VALUE / (numColors - 1);
                break;
            case DataBuffer.TYPE_INT:
                increment = Integer.MAX_VALUE / (numColors - 1);
                break;
        }
    }

    public Raster computeTile(int tileX, int tileY) {
        int orgX = tileXToX(tileX);
        int orgY = tileYToY(tileY);

        WritableRaster dst = RasterFactory.createWritableRaster(sampleModel, new Point(orgX, orgY));

        Rectangle rect = new Rectangle(orgX, orgY, sampleModel.getWidth(), sampleModel.getHeight());
        rect = rect.intersection(getBounds());

        int numBands = sampleModel.getNumBands();
        int p[] = new int[numBands];

        for (int y = rect.y; y < (rect.y + rect.height); y++) {
            for (int x = rect.x; x < (rect.x + rect.width); x++) {
                int value = getPixelValue(x, y);
                for (int i = 0; i < numBands; i++) {
                    p[i] = value;
                }
                dst.setPixel(x, y, p);
            }
        }
        return dst;
    }

    private int getPixelValue(int x, int y) {
        int squareX = x / checkerSize;
        int squareY = y / checkerSize;
        return (increment * ((squareX + squareY) % numColors));
    }
}
