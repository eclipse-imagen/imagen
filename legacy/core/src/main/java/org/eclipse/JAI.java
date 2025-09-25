package org.eclipse;

import java.awt.*;
import org.eclipse.imagen.ImageN;

/**
 * Placeholder class for those migration from Java Advanced Imaging (JAI) library.
 *
 * @deprecated Use {@link ImageN} instead.
 */
@Deprecated
public class JAI extends ImageN {
    public static RenderingHints.Key KEY_IMAGE_LAYOUT = ImageN.KEY_IMAGE_LAYOUT;
}
