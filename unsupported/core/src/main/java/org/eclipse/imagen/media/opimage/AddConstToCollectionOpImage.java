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

package org.eclipse.imagen.media.opimage;

import java.awt.RenderingHints;
import java.awt.image.renderable.ParameterBlock;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
import org.eclipse.imagen.CollectionImage;
import org.eclipse.imagen.ImageN;

/**
 * An <code>OpImage</code> implementing the "AddConstToCollection" operation.
 *
 * @see org.eclipse.imagen.operator.AddConstToCollectionDescriptor
 * @see AddConstToCollectionCIF
 * @since EA4
 */
final class AddConstToCollectionOpImage extends CollectionImage {

    /**
     * Constructor.
     *
     * @param sourceCollection A collection of rendered images.
     * @param hints Optionally contains destination image layout.
     * @param constants The constants to be added, stored as reference.
     */
    public AddConstToCollectionOpImage(Collection sourceCollection, RenderingHints hints, double[] constants) {
        /**
         * Try to create a new instance of the sourceCollection to be used to store output images. If failed, use a
         * Vector.
         */
        try {
            imageCollection = (Collection) sourceCollection.getClass().newInstance();
        } catch (Exception e) {
            imageCollection = new Vector();
        }

        Iterator iter = sourceCollection.iterator();
        while (iter.hasNext()) {
            ParameterBlock pb = new ParameterBlock();
            pb.addSource(iter.next());
            pb.add(constants);

            imageCollection.add(ImageN.create("AddConst", pb, hints));
        }
    }
}
