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
import java.awt.image.renderable.RenderedImageFactory;
import org.eclipse.imagen.ROI;
import org.eclipse.imagen.media.util.ImageUtil;
import org.eclipse.imagen.util.ImagingListener;

/**
 * A <code>RIF</code> supporting the "Histogram" operation in the rendered image layer.
 *
 * @since EA2
 * @see org.eclipse.imagen.operator.HistogramDescriptor
 * @see HistogramOpImage
 */
public class HistogramRIF implements RenderedImageFactory {

    /** Constructor. */
    public HistogramRIF() {}

    /**
     * Creates a new instance of <code>HistogramOpImage</code> in the rendered layer. Any image layout information in
     * <code>RenderingHints</code> is ignored. This method satisfies the implementation of RIF.
     */
    public RenderedImage create(ParameterBlock args, RenderingHints hints) {
        RenderedImage src = args.getRenderedSource(0);

        int xStart = src.getMinX(); // default values
        int yStart = src.getMinY();

        int maxWidth = src.getWidth();
        int maxHeight = src.getHeight();

        ROI roi = (ROI) args.getObjectParameter(0);
        int xPeriod = args.getIntParameter(1);
        int yPeriod = args.getIntParameter(2);
        int[] numBins = (int[]) args.getObjectParameter(3);
        double[] lowValue = (double[]) args.getObjectParameter(4);
        double[] highValue = (double[]) args.getObjectParameter(5);

        HistogramOpImage op = null;
        try {
            op = new HistogramOpImage(src, roi, xStart, yStart, xPeriod, yPeriod, numBins, lowValue, highValue);
        } catch (Exception e) {
            ImagingListener listener = ImageUtil.getImagingListener(hints);
            String message = JaiI18N.getString("HistogramRIF0");
            listener.errorOccurred(message, e, this, false);
        }

        return op;
    }
}
