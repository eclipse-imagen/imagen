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
import java.awt.image.Raster;
import java.awt.image.WritableRenderedImage;
import org.eclipse.imagen.iterator.WritableRandomIter;

/** @since EA2 */
public final class WritableRandomIterFallback extends RandomIterFallback implements WritableRandomIter {

    WritableRenderedImage wim;

    public WritableRandomIterFallback(WritableRenderedImage im, Rectangle bounds) {
        super(im, bounds);
        this.wim = im;
    }

    private void makeCurrentWritable(int xLocal, int yLocal) {
        int xIDNew = xTiles[xLocal];
        int yIDNew = yTiles[yLocal];

        if ((xIDNew != xID) || (yIDNew != yID) || (dataBuffer == null)) {
            if (dataBuffer != null) {
                wim.releaseWritableTile(xID, yID);
            }
            xID = xIDNew;
            yID = yIDNew;
            Raster tile = wim.getWritableTile(xID, yID);

            this.dataBuffer = tile.getDataBuffer();
            this.sampleModelTranslateX = tile.getSampleModelTranslateX();
            this.sampleModelTranslateY = tile.getSampleModelTranslateY();
        }
    }

    public void setSample(int x, int y, int b, int s) {
        makeCurrentWritable(x - boundsX, y - boundsY);
        sampleModel.setSample(x - sampleModelTranslateX, y - sampleModelTranslateY, b, s, dataBuffer);
    }

    public void setSample(int x, int y, int b, float s) {
        makeCurrentWritable(x - boundsX, y - boundsY);
        sampleModel.setSample(x - sampleModelTranslateX, y - sampleModelTranslateY, b, s, dataBuffer);
    }

    public void setSample(int x, int y, int b, double s) {
        makeCurrentWritable(x - boundsX, y - boundsY);
        sampleModel.setSample(x - sampleModelTranslateX, y - sampleModelTranslateY, b, s, dataBuffer);
    }

    public void setPixel(int x, int y, int[] iArray) {
        makeCurrentWritable(x - boundsX, y - boundsY);
        sampleModel.setPixel(x - sampleModelTranslateX, y - sampleModelTranslateY, iArray, dataBuffer);
    }

    public void setPixel(int x, int y, float[] fArray) {
        makeCurrentWritable(x - boundsX, y - boundsY);
        sampleModel.setPixel(x - sampleModelTranslateX, y - sampleModelTranslateY, fArray, dataBuffer);
    }

    public void setPixel(int x, int y, double[] dArray) {
        makeCurrentWritable(x - boundsX, y - boundsY);
        sampleModel.setPixel(x - sampleModelTranslateX, y - sampleModelTranslateY, dArray, dataBuffer);
    }

    public void done() {
        if (dataBuffer != null) {
            wim.releaseWritableTile(xID, yID);
        }
        dataBuffer = null;
    }
}
