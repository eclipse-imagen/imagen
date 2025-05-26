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

package org.eclipse.imagen.media.iterator;

import java.awt.Rectangle;
import java.awt.image.ComponentSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;

/** @since EA2 */
public abstract class RandomIterCSM extends RandomIterFallback {

    protected ComponentSampleModel sampleModel;
    protected int pixelStride;
    protected int scanlineStride;
    protected int[] bandOffsets;
    protected int numBands;

    public RandomIterCSM(RenderedImage im, Rectangle bounds) {
        super(im, bounds);
        this.sampleModel = (ComponentSampleModel) im.getSampleModel();
        this.numBands = sampleModel.getNumBands();
        this.pixelStride = sampleModel.getPixelStride();
        this.scanlineStride = sampleModel.getScanlineStride();
    }

    protected void dataBufferChanged() {}

    /**
     * Sets dataBuffer to the correct buffer for the pixel (x, y) = (xLocal + boundsRect.x, yLocal + boundsRect.y).
     *
     * @param xLocal the X coordinate in the local coordinate system.
     * @param yLocal the Y coordinate in the local coordinate system.
     */
    protected final void makeCurrent(int xLocal, int yLocal) {
        int xIDNew = xTiles[xLocal];
        int yIDNew = yTiles[yLocal];

        if ((xIDNew != xID) || (yIDNew != yID) || (dataBuffer == null)) {
            xID = xIDNew;
            yID = yIDNew;
            Raster tile = im.getTile(xID, yID);

            this.dataBuffer = tile.getDataBuffer();
            dataBufferChanged();

            this.bandOffsets = dataBuffer.getOffsets();
        }
    }

    public float getSampleFloat(int x, int y, int b) {
        return (float) getSample(x, y, b);
    }

    public double getSampleDouble(int x, int y, int b) {
        return (double) getSample(x, y, b);
    }

    public int[] getPixel(int x, int y, int[] iArray) {
        if (iArray == null) {
            iArray = new int[numBands];
        }
        for (int b = 0; b < numBands; b++) {
            iArray[b] = getSample(x, y, b);
        }
        return iArray;
    }

    public float[] getPixel(int x, int y, float[] fArray) {
        if (fArray == null) {
            fArray = new float[numBands];
        }
        for (int b = 0; b < numBands; b++) {
            fArray[b] = getSampleFloat(x, y, b);
        }
        return fArray;
    }

    public double[] getPixel(int x, int y, double[] dArray) {
        if (dArray == null) {
            dArray = new double[numBands];
        }
        for (int b = 0; b < numBands; b++) {
            dArray[b] = getSampleDouble(x, y, b);
        }
        return dArray;
    }
}
