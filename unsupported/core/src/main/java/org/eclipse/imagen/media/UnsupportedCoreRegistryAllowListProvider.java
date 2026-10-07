/* Copyright (c) 2026 Jody Garnett and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution and is available at
 * http://www.opensource.org/licenses/apache2.0.php.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.eclipse.imagen.media;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.eclipse.imagen.spi.RegistryAllowListProvider;

/** Allows the operations registered by {@code imagen-unsupported-core} in {@code META-INF/registryFile.imagen}. */
public class UnsupportedCoreRegistryAllowListProvider implements RegistryAllowListProvider {

    private static final Set<String> CLASSES = Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList(
            "org.eclipse.imagen.media.opimage.AWTImageRIF",
            "org.eclipse.imagen.media.opimage.AddCollectionCRIF",
            "org.eclipse.imagen.media.opimage.BoxFilterRIF",
            "org.eclipse.imagen.media.opimage.ColorQuantizerRIF",
            "org.eclipse.imagen.media.opimage.CompositeCRIF",
            "org.eclipse.imagen.media.opimage.ConjugateCRIF",
            "org.eclipse.imagen.media.opimage.DCTCRIF",
            "org.eclipse.imagen.media.opimage.DFTCRIF",
            "org.eclipse.imagen.media.opimage.DilateRIF",
            "org.eclipse.imagen.media.opimage.DivideComplexCRIF",
            "org.eclipse.imagen.media.opimage.ErodeRIF",
            "org.eclipse.imagen.media.opimage.GradientRIF",
            "org.eclipse.imagen.media.opimage.IDCTCRIF",
            "org.eclipse.imagen.media.opimage.IDFTCRIF",
            "org.eclipse.imagen.media.opimage.MagnitudeCRIF",
            "org.eclipse.imagen.media.opimage.MagnitudeSquaredCRIF",
            "org.eclipse.imagen.media.opimage.MatchCDFCRIF",
            "org.eclipse.imagen.media.opimage.MultiplyComplexCRIF",
            "org.eclipse.imagen.media.opimage.OverlayCRIF",
            "org.eclipse.imagen.media.opimage.PeriodicShiftCRIF",
            "org.eclipse.imagen.media.opimage.PhaseCRIF",
            "org.eclipse.imagen.media.opimage.PiecewiseCRIF",
            "org.eclipse.imagen.media.opimage.PolarToComplexCRIF",
            "org.eclipse.imagen.media.opimage.SubsampleBinaryToGrayCRIF",
            "org.eclipse.imagen.operator.AWTImageDescriptor",
            "org.eclipse.imagen.operator.AddCollectionDescriptor",
            "org.eclipse.imagen.operator.AddConstToCollectionDescriptor",
            "org.eclipse.imagen.operator.BoxFilterDescriptor",
            "org.eclipse.imagen.operator.ColorQuantizerDescriptor",
            "org.eclipse.imagen.operator.CompositeDescriptor",
            "org.eclipse.imagen.operator.ConjugateDescriptor",
            "org.eclipse.imagen.operator.DCTDescriptor",
            "org.eclipse.imagen.operator.DFTDescriptor",
            "org.eclipse.imagen.operator.DilateDescriptor",
            "org.eclipse.imagen.operator.DivideComplexDescriptor",
            "org.eclipse.imagen.operator.ErodeDescriptor",
            "org.eclipse.imagen.operator.GradientMagnitudeDescriptor",
            "org.eclipse.imagen.operator.IDCTDescriptor",
            "org.eclipse.imagen.operator.IDFTDescriptor",
            "org.eclipse.imagen.operator.IIPDescriptor",
            "org.eclipse.imagen.operator.IIPResolutionDescriptor",
            "org.eclipse.imagen.operator.MagnitudeDescriptor",
            "org.eclipse.imagen.operator.MagnitudeSquaredDescriptor",
            "org.eclipse.imagen.operator.MatchCDFDescriptor",
            "org.eclipse.imagen.operator.MultiplyComplexDescriptor",
            "org.eclipse.imagen.operator.PeriodicShiftDescriptor",
            "org.eclipse.imagen.operator.PhaseDescriptor",
            "org.eclipse.imagen.operator.PiecewiseDescriptor",
            "org.eclipse.imagen.operator.PolarToComplexDescriptor",
            "org.eclipse.imagen.operator.SubsampleBinaryToGrayDescriptor")));

    @Override
    public Set<String> getAllowedRegistryClasses() {
        return CLASSES;
    }
}
