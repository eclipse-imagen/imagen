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
package org.eclipse.imagen.demo.mpv;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.MouseAdapter;
import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.eclipse.imagen.JAI;
import org.eclipse.imagen.ImageLayout;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;

/** SimpleOverviewImage display and event handling class that includes a popup menu for enabling
 *  window leveling and hiding the overview display. This class contains a 
 *  <code>SimpleOverviewImageDisplayPane</code> which manages an overview image contents.
 *
 */



public class SimpleOverviewImage extends JFrame implements ActionListener {

    // Constants
    private static final int MENU_ITEM_INVALID        = -1;
    private static final int MENU_ITEM_WIN_LEVEL      = 0;
    private static final int MENU_ITEM_HIDE_OVERVIEW  = 1;

    private JPopupMenu popupMenu;

    private static final String WIN_LEVEL_STRING = "Adjust Display Intensity";
    private static final String OVERVIEW_STRING  = "Hide Overview";
    private SimpleOverviewImageDisplayPane overviewDisplayPane;

    /** Constructor
     *
     * @param ovwDisplayPane the image display panel containing an overview display
     * @param imagePath the file path of the overview image
     */


    public SimpleOverviewImage(SimpleOverviewImageDisplayPane ovwDisplayPane, String imagePath) {
	super("Overview: " + imagePath);
        PopupListener popupListener = new PopupListener();
        this.addWindowListener(new WindowHandler()); // Add window listener   
        this.overviewDisplayPane = ovwDisplayPane;
        this.overviewDisplayPane.addMouseListener(popupListener);
        this.overviewDisplayPane.mtw.setVisible(false);
	this.getContentPane().add(ovwDisplayPane);
        this.buildPopup();
        this.pack();
        this.setVisible(false);
	this.setResizable(false);
    }

    // Build a popup menu (follow pattern to add new items)
    private void buildPopup() {

        //Create a popup menu for a pane
        popupMenu = new JPopupMenu();

        JMenuItem menuItem = new JMenuItem(WIN_LEVEL_STRING);
        menuItem.addActionListener(this);
        popupMenu.add(menuItem);

        menuItem = new JMenuItem(OVERVIEW_STRING);
        menuItem.addActionListener(this);
        popupMenu.add(menuItem);

    }

    // Handler class for window events
    class WindowHandler extends WindowAdapter {
	// Handler for window closing event
	public void windowClosing(WindowEvent e) {
	    setVisible(false);
	}
    } // class WindowHandler

    // parseAction is a convenience method to compute menu item ids
    private int parseAction(String menuString) {
       if (menuString.startsWith(WIN_LEVEL_STRING)) return MENU_ITEM_WIN_LEVEL;
       if (menuString.startsWith(OVERVIEW_STRING)) return MENU_ITEM_HIDE_OVERVIEW;
       return MENU_ITEM_INVALID;
    } // parseAction

    /** menu callback event handler. 
     *
     * @param e menu event
     */


    public void actionPerformed(ActionEvent e) {
        JMenuItem source = (JMenuItem)(e.getSource());
	switch (parseAction(source.getText())) {

            case MENU_ITEM_WIN_LEVEL:

                // Display a win-level widget for current overview display
                this.overviewDisplayPane.mtw.setVisible(true);
                break;

            case MENU_ITEM_HIDE_OVERVIEW:

                // Hide the overview
                this.setVisible(false);
                break;

        }
    } // actionPerformed

    /** Inner class to include a popup menu */


    class PopupListener extends MouseAdapter {

        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }

        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger())
                popupMenu.show(e.getComponent(), e.getX(), e.getY());
        }
    } // class PopupListener
}

