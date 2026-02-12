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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.eclipse.imagen.media.codec.BMPEncodeParam;
import org.eclipse.imagen.media.codec.ImageCodec;
import org.eclipse.imagen.media.codec.ImageDecodeParam;
import org.eclipse.imagen.media.codec.ImageDecoder;
import org.eclipse.imagen.media.codec.ImageEncodeParam;
import org.eclipse.imagen.media.codec.ImageEncoder;
import org.eclipse.imagen.media.codec.SeekableStream;

/** @since EA2 */
public final class BMPCodec extends ImageCodec {

    public BMPCodec() {}

    public String getFormatName() {
        return "bmp";
    }

    public Class getEncodeParamClass() {
        return org.eclipse.imagen.media.codec.BMPEncodeParam.class;
    }

    public Class getDecodeParamClass() {
        return Object.class;
    }

    public boolean canEncodeImage(RenderedImage im, ImageEncodeParam param) {
        SampleModel sampleModel = im.getSampleModel();
        int dataType = sampleModel.getTransferType();
        if (dataType != DataBuffer.TYPE_BYTE && !CodecUtils.isPackedByteImage(im)) {
            return false;
        }

        if (param != null) {
            if (!(param instanceof BMPEncodeParam)) {
                return false;
            }
            BMPEncodeParam BMPParam = (BMPEncodeParam) param;

            int version = BMPParam.getVersion();
            if ((version == BMPEncodeParam.VERSION_2) || (version == BMPEncodeParam.VERSION_4)) {
                return false;
            }
        }

        return true;
    }

    protected ImageEncoder createImageEncoder(OutputStream dst, ImageEncodeParam param) {
        BMPEncodeParam p = null;
        if (param != null) {
            p = (BMPEncodeParam) param;
        }

        return new BMPImageEncoder(dst, p);
    }

    protected ImageDecoder createImageDecoder(InputStream src, ImageDecodeParam param) {
        return new BMPImageDecoder(src, null);
    }

    protected ImageDecoder createImageDecoder(File src, ImageDecodeParam param) throws IOException {
        return new BMPImageDecoder(new FileInputStream(src), null);
    }

    protected ImageDecoder createImageDecoder(SeekableStream src, ImageDecodeParam param) {
        return new BMPImageDecoder(src, null);
    }

    public int getNumHeaderBytes() {
        return 2;
    }

    public boolean isFormatRecognized(byte[] header) {
        return ((header[0] == 0x42) && (header[1] == 0x4d));
    }
}
