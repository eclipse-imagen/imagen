/* Copyright (c) 2025 Daniele Romagnoli and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution and is available at
 * http://www.opensource.org/licenses/apache2.0.php.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.eclipse.imagen.media.testclasses;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import org.eclipse.imagen.Interpolation;
import org.eclipse.imagen.ROI;
import org.eclipse.imagen.ROIShape;
import org.eclipse.imagen.RenderedOp;
import org.eclipse.imagen.media.interpolators.InterpolationBicubic;
import org.eclipse.imagen.media.interpolators.InterpolationBilinear;
import org.eclipse.imagen.media.interpolators.InterpolationNearest;
import org.eclipse.imagen.media.range.Range;
import org.eclipse.imagen.media.range.RangeFactory;
import org.junit.Test;

/** Base class for comparison tests. */
public class ComparisonTestBase extends TestBase {

    /** Default value for image width */
    private static int COMPARISON_DEFAULT_WIDTH = 256;

    /** Default value for image height */
    private static int COMPARISON_DEFAULT_HEIGHT = 256;

    // Root folder for all test outputs
    private static final Path ROOT_OUT_DIR = Paths.get("src/test", "resources");

    protected static final boolean WRITE_TEST = Boolean.getBoolean("imagen.test.write");
    private static final boolean PRINT_TEST = Boolean.getBoolean("imagen.test.print");
    private static final byte noDataB = 100;
    private static final short noDataUS = 100;
    private static final short noDataS = 100;
    private static final int noDataI = 100;
    private static final float noDataF = 100;
    private static final double noDataD = 100;

    /**
     * Create a test image
     *
     * @param dataType
     * @param width
     * @param height
     * @param isBinary
     * @param numBands
     * @return
     */
    public static RenderedImage createTestImage(int dataType, int width, int height, boolean isBinary, int numBands) {
        return createTestImage(dataType, width, height, getDefaultNoData(dataType), isBinary, numBands, null);
    }

    /**
     * Create a Default test image
     *
     * @param dataType
     * @param numBands
     * @return
     */
    public static RenderedImage createDefaultTestImage(int dataType, int numBands, boolean toggleFiller) {
        RenderedImage image;

        boolean previousFiller = IMAGE_FILLER;
        if (toggleFiller) {
            IMAGE_FILLER = true;
        }

        // Image creation
        switch (dataType) {
            case DataBuffer.TYPE_BYTE:
                image = createTestImage(
                        DataBuffer.TYPE_BYTE, COMPARISON_DEFAULT_WIDTH, COMPARISON_DEFAULT_HEIGHT, false, numBands);
                break;
            case DataBuffer.TYPE_USHORT:
                image = createTestImage(
                        DataBuffer.TYPE_USHORT, COMPARISON_DEFAULT_WIDTH, COMPARISON_DEFAULT_HEIGHT, false, numBands);
                break;
            case DataBuffer.TYPE_SHORT:
                image = createTestImage(
                        DataBuffer.TYPE_SHORT, COMPARISON_DEFAULT_WIDTH, COMPARISON_DEFAULT_HEIGHT, false, numBands);
                break;
            case DataBuffer.TYPE_INT:
                image = createTestImage(
                        DataBuffer.TYPE_INT, COMPARISON_DEFAULT_WIDTH, COMPARISON_DEFAULT_HEIGHT, false, numBands);
                break;
            case DataBuffer.TYPE_FLOAT:
                image = createTestImage(
                        DataBuffer.TYPE_FLOAT, COMPARISON_DEFAULT_WIDTH, COMPARISON_DEFAULT_HEIGHT, false, numBands);
                break;
            case DataBuffer.TYPE_DOUBLE:
                image = createTestImage(
                        DataBuffer.TYPE_DOUBLE, COMPARISON_DEFAULT_WIDTH, COMPARISON_DEFAULT_HEIGHT, false, numBands);
                break;
            default:
                throw new IllegalArgumentException("Wrong data type");
        }

        if (toggleFiller) {
            IMAGE_FILLER = previousFiller;
        }
        return image;
    }

    public static ROI getTestRoi(TestSelection testType) {
        boolean useRoi = testType == TestSelection.ROI_NO_DATA || testType == TestSelection.ROI_ONLY_DATA;
        ROI roi = null;
        if (useRoi) {
            Rectangle rect = new Rectangle(0, 0, COMPARISON_DEFAULT_WIDTH / 4, COMPARISON_DEFAULT_HEIGHT / 4);
            roi = new ROIShape(rect);
        }
        return roi;
    }

