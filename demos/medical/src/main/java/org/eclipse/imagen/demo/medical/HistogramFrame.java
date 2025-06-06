/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 *
 * This Example Content is intended to demonstrate usage of Eclipse technology. It is
 * provided to you under the terms and conditions of the Eclipse Distribution License
 * v1.0 which is available at http://www.eclipse.org/org/documents/edl-v10.php.
 */
package org.eclipse.imagen.demo.medical;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import org.eclipse.imagen.Histogram;

/**
 * This class is defined to display the statistics or the histogram in a <code>JFrame</code>.
 *
 * <p>When the histogram is displayed, the color of the bar for each bin is corresponding to the color of the pixels
 * whose values fall into this bin.
 *
 * <p>When the statistic parameters are displayed, the following parameters of a rectangular ROI are listed in a table:
 * the area, the width, the height, the maximum, the minimum, the mean, the standard deviation and the entropy of the
 * values of the pixels located in this ROI.
 */
public class HistogramFrame extends JFrame {
    /**
     * The LUT (look-up-table) to map the 12-bit image into 8-bit image. Used to define the color of the bar for each
     * bin of the histogram when display.
     */
    private byte[][] lut;

    /** The histogram to be displayed or to be used to calculate the statistics. */
    private Histogram histogram;

    /**
     * The ROI on which the histogram or the statistics is calculated. It is defined in the physical coordinate system.
     */
    private Area roi;

    /**
     * Constructor.
     *
     * <p>It accepts four parameters as defined below:
     *
     * @param h The histogram object to display or to calculate the statistics.
     * @param displayHistogram Indicates to display histogram or statistics.
     * @param lut The lut for window/level operation. Used to define the color of each bin of the histogram when
     *     display.
     * @param roi The ROI in the original image coordinate system.
     */
    public HistogramFrame(Histogram h, boolean displayHistogram, byte[][] lut, Shape roi) {
        this.lut = lut;
        this.histogram = h;
        this.roi = new Area(roi);

        if (displayHistogram) {
            // display histogram
            this.setTitle("Histogram");
            ImagePane pane = new ImagePane(displayHistogram(h), "Histogram");
            pane.setDisplayCenter(false);
            this.getContentPane().add(pane);
        } else {
            // display statistics
            this.setTitle("Statistics");
            JTable table = getStatistics(h);
            JScrollPane scrollpane = new JScrollPane(table);
            this.getContentPane().add(scrollpane);
        }
    }

    /**
     * Create the table of the statistic parameters. Currently, the following parameters are calculated and displayed:
     * the area, the width, the height, the maximum, the minimum, the mean, the standard deviation and the entropy of
     * the values of the pixels located in the ROI.
     */
    private JTable getStatistics(Histogram h) {
        double[] mean = h.getMean();
        mean[0] = ((int) (mean[0] * 10)) / 10.0;
        int[] minValue = getMinValue();
        int[] maxValue = getMaxValue();
        double[] stdev = h.getStandardDeviation();
        stdev[0] = ((int) (stdev[0] * 10)) / 10.0;

        double[] entropy = h.getEntropy();
        entropy[0] = ((int) (entropy[0] * 10)) / 10.0;

        String[] heading = new String[] {"Parameter", "Value"};
        String[][] content = new String[][] {
            {"Area (mm2)", "" + getArea(roi)},
            {"Width (mm)", "" + getWidth(roi)},
            {"Height (mm)", "" + getHeight(roi)},
            {"Max", "" + maxValue[0]},
            {"Min", "" + minValue[0]},
            {"Mean", "" + mean[0]},
            {"StDev", "" + stdev[0]},
            {"Entropy", "" + entropy[0]}
        };

        return new JTable(content, heading);
    }

    /** Calculate the area of the ROI, in <code>mm<sup>2</sup></code>, based on the path iterator. */
    private double getArea(Area roi) {
        PathIterator path = roi.getPathIterator(new AffineTransform(), 0.1);
        double[] coordinates = new double[6];
        double x1 = 0.0, y1 = 0.0;
        double area = 0.0;

        while (path.isDone() == false) {
            int type = path.currentSegment(coordinates);
            switch (type) {
                case PathIterator.SEG_MOVETO:
                    x1 = coordinates[0];
                    y1 = coordinates[1];
                    break;

                case PathIterator.SEG_LINETO:
                    area += (coordinates[1] - y1) * (x1 + coordinates[0]) / 2.0;
                    x1 = coordinates[0];
                    y1 = coordinates[1];
                    break;
            }
            path.next();
        }
        area = Math.abs(((int) (area * 10)) / 10.0);
        return area;
    }

    /** Compute the X-direction extension of the ROI, in <code>mm</code>. */
    private double getWidth(Area roi) {
        PathIterator path = roi.getPathIterator(new AffineTransform(), 0.1);
        double[] coordinates = new double[6];
        double minX = Double.MAX_VALUE, maxX = Double.MIN_VALUE;

        while (path.isDone() == false) {
            int type = path.currentSegment(coordinates);
            if (coordinates[0] > maxX) maxX = coordinates[0];
            if (coordinates[0] < minX) minX = coordinates[0];
            path.next();
        }
        return ((int) Math.abs(maxX - minX) * 10) / 10.0;
    }

