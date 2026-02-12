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

import java.awt.image.renderable.ParameterBlock;
import java.util.Vector;
import org.eclipse.imagen.*;
import org.eclipse.imagen.remote.RemoteJAI;

public class JAIDyadicSubtractPanel extends JAIDyadicPanel {

    public JAIDyadicSubtractPanel(JAINetworkDemo demo, Vector sourceVec, RemoteJAI pClient) {
        super(demo, sourceVec, pClient);
    }

    public String getDemoName() {
        return "Subtract";
    }

    public PlanarImage process() {
        PlanarImage im0 = getSource(0);
        PlanarImage im1 = getSource(1);

        ParameterBlock pb = new ParameterBlock();
        pb.addSource(im1);
        pb.addSource(im0);

        return client.create("subtract", pb, renderHints);
    }
}
