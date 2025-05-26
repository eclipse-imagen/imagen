/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 *
 * This Example Content is intended to demonstrate usage of Eclipse technology. It is
 * provided to you under the terms and conditions of the Eclipse Distribution License
 * v1.0 which is available at http://www.eclipse.org/org/documents/edl-v10.php.
 */
package org.eclipse.imagen.demo.codec;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferUShort;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import org.eclipse.imagen.media.codec.ImageCodec;
import org.eclipse.imagen.media.codec.ImageDecoder;
import org.eclipse.imagen.media.codec.ImageDecoderImpl;
import org.eclipse.imagen.media.codec.ImageDecodeParam;
import org.eclipse.imagen.media.codec.SeekableStream;

/**
 * An <code>ImageDecoder</code> for the PNM family of file formats.
 * <p> The PNM file format includes PBM for monochrome images, PGM for
 * grey scale images, and PPM for color images.  The decoder understands
 * both the ASCII and raw versions of all three formats.
 */


public class SamplePNMImageDecoder extends ImageDecoderImpl {

    public SamplePNMImageDecoder(SeekableStream input,
                                 ImageDecodeParam param) {
        super(input, param);
    }

    public RenderedImage decodeAsRenderedImage(int page) throws IOException {
        if (page != 0) {
            throw new IOException(
                             "Illegal page requested from a SamplePNM image.");
        }
        return new SamplePNMImage(input);
    }
}

class SamplePNMImage extends SimpleRenderedImage {

    private static final int PBM_ASCII = '1';
    private static final int PGM_ASCII = '2';
    private static final int PPM_ASCII = '3';
    private static final int PBM_RAW = '4';
    private static final int PGM_RAW = '5';
    private static final int PPM_RAW = '6';

    private static final int LINE_FEED = 0x0A;

    private SeekableStream input;

    private byte[] lineSeparator;

    /** File variant: PBM/PGM/PPM, ASCII/RAW. */


    private int variant;

    /** Maximum pixel value. */


    private int maxValue;

    /** Raster that is the entire image. */


    private WritableRaster theTile;

    private int numBands;

    private int dataType;

    /**
     * Construct a SamplePNMImage.
     *
     * @param input The SeekableStream for the PNM file.
     */


