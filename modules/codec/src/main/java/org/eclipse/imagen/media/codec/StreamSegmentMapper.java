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
 * An interface for use with the <code>SegmentedSeekableStream</code> class. An instance of the <code>
 * StreamSegmentMapper</code> interface provides the location and length of a segment of a source <code>SeekableStream
 * </code> corresponding to the initial portion of a desired segment of the output stream.
 *
 * <p>As an example, consider a mapping between a source <code>SeekableStream src</code> and a <code>
 * SegmentedSeekableStream
 * dst</code> comprising bytes 100-149 and 200-249 of the source stream. The <code>dst</code> stream has a reference to
 * an instance <code>mapper</code> of <code>StreamSegmentMapper</code>.
 *
 * <p>A call to <code>dst.seek(0); dst.read(buf, 0, 10)</code> will result in a call to <code>
 * mapper.getStreamSegment(0, 10)</code>, returning a new <code>StreamSegment</code> with a starting position of 100 and
 * a length of 10 (or less). This indicates that in order to read bytes 0-9 of the segmented stream, bytes 100-109 of
 * the source stream should be read.
 *
 * <p>A call to <code>dst.seek(10); int nbytes = dst.read(buf, 0,
 * 100)</code> is somewhat more complex, since it will require data from both segments of <code>src</code>. The method
 * <code>
 * mapper.getStreamSegment(10, 100)</code> will be called. This method will return a new <code>StreamSegment</code> with
 * a starting position of 110 and a length of 40 (or less). The length is limited to 40 since a longer value would
 * result in a read past the end of the first segment. The read will stop after the first 40 bytes and an addition read
 * or reads will be required to obtain the data contained in the second segment.
 *
 * <p><b> This interface is not a committed part of the JAI API. It may be removed or changed in future releases of
 * JAI.</b>
 */
public interface StreamSegmentMapper {

    /**
     * Returns a <code>StreamSegment</code> object indicating the location of the initial portion of a desired segment
     * in the source stream. The length of the returned <code>StreamSegment</code> may be smaller than the desired
     * length.
     *
     * @param pos The desired starting position in the <code>SegmentedSeekableStream</code>, as a <code>long</code>.
     * @param length The desired segment length.
     */
    StreamSegment getStreamSegment(long pos, int length);

    /**
     * Sets the values of a <code>StreamSegment</code> object indicating the location of the initial portion of a
     * desired segment in the source stream. The length of the returned <code>StreamSegment</code> may be smaller than
     * the desired length.
     *
     * @param pos The desired starting position in the <code>SegmentedSeekableStream</code>, as a <code>long</code>.
     * @param length The desired segment length.
     * @param seg A <code>StreamSegment</code> object to be overwritten.
     */
    void getStreamSegment(long pos, int length, StreamSegment seg);
}
