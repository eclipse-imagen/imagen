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
 * SPDX-License-Identifier: Apache-2.0
 */
package org.eclipse.imagen.iterator;

/**
 * An iterator for traversing a read/write image in top-to-bottom, left-to-right order. This will generally be the
 * fastest style of iterator, since it does not need to perform bounds checks against the top or left edges of tiles.
 *
 * <p>The iterator is initialized with a particular rectangle as its bounds, which it is illegal to exceed. This
 * initialization takes place in a factory method and is not a part of the iterator interface itself. Once initialized,
 * the iterator may be reset to its initial state by means of the startLine(), startPixels(), and startBands() methods.
 * Its position may be advanced using the nextLine(), jumpLines(), nextPixel(), jumpPixels(), and nextBand() methods.
 *
 * <p>The iterator's position may be tested against the bounding rectangle by means of the finishedLines(),
 * finishedPixels(), and finishedBands() methods, as well as the hybrid methods nextLineDone(), nextPixelDone(), and
 * nextBandDone().
 *
 * <p>The getSample(), getSampleFloat(), and getSampleDouble() methods are provided to allow read-only access to the
 * source data. The various source bands may also be accessed in random fashion using the variants that accept a band
 * index. The getPixel() methods allow retrieval of all bands simultaneously.
 *
 * <p>WritableRookIter adds the ability to alter the source pixel values using the various setSample() and setPixel()
 * methods.
 *
 * <p>An instance of WritableRectIter may be obtained by means of the RectIterFactory.createWritable() method, which
 * returns an opaque object implementing this interface.
 *
 * @see RectIter
 * @see RectIterFactory
 */
public interface WritableRectIter extends RectIter {

    /** Sets the current sample to an integral value. */
    void setSample(int s);

    /** Sets the specified sample of the current pixel to an integral value. */
    void setSample(int b, int s);

    /** Sets the current sample to a float value. */
    void setSample(float s);

    /** Sets the specified sample of the current pixel to a float value. */
    void setSample(int b, float s);

    /** Sets the current sample to a double value. */
    void setSample(double x);

    /** Sets the specified sample of the current pixel to a double value. */
    void setSample(int b, double s);

    /**
     * Sets all samples of the current pixel to a set of int values.
     *
     * @param iArray an int array containing a value for each band.
     */
    void setPixel(int[] iArray);

    /**
     * Sets all samples of the current pixel to a set of float values.
     *
     * @param fArray a float array containing a value for each band.
     */
    void setPixel(float[] fArray);

    /**
     * Sets all samples of the current pixel to a set of double values.
     *
     * @param dArray a double array containing a value for each band.
     */
    void setPixel(double[] dArray);
}
