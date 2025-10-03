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
 * Class used to represent the acceptable values of the "scalingType" parameter of the "DFT" and "IDFT" operations.
 * Acceptable values for the "scalingType" parameter are defined in the <code>DFTDescriptor</code> and <code>
 * IDFTDescriptor</code> by the constants <code>SCALING_NONE</code>, <code>SCALING_UNITARY</code>, and <code>
 * SCALING_DIMENSIONS</code>.
 */
public final class DFTScalingType extends EnumeratedParameter {
    DFTScalingType(String name, int value) {
        super(name, value);
    }
}
