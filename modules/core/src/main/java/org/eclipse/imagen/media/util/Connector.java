package org.eclipse.imagen.media.util;

import java.io.IOException;
import java.io.InputStream;

public interface Connector {

    InputStream open(String path) throws IOException;
}
