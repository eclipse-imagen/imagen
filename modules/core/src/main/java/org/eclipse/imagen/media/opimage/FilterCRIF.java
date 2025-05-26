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

package org.eclipse.imagen.media.opimage;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.util.Arrays;
import org.eclipse.imagen.CRIFImpl;
import org.eclipse.imagen.JAI;
import org.eclipse.imagen.KernelJAI;

/**
 * This CRIF implements rendering-independent filtering (blur/sharpen).
 *
 * @since 1.0
 * @see FilterDescriptor
 */
final class FilterCRIF extends CRIFImpl {
    /** Step size of the filter parameter indicating a step from one kernel size to the next. */
    private static final int STEPSIZE = 5;

    /** Create a kernel given the filter parameter. Positive is blur, negative sharpen. */
    private static final KernelJAI createKernel(double p) {
        int STEPSIZE = 5;

        if (p == 0.0) {
            return null;
        }

        double pAbs = Math.abs(p);
        int idx = ((int) pAbs) / STEPSIZE;
        double frac = (10.0F / STEPSIZE) * (pAbs - idx * STEPSIZE);
        double blend = 1.0 / 99.0 * (Math.pow(10.0, 0.2 * frac) - 1.0);

        // First create a low-pass kernel.
        int size;
        float[] data;
        if (idx * STEPSIZE == pAbs) {
            // The parameter is at the left end of an interval so no
            // blending of kernels is required.
            size = 2 * idx + 1;
            data = new float[size * size];
            float val = 1.0F / (size * size);
            Arrays.fill(data, val);
        } else {
            // Create data for the left and right intervals and blend them.
            int size1 = 2 * idx + 1;
            size = size1 + 2;
            data = new float[size * size];
            float val1 = (1.0F / (size1 * size1)) * (1.0F - (float) blend);
            int row = size;
            for (int j = 1; j < size - 1; j++) {
                for (int i = 1; i < size - 1; i++) {
                    data[row + i] = val1;
                }
                row += size;
            }
            float val2 = (1.0F / (size * size)) * (float) blend;
            for (int i = 0; i < data.length; i++) {
                data[i] += val2;
            }
        }

        // For positive factor generate a high-pass kernel.
        if (p > 0.0) {
            // Subtract the low-pass kernel data from the image.
            for (int i = 0; i < data.length; i++) {
                data[i] *= -1.0;
            }
            data[data.length / 2] += 2.0F;
        }

        return new KernelJAI(size, size, data);
    }

    /** Constructor. */
    public FilterCRIF() {
        super();
    }

    /** Implementation of "RIF" create(). */
    public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints) {
        KernelJAI kernel = createKernel(paramBlock.getFloatParameter(0));

        return kernel == null
                ? paramBlock.getRenderedSource(0)
                : JAI.create("convolve", paramBlock.getRenderedSource(0), kernel);
    }
}
