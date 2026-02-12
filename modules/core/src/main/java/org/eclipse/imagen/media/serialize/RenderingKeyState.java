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
import java.util.Hashtable;

/**
 * This class is a serializable proxy for the predefined <code>
 * RenderingHints.Key</code> objects. For a hint key, the name of the class which contains the declaration of this key
 * and the field name of this declaration are recorded.
 *
 * @since 1.1
 */
public class RenderingKeyState extends SerializableStateImpl {
    /** Returns the classes supported by this SerializableState. */
    public static Class[] getSupportedClasses() {
        return new Class[] {RenderingHints.Key.class};
    }

    /** Support subclasses as Raster is a factory class. */
    public static boolean permitsSubclasses() {
        return true;
    }

    private transient RenderingHintsState.HintElement predefinedKey;

    /**
     * Constructs a <code>RenderingKeyState</code> from a <code>RenderingHints.Key</code> object.
     *
     * @param c The <code>Class</code> of the object to be serialized.
     * @param o The object to be serialized.
     * @param h The <code>RenderingHints</code> used in serialization.
     */
    public RenderingKeyState(Class c, Object o, RenderingHints h) {
        super(c, o, h);

        Hashtable predefinedObjects = RenderingHintsState.getHintTable();

        predefinedKey = (RenderingHintsState.HintElement) predefinedObjects.get(o);

        if (predefinedKey == null) throw new RuntimeException(JaiI18N.getString("RenderingKeyState0"));
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(predefinedKey);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        predefinedKey = (RenderingHintsState.HintElement) in.readObject();
        theObject = predefinedKey.getObject();
    }
}
