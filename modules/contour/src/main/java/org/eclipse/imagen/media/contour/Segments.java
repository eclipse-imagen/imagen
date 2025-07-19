/* Copyright 2010 Michael Bedward, and others
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
 *  Copyright (c) 2010, Michael Bedward. All rights reserved.
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.eclipse.imagen.media.contour.Segment.MergePoint;
import org.locationtech.jts.geom.LineString;

/**
 * A container for the segments collected by ContourOpImage. It will return them as merged lines eventually applying
 * simplification procedures
 *
 * @author Andrea Aime - GeoSolutions
 * @since 1.1
 * @version $Id$
 */
class Segments {
    static final int MAX_SIZE = 16348; // this amounts to 130KB storage
    boolean simplify;

    Segment temp = new Segment();

    /** List of segments sorted by start element */
    TreeSet<Segment> startList = new TreeSet<Segment>(Segment.START_COMPARATOR);

    /** List of segments sorted by end element */
    TreeSet<Segment> endList = new TreeSet<Segment>(Segment.END_COMPARATOR);

    /** The completed segment list */
    List<LineString> result = new ArrayList<LineString>();

    public Segments(boolean simplify) {
        this.simplify = simplify;
    }

    /**
     * Adds a segment to the mix
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     */
    public void add(double x1, double y1, double x2, double y2) {
        // we don't add single points, only full segments
        if (Segment.samePoint(x1, y1, x2, y2)) {
            return;
        }

        // keep the lower ordinate first, it's the one that can connect with
        // previous segments
        if (y2 < y1) {
            double tmp = y1;
            y1 = y2;
            y2 = tmp;
            tmp = x1;
            x1 = x2;
            x2 = tmp;
        }

        // try to add using the lowest point first
        if (appendSegment(x1, y1, x2, y2)) {
            return;
        }

        // no connection, need to create a new segment
        Segment segment = new Segment(x1, y1, x2, y2, simplify);
        startList.add(segment);
        endList.add(segment);
        // insertInStartList(segment);
        // insertInEndList(segment);
    }

    private boolean appendSegment(double x1, double y1, double x2, double y2) {
        temp.setXY(x1, y1, x1, y1);
        Segment segment = search(startList, temp);
        if (segment != null) {
            addToStartList(x2, y2, segment);
            return true;
        } else {
            segment = search(endList, temp);
            if (segment != null) {
                addToEndList(x2, y2, segment);
                return true;
            }
        }
        temp.setXY(x2, y2, x2, y2);
        segment = search(startList, temp);
        if (segment != null) {
            addToStartList(x1, y1, segment);
            return true;
        } else {
            segment = search(endList, temp);
            if (segment != null) {
                addToEndList(x1, y1, segment);
                return true;
            }
        }
        return false;
    }

    private void addToEndList(double x, double y, Segment segment) {
        // add the point
        endList.remove(segment);
        segment.addAfterEnd(x, y);
        // can we merge the segment to another in the end list now?
        // (we cannot keep two segments with the same endpoint in the treeset, they would be
        // considered equal)
        temp.setXY(x, y, x, y);
        Segment mergeTarget = search(endList, segment);
        if (mergeTarget == null) {
            // add back in the end list
            endList.add(segment);
        } else {
            while (mergeTarget != null) {
                // this segment is getting merged, removed it also from the start list
                startList.remove(segment);
                // update the merged target
                endList.remove(mergeTarget);
                mergeTarget.merge(segment, MergePoint.END_END);
                mergeTarget.touched = true;
                // is the merge going to cascade to another segment now that
                // we have modified the end point of s2 (and made it become the
                // start point of "segment"?
                Segment next = search(endList, mergeTarget);
                if (next != null) {
                    segment = mergeTarget;
                    mergeTarget = next;
                } else {
                    // no next, we can add back the merge target to the start list
                    endList.add(mergeTarget);
                    mergeTarget = null;
                }
            }
        }
        assert listConsistent() : "Start: " + startList + "\nEnd: " + endList;
    }

