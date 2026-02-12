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
package org.eclipse.imagen.media.tilecodec;

import java.io.InputStream;
import java.util.Vector;
import org.eclipse.imagen.NegotiableCapability;
import org.eclipse.imagen.ParameterListDescriptorImpl;
import org.eclipse.imagen.tilecodec.TileCodecParameterList;
import org.eclipse.imagen.tilecodec.TileDecoder;
import org.eclipse.imagen.tilecodec.TileDecoderFactory;

/**
 * A factory for creating <code>GZIPTileDecoder</code>s.
 *
 * <p>This class stipulates that the capabilities of the <code>TileDecoder</code> be specified by implementing the
 * <code>getDecodingCapability()</code> method.
 *
 * @see org.eclipse.imagen.NegotiableCapability
 */
public class GZIPTileDecoderFactory implements TileDecoderFactory {

    /**
     * Creates a <code>GZIPTileDecoder</code> capable of decoding the encoded data from the given <code>InputStream
     * </code> using the specified <code>TileCodecParameterList</code> containing the decoding parameters to be used.
     *
     * <p>This method can return null if the <code>TileDecoder</code> is not capable of producing output for the given
     * set of parameters. For example, if a <code>TileDecoder</code> is only capable of dealing with a jpeg quality
     * factor of 0.5, and the associated <code>TileCodecParameterList</code> specifies a quality factor of 0.75, null
     * should be returned.
     *
     * <p>It is recommended that the data in the supplied <code>InputStream</code> not be used as a factor in
     * determining whether this <code>InputStream</code> can be successfully decoded, unless the supplied <code>
     * InputStream</code> is known to be rewindable (i.e. its <code>markSupported()</code> method returns true or it has
     * additional functionality that allows backward seeking). It is required that <code>the</code> InputStream contain
     * the same data on returning from this method as before this method was called. In other words, the <code>
     * InputStream</code> should only be used as a discriminator if it can be rewound to its starting position before
     * returning from this method. Note that wrapping the incoming <code>InputStream</code> in a <code>
     * PushbackInputStream</code> and then rewinding the <code>PushbackInputStream</code> before returning does not
     * rewind the wrapped <code>InputStream</code>.
     *
     * <p>If the supplied <code>TileCodecParameterList</code> is null, a default <code>TileCodecParameterList</code>
     * from the <code>TileCodecDescriptor</code> will be used to create the decoder.
     *
     * <p>Exceptions thrown by the <code>TileDecoder</code> will be caught by this method and will not be propagated.
     *
     * @param input The <code>InputStream</code> containing the encoded data to decode.
     * @param param The parameters to be be used in the decoding process.
     * @throws IllegalArgumentException if input is null.
     */
    public TileDecoder createDecoder(InputStream input, TileCodecParameterList param) {

        if (input == null) throw new IllegalArgumentException(JaiI18N.getString("TileDecoder0"));

        return new GZIPTileDecoder(input, param);
    }

    /** Returns the capabilities of this <code>TileDecoder</code> as a <code>NegotiableCapability</code>. */
    public NegotiableCapability getDecodeCapability() {

        Vector generators = new Vector();
        generators.add(GZIPTileDecoderFactory.class);

        return new NegotiableCapability(
                "tileCodec", "gzip", generators, new ParameterListDescriptorImpl(null, null, null, null, null), false);
    }
}
