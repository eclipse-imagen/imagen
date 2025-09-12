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

package org.eclipse.imagen.media.util;

import java.awt.image.RenderedImage;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.Map;
import org.eclipse.imagen.ImageLayout;
import org.eclipse.imagen.NullOpImage;
import org.eclipse.imagen.PlanarImage;
import org.eclipse.imagen.RenderedImageAdapter;

/**
 * <code>NullOpImage</code> subclass which conditionally forwards {@link #dispose()} to its source. The call will be
 * forwarded if the source is a <code>PlanarImage</code> or a <code>RenderedImage</code> wrapped by <code>
 * RenderedImageAdapter</code> and which has a <code>dispose()</code> method with no parameters. In the former case the
 * call is forwarded directly, and in the latter via reflection.
 *
 * @since JAI 1.1.3
 */
public class DisposableNullOpImage extends NullOpImage {
    public DisposableNullOpImage(RenderedImage source, ImageLayout layout, Map configuration, int computeType) {
        super(source, layout, configuration, computeType);
    }

    public synchronized void dispose() {
        PlanarImage src = getSource(0);
        if (src instanceof RenderedImageAdapter) {
            // Use relection to invoke dispose();
            RenderedImage trueSrc = ((RenderedImageAdapter) src).getWrappedImage();
            Method disposeMethod = null;
            try {
                Class cls = trueSrc.getClass();
                disposeMethod = cls.getMethod("dispose", null);
                if (!disposeMethod.isAccessible()) {
                    AccessibleObject.setAccessible(new AccessibleObject[] {disposeMethod}, true);
                }
                disposeMethod.invoke(trueSrc, null);
            } catch (Exception e) {
                // Ignore it.
            }
        } else {
            // Invoke dispose() directly.
            src.dispose();
        }
    }
}
