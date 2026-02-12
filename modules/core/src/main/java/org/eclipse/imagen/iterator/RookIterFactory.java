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
package org.eclipse.imagen.iterator;

import java.awt.Rectangle;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.awt.image.WritableRenderedImage;
// import org.eclipse.imagen.media.iterator.RookIterCSMByte;
// import org.eclipse.imagen.media.iterator.RookIterCSMShort;
// import org.eclipse.imagen.media.iterator.RookIterCSMUShort;
// import org.eclipse.imagen.media.iterator.RookIterCSMInt;
// import org.eclipse.imagen.media.iterator.RookIterCSMFloat;
// import org.eclipse.imagen.media.iterator.RookIterCSMDouble;
import org.eclipse.imagen.media.iterator.RookIterFallback;
import org.eclipse.imagen.media.iterator.WrapperRI;
import org.eclipse.imagen.media.iterator.WrapperWRI;
// import org.eclipse.imagen.media.iterator.WritableRookIterCSMByte;
// import org.eclipse.imagen.media.iterator.WritableRookIterCSMShort;
// import org.eclipse.imagen.media.iterator.WritableRookIterCSMUShort;
// import org.eclipse.imagen.media.iterator.WritableRookIterCSMInt;
// import org.eclipse.imagen.media.iterator.WritableRookIterCSMFloat;
// import org.eclipse.imagen.media.iterator.WritableRookIterCSMDouble;
import org.eclipse.imagen.media.iterator.WritableRookIterFallback;

/**
 * A factory class to instantiate instances of the RookIter and WritableRookIter interfaces on sources of type Raster,
 * RenderedImage, and WritableRenderedImage.
 *
 * @see RookIter
 * @see WritableRookIter
 */
public class RookIterFactory {

    /** Prevent this class from ever being instantiated. */
    private RookIterFactory() {}

    /**
     * Constructs and returns an instance of RookIter suitable for iterating over the given bounding rectangle within
     * the given RenderedImage source. If the bounds parameter is null, the entire image will be used.
     *
     * @param im a read-only RenderedImage source.
     * @param bounds the bounding Rectangle for the iterator, or null.
     * @return a RookIter allowing read-only access to the source.
     */
    public static RookIter create(RenderedImage im, Rectangle bounds) {
        if (bounds == null) {
            bounds = new Rectangle(
                    im.getMinX(), im.getMinY(),
                    im.getWidth(), im.getHeight());
        }

        SampleModel sm = im.getSampleModel();
        if (sm instanceof ComponentSampleModel) {
            switch (sm.getDataType()) {
                case DataBuffer.TYPE_BYTE:
                // return new RookIterCSMByte(im, bounds);
                case DataBuffer.TYPE_SHORT:
                // return new RookIterCSMShort(im, bounds);
                case DataBuffer.TYPE_USHORT:
                // return new RookIterCSMUShort(im, bounds);
                case DataBuffer.TYPE_INT:
                // return new RookIterCSMInt(im, bounds);
                case DataBuffer.TYPE_FLOAT:
                // return new RookIterCSMFloat(im, bounds);
                case DataBuffer.TYPE_DOUBLE:
                    // return new RookIterCSMDouble(im, bounds);
            }
        }

        return new RookIterFallback(im, bounds);
    }

    /**
     * Constructs and returns an instance of RookIter suitable for iterating over the given bounding rectangle within
     * the given Raster source. If the bounds parameter is null, the entire Raster will be used.
     *
     * @param ras a read-only Raster source.
     * @param bounds the bounding Rectangle for the iterator, or null.
     * @return a RookIter allowing read-only access to the source.
     */
    public static RookIter create(Raster ras, Rectangle bounds) {
        RenderedImage im = new WrapperRI(ras);
        return create(im, bounds);
    }

    /**
     * Constructs and returns an instance of WritableRookIter suitable for iterating over the given bounding rectangle
     * within the given WritableRenderedImage source. If the bounds parameter is null, the entire image will be used.
     *
     * @param im a WritableRenderedImage source.
     * @param bounds the bounding Rectangle for the iterator, or null.
     * @return a WritableRookIter allowing read/write access to the source.
     */
    public static WritableRookIter createWritable(WritableRenderedImage im, Rectangle bounds) {
        if (bounds == null) {
            bounds = new Rectangle(
                    im.getMinX(), im.getMinY(),
                    im.getWidth(), im.getHeight());
        }

        SampleModel sm = im.getSampleModel();
        if (sm instanceof ComponentSampleModel) {
            switch (sm.getDataType()) {
                case DataBuffer.TYPE_BYTE:
                // return new WritableRookIterCSMByte(im, bounds);
                case DataBuffer.TYPE_SHORT:
                // return new WritableRookIterCSMShort(im, bounds);
                case DataBuffer.TYPE_USHORT:
                // return new WritableRookIterCSMUShort(im, bounds);
                case DataBuffer.TYPE_INT:
                // return new WritableRookIterCSMInt(im, bounds);
                case DataBuffer.TYPE_FLOAT:
                // return new WritableRookIterCSMFloat(im, bounds);
                case DataBuffer.TYPE_DOUBLE:
                    // return new WritableRookIterCSMDouble(im, bounds);
            }
        }

        return new WritableRookIterFallback(im, bounds);
    }

    /**
     * Constructs and returns an instance of WritableRookIter suitable for iterating over the given bounding rectangle
     * within the given WritableRaster source. If the bounds parameter is null, the entire Raster will be used.
     *
     * @param ras a WritableRaster source.
     * @param bounds the bounding Rectangle for the iterator, or null.
     * @return a WritableRookIter allowing read/write access to the source.
     */
    public static WritableRookIter createWritable(WritableRaster ras, Rectangle bounds) {
        WritableRenderedImage im = new WrapperWRI(ras);
        return createWritable(im, bounds);
    }
}
