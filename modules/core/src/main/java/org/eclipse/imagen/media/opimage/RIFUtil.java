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
import org.eclipse.imagen.BorderExtender;
import org.eclipse.imagen.ImageLayout;
import org.eclipse.imagen.JAI;
import org.eclipse.imagen.TileCache;

public class RIFUtil {

    public static ImageLayout getImageLayoutHint(RenderingHints renderHints) {
        if (renderHints == null) {
            return null;
        } else {
            return (ImageLayout) renderHints.get(JAI.KEY_IMAGE_LAYOUT);
        }
    }

    public static TileCache getTileCacheHint(RenderingHints renderHints) {
        if (renderHints == null) {
            return null;
        } else {
            return (TileCache) renderHints.get(JAI.KEY_TILE_CACHE);
        }
    }

    public static BorderExtender getBorderExtenderHint(RenderingHints renderHints) {
        if (renderHints == null) {
            return null;
        } else {
            return (BorderExtender) renderHints.get(JAI.KEY_BORDER_EXTENDER);
        }
    }
}
