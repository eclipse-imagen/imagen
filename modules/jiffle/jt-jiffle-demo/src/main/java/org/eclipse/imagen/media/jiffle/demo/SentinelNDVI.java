/* JAI-Ext - OpenSource Java Advanced Image Extensions Library
 *    http://www.geo-solutions.it/
 *    Copyright 2018 GeoSolutions
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
/*
 * Copyright (c) 2018, Michael Bedward. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice, this
 *   list of conditions and the following disclaimer in the documentation and/or
 *   other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * SPDX-License-Identifier: BSD-2-Clause
 */
package org.eclipse.imagen.media.jiffle.demo;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import org.eclipse.imagen.RenderedOp;
import org.eclipse.imagen.TiledImage;
import org.eclipse.imagen.media.imageread.ImageReadDescriptor;
import org.eclipse.imagen.media.jiffle.JiffleBuilder;
import org.eclipse.imagen.media.jiffle.JiffleException;
import org.eclipse.imagen.media.jiffle.runtime.JiffleDirectRuntime;
import org.eclipse.imagen.media.utilities.ImageUtilities;

public class SentinelNDVI {

    public static void main(String[] args) throws IOException, JiffleException {
        String basePath =
                "/opt/gisData/evo-odas/coverave-view-hetero/multires-s2/S2A_MSIL1C_20170410T103021_N0204_R108_T32UNU_20170410T103020.SAFE/20170410T103021026Z_fullres_CC2.4251_T32UNU_";

        // prepare inputs and outputs
        RenderedImage red = readImage(new File(basePath + "B04.tif"));
        RenderedImage nir = readImage(new File(basePath + "B08.tif"));
        TiledImage result = ImageUtilities.createConstantImage(red.getWidth(), red.getHeight(), (float) 0);

        // build the operation
        JiffleBuilder builder = new JiffleBuilder();
        builder.dest("res", result).source("red", red).source("nir", nir);
        builder.script("n = nir; r = red; res = (n - r) / (n + r);"); // HERE IS THE NDVI SCRIPT! 5.5 seconds!
        // builder.script("res = (nir - red) / (nir + red);"); // HERE IS THE NDVI SCRIPT! 7.65 sec!
        JiffleDirectRuntime runtime = builder.getRuntime();

        // actually running the calculation
        final double pixels = (double) red.getWidth() * (double) red.getHeight();
        System.out.println("Computing " + NumberFormat.getNumberInstance().format(pixels) + " pixels");
        long start = System.currentTimeMillis();
        runtime.evaluateAll(null);
        long end = System.currentTimeMillis();
        System.out.println("Computation of output took " + (end - start) / 1000.0 + " secs");
        System.out.println("Writing output to disk");
        ImageIO.write(result, "TIF", new File("/tmp/ndvi.tif"));
        System.out.println("Writing complete");
    }

    private static RenderedOp readImage(File file) throws IOException {
        FileImageInputStream stream = new FileImageInputStream(file);
        ImageReader reader = ImageIO.getImageReaders(stream).next();
        return ImageReadDescriptor.create(stream, 0, false, false, false, null, null, null, reader, null);
    }
}
