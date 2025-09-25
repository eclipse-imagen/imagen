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

package org.eclipse.imagen;

import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;

/**
 * A subclass of <code>BorderExtender</code> that implements border extension by filling all pixels outside of the image
 * bounds with constant values. For example, the image:
 *
 * <p><center>
 *
 * <table border=1>
 * <tr align=center><td>A</td><td>B</td><td>C</td> </tr>
 * <tr align=center><td>D</td><td>E</td><td>F</td> </tr>
 * <tr align=center><td>G</td><td>H</td><td>I</td> </tr>
 * </table>
 *
 * </center> <br>
 * if extended by adding two extra rows to the top and bottom and two extra columns on the left and right sides, would
 * become:
 *
 * <p><center>
 *
 * <table border=1>
 * <tr align=center>
 * <td>X</td><td>X</td><td>X</td><td>X</td><td>X</td><td>X</td><td>X</td> </tr>
 * <td>X</td><td>X</td><td>X</td><td>X</td><td>X</td><td>X</td><td>X</td> </tr>
 * <td>X</td><td>X</td><td>A</td><td>B</td><td>C</td><td>X</td><td>X</td> </tr>
 * <td>X</td><td>X</td><td>D</td><td>E</td><td>F</td><td>X</td><td>X</td> </tr>
 * <td>X</td><td>X</td><td>G</td><td>H</td><td>I</td><td>X</td><td>X</td> </tr>
 * <td>X</td><td>X</td><td>X</td><td>X</td><td>X</td><td>X</td><td>X</td> </tr>
 * <td>X</td><td>X</td><td>X</td><td>X</td><td>X</td><td>X</td><td>X</td> </tr>
 * </table>
 *
 * </center>
 *
 * <p>where X is the constant fill value. The set of constants is clamped to the range and precision of the data type of
 * the <code>WritableRaster</code> being filled. The number of constants used is given by the number of bands of the
 * <code>WritableRaster</code>. If the <code>WritableRaster</code> has <code>b</code> bands, and there are <code>c
 * </code> constants, constants <code>0</code> through <code>b - 1</code> are used when <code>b <= c</code>. If there is
 * only a single constant, then it is used for all bands. If <code>b > c</code>, an <code>UnsupportedOperationException
 * </code> is thrown.
 *
 * @see BorderExtender
 */
public final class BorderExtenderConstant extends BorderExtender {

    private double[] constants;

    /**
     * Constructs an instance of <code>BorderExtenderConstant</code> with a given set of constants. The constants are
     * specified as an array of <code>double</code>s.
     */
    public BorderExtenderConstant(double[] constants) {
        this.constants = constants;
    }

    private int clamp(int band, int min, int max) {
        int length = constants.length;
        double c;
        if (length == 1) {
            c = constants[0];
        } else if (band < length) {
            c = constants[band];
        } else {
            throw new UnsupportedOperationException(ImageNI18N.getString("BorderExtenderConstant0"));
        }

        return (c > min) ? ((c > max) ? max : (int) c) : min;
    }

    /**
     * Returns a clone of the <code>constants</code> array originally supplied to the constructor.
     *
     * @since JAI 1.1.2
     */
    public final double[] getConstants() {
        return (double[]) constants;
    }

