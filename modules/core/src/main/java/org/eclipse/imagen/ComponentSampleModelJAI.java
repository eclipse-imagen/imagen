/* Copyright (c) 2025 Andrea Aime and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution and is available at
 * http://www.opensource.org/licenses/apache2.0.php.
 */
package org.eclipse.imagen;

/**
 * This class is a subclass of ComponentSampleModelImageN to work around serialization issues. It is deprecated and
 * you're not supposed to use it anymore. It is only kept around for backward compatibility. Won't be removed anytime
 * soon, in order to support long term serialization of ImageLayout descriptions.
 */
@Deprecated
public class ComponentSampleModelJAI extends ComponentSampleModelImageN {
    public ComponentSampleModelJAI(int dataType, int w, int h, int pixelStride, int scanlineStride, int[] bandOffsets) {
        super(dataType, w, h, pixelStride, scanlineStride, bandOffsets);
    }

    public ComponentSampleModelJAI(
            int dataType, int w, int h, int pixelStride, int scanlineStride, int[] bankIndices, int[] bandOffsets) {
        super(dataType, w, h, pixelStride, scanlineStride, bankIndices, bandOffsets);
    }
}
