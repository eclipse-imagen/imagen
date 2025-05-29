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

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import org.eclipse.imagen.CRIFImpl;
import org.eclipse.imagen.Histogram;
import org.eclipse.imagen.ImageLayout;

/**
 * A <code>CRIF</code> supporting the "MatchCDF" operation in the rendered and renderable image layers.
 *
 * @see org.eclipse.imagen.operator.MatchCDFDescriptor
 * @see org.eclipse.imagen.operator.PiecewiseDescriptor
 * @see PiecewiseOpImage
 * @since EA4
 */
public class MatchCDFCRIF extends CRIFImpl {
    /*
     * Creates a piecewise mapping from an image with cumulative distribution
     * function of pixel values CDFin to another given by CDFout.
     */
    private static void createHistogramMap(
            float[] CDFin,
            float[] CDFout,
            double lowValue,
            double binWidth,
            int numBins,
            float[] abscissa,
            float[] ordinate) {
        // Initialize the first abscissa and the output CDF index.
        double x = lowValue;
        int j = 0;
        int jMax = numBins - 1;

        // Generate one breakpoint for each bin.
        for (int i = 0; i < numBins; i++) {
            // Find the first output bin such that the output CDF is
            // greater than or equal to the input CDF at the current bin.
            float w = CDFin[i];
            while (CDFout[j] < w && j < jMax) j++;

            // Set the breakpoint values.
            abscissa[i] = (float) x;
            ordinate[i] = (float) (j * binWidth);

            // Increment the abscissa.
            x += binWidth;
        }
    }

    /**
     * Create a set of breakpoints which will map the input histogram to an output histogram with the specified
     * cumulative distribution function.
     *
     * @param histIn The input histogram.
     * @param CDFOut The output CDF.
     * @return A piecewise transform which will modify the input histogram to match the output CDF.
     */
    private static float[][][] createSpecificationMap(Histogram histIn, float[][] CDFOut) {
        // Allocate the overall breakpoint array.
        int numBands = histIn.getNumBands();
        float[][][] bp = new float[numBands][][];

        // Calculate a different set of breakpoints for each band.
        float[] CDFin = null;
        for (int band = 0; band < numBands; band++) {
            // Allocate memory for the breakpoints for this band.
            int numBins = histIn.getNumBins(band);
            bp[band] = new float[2][];
            bp[band][0] = new float[numBins];
            bp[band][1] = new float[numBins];

            // Calculate the total count over all bins of this band.
            int[] binsIn = histIn.getBins(band);
            long binTotalIn = binsIn[0];
            for (int i = 1; i < numBins; i++) {
                binTotalIn += binsIn[i];
            }

            // Allocate memory for the CDF for this band only if needed.
            if (CDFin == null || CDFin.length < numBins) {
                CDFin = new float[numBins];
            }

            // Calculate the Cumulative Distribution Function (CDF) for the
            // input histogram for this band.
            CDFin[0] = (float) binsIn[0] / binTotalIn;
            for (int i = 1; i < numBins; i++) {
                CDFin[i] = CDFin[i - 1] + (float) binsIn[i] / binTotalIn;
            }

            // Calculate the mapping function.
            double binWidth = (histIn.getHighValue(band) - histIn.getLowValue(band)) / numBins;
            createHistogramMap(
                    CDFin,
                    CDFOut.length > 1 ? CDFOut[band] : CDFOut[0],
                    histIn.getLowValue(band),
                    binWidth,
                    numBins,
                    bp[band][0],
                    bp[band][1]);
        }

        return bp;
    }

    /** Constructor. */
    public MatchCDFCRIF() {
        super("matchcdf");
    }

    /**
     * Creates a new instance of <code>PiecewiseOpImage</code> in the rendered layer.
     *
     * @param args The source image and the breakpoints.
     * @param hints Optionally contains destination image layout.
     */
    public RenderedImage create(ParameterBlock args, RenderingHints renderHints) {
        // Get ImageLayout from renderHints if any.
        ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);

        // Derive breakpoints from the histogram and specified CDF.
        RenderedImage src = args.getRenderedSource(0);
        Histogram hist = (Histogram) src.getProperty("histogram");
        float[][] CDF = (float[][]) args.getObjectParameter(0);
        float[][][] bp = createSpecificationMap(hist, CDF);

        return new PiecewiseOpImage(src, renderHints, layout, bp);
    }
}
