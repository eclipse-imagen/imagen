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

import java.awt.image.SampleModel;
import java.io.OutputStream;
import org.eclipse.imagen.NegotiableCapability;

/**
 * A factory for creating <code>TileEncoder</code>s.
 *
 * <p>This class stipulates that the capabilities of the <code>TileEncoder</code> be specified by implementing the
 * <code>getEncodingCapability()</code> method.
 *
 * @see org.eclipse.imagen.NegotiableCapability
 */
public interface TileEncoderFactory {

    /**
     * Creates a <code>TileEncoder</code> capable of encoding a <code>Raster</code> with the specified <code>SampleModel
     * </code> using the encoding parameters specified via the <code>TileCodecParameterList</code>, to the given <code>
     * OutputStream</code>.
     *
     * <p>This method can return null if the <code>TileEncoder</code> is not capable of producing output for the given
     * set of parameters. For example, if a <code>TileEncoder</code> is only capable of dealing with a <code>
     * PixelInterleavedSampleModel</code>, and the supplied <code>SampleModel</code> is not an instance of <code>
     * PixelInterleavedSampleModel</code>, null should be returned. The supplied <code>SampleModel</code> should be used
     * to decide whether it can be encoded by this class, and is not needed to actually construct a <code>TileEncoder
     * </code>.
     *
     * <p>If the supplied <code>TileCodecParameterList</code> is null, a default <code>TileCodecParameterList</code>
     * from the <code>TileCodecDescriptor</code> will be used to create the encoder.
     *
     * <p>Exceptions thrown by the <code>TileEncoder</code> will be caught by this method and will not be propagated.
     *
     * @param output The <code>OutputStream</code> to write the encoded data to.
     * @param paramList The <code>TileCodecParameterList</code> containing the encoding parameters.
     * @param sampleModel The <code>SampleModel</code> of the <code>Raster</code> to be encoded.
     * @throws IllegalArgumentException if output is null.
     * @throws IllegalArgumentException if sampleModel is null.
     */
    TileEncoder createEncoder(OutputStream output, TileCodecParameterList paramList, SampleModel sampleModel);

    /**
     * Returns the capabilities of this <code>TileEncoder</code> as a <code>NegotiableCapability</code>.
     *
     * @see org.eclipse.imagen.NegotiableCapability
     */
    NegotiableCapability getEncodeCapability();
}
