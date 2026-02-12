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

import java.awt.RenderingHints;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

/**
 * This class is a serializable proxy for a Hashtable object. <br>
 * (entries which are neither <code>Serializable</code> nor supported by <code>SerializerFactory</code> are omitted);
 *
 * @since 1.1
 */
public class HashtableState extends SerializableStateImpl {
    /** Returns the classes supported by this SerializableState. */
    public static Class[] getSupportedClasses() {
        return new Class[] {Hashtable.class};
    }

    /**
     * Constructs a <code>HashtableState</code> from a <code>Hashtable</code> object.
     *
     * @param c The <code>Class</code> of the object to be serialized.
     * @param o The <code>Hashtable</code> object to be serialized.
     * @param h The <code>RenderingHints</code> for this serialization.
     */
    public HashtableState(Class c, Object o, RenderingHints h) {
        super(c, o, h);
    }

    /** Serialize the HashtableState. */
    private void writeObject(ObjectOutputStream out) throws IOException {
        // -- Create a serializable form of the Hashtable object. --
        Hashtable table = (Hashtable) theObject;

        Hashtable serializableTable = new Hashtable();

        // If there are hints, add them to the table.
        if (table != null && !table.isEmpty()) {
            // Get a view of the hints' keys.
            Set keySet = table.keySet();

            // Proceed if the key set is non-empty.
            if (!keySet.isEmpty()) {
                // Get an iterator for the key set.
                Iterator keyIterator = keySet.iterator();

                // Loop over the keys.
                while (keyIterator.hasNext()) {
                    // Get the next key.
                    Object key = keyIterator.next();
                    Object serializableKey = getSerializableForm(key);

                    if (serializableKey == null) continue;

                    // Get the next value.
                    Object value = table.get(key);

                    Object serializableValue = getSerializableForm(value);

                    if (serializableValue == null) continue;

                    serializableTable.put(serializableKey, serializableValue);
                }
            }
        }

        // Write serialized form to the stream.
        out.writeObject(serializableTable);
    }

    /** Deserialize the HashtableState. */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        // Read serialized form from the stream.
        Hashtable serializableTable = (Hashtable) in.readObject();

        // Create an empty Hashtable object.
        Hashtable table = new Hashtable();
        theObject = table;

        // If the table is empty just return.
        if (serializableTable.isEmpty()) {
            return;
        }

        // Get an enumeration of the table keys.
        Enumeration keys = serializableTable.keys();

        // Loop over the table keys.
        while (keys.hasMoreElements()) {
            // Get the next key element.
            Object serializableKey = keys.nextElement();
            Object key = getDeserializedFrom(serializableKey);

            // Get the value element.
            Object serializableValue = serializableTable.get(serializableKey);
            Object value = getDeserializedFrom(serializableValue);

            // Add an entry to the table.
            table.put(key, value);
        }
    }
}