    /** Compute the Y-direction extension of the ROI, in <code>mm</code>. */
    private double getHeight(Area roi) {
        PathIterator path = roi.getPathIterator(new AffineTransform(), 0.1);
        double[] coordinates = new double[6];
        double minY = Double.MAX_VALUE, maxY = Double.MIN_VALUE;

        while (path.isDone() == false) {
            int type = path.currentSegment(coordinates);
            if (coordinates[1] > maxY) maxY = coordinates[1];
            if (coordinates[1] < minY) minY = coordinates[1];
            path.next();
        }
        return ((int) Math.abs(maxY - minY) * 10) / 10.0;
    }

    /** Return the minimum pixel value in the provided ROI. */
    private int[] getMinValue() {
        int numBands = histogram.getNumBands();
        int numBins = histogram.getNumBins(0);
        int[] minValue = new int[numBands];
        double[] lowValue = histogram.getLowValue();
        double[] highValue = histogram.getHighValue();

        for (int b = 0; b < numBands; b++) {
            int[] bins = histogram.getBins(b);
            for (int i = 0; i < numBins; i++) {
                if (bins[i] > 0) {
                    minValue[b] = (int) (i * (highValue[b] - lowValue[b]) / numBins + lowValue[b]);
                    break;
                }
            }
        }

        return minValue;
    }

    /** Return the maximum pixel value of the provided ROI. */
    private int[] getMaxValue() {
        int numBands = histogram.getNumBands();
        int numBins = histogram.getNumBins(0);
        int[] maxValue = new int[numBands];
        double[] lowValue = histogram.getLowValue();
        double[] highValue = histogram.getHighValue();

        for (int b = 0; b < numBands; b++) {
            int[] bins = histogram.getBins(b);
            for (int i = numBins - 1; i >= 0; i--) {
                if (bins[i] > 0) {
                    maxValue[b] = (int) (i * (highValue[b] - lowValue[b]) / numBins + lowValue[b]);
                    break;
                }
            }
        }

        return maxValue;
    }

    /**
     * Draw the histogram in a RenderedImage for display. The color of the bar for each bin is defined by the provided
     * LUT.
     *
     * @throws RuntimeException When the bands have different number of bins.
     */
    private RenderedImage displayHistogram(Histogram h) {
        int numBands = h.getNumBands();
        int numBins = h.getNumBins(0);
        for (int b = 1; b < numBands; b++) {
            if (h.getNumBins(b) != numBins) {
                throw new RuntimeException("All bands must have same numBins.");
            }
        }

        // compute the maximum count
        double maxCount = 0;
        for (int b = 0; b < numBands; b++) {
            int[] bins = h.getBins(b);
            for (int i = 0; i < numBins; i++) {
                if (bins[i] > maxCount) {
                    maxCount = bins[i];
                }
            }
        }

        // define the size of the image and create the image
        int width = 2 * numBins + 70;
        int height = numBins + 30;
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);

        // set background color
        Graphics2D g = bi.createGraphics();
        int maxVal = numBins - 1;
        g.setColor(new Color(10, 128, 128));
        g.fillRect(0, 0, width, height);

        // draw the bars for all the bins
        for (int b = 0; b < numBands; b++) {
            int[] bins = h.getBins(b);
            for (int x2 = 0; x2 < numBins; x2++) {
                int gray = lut[0][(int) (x2 * h.getHighValue()[0] / numBins)] & 0xff;
                if (gray > 255) gray = 255;
                g.setColor(new Color(gray, gray, gray));
                int y2 = maxVal - (int) ((maxVal - 1) * bins[x2] / maxCount + 0.5);
                g.fillRect(x2 * 2 + 40, y2, 2, maxVal);
            }
        }

        // set the area for the rulers to the background color
        g.setColor(new Color(10, 128, 128));
        g.fillRect(0, maxVal + 1, width, height);

        // draw the coordinate axes
        g.setColor(Color.green);
        g.drawLine(40, 0, 40, maxVal);
        g.drawLine(35, 0, 40, 0);
        g.drawLine(35, maxVal, 40, maxVal);
        g.drawString("0", 10, maxVal);
        g.drawString("" + ((int) maxCount), 0, 10);

        // draw the ticks and the labels
        g.drawLine(40, maxVal, width - 30, maxVal);
        for (int i = 0; i <= 8; i++) {
            int x = 40 + i * numBins / 4;
            g.drawLine(x, maxVal, x, maxVal + 5);
            String label = "" + (i * ((int) h.getHighValue()[0]) / 8);
            g.drawString(label, x - label.length() * 3, maxVal + 18);
        }

        g.dispose();

        return bi;
    }
}
