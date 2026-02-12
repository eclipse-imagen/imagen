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
package org.eclipse.imagen.media.util;

import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageProducer;
import java.awt.image.Raster;
import java.util.Vector;
import org.eclipse.imagen.PlanarImage;

public class PlanarImageProducer implements ImageProducer {

    PlanarImage im;
    Vector consumers = new Vector();

    public PlanarImageProducer(PlanarImage im) {
        this.im = im.createSnapshot();
    }

    public void addConsumer(ImageConsumer ic) {
        if (!consumers.contains(ic)) {
            consumers.add(ic);
        }
        produceImage();
    }

    public boolean isConsumer(ImageConsumer ic) {
        return consumers.contains(ic);
    }

    public void removeConsumer(ImageConsumer ic) {
        consumers.remove(ic);
    }

    public void requestTopDownLeftRightResend(ImageConsumer ic) {
        startProduction(ic);
    }

    public void startProduction(ImageConsumer ic) {
        if (!consumers.contains(ic)) {
            consumers.add(ic);
        }
        produceImage();
    }

    private synchronized void produceImage() {
        int numConsumers = consumers.size();

        int minX = im.getMinX();
        int minY = im.getMinY();
        int width = im.getWidth();
        int height = im.getHeight();
        int numBands = im.getSampleModel().getNumBands();
        int scansize = width * numBands;
        ColorModel colorModel = im.getColorModel();
        int[] pixels = new int[scansize];

        Rectangle rect = new Rectangle(minX, minY, width, 1);

        for (int i = 0; i < numConsumers; i++) {
            ImageConsumer ic = (ImageConsumer) consumers.elementAt(i);
            ic.setHints(ImageConsumer.COMPLETESCANLINES | ImageConsumer.TOPDOWNLEFTRIGHT | ImageConsumer.SINGLEFRAME);
        }

        for (int y = minY; y < minY + height; y++) {
            rect.y = y;
            Raster row = im.getData(rect);
            row.getPixels(minX, y, width, 1, pixels);

            for (int i = 0; i < numConsumers; i++) {
                ImageConsumer ic = (ImageConsumer) consumers.elementAt(i);
                ic.setPixels(0, y - minY, width, 1, colorModel, pixels, 0, scansize);
            }
        }

        for (int i = 0; i < numConsumers; i++) {
            ImageConsumer ic = (ImageConsumer) consumers.elementAt(i);
            ic.imageComplete(ImageConsumer.STATICIMAGEDONE);
        }
    }
}
