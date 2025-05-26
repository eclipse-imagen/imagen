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

package org.eclipse.imagen.media.codecimpl.util;

import java.text.MessageFormat;
import java.util.Locale;

class JaiI18N {
    static String packageName = "org.eclipse.imagen.media.codecimpl.util";

    public static String getString(String key) {
        return PropertyUtil.getString(packageName, key);
    }

    public static String formatMsg(String key, Object[] args) {
        MessageFormat mf = new MessageFormat(getString(key));
        mf.setLocale(Locale.getDefault());

        return mf.format(args);
    }
}
