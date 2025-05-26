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

package org.eclipse.imagen.media.opimage;

import java.awt.Rectangle;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Map;
import org.eclipse.imagen.ImageLayout;
import org.eclipse.imagen.PointOpImage;
import org.eclipse.imagen.RasterAccessor;
import org.eclipse.imagen.RasterFactory;
import org.eclipse.imagen.RasterFormatTag;
import org.eclipse.imagen.media.util.ImageUtil;
import org.eclipse.imagen.media.util.JDKWorkarounds;

/// XXX Testing
/// import java.awt.Point;
/// import org.eclipse.imagen.TiledImage;

/**
 * An <code>OpImage</code> implementing magnitude, magnitude squared, and phase operations as described in <code>
 * org.eclipse.imagen.operator.MagnitudeDescriptor</code>, <code>org.eclipse.imagen.operator.MagnitudeSquaredDescriptor
 * </code>, and <code>org.eclipse.imagen.operator.PhaseDescriptor</code>
 *
 * <p>This implementation assumes that the number of bands in the source image is at least two. The number of bands in
 * the destination image is clamped to half (in the integer division sense) of the number of bands in the source image.
 *
 * @since EA3
 * @see org.eclipse.imagen.UntiledOpImage
 * @see org.eclipse.imagen.operator.MagnitudeDescriptor
 * @see org.eclipse.imagen.operator.MagnitudeSquaredDescriptor
 * @see org.eclipse.imagen.operator.PhaseDescriptor
 */
final class MagnitudePhaseOpImage extends PointOpImage {
    /** A flag indicating a magnitude or modulus operation. */
    public static final int MAGNITUDE = 1;

    /** A flag indicating a magnitude squared or "power spectrum" operation. */
    public static final int MAGNITUDE_SQUARED = 2;

    /** A flag indicating a phase operation. */
    public static final int PHASE = 3;

    /** The type of operation as specified by one of the static final types. */
    protected int operationType;

    /** The gain to be applied to the phase. */
    private double phaseGain = 1.0;

    /** The bias to be applied to the phase. */
    private double phaseBias = 0.0;

    /**
     * Constructs a <code>MagnitudePhaseOpImage</code> object.
     *
     * <p>The tile grid layout, SampleModel, and ColorModel may optionally be specified by an ImageLayout object.
     *
     * @param source A RenderedImage.
     * @param layout An ImageLayout optionally containing the tile grid layout, SampleModel, and ColorModel, or null.
     * @param operationType One of the static final flag values defined in this class which indicates the type of
     *     operation to perform.
     */
    public MagnitudePhaseOpImage(RenderedImage source, Map config, ImageLayout layout, int operationType) {
        super(source, layout, config, true);

        // Cache the parameter.
        this.operationType = operationType;

        // Initialize the SampleModel flag.
        boolean needNewSampleModel = false;

        // Reset the data type to that specified by the layout if it
        // has been modified within the superclass constructor chain.
        int dataType = sampleModel.getTransferType();
        if (layout != null && dataType != layout.getSampleModel(source).getTransferType()) {
            dataType = layout.getSampleModel(source).getTransferType();
            needNewSampleModel = true;
        }

        // Force the band count to be at most half that of the source image.
        int numBands = sampleModel.getNumBands();
        if (numBands > source.getSampleModel().getNumBands() / 2) {
            numBands = source.getSampleModel().getNumBands() / 2;
            needNewSampleModel = true;
        }

        // Create a new SampleModel for the destination.
        if (needNewSampleModel) {
            sampleModel = RasterFactory.createComponentSampleModel(
                    sampleModel, dataType, sampleModel.getWidth(), sampleModel.getHeight(), numBands);

            if (colorModel != null && !JDKWorkarounds.areCompatibleDataModels(sampleModel, colorModel)) {
                colorModel = ImageUtil.getCompatibleColorModel(sampleModel, config);
            }
        }

        if (operationType == PHASE) {
            // Set phase gain and bias as a function of destination data type.
            switch (dataType) {
                case DataBuffer.TYPE_BYTE:
                    phaseGain = 255.0 / (2.0 * Math.PI);
                    phaseBias = Math.PI;
                    break;
                case DataBuffer.TYPE_SHORT:
                    phaseGain = Short.MAX_VALUE / (2.0 * Math.PI);
                    phaseBias = Math.PI;
                    break;
                case DataBuffer.TYPE_USHORT:
                    phaseGain = (Short.MAX_VALUE - Short.MIN_VALUE) / (2.0 * Math.PI);
                    phaseBias = Math.PI;
                    break;
                case DataBuffer.TYPE_INT:
                    phaseGain = Integer.MAX_VALUE / (2.0 * Math.PI);
                    phaseBias = Math.PI;
                    break;
                default:
                    // A floating point type: do nothing.
            }
        }
    }