    public static Range getTestRange(int dataType, TestSelection testType) {
        Range range = null;
        boolean useRange = testType == TestSelection.ROI_NO_DATA || testType == TestSelection.NO_ROI_NO_DATA;
        if (useRange) {
            switch (dataType) {
                case DataBuffer.TYPE_BYTE:
                    range = RangeFactory.create(noDataB, true, noDataB, true);
                    break;
                case DataBuffer.TYPE_USHORT:
                    range = RangeFactory.createU(noDataUS, true, noDataUS, true);
                    break;
                case DataBuffer.TYPE_SHORT:
                    range = RangeFactory.create(noDataS, true, noDataS, true);
                    break;
                case DataBuffer.TYPE_INT:
                    range = RangeFactory.create(noDataI, true, noDataI, true);
                    break;
                case DataBuffer.TYPE_FLOAT:
                    range = RangeFactory.create(noDataF, true, noDataF, true, true);
                    break;
                case DataBuffer.TYPE_DOUBLE:
                    range = RangeFactory.create(noDataD, true, noDataD, true, true);
                    break;
                default:
                    throw new IllegalArgumentException("Wrong data type");
            }
        }
        return range;
    }

    @Test
    public void testBase() {
        testAllTypes(TestSelection.NO_ROI_ONLY_DATA);
    }

    /**
     * Get the suffix for the test name to be used for the output file
     *
     * @param testType
     * @param prefix
     * @return
     */
    public static String getSuffix(TestSelection testType, String prefix) {
        String suffix = prefix != null ? prefix : "";
        if (testType == TestSelection.ROI_NO_DATA) {
            suffix += "_ROI_NoData";
        } else if (testType == TestSelection.ROI_ONLY_DATA) {
            suffix += "_ROI";
        } else if (testType == TestSelection.NO_ROI_NO_DATA) {
            suffix += "_NoData";
        } else {
            suffix += "";
        }
        return suffix;
    }

    /**
     * Get the interpolation object
     *
     * @param dataType
     * @param interpolationType
     * @param range
     * @param destinationNoData
     * @return
     */
    public static Interpolation getInterpolation(
            int dataType, int interpolationType, Range range, double destinationNoData) {
        Interpolation interpolation;
        switch (interpolationType) {
            case 0:
                interpolation = new InterpolationNearest(range, false, destinationNoData, dataType);
                break;
            case 1:
                interpolation =
                        new InterpolationBilinear(DEFAULT_SUBSAMPLE_BITS, range, false, destinationNoData, dataType);
                break;
            case 2:
                interpolation = new InterpolationBicubic(
                        DEFAULT_SUBSAMPLE_BITS,
                        range,
                        false,
                        destinationNoData,
                        dataType,
                        true,
                        DEFAULT_PRECISION_BITS);
                break;
            default:
                throw new IllegalArgumentException("Wrong interpolation type");
        }
        return interpolation;
    }

    /**
     * get the suffix for the interpolation type
     *
     * @param interpolationType
     * @return
     */
    public static String getInterpolationSuffix(int interpolationType) {
        switch (interpolationType) {
            case 0:
                return "Nearest";
            case 1:
                return "Bilinear";
            case 2:
                return "Bicubic";
            default:
                throw new IllegalArgumentException("Wrong interpolation type");
        }
    }

    public void finalizeTest(String suffix, Integer dataType, RenderedImage image) {
        finalizeTest(suffix, dataType, image, true);
    }

