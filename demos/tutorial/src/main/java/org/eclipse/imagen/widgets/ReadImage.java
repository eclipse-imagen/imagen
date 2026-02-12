/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 *
 * This Example Content is intended to demonstrate usage of Eclipse technology. It is
 * provided to you under the terms and conditions of the Eclipse Distribution License
 * v1.0 which is available at http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */
package org.eclipse.imagen.widgets;

import java.io.*;
import org.eclipse.imagen.ImageN;
import org.eclipse.imagen.PlanarImage;

public class ReadImage {

    public static PlanarImage getImage(String filename) {
        PlanarImage src = null;

        if (filename != null) {
            File f = new File(filename);

            if (f.exists() && f.canRead()) {
                src = ImageN.create("fileload", filename);
            } else {
                // custom file reader
            }
        }

        return src;
    }
}
