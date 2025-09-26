/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 *
 * This Example Content is intended to demonstrate usage of Eclipse technology. It is
 * provided to you under the terms and conditions of the Eclipse Distribution License
 * v1.0 which is available at http://www.eclipse.org/org/documents/edl-v10.php.
 */
package org.eclipse.imagen.demo;

import java.awt.*;
import java.awt.image.DataBuffer;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;
import java.util.Vector;
import javax.swing.*;
import org.eclipse.imagen.*;

public class JAIOpPanel extends JAIDemoPanel {

    String opName;
    int id;
    Icon imageIcon = null;
    JLabel imageLabel = null;
    Vector sourceVec;
    JAIDyadicPanel demo;

    public JAIOpPanel(JAIDyadicPanel demo, int id, String opName, Vector sourceVec) {
        super(sourceVec);
        this.opName = opName;
        this.sourceVec = sourceVec;
        masterSetup();
        this.id = id;
        this.demo = demo;
    }

    public String getDemoName() {
        return opName;
    }

    public void makeControls(JPanel controls) {
        controls.setLayout(new BorderLayout());
    }

    public PlanarImage process() {
        PlanarImage im, dst1;
        double[] constants;
        //   SampleModel sm;
        int nBands;
        Rectangle rect;
        // ImageLayout il;
        //   RenderingHints rh;

        PlanarImage im0 = getSource(0);
        PlanarImage im1 = getSource(1);

        ParameterBlock pb = new ParameterBlock();
        pb.addSource(im1);
        pb.addSource(im0);

        switch (id) {
            case 0:
                return im0;
            case 1:
                return im1;

            case 2:
                return ImageN.create("add", pb, renderHints);
            case 3:
                return ImageN.create("subtract", pb, renderHints);
            case 4:
                rect = im0.getBounds().intersection(im1.getBounds());
                nBands = Math.min(
                        im0.getSampleModel().getNumBands(), im1.getSampleModel().getNumBands());

                pb = new ParameterBlock();
                pb.addSource(im0);
                pb.addSource(im1);
                im = ImageN.create("multiply", pb, getRenderingHints(DataBuffer.TYPE_USHORT, rect, nBands));

                // Constants
                constants = new double[3];
                constants[0] = 255.0;
                constants[1] = 255.0;
                constants[2] = 255.0;

                pb = new ParameterBlock();
                pb.addSource(im);
                pb.add(constants);
                dst1 = (PlanarImage)
                        ImageN.create("dividebyconst", pb, getRenderingHints(DataBuffer.TYPE_BYTE, rect, nBands));
                return dst1;

            case 5:
                rect = im0.getBounds().intersection(im1.getBounds());
                nBands = Math.min(
                        im0.getSampleModel().getNumBands(), im1.getSampleModel().getNumBands());

                pb = new ParameterBlock();
                pb.addSource(im0);
                pb.addSource(im1);
                im = ImageN.create("divide", pb, getRenderingHints(DataBuffer.TYPE_FLOAT, rect, nBands));

                // Constants
                constants = new double[3];
                constants[0] = 255.0;
                constants[1] = 255.0;
                constants[2] = 255.0;

                pb = new ParameterBlock();
                pb.addSource(im);
                pb.add(constants);
                dst1 = (PlanarImage)
                        ImageN.create("multiplyconst", pb, getRenderingHints(DataBuffer.TYPE_BYTE, rect, nBands));
                return dst1;

            default:
                return im0;
        }
    }

    RenderingHints getRenderingHints(int dType, Rectangle rect, int nBands) {
        SampleModel sm = RasterFactory.createPixelInterleavedSampleModel(dType, rect.width, rect.height, nBands);

        ImageLayout il = new ImageLayout();
        il.setSampleModel(sm);
        return new RenderingHints(ImageN.KEY_IMAGE_LAYOUT, il);
    }

    public void reset() {
        demo.reset();
    }
}
