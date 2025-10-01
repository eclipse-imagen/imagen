/* Copyright 2009-2011 Michael Bedward, and others
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
 *  Copyright (c) 2009-2011, Michael Bedward. All rights reserved.
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

import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.imagen.KernelImageN;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPoint;

/**
 * Provides static methods to create a variety of raster kernels ({@code KernelImageN} objects).
 *
 * @author Michael Bedward
 * @since 1.0
 * @version $Id$
 */
public class KernelFactory {

    private static final double C_GAUSS = 1.0 / Math.sqrt(2 * Math.PI);
    private static final double C_QUARTIC = 15.0 / 16.0;
    private static final double C_TRIWEIGHT = 35.0 / 32.0;
    private static final double PI_ON_2 = Math.PI / 2;
    private static final double PI_ON_4 = Math.PI / 4;

    /** Constants specifying how kernel element values are calculated. */
    public static enum ValueType {

        /** Inside elements have value 1.0; outside 0.0 */
        BINARY,

        /** Value is {@code PI/4 * cos(uPI/2)} where {@code u} is proportional distance to the key element. */
        COSINE,

        /** Value is the distance to the kernel's key element. */
        DISTANCE,

        /** Value is {@code 3(1 - u^2)/4} where {@code u} is proportional distance to the key element. */
        EPANECHNIKOV,

        /** Value is {@code 1/sqrt(2PI) e^(-u^2 / 2)} where {@code u} is proportional distance to the key element. */
        GAUSSIAN,

        /** Value is the inverse distance to the kernel's key element. */
        INVERSE_DISTANCE,

        /**
         * Also known as biweight. Value is {@code 15/16 (1 - u^2)^2} where {@code u} is proportional distance to the
         * key element.
         */
        QUARTIC,

        /** Value is {@code 1 - u} where {@code u} is proportional distance to the key element. */
        TRIANGULAR,

        /**
         * Also known as tricubic. Value is {@code 35/32 (1 - u^2)^3} where {@code u} is proportional distance to the
         * key element.
         */
        TRIWEIGHT;
    };

    /**
     * Create a circular kernel where all elements within the circle have value 1.0 while those outside have value 0.0.
     * Kernel width is {@code 2*radius + 1}. The key element is at position {@code x=radius, y=radius}.
     *
     * <p>This is equivalent to:
     *
     * <pre><code>
     *     createCircle(radius, Kernel.ValueType.BINARY, 1.0f)
     * </code></pre>
     *
     * @param radius radius of the circle
     * @return a new {@code KernelImageN} object
     */
    public static KernelImageN createCircle(int radius) {
        return createConstantCircle(radius, 1.0f);
    }

    /**
     * Create a circular kernel where all elements within the circle have a constant value while those outside have
     * value 0.0. Kernel width is {@code 2*radius + 1}. The key element is at position {@code x=radius, y=radius}.
     *
     * @param radius radius of the circle
     * @param value constant value
     * @return a new {@code KernelImageN} object
     */
    public static KernelImageN createConstantCircle(int radius, float value) {
        if (radius <= 0) {
            throw new IllegalArgumentException("Invalid radius (" + radius + "); must be > 0");
        }

        KernelFactoryHelper helper = new KernelFactoryHelper();
        float[] weights = helper.makeCircle(radius);

        int w = 2 * radius + 1;
        helper.rowFill(weights, w, w, value);

        return new KernelImageN(w, w, weights);
    }

    /**
     * Creates a circular kernel with width {@code 2*radius + 1}. The key element is at position {@code x=radius,
     * y=radius}.
     *
     * @param radius the radius of the circle expressed in pixels
     * @param type a {@link ValueType} constant
     * @param centreValue the value to assign to the kernel centre (key element)
     * @return a new {@code KernelImageN} object
     * @deprecated Please use {@link #createCircle(int, ValueType)} instead and set the centre element value on the
     *     returned kernel if that is required.
     */
    public static KernelImageN createCircle(int radius, ValueType type, float centreValue) {
        KernelImageN kernel = createCircle(radius, type);
        return KernelUtil.setElement(kernel, radius, radius, centreValue);
    }

