/* JAI-Ext - OpenSource Java Advanced Image Extensions Library
*    http://www.geo-solutions.it/
*    Copyright 2014 GeoSolutions


* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at

* http://www.apache.org/licenses/LICENSE-2.0

* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.eclipse.imagen.media.piecewise;

import org.eclipse.imagen.media.range.Range;
import org.eclipse.imagen.media.range.RangeFactory;

/**
 * This class implements the {@link DomainElement1D} interface in order to provide basic capabilities for
 * {@link DomainElement1D} subclasses.
 *
 * @author Simone Giannecchini, GeoSolutions.
 * @source $URL$
 */
public class DefaultDomainElement1D implements DomainElement1D {

    /** UID */
    private static final long serialVersionUID = -2520449231656622013L;

    /**
     * Base implementation for the {@link Comparable#compareTo(Object)} method. This method will work only if the
     * provided input object is a {@link DefaultDomainElement1D}.
     *
     * <p>Two {@link DefaultDomainElement1D}s are compared by comparing their lower bounds in order to establish an
     * order between them.
     *
     * @return a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than
     *     the specified object.
     * @throws ClassCastException if the specified object's type prevents it from being compared to this Object.
     * @see Comparable#compareTo(Object)
     * @todo we could improve this check for the specific case when the two minimums are equal
     */
    public int compareTo(DomainElement1D o) {
        if (o instanceof DefaultDomainElement1D)
            return new Double(inputMinimum).compareTo(((DefaultDomainElement1D) o).inputMinimum);
        return new Double(inputMinimum).compareTo(o.getRange().getMin().doubleValue());
    }

