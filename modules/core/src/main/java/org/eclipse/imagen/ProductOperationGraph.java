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
 * ProductOperationGraph manages a list of descriptors belonging to a particular product. The descriptors have pairwise
 * preferences between them.
 *
 * <p>This class extends "OperationGraph" which provide the other operations such as remove, lookup, set/unset
 * preference etc.
 *
 * <p>This class is used by the implementation of the OperationRegistry class and is not intended to be part of the API.
 *
 * @see OperationGraph
 *     <p>- Moved most of the functionality to "OperationGraph" which has been generalized to maintain product as well
 *     as factory tree
 */
final class ProductOperationGraph extends OperationGraph implements java.io.Serializable {

    /** Constructs an <code>ProductOperationGraph</code>. */
    ProductOperationGraph() {
        // Use the name of the PartialOrderNode for comparisions
        super(true);
    }

    /**
     * Adds a product to an <code>ProductOperationGraph</code>. A new <code>PartialOrderNode</code> is constructed to
     * hold the product and its graph adjacency information.
     */
    void addProduct(String productName) {
        addOp(new PartialOrderNode(new OperationGraph(), productName));
    }
}
