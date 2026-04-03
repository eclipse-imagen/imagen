/*
 * Copyright (c) 2026 Fernando Mino and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution and is available at
 * http://www.opensource.org/licenses/apache2.0.php.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.eclipse.imagen;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.imagen.spi.RegistryAllowListProvider;
import org.eclipse.imagen.util.ExactClassAllowList;
import org.eclipse.imagen.util.ExactClassAllowList.Snapshot;

/**
 * Loads the registry-class allow-list used by {@link RegistryFileParser} before reflective instantiation.
 *
 * <p>Built-in defaults are derived from the ImageN registry files shipped by the repository modules. Runtime
 * configuration extends, but never replaces, those defaults. Additional trusted classes may be contributed by
 * downstream libraries through {@link RegistryAllowListProvider}. The effective allow-list is resolved once per JVM and
 * cached as an immutable snapshot, so runtime configuration changes are not observed after the first access.
 */
final class AllowedRegistryClasses {

    static final String PROPERTY_NAME = "org.eclipse.imagen.allowedRegistryClasses";
    static final String ENV_NAME = "ORG_ECLIPSE_IMAGEN_ALLOWED_REGISTRY_CLASSES";

    private static final Logger LOGGER = Logger.getLogger(AllowedRegistryClasses.class.getName());
    private static volatile Set<String> cachedAllowedClasses;

