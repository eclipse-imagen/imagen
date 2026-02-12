/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 *
 * This Example Content is intended to demonstrate usage of Eclipse technology. It is
 * provided to you under the terms and conditions of the Eclipse Distribution License
 * v1.0 which is available at http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */
package org.eclipse.imagen.demo.medical;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.border.Border;
import javax.swing.event.ChangeListener;

/**
 * This class defines the utility menu of this medical image application. After a data set is loaded, the utility menu
 * is created and placed at the right side of the <code>JFrame</code>. The memu includes a title and six sub-menus. They
 * are located in a rectangle area in a <code>GridBagLayout</code>. The user can adjust the position of the title and
 * sub-menus by modifying the parameters defined in the property file.
 *
 * <p>The first sub-menu is "General" sub-menu: the user can change the layout of of the displayed images and the
 * operation scope: process-all or process-current.
 *
 * <p>The second one is "Transform" sub-menu: Two slider bars are defined there to allow the users to change the zoom
 * factor and the rotation angle.
 *
 * <p>The third one is for starting/stoping the cining-loop. Also, the users can change the frame-rate of the cining
 * loop. The frame rate is defined in frames-per-second.
 *
 * <p>The fourth one is for window/level: Two slider bars are defined there to allow the users to change the
 * window/level values. The maximum of these slider bars are adjusted based on the maximum pixel value of the loaded
 * data set.
 *
 * <p>The fifth sub-menu is for annotation and measurement. And the last one is for displaying the histogram or
 * statistics of a rectangular ROI.
 */
public class UtilityMenuPane extends JPanel implements MedicalAppConstants, ActionListener {

    /** The single instance of this class */
    private static UtilityMenuPane instance;

    /** Cache the command center class */
    private static MedicalAppState medicalAppState;

    /** The user interface components. */
    private JComboBox layoutList;

    private JToggleButton annotateButton;
    private JToggleButton measurementButton;
    private JToggleButton statisticsButton;
    private JToggleButton histogramButton;
    private JToggleButton[] tiedButtons;

    private JRadioButton allViewsButton;
    private JRadioButton currentViewButton;
    private JRadioButton cineStartButton;
    private JRadioButton cineStopButton;

    private JTextField speedInput;

    private JSlider zoomSlider;
    private JSlider rotationSlider;
    private JSlider windowSlider;
    private JSlider levelSlider;

    /** The slider table. */
    private Hashtable sliderTable = new Hashtable();

    /** Singleton pattern. */
    public static synchronized UtilityMenuPane getInstance() {
        if (instance == null) instance = new UtilityMenuPane();
        return instance;
    }

    /** Private method of the singleton. */
    private UtilityMenuPane() {
        super(new GridBagLayout());
        medicalAppState = MedicalAppState.getInstance();
        setLayout(new BorderLayout());
        JPanel container = new JPanel(new GridBagLayout());
        addUtilities(container);
        add(container, BorderLayout.NORTH);
        tiedButtons = new JToggleButton[] {annotateButton, measurementButton, statisticsButton, histogramButton};

        for (int i = 0; i < tiedButtons.length; i++) tiedButtons[i].addActionListener(this);
    }

    /** Return the slider table. */
    public Hashtable getSliderTable() {
        return sliderTable;
    }

    /** Update the display of the window, level, zoom and rotation sliders. */
    public void updateSliders(int zoom, int rotation, int window, int level) {
        zoomSlider.setValue(zoom);
        rotationSlider.setValue(rotation);
        windowSlider.setValue(window);
        levelSlider.setValue(level);
    }

    /** Initialize the state of the utility menu. */
    public void initUtilityMenu() {
        setComponentsEnabled(true);
        allViewsButton.setSelected(false);
        currentViewButton.setSelected(true);
        updateSliders(nozoom * 10, 0, defaultWindow, defaultLevel);
        speedInput.setText(JaiI18N.getString("DefaultCineSpeed"));
        cineStartButton.setSelected(false);
        cineStopButton.setSelected(true);
        annotateButton.setSelected(false);
        measurementButton.setSelected(false);
        statisticsButton.setSelected(false);
        histogramButton.setSelected(false);
    }