    public SamplePNMImage(SeekableStream input) {
        theTile = null;

        this.input = input;

        String ls = System.lineSeparator();
        lineSeparator = ls.getBytes();

        // Read file header.
        try {
            if (this.input.read() != 'P') {	// magic number
                throw new RuntimeException(
                                  "Invalid magic value for PBM/PGM/PPM file.");
            }

            variant = this.input.read();	// file variant
            if ((variant < PBM_ASCII) || (variant > PPM_RAW)) {
                throw new RuntimeException("Unrecognized file variant.");
            }

            width = readInteger(this.input);	// width
            height = readInteger(this.input);	// height

            if (variant == PBM_ASCII || variant == PBM_RAW) {
                maxValue = 1;
            } else {
                maxValue = readInteger(this.input);	// maximum value
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(
                         "IOException occured while reading PNM file header.");
        }

        // The RAWBITS format can only support byte image data, which means
        // maxValue should be less than 0x100. In case there's a conflict,
        // base the maxValue on variant.
        if (isRaw(variant) && maxValue >= 0x100) {
            maxValue = 0xFF;
        }

        // Reset image layout so there's only one tile.
        tileWidth = width;
        tileHeight = height;

        // Determine number of bands: pixmap (PPM) is 3 bands,
        // bitmap (PBM) and greymap (PGM) are 1 band.
        if (variant == PPM_ASCII || variant == PPM_RAW) {
            this.numBands = 3;
        } else {
            this.numBands = 1;
        }

        // Determine data type based on maxValue.
        if (maxValue < 0x100) {
            this.dataType = DataBuffer.TYPE_BYTE;
        } else if (maxValue < 0x10000) {
            this.dataType = DataBuffer.TYPE_USHORT;
        } else {
            this.dataType = DataBuffer.TYPE_INT;
        }

        // Choose an appropriate SampleModel.
        if ((variant == PBM_ASCII) || (variant == PBM_RAW)) {
            // Each pixel takes 1 bit, pack 8 pixels into a byte.
            sampleModel =
                new MultiPixelPackedSampleModel(DataBuffer.TYPE_BYTE,
                                                width, height, 1);
            colorModel =
                ImageCodec.createGrayIndexColorModel(sampleModel, true);
        } else {
            int[] bandOffsets = new int[numBands];
            for (int i = 0; i < numBands; i++) {
                bandOffsets[i] = numBands - 1 - i;
            }
            
            int pixelStride = numBands;
            int scanlineStride = numBands*width;

            sampleModel = new PixelInterleavedSampleModel(dataType,
                                                          width, height,
                                                          pixelStride,
                                                          scanlineStride,
                                                          bandOffsets);
            colorModel =
                ImageCodec.createComponentColorModel(sampleModel);
        }
    }

    /** Returns true if file variant is raw format, false if ASCII. */


    private boolean isRaw(int v) {
        return (v >= PBM_RAW);
    }

    /** Reads the next integer. */


    private int readInteger(SeekableStream in) throws IOException {
        int ret = 0;
        boolean foundDigit = false;

        int b;
        while ((b = in.read()) != -1) {
            char c = (char)b;
            if (Character.isDigit(c)) {
                ret = ret * 10 + Character.digit(c, 10);
                foundDigit = true;
            } else {
                if (c == '#') { // skip to the end of comment line
                    int length = lineSeparator.length;

                    while ((b = in.read()) != -1) {
                        boolean eol = false;
                        for (int i = 0; i < length; i++) {
                            if (b == lineSeparator[i]) {
                                eol = true;
                                break;
                            }
                        }
                        if (eol) {
                            break;
                        }
                    }
                    if (b == -1) {
                        break;
                    }
                }
                if (foundDigit) {
                    break;
                }
            }
        }

        return ret;
    }

    private Raster computeTile(int tileX, int tileY) {
        if (theTile != null) {
            return theTile;
        }

        // Create a new tile.
        Point org = new Point(tileXToX(tileX), tileYToY(tileY));
        theTile = Raster.createWritableRaster(sampleModel, org);
        Rectangle tileRect = theTile.getBounds();

        // There should only be one tile.
        try {
            switch (variant) {
            case PBM_ASCII:
            case PBM_RAW:
                // SampleModel for these cases should be MultiPixelPacked.

                DataBuffer dataBuffer = theTile.getDataBuffer();
                byte[] pixels = new byte[8*width];
                byte[] buf = null;

                if (isRaw(variant)) {
                    buf = new byte[width];
                }
                
                // Read 8 rows at a time
                for (int row = 0; row < height; row += 8) {
                    int rows = Math.min(8, height - row);
                    int len = (rows*width + 7)/8;
                    
                    if (isRaw(variant)) {
                        int nread = 0;
                        while (nread < len) {
                            nread += input.read(buf, nread, len - nread);
                        }
                        
                        for (int i = 0; i < len; i++) {
                            int b = buf[i];
                            pixels[8*i]     = (byte)((b >> 7) & 0x1);
                            pixels[8*i + 1] = (byte)((b >> 6) & 0x1);
                            pixels[8*i + 2] = (byte)((b >> 5) & 0x1);
                            pixels[8*i + 3] = (byte)((b >> 4) & 0x1);
                            pixels[8*i + 4] = (byte)((b >> 3) & 0x1);
                            pixels[8*i + 5] = (byte)((b >> 2) & 0x1);
                            pixels[8*i + 6] = (byte)((b >> 1) & 0x1);
                            pixels[8*i + 7] = (byte)(b & 0x1);
                        }
                    } else {
                        for (int i = 0; i < rows*width; i++) {
                            pixels[i] = (byte)readInteger(input);
                        }
                    }
                    
                    sampleModel.setDataElements(tileRect.x,
                                                row,
                                                tileRect.width,
                                                rows,
                                                pixels,
                                                dataBuffer);
                    
                }
                break;

            case PGM_ASCII:
            case PGM_RAW:
            case PPM_ASCII:
            case PPM_RAW:
                // SampleModel for these cases should be PixelInterleaved.
                int size = width*height*numBands;

                switch (dataType) {
                case DataBuffer.TYPE_BYTE:
                    DataBufferByte bbuf =
                        (DataBufferByte)theTile.getDataBuffer();
                    byte[] byteArray = bbuf.getData();
                    if (isRaw(variant)) {
                        input.readFully(byteArray);

                        if (numBands == 3) {
                            for (int i = 0; i < size; i += 3) {
                                byte tmp = byteArray[i];
                                byteArray[i] = byteArray[i + 2];
                                byteArray[i+2] = tmp;
                            }
                        }
                    } else {
                        if (numBands == 1) {
                            for (int i = 0; i < size; i++) {
                                byteArray[i] = (byte)readInteger(input);
                            }
                        } else {
                            for (int i = 0; i < size; i += 3) {
                                byteArray[i + 2] = (byte)readInteger(input);
                                byteArray[i + 1] = (byte)readInteger(input);
                                byteArray[i]     = (byte)readInteger(input);
                            }
                        }
                    }
                    break;

                case DataBuffer.TYPE_USHORT:
                    DataBufferUShort sbuf =
                        (DataBufferUShort)theTile.getDataBuffer();
                    short[] shortArray = sbuf.getData();
                    if (numBands == 1) {
                        for (int i = 0; i < size; i++) {
                            shortArray[i] = (short)readInteger(input);
                        }
                    } else {
                        for (int i = 0; i < size; i += 3) {
                            shortArray[i + 2] = (short)readInteger(input);
                            shortArray[i + 1] = (short)readInteger(input);
                            shortArray[i]     = (short)readInteger(input);
                        }
                    }
                    break;

                case DataBuffer.TYPE_INT:
                    DataBufferInt ibuf =
                        (DataBufferInt)theTile.getDataBuffer();
                    int[] intArray = ibuf.getData();
                    if (numBands == 1) {
                        for (int i = 0; i < size; i++) {
                            intArray[i] = readInteger(input);
                        }
                    } else {
                        for (int i = 0; i < size; i += 3) {
                            intArray[i + 2] = readInteger(input);
                            intArray[i + 1] = readInteger(input);
                            intArray[i]     = readInteger(input);
                        }
                    }
                    break;
                }
                break;
            }

            // Close the PNM stream and release system resources.
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(
                             "IOException occured while processing PNM file.");
        }

        return theTile;
    }

    public synchronized Raster getTile(int tileX, int tileY) {
        if ((tileX != 0) || (tileY != 0)) {
            throw new IllegalArgumentException(
                              "Illegal tile requested from a SamplePNMImage.");
        }
        return computeTile(tileX, tileY);
    }
}
