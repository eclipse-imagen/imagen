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

/**
 * An interface to represent a list of parameter name-value pairs.
 *
 * <p>All comparisons using <code>String</code>s are done in a case insensitive (but retentive) manner.
 *
 * @see ParameterListDescriptor
 */
public interface ParameterList {

    /** Returns the associated <code>ParameterListDescriptor</code>. */
    public ParameterListDescriptor getParameterListDescriptor();

    /**
     * Sets a named parameter to a <code>byte</code> value.
     *
     * <p>Implementing classes are free but not required to check class type, ranges, and enumeration types.
     *
     * @param paramName a <code>String</code> naming a parameter.
     * @param b a <code>byte</code> value for the parameter.
     * @throws IllegalArgumentException if paramName is null.
     * @throws IllegalArgumentException if there is no parameter with the specified name.
     */
    public ParameterList setParameter(String paramName, byte b);

    /**
     * Sets a named parameter to a <code>boolean</code> value.
     *
     * <p>Implementing classes are free but not required to check class type, ranges, and enumeration types.
     *
     * @param paramName a <code>String</code> naming a parameter.
     * @param b a <code>boolean</code> value for the parameter.
     * @throws IllegalArgumentException if paramName is null.
     * @throws IllegalArgumentException if there is no parameter with the specified name.
     */
    public ParameterList setParameter(String paramName, boolean b);

    /**
     * Sets a named parameter to a <code>char</code> value.
     *
     * <p>Implementing classes are free but not required to check class type, ranges, and enumeration types.
     *
     * @param paramName a <code>String</code> naming a parameter.
     * @param c a <code>char</code> value for the parameter.
     * @throws IllegalArgumentException if paramName is null.
     * @throws IllegalArgumentException if there is no parameter with the specified name.
     */
    public ParameterList setParameter(String paramName, char c);

    /**
     * Sets a named parameter to a <code>short</code> value.
     *
     * <p>Implementing classes are free but not required to check class type, ranges, and enumeration types.
     *
     * @param paramName a <code>String</code> naming a parameter.
     * @param s a <code>short</code> value for the parameter.
     * @throws IllegalArgumentException if paramName is null.
     * @throws IllegalArgumentException if there is no parameter with the specified name.
     */
    public ParameterList setParameter(String paramName, short s);

    /**
     * Sets a named parameter to an <code>int</code> value.
     *
     * <p>Implementing classes are free but not required to check class type, ranges, and enumeration types.
     *
     * @param paramName a <code>String</code> naming a parameter.
     * @param i an <code>int</code> value for the parameter.
     * @throws IllegalArgumentException if paramName is null.
     * @throws IllegalArgumentException if there is no parameter with the specified name.
     */
    public ParameterList setParameter(String paramName, int i);

    /**
     * Sets a named parameter to a <code>long</code> value.
     *
     * <p>Implementing classes are free but not required to check class type, ranges, and enumeration types.
     *
     * @param paramName a <code>String</code> naming a parameter.
     * @param l a <code>long</code> value for the parameter.
     * @throws IllegalArgumentException if paramName is null.
     * @throws IllegalArgumentException if there is no parameter with the specified name.
     */
    public ParameterList setParameter(String paramName, long l);

    /**
     * Sets a named parameter to a <code>float</code> value.
     *
     * <p>Implementing classes are free but not required to check class type, ranges, and enumeration types.
     *
     * @param paramName a <code>String</code> naming a parameter.
     * @param f a <code>float</code> value for the parameter.
     * @throws IllegalArgumentException if paramName is null.
     * @throws IllegalArgumentException if there is no parameter with the specified name.
     */
    public ParameterList setParameter(String paramName, float f);

    /**
     * Sets a named parameter to a <code>double</code> value.
     *
     * <p>Implementing classes are free but not required to check class type, ranges, and enumeration types.
     *
     * @param paramName a <code>String</code> naming a parameter.
     * @param d a <code>double</code> value for the parameter.
     * @throws IllegalArgumentException if paramName is null.
     * @throws IllegalArgumentException if there is no parameter with the specified name.
     */
    public ParameterList setParameter(String paramName, double d);

    /**
     * Sets a named parameter to an <code>Object</code> value.
     *
     * <p>Implementing classes are free but not required to check class type, ranges, and enumeration types.
     *
     * @param paramName a <code>String</code> naming a parameter.
     * @param obj an <code>Object</code> value for the parameter.
     * @throws IllegalArgumentException if paramName is null.
     * @throws IllegalArgumentException if there is no parameter with the specified name. pointed to by the paramName.
     */
    public ParameterList setParameter(String paramName, Object obj);

