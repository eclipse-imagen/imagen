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
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;
import java.util.Map;
import org.eclipse.imagen.CRIFImpl;
import org.eclipse.imagen.ImageLayout;
import org.eclipse.imagen.ImageN;
import org.eclipse.imagen.NullOpImage;
import org.eclipse.imagen.OpImage;
import org.eclipse.imagen.RasterFactory;
import org.eclipse.imagen.media.util.JDKWorkarounds;

/**
 * A <code>CRIF</code> supporting the "Format" operation in the rendered and renderable image layers.
 *
 * @see org.eclipse.imagen.operator.FormatDescriptor
 * @see FormatOpImage
 * @since EA4
 */
public class FormatCRIF extends CRIFImpl {

    /** Constructor. */
    public FormatCRIF() {
        super("format");
    }

    /**
     * Creates a new instance of <code>FormatOpImage</code> in the rendered layer.
     *
     * @param args The source image and data type
     * @param hints Contains destination image layout.
     */
    public RenderedImage create(ParameterBlock args, RenderingHints renderHints) {

        // Get the source image and the data type parameter.
        RenderedImage src = args.getRenderedSource(0);
        Integer datatype = (Integer) args.getObjectParameter(0);
        int type = datatype.intValue();

        // Get ImageLayout from renderHints if any.
        ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);

        // If there is no change return the source image directly.
        if (layout == null && type == src.getSampleModel().getDataType()) {
            return src;
        }

        // Create or clone the ImageLayout.
        if (layout == null) {
            layout = new ImageLayout(src);
        } else {
            layout = (ImageLayout) layout.clone();
        }

        boolean isDataTypeChange = false;

        // Get prospective destination SampleModel.
        SampleModel sampleModel = layout.getSampleModel(src);

        // Create a new SampleModel if the type is not as desired.
        if (sampleModel.getDataType() != type) {
            int tileWidth = layout.getTileWidth(src);
            int tileHeight = layout.getTileHeight(src);
            int numBands = src.getSampleModel().getNumBands();

            SampleModel csm =
                    RasterFactory.createComponentSampleModel(sampleModel, type, tileWidth, tileHeight, numBands);

            layout.setSampleModel(csm);
            isDataTypeChange = true;
        }

        // Check ColorModel.
        ColorModel colorModel = layout.getColorModel(null);
        if (colorModel != null && !JDKWorkarounds.areCompatibleDataModels(layout.getSampleModel(src), colorModel)) {
            // Clear the mask bit if incompatible.
            layout.unsetValid(ImageLayout.COLOR_MODEL_MASK);
        }

        // Check whether anything but the ColorModel is changing.
        if (layout.getSampleModel(src) == src.getSampleModel()
                && layout.getMinX(src) == src.getMinX()
                && layout.getMinY(src) == src.getMinY()
                && layout.getWidth(src) == src.getWidth()
                && layout.getHeight(src) == src.getHeight()
                && layout.getTileWidth(src) == src.getTileWidth()
                && layout.getTileHeight(src) == src.getTileHeight()
                && layout.getTileGridXOffset(src) == src.getTileGridXOffset()
                && layout.getTileGridYOffset(src) == src.getTileGridYOffset()) {

            if (layout.getColorModel(src) == src.getColorModel()) {
                // Nothing changed: return the source directly.
                return src;
            } else {
                // Remove TileCache hint from RenderingHints if present.
                RenderingHints hints = renderHints;
                if (hints != null && hints.containsKey(ImageN.KEY_TILE_CACHE)) {
                    hints = new RenderingHints((Map) renderHints);
                    hints.remove(ImageN.KEY_TILE_CACHE);
                }

                // Only the ColorModel is changing.
                return new NullOpImage(src, layout, hints, OpImage.OP_IO_BOUND);
            }
        }

        if (isDataTypeChange == true) {

            // Add ImageN.KEY_REPLACE_INDEX_COLOR_MODEL hint to renderHints
            if (renderHints == null) {
                renderHints = new RenderingHints(ImageN.KEY_REPLACE_INDEX_COLOR_MODEL, Boolean.TRUE);

            } else if (!renderHints.containsKey(ImageN.KEY_REPLACE_INDEX_COLOR_MODEL)) {
                // If the user specified a value for this hint, we don't
                // want to change that
                renderHints.put(ImageN.KEY_REPLACE_INDEX_COLOR_MODEL, Boolean.TRUE);
            }
        }

        return new CopyOpImage(src, renderHints, layout);
    }
}
