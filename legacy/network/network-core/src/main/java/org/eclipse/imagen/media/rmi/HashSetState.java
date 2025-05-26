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

import java.awt.RenderingHints;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Iterator;

/**
 * This class is a serializable proxy for a HashSet object. <br>
 * (entries which are neither <code>Serializable</code> nor supported by <code>SerializerFactory</code> are omitted);
 *
 * @since 1.1
 */
public class HashSetState extends SerializableStateImpl {
    /** Returns the classes supported by this SerializableState. */
    public static Class[] getSupportedClasses() {
        return new Class[] {HashSet.class};
    }

    /**
     * Constructs a <code>HashSetState</code> from a <code>HashSet</code> object.
     *
     * @param c The <code>Class</code> of the object to be serialized.
     * @param o The <code>HashSet</code> object to be serialized.
     * @param h The <code>RebderingHint</code> for this serialization.
     */
    public HashSetState(Class c, Object o, RenderingHints h) {
        super(c, o, h);
    }

    /** Serialize the HashSetState. */
    private void writeObject(ObjectOutputStream out) throws IOException {
        // -- Create a serializable form of the HashSet object. --
        HashSet set = (HashSet) theObject;

        HashSet serializableSet = new HashSet();

        // If there are hints, add them to the set.
        if (set != null && !set.isEmpty()) {
            // Get an iterator for the set.
            Iterator iterator = set.iterator();

            // Loop over the set.
            while (iterator.hasNext()) {
                Object object = iterator.next();
                Object serializableObject = getSerializableForm(object);
                serializableSet.add(serializableObject);
            }
        }

        // Write serialized form to the stream.
        out.writeObject(serializableSet);
    }

    /** Deserialize the HashSetState. */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        // Read serialized form from the stream.
        HashSet serializableSet = (HashSet) in.readObject();

        // Create an empty HashSet object.
        HashSet set = new HashSet();

        // If the set is empty just return.
        if (serializableSet.isEmpty()) {
            theObject = set;
            return;
        }

        Iterator iterator = serializableSet.iterator();

        // Loop over the set keys.
        while (iterator.hasNext()) {
            // Get the next key element.
            Object serializableObject = iterator.next();
            Object object = getDeserializedFrom(serializableObject);

            // Add an entry to the set.
            set.add(object);
        }

        theObject = set;
    }
}
