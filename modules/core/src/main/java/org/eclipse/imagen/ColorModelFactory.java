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

package org.eclipse.imagen;

import java.awt.image.ColorModel;
import java.awt.image.SampleModel;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Interface defining a callback which may be used to create a <code>ColorModel</code> for the rendering of a node in an
 * operation chain. The value corresponding to the key {@link ImageN#KEY_COLOR_MODEL_FACTORY} in a configuration mapping
 * must be of type <code>ColorModelFactory</code>. This configuration variable is recognized by the constructor
 * {@link OpImage#OpImage(Vector,ImageLayout,Map,boolean)}.
 */
public interface ColorModelFactory {
    /**
     * Create a <code>ColorModel</code> given the image <code>SampleModel</code> and configuration variables. When
     * invoked in the context of {@link OpImage#OpImage(Vector,ImageLayout,Map,boolean)}, the <code>SampleModel</code>
     * will be that of the <code>OpImage</code> and the source list and configuration mapping will be those which were
     * supplied to the <code>OpImage</code> constructor.
     *
     * <p>The implementing class should in general ensure that the <code>ColorModel</code> created is compatible with
     * the supplied <code>SampleModel</code>. If it is known a priori that compatibility is verified by the object which
     * invokes this method, then such compatibility verification might be safely omitted.
     *
     * @param sampleModel The <code>SampleModel</code> to which the <code>ColorModel</code> to be created must
     *     correspond; may <b>not</b> be <code>null</code>.
     * @param sources A <code>List</code> of <code>RenderedImage</code>s; may be <code>null</code>.
     * @param configuration A configuration mapping; may be <code>null</code>.
     * @return A new <code>ColorModel</code> or <code>null</code> if it is not possible for the <code>ColorModelFactory
     *     </code> to create a <code>ColorModel</code> for the supplied parameters.
     * @exception IllegalArgumentException if <code>sampleModel</code> is <code>null</code>.
     */
    ColorModel createColorModel(SampleModel sampleModel, List sources, Map configuration);
}
