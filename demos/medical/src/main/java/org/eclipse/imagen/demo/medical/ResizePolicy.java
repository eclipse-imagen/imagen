/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 *
 * This Example Content is intended to demonstrate usage of Eclipse technology. It is
 * provided to you under the terms and conditions of the Eclipse Distribution License
 * v1.0 which is available at http://www.eclipse.org/org/documents/edl-v10.php.
 */
package org.eclipse.imagen.demo.medical;

import java.awt.Point;

/**
 * The class to define the resizing policy of an <code>ImagePane</code>. When the size of an <code>ImagePane</code> is
 * changed, the position of the image needs to be adjusted adequately. A policy is defined to indicate how to allign the
 * image region displayed in the <code>ImagePane</code> with the pane: for example, keep a fixed pixel at the center of
 * the <code>ImagePane</code>. The supported policies are: center-fixed, leftup-corner fixed, rightup-corner fixed,
 * leftbottom-fixed and rightbottom-fixed.
 */
public class ResizePolicy {

    /** The defined resizing policies */
    public static int POLICY_CENTER = 0;

    public static int POLICY_LEFTUP = 1;
    public static int POLICY_RIGHTUP = 2;
    public static int POLICY_LEFTBOTTOM = 3;
    public static int POLICY_RIGHTBOTTOM = 4;

    /** Cache the policy serial number */
    private int policy;

    /** The constructor. */
    public ResizePolicy(int policy) {
        if (policy > 4) throw new IllegalArgumentException("Invalid policy");

        this.policy = policy;
    }

    /** Return the policy. */
    public int getPolicy() {
        return policy;
    }

    /** Compute the origin from the reference point and the size of the image display panel. */
    public Point computeOrigin(int referenceX, int referenceY, int w, int h) {
        if (policy == POLICY_CENTER) return new Point(referenceX - w / 2, referenceY - h / 2);
        else if (policy == POLICY_LEFTUP) return new Point(referenceX, referenceY);
        else if (policy == POLICY_RIGHTUP) return new Point(referenceX - w, referenceY);
        else if (policy == POLICY_LEFTBOTTOM) return new Point(referenceX, referenceY - h);
        else if (policy == POLICY_RIGHTBOTTOM) return new Point(referenceX - w, referenceY - h);
        return new Point(referenceX, referenceY);
    }

    /** Compute the reference based on the image size and the image display panel size. */
    public Point computeReference(int imageWidth, int imageHeight, int w, int h) {
        Point reference = new Point(imageWidth / 2, imageHeight / 2);

        if (policy == POLICY_LEFTUP) reference.translate(-w / 2, -h / 2);
        else if (policy == POLICY_RIGHTUP) reference.translate(w / 2, -h / 2);
        else if (policy == POLICY_LEFTBOTTOM) reference.translate(-w / 2, h / 2);
        else if (policy == POLICY_RIGHTBOTTOM) reference.translate(w / 2, h / 2);

        return reference;
    }

    /** Return the shift based on the delta values. */
    public Point shiftReference(int dx, int dy) {
        if (policy == POLICY_LEFTUP) return new Point(dx, dy);
        if (policy == POLICY_RIGHTUP) return new Point(-dx, dy);
        if (policy == POLICY_LEFTBOTTOM) return new Point(dx, -dy);
        if (policy == POLICY_RIGHTBOTTOM) return new Point(-dx, -dy);
        return new Point(dx / 2, dy / 2);
    }

    /** Move the reference. */
    public Point shiftReference(int minx, int miny, int maxx, int maxy, int width, int height) {

        if (policy == POLICY_LEFTUP) return new Point(minx, miny);

        if (policy == POLICY_RIGHTUP) return new Point(maxx - width, miny);

        if (policy == POLICY_LEFTBOTTOM) return new Point(minx, maxy - height);

        if (policy == POLICY_RIGHTBOTTOM) return new Point(maxx - width, maxy - height);

        return new Point((width - minx - maxx) / 2, (height - miny - maxy) / 2);
    }
}
