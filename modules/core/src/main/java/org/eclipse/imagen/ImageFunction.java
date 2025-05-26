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
 * ImageFunction is a common interface for vector-valued functions which are to be evaluated at positions in the X-Y
 * coordinate system. At each position the value of such a function may contain one or more elements each of which may
 * be complex.
 */
public interface ImageFunction {

    /** Returns whether or not each value's elements are complex. */
    boolean isComplex();

    /** Returns the number of elements per value at each position. */
    int getNumElements();

    /**
     * Returns all values of a given element for a specified set of coordinates. An ArrayIndexOutOfBoundsException may
     * be thrown if the length of the supplied array(s) is insufficient.
     *
     * @param startX The X coordinate of the upper left location to evaluate.
     * @param startY The Y coordinate of the upper left location to evaluate.
     * @param deltaX The horizontal increment.
     * @param deltaY The vertical increment.
     * @param countX The number of points in the horizontal direction.
     * @param countY The number of points in the vertical direction.
     * @param real A pre-allocated float array of length at least countX*countY in which the real parts of all elements
     *     will be returned.
     * @param imag A pre-allocated float array of length at least countX*countY in which the imaginary parts of all
     *     elements will be returned; may be null for real data, i.e., when <code>isComplex()</code> returns false.
     * @throws ArrayIndexOutOfBoundsException if the length of the supplied array(s) is insufficient.
     */
    void getElements(
            float startX,
            float startY,
            float deltaX,
            float deltaY,
            int countX,
            int countY,
            int element,
            float[] real,
            float[] imag);

    /**
     * Returns all values of a given element for a specified set of coordinates. An ArrayIndexOutOfBoundsException may
     * be thrown if the length of the supplied array(s) is insufficient.
     *
     * @param startX The X coordinate of the upper left location to evaluate.
     * @param startY The Y coordinate of the upper left location to evaluate.
     * @param deltaX The horizontal increment.
     * @param deltaY The vertical increment.
     * @param countX The number of points in the horizontal direction.
     * @param countY The number of points in the vertical direction.
     * @param real A pre-allocated double array of length at least countX*countY in which the real parts of all elements
     *     will be returned.
     * @param imag A pre-allocated double array of length at least countX*countY in which the imaginary parts of all
     *     elements will be returned; may be null for real data, i.e., when <code>isComplex()</code> returns false.
     * @throws ArrayIndexOutOfBoundsException if the length of the supplied array(s) is insufficient.
     */
    void getElements(
            double startX,
            double startY,
            double deltaX,
            double deltaY,
            int countX,
            int countY,
            int element,
            double[] real,
            double[] imag);
}