    /**
     * Creates a circular kernel with width {@code 2*radius + 1}. The key element is at position {@code x=radius,
     * y=radius}.
     *
     * <p>If {@code type} is {@link ValueType#INVERSE_DISTANCE} the kernel's key element will be set to 1.0f.
     *
     * @param radius the radius of the circle expressed in pixels
     * @param type a {@link ValueType} constant
     * @return a new {@code KernelImageN} object
     */
    public static KernelImageN createCircle(int radius, ValueType type) {

        if (radius <= 0) {
            throw new IllegalArgumentException("Invalid radius (" + radius + "); must be > 0");
        }

        int width = 2 * radius + 1;
        float[] weights = new float[width * width];

        float r2 = radius * radius;
        int k0 = 0;
        int k1 = weights.length - 1;

        double dist2 = 0;
        float value = 0f;
        for (int y = radius; y > 0; y--) {
            int y2 = y * y;
            for (int x = -radius; x <= radius; x++, k0++, k1--) {
                dist2 = x * x + y2;

                if (CompareOp.acompare(r2, dist2) >= 0) {
                    switch (type) {
                        case BINARY:
                            value = 1.0f;
                            break;

                        case COSINE:
                            value = (float) (PI_ON_4 * Math.cos(PI_ON_2 * Math.sqrt(dist2) / radius));
                            break;

                        case DISTANCE:
                            value = (float) Math.sqrt(dist2);
                            break;

                        case EPANECHNIKOV:
                            value = (float) (3.0 * (1.0 - dist2 / r2) / 4.0);
                            break;

                        case GAUSSIAN:
                            value = (float) (C_GAUSS * Math.exp(-0.5 * dist2 / r2));
                            break;

                        case INVERSE_DISTANCE:
                            value = (float) (1.0 / Math.sqrt(dist2));
                            break;

                        case QUARTIC:
                            double termq = 1.0 - dist2 / r2;
                            value = (float) (C_QUARTIC * termq * termq);
                            break;

                        case TRIANGULAR:
                            value = (float) (1.0 - Math.sqrt(dist2) / radius);
                            break;

                        case TRIWEIGHT:
                            double termt = 1.0 - dist2 / r2;
                            value = (float) (C_TRIWEIGHT * termt * termt * termt);
                            break;
                    }

                    weights[k0] = weights[k1] = value;
                }
            }
        }

        for (int x = -radius; x <= radius; x++, k0++) {
            if (x == 0 && type == ValueType.INVERSE_DISTANCE) {
                value = 1.0f;
            } else {
                switch (type) {
                    case BINARY:
                        value = 1.0f;
                        break;

                    case COSINE:
                        value = (float) (PI_ON_4 * Math.cos(PI_ON_2 * x / radius));
                        break;

                    case DISTANCE:
                        value = (float) Math.abs(x);
                        break;

                    case EPANECHNIKOV:
                        value = (float) (3.0 * (1.0 - (double) x * x / r2) / 4.0);
                        break;

                    case GAUSSIAN:
                        value = (float) (C_GAUSS * Math.exp(-0.5 * x * x / r2));
                        break;

                    case INVERSE_DISTANCE:
                        value = (float) (1.0 / Math.abs(x));
                        break;

                    case QUARTIC:
                        double termq = 1.0 - (double) x * x / r2;
                        value = (float) (C_QUARTIC * termq * termq);
                        break;

                    case TRIANGULAR:
                        value = (float) (1.0 - (double) Math.abs(x) / radius);
                        break;

                    case TRIWEIGHT:
                        double termt = 1.0 - (double) x * x / r2;
                        value = (float) (C_TRIWEIGHT * termt * termt * termt);
                        break;
                }
            }
            weights[k0] = value;
        }

        return new KernelImageN(width, width, weights);
    }