    private static final Set<String> DEFAULTS = Collections.unmodifiableSet(new LinkedHashSet<String>(Arrays.asList(
            "org.eclipse.imagen.media.affine.AffineDescriptor",
            "org.eclipse.imagen.media.affine.AffineCRIF",
            "org.eclipse.imagen.media.algebra.AlgebraDescriptor",
            "org.eclipse.imagen.media.algebra.AddDescriptor",
            "org.eclipse.imagen.media.algebra.AndDescriptor",
            "org.eclipse.imagen.media.algebra.SubtractDescriptor",
            "org.eclipse.imagen.media.algebra.XorDescriptor",
            "org.eclipse.imagen.media.algebra.constant.OperationConstDescriptor",
            "org.eclipse.imagen.media.algebra.AlgebraCRIF",
            "org.eclipse.imagen.media.algebra.AddCRIF",
            "org.eclipse.imagen.media.algebra.AndCRIF",
            "org.eclipse.imagen.media.algebra.SubtractCRIF",
            "org.eclipse.imagen.media.algebra.XorCRIF",
            "org.eclipse.imagen.media.algebra.constant.OperationConstCRIF",
            "org.eclipse.imagen.media.artifacts.ArtifactsFilterDescriptor",
            "org.eclipse.imagen.media.artifacts.ArtifactsFilterRIF",
            "org.eclipse.imagen.media.bandcombine.BandCombineDescriptor",
            "org.eclipse.imagen.media.bandcombine.BandCombineCRIF",
            "org.eclipse.imagen.media.bandmerge.BandMergeDescriptor",
            "org.eclipse.imagen.media.bandmerge.BandMergeCRIF",
            "org.eclipse.imagen.media.bandselect.BandSelectDescriptor",
            "org.eclipse.imagen.media.bandselect.BandSelectCRIF",
            "org.eclipse.imagen.media.binarize.BinarizeDescriptor",
            "org.eclipse.imagen.media.binarize.BinarizeCRIF",
            "org.eclipse.imagen.media.border.BorderDescriptor",
            "org.eclipse.imagen.media.border.BorderRIF",
            "org.eclipse.imagen.media.buffer.BufferDescriptor",
            "org.eclipse.imagen.media.buffer.BufferRIF",
            "org.eclipse.imagen.media.clamp.ClampDescriptor",
            "org.eclipse.imagen.media.clamp.ClampCRIF",
            "org.eclipse.imagen.media.classbreaks.ClassBreaksDescriptor",
            "org.eclipse.imagen.media.classbreaks.ClassBreaksRIF",
            "org.eclipse.imagen.media.classifier.RasterClassifierDescriptor",
            "org.eclipse.imagen.media.classifier.RasterClassifierCRIF",
            "org.eclipse.imagen.media.colorconvert.ColorConvertDescriptor",
            "org.eclipse.imagen.media.colorconvert.ColorConvertCRIF",
            "org.eclipse.imagen.media.colorindexer.ColorIndexerDescriptor",
            "org.eclipse.imagen.media.colorindexer.ColorIndexerCRIF",
            "org.eclipse.imagen.media.contour.ContourDescriptor",
            "org.eclipse.imagen.media.contour.ContourRIF",
            "org.eclipse.imagen.media.convolve.ConvolveDescriptor",
            "org.eclipse.imagen.media.convolve.UnsharpMaskDescriptor",
            "org.eclipse.imagen.media.convolve.ConvolveRIF",
            "org.eclipse.imagen.media.convolve.UnsharpMaskRIF",
            "org.eclipse.imagen.operator.ConstantDescriptor",
            "org.eclipse.imagen.operator.FilteredSubsampleDescriptor",
            "org.eclipse.imagen.operator.MaxFilterDescriptor",
            "org.eclipse.imagen.operator.MedianFilterDescriptor",
            "org.eclipse.imagen.operator.MinFilterDescriptor",
            "org.eclipse.imagen.operator.OverlayDescriptor",
            "org.eclipse.imagen.operator.PatternDescriptor",
            "org.eclipse.imagen.operator.RenderableDescriptor",
            "org.eclipse.imagen.operator.SubsampleAverageDescriptor",
            "org.eclipse.imagen.media.opimage.ConstantCRIF",
            "org.eclipse.imagen.media.opimage.FilteredSubsampleRIF",
            "org.eclipse.imagen.media.opimage.MaxFilterRIF",
            "org.eclipse.imagen.media.opimage.MedianFilterRIF",
            "org.eclipse.imagen.media.opimage.MinFilterRIF",
            "org.eclipse.imagen.media.opimage.OverlayCRIF",
            "org.eclipse.imagen.media.opimage.PatternRIF",
            "org.eclipse.imagen.media.opimage.SubsampleAverageCRIF",
            "org.eclipse.imagen.media.crop.CropDescriptor",
            "org.eclipse.imagen.media.crop.CropCRIF",
            "org.eclipse.imagen.media.errordiffusion.ErrorDiffusionDescriptor",
            "org.eclipse.imagen.media.errordiffusion.ErrorDiffusionRIF",
            "org.eclipse.imagen.media.format.FormatDescriptor",
            "org.eclipse.imagen.media.format.FormatCRIF",
            "org.eclipse.imagen.media.imagefunction.ImageFunctionDescriptor",
            "org.eclipse.imagen.media.imagefunction.ImageFunctionRIF",
            "org.eclipse.imagen.media.imageread.ImageReadDescriptor",
            "org.eclipse.imagen.media.imageread.ImageReadCRIF",
            "org.eclipse.imagen.media.jiffleop.JiffleDescriptor",
            "org.eclipse.imagen.media.jiffleop.JiffleRIF",
            "org.eclipse.imagen.media.lookup.LookupDescriptor",
            "org.eclipse.imagen.media.lookup.LookupCRIF",
            "org.eclipse.imagen.media.mosaic.MosaicDescriptor",
            "org.eclipse.imagen.media.mosaic.MosaicRIF",
            "org.eclipse.imagen.media.nullop.NullDescriptor",
            "org.eclipse.imagen.media.nullop.NullCRIF",
            "org.eclipse.imagen.media.orderdither.OrderedDitherDescriptor",
            "org.eclipse.imagen.media.orderdither.OrderedDitherRIF",
            "org.eclipse.imagen.media.piecewise.GenericPiecewiseDescriptor",
            "org.eclipse.imagen.media.piecewise.GenericPiecewiseCRIF",
            "org.eclipse.imagen.media.rescale.RescaleDescriptor",
            "org.eclipse.imagen.media.rescale.RescaleCRIF",
            "org.eclipse.imagen.media.rlookup.RangeLookupDescriptor",
            "org.eclipse.imagen.media.rlookup.RangeLookupRIF",
            "org.eclipse.imagen.media.scale.ScaleDescriptor",
            "org.eclipse.imagen.media.scale.ScaleCRIF",
            "org.eclipse.imagen.media.scale.Scale2Descriptor",
            "org.eclipse.imagen.media.scale.Scale2CRIF",
            "org.eclipse.imagen.media.shadedrelief.ShadedReliefDescriptor",
            "org.eclipse.imagen.media.shadedrelief.ShadedReliefRIF",
            "org.eclipse.imagen.media.stats.StatisticsDescriptor",
            "org.eclipse.imagen.media.stats.StatisticsRIF",
            "org.eclipse.imagen.media.threshold.ThresholdDescriptor",
            "org.eclipse.imagen.media.threshold.ThresholdCRIF",
            "org.eclipse.imagen.media.translate.TranslateDescriptor",
            "org.eclipse.imagen.media.translate.TranslateCRIF",
            "org.eclipse.imagen.media.vectorbin.VectorBinarizeDescriptor",
            "org.eclipse.imagen.media.vectorbin.VectorBinarizeRIF",
            "org.jaitools.media.jai.vectorize.VectorizeDescriptor",
            "org.jaitools.media.jai.vectorize.VectorizeRIF",
            "org.eclipse.imagen.media.warp.WarpDescriptor",
            "org.eclipse.imagen.media.warp.WarpRIF",
            "org.eclipse.imagen.media.zonal.ZonalStatsDescriptor",
            "org.eclipse.imagen.media.zonal.ZonalStatsRIF")));

