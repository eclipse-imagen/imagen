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

/**
 * Diagnostics Interface for SunTileCache. These routines apply to the tile cache, not the cached tile. All methods are
 * implicitly public.
 *
 * @since ImageN 1.1
 */
package org.eclipse.imagen.media.util;

public interface CacheDiagnostics {

    /** Enable diagnostic monitoring of the tile cache. */
    void enableDiagnostics();

    /** Disable diagnostic monitoring of the tile cache. */
    void disableDiagnostics();

    /** Returns the total number of tiles in a particular cache. */
    long getCacheTileCount();

    /** Returns the total memory used in a particular cache. */
    long getCacheMemoryUsed();

    /** Returns the number of times this tile was requested when it was in the tile cache. */
    long getCacheHitCount();

    /** Returns the number of times this tile was requested when it was not in the tile cache. */
    long getCacheMissCount();

    /** Resets the hit and miss counts to zero. */
    void resetCounts(); // resets hit,miss counts
}
