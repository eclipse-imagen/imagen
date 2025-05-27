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

package org.eclipse.imagen.media.codecimpl;

import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.io.IOException;
import java.lang.reflect.Method;

/** A class for utility functions for codecs. */
class CodecUtils {
    /** The <code>initCause()</code> method of <code>IOException</code> which is available from J2SE 1.4 onward. */
    static Method ioExceptionInitCause;

    static {
        try {
            Class c = Class.forName("java.io.IOException");
            ioExceptionInitCause = c.getMethod("initCause", new Class[] {java.lang.Throwable.class});
        } catch (Exception e) {
            ioExceptionInitCause = null;
        }
    }

    /**
     * Returns <code>true</code> if and only if <code>im</code> has a <code>SinglePixelPackedSampleModel</code> with a
     * sample size of at most 8 bits for each of its bands.
     *
     * @param src The <code>RenderedImage</code> to test.
     * @return Whether the image is byte-packed.
     */
    static final boolean isPackedByteImage(RenderedImage im) {
        SampleModel imageSampleModel = im.getSampleModel();

        if (imageSampleModel instanceof SinglePixelPackedSampleModel) {
            for (int i = 0; i < imageSampleModel.getNumBands(); i++) {
                if (imageSampleModel.getSampleSize(i) > 8) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    /** Converts the parameter exception to an <code>IOException</code>. */
    static final IOException toIOException(Exception cause) {
        IOException ioe;

        if (cause != null) {
            if (cause instanceof IOException) {
                ioe = (IOException) cause;
            } else if (ioExceptionInitCause != null) {
                ioe = new IOException(cause.getMessage());
                try {
                    ioExceptionInitCause.invoke(ioe, new Object[] {cause});
                } catch (Exception e2) {
                    // Ignore it ...
                }
            } else {
                ioe = new IOException(cause.getClass().getName() + ": " + cause.getMessage());
            }
        } else {
            ioe = new IOException();
        }

        return ioe;
    }
}