    private void addToStartList(double x, double y, Segment segment) {
        assert listConsistent() : "Start: " + startList + "\nEnd: " + endList;
        // add the point
        startList.remove(segment);
        segment.addBeforeStart(x, y);
        // can we merge the segment to another in the start list now?
        temp.setXY(x, y, x, y);
        Segment mergeTarget = search(startList, segment);
        if (mergeTarget == null) {
            // add back in the start list
            startList.add(segment);
        } else {
            while (mergeTarget != null) {
                // this segment is getting merged, removed it also from the end list
                endList.remove(segment);
                // update the merge target
                startList.remove(mergeTarget);
                mergeTarget.merge(segment, MergePoint.START_START);
                mergeTarget.touched = true;
                // is the merge going to cascade to another segment now that
                // we have modified the start point of s2 (and made it become the
                // end point of "segment"?
                Segment next = search(startList, mergeTarget);
                if (next != null) {
                    segment = mergeTarget;
                    mergeTarget = next;
                } else {
                    // no next, we can add back the merge target to the start list
                    startList.add(mergeTarget);
                    mergeTarget = null;
                }
            }
        }
        assert listConsistent() : "Start: " + startList + "\nEnd: " + endList;
    }

    boolean sorted(List<Segment> list, Comparator<Segment> comparator) {
        Segment prev = null;
        for (Segment elem : list) {
            if (prev != null && comparator.compare(prev, elem) > 0) {
                return false;
            }
            prev = elem;
        }
        return true;
    }

    /** Informs the segments a new scanline is started */
    public void lineComplete(int line) {
        // look for all the segments that have not been touched during the last scan
        for (Segment segment : new ArrayList<Segment>(startList)) {
            // if touched, we can continue using it
            if (segment.touched) {
                segment.touched = false;
                continue;
            }

            // if not, remove it from the search lists
            startList.remove(segment);
            endList.remove(segment);

            // can we merge it with an existing one?
            temp.setXY(segment.xStart, segment.yStart, segment.xStart, segment.yStart);
            Segment mergeTarget = null;
            MergePoint mergePoint = null;

            // end-start is the most efficient merge we can make, try it first
            mergeTarget = search(endList, temp);
            if (mergeTarget != null) {
                mergePoint = MergePoint.END_START;
            } else {
                mergeTarget = search(startList, temp);
                if (mergeTarget != null) {
                    mergePoint = MergePoint.START_START;
                } else {
                    temp.setXY(segment.xEnd, segment.yEnd, segment.xEnd, segment.yEnd);
                    mergeTarget = search(startList, temp);
                    if (mergeTarget != null) {
                        mergePoint = MergePoint.START_END;
                    } else {
                        mergeTarget = search(endList, temp);
                        if (mergeTarget != null) {
                            mergePoint = MergePoint.END_END;
                        }
                    }
                }
            }

            if (mergeTarget != null) {
                startList.remove(mergeTarget);
                endList.remove(mergeTarget);
                mergeTarget.merge(segment, mergePoint);
                startList.add(mergeTarget);
                endList.add(mergeTarget);
            } else {
                LineString ls = segment.toLineString();
                result.add(ls);
            }

            assert listConsistent() : "Start: " + startList + "\nEnd: " + endList;
        }
    }

    private boolean listConsistent() {
        Set<Segment> start = new HashSet<Segment>(startList);
        Set<Segment> end = new HashSet<Segment>(endList);
        return start.containsAll(end) && end.containsAll(start);
    }

    Segment search(TreeSet<Segment> list, Segment reference) {
        Segment segment = list.ceiling(reference);
        if (segment == null) {
            return null;
        }
        if (list == startList) {
            if (Segment.samePoint(segment.xStart, segment.yStart, reference.xStart, reference.yStart)) {
                return segment;
            }
        } else {
            if (Segment.samePoint(segment.xEnd, segment.yEnd, reference.xEnd, reference.yEnd)) {
                return segment;
            }
        }

        return null;
    }

    /**
     * Returns the merged and eventually simplified segments
     *
     * @return
     */
    public List<LineString> getMergedSegments() {
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Segments(")
                .append(startList.size())
                .append(",")
                .append(result.size())
                .append(") ");
        sb.append("active=");
        for (Segment segment : startList) {
            sb.append(segment).append("\n");
        }
        sb.append("complete=");
        for (LineString ls : result) {
            sb.append(ls).append("\n");
        }
        return sb.toString();
    }
}
