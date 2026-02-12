/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 *
 * This Example Content is intended to demonstrate usage of Eclipse technology. It is
 * provided to you under the terms and conditions of the Eclipse Distribution License
 * v1.0 which is available at http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */
package org.eclipse.imagen.demo;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.renderable.ParameterBlock;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.*;
import org.eclipse.imagen.*;

class SourcePanel extends JPanel implements ListSelectionListener {

    JAIDemo demo;
    PlanarImage[] sources;
    int sourceNum;

    JLabel picture;
    Icon imageIcon;
    JList listOfImages;

    public SourcePanel(JAIDemo demo, String[] filenames, PlanarImage[] sources, int sourceNum) {
        this.demo = demo;
        this.sources = sources;
        this.sourceNum = sourceNum;

        Vector filenameList = new Vector();
        for (int i = 0; i < filenames.length; i++) {
            filenameList.add(filenames[i]);
        }

        // Create the list
        listOfImages = new JList(filenameList);
        listOfImages.setSelectedIndex(0);
        listOfImages.addListSelectionListener(this);

        // Put it in a scroll pane
        JScrollPane scrollPane = new JScrollPane(listOfImages);

        // Set up the picture label
        imageIcon = makeIcon(0);
        picture = new JLabel(imageIcon);
        Dimension size = new Dimension(imageIcon.getIconWidth(), imageIcon.getIconHeight());
        picture.setPreferredSize(size);

        // Put it in a scroll pane
        JScrollPane pictureScrollPane = new JScrollPane(
                picture,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(scrollPane);
        add(Box.createHorizontalStrut(10));

        add(pictureScrollPane);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    public void setListOfImages(int index) {
        listOfImages.setSelectedIndex(0);
    }

    public Icon makeIcon(int index) {
        float scale = 190.0F / Math.max(sources[index].getWidth(), sources[index].getHeight());
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(sources[index]);
        pb.add(scale);
        pb.add(scale);
        pb.add(0.0F);
        pb.add(0.0F);
        pb.add(new InterpolationNearest());

        PlanarImage scaled = ImageN.create("scale", pb, null);
        return new IconJAI(scaled);
    }

    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {

            JList theList = (JList) e.getSource();
            int index = theList.getSelectedIndex();

            imageIcon = makeIcon(index);
            picture.setIcon(imageIcon);
            Dimension size = new Dimension(imageIcon.getIconWidth(), imageIcon.getIconHeight());
            picture.setPreferredSize(size);
            picture.revalidate();
            demo.setSourceIndex(sourceNum, index);
        }
    }
}

class SourceAdj extends JAISourceAdjPanel implements ChangeListener {

    float xscale = 1.0F;
    float yscale = 1.0F;
    float xtrans = 0.0F;
    float ytrans = 0.0F;
    Interpolation interp;
    int srcId;
    JSlider xySlider;
    JSlider xTSlider;
    JSlider yTSlider;
    Vector sourceVec;
    JAIDemo demo;

    public SourceAdj(JAIDemo demo, int srcId, Vector sourceVec) {
        super(sourceVec);
        this.srcId = srcId;
        this.sourceVec = sourceVec;
        this.demo = demo;
        masterSetup();
    }

    public PlanarImage process() {

        PlanarImage im0 = getSource(srcId);

        ParameterBlock pb = new ParameterBlock();
        pb.addSource(im0);
        pb.add(xscale);
        pb.add(yscale);
        pb.add(xtrans);
        pb.add(ytrans);
        interp = Interpolation.getInstance(Interpolation.INTERP_NEAREST);
        pb.add(interp);

        PlanarImage img1 = ImageN.create("scale", pb, renderHints);
        demo.setDyadicSource(img1, srcId);
        return img1;
    }

    public void reset() {
        xscale = yscale = 1.0F;
        interp = Interpolation.getInstance(Interpolation.INTERP_NEAREST);
        xySlider.setValue(0);
        xTSlider.setValue(0);
        yTSlider.setValue(0);
    }

    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();
        if (!(interp instanceof InterpolationNearest) && source.getValueIsAdjusting()) {
            return;
        }
        int value = source.getValue();

        float fvalue = Math.abs(value) / 10.0F + 1.0F;
        if (value < 0) {
            fvalue = 1.0F / fvalue;
        }

        if (source == xySlider) {
            xySlider.setValue(value);
            xscale = yscale = fvalue;

        } else if (source == xTSlider) {
            xtrans = value;

        } else if (source == yTSlider) {
            ytrans = value;
        }
        repaint();
    }

