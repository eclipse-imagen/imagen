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

import java.awt.image.renderable.ContextualRenderedImageFactory;
import java.awt.image.renderable.RenderedImageFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.imagen.NullCRIF;
import org.eclipse.imagen.OperationDescriptor;
import org.eclipse.imagen.OperationRegistry;
import org.eclipse.imagen.OperationRegistrySpi;
import org.eclipse.imagen.media.opimage.AddCRIF;
import org.eclipse.imagen.media.opimage.AffineCRIF;
import org.eclipse.imagen.media.opimage.AndCRIF;
import org.eclipse.imagen.media.opimage.BandCombineCRIF;
import org.eclipse.imagen.media.opimage.BandMergeCRIF;
import org.eclipse.imagen.media.opimage.BandSelectCRIF;
import org.eclipse.imagen.media.opimage.BinarizeCRIF;
import org.eclipse.imagen.media.opimage.BorderRIF;
import org.eclipse.imagen.media.opimage.ClampCRIF;
import org.eclipse.imagen.media.opimage.ColorConvertCRIF;
import org.eclipse.imagen.media.opimage.ConvolveRIF;
import org.eclipse.imagen.media.opimage.CropCRIF;
import org.eclipse.imagen.media.opimage.ErrorDiffusionRIF;
import org.eclipse.imagen.media.opimage.ImageFunctionRIF;
import org.eclipse.imagen.media.opimage.MosaicRIF;
import org.eclipse.imagen.media.opimage.RescaleCRIF;
import org.eclipse.imagen.media.opimage.ScaleCRIF;
import org.eclipse.imagen.media.opimage.SubtractCRIF;
import org.eclipse.imagen.media.opimage.ThresholdCRIF;
import org.eclipse.imagen.media.opimage.TranslateCRIF;
import org.eclipse.imagen.media.opimage.WarpRIF;
import org.eclipse.imagen.media.opimage.XorCRIF;
import org.eclipse.imagen.operator.AddDescriptor;
import org.eclipse.imagen.operator.AffineDescriptor;
import org.eclipse.imagen.operator.AndDescriptor;
import org.eclipse.imagen.operator.BandCombineDescriptor;
import org.eclipse.imagen.operator.BandMergeDescriptor;
import org.eclipse.imagen.operator.BandSelectDescriptor;
import org.eclipse.imagen.operator.BinarizeDescriptor;
import org.eclipse.imagen.operator.BorderDescriptor;
import org.eclipse.imagen.operator.ClampDescriptor;
import org.eclipse.imagen.operator.ColorConvertDescriptor;
import org.eclipse.imagen.operator.ConvolveDescriptor;
import org.eclipse.imagen.operator.CropDescriptor;
import org.eclipse.imagen.operator.ErrorDiffusionDescriptor;
import org.eclipse.imagen.operator.FormatDescriptor;
import org.eclipse.imagen.operator.ImageFunctionDescriptor;
import org.eclipse.imagen.operator.MosaicDescriptor;
import org.eclipse.imagen.operator.NullDescriptor;
import org.eclipse.imagen.operator.OrderedDitherDescriptor;
import org.eclipse.imagen.operator.RescaleDescriptor;
import org.eclipse.imagen.operator.ScaleDescriptor;
import org.eclipse.imagen.operator.SubtractDescriptor;
import org.eclipse.imagen.operator.ThresholdDescriptor;
import org.eclipse.imagen.operator.TranslateDescriptor;
import org.eclipse.imagen.operator.WarpDescriptor;
import org.eclipse.imagen.operator.XorDescriptor;
import org.eclipse.imagen.registry.RenderableRegistryMode;
import org.eclipse.imagen.registry.RenderedRegistryMode;

/**
 * Registers the {@code imagen-legacy-core} operations that share a name with an {@code imagen-*} module operation.
 *
 * <p>Each operation is registered only when no descriptor with that name is already present, so the module
 * implementations take precedence when both are on the classpath.
 */
public class LegacyCoreSpi implements OperationRegistrySpi {

    private static final Logger LOGGER = Logger.getLogger(LegacyCoreSpi.class.getName());

    private static final String PRODUCT = "org.eclipse.imagen.media";

    @Override
    public void updateRegistry(OperationRegistry registry) {
        register(registry, new AddDescriptor(), PRODUCT, new AddCRIF(), true);
        register(registry, new AffineDescriptor(), PRODUCT, new AffineCRIF(), true);
        register(registry, new AndDescriptor(), PRODUCT, new AndCRIF(), true);
        register(registry, new BandCombineDescriptor(), PRODUCT, new BandCombineCRIF(), true);
        register(registry, new BandMergeDescriptor(), PRODUCT, new BandMergeCRIF(), true);
        register(registry, new BandSelectDescriptor(), PRODUCT, new BandSelectCRIF(), true);
        register(registry, new BinarizeDescriptor(), PRODUCT, new BinarizeCRIF(), true);
        register(registry, new BorderDescriptor(), PRODUCT, new BorderRIF(), false);
        register(registry, new ClampDescriptor(), PRODUCT, new ClampCRIF(), true);
        register(registry, new ColorConvertDescriptor(), PRODUCT, new ColorConvertCRIF(), true);
        register(registry, new ConvolveDescriptor(), PRODUCT, new ConvolveRIF(), false);
        register(registry, new CropDescriptor(), PRODUCT, new CropCRIF(), true);
        register(registry, new ErrorDiffusionDescriptor(), PRODUCT, new ErrorDiffusionRIF(), false);
        register(registry, new FormatDescriptor(), PRODUCT, null, false);
        register(registry, new ImageFunctionDescriptor(), PRODUCT, new ImageFunctionRIF(), false);
        register(registry, new MosaicDescriptor(), PRODUCT, new MosaicRIF(), false);
        register(registry, new NullDescriptor(), "org.eclipse.imagen", new NullCRIF(), true);
        register(registry, new OrderedDitherDescriptor(), PRODUCT, null, false);
        register(registry, new RescaleDescriptor(), PRODUCT, new RescaleCRIF(), true);
        register(registry, new ScaleDescriptor(), PRODUCT, new ScaleCRIF(), true);
        register(registry, new SubtractDescriptor(), PRODUCT, new SubtractCRIF(), true);
        register(registry, new ThresholdDescriptor(), PRODUCT, new ThresholdCRIF(), true);
        register(registry, new TranslateDescriptor(), PRODUCT, new TranslateCRIF(), true);
        register(registry, new WarpDescriptor(), PRODUCT, new WarpRIF(), false);
        register(registry, new XorDescriptor(), PRODUCT, new XorCRIF(), true);
    }

    private static void register(
            OperationRegistry registry,
            OperationDescriptor descriptor,
            String product,
            RenderedImageFactory factory,
            boolean renderable) {
        String name = descriptor.getName();
        if (registry.getDescriptor(OperationDescriptor.class, name) != null) {
            LOGGER.log(Level.FINE, "Operation {0} already registered, skipping {1}", new Object[] {
                name, descriptor.getClass().getName()
            });
            return;
        }
        registry.registerDescriptor(descriptor);
        if (factory != null) {
            registry.registerFactory(RenderedRegistryMode.MODE_NAME, name, product, factory);
            if (renderable) {
                registry.registerFactory(
                        RenderableRegistryMode.MODE_NAME, name, product, (ContextualRenderedImageFactory) factory);
            }
        }
    }
}
