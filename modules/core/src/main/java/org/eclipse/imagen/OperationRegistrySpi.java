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

// This uses the ImageIO idea of "services" to look for
// concrete class that implement this interface. These concrete
// classes must have been registered/listed in the
// META-INF/services/org.eclipse.imagen.OperationRegistrySpi file.

/**
 * An interface definition to aid in the automatic loading of user-defined ImageN operations.
 *
 * <p>All concrete classes that implement this interface can register by listing themselves in the "<code>
 * META-INF/services/org.eclipse.imagen.OperationRegistrySpi</code>" file that can be found in the classpath (this file
 * is often contained in a jar file along with the class files). The file should contain a list of fully-qualified
 * concrete provider-class names, one per line. Space and tab characters surrounding each name, as well as blank lines,
 * are ignored. The comment character is <tt>'#'</tt> (<tt>0x23</tt>); on each line all characters following the first
 * comment character are ignored. The file must be encoded in UTF-8.
 *
 * <p>If a particular concrete provider class is named in more than one configuration file, or is named in the same
 * configuration file more than once, then the duplicates will be ignored. The configuration file naming a particular
 * provider need not be in the same jar file or other distribution unit as the provider itself. The provider must be
 * accessible from the same class loader that was initially queried to locate the configuration file; note that this is
 * not necessarily the class loader that found the file.
 *
 * <p>All such concrete classes must have a zero-argument constructor so that they may be instantiated during lookup.
 * The <code>updateRegistry()</code> method of all such registered classes will be called with the default instance of
 * the ImageN <code>OperationRegistry</code> after it has been initialized with the default ImageN registry file
 * (META-INF/org.eclipse.imagen.registryFile.jai) and once all "META-INF/registryFile.jai"s found in the classpath are
 * loaded. There is no guarantee of the order in which the <code>updateRegistry()</code> method of each <code>
 * OperationRegistrySpi</code> instance will be invoked.
 *
 * <p>The <code>OperationRegistrySpi</code> could also be used to for the registration of other ImageN related objects
 * done through static methods such as the <code>Serializer</code> objects and image codecs.
 *
 * @see org.eclipse.imagen.remote.Serializer
 * @see org.eclipse.imagen.OperationRegistry
 * @see org.eclipse.imagen.OperationRegistry#writeExternal
 */
public interface OperationRegistrySpi {

    /**
     * This method will be called for all registered "service-providers" of this interface just after the default
     * registry has been read-in.
     */
    public void updateRegistry(OperationRegistry registry);
}