    public void makeControls(JPanel controls) {

        xySlider = new JSlider(JSlider.HORIZONTAL, -40, 90, 0);
        xTSlider = new JSlider(JSlider.HORIZONTAL, -99, 99, 0);
        yTSlider = new JSlider(JSlider.HORIZONTAL, -99, 99, 0);

        Hashtable labels = new Hashtable();
        labels.put(new Integer(-40), new JLabel("1/5"));
        labels.put(new Integer(-30), new JLabel("1/4"));
        labels.put(new Integer(-20), new JLabel("1/3"));
        labels.put(new Integer(-10), new JLabel("1/2"));
        labels.put(new Integer(0), new JLabel("1"));
        labels.put(new Integer(10), new JLabel("2"));
        labels.put(new Integer(20), new JLabel("3"));
        labels.put(new Integer(30), new JLabel("4"));
        labels.put(new Integer(40), new JLabel("5"));
        labels.put(new Integer(50), new JLabel("6"));
        labels.put(new Integer(60), new JLabel("7"));
        labels.put(new Integer(70), new JLabel("8"));
        labels.put(new Integer(80), new JLabel("9"));
        labels.put(new Integer(90), new JLabel("10"));
        xySlider.setLabelTable(labels);
        xySlider.setPaintLabels(true);

        Hashtable labels1 = new Hashtable();

        labels1.put(new Integer(-100), new JLabel("-100"));
        labels1.put(new Integer(-75), new JLabel("-75"));
        labels1.put(new Integer(-50), new JLabel("-50"));
        labels1.put(new Integer(-25), new JLabel("-25"));
        labels1.put(new Integer(-10), new JLabel("-10"));
        labels1.put(new Integer(0), new JLabel("0"));
        labels1.put(new Integer(10), new JLabel("10"));
        labels1.put(new Integer(25), new JLabel("25"));
        labels1.put(new Integer(50), new JLabel("50"));
        labels1.put(new Integer(75), new JLabel("75"));
        labels1.put(new Integer(100), new JLabel("100"));

        xTSlider.setLabelTable(labels1);
        xTSlider.setPaintLabels(true);

        yTSlider.setLabelTable(labels1);
        yTSlider.setPaintLabels(true);

        JPanel xySliderPanel = new JPanel();
        xySliderPanel.setLayout(new GridLayout(0, 1));

        JLabel xyLabel = new JLabel("X/Y Scale", (int) Component.CENTER_ALIGNMENT);
        xySliderPanel.add(xyLabel);
        xySliderPanel.add(xySlider);

        JPanel xTSliderPanel = new JPanel();
        xTSliderPanel.setLayout(new GridLayout(0, 1));

        JLabel xTLabel = new JLabel("X Translate", (int) Component.CENTER_ALIGNMENT);
        xTLabel.setPreferredSize(xyLabel.getPreferredSize());
        xTSliderPanel.add(xTLabel);
        xTSliderPanel.add(xTSlider);

        JPanel yTSliderPanel = new JPanel();
        yTSliderPanel.setLayout(new GridLayout(0, 1));

        JLabel yTLabel = new JLabel("Y Translate", (int) Component.CENTER_ALIGNMENT);
        yTLabel.setPreferredSize(xyLabel.getPreferredSize());
        yTSliderPanel.add(yTLabel);
        yTSliderPanel.add(yTSlider);

        xySlider.addChangeListener(this);
        xTSlider.addChangeListener(this);
        yTSlider.addChangeListener(this);
        JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.Y_AXIS));

        sliderPanel.add(xySliderPanel);
        sliderPanel.add(xTSliderPanel);
        sliderPanel.add(yTSliderPanel);
        sliderPanel.setLayout(new GridLayout(0, 1));
        controls.setLayout(new BorderLayout());
        controls.add("Center", sliderPanel);
    }
}

class CachePanel extends JPanel implements ActionListener, ChangeListener {

    JAIDemo demo;

    private static final int TILE_CACHE_MBYTES_PER_TICK = 4;
    private static final int TILE_CACHE_BYTES_PER_TICK = TILE_CACHE_MBYTES_PER_TICK * 1024 * 1024;

    public CachePanel(JAIDemo demo) {
        this.demo = demo;

        setLayout(new GridLayout(4, 1));

        add(new JLabel(""));

        JButton flushButton = new JButton("Flush Tile Cache");
        add(flushButton);
        flushButton.addActionListener(this);

        JLabel capacityLabel = new JLabel("Tile Cache Capacity", SwingConstants.CENTER);
        add(capacityLabel);

        long cacheCapacity = ImageN.getDefaultInstance().getTileCache().getMemoryCapacity();
        int capacity = (int) (cacheCapacity / TILE_CACHE_BYTES_PER_TICK);
        JSlider capacitySlider = new JSlider(JSlider.HORIZONTAL, 0, 16, capacity);
        Hashtable labels = new Hashtable();
        for (int i = 0; i <= 16; i += 4) {
            labels.put(new Integer(i), new JLabel((i * TILE_CACHE_MBYTES_PER_TICK) + "M"));
        }
        capacitySlider.setLabelTable(labels);
        capacitySlider.setPaintLabels(true);
        capacitySlider.setMajorTickSpacing(2);
        capacitySlider.setMinorTickSpacing(1);
        capacitySlider.setPaintTicks(true);
        capacitySlider.setSnapToTicks(true);

        add(capacitySlider);
        capacitySlider.addChangeListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        ImageN.getDefaultInstance().getTileCache().flush();
    }

    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();
        if (source.getValueIsAdjusting()) {
            return;
        }

