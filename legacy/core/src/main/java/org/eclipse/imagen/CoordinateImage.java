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
 * A class representing an image that is associated with a coordinate. This class is used with <code>ImageStack</code>.
 *
 * @see ImageStack
 * @deprecated Use <code>AttributedImage</code> instead.
 */
public class CoordinateImage {

    /** The image. */
    public PlanarImage image;

    /**
     * The coordinate associated with the image. The type of this parameter is <code>Object</code> so that the
     * application may choose any class to represent a coordinate based on the individual's needs.
     */
    public Object coordinate;

    /**
     * Constructor.
     *
     * @throws IllegalArgumentException if <code>pi</code> is <code>null</code>.
     * @throws IllegalArgumentException if <code>c</code> is <code>null</code>.
     */
    public CoordinateImage(PlanarImage pi, Object c) {
        if (pi == null || c == null) {
            throw new IllegalArgumentException(ImageNLegacy18N.getString("Generic0"));
        }

        image = pi;
        coordinate = c;
    }
}
