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

/** An interface contains the public constants used in this application. */
interface MedicalAppConstants {
    // Currently the zoom slider is not linear, so the position for
    // zoom factor 1.0 is defined in the property file.
    int nozoom = (new Integer(JaiI18N.getString("NoZoomTickPosition")).intValue() - 1);

    // The command name and/or message names
    String setLayoutCommand = "SetLayout";
    String allViewsCommand = "AllViews";
    String currentViewCommand = "CurrentView";
    String zoomCommand = "zoom";
    String rotationCommand = "rotation";
    String speedCommand = "cinespeed";
    String startCommand = "cinestart";
    String stopCommand = "cinestop";
    String windowCommand = "window";
    String levelCommand = "level";
    String annotationCommand = "anotation";
    String measurementCommand = "measurement";
    String statisticsCommand = "statistics";
    String histogramCommand = "histogram";
    String paramSync = "paramSync";

    // The default values for window/level.
    int smallestWindow = 0;
    int largestWindow = 4096;
    int defaultWindow = 700;
    int smallestLevel = 0;
    int largestLevel = 4096;
    int defaultLevel = 300;
}
