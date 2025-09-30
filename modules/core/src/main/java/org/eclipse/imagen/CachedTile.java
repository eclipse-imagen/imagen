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

import java.awt.image.Raster;
import java.awt.image.RenderedImage;

/**
 * Public interface for cached tiles used to retrieve information about the tile.
 *
 * @since ImageN 0.4.0
 */
public interface CachedTile {

    /**
     * Returns the image operation to which this cached tile belongs. In Sun Microsystems implementation, this is a
     * RenderedImage.
     */
    RenderedImage getOwner();

    /** Returns the cached tile. In Sun Microsystems implementation, this object is a Raster. */
    Raster getTile();

    /**
     * Returns a cost metric associated with the tile. This value is used to determine which tiles get removed from the
     * cache.
     */
    Object getTileCacheMetric();

    /** Returns the time stamp of the cached tile. */
    long getTileTimeStamp();

    /** Returns the memory size of the cached tile */
    long getTileSize();

    /**
     * Returns information about which method triggered a notification event. In the Sun Microsystems implementation,
     * events include add, remove and update tile information.
     */
    int getAction();
}
