/* Copyright (c) 2026 Jody Garnett and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution and is available at
 * http://www.opensource.org/licenses/apache2.0.php.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.eclipse.imagen.all;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.image.Raster;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.eclipse.imagen.ImageN;
import org.eclipse.imagen.OperationRegistry;
import org.eclipse.imagen.RenderedOp;
import org.eclipse.imagen.media.bandselect.BandSelectDescriptor;
import org.eclipse.imagen.operator.ConstantDescriptor;
import org.junit.BeforeClass;
import org.junit.Test;

/** Checks the shaded imagen-all jar, run by failsafe with the individual ImageN modules excluded from the classpath. */
public class ImageNAllIT {

    private static final String CORE_REGISTRY = "META-INF/org.eclipse.imagen.registryFile.imagen";

    private static final String OPERATION_REGISTRY = "META-INF/registryFile.imagen";

    private static String startupErrors;

    private static List<String> startupWarnings = new ArrayList<>();

    /** Capture warnings and System.err output while the default instance is created. */
    @BeforeClass
    public static void createDefaultInstance() {
        Logger logger = Logger.getLogger("org.eclipse.imagen");
        Handler handler = new Handler() {
            @Override
            public void publish(LogRecord record) {
                if (record.getLevel().intValue() >= Level.WARNING.intValue()) {
                    startupWarnings.add(record.getMessage());
                }
            }

            @Override
            public void flush() {}

            @Override
            public void close() {}
        };
        PrintStream err = System.err;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        logger.addHandler(handler);
        System.setErr(new PrintStream(buffer, true));
        try {
            ImageN.getDefaultInstance();
        } finally {
            System.setErr(err);
            logger.removeHandler(handler);
        }
        startupErrors = new String(buffer.toByteArray());
    }

    @Test
    public void testedAgainstShadedJar() throws IOException {
        URL location = ImageN.class.getProtectionDomain().getCodeSource().getLocation();
        String path = location.getPath();
        assertTrue("ImageN loaded from " + path, path.contains("imagen-all") && path.endsWith(".jar"));
        assertEquals(
                1,
                Collections.list(getClass().getClassLoader().getResources(CORE_REGISTRY))
                        .size());
        assertEquals(
                1,
                Collections.list(getClass().getClassLoader().getResources(OPERATION_REGISTRY))
                        .size());
    }

    @Test
    public void registryFilesHaveNoDuplicateEntries() throws IOException {
        assertNoDuplicates(CORE_REGISTRY);
        assertNoDuplicates(OPERATION_REGISTRY);
    }

    @Test
    public void startupIsSilent() {
        assertEquals("", startupErrors);
        assertEquals(Collections.emptyList(), startupWarnings);
    }

    @Test
    public void allRenderedOperationsRegistered() throws IOException {
        OperationRegistry registry = ImageN.getDefaultInstance().getOperationRegistry();
        List<String> missing = new ArrayList<>();
        for (String name : List.of(CORE_REGISTRY, OPERATION_REGISTRY)) {
            for (String entry : entries(name)) {
                String[] tokens = entry.split(" ");
                if ("rendered".equals(tokens[0]) && registry.getDescriptor("rendered", tokens[3]) == null) {
                    missing.add(tokens[3]);
                }
            }
        }
        assertEquals(Collections.emptyList(), missing);
    }

    @Test
    public void operationChain() {
        RenderedOp constant = ConstantDescriptor.create(4f, 4f, new Byte[] {1, 2, 3}, null);
        RenderedOp selected = BandSelectDescriptor.create(constant, new int[] {2}, null);

        Raster data = selected.getData();
        assertEquals(1, data.getNumBands());
        assertEquals(3, data.getSample(0, 0, 0));
    }

    private static void assertNoDuplicates(String name) throws IOException {
        Set<String> seen = new HashSet<>();
        List<String> duplicates = new ArrayList<>();
        for (String entry : entries(name)) {
            if (!seen.add(entry)) {
                duplicates.add(entry);
            }
        }
        assertEquals(name + " duplicates", Collections.emptyList(), duplicates);
    }

    /** Registry file entries with comments removed and whitespace normalized. */
    private static List<String> entries(String name) throws IOException {
        URL url = ImageNAllIT.class.getClassLoader().getResource(name);
        assertNotNull(name, url);
        List<String> entries = new ArrayList<>();
        try (BufferedReader reader =
                new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                int comment = line.indexOf('#');
                String entry = (comment >= 0 ? line.substring(0, comment) : line)
                        .trim()
                        .replaceAll("\\s+", " ");
                if (!entry.isEmpty()) {
                    entries.add(entry);
                }
            }
        }
        return entries;
    }
}
