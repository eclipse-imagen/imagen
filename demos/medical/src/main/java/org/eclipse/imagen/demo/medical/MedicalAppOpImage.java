/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 *
 * This Example Content is intended to demonstrate usage of Eclipse technology. It is
 * provided to you under the terms and conditions of the Eclipse Distribution License
 * v1.0 which is available at http://www.eclipse.org/org/documents/edl-v10.php.
 */
package org.eclipse.imagen.demo.medical;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.ParameterBlock;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Observable;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.event.SwingPropertyChangeSupport;
import org.eclipse.imagen.Histogram;
import org.eclipse.imagen.ImageLayout;
import org.eclipse.imagen.ImageN;
import org.eclipse.imagen.InterpolationBilinear;
import org.eclipse.imagen.LookupTableJAI;
import org.eclipse.imagen.PlanarImage;
import org.eclipse.imagen.ROIShape;

/**
 * A wrapper class to generate the result image of the processing chain for window/leveling, rotation and scaling.
 *
 * <p>The rendering chain is Load-->Window/Level-->Rotate-->Scale. To optimize the memory use and compuation time, (1)
 * The loaded DiCOMImage is wrapped into a NullOpimage so that the loaded tile can be cached; (2) The first 3 nodes are
 * untiled; (3) The node "scale" is tiled with the dimension defined in the property file.
 */
