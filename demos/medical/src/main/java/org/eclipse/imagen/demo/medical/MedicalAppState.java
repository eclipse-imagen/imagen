/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 *
 * This Example Content is intended to demonstrate usage of Eclipse technology. It is
 * provided to you under the terms and conditions of the Eclipse Distribution License
 * v1.0 which is available at http://www.eclipse.org/org/documents/edl-v10.php.
 */
package org.eclipse.imagen.demo.medical;

import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Hashtable;
import javax.swing.JComboBox;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ProgressMonitor;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.eclipse.imagen.JAI;
import org.eclipse.imagen.NullOpImage;
import org.eclipse.imagen.OpImage;
import org.eclipse.imagen.RenderedOp;

/**
 * The event processing center for this application. This class responses for loading data sets, processing the events
 * from the utility menu that includes changing the operation scope, the layout, window/level, rotation angle, zoom
 * factor, starting/stoping the cining-loop and etc.
 */
public class MedicalAppState implements ActionListener, ChangeListener, MedicalAppConstants, PropertyChangeListener {
    /** The single instance of this singleton class. */
    private static MedicalAppState instance;

    /** The operation scope: operation will be implemented on all the views or only the current view. */
    private static int OPERATE_SCOPE_ALLVIEWS = 1;

    private static int OPERATE_SCOPE_CURRENTVIEW = 2;

    /** The loaded DICOM images. */
    private RenderedImage[] images;

    /** The images after window/level, rotation and zoom. */
    private RenderedImage[] wrapedImages;

    /** The current image in the original image list. */
    private int currentImageNum;

    /** The current layout of this view pane. */
    private int currentLayout = (new Integer(JaiI18N.getString("DefaultLayout"))).intValue();

    /** The operation scope. */
    private int operateScope;

    /** The operation parameters. */
    private double zoomFactor;

    private int rotationAngle;
    private int window;
    private int level;

    /** Indicates whether the cining-loop is started. */
    private boolean cining;

    /** The cining speed. */
    private int speed;

    /** The property change event manager. */
    private MedicalPropertyChangeSupport changeSupport;

    /** Intialize the parameters. */
    {
        initializeParameters();
    }

    /** Return the single instance of this class. This method guarantees the singleton property of this class. */
    public static synchronized MedicalAppState getInstance() {
        if (instance == null) instance = new MedicalAppState();
        return instance;
    }

    /** The default private constructor to guarantee the singleton property of this class. */
    private MedicalAppState() {
        changeSupport = new MedicalPropertyChangeSupport(this);
    }

