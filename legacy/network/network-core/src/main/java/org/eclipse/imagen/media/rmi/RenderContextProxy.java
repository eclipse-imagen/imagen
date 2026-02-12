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

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.renderable.RenderContext;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.eclipse.imagen.ROIShape;

/**
 * This class is a serializable proxy for a RenderContext from which the RenderContext may be reconstituted.
 *
 * @since EA3
 */
public class RenderContextProxy implements Serializable {
    /** The RenderContext. */
    private transient RenderContext renderContext;

    /**
     * Constructs a <code>DatabufferProxy</code> from a <code>Databuffer</code>.
     *
     * @param source The <code>Databuffer</code> to be serialized.
     */
    public RenderContextProxy(RenderContext source) {
        renderContext = source;
    }

    /**
     * Retrieves the associated <code>Databuffer</code>.
     *
     * @return The (perhaps reconstructed) <code>Databuffer</code>.
     */
    public RenderContext getRenderContext() {
        return renderContext;
    }

    /**
     * Serialize the <code>RenderContextProxy</code>.
     *
     * @param out The <code>ObjectOutputStream</code>.
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        boolean isNull = renderContext == null;
        out.writeBoolean(isNull);
        if (isNull) return;

        // Extract the affine transform.
        AffineTransform usr2dev = renderContext.getTransform();

        // Create serializable form of the hints.
        RenderingHintsProxy rhp = new RenderingHintsProxy(renderContext.getRenderingHints());

        Shape aoi = renderContext.getAreaOfInterest();

        // Write serialized form to the stream.
        out.writeObject(usr2dev);

        out.writeBoolean(aoi != null);
        if (aoi != null) {
            if (aoi instanceof Serializable) {
                out.writeObject(aoi);
            } else {
                out.writeObject(new ROIShape(aoi));
            }
        }

        out.writeObject(rhp);
    }

    /**
     * Deserialize the <code>RenderContextProxy</code>.
     *
     * @param out The <code>ObjectInputStream</code>.
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {

        if (in.readBoolean()) {
            renderContext = null;
            return;
        }

        // Read serialized form from the stream.
        AffineTransform usr2dev = (AffineTransform) in.readObject();

        Shape shape = null;
        Object aoi = in.readBoolean() ? (Object) in.readObject() : null;
        RenderingHintsProxy rhp = (RenderingHintsProxy) in.readObject();

        RenderingHints hints = rhp.getRenderingHints();

        // Restore the transient RenderContext.
        if (aoi != null) {
            if (aoi instanceof ROIShape) {
                shape = ((ROIShape) aoi).getAsShape();
            } else {
                shape = (Shape) aoi;
            }
        }

        if (aoi == null && hints.isEmpty()) {
            renderContext = new RenderContext(usr2dev);
        } else if (aoi == null) {
            renderContext = new RenderContext(usr2dev, hints);
        } else if (hints.isEmpty()) {
            renderContext = new RenderContext(usr2dev, shape);
        } else {
            renderContext = new RenderContext(usr2dev, shape, hints);
        }
    }
}
