/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 *
 * This Example Content is intended to demonstrate usage of Eclipse technology. It is
 * provided to you under the terms and conditions of the Eclipse Distribution License
 * v1.0 which is available at http://www.eclipse.org/org/documents/edl-v10.php.
 */
package org.eclipse.imagen.demo.medical;

import java.awt.Point;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.awt.image.Raster;
import java.io.IOException;
import java.io.RandomAccessFile;
import org.eclipse.imagen.RasterFactory;
import org.eclipse.imagen.media.codecimpl.SimpleRenderedImage;

/**
 * An DICOM image reader that wraps the <code>DicomImage</code> class. This reader accepts the file name for a DICOM
 * image file as input, and send this file name to the constructor of the <code>DicomImage</code> object.
 *
 * <p>Also, a convenient method <code>getDicomImage</code> is provided in this class to get the memory image.
 */
public class DicomDecoder {
    /** The name of the file storing the DICOM image */
    private String fileName;

    /** Constructor. Create the <code>DicomDecoder</code> object. */
    public DicomDecoder(String fileName) {
        this.fileName = fileName;
    }

    /** Return a <code>DicomImage</code> object decoded from the provided DICOM image file. */
    public DicomImage getDicomImage() {
        return new DicomImage(fileName);
    }
}

/**
 * A class defines the decoded 12-bit DICOM image. This class contains a single-tile short-type image in a little-endian
 * file format. A list of primary medical image parameters such as image width, image height, image position, image
 * orientation, and etc are decoded and stored in this class.
 *
 * <p>Note that this simple DICOM decoder may not support some of the DICOM images. In this case, the user should extend
 * the functionality of this class or write their own DICOM decoder.
 */
class DicomImage extends SimpleRenderedImage {

    /**
     * Indicate little endian or big endian. It is better to set as a decoding parameter. Some of the DICOM image files
     * contains a tag to indicate the big-endian or little-endian but has to be decoded in both little-big-endian to
     * find this tag. As for this demo only supports little-endian 12-bit DICOM image, this boolean flag is set to
     * <code>true</code>.
     */
    private boolean littleEndian = true;

    /** The name of the image file storing this DICOM image. */
    private String fileName;

    /**
     * The image type, e. g., PRIMARY, ORIGINAL, and etc. Attribute identifies important image identification
     * characteristics. For the detailed definition, see C.7.6.1.1.2 in the DICOM standard part 3.
     */
    private String imageType;

    /**
     * The type of the equipment that acquires this data: e. g., CT, MR and etc. See C.7.3.1.1.1 in the DICOM standard
     * part 3.
     */
    private String imageModality;

    /** The nominal slice thickness, in <code>mm</code>. */
    private double sliceThickness;

    /**
     * Spacing between slices, in <code>mm</code>. The spacing is measured from center-to-center of each slice along the
     * normal to the image.
     */
    private double sliceSpacing;

    /** The angle of gantry tilt, in degrees. See C.8.9.1.1.7 in the DICOM standard part 3. */
    private double gantryTilt;

    /** The distance in mm of the top of the patient table to the center of rotation; below the center is positive. */
    private double tableHeight;

    /**
     * The patient position descriptor relative to the equipment. See C.7.3.1.1.2 in the DICOM standard part 3. Valid
     * value examples are HFP (head first prone), HFS (head-first supine).
     */
    private String patientPosition;

    /**
     * Patient direction of the rows and columns of the image. See C.7.6.1.1.1 in the DICOM standard part 3 for the
     * detail.
     */
    private String patientOrientation;

    /**
     * The 3D coordinates of the up-left hand corner (center of the first pixel transmitted) of the image pixel in the
     * physical, in <code>mm</code>. See C.7.6.2.1.1 in the DICOM standard part 3 for the detail.
     */
    private double[] imagePosition;

    /**
     * The direction cosine of the first row and the first column with respect to the patient. See C.7.6.2.1.1 in the
     * DICOM standard part 3 for the detail.
     */
    private double[] imageOrientation;

    /**
     * The relative position of exposure expressed in <code>mm</code>. See C.7.6.2.1.2 in the DICOM standard part 3 for
     * the detail.
     */
    private double slicePosition;

    /** The number of samples per voxel. */
    private short samplePerPixel;

    /** The physical distance between the center of each pixel, expressed in <code>mm</code>. */
    private double[] pixelSpacing;

    /** Ratio of the vertical and horizontal size of the pixel. */
    private double pixelAspectRatio;

    /** Number of bits allocated for each pixel sample. */
    private short bitsAllocated;

    /** Number of bits stored for each pixel sample. */
    private short bitsStored;

    /** Most significant bit for the pixel sample. */
    private short highBit;

    /** Data representation of the pixel sample. Enumerated values: unsigned integer or 2's complement. */
    private short pixelRepresentation;

    /** The minimum pixel value encountered in this image. */
    private short minimum;

