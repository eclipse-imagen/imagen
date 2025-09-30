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

package org.eclipse.imagen.media.tilecodec;

import java.awt.*;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.text.MessageFormat;
import org.eclipse.imagen.ImageN;
import org.eclipse.imagen.media.serialize.SerializableState;
import org.eclipse.imagen.media.serialize.SerializerFactory;
import org.eclipse.imagen.tilecodec.TileCodecDescriptor;
import org.eclipse.imagen.tilecodec.TileCodecParameterList;

/** A class containing methods of utility to all TileCodec implementations. */
public class TileCodecUtils {

    private static final int HINT_TILE_CODEC_FORMAT = 112;
    private static final int HINT_TILE_ENCODING_PARAM = 113;
    private static final int HINT_TILE_DECODING_PARAM = 114;

    /** An inner class defining rendering hint keys. */
    static class RenderingKey extends RenderingHints.Key {
        // cache the class to keep TileCodecUtils.class in memory unless
        // the class RenderingKey is GC'ed.  In this case, the
        // WeakReferences in the map of RenderingHints.Key will release
        // the instances of RenderingKey. So when ImageN is loaded next
        // time, the keys can be recreated without any exception.
        // Fix bug: 4754807
        private static Class JAIclass = TileCodecUtils.class;

        private Class objectClass;

        RenderingKey(int privateKey, Class objectClass) {
            super(privateKey);
            this.objectClass = objectClass;
        }

        public boolean isCompatibleValue(Object val) {
            return objectClass.isInstance(val);
        }
    }

    /* Required to I18N compound messages. */
    private static MessageFormat formatter = new MessageFormat("");

    /**
     * Key for specifying the default format to be used for tile serialization via <code>TileCodec</code>s. The
     * corresponding object must be a <code>String</code>. The common <code>RenderingHints</code> do not contain a
     * default hint corresponding to this key.
     */
    public static RenderingHints.Key KEY_TILE_CODEC_FORMAT = new RenderingKey(HINT_TILE_CODEC_FORMAT, String.class);

    /**
     * Key for specifying the default encoding parameters to be used for tile serialization via <code>TileCodec</code>s.
     * The corresponding object must be a <code>TileCodecParameterList</code>. The common <code>RenderingHints</code> do
     * not contain a default hint corresponding to this key.
     */
    public static RenderingHints.Key KEY_TILE_ENCODING_PARAM =
            new RenderingKey(HINT_TILE_ENCODING_PARAM, TileCodecParameterList.class);

    /**
     * Key for specifying the default decoding parameters to be used for tile serialization via <code>TileCodec</code>s.
     * The corresponding object must be a <code>TileCodecParameterList</code>. The common <code>RenderingHints</code> do
     * not contain a default hint corresponding to this key.
     */
    public static RenderingHints.Key KEY_TILE_DECODING_PARAM =
            new RenderingKey(HINT_TILE_DECODING_PARAM, TileCodecParameterList.class);

    /** Get the <code>TileCodecDescriptor</code> associated with the specified registry mode. */
    public static TileCodecDescriptor getTileCodecDescriptor(String registryMode, String formatName) {
        return (TileCodecDescriptor)
                ImageN.getDefaultInstance().getOperationRegistry().getDescriptor(registryMode, formatName);
    }

    /** Deserialize a <code>Raster</code> from its serialized version */
    public static Raster deserializeRaster(Object object) {
        if (!(object instanceof SerializableState)) return null;

        SerializableState ss = (SerializableState) object;
        Class c = ss.getObjectClass();
        if (Raster.class.isAssignableFrom(c)) {
            return (Raster) ss.getObject();
        }
        return null;
    }

    /** Deserialize a <code>SampleModel</code> from its serialized version */
    public static SampleModel deserializeSampleModel(Object object) {
        if (!(object instanceof SerializableState)) return null;

        SerializableState ss = (SerializableState) object;
        Class c = ss.getObjectClass();
        if (SampleModel.class.isAssignableFrom(c)) {
            return (SampleModel) ss.getObject();
        }
        return null;
    }

    /** Serialize a <code>Raster</code>. */
    public static Object serializeRaster(Raster ras) {
        return SerializerFactory.getState(ras, null);
    }

    /** Serialize a <code>SampleModel</code>. */
    public static Object serializeSampleModel(SampleModel sm) {
        return SerializerFactory.getState(sm, null);
    }
}
