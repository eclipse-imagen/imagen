/* Copyright (c) 2018 Jody Garnett and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution and is available at
 * http://www.opensource.org/licenses/apache2.0.php.
 */
package org.eclipse.imagen.builder;

import java.awt.image.RenderedImage;

public class Affine extends OperationBuilder {
    public static final String NAME = "affine";
    private static final String SY = "sy";
    private static final String SX = "sx";
    private static final String TY = "ty";
    private static final String TX = "tx";
    private static final String SHY = "shy";
    private static final String SHX = "shx";
    private static final String VECY = "vecy";
    private static final String VECX = "vecx";

    public Affine() {
        super(NAME);
    }

    public Affine(RenderedImage source) {
        super(NAME);
        parameter("source0", source);
    }

    Affine scale(double sx, double sy) {
        parameter(SX, sx);
        parameter(SY, sy);
        return this;
    }

    Affine rotate(double vecx, double vecy) {
        parameter(VECX, vecx);
        parameter(VECY, vecy);
        return this;
    }

    Affine shear(double shx, double shy) {
        parameter(SHX, shx);
        parameter(SHY, shy);
        return this;
    }

    Affine translate(double tx, double ty) {
        parameter(TX, tx);
        parameter(TY, ty);
        return this;
    }
}
