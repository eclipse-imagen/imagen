import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import org.eclipse.imagen.ImageN;
import org.eclipse.imagen.Interpolation;
import org.eclipse.imagen.ParameterBlockImageN;
import org.eclipse.imagen.RenderedOp;
/**
 * This program decodes an image file of any format supported
 * by Java Image I/O, such as GIF, JPEG, BMP, PNG, into a
 * RenderedImage, scales the image by 2X with bilinear
 * interpolation, and then displays the result of the scale
 * operation.
 */
public class ImageNSampleProgram {
    /** The main method. */
    public static void main(String[] args) {
        /* Validate input. */
        if (args.length != 1) {
            System.out.println("Usage: java ImageNSampleProgram " +
                               "input_image_filename");
            System.exit(-1);
        }
        /* Create an operator to decode the image file. */
        ParameterBlockImageN read = new ParameterBlockImageN("ImageRead");
        read.setParameter("Input", new File(args[0]));
        RenderedOp image1 = ImageN.create("ImageRead", read);
        /*
         * Create a standard bilinear interpolation object to be
         * used with the "Scale" operator.
         */
        Interpolation interp = Interpolation.getInstance(
                                   Interpolation.INTERP_BILINEAR);
        /**
         * Stores the required input source and parameters in a
         * ParameterBlock to be sent to the operation registry,
         * and eventually to the "Scale" operator.
         */
        ParameterBlockImageN params = new ParameterBlockImageN("Scale");
        params.addSource(image1);
        params.setParameter("xScale", 2.0F);
        params.setParameter("yScale", 2.0F);
        params.setParameter("xTrans", 0.0F);
        params.setParameter("yTrans", 0.0F);
        params.setParameter("interpolation", interp);
        /* Create an operator to scale image1. */
        RenderedOp image2 = ImageN.create("Scale", params);
        /* Attach image2 to a scrolling panel to be displayed. */
        ImageIcon icon = new ImageIcon(image2.getAsBufferedImage());
        SwingUtilities.invokeLater(() -> {
            /* Create a frame to contain the panel. */
            JFrame window = new JFrame("ImageN Sample Program");
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.add(new JScrollPane(new JLabel(icon)));
            window.pack();
            window.setVisible(true);
        });
    }
}