        long capacity = source.getValue() * TILE_CACHE_BYTES_PER_TICK;
        ImageN.getDefaultInstance().getTileCache().setMemoryCapacity(capacity);
    }
}

class HintPanel extends JPanel implements ItemListener {

    static final String[] extenderLabels = {
        "No Border Extension",
        "Extend borders with zeros",
        "Extend borders by copying edges",
        "Extend borders by reflection",
        "Extend borders by wrapping"
    };

    static final BorderExtender[] extenders = {
        null,
        BorderExtender.createInstance(BorderExtender.BORDER_ZERO),
        BorderExtender.createInstance(BorderExtender.BORDER_COPY),
        BorderExtender.createInstance(BorderExtender.BORDER_REFLECT),
        BorderExtender.createInstance(BorderExtender.BORDER_WRAP)
    };

    static final RenderingHints.Key extenderKey = ImageN.KEY_BORDER_EXTENDER;

    JAIDemo demo;
    RenderingHints renderHints = null;

    public HintPanel(JAIDemo demo) {
        this.demo = demo;

        JComboBox extenderBox = new JComboBox();
        for (int i = 0; i < extenderLabels.length; i++) {
            extenderBox.addItem(extenderLabels[i]);
        }
        extenderBox.addItemListener(this);

        add(extenderBox);
    }

    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.DESELECTED) {
            return;
        }

        String item = (String) e.getItem();
        for (int i = 0; i < extenderLabels.length; i++) {
            if (item.equals(extenderLabels[i])) {
                if (i == 0) {
                    renderHints = null;
                } else {
                    renderHints = new RenderingHints(extenderKey, extenders[i]);
                }
            }
        }

        demo.setRenderingHints(renderHints);
    }
}

public class JAIDemo extends WindowAdapter implements ChangeListener {

    JFrame frame;
    JAIDemoPanel currPanel = null;
    JAIDemoPanel[] panels;
    JAISourceAdjPanel srcAdjPanel, srcAdjPanel0;
    SourcePanel sourcePanel, sourcePanel1;

    int numPanels;
    int numMonadicPanels;

    JTabbedPane srcTabbedPane;
    JTabbedPane mainTabbedPane;
    JTabbedPane dyadicTabbedPane;
    PlanarImage[] sources;
    RenderingHints renderHints = null;

    public JAIDemo(String[] args) {
        // Create a top-level frame to hold the demo
        frame = new JFrame("ImageN Demo");

        // Read in the images
        sources = new PlanarImage[args.length];
        for (int i = 0; i < args.length; i++) {
            sources[i] = JAIImageReader.readImage(args[i]);
        }

        Vector sourceVec = new Vector();
        sourceVec.add(sources[0]);
        sourceVec.add(sources[0]);

        // Create a panel for tile cache control.
        JPanel cachePanel = new CachePanel(this);

        JPanel hintPanel = new HintPanel(this);

        JPanel extraPanel = new JPanel();
        extraPanel.setLayout(new GridLayout(2, 1));
        extraPanel.add(cachePanel);
        extraPanel.add(hintPanel);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BorderLayout());

        controlPanel.add("East", extraPanel);

        frame.getContentPane().add("North", controlPanel);

        // Instantiate panels for each operation to be demonstrated.
        // New operation panels should be added here.
        numPanels = 0;
        panels = new JAIDemoPanel[16];
        panels[numPanels++] = new JAIScalePanel(sourceVec);
        panels[numPanels++] = new JAIRotatePanel(sourceVec);
        panels[numPanels++] = new JAIConvolvePanel(sourceVec);
        panels[numPanels++] = new JAIPatternPanel(sourceVec);
        panels[numPanels++] = new JAIAddConstPanel(sourceVec);
        panels[numPanels++] = new JAITransposePanel(sourceVec);
        panels[numPanels++] = new JAIBlurSharpPanel(sourceVec);
        panels[numPanels++] = new JAIGradientPanel(sourceVec);
        panels[numPanels++] = new JAIMedianPanel(sourceVec);
        panels[numPanels++] = new JAISamplePanel(sourceVec);
        currPanel = panels[0];

