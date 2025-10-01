import java.awt.image.*;
import java.awt.image.renderable.ParameterBlock;
import org.eclipse.imagen.*;
import org.eclipse.imagen.widget.*;

/**
 * This test creates an imaging chain spread across two remote 
 * nodes and displays the result locally.
 */

public class MultiNodeTest extends WindowContainer {
    public static void main(String[] args) {
        if(args.length != 3) {
          throw new RuntimeException("Usage: java MultiNodeTest "+
                                       "file node1 node2");
        }

        new MultiNodeTest(args[0], args[1], args[2]);
    }
public MultiNodeTest(String fileName, String node1, String
                     node2) {

// Create a chain on node 1.
System.out.println("Creating dst1 = log(invert(fileload("+
                           fileName+"))) on "+node1);
        RenderedOp src = ImageN.create("fileload", fileName);
        RenderedOp op1 = ImageN.create("invert", src);
        RenderedOp op2 = ImageN.create("log", op1);
        RemoteImage rmt1 = new RemoteImage(node1, op2);

// Create a chain on node 2.
System.out.println("Creating dst2 = not(exp(dst1)) on "+node2);
        RenderedOp op3 = ImageN.create("exp", rmt1);
        RenderedOp op4 = ImageN.create("not", op3);
        RemoteImage rmt2 = new RemoteImage(node2, op4);

// Display the result of node 2.
System.out.println("Displaying results");
setTitle(getClass().getName()+" "+fileName);
add(new ScrollingImagePanel(rmt2, rmt2.getWidth(),
                            rmt2.getHeight()));
        pack();
        show();
    }
}
