import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import org.eclipse.imagen.ImageN;
import org.eclipse.imagen.Interpolation;
import org.eclipse.imagen.ImageMIPMap;
import org.eclipse.imagen.ImagePyramid;
import org.eclipse.imagen.PlanarImage;
import org.eclipse.imagen.RenderedOp;
import org.eclipse.imagen.media.codec.FileSeekableStream;

public class ImagePyramidTest extends ImageMIPMapTest {

    protected RenderedOp upSampler;
    protected RenderedOp differencer;
    protected RenderedOp combiner;

    protected ImagePyramid pyramid;

    private void test1() {
    }

    public void test2() {
        downSampler = createScaleOp(image, 0.9F);
        downSampler.removeSources();
        downSampler = createScaleOp(downSampler, 0.9F);

        upSampler = createScaleOp(image, 1.2F);
        upSampler.removeSources();
        upSampler = createScaleOp(upSampler, 1.2F);

        differencer = createSubtractOp(image, image);
        differencer.removeSources();

        combiner = createAddOp(image, image);
        combiner.removeSources();

        pyramid = new ImagePyramid(image, downSampler, upSampler,
                                   differencer, combiner);
        display(pyramid.getImage(0));
        display(pyramid.getImage(4));
        display(pyramid.getImage(1));
        display(pyramid.getImage(6));
    }

    public void test3() {
        downSampler = createScaleOp(image, 0.9F);
        downSampler = createScaleOp(downSampler, 0.9F);

        upSampler = createScaleOp(image, 1.2F);
        upSampler.removeSources();

        differencer = createSubtractOp(image, image);
        differencer.removeSources();

        combiner = createAddOp(image, image);
        combiner.removeSources();

        pyramid = new ImagePyramid(downSampler, upSampler,
                                   differencer, combiner);
        // display(pyramid.getCurrentImage());
        display(pyramid.getDownImage());
        // display(pyramid.getDownImage());
        display(pyramid.getUpImage());
    }

    public void test4() {
        downSampler = createScaleOp(image, 0.5F);

        upSampler = createScaleOp(image, 2.0F);
        upSampler.removeSources();

        differencer = createSubtractOp(image, image);
        differencer.removeSources();

        combiner = createAddOp(image, image);
        combiner.removeSources();

        pyramid = new ImagePyramid(downSampler, upSampler,
                                   differencer, combiner);
        pyramid.getDownImage();

        display(pyramid.getCurrentImage());
        display(pyramid.getDiffImage());
        display(pyramid.getCurrentImage());
    }

    protected RenderedOp createSubtractOp(RenderedImage src1,
                                          RenderedImage src2) {
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(src1);
        pb.addSource(src2);
        return JAI.create("subtract", pb);
    }

    protected RenderedOp createAddOp(RenderedImage src1,
                                     RenderedImage src2) {
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(src1);
        pb.addSource(src2);
        return JAI.create("add", pb);
    }

    public ImagePyramidTest(String name) {
        super(name);
    }

    public static void main(String args[]) {
        ImagePyramidTest test = new ImagePyramidTest("ImagePyramid");
        // test.test2();
        test.test3();
        // test.test4();
    }
}