    /** The maximum pixel value encountered in this image. */
    private short maximum;

    /** The position of the data */
    private long dataPos;

    /** RandomAccessFile generated from the provided file. */
    private RandomAccessFile file;

    /**
     * Constructor. It accepts the name of a DICOM image file, loads the tag data and parses some of the important tags
     * into instance variables, load the data into a short-type buffer and creates the sample model for this image.
     */
    DicomImage(String fileName) {
        this.fileName = fileName;
        try {
            // create the input stream pipeline
            file = new RandomAccessFile(fileName, "r");

            // decode the tag and record the data position
            while (getNextDataElement())
                ;
        } catch (Exception e) {
            e.printStackTrace();
        }

        // create a short sample model based on the image size.
        if (bitsAllocated <= 8)
            sampleModel = RasterFactory.createPixelInterleavedSampleModel(DataBuffer.TYPE_BYTE, width, height, 1);
        else if (bitsAllocated <= 16 && pixelRepresentation == 0)
            sampleModel = RasterFactory.createPixelInterleavedSampleModel(DataBuffer.TYPE_USHORT, width, height, 1);
        else if (bitsAllocated <= 16 && pixelRepresentation == 1)
            sampleModel = RasterFactory.createPixelInterleavedSampleModel(DataBuffer.TYPE_SHORT, width, height, 1);
    }

    /** Return the pixel size in X-direction, in <code>mm</code>. */
    public double getPixelSizeX() {
        return pixelSpacing[0];
    }

    /** Return the pixel size in Y-direction, in <code>mm</code>. */
    public double getPixelSizeY() {
        if (pixelAspectRatio > 0.0) {
            if (pixelSpacing[1] > 0.0) return pixelSpacing[1] * pixelAspectRatio;
            return pixelSpacing[0] * pixelAspectRatio;
        } else return pixelSpacing[0];
    }

    /** Return the maximum pixel value in this image. */
    public int getMaximum() {
        return maximum;
    }

    /** Return the minimum pixel value of this image. */
    public int getMinimum() {
        return minimum;
    }

