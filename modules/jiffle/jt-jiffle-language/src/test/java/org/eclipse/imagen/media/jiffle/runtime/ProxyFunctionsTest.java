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
 */

/*
 *  Copyright (c) 2011, Michael Bedward. All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification,
 *  are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.eclipse.imagen.media.jiffle.runtime;

import java.awt.image.RenderedImage;
import org.eclipse.imagen.media.utilities.ImageUtilities;
import org.junit.Test;

/**
 * Unit tests for the evaluation of expressions with Jiffle's image info functions.
 *
 * @author Michael Bedward
 * @since 0.1
 * @version $Id$
 */
public class ProxyFunctionsTest extends RuntimeTestBase {

    @Test
    public void x() throws Exception {
        System.out.println("   x()");

        String script = "dest = x();";

        Evaluator e = new Evaluator() {
            public double eval(double val) {
                double xx = x;
                move();
                return xx;
            }
        };

        testScript(script, e);
    }

    @Test
    public void y() throws Exception {
        System.out.println("   y()");

        String script = "dest = y();";

        Evaluator e = new Evaluator() {
            public double eval(double val) {
                double yy = y;
                move();
                return yy;
            }
        };

        testScript(script, e);
    }

    @Test
    public void xmin() throws Exception {
        System.out.println("   xmin()");

        String script = "dest = xmin();";
        RenderedImage srcImg = ImageUtilities.createConstantImage(-5, 5, 10, 20, 0);

        Evaluator e = new Evaluator() {
            public double eval(double val) {
                return -5;
            }
        };

        testScript(script, srcImg, e);
    }

    @Test
    public void ymin() throws Exception {
        System.out.println("   ymin()");

        String script = "dest = ymin();";
        RenderedImage srcImg = ImageUtilities.createConstantImage(-5, 5, 10, 20, 0);

        Evaluator e = new Evaluator() {
            public double eval(double val) {
                return 5;
            }
        };

        testScript(script, srcImg, e);
    }

    @Test
    public void width() throws Exception {
        System.out.println("   width()");

        String script = "dest = width();";

        Evaluator e = new Evaluator() {
            public double eval(double val) {
                return IMG_WIDTH;
            }
        };

        testScript(script, e);
    }

    @Test
    public void height() throws Exception {
        System.out.println("   height()");

        String script = "dest = height();";

        Evaluator e = new Evaluator() {
            public double eval(double val) {
                return IMG_WIDTH;
            }
        };

        testScript(script, e);
    }
}