    /** Set the maximum for window/level based on the current loaded data set. */
    public void setMaxWindowLevel(int max) {
        windowSlider.setMaximum(max);
        levelSlider.setMaximum(max);
    }

    /**
     * Process the action event. Enable/disable the components when start or stop the cining. Synchronize the toggle
     * buttons.
     */
    public void actionPerformed(ActionEvent evt) {
        Object source = evt.getSource();
        if (source.equals(cineStartButton)) {
            setComponentsEnabled(false);
        } else if (source.equals(cineStopButton)) {
            setComponentsEnabled(true);
        } else if (source instanceof JToggleButton) {
            boolean needSync = false;

            for (int i = 0; i < tiedButtons.length; i++) if (tiedButtons[i].equals(source)) needSync = true;
            if (needSync)
                for (int i = 0; i < tiedButtons.length; i++)
                    if (!tiedButtons[i].equals(source)) tiedButtons[i].setSelected(false);
        }
    }

    /** Private utility method to turn on/off the components. */
    private void setComponentsEnabled(boolean enabled) {
        layoutList.setEnabled(enabled);
        // allViewsButton.setEnabled(enabled);
        // currentViewButton.setEnabled(enabled);
        annotateButton.setEnabled(enabled);
        measurementButton.setEnabled(enabled);
        statisticsButton.setEnabled(enabled);
        histogramButton.setEnabled(enabled);
        windowSlider.setEnabled(enabled);
        levelSlider.setEnabled(enabled);
        zoomSlider.setEnabled(enabled);
        rotationSlider.setEnabled(enabled);
    }

    /** Add the GUI components */
    private void addUtilities(JPanel utilityPane) {

        // set the layout and layout parameters
        GridBagLayout gridBag = (GridBagLayout) utilityPane.getLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        int leftMargin = new Integer(JaiI18N.getString("LeftMargin")).intValue();
        int topMargin = new Integer(JaiI18N.getString("TopMargin")).intValue();
        int rightMargin = new Integer(JaiI18N.getString("RightMargin")).intValue();
        int bottomMargin = new Integer(JaiI18N.getString("BottomMargin")).intValue();

        constraints.insets = new Insets(leftMargin, topMargin, rightMargin, bottomMargin);
        constraints.ipadx = new Integer(JaiI18N.getString("MenuTitlePadX")).intValue();
        constraints.ipady = new Integer(JaiI18N.getString("MenuTitlePadY")).intValue();

        // add the title
        MenuTitle menuTitle = new MenuTitle();
        constraints.gridx = new Integer(JaiI18N.getString("ItemXPosition")).intValue();
        constraints.gridy = new Integer(JaiI18N.getString("MenuTitleYPosition")).intValue();
        gridBag.setConstraints(menuTitle, constraints);
        utilityPane.add(menuTitle);

        constraints.ipadx = new Integer(JaiI18N.getString("OthersPadX")).intValue();
        constraints.ipady = new Integer(JaiI18N.getString("OthersPadY")).intValue();

        // add the general command panel
        JPanel layoutPanel = createBorderedPanel(BoxLayout.Y_AXIS, JaiI18N.getString("GeneralBorderTitle"));
        buildGeneralPanel(layoutPanel);
        constraints.gridy = new Integer(JaiI18N.getString("GeneralPaneYPosition")).intValue();
        gridBag.setConstraints(layoutPanel, constraints);
        utilityPane.add(layoutPanel);

        // add the transform panel.
        JPanel transformPanel = createBorderedPanel(BoxLayout.Y_AXIS, JaiI18N.getString("TransformBorderTitle"));

        buildTransformPanel(transformPanel);
        constraints.gridy = new Integer(JaiI18N.getString("TransformPaneYPosition")).intValue();
        gridBag.setConstraints(transformPanel, constraints);
        utilityPane.add(transformPanel);

        // add the cining panel.
        JPanel cinePanel = createBorderedPanel(BoxLayout.X_AXIS, JaiI18N.getString("CineBorderTitle"));
        buildCinePanel(cinePanel);
        constraints.gridy = new Integer(JaiI18N.getString("CinePaneYPosition")).intValue();
        gridBag.setConstraints(cinePanel, constraints);
        utilityPane.add(cinePanel);

        // add the window/level panel.
        JPanel winlevelPanel = createBorderedPanel(BoxLayout.Y_AXIS, JaiI18N.getString("WindowLevelBorderTitle"));
        buildWinlevelPanel(winlevelPanel);
        constraints.gridy = new Integer(JaiI18N.getString("WindowLevelPaneYPosition")).intValue();
        gridBag.setConstraints(winlevelPanel, constraints);
        utilityPane.add(winlevelPanel);

        // add the measurement panel.
        JPanel measurePanel = createBorderedPanel(BoxLayout.X_AXIS, JaiI18N.getString("MeasurementBorderTitle"));
        buildMeasurePanel(measurePanel);
        constraints.gridy = new Integer(JaiI18N.getString("MeasurementPaneYPosition")).intValue();
        gridBag.setConstraints(measurePanel, constraints);
        utilityPane.add(measurePanel);

        // add the roi panel.
        JPanel roiPanel = createBorderedPanel(BoxLayout.X_AXIS, JaiI18N.getString("ROIBorderTitle"));
        buildROIPanel(roiPanel);
        constraints.gridy = new Integer(JaiI18N.getString("ROIPaneYPosition")).intValue();
        gridBag.setConstraints(roiPanel, constraints);
        utilityPane.add(roiPanel);
    }