    /**
     * Creates an annular kernel (a doughnut). The kernel width is {@code 2*outerRadius + 1}. The kernel's key element
     * is at position {@code x=outerRadius, y=outerRadius}.
     *
     * <p>Calling this method with {@code innerRadius == 0} is equivalent to calling {@link #createCircle }
     *
     * @param outerRadius the radius of the annulus
     * @param innerRadius the radius of the 'hole'
     * @param type a {@link ValueType} constant
     * @param centreValue the value to assign to the kernel centre (key element)
     * @return a new {@code KernelImageN} object
     * @throws IllegalArgumentException if {@code outerRadius <= 0} or {@code innerRadius >= outerRadius}
     * @deprecated Please use {@link #createAnnulus(int, int, ValueType)} instead and set the centre element value on
     *     the returned kernel if that is required.
     */
    public static KernelImageN createAnnulus(int outerRadius, int innerRadius, ValueType type, float centreValue) {
        KernelImageN kernel = createAnnulus(outerRadius, innerRadius, type);
        return KernelUtil.setElement(kernel, outerRadius, outerRadius, centreValue);
    }

    /**
     * Creates an annular kernel (a doughnut) where elements inside the annulus have a constant value while those
     * outside are set to 0.
     *
     * <p>The kernel width is {@code 2*outerRadius + 1}. The kernel's key element is at position {@code x=outerRadius,
     * y=outerRadius}.
     *
     * @param outerRadius the outer radius of the annulus
     * @param innerRadius the radius of the 'hole'
     * @param value element value
     * @return a new {@code KernelImageN} object
     * @throws IllegalArgumentException if {@code outerRadius <= 0} or {@code innerRadius >= outerRadius}
     */
    public static KernelImageN createConstantAnnulus(int outerRadius, int innerRadius, float value) {
        if (outerRadius <= 0) {
            throw new IllegalArgumentException("outerRadius must be > 0");
        }
        if (innerRadius >= outerRadius) {
            throw new IllegalArgumentException("innerRadius must be less than outerRadius");
        }

        final int w = 2 * outerRadius + 1;
        float[] data = new float[w * w];

        double outer2 = outerRadius * outerRadius;
        double inner2 = innerRadius * innerRadius;
        double d2;
        int k = 0;
        for (int y = 0; y < w; y++) {
            double y2 = y * y;
            for (int x = 0; x < w; x++) {
                d2 = y2 + x * x;
                if (CompareOp.acompare(d2, outer2) <= 0 && CompareOp.acompare(d2, inner2) > 0) {
                    data[k] = value;
                } else {
                    data[k] = 0;
                }
            }
        }

        return new KernelImageN(w, w, data);
    }

