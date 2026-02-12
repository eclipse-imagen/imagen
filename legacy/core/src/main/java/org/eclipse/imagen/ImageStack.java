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
package org.eclipse.imagen;

import java.util.Collection;
import java.util.Iterator;

/**
 * A class representing a stack of images, each associated with a spatial position/orientation defined in a common
 * coordinate system. The images are of the type <code>org.eclipse.imagen.PlanarImage</code>; the coordinates are of the
 * type <code>java.lang.Object</code>. The tuple (image, coordinate) is represented by class <code>
 * org.eclipse.imagen.CoordinateImage</code>.
 *
 * <p>This class can be used to represent medical or geophysical images.
 *
 * @see PlanarImage
 * @deprecated Use <code>AttributedImageCollection</code> instead.
 */
public abstract class ImageStack extends CollectionImage {

    /** The default constructor. */
    protected ImageStack() {}

    /**
     * Constructor.
     *
     * @param images A collection of <code>CoordinateImage</code>.
     * @throws IllegalArgumentException if <code>images</code> is <code>null</code>.
     */
    public ImageStack(Collection images) {
        super(images);
    }

    /**
     * Returns the image associated with the specified coordinate, or <code>null</code> if <code>c</code> is <code>null
     * </code> or if no match is found.
     *
     * @param c The specified coordinate object.
     */
    public PlanarImage getImage(Object c) {
        if (c != null) {
            Iterator iter = iterator();

            while (iter.hasNext()) {
                CoordinateImage ci = (CoordinateImage) iter.next();
                if (ci.coordinate.equals(c)) {
                    return ci.image;
                }
            }
        }

        return null;
    }

    /**
     * Returns the coordinate associated with the specified image, or <code>null</code> if <code>pi</code> is <code>null
     * </code> or if no match is found.
     *
     * @param pi The specified planar image.
     */
    public Object getCoordinate(PlanarImage pi) {
        if (pi != null) {
            Iterator iter = iterator();

            while (iter.hasNext()) {
                CoordinateImage ci = (CoordinateImage) iter.next();
                if (ci.image.equals(pi)) {
                    return ci.coordinate;
                }
            }
        }

        return null;
    }

    /**
     * Adds a <code>CoordinateImage</code> to this collection. If the specified image is <code>null</code>, it is not
     * added to the collection.
     *
     * @return true if and only if the <code>CoordinateImage</code> is added to the collection.
     */
    public boolean add(Object o) {
        if (o != null && o instanceof CoordinateImage) {
            return super.add(o);
        } else {
            return false;
        }
    }

    /**
     * Removes the <code>CoordinateImage</code> that contains the specified image from this collection.
     *
     * @param pi The specified planar image.
     * @return true if and only if a <code>CoordinateImage</code> containing the specified image is removed from the
     *     collection.
     */
    public boolean remove(PlanarImage pi) {
        if (pi != null) {
            Iterator iter = iterator();

            while (iter.hasNext()) {
                CoordinateImage ci = (CoordinateImage) iter.next();
                if (ci.image.equals(pi)) {
                    return super.remove(ci);
                }
            }
        }

        return false;
    }

    /**
     * Removes the <code>CoordinateImage</code> that contains the specified coordinate from this collection.
     *
     * @param c The specified coordinate object.
     * @return true if and only if a <code>CoordinateImage</code> containing the specified coordinate is removed from
     *     the collection.
     */
    public boolean remove(Object c) {
        if (c != null) {
            Iterator iter = iterator();

            while (iter.hasNext()) {
                CoordinateImage ci = (CoordinateImage) iter.next();
                if (ci.coordinate.equals(c)) {
                    return super.remove(ci);
                }
            }
        }

        return false;
    }
}
