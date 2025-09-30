/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 *
 * This Example Content is intended to demonstrate usage of Eclipse technology. It is
 * provided to you under the terms and conditions of the Eclipse Distribution License
 * v1.0 which is available at http://www.eclipse.org/org/documents/edl-v10.php.
 */
package org.eclipse.imagen.demo;

import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import org.eclipse.imagen.ImageLayout;
import org.eclipse.imagen.OperationDescriptorImpl;
import org.eclipse.imagen.PointOpImage;
import org.eclipse.imagen.RasterAccessor;
import org.eclipse.imagen.RasterFormatTag;

/**
 * A single class that is both an OperationDescriptor and a RenderedImageFactory along with the one OpImage it is
 * capable of creating. The operation implemented is a variation on threshold, although the code may be used as a
 * template for a variety of other point operations.
 */
public class SampleDescriptor extends OperationDescriptorImpl implements RenderedImageFactory {
    /**
     * The resource strings that provide the general documentation and specify the parameter list for the "Sample"
     * operation.
     */
    private static final String[][] resources = {
        {"GlobalName", "Sample"},
        {"LocalName", "Sample"},
        {"Vendor", "com.mycompany"},
        {"Description", "A sample operation that thresholds source pixels"},
        {"DocURL", "http://www.mycompany.com/SampleDescriptor.html"},
        {"Version", "1.0"},
        {"arg0Desc", "param1"},
        {"arg1Desc", "param2"}
    };

    /**
     * The parameter names for the "Sample" operation. Extenders may want to rename them to something more meaningful.
     */
    private static final String[] paramNames = {"param1", "param2"};

    /**
     * The class types for the parameters of the "Sample" operation. User defined classes can be used here as long as
     * the fully qualified name is used and the classes can be loaded.
     */
    private static final Class[] paramClasses = {java.lang.Integer.class, java.lang.Integer.class};

    /** The default parameter values for the "Sample" operation when using a ParameterBlockImageN. */
    private static final Object[] paramDefaults = {new Integer(0), new Integer(255)};

    /** The supported modes. */
    private static final String[] supportedModes = {"rendered"};

    /** Constructor. */
    public SampleDescriptor() {
        super(resources, supportedModes, 1, paramNames, paramClasses, paramDefaults, null);
    }

    /**
     * Creates a SampleOpImage with the given ParameterBlock if the SampleOpImage can handle the particular
     * ParameterBlock.
     */
    public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints) {
        if (!validateParameters(paramBlock)) {
            return null;
        }
        return new SampleOpImage(
                paramBlock.getRenderedSource(0),
                new ImageLayout(),
                (Integer) paramBlock.getObjectParameter(0),
                (Integer) paramBlock.getObjectParameter(1));
    }

    /** Checks that all parameters in the ParameterBlock have the correct type before constructing the SampleOpImage */
    public boolean validateParameters(ParameterBlock paramBlock) {
        for (int i = 0; i < this.getParameterListDescriptor("rendered").getNumParameters(); i++) {
            Object arg = paramBlock.getObjectParameter(i);
            if (arg == null) {
                return false;
            }
            if (!(arg instanceof Integer)) {
                return false;
            }
        }
        return true;
    }
}

/**
 * SampleOpImage is an extension of PointOpImage that takes two integer parameters and one source and performs a
 * modified threshold operation on the given source.
 */
class SampleOpImage extends PointOpImage {

    private int param1;
    private int param2;

    /**
     * Constructs an SampleOpImage. The image dimensions are copied from the source image. The tile grid layout,
     * SampleModel, and ColorModel may optionally be specified by an ImageLayout object.
     *
     * @param source a RenderedImage.
     * @param layout an ImageLayout optionally containing the tile grid layout, SampleModel, and ColorModel, or null.
     */
    public SampleOpImage(RenderedImage source, ImageLayout layout, Integer param1, Integer param2) {
        super(source, layout, null, true);

        this.param1 = param1.intValue();
        this.param2 = param2.intValue();
    }

    /**
     * Performs a modified threshold operation on the pixels in a given rectangle. Sample values below a lower limit are
     * clamped to 0, while those above an upper limit are clamped to 255. The results are returned in the input
     * WritableRaster dest. The sources are cobbled.
     *
     * @param sources an array of sources, guarantee to provide all necessary source data for computing the rectangle.
     * @param dest a tile that contains the rectangle to be computed.
     * @param destRect the rectangle within this OpImage to be processed.
     */
    protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        Raster source = sources[0];
        Rectangle srcRect = mapDestRect(destRect, 0);

        // RasterAccessor is a convienient way to represent any given
        // Raster in a usable format.  It has very little overhead if
        // the underlying Raster is in a common format (PixelSequential
        // for this release) and allows generic code to process
        // a Raster with an exotic format.  Essentially, it allows the
        // common case to processed quickly and the rare case to be
        // processed easily.

        // This "best case" formatTag is used to create a pair of
        // RasterAccessors for processing the source and dest rasters

        RasterFormatTag[] formatTags = getFormatTags();

        RasterAccessor srcAccessor = new RasterAccessor(
                sources[0], srcRect, formatTags[0], getSourceImage(0).getColorModel());
        RasterAccessor dstAccessor = new RasterAccessor(dest, destRect, formatTags[1], getColorModel());