    /*
     * Calculate the magnitude [squared] or phase of the source image.
     *
     * @param sources   Cobbled sources, guaranteed to provide all the
     *                  source data necessary for computing the rectangle.
     * @param dest      The tile containing the rectangle to be computed.
     * @param destRect  The rectangle within the tile to be computed.
     */
    protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        // Retrieve format tags.
        RasterFormatTag[] formatTags = getFormatTags();

        // Construct RasterAccessors.
        RasterAccessor srcAccessor = new RasterAccessor(
                sources[0], destRect, formatTags[0], getSourceImage(0).getColorModel());
        RasterAccessor dstAccessor = new RasterAccessor(dest, destRect, formatTags[1], getColorModel());

        // Branch to the method appropriate to the accessor data type.
        switch (dstAccessor.getDataType()) {
            case DataBuffer.TYPE_BYTE:
                computeRectByte(srcAccessor, dstAccessor, destRect.height, destRect.width);
                break;
            case DataBuffer.TYPE_SHORT:
                computeRectShort(srcAccessor, dstAccessor, destRect.height, destRect.width);
                break;
            case DataBuffer.TYPE_USHORT:
                computeRectUShort(srcAccessor, dstAccessor, destRect.height, destRect.width);
                break;
            case DataBuffer.TYPE_INT:
                computeRectInt(srcAccessor, dstAccessor, destRect.height, destRect.width);
                break;
            case DataBuffer.TYPE_FLOAT:
                computeRectFloat(srcAccessor, dstAccessor, destRect.height, destRect.width);
                break;
            case DataBuffer.TYPE_DOUBLE:
                computeRectDouble(srcAccessor, dstAccessor, destRect.height, destRect.width);
                break;
            default:
                // NB: This statement should be unreachable.
                throw new RuntimeException(JaiI18N.getString("MagnitudePhaseOpImage0"));
        }

        if (dstAccessor.needsClamping()) {
            dstAccessor.clampDataArrays();
        }