    /**
     * Creates an annular kernel (a doughnut). If {@code innerRadius} is 0 the returned kernel will be identical to that
     * from {@code createCircle(outerRadius, type)}.
     *
     * <p>The kernel width is {@code 2*outerRadius + 1}. The kernel's key element is at position {@code x=outerRadius,
     * y=outerRadius}.
     *
     * <p>
     *
     * @param outerRadius the outer radius of the annulus
     * @param innerRadius the radius of the 'hole'
     * @param type a {@link ValueType} constant
     * @return a new {@code KernelImageN} object
     * @throws IllegalArgumentException if {@code outerRadius <= 0} or {@code innerRadius >= outerRadius}
     */
    public static KernelImageN createAnnulus(int outerRadius, int innerRadius, ValueType type) {

        if (innerRadius < 0) {
            throw new IllegalArgumentException("Invalid innerRadius (" + innerRadius + "); must be >= 0");
        }

        if (outerRadius <= innerRadius) {
            throw new IllegalArgumentException("outerRadius must be greater than innerRadius");
        }

        if (innerRadius == 0) {
            return createCircle(outerRadius, type);
        }

        int width = 2 * outerRadius + 1;
        float[] weights = new float[width * width];

        double outer2 = outerRadius * outerRadius;
        double inner2 = innerRadius * innerRadius;

        double dist2 = 0;
        float value = 0f;
        int k0 = 0;
        int k1 = weights.length - 1;

        for (int y = outerRadius; y > 0; y--) {
            int y2 = y * y;
            for (int x = -outerRadius; x <= outerRadius; x++, k0++, k1--) {
                dist2 = x * x + y2;

                if (CompareOp.acompare(dist2, outer2) <= 0 && CompareOp.acompare(dist2, inner2) > 0) {

                    switch (type) {
                        case BINARY:
                            value = 1.0f;
                            break;

                        case COSINE:
                            value = (float) (PI_ON_4 * Math.cos(PI_ON_2 * Math.sqrt(dist2) / outerRadius));
                            break;

                        case DISTANCE:
                            value = (float) Math.sqrt(dist2);
                            break;

                        case EPANECHNIKOV:
                            value = (float) (3.0 * (1.0 - dist2 / outer2) / 4.0);
                            break;

                        case GAUSSIAN:
                            value = (float) (C_GAUSS * Math.exp(-0.5 * dist2 / outer2));
                            break;

                        case INVERSE_DISTANCE:
                            value = (float) (1.0 / Math.sqrt(dist2));
                            break;

                        case QUARTIC:
                            double termq = 1.0 - dist2 / outer2;
                            value = (float) (C_QUARTIC * termq * termq);
                            break;

                        case TRIANGULAR:
                            value = (float) (1.0 - Math.sqrt(dist2) / outerRadius);
                            break;

                        case TRIWEIGHT:
                            double termt = 1.0 - dist2 / outer2;
                            value = (float) (C_TRIWEIGHT * termt * termt * termt);
                            break;
                    }
                    weights[k0] = weights[k1] = value;
                }
            }
        }

        for (int x = -outerRadius; x <= outerRadius; x++, k0++) {
            if (x < -innerRadius || x > innerRadius) {
                switch (type) {
                    case BINARY:
                        value = 1.0f;
                        break;

                    case COSINE:
                        value = (float) (PI_ON_4 * Math.cos(PI_ON_2 * x / outerRadius));
                        break;

                    case DISTANCE:
                        value = (float) Math.abs(x);
                        break;

                    case EPANECHNIKOV:
                        value = (float) (3.0 * (1.0 - (double) x * x / outer2) / 4.0);
                        break;

                    case GAUSSIAN:
                        value = (float) (C_GAUSS * Math.exp(-0.5 * x * x / outer2));
                        break;

                    case INVERSE_DISTANCE:
                        value = (float) (1.0 / Math.abs(x));
                        break;

                    case QUARTIC:
                        double termq = 1.0 - (double) x * x / outer2;
                        value = (float) (C_QUARTIC * termq * termq);
                        break;

                    case TRIANGULAR:
                        value = (float) (1.0 - (double) Math.abs(x) / outerRadius);
                        break;

                    case TRIWEIGHT:
                        double termt = 1.0 - (double) x * x / outer2;
                        value = (float) (C_TRIWEIGHT * termt * termt * termt);
                        break;
                }
                weights[k0] = value;
            }
        }

        return new KernelImageN(width, width, weights);
    }

    /**
     * Creates a rectangular kernel where all elements have value 1.0. The key element will be at {@code (width/2,
     * height/2)}.
     *
     * @param width rectangle width
     * @param height rectangle height
     * @return a new {@code KernelImageN} object
     * @throws IllegalArgumentException if either {@code width} or {@code height} are less than 1
     */
    public static KernelImageN createRectangle(int width, int height) {
        return createConstantRectangle(width, height, 1.0f);
    }