    /** Build the general panel. */
    private void buildGeneralPanel(JPanel panel) {

        // create, add layout label.
        Box box = Box.createHorizontalBox();
        JLabel layout = new JLabel(JaiI18N.getString("LayoutLabel"));
        layout.setAlignmentX(Component.CENTER_ALIGNMENT);
        int gap = new Integer(JaiI18N.getString("GapAfterLayoutLabel")).intValue();
        box.add(Box.createHorizontalStrut(gap));
        box.add(layout);

        // create, add layout list
        layoutList = createJComboBox(panel, getSupportedLayouts(), setLayoutCommand, medicalAppState);
        box.add(layoutList);

        gap = new Integer(JaiI18N.getString("GapAfterComboBox")).intValue();
        box.add(Box.createHorizontalStrut(gap));
        panel.add(box);

        // create, add operation-scope selection button group
        JPanel viewPanel = createBoxLayoutJPanel(BoxLayout.X_AXIS);
        ButtonGroup group = new ButtonGroup();

        allViewsButton =
                createJRadioButton(group, JaiI18N.getString("AllViewsButton"), allViewsCommand, false, medicalAppState);

        viewPanel.add(allViewsButton);

        currentViewButton = createJRadioButton(
                group, JaiI18N.getString("CurrentViewButton"), currentViewCommand, true, medicalAppState);

        viewPanel.add(currentViewButton);
        panel.add(viewPanel);
    }

    /** Create the transform panel. */
    private void buildTransformPanel(JPanel panel) {

        // create, add zoom slider
        JLabel zoom = new JLabel(JaiI18N.getString("ZoomLabel"), JLabel.CENTER);
        zoom.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(zoom);

        String[] labels = new String[12];
        for (int i = 1; i <= 12; i++) labels[i - 1] = JaiI18N.getString("ZoomTickMark" + i);

        zoomSlider = createJSlider(0, 110, nozoom * 10, labels, zoomCommand, medicalAppState);
        panel.add(zoomSlider);

        // create, add rotation slider
        JPanel rotationPanel = createBoxLayoutJPanel(BoxLayout.X_AXIS);
        JLabel rotation = new JLabel(JaiI18N.getString("RotationLabel"), JLabel.CENTER);
        rotation.setAlignmentX(Component.CENTER_ALIGNMENT);

        int majorTick = (new Integer(JaiI18N.getString("MajorRotationTick"))).intValue();
        rotationSlider = createJSlider(0, 360, 0, majorTick, rotationCommand, medicalAppState);
        panel.add(rotation);
        panel.add(rotationSlider);
    }

