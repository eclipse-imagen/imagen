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

package org.eclipse.imagen.media.codec;

/**
 * An instance of <code>ImageDecodeParam</code> for decoding images in the FlashPIX format.
 *
 * <p><b> This class is not a committed part of the ImageN API. It may be removed or changed in future releases of
 * ImageN.</b>
 */
public class FPXDecodeParam implements ImageDecodeParam {

    private int resolution = -1;

    /** Constructs a default instance of <code>FPXDecodeParam</code>. */
    public FPXDecodeParam() {}

    /**
     * Constructs an instance of <code>FPXDecodeParam</code> to decode a given resolution.
     *
     * @param resolution The resolution number to be decoded.
     */
    public FPXDecodeParam(int resolution) {
        this.resolution = resolution;
    }

    /**
     * Sets the resolution to be decoded.
     *
     * @param resolution The resolution number to be decoded.
     */
    public void setResolution(int resolution) {
        this.resolution = resolution;
    }

    /** Returns the resolution to be decoded. */
    public int getResolution() {
        return resolution;
    }
}
