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

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Unit tests of scripts with image scope variables declared in an init block.
 *
 * @author Michael Bedward
 * @since 0.1
 * @version $Id$
 */
public class ImageScopeVarsTest extends RuntimeTestBase {

    @Test
    public void increment() throws Exception {
        System.out.println("   incrementing value");

        String script = "init { n = 0; } dest = n++;";

        Evaluator e = new Evaluator() {
            double n = 0;

            public double eval(double val) {
                return n++;
            }

            @Override
            public void reset() {
                n = 0;
            }
        };

        testScript(script, e);
    }

    @Test
    public void decrement() throws Exception {
        System.out.println("   decrementing value");

        String script = "init { n = 0; } dest = n--;";

        Evaluator e = new Evaluator() {
            double n = 0;

            public double eval(double val) {
                return n--;
            }

            @Override
            public void reset() {
                n = 0;
            }
        };

        testScript(script, e);
    }

    @Test
    public void getter() throws Exception {
        System.out.println("   getting values from runtime instance");

        String script = String.format("init { n = 0; } n += con(src < %s); dest = n;", NUM_PIXELS / 2);

        Evaluator e = new Evaluator() {
            double n = 0;

            public double eval(double val) {
                if (val < NUM_PIXELS / 2) {
                    n++;
                }
                return n;
            }

            @Override
            public void reset() {
                n = 0;
            }
        };

        testScript(script, e);
        assertEquals(NUM_PIXELS / 2, directRuntimeInstance.getVar("n"), TOL);
    }

    @Test
    public void proxyFunctionInInitBlock() throws Exception {
        System.out.println("   using image info function in init block");

        String script = "init { n = width(); } dest = n;";

        Evaluator e = new Evaluator() {

            public double eval(double val) {
                return IMG_WIDTH;
            }
        };

        testScript(script, e);
    }

    @Test
    public void ifWithinInitBlock() throws Exception {
        System.out.println("   using if expressions in init block");

        String script = "init { n = con(width() > 100, 2, 1); } dest = n;";

        Evaluator e = new Evaluator() {

            public double eval(double val) {
                return IMG_WIDTH > 100 ? 2 : 1;
            }
        };

        testScript(script, e);
    }
}
