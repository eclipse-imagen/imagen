/* Copyright (c) 2025 Andrea Aime and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution and is available at
 * http://www.opensource.org/licenses/apache2.0.php.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.eclipse.imagen.media.colorconvert;

import org.eclipse.imagen.ColorSpaceImageN;

/**
 * This class is a subclass of ColorSpaceImageNExtWrapper to work around serialization issues. It is deprecated and
 * you're not supposed to use it anymore. It is only kept around for backward compatibility. Won't be removed anytime
 * soon, in order to support long term serialization of ImageLayout descriptions.
 */
@Deprecated
public class ColorSpaceJAIExtWrapper extends ColorSpaceImageNExtWrapper {

    public ColorSpaceJAIExtWrapper(ColorSpaceImageN cs) {
        super(cs);
    }
}
