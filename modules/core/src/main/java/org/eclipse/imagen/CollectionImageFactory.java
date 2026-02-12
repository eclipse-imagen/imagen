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
package org.eclipse.imagen;

import java.awt.RenderingHints;
import java.awt.image.renderable.ParameterBlock;

/**
 * The <code>CollectionImageFactory</code> (CIF) interface is intended to be implemented by classes that wish to act as
 * factories to produce different collection image operators. In ImageN, the <code>create()</code> method will be
 * invoked in a chain of <code>CollectionOp</code>s when the operation is being executed in rendered mode.
 */
public interface CollectionImageFactory {

    /**
     * Creates a <code>CollectionImage</code> that represents the result of an operation (or chain of operations) for a
     * given <code>ParameterBlock</code> and <code>RenderingHints</code>. If the operation is unable to handle the input
     * arguments, this method should return <code>null</code>.
     *
     * <p>Generally this method is expected to be invoked by an operation being executed in rendered mode.
     *
     * @param args Input arguments to the operation, including sources and/or parameters.
     * @param hints The rendering hints.
     * @return A <code>CollectionImage</code> containing the desired output.
     */
    CollectionImage create(ParameterBlock args, RenderingHints hints);

    /**
     * Attempts to modify a rendered <code>CollectionImage</code> previously created by this <code>
     * CollectionImageFactory</code> as a function of how the sources, parameters and hints of the operation have
     * changed. The <code>CollectionImage</code> passed in should not be modified in place but some or or all of its
     * contents may be copied by reference into the <code>CollectionImage</code> returned, if any. If none of the
     * contents of the old <code>CollectionImage</code> can be re-used, then <code>null</code> should be returned.
     *
     * @throws IllegalArgumentException if the name of the operation associated with the <code>CollectionOp</code> does
     *     not match that expected by this <code>CollectionImageFactory</code>.
     * @return A <code>CollectionImage</code> modified according to the new values of the <code>ParameterBlock</code>
     *     and <code>RenderingHints</code> or <code>null</code> if it is impracticable to perform the update.
     */
    CollectionImage update(
            ParameterBlock oldParamBlock,
            RenderingHints oldHints,
            ParameterBlock newParamBlock,
            RenderingHints newHints,
            CollectionImage oldRendering,
            CollectionOp op);
}
