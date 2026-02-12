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
import java.awt.image.WritableRenderedImage;
import org.eclipse.imagen.iterator.WritableRookIter;

/** @since EA2 */
public class WritableRookIterFallback extends RookIterFallback implements WritableRookIter {

    public WritableRookIterFallback(WritableRenderedImage im, Rectangle bounds) {
        super(im, bounds);
    }

    public void setSample(int s) {
        sampleModel.setSample(localX, localY, b, s, dataBuffer);
    }

    public void setSample(int b, int s) {
        sampleModel.setSample(localX, localY, b, s, dataBuffer);
    }

    public void setSample(float s) {
        sampleModel.setSample(localX, localY, b, s, dataBuffer);
    }

    public void setSample(int b, float s) {
        sampleModel.setSample(localX, localY, b, s, dataBuffer);
    }

    public void setSample(double s) {
        sampleModel.setSample(localX, localY, b, s, dataBuffer);
    }

    public void setSample(int b, double s) {
        sampleModel.setSample(localX, localY, b, s, dataBuffer);
    }

    public void setPixel(int[] iArray) {
        sampleModel.setPixel(localX, localY, iArray, dataBuffer);
    }

    public void setPixel(float[] fArray) {
        sampleModel.setPixel(localX, localY, fArray, dataBuffer);
    }

    public void setPixel(double[] dArray) {
        sampleModel.setPixel(localX, localY, dArray, dataBuffer);
    }
}
