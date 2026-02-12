/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 *
 * This Example Content is intended to demonstrate usage of Eclipse technology. It is
 * provided to you under the terms and conditions of the Eclipse Distribution License
 * v1.0 which is available at http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */
package org.eclipse.imagen.tutorial.network.server;

import java.io.File;
import java.io.FilenameFilter;

/**
 * An implementation of the <code>FilenameFilter</code> interface specific to image file formats supported by ImageN
 * 1.1. This class allows a given file name to be classified as an image file name if the file name extension (the last
 * three or four letters after the ".") are any one of the following: bmp, gif, fpx, jpeg, jpg, png, pnm, pgm, ppm, tiff
 * or tif.
 */
public class ImageFilenameFilterImpl implements FilenameFilter {

    /** Default constructor. */
    public ImageFilenameFilterImpl() {}

    /**
     * Tests if a specified file should be included in a file list. For this particular implementation of the <code>
     * FilenameFilter</code> interface, the specified file is included in the file list if the file name extension (the
     * last three or four letters after the ".") are any one of the following: bmp, gif, fpx, jpeg, jpg, png, pnm, pgm,
     * ppm, tiff or tif.
     *
     * @param dir the directory in which the file was found.
     * @param name the name of the file.
     * @return <code>true</code> if and only if the name should be included in the file list; <code>false</code>
     *     otherwise.
     */
    public boolean accept(File dir, String name) {

        int index = name.indexOf(".");
        String extension = name.substring(index + 1);

        if (extension.equalsIgnoreCase("bmp")
                || extension.equalsIgnoreCase("gif")
                || extension.equalsIgnoreCase("fpx")
                || extension.equalsIgnoreCase("jpeg")
                || extension.equalsIgnoreCase("jpg")
                || extension.equalsIgnoreCase("png")
                || extension.equalsIgnoreCase("pnm")
                || extension.equalsIgnoreCase("pgm")
                || extension.equalsIgnoreCase("ppm")
                || extension.equalsIgnoreCase("tiff")
                || extension.equalsIgnoreCase("tif")) {

            return true;

        } else {

            return false;
        }
    }
}
