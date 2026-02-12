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

import java.math.BigDecimal;
import java.math.BigInteger;
import org.eclipse.imagen.util.Range;

/**
 * A class that wraps a <code>Range</code> which contains numeric elements, to implement the <code>Negotiable</code>
 * interface. <code>NegotiableNumericRange</code> is a convenience class to specify a <code>Negotiable</code> parameter
 * whose valid numeric values are specified by a <code>Range</code>.
 */
public class NegotiableNumericRange implements Negotiable {

    private Range range;

    /**
     * Creates a <code>NegotiableNumericRange</code> given an <code>Range</code> containing elements of a subclass of
     * <code>Number</code>.
     *
     * @throws IllegalArgumentException if range is null.
     * @throws IllegalArgumentException if the elements of the supplied range are not a <code>Number</code> subclass.
     */
    public NegotiableNumericRange(Range range) {

        if (range == null) {
            throw new IllegalArgumentException(ImageNLegacy18N.getString("NegotiableNumericRange0"));
        }

        // If the elementClass of the supplied Range is not a subclass of
        // Number, throw an IllegalArgumentException
        if (!(Number.class.isAssignableFrom(range.getElementClass()))) {
            throw new IllegalArgumentException(ImageNLegacy18N.getString("NegotiableNumericRange1"));
        }

        this.range = range;
    }

    /**
     * Returns the <code>Range</code> of values which are currently valid for this class, null if there are no valid
     * values.
     */
    public Range getRange() {
        if (range.isEmpty()) return null;
        return range;
    }

    /**
     * Returns a <code>NegotiableNumericRange</code> that contains the range of values that are common to this <code>
     * NegotiableNumericRange</code> and the one supplied. If the supplied <code>Negotiable</code> is not a <code>
     * NegotiableNumericRange</code> with its elements being of the same <code>Class</code> as this class', or if there
     * is no common range of values, the negotiation will fail and <code>null</code> will be returned.
     *
     * @param other The <code>Negotiable</code> to negotiate with.
     */
    public Negotiable negotiate(Negotiable other) {

        if (other == null) return null;

        // if other is not an instance of NegotiableNumericRange,
        // or the element class doesn't match, negotiation fails
        if (!(other instanceof NegotiableNumericRange)) return null;

        NegotiableNumericRange otherNNRange = (NegotiableNumericRange) other;
        Range otherRange = otherNNRange.getRange();

        // If the range is null, i.e there are no valid values, then
        // negotiation fails.
        if (otherRange == null) return null;

        // If the elementClass' don't match, negotiation fails.
        if (otherRange.getElementClass() != range.getElementClass()) return null;

        Range result = range.intersect(otherRange);

        // If there are no valid values, negotiation failed.
        if (result.isEmpty()) return null;

        return new NegotiableNumericRange(result);
    }

    /**
     * Returns a single value that is valid for this <code>NegotiableNumericRange</code>. The returned value is the
     * lowest value contained in this <code>NegotiableNumericRange</code> if the range is not unbounded on the minimum
     * end, or the highest value in the range, if the range is unbounded on the minimum end. If both ends are unbounded,
     * 0 will be returned wrapped in the appropriate <code>Number</code> wrapper. Returns <code>null</code> if there are
     * no valid elements in this <code>NegotiableNumericRange</code>.
     */
    public Object getNegotiatedValue() {

        // If there are no valid values, negotiation fails.
        if (range.isEmpty()) return null;

        Number minValue = (Number) range.getMinValue();
        // Is minimum end unbounded
        if (minValue == null) {
            Number maxValue = (Number) range.getMaxValue();
            // Is maximum unbounded
            if (maxValue == null) {
                // Both ends are unbounded
                Class elementClass = range.getElementClass();
                // Have elementClass specific case statements, and get the
                // negotiated value on the datatype basis
                if (elementClass == Byte.class) {
                    return new Byte((byte) 0);
                } else if (elementClass == Short.class) {
                    return new Short((short) 0);
                } else if (elementClass == Integer.class) {
                    return new Integer(0);
                } else if (elementClass == Long.class) {
                    return new Long((long) 0);
                } else if (elementClass == Float.class) {
                    return new Float(0);
                } else if (elementClass == Double.class) {
                    return new Double(0);
                } else if (elementClass == BigInteger.class) {
                    return BigInteger.ZERO;
                } else if (elementClass == BigDecimal.class) {
                    return new BigDecimal(BigInteger.ZERO);
                }
            } else {
                return maxValue;
            }
        }

        return minValue;
    }

    /**
     * Returns the <code>Class</code> of the Object returned as the result of the negotiation. This will be a subclass
     * of <code>Number</code>.
     */
    public Class getNegotiatedValueClass() {
        return range.getElementClass();
    }
}
