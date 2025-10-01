/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 *
 * This Example Content is intended to demonstrate usage of Eclipse technology. It is
 * provided to you under the terms and conditions of the Eclipse Distribution License
 * v1.0 which is available at http://www.eclipse.org/org/documents/edl-v10.php.
 */
package org.eclipse.imagen.tutorial;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.renderable.*;
import javax.swing.*;
import javax.swing.border.*;
import org.eclipse.imagen.*;
import org.eclipse.imagen.widgets.*;

public class Convolution extends JPanel implements ActionListener {

    private PlanarImage source;
    private PlanarImage target;
    private ImageDisplay srcDisplay;
    private ImageDisplay dstDisplay;
    private JTextField[][] krn;
    private JLabel textline;
    private JButton[] btns;
    private KernelImageN kernel;
    private float sum = 9.0F;
    private Font btnFont = new Font("sanserif", Font.BOLD, 10);
    private final String[] labels = {
        "Sharp1", "Sharp2", "Sharp3", "Sharp4", "Laplace1", "Laplace2", "Box", "Low Pass", "Emboss", "Custom"
    };

    public Convolution(String filename) {
        super(true);
        setOpaque(true);
        setBackground(Color.white);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayout(1, 2));
        panel1.setBackground(Color.white);

        JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayout(1, 2));
        panel2.setBackground(Color.white);

        textline = new JLabel("Kernel coefficients are divided by " + sum);

        add(panel1);
        add(textline);
        add(panel2);

        JPanel source_panel = new JPanel();
        source_panel.setLayout(new BorderLayout());

        JLabel source_label = new JLabel("Source");
        source_panel.add(source_label, BorderLayout.NORTH);
        source_panel.setBackground(Color.white);

        JPanel target_panel = new JPanel();
        target_panel.setLayout(new BorderLayout());
        target_panel.setBackground(Color.white);

        JLabel target_label = new JLabel("Destination");
        target_panel.add(target_label, BorderLayout.NORTH);

        panel1.add(source_panel);
        panel1.add(target_panel);

        JPanel convolve_panel = new JPanel();
        convolve_panel.setLayout(new BorderLayout());

        JLabel convolve_label = new JLabel(" Convolve");
        convolve_panel.add(convolve_label, BorderLayout.NORTH);

        JPanel kernel_panel = new JPanel();
        kernel_panel.setLayout(new BorderLayout());

        JLabel kernel_label = new JLabel(" Kernel (editable)");
        kernel_panel.add(kernel_label, BorderLayout.NORTH);

        JPanel btn_panel = new JPanel();
        btn_panel.setLayout(new GridLayout(5, 3, 8, 15));
        btn_panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        btns = new JButton[10];

        for (int i = 0; i < 10; i++) {
            btns[i] = new JButton(labels[i]);
            btns[i].setFont(btnFont);
            btns[i].addActionListener(this);
            btn_panel.add(btns[i]);
        }

        convolve_panel.add(btn_panel, BorderLayout.CENTER);
        convolve_panel.setBorder(new LineBorder(Color.black, 1));

        JPanel krn_panel = new JPanel();
        krn_panel.setLayout(new GridLayout(3, 3, 8, 50));
        krn_panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        krn = new JTextField[3][3];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                krn[i][j] = new JTextField();
                krn[i][j].setDocument(new FloatDocument());
                krn[i][j].setText("1.0");
                krn_panel.add(krn[i][j]);
            }
        }

        kernel_panel.add(krn_panel);
        kernel_panel.setBorder(new LineBorder(Color.black, 1));

        panel2.add(convolve_panel);
        panel2.add(kernel_panel);

        source = ReadImage.getImage(filename);
        srcDisplay = new ImageDisplay(source);
        srcDisplay.setBackground(Color.white);
        source_panel.add(srcDisplay);

        dstDisplay = new ImageDisplay(source.getWidth(), source.getHeight());
        dstDisplay.setBackground(Color.white);
        target_panel.add(dstDisplay);
    }

    public void actionPerformed(ActionEvent e) {
        JButton bt = (JButton) e.getSource();
        int k = -1;

        for (int i = 0; i < labels.length; i++) {
            if (bt == btns[i]) {
                k = i;
            }
        }

        loadKernel(k);
        convolve(k);
    }

    public void convolve(int k) {
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(source);
        pb.add(kernel);
        target = ImageN.create("convolve", pb, null);

        // emboss (special case)
        if (k == 8) {
            double[] constants = new double[3];

            constants[0] = 128.0;
            constants[1] = 128.0;
            constants[2] = 128.0;

            pb = new ParameterBlock();
            pb.addSource(target);
            pb.add(constants);
            target = ImageN.create("addconst", pb, null);
        }

        dstDisplay.set(target);
    }

    public void loadKernel(int choice) {

        float[] data = new float[9];

        switch (choice) {
            case 0:
                data[0] = 0.0F;
                data[1] = -1.0F;
                data[2] = 0.0F;
                data[3] = -1.0F;
                data[4] = 5.0F;
                data[5] = -1.0F;
                data[6] = 0.0F;
                data[7] = -1.0F;
                data[8] = 0.0F;
                break;

            case 1:
                data[0] = -1.0F;
                data[1] = -1.0F;
                data[2] = -1.0F;
                data[3] = -1.0F;
                data[4] = 9.0F;
                data[5] = -1.0F;
                data[6] = -1.0F;
                data[7] = -1.0F;
                data[8] = -1.0F;
                break;

            case 2:
                data[0] = 1.0F;
                data[1] = -2.0F;
                data[2] = 1.0F;
                data[3] = -2.0F;
                data[4] = 5.0F;
                data[5] = -2.0F;
                data[6] = 1.0F;
                data[7] = -2.0F;
                data[8] = 1.0F;
                break;

            case 3:
                data[0] = -1.0F;
                data[1] = 1.0F;
                data[2] = -1.0F;
                data[3] = 1.0F;
                data[4] = 1.0F;
                data[5] = 1.0F;
                data[6] = -1.0F;
                data[7] = 1.0F;
                data[8] = -1.0F;
                break;

            case 4:
                data[0] = -1.0F;
                data[1] = -1.0F;
                data[2] = -1.0F;
                data[3] = -1.0F;
                data[4] = 8.0F;
                data[5] = -1.0F;
                data[6] = -1.0F;
                data[7] = -1.0F;
                data[8] = -1.0F;
                break;

            case 5:
                data[0] = 0.0F;
                data[1] = -1.0F;
                data[2] = 0.0F;
                data[3] = -1.0F;
                data[4] = 4.0F;
                data[5] = -1.0F;
                data[6] = 0.0F;
                data[7] = -1.0F;
                data[8] = 0.0F;
                break;

            case 6:
                data[0] = 1.0F;
                data[1] = 1.0F;
                data[2] = 1.0F;
                data[3] = 1.0F;
                data[4] = 1.0F;
                data[5] = 1.0F;
                data[6] = 1.0F;
                data[7] = 1.0F;
                data[8] = 1.0F;
                break;

            case 7:
                data[0] = 1.0F;
                data[1] = 2.0F;
                data[2] = 1.0F;
                data[3] = 2.0F;
                data[4] = 4.0F;
                data[5] = 2.0F;
                data[6] = 1.0F;
                data[7] = 2.0F;
                data[8] = 1.0F;
                break;

            case 8:
                data[0] = -1.0F;
                data[1] = -2.0F;
                data[2] = 0.0F;
                data[3] = -2.0F;
                data[4] = 0.0F;
                data[5] = 2.0F;
                data[6] = 0.0F;
                data[7] = 2.0F;
                data[8] = 1.0F;
                break;

            case 9:
                // get text for custom kernel
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        try {
                            data[3 * i + j] = Float.parseFloat(krn[i][j].getText());
                        } catch (NumberFormatException e) {
                            data[3 * i + j] = 0.0F;
                        }
                    }
                }
                break;
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                krn[i][j].setText("" + data[3 * i + j]);
            }
        }

        normalize(data);
        kernel = new KernelImageN(3, 3, data);
    }

    public void normalize(float[] data) {
        sum = 0.0F;

        for (int i = 0; i < data.length; i++) {
            sum += data[i];
        }

        if (sum > 0.0F) {
            for (int i = 0; i < data.length; i++) {
                data[i] = data[i] / sum;
            }
        } else {
            sum = 1.0F;
        }

        textline.setText("Kernel coefficients are divided by " + sum);
    }
}
