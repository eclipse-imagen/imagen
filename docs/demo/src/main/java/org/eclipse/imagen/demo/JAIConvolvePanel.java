/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.eclipse.imagen.demo;

import java.awt.*;
import java.awt.image.renderable.*;
import java.awt.event.*;
import java.util.Vector;
import org.eclipse.imagen.*;
import javax.swing.*;
import javax.swing.event.*;

public class JAIConvolvePanel extends JAIDemoPanel implements ItemListener {

    static final String[] kernelLabels = { "Normal",
                                           "Blur",
                                           "Blur More",
                                           "Sharpen",
                                           "Sharpen More",
                                           "Detect Edges",
                                           "Emboss",
                                           "Gaussian (3x3)",
                                           "Gaussian (5x5)",
                                           "Gaussian (7x7)",
                                           "Gaussian (9x9)",
                                           "Gaussian (11x11)",
                                           "Gaussian (21x21)",
                                           "Gaussian (31x31)",
                                           "Gaussian (41x41)",
                                           "Gaussian (51x51)",
                                           "Gaussian (61x61)",
                                           "Gaussian (71x71)"
    };

    JComboBox kernelBox;

    KernelJAI[] kernels;
    KernelJAI kernel;

    public JAIConvolvePanel(Vector sourceVec) {
        super(sourceVec);
        masterSetup();
    }

    public String getDemoName() {
        return "Convolve";
    }

    public void makeControls(JPanel controls) {
        kernelBox = new JComboBox();
        for (int i = 0; i < kernelLabels.length; i++) {
            kernelBox.addItem(kernelLabels[i]);
        }

        kernelBox.addItemListener(this);
        controls.add(kernelBox);
    }

    private KernelJAI makeGaussianKernel(int radius) {
        int diameter = 2*radius + 1;
        float invrsq = 1.0F/(radius*radius);

        float[] gaussianData = new float[diameter];

        float sum = 0.0F;
        for (int i = 0; i < diameter; i++) {
            float d = i - radius;
            float val = (float)Math.exp(-d*d*invrsq);
            gaussianData[i] = val;
            sum += val;        
        }

        // Normalize
        float invsum = 1.0F/sum;
        for (int i = 0; i < diameter; i++) {
            gaussianData[i] *= invsum;
        }

        return new KernelJAI(diameter, diameter, radius, radius,
                             gaussianData, gaussianData);
    }

    private void initKernels() {
        kernels = new KernelJAI[kernelLabels.length];

        float[] normalData      = {  1.0F };

        float[] blurData        = {  0.0F,        1.0F/ 8.0F,  0.0F,
                                     1.0F/ 8.0F,  4.0F/ 8.0F,  1.0F/ 8.0F,
                                     0.0F,        1.0F/ 8.0F,  0.0F
        };

        float[] blurMoreData    = {  1.0F/14.0F,  2.0F/14.0F,  1.0F/14.0F,
                                     2.0F/14.0F,  2.0F/14.0F,  2.0F/14.0F,
                                     1.0F/14.0F,  2.0F/14.0F,  1.0F/14.0F
        };

        float[] sharpenData     = {  0.0F,       -1.0F/ 4.0F,  0.0F,
                                    -1.0F/ 4.0F,  8.0F/ 4.0F, -1.0F/ 4.0F,
                                     0.0F,       -1.0F/ 4.0F,  0.0F
        };

        float[] sharpenMoreData = { -1.0F/ 4.0F, -1.0F/ 4.0F, -1.0F/ 4.0F,
                                    -1.0F/ 4.0F, 12.0F/ 4.0F, -1.0F/ 4.0F,
                                    -1.0F/ 4.0F, -1.0F/ 4.0F, -1.0F/ 4.0F
        };

        float[] edgeData =        {  0.0F,       -1.0F,        0.0F,
                                    -1.0F,        4.0F,       -1.0F,
                                     0.0F,       -1.0F,        0.0F
        };

        float[] embossData =      { -5.0F,        0.0F,        0.0F,
                                     0.0F,        1.0F,        0.0F,
                                     0.0F,        0.0F,        5.0F
        };

        kernels[0] = new KernelJAI(1, 1, 0, 0, normalData);
        kernels[1] = new KernelJAI(3, 3, 1, 1, blurData);
        kernels[2] = new KernelJAI(3, 3, 1, 1, blurMoreData);
        kernels[3] = new KernelJAI(3, 3, 1, 1, sharpenData);
        kernels[4] = new KernelJAI(3, 3, 1, 1, sharpenMoreData);
        kernels[5] = new KernelJAI(3, 3, 1, 1, edgeData);
        kernels[6] = new KernelJAI(3, 3, 1, 1, embossData);
        kernels[7] = makeGaussianKernel(1);
        kernels[8] = makeGaussianKernel(2);
        kernels[9] = makeGaussianKernel(3);
        kernels[10] = makeGaussianKernel(4);
        kernels[11] = makeGaussianKernel(5);
        kernels[12] = makeGaussianKernel(10);
        kernels[13] = makeGaussianKernel(15);
        kernels[14] = makeGaussianKernel(20);
        kernels[15] = makeGaussianKernel(25);
        kernels[16] = makeGaussianKernel(30);
        kernels[17] = makeGaussianKernel(35);
        kernel = kernels[0];
    }

    public boolean supportsAutomatic() {
        return true;
    }

    public PlanarImage process() {
        PlanarImage im = getSource(0);

        if (kernel == null) {
            initKernels();
        }
        ParameterBlock paramBlock = new ParameterBlock();
        paramBlock.addSource(im);
        paramBlock.add(kernel);
        return JAI.create("convolve", paramBlock, renderHints);
    }

    public void startAnimation() {
    }

    public void animate() {
        try {
            int current = kernelBox.getSelectedIndex() + 1;

            if ( current >= kernelLabels.length ) {
                current = 0;
            }

            kernelBox.setSelectedIndex(current);
            Thread.sleep(500);
        } catch( InterruptedException e ) {
        }
    }

    public void reset() {
        kernelBox.setSelectedIndex(0);
        kernel = kernels[0];
    }

    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.DESELECTED) {
            return;
        }

        String item = (String)e.getItem();
        for (int i = 0; i < kernelLabels.length; i++) {
            if (item.equals(kernelLabels[i])) {
                kernel = kernels[i];
                break;
            }
        }
        repaint();
    }
}
