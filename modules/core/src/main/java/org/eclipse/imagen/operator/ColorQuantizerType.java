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
 * Class used to represent the acceptable values of the "quantizationAlgorithm" parameter of the "ColorQuantizer"
 * operation. Acceptable values for the "quantizationAlgorithm" parameter are defined in the <code>
 * ColorQuantizerDescriptor</code> by the constants <code>MEDIANCUT</code>, <code>NEUQUANT</code>, and <code>OCTTREE
 * </code>.
 *
 * @see ColorQuantizerDescriptor
 * @since ImageN 0.4.0.2
 */
public final class ColorQuantizerType extends EnumeratedParameter {
    ColorQuantizerType(String name, int value) {
        super(name, value);
    }
}
