/* Copyright (c) 2025 Daniele Romagnoli and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution and is available at
 * http://www.opensource.org/licenses/apache2.0.php.
 */
package org.eclipse.imagen.media.algebra;

import java.awt.*;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import org.eclipse.imagen.CRIFImpl;
import org.eclipse.imagen.ImageLayout;
import org.eclipse.imagen.ROI;
import org.eclipse.imagen.media.opimage.RIFUtil;
import org.eclipse.imagen.media.range.Range;

/** A CRIF supporting the "Xor" operation, through the Algebra operation. */
public class XorCRIF extends CRIFImpl {

    /** Constructor. */
    public XorCRIF() {
        super("xor");
    }

    /**
     * Creates a new instance of AlgebraOpImage for the Xor operation.
     *
     * @param pb The two source images to be "Xored" together.
     * @param renderHints Optionally contains destination image layout.
     */
    public RenderedImage create(ParameterBlock pb, RenderingHints renderHints) {
        // Get ImageLayout from renderHints if any.
        ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);

        int numSrc = pb.getNumSources();
        RenderedImage[] sources = new RenderedImage[numSrc];
        for (int i = 0; i < numSrc; i++) {
            sources[i] = pb.getRenderedSource(i);
        }

        ROI roi = (ROI) pb.getObjectParameter(0);
        Range noData = (Range) pb.getObjectParameter(1);
        double destinationNoData = pb.getDoubleParameter(2);

        return new AlgebraOpImage(
                renderHints, layout, AlgebraDescriptor.Operator.XOR, roi, noData, destinationNoData, sources);
    }
}
