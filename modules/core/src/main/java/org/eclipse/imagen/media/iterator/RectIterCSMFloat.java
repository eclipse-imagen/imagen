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
 * SPDX-License-Identifier: Apache-2.0
 */
package org.eclipse.imagen.media.iterator;

import java.awt.Rectangle;
import java.awt.image.DataBufferFloat;
import java.awt.image.RenderedImage;

/** */
public class RectIterCSMFloat extends RectIterCSM {

    float[][] bankData;
    float[] bank;

    public RectIterCSMFloat(RenderedImage im, Rectangle bounds) {
        super(im, bounds);

        this.bankData = new float[numBands + 1][];
        dataBufferChanged();
    }

    protected final void dataBufferChanged() {
        if (bankData == null) {
            return;
        }

        float[][] bd = ((DataBufferFloat) dataBuffer).getBankData();
        for (int i = 0; i < numBands; i++) {
            bankData[i] = bd[bankIndices[i]];
        }
        bank = bankData[b];

        adjustBandOffsets();
    }

    public void startBands() {
        super.startBands();
        bank = bankData[0];
    }

    public void nextBand() {
        super.nextBand();
        bank = bankData[b];
    }

    public final int getSample() {
        return (int) bank[offset + bandOffset];
    }

    public final int getSample(int b) {
        return (int) bankData[b][offset + bandOffsets[b]];
    }

    public final float getSampleFloat() {
        return bank[offset + bandOffset];
    }

    public final float getSampleFloat(int b) {
        return bankData[b][offset + bandOffsets[b]];
    }

    public final double getSampleDouble() {
        return (double) bank[offset + bandOffset];
    }

    public final double getSampleDouble(int b) {
        return (double) bankData[b][offset + bandOffsets[b]];
    }

    public int[] getPixel(int[] iArray) {
        if (iArray == null) {
            iArray = new int[numBands];
        }
        for (int b = 0; b < numBands; b++) {
            iArray[b] = (int) bankData[b][offset + bandOffsets[b]];
        }
        return iArray;
    }

    public float[] getPixel(float[] fArray) {
        if (fArray == null) {
            fArray = new float[numBands];
        }
        for (int b = 0; b < numBands; b++) {
            fArray[b] = bankData[b][offset + bandOffsets[b]];
        }
        return fArray;
    }

    public double[] getPixel(double[] dArray) {
        if (dArray == null) {
            dArray = new double[numBands];
        }
        for (int b = 0; b < numBands; b++) {
            dArray[b] = (double) bankData[b][offset + bandOffsets[b]];
        }
        return dArray;
    }
}
