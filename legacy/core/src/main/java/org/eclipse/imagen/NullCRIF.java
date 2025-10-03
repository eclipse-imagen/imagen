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

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;

/**
 * A <code>ContextualRenderedImageFactory</code> representing an operation which performs no processing of its image
 * source(s) per se, i.e., a no-op.
 *
 * <p>The primary use of this image factory is as a utility class in implementing operations which generate only
 * non-image data via the use of <code>PropertyGenerator</code>s. A <code>PropertyGenerator</code> is defined as always
 * contributing to the property environment of a given operation when it is returned by the <code>
 * getPropertyGenerators()</code> method of the <code>OperationDescriptor</code> corresponding to the operation.
 *
 * <p>The procedure to be followed to register an operation which generates only non-image data as ImageN image
 * properties is as follows:
 *
 * <ul>
 *   <li>Create a <code>PropertyGenerator</code> which calculates the non-image data given the operation node;
 *   <li>Create an <code>OperationDescriptor</code> the <code>getPropertyGenerators()</code> method of which returns the
 *       <code>PropertyGenerator</code> defined in the previous step;
 *   <li>Register the <code>OperationDescriptor</code> with the <code>OperationRegistry</code> as usual by passing it to
 *       <code>registerOperationDescriptor()</code> along with the operation name;
 *   <li>Register a <code>NullCRIF</code> as the image factory corresponding to this operation.
 * </ul>
 *
 * The properties emitted by the associated <code>PropertyGenerator</code>(s) will then be available by invoking <code>
 * getProperty()</code> on the node returned by <code>ImageN.create()</code> using the registered operation name.
 *
 * @see CRIFImpl
 * @see java.awt.image.renderable.ContextualRenderedImageFactory
 */
public class NullCRIF extends CRIFImpl {

    /** Image returned by <code>RenderedImageFactory.create()</code> when there are ono sources. */
    private static RenderedImage sourcelessImage = null;

    /**
     * Constructs a <code>NullCRIF</code>. The <code>operationName</code> in the superclass is set to <code>null</code>.
     */
    public NullCRIF() {
        super();
    }

    /**
     * Sets the value of the <code>RenderedImage</code> to be returned by the <code>RenderedImageFactory.create()</code>
     * method when there are no sources in the <code>ParameterBlock</code>.
     *
     * @param a <code>RenderedImage</code> or <code>null</code>.
     */
    public static final synchronized void setSourcelessImage(RenderedImage im) {
        sourcelessImage = im;
    }

    /**
     * Gets the value of the RenderedImage to be returned by the RIF.create() method when there are no sources in the
     * <code>ParameterBlock</code>.
     *
     * @return a <code>RenderedImage</code> or <code>null</code>.
     */
    public static final synchronized RenderedImage getSourcelessImage() {
        return sourcelessImage;
    }

    /**
     * Returns the first source in the source list in the <code>ParameterBlock</code> or the value returned by <code>
     * getSourcelessImage()</code> if there are no soures.
     *
     * @throws ClassCastException if there are sources and the source at index zero is not a <code>RenderedImage</code>.
     */
    public RenderedImage create(ParameterBlock args, RenderingHints renderHints) {
        return args.getNumSources() == 0 ? getSourcelessImage() : args.getRenderedSource(0);
    }
}
