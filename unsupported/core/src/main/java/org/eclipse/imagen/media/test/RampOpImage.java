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
package org.eclipse.imagen.media.test;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Map;
import org.eclipse.imagen.ImageLayout;
import org.eclipse.imagen.RasterFactory;
import org.eclipse.imagen.SourcelessOpImage;

/** Defines a ramp image for testing purpose. */
final class RampOpImage extends SourcelessOpImage {

    public RampOpImage(
            int minX, int minY, int width, int height, SampleModel sampleModel, Map configuration, ImageLayout layout) {
        super(layout, configuration, sampleModel, minX, minY, width, height);
    }

    public Raster computeTile(int tileX, int tileY) {
        int orgX = tileXToX(tileX);
        int orgY = tileYToY(tileY);

        WritableRaster dst = RasterFactory.createWritableRaster(sampleModel, new Point(orgX, orgY));

        Rectangle rect = new Rectangle(orgX, orgY, sampleModel.getWidth(), sampleModel.getHeight());
        rect = rect.intersection(getBounds());

        int numBands = sampleModel.getNumBands();
        int p[] = new int[numBands];

        for (int y = rect.y; y < (rect.y + rect.height); y++) {
            for (int x = rect.x; x < (rect.x + rect.width); x++) {
                int value = Math.max(x & 0xFF, y & 0xFF);
                for (int i = 0; i < numBands; i++) {
                    p[i] = value;
                }
                dst.setPixel(x, y, p);
            }
        }
        return dst;
    }
}
