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

package org.eclipse.imagen.media.util;

import java.lang.ref.SoftReference;
import org.eclipse.imagen.OperationRegistry;
import org.eclipse.imagen.util.ImagingListener;

/**
 * A simply implementation of the interface <code> ImagingListener</code>. In the method <code>errorOccurred</code>,
 * only the message and the stack trace of the provided <code>Throwable</code> is printed into the stream <code>
 * System.err</code>. This keeps the backward compatibility.
 *
 * <p>This class is a singleton that has only one instance. This single instance can be retrieved by calling the static
 * method <code>getInstance</code>.
 *
 * @see ImagingListener
 */
public class ImagingListenerImpl implements ImagingListener {
    private static SoftReference reference = new SoftReference(null);

    /**
     * Retrieves the unique instance of this class the construction of which is deferred until the first invocation of
     * this method.
     */
    public static ImagingListenerImpl getInstance() {
        synchronized (reference) {
            Object referent = reference.get();
            ImagingListenerImpl listener;
            if (referent == null) {
                // First invocation or SoftReference has been cleared.
                reference = new SoftReference(listener = new ImagingListenerImpl());
            } else {
                // SoftReference has not been cleared.
                listener = (ImagingListenerImpl) referent;
            }

            return listener;
        }
    }

    /** The constructor. */
    private ImagingListenerImpl() {}

    public synchronized boolean errorOccurred(String message, Throwable thrown, Object where, boolean isRetryable)
            throws RuntimeException {
        // Silent the RuntimeException occuring in any OperationRegistry
        // and rethrown all the other RuntimeExceptions.
        if (thrown instanceof RuntimeException && !(where instanceof OperationRegistry))
            throw (RuntimeException) thrown;

        System.err.println("Error: " + message);
        System.err.println("Occurs in: "
                + ((where instanceof Class)
                        ? ((Class) where).getName()
                        : where.getClass().getName()));
        thrown.printStackTrace(System.err);
        return false;
    }
}
