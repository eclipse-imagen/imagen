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

package org.eclipse.imagen.media.opimage;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.Map;
import org.eclipse.imagen.NullOpImage;
import org.eclipse.imagen.PlanarImage;

/**
 * A class which merely wraps another <code>PlanarImage</code> but uses a supplied <code>AffineTransform</code> object
 * for point mapping.
 */
public class PointMapperOpImage extends NullOpImage {
    private AffineTransform transform;
    private AffineTransform inverseTransform;

    public PointMapperOpImage(PlanarImage source, Map configuration, AffineTransform transform)
            throws NoninvertibleTransformException {
        super(source, null, configuration, OP_COMPUTE_BOUND);

        if (transform == null) {
            throw new IllegalArgumentException("transform == null!");
        }

        this.transform = transform;
        this.inverseTransform = transform.createInverse();
    }

    public Point2D mapDestPoint(Point2D destPt, int sourceIndex) {
        if (sourceIndex != 0) {
            throw new IndexOutOfBoundsException("sourceIndex != 0!");
        }

        return inverseTransform.transform(destPt, null);
    }

    public Point2D mapSourcePoint(Point2D sourcePt, int sourceIndex) {
        if (sourceIndex != 0) {
            throw new IndexOutOfBoundsException("sourceIndex != 0!");
        }

        return inverseTransform.transform(sourcePt, null);
    }

    public synchronized void dispose() {
        getSourceImage(0).dispose();
        super.dispose();
    }
}
