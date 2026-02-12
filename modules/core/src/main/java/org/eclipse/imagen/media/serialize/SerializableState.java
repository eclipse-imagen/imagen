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
 * SPDX-License-Identifier: Apache-2.0
 */
package org.eclipse.imagen.media.serialize;

import java.io.Serializable;

/**
 * An interface to be implemented by classes instances of which act as serializable proxies for instances of
 * non-serializable classes.
 *
 * @see java.io.Serializable
 */
public interface SerializableState extends Serializable {

    /**
     * Retrieve the class of the object which would be returned by invoking <code>getObject()</code>.
     *
     * @return The class of the object which would be returned by <code>getObject()</code>.
     */
    Class getObjectClass();

    /**
     * Reconstitutes an object from a serializable version of its state wrapped by an implementation of this interface.
     *
     * @return Deserialized form of the state.
     */
    Object getObject();
}
