/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 *
 * This Example Content is intended to demonstrate usage of Eclipse technology. It is
 * provided to you under the terms and conditions of the Eclipse Distribution License
 * v1.0 which is available at http://www.eclipse.org/org/documents/edl-v10.php.
 */
package org.eclipse.imagen.widgets;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;

public class FloatDocument extends PlainDocument {

    public void insertString(int offset, String s, AttributeSet as) throws BadLocationException {

        if (s == null || s.length() == 0) {
            return;
        }

        try {
            if (s.equals(".") == false && s.equals("-") == false) {
                try {
                    Integer.parseInt(s);
                } catch (NumberFormatException e) {
                    Float.parseFloat(s);
                }
            }
        } catch (Exception e) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }

        super.insertString(offset, s, as);
    }
}
