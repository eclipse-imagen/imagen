package org.eclipse.imagen.media.algebra;

import java.awt.*;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import org.eclipse.imagen.CRIFImpl;
import org.eclipse.imagen.ImageLayout;
import org.eclipse.imagen.ROI;
import org.eclipse.imagen.media.opimage.RIFUtil;
import org.eclipse.imagen.media.range.Range;

/** A CRIF supporting the "Add" operation, through the Algebra operation. */
public class AddCRIF extends CRIFImpl {

    /** Constructor. */
    public AddCRIF() {
        super("add");
    }

    /**
     * Creates a new instance of AlgebraOpImage for the Add operation.
     *
     * @param pb The two source images to be "added" together.
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
                renderHints, layout, AlgebraDescriptor.Operator.SUM, roi, noData, destinationNoData, sources);
    }
}
