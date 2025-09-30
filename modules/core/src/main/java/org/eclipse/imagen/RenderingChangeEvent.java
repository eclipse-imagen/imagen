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

import java.awt.Shape;

/**
 * Class representing the event that occurs when a <code>RenderedOp</code> node is re-rendered.
 *
 * @since ImageN 0.4.0
 */
public class RenderingChangeEvent extends PropertyChangeEventJAI {
    private Shape invalidRegion;

    /**
     * Constructs a <code>RenderingChangeEvent</code>. The inherited <code>getSource()</code> method of the event would
     * return the <code>RenderedOp</code> source; the inherited methods <code>getOldValue()</code> and <code>
     * getNewValue()</code> would return the old and new renderings, respectively. If either the new rendering or the
     * invalid region is null, the data of the node's rendering need to be re-requested.
     *
     * <p>The invalid region should be <code>null</code> if there is no area of the extant rendering which remains
     * valid. If the invalid region is empty, this serves to indicate that all pixels within the bounds of the old
     * rendering remain valid. Pixels outside the image bounds proper but within the bounds of all tiles of the image
     * are not guaranteed to be valid of the invalid region is empty.
     */
    public RenderingChangeEvent(
            RenderedOp source, PlanarImage oldRendering, PlanarImage newRendering, Shape invalidRegion) {
        super(source, "Rendering", oldRendering, newRendering);
        this.invalidRegion = invalidRegion;
    }

    /**
     * Returns an object which represents the region over which the the two renderings should differ.
     *
     * @return The region over which the two renderings differ or <code>null</code> to indicate that they differ
     *     everywhere. An empty <code>Shape</code> indicates that all pixels within the bounds of the old rendering
     *     remain valid.
     */
    public Shape getInvalidRegion() {
        return invalidRegion;
    }
}
