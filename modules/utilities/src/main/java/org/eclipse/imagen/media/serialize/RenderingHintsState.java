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
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import org.eclipse.imagen.JAI;

/**
 * This class is a serializable proxy for a RenderingHints object from which the RenderingHints object may be
 * reconstituted.
 *
 * @since 1.1
 */
public class RenderingHintsState extends SerializableStateImpl {
    /** Returns the classes supported by this SerializableState. */
    public static Class[] getSupportedClasses() {
        return new Class[] {RenderingHints.class};
    }

    /**
     * The classes wherein all possible relevant public static RenderingHints.Key objects are defined. Classes which
     * contain declarations of such keys should be added to this array.
     */
    private static final Class[] KEY_CLASSES = {RenderingHints.class, JAI.class};

    /**
     * Instances of keys which should not be serialized. Objects which represent such keys should be added to this
     * array. Presumably such objects would be static and final members of one of the classes in the KEY_CLASSES array.
     */
    private static final Object[] SUPPRESSED_KEYS = {JAI.KEY_OPERATION_REGISTRY, JAI.KEY_TILE_CACHE};

    /** A SoftReference to a Vector of keys which are to be suppressed. */
    private static SoftReference suppressedKeyReference = null;

    /**
     * A SoftReference to a Hashtable containing serializable versions of all public static fields in the classes in the
     * KEY_CLASSES array.
     */
    private static SoftReference hintTableReference = null;

    /**
     * Constructs a <code>RenderingHintsState</code> from a <code>RenderingHints</code> object.
     *
     * @param source The <code>RenderingHints</code> object to be serialized.
     */
    public RenderingHintsState(Class c, Object o, RenderingHints h) {
        super(c, o, h);
    }

    /**
     * An inner class representing either a hint key or a hint value. For a hint key, the name of the class which
     * contains the declaration of this key and the field name of this declaration are recorded. For a value, if it is
     * serializable, the object is recorded. Otherwise, if it is predefined, the class name and the field name are
     * recorded.
     */
    static class HintElement implements Serializable {
        /** The class represents a serializable object. */
        private static final int TYPE_OBJECT = 1;

        /** The class represents an element by class and field name. */
        private static final int TYPE_FIELD = 2;

        /** The type of the element representation. */
        private int type;

        /** The element itself. */
        private Object obj;

        /** The name of the class of the element (type == TYPE_FIELD only). */
        private String className;

        /** The name of the field of the element (type == TYPE_FIELD only). */
        private String fieldName;

        /** Constructs a HintElement representing a serializable object. */
        public HintElement(Object obj) throws NotSerializableException {
            if (!(obj instanceof Serializable)) {
                throw new NotSerializableException();
            }
            type = TYPE_OBJECT;
            this.obj = obj;
        }

        /** Constructs a HintElement representing a public static field. */
        public HintElement(Class cls, Field fld) {
            type = TYPE_FIELD;
            this.className = cls.getName();
            this.fieldName = fld.getName();
        }

        /** Retrieves the element value. Returns null on error. */
        public Object getObject() {
            Object elt = null;

            if (type == TYPE_OBJECT) {
                elt = obj;
            } else if (type == TYPE_FIELD) {
                try {
                    Class cls = Class.forName(className);
                    Field fld = cls.getField(fieldName);
                    elt = fld.get(null);
                } catch (Exception e) {
                    // Do nothing.
                }
            }

            return elt;
        }
    }

    /** Returns a Vector of keys which should not be serialized or null. */
    private static synchronized Vector getSuppressedKeys() {
        Vector suppressedKeys = null;

        if (SUPPRESSED_KEYS != null) {
            // Initialize the Vector to the SoftReference's referent or null.
            suppressedKeys = suppressedKeyReference != null ? (Vector) suppressedKeyReference.get() : null;

            if (suppressedKeys == null) {
                // Cache the number of suppressed keys.
                int numSuppressedKeys = SUPPRESSED_KEYS.length;

                // Load the Vector with the suppressed key objects.
                suppressedKeys = new Vector(numSuppressedKeys);
                for (int i = 0; i < numSuppressedKeys; i++) {
                    suppressedKeys.add(SUPPRESSED_KEYS[i]);
                }

                // Cache the Vector of suppressed keys.
                suppressedKeyReference = new SoftReference(suppressedKeys);
            }
        }

        return suppressedKeys;
    }

