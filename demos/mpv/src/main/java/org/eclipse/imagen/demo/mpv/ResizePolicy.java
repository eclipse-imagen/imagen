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

import java.awt.Point;

/**
 * This class defines the resizing policy of an image display panel. The supported policies include maintaining a fixed
 * image reference point at the display center or a display corner (upper left, upper right, lower left, or lower right)
 * whenever an image display panel is resized. For example, if the resize policy is set to maintain the upper right
 * corner, then the displayed upper right image pixel would not change following a panel resize operation.
 *
 * <p>This class is used to support operations that include wiping one image over another in two or four displays.
 *
 * @see MPVDemo
 */
public class ResizePolicy {

    /** Resize policy to keep window center at the center */
    public static int POLICY_CENTER = 0;

    /** Resize policy to keep window upper left at the upper left */
    public static int POLICY_LEFTUP = 1;

    /** Resize policy to keep window upper right at the upper right */
    public static int POLICY_RIGHTUP = 2;

    /** Resize policy to keep window lower left at the lower left */
    public static int POLICY_LEFTBOTTOM = 3;

    /** Resize policy to keep window lower right at the lower right */
    public static int POLICY_RIGHTBOTTOM = 4;

    private int policy;

    /**
     * Constructor.
     *
     * @param policy one of <code>POLICY_CENTER</code>, <code>POLICY_LEFTUP</code>, <code>POLICY_RIGHTUP</code>, <code>
     *     POLICY_LEFTBOTTOM</code>, <code>POLICY_RIGHTBOTTOM</code>
     * @throws IllegalArgumentException if <code>policy</code> invalid
     */
    public ResizePolicy(int policy) {
        if (policy > 4) throw new IllegalArgumentException("Invalid policy");

        this.policy = policy;
    } // constructor

    /** returns current <code>policy</code> in effect. */
    public int getPolicy() {
        return policy;
    } // getPolicy

    /** Computes the upper left coordinates of a window based on the input
     *  reference point and current active <code>policy</code>.  The input
     *  <code>referenceX/Y</code> coordinates are the location of the
     *  fixed point based on the current active <code>policy</code>.
     *
     *  Example: Active policy is <code>POLICY_CENTER<code> and coordinates as defined below
     *
     *        A----------------   A = R - (w/2, h/2)
     *        |               |     = returned value
     *        |               |   R = A + (w/2, h/2)
     *        |               |     = reference point
     *        |       R       |
     *        |               |
     *        <height = h>    |
     *        |               |
     *        ---<width = w>--R
     *
     * @param referenceX the fixed point X coordinate (doesn't move relative
     *        to a window during a resize).
     * @param referenceY the fixed point Y coordinate (doesn't move
     *        relative to a window during a resize).
     * @param w the window width
     * @param h the window height
     */
    public Point computeOrigin(int referenceX, int referenceY, int w, int h) {
        if (policy == POLICY_CENTER) return new Point(referenceX - w / 2, referenceY - h / 2);
        else if (policy == POLICY_LEFTUP) return new Point(referenceX, referenceY);
        else if (policy == POLICY_RIGHTUP) return new Point(referenceX - w, referenceY);
        else if (policy == POLICY_LEFTBOTTOM) return new Point(referenceX, referenceY - h);
        else if (policy == POLICY_RIGHTBOTTOM) return new Point(referenceX - w, referenceY - h);
        return new Point(referenceX, referenceY);
    } // computeOrigin

    /** Computes the fixed point coordinates of a window based on the input
     *  upper left window and window dimensions. The output
     *  <code>Point</code> coordinates are the location of the
     *  fixed point based on the current active <code>policy</code>.
     *
     *  Example: Active policy is <code>POLICY_RIGHTBOTTOM<code> and coordinates as defined below
     *
     *        A----------------   A = (originX, originY)
     *        |               |
     *        |               |   R = A + (w, h)
     *        <height = h>    |     = returned value
     *        |               |
     *        |               |
     *        |               |
     *        ---<width = w>--R
     *
     * @param originX the window upper left X coordinate
     * @param originY the window upper left Y coordinate
     * @param w the window width
     * @param h the window height
     */
    public Point computeReferenceFromOrigin(int originX, int originY, int w, int h) {
        if (policy == POLICY_CENTER) return new Point(originX + w / 2, originY + h / 2);
        else if (policy == POLICY_LEFTUP) return new Point(originX, originY);
        else if (policy == POLICY_RIGHTUP) return new Point(originX + w, originY);
        else if (policy == POLICY_LEFTBOTTOM) return new Point(originX, originY + h);
        else if (policy == POLICY_RIGHTBOTTOM) return new Point(originX + w, originY + h);
        return new Point(originX, originY);
    } // computeReferenceFromOrigin

