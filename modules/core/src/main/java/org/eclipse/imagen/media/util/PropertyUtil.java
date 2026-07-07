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
package org.eclipse.imagen.media.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.stream.Stream;

public class PropertyUtil {

    private static final Hashtable<String, ResourceBundle> bundles = new Hashtable<>();
    private static final String propertiesFile = "org.eclipse.imagen/%s.properties";

    public static InputStream getFileFromClasspath(Connector connector, String path)
            throws IOException, FileNotFoundException {
        InputStream is;

        is = connector.open(path);
        if (is != null) {
            return is;
        }
        is = connector.open("/" + path);
        if (is != null) {
            return is;
        }

        return null;
    }

    /** Get bundle from .properties files in org.eclipse.imagen dir. */
    private static ResourceBundle getBundle(Connector connector, String packageName) {

        try (InputStream in = getFileFromClasspath(connector, propertiesFile.formatted(packageName))) {
            if (in != null) {
                ResourceBundle bundle = new PropertyResourceBundle(in);
                bundles.put(packageName, bundle);
                return bundle;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getString(Connector connector, String packageName, String key) {
        ResourceBundle b = bundles.get(packageName);
        if (b == null) {
            b = getBundle(connector, packageName);
        }
        return b.getString(key);
    }

    /**
     * Utility method to search the full list of property names for matches. If <code>propertyNames</code> is <code>null
     * </code> then <code>null</code> is returned.
     *
     * @exception IllegalArgumentException if <code>prefix</code> is <code>null</code> and <code>propertyNames</code> is
     *     non-<code>null</code>.
     */
    public static String[] getPropertyNames(String[] propertyNames, String prefix) {
        if (propertyNames == null) {
            return null;
        }
        if (prefix == null) {
            throw new IllegalArgumentException(JaiI18N.getString("PropertyUtil0"));
        }
        String lowerCasePrefix = prefix.toLowerCase();
        return Stream.of(propertyNames)
                .filter(name -> name.toLowerCase().startsWith(lowerCasePrefix))
                .toArray(String[]::new);
    }
}
