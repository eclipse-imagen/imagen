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
 * An instance of <code>ImageEncodeParam</code> for encoding images in the BMP format.
 *
 * <p>This class allows for the specification of various parameters while encoding (writing) a BMP format image file. By
 * default, the version used is VERSION_3, no compression is used, and the data layout is bottom_up, such that the
 * pixels are stored in bottom-up order, the first scanline being stored last.
 *
 * <p><b> This class is not a committed part of the ImageN API. It may be removed or changed in future releases of
 * ImageN.</b>
 */
public class BMPEncodeParam implements ImageEncodeParam {

    // version constants

    /** Constant for BMP version 2. */
    public static final int VERSION_2 = 0;

    /** Constant for BMP version 3. */
    public static final int VERSION_3 = 1;

    /** Constant for BMP version 4. */
    public static final int VERSION_4 = 2;

    // Default values
    private int version = VERSION_3;
    private boolean compressed = false;
    private boolean topDown = false;

    /** Constructs an BMPEncodeParam object with default values for parameters. */
    public BMPEncodeParam() {}

    /** Sets the BMP version to be used. */
    public void setVersion(int versionNumber) {
        checkVersion(versionNumber);
        this.version = versionNumber;
    }

    /** Returns the BMP version to be used. */
    public int getVersion() {
        return version;
    }

    /** If set, the data will be written out compressed, if possible. */
    public void setCompressed(boolean compressed) {
        this.compressed = compressed;
    }

    /** Returns the value of the parameter <code>compressed</code>. */
    public boolean isCompressed() {
        return compressed;
    }

    /** If set, the data will be written out in a top-down manner, the first scanline being written first. */
    public void setTopDown(boolean topDown) {
        this.topDown = topDown;
    }

    /** Returns the value of the <code>topDown</code> parameter. */
    public boolean isTopDown() {
        return topDown;
    }

    // Method to check whether we can handle the given version.
    private void checkVersion(int versionNumber) {
        if (!(versionNumber == VERSION_2 || versionNumber == VERSION_3 || versionNumber == VERSION_4)) {
            throw new RuntimeException(JaiI18N.getString("BMPEncodeParam0"));
        }
    }
}
