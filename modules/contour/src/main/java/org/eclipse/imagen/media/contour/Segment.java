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
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.eclipse.imagen.media.contour;

import java.util.Comparator;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.impl.PackedCoordinateSequence;
import org.locationtech.jts.geom.impl.PackedCoordinateSequence.Double;
import org.locationtech.jts.geom.impl.PackedCoordinateSequenceFactory;

final class Segment {
    private static final GeometryFactory GF = new GeometryFactory();

    enum MergePoint {
        START_START,
        START_END,
        END_START,
        END_END
    };

    static final Comparator<Segment> START_COMPARATOR = new StartComparator();

    static final Comparator<Segment> END_COMPARATOR = new EndComparator();

    static final double EPS = 1e-4;

    static final int DEFAULT_SIZE = 32;

    /** Modified during the last scanline */
    boolean touched = true;

    /**
     * Ordinates array. It gets populated from the mid, we don't know if we are going to grow it towards left or right
     * (and it might be grown both directions, actually)
     */
    double[] ordinates;

    int idxFirst;

    int idxLast;

    double xStart;

    double yStart;

    double xEnd;

    double yEnd;

    final boolean simplify;

    double dxStart;

    double dyStart;

    double dxEnd;

    double dyEnd;

    /** Special constructor for the segment used to search in the start and end lists */
    public Segment() {
        this.simplify = false;
    }

    public Segment(double x1, double y1, double x2, double y2, int initialSize, boolean simplify) {
        assert initialSize % 4 == 0 && initialSize > 0;
        setXY(x1, y1, x2, y2);
        this.simplify = simplify;
        ordinates = new double[initialSize];
        idxFirst = initialSize / 2;
        idxLast = idxFirst;
        ordinates[idxLast++] = x1;
        ordinates[idxLast++] = y1;
        ordinates[idxLast++] = x2;
        ordinates[idxLast++] = y2;
    }

    /**
     * Method used only to initialize the search segment
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     */
    public void setXY(double x1, double y1, double x2, double y2) {
        assert ordinates == null;
        this.xStart = x1;
        this.yStart = y1;
        this.xEnd = x2;
        this.yEnd = y2;
        this.xStart = x1;
        this.yStart = y1;
        this.xEnd = x2;
        this.yEnd = y2;
        this.dxEnd = x2 - x1;
        this.dyEnd = y2 - y1;
        this.dxStart = dxEnd;
        this.dyStart = dyEnd;
    }

    Segment(double x1, double y1, double x2, double y2, boolean simplify) {
        this(x1, y1, x2, y2, DEFAULT_SIZE, simplify);
    }

    private void addAfterEnd(double x, double y, boolean touch) {
        if (simplify && sameSlope(dxEnd, dyEnd, x - xEnd, y - yEnd)) {
            ordinates[idxLast - 2] = x;
            ordinates[idxLast - 1] = y;
        } else {
            if (idxLast >= ordinates.length - 1) {
                int extra = ordinates.length / 2;
                double[] newOrdinates = new double[ordinates.length + extra];
                System.arraycopy(ordinates, 0, newOrdinates, 0, ordinates.length);
                this.ordinates = newOrdinates;
                assert isConsistent();
            }
            ordinates[idxLast++] = x;
            ordinates[idxLast++] = y;
            if (simplify) {
                dxEnd = x - xEnd;
                dyEnd = y - yEnd;
            }
        }
        xEnd = x;
        yEnd = y;
        if (touch) {
            touched = true;
        }
    }

    private boolean isConsistent() {
        boolean sameStart = samePoint(xStart, yStart, ordinates[idxFirst], ordinates[idxFirst + 1]);
        boolean sameEnd = samePoint(xEnd, yEnd, ordinates[idxLast - 2], ordinates[idxLast - 1]);
        return sameStart && sameEnd;
    }

    public void addAfterEnd(double x, double y) {
        addAfterEnd(x, y, true);
    }

    public void addBeforeStart(double x, double y) {
        addBeforeStart(x, y, true);
    }