    /**
     * Creates a rectangular kernel where all elements have the same value. The key element will be at {@code (width/2,
     * height/2)}.
     *
     * @param width rectangle width
     * @param height rectangle height
     * @param value element value
     * @return a new {@code KernelImageN} object
     * @throws IllegalArgumentException if either {@code width} or {@code height} are less than 1
     */
    public static KernelImageN createConstantRectangle(int width, int height, float value) {
        if (width < 1 || height < 1) {
            throw new IllegalArgumentException("width and height must both be >= 1");
        }

        float[] weights = (new KernelFactoryHelper()).makeRect(width, height, value);
        return new KernelImageN(width, height, weights);
    }

    /**
     * Creates a rectangular kernel where all elements have the same value.
     *
     * @param width rectangle width
     * @param height rectangle height
     * @param value element value
     * @param keyX key element X ordinate
     * @param keyY key element Y ordinate
     * @return a new {@code KernelImageN} object
     * @throws IllegalArgumentException if either {@code width} or {@code height} are less than 1 or if the key element
     *     location is outside the rectangle
     */
    public static KernelImageN createConstantRectangle(int width, int height, int keyX, int keyY, float value) {

        if (width < 1 || height < 1) {
            throw new IllegalArgumentException("width and height must both be >= 1");
        }

        if (keyX < 0 || keyX >= width || keyY < 0 || keyY >= height) {
            throw new IllegalArgumentException("key element must be within the rectangle");
        }

        float[] weights = (new KernelFactoryHelper()).makeRect(width, height, value);
        return new KernelImageN(width, height, keyX, keyY, weights);
    }

    /**
     * Creates a rectangular kernel. If the element value type is one that involves proportional distance, such as
     * {@link ValueType#COSINE} or {@link ValueType#EPANECHNIKOV}, this is calculated as the proportion of the maximum
     * distance from the key element to a kernel edge element.
     *
     * @param width rectangle width
     * @param height rectangle height
     * @param type a {@link ValueType} constant
     * @param keyX X ordinate of the key element
     * @param keyY Y ordinate of the key element (0 is top)
     * @param keyValue value of the key element
     * @return a new {@code KernelImageN} object
     * @throws IllegalArgumentException if either {@code width} or {@code height} are less than 1; or if {@code keyX} is
     *     not in the interval {@code [0,width)}; or if {@code keyY} is not in the interval {@code [0,height)};
     * @deprecated Please use {@link #createRectangle(int, int, ValueType, int, int)} instead and set the centre element
     *     value on the returned kernel if that is required.
     */
    public static KernelImageN createRectangle(
            int width, int height, ValueType type, int keyX, int keyY, float keyValue) {

        KernelImageN kernel = createRectangle(width, height, type, keyX, keyY);
        return KernelUtil.setElement(kernel, keyX, keyY, keyValue);
    }

