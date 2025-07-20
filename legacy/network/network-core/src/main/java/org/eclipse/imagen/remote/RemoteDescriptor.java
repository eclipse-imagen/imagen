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
import java.net.URL;
import org.eclipse.imagen.NegotiableCapabilitySet;
import org.eclipse.imagen.OperationDescriptor;
import org.eclipse.imagen.OperationNode;
import org.eclipse.imagen.RegistryElementDescriptor;

/**
 * This interface provides a description of a specific remote imaging protocol. Information regarding the remote imaging
 * protocol such as its name, the list of operations supported on a particular server, the capabilities of a server
 * implementing this protocol, human readable documentation detailing how the <code>String</code> identifying the server
 * is structured should all be provided through this interface. Each remote imaging protocol registered with the <code>
 * OperationRegistry</code> must have a <code>RemoteDescriptor</code>.
 *
 * <p>Any implementation of the <code>getName</code> method of <code>RegistryElementDescriptor</code> is expected to
 * return the name of the remote imaging protocol. This is the name under which this <code>RemoteDescriptor</code> will
 * be registered in the <code>OperationRegistry</code>.
 *
 * <p>There are two <code>RegistryMode</code>s associated with remote imaging. The first is "remoteRendered" which
 * signifies that the remote imaging operations lie in the rendered mode, the other is "remoteRenderable" which
 * signifies that the remote imaging operations lie in the renderable domain and deal with renderable operations.
 *
 * <p>The <code>getServerCapabilities()</code> method returns the capabilities of the specified server. This information
 * may already be known by virtue of being specified in the imaging protocol, or may have to be determined from the
 * server, in which case, the implementation must communicate with the server to get this information. To get the
 * capabilities of the client, the <code>getClientCapabilities()</code> method, which exists on <code>RemoteRIF</code>
 * can be used. The reason for the <code>getClientCapabilities</code> method being defined on the <code>RemoteRIF</code>
 * instead of on the <code>RemoteDescriptor</code> is that the descriptor does not have any way to reference the client.
 * Thus there is no way for the descriptor to report the client capabilities. On the other hand, the <code>RemoteRIF
 * </code> is the factory that creates the client, and therefore can be expected to either know or determine the
 * capabilities of the client.
 *
 * @see org.eclipse.imagen.registry.RemoteRenderedRegistryMode
 * @see org.eclipse.imagen.registry.RemoteRenderableRegistryMode
 * @since JAI 1.1
 */
public interface RemoteDescriptor extends RegistryElementDescriptor {

    /**
     * Returns the list of <code>OperationDescriptor</code>s that describe the operations supported by the server. It is
     * the implementing class's responsibility to extract this information from either the server or from its own
     * knowledge of the remote imaging protocol.
     *
     * <p>The format of the serverName argument is protocol-dependent. Thus different protocol specific subclasses may
     * treat the same serverName argument in different ways, i.e. one protocol may allow the serverName argument to be
     * null (if this protocol defines a default server), while another may consider null an invalid serverName and throw
     * an <code>Exception</code>.
     *
     * @param serverName The <code>String</code> identifying the server.
     */
    OperationDescriptor[] getServerSupportedOperationList(String serverName) throws RemoteImagingException;

    /**
     * Returns the set of capabilites supported by the server. It is the implementing class's responsibility to extract
     * this information from either the server or from its own knowledge of the remote imaging protocol.
     *
     * <p>The format of the serverName argument is protocol-dependent. Thus different protocol specific subclasses may
     * treat the same serverName argument in different ways, i.e. one protocol may allow the serverName argument to be
     * null (if this protocol defines a default server), while another may consider null an invalid serverName and throw
     * an <code>Exception</code>.
     *
     * @param serverName The <code>String</code> identifying the server.
     */
    NegotiableCapabilitySet getServerCapabilities(String serverName) throws RemoteImagingException;

    /**
     * Returns a <code>URL</code> that points to documentation containing instructions on constructing a server name
     * string for the protocol with which this class is associated.
     */
    URL getServerNameDocs();

    /**
     * Calculates the region over which two distinct remote renderings of an operation may be expected to differ. The
     * operation is represented by the <code>OperationNode</code> argument to this method. The <code>String</code> that
     * identifies the operation can be retrieved via the <code>OperationNode</code>'s <code>getOperationName()</code>
     * method.
     *
     * <p>The class of the returned object will vary as a function of the nature of the operation. For rendered and
     * renderable two- dimensional images this should be an instance of a class which implements <code>java.awt.Shape
     * </code>.
     *
     * @param registryModeName The name of the mode.
     * @param oldServerName The previous server name.
     * @param oldParamBlock The previous sources and parameters.
     * @param oldHints The previous hints.
     * @param newServerName The current server name.
     * @param newParamBlock The current sources and parameters.
     * @param newHints The current hints.
     * @param node The affected node in the processing chain.
     * @return The region over which the data of two renderings of this operation may be expected to be invalid or
     *     <code>null</code> if there is no common region of validity. If an empty <code>java.awt.Shape</code> is
     *     returned, this indicates that all pixels within the bounds of the old rendering remain valid.
     * @throws IllegalArgumentException if <code>registryModeName</code> is <code>null</code> or if the operation
     *     requires either sources or parameters and either <code>oldParamBlock</code> or <code>newParamBlock</code> is
     *     <code>null</code>.
     * @throws IllegalArgumentException if there is no OperationDescriptor for the specified operation on any one or
     *     both of the servers identified by <code>oldServerName</code> and <code>newServerName</code>, or if the number
     *     of sources or the name, number and <code>Class</code> of the operation's parameters is not the same on both
     *     the servers.
     * @throws IllegalArgumentException if <code>oldParamBlock</code> or <code>newParamBlock</code> do not contain
     *     sufficient sources or parameters for the operation in question.
     */
    Object getInvalidRegion(
            String registryModeName,
            String oldServerName,
            ParameterBlock oldParamBlock,
            RenderingHints oldHints,
            String newServerName,
            ParameterBlock newParamBlock,
            RenderingHints newHints,
            OperationNode node)
            throws RemoteImagingException;
}
