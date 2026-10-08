/*
 * Copyright (c) 2026 Eclipse ImageN contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution and is available at
 * http://www.opensource.org/licenses/apache2.0.php.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.eclipse.imagen.it.all;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.util.Map;
import org.eclipse.imagen.ImageN;
import org.eclipse.imagen.OperationDescriptor;
import org.eclipse.imagen.OperationRegistry;
import org.eclipse.imagen.RenderedOp;
import org.eclipse.imagen.media.jiffle.Jiffle;
import org.eclipse.imagen.media.jiffle.runtime.JiffleDirectRuntime;
import org.eclipse.imagen.media.jiffleop.JiffleDescriptor;

/** Uses imagen-all from a named module, printing each result for postbuild.bsh to check. */
public class AllCheck {

    public static void main(String[] args) throws Exception {
        check("module", AllCheck.class.getModule().getName(), "org.eclipse.imagen.it.all");
        check("imagen module", ImageN.class.getModule().getName(), "org.eclipse.imagen.all");

        OperationRegistry registry = ImageN.getDefaultInstance().getOperationRegistry();
        for (String name : new String[] {"Affine", "Scale", "Scale2", "Add", "Stats", "Jiffle", "Contour", "Vectorize", "ImageRead"}) {
            check("registered " + name, registry.getDescriptor(OperationDescriptor.class, name) != null, true);
        }

        BufferedImage source = new BufferedImage(64, 64, BufferedImage.TYPE_BYTE_GRAY);
        source.getRaster().setSample(3, 4, 0, 7);

        RenderedOp affine = ImageN.create(
                "Affine", new ParameterBlock().addSource(source).add(AffineTransform.getScaleInstance(2, 2)));
        check("Affine width", affine.getWidth(), 128);

        RenderedOp scale = ImageN.create("Scale", new ParameterBlock().addSource(source).add(0.5f).add(0.5f));
        check("Scale width", scale.getWidth(), 32);

        RenderedOp scale2 = ImageN.create("Scale2", new ParameterBlock().addSource(source).add(0.5d).add(0.5d));
        check("Scale2 width", scale2.getWidth(), 32);

        RenderedOp add = ImageN.create("Add", new ParameterBlock().addSource(source).addSource(source));
        check("Add sample", add.getData().getSample(3, 4, 0), 14);

        RenderedOp jiffle = JiffleDescriptor.create(
                new RenderedImage[] {source}, null, null, "dest = src * 2 + x();", null, null, null, null, null);
        check("Jiffle sample", jiffle.getData().getSampleDouble(3, 4, 0), 17.0);

        Jiffle script = new Jiffle("init { n = 0; } dest = n;", Map.of("dest", Jiffle.ImageRole.DEST));
        JiffleDirectRuntime runtime = script.getRuntimeInstance();
        runtime.setVar("n", 42.0);
        check("Jiffle var", runtime.getVar("n"), 42.0);
        BufferedImage dest = new BufferedImage(8, 8, BufferedImage.TYPE_BYTE_GRAY);
        runtime.setDestinationImage("dest", dest);
        runtime.evaluateAll(null);
        check("Jiffle runtime sample", dest.getRaster().getSample(3, 4, 0), 42);

        System.out.println("JPMS jpms-all: all checks passed");
    }

    private static void check(String name, Object actual, Object expected) {
        if (!expected.equals(actual)) {
            throw new IllegalStateException(name + ": expected " + expected + " but was " + actual);
        }
        System.out.println("JPMS jpms-all: " + name + " " + actual);
    }
}
