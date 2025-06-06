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

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Locale;
import java.util.MissingResourceException;
import org.eclipse.imagen.media.range.Range;

/**
 * Convenience implementation of the {@link Domain1D} interface.
 *
 * @author Simone Giannecchini
 * @source $URL$
 */
public class DefaultDomain1D<E extends DefaultDomainElement1D> extends AbstractList<E> implements Domain1D<E> {

    /*
     * (non-Javadoc)
     *
     * @see Domain1D#getName()
     */
    public synchronized String getName() {
        if (name == null) {
            final StringBuffer buffer = new StringBuffer(30);
            final Locale locale = Locale.getDefault();
            if (main != null) {
                buffer.append(main.getName());
            } else {
                buffer.append('(');
                buffer.append("Untitled");
                buffer.append(')');
            }
            name = buffer.toString();
        }
        return name;
    }

    /*
     * (non-Javadoc)
     *
     * @see Domain1D#getRange()
     */
    public Range getApproximateDomainRange() {
        synchronized (elements) {
            // @todo TODO should I include the NaN value?
            if (range == null) {
                Range range = null;
                for (E element : elements) {
                    final Range extent = element.getRange();
                    if (!Double.isNaN(extent.getMin().doubleValue())
                            && !Double.isNaN(extent.getMax().doubleValue())) {
                        if (range != null) { // TODO FIXME ADD RANGE UNION
                            range = range.union(extent);
                        } else {
                            range = extent;
                        }
                    }
                }
                this.range = range;
            }
            return range;
        }
    }

    /** The list of elements. This list most be sorted in increasing order of left range element. */
    private E[] elements;

    /**
     * {@code true} if there is gaps between elements, or {@code false} otherwise. A gap is found if for example the
     * range of value is [-9999 .. -9999] for the first domain element and [0 .. 1000] for the second one.
     */
    private boolean hasGaps;

    /**
     * The "main" domain element, or {@code null} if there is none. The main domain element is the quantitative domain
     * element with the widest range of sample values.
     */
    private E main;

    /**
     * List of {@link #inputMinimum} values for each domain element in {@link #elements} . This array
     * <strong>must</strong> be in increasing order. Actually, this is the need to sort this array that determines the
     * element order in {@link #elements} .
     */
    private double[] minimums;

    /**
     * The name for this domain element list. Will be constructed only when first needed.
     *
     * @see #getName
     */
    private String name;

    /**
     * The range of values in this domain element list. This is the union of the range of values of every elements,
     * excluding {@code NaN} values. This field will be computed only when first requested.
     */
    private Range range;

    /** Lazily initialized hashcode for this class */
    private int hashCode = -1;

    /**
     * Constructor for {@link DefaultDomain1D}.
     *
     * @param inDomainElements {@link DomainElement1D} objects that make up this list.
     */
    public DefaultDomain1D(E[] inDomainElements) {
        init(inDomainElements);
    }

    /**
     * @param inDomainElements
     * @throws IllegalArgumentException
     * @throws MissingResourceException
     */
    @SuppressWarnings("unchecked")
    private void init(E[] inDomainElements) throws IllegalArgumentException, MissingResourceException {

        // /////////////////////////////////////////////////////////////////////
        //
        // input checks
        //
        // /////////////////////////////////////////////////////////////////////
        PiecewiseUtilities.ensureNonNull("DomainElement1D[]", inDomainElements);

        // @todo TODOCHECK ME
        if (inDomainElements == null)
            this.elements =
                    (E[]) new DefaultDomainElement1D[] {new DefaultPassthroughPiecewiseTransform1DElement("p0")};
        else this.elements = inDomainElements.clone();

        // /////////////////////////////////////////////////////////////////////
        //
        // Sort the input elements.
        //
        // /////////////////////////////////////////////////////////////////////
        Arrays.sort(this.elements);

        // /////////////////////////////////////////////////////////////////////
        //
        // Construct the array of minimum values. During the loop, we make sure
        // there is no overlapping in input and output.
        //
        // /////////////////////////////////////////////////////////////////////
        hasGaps = false;
        minimums = new double[elements.length];
        for (int i = 0; i < elements.length; i++) {
            final DefaultDomainElement1D c = elements[i];
            final double inMinimum = minimums[i] = c.getInputMinimum();
            if (i != 0) {
                assert !(inMinimum < minimums[i - 1]) : inMinimum;
                // Use '!' to accept NaN.
                final DefaultDomainElement1D previous = elements[i - 1];
                if (PiecewiseUtilities.compare(inMinimum, previous.getInputMaximum()) <= 0) {
                    PiecewiseUtilities.domainElementsOverlap(elements, i);
                }
                // Check if there is a gap between this domain element and the
                // previous one.
                if (!Double.isNaN(inMinimum) // TODO FIXME ADD POSSIBILITY TO GET THE MAXIMUM NOT INCLUDED/INCLUDED
                        && inMinimum
                                != ((Range) previous.getRange()).getMax(false).doubleValue()) {
                    hasGaps = true;
                }
            }
        }

        /*
         * Search for what seems to be the "main" domain element. This loop looks for the quantitative domain element (if there is one) with the
         * widest range of sample values.
         */
        double range = 0;
        E main = null;
        for (int i = elements.length; --i >= 0; ) {
            final E candidate = elements[i];
            if (Double.isInfinite(candidate.getInputMinimum()) && Double.isInfinite(candidate.getInputMaximum())) {
                range = Double.POSITIVE_INFINITY;
                main = candidate;
                continue;
            }
            final double candidateRange = candidate.getInputMaximum() - candidate.getInputMinimum();
            if (candidateRange >= range) {
                range = candidateRange;
                main = candidate;
            }
        }
        this.main = main;

        // postcondition
        assert PiecewiseUtilities.isSorted(elements);
    }

