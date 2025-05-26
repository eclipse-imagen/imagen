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

package org.eclipse.imagen.media.opimage;

import java.awt.image.ColorModel;
import java.awt.image.Raster;
import org.eclipse.imagen.ImageLayout;
import org.eclipse.imagen.SourcelessOpImage;

/**
 * An OpImage class to generate a repeating pattern of pixels.
 *
 * <p>PatternOpImage defines an image consisting of a repeated pattern. The pattern is stored internally as a Raster,
 * and translated versions of the master tile (sharing the same DataBuffer) are returned by computeTile().
 */
// public since ../test/OpImageTester.java uses it
public class PatternOpImage extends SourcelessOpImage {

    /** The master tile (0, 0) containing the pattern. */
    protected Raster pattern;

    /** Set up image layout. */
    private static ImageLayout layoutHelper(Raster pattern, ColorModel colorModel) {
        return new ImageLayout(
                pattern.getMinX(), pattern.getMinY(),
                pattern.getWidth(), pattern.getHeight(),
                pattern.getSampleModel(), colorModel);
    }

    /**
     * Constructs a PatternOpImage from a Raster.
     *
     * @param pattern The Raster pattern to be repeated.
     * @param colorModel The output image ColorModel.
     * @param width The output image width.
     * @param height The output image height.
     */
    public PatternOpImage(Raster pattern, ColorModel colorModel, int minX, int minY, int width, int height) {
        super(layoutHelper(pattern, colorModel), null, pattern.getSampleModel(), minX, minY, width, height);

        this.pattern = pattern;
    }

    public Raster getTile(int tileX, int tileY) {
        return computeTile(tileX, tileY);
    }

    /**
     * Returns a suitably translated version of the pattern tile for reading.
     *
     * @param tileX the X index of the tile
     * @param tileY the Y index of the tile
     */
    public Raster computeTile(int tileX, int tileY) {
        return pattern.createChild(
                tileGridXOffset, tileGridYOffset, tileWidth, tileHeight, tileXToX(tileX), tileYToY(tileY), null);
    }
}
