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

/** Allows the operations registered by {@code imagen-legacy-core} in {@code META-INF/registryFile.imagen}. */
public class LegacyCoreRegistryAllowListProvider implements RegistryAllowListProvider {

    private static final Set<String> CLASSES = Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList(
            "org.eclipse.imagen.NullCRIF",
            "org.eclipse.imagen.media.opimage.AbsoluteCRIF",
            "org.eclipse.imagen.media.opimage.AddCRIF",
            "org.eclipse.imagen.media.opimage.AddConstCRIF",
            "org.eclipse.imagen.media.opimage.AddConstToCollectionCIF",
            "org.eclipse.imagen.media.opimage.AffineCRIF",
            "org.eclipse.imagen.media.opimage.AndCRIF",
            "org.eclipse.imagen.media.opimage.AndConstCRIF",
            "org.eclipse.imagen.media.opimage.BandCombineCRIF",
            "org.eclipse.imagen.media.opimage.BandMergeCRIF",
            "org.eclipse.imagen.media.opimage.BandSelectCRIF",
            "org.eclipse.imagen.media.opimage.BinarizeCRIF",
            "org.eclipse.imagen.media.opimage.BorderRIF",
            "org.eclipse.imagen.media.opimage.ClampCRIF",
            "org.eclipse.imagen.media.opimage.ColorConvertCRIF",
            "org.eclipse.imagen.media.opimage.ConvolveRIF",
            "org.eclipse.imagen.media.opimage.CropCRIF",
            "org.eclipse.imagen.media.opimage.DivideByConstCRIF",
            "org.eclipse.imagen.media.opimage.DivideCRIF",
            "org.eclipse.imagen.media.opimage.DivideIntoConstCRIF",
            "org.eclipse.imagen.media.opimage.ErrorDiffusionRIF",
            "org.eclipse.imagen.media.opimage.ExpCRIF",
            "org.eclipse.imagen.media.opimage.ExtremaRIF",
            "org.eclipse.imagen.media.opimage.HistogramRIF",
            "org.eclipse.imagen.media.opimage.ImageFunctionRIF",
            "org.eclipse.imagen.media.opimage.InvertCRIF",
            "org.eclipse.imagen.media.opimage.LogCRIF",
            "org.eclipse.imagen.media.opimage.LookupCRIF",
            "org.eclipse.imagen.media.opimage.MaxCRIF",
            "org.eclipse.imagen.media.opimage.MeanRIF",
            "org.eclipse.imagen.media.opimage.MinCRIF",
            "org.eclipse.imagen.media.opimage.MosaicRIF",
            "org.eclipse.imagen.media.opimage.MultiplyCRIF",
            "org.eclipse.imagen.media.opimage.MultiplyConstCRIF",
            "org.eclipse.imagen.media.opimage.NotCRIF",
            "org.eclipse.imagen.media.opimage.OrCRIF",
            "org.eclipse.imagen.media.opimage.OrConstCRIF",
            "org.eclipse.imagen.media.opimage.RescaleCRIF",
            "org.eclipse.imagen.media.opimage.RotateCRIF",
            "org.eclipse.imagen.media.opimage.ScaleCRIF",
            "org.eclipse.imagen.media.opimage.ShearRIF",
            "org.eclipse.imagen.media.opimage.SubsampleAverageCRIF",
            "org.eclipse.imagen.media.opimage.SubtractCRIF",
            "org.eclipse.imagen.media.opimage.SubtractConstCRIF",
            "org.eclipse.imagen.media.opimage.SubtractFromConstCRIF",
            "org.eclipse.imagen.media.opimage.ThresholdCRIF",
            "org.eclipse.imagen.media.opimage.TranslateCRIF",
            "org.eclipse.imagen.media.opimage.TransposeCRIF",
            "org.eclipse.imagen.media.opimage.WarpRIF",
            "org.eclipse.imagen.media.opimage.XorCRIF",
            "org.eclipse.imagen.media.opimage.XorConstCRIF",
            "org.eclipse.imagen.operator.AbsoluteDescriptor",
            "org.eclipse.imagen.operator.AddConstDescriptor",
            "org.eclipse.imagen.operator.AddDescriptor",
            "org.eclipse.imagen.operator.AffineDescriptor",
            "org.eclipse.imagen.operator.AndConstDescriptor",
            "org.eclipse.imagen.operator.AndDescriptor",
            "org.eclipse.imagen.operator.BandCombineDescriptor",
            "org.eclipse.imagen.operator.BandMergeDescriptor",
            "org.eclipse.imagen.operator.BandSelectDescriptor",
            "org.eclipse.imagen.operator.BinarizeDescriptor",
            "org.eclipse.imagen.operator.BorderDescriptor",
            "org.eclipse.imagen.operator.ClampDescriptor",
            "org.eclipse.imagen.operator.ColorConvertDescriptor",
            "org.eclipse.imagen.operator.ConvolveDescriptor",
            "org.eclipse.imagen.operator.CropDescriptor",
            "org.eclipse.imagen.operator.DivideByConstDescriptor",
            "org.eclipse.imagen.operator.DivideDescriptor",
            "org.eclipse.imagen.operator.DivideIntoConstDescriptor",
            "org.eclipse.imagen.operator.ErrorDiffusionDescriptor",
            "org.eclipse.imagen.operator.ExpDescriptor",
            "org.eclipse.imagen.operator.ExtremaDescriptor",
            "org.eclipse.imagen.operator.FormatDescriptor",
            "org.eclipse.imagen.operator.HistogramDescriptor",
            "org.eclipse.imagen.operator.ImageFunctionDescriptor",
            "org.eclipse.imagen.operator.InvertDescriptor",
            "org.eclipse.imagen.operator.LogDescriptor",
            "org.eclipse.imagen.operator.MaxDescriptor",
            "org.eclipse.imagen.operator.MeanDescriptor",
            "org.eclipse.imagen.operator.MinDescriptor",
            "org.eclipse.imagen.operator.MosaicDescriptor",
            "org.eclipse.imagen.operator.MultiplyConstDescriptor",
            "org.eclipse.imagen.operator.MultiplyDescriptor",
            "org.eclipse.imagen.operator.NotDescriptor",
            "org.eclipse.imagen.operator.NullDescriptor",
            "org.eclipse.imagen.operator.OrConstDescriptor",
            "org.eclipse.imagen.operator.OrDescriptor",
            "org.eclipse.imagen.operator.OrderedDitherDescriptor",
            "org.eclipse.imagen.operator.RescaleDescriptor",
            "org.eclipse.imagen.operator.RotateDescriptor",
            "org.eclipse.imagen.operator.ScaleDescriptor",
            "org.eclipse.imagen.operator.ShearDescriptor",
            "org.eclipse.imagen.operator.SubtractConstDescriptor",
            "org.eclipse.imagen.operator.SubtractDescriptor",
            "org.eclipse.imagen.operator.SubtractFromConstDescriptor",
            "org.eclipse.imagen.operator.ThresholdDescriptor",
            "org.eclipse.imagen.operator.TranslateDescriptor",
            "org.eclipse.imagen.operator.TransposeDescriptor",
            "org.eclipse.imagen.operator.WarpDescriptor",
            "org.eclipse.imagen.operator.XorConstDescriptor",
            "org.eclipse.imagen.operator.XorDescriptor")));

    @Override
    public Set<String> getAllowedRegistryClasses() {
        return CLASSES;
    }
}
