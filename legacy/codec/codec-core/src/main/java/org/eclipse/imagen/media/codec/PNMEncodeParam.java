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
package org.eclipse.imagen.media.codec;

/**
 * An instance of <code>ImageEncodeParam</code> for encoding images in the PNM format.
 *
 * <p>This class allows for the specification of whether to encode in the ASCII or raw variants of the PBM, PGM, and PPM
 * formats. By default, raw encoding is used.
 *
 * <p><b> This class is not a committed part of the ImageN API. It may be removed or changed in future releases of
 * ImageN.</b>
 */
public class PNMEncodeParam implements ImageEncodeParam {

    private boolean raw = true;

    /** Constructs a PNMEncodeParam object with default values for parameters. */
    public PNMEncodeParam() {}

    /**
     * Sets the representation to be used. If the <code>raw</code> parameter is <code>true</code>, raw encoding will be
     * used; otherwise ASCII encoding will be used.
     *
     * @param raw <code>true</code> if raw format is to be used.
     */
    public void setRaw(boolean raw) {
        this.raw = raw;
    }

    /** Returns the value of the <code>raw</code> parameter. */
    public boolean getRaw() {
        return raw;
    }
}
