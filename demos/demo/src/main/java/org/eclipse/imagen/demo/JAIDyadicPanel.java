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

public abstract class JAIDyadicPanel extends JAIDemoPanel {
  
    JAIDemo demo;
    
    public JAIDyadicPanel(JAIDemo demo, Vector sourceVec) {
        super(sourceVec);
        this.demo = demo;
        masterSetup();
    }  
    
    public void makeControls(JPanel controls) {
    }
    
    public RenderingHints getRenderingHints(int dType,
                                            Rectangle rect,
                                            int nBands) {
        SampleModel sm =
            RasterFactory.createPixelInterleavedSampleModel(dType,
                                                            rect.width,
                                                            rect.height,
                                                            nBands);
        
        ImageLayout il = new ImageLayout();
        il.setSampleModel(sm);
        return new RenderingHints(JAI.KEY_IMAGE_LAYOUT, il);
    }
    
    public void reset() {
        demo.resetAdj();
    }
}