    /** Computes the fixed reference point coordinates of a window based on the input
     *  image dimensions, window dimensions, and current active <code>policy</code>.
     *  The output <code>Point</code> coordinates are the location of the
     *  fixed point based on the current active <code>policy</code>.
     *
     *  Example: Active policy is <code>POLICY_RIGHTUP<code> and coordinates as defined below
     *
     *        0,0--------------   Upper left of base image has coordinates 0,0
     *        |               |
     *        |    ------R    |
     *        |    |     |    |   C = (imageWidth/2, imageHeight/2)
     *        |    |     |    |     = IMAGE and DISPLAY center
     *        |    |     |    |
     *        |    |  C  |    |   R = reference point in image frame
     *        |    |     |    |     = C + (w/2, -h/2)
     *        |    |     |    |     = returned value
     *        |    |     |    |
     *        |    DISPLAY    |
     *        |               |
     *        IMAGE-----------|
     *
     * @param imageWidth the overall image width
     * @param imageHeight the overall image height
     * @param w the width of a window covering a portion of the image
     * @param h the height of a window covering a portion of the image
     */
    public Point computeReference(int imageWidth, int imageHeight, int w, int h) {

        /* Compute the POLICY_CENTER case and translate it if needed */

        Point reference = new Point(imageWidth / 2, imageHeight / 2);

        if (policy == POLICY_LEFTUP) reference.translate(-w / 2, -h / 2);
        else if (policy == POLICY_RIGHTUP) reference.translate(w / 2, -h / 2);
        else if (policy == POLICY_LEFTBOTTOM) reference.translate(-w / 2, h / 2);
        else if (policy == POLICY_RIGHTBOTTOM) reference.translate(w / 2, h / 2);

        return reference;
    } // computeReference

    /**
     * Compute the shift to the current reference point under the following scenario:
     *
     * <p>1) The current window has upper left coordinates 0,0 and dimensions width by height 2) A second newly defined
     * window has upper left coordinates minX, minY and lower right coordinates maxX, maxY 3) The current active policy
     * is respected
     *
     * <p>Example: Active policy is POLICY_LEFTBOTTOM and coordinates as defined below
     *
     * <p>0,0-----CURRENT--- Upper left of current window has coordinates 0,0 | | A = (minX, minY) | A------- | B =
     * (maxX, maxY) | | | | C = (width, height) | | | | | | | | R = reference point in current display window frame | |
     * | | R' = new reference point in current display window frame | | | | = (minX, maxY) | | | | dR = R' - R |
     * R'-----B | = (minX, maxY - height) | | = returned value | | R----------------C
     *
     * @param minX the upper left x coordinate of new display window
     * @param minY the upper left y coordinate of new display window
     * @param maxX the lower right x coordinate of new display window
     * @param maxY the lower right y coordinate of new display window
     * @param width original window width
     * @param height original window height
     */
    public Point shiftReference(int minX, int minY, int maxX, int maxY, int width, int height) {

        if (policy == POLICY_LEFTUP) return new Point(minX, minY);

        if (policy == POLICY_RIGHTUP) return new Point(maxX - width, minY);

        if (policy == POLICY_LEFTBOTTOM) return new Point(minX, maxY - height);

        if (policy == POLICY_RIGHTBOTTOM) return new Point(maxX - width, maxY - height);

        return new Point((width - minX - maxX) / 2, (height - minY - maxY) / 2);
    } // shiftReference
} // ResizePolicy
