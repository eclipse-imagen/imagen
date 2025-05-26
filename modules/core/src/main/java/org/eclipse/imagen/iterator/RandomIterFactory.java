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

package org.eclipse.imagen.iterator;

import java.awt.Rectangle;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.awt.image.WritableRenderedImage;
import org.eclipse.imagen.media.iterator.RandomIterFallback;
import org.eclipse.imagen.media.iterator.WrapperRI;
import org.eclipse.imagen.media.iterator.WrapperWRI;
import org.eclipse.imagen.media.iterator.WritableRandomIterFallback;

/**
 * A factory class to instantiate instances of the RandomIter and WritableRandomIter interfaces on sources of type
 * Raster, RenderedImage, and WritableRenderedImage.
 *
 * @see RandomIter
 * @see WritableRandomIter
 */
public class RandomIterFactory {

    /** Prevent this class from ever being instantiated. */
    private RandomIterFactory() {}

    /**
     * Constructs and returns an instance of RandomIter suitable for iterating over the given bounding rectangle within
     * the given RenderedImage source. If the bounds parameter is null, the entire image will be used.
     *
     * @param im a read-only RenderedImage source.
     * @param bounds the bounding Rectangle for the iterator, or null.
     * @return a RandomIter allowing read-only access to the source.
     */
    public static RandomIter create(RenderedImage im, Rectangle bounds) {
        if (bounds == null) {
            bounds = new Rectangle(
                    im.getMinX(), im.getMinY(),
                    im.getWidth(), im.getHeight());
        }

        SampleModel sm = im.getSampleModel();
        if (sm instanceof ComponentSampleModel) {
            switch (sm.getDataType()) {
                case DataBuffer.TYPE_BYTE:
                    // return new RandomIterCSMByte(im, bounds);
                case DataBuffer.TYPE_SHORT:
                    // return new RandomIterCSMShort(im, bounds);
                case DataBuffer.TYPE_USHORT:
                    // return new RandomIterCSMUShort(im, bounds);
                case DataBuffer.TYPE_INT:
                    // return new RandomIterCSMInt(im, bounds);
                case DataBuffer.TYPE_FLOAT:
                    // return new RandomIterCSMFloat(im, bounds);
                case DataBuffer.TYPE_DOUBLE:
                    // return new RandomIterCSMDouble(im, bounds);
            }
        }

        return new RandomIterFallback(im, bounds);
    }

    /**
     * Constructs and returns an instance of RandomIter suitable for iterating over the given bounding rectangle within
     * the given Raster source. If the bounds parameter is null, the entire Raster will be used.
     *
     * @param ras a read-only Raster source.
     * @param bounds the bounding Rectangle for the iterator, or null.
     * @return a RandomIter allowing read-only access to the source.
     */
    public static RandomIter create(Raster ras, Rectangle bounds) {
        RenderedImage im = new WrapperRI(ras);
        return create(im, bounds);
    }

    /**
     * Constructs and returns an instance of WritableRandomIter suitable for iterating over the given bounding rectangle
     * within the given WritableRenderedImage source. If the bounds parameter is null, the entire image will be used.
     *
     * @param im a WritableRenderedImage source.
     * @param bounds the bounding Rectangle for the iterator, or null.
     * @return a WritableRandomIter allowing read/write access to the source.
     */
    public static WritableRandomIter createWritable(WritableRenderedImage im, Rectangle bounds) {
        if (bounds == null) {
            bounds = new Rectangle(
                    im.getMinX(), im.getMinY(),
                    im.getWidth(), im.getHeight());
        }

        SampleModel sm = im.getSampleModel();
        if (sm instanceof ComponentSampleModel) {
            switch (sm.getDataType()) {
                case DataBuffer.TYPE_BYTE:
                    // return new WritableRandomIterCSMByte(im, bounds);
                case DataBuffer.TYPE_SHORT:
                    // return new WritableRandomIterCSMShort(im, bounds);
                case DataBuffer.TYPE_USHORT:
                    // return new WritableRandomIterCSMUShort(im, bounds);
                case DataBuffer.TYPE_INT:
                    // return new WritableRandomIterCSMInt(im, bounds);
                case DataBuffer.TYPE_FLOAT:
                    // return new WritableRandomIterCSMFloat(im, bounds);
                case DataBuffer.TYPE_DOUBLE:
                    // return new WritableRandomIterCSMDouble(im, bounds);
            }
        }

        return new WritableRandomIterFallback(im, bounds);
    }

    /**
     * Constructs and returns an instance of WritableRandomIter suitable for iterating over the given bounding rectangle
     * within the given WritableRaster source. If the bounds parameter is null, the entire Raster will be used.
     *
     * @param ras a WritableRaster source.
     * @param bounds the bounding Rectangle for the iterator, or null.
     * @return a WritableRandomIter allowing read/write access to the source.
     */
    public static WritableRandomIter createWritable(WritableRaster ras, Rectangle bounds) {
        WritableRenderedImage im = new WrapperWRI(ras);
        return createWritable(im, bounds);
    }
}
