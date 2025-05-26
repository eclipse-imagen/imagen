/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 *
 * This Example Content is intended to demonstrate usage of Eclipse technology. It is
 * provided to you under the terms and conditions of the Eclipse Distribution License
 * v1.0 which is available at http://www.eclipse.org/org/documents/edl-v10.php.
 */
package org.eclipse.imagen.demo.medical;

import java.util.Locale;
import java.util.ResourceBundle;

/** The class for inetrnationalization. */
class JaiI18N {
    static String packageName = "";
    private static ResourceBundle resources = ResourceBundle.getBundle("MedicalApp", Locale.getDefault());

    /** Return a property defined in the property file. */
    public static String getString(String key) {
        String s = resources.getString(key);

        // If the string is quoted, remove the quotation mark.
        // Sometimes, put the quotation marks are used to reserve more space
        // by adding white charaters.
        if (s.startsWith("\"")) s = s.substring(1, s.length() - 1);
        return s;
    }
}
