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

package org.eclipse.imagen.media.codecimpl;

import java.awt.image.ColorModel;
import java.awt.image.Raster;

/** A simple class that provides RenderedImage functionality given a Raster and a ColorModel. */
public class SingleTileRenderedImage extends SimpleRenderedImage {

    Raster ras;

    /**
     * Constructs a SingleTileRenderedImage based on a Raster and a ColorModel.
     *
     * @param ras A Raster that will define tile (0, 0) of the image.
     * @param cm A ColorModel that will serve as the image's ColorModel.
     */
    public SingleTileRenderedImage(Raster ras, ColorModel colorModel) {
        this.ras = ras;

        this.tileGridXOffset = this.minX = ras.getMinX();
        this.tileGridYOffset = this.minY = ras.getMinY();
        this.tileWidth = this.width = ras.getWidth();
        this.tileHeight = this.height = ras.getHeight();
        this.sampleModel = ras.getSampleModel();
        this.colorModel = colorModel;
    }

    /** Returns the image's Raster as tile (0, 0). */
    public Raster getTile(int tileX, int tileY) {
        if (tileX != 0 || tileY != 0) {
            throw new IllegalArgumentException(JaiI18N.getString("SingleTileRenderedImage0"));
        }
        return ras;
    }
}
