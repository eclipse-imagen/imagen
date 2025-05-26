/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 *
 * This Example Content is intended to demonstrate usage of Eclipse technology. It is
 * provided to you under the terms and conditions of the Eclipse Distribution License
 * v1.0 which is available at http://www.eclipse.org/org/documents/edl-v10.php.
 */
package org.eclipse.imagen.tutorial.network;

import java.util.Vector;
import org.eclipse.imagen.*;
import org.eclipse.imagen.remote.RemoteJAI;

public class JAIDyadicSource0Panel extends JAIDyadicPanel {

    public JAIDyadicSource0Panel(JAINetworkDemo demo, Vector sourceVec, RemoteJAI pClient) {
        super(demo, sourceVec, pClient);
    }

    public String getDemoName() {
        return "Source0";
    }

    public PlanarImage process() {
        return getSource(0);
    }
}
