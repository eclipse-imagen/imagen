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
package org.eclipse.imagen.media.rmi;

import java.awt.image.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.eclipse.imagen.ComponentSampleModelImageN;
import org.eclipse.imagen.RasterFactory;

/**
 * This class is a serializable proxy for a SampleModel from which the SampleModel may be reconstituted.
 *
 * @since EA3
 */
public class SampleModelProxy implements Serializable {
    /** Flag indicating a BandedSampleModel. */
    private static final int TYPE_BANDED = 1;

    /** Flag indicating a PixelInterleavedSampleModel. */
    private static final int TYPE_PIXEL_INTERLEAVED = 2;

    /** Flag indicating a SinglePixelPackedSampleModel. */
    private static final int TYPE_SINGLE_PIXEL_PACKED = 3;

    /** Flag indicating a MultiPixelPackedSampleModel. */
    private static final int TYPE_MULTI_PIXEL_PACKED = 4;

    /** Flag indicating a ComponentSampleModelImageN. */
    private static final int TYPE_COMPONENT_JAI = 5;

    /** Flag indicating a generic ComponentSampleModel. */
    private static final int TYPE_COMPONENT = 6;

    /** The SampleModel. */
    private transient SampleModel sampleModel;

    /*
      parameter	        banded	ileaved	packed1 packedN
      ---------	        ------	-------	------- -------
      dataType	        *       *       *       *
      width		*       *       *       *
      height		*       *       *       *
      numBands          *
      bankIndices       *
      bandOffsets       *       *
      pixelStride               *
      scanlineStride            *       *       *
      bitMasks                          *
      numberOfBits                              *
      dataBitOffset                             *
    */

    /**
     * Constructs a <code>SampleModelProxy</code> from a <code>SampleModel</code>.
     *
     * @param source The <code>SampleModel</code> to be serialized.
     */
    public SampleModelProxy(SampleModel source) {
        sampleModel = source;
    }

    /**
     * Retrieves the associated <code>SampleModel</code>.
     *
     * @return The (perhaps reconstructed) <code>SampleModel</code>.
     */
    public SampleModel getSampleModel() {
        return sampleModel;
    }

    /**
     * Serialize the <code>SampleModelProxy</code>.
     *
     * @param out The <code>ObjectOutputStream</code>.
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        if (sampleModel instanceof ComponentSampleModel) {
            ComponentSampleModel sm = (ComponentSampleModel) sampleModel;
            int sampleModelType = TYPE_COMPONENT;
            int transferType = sm.getTransferType();
            if (sampleModel instanceof PixelInterleavedSampleModel) {
                sampleModelType = TYPE_PIXEL_INTERLEAVED;
            } else if (sampleModel instanceof BandedSampleModel) {
                sampleModelType = TYPE_BANDED;
            } else if (sampleModel instanceof ComponentSampleModelImageN
                    || transferType == DataBuffer.TYPE_FLOAT
                    || transferType == DataBuffer.TYPE_DOUBLE) {
                sampleModelType = TYPE_COMPONENT_JAI;
            }
            out.writeInt(sampleModelType);
            out.writeInt(transferType);
            out.writeInt(sm.getWidth());
            out.writeInt(sm.getHeight());
            if (sampleModelType != TYPE_BANDED) {
                out.writeInt(sm.getPixelStride());
            }
            out.writeInt(sm.getScanlineStride());
            if (sampleModelType != TYPE_PIXEL_INTERLEAVED) {
                out.writeObject(sm.getBankIndices());
            }
            out.writeObject(sm.getBandOffsets());
        } else if (sampleModel instanceof SinglePixelPackedSampleModel) {
            SinglePixelPackedSampleModel sm = (SinglePixelPackedSampleModel) sampleModel;
            out.writeInt(TYPE_SINGLE_PIXEL_PACKED);
            out.writeInt(sm.getTransferType());
            out.writeInt(sm.getWidth());
            out.writeInt(sm.getHeight());
            out.writeInt(sm.getScanlineStride());
            out.writeObject(sm.getBitMasks());
        } else if (sampleModel instanceof MultiPixelPackedSampleModel) {
            MultiPixelPackedSampleModel sm = (MultiPixelPackedSampleModel) sampleModel;
            out.writeInt(TYPE_MULTI_PIXEL_PACKED);
            out.writeInt(sm.getTransferType());
            out.writeInt(sm.getWidth());
            out.writeInt(sm.getHeight());
            out.writeInt(sm.getPixelBitStride());
            out.writeInt(sm.getScanlineStride());
            out.writeInt(sm.getDataBitOffset());
        } else {
            throw new RuntimeException(JaiI18N.getString("SampleModelProxy0"));
        }
    }

    /**
     * Deserialize the <code>SampleModelProxy</code>.
     *
     * @param out The <code>ObjectInputStream</code>.
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        int sampleModelType = (int) in.readInt();
        switch (sampleModelType) {
            case TYPE_PIXEL_INTERLEAVED:
                sampleModel = RasterFactory.createPixelInterleavedSampleModel(
                        in.readInt(), in.readInt(), in.readInt(), in.readInt(), in.readInt(), (int[]) in.readObject());
                break;
            case TYPE_BANDED:
                sampleModel = RasterFactory.createBandedSampleModel(
                        in.readInt(), in.readInt(), in.readInt(), in.readInt(), (int[]) in.readObject(), (int[])
                                in.readObject());
                break;
            case TYPE_COMPONENT_JAI:
                sampleModel = new ComponentSampleModelImageN(
                        in.readInt(),
                        in.readInt(),
                        in.readInt(),
                        in.readInt(),
                        in.readInt(),
                        (int[]) in.readObject(),
                        (int[]) in.readObject());
                break;
            case TYPE_COMPONENT:
                sampleModel = new ComponentSampleModel(
                        in.readInt(),
                        in.readInt(),
                        in.readInt(),
                        in.readInt(),
                        in.readInt(),
                        (int[]) in.readObject(),
                        (int[]) in.readObject());
                break;
            case TYPE_SINGLE_PIXEL_PACKED:
                sampleModel = new SinglePixelPackedSampleModel(
                        in.readInt(), in.readInt(), in.readInt(), in.readInt(), (int[]) in.readObject());
                break;
            case TYPE_MULTI_PIXEL_PACKED:
                sampleModel = new MultiPixelPackedSampleModel(
                        in.readInt(), in.readInt(), in.readInt(), in.readInt(), in.readInt(), in.readInt());
                break;
            default:
                throw new RuntimeException(JaiI18N.getString("SampleModelProxy0"));
        }
    }
}
