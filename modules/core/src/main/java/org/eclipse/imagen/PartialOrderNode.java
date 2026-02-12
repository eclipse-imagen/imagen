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
package org.eclipse.imagen;

import java.util.Enumeration;
import java.util.Vector;

/**
 * A node in a directed graph of operations. Each node maintains three pieces of information, in addition to an
 * arbitrary <code>Object</code> containing user data associated with the node, in order to allow topological sorting to
 * be performed in linear time.
 *
 * <p>First, the in-degree (number of other nodes pointing to this node) is stored as an int. Nodes with in-degree equal
 * to 0 are "free" and may appear first in a topological sort.
 *
 * <p>Second, a reference called <code>zeroLink</code> to another <code>PartialOrderNode</code> is kept in order to
 * allow construction of a linked list of nodes with zero in-degree.
 *
 * <p>Third, a <code>Vector</code> of neighboring nodes is maintained (in no particular order). These are the nodes
 * which are pointed to by the current node.
 *
 * <p>This class is used by the implementation of the <code>OperationRegistry</code> class and is not intended to be
 * part of the API.
 */
final class PartialOrderNode implements Cloneable, java.io.Serializable {

    /** The name of the object associated with this node. */
    protected String name;

    /** The data associated with this node. */
    protected Object nodeData;

    /** The in-degree of the node. */
    protected int inDegree = 0;

    /** Copy of the inDegree of the node. */
    protected int copyInDegree = 0;

    /** A link to another node with 0 in-degree, or null. */
    protected PartialOrderNode zeroLink = null;

    /** A Vector of neighboring nodes. */
    Vector neighbors = new Vector();

    /**
     * Constructs an <code>PartialOrderNode</code> with given associated data.
     *
     * @param nodeData an <code>Object</code> to associate with this node.
     */
    PartialOrderNode(Object nodeData, String name) {
        this.nodeData = nodeData;
        this.name = name;
    }

    /** Returns the <code>Object</code> represented by this node. */
    Object getData() {
        return nodeData;
    }

    /** Returns the name of the <code>Object</code> represented by this node. */
    String getName() {
        return name;
    }

    /** Returns the in-degree of this node. */
    int getInDegree() {
        return inDegree;
    }

    /** Returns the copy in-degree of this node. */
    int getCopyInDegree() {
        return copyInDegree;
    }

    /** Sets the copy in-degree of this node. */
    void setCopyInDegree(int copyInDegree) {
        this.copyInDegree = copyInDegree;
    }

    /** Returns the next zero in-degree node in the linked list. */
    PartialOrderNode getZeroLink() {
        return zeroLink;
    }

    /** Sets the next zero in-degree node in the linked list. */
    void setZeroLink(PartialOrderNode poNode) {
        zeroLink = poNode;
    }

    /** Returns the neighbors of this node as an <code>Enumeration</code>. */
    Enumeration getNeighbors() {
        return neighbors.elements();
    }

    /**
     * Adds a directed edge to the graph. The neighbors list of this node is updated and the in-degree of the other node
     * is incremented.
     */
    void addEdge(PartialOrderNode poNode) {
        neighbors.addElement(poNode);
        poNode.incrementInDegree();
    }

    /**
     * Removes a directed edge from the graph. The neighbors list of this node is updated and the in-degree of the other
     * node is decremented.
     */
    void removeEdge(PartialOrderNode poNode) {
        neighbors.removeElement(poNode);
        poNode.decrementInDegree();
    }

    /** Increments the in-degree of a node. */
    void incrementInDegree() {
        ++inDegree;
    }

    /** Increments the copy-in-degree of a node. */
    void incrementCopyInDegree() {
        ++copyInDegree;
    }

    /** Decrements the in-degree of a node. */
    void decrementInDegree() {
        --inDegree;
    }

    /** Decrements the copy in-degree of a node. */
    void decrementCopyInDegree() {
        --copyInDegree;
    }
}
