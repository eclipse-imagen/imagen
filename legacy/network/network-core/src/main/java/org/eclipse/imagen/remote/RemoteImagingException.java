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

package org.eclipse.imagen.remote;

import org.eclipse.imagen.util.ImagingException;

/**
 * <code>RemoteImagingException</code> is an <code>Exception</code> thrown to indicate that an error condition was
 * encountered during remote image processing. All methods which might encounter error conditions during remote image
 * processing should be tagged as throwing this exception, as this is something an application might want to catch.
 *
 * <p>From ImageN 0.4.0 on, this class is re-parented to <code>ImagingException</code> which behaves as a chained
 * exception. Thus, the cause of a <code>RemoteImagingException</code> can be retrieved by using the method <code>
 * getCause</code>.
 *
 * @since ImageN 0.4.0
 */
public class RemoteImagingException extends ImagingException {

    /**
     * Constructs a <code>RemoteImagingException</code> with no detail message. A detail message is a <code>String
     * </code> that describes this particular exception.
     */
    public RemoteImagingException() {
        super();
    }

    /**
     * Constructs a <code>RemoteImagingException</code> with the specified detail message. A detail message is a <code>
     * String</code> that describes this particular exception.
     *
     * @param message the <code>String</code> that contains a detailed message.
     */
    public RemoteImagingException(String message) {
        super(message);
    }

    /**
     * Constructs a <code>RemoteImagingException</code> with the provided cause.
     *
     * @param cause The cause of this <code>RemoteImagingException</code>.
     * @since ImageN 0.4.0.2
     */
    public RemoteImagingException(Throwable cause) {
        super(cause);
    }

    /**
     * The constructor to accept the cause of this <code>RemoteImagingException</code> and the message that describes
     * the situation.
     *
     * @param message The message that describes the situation.
     * @param cause The cause of this <code>RemoteImagingException</code>
     * @since ImageN 0.4.0.2
     */
    public RemoteImagingException(String message, Throwable cause) {
        super(message, cause);
    }
}