    /** Read the next DICOM tag from the provided input stream and parse it. */
    private boolean getNextDataElement() {
        try {
            int group = file.readInt();
            int length = file.readInt();

            // identify the group and element
            short grp = (short) ((group >> 16) & 0xffff);
            short elm = (short) (group & 0xffff);

            // if little-endian, swap the bytes.
            if (littleEndian) {
                length = swap(length);
                grp = swap(grp);
                elm = swap(elm);
                group = (grp << 16) + elm;
            }

            // create buffer and read the tag data.
            if (group == 0x7fe00010) {
                dataPos = file.getFilePointer();
                file.seek(dataPos + length);
            } else {
                byte[] buf = new byte[length];
                file.readFully(buf);

                // decode the data.
                retrieveData(group, buf);
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /** Swap the bytes of the provided integer in the reverse order. */
    private int swap(int i) {
        int j = i >>> 24;
        j |= ((i >>> 16) & 0xff) << 8;
        j |= ((i >>> 8) & 0xff) << 16;
        j |= (i & 0xff) << 24;
        return j;
    }

    /** Swap the bytes of the provided short integer in the reverse order. */
    short swap(short i) {
        return (short) (((i >>> 8) & 0xff) | ((i & 0xff) << 8));
    }

    /** Parse a little-endian short integer from a byte array. */
    short parseShort(byte[] buf) {
        return (short) ((buf[0] & 0xff) | ((buf[1] & 0xff) << 8));
    }

    /** Parse the double number from the byte array of the tag data. */
    double parseDouble(byte[] buf) {
        String s = new String(buf);
        return (new Double(s)).doubleValue();
    }

    /** Parse the double array from the byte array of the tag data. */
    double[] parseDoubleArray(byte[] buf) {
        String s = new String(buf);
        double[] darray = new double[(s.length() + 8) / 9];
        int pos;
        int index = 0;

        // the delimiter is '\'
        while ((pos = s.indexOf("\\")) >= 0) {
            String sub = s.substring(0, pos - 1);
            darray[index++] = (new Double(sub)).doubleValue();
            s = s.substring(pos + 1);
        }

        // for the last double
        if (s.length() >= 8) darray[index++] = (new Double(s)).doubleValue();

        // copy if the decoded number less than expected.
        if (index < darray.length) {
            double[] darray1 = new double[index];
            for (int i = 0; i < index; i++) darray1[i] = darray[i];

            darray = darray1;
        }

        return darray;
    }

    /** Retrieve the parsed data from the tag data and store them into the instance variables. */
    void retrieveData(int group, byte[] buf) {
        switch (group) {
            case 0x00080008: // image type
                imageType = new String(buf);
                break;
            case 0x00080060: // imaging modality
                imageModality = new String(buf);
                break;

            case 0x00180050: // slice thickness
                sliceThickness = parseDouble(buf);
                break;

            case 0x00180088: // slice spacing
                sliceSpacing = parseDouble(buf);
                break;
            case 0x00181120: // gantry tilt angle
                gantryTilt = parseDouble(buf);
                break;

            case 0x00181130: // table height
                tableHeight = parseDouble(buf);
                break;

            case 0x00185100: // patient position
                patientPosition = new String(buf);
                break;

            case 0x00200020: // patient orientation
                patientOrientation = new String(buf);
                break;

            case 0x00200032: // image position
                imagePosition = parseDoubleArray(buf);
                break;

            case 0x00200037: // image orientation cosine
                imageOrientation = parseDoubleArray(buf);
                break;

            case 0x00201041: // slice position
                slicePosition = parseDouble(buf);
                break;

            case 0x00280002: // samples per pixel
                samplePerPixel = parseShort(buf);
                break;

            case 0x00280010: // image height
                tileHeight = height = parseShort(buf);
                break;

            case 0x00280011: // image width
                tileWidth = width = parseShort(buf);
                break;

            case 0x00280030: // pixel spacings
                pixelSpacing = parseDoubleArray(buf);
                break;

            case 0x00200034: // pixel aspect ratios
                pixelAspectRatio = parseDouble(buf);
                break;

            case 0x00280100: // bits allocated
                bitsAllocated = parseShort(buf);
                break;

            case 0x00280101: // bits stored
                bitsStored = parseShort(buf);
                break;

            case 0x00280102: // high bit
                highBit = parseShort(buf);
                break;

            case 0x00280103: // pixel representation
                pixelRepresentation = parseShort(buf);
                break;

            case 0x00280106: // smallest pixel value
                minimum = parseShort(buf);
                break;

            case 0x00280107: // maximum pixel value
                maximum = parseShort(buf);
                break;
        }
    }

    /**
     * Get the tile. As for after reading, this image only have one tile. So for the other tile position, an exception
     * is thrown.
     *
     * @throws IllegalArgumentException When the tile position is not <code>(0, 0)</code>.
     */
    public synchronized Raster getTile(int tileX, int tileY) {
        if ((tileX != 0) || (tileY != 0)) throw new IllegalArgumentException("Wrong tile");

        return computeTile(tileX, tileY);
    }

    /**
     * Create the single tile of this image. The provided tile position is ignored. The sample model of the tile ratser
     * is the sample model of this image. The location of the tile ratser is set to <code>(0, 0)</code>.
     */
    public synchronized Raster computeTile(int tileX, int tileY) {
        int type = sampleModel.getDataType();
        try {
            file.seek(dataPos);
        } catch (IOException e) {
            e.printStackTrace();
        }

        DataBuffer db = null;
        if (type == DataBuffer.TYPE_BYTE) {
            byte[] buf = new byte[width * height];
            try {
                file.readFully(buf);
            } catch (IOException e) {
                e.printStackTrace();
            }

            db = new DataBufferByte(buf, buf.length);
        } else if (type == DataBuffer.TYPE_USHORT) {
            byte[] buf = new byte[width * 2];
            short[] shortBuf = new short[width * height];

            for (int i = 0, k = 0; i < height; i++) {
                try {
                    file.readFully(buf);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (littleEndian)
                    for (int j = 0; j < buf.length; j += 2, k++) {
                        shortBuf[k] = (short) ((buf[j] & 0xff) + ((buf[j + 1] & 0xff) << 8));
                    }
                else
                    for (int j = 0; j < buf.length; j += 2, k++) {
                        shortBuf[k] = (short) ((buf[j + 1] & 0xff) + ((buf[j] & 0xff) << 8));
                    }
            }

            int min = 0;
            for (int i = shortBuf.length - 1; i >= 0; i--) if (min > shortBuf[i]) min = shortBuf[i];

            if (min < 0) for (int i = shortBuf.length - 1; i >= 0; i--) shortBuf[i] += 1024;

            db = new DataBufferUShort(shortBuf, shortBuf.length);
        } else if (type == DataBuffer.TYPE_SHORT) {
            byte[] buf = new byte[width * 2];
            short[] shortBuf = new short[width * height];

            for (int i = 0, k = 0; i < height; i++) {
                try {
                    file.readFully(buf);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (littleEndian)
                    for (int j = 0; j < buf.length; j += 2, k++) {
                        byte low = buf[j];
                        byte high = buf[j + 1];
                        // for signed data. ignore negative value
                        shortBuf[k] = (short) ((low & 0xff) + ((high & 0xff) << 8));
                    }
                else
                    for (int j = 0; j < buf.length; j += 2, k++) {
                        byte low = buf[j + 1];
                        byte high = buf[j];
                        // for signed data. ignore negative value
                        shortBuf[k] = (short) ((low & 0xff) + ((high & 0xff) << 8));
                    }
            }

            db = new DataBufferShort(shortBuf, shortBuf.length);
        }

        return Raster.createWritableRaster(sampleModel, db, new Point(0, 0));
    }
}
