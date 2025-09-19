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
import java.awt.image.DataBufferFloat;
import java.awt.image.RenderedImage;

/** @since EA2 */
public class RandomIterCSMFloat extends RandomIterCSM {

    float[][] bankData;

    public RandomIterCSMFloat(RenderedImage im, Rectangle bounds) {
        super(im, bounds);
    }

    protected final void dataBufferChanged() {
        this.bankData = ((DataBufferFloat) dataBuffer).getBankData();
    }

    public final int getSample(int x, int y, int b) {
        makeCurrent(x - boundsX, y - boundsX);
        return (int) bankData[b][
                (x - sampleModelTranslateX) * pixelStride
                        + (y - sampleModelTranslateY) * scanlineStride
                        + bandOffsets[b]];
    }

    public final float getSampleFloat(int x, int y, int b) {
        makeCurrent(x - boundsX, y - boundsX);
        return bankData[b][
                (x - sampleModelTranslateX) * pixelStride
                        + (y - sampleModelTranslateY) * scanlineStride
                        + bandOffsets[b]];
    }

    public final double getSampleDouble(int x, int y, int b) {
        makeCurrent(x - boundsX, y - boundsX);
        return (double) bankData[b][
                (x - sampleModelTranslateX) * pixelStride
                        + (y - sampleModelTranslateY) * scanlineStride
                        + bandOffsets[b]];
    }

    public float[] getPixel(int x, int y, float[] fArray) {
        if (fArray == null) {
            fArray = new float[numBands];
        }

        int offset = (x - sampleModelTranslateX) * pixelStride + (y - sampleModelTranslateY) * scanlineStride;
        for (int b = 0; b < numBands; b++) {
            fArray[b] = bankData[b][offset + bandOffsets[b]];
        }
        return fArray;
    }
}
