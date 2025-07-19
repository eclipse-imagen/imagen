/* Copyright 2010-2011 Michael Bedward, and others
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
 *  Copyright (c) 2010-2011, Michael Bedward. All rights reserved.
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

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;

/**
 * Base class for Bezier smoothing of JTS Geometry objects.
 *
 * @author Michael Bedward
 * @since 1.1
 * @version $Id$
 */
public abstract class AbstractSmoother {

    /**
     * Default smoothing control. Specifies no minimum vertex distance and a constant number of points per smoothed
     * segment.
     */
    public static final SmootherControl DEFAULT_CONTROL = new SmootherControl() {
        public double getMinLength() {
            return 0.0;
        }

        public int getNumVertices(double length) {
            return 10;
        }
    };

    /** The current SmootherControl instance. */
    protected SmootherControl control;
    ;

    /** The current {@code GeometryFactory} being used. */
    protected final GeometryFactory geomFactory;

    /** Class to hold interpolation parameters for a given point. */
    protected static final class InterpPoint {
        double[] t = new double[4];
        double tsum;
    }

    /** Cache of previously calculated interpolation parameters */
    protected Map<Integer, WeakReference<InterpPoint[]>> lookup = new HashMap<Integer, WeakReference<InterpPoint[]>>();

    /**
     * Creates a new smoother that will use the given {@code GeometryFactory}.
     *
     * @param geomFactory factory to use for creating smoothed objects
     * @throws IllegalArgumentException if {@code geomFactory} is {@code null}
     */
    public AbstractSmoother(GeometryFactory geomFactory) {
        if (geomFactory == null) {
            throw new IllegalArgumentException("geomFactory must not be null");
        }
        this.geomFactory = geomFactory;

        this.control = DEFAULT_CONTROL;
    }

    /**
     * Sets a new {@code Control} object to for smoothing.
     *
     * @param control the control to use for smoothing; if {@code null} the default control will be set
     */
    public void setControl(SmootherControl control) {
        this.control = control == null ? DEFAULT_CONTROL : control;
    }

    /**
     * Calculates vertices along a cubic Bazier curve given start point, end point and two control points.
     *
     * @param start start position
     * @param end end position
     * @param ctrl1 first control point
     * @param ctrl2 second control point
     * @param nv number of vertices including the start and end points
     * @return vertices along the Bezier curve
     */
    protected Coordinate[] cubicBezier(
            final Coordinate start,
            final Coordinate end,
            final Coordinate ctrl1,
            final Coordinate ctrl2,
            final int nv) {

        final Coordinate[] curve = new Coordinate[nv];

        final Coordinate[] buf = new Coordinate[3];
        for (int i = 0; i < buf.length; i++) {
            buf[i] = new Coordinate();
        }

        curve[0] = new Coordinate(start);
        curve[nv - 1] = new Coordinate(end);
        InterpPoint[] ip = getInterpPoints(nv);

        for (int i = 1; i < nv - 1; i++) {
            Coordinate c = new Coordinate();

            c.x = ip[i].t[0] * start.x + ip[i].t[1] * ctrl1.x + ip[i].t[2] * ctrl2.x + ip[i].t[3] * end.x;
            c.x /= ip[i].tsum;
            c.y = ip[i].t[0] * start.y + ip[i].t[1] * ctrl1.y + ip[i].t[2] * ctrl2.y + ip[i].t[3] * end.y;
            c.y /= ip[i].tsum;

            curve[i] = c;
        }

        return curve;
    }

    /**
     * Gets the interpolation parameters for a Bezier curve approximated by the given number of vertices.
     *
     * @param npoints number of vertices
     * @return array of {@code InterpPoint} objects holding the parameter values
     */
    protected InterpPoint[] getInterpPoints(int npoints) {
        WeakReference<InterpPoint[]> ref = lookup.get(npoints);
        InterpPoint[] ip = null;
        if (ref != null) ip = ref.get();

        if (ip == null) {
            ip = new InterpPoint[npoints];

            for (int i = 0; i < npoints; i++) {
                double t = (double) i / (npoints - 1);
                double tc = 1.0 - t;

                ip[i] = new InterpPoint();
                ip[i].t[0] = tc * tc * tc;
                ip[i].t[1] = 3.0 * tc * tc * t;
                ip[i].t[2] = 3.0 * tc * t * t;
                ip[i].t[3] = t * t * t;
                ip[i].tsum = ip[i].t[0] + ip[i].t[1] + ip[i].t[2] + ip[i].t[3];
            }

            lookup.put(npoints, new WeakReference<InterpPoint[]>(ip));
        }

        return ip;
    }
}
