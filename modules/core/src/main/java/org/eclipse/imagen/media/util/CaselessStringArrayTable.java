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
package org.eclipse.imagen.media.util;

import java.util.Hashtable;
import org.eclipse.imagen.util.CaselessStringKey;

/**
 * A class that maps an array of <code>String</code>s or <code>CaselessStringKey</code>s into the array indices and vice
 * versa (all in a caseless fashion).
 *
 * <p>This is used to map source names and parameter names to their indices in a case insensitive fashion.
 */
public class CaselessStringArrayTable implements java.io.Serializable {

    private CaselessStringKey[] keys;
    private Hashtable indices;

    /** Constructor for an array of <code>CaselessStringKey</code>s. */
    public CaselessStringArrayTable() {
        this((CaselessStringKey[]) null);
    }

    /** Constructor for an array of <code>CaselessStringKey</code>s. */
    public CaselessStringArrayTable(CaselessStringKey[] keys) {

        this.keys = keys;
        this.indices = new Hashtable();

        if (keys != null)
            for (int i = 0; i < keys.length; i++) {
                this.indices.put(keys[i], new Integer(i));
            }
    }

    /** Constructor for an array of <code>String</code>s. */
    public CaselessStringArrayTable(String[] keys) {
        this(toCaselessStringKey(keys));
    }

    /** Map an array of <code>String</code>s to <code>CaselessStringKey</code>s. */
    private static CaselessStringKey[] toCaselessStringKey(String strings[]) {
        if (strings == null) return null;

        CaselessStringKey[] keys = new CaselessStringKey[strings.length];

        for (int i = 0; i < strings.length; i++) keys[i] = new CaselessStringKey(strings[i]);

        return keys;
    }

    /**
     * Get the index of the specified key.
     *
     * @throws IllegalArgumentException if the key is <code>null or
     *	if the key is not found.
     */
    public int indexOf(CaselessStringKey key) {
        if (key == null) {
            throw new IllegalArgumentException(JaiI18N.getString("CaselessStringArrayTable0"));
        }

        Integer i = (Integer) indices.get(key);

        if (i == null) {
            throw new IllegalArgumentException(key.getName() + " - " + JaiI18N.getString("CaselessStringArrayTable1"));
        }

        return i.intValue();
    }

    /**
     * Get the index of the specified key.
     *
     * @throws IllegalArgumentException if the key is <code>null or
     *	if the key is not found.
     */
    public int indexOf(String key) {
        return indexOf(new CaselessStringKey(key));
    }

    /**
     * Get the <code>String</code> corresponding to the index <code>i</code>.
     *
     * @throws ArrayIndexOutOfBoundsException if <code>i</code> is out of range.
     */
    public String getName(int i) {
        if (keys == null) throw new ArrayIndexOutOfBoundsException();

        return keys[i].getName();
    }

    /**
     * Get the <code>CaselessStringKey</code> corresponding to the index <code>i</code>.
     *
     * @throws ArrayIndexOutOfBoundsException if <code>i</code> is out of range.
     */
    public CaselessStringKey get(int i) {
        if (keys == null) throw new ArrayIndexOutOfBoundsException();

        return keys[i];
    }

    /**
     * Tests if this table contains the specified key.
     *
     * @return true if the key is present. false otherwise.
     */
    public boolean contains(CaselessStringKey key) {
        if (key == null) {
            throw new IllegalArgumentException(JaiI18N.getString("CaselessStringArrayTable0"));
        }

        return indices.get(key) != null;
    }

    /**
     * Tests if this table contains the specified key.
     *
     * @return true if the key is present. false otherwise.
     */
    public boolean contains(String key) {
        return contains(new CaselessStringKey(key));
    }
}
