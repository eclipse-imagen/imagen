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

package org.eclipse.imagen;

import java.awt.RenderingHints;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.util.Hashtable;
import java.util.Map;
import org.eclipse.imagen.media.util.JDKWorkarounds;

/**
 * A trivial <code>OpImage</code> subclass that simply transmits its source unchanged. This may be useful when an
 * interface requires an <code>OpImage</code> but another sort of <code>RenderedImage</code> (such as a <code>
 * BufferedImage</code> or <code>TiledImage</code>) is available. Additionally, <code>NullOpImage</code> is able to make
 * use of ImageN's tile caching mechanisms.
 *
 * <p>Methods that get or set properties are implemented to forward the requests to the source image; no independent
 * property information is stored in the <code>NullOpImage</code> itself.
 *
 * @see PointOpImage
 */
public class NullOpImage extends PointOpImage {

    protected int computeType;

    /** Create a new ImageLayout from the source image optionally overriding a ColorModel supplied via the layout. */
    private static ImageLayout layoutHelper(RenderedImage source, ImageLayout layout) {
        // Create basic layout from the source.
        ImageLayout il = new ImageLayout(source);

        // If a layout containing a valid ColorModel field is supplied then
        // reset the ColorModel if it is compatible with the SampleModel.
        if (layout != null && layout.isValid(ImageLayout.COLOR_MODEL_MASK)) {
            ColorModel colorModel = layout.getColorModel(null);
            if (JDKWorkarounds.areCompatibleDataModels(source.getSampleModel(), colorModel)) {
                il.setColorModel(colorModel);
            }
        }

        return il;
    }

    /**
     * Constructs a <code>NullOpImage</code>. The superclass constructor will be passed a new <code>ImageLayout</code>
     * object with all of its fields filled in. The <code>ColorModel</code> may be overridden via the supplied <code>
     * ImageLayout</code>; all other layout fields are derived from the source image. Any specified <code>ColorModel
     * </code> will be used if and only if it is compatible with the source image <code>SampleModel</code>.
     *
     * @param layout An <code>ImageLayout</code> optionally specifying the image <code>ColorModel</code>; all other
     *     fields are ignored. This parameter may be <code>null</code>.
     * @param source A <code>RenderedImage</code>; must not be <code>null</code> or a <code>IllegalArgumentException
     *     </code> will be thrown.
     * @param configuration Configurable attributes of the image including configuration variables indexed by <code>
     *     RenderingHints.Key</code>s and image properties indexed by <code>String</code>s or <code>CaselessStringKey
     *     </code>s. This is simply forwarded to the superclass constructor.
     * @param computeType A tag indicating whether the source is <code>OpImage.OP_COMPUTE_BOUND</code>, <code>
     *     OpImage.OP_IO_BOUND</code> or <code>OpImage.OP_NETWORK_BOUND</code>. This information is used as a hint to
     *     optimize <code>OpImage</code> computation.
     * @throws <code>IllegalArgumentException</code> if <code>source</code> is <code>null</code>.
     * @throws <code>IllegalArgumentException</code> if <code>computeType</code> is not one of the known <code>
     *     OP_*_BOUND</code> values.
     */
    public NullOpImage(RenderedImage source, ImageLayout layout, Map configuration, int computeType) {
        // cobbleSources is irrelevant since we override getTile().
        super(
                PlanarImage.wrapRenderedImage(source).createSnapshot(),
                layoutHelper(source, layout),
                configuration,
                false);

        if (computeType != OP_COMPUTE_BOUND && computeType != OP_IO_BOUND && computeType != OP_NETWORK_BOUND) {
            throw new IllegalArgumentException(ImageNLegacy18N.getString("NullOpImage0"));
        }

        this.computeType = computeType;
    }

    /**
     * Constructs a <code>NullOpImage</code>. The superclass constructor will be passed a new <code>ImageLayout</code>
     * object with all of its fields filled in. The <code>ColorModel</code> may be overridden via the supplied <code>
     * ImageLayout</code>; all other layout fields are derived from the source image. Any specified <code>ColorModel
     * </code> will be used if and only if it is compatible with the source image <code>SampleModel</code>.
     *
     * @param source A <code>RenderedImage</code>; must not be <code>null</code> or a <code>IllegalArgumentException
     *     </code> will be thrown.
     * @param cache a TileCache object to store tiles from this OpImage, or null. If null, a default cache will be used.
     * @param computeType A tag indicating whether the source is <code>OpImage.OP_COMPUTE_BOUND</code>, <code>
     *     OpImage.OP_IO_BOUND</code> or <code>OpImage.OP_NETWORK_BOUND</code>. This information is used as a hint to
     *     optimize <code>OpImage</code> computation.
     * @param layout An <code>ImageLayout</code> optionally specifying the image <code>ColorModel</code>; all other
     *     fields are ignored. This parameter may be <code>null</code>.
     * @throws IllegalArgumentException if <code>source</code> is <code>null</code>.
     * @throws IllegalArgumentException if <code>computeType</code> is not one of the known <code>OP_*_BOUND</code>
     *     values.
     * @deprecated as of ImageN 0.4.0.
     */
    public NullOpImage(RenderedImage source, TileCache cache, int computeType, ImageLayout layout) {
        this(source, layout, cache != null ? new RenderingHints(ImageN.KEY_TILE_CACHE, cache) : null, computeType);
    }

    /**
     * Returns a tile for reading.
     *
     * @param tileX The X index of the tile.
     * @param tileY The Y index of the tile.
     * @return The tile as a <code>Raster</code>.
     */
    public Raster computeTile(int tileX, int tileY) {
        return getSource(0).getTile(tileX, tileY);
    }

    /** Returns false as NullOpImage can return via computeTile() tiles that are internally cached. */
    public boolean computesUniqueTiles() {
        return false;
    }

    /** Returns the properties from the source image. */
    protected synchronized Hashtable getProperties() {
        return getSource(0).getProperties();
    }

    /** Set the properties <code>Hashtable</code> of the source image to the supplied <code>Hashtable</code>. */
    protected synchronized void setProperties(Hashtable properties) {
        getSource(0).setProperties(properties);
    }

    /** Returns the property names from the source image or <code>null</code> if no property names are recognized. */
    public String[] getPropertyNames() {
        return getSource(0).getPropertyNames();
    }

    /**
     * Returns the property names with the supplied prefix from the source image or <code>null</code> if no property
     * names are recognized.
     */
    public String[] getPropertyNames(String prefix) {
        return getSource(0).getPropertyNames(prefix);
    }

    /** Returns the class of the specified property from the source image. */
    public Class getPropertyClass(String name) {
        return getSource(0).getPropertyClass(name);
    }

    /**
     * Retrieves a property from the source image by name or <code>java.awt.Image.UndefinedProperty</code> if the
     * property with the specified name is not defined.
     */
    public Object getProperty(String name) {
        return getSource(0).getProperty(name);
    }

    /** Sets a property on the source image by name. */
    public void setProperty(String name, Object value) {
        getSource(0).setProperty(name, value);
    }

    /** Removes a property from the source image by name. */
    public void removeProperty(String name) {
        getSource(0).removeProperty(name);
    }

    /**
     * Returns one of OP_COMPUTE_BOUND, OP_IO_BOUND, or OP_NETWORK_BOUND to indicate how the operation is likely to
     * spend its time. The answer does not affect the output of the operation, but may allow a scheduler to parallelize
     * the computation of multiple operations more effectively. The default implementation returns OP_COMPUTE_BOUND.
     */
    public int getOperationComputeType() {
        return computeType;
    }
}
