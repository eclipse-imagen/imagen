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

/**
 * An interface to a mechanism which is capable of recycling tiles. In general the term <i>recycle</i> in this context
 * is taken to mean re-using memory allocated to the tile. This would usually be accomplished by reclaiming the data
 * bank (array) associated with the <code>DataBuffer</code> of the tile <code>Raster</code>. It would also be possible
 * by simple translation of a <code>WritableRaster</code> provided the <code>SampleModel</code> was compatible with the
 * tile required by its eventual user.
 *
 * <p><i>Tile recycling should be used with caution. In particular, the calling code must be certain that any tile
 * submitted for recycling not be used elsewhere. If one or more references to tiles submitted to a recycler are held by
 * the calling code then undefined and unexpected behavior may be observed. A similar caution applies to the tile's
 * <code>DataBuffer</code> and the data bank array contained therein.</i>
 */
public interface TileRecycler {
    /**
     * Suggests to the <code>TileRecycler</code> that the parameter tile is no longer needed and may be used in creating
     * a new <code>Raster</code>. This will inevitably result in at least the internal array being overwritten. If a
     * reference to the tile, its <code>DataBuffer</code>, or the data bank(s) of its <code>DataBuffer</code> is held
     * elsewhere in the caller's code, undefined behavior may result. <i>It is the responsibilty of the calling code to
     * ensure that this does not occur.</i>
     *
     * @param tile A tile which mey be re-used either directly or by reclaiming its internal <code>DataBuffer</code> or
     *     primitive data array.
     * @throws IllegalArgumentException if <code>tile</code> is <code>null</code>.
     */
    void recycleTile(Raster tile);
}
