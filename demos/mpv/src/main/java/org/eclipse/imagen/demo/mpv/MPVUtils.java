/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 *
 * This Example Content is intended to demonstrate usage of Eclipse technology. It is
 * provided to you under the terms and conditions of the Eclipse Distribution License
 * v1.0 which is available at http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */
package org.eclipse.imagen.demo.mpv;

import javax.swing.JFrame;

/** A utility class to contain the convenient methods for Multi-Pane Viewer. */
public class MPVUtils {

    private static JFrame mainFrame = null;

    public static void setMainFrame(JFrame f) {
        mainFrame = f;
    }

    public static JFrame getMainFrame() {
        return mainFrame;
    }
}
