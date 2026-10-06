// Specify the classes to import.
import java.awt.Frame;
import java.io.File;
import org.eclipse.imagen.ImageN;
import org.eclipse.imagen.ParameterBlockImageN;
import org.eclipse.imagen.RenderedOp;
import org.eclipse.imagen.widget.ScrollingImagePanel;
public class FileTest extends Frame {
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
                               FileTest.class.getName() +
                               " [file]\n");
            System.exit(0);
        }
        new FileTest(fileName);
    }
    public FileTest(String fileName) {
   // Read the image from the designated path.
   System.out.println("Creating operation to load image from '" +
                       fileName+"'");
   ParameterBlockImageN pb = new ParameterBlockImageN("ImageRead")
           .setParameter("Input", new File(fileName));
   RenderedOp img = ImageN.create("ImageRead", pb);
   // Set display name and layout.
   setTitle(getClass().getName()+": "+fileName);
        // Display the image.
        System.out.println("Displaying image");
        add(new ScrollingImagePanel(img, img.getWidth(),
                                    img.getHeight()));
        pack();
        setVisible(true);
    }
}
