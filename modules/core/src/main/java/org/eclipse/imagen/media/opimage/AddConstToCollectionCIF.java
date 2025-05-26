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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.eclipse.imagen.CollectionImage;
import org.eclipse.imagen.CollectionImageFactory;
import org.eclipse.imagen.CollectionOp;
import org.eclipse.imagen.PlanarImage;
import org.eclipse.imagen.RenderedOp;

/**
 * A <code>CIF</code> supporting the "AddConstToCollection" operation.
 *
 * @see org.eclipse.imagen.operator.AddConstToCollectionDescriptor
 * @see AddConstToCollectionOpImage
 * @since EA4
 */
public class AddConstToCollectionCIF implements CollectionImageFactory {

    /** Constructor. */
    public AddConstToCollectionCIF() {}

    /**
     * Creates a new instance of <code>AddConstToCollectionOpImage</code>.
     *
     * @param args Input source collection and constants
     * @param hints Optionally contains destination image layout.
     */
    public CollectionImage create(ParameterBlock args, RenderingHints hints) {
        return new AddConstToCollectionOpImage(
                (Collection) args.getSource(0), hints, (double[]) args.getObjectParameter(0));
    }

    /** Updates an instance of <code>AddConstToCollectionOpImage</code>. */
    public CollectionImage update(
            ParameterBlock oldParamBlock,
            RenderingHints oldHints,
            ParameterBlock newParamBlock,
            RenderingHints newHints,
            CollectionImage oldRendering,
            CollectionOp op) {
        CollectionImage updatedCollection = null;

        if (oldParamBlock.getObjectParameter(0).equals(newParamBlock.getObjectParameter(0))
                && (oldHints == null ? newHints == null : oldHints.equals(newHints))) {

            // Retrieve the old and new sources and the parameters.
            Collection oldSource = (Collection) oldParamBlock.getSource(0);
            Collection newSource = (Collection) newParamBlock.getSource(0);
            double[] constants = (double[]) oldParamBlock.getObjectParameter(0);

            // Construct a Collection of common sources.
            Collection commonSources = new ArrayList();
            Iterator it = oldSource.iterator();
            while (it.hasNext()) {
                Object oldElement = it.next();
                if (newSource.contains(oldElement)) {
                    commonSources.add(oldElement);
                }
            }

            if (commonSources.size() != 0) {
                // Construct a Collection of the RenderedOp nodes that
                // will be retained in the new CollectionImage.
                ArrayList commonNodes = new ArrayList(commonSources.size());
                it = oldRendering.iterator();
                while (it.hasNext()) {
                    RenderedOp node = (RenderedOp) it.next();
                    PlanarImage source = (PlanarImage) node.getSourceImage(0);
                    if (commonSources.contains(source)) {
                        commonNodes.add(node);
                    }
                }

                // Create a new CollectionImage.
                updatedCollection = new AddConstToCollectionOpImage(newSource, newHints, constants);

                // Remove from the new CollectionImage all nodes that
                // are common with the old CollectionImage.
                ArrayList newNodes = new ArrayList(oldRendering.size() - commonSources.size());
                it = updatedCollection.iterator();
                while (it.hasNext()) {
                    RenderedOp node = (RenderedOp) it.next();
                    PlanarImage source = (PlanarImage) node.getSourceImage(0);
                    if (commonSources.contains(source)) {
                        it.remove();
                    }
                }

                // Add all the common nodes to the new CollectionImage.
                it = commonNodes.iterator();
                while (it.hasNext()) {
                    updatedCollection.add(it.next());
                }
            }
        }

        return updatedCollection;
    }
}
