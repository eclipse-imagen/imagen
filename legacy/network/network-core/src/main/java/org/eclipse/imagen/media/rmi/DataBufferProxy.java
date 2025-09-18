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

import java.awt.image.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * This class is a serializable proxy for a DataBuffer from which the DataBuffer may be reconstituted.
 *
 * @since EA3
 */
public class DataBufferProxy implements Serializable {
    /** The DataBuffer. */
    private transient DataBuffer dataBuffer;

    /**
     * Constructs a <code>DataBufferProxy</code> from a <code>DataBuffer</code>.
     *
     * @param source The <code>DataBuffer</code> to be serialized.
     */
    public DataBufferProxy(DataBuffer source) {
        dataBuffer = source;
    }

    /**
     * Retrieves the associated <code>DataBuffer</code>.
     *
     * @return The (perhaps reconstructed) <code>DataBuffer</code>.
     */
    public DataBuffer getDataBuffer() {
        return dataBuffer;
    }

    // XXX Note that there is potential for some form of data compression in
    // the readObject() and writeObject() methods.

    /**
     * Serialize the <code>DataBufferProxy</code>.
     *
     * @param out The <code>ObjectOutputStream</code>.
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
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
                dataArray = ((DataBufferDouble) dataBuffer).getBankData();
                break;
            default:
                throw new RuntimeException(JaiI18N.getString("DataBufferProxy0"));
        }
        out.writeObject(dataArray);
    }

    /**
     * Deserialize the <code>DataBufferProxy</code>.
     *
     * @param out The <code>ObjectInputStream</code>.
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
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
                dataBuffer = new DataBufferFloat((float[][]) dataArray, size, offsets);
                break;
            case DataBuffer.TYPE_DOUBLE:
                dataBuffer = new DataBufferDouble((double[][]) dataArray, size, offsets);
                break;
            default:
                throw new RuntimeException(JaiI18N.getString("DataBufferProxy0"));
        }
    }
}
