/* JAI-Ext - OpenSource Java Advanced Image Extensions Library
 *    http://www.geo-solutions.it/
 *    Copyright 2014 GeoSolutions
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.eclipse.imagen.media.convolve;

import java.awt.RenderingHints;
import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import org.eclipse.imagen.BorderExtender;
import org.eclipse.imagen.ImageLayout;
import org.eclipse.imagen.KernelImageN;
import org.eclipse.imagen.ROI;
import org.eclipse.imagen.media.opimage.RIFUtil;
import org.eclipse.imagen.media.range.Range;

public class ConvolveRIF implements RenderedImageFactory {

    public ConvolveRIF() {}

    public RenderedImage create(ParameterBlock pb, RenderingHints hints) {
        // Getting the Layout
        ImageLayout l = RIFUtil.getImageLayoutHint(hints);
        // Get BorderExtender from renderHints if present.
        BorderExtender extender = RIFUtil.getBorderExtenderHint(hints);
        // Getting source
        RenderedImage img = pb.getRenderedSource(0);
        // Getting parameters
        KernelImageN kernel = (KernelImageN) pb.getObjectParameter(0);
        ROI roi = (ROI) pb.getObjectParameter(1);
        Range nodata = (Range) pb.getObjectParameter(2);
        double destinationNoData = pb.getDoubleParameter(3);
        boolean skipNoData = (Boolean) pb.getObjectParameter(4);

        kernel = kernel.getRotatedKernel();

        int dataType = pb.getRenderedSource(0).getSampleModel().getDataType();
        boolean dataTypeOk = (dataType == DataBuffer.TYPE_BYTE
                || dataType == DataBuffer.TYPE_SHORT
                || dataType == DataBuffer.TYPE_INT);
        if (kernel.getWidth() == 3
                && kernel.getHeight() == 3
                && kernel.getXOrigin() == 1
                && kernel.getYOrigin() == 1
                && dataTypeOk) {
            return new Convolve3x3OpImage(img, extender, hints, l, kernel, roi, nodata, destinationNoData, skipNoData);
        }

        return new ConvolveGeneralOpImage(img, extender, hints, l, kernel, roi, nodata, destinationNoData, skipNoData);
        // if (kernel.getWidth() == 3 && kernel.getHeight() == 3 &&
        // kernel.getXOrigin() == 1 && kernel.getYOrigin() == 1 &&
        // dataTypeOk) {
        // return new Convolve3x3OpImage(img,
        // extender,
        // hints,
        // l,
        // kernel);
        // } else if (kernel.isSeparable()) {
        // return new SeparableConvolveOpImage(img,
        // extender,
        // hints,
        // l,
        // kernel);
        //
        // } else {
        // return new ConvolveOpImage(img,
        // extender,
        // hints,
        // l,
        // kernel);
        // }
    }
}
