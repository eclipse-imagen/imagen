/* Copyright 2015 Michael Bedward, and others
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
 *  Copyright (c) 2015, Michael Bedward. All rights reserved.
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

package org.eclipse.imagen.media.contour;

import static org.junit.Assert.assertEquals;

import org.eclipse.imagen.ImageN;
import org.eclipse.imagen.media.contour.Segment.MergePoint;
import org.eclipse.imagen.util.ImagingListener;
import org.junit.BeforeClass;
import org.junit.Test;

public class SegmentTest {

    @BeforeClass
    public static void quiet() {
        ImageN imageN = ImageN.getDefaultInstance();
        final ImagingListener imagingListener = imageN.getImagingListener();
        if (imagingListener == null || imagingListener.getClass().getName().contains("ImagingListenerImpl")) {
            imageN.setImagingListener(new ImagingListener() {
                @Override
                public boolean errorOccurred(String message, Throwable thrown, Object where, boolean isRetryable)
                        throws RuntimeException {
                    if (message.contains("Continuing in pure Java mode")) {
                        return false;
                    }
                    return imagingListener.errorOccurred(message, thrown, where, isRetryable);
                }
            });
        }
    }

    @Test
    public void testAddAfterEnd() {
        Segment s = new Segment(0, 0, 0, 1, true);
        s.addAfterEnd(0, 2);
        assertEquals(2, s.getNumCoordinates());
        assertEquals(0, s.xStart, 0d);
        assertEquals(0, s.yStart, 0d);
        assertEquals(0, s.xEnd, 0d);
        assertEquals(2, s.yEnd, 0d);
    }

    @Test
    public void testAddBeforeStart() {
        Segment s = new Segment(0, 0, 0, 1, true);
        s.addBeforeStart(0, -1);
        assertEquals(2, s.getNumCoordinates());
        assertEquals(0, s.xStart, 0d);
        assertEquals(-1, s.yStart, 0d);
        assertEquals(0, s.xEnd, 0d);
        assertEquals(1, s.yEnd, 0d);
    }

    @Test
    public void testMergeStartStart() {
        // <---s1---++---s2--->
        Segment s2 = new Segment(0, 0, 1, 1, true);
        Segment s1 = new Segment(0, 0, -1, -1, true);
        s1.merge(s2, MergePoint.START_START);

        assertEquals(2, s1.getNumCoordinates());
        assertEquals(1, s1.xStart, 0d);
        assertEquals(1, s1.yStart, 0d);
        assertEquals(-1, s1.xEnd, 0d);
        assertEquals(-1, s1.yEnd, 0d);
    }

    @Test
    public void testMergeStartEnd() {
        // <---s1---+<---s2---+
        Segment s1 = new Segment(0, 0, -1, -1, true);
        Segment s2 = new Segment(1, 1, 0, 0, true);
        s1.merge(s2, MergePoint.START_END);

        assertEquals(2, s1.getNumCoordinates());
        assertEquals(1, s1.xStart, 0d);
        assertEquals(1, s1.yStart, 0d);
        assertEquals(-1, s1.xEnd, 0d);
        assertEquals(-1, s1.yEnd, 0d);
    }

    @Test
    public void testMergeEndEnd() {
        // +---s1---><---s2---+
        Segment s1 = new Segment(-1, -1, 0, 0, true);
        Segment s2 = new Segment(1, 1, 0, 0, true);
        s1.merge(s2, MergePoint.END_END);

        assertEquals(2, s1.getNumCoordinates());
        assertEquals(-1, s1.xStart, 0d);
        assertEquals(-1, s1.yStart, 0d);
        assertEquals(1, s1.xEnd, 0d);
        assertEquals(1, s1.yEnd, 0d);
    }

    @Test
    public void testMergeEndStart() {
        // +---s1--->+---s2--->
        Segment s1 = new Segment(-1, -1, 0, 0, true);
        Segment s2 = new Segment(0, 0, 1, 1, true);
        s1.merge(s2, MergePoint.END_START);

        assertEquals(2, s1.getNumCoordinates());
        assertEquals(-1, s1.xStart, 0d);
        assertEquals(-1, s1.yStart, 0d);
        assertEquals(1, s1.xEnd, 0d);
        assertEquals(1, s1.yEnd, 0d);
    }
}
