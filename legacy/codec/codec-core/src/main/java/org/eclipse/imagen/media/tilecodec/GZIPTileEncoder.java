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

import java.awt.image.Raster;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;
import org.eclipse.imagen.tilecodec.TileCodecParameterList;
import org.eclipse.imagen.tilecodec.TileEncoderImpl;

/** A concrete implementation of the <code>TileEncoderImpl</code> class for the gzip tile codec. */
public class GZIPTileEncoder extends TileEncoderImpl {

    /**
     * Constructs an <code>GZIPTileEncoder</code>.
     *
     * @param output The <code>OutputStream</code> to write encoded data to.
     * @param param The object containing the tile encoding parameters.
     * @throws IllegalArgumentException if param is not the appropriate Class type.
     * @throws IllegalArgumentException is output is null.
     */
    public GZIPTileEncoder(OutputStream output, TileCodecParameterList param) {
        super("gzip", output, param);
    }

    /**
     * Encodes a <code>Raster</code> and writes the output to the <code>OutputStream</code> associated with this <code>
     * TileEncoder</code>.
     *
     * @param ras the <code>Raster</code> to encode.
     * @throws IOException if an I/O error occurs while writing to the OutputStream.
     * @throws IllegalArgumentException if ras is null.
     */
    public void encode(Raster ras) throws IOException {
        if (ras == null) throw new IllegalArgumentException(JaiI18N.getString("TileEncoder1"));

        ObjectOutputStream oos = new ObjectOutputStream(new GZIPOutputStream(outputStream));
        Object object = TileCodecUtils.serializeRaster(ras);
        oos.writeObject(object);
        oos.close();
    }
}
