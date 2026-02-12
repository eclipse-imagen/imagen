/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 *
 * This Example Content is intended to demonstrate usage of Eclipse technology. It is
 * provided to you under the terms and conditions of the Eclipse Distribution License
 * v1.0 which is available at http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */
package org.eclipse.imagen.demo.medical;

/**
 * A class defines an grid layout for an <code>MultipleImagePane</code>. The valid layouts for this application are
 * defined in the property file. An example for a layout is listed below:
 *
 * <pre>
 *              Layout1Label="     4-Views     "
 *              Layout1ImageNum=4
 *              Layout1ImageNumInARow=2
 *              Layout1ImageNumInAColumn=2
 *  </pre>
 *
 * <p>The first item is the name of the layout displayed on the GUI of the application. The second item is the total
 * image number to be displayed in the <code>MultipleImagePane</code>. The third and fourth items is the number of
 * images to be displayed on each row or column. If the total image number is less than the total grid number, some of
 * the grid will be displayed as empty panels.
 */
public class ImageGridLayout {
    /** The total number of images that will be displayed within this layout. */
    private int imageNum;

    /** The number of images that will be located in each row. */
    private int imageNumInARow;

    /** The number of images that will be located in each column. */
    private int imageNumInAColumn;

    /** The layout name based on its serial number. */
    private String name;

    /** The layout name to be displayed in the layout list in the GUI. */
    private String label;

    /**
     * Construct an <code>ImageGridLayout</code> object based on the name composed with the string "Layout" and the
     * serial number of this layout when it is defined in the property file. Retrieve the parameters of this layout from
     * the property file.
     */
    public ImageGridLayout(String name) {
        String s = JaiI18N.getString(name + "ImageNum");
        imageNum = (new Integer(s)).intValue();

        s = JaiI18N.getString(name + "ImageNumInARow");
        imageNumInARow = (new Integer(s)).intValue();

        s = JaiI18N.getString(name + "ImageNumInAColumn");
        imageNumInAColumn = (new Integer(s)).intValue();

        this.name = name;
        label = JaiI18N.getString(name + "Label");
    }

    /** Return the capacity of this layout. */
    public int getImageNum() {
        return imageNum;
    }

    /** Return the number of columns. */
    public int getImageNumInARow() {
        return imageNumInARow;
    }

    /** Return the number of rows. */
    public int getImageNumInAColumn() {
        return imageNumInAColumn;
    }

    /** Return the name to be displayed in the layout list of the GUI. */
    public String getLabel() {
        return label;
    }

    /** Stringify for print for debugging. */
    public String toString() {
        return name + imageNum + "  " + imageNumInARow + "  " + imageNumInAColumn;
    }
}
