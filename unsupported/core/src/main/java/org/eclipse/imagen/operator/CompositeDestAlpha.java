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

package org.eclipse.imagen.operator;

import org.eclipse.imagen.EnumeratedParameter;

/**
 * Class used to represent the acceptable values of the "destAlpha" parameter of the "Composite" operation. Acceptable
 * values for the "destAlpha" parameter are defined in the <code>CompositeDescriptor</code> by the constants <code>
 * NO_DESTINATION_ALPHA</code>, <code>DESTINATION_ALPHA_FIRST</code>, and <code>DESTINATION_ALPHA_LAST</code>.
 *
 * @since ImageN 0.4.0
 */
public final class CompositeDestAlpha extends EnumeratedParameter {
    CompositeDestAlpha(String name, int value) {
        super(name, value);
    }
}
