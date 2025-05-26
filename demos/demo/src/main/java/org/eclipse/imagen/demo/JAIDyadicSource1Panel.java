/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 *
 * This Example Content is intended to demonstrate usage of Eclipse technology. It is
 * provided to you under the terms and conditions of the Eclipse Distribution License
 * v1.0 which is available at http://www.eclipse.org/org/documents/edl-v10.php.
 */
package org.eclipse.imagen.demo;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.*;
import org.eclipse.imagen.*;

public class JAIDyadicSource1Panel extends JAIDyadicPanel {

    public JAIDyadicSource1Panel(JAIDemo demo, Vector sourceVec) {
        super(demo, sourceVec);
    }

    public String getDemoName() {
        return "Source1";
    }

    public PlanarImage process() {
        return getSource(1);
    }
}