    /**
     * Returns the domain element of the specified sample value. If no domain element fits, then this method returns
     * {@code null}.
     *
     * @param value The value.
     * @return The domain element of the supplied value, or {@code null}.
     */
    public E findDomainElement(final double value) {

        int i = getDomainElementIndex(value);

        // //
        //
        // Checks
        //
        // //
        if (i < 0) return null;
        E domainElement1D;
        if (i > elements.length) return null;

        // //
        //
        // First of all let's check if we spotted a break point in out domains
        // element. If so the index we got is not an insertion point but it is
        // an actual domain element index. This happens when we catch precisely
        // a minimum element for a domain.
        //
        // //
        if (i < elements.length) {
            domainElement1D = elements[i];
            if (domainElement1D.contains(value)) return domainElement1D;
            // if the index was 0, unless we caught the smallest minimum we have
            // got something smaller than the leftmost domain
            if (i == 0) return null;
        }
        // //
        //
        // Ok, now we know that we did not precisely caught a minimum for a
        // domain, we therefore got an insertion point. This means that, unless
        // we have fallen into a gap we need to subtract 1 to check for
        // inclusion in the right domain.
        //
        // //
        domainElement1D = elements[i - 1];
        if (domainElement1D.contains(value)) return domainElement1D;

        // //
        //
        // Well, if we get here, we have definitely fallen into a gap or the
        // value is beyond the limits of the last domain, too bad....
        //
        // //
        assert i >= elements.length || hasGaps : value;
        return null;
    }

    /**
     * @param sample
     * @return
     */
    private int getDomainElementIndex(final double sample) {
        int i = -1;
        // Special 'binarySearch' for NaN
        i = PiecewiseUtilities.binarySearch(minimums, sample);

        if (i >= 0) {
            // The value is exactly equals to one of minimum,
            // or is one of NaN values. There is nothing else to do.
            assert Double.doubleToRawLongBits(sample) == Double.doubleToRawLongBits(minimums[i]);
            return i;
        }

        assert i == Arrays.binarySearch(minimums, sample) : i;
        // 'binarySearch' found the index of "insertion point" (-(insertion
        // point) - 1). The
        // insertion point is defined as the point at which the key would be
        // inserted into the list: the index of the first element greater than
        // the key, or list.size(), if all elements in the list are less than
        // the specified key. Note that this guarantees that the return value
        // will be >= 0 if and only if the key is found.
        i = -i - 1;
        return i;
    }

    // ////////////////////////////////////////////////////////////////////////////////////////
    // ////// ////////
    // ////// I M P L E M E N T A T I O N O F List I N T E R F A C E ////////
    // ////// ////////
    // ////////////////////////////////////////////////////////////////////////////////////////
    /** Returns the number of elements in this list. */
    @Override
    public int size() {
        return elements.length;
    }

    /** Returns the element at the specified position in this list. */
    @Override
    public E get(final int i) {
        return elements[i];
    }

    /** Returns all elements in this {@code }. */
    @Override
    public Object[] toArray() {
        return (DomainElement1D[]) elements.clone();
    }

    /**
     * Compares the specified object with this domain element list for equality. If the two objects are instances of the
     * {@link DefaultDomain1D} class, then the test check for the equality of the single elements.
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(final Object object) {
        if (this == object) return true;
        if (!(object instanceof DefaultDomain1D)) return false;
        final DefaultDomain1D that = (DefaultDomain1D) object;
        if (getEquivalenceClass() != that.getEquivalenceClass()) return false;
        if (!this.getName().equals(that.getName())) return false;
        if (!this.getApproximateDomainRange().equals(that.getApproximateDomainRange())) return false;
        if (Arrays.equals(this.elements, that.elements)) {
            assert Arrays.equals(this.minimums, that.minimums);
            return true;
        }
        return false;
    }

    protected Class<?> getEquivalenceClass() {
        return DefaultDomain1D.class;
    }

    /*
     * (non-Javadoc)
     *
     * @see Domain1D#hasGaps()
     */
    public boolean hasGaps() {
        return hasGaps;
    }

    /**
     * Return what seems to be the main {@link DomainElement1D} for this list.
     *
     * @return what seems to be the main {@link DomainElement1D} for this list.
     */
    public E getMain() {
        return main;
    }

    /** @return */
    public double[] getMinimums() {
        return (double[]) minimums.clone();
    }

    @Override
    public int hashCode() {
        if (hashCode < 0) {
            int result = PiecewiseUtilities.deepHashCode(elements);
            result = PiecewiseUtilities.deepHashCode(minimums);
            result = PiecewiseUtilities.hash(getName(), result);
            hashCode = PiecewiseUtilities.hash(getApproximateDomainRange(), hashCode);
        }
        return hashCode;
    }
}
