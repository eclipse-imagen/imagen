/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 *
 * This Example Content is intended to demonstrate usage of Eclipse technology. It is
 * provided to you under the terms and conditions of the Eclipse Distribution License
 * v1.0 which is available at http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */
package org.eclipse.imagen.tutorial.network;

import java.awt.*;
import java.awt.image.DataBuffer;
import java.awt.image.renderable.ParameterBlock;
import java.util.Vector;
import javax.swing.*;
import org.eclipse.imagen.*;
import org.eclipse.imagen.remote.RemoteJAI;

public class JAIDyadicDividePanel extends JAIDyadicPanel {

    public JAIDyadicDividePanel(JAINetworkDemo demo, Vector sourceVec, RemoteJAI pClient) {
        super(demo, sourceVec, pClient);
    }

    public String getDemoName() {
        return "Divide";
    }

    public void makeControls(JPanel controls) {}

    public PlanarImage process() {
        PlanarImage im0 = getSource(0);
        PlanarImage im1 = getSource(1);

        ParameterBlock pb = new ParameterBlock();
        pb.addSource(im1);
        pb.addSource(im0);

        Rectangle rect = im0.getBounds().intersection(im1.getBounds());
        int nBands = Math.min(
                im0.getSampleModel().getNumBands(), im1.getSampleModel().getNumBands());

        pb = new ParameterBlock();
        pb.addSource(im0);
        pb.addSource(im1);
        PlanarImage im = client.create("divide", pb, getRenderingHints(DataBuffer.TYPE_FLOAT, rect, nBands));

        // Constants
        double[] constants = new double[3];
        constants[0] = 255.0;
        constants[1] = 255.0;
        constants[2] = 255.0;

        pb = new ParameterBlock();
        pb.addSource(im);
        pb.add(constants);
        PlanarImage dst1 =
                (PlanarImage) client.create("multiplyconst", pb, getRenderingHints(DataBuffer.TYPE_BYTE, rect, nBands));
        return dst1;
    }
}
