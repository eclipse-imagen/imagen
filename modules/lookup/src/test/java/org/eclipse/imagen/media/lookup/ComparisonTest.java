/* ImageN-Ext - OpenSource Java Advanced Image Extensions Library
*    http://www.geo-solutions.it/
*    Copyright 2014 GeoSolutions


* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at

* http://www.apache.org/licenses/LICENSE-2.0

* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.eclipse.imagen.media.lookup;

import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import org.eclipse.imagen.PlanarImage;
import org.eclipse.imagen.media.range.Range;
import org.eclipse.imagen.media.testclasses.ComparisonTestBase;
import org.junit.BeforeClass;

public class ComparisonTest extends ComparisonTestBase {

    /** Destination No Data value */
    private static double destinationNoDataValue;

    /** Byte image */
    private static RenderedImage testImageByte;

    /** Unsigned Short image */
    private static RenderedImage testImageUShort;

    /** Short image */
    private static RenderedImage testImageShort;

    /** Integer image */
    private static RenderedImage testImageInt;

    /** LookupTable from byte to byte */
    private static LookupTable byteToByteTableNew;

    /** LookupTable from ushort to byte */
    private static LookupTable ushortToByteTableNew;

    /** LookupTable from short to byte */
    private static LookupTable shortToByteTableNew;

    /** LookupTable from int to byte */
    private static LookupTable intToByteTableNew;

    // Initial static method for preparing all the test data
    @BeforeClass
    public static void initialSetup() {
        // Setting of the imaage filler parameter to false for a faster image creation
        IMAGE_FILLER = false;
        // Images initialization
        // Byte Range goes from 0 to 255
        byte noDataB = -100;
        short noDataUS = 100;
        short noDataS = -100;
        int noDataI = -100;
        // Image creations
        testImageByte = createTestImage(DataBuffer.TYPE_BYTE, DEFAULT_WIDTH, DEFAULT_HEIGHT, noDataB, false);
        testImageUShort = createTestImage(DataBuffer.TYPE_USHORT, DEFAULT_WIDTH, DEFAULT_HEIGHT, noDataUS, false);
        testImageShort = createTestImage(DataBuffer.TYPE_SHORT, DEFAULT_WIDTH, DEFAULT_HEIGHT, noDataS, false);
        testImageInt = createTestImage(DataBuffer.TYPE_INT, DEFAULT_WIDTH, DEFAULT_HEIGHT, noDataI, false);
        // Offset creation
        int byteOffset = 0;
        int ushortOffset = 0;
        int shortOffset = noDataS;
        int intOffset = noDataI;

        // Array Lookup creation
        int arrayLength = 201;
        int startValue = -100;
        // Array Initialization
        byte[] dataByteB = new byte[arrayLength];
        byte[] dataUShortB = new byte[arrayLength];
        byte[] dataShortB = new byte[arrayLength];
        byte[] dataIntB = new byte[arrayLength];
        // Construction of the various arrays
        for (int i = 0; i < arrayLength; i++) {
            dataByteB[i] = 0;
            dataUShortB[i] = 0;
            dataShortB[i] = 0;
            dataIntB[i] = 0;

            int value = i + startValue;

            if (value == noDataI) {
                dataShortB[i] = 50;
                dataIntB[i] = 50;
                dataByteB[i] = 50;
            }

            if (i == noDataUS) {
                // ushort-to-all arrays
                dataUShortB[i] = 50;
            }
        }

        // LookupTables creation
        byteToByteTableNew = new LookupTable(dataByteB, byteOffset);

        ushortToByteTableNew = new LookupTable(dataUShortB, ushortOffset);

        shortToByteTableNew = new LookupTable(dataShortB, shortOffset);

        intToByteTableNew = new LookupTable(dataIntB, intOffset);

        // Destination No Data
        destinationNoDataValue = 50;
    }

    @Override
    protected boolean supportDataType(int dataType) {
        return (dataType != DataBuffer.TYPE_FLOAT && dataType != DataBuffer.TYPE_DOUBLE);
    }

    // General method for showing calculation time of the 2 LookupDescriptors
    public void testOperation(int dataType, TestSelection testType) {
        Range rangeND = getTestRange(dataType, testType);
        LookupTable table;
        switch (dataType) {
            case DataBuffer.TYPE_BYTE:
                table = byteToByteTableNew;
                break;
            case DataBuffer.TYPE_USHORT:
                table = ushortToByteTableNew;
                break;
            case DataBuffer.TYPE_SHORT:
                table = shortToByteTableNew;
                break;
            case DataBuffer.TYPE_INT:
                table = intToByteTableNew;
                break;
            default:
                throw new IllegalArgumentException("DataType not supported");
        }

        RenderedImage testImage;
        switch (dataType) {
            case DataBuffer.TYPE_BYTE:
                testImage = testImageByte;
                break;
            case DataBuffer.TYPE_USHORT:
                testImage = testImageUShort;
                break;
            case DataBuffer.TYPE_SHORT:
                testImage = testImageShort;
                break;
            case DataBuffer.TYPE_INT:
                testImage = testImageInt;
                break;
            default:
                throw new IllegalArgumentException("DataType not supported");
        }

        // PlanarImage
        PlanarImage image =
                LookupDescriptor.create(testImage, table, destinationNoDataValue, null, rangeND, false, null);
        finalizeTest(null, dataType, image);
    }
}
