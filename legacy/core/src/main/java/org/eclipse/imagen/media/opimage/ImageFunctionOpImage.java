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
package org.eclipse.imagen.media.opimage;

import java.awt.Rectangle;
import java.awt.image.DataBuffer;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Map;
import org.eclipse.imagen.ImageFunction;
import org.eclipse.imagen.ImageLayout;
import org.eclipse.imagen.PlanarImage;
import org.eclipse.imagen.RasterFactory;
import org.eclipse.imagen.SourcelessOpImage;

/**
 * An OpImage class to generate an image from a functional description.
 *
 * @see org.eclipse.imagen.operator.ImageFunctionDescriptor
 * @see org.eclipse.imagen.ImageFunction
 * @since EA4
 */
final class ImageFunctionOpImage extends SourcelessOpImage {
    /** The functional description of the image. */
    protected ImageFunction function;

    /** The X scale factor. */
    protected float xScale;

    /** The Y scale factor. */
    protected float yScale;

    /** The X translation. */
    protected float xTrans;

    /** The Y translation. */
    protected float yTrans;

    private static SampleModel sampleModelHelper(int numBands, ImageLayout layout) {
        SampleModel sampleModel;
        if (layout != null && layout.isValid(ImageLayout.SAMPLE_MODEL_MASK)) {
            sampleModel = layout.getSampleModel(null);

            if (sampleModel.getNumBands() != numBands) {
                throw new RuntimeException(JaiI18N.getString("ImageFunctionRIF0"));
            }
        } else { // Create a SampleModel.
            // Use a dummy width and height, OpImage will fix them
            sampleModel = RasterFactory.createBandedSampleModel(DataBuffer.TYPE_FLOAT, 1, 1, numBands);
        }

        return sampleModel;
    }

    /**
     * Constructs an ImageFunctionOpImage.
     *
     * @param width The output image width.
     * @param height The output image height.
     */
    public ImageFunctionOpImage(
            ImageFunction function,
            int minX,
            int minY,
            int width,
            int height,
            float xScale,
            float yScale,
            float xTrans,
            float yTrans,
            Map config,
            ImageLayout layout) {
        super(
                layout,
                config,
                sampleModelHelper(function.getNumElements() * (function.isComplex() ? 2 : 1), layout),
                minX,
                minY,
                width,
                height);

        // Cache the parameters.
        this.function = function;
        this.xScale = xScale;
        this.yScale = yScale;
        this.xTrans = xTrans;
        this.yTrans = yTrans;
    }

    /** Compute a Rectangle of output data based on the ImageFunction. Note that the sources parameter is not used. */
    protected void computeRect(PlanarImage[] sources, WritableRaster dest, Rectangle destRect) {
        // Cache some info.
        int dataType = sampleModel.getTransferType();
        int numBands = sampleModel.getNumBands();

        // Allocate the actual data memory.
        int length = width * height;
        Object data;
        if (dataType == DataBuffer.TYPE_DOUBLE) {
            data = function.isComplex() ? (Object) new double[2][length] : (Object) new double[length];
        } else {
            data = function.isComplex() ? (Object) new float[2][length] : (Object) new float[length];
        }

        if (dataType == DataBuffer.TYPE_DOUBLE) {
            double[] real = function.isComplex() ? ((double[][]) data)[0] : ((double[]) data);
            double[] imag = function.isComplex() ? ((double[][]) data)[1] : null;

            int element = 0;
            for (int band = 0; band < numBands; band++) {
                function.getElements(
                        xScale * (destRect.x - xTrans),
                        yScale * (destRect.y - yTrans),
                        xScale,
                        yScale,
                        destRect.width,
                        destRect.height,
                        element++,
                        real,
                        imag);
                dest.setSamples(destRect.x, destRect.y, destRect.width, destRect.height, band, (double[]) real);
                if (function.isComplex()) {
                    dest.setSamples(destRect.x, destRect.y, destRect.width, destRect.height, ++band, imag);
                }
            } // for (band ...
        } else { // not double precision
            float[] real = function.isComplex() ? ((float[][]) data)[0] : ((float[]) data);
            float[] imag = function.isComplex() ? ((float[][]) data)[1] : null;

            int element = 0;
            for (int band = 0; band < numBands; band++) {
                function.getElements(
                        xScale * (destRect.x - xTrans),
                        yScale * (destRect.y - yTrans),
                        xScale,
                        yScale,
                        destRect.width,
                        destRect.height,
                        element++,
                        real,
                        imag);
                dest.setSamples(destRect.x, destRect.y, destRect.width, destRect.height, band, real);
                if (function.isComplex()) {
                    dest.setSamples(destRect.x, destRect.y, destRect.width, destRect.height, ++band, imag);
                }
            } // for (band ...
        }
    }
}
