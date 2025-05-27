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

package org.eclipse.imagen.media.codec;

/**
 * A utility class representing a segment within a stream as a <code>long</code> starting position and an <code>int
 * </code> length.
 *
 * <p><b> This class is not a committed part of the JAI API. It may be removed or changed in future releases of JAI.</b>
 */
public class StreamSegment {

    private long startPos = 0L;
    private int segmentLength = 0;

    /** Constructs a <code>StreamSegment</code>. The starting position and length are set to 0. */
    public StreamSegment() {}

    /** Constructs a <code>StreamSegment</code> with a given starting position and length. */
    public StreamSegment(long startPos, int segmentLength) {
        this.startPos = startPos;
        this.segmentLength = segmentLength;
    }

    /** Returns the starting position of the segment. */
    public final long getStartPos() {
        return startPos;
    }

    /** Sets the starting position of the segment. */
    public final void setStartPos(long startPos) {
        this.startPos = startPos;
    }

    /** Returns the length of the segment. */
    public final int getSegmentLength() {
        return segmentLength;
    }

    /** Sets the length of the segment. */
    public final void setSegmentLength(int segmentLength) {
        this.segmentLength = segmentLength;
    }
}
