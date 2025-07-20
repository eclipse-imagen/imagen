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
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.eclipse.imagen.media.serialize.SerializableState;
import org.eclipse.imagen.media.serialize.SerializerFactory;

/**
 * This class is a serializable proxy for a Raster from which the Raster may be reconstituted.
 *
 * @since EA3
 */
public class RasterProxy implements Serializable {
    /** The Raster. */
    private transient Raster raster;

    /**
     * Constructs a <code>RasterProxy</code> from a <code>Raster</code>.
     *
     * @param source The <code>Raster</code> to be serialized.
     */
    public RasterProxy(Raster source) {
        raster = source;
    }

    /**
     * Retrieves the associated <code>Raster</code>.
     *
     * @return The (perhaps reconstructed) <code>Raster</code>.
     */
    public Raster getRaster() {
        return raster;
    }

    /**
     * Serialize the <code>RasterProxy</code>.
     *
     * @param out The <code>ObjectOutputStream</code>.
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        Raster r;

        if (raster.getParent() != null) {
            // Use the child ratser to create another Raster
            // containing data for the requested bounds but which does
            // not share the SampleModel and DataBuffer of the parent.
            // Fix : 4631478
            r = raster.createCompatibleWritableRaster(raster.getBounds());
            ((WritableRaster) r).setRect(raster);
        } else {
            r = raster;
        }

        out.writeInt(r.getWidth());
        out.writeInt(r.getHeight());
        out.writeObject(SerializerFactory.getState(r.getSampleModel(), null));
        out.writeObject(SerializerFactory.getState(r.getDataBuffer(), null));
        out.writeObject(new Point(r.getMinX(), r.getMinY()));
    }

    /**
     * Deserialize the <code>RasterProxy</code>.
     *
     * @param out The <code>ObjectInputStream</code>.
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        int width;
        int height;
        SerializableState sampleModelState = null;
        SerializableState dataBufferState = null;
        Point location = null;

        width = in.readInt();
        height = in.readInt();
        sampleModelState = (SerializableState) in.readObject();
        dataBufferState = (SerializableState) in.readObject();
        location = (Point) in.readObject();

        // Restore the SampleModel from its serialized form.
        SampleModel sampleModel = (SampleModel) sampleModelState.getObject();
        if (sampleModel == null) {
            raster = null;
            return;
        }

        // Restore the DataBuffer from its serialized form.
        DataBuffer dataBuffer = (DataBuffer) dataBufferState.getObject();

        // Reconstruct the Raster.
        raster = Raster.createRaster(sampleModel, dataBuffer, location);
    }
}
