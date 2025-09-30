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

package org.eclipse.imagen.media.mosaic;

import org.eclipse.imagen.EnumeratedParameter;

/**
 * Class used to represent the acceptable values of the "mosaicType" parameter of the "Mosaic" operation. Acceptable
 * values for the "maskShape" parameter are defined in the {@link MosaicDescriptor} by the constants
 * {@link MosaicDescriptor#MOSAIC_TYPE_BLEND} and {@link MosaicDescriptor#MOSAIC_TYPE_OVERLAY}.
 */
public final class MosaicType extends EnumeratedParameter {
    MosaicType(String name, int value) {
        super(name, value);
    }
}
