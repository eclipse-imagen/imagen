import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.geom.*;
import java.awt.image.*;
import java.awt.image.renderable.*;
import java.util.*;
import org.eclipse.imagen.*;
import org.eclipse.imagen.operator.*;
import org.eclipse.imagen.widget.*;
public class RemoteImagingTest extends WindowContainer {

/** Default remote server. */
private static final String DEFAULT_SERVER =
                         "camus.eng.sun.com:1099";

/** Tile dimensions. */
private static final int TILE_WIDTH = 256;
private static final int TILE_HEIGHT = 256;

public static void main(String args[]) {
String fileName1 = null;
String fileName2 = null;

// Check args.
    if(!(args.length >= 0 && args.length <= 3)) {
       System.out.println("\nUsage: java RemoteImagingTest "+
                      "[[[serverName] | [fileName1 fileName2]] | "+
                      "[serverName fileName1 fileName2]]"+"\n");
       System.exit(1);
    }

// Set the server name.
String serverName = null;
   if(args.length == 0 || args.length == 2) {
       serverName = DEFAULT_SERVER;
       System.out.println("\nUsing default server '"+
                          DEFAULT_SERVER+"'\n");
   } else {
       serverName = args[0];
   }

// Set the file names.
   if(args.length == 2) {
       fileName1 = args[0];
       fileName2 = args[1];
   } else if(args.length == 3) {
       fileName1 = args[1];
       fileName2 = args[2];
   } else {
 fileName1 = "/import/jai/JAI_RP/test/images/Boat_At_Dock.tif";
 fileName2 = "/import/jai/JAI_RP/test/images/FarmHouse.tif";
       System.out.println("\nUsing default images '"+
                          fileName1 + "' and '" + fileName2 + "'\n");
     }

RemoteImagingTest riTest =
       new RemoteImagingTest(serverName, fileName1, fileName2);
    }

/**
* Run a remote imaging test.
*
* @param serverName The name of the remote server to use.
* @param fileName1 The first addend image file to use.
* @param fileName2 The second addend image file to use.
*/
RemoteImagingTest(String serverName, String fileName1, String
                  fileName2) {
// Create the operations to load the images from files.
RenderedOp src1 = ImageN.create("fileload", fileName1);
RenderedOp src2 = ImageN.create("fileload", fileName2);

// Render the sources without freezing the nodes.
PlanarImage ren1 = src1.createInstance();
PlanarImage ren2 = src2.createInstance();

// Create TiledImages with the file images as their sources
// thereby ensuring that the serialized images are truly tiled.
   SampleModel sampleModel1 =
ren1.getSampleModel().createCompatibleSampleModel(TILE_WIDTH,
                                                  TILE_HEIGHT);
TiledImage ti1 = new TiledImage(ren1.getMinX(), ren1.getMinY(),
                                ren1.getWidth(), ren1.getHeight(),
                                ren1.getTileGridXOffset(),
                                ren1.getTileGridYOffset(),
                                sampleModel1, ren1.getColorModel());
ti1.set(src1);
SampleModel sampleModel2 =
  ren2.getSampleModel().createCompatibleSampleModel(TILE_WIDTH,
                                                    TILE_HEIGHT);
 TiledImage ti2 = new TiledImage(ren2.getMinX(), ren2.getMinY(),
                                 ren2.getWidth(), ren2.getHeight(),
                                 ren2.getTileGridXOffset(),
                                 ren2.getTileGridYOffset(),
                               sampleModel2, ren2.getColorModel());
  ti2.set(src2);

// Create a hint to specify the tile dimensions.
ImageLayout layout = new ImageLayout();
layout.setTileWidth(TILE_WIDTH).setTileHeight(TILE_HEIGHT);
     RenderingHints rh = new
         RenderingHints(ImageN.KEY_IMAGE_LAYOUT, layout);

// Rescale the images to the range [0, 127].
ParameterBlock pb = (new ParameterBlock());
pb.addSource(ti1);
pb.add(new double[] {0.5}).add(new double[] {0.0});
RenderedOp addend1 = ImageN.create("rescale", pb, rh);
pb = (new ParameterBlock());
pb.addSource(ti2);
pb.add(new double[] {0.5}).add(new double[] {0.0});
RenderedOp addend2 = ImageN.create("rescale", pb, rh);

// Add the rescaled images.
pb = (new
   ParameterBlock()).addSource(addend1).addSource(addend2);
        RenderedOp sum = ImageN.create("add", pb, rh);

        // Dither the sum of the rescaled images.
        pb = (new ParameterBlock()).addSource(sum);
        
pb.add(ColorCube.BYTE_496).add(KernelJAI.DITHER_MASK_443);
        RenderedOp dithered = ImageN.create("ordereddither", pb, rh);

// Construct a RemoteImage from the RenderedOp chain.
RemoteImage remoteImage = new RemoteImage(serverName, sum);

// Set the display title and window layout.
setTitle(getClass().getName());
setLayout(new GridLayout(2, 2));

// Local rendering.
add(new ScrollingImagePanel(sum,
                            sum.getWidth(),
                            sum.getHeight()));

// RenderedOp remote rendering.
add(new ScrollingImagePanel(remoteImage,
                            remoteImage.getWidth(),
                            remoteImage.getHeight()));

// RenderedImage remote rendering
PlanarImage sumImage = sum.getRendering();
remoteImage = new RemoteImage(serverName, sumImage);
add(new ScrollingImagePanel(remoteImage,
                            remoteImage.getWidth(),
                            remoteImage.getHeight()));

// RenderableOp remote rendering.
pb = new ParameterBlock();
pb.addSource(dithered);
RenderableOp absImage = ImageN.createRenderable("absolute", pb);
pb = new ParameterBlock();
pb.addSource(absImage).add(ColorCube.BYTE_496);
RenderableOp lutImage = ImageN.createRenderable("lookup", pb);
AffineTransform tf =
       AffineTransform.getScaleInstance(384/dithered.getWidth(),
                                        256/dithered.getHeight());
Rectangle aoi = new Rectangle(128, 128, 384, 256);
RenderContext rc = new RenderContext(tf, aoi, rh);
remoteImage = new RemoteImage(serverName, lutImage, rc);
add(new ScrollingImagePanel(remoteImage,
                            remoteImage.getWidth(),
                            remoteImage.getHeight()));

// Finally display everything
        pack();
        show();
    }
}
