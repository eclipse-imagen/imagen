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

/**
 * A class representing an image that is associated with a time stamp and a camera position. This class is used with
 * <code>ImageSequence</code>.
 *
 * <p>This class is equivalent to an <code>AttributedImage</code> with an attribute defined as:
 *
 * <pre>{@code
 * public class SequentialAttribute {
 *     protected Object position;
 *     protected Float timeStamp;
 *
 *     public SequentialAttribute(Object position, float timeStamp);
 *
 *     public Object getPosition();
 *     public float getTimeStamp();
 *
 *     public boolean equals(Object o) {
 *         if(o instanceof SequentialAttribute) {
 *        SequentialAttribute sa = (SequentialAttribute)o;
 *        return sa.getPosition().equals(position) &&
 *               sa.getTimeStamp().equals(timeStamp);
 *    }
 *         return false;
 *     }
 * }
 * }</pre>
 *
 * @see ImageSequence
 * @deprecated Use <code>AttributedImage</code> instead.
 */
public class SequentialImage {

    /** The image. */
    public PlanarImage image;

    /** The time stamp associated with the image. */
    public float timeStamp;

    /**
     * The camera position associated with the image. The type of this parameter is <code>Object</code> so that the
     * application may choose any class to represent a camera position based on the individual's needs.
     */
    public Object cameraPosition;

    /**
     * Constructor.
     *
     * @param pi The specified planar image.
     * @param ts The time stamp, as a float.
     * @param cp The camera position object.
     * @throws IllegalArgumentException if <code>pi</code> is <code>null</code>.
     */
    public SequentialImage(PlanarImage pi, float ts, Object cp) {
        if (pi == null) {
            throw new IllegalArgumentException(ImageNLegacy18N.getString("Generic0"));
        }

        image = pi;
        timeStamp = ts;
        cameraPosition = cp;
    }
}
