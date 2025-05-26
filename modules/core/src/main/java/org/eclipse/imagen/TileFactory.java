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

import java.awt.Point;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;

/**
 * An interface defining a mechanism which may be used to create tiles for an image. Implementations of this interface
 * might be based for example on managing a pool of memory, recycling previously allocated memory, or using an image as
 * a backing store.
 *
 * @since JAI 1.1.2
 */
public interface TileFactory {
    /**
     * Returns a value indicating whether a tile returned by <code>createTile()</code> might be created without
     * allocating new memory for the requisite data array.
     */
    boolean canReclaimMemory();

    /**
     * Returns <code>true</code> if this object can cache in main memory any data arrays which might be made available
     * for future use.
     */
    boolean isMemoryCache();

    /**
     * Returns the amount of memory currently being consumed by data arrays cached within this object. If this object
     * does not cache data arrays then this method will always return zero.
     *
     * @return The amount of memory used by internally cached data arrays, measured in bytes.
     */
    long getMemoryUsed();

    /**
     * Removes references to all internally cached data arrays, if any. If such arrays are objects potentially subject
     * to finalization, then this should make them available for garbage collection provided no references to them are
     * held elsewhere.
     */
    void flush();

    // XXX Comment about optionally not clearing the data arrays?
    /**
     * Create a tile with the specified <code>SampleModel</code> and location, possibly using a <code>DataBuffer</code>
     * constructed using a reclaimed data bank (array). If it is not possible to reclaim an array, a new one will be
     * allocated so that a tile is guaranteed to be generated. If a reclaimed array is used to create the tile, its
     * elements should be set to zero before the tile is returned to the caller.
     *
     * @param sampleModel The <code>SampleModel</code> to use in creating the <code>WritableRaster</code>.
     * @param location The location (<code>minX,&nbsp;minY</code>) of the <code>WritableRaster</code>; if <code>null
     *     </code>, <code>(0,&nbsp;0)</code> will be used.
     * @return A <code>WritableRaster</code> which might have a <code>DataBuffer</code> created using a previously
     *     allocated array.
     * @throws IllegalArgumentionException if <code>sampleModel</code> is <code>null</code>.
     */
    WritableRaster createTile(SampleModel sampleModel, Point location);
}
