import java.awt.Frame;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.File;
import org.eclipse.imagen.ImageN;
import org.eclipse.imagen.Interpolation;
import org.eclipse.imagen.InterpolationNearest;
import org.eclipse.imagen.ImageMIPMap;
import org.eclipse.imagen.ParameterBlockImageN;
import org.eclipse.imagen.RenderedOp;
import org.eclipse.imagen.widget.ScrollingImagePanel;

public class ImageMIPMapTest {

   protected static String file = "./images/pond.jpg";

   protected Interpolation interp = new InterpolationNearest();

   protected ImageMIPMap MIPMap;

   protected RenderedOp image;
   protected RenderedOp downSampler;

   private void test1() {
      AffineTransform at = new AffineTransform(0.8, 0.0, 0.0, 0.8,
                                               0.0, 0.0);
      InterpolationNearest interp = new InterpolationNearest();

      MIPMap = new ImageMIPMap(image, at, interp);

      display(MIPMap.getDownImage());
      display(MIPMap.getImage(4));
      display(MIPMap.getImage(1));
   }

   public void test2() {
      downSampler = createScaleOp(image, 0.9F);
      downSampler.removeSources();
      downSampler = createScaleOp(downSampler, 0.9F);

      MIPMap = new ImageMIPMap(image, downSampler);

      display(MIPMap.getImage(0));
      display(MIPMap.getImage(5));
      display(MIPMap.getImage(2));
   }

   public void test3() {
       downSampler = createScaleOp(image, 0.9F);
       downSampler = createScaleOp(downSampler, 0.9F);

       MIPMap = new ImageMIPMap(downSampler);

       display(MIPMap.getImage(5));
       System.out.println(MIPMap.getCurrentLevel());
       display(MIPMap.getCurrentImage());
       System.out.println(MIPMap.getCurrentLevel());
       display(MIPMap.getImage(1));
       System.out.println(MIPMap.getCurrentLevel());
   }

   protected RenderedOp createScaleOp(RenderedImage src,
                                      float factor) {
      ParameterBlock pb = new ParameterBlock();
      pb.addSource(src);
      pb.add(factor);
      pb.add(factor);
      pb.add(1.0F);
      pb.add(1.0F);
      pb.add(interp);
      return ImageN.create("scale", pb);
   }

   protected void display(RenderedImage img) {
      Frame frame = new Frame(getClass().getName());
      frame.add(new ScrollingImagePanel(img, img.getWidth(),
                                        img.getHeight()));
      frame.pack();
      frame.setVisible(true);
   }

   public ImageMIPMapTest(String name) {
      ParameterBlockImageN pb = new ParameterBlockImageN("ImageRead")
              .setParameter("Input", new File(file));
      image = ImageN.create("ImageRead", pb);
   }

   public static void main(String args[]) {
       ImageMIPMapTest test = new ImageMIPMapTest("ImageMIPMap");
       // test.test1();
       // test.test2();
       test.test3();
   }
}