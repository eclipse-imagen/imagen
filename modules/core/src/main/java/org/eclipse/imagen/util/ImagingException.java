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

package org.eclipse.imagen.util;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * This class is designed to contain information of the (abnormal) situations that happen in ImageN and the operations
 * plugged into ImageN. The behavior of this class imitates the <code>Exception</code> class in Java<sup><font
 * size="-2">TM</font></sup> 2 Platform version 1.4 to define a chained exception and is call-compatible with
 * Java<sup><font size="-2">TM</font></sup> 2 Platform version 1.4: The cause can be stored and retrieved from the
 * instance of this class. Also, the root cause for an instance can be retrieved.
 *
 * @since ImageN 0.4.0.2
 */
public class ImagingException extends RuntimeException {
    /** The cached cause. */
    private Throwable cause = null;

    /** The default constructor. */
    public ImagingException() {
        super();
    }

    /**
     * The constructor to accept the message that describes the situation.
     *
     * @param message The message to describe the situation.
     */
    public ImagingException(String message) {
        super(message);
    }

    /**
     * The constructor to accept the cause of this <code>ImagingException</code>.
     *
     * @param cause The cause of this <code>ImagingException</code>.
     */
    public ImagingException(Throwable cause) {
        super();
        this.cause = cause;
    }

    /**
     * The constructor to accept the cause of this <code>ImagingException</code> and the message that describes the
     * situation.
     *
     * @param message The message that describes the situation.
     * @param cause The cause of this <code>ImagingException</code>
     */
    public ImagingException(String message, Throwable cause) {
        super(message);
        this.cause = cause;
    }

    /** Returns the cause of this <code>ImagingException</code>. */
    public Throwable getCause() {
        return cause;
    }

    /** Recursively retrieves the root cause of this <code>ImagingException</code>. */
    public Throwable getRootCause() {
        Throwable rootCause = cause;
        Throwable atop = this;

        while (rootCause != atop && rootCause != null) {
            atop = rootCause;
            rootCause = rootCause.getCause();
            if (rootCause == null) rootCause = atop;
        }

        return rootCause;
    }

    /**
     * Prints the stack trace. For Java<sup><font size="-2">TM</font></sup> 2 Platform version 1.4 and up, calls the
     * same method in the super class. For Java<sup><font size="-2">TM</font></sup> 2 Platform version 1.3, prints the
     * stack trace of this <code>ImagingException</code> and the trace of its cause.
     */
    public void printStackTrace() {
        printStackTrace(System.err);
    }

    /**
     * Prints the stack trace. For Java<sup><font size="-2">TM</font></sup> 2 Platform version 1.4 and up, calls the
     * same method in the super class. For Java<sup><font size="-2">TM</font></sup> 2 Platform version 1.4, prints the
     * stack trace of this <code>ImagingException</code> and the trace of its cause.
     */
    public void printStackTrace(PrintStream s) {

        synchronized (s) {
            super.printStackTrace(s);
            boolean is14 = false;
            try {
                String version = System.getProperty("java.version");
                is14 = version.indexOf("1.4") >= 0;
            } catch (Exception e) {
            }

            if (!is14 && cause != null) {
                s.println("Caused by:");
                cause.printStackTrace(s);
            }
        }
    }

    /**
     * Prints the stack trace. For Java<sup><font size="-2">TM</font></sup> 2 Platform version 1.4 and up, calls the
     * same method in the super class. For Java<sup><font size="-2">TM</font></sup> 2 Platform version 1.43, prints the
     * stack trace of this <code>ImagingException</code> and the trace of its cause.
     */
    public void printStackTrace(PrintWriter s) {
        synchronized (s) {
            super.printStackTrace(s);
            boolean is14 = false;
            try {
                String version = System.getProperty("java.version");
                is14 = version.indexOf("1.4") >= 0;
            } catch (Exception e) {
            }

            if (!is14 && cause != null) {
                s.println("Caused by:");
                cause.printStackTrace(s);
            }
        }
    }
}
