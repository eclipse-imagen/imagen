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

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.eclipse.imagen.media.codec.ImageCodec;
import org.eclipse.imagen.media.codec.ImageDecodeParam;
import org.eclipse.imagen.media.codec.ImageDecoder;
import org.eclipse.imagen.media.codec.ImageEncodeParam;
import org.eclipse.imagen.media.codec.ImageEncoder;
import org.eclipse.imagen.media.codec.SeekableStream;
// import org.eclipse.imagen.media.codec.GIFEncodeParam;

/** @since EA3 */
public final class GIFCodec extends ImageCodec {

    public GIFCodec() {}

    public String getFormatName() {
        return "gif";
    }

    public Class getEncodeParamClass() {
        return Object.class;
    }

    public Class getDecodeParamClass() {
        return Object.class;
    }

    public boolean canEncodeImage(RenderedImage im, ImageEncodeParam param) {
        return false;
    }

    protected ImageEncoder createImageEncoder(OutputStream dst, ImageEncodeParam param) {
        /*
        GIFEncodeParam p = null;
        if (param != null) {
            p = (GIFEncodeParam)param;
        }

        return new GIFImageEncoder(dst, p);
        */

        return null;
    }

    protected ImageDecoder createImageDecoder(InputStream src, ImageDecodeParam param) {
        return new GIFImageDecoder(src, param);
    }

    protected ImageDecoder createImageDecoder(File src, ImageDecodeParam param) throws IOException {
        return new GIFImageDecoder(new FileInputStream(src), null);
    }

    protected ImageDecoder createImageDecoder(SeekableStream src, ImageDecodeParam param) {
        return new GIFImageDecoder(src, param);
    }

    public int getNumHeaderBytes() {
        return 4;
    }

    public boolean isFormatRecognized(byte[] header) {
        return ((header[0] == 'G') && (header[1] == 'I') && (header[2] == 'F') && (header[3] == '8'));
    }
}
