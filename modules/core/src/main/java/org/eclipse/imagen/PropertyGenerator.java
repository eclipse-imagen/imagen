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
package org.eclipse.imagen;

import java.io.Serializable;

/**
 * An interface through which properties may be computed dynamically with respect to an environment of pre-existing
 * properties. In the interest of simplicity and consistency, a <code>PropertyGenerator</code> is required to be a pure
 * function; that is, if called multiple times with the same environment it must produce identical results.
 *
 * <p>The <code>OperationRegistry</code> class allows <code>PropertyGenerator</code>s to be associated with a particular
 * operation type, and will automatically insert them into imaging chains as needed.
 *
 * <p>Properties are treated in a case-insensitive manner.
 *
 * @see OperationRegistry
 */
public interface PropertyGenerator extends Serializable {

    /**
     * Returns an array of <code>String</code>s naming properties emitted by this property generator. The <code>String
     * </code>s may contain characters of any case.
     *
     * @return an array of <code>String</code>s that may be passed as parameter names to the <code>getProperty()</code>
     *     method.
     */
    String[] getPropertyNames();

    /**
     * Returns the class expected to be returned by a request for the property with the specified name. If this
     * information is unavailable, <code>null</code> will be returned indicating that <code>
     * getProperty(propertyName).getClass()</code> should be executed instead. A <code>null</code> value might be
     * returned for example to prevent generating the value of a deferred property solely to obtain its class.
     *
     * @return The <code>Class</code> expected to be return by a request for the value of this property or <code>null
     *     </code>.
     * @exception IllegalArgumentException if <code>propertyName</code> is <code>null</code>.
     */
    Class getClass(String propertyName);

    /**
     * Determines whether the specified <code>Object</code> will be recognized by <code>getProperty(String,Object)
     * </code>.
     *
     * @exception IllegalArgumentException if <code>opNode</code> is <code>null</code>.
     */
    boolean canGenerateProperties(Object opNode);

    /**
     * Computes the value of a property relative to an environment of pre-existing properties. The case of the supplied
     * <code>String</code> is ignored.
     *
     * <p>In the case of an <code>OperationNode</code> in a chain of operations these properties may be emitted by the
     * sources of the node in a chain or the parameters of that operation. The information requisite to compute the
     * requested property must be available via the supplied <code>OperationNode</code>. It is legal to call <code>
     * getProperty()</code> on the operation's sources.
     *
     * @param name the name of the property, as a <code>String</code>.
     * @param op the <code>Object</code> from which properties will be generated.
     * @return the value of the property, as an <code>Object</code> or the value <code>java.awt.Image.UndefinedProperty
     *     </code>.
     * @exception IllegalArgumentException if <code>name</code> or <code>opNode</code> is <code>null</code>.
     * @exception IllegalArgumentException if <code>opNode</code> is not an instance of a supported class for this
     *     method, i.e., <code>canGenerateProperties(opNode)</code> returns <code>false</code>.
     */
    Object getProperty(String name, Object opNode);

    /**
     * Computes the value of a property relative to an environment of pre-existing properties emitted by the sources of
     * a <code>RenderedOp</code>, and the parameters of that operation.
     *
     * <p>The operation name, sources, and <code>ParameterBlock</code> of the <code>RenderedOp</code> being processed
     * may be obtained by means of the <code>op.getOperationName</code>, <code>op.getSources()</code>, and <code>
     * op.getParameterBlock()</code> methods. It is legal to call <code>getProperty()</code> on the operation's sources.
     *
     * @param name the name of the property, as a <code>String</code>.
     * @param op the <code>RenderedOp</code> representing the operation.
     * @return the value of the property, as an <code>Object</code> or the value <code>java.awt.Image.UndefinedProperty
     *     </code>.
     * @exception IllegalArgumentException if <code>name</code> or <code>op</code> is <code>null</code>.
     * @deprecated Use <code>getProperty(String,Object)</code> instead.
     */
    Object getProperty(String name, RenderedOp op);

    /**
     * Computes the value of a property relative to an environment of pre-existing properties emitted by the sources of
     * a <code>RenderableOp</code>, and the parameters of that operation.
     *
     * <p>The operation name, sources, and <code>ParameterBlock</code> of the <code>RenderableOp</code> being processed
     * may be obtained by means of the <code>op.getOperationName</code>, <code>op.getSources()</code>, and <code>
     * op.getParameterBlock()</code> methods. It is legal to call <code>getProperty()</code> on the operation's sources.
     *
     * @param name the name of the property, as a <code>String</code>.
     * @param op the <code>RenderableOp</code> representing the operation.
     * @return the value of the property, as an <code>Object</code> or the value <code>java.awt.Image.UndefinedProperty
     *     </code>.
     * @exception IllegalArgumentException if <code>name</code> or <code>op</code> is <code>null</code>.
     * @deprecated Use <code>getProperty(String,Object)</code> instead.
     */
    Object getProperty(String name, RenderableOp op);
}
