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

package org.eclipse.imagen;

/**
 * A class which associates a <code>PlanarImage</code> with an attribute of unspecified type. The class is itself a
 * <code>PlanarImage</code> equivalent to the one which it wraps.
 */
public class AttributedImage extends RenderedImageAdapter {
    /** The attribute associated with the image. */
    protected Object attribute;

    /**
     * Constructs an <code>AttributedImage</code>. The attribute parameter may be <code>null</code>
     *
     * @throws IllegalArgumentException if <code>theImage</code> is <code>null</code>.
     */
    public AttributedImage(PlanarImage image, Object attribute) {
        super(image);
        this.attribute = attribute;
    }

    /** Retrieves the wrapped image. */
    public PlanarImage getImage() {
        return (PlanarImage) theImage;
    }

    /** Stores the attribute. */
    public void setAttribute(Object attribute) {
        this.attribute = attribute;
    }

    /** Retrieves the attribute. */
    public Object getAttribute() {
        return attribute;
    }

    /**
     * Tests for equality. The parameter <code>Object</code> must be an <code>AttributedImage</code> the image and
     * attribute of which are equal those of this object according to the <code>equals()</code> methods of the image and
     * attribute of this image, respectively. Attributes are also considered equal if they are both <code>null</code>.
     */
    public boolean equals(Object o) {
        if (o != null && o instanceof AttributedImage) {
            AttributedImage ai = (AttributedImage) o;
            Object a = ai.getAttribute();
            return getImage().equals(ai.getImage())
                    && (attribute == null ? a == null : ((a != null) && attribute.equals(a)));
        }

        return false;
    }

    /** toString() method. */
    public String toString() {
        return "Attribute=(" + getAttribute() + ")  Image=" + getImage();
    }
}
