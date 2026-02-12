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

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;

/**
 * An extension of <code>java.awt.Canvas</code> for use with ImageN. <code>CanvasJAI</code> automatically returns an
 * instance of <code>GraphicsJAI</code> from its <code>getGraphics()</code> method. This guarantees that the <code>
 * update(Graphics g)</code> and <code>paint(Graphics g)</code> methods will receive a <code>GraphicsJAI</code> instance
 * for accelerated rendering of <code>ImageN</code> images.
 *
 * <p>In circumstances where it is not possible to use <code>CanvasJAI</code>, a similar effect may be obtained by
 * manually calling <code>GraphicsJAI.createGraphicsJAI()</code> to "wrap" a <code>Graphics2D</code> object.
 *
 * @see GraphicsJAI
 */
public class CanvasJAI extends Canvas {

    /** Constructs an instance of <code>CanvasJAI</code> using the given <code>GraphicsConfiguration</code>. */
    public CanvasJAI(GraphicsConfiguration config) {
        super(config);
    }

    /** Returns an instance of <code>GraphicsJAI</code> for drawing to this canvas. */
    public Graphics getGraphics() {
        Graphics2D g = (Graphics2D) super.getGraphics();
        return GraphicsJAI.createGraphicsJAI(g, this);
    }
}
