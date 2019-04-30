/*
 * Copyright (c) [2019,] 2019, Oracle and/or its affiliates. All rights reserved.
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
import java.awt.image.RenderedImage;
import org.eclipse.imagen.iterator.RandomIter;

/**
 * @since EA2
 */
public class RandomIterCSMDouble extends RandomIterCSM {

    public RandomIterCSMDouble(RenderedImage im, Rectangle bounds) {
        super(im, bounds);
    }

    public final int getSample(int x, int y, int b) {
        return (int)getSampleDouble(x, y, b);
    }

    public final float getSampleFloat(int x, int y, int b) {
        return (float)getSampleDouble(x, y, b);
    }

    public final double getSampleDouble(int x, int y, int b) {
        return 0.0;
    }
}