    /**
     * Fills in the portions of a given <code>Raster</code> that lie outside the bounds of a given <code>PlanarImage
     * </code> with constant values.
     *
     * <p>The portion of <code>raster</code> that lies within <code>im.getBounds()</code> is not altered.
     *
     * @param raster The <code>WritableRaster</code> the border area of which is to be filled with constants.
     * @param im The <code>PlanarImage</code> which determines the portion of the <code>WritableRaster</code> <i>not</i>
     *     to be filled.
     * @throws <code>IllegalArgumentException</code> if either parameter is <code>null</code>.
     * @throws <code>UnsupportedOperationException</code> if the number of image bands exceeds the number of constants
     *     and the latter is not unity.
     */
    public final void extend(WritableRaster raster, PlanarImage im) {

        if (raster == null || im == null) {
            throw new IllegalArgumentException(ImageNI18N.getString("Generic0"));
        }

        int width = raster.getWidth();
        int height = raster.getHeight();
        int numBands = raster.getNumBands();

        int minX = raster.getMinX();
        int maxX = minX + width;
        int minY = raster.getMinY();
        int maxY = minY + height;

        int validMinX = Math.max(im.getMinX(), minX);
        int validMaxX = Math.min(im.getMaxX(), maxX);
        int validMinY = Math.max(im.getMinY(), minY);
        int validMaxY = Math.min(im.getMaxY(), maxY);

        int row, index;

        int dataType = raster.getSampleModel().getDataType();
        if (dataType == DataBuffer.TYPE_FLOAT) {
            float[] fBandData = new float[numBands];
            for (int b = 0; b < numBands; b++) {
                fBandData[b] = (b < constants.length) ? (float) constants[b] : 0.0F;
            }
            float[] fData = new float[width * numBands];
            index = 0;
            for (int i = 0; i < width; i++) {
                for (int b = 0; b < numBands; b++) {
                    fData[index++] = fBandData[b];
                }
            }

            if (validMinX > validMaxX || validMinY > validMaxY) {
                // Raster does not intersect image.
                for (row = minY; row < maxY; row++) {
                    raster.setPixels(minX, row, width, 1, fData);
                }
            } else {
                for (row = minY; row < validMinY; row++) {
                    raster.setPixels(minX, row, width, 1, fData);
                }
                for (row = validMinY; row < validMaxY; row++) {
                    if (minX < validMinX) {
                        raster.setPixels(minX, row, validMinX - minX, 1, fData);
                    }
                    if (validMaxX < maxX) {
                        raster.setPixels(validMaxX, row, maxX - validMaxX, 1, fData);
                    }
                }
                for (row = validMaxY; row < maxY; row++) {
                    raster.setPixels(minX, row, width, 1, fData);
                }
            }
        } else if (dataType == DataBuffer.TYPE_DOUBLE) {
            double[] dBandData = new double[numBands];
            for (int b = 0; b < numBands; b++) {
                dBandData[b] = (b < constants.length) ? constants[b] : 0.0;
            }
            double[] dData = new double[width * numBands];
            index = 0;
            for (int i = 0; i < width; i++) {
                for (int b = 0; b < numBands; b++) {
                    dData[index++] = dBandData[b];
                }
            }

            if (validMinX > validMaxX || validMinY > validMaxY) {
                // Raster does not intersect image.
                for (row = minY; row < maxY; row++) {
                    raster.setPixels(minX, row, width, 1, dData);
                }
            } else {
                for (row = minY; row < validMinY; row++) {
                    raster.setPixels(minX, row, width, 1, dData);
                }
                for (row = validMinY; row < validMaxY; row++) {
                    if (minX < validMinX) {
                        raster.setPixels(minX, row, validMinX - minX, 1, dData);
                    }
                    if (validMaxX < maxX) {
                        raster.setPixels(validMaxX, row, maxX - validMaxX, 1, dData);
                    }
                }
                for (row = validMaxY; row < maxY; row++) {
                    raster.setPixels(minX, row, width, 1, dData);
                }
            }
        } else {
            int[] iBandData = new int[numBands];
            switch (dataType) {
                case DataBuffer.TYPE_BYTE:
                    for (int b = 0; b < numBands; b++) {
                        iBandData[b] = clamp(b, 0, 255);
                    }
                    break;
                case DataBuffer.TYPE_SHORT:
                    for (int b = 0; b < numBands; b++) {
                        iBandData[b] = clamp(b, Short.MIN_VALUE, Short.MAX_VALUE);
                    }
                    break;
                case DataBuffer.TYPE_USHORT:
                    for (int b = 0; b < numBands; b++) {
                        iBandData[b] = clamp(b, 0, 65535);
                    }
                    break;
                case DataBuffer.TYPE_INT:
                    for (int b = 0; b < numBands; b++) {
                        iBandData[b] = clamp(b, Integer.MIN_VALUE, Integer.MAX_VALUE);
                    }
                    break;
                default:
                    throw new IllegalArgumentException(ImageNI18N.getString("Generic3"));
            }

            int[] iData = new int[width * numBands];
            index = 0;
            for (int i = 0; i < width; i++) {
                for (int b = 0; b < numBands; b++) {
                    iData[index++] = iBandData[b];
                }
            }

            if (validMinX > validMaxX || validMinY > validMaxY) {
                // Raster does not intersect image.
                for (row = minY; row < maxY; row++) {
                    raster.setPixels(minX, row, width, 1, iData);
                }
            } else {
                for (row = minY; row < validMinY; row++) {
                    raster.setPixels(minX, row, width, 1, iData);
                }
                for (row = validMinY; row < validMaxY; row++) {
                    if (minX < validMinX) {
                        raster.setPixels(minX, row, validMinX - minX, 1, iData);
                    }
                    if (validMaxX < maxX) {
                        raster.setPixels(validMaxX, row, maxX - validMaxX, 1, iData);
                    }
                }
                for (row = validMaxY; row < maxY; row++) {
                    raster.setPixels(minX, row, width, 1, iData);
                }
            }
        }
    }
}
