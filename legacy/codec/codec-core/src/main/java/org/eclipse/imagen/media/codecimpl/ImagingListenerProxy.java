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
package org.eclipse.imagen.media.codecimpl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.eclipse.imagen.media.codecimpl.util.ImagingException;

public class ImagingListenerProxy {
    public static synchronized boolean errorOccurred(
            String message, Throwable thrown, Object where, boolean isRetryable) throws RuntimeException {
        Method errorOccurred = null;
        Object listener = null;

        try {
            Class jaiClass = Class.forName("org.eclipse.imagen.ImageN");
            if (jaiClass == null) return defaultImpl(message, thrown, where, isRetryable);

            Method jaiInstance = jaiClass.getMethod("getDefaultInstance", null);
            Method getListener = jaiClass.getMethod("getImagingListener", null);

            Object jai = jaiInstance.invoke(null, null);
            if (jai == null) return defaultImpl(message, thrown, where, isRetryable);

            listener = getListener.invoke(jai, null);
            Class listenerClass = listener.getClass();

            errorOccurred = listenerClass.getMethod(
                    "errorOccurred", new Class[] {String.class, Throwable.class, Object.class, boolean.class});
        } catch (Throwable e) {
            return defaultImpl(message, thrown, where, isRetryable);
        }

        try {
            Boolean result = (Boolean)
                    errorOccurred.invoke(listener, new Object[] {message, thrown, where, new Boolean(isRetryable)});
            return result.booleanValue();
        } catch (InvocationTargetException e) {
            Throwable te = ((InvocationTargetException) e).getTargetException();
            throw new ImagingException(te);
        } catch (Throwable e) {
            return defaultImpl(message, thrown, where, isRetryable);
        }
    }

    private static synchronized boolean defaultImpl(String message, Throwable thrown, Object where, boolean isRetryable)
            throws RuntimeException {
        // Silent the RuntimeException occuring in any OperationRegistry
        // and rethrown all the other RuntimeExceptions.
        if (thrown instanceof RuntimeException) throw (RuntimeException) thrown;

        System.err.println("Error: " + message);
        System.err.println("Occurs in: "
                + ((where instanceof Class)
                        ? ((Class) where).getName()
                        : where.getClass().getName()));
        thrown.printStackTrace(System.err);
        return false;
    }
}