        // Dyadic ops
        numMonadicPanels = numPanels;
        panels[numPanels++] = new JAIDyadicSource0Panel(this, sourceVec);
        panels[numPanels++] = new JAIDyadicSource1Panel(this, sourceVec);
        panels[numPanels++] = new JAIDyadicAddPanel(this, sourceVec);
        panels[numPanels++] = new JAIDyadicSubtractPanel(this, sourceVec);
        panels[numPanels++] = new JAIDyadicMultiplyPanel(this, sourceVec);
        panels[numPanels++] = new JAIDyadicDividePanel(this, sourceVec);

        // Create a panel for source selection.
        sourcePanel = new SourcePanel(this, args, sources, 0);
        sourcePanel1 = new SourcePanel(this, args, sources, 1);
        srcAdjPanel0 = new SourceAdj(this, 0, sourceVec);
        srcAdjPanel = new SourceAdj(this, 1, sourceVec);

        srcTabbedPane = new JTabbedPane();
        srcTabbedPane.add("Source0", sourcePanel);
        srcTabbedPane.add("Source1", sourcePanel1);
        srcTabbedPane.add("Source0 Adj", srcAdjPanel0);
        srcTabbedPane.add("Source1 Adj", srcAdjPanel);
        srcTabbedPane.setEnabledAt(1, false);
        srcTabbedPane.setEnabledAt(2, false);
        srcTabbedPane.setEnabledAt(3, false);

        srcTabbedPane.setPreferredSize(new Dimension(600, 300));
        srcTabbedPane.addChangeListener(this);
        controlPanel.add("Center", srcTabbedPane);

        // Insert the operation panels into a tabbed pane.
        mainTabbedPane = new JTabbedPane();
        for (int i = 0; i < numMonadicPanels; i++) {
            mainTabbedPane.add(panels[i].getDemoName(), panels[i]);
        }
        dyadicTabbedPane = new JTabbedPane();
        for (int i = numMonadicPanels; i < numPanels; i++) {
            dyadicTabbedPane.add(panels[i].getDemoName(), panels[i]);
        }

        mainTabbedPane.add("Dyadic", dyadicTabbedPane);
        mainTabbedPane.setPreferredSize(new Dimension(800, 450));
        frame.getContentPane().add("Center", mainTabbedPane);
        mainTabbedPane.addChangeListener(this);
        dyadicTabbedPane.addChangeListener(this);

        // Pack the frame and make it visible.
        frame.pack();
        frame.setVisible(true);
        frame.addWindowListener(this);
    }

    public void stateChanged(ChangeEvent e) {
        JTabbedPane pane = (JTabbedPane) e.getSource();
        int index = pane.getSelectedIndex();

        if (pane == mainTabbedPane) {
            currPanel.deactivate();
            enableSource1(false);
            if (index >= numMonadicPanels) {
                currPanel = panels[dyadicTabbedPane.getSelectedIndex() + numMonadicPanels];
                enableSource1(true);
            } else {
                currPanel = panels[index];
            }
            currPanel.activate();
        } else if (pane == dyadicTabbedPane) {
            currPanel.deactivate();
            currPanel = panels[index + numMonadicPanels];
            currPanel.activate();
        }
    }

    /** Sets the index of one of the sources in response to an event in one of the source selector panels. */
    public void setSourceIndex(int sourceNum, int index) {
        for (int i = 0; i < numPanels; i++) {
            panels[i].setSource(sourceNum, sources[index]);
        }
        srcAdjPanel.setSource(sourceNum, sources[index]);
        srcAdjPanel0.setSource(sourceNum, sources[index]);
    }

    public void setDyadicSource(PlanarImage img, int index) {
        for (int i = numMonadicPanels; i < numPanels; i++) {
            panels[i].setSource(index, img);
        }
    }

    public void resetAdj() {
        srcAdjPanel0.reset();
        srcAdjPanel.reset();
    }

    public void enableSource1(boolean isSet) {
        srcTabbedPane.setEnabledAt(1, isSet);
        srcTabbedPane.setEnabledAt(2, isSet);
        srcTabbedPane.setEnabledAt(3, isSet);
        srcTabbedPane.setSelectedIndex(0);
    }

    public void setRenderingHints(RenderingHints renderHints) {
        this.renderHints = renderHints;
        for (int i = 0; i < numPanels; i++) {
            panels[i].setRenderingHints(renderHints);
        }
    }

    public void windowClosing(WindowEvent e) {
        System.exit(0);
    }

    public static void main(String[] args) {
        ImageN.getDefaultInstance().setRenderingHint(ImageN.KEY_CACHED_TILE_RECYCLING_ENABLED, Boolean.TRUE);
        new JAIDemo(args);
    }
}
