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

package org.eclipse.imagen.tilecodec;

import org.eclipse.imagen.PropertyGenerator;

/**
 * An abstract class that implements the <code>TileCodecDescriptor</code> interface and is suitable for subclassing.
 * This class provides default implementations for some of the methods from <code>TileCodecDescriptor</code>. Subclasses
 * should override these methods if they do not wish to retain the default implementation.
 *
 * <p>All <code>String</code>s are treated in a case-retentive and case-insensitive manner.
 */
public abstract class TileCodecDescriptorImpl implements TileCodecDescriptor {

    private String formatName;
    private boolean includesSMInfo, includesLocationInfo;

    /**
     * Creates a <code>TileCodecDescriptorImpl</code> with the given format name and <code>boolean</code>s to specify
     * whether layout information is included in the encoded stream.
     *
     * @param formatName The name of the format. This is also the name under which this descriptor will be registered
     *     under in the <code>OperationRegistry</code>.
     * @param includesSampleModelInfo Whether the format encodes the tile's <code>SampleModel</code> or equivalent
     *     information into the encoded stream.
     * @param includesLocationInfo Whether the format encodes the tile's upper left corner position or equivalent
     *     information into the encoded stream.
     * @throws IllegalArgumentException if formatName is null.
     */
    public TileCodecDescriptorImpl(String formatName, boolean includesSampleModelInfo, boolean includesLocationInfo) {

        // Cause IllegalArgumentException if formatName is null
        if (formatName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("TileCodecDescriptorImpl0"));
        }

        this.formatName = formatName;
        this.includesSMInfo = includesSampleModelInfo;
        this.includesLocationInfo = includesLocationInfo;
    }

    /** Returns the name of the format. */
    public String getName() {
        return formatName;
    }

    /**
     * Returns the registry modes supported by this descriptor. The default implementation of this method in this class
     * returns a <code>String</code> array containing the "tileDecoder" and "tileEncoder" strings. If the subclass does
     * not support any of these modes, it should override this method to return the names of those modes that it
     * supports.
     *
     * @see org.eclipse.imagen.RegistryMode
     */
    public String[] getSupportedModes() {
        return new String[] {"tileDecoder", "tileEncoder"};
    }

    /**
     * This method is implemented to return true if the specified registryModeName is either "tileDecoder" or
     * "tileEncoder". If the subclass doesn't support any one of these modes, it should override this method to return
     * true only for the supported mode(s).
     *
     * @param registryModeName The name of the registry mode to check support for.
     * @throws IllegalArgumentException if registryModeName is null.
     */
    public boolean isModeSupported(String registryModeName) {

        if (registryModeName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("TileCodecDescriptorImpl1"));
        }

        if (registryModeName.equalsIgnoreCase("tileDecoder") == true
                || registryModeName.equalsIgnoreCase("tileEncoder") == true) {
            return true;
        }

        return false;
    }

    /**
     * Whether this descriptor supports properties.
     *
     * @return true, if the implementation of this descriptor supports properties. false otherwise. Since tile codecs do
     *     not support properties, so this default implementation returns false.
     * @see PropertyGenerator
     */
    public boolean arePropertiesSupported() {
        return false;
    }

    /**
     * Returns an array of <code>PropertyGenerator</code>s implementing the property inheritance for this operation.
     * Since neither <code>TileEncoder</code> or <code>TileDecoder</code> supports properties, the default
     * implementation throws an <code>UnsupportedOperationException</code>. Subclasses should override this method if
     * they wish to produce inherited properties.
     *
     * @throws IllegalArgumentException if <code>modeName</code> is null.
     * @throws UnsupportedOperationException if <code>arePropertiesSupported()</code> returns <code>false</code>
     */
    public PropertyGenerator[] getPropertyGenerators(String modeName) {

        if (modeName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("TileCodecDescriptorImpl1"));
        }

        throw new UnsupportedOperationException(JaiI18N.getString("TileCodecDescriptorImpl2"));
    }

    /**
     * Returns true if the format encodes layout information generally specified via the <code>SampleModel</code> in the
     * encoded data stream.
     */
    public boolean includesSampleModelInfo() {
        return includesSMInfo;
    }

    /**
     * Returns true if the format encodes in the data stream the location of the <code>Raster</code> with respect to its
     * enclosing image.
     */
    public boolean includesLocationInfo() {
        return includesLocationInfo;
    }
}
