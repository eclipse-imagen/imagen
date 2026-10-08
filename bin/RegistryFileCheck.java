/*
 * Copyright (c) 2025, Jody Garnett, and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
 
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Quick QA check of registryFile.imagen contents
 */
public class RegistryFileCheck {
    static int missing = 0;
    static Set<String> skip = Set.of(
        "ParameterListDescriptor.java",
        "OperationDescriptor.java",
        "RegistryElementDescriptor.java",
        "TileCodecDescriptor.java"
    );
    public static void main(String[] args) throws IOException {
        List<Path> registryFiles;
        try (Stream<Path> paths = Files.walk(Paths.get(".."))) {
            registryFiles = paths.filter(RegistryFileCheck::isRegistryFile).sorted().collect(Collectors.toList());
        }
        for (Path registryFile : registryFiles) {
            checkRegistryFile(registryFile);
        }

        if (missing > 0) {
            throw new IOException("registryFile descriptor missing: " + missing + " files");
        }

    }

    static boolean isRegistryFile(Path path) {
        return path.getFileName().toString().endsWith("registryFile.imagen")
                && path.getParent().endsWith(Paths.get("src", "main", "resources", "META-INF"));
    }

    public static void checkRegistryFile(Path registryFile) throws IOException {
        Path module = registryFile.getParent().getParent().getParent().getParent().getParent();
        Set<String> descriptors = new HashSet<>();
        for (String line : Files.readAllLines(registryFile)) {
            String[] keys = line.replaceFirst("^\\s*#", "").trim().split("\\s+");
            if (keys.length > 1 && keys[0].equals("descriptor")) {
                descriptors.add(keys[1]);
            }
        }

        System.out.println("Check " + registryFile);
        checkRegistryFile(module, descriptors, "Descriptor.java");
    }

    public static void checkRegistryFile(Path module, Set<String> descriptors, String suffix) throws IOException {
        Path java = module.resolve("src/main/java");

        try (Stream<Path> paths = Files.walk(java)) {
            paths.filter((Path path) -> {
                return path.getFileName().toString().endsWith(suffix) && !skip.contains(path.getFileName().toString());
            }).forEach((path) -> {
                String relative = java.relativize(path).toString();
                String className = relative.substring(0, relative.length() - ".java".length())
                        .replace(path.getFileSystem().getSeparator(), ".");
                if (!descriptors.contains(className)) {
                    ++missing;
                    System.out.println("    missing: " + className);
                }
            });
        }
    }
}
