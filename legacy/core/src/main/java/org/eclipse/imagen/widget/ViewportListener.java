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
package org.eclipse.imagen.widget;

/**
 * An interface used by the <code>ScrollingImagePanel</code> class to inform listeners of the current viewable area of
 * the image.
 *
 * @see ScrollingImagePanel
 *     <p>This class has been deprecated. The source code has been moved to the samples/widget directory. These widgets
 *     are no longer supported.
 * @deprecated as of ImageN 0.4.0
 */
public interface ViewportListener {

    /**
     * Called to inform the listener of the currently viewable area od the source image.
     *
     * @param x The X coordinate of the upper-left corner of the current viewable area.
     * @param y The Y coordinate of the upper-left corner of the current viewable area.
     * @param width The width of the current viewable area in pixels.
     * @param height The height of the current viewable area in pixels.
     */
    void setViewport(int x, int y, int width, int height);
}