    /**
     * Gets a named parameter as an <code>Object</code>. Parameters belonging to a primitive type, such as int, will be
     * returned as a member of the corresponding wrapper class, such as <code>Integer</code>.
     *
     * @param paramName the name of the parameter to be returned.
     * @throws IllegalArgumentException if paramName is null.
     * @throws IllegalArgumentException if there is no parameter with the specified name.
     * @throws IllegalStateException if the parameter value is still ParameterListDescriptor.NO_PARAMETER_DEFAULT
     */
    public Object getObjectParameter(String paramName);

    /**
     * A convenience method to return a parameter as a <code>byte</code>.
     *
     * @param paramName the name of the parameter to be returned.
     * @throws IllegalArgumentException if paramName is null.
     * @throws IllegalArgumentException if there is no parameter with the specified name.
     * @throws ClassCastException if the parameter is of a different type.
     * @throws IllegalStateException if the parameter value is still ParameterListDescriptor.NO_PARAMETER_DEFAULT
     */
    public byte getByteParameter(String paramName);

    /**
     * A convenience method to return a parameter as a <code>boolean</code>.
     *
     * @param paramName the name of the parameter to be returned.
     * @throws IllegalArgumentException if paramName is null.
     * @throws IllegalArgumentException if there is no parameter with the specified name.
     * @throws ClassCastException if the parameter is of a different type.
     * @throws IllegalStateException if the parameter value is still ParameterListDescriptor.NO_PARAMETER_DEFAULT
     */
    public boolean getBooleanParameter(String paramName);

    /**
     * A convenience method to return a parameter as a <code>char</code>.
     *
     * @param paramName the name of the parameter to be returned.
     * @throws IllegalArgumentException if paramName is null.
     * @throws IllegalArgumentException if there is no parameter with the specified name.
     * @throws ClassCastException if the parameter is of a different type.
     * @throws IllegalStateException if the parameter value is still ParameterListDescriptor.NO_PARAMETER_DEFAULT
     */
    public char getCharParameter(String paramName);

    /**
     * A convenience method to return a parameter as a <code>short</code>.
     *
     * @param paramName the name of the parameter to be returned.
     * @throws IllegalArgumentException if paramName is null.
     * @throws IllegalArgumentException if there is no parameter with the specified name.
     * @throws ClassCastException if the parameter is of a different type.
     * @throws IllegalStateException if the parameter value is still ParameterListDescriptor.NO_PARAMETER_DEFAULT
     */
    public short getShortParameter(String paramName);

    /**
     * A convenience method to return a parameter as an <code>int</code>.
     *
     * @param paramName the name of the parameter to be returned.
     * @throws IllegalArgumentException if paramName is null.
     * @throws IllegalArgumentException if there is no parameter with the specified name.
     * @throws ClassCastException if the parameter is of a different type.
     * @throws IllegalStateException if the parameter value is still ParameterListDescriptor.NO_PARAMETER_DEFAULT
     */
    public int getIntParameter(String paramName);

    /**
     * A convenience method to return a parameter as a <code>long</code>.
     *
     * @param paramName the name of the parameter to be returned.
     * @throws IllegalArgumentException if paramName is null.
     * @throws IllegalArgumentException if there is no parameter with the specified name.
     * @throws ClassCastException if the parameter is of a different type.
     * @throws IllegalStateException if the parameter value is still ParameterListDescriptor.NO_PARAMETER_DEFAULT
     */
    public long getLongParameter(String paramName);

    /**
     * A convenience method to return a parameter as a <code>float</code>.
     *
     * @param paramName the name of the parameter to be returned.
     * @throws IllegalArgumentException if paramName is null.
     * @throws IllegalArgumentException if there is no parameter with the specified name.
     * @throws ClassCastException if the parameter is of a different type.
     * @throws IllegalStateException if the parameter value is still ParameterListDescriptor.NO_PARAMETER_DEFAULT
     */
    public float getFloatParameter(String paramName);

    /**
     * A convenience method to return a parameter as a <code>double</code>.
     *
     * @param paramName the name of the parameter to be returned.
     * @throws IllegalArgumentException if paramName is null.
     * @throws IllegalArgumentException if there is no parameter with the specified name.
     * @throws ClassCastException if the parameter is of a different type.
     * @throws IllegalStateException if the parameter value is still ParameterListDescriptor.NO_PARAMETER_DEFAULT
     */
    public double getDoubleParameter(String paramName);
}