        // Make sure that the output data is copied to the destination.
        dstAccessor.copyDataToRaster();
    }

    private void computeRectDouble(RasterAccessor srcAccessor, RasterAccessor dstAccessor, int numRows, int numCols) {
        // Set pixel and line strides.
        int srcPixelStride = srcAccessor.getPixelStride();
        int srcScanlineStride = srcAccessor.getScanlineStride();
        int dstPixelStride = dstAccessor.getPixelStride();
        int dstScanlineStride = dstAccessor.getScanlineStride();

        // Loop over the destination bands.
        int numDstBands = sampleModel.getNumBands();
        for (int dstBand = 0; dstBand < numDstBands; dstBand++) {
            // Set source band indices.
            int srcBandReal = 2 * dstBand;
            int srcBandImag = srcBandReal + 1;

            // Get the source and destination arrays for this band.
            double[] srcReal = srcAccessor.getDoubleDataArray(srcBandReal);
            double[] srcImag = srcAccessor.getDoubleDataArray(srcBandImag);
            double[] dstData = dstAccessor.getDoubleDataArray(dstBand);

            // Initialize the data offsets for this band.
            int srcOffsetReal = srcAccessor.getBandOffset(srcBandReal);
            int srcOffsetImag = srcAccessor.getBandOffset(srcBandImag);
            int dstOffset = dstAccessor.getBandOffset(dstBand);

            // Initialize the line offsets for looping.
            int srcLineReal = srcOffsetReal;
            int srcLineImag = srcOffsetImag;
            int dstLine = dstOffset;

            for (int row = 0; row < numRows; row++) {
                // Initialize pixel offsets for this row.
                int srcPixelReal = srcLineReal;
                int srcPixelImag = srcLineImag;
                int dstPixel = dstLine;

                // Switch per line based on operation type.
                switch (operationType) {
                    case MAGNITUDE:
                        for (int col = 0; col < numCols; col++) {
                            double real = srcReal[srcPixelReal];
                            double imag = srcImag[srcPixelImag];

                            dstData[dstPixel] = Math.sqrt(real * real + imag * imag);

                            srcPixelReal += srcPixelStride;
                            srcPixelImag += srcPixelStride;
                            dstPixel += dstPixelStride;
                        }
                        break;
                    case MAGNITUDE_SQUARED:
                        for (int col = 0; col < numCols; col++) {
                            double real = srcReal[srcPixelReal];
                            double imag = srcImag[srcPixelImag];

                            dstData[dstPixel] = real * real + imag * imag;

                            srcPixelReal += srcPixelStride;
                            srcPixelImag += srcPixelStride;
                            dstPixel += dstPixelStride;
                        }
                        break;
                    case PHASE:
                        for (int col = 0; col < numCols; col++) {
                            double real = srcReal[srcPixelReal];
                            double imag = srcImag[srcPixelImag];

                            dstData[dstPixel] = Math.atan2(imag, real);

                            srcPixelReal += srcPixelStride;
                            srcPixelImag += srcPixelStride;
                            dstPixel += dstPixelStride;
                        }
                        break;
                }

                // Increment the line offsets.
                srcLineReal += srcScanlineStride;
                srcLineImag += srcScanlineStride;
                dstLine += dstScanlineStride;
            }
        }
    }

    private void computeRectFloat(RasterAccessor srcAccessor, RasterAccessor dstAccessor, int numRows, int numCols) {
        // Set pixel and line strides.
        int srcPixelStride = srcAccessor.getPixelStride();
        int srcScanlineStride = srcAccessor.getScanlineStride();
        int dstPixelStride = dstAccessor.getPixelStride();
        int dstScanlineStride = dstAccessor.getScanlineStride();

        // Loop over the destination bands.
        int numDstBands = sampleModel.getNumBands();
        for (int dstBand = 0; dstBand < numDstBands; dstBand++) {
            // Set source band indices.
            int srcBandReal = 2 * dstBand;
            int srcBandImag = srcBandReal + 1;

            // Get the source and destination arrays for this band.
            float[] srcReal = srcAccessor.getFloatDataArray(srcBandReal);
            float[] srcImag = srcAccessor.getFloatDataArray(srcBandImag);
            float[] dstData = dstAccessor.getFloatDataArray(dstBand);

            // Initialize the data offsets for this band.
            int srcOffsetReal = srcAccessor.getBandOffset(srcBandReal);
            int srcOffsetImag = srcAccessor.getBandOffset(srcBandImag);
            int dstOffset = dstAccessor.getBandOffset(dstBand);

            // Initialize the line offsets for looping.
            int srcLineReal = srcOffsetReal;
            int srcLineImag = srcOffsetImag;
            int dstLine = dstOffset;

            for (int row = 0; row < numRows; row++) {
                // Initialize pixel offsets for this row.
                int srcPixelReal = srcLineReal;
                int srcPixelImag = srcLineImag;
                int dstPixel = dstLine;

                // Switch per line based on operation type.
                switch (operationType) {
                    case MAGNITUDE:
                        for (int col = 0; col < numCols; col++) {
                            float real = srcReal[srcPixelReal];
                            float imag = srcImag[srcPixelImag];

                            dstData[dstPixel] = ImageUtil.clampFloat(Math.sqrt(real * real + imag * imag));

                            srcPixelReal += srcPixelStride;
                            srcPixelImag += srcPixelStride;
                            dstPixel += dstPixelStride;
                        }
                        break;
                    case MAGNITUDE_SQUARED:
                        for (int col = 0; col < numCols; col++) {
                            float real = srcReal[srcPixelReal];
                            float imag = srcImag[srcPixelImag];

                            dstData[dstPixel] = real * real + imag * imag;

                            srcPixelReal += srcPixelStride;
                            srcPixelImag += srcPixelStride;
                            dstPixel += dstPixelStride;
                        }
                        break;
                    case PHASE:
                        for (int col = 0; col < numCols; col++) {
                            float real = srcReal[srcPixelReal];
                            float imag = srcImag[srcPixelImag];

                            dstData[dstPixel] = ImageUtil.clampFloat(Math.atan2(imag, real));

                            srcPixelReal += srcPixelStride;
                            srcPixelImag += srcPixelStride;
                            dstPixel += dstPixelStride;
                        }
                        break;
                }

                // Increment the line offsets.
                srcLineReal += srcScanlineStride;
                srcLineImag += srcScanlineStride;
                dstLine += dstScanlineStride;
            }
        }
    }

    private void computeRectInt(RasterAccessor srcAccessor, RasterAccessor dstAccessor, int numRows, int numCols) {
        // Set pixel and line strides.
        int srcPixelStride = srcAccessor.getPixelStride();
        int srcScanlineStride = srcAccessor.getScanlineStride();
        int dstPixelStride = dstAccessor.getPixelStride();
        int dstScanlineStride = dstAccessor.getScanlineStride();

        // Loop over the destination bands.
        int numDstBands = sampleModel.getNumBands();
        for (int dstBand = 0; dstBand < numDstBands; dstBand++) {
            // Set source band indices.
            int srcBandReal = 2 * dstBand;
            int srcBandImag = srcBandReal + 1;

            // Get the source and destination arrays for this band.
            int[] srcReal = srcAccessor.getIntDataArray(srcBandReal);
            int[] srcImag = srcAccessor.getIntDataArray(srcBandImag);
            int[] dstData = dstAccessor.getIntDataArray(dstBand);

            // Initialize the data offsets for this band.
            int srcOffsetReal = srcAccessor.getBandOffset(srcBandReal);
            int srcOffsetImag = srcAccessor.getBandOffset(srcBandImag);
            int dstOffset = dstAccessor.getBandOffset(dstBand);

            // Initialize the line offsets for looping.
            int srcLineReal = srcOffsetReal;
            int srcLineImag = srcOffsetImag;
            int dstLine = dstOffset;

            for (int row = 0; row < numRows; row++) {
                // Initialize pixel offsets for this row.
                int srcPixelReal = srcLineReal;
                int srcPixelImag = srcLineImag;
                int dstPixel = dstLine;

                // Switch per line based on operation type.
                switch (operationType) {
                    case MAGNITUDE:
                        for (int col = 0; col < numCols; col++) {
                            int real = srcReal[srcPixelReal];
                            int imag = srcImag[srcPixelImag];

                            dstData[dstPixel] = ImageUtil.clampRoundInt(Math.sqrt(real * real + imag * imag));

                            srcPixelReal += srcPixelStride;
                            srcPixelImag += srcPixelStride;
                            dstPixel += dstPixelStride;
                        }
                        break;
                    case MAGNITUDE_SQUARED:
                        for (int col = 0; col < numCols; col++) {
                            int real = srcReal[srcPixelReal];
                            int imag = srcImag[srcPixelImag];

                            dstData[dstPixel] = real * real + imag * imag;

                            srcPixelReal += srcPixelStride;
                            srcPixelImag += srcPixelStride;
                            dstPixel += dstPixelStride;
                        }
                        break;
                    case PHASE:
                        for (int col = 0; col < numCols; col++) {
                            int real = srcReal[srcPixelReal];
                            int imag = srcImag[srcPixelImag];

                            dstData[dstPixel] =
                                    ImageUtil.clampRoundInt((Math.atan2(imag, real) + phaseBias) * phaseGain);

                            srcPixelReal += srcPixelStride;
                            srcPixelImag += srcPixelStride;
                            dstPixel += dstPixelStride;
                        }
                        break;
                }

                // Increment the line offsets.
                srcLineReal += srcScanlineStride;
                srcLineImag += srcScanlineStride;
                dstLine += dstScanlineStride;
            }
        }
    }

    private void computeRectUShort(RasterAccessor srcAccessor, RasterAccessor dstAccessor, int numRows, int numCols) {
        // Set pixel and line strides.
        int srcPixelStride = srcAccessor.getPixelStride();
        int srcScanlineStride = srcAccessor.getScanlineStride();
        int dstPixelStride = dstAccessor.getPixelStride();
        int dstScanlineStride = dstAccessor.getScanlineStride();

        // Loop over the destination bands.
        int numDstBands = sampleModel.getNumBands();
        for (int dstBand = 0; dstBand < numDstBands; dstBand++) {
            // Set source band indices.
            int srcBandReal = 2 * dstBand;
            int srcBandImag = srcBandReal + 1;

            // Get the source and destination arrays for this band.
            short[] srcReal = srcAccessor.getShortDataArray(srcBandReal);
            short[] srcImag = srcAccessor.getShortDataArray(srcBandImag);
            short[] dstData = dstAccessor.getShortDataArray(dstBand);

            // Initialize the data offsets for this band.
            int srcOffsetReal = srcAccessor.getBandOffset(srcBandReal);
            int srcOffsetImag = srcAccessor.getBandOffset(srcBandImag);
            int dstOffset = dstAccessor.getBandOffset(dstBand);

            // Initialize the line offsets for looping.
            int srcLineReal = srcOffsetReal;
            int srcLineImag = srcOffsetImag;
            int dstLine = dstOffset;

            for (int row = 0; row < numRows; row++) {
                // Initialize pixel offsets for this row.
                int srcPixelReal = srcLineReal;
                int srcPixelImag = srcLineImag;
                int dstPixel = dstLine;

                // Switch per line based on operation type.
                switch (operationType) {
                    case MAGNITUDE:
                        for (int col = 0; col < numCols; col++) {
                            int real = srcReal[srcPixelReal] & 0xffff;
                            int imag = srcImag[srcPixelImag] & 0xffff;

                            dstData[dstPixel] = ImageUtil.clampRoundUShort(Math.sqrt(real * real + imag * imag));

                            srcPixelReal += srcPixelStride;
                            srcPixelImag += srcPixelStride;
                            dstPixel += dstPixelStride;
                        }
                        break;
                    case MAGNITUDE_SQUARED:
                        for (int col = 0; col < numCols; col++) {
                            int real = srcReal[srcPixelReal] & 0xffff;
                            int imag = srcImag[srcPixelImag] & 0xffff;

                            dstData[dstPixel] = ImageUtil.clampUShort(real * real + imag * imag);

                            srcPixelReal += srcPixelStride;
                            srcPixelImag += srcPixelStride;
                            dstPixel += dstPixelStride;
                        }
                        break;
                    case PHASE:
                        for (int col = 0; col < numCols; col++) {
                            int real = srcReal[srcPixelReal] & 0xffff;
                            int imag = srcImag[srcPixelImag] & 0xffff;

                            dstData[dstPixel] =
                                    ImageUtil.clampRoundUShort((Math.atan2(imag, real) + phaseBias) * phaseGain);

                            srcPixelReal += srcPixelStride;
                            srcPixelImag += srcPixelStride;
                            dstPixel += dstPixelStride;
                        }
                        break;
                }

                // Increment the line offsets.
                srcLineReal += srcScanlineStride;
                srcLineImag += srcScanlineStride;
                dstLine += dstScanlineStride;
            }
        }
    }

    private void computeRectShort(RasterAccessor srcAccessor, RasterAccessor dstAccessor, int numRows, int numCols) {
        // Set pixel and line strides.
        int srcPixelStride = srcAccessor.getPixelStride();
        int srcScanlineStride = srcAccessor.getScanlineStride();
        int dstPixelStride = dstAccessor.getPixelStride();
        int dstScanlineStride = dstAccessor.getScanlineStride();

        // Loop over the destination bands.
        int numDstBands = sampleModel.getNumBands();
        for (int dstBand = 0; dstBand < numDstBands; dstBand++) {
            // Set source band indices.
            int srcBandReal = 2 * dstBand;
            int srcBandImag = srcBandReal + 1;

            // Get the source and destination arrays for this band.
            short[] srcReal = srcAccessor.getShortDataArray(srcBandReal);
            short[] srcImag = srcAccessor.getShortDataArray(srcBandImag);
            short[] dstData = dstAccessor.getShortDataArray(dstBand);

            // Initialize the data offsets for this band.
            int srcOffsetReal = srcAccessor.getBandOffset(srcBandReal);
            int srcOffsetImag = srcAccessor.getBandOffset(srcBandImag);
            int dstOffset = dstAccessor.getBandOffset(dstBand);

            // Initialize the line offsets for looping.
            int srcLineReal = srcOffsetReal;
            int srcLineImag = srcOffsetImag;
            int dstLine = dstOffset;

            for (int row = 0; row < numRows; row++) {
                // Initialize pixel offsets for this row.
                int srcPixelReal = srcLineReal;
                int srcPixelImag = srcLineImag;
                int dstPixel = dstLine;

                // Switch per line based on operation type.
                switch (operationType) {
                    case MAGNITUDE:
                        for (int col = 0; col < numCols; col++) {
                            short real = srcReal[srcPixelReal];
                            short imag = srcImag[srcPixelImag];

                            dstData[dstPixel] = ImageUtil.clampRoundShort(Math.sqrt(real * real + imag * imag));

                            srcPixelReal += srcPixelStride;
                            srcPixelImag += srcPixelStride;
                            dstPixel += dstPixelStride;
                        }
                        break;
                    case MAGNITUDE_SQUARED:
                        for (int col = 0; col < numCols; col++) {
                            short real = srcReal[srcPixelReal];
                            short imag = srcImag[srcPixelImag];

                            dstData[dstPixel] = ImageUtil.clampShort(real * real + imag * imag);

                            srcPixelReal += srcPixelStride;
                            srcPixelImag += srcPixelStride;
                            dstPixel += dstPixelStride;
                        }
                        break;
                    case PHASE:
                        for (int col = 0; col < numCols; col++) {
                            short real = srcReal[srcPixelReal];
                            short imag = srcImag[srcPixelImag];

                            dstData[dstPixel] =
                                    ImageUtil.clampRoundShort((Math.atan2(imag, real) + phaseBias) * phaseGain);

                            srcPixelReal += srcPixelStride;
                            srcPixelImag += srcPixelStride;
                            dstPixel += dstPixelStride;
                        }
                        break;
                }

                // Increment the line offsets.
                srcLineReal += srcScanlineStride;
                srcLineImag += srcScanlineStride;
                dstLine += dstScanlineStride;
            }
        }
    }

    private void computeRectByte(RasterAccessor srcAccessor, RasterAccessor dstAccessor, int numRows, int numCols) {
        // Set pixel and line strides.
        int srcPixelStride = srcAccessor.getPixelStride();
        int srcScanlineStride = srcAccessor.getScanlineStride();
        int dstPixelStride = dstAccessor.getPixelStride();
        int dstScanlineStride = dstAccessor.getScanlineStride();

        // Loop over the destination bands.
        int numDstBands = sampleModel.getNumBands();
        for (int dstBand = 0; dstBand < numDstBands; dstBand++) {
            // Set source band indices.
            int srcBandReal = 2 * dstBand;
            int srcBandImag = srcBandReal + 1;

            // Get the source and destination arrays for this band.
            byte[] srcReal = srcAccessor.getByteDataArray(srcBandReal);
            byte[] srcImag = srcAccessor.getByteDataArray(srcBandImag);
            byte[] dstData = dstAccessor.getByteDataArray(dstBand);

            // Initialize the data offsets for this band.
            int srcOffsetReal = srcAccessor.getBandOffset(srcBandReal);
            int srcOffsetImag = srcAccessor.getBandOffset(srcBandImag);
            int dstOffset = dstAccessor.getBandOffset(dstBand);

            // Initialize the line offsets for looping.
            int srcLineReal = srcOffsetReal;
            int srcLineImag = srcOffsetImag;
            int dstLine = dstOffset;

            for (int row = 0; row < numRows; row++) {
                // Initialize pixel offsets for this row.
                int srcPixelReal = srcLineReal;
                int srcPixelImag = srcLineImag;
                int dstPixel = dstLine;

                // Switch per line based on operation type.
                switch (operationType) {
                    case MAGNITUDE:
                        for (int col = 0; col < numCols; col++) {
                            int real = srcReal[srcPixelReal] & 0xff;
                            int imag = srcImag[srcPixelImag] & 0xff;

                            dstData[dstPixel] = ImageUtil.clampRoundByte(Math.sqrt(real * real + imag * imag));

                            srcPixelReal += srcPixelStride;
                            srcPixelImag += srcPixelStride;
                            dstPixel += dstPixelStride;
                        }
                        break;
                    case MAGNITUDE_SQUARED:
                        for (int col = 0; col < numCols; col++) {
                            int real = srcReal[srcPixelReal] & 0xff;
                            int imag = srcImag[srcPixelImag] & 0xff;

                            dstData[dstPixel] = ImageUtil.clampByte(real * real + imag * imag);

                            srcPixelReal += srcPixelStride;
                            srcPixelImag += srcPixelStride;
                            dstPixel += dstPixelStride;
                        }
                        break;
                    case PHASE:
                        for (int col = 0; col < numCols; col++) {
                            int real = srcReal[srcPixelReal] & 0xff;
                            int imag = srcImag[srcPixelImag] & 0xff;

                            dstData[dstPixel] =
                                    ImageUtil.clampRoundByte((Math.atan2(imag, real) + phaseBias) * phaseGain);

                            srcPixelReal += srcPixelStride;
                            srcPixelImag += srcPixelStride;
                            dstPixel += dstPixelStride;
                        }
                        break;
                }

                // Increment the line offsets.
                srcLineReal += srcScanlineStride;
                srcLineImag += srcScanlineStride;
                dstLine += dstScanlineStride;
            }
        }
    }

    //     public static void main(String args[]) {
    //         int opType = args.length > 0 ?
    //             Integer.valueOf(args[0]).intValue() : 1;
    //         int dataType = args.length > 1 ?
    //             Integer.valueOf(args[1]).intValue() : DataBuffer.TYPE_DOUBLE;
    //         double[] pixels = new double[] { 1, 2, 3, 4,
    //                                              5, 6, 7, 8,
    //                                              9, 10, 11, 12,
    //                                              13, 14, 15, 16 };
    //         WritableRaster pattern =
    //             RasterFactory.createBandedRaster(dataType, 2, 2, 4,
    //                                              new Point(0, 0));
    //         pattern.setPixels(0, 0, 2, 2, pixels);
    //         TiledImage src =
    //             TiledImage.createInterleaved(0, 0,
    //                                          pattern.getWidth(), pattern.getHeight(),
    //                                          pattern.getSampleModel().getNumBands(),
    //                                          pattern.getSampleModel().getTransferType(),
    //                                          pattern.getWidth(), pattern.getHeight(),
    //                                          new int[] {0, 1, 2, 3});
    //         src.setData(pattern);

    //         MagnitudePhaseOpImage dst =
    //             new MagnitudePhaseOpImage(src, null, opType);
    //         pixels = dst.getData().getPixels(0, 0, 2, 2, (double[])null);
    //         System.out.println("");
    //         for(int i = 0; i < pixels.length; i += 2) {
    //             System.out.println(pixels[i] + " " + pixels[i+1]);
    //         }
    //     }
}