    /** Utility class; do not instantiate. */
    private AllowedRegistryClasses() {}

    /**
     * Returns the cached immutable registry allow-list for the current JVM.
     *
     * <p>The allow-list is initialized lazily on first access and then reused for the lifetime of the JVM. This keeps
     * SPI discovery and property parsing off the registry parsing path.
     *
     * @return immutable set of exact class names allowed for registry-file instantiation
     */
    static Set<String> allowedClasses() {
        Set<String> allowedClasses = cachedAllowedClasses;
        if (allowedClasses == null) {
            synchronized (AllowedRegistryClasses.class) {
                allowedClasses = cachedAllowedClasses;
                if (allowedClasses == null) {
                    allowedClasses = load();
                    cachedAllowedClasses = allowedClasses;
                }
            }
        }
        return allowedClasses;
    }

    /**
     * Builds a fresh immutable registry allow-list from built-ins, SPI contributions, and startup configuration.
     *
     * <p>Production enforcement should call {@link #allowedClasses()} so the effective set is resolved once per JVM.
     * This method remains separate to support test setup and cache initialization.
     *
     * @return immutable set of exact class names allowed for registry-file instantiation
     */
    static Set<String> load() {
        Set<String> contributedDefaults = loadContributedDefaults();
        Snapshot snapshot = ExactClassAllowList.load(PROPERTY_NAME, ENV_NAME, DEFAULTS, contributedDefaults);
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(
                    Level.FINE,
                    "Loaded registry allow-list from {0} with {1} entries ({2} SPI-contributed)",
                    new Object[] {
                        snapshot.getSource(),
                        Integer.valueOf(snapshot.getAllowedClasses().size()),
                        Integer.valueOf(contributedDefaults.size())
                    });
        }
        return snapshot.getAllowedClasses();
    }

    /**
     * Returns the built-in ImageN defaults without SPI or runtime configuration applied.
     *
     * @return immutable built-in registry defaults
     */
    static Set<String> defaultClasses() {
        return DEFAULTS;
    }

    /**
     * Clears the cached allow-list snapshot.
     *
     * <p>This hook exists only for test isolation and must not be used as a runtime refresh mechanism.
     */
    static void clearCacheForTests() {
        cachedAllowedClasses = null;
    }

    private static Set<String> loadContributedDefaults() {
        LinkedHashSet<String> contributed = new LinkedHashSet<String>();
        ServiceLoader<RegistryAllowListProvider> loader = ServiceLoader.load(RegistryAllowListProvider.class);
        Iterator<RegistryAllowListProvider> iterator = loader.iterator();

        while (true) {
            RegistryAllowListProvider provider;
            try {
                if (!iterator.hasNext()) {
                    break;
                }
                provider = iterator.next();
            } catch (ServiceConfigurationError e) {
                LOGGER.log(Level.WARNING, "Failed to load registry allow-list contributor", e);
                continue;
            }

            try {
                int before = contributed.size();
                addContributedClasses(contributed, provider.getAllowedRegistryClasses());
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.log(Level.FINE, "Loaded {0} registry allow-list entries from contributor {1}", new Object[] {
                        Integer.valueOf(contributed.size() - before),
                        provider.getClass().getName()
                    });
                }
            } catch (RuntimeException e) {
                LOGGER.log(
                        Level.WARNING,
                        e,
                        () -> "Ignoring registry allow-list contributor "
                                + provider.getClass().getName());
            }
        }

        return Collections.unmodifiableSet(contributed);
    }

    private static void addContributedClasses(Set<String> target, Set<String> contributedClasses) {
        if (contributedClasses == null) {
            return;
        }

        for (String className : contributedClasses) {
            if (className == null) {
                continue;
            }
            String trimmed = className.trim();
            if (!trimmed.isEmpty()) {
                target.add(trimmed);
            }
        }
    }
}
