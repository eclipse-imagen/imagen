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

package org.eclipse.imagen.media.rmi;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * A class which acts as a proxy for an object which is serialized as an amalgam of <code>Serializer</code>s for the
 * various interfaces that it implements.
 *
 * @since 1.1
 */
public final class InterfaceProxy extends Proxy {
    public InterfaceProxy(InvocationHandler h) {
        super(h);
    }
}
