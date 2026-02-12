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

import java.awt.image.renderable.*;
import org.eclipse.imagen.*;

public class TutorUtils {

    /** produce a 3 band luminance image from a 3 band color image */
    public static PlanarImage convertColorToGray(PlanarImage src, int brightness) {
        PlanarImage dst = null;
        double b = (double) brightness;
        double[][] matrix = {
            {.114D, 0.587D, 0.299D, b},
            {.114D, 0.587D, 0.299D, b},
            {.114D, 0.587D, 0.299D, b}
        };

        if (src != null) {
            ParameterBlock pb = new ParameterBlock();
            pb.addSource(src);
            pb.add(matrix);
            dst = ImageN.create("bandcombine", pb, null);
        }

        return dst;
    }

    /** produce a 3 band image from a single band gray scale image */
    public static PlanarImage convertGrayToColor(PlanarImage src, int brightness) {
        PlanarImage dst = null;
        double b = (double) brightness;
        double[][] matrix = {
            {1.0D, b},
            {1.0D, b},
            {1.0D, b}
        };

        if (src != null) {
            int nbands = src.getSampleModel().getNumBands();

            // MUST check color model here
            if (nbands == 1) {
                ParameterBlock pb = new ParameterBlock();
                pb.addSource(src);
                pb.add(matrix);
                dst = ImageN.create("bandcombine", pb, null);
            } else {
                dst = src;
            }
        }

        return dst;
    }
}
