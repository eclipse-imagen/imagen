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

package org.eclipse.imagen.remote;

import java.io.Serializable;

/**
 * An interface that defines objects that can be negotiated upon. Negotiation amongst objects is performed using the
 * <code>negotiate()</code> method. This method can be used to perform a chaining of successful negotiations, i.e., the
 * results of one successful negotiation can be used to negotiate with another <code>Negotiable</code> and so on. In
 * order to retrieve a single negotiated value out of the <code>Negotiable</code>, the <code>getNegotiatedValue()</code>
 * method can be used at any point during this series of negotiations.
 *
 * @since JAI 1.1
 */
public interface Negotiable extends Serializable {

    /**
     * Returns a <code>Negotiable</code> object that represents the set intersection of this <code>Negotiable</code>
     * with the given <code>Negotiable</code>. The returned <code>Negotiable</code> represents the support that is
     * common to both the <code>Negotiable</code>s. If the negotiation fails, i.e there is no common subset, null is
     * returned.
     *
     * <p>If the supplied argument is null, negotiation will fail and null will be returned, as it is not possible to
     * negotiate with a null value. It may, however, be noted that it is valid for <code>getNegotiatedValue()</code> to
     * return null, i.e the single value result of the negotiation can be null.
     *
     * @param other The <code>Negotiable</code> object to negotiate with.
     * @returns The <code>Negotiable</code> that represents the subset common to this and the given <code>Negotiable
     *     </code>. <code>null</code> is returned if there is no common subset.
     */
    Negotiable negotiate(Negotiable other);

    /**
     * Returns a value that is valid for this <code>Negotiable</code>. If more than one value is valid for this <code>
     * Negotiable</code>, this method can choose one arbitrarily, e.g. picking the first one or by having static
     * preferences amongst the valid values. Which of the many valid values is returned is upto the particular
     * implementation of this method.
     *
     * @returns The negotiated value.
     */
    Object getNegotiatedValue();

    /**
     * Returns the <code>Class</code> of the object that would be returned from the <code>getNegotiatedValue</code>
     * method on a successful negotiation. This method can be used to learn what the <code>Class</code> of the
     * negotiated value will be if the negotiation is successful. Implementing classes are encouraged to return an
     * accurate <code>Class</code> from this method if at all possible. However it is permissible to return null, if the
     * <code>Class</code> of the negotiated value is indeterminate for any reason.
     *
     * @returns the <code>Class</code> of the negotiated value.
     */
    Class getNegotiatedValueClass();
}