    /**
     * Finalize the test by either writing the output image or comparing it with the reference one
     *
     * @param suffix
     * @param dataType
     * @param image
     * @param strictBounds
     */
    public void finalizeTest(String suffix, Integer dataType, RenderedImage image, boolean strictBounds) {
        String testName = dataType != null ? "test" + dataTypeName(dataType) : name.getMethodName();
        Path path;
        try {
            path = preparePath(testName, suffix);
            if (WRITE_TEST) {
                System.out.println("Saving image to: " + path.toAbsolutePath());
                TestImageDumper.saveAsDeflateTiff(path, image);
            } else {
                if (PRINT_TEST) {
                    System.out.println("Testing: " + testName + (suffix != null ? (" " + suffix) : ""));
                }
                BufferedImage expectedBI = readImage(path.toFile(), dataType);
                ImageComparator.assertEquals(expectedBI, image);
                disposeImage(image);
                disposeImage(expectedBI);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to ");
        }
    }

    /**
     * Prepare the output path for the test image
     *
     * @param testName
     * @param suffix
     * @return
     * @throws IOException
     */
    public static Path preparePath(String testName, String suffix) throws IOException {

        // Find the calling test class from the stack trace
        String testClassName = findCallingTestClass();
        System.out.println(testClassName);
        String packagePath = "";
        if (testClassName != null && testClassName.contains(".")) {
            testClassName = testClassName
                    .replace("it.geosolutions.jaiext", "org.eclipse.imagen.media")
                    .replace("testclasses", "");
            String pkg = testClassName.substring(0, testClassName.lastIndexOf('.'));
            packagePath = pkg.replace('.', '/');
        }

        // Build final output dir
        Path outDir = ROOT_OUT_DIR;
        if (!packagePath.isEmpty()) {
            outDir = outDir.resolve(packagePath).resolve("test-data");
        }
        Files.createDirectories(outDir);
        String safeName = testName.replaceAll("Old|New", "").replaceAll("[^a-zA-Z0-9_.-]", "_");
        safeName += (suffix == null || suffix.trim().isEmpty()) ? "" : suffix;
        return outDir.resolve(safeName + ".tif");
    }

    /**
     * Read an image from file
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static BufferedImage readImage(File file) throws IOException {
        return readImage(file, null);
    }

    /**
     * Read an image from file, using the TIFF ImageReader if the suggestedDataType is floating point
     *
     * @param file
     * @param suggestedDataType
     * @return
     * @throws IOException
     */
    public static BufferedImage readImage(File file, Integer suggestedDataType) throws IOException {
        System.out.println("Comparing image with: " + file.getAbsolutePath());
        if (suggestedDataType == DataBuffer.TYPE_FLOAT || suggestedDataType == DataBuffer.TYPE_DOUBLE) {
            // Only the TIFF reader from the JDK supports floating point data
            try (ImageInputStream iis = ImageIO.createImageInputStream(file)) {

                Iterator<ImageReader> it = ImageIO.getImageReaders(iis);
                while (it.hasNext()) {
                    ImageReader reader = it.next();
                    try {
                        String className = reader.getClass().getName();
                        if ("com.sun.imageio.plugins.tiff.TIFFImageReader".equals(className)) {
                            reader.setInput(iis, true, true);
                            return reader.read(0, null);
                        }
                    } finally {
                        reader.dispose();
                    }
                }
            }
        } else {
            return ImageIO.read(file);
        }
        throw new IOException("No TIFF ImageReader found");
    }

    public void testOperation(int dataType, TestSelection testType) {
        // empty implementation
    }

    /**
     * Dispose the image if needed
     *
     * @param image
     */
    public static void disposeImage(RenderedImage image) {
        // If the image is a PlanarImage or a TiledImage it has to be disposed
        if (image instanceof RenderedOp) {
            ((RenderedOp) image).dispose();
        }
    }

    private static String findCallingTestClass() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        String candidate = null;

        for (StackTraceElement el : stack) {
            String cls = el.getClassName();
            if (cls.startsWith("java.")
                    || cls.startsWith("sun.")
                    || cls.equals(TestImageDumper.class.getName())
                    || cls.endsWith("TestBase")) {
                continue; // skip infra/base classes
            }
            candidate = cls;
            break;
        }
        return candidate;
    }

    protected void testAllTypes(TestSelection testType) {
        for (int dataType = 0; dataType < 6; dataType++) {
            if (supportDataType(dataType)) {
                testOperation(dataType, testType);
            }
        }
    }

    private static String dataTypeName(int dataType) {
        switch (dataType) {
            case DataBuffer.TYPE_BYTE:
                return "Byte";
            case DataBuffer.TYPE_USHORT:
                return "UShort"; // unsigned 16-bit
            case DataBuffer.TYPE_SHORT:
                return "Short";
            case DataBuffer.TYPE_INT:
                return "Int";
            case DataBuffer.TYPE_FLOAT:
                return "Float";
            case DataBuffer.TYPE_DOUBLE:
                return "Double";
            default:
                return "Unknown";
        }
    }

    protected boolean supportDataType(int dataType) {
        return dataType == DataBuffer.TYPE_BYTE
                || dataType == DataBuffer.TYPE_USHORT
                || dataType == DataBuffer.TYPE_SHORT
                || dataType == DataBuffer.TYPE_INT
                || dataType == DataBuffer.TYPE_FLOAT
                || dataType == DataBuffer.TYPE_DOUBLE;
    }

    private static int getDefaultNoData(int dataType) {
        switch (dataType) {
            case DataBuffer.TYPE_BYTE:
                return noDataB;
            case DataBuffer.TYPE_USHORT:
                return noDataUS;
            case DataBuffer.TYPE_SHORT:
                return noDataS;
            case DataBuffer.TYPE_INT:
                return noDataI;
            case DataBuffer.TYPE_FLOAT:
                return (int) noDataF;
            case DataBuffer.TYPE_DOUBLE:
                return (int) noDataD;
            default:
                throw new IllegalArgumentException("Wrong data type");
        }
    }
}
