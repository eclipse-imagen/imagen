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

/** Allows the operations registered by {@code imagen-legacy-codec-core} in {@code META-INF/registryFile.imagen}. */
public class LegacyCodecRegistryAllowListProvider implements RegistryAllowListProvider {

    private static final Set<String> CLASSES = Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList(
            "org.eclipse.imagen.media.opimage.BMPRIF",
            "org.eclipse.imagen.media.opimage.EncodeRIF",
            "org.eclipse.imagen.media.opimage.FPXRIF",
            "org.eclipse.imagen.media.opimage.FileLoadRIF",
            "org.eclipse.imagen.media.opimage.FileStoreRIF",
            "org.eclipse.imagen.media.opimage.GIFRIF",
            "org.eclipse.imagen.media.opimage.JPEGRIF",
            "org.eclipse.imagen.media.opimage.PNGRIF",
            "org.eclipse.imagen.media.opimage.PNMRIF",
            "org.eclipse.imagen.media.opimage.StreamRIF",
            "org.eclipse.imagen.media.opimage.TIFFRIF",
            "org.eclipse.imagen.media.opimage.URLRIF",
            "org.eclipse.imagen.media.tilecodec.GZIPTileDecoderFactory",
            "org.eclipse.imagen.media.tilecodec.GZIPTileEncoderFactory",
            "org.eclipse.imagen.media.tilecodec.RawTileDecoderFactory",
            "org.eclipse.imagen.media.tilecodec.RawTileEncoderFactory",
            "org.eclipse.imagen.operator.BMPDescriptor",
            "org.eclipse.imagen.operator.EncodeDescriptor",
            "org.eclipse.imagen.operator.FPXDescriptor",
            "org.eclipse.imagen.operator.FileLoadDescriptor",
            "org.eclipse.imagen.operator.FileStoreDescriptor",
            "org.eclipse.imagen.operator.GIFDescriptor",
            "org.eclipse.imagen.operator.JPEGDescriptor",
            "org.eclipse.imagen.operator.PNGDescriptor",
            "org.eclipse.imagen.operator.PNMDescriptor",
            "org.eclipse.imagen.operator.StreamDescriptor",
            "org.eclipse.imagen.operator.TIFFDescriptor",
            "org.eclipse.imagen.operator.URLDescriptor",
            "org.eclipse.imagen.registry.TileDecoderRegistryMode",
            "org.eclipse.imagen.registry.TileEncoderRegistryMode",
            "org.eclipse.imagen.tilecodec.GZIPTileCodecDescriptor",
            "org.eclipse.imagen.tilecodec.JPEGTileCodecDescriptor",
            "org.eclipse.imagen.tilecodec.RawTileCodecDescriptor")));

    @Override
    public Set<String> getAllowedRegistryClasses() {
        return CLASSES;
    }
}