    /** Create the cining-loop panel. */
    private void buildCinePanel(JPanel panel) {

        // create, add speed label and text field
        Box box = Box.createHorizontalBox();
        JLabel speedLabel = new JLabel(JaiI18N.getString("CineSpeedLabel"));
        int gap = new Integer(JaiI18N.getString("GapBeforeSpeedLabel")).intValue();
        box.add(Box.createHorizontalStrut(gap));
        box.add(speedLabel);
        gap = new Integer(JaiI18N.getString("GapBeforeTextField")).intValue();
        box.add(Box.createHorizontalStrut(gap));

        speedInput = createJTextField(panel, JaiI18N.getString("DefaultCineSpeed"), 3, speedCommand, medicalAppState);

        box.add(speedInput);
        gap = new Integer(JaiI18N.getString("GapBeforeStartButton")).intValue();
        box.add(Box.createHorizontalStrut(gap));

        // create and add cine start/stop button group
        ButtonGroup group = new ButtonGroup();

        cineStartButton =
                createJRadioButton(group, JaiI18N.getString("CineStartButton"), startCommand, false, medicalAppState);

        cineStartButton.addActionListener(this);
        box.add(cineStartButton);
        gap = new Integer(JaiI18N.getString("GapBeforeStopButton")).intValue();
        box.add(Box.createHorizontalStrut(gap));

        cineStopButton =
                createJRadioButton(group, JaiI18N.getString("CineStopButton"), stopCommand, true, medicalAppState);

        cineStopButton.addActionListener(this);
        box.add(cineStopButton);
        gap = new Integer(JaiI18N.getString("GapAfterStopButton")).intValue();
        box.add(Box.createHorizontalStrut(gap));
        panel.add(box);
    }

    /** Create the window/level panel. */
    private void buildWinlevelPanel(JPanel panel) {

        // create, add window slider.
        JLabel windowLabel = new JLabel(JaiI18N.getString("WindowLabel"));
        windowLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(windowLabel);

        windowSlider = createJSlider(
                smallestWindow,
                largestWindow,
                defaultWindow,
                (largestWindow - smallestWindow) / 4,
                windowCommand,
                medicalAppState);
        panel.add(windowSlider);

        // create, add level slider.
        JLabel levelLabel = new JLabel(JaiI18N.getString("LevelLabel"));
        levelLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(levelLabel);
        levelSlider = createJSlider(
                smallestLevel,
                largestLevel,
                defaultLevel,
                (largestLevel - smallestLevel) / 4,
                levelCommand,
                medicalAppState);
        panel.add(levelSlider);
    }

    /** Create the measurement panel. */
    private void buildMeasurePanel(JPanel panel) {

        // create, add annotation button
        Box box = Box.createHorizontalBox();
        ButtonGroup group = new ButtonGroup();
        int gap = new Integer(JaiI18N.getString("GapBeforeAnnoButton")).intValue();
        box.add(Box.createHorizontalStrut(gap));
        annotateButton = createJToggleButton(
                JaiI18N.getString("AnnotationToggleButton"), annotationCommand, false, medicalAppState);
        box.add(annotateButton);
        gap = new Integer(JaiI18N.getString("GapBeforeMeasButton")).intValue();
        box.add(Box.createHorizontalStrut(gap));

        // create add measurement button
        measurementButton = createJToggleButton(
                JaiI18N.getString("MeasurementToggleButton"), measurementCommand, false, medicalAppState);
        box.add(measurementButton);
        gap = new Integer(JaiI18N.getString("GapAfterMeasButton")).intValue();
        box.add(Box.createHorizontalStrut(gap));
        panel.add(box);
    }

