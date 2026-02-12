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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * A class to represent a ROI (region of interest) for an overview image.
 *
 * <p>This ROI is simplified in that it only allows translation. A more useful version might include rotation and scale
 * changes.
 */
public class SimpleOverviewROI {

    private static final int THRESHOLD = 10;
    private static final int PLUS_RADIUS = 5;

    private Point2D position;
    private Dimension2D size;
    private boolean pickCenter = false;

    public SimpleOverviewROI() {
        this(new DimensionFloat(10F, 10F));
    }

    public SimpleOverviewROI(Dimension2D size) {
        this(new Point(), size);
    }

    public SimpleOverviewROI(Point2D position, Dimension2D size) {
        this.position = new Point2D.Float((float) position.getX(), (float) position.getY());
        this.size = new DimensionFloat(size.getWidth(), size.getHeight());
    }

    public Point2D getPosition() {
        return position;
    }

    public Dimension2D getSize() {
        return size;
    }

    public void setPosition(Point2D position) {
        this.position = new Point2D.Float((float) position.getX(), (float) position.getY());
    }

    public void setSize(Dimension2D size) {
        this.size = new DimensionFloat(size.getWidth(), size.getHeight());
    }

    public void draw(Graphics g) {
        float halfW = (float) size.getWidth() / 2.0f;
        float halfH = (float) size.getHeight() / 2.0f;

        Point2D p1 = transform(new Point2D.Float(-halfW, -halfH));
        Point2D p2 = transform(new Point2D.Float(halfW, -halfH));
        Point2D p3 = transform(new Point2D.Float(halfW, halfH));
        Point2D p4 = transform(new Point2D.Float(-halfW, halfH));

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Stroke stroke = g2d.getStroke();
        g2d.setStroke(new BasicStroke(2));

        // Draw ROI rectangle
        g2d.setPaint(Color.red);
        g2d.draw(new Line2D.Float(p1, p2));
        g2d.draw(new Line2D.Float(p2, p3));
        g2d.draw(new Line2D.Float(p3, p4));
        g2d.draw(new Line2D.Float(p4, p1));

        // Draw a + at the rectangle center
        p1 = transform(new Point2D.Float(-PLUS_RADIUS, 0));
        p2 = transform(new Point2D.Float(PLUS_RADIUS, 0));
        p3 = transform(new Point2D.Float(0, -PLUS_RADIUS));
        p4 = transform(new Point2D.Float(0, PLUS_RADIUS));
        g2d.draw(new Line2D.Float(p1, p2));
        g2d.draw(new Line2D.Float(p3, p4));
    }

    public void getDragPolicy(Point2D p) {
        pickCenter = false;
        p = new Point2D.Float((float) p.getX(), (float) p.getY());
        p = inverseTransform(p);
        if (Math.abs(p.getX()) < THRESHOLD && Math.abs(p.getY()) < THRESHOLD) pickCenter = true;
        return;
    }

    public void adjust(Point2D p) {
        p = new Point2D.Float((float) p.getX(), (float) p.getY());

        if (pickCenter) {
            position = p;
        }
        return;
    }

    private Point2D transform(Point2D p) {
        AffineTransform transform = new AffineTransform();
        p = transform.transform(p, null);
        transform = new AffineTransform();
        transform.translate(position.getX(), position.getY());
        return transform.transform(p, null);
    }

    private Point2D inverseTransform(Point2D p) {
        AffineTransform transform = new AffineTransform();
        transform.translate(-position.getX(), -position.getY());
        p = transform.transform(p, null);
        transform = new AffineTransform();
        return transform.transform(p, null);
    }
}
