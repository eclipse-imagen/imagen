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

package org.eclipse.imagen.media.rmi;

import java.awt.*;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRenderedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.eclipse.imagen.ImageN;
import org.eclipse.imagen.OperationRegistry;
import org.eclipse.imagen.TiledImage;
import org.eclipse.imagen.media.serialize.SerializableStateImpl;
import org.eclipse.imagen.media.tilecodec.TileCodecUtils;
import org.eclipse.imagen.remote.SerializableRenderedImage;
import org.eclipse.imagen.tilecodec.TileCodecParameterList;

/**
 * A <code>SerializableState</code> wrapper for a <code>RenderedImage</code> or <code>WritableRenderedImage</code>. This
 * class simply uses a <code>SerializableRenderedImage</code> to do the work.
 *
 * @since 1.1
 */
public final class RenderedImageState extends SerializableStateImpl {
    private boolean isWritable;

    private transient boolean useDeepCopy;
    private transient OperationRegistry registry;
    private transient String formatName;
    private transient TileCodecParameterList encodingParam;
    private transient TileCodecParameterList decodingParam;

    public static Class[] getSupportedClasses() {
        return new Class[] {RenderedImage.class, WritableRenderedImage.class};
    }

    public RenderedImageState(Class c, Object o, RenderingHints h) {
        super(c, o, h);

        isWritable = o instanceof WritableRenderedImage;

        if (h != null) {
            Object value = h.get(ImageN.KEY_SERIALIZE_DEEP_COPY);
            if (value != null) {
                useDeepCopy = ((Boolean) value).booleanValue();
            } else {
                useDeepCopy = false;
            }

            value = h.get(ImageN.KEY_OPERATION_REGISTRY);
            if (value != null) {
                registry = (OperationRegistry) value;
            }

            value = h.get(TileCodecUtils.KEY_TILE_CODEC_FORMAT);
            if (value != null) {
                formatName = (String) value;
            }

            value = h.get(TileCodecUtils.KEY_TILE_ENCODING_PARAM);
            if (value != null) {
                encodingParam = (TileCodecParameterList) value;
            }

            value = h.get(TileCodecUtils.KEY_TILE_DECODING_PARAM);
            if (value != null) {
                decodingParam = (TileCodecParameterList) value;
            }
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {

        out.defaultWriteObject();

        SerializableRenderedImage sri;

        if (formatName == null || encodingParam == null || decodingParam == null) {
            sri = new SerializableRenderedImage((RenderedImage) theObject, useDeepCopy);
        } else {
            sri = new SerializableRenderedImage(
                    (RenderedImage) theObject, useDeepCopy, registry, formatName, encodingParam, decodingParam);
        }

        out.writeObject(sri);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        theObject = in.readObject();
        if (isWritable) {
            theObject = new TiledImage((RenderedImage) theObject, true);
        }
    }
}
