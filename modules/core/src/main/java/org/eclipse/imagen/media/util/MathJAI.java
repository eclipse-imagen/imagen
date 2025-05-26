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

package org.eclipse.imagen.media.util;

/** A utility class to contain miscellaneous static methods. */
public class MathJAI {
    /**
     * Calculate the smallest positive power of 2 greater than or equal to the provided parameter.
     *
     * @param n The value for which the next power of 2 is to be found.
     * @return The smallest power of 2 >= <i>n</i>.
     */
    public static final int nextPositivePowerOf2(int n) {
        if (n < 2) {
            return 2;
        }

        int power = 1;
        while (power < n) {
            power <<= 1;
        }

        return power;
    }

    /**
     * Determine whether the parameter is equal to a positive power of 2.
     *
     * @param n The value to check.
     * @return Whether <code>n</code> is a positive power of 2.
     */
    public static final boolean isPositivePowerOf2(int n) {
        return n == nextPositivePowerOf2(n);
    }
}
