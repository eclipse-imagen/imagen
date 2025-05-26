/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 *
 * This Example Content is intended to demonstrate usage of Eclipse technology. It is
 * provided to you under the terms and conditions of the Eclipse Distribution License
 * v1.0 which is available at http://www.eclipse.org/org/documents/edl-v10.php.
 */
package org.eclipse.imagen.demo.codec;

import java.awt.Rectangle;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.io.IOException;
import java.io.OutputStream;
import org.eclipse.imagen.media.codec.ImageEncodeParam;
import org.eclipse.imagen.media.codec.ImageEncoderImpl;
import org.eclipse.imagen.media.codec.PNMEncodeParam;

/**
 * An <code>ImageEncoder</code> for the PNM family of file formats.
 *
 * <p>The PNM file format includes PBM for monochrome images, PGM for grey scale images, and PPM for color images. When
 * writing the source data out, the encoder chooses the appropriate file variant based on the actual SampleModel of the
 * source image. In case the source image data is unsuitable for the PNM file format, for example when source has 4
 * bands or float data type, the encoder throws an Error.
 *
 * <p>The raw file format is used wherever possible, unless the PNMEncodeParam object supplied to the constructor
 * returns <code>true</code> from its <code>getRaw()</code> method.
 */
public class SamplePNMImageEncoder extends ImageEncoderImpl {

    private static final int PBM_ASCII = '1';
    private static final int PGM_ASCII = '2';
    private static final int PPM_ASCII = '3';
    private static final int PBM_RAW = '4';
    private static final int PGM_RAW = '5';
    private static final int PPM_RAW = '6';

    private static final int SPACE = ' ';

    private static final String COMMENT = "# written by SamplePNMImageEncoder";

    private byte[] lineSeparator;

    private int variant;
    private int maxValue;

    public SamplePNMImageEncoder(OutputStream output, ImageEncodeParam param) {
        super(output, param);
        if (this.param == null) {
            this.param = new PNMEncodeParam();
        }
    }

    /** Encodes a RenderedImage and writes the output to the OutputStream associated with this ImageEncoder. */
    public void encode(RenderedImage im) throws IOException {
        int minX = im.getMinX();
        int minY = im.getMinY();
        int width = im.getWidth();
        int height = im.getHeight();
        SampleModel sampleModel = im.getSampleModel();

        String ls = System.lineSeparator();
        lineSeparator = ls.getBytes();

        int dataType = sampleModel.getTransferType();
        if ((dataType == DataBuffer.TYPE_FLOAT) || (dataType == DataBuffer.TYPE_DOUBLE)) {
            throw new RuntimeException("Source image has float/double data type, unsuitable for PNM file format.");
        }

        // Raw data can only handle bytes, everything greater must be ASCII.
        int[] sampleSize = sampleModel.getSampleSize();
        int numBands = sampleModel.getNumBands();
        if (numBands == 1) {
            if (sampleSize[0] == 1) {
                variant = PBM_RAW;
            } else if (sampleSize[0] <= 8) {
                variant = PGM_RAW;
            } else {
                variant = PGM_ASCII;
            }
        } else if (numBands == 3) {
            if (sampleSize[0] <= 8) {
                variant = PPM_RAW;
            } else {
                variant = PPM_ASCII;
            }
        } else {
            throw new RuntimeException("Source image has unsuitable number of bands for PNM file format.");
        }

        // Read parameters
        if (((PNMEncodeParam) param).getRaw()) {
            if (!isRaw(variant)) {
                boolean canUseRaw = true;

                // Make sure sampleSize for all bands no greater than 8.
                for (int i = 0; i < sampleSize.length; i++) {
                    if (sampleSize[i] > 8) {
                        canUseRaw = false;
                        break;
                    }
                }

                if (canUseRaw) {
                    variant += 0x3;
                }
            }
        } else {
            if (isRaw(variant)) {
                variant -= 0x3;
            }
        }

        maxValue = (1 << sampleSize[0]) - 1;

        // Write PNM file.
        output.write('P'); // magic value
        output.write(variant);

        output.write(lineSeparator);
        output.write(COMMENT.getBytes()); // comment line

        output.write(lineSeparator);
        writeInteger(output, width); // width
        output.write(SPACE);
        writeInteger(output, height); // height

        // Writ esample max value for non-binary images
        if ((variant != PBM_RAW) && (variant != PBM_ASCII)) {
            output.write(lineSeparator);
            writeInteger(output, maxValue);
        }

        // The spec allows a single character between the
        // last header value and the start of the raw data.
        if (variant == PBM_RAW || variant == PGM_RAW || variant == PPM_RAW) {
            output.write('\n');
        }

        // Buffer for up to 8 rows of pixels
        int[] pixels = new int[8 * width * numBands];

        // The index of the sample being written, used to
        // place a line separator after every 16th sample in
        // ASCII mode.  Not used in raw mode.
        int count = 0;

        // Process 8 rows at a time so all but the last will have
        // a multiple of 8 pixels.  This simplifies PBM_RAW encoding.
        int lastRow = minY + height;
        for (int row = minY; row < lastRow; row += 8) {
            int rows = Math.min(8, lastRow - row);
            int size = rows * width * numBands;

            // Grab the pixels
            Raster src = im.getData(new Rectangle(minX, row, width, rows));
            src.getPixels(minX, row, width, rows, pixels);

            switch (variant) {
                case PBM_ASCII:
                case PGM_ASCII:
                case PPM_ASCII:
                    for (int i = 0; i < size; i++) {
                        if ((count++ % 16) == 0) {
                            output.write(lineSeparator);
                        } else {
                            output.write(SPACE);
                        }
                        writeInteger(output, pixels[i]);
                    }
                    output.write(lineSeparator);
                    break;

                case PBM_RAW:
                    // 8 pixels packed into 1 byte, the leftovers are padded.
                    int tmp = size % 8;
                    size -= tmp;

                    for (int i = 0; i < size; i += 8) {
                        int b = ((pixels[i] & 0x1) << 7)
                                | ((pixels[i + 1] & 0x1) << 6)
                                | ((pixels[i + 2] & 0x1) << 5)
                                | ((pixels[i + 3] & 0x1) << 4)
                                | ((pixels[i + 4] & 0x1) << 3)
                                | ((pixels[i + 5] & 0x1) << 2)
                                | ((pixels[i + 6] & 0x1) << 1)
                                | (pixels[i + 7] & 0x1);
                        output.write(b);
                    }

                    // Leftover pixels, only possible at the end of the file.
                    if (tmp != 0) {
                        int b = 0x0;
                        for (int i = 0; i < tmp; i++) {
                            b |= (pixels[size + i] & 0x1) << (7 - i);
                        }
                        output.write(b);
                    }
                    break;

                case PGM_RAW:
                case PPM_RAW:
                    for (int i = 0; i < size; i++) {
                        output.write(pixels[i] & 0xFF);
                    }
                    break;
            }
        }

        // Force all buffered bytes to be written out.
        output.flush();
    }

    /** Writes an ineteger to the output in ASCII format. */
    private void writeInteger(OutputStream output, int i) throws IOException {
        output.write(Integer.toString(i).getBytes());
    }

    /** Returns true if file variant is raw format, false if ASCII. */
    private boolean isRaw(int v) {
        return (v >= PBM_RAW);
    }
}
