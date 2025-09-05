/* JAI-Ext - OpenSource Java Advanced Image Extensions Library
*    http://www.geo-solutions.it/
*    Copyright 2014 GeoSolutions


* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at

* http://www.apache.org/licenses/LICENSE-2.0

* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.eclipse.imagen.media.mosaic;

import java.awt.RenderingHints;
import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import org.eclipse.imagen.ImageLayout;
import org.eclipse.imagen.JAI;
import org.eclipse.imagen.PlanarImage;
import org.eclipse.imagen.media.nullop.NullDescriptor;
import org.eclipse.imagen.media.range.Range;
import org.eclipse.imagen.media.testclasses.ComparisonTestBase;
import org.eclipse.imagen.media.translate.TranslateDescriptor;
import org.junit.BeforeClass;
import org.junit.Test;

public class ComparisonTest extends ComparisonTestBase {

    /** Value indicating No Data for the destination image */
    private static double destinationNoData = 0;

    /** Image to elaborate */
    private static RenderedImage[] images;

    /** RenderingHints used for selecting the borderExtender */
    private static RenderingHints hints;

    @BeforeClass
    public static void initialSetup() {
        // Selection of the images
        // image creation
        byte value1 = 50;
        byte value2 = 100;
        RenderedImage image1 = getSyntheticImage(value1);
        RenderedImage image2 = getSyntheticImage(value2);
        int width = image1.getWidth();
        // layout creation (same height of the source images, doubled width)
        ImageLayout layout = new ImageLayout(0, 0, image1.getWidth() + image2.getWidth(), image1.getHeight());
        hints = new RenderingHints(JAI.KEY_IMAGE_LAYOUT, layout);

        // translation of the first image
        RenderedImage image3 = TranslateDescriptor.create(image1, (float) ((int) (width * 0.1F)), 0F, null, null);
        // No op for the second image
        RenderedImage image4 = NullDescriptor.create(image2, hints);
        // array creation
        images = new RenderedImage[2];
        images[0] = image4;
        images[1] = image3;
    }

    @Test
    public void testNearestMosaicOverlayDescriptor() {
        testMosaic(org.eclipse.imagen.media.mosaic.MosaicDescriptor.MOSAIC_TYPE_OVERLAY);
    }

    @Test
    public void testNearestMosaicBlendDescriptor() {
        testMosaic(org.eclipse.imagen.media.mosaic.MosaicDescriptor.MOSAIC_TYPE_BLEND);
    }

    public void testMosaic(MosaicType mosaicType) {
        String suffix = mosaicType == org.eclipse.imagen.media.mosaic.MosaicDescriptor.MOSAIC_TYPE_OVERLAY
                ? "Overlay"
                : "Blend";
        Range range = getTestRange(DataBuffer.TYPE_BYTE, TestSelection.NO_ROI_NO_DATA);
        Range[] rangeND = new Range[] {range, range};
        double[] destnodata = {destinationNoData, destinationNoData};
        PlanarImage image = MosaicDescriptor.create(images, mosaicType, null, null, null, destnodata, rangeND, hints);
        finalizeTest(suffix, DataBuffer.TYPE_BYTE, image);
    }

    public static RenderedImage getSyntheticImage(byte value) {
        final float width = 512;
        final float height = 512;
        ParameterBlock pb = new ParameterBlock();
        Byte[] array = new Byte[] {value, (byte) (value + 1), (byte) (value + 2)};
        pb.add(width);
        pb.add(height);
        pb.add(array);
        // Create the constant operation.
        return JAI.create("constant", pb);
    }
}