    /**
     * Implementation of {@link Object#equals(Object)} for {@link DomainElement1D}s.
     *
     * <p>Two {@link DefaultDomainElement1D}s are considered to be equal if they have the same inputr range and the same
     * name.
     *
     * @param obj the reference object with which to compare.
     * @return <code>true</code> if this object is the same as the obj argument; <code>false</code> otherwise.
     * @see Object#equals(Object)
     */
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof DefaultDomainElement1D) return false;
        final DefaultDomainElement1D that = (DefaultDomainElement1D) obj;
        if (getEquivalenceClass() != that.getEquivalenceClass()) return false;
        if (!PiecewiseUtilities.equals(inputMinimum, that.inputMinimum)) return false;
        if (!PiecewiseUtilities.equals(inputMaximum, that.inputMaximum)) return false;
        if (!this.name.equals(that.name)) return false;
        if (!this.range.equals(that.range)) return false;
        return true;
    }

    protected Class<?> getEquivalenceClass() {
        return DefaultDomainElement1D.class;
    }

    /** @see DomainElement1D#contains(Number) */
    public boolean contains(Number value) {
        return range.containsN(value);
    }

    /** @see DomainElement1D#contains(NumberRange) */
    public boolean contains(Range range) {
        return this.range.contains(range);
    }

    /** @see DomainElement1D#contains(double) */
    public boolean contains(double value) {
        return (value <= inputMaximum && value >= inputMinimum)
                || (Double.doubleToRawLongBits(value) == Double.doubleToRawLongBits(inputMinimum));
    }

    /**
     * The domain element name.
     *
     * @uml.property name="name"
     */
    private String name;

    /**
     * The minimal sample value (inclusive). This domain element is made of all values in the range {@code inputMinimum}
     * to {@code inputMaximum} inclusive.
     *
     * @uml.property name="inputMinimum"
     */
    private double inputMinimum;

    /**
     * The maximal sample value (inclusive). This domain element is made of all values in the range {@code inputMinimum}
     * to {@code inputMaximum} inclusive.
     *
     * @uml.property name="inputMaximum"
     */
    private double inputMaximum;

    /**
     * The range of values {@code [inputMinimum..maximum]} . May be computed only when first requested, or may be
     * user-supplied .
     *
     * @uml.property name="range"
     */
    private Range range;

    /** Is lower input bound infinite? */
    private boolean inputMinimumInf;

    /** Is uper input bound infinite? */
    private boolean inputMaximumInf;

    /**
     * Is upper input bound NaN?
     *
     * @uml.property name="inputMaximumNaN"
     */
    private boolean inputMaximumNaN;

    /**
     * Is lower input bound NaN?
     *
     * @uml.property name="inputMinimumNaN"
     */
    private boolean inputMinimumNaN;

    private int hashCode = -1;

    /**
     * Abstract domain element constructor.
     *
     * <p>It builds up an {@link DefaultDomainElement1D} with the provided name and input range.
     *
     * @param name for this {@link DefaultDomainElement1D}.
     * @param range for this {@link DefaultDomainElement1D}.
     * @throws IllegalArgumentException in case one of the input arguments is invalid.
     */
    public DefaultDomainElement1D(final CharSequence name, final Range inputRange) throws IllegalArgumentException {
        // /////////////////////////////////////////////////////////////////////
        //
        // Initial checks
        //
        // /////////////////////////////////////////////////////////////////////
        PiecewiseUtilities.ensureNonNull("name", name);
        PiecewiseUtilities.ensureNonNull("range", inputRange);

        // /////////////////////////////////////////////////////////////////////
        //
        // Initialise fields
        //
        // /////////////////////////////////////////////////////////////////////
        this.name = name + "";
        this.range = RangeFactory.convertToDoubleRange(inputRange);
        Class<? extends Number> type = inputRange.getDataType().getClassValue();
        boolean minInc = inputRange.isMinIncluded();
        boolean maxInc = inputRange.isMaxIncluded();
        final double tempMin = inputRange.getMin().doubleValue();
        final double tempMax = inputRange.getMax().doubleValue();
        if (Double.isInfinite(tempMin)) {
            this.inputMinimum = tempMin;
            inputMinimumInf = true;
        } else
            this.inputMinimum =
                    PiecewiseUtilities.doubleValue(type, inputRange.getMin().doubleValue(), minInc ? 0 : +1);
        if (Double.isInfinite(tempMax)) {
            this.inputMaximum = tempMax;
            inputMaximumInf = true;
        } else
            this.inputMaximum =
                    PiecewiseUtilities.doubleValue(type, inputRange.getMax().doubleValue(), maxInc ? 0 : -1);
        // /////////////////////////////////////////////////////////////////////
        //
        // Checks
        //
        // /////////////////////////////////////////////////////////////////////

        // //
        //
        // only one input values is NaN
        //
        // //
        inputMinimumNaN = Double.isNaN(inputMinimum);
        inputMaximumNaN = Double.isNaN(inputMaximum);
        if ((inputMinimumNaN && !inputMaximumNaN) || (!inputMinimumNaN && inputMaximumNaN))
            throw new IllegalArgumentException("Bad Range format: " + inputRange.getMin() + "/ " + inputRange.getMax());
    }

    /**
     * Returns a hash value for this domain element. This value need not remain consistent between different
     * implementations of the same class.
     */
    public int hashCode() {
        if (hashCode >= 0) return hashCode;
        hashCode = 37;
        hashCode = PiecewiseUtilities.hash(name, hashCode);
        hashCode = PiecewiseUtilities.hash(range, hashCode);
        hashCode = PiecewiseUtilities.hash(inputMaximum, hashCode);
        hashCode = PiecewiseUtilities.hash(inputMinimum, hashCode);
        return hashCode;
    }

    /**
     * Getter method for this {@link DomainElement1D} 's name.
     *
     * @return this {@link DefaultDomainElement1D} 's name.
     * @uml.property name="name"
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the upper bound of the range where this {@link DomainElement1D} is defined.
     *
     * <p><strong>This is just a convenience method</strong>
     *
     * @return the upper bound of the range where this {@link DomainElement1D} is defined.
     * @uml.property name="inputMaximum"
     */
    public double getInputMaximum() {
        return inputMaximum;
    }

    /**
     * Tells us if the upper bound of the range where this {@link DomainElement1D} is defined is an infinite number
     *
     * <p><strong>This is just a convenience method</strong>
     *
     * @return <code>true</code> if the upper bound of the range where this {@link DomainElement1D} is defined is
     *     infinite, <code>false</code> otherwise.
     */
    public boolean isInputMaximumInfinite() {
        return inputMaximumInf;
    }

    /**
     * Tells us if the upper bound of the range where this {@link DomainElement1D} is defined is NaN.
     *
     * <p><strong>This is just a convenience method</strong>
     *
     * @return <code>true</code> if the upper bound of the range where this {@link DomainElement1D} is defined is NaN,
     *     <code>false</code> otherwise.
     * @uml.property name="inputMaximumNaN"
     */
    public boolean isInputMaximumNaN() {
        return inputMaximumNaN;
    }

    /**
     * Retrieves the lower bound of the range where this {@link DomainElement1D} is defined.
     *
     * <p><strong>This is just a convenience method</strong>
     *
     * @return the lower bound of the range where this {@link DomainElement1D} is defined.
     * @uml.property name="inputMinimum"
     */
    public double getInputMinimum() {
        return inputMinimum;
    }

    /**
     * Tells us if the lower bound of the range where this {@link DomainElement1D} is defined is an infinite number.
     *
     * <p><strong>This is just a convenience method</strong>
     *
     * @return <code>true</code> if the lower bound of the range where this {@link DomainElement1D} is defined is
     *     infinite, <code>false</code> otherwise.
     */
    public boolean isInputMinimumInfinite() {
        return inputMinimumInf;
    }

    /**
     * This method retrieves the input range.
     *
     * @return the input range.
     * @uml.property name="range"
     */
    public Range getRange() {
        return range;
    }

    /**
     * Tells us if the lower bound of the range where this {@link DomainElement1D} is defined is NaN
     *
     * <p><strong>This is just a convenience method</strong>
     *
     * @return <code>true</code> if the lower bound of the range where this {@link DomainElement1D} is defined is NaN,
     *     <code>false</code> otherwise.
     * @uml.property name="inputMinimumNaN"
     */
    public boolean isInputMinimumNaN() {
        return inputMinimumNaN;
    }

    public String toString() {
        final StringBuilder buffer = new StringBuilder("Domain description:");
        buffer.append("\n").append("name=").append(name);
        buffer.append("\n").append("input range=").append(range);
        return buffer.toString();
    }
}
