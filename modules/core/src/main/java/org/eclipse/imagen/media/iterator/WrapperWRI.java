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
package org.eclipse.imagen.media.iterator;

import java.awt.Point;
import java.awt.image.Raster;
import java.awt.image.TileObserver;
import java.awt.image.WritableRaster;
import java.awt.image.WritableRenderedImage;

/** @since EA2 */
public class WrapperWRI extends WrapperRI implements WritableRenderedImage {

    WritableRaster wras;

    public WrapperWRI(WritableRaster wras) {
        super(wras);
        this.wras = wras;
    }

    public void addTileObserver(TileObserver to) {
        throw new RuntimeException(JaiI18N.getString("WrapperWRI0"));
    }

    public void removeTileObserver(TileObserver to) {
        throw new RuntimeException(JaiI18N.getString("WrapperWRI0"));
    }

    public WritableRaster getWritableTile(int tileX, int tileY) {
        if ((tileX != 0) || (tileY != 0)) {
            throw new IllegalArgumentException();
        }
        return wras;
    }

    public void releaseWritableTile(int tileX, int tileY) {
        if ((tileX != 0) || (tileY != 0)) {
            throw new IllegalArgumentException();
        }
    }

    public boolean isTileWritable(int tileX, int tileY) {
        return true;
    }

    public Point[] getWritableTileIndices() {
        Point[] p = new Point[1];
        p[0] = new Point(0, 0);
        return p;
    }

    public boolean hasTileWriters() {
        return true;
    }

    public void setData(Raster r) {
        throw new RuntimeException(JaiI18N.getString("WrapperWRI0"));
    }
}
