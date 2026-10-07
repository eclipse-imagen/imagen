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

import java.util.ResourceBundle;

/** Labels and layout settings for the medical demo, read from {@code messages.properties}. */
final class MedicalAppMessages {

    private static final String BUNDLE_NAME = "org.eclipse.imagen.demo.medical.messages";

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    private MedicalAppMessages() {}

    static String getString(String key) {
        String s = RESOURCE_BUNDLE.getString(key);
        if (s.startsWith("\"")) {
            s = s.substring(1, s.length() - 1);
        }
        return s;
    }
}
