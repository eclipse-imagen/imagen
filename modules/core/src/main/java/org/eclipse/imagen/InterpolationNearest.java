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

package org.eclipse.imagen;

/**
 * A class representing nearest-neighbor interpolation. Since nearest-neighbor interpolation is simply pixel copying,
 * and not really interpolation at all, most code that performs nearest-neighbor sampling will want to use
 * special-purpose code. However, this class is provided both as a way to specify such interpolation, with the consumer
 * making use of 'instanceof' to detect the particular class, and as a way to force general Interpolation users to use
 * nearest-neighbor sampling.
 *
 * <p>Note that this interpolator does not actually select the "nearest" pixel, but only uses the truncated integer
 * pixel location (floor). This is an optimization reflecting an assumption about the implementation of the resampler.
 * It is assumed that the conversion of continuous source image coordinates to discrete pixel indices drops the final
 * subtraction of 0.5 for the case of a nearest-neighbor interpolator.
 *
 * <p>Neighborhoods of sizes 2x1, 1x2, 2x2, 4x1, 1x4, 4x4, Nx1 and 1xN, that is, all the interpolate() methods defined
 * in the Interpolation class, are supported in the interest of simplifying code that handles a number of types of
 * interpolation. In each case, the central sample is returned and the rest are ignored.
 *
 * <p>The class is marked 'final' so that it may be more easily inlined.
 */
public final class InterpolationNearest extends Interpolation {

    /**
     * Constructs an <code>InterpolationNearest</code>. The return value of <code>getSubsampleBitsH()</code> and <code>
     * getSubsampleBitsV()</code> will be 0.
     */
    public InterpolationNearest() {
        super(1, 1, 0, 0, 0, 0, 0, 0);
    }

    /**
     * Performs horizontal interpolation on a one-dimensional array of integral samples. The central sample (samples[0])
     * is returned.
     */
    public int interpolateH(int[] samples, int xfrac) {
        return samples[0];
    }

    /**
     * Performs vertical interpolation on a one-dimensional array of integral samples. The central sample (samples[0])
     * is returned.
     */
    public int interpolateV(int[] samples, int yfrac) {
        return samples[0];
    }

    /**
     * Performs interpolation on a two-dimensional array of integral samples. The central sample (samples[0][0]) is
     * returned.
     */
    public int interpolate(int[][] samples, int xfrac, int yfrac) {
        return samples[0][0];
    }

    /** Performs horizontal interpolation on a pair of integral samples. The central sample (s0) is returned. */
    public int interpolateH(int s0, int s1, int xfrac) {
        return s0;
    }

    /** Performs vertical interpolation on a pair of integral samples. The central sample (s0) is returned. */
    public int interpolateV(int s0, int s1, int yfrac) {
        return s0;
    }

    /** Performs interpolation on a 2x2 grid of integral samples. The central sample (s00) is returned. */
    public int interpolate(int s00, int s01, int s10, int s11, int xfrac, int yfrac) {
        return s00;
    }

    /** Performs interpolation on a 4x4 grid of integral samples. The central sample (s00) is returned. */
    public int interpolate(
            int s__,
            int s_0,
            int s_1,
            int s_2,
            int s0_,
            int s00,
            int s01,
            int s02,
            int s1_,
            int s10,
            int s11,
            int s12,
            int s2_,
            int s20,
            int s21,
            int s22,
            int xfrac,
            int yfrac) {
        return s00;
    }

    /**
     * Performs horizontal interpolation on a one-dimensional array of floating-point samples. The central sample (s0)
     * is returned.
     */
    public float interpolateH(float[] samples, float xfrac) {
        return samples[0];
    }

    /**
     * Performs vertical interpolation on a one-dimensional array of floating-point samples. The central sample (s0) is
     * returned.
     */
    public float interpolateV(float[] samples, float yfrac) {
        return samples[0];
    }

    /**
     * Performs interpolation on a two-dimensional array of floating-point samples. The central sample (samples[0][0])
     * is returned.
     */
    public float interpolate(float[][] samples, float xfrac, float yfrac) {
        return samples[0][0];
    }

    /** Performs horizontal interpolation on a pair of floating-point samples. The central sample (s0) is returned. */
    public float interpolateH(float s0, float s1, float xfrac) {
        return s0;
    }

    /** Performs vertical interpolation on a pair of floating-point samples. The central sample (s0) is returned. */
    public float interpolateV(float s0, float s1, float yfrac) {
        return s0;
    }

    /** Performs interpolation on a 2x2 grid of floating-point samples. The central sample (s00) is returned. */
    public float interpolate(float s00, float s01, float s10, float s11, float xfrac, float yfrac) {
        return s00;
    }

    /** Performs interpolation on a 4x4 grid of floating-point samples. The central sample (s00) is returned. */
    public float interpolate(
            float s__,
            float s_0,
            float s_1,
            float s_2,
            float s0_,
            float s00,
            float s01,
            float s02,
            float s1_,
            float s10,
            float s11,
            float s12,
            float s2_,
            float s20,
            float s21,
            float s22,
            float xfrac,
            float yfrac) {
        return s00;
    }

    /**
     * Performs horizontal interpolation on a one-dimensional array of double samples. The central sample (s0) is
     * returned.
     */
    public double interpolateH(double[] samples, float xfrac) {
        return samples[0];
    }

    /**
     * Performs vertical interpolation on a one-dimensional array of double samples. The central sample (s0) is
     * returned.
     */
    public double interpolateV(double[] samples, float yfrac) {
        return samples[0];
    }

    /**
     * Performs interpolation on a two-dimensional array of double samples. The central sample (samples[0][0]) is
     * returned.
     */
    public double interpolate(double[][] samples, float xfrac, float yfrac) {
        return samples[0][0];
    }

    /** Performs horizontal interpolation on a pair of double samples. The central sample (s0) is returned. */
    public double interpolateH(double s0, double s1, float xfrac) {
        return s0;
    }

    /** Performs vertical interpolation on a pair of double samples. The central sample (s0) is returned. */
    public double interpolateV(double s0, double s1, float yfrac) {
        return s0;
    }

    /** Performs interpolation on a 2x2 grid of double samples. The central sample (s00) is returned. */
    public double interpolate(double s00, double s01, double s10, double s11, float xfrac, float yfrac) {
        return s00;
    }

    /** Performs interpolation on a 4x4 grid of double samples. The central sample (s00) is returned. */
    public double interpolate(
            double s__,
            double s_0,
            double s_1,
            double s_2,
            double s0_,
            double s00,
            double s01,
            double s02,
            double s1_,
            double s10,
            double s11,
            double s12,
            double s2_,
            double s20,
            double s21,
            double s22,
            float xfrac,
            float yfrac) {
        return s00;
    }
}
