/* JAI-Ext - OpenSource Java Advanced Image Extensions Library
 *    http://www.geo-solutions.it/
 *    Copyright 2014 GeoSolutions
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 
 * http://www.apache.org/licenses/LICENSE-2.0
 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.eclipse.imagen.media.lookup;

import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import org.eclipse.imagen.LookupTableImageN;

public class LookupTableWrapper extends LookupTable {

    private LookupTableImageN lut;

    public LookupTableWrapper(LookupTableImageN lut) {
        // Fake constructor
        super(new byte[1]);
        this.lut = lut;
    }

    @Override
    protected void lookup(Raster source, WritableRaster dst, Rectangle rect, Raster roi) {
        lut.lookup(source, dst, rect);
    }

    @Override
    public int getNumBands() {
        return lut.getNumBands();
    }
}
