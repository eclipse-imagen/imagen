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
 * An iterator for traversing a read/write image using arbitrary up-down and left-right moves. This will generally be
 * somewhat slower than a corresponding instance of RectIter, since it must perform bounds checks against the top and
 * left edges of tiles in addition to their botton and right edges.
 *
 * <p>The iterator is initialized with a particular rectangle as its bounds, which it is illegal to exceed. This
 * initialization takes place in a factory method and is not a part of the iterator interface itself. Once initialized,
 * the iterator may be reset to its initial state by means of the startLine(), startPixels(), and startBands() methods.
 * As with RectIter, its position may be advanced using the nextLine(), jumpLines(), nextPixel(), jumpPixels(), and
 * nextBand() methods.
 *
 * <p>In addition, prevLine(), prevPixel(), and prevBand() methods exist to move in the upwards and leftwards directions
 * and to access smaller band indices. The iterator may be set to the far edges of the bounding rectangle by means of
 * the endLines(), endPixels(), and endBands() methods.
 *
 * <p>The iterator's position may be tested against the bounding rectangle by means of the finishedLines(),
 * finishedPixels(), and finishedBands() methods, as well as the hybrid methods nextLineDone(), prevLineDone(),
 * nextPixelDone(), prevPixelDone(), nextBandDone(), and prevBandDone().
 *
 * <p>The getSample(), getSampleFloat(), and getSampleDouble() methods are provided to allow read-only access to the
 * source data. The various source bands may also be accessed in random fashion using the variants that accept a band
 * index. The getPixel() methods allow retrieval of all bands simultaneously.
 *
 * <p>WritableRookIter adds the ability to alter the source pixel values using the various setSample() and setPixel()
 * methods. These methods are inherited from the WritableRectIter interface unchanged.
 *
 * <p>An instance of WritableRookIter may be obtained by means of the RookIterFactory.createWritable() method, which
 * returns an opaque object implementing this interface.
 *
 * <p>Note that a WritableRookIter inherits multiply from RookIter and WritableRectIter, and so may be passed into code
 * expecting either interface. WritableRookIter in fact adds no methods not found in one of its parent interfaces.
 *
 * @see RookIter
 * @see WritableRectIter
 * @see RookIterFactory
 */
public interface WritableRookIter extends RookIter, WritableRectIter {

    // No new methods
}