        // Depending on the base dataType of the RasterAccessors,
        // either the byteLoop or intLoop method is called.  The two
        // functions are virtually the same, except for the data type
        // of the underlying arrays.

        switch (dstAccessor.getDataType()) {
            case DataBuffer.TYPE_BYTE:
                byteLoop(srcAccessor, dstAccessor);
                break;
            case DataBuffer.TYPE_INT:
                intLoop(srcAccessor, dstAccessor);
                break;
            default:
                String className = this.getClass().getName();
                throw new RuntimeException(
                        className + " does not implement computeRect" + " for short/float/double data");
        }

        // If the RasterAccessor object set up a temporary buffer for the
        // op to write to, tell the RasterAccessor to write that data
        // to the raster no that we're done with it.
        if (dstAccessor.isDataCopy()) {
            dstAccessor.clampDataArrays();
            dstAccessor.copyDataToRaster();
        }
    }

    /**
     * Computes an area of a given byte-based destination Raster using a souce RasterAccessor and a destination
     * RasterAccesor. Processing is done as if the bytes are unsigned, even though the Java language has support only
     * for signed bytes as a primitive datatype.
     */
    private void byteLoop(RasterAccessor src, RasterAccessor dst) {
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int dnumBands = dst.getNumBands();

        byte dstDataArrays[][] = dst.getByteDataArrays();
        int dstBandOffsets[] = dst.getBandOffsets();
        int dstPixelStride = dst.getPixelStride();
        int dstScanlineStride = dst.getScanlineStride();

        byte srcDataArrays[][] = src.getByteDataArrays();
        int srcBandOffsets[] = src.getBandOffsets();
        int srcPixelStride = src.getPixelStride();
        int srcScanlineStride = src.getScanlineStride();

        byte bp1 = (byte) (param1 & 0xff);
        byte bp2 = (byte) (param2 & 0xff);

        // A standard imaging loop

        for (int k = 0; k < dnumBands; k++) {
            byte dstData[] = dstDataArrays[k];
            byte srcData[] = srcDataArrays[k];
            int srcScanlineOffset = srcBandOffsets[k];
            int dstScanlineOffset = dstBandOffsets[k];
            for (int j = 0; j < dheight; j++) {
                int srcPixelOffset = srcScanlineOffset;
                int dstPixelOffset = dstScanlineOffset;
                for (int i = 0; i < dwidth; i++) {

                    // This code can be specialized by rewriting the
                    // following block of code to do some other
                    // operation.
                    //
                    //  Some examples:
                    //   InvertOp:
                    //    dstData[dstPixelOffset] =
                    //        (byte)(0xff & ~srcData[srcPixelOffset]);
                    //
                    //   AddConst:
                    //    dstData[dstPixelOffset] =
                    //        (byte)(0xff & (srcData[srcPixelOffset]+param1));
                    //
                    //   Currently, the operation performs a threshold.

                    int pixel = srcData[srcPixelOffset] & 0xff;
                    if (pixel < param1) {
                        dstData[dstPixelOffset] = 0; // bp1;
                    } else if (pixel > param2) {
                        dstData[dstPixelOffset] = (byte) 255; // bp2;
                    } else {
                        dstData[dstPixelOffset] = srcData[srcPixelOffset];
                    }

                    srcPixelOffset += srcPixelStride;
                    dstPixelOffset += dstPixelStride;
                }
                srcScanlineOffset += srcScanlineStride;
                dstScanlineOffset += dstScanlineStride;
            }
        }
    }

    /**
     * Computes an area of a given int-based destination Raster using a source RasterAccessor and a destination
     * RasterAccesor.
     */
    private void intLoop(RasterAccessor src, RasterAccessor dst) {
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int dnumBands = dst.getNumBands();

        int dstDataArrays[][] = dst.getIntDataArrays();
        int dstBandOffsets[] = dst.getBandOffsets();
        int dstPixelStride = dst.getPixelStride();
        int dstScanlineStride = dst.getScanlineStride();

        int srcDataArrays[][] = src.getIntDataArrays();
        int srcBandOffsets[] = src.getBandOffsets();
        int srcPixelStride = src.getPixelStride();
        int srcScanlineStride = src.getScanlineStride();

        for (int k = 0; k < dnumBands; k++) {
            int dstData[] = dstDataArrays[k];
            int srcData[] = srcDataArrays[k];
            int srcScanlineOffset = srcBandOffsets[k];
            int dstScanlineOffset = dstBandOffsets[k];
            for (int j = 0; j < dheight; j++) {
                int srcPixelOffset = srcScanlineOffset;
                int dstPixelOffset = dstScanlineOffset;
                for (int i = 0; i < dwidth; i++) {
                    int pixel = srcData[srcPixelOffset];
                    if (pixel < param1) {
                        dstData[dstPixelOffset] = 0;
                    } else if (pixel > param2) {
                        dstData[dstPixelOffset] = 255;
                    } else {
                        dstData[dstPixelOffset] = srcData[srcPixelOffset];
                    }
                    srcPixelOffset += srcPixelStride;
                    dstPixelOffset += dstPixelStride;
                }
                srcScanlineOffset += srcScanlineStride;
                dstScanlineOffset += dstScanlineStride;
            }
        }
    }
}