    /** process the action events. */
    public void actionPerformed(ActionEvent evt) {
        String command = evt.getActionCommand();
        Object source = evt.getSource();

        MedicalApp medicalApp = MedicalApp.getInstance();
        MultipleImagePane multipleImagePane = medicalApp.getMultipleImagePane();

        if (command.equals(setLayoutCommand)) {

            // change layout
            synchronized (changeSupport) {
                removePropertyChangeListeners();
            }
            currentLayout = ((JComboBox) source).getSelectedIndex() + 1;
            initializeParameters();
            UtilityMenuPane.getInstance().initUtilityMenu();
            multipleImagePane.setImageGridLayout(currentLayout);
            multipleImagePane.createLayout(getImagesForDisplay());
            // when reload, then refocus trigger the addPropertyChangeListener
            // so don't need to add again.
            // if the listener list is a set instead of vector will be better
            // to avoid duplicated add.
            // addPropertyChangeListeners();
            medicalApp.resizeAfterChangeLayout();
        } else if (command.equals(allViewsCommand)) {

            // turn on the operation on all the views.
            synchronized (changeSupport) {
                removePropertyChangeListeners();
                operateScope = OPERATE_SCOPE_ALLVIEWS;
                addPropertyChangeListeners();
            }
            updateSliders();
        } else if (command.equals(currentViewCommand)) {

            // operation only on the current view.
            synchronized (changeSupport) {
                removePropertyChangeListeners();
                operateScope = OPERATE_SCOPE_CURRENTVIEW;
                addPropertyChangeListeners();
            }
            updateSliders();
        } else if (command.equals(speedCommand)) {

            // change cinig speed.
            String speedString = ((JTextField) source).getText();
            speed = (new Integer(speedString)).intValue();
        } else if (command.equals(startCommand)) {

            // turn cining on.
            startCining();
        } else if (command.equals(stopCommand)) {

            // turn cine off.
            stopCining();
        } else if (command.equals(annotationCommand)) {

            // toggle annotation.
            boolean annotation = ((JToggleButton) source).isSelected();
            firePropertyChange(annotationCommand, !annotation, annotation);
        } else if (command.equals(measurementCommand)) {

            // toggle measurement.
            boolean measurement = ((JToggleButton) source).isSelected();
            firePropertyChange(measurementCommand, !measurement, measurement);
        } else if (command.equals(statisticsCommand)) {

            // toggle statistics.
            boolean statistics = ((JToggleButton) source).isSelected();
            firePropertyChange(statisticsCommand, !statistics, statistics);
        } else if (command.equals(histogramCommand)) {

            // toggle histogram.
            boolean histogram = ((JToggleButton) source).isSelected();
            firePropertyChange(histogramCommand, !histogram, histogram);
        }
    }

