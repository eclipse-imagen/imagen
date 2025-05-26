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

import java.awt.Point;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import org.eclipse.imagen.PlanarImage;
import org.eclipse.imagen.RasterFactory;
import org.eclipse.imagen.media.util.ImageUtil;

/**
 * An OpImage class to generate an image of constant color.
 *
 * <p>ConstantOpImage defines a constant PlanarImage. It is implemented as a subclass of PatternOpImage with a
 * constant-colored pattern.
 */
final class ConstantOpImage extends PatternOpImage {

    /** Creates a Raster defining tile (0, 0) of the master pattern. */
    private static Raster makePattern(SampleModel sampleModel, Number[] bandValues) {
        WritableRaster pattern = RasterFactory.createWritableRaster(sampleModel, new Point(0, 0));

        int width = sampleModel.getWidth();
        int height = sampleModel.getHeight();
        int dataType = sampleModel.getTransferType();
        int numBands = sampleModel.getNumBands();

        switch (dataType) {
            case DataBuffer.TYPE_BYTE:
                int[] bvalues = new int[numBands];
                for (int i = 0; i < numBands; i++) {
                    bvalues[i] = bandValues[i].intValue() & ImageUtil.BYTE_MASK;
                }

                /* Put the first scanline in with setPixels. */
                for (int x = 0; x < width; x++) {
                    pattern.setPixel(x, 0, bvalues);
                }
                break;

            case DataBuffer.TYPE_USHORT: // USHORT is less than 127
            case DataBuffer.TYPE_SHORT:
            case DataBuffer.TYPE_INT:
                int[] ivalues = new int[numBands];
                for (int i = 0; i < numBands; i++) {
                    ivalues[i] = bandValues[i].intValue();
                }

                /* Put the first scanline in with setPixels. */
                for (int x = 0; x < width; x++) {
                    pattern.setPixel(x, 0, ivalues);
                }
                break;

            case DataBuffer.TYPE_FLOAT:
                float[] fvalues = new float[numBands];
                for (int i = 0; i < numBands; i++) {
                    fvalues[i] = bandValues[i].floatValue();
                }

                /* Put the first scanline in with setPixels. */
                for (int x = 0; x < width; x++) {
                    pattern.setPixel(x, 0, fvalues);
                }
                break;

            case DataBuffer.TYPE_DOUBLE:
                double[] dvalues = new double[numBands];
                for (int i = 0; i < numBands; i++) {
                    dvalues[i] = bandValues[i].doubleValue();
                }

                /* Put the first scanline in with setPixels. */
                for (int x = 0; x < width; x++) {
                    pattern.setPixel(x, 0, dvalues);
                }
                break;
        }

        /* Copy the first line out. */
        Object odata = pattern.getDataElements(0, 0, width, 1, null);

        /* Use the first line to copy other rows. */
        for (int y = 1; y < height; y++) {
            pattern.setDataElements(0, y, width, 1, odata);
        }

        return pattern;
    }

    private static SampleModel makeSampleModel(int width, int height, Number[] bandValues) {
        int numBands = bandValues.length;
        int dataType;

        if (bandValues instanceof Byte[]) {
            dataType = DataBuffer.TYPE_BYTE;
        } else if (bandValues instanceof Short[]) {
            /* If all band values are positive, use UShort, else use Short. */
            dataType = DataBuffer.TYPE_USHORT;

            Short[] shortValues = (Short[]) bandValues;
            for (int i = 0; i < numBands; i++) {
                if (shortValues[i].shortValue() < 0) {
                    dataType = DataBuffer.TYPE_SHORT;
                    break;
                }
            }
        } else if (bandValues instanceof Integer[]) {
            dataType = DataBuffer.TYPE_INT;
        } else if (bandValues instanceof Float[]) {
            dataType = DataBuffer.TYPE_FLOAT;
        } else if (bandValues instanceof Double[]) {
            dataType = DataBuffer.TYPE_DOUBLE;
        } else {
            dataType = DataBuffer.TYPE_UNDEFINED;
        }

        return RasterFactory.createPixelInterleavedSampleModel(dataType, width, height, numBands);
    }

    private static Raster patternHelper(int width, int height, Number[] bandValues) {
        SampleModel sampleModel = makeSampleModel(width, height, bandValues);
        return makePattern(sampleModel, bandValues);
    }

    private static ColorModel colorModelHelper(Number[] bandValues) {
        SampleModel sampleModel = makeSampleModel(1, 1, bandValues);
        return PlanarImage.createColorModel(sampleModel);
    }

    /**
     * Constructs a ConstantOpImage from a set of sample values. The ImageLayout object must contain a complete set of
     * information.
     *
     * @param layout an ImageLayout containing image bounds, tile layout, and SampleModel information.
     * @param bandValues an array of Numbers representing the values of each image band.
     */
    public ConstantOpImage(
            int minX, int minY, int width, int height, int tileWidth, int tileHeight, Number[] bandValues) {
        super(
                patternHelper(tileWidth, tileHeight, bandValues),
                colorModelHelper(bandValues),
                minX,
                minY,
                width,
                height);
    }
}
