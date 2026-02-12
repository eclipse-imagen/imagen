/* JAI-Ext - OpenSource Java Advanced Image Extensions Library
 *    http://www.geo-solutions.it/
 *    Copyright 2014 GeoSolutions
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.eclipse.imagen.media.range;

import java.util.Arrays;

/**
 * Simple class containing the NoData value/s to pass as a property object
 *
 * @author Nicola Lagomarsini GeoSolutions
 */
public class NoDataContainer {

    public static final String GC_NODATA = "GC_NODATA";

    private Range nodataR;

    private double[] array;

    private double singleValue;

    public NoDataContainer(NoDataContainer other) {
        this.nodataR = other.nodataR;
        this.singleValue = other.singleValue;
        this.array = other.array;
    }

    public NoDataContainer(Range nodataR) {
        this.nodataR = nodataR;
        this.singleValue = nodataR.getMin(true).doubleValue();
        this.array = new double[] {singleValue};
    }

    public NoDataContainer(double[] array) {
        this.singleValue = array[0];
        this.nodataR = RangeFactory.create(singleValue, singleValue);
        this.array = array;
    }

    public NoDataContainer(double singleValue) {
        this.nodataR = RangeFactory.create(singleValue, singleValue);
        this.singleValue = singleValue;
        this.array = new double[] {singleValue};
    }

    public double getAsSingleValue() {
        return singleValue;
    }

    public double[] getAsArray() {
        return array;
    }

    public Range getAsRange() {
        return nodataR;
    }

    @Override
    public String toString() {
        return "NoDataContainer [nodataR=" + nodataR + ", array=" + Arrays.toString(array) + ", singleValue="
                + singleValue + "]";
    }
}
