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
import org.eclipse.imagen.OperationNode;
import org.eclipse.imagen.ParameterListDescriptor;
import org.eclipse.imagen.PropertyGenerator;

/**
 * This abstract class provides a partial implementation of the <code>RemoteDescriptor</code> interface, and is suitable
 * for subclassing.
 *
 * @see RemoteDescriptor
 * @since ImageN 1.1
 */
public abstract class RemoteDescriptorImpl implements RemoteDescriptor {

    /** The name of the protocol that this descriptor describes. */
    protected String protocolName;

    /**
     * The <code>URL</code> pointing to the documentation regarding the format of the server name <code>String</code>.
     */
    protected URL serverNameDocURL;

    /**
     * Creates a <code>RemoteDescriptorImpl</code> given the protocol name and the <code>URL</code> that points to
     * documentation regarding the format of the server name <code>String</code>.
     *
     * <p>While the <code>serverNameDocURL</code> argument is allowed to be null, this is strongly discouraged, since
     * this <code>URL</code> is the only description available to the user to help with creating a serverName <code>
     * String</code> correctly.
     *
     * @param protocolName The name of the protocol.
     * @param serverNameDocURL The <code>URL</code> pointing to server name format documentation.
     * @throws IllegalArgumentException if protocolName is null.
     */
    public RemoteDescriptorImpl(String protocolName, URL serverNameDocURL) {

        if (protocolName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic1"));
        }

        this.protocolName = protocolName;
        this.serverNameDocURL = serverNameDocURL;
    }

    /**
     * Returns the name of the remote imaging protocol under which this <code>RemoteDescriptor</code> will be registered
     * in the <code>OperationRegistry</code>.
     */
    public String getName() {
        return protocolName;
    }

    /**
     * The registry modes supported by this descriptor. The default implementation in this class returns two modes -
     * "remoteRendered" and "remoteRenderable". If the subclass does not support both these modes it should override
     * this method to reflect that.
     *
     * @see org.eclipse.imagen.RegistryMode
     */
    public String[] getSupportedModes() {
        return new String[] {"remoteRendered", "remoteRenderable"};
    }

    /**
     * Returns true if the supplied modeName is supported by this descriptor. The default implementation in this class
     * returns true only if the supplied modeName is one of either "remoteRendered" or "remoteRenderable".
     *
     * @param modeName The mode name to check support for.
     * @return true, if the implementation of this descriptor supports the specified mode. false otherwise.
     * @throws IllegalArgumentException if <code>modeName</code> is null.
     */
    public boolean isModeSupported(String modeName) {

        if (modeName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("RemoteDescriptorImpl1"));
        }

        if (modeName.equalsIgnoreCase("remoteRendered") || modeName.equalsIgnoreCase("remoteRenderable")) {
            return true;
        }

        return false;
    }

    /**
     * Returns true, if the implementation of this descriptor supports properties, false otherwise. The default
     * implementation in this class returns false, signifying that no properties are supported independent of the
     * operations themselves.
     *
     * @see PropertyGenerator
     */
    public boolean arePropertiesSupported() {
        return false;
    }

    /**
     * Returns an array of <code>PropertyGenerator</code>s implementing the property inheritance for this descriptor.
     * Since neither the "remoteRendered" or "remoteRendered" modes support properties independent of the operations
     * themselves, the default implementation throws an <code>UnsupportedOperationException</code>. Subclasses should
     * override this method if they wish to produce inherited properties.
     *
     * @param modeName The mode name to get <code>PropertyGenerator</code>s for.
     * @throws IllegalArgumentException if <code>modeName</code> is null.
     * @throws UnsupportedOperationException if <code>arePropertiesSupported()</code> returns <code>false</code>
     * @return An array of <code>PropertyGenerator</code>s, or <code>null</code> if this operation does not have any of
     *     its own <code>PropertyGenerator</code>s.
     */
    public PropertyGenerator[] getPropertyGenerators(String modeName) {

        if (modeName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("RemoteDescriptorImpl1"));
        }

        throw new UnsupportedOperationException(JaiI18N.getString("RemoteDescriptorImpl2"));
    }

    /**
     * Returns a <code>URL</code> that points to an HTML page containing instructions on constructing a server name
     * string for the protocol with which this class is associated.
     */
    public URL getServerNameDocs() {
        return serverNameDocURL;
    }

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
     * <p>The implementation in this class always returns null as the invalid region signifying that there is no common
     * region of validity. Since null is always returned, in the interests of efficiency, none of the checks for
     * ensuring that the <code>ParameterBlock</code> arguments passed to this method contain the correct number and
     * <code>Class</code> of sources and parameters are performed in this implementation.
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
     * @throws IllegalArgumentException if there is no OperationDescriptor for the specified operationName on any one or
     *     both of the servers identified by <code>oldServerName</code> and <code>newServerName</code>, or if the number
     *     of sources or the name, number and <code>Class</code> of the operation's parameters is not the same on both
     *     the servers.
     * @throws IllegalArgumentException if <code>oldParamBlock</code> or <code>newParamBlock</code> do not contain
     *     sufficient sources or parameters for the operation in question.
     */
    public Object getInvalidRegion(
            String registryModeName,
            String oldServerName,
            ParameterBlock oldParamBlock,
            RenderingHints oldHints,
            String newServerName,
            ParameterBlock newParamBlock,
            RenderingHints newHints,
            OperationNode node)
            throws RemoteImagingException {
        return null;
    }

    /**
     * The two modes supported by this descriptor are "remoteRendered" and "remoteRenderable". Since neither of these
     * modes supports any parameters, this default implementation always returns null.
     *
     * @param modeName The mode name to get the <code>ParameterListDescriptor</code> for.
     * @throws IllegalArgumentException if modeName is null.
     */
    public ParameterListDescriptor getParameterListDescriptor(String modeName) {
        if (modeName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("RemoteDescriptorImpl1"));
        }

        return null;
    }
}
