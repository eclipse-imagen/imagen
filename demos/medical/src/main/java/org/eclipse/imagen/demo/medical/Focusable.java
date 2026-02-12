/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 *
 * This Example Content is intended to demonstrate usage of Eclipse technology. It is
 * provided to you under the terms and conditions of the Eclipse Distribution License
 * v1.0 which is available at http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */
package org.eclipse.imagen.demo.medical;

/**
 * An interface for objects that gain and loss focus. This interface is defined to process the focus-related events and
 * get the focus status of the object.
 */
interface Focusable {
    /** Process focus-related events. */
    void setFocused(boolean b);

    /** Get the focus status. */
    boolean isFocused();
}
