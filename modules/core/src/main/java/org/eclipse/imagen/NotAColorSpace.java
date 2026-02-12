/* Copyright (c) 2025 Andrea Aime and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution and is available at
 * http://www.opensource.org/licenses/apache2.0.php.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.eclipse.imagen;

import java.awt.color.ColorSpace;

/**
 * A dummy {@code ColorSpace} to enable {@code ColorModel} for image data which do not have an innate color
 * representation.
 *
 * <p>Conversion methods are implemented to simply copy the color values from the source to the destination without any
 * transformation.
 */
public class NotAColorSpace extends ColorSpace {

    public static final int UNKNOWN_TYPE = -1;

    private static int getType(int numComponents) {
        if (numComponents < 1) {
            throw new IllegalArgumentException("A color space must have at least one component.");
        }

        if (numComponents == 1) {
            return TYPE_GRAY;
        } else if (numComponents <= 15) {
            // ColorSpace.TYPE_2CLR = 12, works up to TYPE_FCLR = 25
            return numComponents + 10;
        } else {
            return UNKNOWN_TYPE;
        }
    }

    /**
     * Constructs a {@code NotAColorSpace} with the specified number of components.
     *
     * @param numComponents the number of components in this color space
     * @throws IllegalArgumentException if {@code numComponents} is less than 1
     */
    public NotAColorSpace(int numComponents) {
        super(getType(numComponents), numComponents);
    }

    private void validateArrayLength(float[] array, int requiredLength, String name) {
        if (array == null) {
            throw new NullPointerException(name + " must not be null.");
        }
        if (array.length < requiredLength) {
            throw new ArrayIndexOutOfBoundsException(name + ".length < " + requiredLength);
        }
    }

    /**
     * Copies up to {@code maxComponents} from {@code src} into a newly allocated {@code dst}.
     *
     * @return the filled destination array
     */
    private float[] copyComponents(float[] src, float[] dst, int maxComponents) {
        final int n = Math.min(maxComponents, Math.min(src.length, dst.length));
        // for small n a loop is more efficient than System.arraycopy
        for (int i = 0; i < n; i++) {
            dst[i] = src[i];
        }
        return dst;
    }

    @Override
    public float[] toRGB(float[] colorvalue) {
        validateArrayLength(colorvalue, getNumComponents(), "colorvalue");
        return copyComponents(colorvalue, new float[3], 3);
    }

    @Override
    public float[] fromRGB(float[] rgbvalue) {
        validateArrayLength(rgbvalue, 3, "rgbvalue");
        return copyComponents(rgbvalue, new float[getNumComponents()], 3);
    }

    @Override
    public float[] toCIEXYZ(float[] colorvalue) {
        validateArrayLength(colorvalue, getNumComponents(), "colorvalue");
        return copyComponents(colorvalue, new float[3], 3);
    }

    @Override
    public float[] fromCIEXYZ(float[] xyzvalue) {
        validateArrayLength(xyzvalue, 3, "xyzvalue");
        return copyComponents(xyzvalue, new float[getNumComponents()], 3);
    }
}
