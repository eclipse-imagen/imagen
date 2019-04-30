/*
 * Copyright (c) [2019,] 2019, Oracle and/or its affiliates. All rights reserved.
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

package org.eclipse.imagen.operator;

import org.eclipse.imagen.EnumeratedParameter;

/**
 * Class used to represent the acceptable values of the "shearDir"
 * parameter of the "Shear" operation.  Acceptable values for the
 * "shearDir" parameter are defined in the <code>ShearDescriptor</code>
 * by the constants <code>SHEAR_HORIZONTAL</code> and
 * <code>SHEAR_VERTICAL</code>.
 *
 * @since JAI 1.1
 */
public final class ShearDir extends EnumeratedParameter {
    ShearDir(String name, int value) {
        super(name, value);
    }
}
