//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Stream;

/** Quick QA check of registryFile.jai contents */
public class RegistryFileCheck {
    static int missing = 0;
    static Set<String> skip = Set.of(
        "ParameterListDescriptor.java",
        "OperationDescriptor.java",
        "RegistryElementDescriptor.java"
    );
    public static void main(String[] args) throws IOException {
        checkRegistryFile("../modules/core","org.eclipse.imagen.registryFile.jai");
        checkRegistryFile("../legacy/core","registryFile.jai");
        checkRegistryFile("../unsupported/core","registryFile.jai");

        if (missing > 0) {
            throw new IOException("registryFile descriptor missing: " + missing + " files");
        }

    }
    public static void checkRegistryFile(String module, String registryFileName) throws IOException {
        Path registryFile = Paths.get(module, "src/main/resources/META-INF/", registryFileName);
        String registry = Files.readString(registryFile);

        System.out.println("Check " + registryFile);
        checkRegistryFile(module,registry,"Descriptor.java");
    }
    
    public static void checkRegistryFile(String module, String registry, String suffix) throws IOException {
        Path java = Paths.get(module, "src/main/java");

        try (Stream<Path> paths = Files.walk(java)) {
        paths.filter((Path path) -> {
            return path.getFileName().toString().endsWith(suffix) && !skip.contains(path.getFileName().toString());
        }).forEach((path) -> {
            String fileName = path.getFileName().toString();
            String className = fileName.substring(0,fileName.length() - suffix.length());
            if (!registry.contains(className)) {
                ++missing;
                System.out.println("    missing: " + className);
            }
        });
    }
}
    
}
