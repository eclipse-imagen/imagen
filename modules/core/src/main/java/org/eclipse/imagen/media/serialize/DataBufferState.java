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

package org.eclipse.imagen.media.serialize;

import java.awt.RenderingHints;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferDouble;
import java.awt.image.DataBufferFloat;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * This class is a serializable proxy for a DataBuffer from which the DataBuffer may be reconstituted.
 *
 * @since 1.1
 */
public class DataBufferState extends SerializableStateImpl {

    /** The DataBuffer. */
    private transient DataBuffer dataBuffer;

    public static Class[] getSupportedClasses() {
        return new Class[] {
            DataBufferByte.class,
            DataBufferShort.class,
            DataBufferUShort.class,
            DataBufferInt.class,
            DataBufferFloat.class,
            DataBufferDouble.class
        };
    }

    /**
     * Constructs a <code>DataBufferState</code> from a <code>DataBuffer</code>.
     *
     * @param c The <code>DataBuffer</code> to be serialized.
     * @param o The <code>SampleModel</code> to be serialized.
     * @param h The <code>RenderingHints</code> (ignored).
     */
    public DataBufferState(Class c, Object o, RenderingHints h) {
        super(c, o, h);
    }

    // XXX Note that there is potential for some form of data compression in
    // the readObject() and writeObject() methods.

    /**
     * Serialize the <code>DataBufferState</code>.
     *
     * @param out The <code>ObjectOutputStream</code>.
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        DataBuffer dataBuffer = (DataBuffer) theObject;

        // Write serialized form to the stream.
        int dataType = dataBuffer.getDataType();
        out.writeInt(dataType);
        out.writeObject(dataBuffer.getOffsets());
        out.writeInt(dataBuffer.getSize());
        Object dataArray = null;
        switch (dataType) {
            case DataBuffer.TYPE_BYTE:
                dataArray = ((DataBufferByte) dataBuffer).getBankData();
                break;
            case DataBuffer.TYPE_SHORT:
                dataArray = ((DataBufferShort) dataBuffer).getBankData();
                break;
            case DataBuffer.TYPE_USHORT:
                dataArray = ((DataBufferUShort) dataBuffer).getBankData();
                break;
            case DataBuffer.TYPE_INT:
                dataArray = ((DataBufferInt) dataBuffer).getBankData();
                break;
            case DataBuffer.TYPE_FLOAT:
                dataArray = ((DataBufferFloat) dataBuffer).getBankData();
                break;
            case DataBuffer.TYPE_DOUBLE:
                dataArray = ((java.awt.image.DataBufferDouble) dataBuffer).getBankData();
                break;
            default:
                throw new RuntimeException(JaiI18N.getString("DataBufferState0"));
        }
        out.writeObject(dataArray);
    }

    /**
     * Deserialize the <code>DataBufferState</code>.
     *
     * @param in The <code>ObjectInputStream</code>.
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        DataBuffer dataBuffer = null;

        // Read serialized form from the stream.
        int dataType = -1;
        int[] offsets = null;
        int size = -1;
        Object dataArray = null;
        dataType = in.readInt();
        offsets = (int[]) in.readObject();
        size = in.readInt();
        dataArray = in.readObject();

        // Restore the transient DataBuffer.
        switch (dataType) {
            case DataBuffer.TYPE_BYTE:
                dataBuffer = new DataBufferByte((byte[][]) dataArray, size, offsets);
                break;
            case DataBuffer.TYPE_SHORT:
                dataBuffer = new DataBufferShort((short[][]) dataArray, size, offsets);
                break;
            case DataBuffer.TYPE_USHORT:
                dataBuffer = new DataBufferUShort((short[][]) dataArray, size, offsets);
                break;
            case DataBuffer.TYPE_INT:
                dataBuffer = new DataBufferInt((int[][]) dataArray, size, offsets);
                break;
            case DataBuffer.TYPE_FLOAT:
                dataBuffer = new java.awt.image.DataBufferFloat((float[][]) dataArray, size, offsets);
                break;
            case DataBuffer.TYPE_DOUBLE:
                dataBuffer = new DataBufferDouble((double[][]) dataArray, size, offsets);
                break;
            default:
                throw new RuntimeException(JaiI18N.getString("DataBufferState0"));
        }

        theObject = dataBuffer;
    }
}