    /**
     * Creates a rectangular kernel. If the element value type is one that involves proportional distance, such as
     * {@link ValueType#COSINE} or {@link ValueType#EPANECHNIKOV}, this is calculated as the proportion of the maximum
     * distance from the key element to a kernel edge element.
     *
     * @param width rectangle width
     * @param height rectangle height
     * @param type a {@link ValueType} constant
     * @param keyX X ordinate of the key element
     * @param keyY Y ordinate of the key element (0 is top)
     * @return a new {@code KernelImageN} object
     * @throws IllegalArgumentException if either {@code width} or {@code height} are less than 1; or if {@code keyX} is
     *     not in the interval {@code [0,width)}; or if {@code keyY} is not in the interval {@code [0,height)};
     */
    public static KernelImageN createRectangle(int width, int height, ValueType type, int keyX, int keyY) {

        if (width < 1) {
            throw new IllegalArgumentException("width must be >= 1");
        }

        if (height < 1) {
            throw new IllegalArgumentException("height must be >= 1");
        }

        if (!(keyX >= 0 && keyX < width) || !(keyY >= 0 && keyY < height)) {
            throw new IllegalArgumentException(
                    "key element position " + keyX + "," + keyY + " is outside rectangle bounds");
        }

        KernelFactoryHelper kh = new KernelFactoryHelper();
        float weights[];

        if (type == ValueType.BINARY) {
            weights = kh.makeRect(width, height, 1.0f);
            return new KernelImageN(width, height, keyX, keyY, weights);
        }

        weights = new float[width * height];
        int k = 0;

        // find distance from key element to most distance edge element
        int dx = Math.max(keyX, width - 1 - keyX);
        int dy = Math.max(keyY, height - 1 - keyY);
        double dmax2 = dx * dx + dy * dy;
        double dmax = Math.sqrt(dmax2);

        Point2D p = new Point(keyX, keyY);
        double dist2 = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++, k++) {
                dist2 = (float) p.distanceSq(x, y);
                switch (type) {
                    case COSINE:
                        weights[k] = (float) (PI_ON_4 * Math.cos(PI_ON_2 * Math.sqrt(dist2) / dmax));
                        break;

                    case DISTANCE:
                        weights[k] = (float) Math.sqrt(dist2);
                        break;

                    case EPANECHNIKOV:
                        weights[k] = (float) (3.0 * (1.0 - dist2 / dmax2) / 4.0);
                        break;

                    case GAUSSIAN:
                        weights[k] = (float) (C_GAUSS * Math.exp(-0.5 * dist2 / dmax2));
                        break;

                    case INVERSE_DISTANCE:
                        if (dist2 < 1.0) {
                            weights[k] = 1.0f;
                        } else {
                            weights[k] = (float) (1.0 / Math.sqrt(dist2));
                        }
                        break;

                    case QUARTIC:
                        double termq = 1.0 - dist2 / dmax2;
                        weights[k] = (float) (C_QUARTIC * termq * termq);
                        break;

                    case TRIANGULAR:
                        weights[k] = (float) (1.0 - Math.sqrt(dist2) / dmax);
                        break;

                    case TRIWEIGHT:
                        double termt = 1.0 - dist2 / dmax2;
                        weights[k] = (float) (C_TRIWEIGHT * termt * termt * termt);
                        break;
                }
            }
        }

        return new KernelImageN(width, height, keyX, keyY, weights);
    }

    /**
     * Create a kernel by rasterizing a shape. The shape must be a closed polygon. Kernel element centre coordinates are
     * used to test whether elements are inside the shape.
     *
     * <p>This method can cope with arbitrary shape bounds, ie. there is no need to set the bounding rectangle to have
     * origin x=0, y=0. The values of keyX and keyY, which specify the position of the kernel's key element, must be
     * within the bounds of the shape as passed to this method, but do not need to be inside the shape itself.
     *
     * @param shape an object representing a closed polygon
     * @param transform an optional AffineTransform to relate shape coordinates to kernel element coordinates. May be
     *     null. This is useful to scale and/or rotate the shape.
     * @param type a {@link ValueType} constant
     * @param keyX X ordinate of the key element
     * @param keyY Y ordinate of the key element
     * @param keyValue the value of the key element
     * @return a new instance of KernelImageN
     */
    public static KernelImageN createFromShape(
            Shape shape, AffineTransform transform, ValueType type, int keyX, int keyY, float keyValue) {
        /*
         * First we transform the shape to a JTS Polygon object
         * so we can take advantage of the JTS intersects method
         * and avoid rasterizing artefacts which can arise when
         * using Shape.contains
         */
        PathIterator iter = shape.getPathIterator(transform, 0.05);
        float[] buf = new float[6];
        List<Coordinate> coords = new ArrayList<>();
        while (!iter.isDone()) {
            iter.currentSegment(buf);
            coords.add(new Coordinate(buf[0], buf[1]));
            iter.next();
        }

        GeometryFactory gf = new GeometryFactory();
        Coordinate[] coordsAr = coords.toArray(new Coordinate[coords.size()]);
        Geometry poly = gf.createPolygon(gf.createLinearRing(coordsAr), null);

        Envelope env = poly.getEnvelopeInternal();

        int left = (int) Math.floor(env.getMinX());
        int right = (int) Math.ceil(env.getMaxX());
        int top = (int) Math.ceil(env.getMaxY());
        int bottom = (int) Math.floor(env.getMinY());

        int width = right - left + 1;
        int height = top - bottom + 1;

        float[] weights = new float[width * height];
        int[] offset = new int[height];
        for (int i = 0, o = 0; i < offset.length; i++, o += width) offset[i] = o;

        coords.clear();
        double y = top;
        for (int iy = 0; iy < height; y--, iy++) {
            double x = left;
            for (int ix = 0; ix < width; x++, ix++) {
                coords.add(new Coordinate(x, y));
            }
        }

        /*
         * Now we buffer the polygon by a small amount to avoid
         * rejecting points that lie exactly on the boundary
         */
        MultiPoint mp = gf.createMultiPoint(coords.toArray(new Coordinate[coords.size()]));
        Geometry inside = mp.intersection(poly.buffer(0.05, Math.max(width / 2, 10)));

        final int n = inside.getNumGeometries();

        /*
         * If required, find the maximum distance between the key element lcoation
         * and a kernel edge element.
         */
        double dmax = 0, dmax2 = 0;
        switch (type) {
            case COSINE:
            case EPANECHNIKOV:
            case GAUSSIAN:
            case QUARTIC:
            case TRIANGULAR:
            case TRIWEIGHT:
                for (int i = 0; i < n; i++) {
                    Geometry g = inside.getGeometryN(i);
                    Coordinate c = g.getCoordinate();
                    double d2 = Point2D.distanceSq(keyX, keyY, (int) c.x, (int) c.y);
                    if (d2 > dmax2) {
                        dmax2 = d2;
                    }
                }
                dmax = Math.sqrt(dmax2);
        }

        /*
         * For each intersecting point we set a kernel element
         */
        double dist2 = 0;
        for (int i = 0; i < n; i++) {
            Geometry g = inside.getGeometryN(i);
            Coordinate c = g.getCoordinate();
            int index = (int) c.x - left + offset[(int) c.y - bottom];

            if (type != ValueType.BINARY) {
                dist2 = Point2D.distanceSq(keyX, keyY, (int) c.x, (int) c.y);
            }

            switch (type) {
                case BINARY:
                    weights[index] = 1.0f;
                    break;

                case COSINE:
                    weights[index] = (float) (PI_ON_4 * Math.cos(PI_ON_2 * Math.sqrt(dist2) / dmax));
                    break;

                case DISTANCE:
                    weights[index] = (float) Math.sqrt(dist2);
                    break;

                case EPANECHNIKOV:
                    weights[index] = (float) (3.0 * (1.0 - dist2 / dmax2) / 4.0);
                    break;

                case GAUSSIAN:
                    weights[index] = (float) (C_GAUSS * Math.exp(-0.5 * dist2 / dmax2));
                    break;

                case INVERSE_DISTANCE:
                    weights[index] = (float) (1.0 / Math.sqrt(dist2));
                    break;

                case QUARTIC:
                    double termq = 1.0 - dist2 / dmax2;
                    weights[index] = (float) (C_QUARTIC * termq * termq);
                    break;

                case TRIANGULAR:
                    weights[index] = (float) (1.0 - Math.sqrt(dist2) / dmax);
                    break;

                case TRIWEIGHT:
                    double termt = 1.0 - dist2 / dmax2;
                    weights[index] = (float) (C_TRIWEIGHT * termt * termt * termt);
                    break;
            }
        }

        // set the key element to the requested value
        weights[keyX + offset[keyY]] = keyValue;

        return new KernelImageN(width, height, keyX, keyY, weights);
    }
}