    /** Create the roi panel. */
    private void buildROIPanel(JPanel panel) {
        Box box = Box.createHorizontalBox();
        ButtonGroup group = new ButtonGroup();
        int gap = new Integer(JaiI18N.getString("GapBeforeStatButton")).intValue();
        box.add(Box.createHorizontalStrut(gap));

        // create, add statistics button
        statisticsButton = createJToggleButton(
                JaiI18N.getString("StatisticsToggleButton"), statisticsCommand, false, medicalAppState);
        box.add(statisticsButton);

        // create, add histogram button
        histogramButton = createJToggleButton(
                JaiI18N.getString("HistogramToggleButton"), histogramCommand, false, medicalAppState);
        gap = new Integer(JaiI18N.getString("GapBeforeHistButton")).intValue();
        box.add(Box.createHorizontalStrut(gap));
        box.add(histogramButton);
        panel.add(box);
    }

    /** Create a BoxLayout panel with the provided orientation. */
    private JPanel createBoxLayoutJPanel(int orientation) {
        JPanel panel = new JPanel();
        if (orientation == BoxLayout.X_AXIS) panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        else panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        return panel;
    }

    /** Create a bordered BoxLayout-ed panel with the provided orientation and title. */
    private JPanel createBorderedPanel(int orientation, String title) {
        JPanel panel = createBoxLayoutJPanel(orientation);
        Border border = BorderFactory.createLineBorder(Color.gray, 2);
        panel.setBorder(BorderFactory.createTitledBorder(border, title));
        return panel;
    }

    /** Return a JComboBox. */
    private JComboBox createJComboBox(JPanel panel, String[] list, String command, ActionListener listener) {
        JComboBox cBox = new JComboBox(list);
        panel.add(cBox);
        cBox.setActionCommand(command);
        cBox.addActionListener(listener);
        return cBox;
    }

    /** Return a JRadioButton. */
    private JRadioButton createJRadioButton(
            ButtonGroup group, String name, String command, boolean selected, ActionListener listener) {
        // Create the radio buttons.
        JRadioButton button = new JRadioButton(name);
        button.setActionCommand(command);
        button.setSelected(selected);
        button.addActionListener(listener);
        group.add(button);
        return button;
    }

    /** Return a JSlider. */
    private JSlider createJSlider(int min, int max, int value, String[] labels, String name, ChangeListener listener) {
        JSlider slider = new JSlider(min, max, value);
        Hashtable table = new Hashtable();
        int size = labels.length;
        int spacing = (max - min) / (size - 1);

        for (int i = 0; i < size; i++) {
            Integer index = new Integer(i * spacing);
            table.put(index, new JLabel(labels[i]));
        }

        slider.setLabelTable(table);
        slider.setMajorTickSpacing(spacing);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.addChangeListener(listener);
        sliderTable.put(slider, name);
        return slider;
    }

    /** Return a JSlider. */
    private JSlider createJSlider(int min, int max, int value, int tick, String name, ChangeListener listener) {
        JSlider slider = new JSlider(min, max, value);
        slider.setMajorTickSpacing(tick);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.addChangeListener(listener);
        sliderTable.put(slider, name);
        return slider;
    }

    /** Return a JTextField. */
    private JTextField createJTextField(
            JPanel container, String text, int size, String command, ActionListener listener) {
        JTextField field = new JTextField(text, size);
        field.setActionCommand(command);
        field.addActionListener(listener);
        field.setHorizontalAlignment(JTextField.RIGHT);
        return field;
    }

    /** return a JToggleButton. */
    private JToggleButton createJToggleButton(String text, String command, boolean selected, ActionListener listener) {
        JToggleButton button = new JToggleButton(text, selected);
        button.setActionCommand(command);
        button.addActionListener(listener);
        return button;
    }

    /** Create the list of supported layouts based on the information read from the property file. */
    private String[] getSupportedLayouts() {
        int supportedNumLayout = (new Integer(JaiI18N.getString("SupportedNumLayout"))).intValue();

        String[] layouts = new String[supportedNumLayout];
        for (int i = 1; i <= supportedNumLayout; i++) layouts[i - 1] = JaiI18N.getString("Layout" + i + "Label");

        return layouts;
    }
}

/** The inner class to display the menu title. */
class MenuTitle extends JPanel {
    public void paint(Graphics g) {
        g.setFont(new Font(null, Font.BOLD, 25));
        g.setColor(Color.black);
        g.drawString("Utility Menu", getWidth() / 2 - 50, 30);
    }
}
