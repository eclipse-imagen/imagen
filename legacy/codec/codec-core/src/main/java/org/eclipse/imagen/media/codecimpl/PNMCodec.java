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
package org.eclipse.imagen.media.codecimpl;

import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import org.eclipse.imagen.media.codec.ForwardSeekableStream;
import org.eclipse.imagen.media.codec.ImageCodec;
import org.eclipse.imagen.media.codec.ImageDecodeParam;
import org.eclipse.imagen.media.codec.ImageDecoder;
import org.eclipse.imagen.media.codec.ImageEncodeParam;
import org.eclipse.imagen.media.codec.ImageEncoder;
import org.eclipse.imagen.media.codec.PNMEncodeParam;
import org.eclipse.imagen.media.codec.SeekableStream;

/**
 * A subclass of <code>ImageCodec</code> that handles the PNM family of formats (PBM, PGM, PPM).
 *
 * <p>The PBM format encodes a single-banded, 1-bit image. The PGM format encodes a single-banded image of any bit depth
 * between 1 and 32. The PPM format encodes three-banded images of any bit depth between 1 and 32. All formats have an
 * ASCII and a raw representation.
 *
 * @since EA2
 */
public final class PNMCodec extends ImageCodec {

    /** Constructs an instance of <code>PNMCodec</code>. */
    public PNMCodec() {}

    /** Returns the name of the format handled by this codec. */
    public String getFormatName() {
        return "pnm";
    }

    public Class getEncodeParamClass() {
        return org.eclipse.imagen.media.codec.PNMEncodeParam.class;
    }

    public Class getDecodeParamClass() {
        return Object.class;
    }

    public boolean canEncodeImage(RenderedImage im, ImageEncodeParam param) {
        SampleModel sampleModel = im.getSampleModel();

        int dataType = sampleModel.getTransferType();
        if ((dataType == DataBuffer.TYPE_FLOAT) || (dataType == DataBuffer.TYPE_DOUBLE)) {
            return false;
        }

        int numBands = sampleModel.getNumBands();
        if (numBands != 1 && numBands != 3) {
            return false;
        }

        return true;
    }

    /**
     * Instantiates a <code>PNMImageEncoder</code> to write to the given <code>OutputStream</code>.
     *
     * @param dst the <code>OutputStream</code> to write to.
     * @param param an instance of <code>PNMEncodeParam</code> used to control the encoding process, or <code>null
     *     </code>. A <code>ClassCastException</code> will be thrown if <code>param</code> is non-null but not an
     *     instance of <code>PNMEncodeParam</code>.
     */
    protected ImageEncoder createImageEncoder(OutputStream dst, ImageEncodeParam param) {
        PNMEncodeParam p = null;
        if (param != null) {
            p = (PNMEncodeParam) param; // May throw a ClassCast exception
        }

        return new PNMImageEncoder(dst, p);
    }

    /**
     * Instantiates a <code>PNMImageDecoder</code> to read from the given <code>InputStream</code>.
     *
     * <p>By overriding this method, <code>PNMCodec</code> is able to ensure that a <code>ForwardSeekableStream</code>
     * is used to wrap the source <code>InputStream</code> instead of the a general (and more expensive) subclass of
     * <code>SeekableStream</code>. Since the PNM decoder does not require the ability to seek backwards in its input,
     * this allows for greater efficiency.
     *
     * @param src the <code>InputStream</code> to read from.
     * @param param an instance of <code>ImageDecodeParam</code> used to control the decoding process, or <code>null
     *     </code>. This parameter is ignored by this class.
     */
    protected ImageDecoder createImageDecoder(InputStream src, ImageDecodeParam param) {
        // Add buffering for efficiency
        if (!(src instanceof BufferedInputStream)) {
            src = new BufferedInputStream(src);
        }
        return new PNMImageDecoder(new ForwardSeekableStream(src), null);
    }

    /**
     * Instantiates a <code>PNMImageDecoder</code> to read from the given <code>SeekableStream</code>.
     *
     * @param src the <code>SeekableStream</code> to read from.
     * @param param an instance of <code>ImageDecodeParam</code> used to control the decoding process, or <code>null
     *     </code>. This parameter is ignored by this class.
     */
    protected ImageDecoder createImageDecoder(SeekableStream src, ImageDecodeParam param) {
        return new PNMImageDecoder(src, null);
    }

    /** Returns the number of bytes from the beginning of the data required to recognize it as being in PNM format. */
    public int getNumHeaderBytes() {
        return 2;
    }

    /**
     * Returns <code>true</code> if the header bytes indicate PNM format.
     *
     * @param header an array of bytes containing the initial bytes of the input data.
     */
    public boolean isFormatRecognized(byte[] header) {
        return ((header[0] == 'P') && (header[1] >= '1') && (header[1] <= '6'));
    }
}
