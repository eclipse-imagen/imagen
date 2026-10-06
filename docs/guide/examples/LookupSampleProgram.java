import java.awt.Frame;
import java.awt.image.DataBuffer;
import java.io.File;
import org.eclipse.imagen.ImageN;
import org.eclipse.imagen.ParameterBlockImageN;
import org.eclipse.imagen.RenderedOp;
import org.eclipse.imagen.media.lookup.LookupTable;
import org.eclipse.imagen.media.lookup.LookupTableFactory;
import org.eclipse.imagen.widget.ScrollingImagePanel;

public class LookupSampleProgram {

    // The main method.
    public static void main(String[] args) {

    // Validate input.
    if (args.length != 1) {
       System.out.println("Usage: java LookupSampleProgram " +
                          "TIFF_image_filename");
        System.exit(-1);
    }

    // Store the file in a ParameterBlock to be sent to
    // the operation registry, and eventually to the TIFF
    // reader.
    ParameterBlockImageN params = new ParameterBlockImageN("ImageRead")
            .setParameter("Input", new File(args[0]));

    // Create an operator to decode the TIFF file.
    RenderedOp image1 = ImageN.create("ImageRead", params);

    // Find out the first image's data type.
    int dataType = image1.getSampleModel().getDataType();
    RenderedOp image2 = null;
    if (dataType == DataBuffer.TYPE_BYTE) {
       // Display the byte image as it is.
       System.out.println("TIFF image is type byte.");
       image2 = image1;
    } else if (dataType == DataBuffer.TYPE_USHORT) {
       // Convert the unsigned short image to byte image.
       System.out.println("TIFF image is type ushort.");

       // Setup a standard window-level lookup table. */
       byte[] tableData = new byte[0x10000];
       for (int i = 0; i < 0x10000; i++) {
           tableData[i] = (byte)(i >> 8);
       }

       // Create a LookupTable object to be used with the
       // "lookup" operator.
       LookupTable table = LookupTableFactory.create(tableData);

       // Create an operator to lookup image1.
       image2 = ImageN.create("lookup", image1, table);
    } else {
        System.out.println("TIFF image is type " + dataType +
                           ", and will not be displayed.");
        System.exit(0);
    }

    // Get the width and height of image2.
    int width = image2.getWidth();
    int height = image2.getHeight();

     // Attach image2 to a scrolling panel to be displayed.
     ScrollingImagePanel panel = new ScrollingImagePanel(
                                     image2, width, height);

     // Create a frame to contain the panel.
     Frame window = new Frame("Lookup Sample Program");
     window.add(panel);
     window.pack();
     window.setVisible(true);
  }
}