public class MedicalAppOpImage extends Observable
        implements RenderedImage, PropertyChangeListener, MedicalAppConstants, Focusable {
    /** Cache the default tile size defined in the property file. */
    private static int tileSize = new Integer(JaiI18N.getString("TileSize")).intValue();

    /** The source image. */
    private RenderedImage source;

    /** The dicom image wrapped in the source. */
    private DicomImage image;

    /** The window value. */
    private int window;

    /** The level value. */
    private int level;

    /** The zoom factor. */
    private double zoomFactor;

    /** The rotation angle. */
    private int rotationAngle;

    /**
     * The result image after window/leveling. The current process is a chain defined as window/level, rotation, zoom.
     */
    private RenderedImage windowLevelResult;

    /** The result image after rotation. */
    private RenderedImage rotationResult;

    /** The result image after zoom. */
    private RenderedImage zoomResult;

    /** The look-up-table for window/leveling. It is also used in the display of histogram/statistics. */
    private byte[][] lut;

    /** Indicates this image is displayed in a focused image display panel. */
    private boolean isFocused = false;

    /** The property change event manager. */
    private SwingPropertyChangeSupport changeSupport;

    /**
     * Constructor.
     *
     * @param source The source image for the win/lvl, rotation, zoom process.
     * @param window The window value.
     * @param level The level value.
     * @param angle The angle in degree.
     * @param zoom The zoom factor.
     */
    public MedicalAppOpImage(
            RenderedImage source, DicomImage dicomImage, int window, int level, int angle, double zoom) {
        this.source = source;
        this.image = dicomImage;
        this.window = window;
        this.level = level;
        this.zoomFactor = zoom;
        this.rotationAngle = angle;

        windowLevelResult = windowLevelOperator(this.source, level - window / 2.0, level + window / 2.0);
        rotationResult = rotationOperator(windowLevelResult, rotationAngle);

        zoomResult = zoomOperator(rotationResult, zoomFactor);
    }

    /** Return the zoom factor. */
    public double getZoomFactor() {
        return this.zoomFactor;
    }

    /** Return the rotation angle in degree. */
    public int getRotationAngle() {
        return rotationAngle;
    }

    /** Return the window value. */
    public int getWindow() {
        return window;
    }

    /** Return the level value. */
    public int getLevel() {
        return level;
    }

    /** Return the zoom factor. */
    public void setZoom(double zoom) {
        if (zoom == this.zoomFactor) return;
        this.zoomFactor = zoom;
        zoomResult = zoomOperator(rotationResult, zoom);
        setChangedAndNotify();
    }

    /**
     * Set the rotation angle.
     *
     * @param angle The rotation angle in degree.
     */
    public void setRotation(int angle) {
        if (angle == this.rotationAngle) return;
        this.rotationAngle = angle;
        rotationResult = rotationOperator(windowLevelResult, rotationAngle);
        zoomResult = zoomOperator(rotationResult, zoomFactor);
        setChangedAndNotify();
    }

    /**
     * Set the new window value.
     *
     * @param window The new window value.
     */
    public void setWindow(int window) {
        if (window == this.window) return;

        this.window = window;
        windowLevelResult = windowLevelOperator(source, level - window / 2.0, level + window / 2.0);
        rotationResult = rotationOperator(windowLevelResult, rotationAngle);
        zoomResult = zoomOperator(rotationResult, zoomFactor);
        setChangedAndNotify();
    }

    /**
     * Set the new level value.
     *
     * @param level The new level value.
     */
    public void setLevel(int level) {
        if (this.level == level) return;

        this.level = level;
        windowLevelResult = windowLevelOperator(source, level - window / 2.0, level + window / 2.0);
        rotationResult = rotationOperator(windowLevelResult, rotationAngle);
        zoomResult = zoomOperator(rotationResult, zoomFactor);
        setChangedAndNotify();
    }

    /** Process the property change events: accepts the new parameters. */
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(windowCommand)) setWindow(((Integer) evt.getNewValue()).intValue());
        if (evt.getPropertyName().equals(levelCommand)) setLevel(((Integer) evt.getNewValue()).intValue());
        if (evt.getPropertyName().equals(rotationCommand)) setRotation(((Integer) evt.getNewValue()).intValue());
        if (evt.getPropertyName().equals(zoomCommand)) setZoom(((Double) evt.getNewValue()).doubleValue());
    }

    /** When focused, synchronize the interface to use the parameters of this image. */
    public void setFocused(boolean b) {
        isFocused = b;
        if (b) {
            Object[] array = new Object[] {
                new Integer(window), new Integer(level), new Integer(rotationAngle), new Double(zoomFactor)
            };
            firePropertyChange(paramSync, null, array);
        }
    }

    /** Return the focus status. */
    public boolean isFocused() {
        return isFocused;
    }

    /** Fire property change event. */
    private void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        if (changeSupport == null) return;

        synchronized (changeSupport) {
            changeSupport.firePropertyChange(propertyName, oldValue, newValue);
        }
    }

    /** Add a property change listener. */
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        if (changeSupport == null) {
            changeSupport = new SwingPropertyChangeSupport(this);
        }

        synchronized (changeSupport) {
            changeSupport.addPropertyChangeListener(propertyName, listener);
        }
    }

    /** Remove a property change listener. */
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        if (changeSupport == null) {
            return;
        }
        synchronized (changeSupport) {
            changeSupport.removePropertyChangeListener(propertyName, listener);
        }
    }

    /** Display the statistics or histogram. */
    public void displayStatistics(Shape shape, boolean b) {
        PlanarImage img = ImageN.create("extrema", source, null);
        double[] maximum = (double[]) img.getProperty("maximum");
        double maxValue = ((int) (maximum[0] + 255) / 256) * 256.0;

        Shape roi = transformOnROI(shape);
        ParameterBlock pb = (new ParameterBlock()).addSource(source);
        pb.add(new ROIShape(roi)).add(1).add(1).add(new int[] {256});
        pb.add(new double[] {0.0}).add(new double[] {maxValue});

        PlanarImage dst = ImageN.create("histogram", pb);
        Histogram h = (Histogram) dst.getProperty("hiStOgRam");
        JFrame frame = new HistogramFrame(h, b, lut, physicalROI(shape));
        Dimension size = frame.getSize();
        Dimension screenSize = MedicalApp.getInstance().getToolkit().getScreenSize();

        frame.setBounds(screenSize.width / 4, screenSize.height / 4, screenSize.width / 2, screenSize.height / 2);
        frame.show();
        frame.pack();
    }

    /** Transform the ROI from the image coordinate system to the physical coordinate system. */
    private Shape physicalROI(Shape shape) {
        AffineTransform transform = new AffineTransform();

        transform.scale(
                1.0 / zoomFactor * ((DicomImage) image).getPixelSizeX(),
                1.0 / zoomFactor * ((DicomImage) image).getPixelSizeY());
        shape = new GeneralPath(shape);
        return ((GeneralPath) shape).createTransformedShape(transform);
    }

    /** Transform the shape object to the original image coordinate system. */
    private Shape transformOnROI(Shape shape) {
        AffineTransform transform = new AffineTransform();
        transform.scale(1.0 / zoomFactor, 1.0 / zoomFactor);
        shape = new GeneralPath(shape);
        shape = ((GeneralPath) shape).createTransformedShape(transform);
        transform = new AffineTransform();
        transform.rotate(-rotationAngle * Math.PI / 180.0, source.getWidth() / 2.0, source.getHeight() / 2.0);
        shape = ((GeneralPath) shape).createTransformedShape(transform);
        return shape;
    }

    /** Computer the physical distance of 2 points in the displayed image. */
    public double computeDistance(Point start, Point end) {
        double dx = (start.x - end.x) / zoomFactor * ((DicomImage) image).getPixelSizeX();
        double dy = (start.y - end.y) / zoomFactor * ((DicomImage) image).getPixelSizeY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        return ((int) (distance * 10)) / 10.0;
    }

    /** Override the getSource methods. */
    public Vector getSources() {
        Vector v = new Vector(1);
        v.add(source);
        return v;
    }

    /** The following override a group of methods from the super classes. */
    public Object getProperty(String name) {
        return source.getProperty(name);
    }

    public String[] getPropertyNames() {
        return source.getPropertyNames();
    }

    public ColorModel getColorModel() {
        return zoomResult.getColorModel();
    }

    public SampleModel getSampleModel() {
        return zoomResult.getSampleModel();
    }

    public int getWidth() {
        return zoomResult.getWidth();
    }

    public int getHeight() {
        return zoomResult.getHeight();
    }

    public int getMinX() {
        return zoomResult.getMinX();
    }

    public int getMinY() {
        return zoomResult.getMinY();
    }

    public int getNumXTiles() {
        return zoomResult.getNumXTiles();
    }

    public int getNumYTiles() {
        return zoomResult.getNumYTiles();
    }

    public int getMinTileX() {
        return zoomResult.getMinTileX();
    }

    public int getMinTileY() {
        return zoomResult.getMinTileY();
    }

    public int getTileWidth() {
        return zoomResult.getTileWidth();
    }

    public int getTileHeight() {
        return zoomResult.getTileHeight();
    }

    public int getTileGridXOffset() {
        return zoomResult.getTileGridXOffset();
    }

    public int getTileGridYOffset() {
        return zoomResult.getTileGridYOffset();
    }

    public Raster getTile(int tileX, int tileY) {
        return zoomResult.getTile(tileX, tileY);
    }

    public Raster getData() {
        return zoomResult.getData();
    }

    public Raster getData(Rectangle rect) {
        return zoomResult.getData(rect);
    }

    public WritableRaster copyData(WritableRaster raster) {
        return zoomResult.copyData(raster);
    }

    /** This private method fire the change event to the observers. */
    private void setChangedAndNotify() {
        setChanged();
        notifyObservers();
    }

    /** Create the window/level result image. */
    private final RenderedImage windowLevelOperator(RenderedImage source, double low, double high) {
        if (source == null) {
            return null;
        }

        ParameterBlock pb = null;
        RenderedImage dst = null;
        SampleModel sampleModel = source.getSampleModel();
        int bands = sampleModel.getNumBands();
        int datatype = sampleModel.getDataType();
        double rmin;
        double rmax;
        double slope;
        double y_int;
        int tableLength = 256;

        if (datatype == DataBuffer.TYPE_SHORT || datatype == DataBuffer.TYPE_USHORT) {
            tableLength = 65536;
        }

        // use a lookup table for rescaling
        if (high != low) {
            slope = 256.0 / (high - low);
            y_int = 256.0 - slope * high;
        } else {
            slope = 0.0;
            y_int = 0.0;
        }
        /*
                ImageLayout il = new ImageLayout();
                il.setTileWidth(tileSize);
                il.setTileHeight(tileSize);
                il.setTileGridXOffset(0);
                il.setTileGridYOffset(0);
                RenderingHints hints =
                        new RenderingHints(ImageN.KEY_IMAGE_LAYOUT, il);
        */

        if (datatype >= DataBuffer.TYPE_BYTE && datatype < DataBuffer.TYPE_INT) {
            lut = new byte[bands][tableLength];

            for (int i = 0; i < tableLength; i++) {
                for (int j = 0; j < bands; j++) {
                    int value = (int) (slope * i + y_int);

                    if (datatype == DataBuffer.TYPE_USHORT) {
                        value &= 0xFFFF;
                    }

                    if (value > 255) value = 255;

                    if (i < (int) low) {
                        value = 0;
                    } else if (i > (int) high) {
                        value = 255;
                    } else {
                        value &= 0xFF;
                    }

                    lut[j][i] = (byte) value;
                }
            }

            LookupTableJAI lookup = new LookupTableJAI(lut);

            pb = new ParameterBlock();
            pb.addSource(source);
            pb.add(lookup);
            dst = ImageN.create("lookup", pb, null); // hints);
        } else if (datatype == DataBuffer.TYPE_INT
                || datatype == DataBuffer.TYPE_FLOAT
                || datatype == DataBuffer.TYPE_DOUBLE) {
            pb = new ParameterBlock();
            pb.addSource(source);
            pb.add(slope);
            pb.add(y_int);
            dst = ImageN.create("rescale", pb, null);

            // produce a byte image
            pb = new ParameterBlock();
            pb.addSource(dst);
            pb.add(DataBuffer.TYPE_BYTE);
            dst = ImageN.create("format", pb, null);
        }
        return dst;
    }

    /** Create the rotation result image. */
    private RenderedImage rotationOperator(RenderedImage source, int angle) {
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(source);
        pb.add(source.getWidth() / 2.0f);
        pb.add(source.getHeight() / 2.0f);
        pb.add((float) (angle * Math.PI / 180.0));
        pb.add(new InterpolationBilinear());

        RenderedImage img = ImageN.create("rotate", pb);

        // Untile this rotate node so that when compute the next node,
        // no extra memory and time are used in PlanarImage.getExtendedData().
        ImageLayout il = new ImageLayout();
        il.setTileWidth(img.getWidth());
        il.setTileHeight(img.getHeight());
        il.setTileGridXOffset(img.getMinX());
        il.setTileGridYOffset(img.getMinY());
        RenderingHints hints = new RenderingHints(ImageN.KEY_IMAGE_LAYOUT, il);

        img = ImageN.create("rotate", pb, hints);
        return img;
    }

    /** Create the zoom result image. */
    private RenderedImage zoomOperator(RenderedImage source, double zoomFactor) {
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(source);
        pb.add((float) zoomFactor);
        pb.add((float) zoomFactor);
        pb.add(0.0f);
        pb.add(0.0f);
        pb.add(new InterpolationBilinear());

        // Tiling on this scale node: (1) Reduces the tiling memory
        // overhead; (2) Pull only the displayed tiles when the zoom
        // factor is large.
        ImageLayout il = new ImageLayout();
        il.setTileWidth(tileSize);
        il.setTileHeight(tileSize);
        RenderingHints hints = new RenderingHints(ImageN.KEY_IMAGE_LAYOUT, il);

        RenderedImage img = ImageN.create("scale", pb, hints);

        return img;
    }
}
