/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 *
 * This Example Content is intended to demonstrate usage of Eclipse technology. It is
 * provided to you under the terms and conditions of the Eclipse Distribution License
 * v1.0 which is available at http://www.eclipse.org/org/documents/edl-v10.php.
 */
package org.eclipse.imagen.demo;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import org.eclipse.imagen.ImageN;
import org.eclipse.imagen.OperationDescriptor;
import org.eclipse.imagen.OperationRegistry;
import org.eclipse.imagen.PlanarImage;
import org.eclipse.imagen.registry.RIFRegistry;
import org.eclipse.imagen.widget.ImageCanvas;

/**
 * JAIExampleApp is a simple program that demonstrates how to incorporate an extension imaging operation into ImageN and
 * use it to produce visible results. An imaging operation is composed of an OperationDescriptor, a RenderedImageFactory
 * and an OpImage.
 *
 * <p>The OperationDescriptor defines the parameters and the output of a family of high level operations called
 * RenderedImageFactorys (RIFs). Multiple RIFs are needed to define an operation because not all RIFs are required to
 * handle all cases. For example, one Convolve RIF may accelerate 3x3 convolves, but have no code for other cases, while
 * a second Convolve RIF could handle all cases in a more general (but possibly slower) way. OpImages are needed because
 * RIFs don't actually process any pixels. Instead they instantiate some number of OpImages via the
 * RenderedImageFactory.create() method and attach them to the operation's sources. The resulting OpImage chain is
 * responsible actually computes pixel values.
 *
 * <p>In order to be used, an OperationDescriptor must be registered by name with the OperationRegistry that is returned
 * by ImageN.getDefaultInstance.getOperationRegistry(). The RIF must also be registered with that name. The OpImages
 * that a given RIF produces are "hardcoded" to that particular RIF, so the OpImages do not need to be registered.
 */
public class JAIExampleApp extends WindowAdapter {

    public JAIExampleApp(String imageName) {

        Frame frame = new Frame("JAIExampleApp ImageN Program");
        frame.addWindowListener(this);

        // The SampleDescriptor is both an OperationDescriptor
        // and a RIF.  Other RIFs can be registered with this
        // OperationDescriptor (although none are in this
        // example.)

        SampleDescriptor sampleDescriptor = new SampleDescriptor();
        OperationDescriptor odesc = sampleDescriptor;
        RenderedImageFactory rif = sampleDescriptor;

        // Here we grab the current registry and then register the
        // OperationDescriptor and the RIF with the imaging
        // operation's name "sample".  The product name is needed
        // so that advanced users can differentiate between different
        // RIFs registered with an OperationDescriptor and order
        // them based on the user's preference.

        String operationName = "sample";
        String productName = "com.mycompany";
        OperationRegistry or = ImageN.getDefaultInstance().getOperationRegistry();
        or.registerDescriptor(odesc);
        RIFRegistry.register(or, operationName, productName, rif);

        // Read in the source image.

        PlanarImage im0 = JAIImageReader.readImage(imageName);

        // We add the source image that was created above into a
        // ParameterBlock object and call ImageN.create() with
        // the "sample" operation name.

        ParameterBlock pb = new ParameterBlock();
        pb.addSource(im0);

        // We set the values of param1 and param2 for the
        // SampleOpImage.  For demonstration purposes, SampleOpImage
        // implements a modified threshold op.

        pb.add(150);
        pb.add(200);
        RenderedImage im1 = ImageN.create("sample", pb);

        // Now that we have created an imaging chain, we hook it
        // up to an ImageCanvas and add it to the frame.  The ImageCanvas
        // will request tiles in response to repaint() events.

        ImageCanvas imageCanvas = new ImageCanvas(im1);
        frame.add(imageCanvas);
        frame.pack();
        frame.setVisible(true);
    }

    /** Handles window close events. */
    public void windowClosing(WindowEvent e) {
        System.exit(0);
    }

    /**
     * Instantiates an JAIExampleApp Frame and then displays it. The program must be run with a single argument that is
     * the name of a TIFF image.
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: JAIExampleApp ImageName");
            System.exit(1);
        }
        new JAIExampleApp(args[0]);
    }
}
