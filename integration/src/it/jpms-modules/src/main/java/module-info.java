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

/** Downstream application using the individual ImageN modules from the module path. */
module org.eclipse.imagen.it.modules {
    requires java.desktop;
    requires org.eclipse.imagen.core;
    requires org.eclipse.imagen.media.affine;
    requires org.eclipse.imagen.media.algebra;
    requires org.eclipse.imagen.media.artifacts;
    requires org.eclipse.imagen.media.bandcombine;
    requires org.eclipse.imagen.media.bandmerge;
    requires org.eclipse.imagen.media.bandselect;
    requires org.eclipse.imagen.media.binarize;
    requires org.eclipse.imagen.media.border;
    requires org.eclipse.imagen.media.buffer;
    requires org.eclipse.imagen.media.clamp;
    requires org.eclipse.imagen.media.classbreaks;
    requires org.eclipse.imagen.media.classifier;
    requires org.eclipse.imagen.media.colorconvert;
    requires org.eclipse.imagen.media.colorindexer;
    requires org.eclipse.imagen.media.cache;
    requires org.eclipse.imagen.media.contour;
    requires org.eclipse.imagen.media.convolve;
    requires org.eclipse.imagen.media.crop;
    requires org.eclipse.imagen.media.errordiffusion;
    requires org.eclipse.imagen.media.format;
    requires org.eclipse.imagen.media.imagefunction;
    requires org.eclipse.imagen.media.imageread;
    requires org.eclipse.imagen.media.iterators;
    requires org.eclipse.imagen.media.jiffle;
    requires org.eclipse.imagen.media.jiffleop;
    requires org.eclipse.imagen.media.lookup;
    requires org.eclipse.imagen.media.mosaic;
    requires org.eclipse.imagen.media.nullop;
    requires org.eclipse.imagen.media.orderdither;
    requires org.eclipse.imagen.media.piecewise;
    requires org.eclipse.imagen.media.viewer;
    requires org.eclipse.imagen.media.rescale;
    requires org.eclipse.imagen.media.rlookup;
    requires org.eclipse.imagen.media.scale;
    requires org.eclipse.imagen.media.scale2;
    requires org.eclipse.imagen.media.shadedrelief;
    requires org.eclipse.imagen.media.stats;
    requires org.eclipse.imagen.media.threshold;
    requires org.eclipse.imagen.media.translate;
    requires org.eclipse.imagen.media.utilities;
    requires org.eclipse.imagen.media.vectorbin;
    requires org.eclipse.imagen.media.vectorize;
    requires org.eclipse.imagen.media.warp;
    requires org.eclipse.imagen.media.zonal;
}
