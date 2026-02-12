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

import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Vector;

/** @since EA2 */
public class WrapperRI implements RenderedImage {

    Raster ras;

    public WrapperRI(Raster ras) {
        this.ras = ras;
    }

    public Vector getSources() {
        return null;
    }

    public Object getProperty(String name) {
        return null;
    }

    public String[] getPropertyNames() {
        return null;
    }

    public ColorModel getColorModel() {
        return null;
    }

    public SampleModel getSampleModel() {
        return ras.getSampleModel();
    }

    public int getWidth() {
        return ras.getWidth();
    }

    public int getHeight() {
        return ras.getHeight();
    }

    public int getMinX() {
        return ras.getMinX();
    }

    public int getMinY() {
        return ras.getMinY();
    }

    public int getNumXTiles() {
        return 1;
    }

    public int getNumYTiles() {
        return 1;
    }

    public int getMinTileX() {
        return 0;
    }

    public int getMinTileY() {
        return 0;
    }

    public int getTileWidth() {
        return ras.getWidth();
    }

    public int getTileHeight() {
        return ras.getHeight();
    }

    public int getTileGridXOffset() {
        return ras.getMinX();
    }

    public int getTileGridYOffset() {
        return ras.getMinY();
    }

    public Raster getTile(int tileX, int tileY) {
        return ras;
    }

    public Raster getData() {
        throw new RuntimeException(JaiI18N.getString("WrapperRI0"));
    }

    public Raster getData(Rectangle rect) {
        throw new RuntimeException(JaiI18N.getString("WrapperRI0"));
    }

    public WritableRaster copyData(WritableRaster raster) {
        throw new RuntimeException(JaiI18N.getString("WrapperRI0"));
    }
}
