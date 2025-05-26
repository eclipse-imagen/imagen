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

import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.text.MessageFormat;
import org.eclipse.imagen.JAI;
import org.eclipse.imagen.remote.SerializableState;
import org.eclipse.imagen.remote.SerializerFactory;
import org.eclipse.imagen.tilecodec.TileCodecDescriptor;

/** A class containing methods of utility to all TileCodec implementations. */
public class TileCodecUtils {
    /* Required to I18N compound messages. */
    private static MessageFormat formatter = new MessageFormat("");

    /** Get the <code>TileCodecDescriptor</code> associated with the specified registry mode. */
    public static TileCodecDescriptor getTileCodecDescriptor(String registryMode, String formatName) {
        return (TileCodecDescriptor)
                JAI.getDefaultInstance().getOperationRegistry().getDescriptor(registryMode, formatName);
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
