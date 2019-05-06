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
import java.io.Serializable;
import java.lang.reflect.Method;
import org.eclipse.imagen.remote.SerializableState;
import org.eclipse.imagen.remote.SerializerFactory;

/**
 * Framework class for adding <code>Serializer</code>s based on
 * <code>SerializableState</code> implementations which support one or
 * more classes or interfaces.
 *
 * <p> Extending classes MUST:
 * <ol>
 * <li> be public;</li>
 * <li> provide a single public constructor with exactly the same signature as
 * the protected constructor of this class;</li>
 * <li> provide a static override of <code>getSupportedClasses()</code>;</li>
 * <li> implement the (de)serialization methods <code>writeObject()</code>
 * and <code>readObject()</code>; and</li>
 * <li> add the class to <code>SerializerImpl.registerSerializers()</code> as
 * <pre>
 *       registerSerializers(MySerializableState.class);
 * </pre></li>
 * </ol>
 *
 * @since 1.1
 */
public abstract class SerializableStateImpl implements SerializableState {
    protected Class theClass;
    protected transient Object theObject;

    /**
     * Returns the classes supported by this SerializableState.
     * Subclasses MUST override this method with their own STATIC method.
     */
    public static Class[] getSupportedClasses() {
        throw new RuntimeException(JaiI18N.getString("SerializableStateImpl0"));
    }

    /**
     * Whether the SerializableStateImpl permits its Serializer to
     * serialize subclasses of the supported class(es).
     * Subclasses SHOULD override this method to return "true" with their
     * own STATIC method IF AND ONLY IF they support subclass serialization.
     */
    public static boolean permitsSubclasses() {
        return false;
    }

    /**
     * Constructor.  All subclasses MUST have exactly ONE constructor with
     * the SAME signature as this constructor.
     */
    protected SerializableStateImpl(Class c, Object o, RenderingHints h) {
        if (c == null || o == null) {
            throw new IllegalArgumentException(JaiI18N.getString("SerializableStateImpl1"));
        } else {
            boolean isInterface = c.isInterface();
            if (isInterface && !c.isInstance(o)) {
                throw new IllegalArgumentException(JaiI18N.getString("SerializableStateImpl2"));
            } else if (!isInterface) {
                boolean permitsSubclasses = false;
                try {
                    Method m =
                        this.getClass().getMethod("permitsSubclasses", null);
                    permitsSubclasses 
                        = ((Boolean)m.invoke(null, null)).booleanValue();
                } catch (Exception e){
                    throw new IllegalArgumentException(JaiI18N.getString("SerializableStateImpl5"));
                }

                if (!permitsSubclasses && !c.equals(o.getClass())) {
                    throw new IllegalArgumentException(JaiI18N.getString("SerializableStateImpl3"));
                } else if (permitsSubclasses &&
                           !c.isAssignableFrom(o.getClass())) {
                    throw new IllegalArgumentException(JaiI18N.getString("SerializableStateImpl4"));
                }
            }
        }
        theClass = c;
        theObject = o;
    }

    public Class getObjectClass() {
        return theClass;
    }

    public Object getObject() {
        return theObject;
    }

    protected Object getSerializableForm(Object object) {
        if (object instanceof Serializable)
            return object;
	if (object != null)
	    try {
		object = SerializerFactory.getState(object, null);
	    } catch (Exception e) {
		object = null;
	    }
        return object;
    }

    protected Object getDeserializedFrom(Object object) {
        if (object instanceof SerializableState)
            object = ((SerializableState)object).getObject();
        return object;
    }
}
