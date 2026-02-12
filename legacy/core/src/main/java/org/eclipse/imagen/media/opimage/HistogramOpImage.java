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
package org.eclipse.imagen.media.opimage;

import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import org.eclipse.imagen.Histogram;
import org.eclipse.imagen.ROI;
import org.eclipse.imagen.StatisticsOpImage;

/**
 * An <code>OpImage</code> implementing the "Histogram" operation as described in <code>
 * org.eclipse.imagen.operator.HistogramDescriptor</code>.
 *
 * @see org.eclipse.imagen.Histogram
 * @see org.eclipse.imagen.operator.HistogramDescriptor
 * @see HistogramCRIF
 */
final class HistogramOpImage extends StatisticsOpImage {

    /** Number of bins per band. */
    private int[] numBins;

    /** The low value checked inclusive for each band. */
    private double[] lowValue;

    /** The high value checked exclusive for each band. */
    private double[] highValue;

    /** The number of bands of the source image. */
    private int numBands;

    private final boolean tileIntersectsROI(int tileX, int tileY) {
        if (roi == null) { // ROI is entire tile
            return true;
        } else {
            return roi.intersects(tileXToX(tileX), tileYToY(tileY), tileWidth, tileHeight);
        }
    }

    /**
     * Constructs an <code>HistogramOpImage</code>.
     *
     * @param source The source image.
     */
    public HistogramOpImage(
            RenderedImage source,
            ROI roi,
            int xStart,
            int yStart,
            int xPeriod,
            int yPeriod,
            int[] numBins,
            double[] lowValue,
            double[] highValue) {
        super(source, roi, xStart, yStart, xPeriod, yPeriod);

        numBands = source.getSampleModel().getNumBands();

        this.numBins = new int[numBands];
        this.lowValue = new double[numBands];
        this.highValue = new double[numBands];

        for (int b = 0; b < numBands; b++) {
            this.numBins[b] = numBins.length == 1 ? numBins[0] : numBins[b];
            this.lowValue[b] = lowValue.length == 1 ? lowValue[0] : lowValue[b];
            this.highValue[b] = highValue.length == 1 ? highValue[0] : highValue[b];
        }
    }

    protected String[] getStatisticsNames() {
        String[] names = new String[1];
        names[0] = "histogram";
        return names;
    }

    protected Object createStatistics(String name) {
        if (name.equalsIgnoreCase("histogram")) {
            return new Histogram(numBins, lowValue, highValue);
        } else {
            return java.awt.Image.UndefinedProperty;
        }
    }

    protected void accumulateStatistics(String name, Raster source, Object stats) {
        Histogram histogram = (Histogram) stats;
        histogram.countPixels(source, roi, xStart, yStart, xPeriod, yPeriod);
    }
}
