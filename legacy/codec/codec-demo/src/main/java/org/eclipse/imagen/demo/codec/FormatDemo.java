/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 *
 * This Example Content is intended to demonstrate usage of Eclipse technology. It is
 * provided to you under the terms and conditions of the Eclipse Distribution License
 * v1.0 which is available at http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */
package org.eclipse.imagen.demo.codec;

import org.eclipse.imagen.media.codec.FileSeekableStream;
import org.eclipse.imagen.media.codec.ImageCodec;
import org.eclipse.imagen.media.codec.SeekableStream;

public class FormatDemo {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Format recognizer demo:\n");
            System.out.println("  Given a file, determine which file formats it may be encoded in.");
            System.out.println("  In addition to the standard formats, a \"samplepnm\" codec is");
            System.out.println("  registered.\n");
            System.out.println("usage: java FormatDemo <filenames>");
            System.exit(0);
        }

        // Register the sample PNM codec
        ImageCodec.registerCodec(new SamplePNMCodec());

        try {
            for (int i = 0; i < args.length; i++) {
                SeekableStream stream = new FileSeekableStream(args[i]);
                String[] names = ImageCodec.getDecoderNames(stream);

                System.out.println("File " + args[i] + " may be in the following format(s):");
                for (int j = 0; j < names.length; j++) {
                    System.out.println("\t" + names[j]);
                }

                System.out.println();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
