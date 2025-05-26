/**
 * /* Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 *
 * <p>This Example Content is intended to demonstrate usage of Eclipse technology. It is provided to you under the terms
 * and conditions of the Eclipse Distribution License v1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */
package org.eclipse.imagen.tutorial.network;

import java.awt.*;
import java.awt.image.SampleModel;
import java.util.Vector;
import javax.swing.*;
import org.eclipse.imagen.*;
import org.eclipse.imagen.remote.RemoteJAI;

public abstract class JAIDyadicPanel extends JAIDemoPanel {

    JAINetworkDemo demo;

    public JAIDyadicPanel(JAINetworkDemo demo, Vector sourceVec, RemoteJAI pClient) {
        super(sourceVec, pClient);
        this.demo = demo;
        this.client = pClient;
        masterSetup();
    }

    public void makeControls(JPanel controls) {}

    public RenderingHints getRenderingHints(int dType, Rectangle rect, int nBands) {
        SampleModel sm = RasterFactory.createPixelInterleavedSampleModel(dType, rect.width, rect.height, nBands);

        ImageLayout il = new ImageLayout();
        il.setSampleModel(sm);
        return new RenderingHints(JAI.KEY_IMAGE_LAYOUT, il);
    }

    public void reset() {
        demo.resetAdj();
    }
}
