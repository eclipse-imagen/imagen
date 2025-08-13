/* Copyright 2011 Michael Bedward, and others
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

package org.eclipse.imagen.media.kernel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Tests for numeric comparison with tolerance.
 *
 * @author Michael Bedward
 * @since 1.1
 * @version $Id$
 */
public class CompareOpTest {

    @Test
    public void doubleIsZero() {
        double x = CompareOp.DTOL / 2;

        assertTrue(CompareOp.isZero(x));
        assertTrue(CompareOp.isZero(-x));

        x = CompareOp.DTOL * 2;
        assertFalse(CompareOp.isZero(x));
    }

    @Test
    public void floatIsZero() {
        float x = CompareOp.FTOL / 2;

        assertTrue(CompareOp.isZero(x));
        assertTrue(CompareOp.isZero(-x));

        x = CompareOp.FTOL * 2;
        assertFalse(CompareOp.isZero(x));
    }

    @Test
    public void doubleIsZeroWithTol() {
        double tol = CompareOp.DTOL * 100;

        double x = tol / 2;
        assertTrue(CompareOp.isZero(x, tol));

        x = tol * 2;
        assertFalse(CompareOp.isZero(x, tol));
    }

    @Test
    public void floatIsZeroWithTol() {
        float tol = CompareOp.FTOL * 100;

        float x = tol / 2;
        assertTrue(CompareOp.isZero(x, tol));

        x = tol * 2;
        assertFalse(CompareOp.isZero(x, tol));
    }

    @Test
    public void acompareDoubles() {
        double x1 = 100;

        double x2 = x1 - CompareOp.DTOL / 10;
        assertEquals(0, CompareOp.acompare(x1, x2));

        x2 = x1 - CompareOp.DTOL * 10;
        assertTrue(CompareOp.acompare(x1, x2) > 0);

        x2 = x1 + CompareOp.DTOL * 10;
        assertTrue(CompareOp.acompare(x1, x2) < 0);
    }

    @Test
    public void acompareFloats() {
        float x1 = 100;

        float x2 = x1 - CompareOp.FTOL / 10;
        assertEquals(0, CompareOp.acompare(x1, x2));

        x2 = x1 - CompareOp.FTOL * 10;
        assertTrue(CompareOp.acompare(x1, x2) > 0);

        x2 = x1 + CompareOp.FTOL * 10;
        assertTrue(CompareOp.acompare(x1, x2) < 0);
    }

    @Test
    public void acompareDoublesWithTol() {
        double tol = 0.01d;
        double x1 = 100;

        double x2 = x1 - tol / 10;
        assertEquals(0, CompareOp.acompare(x1, x2, tol));

        x2 = x1 - tol * 10;
        assertTrue(CompareOp.acompare(x1, x2, tol) > 0);

        x2 = x1 + tol * 10;
        assertTrue(CompareOp.acompare(x1, x2, tol) < 0);
    }

    @Test
    public void acompareFloatsWithTol() {
        float tol = 0.01f;
        float x1 = 100;

        float x2 = x1 - tol / 10;
        assertEquals(0, CompareOp.acompare(x1, x2, tol));

        x2 = x1 - tol * 10;
        assertTrue(CompareOp.acompare(x1, x2, tol) > 0);

        x2 = x1 + tol * 10;
        assertTrue(CompareOp.acompare(x1, x2, tol) < 0);
    }

    @Test
    public void pcompareDoubles() {
        double ptol = 0.01d;
        double x1 = 10000;

        double x2 = 0.995 * x1;
        assertEquals(0, CompareOp.pcompare(x1, x2, ptol));

        x2 = 0.98 * x1;
        assertTrue(CompareOp.pcompare(x1, x2, ptol) > 0);
    }

    @Test
    public void pcompareFloats() {
        float ptol = 0.01f;
        float x1 = 10000;

        float x2 = 0.995f * x1;
        assertEquals(0, CompareOp.pcompare(x1, x2, ptol));

        x2 = 0.98f * x1;
        assertTrue(CompareOp.pcompare(x1, x2, ptol) > 0);
    }

    @Test
    public void aequalDoubles() {
        double x1 = 42;
        double x2 = x1 + CompareOp.DTOL * 10;
        assertFalse(CompareOp.aequal(x1, x2));

        x2 = x1 + CompareOp.DTOL / 10;
        assertTrue(CompareOp.aequal(x1, x2));
    }

    @Test
    public void aequalFloats() {
        float x1 = 42;
        float x2 = x1 + CompareOp.FTOL * 10;
        assertFalse(CompareOp.aequal(x1, x2));

        x2 = x1 + CompareOp.FTOL / 10;
        assertTrue(CompareOp.aequal(x1, x2));
    }

    @Test
    public void aequalDoublesWithTol() {
        double tol = 0.1d;
        double x1 = 42;
        double x2 = x1 + tol * 10;
        assertFalse(CompareOp.aequal(x1, x2, tol));

        x2 = x1 + tol / 10;
        assertTrue(CompareOp.aequal(x1, x2, tol));
    }

    @Test
    public void aequalFloatsWithTol() {
        float tol = 0.1f;
        float x1 = 42;
        float x2 = x1 + tol * 10;
        assertFalse(CompareOp.aequal(x1, x2, tol));

        x2 = x1 + tol / 10;
        assertTrue(CompareOp.aequal(x1, x2, tol));
    }

    @Test
    public void pequalDoubles() {
        double ptol = 0.01d;
        double x1 = 10000;

        double x2 = 0.985 * x1;
        assertFalse(CompareOp.pequal(x1, x2, ptol));

        x2 = 0.995 * x1;
        assertTrue(CompareOp.pequal(x1, x2, ptol));
    }

    @Test
    public void pequalFloats() {
        float ptol = 0.01f;
        float x1 = 10000;

        float x2 = 0.985f * x1;
        assertFalse(CompareOp.pequal(x1, x2, ptol));

        x2 = 0.995f * x1;
        assertTrue(CompareOp.pequal(x1, x2, ptol));
    }
}
