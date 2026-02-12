// Specify the classes to import.
import java.awt.image.renderable.ParameterBlock;
import java.io.File;
import org.eclipse.imagen.JAI;
import org.eclipse.imagen.PlanarImage;
import org.eclipse.imagen.RenderedOp;
import org.eclipse.imagen.widget.ScrollingImagePanel;
public class FileTest extends WindowContainer {
// Specify a default image in case the user fails to specify
// one at run time.
public static final String DEFAULT_FILE = 
                               "./images/earth.jpg";
    public static void main(String args[]) {
        String fileName = null;
        // Check for a filename in the argument.
        if(args.length == 0) {
            fileName = DEFAULT_FILE;
        } else if(args.length == 1) {
            fileName = args[0];
        } else {
            System.out.println("\nUsage: java " +
                               (new FileTest()).getClass().getName() +
                               " [file]\n");
            System.exit(0);
        }
        new FileTest(fileName);
    }
    public FileTest() {}
    public FileTest(String fileName) {
   // Read the image from the designated path.
   System.out.println("Creating operation to load image from '" +
                       fileName+"'");
   RenderedOp img =  JAI.create("fileload", fileName);
   // Set display name and layout.
   setTitle(getClass().getName()+": "+fileName);
        // Display the image.
        System.out.println("Displaying image");
        add(new ScrollingImagePanel(img, img.getWidth(),
                                    img.getHeight()));
        pack();
        show();
    }
}