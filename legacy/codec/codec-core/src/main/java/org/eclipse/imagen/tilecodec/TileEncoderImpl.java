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
package org.eclipse.imagen.tilecodec;

import java.io.OutputStream;
import org.eclipse.imagen.ParameterListDescriptor;
import org.eclipse.imagen.media.tilecodec.TileCodecUtils;

/** A partial implementation of the <code>TileEncoder</code> interface useful for subclassing. */
public abstract class TileEncoderImpl implements TileEncoder {

    /** The name of the format. */
    protected String formatName;

    /** The <code>OutputStream</code> to write the encoded data to. */
    protected OutputStream outputStream;

    /** The <code>TileCodecParameterList</code> object containing the encoding parameters. */
    protected TileCodecParameterList paramList;

    /**
     * Constructs an <code>TileEncoderImpl</code>. An <code>IllegalArgumentException</code> will be thrown if <code>
     * param</code>'s <code>getParameterListDescriptor()</code> method does not return the same descriptor as that from
     * the associated <code>TileCodecDescriptor</code>'s <code>getParameterListDescriptor</code> method for the
     * "tileEncoder" registry mode.
     *
     * <p>If param is null, then the default parameter list for encoding as defined by the associated <code>
     * TileCodecDescriptor</code>'s <code>getDefaultParameters()</code> method will be used for encoding. If this too is
     * null, an <code>IllegalArgumentException</code> will be thrown if the <code>ParameterListDescriptor</code>
     * associated with the associated <code>TileCodecDescriptor</code> for the "tileEncoder" registry mode, reports that
     * the number of parameters for this format is non-zero.
     *
     * @param formatName The name of the format.
     * @param output The <code>OutputStream</code> to write encoded data to.
     * @param param The object containing the tile encoding parameters.
     * @throws IllegalArgumentException if formatName is null.
     * @throws IllegalArgumentException if output is null.
     * @throws IllegalArgumentException if param's getFormatName() method does not return the same formatName as the one
     *     specified to this method.
     * @throws IllegalArgumentException if the ParameterListDescriptors associated with the param and the associated
     *     TileCodecDescriptor are not equal.
     * @throws IllegalArgumentException if param does not have "tileEncoder" as one of the valid modes that it supports.
     */
    public TileEncoderImpl(String formatName, OutputStream output, TileCodecParameterList param) {

        // Cause a IllegalArgumentException to be thrown if formatName, output
        // is null
        if (formatName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("TileCodecDescriptorImpl0"));
        }

        if (output == null) {
            throw new IllegalArgumentException(JaiI18N.getString("TileEncoderImpl0"));
        }

        TileCodecDescriptor tcd = TileCodecUtils.getTileCodecDescriptor("tileEncoder", formatName);

        // If param is null, get the default parameter list.
        if (param == null) param = tcd.getDefaultParameters("tileEncoder");

        if (param != null) {

            // Check whether the formatName from the param is the same as the
            // one supplied to this method.
            if (param.getFormatName().equalsIgnoreCase(formatName) == false) {
                throw new IllegalArgumentException(JaiI18N.getString("TileEncoderImpl1"));
            }

            // Check whether the supplied parameterList supports the
            // "tileDecoder" mode.
            if (param.isValidForMode("tileEncoder") == false) {
                throw new IllegalArgumentException(JaiI18N.getString("TileEncoderImpl2"));
            }

            // Check whether the ParameterListDescriptors are the same.
            if (param.getParameterListDescriptor().equals(tcd.getParameterListDescriptor("tileEncoder")) == false)
                throw new IllegalArgumentException(JaiI18N.getString("TileCodec0"));

        } else {

            // If the supplied parameterList is null and the default one is
            // null too, then check whether this format supports no parameters
            ParameterListDescriptor pld = tcd.getParameterListDescriptor("tileEncoder");

            // If the PLD is not null and says that there are supposed to
            // be some parameters (numParameters returns non-zero value)
            // throw an IllegalArgumentException
            if (pld != null && pld.getNumParameters() != 0) {
                throw new IllegalArgumentException(JaiI18N.getString("TileDecoderImpl6"));
            }
        }

        this.formatName = formatName;
        this.outputStream = output;
        this.paramList = param;
    }

    /** Returns the format name of the encoding scheme. */
    public String getFormatName() {
        return formatName;
    }

    /** Returns the current parameters as an instance of the <code>TileCodecParameterList</code> interface. */
    public TileCodecParameterList getEncodeParameterList() {
        return paramList;
    }

    /** Returns the <code>OutputStream</code> to which the encoded data will be written. */
    public OutputStream getOutputStream() {
        return outputStream;
    }
}
