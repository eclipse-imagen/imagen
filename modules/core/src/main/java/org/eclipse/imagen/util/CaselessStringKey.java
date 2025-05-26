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

package org.eclipse.imagen.util;

import java.io.Serializable;
import java.util.Locale;

/**
 * Class to use as the key in a <code>java.util.Map</code>. The case of the name is maintained but the <code>equals()
 * </code> method performs case-insensitive comparison.
 *
 * @see org.eclipse.imagen.PropertySourceImpl
 * @see java.util.Map
 * @since JAI 1.1
 */
public final class CaselessStringKey implements Cloneable, Serializable {

    private String name;
    private String lowerCaseName;

    /**
     * Creates a <code>CaselessStringKey</code> for the given name. The parameter <code>name</code> is stored by
     * reference.
     *
     * @throws IllegalArgumentException if <code>name</code> is <code>null</code>.
     */
    public CaselessStringKey(String name) {
        setName(name);
    }

    /** Returns a hash code value for the <code>CaselessStringKey</code>. */
    public int hashCode() {
        return lowerCaseName.hashCode();
    }

    /** Returns the internal name by reference. */
    public String getName() {
        return name;
    }

    /** Returns a lower case version of the internal name. */
    private String getLowerCaseName() {
        return lowerCaseName;
    }

    /**
     * Stores the parameter by reference in the internal name.
     *
     * @throws IllegalArgumentException if <code>name</code> is <code>null</code>.
     */
    public void setName(String name) {
        if (name == null) {
            throw new IllegalArgumentException(JaiI18N.getString("CaselessStringKey0"));
        }
        this.name = name;
        lowerCaseName = name.toLowerCase(Locale.ENGLISH);
    }

    /** Returns a clone of the <code>CaselessStringKey</code> as an <code>Object</code>. */
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since object is Cloneable
            throw new InternalError();
        }
    }

    /**
     * Whether another <code>Object</code> equals this one. This will obtain if and only if the parameter is non-<code>
     * null</code> and is a <code>CaselessStringKey</code> whose lower case name equals the lower case name of this
     * <code>CaselessStringKey</code>.
     */
    public boolean equals(Object o) {
        if (o != null && o instanceof CaselessStringKey) {
            return lowerCaseName.equals(((CaselessStringKey) o).getLowerCaseName());
        }
        return false;
    }

    /** Returns the value returned by <code>getName()</code>. */
    public String toString() {
        return getName();
    }
}
