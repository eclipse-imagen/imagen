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

package org.eclipse.imagen.media.serialize;

import java.awt.RenderingHints;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.Vector;

/**
 * This class is a serializable proxy for a Vector object. <br>
 * (entries which are neither <code>Serializable</code> nor supported by <code>SerializerFactory</code> are omitted);
 *
 * @since 1.1
 */
public class VectorState extends SerializableStateImpl {
    /** Returns the classes supported by this SerializableState. */
    public static Class[] getSupportedClasses() {
        return new Class[] {Vector.class};
    }

    /**
     * Constructs a <code>VectorState</code> from a <code>Vector</code> object.
     *
     * @param c The <code>Class</code> of the object to be serialized.
     * @param o The <code>Vector</code> object to be serialized.
     * @param h The <code>RebderingHint</code> for this serialization.
     */
    public VectorState(Class c, Object o, RenderingHints h) {
        super(c, o, h);
    }

    /** Serialize the VectorState. */
    private void writeObject(ObjectOutputStream out) throws IOException {
        // -- Create a serializable form of the Vector object. --
        Vector vector = (Vector) theObject;
        Vector serializableVector = new Vector();
        Iterator iterator = vector.iterator();

        // If there are hints, add them to the vector.
        while (iterator.hasNext()) {
            Object object = iterator.next();
            Object serializableObject = getSerializableForm(object);
            serializableVector.add(serializableObject);
        }

        // Write serialized form to the stream.
        out.writeObject(serializableVector);
    }

    /** Deserialize the VectorState. */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        // Read serialized form from the stream.
        Vector serializableVector = (Vector) in.readObject();

        // Create an empty Vector object.
        Vector vector = new Vector();
        theObject = vector;

        // If the vector is empty just return.
        if (serializableVector.isEmpty()) {
            return;
        }

        // Get an enumeration of the vector keys.
        Iterator iterator = serializableVector.iterator();

        // Loop over the vector keys.
        while (iterator.hasNext()) {
            // Get the next key element.
            Object serializableObject = iterator.next();
            Object object = getDeserializedFrom(serializableObject);

            // Add an entry to the vector.
            vector.add(object);
        }
    }
}
