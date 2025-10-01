/* Copyright 2009-2011 Michael Bedward, and others
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
 *  Copyright (c) 2009-2011, Michael Bedward. All rights reserved.
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

import org.eclipse.imagen.KernelImageN;

/**
 * Static utility methods for KernelImageN objects.
 *
 * @author Michael Bedward
 * @since 1.0
 * @version $Id$
 */
public class KernelUtil {

    /**
     * Create a copy of the input kernel with element values standardized to sum to 1.0. The input kernel's dimensions
     * and location of the key element are retained by the new kernel.
     *
     * @param kernel the input kernel
     * @return a new KernelImageN object
     */
    public static KernelImageN standardize(KernelImageN kernel) {
        float[] data = kernel.getKernelData();
        float sum = 0f;
        for (float f : data) {
            sum += f;
        }

        for (int i = 0; i < data.length; i++) {
            data[i] /= sum;
        }

        return new KernelImageN(kernel.getWidth(), kernel.getHeight(), kernel.getXOrigin(), kernel.getYOrigin(), data);
    }

    /**
     * Creates a new kernel by copying the one provided and setting the element at {@code (x, y)} to {@code value}.
     *
     * @param kernel the existing kernel
     * @param x element X ordinate
     * @param y element Y ordinate
     * @param newValue new element value
     * @return the new kernel
     * @throws IllegalArgumentException if {@code kernel} is {@code null} or {@code x} or {@code y} are out of bounds
     */
    public static KernelImageN setElement(KernelImageN kernel, int x, int y, float newValue) {
        if (kernel == null) {
            throw new IllegalArgumentException("kernel must not be null");
        }

        float data[] = kernel.getKernelData();
        final int w = kernel.getWidth();
        final int h = kernel.getHeight();

        if (x < 0 || x >= w) {
            throw new IllegalArgumentException("x out of bounds: " + x);
        }
        if (y < 0 || y >= w) {
            throw new IllegalArgumentException("y out of bounds: " + y);
        }

        int index = y * w + x;
        data[index] = newValue;
        return new KernelImageN(w, h, kernel.getXOrigin(), kernel.getYOrigin(), data);
    }

    /**
     * A utility function that returns a string representation of a KernelImageN object's data.
     *
     * @param kernel the input kernel
     * @param multiLine if true, each row of kernel data is followed by a newline; if false, the string contains no
     *     newlines
     * @return a String
     */
    public static String kernelToString(KernelImageN kernel, boolean multiLine) {
        float[] data = kernel.getKernelData();
        int w = kernel.getWidth();
        int h = kernel.getHeight();
        StringBuilder sb = new StringBuilder();

        boolean binaryData = true;
        for (int i = 0; i < data.length && binaryData; i++) {
            if (!(CompareOp.aequal(data[i], 0) || CompareOp.aequal(data[i], 1))) {
                binaryData = false;
            }
        }

        int k = 0;
        sb.append("[");
        for (int i = 0; i < w; i++) {
            sb.append("[");
            for (int j = 0; j < h; j++, k++) {
                if (binaryData) {
                    sb.append((int) data[k]);
                } else {
                    sb.append(String.format("%.4f", data[k]));
                    if (j < w - 1) sb.append(" ");
                }
            }
            sb.append("]");
            if (i < w - 1) {
                if (multiLine) {
                    sb.append("\n ");
                } else {
                    sb.append(" ");
                }
            }
        }
        sb.append("]");

        return sb.toString();
    }
}
