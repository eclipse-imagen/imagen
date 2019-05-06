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
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import org.eclipse.imagen.BorderExtender;
import org.eclipse.imagen.BorderExtenderConstant;
import org.eclipse.imagen.ImageLayout;
import org.eclipse.imagen.IntegerSequence;
import org.eclipse.imagen.OpImage;
import org.eclipse.imagen.PlanarImage;
import org.eclipse.imagen.RasterFactory;
import org.eclipse.imagen.RasterFormatTag;
import org.eclipse.imagen.RasterAccessor;
import org.eclipse.imagen.RasterFormatTag;
import java.util.Map;
import org.eclipse.imagen.iterator.RandomIter;
import org.eclipse.imagen.iterator.RandomIterFactory;
import org.eclipse.imagen.operator.BorderDescriptor;
import org.eclipse.imagen.media.util.ImageUtil;

/**
 * An <code>OpImage</code> implementing the "border" operation.
 *
 * <p>It adds a border around a source image. The size of the border
 * is specified by the left, right, top, and bottom padding parameters.
 * The border may be filled in a variety of ways specified by the
 * border type parameter, defined in
 * <code>org.eclipse.imagen.operator.BorderDescriptor</code>:
 * <ul>
 * <li> it may be extended with zeros (BORDER_ZERO_FILL);
 * <li> it may be extended with a constant set of values (BORDER_CONST_FILL);
 * <li) it may be created by copying the edge and corner pixels
 *      (BORDER_EXTEND);
 * <li> it may be created by reflection about the edges of the image
 *      (BORDER_REFLECT); or,
 * <li> it may be extended by "wrapping" the image plane toroidally,
 *      that is, joining opposite edges of the image.
 * </ul>
 *
 * <p>When choosing the <code>BORDER_CONST_FILL</code> option, an array
 * of constants must be supplied. The array must have at least one element,
 * in which case this same constant is applied to all image bands. Or,
 * it may have a different constant entry for each cooresponding band.
 * For all other border types, this <code>constants</code> parameter may
 * be <code>null</code>.
 *
 * <p>The layout information for this image may be specified via the
 * <code>layout</code> parameter. However, due to the nature of this
 * operation, the <code>minX</code>, <code>minY</code>, <code>width</code>,
 * and <code>height</code>, if specified, will be ignored. They will
 * be calculated based on the source's dimensions and the padding values.
 * Likewise, the <code>SampleModel</code> and </code>ColorModel</code> hints
 * will be ignored.
 *
 * @see org.eclipse.imagen.OpImage
 * @see org.eclipse.imagen.operator.BorderDescriptor
 * @see BorderRIF
 *
 */
final class BorderOpImage extends OpImage {
    /**
     * The <code>BorderExtender</code> object used to extend the source data.
     */
    protected BorderExtender extender;

    /**
     * Constructor.
     *
     * @param source     The source image.
     * @param layout     The destination image layout.
     * @param leftPad    The amount of padding to the left of the source.
     * @param rightPad   The amount of padding to the right of the source.
     * @param topPad     The amount of padding to the top of the source.
     * @param bottomPad  The amount of padding to the bottom of the source.
     * @param type       The border type.
     * @param constants  The constants used with border type
     *                   <code>BorderDescriptor.BORDER_CONST_FILL</code>,
     *                   stored as reference.
     */
    public BorderOpImage(RenderedImage source,
                         Map config,
                         ImageLayout layout,
                         int leftPad,
                         int rightPad,
                         int topPad,
                         int bottomPad,
                         BorderExtender extender) {
        super(vectorize(source),
              layoutHelper(layout, source,
                           leftPad, rightPad, topPad, bottomPad),
              config, true);

        this.extender = extender;
    }

    /**
     * Sets up the image layout information for this Op.
     * The minX, minY, width, and height are calculated based on
     * the source's dimension and padding values. Any of these
     * values specified in the layout parameter is ignored.
     * All other variables are taken from the layout parameter or
     * inherited from the source.
     */
    private static ImageLayout layoutHelper(ImageLayout layout,
                                            RenderedImage source,
                                            int leftPad,
                                            int rightPad,
                                            int topPad,
                                            int bottomPad) {
        ImageLayout il = layout == null ?
            new ImageLayout() : (ImageLayout)layout.clone();

        // Set the image bounds according to the padding.
        il.setMinX(source.getMinX() - leftPad);
        il.setMinY(source.getMinY() - topPad);
        il.setWidth(source.getWidth() + leftPad + rightPad);
        il.setHeight(source.getHeight() + topPad + bottomPad);

        // Set tile grid offset to minimize the probability that a
        // tile's bounds does not intersect the source image bounds.
        if(!il.isValid(ImageLayout.TILE_GRID_X_OFFSET_MASK)) {
            il.setTileGridXOffset(il.getMinX(null));
        }

        if (!il.isValid(ImageLayout.TILE_GRID_Y_OFFSET_MASK)) {
            il.setTileGridYOffset(il.getMinY(null));
        }

        // Force inheritance of source image SampleModel and ColorModel.
        il.setSampleModel(source.getSampleModel());
        il.setColorModel(source.getColorModel());

        return il;
    }

    /**
     * Returns a conservative estimate of the destination region that
     * can potentially be affected by the pixels of a rectangle of a
     * given source.
     *
     * @param sourceRect the Rectangle in source coordinates.
     * @param sourceIndex the index of the source image.
     * @return a Rectangle indicating the potentially affected
     *         destination region.  or null if the region is unknown.
     * @throws IllegalArgumentException if the source index is
     *         negative or greater than that of the last source.
     * @throws IllegalArgumentException if sourceRect is null.
     */
    public Rectangle mapSourceRect(Rectangle sourceRect,
                                   int sourceIndex) {

        if ( sourceRect == null ) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }

        if (sourceIndex < 0 || sourceIndex >= getNumSources()) {
            throw new IllegalArgumentException(JaiI18N.getString("BorderOpImage0"));
        }

        return new Rectangle(sourceRect);
    }

    /**
     * Returns a conservative estimate of the region of a specified
     * source that is required in order to compute the pixels of a
     * given destination rectangle.
     *
     * @param destRect the Rectangle in destination coordinates.
     * @param sourceIndex the index of the source image.
     * @return a Rectangle indicating the required source region.
     * @throws IllegalArgumentException if the source index is
     *         negative or greater than that of the last source.
     * @throws IllegalArgumentException if destRect is null.
     */
    public Rectangle mapDestRect(Rectangle destRect,
                                 int sourceIndex) {

        if ( destRect == null ) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }

        if (sourceIndex < 0 || sourceIndex >= getNumSources()) {
            throw new IllegalArgumentException(JaiI18N.getString("BorderOpImage2"));
        }

        Rectangle srcBounds = getSourceImage(0).getBounds();
        return destRect.intersection(srcBounds);
    }

    /** Computes the pixel values for the specified tile. */
    public Raster computeTile(int tileX, int tileY) {
        // Create a new Raster.
        WritableRaster dest = createTile(tileX, tileY);

        // Extend the data.
        getSourceImage(0).copyExtendedData(dest, extender);

        return dest;
    }
}
