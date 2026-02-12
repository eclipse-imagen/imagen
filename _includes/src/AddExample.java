import org.eclipse.imagen.*;
import org.eclipse.imagen.widget.*;
import java.awt.Frame;

public class AddExample extends Frame {

    // ScrollingImagePanel is a utility widget that
    // contains a Graphics2D (i.e., is an image sink).
    ScrollingImagePanel imagePanel1;

    // For simplicity, we just do all the work in the
    // class constructor.
    public AddExample(ParameterBlock param1,
                      ParameterBlock param2) {

         // Create a constant image
         RenderedOp im0 = JAI.create("constant", param1);

         // Create another constant image.
         RenderedOp im1 = JAI.create("constant", param2);
         // Add the two images together.

         RenderedOp im2 = JAI.create("add", im0, im1);

         // Display the original in a scrolling window
         imagePanel1 = new ScrollingImagePanel(im2, 100, 100);

         // Add the display widget to our frame.
         add(imagePanel1);
    }
}