    private void addBeforeStart(double x, double y, boolean touch) {
        // ensure we are not adding useless points
        if (simplify && sameSlope(dxStart, dyStart, xStart - x, yStart - y)) {
            ordinates[idxFirst] = x;
            ordinates[idxFirst + 1] = y;
        } else {
            if (idxFirst <= 1) {
                int extra = ordinates.length / 2;
                double[] newOrdinates = new double[ordinates.length + extra];
                System.arraycopy(ordinates, 0, newOrdinates, extra, ordinates.length);
                this.ordinates = newOrdinates;
                idxFirst += extra;
                idxLast += extra;
                assert isConsistent();
            }
            ordinates[--idxFirst] = y;
            ordinates[--idxFirst] = x;
            if (simplify) {
                dxStart = xStart - x;
                dyStart = yStart - y;
            }
        }
        xStart = x;
        yStart = y;
        if (touch) {
            touched = true;
        }
    }

    private boolean sameSlope(double dx1, double dy1, double dx2, double dy2) {
        if (sameOrdinate(dx1, 0)) {
            return sameOrdinate(dx2, 0) && Math.signum(dy1) == Math.signum(dy2);
        } else if (sameOrdinate(dx2, 0)) {
            return false;
        } else {
            return sameOrdinate(dy1 / dx1, dy2 / dx2) && Math.signum(dx1) == Math.signum(dx2);
        }
    }

    public LineString toLineString() {
        double[] ordinates = new double[idxLast - idxFirst];
        System.arraycopy(this.ordinates, idxFirst, ordinates, 0, ordinates.length);
        PackedCoordinateSequence.Double cs =
                (Double) PackedCoordinateSequenceFactory.DOUBLE_FACTORY.create(ordinates, 2);
        return GF.createLineString(cs);
    }

    public void merge(Segment other, MergePoint mergePoint) {
        switch (mergePoint) {
            case START_START:
                mergeStartStart(other);
                break;
            case START_END:
                mergeStartEnd(other);
                break;
            case END_START:
                mergeEndStart(other);
                break;
            case END_END:
                mergeEndEnd(other);
                break;
            default:
                throw new IllegalArgumentException("Unrecognized merge point: " + mergePoint);
        }
    }

    private void mergeEndEnd(Segment other) {
        int lengthOther = other.idxLast - other.idxFirst - 2;
        if (ordinates.length - idxLast < lengthOther) {
            int extra = lengthOther - ordinates.length + idxLast;
            double[] newOrdinates = new double[ordinates.length + extra];
            System.arraycopy(ordinates, 0, newOrdinates, 0, ordinates.length);
            this.ordinates = newOrdinates;
            assert isConsistent();
        }
        if (simplify && sameSlope(dxEnd, dyEnd, -other.dxEnd, -other.dyEnd)) {
            // skip our ending point
            idxLast -= 2;
        }
        for (int i = other.idxLast - 2; i > other.idxFirst; ) {
            double y = other.ordinates[--i];
            double x = other.ordinates[--i];
            ordinates[idxLast++] = x;
            ordinates[idxLast++] = y;
        }
        this.xEnd = other.xStart;
        this.yEnd = other.yStart;
        this.dxEnd = other.dxStart;
        this.dyEnd = other.dyStart;
    }

    private void mergeEndStart(Segment other) {
        int lengthOther = other.idxLast - other.idxFirst - 2;
        if (ordinates.length - idxLast < lengthOther) {
            int extra = lengthOther - ordinates.length + idxLast;
            double[] newOrdinates = new double[ordinates.length + extra];
            System.arraycopy(ordinates, 0, newOrdinates, 0, ordinates.length);
            this.ordinates = newOrdinates;
            assert isConsistent();
        }
        if (simplify && sameSlope(dxEnd, dyEnd, other.dxStart, other.dyStart)) {
            // skip our original starting point
            idxLast -= 2;
        }
        System.arraycopy(other.ordinates, other.idxFirst + 2, ordinates, idxLast, lengthOther);
        idxLast += lengthOther;
        this.xEnd = other.xEnd;
        this.yEnd = other.yEnd;
        this.dxEnd = other.dxEnd;
        this.dyEnd = other.dyEnd;
    }

