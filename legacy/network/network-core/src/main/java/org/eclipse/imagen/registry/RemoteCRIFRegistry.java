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

package org.eclipse.imagen.registry;

import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderContext;
import org.eclipse.imagen.ImageN;
import org.eclipse.imagen.OperationRegistry;
import org.eclipse.imagen.remote.RemoteCRIF;
import org.eclipse.imagen.remote.RemoteRenderedImage;

/**
 * Utility class to provide type-safe interaction with the <code>OperationRegistry</code> for <code>RemoteCRIF</code>
 * objects.
 *
 * <p>If the <code>OperationRegistry</code> specified as an argument to the methods in this class is null, then <code>
 * ImageN.getOperationRegistry()</code> will be used.
 */
public final class RemoteCRIFRegistry {

    private static final String MODE_NAME = RemoteRenderableRegistryMode.MODE_NAME;

    /**
     * Registers the given <code>RemoteCRIF</code> with the given <code>OperationRegistry</code> under the given
     * protocolName
     *
     * @param registry The <code>OperationRegistry</code> to register the <code>RemoteCRIF</code> with. If this is
     *     <code>null</code>, then <code>
     *                     ImageN.getDefaultInstance().getOperationRegistry()</code> will be used.
     * @param protocolName The protocolName to register the <code>RemoteCRIF</code> under.
     * @param rcrif The <code>RemoteCRIF</code> to register.
     * @throws IllegalArgumentException if protocolName is null.
     * @throws IllegalArgumentException if rcrif is null.
     * @throws IllegalArgumentException if there is no <code>RemoteDescriptor</code> registered against the given
     *     protocolName.
     */
    public static void register(OperationRegistry registry, String protocolName, RemoteCRIF rcrif) {

        registry = (registry != null) ? registry : ImageN.getDefaultInstance().getOperationRegistry();

        registry.registerFactory(MODE_NAME, protocolName, null, rcrif);
    }

    /**
     * Unregisters the given <code>RemoteCRIF</code> previously registered under the given protocolName in the given
     * <code>OperationRegistry</code>.
     *
     * @param registry The <code>OperationRegistry</code> to unregister the <code>RemoteCRIF</code> from. If this is
     *     <code>null</code>, then <code>
     *                     ImageN.getDefaultInstance().getOperationRegistry()</code> will be used.
     * @param protocolName The protocolName to unregister the <code>RemoteCRIF</code> from under.
     * @param rcrif The <code>RemoteCRIF</code> to unregister.
     * @throws IllegalArgumentException if protocolName is null.
     * @throws IllegalArgumentException if rcrif is null.
     * @throws IllegalArgumentException if there is no <code>RemoteDescriptor</code> registered against the given
     *     protocolName.
     * @throws IllegalArgumentException if the rcrif was not previously registered against protocolName.
     */
    public static void unregister(OperationRegistry registry, String protocolName, RemoteCRIF rcrif) {

        registry = (registry != null) ? registry : ImageN.getDefaultInstance().getOperationRegistry();

        registry.unregisterFactory(MODE_NAME, protocolName, null, rcrif);
    }

    /**
     * Returns the <code>RemoteCRIF</code> registered under the given protocol name in the specified <code>
     * OperationRegistry</code>.
     *
     * @param registry The <code>OperationRegistry</code> to use. If this is <code>null</code>, then <code>
     *                     ImageN.getDefaultInstance().getOperationRegistry()</code> will be used.
     * @param protocolName The name of the remote imaging protocol.
     * @throws IllegalArgumentException if protocolName is null.
     * @throws IllegalArgumentException if there is no <code>RemoteDescriptor</code> registered against the given <code>
     *     protocolName</code>.
     */
    public static RemoteCRIF get(OperationRegistry registry, String protocolName) {

        registry = (registry != null) ? registry : ImageN.getDefaultInstance().getOperationRegistry();

        return (RemoteCRIF) registry.getFactory(MODE_NAME, protocolName);
    }

    /**
     * Creates a rendering remotely, given the serverName, protocolName, name of the operation to be performed, a <code>
     * RenderContext</code> and a <code>ParameterBlock</code> containing the operation's sources and parameters. The
     * registry is used to determine the <code>RemoteCRIF</code> to be used to instantiate the operation.
     *
     * <p>Since this class is a simple type-safe wrapper around <code>OperationRegistry</code>'s type-unsafe methods, no
     * additional argument validation is performed in this method. Thus errors/exceptions may occur if incorrect values
     * are provided for the input arguments. If argument validation is desired as part of creating a rendering, <code>
     * RemoteJAI.createRenderable()</code> may be used instead to create a <code>RemoteRenderableOp</code> which can
     * then be asked for a rendering.
     *
     * <p>Exceptions thrown by the <code>RemoteRIF</code>s used to create the rendering will be caught by this method
     * and will not be propagated.
     *
     * @param registry The <code>OperationRegistry</code> to use to create the rendering. If this is <code>null</code>,
     *     then <code>
     *                     ImageN.getDefaultInstance().getOperationRegistry()</code> will be used.
     * @param protocolName The protocol to be used for remote imaging.
     * @param serverName The name of the server.
     * @param operationName The name of the operation to be performed remotely.
     * @param renderContext A <code>RenderContext</code> specifying the context in which the rendering should be
     *     requested.
     * @param paramBlock The <code>ParameterBlock</code> specifying the sources and parameters required for the
     *     operation.
     * @throws IllegalArgumentException if protocolName is null.
     * @throws IllegalArgumentException if there is no <code>RemoteDescriptor</code> registered against the given
     *     protocolName.
     */
    public static RemoteRenderedImage create(
            OperationRegistry registry,
            String protocolName,
            String serverName,
            String operationName,
            RenderContext renderContext,
            ParameterBlock paramBlock) {

        registry = (registry != null) ? registry : ImageN.getDefaultInstance().getOperationRegistry();

        Object args[] = {serverName, operationName, renderContext, paramBlock};

        return (RemoteRenderedImage) registry.invokeFactory(MODE_NAME, protocolName, args);
    }
}
