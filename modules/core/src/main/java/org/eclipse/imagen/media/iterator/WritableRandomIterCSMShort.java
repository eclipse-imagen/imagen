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
import org.eclipse.imagen.iterator.WritableRandomIter;

/** @since EA2 */
public final class WritableRandomIterCSMShort extends RandomIterCSMShort implements WritableRandomIter {

    public WritableRandomIterCSMShort(WritableRenderedImage im, Rectangle bounds) {
        super(im, bounds);
    }

    public void setSample(int x, int y, int b, int val) {}

    public void setSample(int x, int y, int b, float val) {}

    public void setSample(int x, int y, int b, double val) {}

    public void setPixel(int x, int y, int[] iArray) {}

    public void setPixel(int x, int y, float[] fArray) {}

    public void setPixel(int x, int y, double[] dArray) {}
}
