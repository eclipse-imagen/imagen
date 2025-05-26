/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 *
 * This Example Content is intended to demonstrate usage of Eclipse technology. It is
 * provided to you under the terms and conditions of the Eclipse Distribution License
 * v1.0 which is available at http://www.eclipse.org/org/documents/edl-v10.php.
 */
package org.eclipse.imagen.tutorial;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.*;
import java.awt.image.renderable.ParameterBlock;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import org.eclipse.imagen.Histogram;
import org.eclipse.imagen.JAI;
import org.eclipse.imagen.LookupTableJAI;
import org.eclipse.imagen.PlanarImage;
import org.eclipse.imagen.RenderedOp;
import org.eclipse.imagen.widgets.*;

public class HistogramDemo extends JPanel implements ActionListener {

    private PlanarImage source = null;
    private PlanarImage target = null;
    private Panner panner;
    private JButton reset;
    private JButton equal;
    private JButton norm;
    private JButton piece;
    private ImageDisplay canvas;
    private XYPlot graph;

    public HistogramDemo(String filename) {
        File f = new File(filename);

        if (f.exists() && f.canRead()) {
            source = JAI.create("fileload", filename);
        } else {
            return;
        }

        canvas = new ImageDisplay(source);
        canvas.setLayout(new FlowLayout(FlowLayout.RIGHT, 2, 2));

        panner = new Panner(canvas, source, 128);
        panner.setBackground(Color.red);
        panner.setBorder(new EtchedBorder());
        canvas.add(panner);

        Font font = new Font("SansSerif", Font.BOLD, 12);
        JLabel title = new JLabel(" Histogram");
        title.setFont(font);
        title.setLocation(0, 32);

        setOpaque(true);
        setLayout(new BorderLayout());
        setBackground(Color.white);

        graph = new XYPlot();
        graph.setBackground(Color.black);
        graph.setBorder(new LineBorder(new Color(0, 0, 255), 1));

        Colorbar cbar = new Colorbar();
        cbar.setBackground(Color.black);
        cbar.setPreferredSize(new Dimension(256, 25));
        cbar.setBorder(new LineBorder(new Color(255, 0, 255), 2));

        JPanel hist_panel = new JPanel();
        hist_panel.setLayout(new BorderLayout());
        hist_panel.setBackground(Color.white);
        hist_panel.add(graph, BorderLayout.CENTER);
        hist_panel.add(cbar, BorderLayout.SOUTH);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1, 5, 5));
        panel.setBackground(Color.white);
        panel.add(canvas);
        panel.add(hist_panel);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        reset = new JButton("Reset");
        equal = new JButton("Equalize");
        norm = new JButton("Normalize");
        piece = new JButton("Piecewise");

        reset.addActionListener(this);
        equal.addActionListener(this);
        norm.addActionListener(this);
        piece.addActionListener(this);

        controlPanel.add(reset);
        controlPanel.add(equal);
        controlPanel.add(norm);
        controlPanel.add(piece);

        add(title, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        // original histogram (remains unmodified)
        graph.plot(getHistogram(source));
    }

    public int[] getHistogram(PlanarImage image) {
        // set up the histogram
        int[] bins = {256};
        double[] low = {0.0D};
        double[] high = {256.0D};

        ParameterBlock pb = new ParameterBlock();
        pb.addSource(image);
        pb.add(null);
        pb.add(1);
        pb.add(1);
        pb.add(bins);
        pb.add(low);
        pb.add(high);

        RenderedOp op = JAI.create("histogram", pb, null);
        Histogram histogram = (Histogram) op.getProperty("histogram");

        // get histogram contents
        int[] local_array = new int[histogram.getNumBins(0)];
        for (int i = 0; i < histogram.getNumBins(0); i++) {
            local_array[i] = histogram.getBinSize(0, i);
        }

        return local_array;
    }

    // one way to do this (old style)
    // this could also be done with matchcdf
    public PlanarImage equalize() {
        int sum = 0;
        byte[] cumulative = new byte[256];
        int array[] = getHistogram(source);

        float scale = 255.0F / (float) (source.getWidth() * source.getHeight());

        for (int i = 0; i < 256; i++) {
            sum += array[i];
            cumulative[i] = (byte) ((sum * scale) + .5F);
        }

        LookupTableJAI lookup = new LookupTableJAI(cumulative);

        ParameterBlock pb = new ParameterBlock();
        pb.addSource(source);
        pb.add(lookup);

        return JAI.create("lookup", pb, null);
    }

    // for a single band
    public PlanarImage normalize() {
        double[] mean = new double[] {128.0};
        double[] stDev = new double[] {34.0};
        float[][] CDFnorm = new float[1][];
        CDFnorm[0] = new float[256];
        double mu = mean[0];
        double twoSigmaSquared = 2.0 * stDev[0] * stDev[0];
        CDFnorm[0][0] = (float) Math.exp(-mu * mu / twoSigmaSquared);

        for (int i = 1; i < 256; i++) {
            double deviation = i - mu;
            CDFnorm[0][i] = CDFnorm[0][i - 1] + (float) Math.exp(-deviation * deviation / twoSigmaSquared);
        }

        double CDFnormLast = CDFnorm[0][255];
        for (int i = 0; i < 256; i++) {
            CDFnorm[0][i] /= CDFnormLast;
        }

        int[] bins = {256};
        double[] low = {0.0D};
        double[] high = {256.0D};

        ParameterBlock pb = new ParameterBlock();
        pb.addSource(source);
        pb.add(null);
        pb.add(1);
        pb.add(1);
        pb.add(bins);
        pb.add(low);
        pb.add(high);

        RenderedOp fmt = JAI.create("histogram", pb, null);

        return JAI.create("matchcdf", fmt, CDFnorm);
    }

    public PlanarImage piecewise() {
        float[][][] bp = new float[1][2][];
        bp[0][0] = new float[] {0.0F, 32.0F, 64.0F, 255.0F};
        bp[0][1] = new float[] {0.0F, 128.0F, 112.0F, 255.0F};

        return JAI.create("piecewise", source, bp);
    }

    public void actionPerformed(ActionEvent e) {
        JButton b = (JButton) e.getSource();

        if (b == reset) {
            target = source;
        } else if (b == equal) {
            target = equalize();
        } else if (b == norm) {
            target = normalize();
        } else if (b == piece) {
            target = piecewise();
        }

        canvas.set(target);
        graph.plot(getHistogram(target));
    }
}
