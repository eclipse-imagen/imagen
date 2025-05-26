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

import org.eclipse.imagen.util.Range;

/** A class that signifies that a parameter has no default value. */
class ParameterNoDefault implements java.io.Serializable {
    ParameterNoDefault() {}

    public String toString() {
        return "No Parameter Default";
    }
}

/**
 * This interface provides a comprehensive description of a set of parameters including parameter names, parameter
 * defaults, valid parameter value ranges, etc.
 *
 * <p>The parameter names should be used in a case retentive manner. i.e. all lookups and comparisons are
 * case-insensitive but any request for a parameter name should return the original name with the case preserved.
 *
 * @see ParameterList
 * @since JAI 1.1
 */
public interface ParameterListDescriptor {

    /** An <code>Object</code> that signifies that a parameter has no default value. */
    public static final Object NO_PARAMETER_DEFAULT = ParameterNoDefault.class;

    /** Returns the total number of parameters. */
    int getNumParameters();

    /**
     * Returns an array of <code>Class</code>es that describe the types of parameters. If there are no parameters, this
     * method returns <code>null</code>.
     */
    Class[] getParamClasses();

    /**
     * Returns an array of <code>String</code>s that are the names of the parameters associated with this descriptor. If
     * there are no parameters, this method returns <code>null</code>.
     */
    String[] getParamNames();

    /**
     * Returns an array of <code>Object</code>s that define the default values of the parameters. Since <code>null
     * </code> might be a valid parameter value, the <code>NO_PARAMETER_DEFAULT</code> static <code>Object</code> is
     * used to indicate that a parameter has no default value. If there are no parameters, this method returns <code>
     * null</code>.
     */
    Object[] getParamDefaults();

    /**
     * Returns the default value of a specified parameter. The default value may be <code>null</code>. If a parameter
     * has no default value, this method returns <code>NO_PARAMETER_DEFAULT</code>.
     *
     * @param parameterName The name of the parameter whose default value is queried.
     * @throws IllegalArgumentException if <code>parameterName</code> is null or if the parameter does not exist.
     */
    Object getParamDefaultValue(String parameterName);

    /**
     * Returns the <code>Range</code> that represents the range of valid values for the specified parameter. Returns
     * <code>null</code> if the parameter can take on any value or if the valid values are not representable as a Range.
     *
     * @param parameterName The name of the parameter whose valid range of values is to be determined.
     * @throws IllegalArgumentException if <code>parameterName</code> is null or if the parameter does not exist.
     */
    Range getParamValueRange(String parameterName);

    /**
     * Return an array of the names of all parameters the type of which is <code>EnumeratedParameter</code>.
     *
     * @return The requested array of names or <code>null</code> if there are no parameters with <code>
     *     EnumeratedParameter</code> type.
     */
    String[] getEnumeratedParameterNames();

    /**
     * Return an array of <code>EnumeratedParameter</code> objects corresponding to the parameter with the specified
     * name.
     *
     * @param parameterName The name of the parameter for which the <code>EnumeratedParameter</code> array is to be
     *     returned.
     * @throws IllegalArgumentException if <code>parameterName</code> is null or if the parameter does not exist.
     * @throws UnsupportedOperationException if there are no enumerated parameters associated with the descriptor.
     * @throws IllegalArgumentException if <code>parameterName</code> is a parameter the class of which is not a
     *     subclass of <code>EnumeratedParameter</code>.
     * @return An array of <code>EnumeratedParameter</code> objects representing the range of values for the named
     *     parameter.
     */
    EnumeratedParameter[] getEnumeratedParameterValues(String parameterName);

    /**
     * Checks to see whether the specified parameter can take on the specified value.
     *
     * @param parameterName The name of the parameter for which the validity check is to be performed.
     * @throws IllegalArgumentException if <code>parameterName</code> is null or if the parameter does not exist.
     * @throws IllegalArgumentException if the class of the object "value" is not an instance of the class type of
     *     parameter pointed to by the parameterName
     * @return true, if it is valid to pass this value in for this parameter, false otherwise.
     */
    boolean isParameterValueValid(String parameterName, Object value);
}
