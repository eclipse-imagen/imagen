import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.Iterator;
import javax.imageio.*;
import javax.imageio.stream.ImageOutputStream;
import org.eclipse.imagen.*;
import org.eclipse.imagen.widget.*;

public class JPEGWriterTest extends Frame {

    public static void main(String args[]) throws IOException {
        String inFile = args.length > 0 ? args[0] : "images/Parrots.tif";
        new JPEGWriterTest(inFile);
    }

    // Load an image using the ImageRead operation.
    private RenderedImage loadImage(String imageName) {
        ParameterBlockImageN read = new ParameterBlockImageN("ImageRead");
        read.setParameter("Input", new File(imageName));
        return ImageN.create("ImageRead", read);
    }

    // Write an image as JPEG with the given quality (0.0 to 1.0).
    private void encodeImage(RenderedImage img, String outFile, float quality) throws IOException {
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpeg");
        ImageWriter writer = writers.next();
        ImageWriteParam param = writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(quality);

        try (ImageOutputStream out = ImageIO.createImageOutputStream(new File(outFile))) {
            writer.setOutput(out);
            writer.write(null, new IIOImage(img, null, null), param);
        } finally {
            writer.dispose();
        }
    }

    public JPEGWriterTest(String inFile) throws IOException {
        RenderedImage src = loadImage(inFile);

        encodeImage(src, "out1.jpg", 0.1F);
        RenderedImage dst1 = loadImage("out1.jpg");

        encodeImage(src, "out2.jpg", 0.75F);
        RenderedImage dst2 = loadImage("out2.jpg");

        setTitle("JPEGWriter Test");
        setLayout(new GridLayout(1, 3));
        add(new ScrollingImagePanel(src, 512, 400));
        add(new ScrollingImagePanel(dst1, 512, 400));
        add(new ScrollingImagePanel(dst2, 512, 400));
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
        pack();
        setVisible(true);
    }
}
