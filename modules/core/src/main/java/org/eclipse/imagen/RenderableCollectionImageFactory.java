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

import java.awt.image.renderable.ParameterBlock;

/**
 * The <code>RenderableCollectionImageFactory</code> (RCIF) interface is intended to be implemented by classes that wish
 * to act as factories to produce different collection image operators. In ImageN, the <code>create()</code> method
 * defined by this interface will be invoked in a chain of <code>CollectionOp</code>s when the operation is being
 * executed in renderable mode. The images contained in the generated <code>CollectionImage</code> would be expected to
 * be <code>
 * RenderableImage</code>s.
 *
 * @since ImageN 0.4.0
 */
public interface RenderableCollectionImageFactory {

    /**
     * Creates a <code>CollectionImage</code> that represents the result of an operation (or chain of operations) for a
     * given <code>ParameterBlock</code>. If the operation is unable to handle the input arguments, this method should
     * return <code>null</code>.
     *
     * <p>Generally this method is expected to be invoked by an operation being executed in renderable mode. Therefore
     * the images contained in the generated <code>CollectionImage</code> would be expected to be <code>RenderableImage
     * </code>s.
     *
     * @param args Input arguments to the operation, including sources and/or parameters.
     * @return A <code>CollectionImage</code> containing the desired output.
     */
    CollectionImage create(ParameterBlock parameters);
}