    /** Fire property change event for the boolean property. */
    private void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
        firePropertyChange(propertyName, new Boolean(oldValue), new Boolean(newValue));
    }

    /** Fire property change event. */
    private void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        synchronized (changeSupport) {
            changeSupport.firePropertyChange(propertyName, oldValue, newValue);
        }
    }

    /** Add a property change listener. */
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(propertyName, listener);
    }

    /** Remove a property change listener. */
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(propertyName, listener);
    }

    /** Remove all the listeners attached to the command center. */
    private void removePropertyChangeListeners() {
        changeSupport.removePropertyChangeListeners();
    }

    /** Add all the property change listeners to this command center. */
    private void addPropertyChangeListeners() {
        PropertyChangeListener[] listeners = getCurrentListeners();

        for (int i = 0; i < listeners.length; i++) {
            if (listeners[i] instanceof MedicalImagePane) {
                // For an image display pane, listen to the change
                // of the toggle buttons.
                addPropertyChangeListener(annotationCommand, listeners[i]);
                addPropertyChangeListener(measurementCommand, listeners[i]);
                addPropertyChangeListener(statisticsCommand, listeners[i]);
                addPropertyChangeListener(histogramCommand, listeners[i]);
            } else if (listeners[i] instanceof RenderedImage) {
                // For the image, listen to the parameter changes
                addPropertyChangeListener(windowCommand, listeners[i]);
                addPropertyChangeListener(levelCommand, listeners[i]);
                addPropertyChangeListener(rotationCommand, listeners[i]);
                addPropertyChangeListener(zoomCommand, listeners[i]);
            }
        }
    }

    /** Return all the objects who listens to this command center. */
    private PropertyChangeListener[] getCurrentListeners() {

        // get the app instance and viewer instance.
        MedicalApp medicalApp = MedicalApp.getInstance();
        MultipleImagePane multipleImagePane = medicalApp.getMultipleImagePane();

        // get all the image display panes.
        MedicalImagePane[] panes = multipleImagePane.getImagePanels();

        // the size of the listeners' buffer is different for the 2 opeartion
        // mode.
        int size;

        // define the listener's buffer and initialize it to null.
        PropertyChangeListener[] listeners = null;

        if (operateScope == OPERATE_SCOPE_ALLVIEWS) {

            // if the operations are on all the displayed images,
            size = 2 * panes.length;
            listeners = new PropertyChangeListener[size];

            // register bother image display panes and images.
            for (int i = 0; i < size; i += 2) {
                listeners[i] = (PropertyChangeListener) panes[i / 2];
                listeners[i + 1] = (PropertyChangeListener) (panes[i / 2]).getImage();
            }
        } else if (operateScope == OPERATE_SCOPE_CURRENTVIEW) {

            // if the operations are on the focused image
            size = panes.length + 1;
            PropertyChangeListener listener = (PropertyChangeListener) panes[multipleImagePane.getFocused()].getImage();
            listeners = new PropertyChangeListener[size];

            // register the focused image
            listeners[0] = listener;

            // register all the image display panes.
            for (int i = 1; i < size; i++) listeners[i] = (PropertyChangeListener) panes[i - 1];
        }
        return listeners;
    }

    /** Process the state change events. */
    public void stateChanged(ChangeEvent evt) {

        // cache the source
        Object source = evt.getSource();

        // get the table for all the sliders to retrieve the name
        Hashtable table = UtilityMenuPane.getInstance().getSliderTable();

        String name = (String) table.get(evt.getSource());
        Object newValue = null;

        if (name == null) return;

        // state change event processing
        if (name.equals(zoomCommand)) {
            zoomFactor = sliderValueToZoom(((JSlider) source).getValue());
            newValue = new Double(zoomFactor);
        } else if (name.equals(rotationCommand)) {
            rotationAngle = ((JSlider) source).getValue();
            newValue = new Integer(rotationAngle);
        } else if (name.equals(windowCommand)) {
            window = ((JSlider) source).getValue();
            newValue = new Integer(window);
        } else if (name.equals(levelCommand)) {
            level = ((JSlider) source).getValue();
            newValue = new Integer(level);
        }

        firePropertyChange(name, null, newValue);
    }

    /**
     * Process the property change event. This is used to synchronize the sliders on the display when the focus is
     * changed.
     */
    public void propertyChange(PropertyChangeEvent evt) {

        // if the property changed is to signal the parameter synchronization
        if (evt.getPropertyName().equals(paramSync)) {

            // Re-register the listeners
            synchronized (changeSupport) {
                removePropertyChangeListeners();
                addPropertyChangeListeners();
            }

            // assign the values to the parameters
            Object[] array = (Object[]) evt.getNewValue();
            window = ((Integer) array[0]).intValue();
            level = ((Integer) array[1]).intValue();
            rotationAngle = ((Integer) array[2]).intValue();
            zoomFactor = ((Double) array[3]).doubleValue();

            // update the display
            updateSliders();
        }
    }

    /** Convert the value marked on the slider to the zoom factor. */
    private double sliderValueToZoom(int value) {
        int nozoom1 = nozoom * 10;
        double zoom;

        if (value > nozoom1) zoom = (value - nozoom1) / 10.0 + 1.0;
        else zoom = value * 0.8 / nozoom1 + 0.2;
        return zoom;
    }

    /** Convert the zoom factor to the value marked on the slider. */
    private int zoomToSliderValue(double zoom) {
        int value;
        if (zoom >= 1.0) value = (int) ((zoom - 1.0 + nozoom) * 10);
        else value = (int) ((zoom - 0.2) / 0.2 * 10);
        return value;
    }

    /** Update the slider marks. */
    public void updateSliders() {
        UtilityMenuPane.getInstance().updateSliders(zoomToSliderValue(zoomFactor), rotationAngle, window, level);

        firePropertyChange(zoomCommand, null, new Double(zoomFactor));
        firePropertyChange(rotationCommand, null, new Integer(rotationAngle));
        firePropertyChange(windowCommand, null, new Integer(window));
        firePropertyChange(levelCommand, null, new Integer(level));
    }

    /** Load the data set. Create a thread to load the images. Create a progress bar to monitor the progress. */
    public void loadDataSet(File infile) {
        final File file = infile;

        // create the loading thread
        Thread loadingThread = new Thread() {
            public void run() {

                // if the provided file is not a directory
                if (!file.isDirectory()) throw new IllegalArgumentException(JaiI18N.getString("Not a directory"));

                // get all the files under that directory
                String[] files = file.list();

                // get the absolute path
                String path = file.getAbsolutePath();

                // create image buffers
                images = new RenderedImage[files.length];
                JAI.getDefaultInstance().getTileCache().setMemoryCapacity((long) files.length << 21);
                wrapedImages = new RenderedImage[files.length];

                // create the progress monitor
                ProgressMonitor progress =
                        new ProgressMonitor(MedicalApp.getInstance(), "Loading Images ......", "", 0, files.length);
                progress.setProgress(0);

                // calculate the maximum of this data set for display
                // the window/level sliders
                int maximum = Integer.MIN_VALUE;

                for (int i = 0; i < files.length; i++) {

                    // label the progress monitor
                    progress.setProgress(i);
                    progress.setNote("Loading " + i + " out of " + files.length);

                    // read the dicom image
                    DicomDecoder decoder = new DicomDecoder(path + File.separator + files[i]);
                    images[i] = decoder.getDicomImage();

                    RenderedImage nullImage = cacheIt(images[i]);
                    wrapedImages[i] = wrapOperations(images[i]);
                    // retrieve the maximum from the dicom image
                    int max = ((DicomImage) images[i]).getMaximum();

                    // if not defined in dicom, calculate the maximum
                    if (max == 0) {
                        ParameterBlock pb = new ParameterBlock();
                        pb.addSource(nullImage);
                        RenderedOp ext = JAI.create("extrema", pb, null);
                        max = (int) (((double[]) ext.getProperty("maximum"))[0]);
                    }

                    if (max > maximum) maximum = max;
                }

                // close the progress monitor
                progress.close();

                // init the parameters, create display layout and
                // resize the frame properly
                initializeParameters();

                MedicalApp medicalApp = MedicalApp.getInstance();
                MultipleImagePane multipleImagePane = medicalApp.getMultipleImagePane();

                multipleImagePane.setImageGridLayout(currentLayout);
                multipleImagePane.createLayout(getImagesForDisplay());

                maximum = (maximum + 255) / 256 * 256;
                UtilityMenuPane.getInstance().setMaxWindowLevel(maximum);

                medicalApp.resizeAfterChangeLayout();
            }
        };

        // start the loading thread
        loadingThread.start();
    }

    /** Create a group of images to be displayed in the current <code>MultipleImagePane</code>. */
    public RenderedImage[] getImagesForDisplay() {

        // get the size and define the buffer
        MultipleImagePane multipleImagePane = MedicalApp.getInstance().getMultipleImagePane();
        int size = multipleImagePane.getImageGridLayout().getImageNum();
        RenderedImage[] imagesInDisplay = new RenderedImage[size];

        // assign the images into the buffer
        for (int i = 0; i < size; i++) {
            int current = (i + currentImageNum) % images.length;

            if (wrapedImages[current] == null) // if it is not cached
            wrapedImages[current] = wrapOperations(images[current]);
            else // if it is cached
            resetParam((MedicalAppOpImage) wrapedImages[current]);

            imagesInDisplay[i] = wrapedImages[current];
        }

        return imagesInDisplay;
    }

    /** Create the wrapped image for the focused panel. */
    public RenderedImage getImageForFocusPane() {

        // get the position of the focused image
        MultipleImagePane multipleImagePane = MedicalApp.getInstance().getMultipleImagePane();
        int focused = multipleImagePane.getFocused();

        int current = (currentImageNum + focused) % images.length;

        if (wrapedImages[current] == null) // if it is not cached
        wrapedImages[current] = wrapOperations(images[current]);
        else // if it is cached
        resetParam((MedicalAppOpImage) wrapedImages[current]);

        return wrapedImages[current];
    }

    /**
     * Wraps the provided image into a NullOpImage with a tile cache (the default tile cache of JAI) as a
     * RenderingHints. So that the computed tiles of the provided image can be cached. This method is designed because
     * of DICOMImages loaded from a customized image reader.
     */
    private RenderedImage cacheIt(RenderedImage src) {
        RenderingHints hints =
                new RenderingHints(JAI.KEY_TILE_CACHE, JAI.getDefaultInstance().getTileCache());
        return new NullOpImage(src, null, hints, OpImage.OP_IO_BOUND);
    }

    /** Synchronize the parameters. */
    private void resetParam(MedicalAppOpImage image) {
        image.setWindow(window);
        image.setLevel(level);
        image.setZoom(zoomFactor);
        image.setRotation(rotationAngle);
    }

    /**
     * Wrap a DICOM image with the window/level, rotation, zoom operators. attach <code>this</code> to listener to
     * synchronize the parameters.
     */
    private RenderedImage wrapOperations(RenderedImage source) {
        MedicalAppOpImage image =
                new MedicalAppOpImage(cacheIt(source), (DicomImage) source, window, level, rotationAngle, zoomFactor);
        PropertyChangeListener listener = (PropertyChangeListener) this;
        image.addPropertyChangeListener(paramSync, listener);
        return image;
    }

    /** Create a thread to cine the images. */
    private Thread createCiningThread() {

        // return a new thread as an instance of an anonymous class
        return new Thread() {
            public void run() {

                // set the delay based on the speed
                int wait = 1000 / speed;
                long start = System.currentTimeMillis();
                int iteration = 0;
                MultipleImagePane multipleImagePane = MedicalApp.getInstance().getMultipleImagePane();

                // Create a robot to monitor the paint
                Robot robot = null;

                try {
                    robot = new java.awt.Robot();
                } catch (Exception e) {
                }

                while (cining) {
                    iteration++;
                    currentImageNum++;
                    currentImageNum %= images.length;

                    // re-register the listeners
                    synchronized (changeSupport) {
                        removePropertyChangeListeners();
                        if (operateScope == OPERATE_SCOPE_ALLVIEWS) multipleImagePane.set(getImagesForDisplay(), false);
                        else multipleImagePane.setFocusedPane(getImageForFocusPane());

                        addPropertyChangeListeners();
                    }

                    // repaint the images
                    multipleImagePane.revalidate();
                    multipleImagePane.repaint();

                    // Wait until the paint is finished
                    robot.waitForIdle();

                    // adjust the delay time based on the current performance
                    long elapsed = (System.currentTimeMillis() - start) / 1000;
                    if (elapsed > 0) {
                        double fps = ((double) iteration) / elapsed;

                        if (fps < speed) wait--;
                        else wait++;
                        if (wait < 0) wait = 0;
                    }

                    // wait
                    if (wait > 0)
                        try {
                            Thread.sleep(wait);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                }

                // Remove the old property change listeners;
                // Synchronized the display to contiguous images
                // Add the new property change listeners;
                synchronized (changeSupport) {
                    removePropertyChangeListeners();
                    multipleImagePane.set(getImagesForDisplay(), false);

                    addPropertyChangeListeners();
                }

                // The last time of setting images may delete the correct
                // listeners; so need to re-set to guarantee the correctness
                // of the image observers
                multipleImagePane.set(getImagesForDisplay(), false);
            }
        };
    }

    /** Start the cining. */
    private void startCining() {
        cining = true;
        createCiningThread().start();
    }

    /** Stop the cining. */
    private void stopCining() {
        cining = false;
    }

    /** Initialize the parameters. */
    private void initializeParameters() {
        currentImageNum = 0;
        operateScope = OPERATE_SCOPE_CURRENTVIEW;

        zoomFactor = 1.0;
        rotationAngle = 0;
        window = defaultWindow;
        level = defaultLevel;

        cining = false;
        speed = new Integer(JaiI18N.getString("DefaultCineSpeed")).intValue();
    }
}
