import java.awt.Frame;
import java.awt.image.renderable.ParameterBlock;
import java.io.IOException;
import org.eclipse.imagen.Interpolation;
import org.eclipse.imagen.ImageN;
import org.eclipse.imagen.RenderedOp;
import org.eclipse.imagen.codec.FileSeekableStream;
import org.eclipse.imagen.widget.ScrollingImagePanel;
/**
 * This program decodes an image file of any supported
 * formats, such as GIF, JPEG, TIFF, BMP, PNM, PNG, into a
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
        /*
         * Create an input stream from the specified file name
         * to be used with the file decoding operator.
         */
        FileSeekableStream stream = null;
        try {
            stream = new FileSeekableStream(args[0]);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
        /* Create an operator to decode the image file. */
        RenderedOp image1 = ImageN.create("stream", stream);
        /*
         * Create a standard bilinear interpolation object to be
         * used with the "scale" operator.
         */
        Interpolation interp = Interpolation.getInstance(
                                   Interpolation.INTERP_BILINEAR);
        /**
         * Stores the required input source and parameters in a
         * ParameterBlock to be sent to the operation registry,
         * and eventually to the "scale" operator.
         */
        ParameterBlock params = new ParameterBlock();
        params.addSource(image1);
        params.add(2.0F);         // x scale factor
        params.add(2.0F);         // y scale factor
        params.add(0.0F);         // x translate
        params.add(0.0F);         // y translate
        params.add(interp);       // interpolation method
        /* Create an operator to scale image1. */
        RenderedOp image2 = JAI.create("scale", params);
        /* Get the width and height of image2. */
        int width = image2.getWidth();
        int height = image2.getHeight();
        /* Attach image2 to a scrolling panel to be displayed. */
        ScrollingImagePanel panel = new ScrollingImagePanel(
                                        image2, width, height);
        /* Create a frame to contain the panel. */
        Frame window = new Frame("ImageN Sample Program");
        window.add(panel);
        window.pack();
        window.show();
    }
}