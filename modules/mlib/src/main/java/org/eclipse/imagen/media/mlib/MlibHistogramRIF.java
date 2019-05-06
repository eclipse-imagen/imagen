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

package org.eclipse.imagen.media.mlib;

import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import org.eclipse.imagen.JAI;
import org.eclipse.imagen.ROI;
import org.eclipse.imagen.util.ImagingListener;
import org.eclipse.imagen.media.util.ImageUtil;
import org.eclipse.imagen.media.opimage.RIFUtil;

/**
 * A <code>RIF</code> supporting the "Histogram" operation in the
 * rendered image layer.
 *
 * @since EA2
 * @see org.eclipse.imagen.operator.HistogramDescriptor
 * @see MlibHistogramOpImage
 */
public class MlibHistogramRIF implements RenderedImageFactory {

    /** Constructor. */
    public MlibHistogramRIF() {}

    /**
     * Creates a new instance of <code>MlibHistogramOpImage</code>
     * in the rendered layer. Any image layout information in
     * <code>RenderingHints</code> is ignored.
     * This method satisfies the implementation of RIF.
     */
    public RenderedImage create(ParameterBlock args,
                                RenderingHints hints) {

        // Return null of not mediaLib-compatible.
        if(!MediaLibAccessor.isMediaLibCompatible(args)) {
            return null;
        }

        // Return null if source data type is floating point.
        RenderedImage src = args.getRenderedSource(0);
        int dataType = src.getSampleModel().getDataType();
        if(dataType == DataBuffer.TYPE_FLOAT ||
           dataType == DataBuffer.TYPE_DOUBLE) {
            return null;
        }

        // Return null if ROI is non-null and not equals to source bounds.
        ROI roi = (ROI)args.getObjectParameter(0);
        if(roi != null &&
           !roi.equals(new Rectangle(src.getMinX(), src.getMinY(),
                                     src.getWidth(), src.getHeight()))) {
            return null;
        }

        // Get the non-ROI parameters.
        int xPeriod = args.getIntParameter(1);
        int yPeriod = args.getIntParameter(2);
        int[] numBins = (int[])args.getObjectParameter(3);
        double[] lowValueFP = (double[])args.getObjectParameter(4);
        double[] highValueFP = (double[])args.getObjectParameter(5);

        // Return null if lowValueFP or highValueFP is out of dataType range.
        int minPixelValue;
        int maxPixelValue;
        switch (dataType) {
        case DataBuffer.TYPE_SHORT:
            minPixelValue = Short.MIN_VALUE;
            maxPixelValue = Short.MAX_VALUE;
            break;
        case DataBuffer.TYPE_USHORT:
            minPixelValue = 0;
            maxPixelValue = -((int)Short.MIN_VALUE) + Short.MAX_VALUE;
            break;
        case DataBuffer.TYPE_INT:
            minPixelValue = Integer.MIN_VALUE;
            maxPixelValue = Integer.MAX_VALUE;
            break;
        case DataBuffer.TYPE_BYTE:
        default:
            minPixelValue = 0;
            maxPixelValue = -((int)Byte.MIN_VALUE) + Byte.MAX_VALUE;
            break;
        }
        for (int i = 0; i < lowValueFP.length; i++) {
            if (lowValueFP[i] < minPixelValue ||
                lowValueFP[i] > maxPixelValue) {
                return null;
            }
        }
        for (int i = 0; i < highValueFP.length; i++) {
            if (highValueFP[i] <= minPixelValue ||
                highValueFP[i] > (maxPixelValue + 1)) {
                return null;
            }
        }

        MlibHistogramOpImage op = null;
        try {
            op = new MlibHistogramOpImage(src,
                                          xPeriod, yPeriod,
                                          numBins, lowValueFP, highValueFP);
        } catch (Exception e) {
            ImagingListener listener = ImageUtil.getImagingListener(hints);
            String message = JaiI18N.getString("MlibHistogramRIF0");
            listener.errorOccurred(message, e, this, false);
        }

        return op;
    }
}