    /** Returns a Hashtable wherein the keys are instances of RenderingHints.Key and the values are HintElements. */
    static synchronized Hashtable getHintTable() {
        // Initialize the table to the SoftReference's referent or null.
        Hashtable table = hintTableReference != null ? (Hashtable) hintTableReference.get() : null;

        if (table == null) {
            // Allocate a table for the field values.
            table = new Hashtable();

            for (int i = 0; i < KEY_CLASSES.length; i++) {
                // Cache the class.
                Class cls = KEY_CLASSES[i];

                // Retrieve the fields for this class.
                Field[] fields = cls.getFields();

                // Load the table with the values of all
                // fields with public and static modifiers.
                for (int j = 0; j < fields.length; j++) {
                    Field fld = fields[j];
                    int modifiers = fld.getModifiers();
                    if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers)) {
                        try {
                            Object fieldValue = fld.get(null);
                            table.put(fieldValue, new HintElement(cls, fld));
                        } catch (Exception e) {
                            // Ignore exception.
                        }
                    }
                }
            }

            // Cache the table.
            hintTableReference = new SoftReference(table);
        }

        return table;
    }

    /*
     * The RenderingHints are serialized by creating a Hashtable of key/value
     * pairs wherein the keys are instances of the inner class HintElement and
     * the values are also instances of HintElement or of seriailizable
     * classes.
     */

    /** Serialize the RenderingHintsState. */
    private void writeObject(ObjectOutputStream out) throws IOException {
        // -- Create a serializable form of the RenderingHints object. --
        RenderingHints hints = (RenderingHints) theObject;

        // Create an empty Hashtable.
        Hashtable table = new Hashtable();

        // If there are hints, add them to the table.
        if (hints != null && !hints.isEmpty()) {
            // Get a view of the hints' keys.
            Set keySet = hints.keySet();

            // Proceed if the key set is non-empty.
            if (!keySet.isEmpty()) {
                // Get an iterator for the key set.
                Iterator keyIterator = keySet.iterator();

                // Get the cached hint table.
                Hashtable hintTable = getHintTable();

                // Get the suppressed key Vector.
                Vector suppressedKeys = getSuppressedKeys();

                // Loop over the keys.
                while (keyIterator.hasNext()) {
                    // Get the next key.
                    Object key = keyIterator.next();

                    // Skip this element if the key is suppressed.
                    if (suppressedKeys != null && suppressedKeys.indexOf(key) != -1) {
                        continue;
                    }

                    // Get the field of the key.
                    Object keyElement = SerializerFactory.getState(key, null);

                    // If the key was not in the table it is not a public
                    // static field in one of the KEY_CLASSES so skip it.
                    if (keyElement == null) {
                        continue;
                    }

                    // Get the next value.
                    Object value = hints.get(key);

                    // Pack the key/value pair in a Hashtable entry.
                    HintElement valueElement = null;
                    try {
                        // Try to create a HintElement from the value directly.
                        valueElement = new HintElement(value);
                    } catch (NotSerializableException nse) {
                        // The value is not serializable so try to get a
                        // HintElement from the table in case the value is
                        // a public static field in one of the KEY_CLASSES.
                        valueElement = (HintElement) hintTable.get(value);
                    }

                    // If the value element is valid add it and its key.
                    if (valueElement != null) {
                        table.put(keyElement, valueElement);
                    }
                }
            }
        }

        // Write serialized form to the stream.
        out.writeObject(table);
    }

    /** Deserialize the RenderingHintsState. */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        // Read serialized form from the stream.
        Hashtable table = (Hashtable) in.readObject();

        // Create an empty RenderingHints object.
        RenderingHints hints = new RenderingHints(null);

        theObject = hints;

        // If the table is empty just return.
        if (table.isEmpty()) {
            return;
        }

        // -- Restore the transient RenderingHints object. --

        // Get an enumeration of the table keys.
        Enumeration keys = table.keys();

        // Loop over the table keys.
        while (keys.hasMoreElements()) {
            // Get the next key element.
            SerializableState keyElement = (SerializableState) keys.nextElement();

            // Get the key object corresponding to this key element.
            Object key = keyElement.getObject();

            // Get the value element.
            HintElement valueElement = (HintElement) table.get(keyElement);

            // Get the value object corresponding to this value element.
            Object value = valueElement.getObject();

            // Add an entry to the hints.
            hints.put(key, value);
        }
    }
}
