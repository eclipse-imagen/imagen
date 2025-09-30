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
import org.eclipse.imagen.CollectionImage;
import org.eclipse.imagen.CollectionOp;
import org.eclipse.imagen.ImageN;
import org.eclipse.imagen.OperationNode;
import org.eclipse.imagen.OperationRegistry;
import org.eclipse.imagen.PropertySource;
import org.eclipse.imagen.RenderableCollectionImageFactory;

/**
 * Utility class to provide type-safe interaction with the <code>OperationRegistry</code> for <code>
 * RenderableCollectionImageFactory</code> objects.
 *
 * <p>If the <code>OperationRegistry</code> is <code>null</code>, then <code>
 * ImageN.getDefaultInstance().getOperationRegistry()</code> will be used.
 *
 * @see CollectionImage
 * @since ImageN 0.4.0
 */
public final class RCIFRegistry {

    private static final String MODE_NAME = RenderableCollectionRegistryMode.MODE_NAME;

    /**
     * Register a RCIF with a particular operation against a specified mode.
     *
     * @param registry the <code>OperationRegistry</code> to register with. if this is <code>null</code>, then <code>
     *         ImageN.getDefaultInstance().getOperationRegistry()</code> will be used.
     * @param operationName the operation name as a <code>String</code>
     * @param rcif the <code>RenderableCollectionImageFactory</code> to be registered
     * @throws IllegalArgumentException if operationName or rcif is <code>null</code>
     * @throws IllegalArgumentException if there is no <code>
     *             OperationDescriptor</code> registered against the <code>operationName</code>
     */
    public static void register(
            OperationRegistry registry, String operationName, RenderableCollectionImageFactory rcif) {

        registry = (registry != null) ? registry : ImageN.getDefaultInstance().getOperationRegistry();

        registry.registerFactory(MODE_NAME, operationName, null, rcif);
    }

    /**
     * Unregister a RCIF previously registered with a operation against the specified mode.
     *
     * @param registry the <code>OperationRegistry</code> to unregister from. if this is <code>null</code>, then <code>
     *         ImageN.getDefaultInstance().getOperationRegistry()</code> will be used.
     * @param operationName the operation name as a <code>String</code>
     * @param rcif the <code>RenderableCollectionImageFactory</code> to be unregistered
     * @throws IllegalArgumentException if operationName or rcif is <code>null</code>
     * @throws IllegalArgumentException if there is no <code>
     *             OperationDescriptor</code> registered against the <code>operationName</code>
     * @throws IllegalArgumentException if the rcif was not previously registered against operationName
     */
    public static void unregister(
            OperationRegistry registry, String operationName, RenderableCollectionImageFactory rcif) {

        registry = (registry != null) ? registry : ImageN.getDefaultInstance().getOperationRegistry();

        registry.unregisterFactory(MODE_NAME, operationName, null, rcif);
    }

    /**
     * Returns the <code>RenderableCollectionImageFactory</code> object registered against the operation name.
     *
     * @param registry the <code>OperationRegistry</code> to use. if this is <code>null</code>, then <code>
     *         ImageN.getDefaultInstance().getOperationRegistry()</code> will be used.
     * @param operationName the operation name as a <code>String</code>
     * @return a registered <code>RenderableCollectionImageFactory</code> object
     * @throws IllegalArgumentException if operationName is <code>null</code>
     * @throws IllegalArgumentException if there is no <code>
     *             OperationDescriptor</code> registered against the <code>operationName</code>
     */
    public static RenderableCollectionImageFactory get(OperationRegistry registry, String operationName) {

        registry = (registry != null) ? registry : ImageN.getDefaultInstance().getOperationRegistry();

        return (RenderableCollectionImageFactory) registry.getFactory(MODE_NAME, operationName);
    }

    /**
     * Creates a renderable <code>CollectionImage</code> given a ParameterBlock containing the operation's sources and
     * parameters. The registry is used to determine the RCIF to be used to instantiate the operation.
     *
     * @param registry the <code>OperationRegistry</code> to use. if this is <code>null</code>, then <code>
     *         ImageN.getDefaultInstance().getOperationRegistry()</code> will be used.
     * @param operationName the operation name as a <code>String</code>
     * @param paramBlock the operation's ParameterBlock.
     * @throws IllegalArgumentException if operationName is <code>null</code>
     * @throws IllegalArgumentException if there is no <code>
     *             OperationDescriptor</code> registered against the <code>operationName</code>
     */
    public static CollectionImage create(OperationRegistry registry, String operationName, ParameterBlock paramBlock) {

        registry = (registry != null) ? registry : ImageN.getDefaultInstance().getOperationRegistry();

        Object args[] = {paramBlock};

        return (CollectionImage) registry.invokeFactory(MODE_NAME, operationName, args);
    }

    /**
     * Constructs and returns a <code>PropertySource</code> suitable for use by a given <code>CollectionOp</code>. The
     * <code>PropertySource</code> includes properties copied from prior nodes as well as those generated at the node
     * itself. Additionally, property suppression is taken into account. The actual implementation of <code>
     * getPropertySource()</code> may make use of deferred execution and caching.
     *
     * @param op the <code>CollectionOp</code> requesting its <code>PropertySource</code>.
     * @throws IllegalArgumentException if <code>op</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>op.isRenderable()</code> returns <code>false</code>
     */
    public static PropertySource getPropertySource(CollectionOp op) {

        if (op == null) throw new IllegalArgumentException("op - " + JaiI18N.getString("Generic0"));

        if (!op.isRenderable()) throw new IllegalArgumentException("op - " + JaiI18N.getString("CIFRegistry1"));

        return op.getRegistry().getPropertySource((OperationNode) op);
    }
}
