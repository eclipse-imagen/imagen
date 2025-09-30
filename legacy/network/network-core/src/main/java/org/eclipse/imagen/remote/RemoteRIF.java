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

import java.awt.*;
import java.awt.image.renderable.ParameterBlock;
import org.eclipse.imagen.NegotiableCapabilitySet;
import org.eclipse.imagen.OperationNode;
import org.eclipse.imagen.PropertyChangeEventJAI;

/**
 * The <code>RemoteRIF</code> interface is intended to be implemented by classes that wish to act as factories to
 * produce different renderings remotely, for example by executing a series of remote operations on a set of sources,
 * depending on a specific set of parameters, properties, and rendering hints.
 *
 * <p>All factories that produce renderings for operations remotely must implement <code>RemoteRIF</code>.
 *
 * <p>Classes that implement this interface must provide a constructor with no arguments.
 */
public interface RemoteRIF {

    /**
     * Creates a <code>RemoteRenderedImage</code> representing the results of an imaging operation (or chain of
     * operations) for a given <code>ParameterBlock</code> and <code>RenderingHints</code>. The <code>RemoteRIF</code>
     * may also query any source images referenced by the <code>ParameterBlock</code> for their dimensions, <code>
     * SampleModel</code>s, properties, etc., as necessary.
     *
     * <p>The <code>create()</code> method should return null if the <code>RemoteRIF</code> (representing the server) is
     * not capable of producing output for the given set of source images and parameters. For example, if a server
     * (represented by a <code>RemoteRIF</code>) is only capable of performing a 3x3 convolution on single-banded image
     * data, and the source image has multiple bands or the convolution Kernel is 5x5, null should be returned.
     *
     * <p>Hints should be taken into account, but can be ignored. The created <code>RemoteRenderedImage</code> may have
     * a property identified by the String HINTS_OBSERVED to indicate which <code>RenderingHints</code> were used to
     * create the image. In addition any <code>RenderedImage</code>s that are obtained via the getSources() method on
     * the created <code>RemoteRenderedImage</code> may have such a property.
     *
     * @param serverName A <code>String</code> specifying the name of the server to perform the remote operation on.
     * @param operationName The <code>String</code> specifying the name of the operation to be performed remotely.
     * @param paramBlock A <code>ParameterBlock</code> containing sources and parameters for the <code>
     *     RemoteRenderedImage</code> to be created.
     * @param hints A <code>RenderingHints</code> object containing hints.
     * @return A <code>RemoteRenderedImage</code> containing the desired output.
     */
    RemoteRenderedImage create(String serverName, String operationName, ParameterBlock paramBlock, RenderingHints hints)
            throws RemoteImagingException;

    /**
     * Creates a <code>RemoteRenderedImage</code> representing the results of an imaging operation represented by the
     * given <code>OperationNode</code>, whose given old rendering is updated according to the given <code>
     * PropertyChangeEventJAI</code>. This factory method should be used to create a new rendering updated according to
     * the changes reported by the given <code>PropertyChangeEventJAI</code>. The <code>RemoteRIF</code> can query the
     * supplied <code>OperationNode</code> for references to the server name, operation name, parameter block, and
     * rendering hints. If only a new rendering of the node is desired in order to handle the supplied <code>
     * PropertyChangeEventJAI</code>, the rendering can be obtained by calling the default <code>create()</code> method,
     * the arguments to which can be retrieved from the supplied <code>OperationNode</code>. The <code>RemoteRIF</code>
     * may also query any source images referenced by the <code>ParameterBlock</code> for their dimensions, <code>
     * SampleModel</code>s, properties, etc., as necessary. The supplied <code>OperationNode</code> should not be edited
     * during the creation of the new rendering, otherwise the <code>OperationNode</code> might have an inconsistent
     * state.
     *
     * <p>The <code>create()</code> method can return null if the <code>RemoteRIF</code> (representing the server) is
     * not capable of producing output for the given set of source images and parameters. For example, if a server
     * (represented by a <code>RemoteRIF</code>) is only capable of performing a 3x3 convolution on single-banded image
     * data, and the source image has multiple bands or the convolution Kernel is 5x5, null should be returned.
     *
     * <p>Hints should be taken into account, but can be ignored. The created <code>RemoteRenderedImage</code> may have
     * a property identified by the String HINTS_OBSERVED to indicate which <code>RenderingHints</code> were used to
     * create the image. In addition any <code>RenderedImage</code>s that are obtained via the getSources() method on
     * the created <code>RemoteRenderedImage</code> may have such a property.
     *
     * @param oldRendering The old rendering of the imaging operation.
     * @param node The <code>OperationNode</code> that represents the imaging operation.
     * @param event An event that specifies the changes made to the imaging operation.
     * @return A <code>RemoteRenderedImage</code> containing the desired output.
     */
    RemoteRenderedImage create(PlanarImageServerProxy oldRendering, OperationNode node, PropertyChangeEventJAI event)
            throws RemoteImagingException;

    /** Returns the set of capabilities supported by the client object. */
    NegotiableCapabilitySet getClientCapabilities();
}
