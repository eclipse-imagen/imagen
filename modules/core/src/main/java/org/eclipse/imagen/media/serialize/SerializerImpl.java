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
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import org.eclipse.imagen.media.util.ImageUtil;
import org.eclipse.imagen.util.ImagingException;
import org.eclipse.imagen.util.ImagingListener;

/**
 * Framework class for automatically creating <code>Serializer</code>s for <code>SerializableStateImpl</code>
 * subclasses. Each subclass of <code>SerializableStateImpl</code> should add a statement like
 *
 * <pre>
 *       registerSerializers(MySerializableState.class);
 * </pre>
 *
 * to the no-argument version of <code>registerSerializers()</code>. This latter method is invoked by the static
 * initializer of <code>SerializerFactory</code>.
 *
 * @since 1.1
 */
public final class SerializerImpl implements Serializer {
    private Class theClass;
    private boolean areSubclassesPermitted;
    private Constructor ctor;

    /** Registers all known <code>Serializer</code>s with the <code>SerializerFactory</code>. */
    public static final void registerSerializers() {
        registerSerializers(ColorModelState.class);
        registerSerializers(DataBufferState.class);
        registerSerializers(HashSetState.class);
        registerSerializers(HashtableState.class);
        registerSerializers(RasterState.class);
        registerSerializers(RenderContextState.class);
        registerSerializers(RenderingHintsState.class);
        registerSerializers(RenderingKeyState.class);
        registerSerializers(SampleModelState.class);
        registerSerializers(VectorState.class);
        registerSerializers(ShapeState.class);
    }

    public static void registerSerializers(Class ssi) {
        if (ssi == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }

        if (!SerializableStateImpl.class.isAssignableFrom(ssi)) {
            throw new IllegalArgumentException(JaiI18N.getString("SerializerImpl0"));
        }

        ImagingListener listener = ImageUtil.getImagingListener((RenderingHints) null);
        Class[] classes = null;
        try {
            Method m1 = ssi.getMethod("getSupportedClasses", null);
            classes = (Class[]) m1.invoke(null, null);
        } catch (java.lang.NoSuchMethodException e) {
            String message = JaiI18N.getString("SerializerImpl1");
            listener.errorOccurred(message, new ImagingException(message, e), SerializerImpl.class, false);
        } catch (java.lang.IllegalAccessException e) {
            String message = JaiI18N.getString("SerializerImpl1");
            listener.errorOccurred(message, new ImagingException(message, e), SerializerImpl.class, false);
        } catch (java.lang.reflect.InvocationTargetException e) {
            String message = JaiI18N.getString("SerializerImpl1");
            listener.errorOccurred(message, new ImagingException(message, e), SerializerImpl.class, false);
        }

        boolean supportsSubclasses = false;
        try {
            Method m2 = ssi.getMethod("permitsSubclasses", null);
            Boolean b = (Boolean) m2.invoke(null, null);
            supportsSubclasses = b.booleanValue();
        } catch (java.lang.NoSuchMethodException e) {
            String message = JaiI18N.getString("SerializerImpl4");
            listener.errorOccurred(message, new ImagingException(message, e), SerializerImpl.class, false);
        } catch (java.lang.IllegalAccessException e) {
            String message = JaiI18N.getString("SerializerImpl4");
            listener.errorOccurred(message, new ImagingException(message, e), SerializerImpl.class, false);
        } catch (java.lang.reflect.InvocationTargetException e) {
            String message = JaiI18N.getString("SerializerImpl4");
            listener.errorOccurred(message, new ImagingException(message, e), SerializerImpl.class, false);
        }

        int numClasses = classes.length;
        for (int i = 0; i < numClasses; i++) {
            Serializer s = new SerializerImpl(ssi, classes[i], supportsSubclasses);
            SerializerFactory.registerSerializer(s);
        }
    }

    /**
     * Constructs a <code>SerializerImpl</code>. The parameter <code>c</code> is saved by reference. The parameter
     * <code>c</code> is used to determine the standard <code>SerializableStateImpl</code> constructor which is saved by
     * reference. The supplied parameters are not checked as this class should never be instantiated except from within
     * <code>registerSerializers(Class)</code>.
     */
    protected SerializerImpl(
            Class ssi, // SerializableStateImpl subclass
            Class c,
            boolean areSubclassesPermitted) {
        theClass = c;
        this.areSubclassesPermitted = areSubclassesPermitted;

        try {
            Class[] paramTypes = new Class[] {Class.class, Object.class, RenderingHints.class};
            ctor = ssi.getConstructor(paramTypes);
        } catch (java.lang.NoSuchMethodException e) {
            String message = theClass.getName() + ": " + JaiI18N.getString("SerializerImpl2");
            sendExceptionToListener(message, new ImagingException(message, e));
        }
    }

    /**
     * Creates a <code>SerializableState</code> using the <code>SerializableStateImpl</code> subclass constructor
     * obtained by reflection.
     */
    public SerializableState getState(Object o, RenderingHints h) {
        Object state = null;
        try {
            state = ctor.newInstance(new Object[] {theClass, o, h});
        } catch (InstantiationException e) {
            String message = theClass.getName() + ": " + JaiI18N.getString("SerializerImpl3");
            sendExceptionToListener(message, new ImagingException(message, e));
        } catch (IllegalAccessException e) {
            String message = theClass.getName() + ": " + JaiI18N.getString("SerializerImpl3");
            sendExceptionToListener(message, new ImagingException(message, e));
        } catch (java.lang.reflect.InvocationTargetException e) {
            String message = theClass.getName() + ": " + JaiI18N.getString("SerializerImpl3");
            sendExceptionToListener(message, new ImagingException(message, e));
        }

        return (SerializableState) state;
    }

    public Class getSupportedClass() {
        return theClass;
    }

    public boolean permitsSubclasses() {
        return areSubclassesPermitted;
    }

    private void sendExceptionToListener(String message, Exception e) {
        ImagingListener listener = ImageUtil.getImagingListener((RenderingHints) null);
        listener.errorOccurred(message, new ImagingException(message, e), this, false);
    }
}
