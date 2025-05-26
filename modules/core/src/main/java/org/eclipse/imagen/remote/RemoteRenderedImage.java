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

import java.awt.image.RenderedImage;

/**
 * <code>RemoteRenderedImage</code> is an interface commonly used to represent objects which contain or can produce
 * image data in the form of <code>Raster</code>s from locations that are remote. The image data may be stored/produced
 * as a single tile or a regular array of tiles.
 *
 * <p>This class is the remote equivalent of the <code>RenderedImage</code> interface and adds methods that deal with
 * the remote location aspect of the image.
 *
 * @see RenderedImage
 * @since JAI 1.1
 */
public interface RemoteRenderedImage extends RenderedImage {

    /** Returns the <code>String</code> that identifies the server. */
    String getServerName();

    /** Returns the <code>String</code> that identifies the remote imaging protocol. */
    String getProtocolName();

    /** Returns the amount of time between retries in milliseconds. */
    int getRetryInterval();

    /**
     * Sets the amount of time between retries in milliseconds.
     *
     * @param retryInterval The amount of time (in milliseconds) to wait between retries.
     * @throws IllegalArgumentException if retryInterval is negative.
     */
    void setRetryInterval(int retryInterval);

    /** Returns the number of retries. */
    int getNumRetries();

    /**
     * Sets the number of retries.
     *
     * @param numRetries The number of times an operation should be retried in case of a network error.
     * @throws IllegalArgumentException if numRetries is negative.
     */
    void setNumRetries(int numRetries);

    /** Returns the current negotiation preferences or null, if none were set previously. */
    NegotiableCapabilitySet getNegotiationPreferences();

    /**
     * Sets the preferences to be used in the client-server communication. These preferences are utilized in the
     * negotiation process. Note that preferences for more than one category can be specified using this method. Also
     * each preference can be a list of values in decreasing order of preference, each value specified as a <code>
     * NegotiableCapability</code>. The <code>NegotiableCapability</code> first (for a particular category) in this list
     * is given highest priority in the negotiation process (for that category).
     *
     * <p>It may be noted that this method allows for multiple negotiation cycles. Everytime this method is called, new
     * preferences are specified for the negotiation, which can be utilized to perform a new round of negotiation to
     * produce new negotiated values to be used in the remote communication.
     *
     * @param preferences The preferences to be used in the negotiation process.
     * @throws IllegalArgumentException if preferences is null.
     */
    void setNegotiationPreferences(NegotiableCapabilitySet preferences);

    /**
     * Returns the results of the negotiation process. This will return null if no negotiation preferences were set, and
     * no negotiation was performed, or if the negotiation failed.
     */
    NegotiableCapabilitySet getNegotiatedValues() throws RemoteImagingException;

    /**
     * Returns the results of the negotiation process for the given category. This will return null if no negotiation
     * preferences were set, and no negotiation was performed, or if the negotiation failed.
     *
     * @param String category The category to get the negotiated results for.
     * @throws IllegalArgumentException if category is null.
     */
    NegotiableCapability getNegotiatedValue(String category) throws RemoteImagingException;

    /**
     * Informs the server of the negotiated values that are the result of a successful negotiation.
     *
     * @param negotiatedValues The result of the negotiation.
     */
    void setServerNegotiatedValues(NegotiableCapabilitySet negotiatedValues) throws RemoteImagingException;
}
