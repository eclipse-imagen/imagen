/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 *
 * This Example Content is intended to demonstrate usage of Eclipse technology. It is
 * provided to you under the terms and conditions of the Eclipse Distribution License
 * v1.0 which is available at http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */
package org.eclipse.imagen.demo.mpv;

import java.awt.geom.Dimension2D;

/** A class to contain a dimension with floating-point size. */
public class DimensionFloat extends Dimension2D {

    public float width;
    public float height;

    public DimensionFloat(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public DimensionFloat(double width, double height) {
        this.width = (float) width;
        this.height = (float) height;
    }

    public Object clone() {
        return new DimensionFloat(this.width, this.height);
    }

    public double getWidth() {
        return this.width;
    }

    public double getHeight() {
        return this.height;
    }

    public void setSize(Dimension2D size) {
        this.width = (float) size.getWidth();
        this.height = (float) size.getHeight();
    }

    public void setSize(double width, double height) {
        this.width = (float) width;
        this.height = (float) height;
    }

    public String toString() {
        return "(" + width + ", " + height + ")";
    }
}
