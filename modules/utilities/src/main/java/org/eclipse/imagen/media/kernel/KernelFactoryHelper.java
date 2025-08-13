/* Copyright 2009 Michael Bedward, and others
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 *  Copyright (c) 2009, Michael Bedward. All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification,
 *  are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.eclipse.imagen.media.kernel;

import java.util.Arrays;

/**
 * A helper class for KernelFactory. This is package-private and not intended for use by client code.
 *
 * @author Michael Bedward
 * @since 1.0
 * @version $Id$
 */
class KernelFactoryHelper {
    private static final float FTOL = 1.0e-8f;
    private static final double DTOL = 1.0e-8d;

    /** Constructor. Empty: defined only to ensure that it is package private. */
    KernelFactoryHelper() {}

    /** Create a new float array with data for a raster circle outline of specified radius */
    float[] makeCircle(int radius) {
        int w = 2 * radius + 1;
        float[] m = new float[w * w];

        int[] offset = new int[w];
        for (int i = 0, o = 0; i < w; i++, o += w) offset[i] = o;

        int x = radius, y = 0;
        int r2 = radius * radius;
        while (x > 0) {
            int ix = radius + x;
            int iy = radius + y;
            m[ix + offset[iy]] = 1f;
            m[w - 1 - ix + offset[iy]] = 1f;
            iy = w - 1 - iy;
            m[ix + offset[iy]] = 1f;
            m[w - 1 - ix + offset[iy]] = 1f;
            y--;
            x = (int) Math.sqrt(r2 - y * y);
        }

        m[radius] = 1f;
        m[radius + offset[2 * radius]] = 1f;

        return m;
    }

    /**
     * Creates an array for a rectangular kernel with all elements set to {@code value}.
     *
     * @param w width
     * @param h height
     * @param value value for all elements
     */
    float[] makeRect(int w, int h, float value) {
        float[] m = new float[w * h];
        Arrays.fill(m, value);
        return m;
    }

    /**
     * Takes a float array with data for a closed raster shape and fills the shape by setting element values to
     * {@code value}
     *
     * @param data data array of length {@code w} * {@code h}
     * @param w width
     * @param h height
     */
    void rowFill(float[] data, int w, int h, float value) {

        int k = 0;
        for (int y = 0; y < h; y++) {
            int left = -1, right = -1;
            for (int x = 0; x < w; x++, k++) {
                if (data[k] > 0) {
                    if (left < 0) {
                        left = k;
                    } else {
                        right = k;
                    }
                }
            }

            while (right > left + 1) {
                data[--right] = value;
            }
        }
    }

    /**
     * Normalize the input angle to be between -PI and PI
     *
     * @param angle input angle (radians)
     * @return normalized angle
     */
    float normalizeAngle(float angle) {
        while (angle < -Math.PI) {
            angle += Math.PI * 2;
        }

        while (angle > Math.PI) {
            angle -= Math.PI * 2;
        }

        return angle;
    }

    /**
     * Normalize the input angle to be between 0 and 2*PI
     *
     * @param angle input angle (radians)
     * @return normalized angle
     */
    float normalizeAnglePositive(float angle) {
        while (angle < 0) {
            angle += Math.PI * 2;
        }

        while (angle > Math.PI * 2) {
            angle -= Math.PI * 2;
        }

        return angle;
    }
}
