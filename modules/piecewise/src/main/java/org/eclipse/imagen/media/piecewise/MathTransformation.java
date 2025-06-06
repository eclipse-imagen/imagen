/* JAI-Ext - OpenSource Java Advanced Image Extensions Library
*    http://www.geo-solutions.it/
*    Copyright 2014 GeoSolutions


* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at

* http://www.apache.org/licenses/LICENSE-2.0

* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.eclipse.imagen.media.piecewise;

/**
 * Interface used for implementing a new Mathematic transformation.
 *
 * @author Nicola Lagomarsini geosolutions
 */
public interface MathTransformation {

    /**
     * Transforms input value using the provided transformation
     *
     * @param value
     * @throws TransformationException
     */
    double transform(double value) throws TransformationException;

    /**
     * Returns the derivative value of the provided transformation
     *
     * @param value
     * @throws Exception
     */
    double derivative(double value) throws Exception;

    /** Returns the input transformation dimensions */
    int getSourceDimensions();

    /** Returns the output transformation dimensions */
    int getTargetDimensions();

    /** Returns the inverse transformation of the current transform */
    MathTransformation inverseTransform();

    /** Indicates whether the transformation is an identity */
    boolean isIdentity();

    /**
     * Transform input {@link Position} into another {@link Position} instance
     *
     * @param ptSrc
     * @param ptDst
     * @throws TransformationException
     */
    Position transform(Position ptSrc, Position ptDst) throws TransformationException;
}
