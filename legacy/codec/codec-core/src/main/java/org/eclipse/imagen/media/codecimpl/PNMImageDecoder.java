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

package org.eclipse.imagen.media.codecimpl;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferUShort;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import org.eclipse.imagen.RasterFactory;
import org.eclipse.imagen.media.codec.ImageCodec;
import org.eclipse.imagen.media.codec.ImageDecodeParam;
import org.eclipse.imagen.media.codec.ImageDecoderImpl;
import org.eclipse.imagen.media.codec.SeekableStream;
import org.eclipse.imagen.media.codecimpl.util.ImagingException;

/** @since EA2 */
public class PNMImageDecoder extends ImageDecoderImpl {

    public PNMImageDecoder(SeekableStream input, ImageDecodeParam param) {
        super(input, param);
    }

    public RenderedImage decodeAsRenderedImage(int page) throws IOException {
        if (page != 0) {
            throw new IOException(JaiI18N.getString("PNMImageDecoder5"));
        }
        try {
            return new PNMImage(input);
        } catch (Exception e) {
            throw CodecUtils.toIOException(e);
        }
    }
}

class PNMImage extends SimpleRenderedImage {

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
    private Raster theTile;

    private int numBands;

    private int dataType;

    /**
     * Construct a PNMImage.
     *
     * @param input The SeekableStream for the PNM file.
     */
    public PNMImage(SeekableStream input) {
        theTile = null;

        this.input = input;

        String ls = System.lineSeparator();
        lineSeparator = ls.getBytes();

        // Read file header.
        try {
            if (this.input.read() != 'P') { // magic number
                throw new RuntimeException(JaiI18N.getString("PNMImageDecoder0"));
            }

            variant = this.input.read(); // file variant
            if ((variant < PBM_ASCII) || (variant > PPM_RAW)) {
                throw new RuntimeException(JaiI18N.getString("PNMImageDecoder1"));
            }

            width = readInteger(this.input); // width
            height = readInteger(this.input); // height

            if (variant == PBM_ASCII || variant == PBM_RAW) {
                maxValue = 1;
            } else {
                maxValue = readInteger(this.input); // maximum value
            }
        } catch (IOException e) {
            String message = JaiI18N.getString("PNMImageDecoder6");
            sendExceptionToListener(message, e);
            //            e.printStackTrace();
            //            throw new RuntimeException(JaiI18N.getString("PNMImageDecoder2"));
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
            sampleModel = new MultiPixelPackedSampleModel(DataBuffer.TYPE_BYTE, width, height, 1);
            colorModel = ImageCodec.createGrayIndexColorModel(sampleModel, false);
        } else {
            int[] bandOffsets = numBands == 1 ? new int[] {0} : new int[] {0, 1, 2};
            sampleModel = RasterFactory.createPixelInterleavedSampleModel(
                    dataType, tileWidth, tileHeight, numBands, tileWidth * numBands, bandOffsets);

            colorModel = ImageCodec.createComponentColorModel(sampleModel);
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
            char c = (char) b;
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
        // Create a new tile.
        Point org = new Point(tileXToX(tileX), tileYToY(tileY));
        WritableRaster tile = Raster.createWritableRaster(sampleModel, org);
        Rectangle tileRect = tile.getBounds();

        // There should only be one tile.
        try {
            switch (variant) {
                case PBM_ASCII:
                case PBM_RAW:
                    // SampleModel for these cases should be MultiPixelPacked.

                    DataBuffer dataBuffer = tile.getDataBuffer();
                    if (isRaw(variant)) {
                        // Read the entire image.
                        byte[] buf = ((DataBufferByte) dataBuffer).getData();
                        input.readFully(buf, 0, buf.length);
                    } else {
                        // Read 8 rows at a time
                        byte[] pixels = new byte[8 * width];
                        int offset = 0;
                        for (int row = 0; row < tileHeight; row += 8) {
                            int rows = Math.min(8, tileHeight - row);
                            int len = (rows * width + 7) / 8;

                            for (int i = 0; i < rows * width; i++) {
                                pixels[i] = (byte) readInteger(input);
                            }
                            sampleModel.setDataElements(tileRect.x, row, tileRect.width, rows, pixels, dataBuffer);
                        }
                    }
                    break;

                case PGM_ASCII:
                case PGM_RAW:
                case PPM_ASCII:
                case PPM_RAW:
                    // SampleModel for these cases should be PixelInterleaved.
                    int size = width * height * numBands;

                    switch (dataType) {
                        case DataBuffer.TYPE_BYTE:
                            DataBufferByte bbuf = (DataBufferByte) tile.getDataBuffer();
                            byte[] byteArray = bbuf.getData();
                            if (isRaw(variant)) {
                                input.readFully(byteArray);
                            } else {
                                for (int i = 0; i < size; i++) {
                                    byteArray[i] = (byte) readInteger(input);
                                }
                            }
                            break;

                        case DataBuffer.TYPE_USHORT:
                            DataBufferUShort sbuf = (DataBufferUShort) tile.getDataBuffer();
                            short[] shortArray = sbuf.getData();
                            for (int i = 0; i < size; i++) {
                                shortArray[i] = (short) readInteger(input);
                            }
                            break;

                        case DataBuffer.TYPE_INT:
                            DataBufferInt ibuf = (DataBufferInt) tile.getDataBuffer();
                            int[] intArray = ibuf.getData();
                            for (int i = 0; i < size; i++) {
                                intArray[i] = readInteger(input);
                            }
                            break;
                    }
                    break;
            }

            // Close the PNM stream and release system resources.
            input.close();
        } catch (IOException e) {
            String message = JaiI18N.getString("PNMImageDecoder7");
            sendExceptionToListener(message, e);
            //            e.printStackTrace();
            //            throw new RuntimeException(JaiI18N.getString("PNMImageDecoder3"));
        }

        return tile;
    }

    public synchronized Raster getTile(int tileX, int tileY) {
        if ((tileX != 0) || (tileY != 0)) {
            throw new IllegalArgumentException(JaiI18N.getString("PNMImageDecoder4"));
        }

        if (theTile == null) {
            theTile = computeTile(tileX, tileY);
        }

        return theTile;
    }

    public void dispose() {
        theTile = null;
    }

    private void sendExceptionToListener(String message, Exception e) {
        ImagingListenerProxy.errorOccurred(message, new ImagingException(message, e), this, false);
    }
}
