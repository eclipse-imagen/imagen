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

package org.eclipse.imagen;

import java.awt.Rectangle;
import java.awt.image.Raster;

/**
 * This class is used by <code>PixelAccessor</code> to store packed image data along with access information. The data
 * must be single banded and one bit in depth, and are stored in a one-dimensional <code>byte</code> array, with eight
 * pixels packed into one <code>byte</code>. To insure that each scanline has an integral number of bytes the last byte
 * may be padded with zeros.
 */
public final class PackedImageData {

    /** The <code>Raster</code> containing the pixel data. */
    public final Raster raster;

    /** The rectangular region within the <code>Raster</code> from which the data are to be retrieved. */
    public final Rectangle rect;

    /** The data array supplied to store the converted data. */
    public final byte[] data;

    /** The number of array elements in each scanline. */
    public final int lineStride;

    /**
     * The number of array elements from the beginning of the data array to the first pixel of the <code>Rectangle
     * </code>. Since there is only one sample per pixel, there is only one offset value.
     */
    public final int offset;

    /**
     * The number of bits into the byte that contains the first pixel of each scanline. This is the same for every
     * scanline. Note that the bit offset is counted from left to right in a byte. That is, the most significant bit
     * (bit 7) has an offset of 0.
     */
    public final int bitOffset;

    /** Whether the data have been coerced to have zero offsets. */
    public final boolean coercedZeroOffset;

    /**
     * Indicates whether the <code>PixelAccessor</code> can and must set the data back into the <code>Raster</code>. If
     * the data does not need to be copied back to the <code>Raster</code>, this variable should be set to <code>false
     * </code>. Only destinations can be set.
     */
    public final boolean convertToDest;

    /**
     * Constructs a PackedImageRaster.
     *
     * @param raster The <code>Raster</code> containing the pixel data.
     * @param rect The rectangular region from which the data are extracted.
     * @param data The byte data array supplied to store the data.
     * @param lineStride The data array increment to move from the coordinate x of line i to coordinate x of line i+1.
     * @param offset The number of bytes from the start of the data array at which to store the first pixel of the
     *     rectangle.
     * @param bitOffset The number of bits into the byte that contains the first pixel of each scanline. This is the
     *     same for every scanline. Note that the bit offset is counted from left to right in a byte. That is, the most
     *     significant bit of a byte (bit 7) has an offset of 0.
     * @param coercedZeroOffset Whether the data have been coerced to have zero offsets.
     * @param convertToDest A <code>boolean</code> indicating whether the data can and must be set back into the <code>
     *     Raster</code>. This applies only to destinations.
     */
    public PackedImageData(
            Raster raster,
            Rectangle rect,
            byte[] data,
            int lineStride,
            int offset,
            int bitOffset,
            boolean coercedZeroOffset,
            boolean convertToDest) {
        this.raster = raster;
        this.rect = rect;
        this.data = data;
        this.lineStride = lineStride;
        this.offset = offset;
        this.bitOffset = bitOffset;
        this.coercedZeroOffset = coercedZeroOffset;
        this.convertToDest = convertToDest;
    }
}