    private void mergeStartEnd(Segment other) {
        int lengthOther = other.idxLast - other.idxFirst - 2;
        if (idxFirst < lengthOther) {
            int extra = lengthOther - idxFirst;
            double[] newOrdinates = new double[ordinates.length + extra];
            System.arraycopy(ordinates, 0, newOrdinates, extra, ordinates.length);
            this.ordinates = newOrdinates;
            idxFirst += extra;
            idxLast += extra;
            assert isConsistent();
        }
        if (simplify && sameSlope(dxStart, dyStart, other.dxEnd, other.dyEnd)) {
            // skip our original starting point
            idxFirst += 2;
        }
        for (int i = other.idxLast - 2; i > other.idxFirst; ) {
            double y = other.ordinates[--i];
            double x = other.ordinates[--i];
            ordinates[--idxFirst] = y;
            ordinates[--idxFirst] = x;
        }
        this.xStart = other.xStart;
        this.yStart = other.yStart;
        this.dxStart = other.dxEnd;
        this.dyStart = other.dyEnd;
    }

    private void mergeStartStart(Segment other) {
        int lengthOther = other.idxLast - other.idxFirst - 2;
        if (idxFirst < lengthOther) {
            int extra = lengthOther - idxFirst;
            double[] newOrdinates = new double[ordinates.length + extra];
            System.arraycopy(ordinates, 0, newOrdinates, extra, ordinates.length);
            this.ordinates = newOrdinates;
            idxFirst += extra;
            idxLast += extra;
            assert isConsistent();
        }
        if (simplify && sameSlope(dxStart, dyStart, -other.dxStart, -other.dyStart)) {
            // skip our original starting point
            idxFirst += 2;
        }
        for (int i = other.idxFirst + 2; i < other.idxLast; ) {
            double x = other.ordinates[i++];
            double y = other.ordinates[i++];
            ordinates[--idxFirst] = y;
            ordinates[--idxFirst] = x;
        }
        this.xStart = other.xEnd;
        this.yStart = other.yEnd;
        this.dxStart = -other.dxEnd;
        this.dyStart = -other.dyEnd;
    }

    static boolean samePoint(double x1, double y1, double x2, double y2) {
        return sameOrdinate(x1, x2) && sameOrdinate(y1, y2);
    }

    static boolean sameOrdinate(double o1, double o2) {
        return Math.abs(o1 - o2) < EPS;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Segment");
        if (touched) {
            sb.append("*");
        }
        sb.append("(")
                .append(xStart)
                .append(" ")
                .append(yStart)
                .append(", ")
                .append(xEnd)
                .append(" ")
                .append(yEnd)
                .append(")");
        sb.append("[");
        for (int i = idxFirst; i < idxLast; ) {
            sb.append(ordinates[i++]).append(" ");
            sb.append(ordinates[i++]);
            if (i < idxLast) {
                sb.append(", ");
            }
        }

        return sb.append("]").toString();
    }

    int getNumCoordinates() {
        return (idxLast - idxFirst) / 2;
    }

    private static final class StartComparator implements Comparator<Segment> {

        public int compare(Segment s1, Segment s2) {
            double difference = s1.xStart - s2.xStart;
            if (Math.abs(difference) < EPS) {
                difference = s1.yStart - s2.yStart;
                if (Math.abs(difference) < EPS) {
                    return 0;
                }
            }
            return (int) Math.signum(difference);
        }
    }

    private static final class EndComparator implements Comparator<Segment> {

        public int compare(Segment s1, Segment s2) {
            double difference = s1.xEnd - s2.xEnd;
            if (Math.abs(difference) < EPS) {
                difference = s1.yEnd - s2.yEnd;
                if (Math.abs(difference) < EPS) {
                    return 0;
                }
            }
            return (int) Math.signum(difference);
        }
    }
}
