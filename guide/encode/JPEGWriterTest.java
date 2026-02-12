import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.image.renderable.*;
import java.io.*;
import org.eclipse.imagen.*;
import org.eclipse.imagen.widget.*;
import org.eclipse.imagen.media.codec.*;

public class JPEGWriterTest extends WindowContainer {

private ImageEncoder encoder = null;
private JPEGEncodeParam encodeParam = null;

// Create some Quantization tables.
    private static int[] qtable1 = {
        1,1,1,1,1,1,1,1,
        1,1,1,1,1,1,1,1,
        1,1,1,1,1,1,1,1,
        1,1,1,1,1,1,1,1,
        1,1,1,1,1,1,1,1,
        1,1,1,1,1,1,1,1,
        1,1,1,1,1,1,1,1,
        1,1,1,1,1,1,1,1
    };

    private static int[] qtable2 = {
        2,2,2,2,2,2,2,2,
        2,2,2,2,2,2,2,2,
        2,2,2,2,2,2,2,2,
        2,2,2,2,2,2,2,2,
        2,2,2,2,2,2,2,2,
        2,2,2,2,2,2,2,2,
        2,2,2,2,2,2,2,2,
        2,2,2,2,2,2,2,2
    };

    private static int[] qtable3 = {
        3,3,3,3,3,3,3,3,
        3,3,3,3,3,3,3,3,
        3,3,3,3,3,3,3,3,
        3,3,3,3,3,3,3,3,
        3,3,3,3,3,3,3,3,
        3,3,3,3,3,3,3,3,
        3,3,3,3,3,3,3,3,
        3,3,3,3,3,3,3,3
    };

    // Really rotten quality Q Table
    private static int[] qtable4 = {
        200,200,200,200,200,200,200,200,
        200,200,200,200,200,200,200,200,
        200,200,200,200,200,200,200,200,
        200,200,200,200,200,200,200,200,
        200,200,200,200,200,200,200,200,
        200,200,200,200,200,200,200,200,
        200,200,200,200,200,200,200,200,
        200,200,200,200,200,200,200,200
    };

public static void main(String args[]) {
    JPEGWriterTest jtest = new JPEGWriterTest(args);
    }

// Load the source image.
private PlanarImage loadImage(String imageName) {
ParameterBlock pb = (new
        ParameterBlock()).add(imageName);
PlanarImage src = ImageN.create("fileload", pb);
        if (src == null) {
        System.out.println("Error in loading image " + imageName);
            System.exit(1);
        }
        return src;
    }

// Create the image encoder.
private void encodeImage(PlanarImage img, FileOutputStream out)
    {
encoder = ImageCodec.createImageEncoder("JPEG", out,
                                        encodeParam);
        try {
            encoder.encode(img);
            out.close();
        } catch (IOException e) {
            System.out.println("IOException at encoding..");
            System.exit(1);
        }
    }

private FileOutputStream createOutputStream(String outFile) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(outFile);
        } catch(IOException e) {
            System.out.println("IOException.");
            System.exit(1);
        }

        return out;
    }

public JPEGWriterTest(String args[]) {
// Set parameters from command line arguments.
String inFile = "images/Parrots.tif";

FileOutputStream out1 = createOutputStream("out1.jpg");
FileOutputStream out2 = createOutputStream("out2.jpg");
FileOutputStream out3 = createOutputStream("out3.jpg");

// Create the source op image.
PlanarImage src = loadImage(inFile);

   double[] constants = new double[3];
   constants[0] = 0.0;
   constants[1] = 0.0;
   constants[2] = 0.0;
   ParameterBlock pb = new ParameterBlock();
   pb.addSource(src);
   pb.add(constants);

// Create a new src image with weird tile sizes
ImageLayout layout = new ImageLayout();
layout.setTileWidth(57);
layout.setTileHeight(57);
RenderingHints hints = new RenderingHints(ImageN.KEY_IMAGE_LAYOUT,
                                                  layout);
PlanarImage src1 = ImageN.create("addconst", pb, hints);

// ----- End src loading ------

// Set the encoding parameters if necessary.
encodeParam = new JPEGEncodeParam();

encodeParam.setQuality(0.1F);

encodeParam.setHorizontalSubsampling(0, 1);
encodeParam.setHorizontalSubsampling(1, 2);
encodeParam.setHorizontalSubsampling(2, 2);

encodeParam.setVerticalSubsampling(0, 1);
encodeParam.setVerticalSubsampling(1, 1);
encodeParam.setVerticalSubsampling(2, 1);

encodeParam.setRestartInterval(64);
//encodeParam.setWriteImageOnly(false);
//encodeParam.setWriteTablesOnly(true);
//encodeParam.setWriteJFIFHeader(true);

// Create the encoder.
encodeImage(src, out1);
PlanarImage dst1 = loadImage("out1.jpg");

//   ----- End first encode ---------

encodeParam.setLumaQTable(qtable1);
encodeParam.setChromaQTable(qtable2);

encodeImage(src, out2);
PlanarImage dst2 = loadImage("out2.jpg");

//   ----- End second encode ---------

encodeParam = new JPEGEncodeParam();
encodeImage(loadImage("images/BlackCat.tif"), out3);
PlanarImage dst3 = loadImage("out3.jpg");

//   ----- End third encode ---------

setTitle ("JPEGWriter Test");
setLayout(new GridLayout(2, 2));
ScrollingImagePanel panel1 = new ScrollingImagePanel(src, 512,
                                                     400);
ScrollingImagePanel panel2 = new ScrollingImagePanel(dst1, 512,
                                                     400);
ScrollingImagePanel panel3 = new ScrollingImagePanel(dst2, 512,
                                                     400);
ScrollingImagePanel panel4 = new ScrollingImagePanel(dst3, 512,
                                                     400);
   add(panel1);
   add(panel2);
   add(panel3);
   add(panel4);
   pack();
   show();    }
}