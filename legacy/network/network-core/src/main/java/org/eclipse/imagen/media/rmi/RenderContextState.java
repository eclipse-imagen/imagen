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

import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.image.renderable.RenderContext;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.eclipse.imagen.remote.SerializableState;
import org.eclipse.imagen.remote.SerializerFactory;

/**
 * This class is a serializable proxy for a RenderContext from which the RenderContext may be reconstituted.
 *
 * @since 1.1
 */
public class RenderContextState extends SerializableStateImpl {
    public static Class[] getSupportedClasses() {
        return new Class[] {RenderContext.class};
    }

    /**
     * Constructs a <code>RenderContextState</code> from a <code>RenderContext</code>.
     *
     * @param c The class of the object to be serialized.
     * @param o The <code>RenderContext</code> to be serialized.
     * @param h The <code>RenderingHints</code> (ignored).
     */
    public RenderContextState(Class c, Object o, RenderingHints h) {
        super(c, o, h);
    }

    /**
     * Serialize the <code>RenderContextState</code>.
     *
     * @param out The <code>ObjectOutputStream</code>.
     */
    private void writeObject(ObjectOutputStream out) throws IOException {

        RenderContext renderContext = (RenderContext) theObject;

        // Extract the affine transform.
        AffineTransform usr2dev = renderContext.getTransform();

        // Extract the hints.
        RenderingHints hints = renderContext.getRenderingHints();

        // Extract the AOI
        Shape aoi = renderContext.getAreaOfInterest();

        // Write serialized form to the stream.
        out.writeObject(usr2dev);
        out.writeObject(SerializerFactory.getState(aoi));
        out.writeObject(SerializerFactory.getState(hints, null));
    }

    /**
     * Deserialize the <code>RenderContextState</code>.
     *
     * @param out The <code>ObjectInputStream</code>.
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {

        RenderContext renderContext = null;

        // Read serialized form from the stream.
        AffineTransform usr2dev = (AffineTransform) in.readObject();

        SerializableState aoi = (SerializableState) in.readObject();
        Shape shape = (Shape) aoi.getObject();

        SerializableState rhs = (SerializableState) in.readObject();
        RenderingHints hints = (RenderingHints) rhs.getObject();

        // Restore the transient RenderContext.
        renderContext = new RenderContext(usr2dev, shape, hints);
        theObject = renderContext;
    }
}
