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

import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.Raster;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.zip.GZIPInputStream;
import org.eclipse.imagen.media.util.ImageUtil;
import org.eclipse.imagen.tilecodec.TileCodecParameterList;
import org.eclipse.imagen.tilecodec.TileDecoderImpl;
import org.eclipse.imagen.util.ImagingListener;

/** A concrete implementation of the <code>TileDecoderImpl</code> class for the gzip tile codec. */
public class GZIPTileDecoder extends TileDecoderImpl {
    /**
     * Constructs a <code>GZIPTileDecoder</code>. <code>GZIPTileDecoder</code> may throw a <code>
     * IllegalArgumentException</code> if <code>param</code>'s <code>getParameterListDescriptor()</code> method does not
     * return the same descriptor as that from the associated <code>TileCodecDescriptor</code>'s <code>
     * getParameterListDescriptor</code> method for the "tileDecoder" registry mode.
     *
     * <p>If param is null, then the default parameter list for decoding as defined by the associated <code>
     * TileCodecDescriptor</code>'s <code>getDefaultParameters()</code> method will be used for decoding.
     *
     * @param input The <code>InputStream</code> to decode data from.
     * @param param The object containing the tile decoding parameters.
     * @throws IllegalArgumentException if input is null.
     * @throws IllegalArgumentException if param is not appropriate.
     */
    public GZIPTileDecoder(InputStream input, TileCodecParameterList param) {
        super("gzip", input, param);
    }

    /**
     * Returns a <code>Raster</code> that contains the decoded contents of the <code>InputStream</code> associated with
     * this <code>TileDecoder</code>.
     *
     * <p>This method can perform the decoding correctly only when <code>includesLocationInfo()</code> returns true.
     *
     * @throws IOException if an I/O error occurs while reading from the associated InputStream.
     * @throws IllegalArgumentException if the associated TileCodecDescriptor's includesLocationInfo() returns false.
     */
    public Raster decode() throws IOException {

        ObjectInputStream ois = new ObjectInputStream(new GZIPInputStream(inputStream));

        try {
            Object object = ois.readObject();
            return TileCodecUtils.deserializeRaster(object);
        } catch (ClassNotFoundException e) {
            ImagingListener listener = ImageUtil.getImagingListener((RenderingHints) null);
            listener.errorOccurred(JaiI18N.getString("ClassNotFound"), e, this, false);

            //            e.printStackTrace();
            return null;
        } finally {
            ois.close();
        }
    }

    public Raster decode(Point location) throws IOException {
        return decode();
    }
}
