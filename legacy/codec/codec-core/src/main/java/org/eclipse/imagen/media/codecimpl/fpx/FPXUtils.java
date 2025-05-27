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

package org.eclipse.imagen.media.codecimpl.fpx;

import java.text.DecimalFormat;

public class FPXUtils {

    //     /** Reads a 2-byte little-endian value. */
    //     public static final int readInt2(SeekableStream file, int offset)
    //         throws IOException {
    //         file.seek(offset);
    //         return file.readShortLE();
    //     }

    //     /** Reads a 4-byte little-endian value. */
    //     public static final int readInt4(SeekableStream file, int offset)
    //         throws IOException {
    //         file.seek(offset);
    //         return file.readIntLE();
    //     }

    //     /** Reads a 4-byte little-endian IEEE float. */
    //     public static final float readFloat(SeekableStream file, int offset)
    //         throws IOException {
    //         file.seek(offset);
    //         return file.readFloatLE();
    //     }

    //     /** Reads an 8-byte little-endian IEEE double. */
    //     public static final double readDouble(SeekableStream file,
    //                                           int offset)
    //         throws IOException {
    //         file.seek(offset);
    //         return file.readDoubleLE();
    //     }

    public static final short getShortLE(byte[] data, int offset) {
        int b0 = data[offset] & 0xff;
        int b1 = data[offset + 1] & 0xff;

        return (short) ((b1 << 8) | b0);
    }

    public static final int getUnsignedShortLE(byte[] data, int offset) {
        int b0 = data[offset] & 0xff;
        int b1 = data[offset + 1] & 0xff;

        return (b1 << 8) | b0;
    }

    public static final int getIntLE(byte[] data, int offset) {
        int b0 = data[offset] & 0xff;
        int b1 = data[offset + 1] & 0xff;
        int b2 = data[offset + 2] & 0xff;
        int b3 = data[offset + 3] & 0xff;

        return (b3 << 24) | (b2 << 16) | (b1 << 8) | b0;
    }

    public static final long getUnsignedIntLE(byte[] data, int offset) {
        long b0 = data[offset] & 0xff;
        long b1 = data[offset + 1] & 0xff;
        long b2 = data[offset + 2] & 0xff;
        long b3 = data[offset + 3] & 0xff;

        return (b3 << 24) | (b2 << 16) | (b1 << 8) | b0;
    }

    public static final String getString(byte[] data, int offset, int length) {
        if (length == 0) {
            return "<none>";
        } else {
            length = length / 2 - 1; // workaround for Kodak bug
        }
        StringBuffer b = new StringBuffer(length);
        for (int i = 0; i < length; i++) {
            int c = getUnsignedShortLE(data, offset);
            b.append((char) c);
            offset += 2;
        }

        return b.toString();
    }

    private static void printDecimal(int i) {
        DecimalFormat d = new DecimalFormat("00000");
        System.out.print(d.format(i));
    }

    private static void printHex(byte b) {
        int i = b & 0xff;
        int hi = i / 16;
        int lo = i % 16;

        if (hi < 10) {
            System.out.print((char) ('0' + hi));
        } else {
            System.out.print((char) ('a' + hi - 10));
        }

        if (lo < 10) {
            System.out.print((char) ('0' + lo));
        } else {
            System.out.print((char) ('a' + lo - 10));
        }
    }

    private static void printChar(byte b) {
        char c = (char) (b & 0xff);

        if (c >= '!' && c <= '~') {
            System.out.print(' ');
            System.out.print(c);
        } else if (c == 0) {
            System.out.print("^@");
        } else if (c < ' ') {
            System.out.print('^');
            System.out.print((char) ('A' + c - 1));
        } else if (c == ' ') {
            System.out.print("__");
        } else {
            System.out.print("??");
        }
    }

    public static void dumpBuffer(byte[] buf, int offset, int length, int printOffset) {
        int lines = length / 8;

        for (int j = 0; j < lines; j++) {
            printDecimal(printOffset);
            System.out.print(": ");

            for (int i = 0; i < 8; i++) {
                printHex(buf[offset + i]);
                System.out.print("  ");
            }
            for (int i = 0; i < 8; i++) {
                printChar(buf[offset + i]);
                System.out.print("  ");
            }

            offset += 8;
            printOffset += 8;
            System.out.println();
        }
    }
}
