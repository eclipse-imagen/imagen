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

import org.eclipse.imagen.media.numeric.CompareOp;
import org.junit.Test;

/**
 * Unit tests for the evaluation of simple logical statements with a single source and destination image.
 *
 * @author Michael Bedward
 * @since 0.1
 * @version $Id$
 */
public class LogicalStatementsTest extends RuntimeTestBase {

    @Test
    public void logical1() throws Exception {

        String src = String.format("dest = src < %d || src > %d;", NUM_PIXELS / 4, 3 * NUM_PIXELS / 4);
        System.out.println("   " + src);

        testScript(src, new Evaluator() {

            public double eval(double val) {
                return val < NUM_PIXELS / 4 || val > 3 * NUM_PIXELS / 4 ? 1.0 : 0.0;
            }
        });
    }

    @Test
    public void logical2() throws Exception {

        String src = String.format("dest = src <= %d || src >= %d;", NUM_PIXELS / 4, 3 * NUM_PIXELS / 4);
        System.out.println("   " + src);

        testScript(src, new Evaluator() {

            public double eval(double val) {
                return val <= NUM_PIXELS / 4 || val >= 3 * NUM_PIXELS / 4 ? 1.0 : 0.0;
            }
        });
    }

    @Test
    public void logical3() throws Exception {

        String src = String.format("dest = src > %d && src < %d;", NUM_PIXELS / 4, 3 * NUM_PIXELS / 4);
        System.out.println("   " + src);

        testScript(src, new Evaluator() {

            public double eval(double val) {
                return val > NUM_PIXELS / 4 && val < 3 * NUM_PIXELS / 4 ? 1.0 : 0.0;
            }
        });
    }

    @Test
    public void logical4() throws Exception {

        String src = String.format("dest = src >= %d && src <= %d;", NUM_PIXELS / 4, 3 * NUM_PIXELS / 4);
        System.out.println("   " + src);

        testScript(src, new Evaluator() {

            public double eval(double val) {
                return val >= NUM_PIXELS / 4 && val <= 3 * NUM_PIXELS / 4 ? 1.0 : 0.0;
            }
        });
    }

    @Test
    public void logical5() throws Exception {

        String src = String.format("dest = src == %d;", NUM_PIXELS / 2);
        System.out.println("   " + src);

        testScript(src, new Evaluator() {

            public double eval(double val) {
                return CompareOp.aequal(val, NUM_PIXELS / 2) ? 1.0 : 0.0;
            }
        });
    }

    @Test
    public void logical6() throws Exception {

        String src = String.format("dest = src != %d;", NUM_PIXELS / 2);
        System.out.println("   " + src);

        testScript(src, new Evaluator() {

            public double eval(double val) {
                return !CompareOp.aequal(val, NUM_PIXELS / 2) ? 1.0 : 0.0;
            }
        });
    }

    @Test
    public void logical7() throws Exception {

        String src = "dest = !(src % 2);";
        System.out.println("   " + src);

        testScript(src, new Evaluator() {

            public double eval(double val) {
                return !CompareOp.aequal(val % 2, 1.0) ? 1.0 : 0.0;
            }
        });
    }

    @Test
    public void logical8() throws Exception {

        String src = String.format("dest = src <= %d ^| src >= %d;", 3 * NUM_PIXELS / 4, NUM_PIXELS / 4);
        System.out.println("   " + src);

        testScript(src, new Evaluator() {

            public double eval(double val) {
                return val <= 3 * NUM_PIXELS / 4 ^ val >= NUM_PIXELS / 4 ? 1.0 : 0.0;
            }
        });
    }
}
