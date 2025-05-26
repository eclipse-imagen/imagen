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

import java.awt.image.SampleModel;
import org.eclipse.imagen.ParameterListDescriptor;
import org.eclipse.imagen.ParameterListDescriptorImpl;

/**
 * This class is the descriptor for the "GZIP" tile codec. This codec scheme uses "gzip" as the method of compressing
 * tile data. This is a lossless tile codec. The format name for the gzip tile codec is "gzip". The encoded stream
 * contains the <code>SampleModel</code> and the tile's upper left corner position, thus the <code>
 * includesSampleModelInfo()</code> and <code>includesLocationInfo()</code> methods in this descriptor return true.
 *
 * <p>The "gzip" codec scheme does not support any parameters.
 *
 * <p>
 *
 * <table border=1>
 * <caption>Resource List</caption>
 * <tr><th>Name</th>        <th>Value</th></tr>
 * <tr><td>Vendor</td>      <td>org.eclipse.imagen.media</td></tr>
 * <tr><td>Description</td> <td>A descriptor to describe the lossless "gzip"
 *                              codec scheme. </td></tr>
 * <tr><td>DocURL</td>      <td>http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/tilecodec/GZIPTileCodecDescriptor.html</td></tr>
 * <tr><td>Version</td>     <td>1.2</td></tr>
 * </table>
 *
 * <p>
 *
 * <table border=1>
 * <caption>Parameter List</caption>
 * <tr><th>Name</th>          <th>Class Type</th>
 *                            <th>Default Value</th></tr>
 * </table>
 *
 * @since JAI 1.1
 */
public class GZIPTileCodecDescriptor extends TileCodecDescriptorImpl {

    private static ParameterListDescriptorImpl pld = new ParameterListDescriptorImpl();

    /** Creates a <code>GZIPTileCodecDescriptor</code> */
    public GZIPTileCodecDescriptor() {
        super("gzip", true, true);
    }

    /**
     * Returns a <code>TileCodecParameterList</code> valid for the specified modeName and compatible with the supplied
     * <code>TileCodecParameterList</code>. For example, given a <code>TileCodecParameterList</code> used to encode a
     * tile with the modeName being specified as "tileDecoder", this method will return a <code>TileCodecParameterList
     * </code> sufficient to decode that same tile. For the gzip tile codec, no parameters are used. So null will be
     * returned for any valid modeName specified.
     *
     * @param modeName The registry mode to return a valid parameter list for.
     * @param otherParamList The parameter list for which a compatible parameter list for the complementary modeName is
     *     to be found.
     * @throws IllegalArgumentException if <code>modeName</code> is null.
     * @throws IllegalArgumentException if <code>modeName</code> is not one of the modes valid for this descriptor, i.e
     *     those returned from the getSupportedNames() method.
     */
    public TileCodecParameterList getCompatibleParameters(String modeName, TileCodecParameterList otherParamList) {
        if (modeName == null) throw new IllegalArgumentException(JaiI18N.getString("TileCodecDescriptorImpl1"));

        String validNames[] = getSupportedModes();
        boolean valid = false;

        for (int i = 0; i < validNames.length; i++) {
            if (modeName.equalsIgnoreCase(validNames[i])) {
                valid = true;
                break;
            }
        }

        if (valid == false) {
            throw new IllegalArgumentException(JaiI18N.getString("TileCodec1"));
        }

        return null;
    }

    /**
     * Returns the default parameters for the specified modeName as an instance of the <code>TileCodecParameterList
     * </code>. For the gzip tile codec, no parameters are used. So null will be returned for any valid modeName
     * specified.
     *
     * @param modeName The registry mode to return a valid parameter list for.
     * @throws IllegalArgumentException if <code>modeName</code> is null.
     * @throws IllegalArgumentException if <code>modeName</code> is not one of the modes valid for this descriptor, i.e
     *     those returned from the getSupportedNames() method.
     */
    public TileCodecParameterList getDefaultParameters(String modeName) {
        if (modeName == null) throw new IllegalArgumentException(JaiI18N.getString("TileCodecDescriptorImpl1"));

        String validNames[] = getSupportedModes();
        boolean valid = false;

        for (int i = 0; i < validNames.length; i++) {
            if (modeName.equalsIgnoreCase(validNames[i])) {
                valid = true;
                break;
            }
        }

        if (valid == false) {
            throw new IllegalArgumentException(JaiI18N.getString("TileCodec1"));
        }

        return null;
    }

    /**
     * Returns the default parameters for the specified modeName as an instance of the <code>TileCodecParameterList
     * </code>, adding a "sampleModel" parameter with the specified value to the parameter list. For the gzip tile
     * codec, no parameters are used. So null will be returned for any valid modeName specified.
     *
     * <p>This method should be used when includesSampleModelInfo() returns false. If includesSampleModelInfo() returns
     * true, the supplied <code>SampleModel</code> is ignored.
     *
     * <p>If a parameter named "sampleModel" exists in the default parameter list, the supplied SampleModel will
     * override the value associated with this default parameter.
     *
     * @param modeName The registry mode to return a valid parameter list for.
     * @param sm The <code>SampleModel</code> used to create the default decoding parameter list.
     * @throws IllegalArgumentException if <code>modeName</code> is null.
     * @throws IllegalArgumentException if <code>modeName</code> is not one of the modes valid for this descriptor, i.e
     *     those returned from the getSupportedNames() method.
     */
    public TileCodecParameterList getDefaultParameters(String modeName, SampleModel sm) {
        if (modeName == null) throw new IllegalArgumentException(JaiI18N.getString("TileCodecDescriptorImpl1"));

        String validNames[] = getSupportedModes();
        boolean valid = false;

        for (int i = 0; i < validNames.length; i++) {
            if (modeName.equalsIgnoreCase(validNames[i])) {
                valid = true;
                break;
            }
        }

        if (valid == false) {
            throw new IllegalArgumentException(JaiI18N.getString("TileCodec1"));
        }

        return null;
    }

    /**
     * Returns the <code>ParameterListDescriptor</code> that describes the associated parameters (NOT sources). This
     * method returns null if there are no parameters for the specified modeName. If the specified modeName supports
     * parameters but the implementing class does not have parameters, then this method returns a non-null <code>
     * ParameterListDescriptor</code> whose <code>getNumParameters()</code> returns 0.
     *
     * @param modeName The mode to get the ParameterListDescriptor for.
     * @throws IllegalArgumentException if <code>modeName</code> is null.
     * @throws IllegalArgumentException if <code>modeName</code> is null.
     * @throws IllegalArgumentException if <code>modeName</code> is not one of the modes valid for this descriptor, i.e
     *     those returned from the getSupportedNames() method.
     */
    public ParameterListDescriptor getParameterListDescriptor(String modeName) {

        if (modeName == null) throw new IllegalArgumentException(JaiI18N.getString("TileCodecDescriptorImpl1"));

        String validNames[] = getSupportedModes();
        boolean valid = false;

        for (int i = 0; i < validNames.length; i++) {
            if (modeName.equalsIgnoreCase(validNames[i])) {
                valid = true;
                break;
            }
        }

        if (valid == false) {
            throw new IllegalArgumentException(JaiI18N.getString("TileCodec1"));
        }

        return pld;
    }
}
