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

package org.eclipse.imagen.media.util;

import org.eclipse.imagen.Interpolation;

/**
 * An <code>Interpolation</code> class which performs simple averaging of all pixels within a specified neighborhood. It
 * is used by the "SubsampleAverage" operation implementations.
 *
 * @since ImageN 0.4.0.2
 */
public class InterpAverage extends Interpolation {
    /**
     * Creates an <code>InterpAverage</code> instance having the supplied dimensions. The left and top padding are
     * <code>(blockX&nbsp;-&nbsp;1)/2</code> and <code>(blockY&nbsp;-&nbsp;1)/2</code>, respectively. The <code>
     * subsampleBitsH</code> and <code>subsampleBitsV</code> instance variables are set to 32.
     *
     * @param blockX The width of the interpolation block.
     * @param blockY The height of the interpolation block.
     * @throws IllegalArgumentException if either parameter is non-positive.
     */
    public InterpAverage(int blockX, int blockY) {
        super(
                blockX,
                blockY,
                (blockX - 1) / 2,
                blockX - 1 - (blockX - 1) / 2,
                (blockY - 1) / 2,
                blockY - 1 - (blockY - 1) / 2,
                32,
                32);

        if (blockX <= 0 || blockY <= 0) {
            throw new IllegalArgumentException("blockX <= 0 || blockY <= 0");
        }
    }

    /** Returns the average of all elements in <code>samples</code>; <code>xfrac</code> is ignored. */
    public int interpolateH(int[] samples, int xfrac) {
        int numSamples = samples.length;
        double total = 0.0;
        for (int i = 0; i < numSamples; i++) {
            total += samples[i] / numSamples;
        }
        return (int) (total + 0.5);
    }

    /** Returns the average of all elements in <code>samples</code>; <code>xfrac</code> is ignored. */
    public float interpolateH(float[] samples, float xfrac) {
        int numSamples = samples.length;
        float total = 0.0F;
        for (int i = 0; i < numSamples; i++) {
            total += samples[i] / numSamples;
        }
        return total;
    }

    /** Returns the average of all elements in <code>samples</code>; <code>xfrac</code> is ignored. */
    public double interpolateH(double[] samples, float xfrac) {
        int numSamples = samples.length;
        double total = 0.0;
        for (int i = 0; i < numSamples; i++) {
            total += samples[i] / numSamples;
        }
        return total;
    }
}
