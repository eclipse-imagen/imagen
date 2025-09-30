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

package org.eclipse.imagen;

/**
 * An interface for all ImageN descriptors that register themselves with the <code>OperationRegistry</code>. Examples
 * include <code>OperationDescriptor</code>, <code>TileCodecDescriptor</code>, <code>RemoteDescriptor</code> etc.
 *
 * @see OperationRegistry
 * @see RegistryMode
 */
public interface RegistryElementDescriptor {

    /**
     * The name this descriptor will be registered under in the <code>OperationRegistry</code>. Individual descriptors
     * implementing this interface will define what this name means in their space. For example this would be "operation
     * name" for <code>OperationDescriptor</code> and "format name" for <code>TileCodecDescriptor</code> etc. The
     * descriptor names are to be treated in a case-insensitive (but retentive) manner.
     */
    String getName();

    /**
     * The registry modes supported by this descriptor. Known modes include those returned by <code>
     * RegistryMode.getModes()</code>.
     *
     * @return an array of <code>String</code>s specifying the supported modes.
     * @see RegistryMode
     */
    String[] getSupportedModes();

    /**
     * Whether this descriptor supports the specified registry mode. The <code>modeName</code>s are to be treated in a
     * case-insensitive (but retentive) manner.
     *
     * @param modeName the registry mode name
     * @return true, if the implementation of this descriptor supports the specified mode. false otherwise.
     * @throws IllegalArgumentException if <code>modeName</code> is null
     */
    boolean isModeSupported(String modeName);

    /**
     * Whether this descriptor supports ImageN properties.
     *
     * @return <code>true</code>, if the implementation of this descriptor supports ImageN properties. <code>false
     *     </code> otherwise.
     * @see PropertyGenerator
     */
    boolean arePropertiesSupported();

    /**
     * Returns an array of <code>PropertyGenerator</code>s implementing the property inheritance for this descriptor.
     * They may be used as a basis for the descriptor's property management.
     *
     * @param modeName the registry mode name
     * @return An array of <code>PropertyGenerator</code>s, or <code>null</code> if this operation does not have any of
     *     its own <code>PropertyGenerator</code>s.
     * @throws IllegalArgumentException if <code>modeName</code> is null or if it is not one of the supported modes.
     * @throws UnsupportedOperationException if <code>arePropertiesSupported()</code> returns <code>false</code>
     */
    PropertyGenerator[] getPropertyGenerators(String modeName);

    /**
     * Returns the <code>ParameterListDescriptor</code> that describes the associated parameters (<u>not</u> sources).
     * This method returns null if the specified modeName does not support parameters. If the specified modeName
     * supports parameters but the implementing class does not have parameters, then this method returns a non-null
     * <code>ParameterListDescriptor</code> whose <code>getNumParameters()</code> returns 0.
     *
     * @param modeName the registry mode name.
     * @throws IllegalArgumentException if <code>modeName</code> is null or if it is not one of the supported modes.
     */
    ParameterListDescriptor getParameterListDescriptor(String modeName);
}
