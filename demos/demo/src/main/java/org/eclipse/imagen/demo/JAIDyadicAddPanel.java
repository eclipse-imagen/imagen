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
import java.awt.image.renderable.ParameterBlock;
import java.util.Hashtable;
import java.util.Vector;
import org.eclipse.imagen.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.image.DataBuffer;
import java.awt.image.SampleModel;

public class JAIDyadicAddPanel extends JAIDyadicPanel {
  
    public JAIDyadicAddPanel(JAIDemo demo, Vector sourceVec) {
        super(demo, sourceVec);
    }  
    
    public String getDemoName() {
        return "Add";
    }
    
    public PlanarImage process() {
        PlanarImage im0 = getSource(0);
        PlanarImage im1 = getSource(1);
        
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(im1);
        pb.addSource(im0); 
   
        return JAI.create("add", pb, renderHints);
    }
}
