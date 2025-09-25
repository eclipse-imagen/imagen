/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 *
 * This Example Content is intended to demonstrate usage of Eclipse technology. It is
 * provided to you under the terms and conditions of the Eclipse Distribution License
 * v1.0 which is available at http://www.eclipse.org/org/documents/edl-v10.php.
 */
package org.eclipse.imagen.demo.mpv;

import java.awt.Dimension;
import java.awt.RenderingHints;
import java.awt.event.*;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import org.eclipse.imagen.ImageLayout;
import org.eclipse.imagen.ImageN;

/**
 * Multi-Panel Viewer (MPV) demonstration class.
 *
 * <p>The image panels for this demo are layed out as follows:
 *
 * <p>Single image case (two input images, one for primary view another for overview): <code><pre>
 *   ------------   ---------   [] panner graphic indicating Primary view location
 *   |          |   |  []   |
 *   |	        |   | Over- |   -- The overview is accessible using the popup menu
 *   | Primary  |   |  view |   -- The panner can be click-and-dragged to change views
 *   |	View    |   ---------   -- Left-mouse clicks in Primary View cause "translate
 *   |	        |                  to center" image operations
 *   |	        |               -- Mouse clicks in the Overview cause the panner to
 *   ------------                  move to the clicked location
 *  </pre></code> Double image case (four input images): <code><pre>
 *   ------------------------  Double image case has same capabilities as single image case
 *   |          ||          |  and includes (eg., each Primary View has an Overview, etc.):
 *   |          ||          |  -- The divider between views can be click-and-dragged
 *   | Primary  || Primary  |    (split pane)
 *   |	View0   ||  View1   |  -- popup exposed in View1 for Overview1, etc.
 *   |          ||          |
 *   |          ||          |
 *   ------------------------
 *  </pre></code> Four image case (eight input images): <code><pre>
 *   ------------------------  Four image case has same capabilities as double image case
 *   |          ||          |  and includes:
 *   |          ||          |  -- Horizontal and vertical dividers between views can
 *   | Primary  ||  Primary |     be click-and-dragged (split panes)
 *   |  View0   ||   View1  |
 *   |          ||          |
 *   |          ||          |
 *   ========================
 *   |          ||          |
 *   |          ||          |
 *   | Primary  || Primary  |
 *   |  View2   ||  View3   |
 *   |          ||          |
 *   |          ||          |
 *   ------------------------
 *  </pre></code> The primary and overview panes are managed in separate classes. They communicate using property change
 * events.
 *
 * <p>This class also contains facilities (widgets + callbacks) to change image intensities and sharpen image displays.
 */
public class MPVDemo extends JFrame implements ActionListener {

    // Popup labels are "context sensitive" to where the user right
    // mouse clicked on the display.
    private String[] popupLabel = {
        "Upper Left Panel", "Upper Right Panel",
        "Lower Left Panel", "Lower Right Panel"
    };

    // Constants used to identify popup menu items
    private static final int MENU_ITEM_INVALID = -1;
    private static final int MENU_ITEM_WIN_LEVEL = 0; // Pops up window level widget
    private static final int MENU_ITEM_SHARP = 1; // Pops up a sharpening widget
    private static final int MENU_ITEM_TOGGLE_DISPLAY = 2; // Turns off image display graphics
    private static final int MENU_ITEM_SHOW_OVERVIEW = 3; // Displays associated overview
    private static final int MENU_ITEM_EXIT = 4;

    // Constants used for popup menu item labels
    private static final String WIN_LEVEL_STRING = "Adjust Display Intensity";
    private static final String SHARP_STRING = "Sharpen Image";
    private static final String TOGGLE_STRING = "Toggle Graphics";
    private static final String OVERVIEW_STRING = "Display Overview";
    private static final String EXIT_STRING = "Exit";
    private static final int DEFAULT_TILE_SIZE = 128;

    // Container classes for the Primary and Overview images
    private static PrimaryImageDisplayPane[] primaryImgDisplay = new PrimaryImageDisplayPane[4];
    private SimpleOverviewImage[] overviewImage = new SimpleOverviewImage[4];

    // There are 4 popup menus, one for up to four image displays
    private JPopupMenu[] popupMenu = new JPopupMenu[4];

    public static void main(String[] args) {

        // There are up to 8 images involved in this demo.  Eight image paths are required
        //  in the "Four image case" described above (4 primary views + 4 overviews).  Even
        //  entries in the imageNames array (0, 2, 4, 6) store the paths for the primary view
        //  files and odd entries (1, 3, 5, 7) store the paths for the overview files.
        //
        // Program arguments are 1, 2, or 4 image file name pairs.
        //
        // <Image1> <OverviewImage1> <Image2> <OverviewImage2>...
        //
        if (args.length == 2 || args.length == 4 || args.length == 8) {
            MPVDemo mpv = new MPVDemo(args.length / 2, args);
            mpv.show();
        } else {
            System.out.println("Usage (single): MPVDemo <Image1> <OverviewImage1> \n"
                    + "(Side-by-side): MPVDemo <Im1> <Ovw1> <Im2> <Ovw2>\n"
                    + "(Quad-display): MPVDemo <Im1> <Ovw1> <Im2> <Ovw2> <Im3> <Ovw3> <Im4> <Ovw4> \n"
                    + "         Notes: For small images, the same image path can be used in a pair. \n"
                    + "              : Use the right mouse button to expose a popup menu of options.");

            System.exit(0);
        }
    } // main

    /**
     * Constructor that builds a 1, 2, or 4 panel display (single, side by side, or quadrant arrangement).
     *
     * @param nPanels number of image display panels (1, 2, or 4)
     * @param imageNames array of 2*nPanels image paths. Primary displays are filled with images from imageNames[0],
     *     imageNames[2], etc. Overview displays are filled with imageNames[1], imageNames[3], etc., and similarly for
     *     up to 4 image pairs.
     */
    public MPVDemo(int nPanels, String[] imageNames) {
        super("Multi-Panel Viewer DEMO");

        // Assert that nPanels is as expected (1, 2 or 4)
        if (nPanels != 1 && nPanels != 2 && nPanels != 4) {
            System.out.println("Non-supported number of panels: " + nPanels);
            System.out.println("This demo supports 1,2, or 4 panels");
            System.exit(0); // Feature opportunity: Make more graceful if desired
        }

        MPVUtils.setMainFrame(this);
        Dimension screenSize = this.getToolkit().getScreenSize();
        setBounds(0, 0, screenSize.width - 128, screenSize.height - 128);

        // Ensure that if the user does a window kill, everything is handled
        addWindowListener(new WindowHandler());

        RenderingHints hints = new RenderingHints(ImageN.KEY_IMAGE_LAYOUT, getDefaultLayout());

        // Build a single popup listener for all the panels
        MouseListener popupListener = new PopupListener();
        for (int pane = 0; pane < nPanels; pane++) {
            primaryImgDisplay[pane] = null;
            overviewImage[pane] = null;
            loadImageToPane(imageNames[2 * pane], imageNames[2 * pane + 1], hints, pane);
            buildPopup(pane);
            primaryImgDisplay[pane].addMouseListener(popupListener);
        }

        SynchronizedSplitPane p1, p2, p3;
        switch (nPanels) {
            case 1: // Single image viewer
                this.getContentPane().add(primaryImgDisplay[0]);
                break;
            case 2: // Side by side image viewer
                p1 = new SynchronizedSplitPane(JSplitPane.HORIZONTAL_SPLIT, primaryImgDisplay[0], primaryImgDisplay[1]);
                p1.setDividerLocation(this.getWidth() / 2);
                this.getContentPane().add(p1);
                break;
            case 4: // Four quadrants image viewer
                p1 = new SynchronizedSplitPane(JSplitPane.HORIZONTAL_SPLIT, primaryImgDisplay[0], primaryImgDisplay[1]);

                p2 = new SynchronizedSplitPane(JSplitPane.HORIZONTAL_SPLIT, primaryImgDisplay[2], primaryImgDisplay[3]);
                p1.addSynchronized(p2);
                p1.setDividerLocation(this.getWidth() / 2);
                p2.setDividerLocation(this.getWidth() / 2);

                p3 = new SynchronizedSplitPane(JSplitPane.VERTICAL_SPLIT, p1, p2);

                p3.setDividerLocation(this.getHeight() / 2);
                this.getContentPane().add(p3);
                break;
            default:
                System.out.println("Non-supported number of panels: " + nPanels);
                System.out.println("This demo supports 1,2, or 4 panels");
                System.exit(0);
        }
    } // MPVDemo constructor

    // Load an image to a pane and if available, load its overview
    //
    // Panel numbers (pane) are defined as follows:
    //   Upper Left (0), Upper right (1),
    //   Lower Left (2), Lower right (3)
    //
    private void loadImageToPane(String imagePath, String ovwPath, RenderingHints hints, int pane) {

        // Create an op chain to load the primary image.
        //
        // The fileload operator is the first node in the primary image op chain.
        //
        RenderedImage primaryImg = ImageN.create("fileload", imagePath);
        if ((primaryImg.getTileWidth() != DEFAULT_TILE_SIZE) || (primaryImg.getTileHeight() != DEFAULT_TILE_SIZE)) {

            // The format operator will locally convert the image as needed to a
            // tiled format.  If the original image is already tiled the way we
            // like, this step will be skipped.
            //
            // As image size increases, this step becomes increasingly important.
            //
            ParameterBlock pb = new ParameterBlock();
            pb.addSource(primaryImg);
            pb.add(primaryImg.getSampleModel().getDataType());
            primaryImg = ImageN.create("format", pb, hints);
        }

        // If this is the first time for setting an image, construct a new
        // PrimaryImageDisplayPane.
        if (primaryImgDisplay[pane] == null) {
            primaryImgDisplay[pane] = new PrimaryImageDisplayPane(primaryImg, new ResizePolicy(1 + pane), imagePath);
        } else {
            primaryImgDisplay[pane].setNewImage(primaryImg, imagePath);
        }

        if (ovwPath != null) {
            RenderedImage overviewImg = ImageN.create("fileload", ovwPath);

            SimpleOverviewImageDisplayPane overviewImgDisplay =
                    new SimpleOverviewImageDisplayPane(overviewImg, ovwPath, new SimpleOverviewROI());

            // Compute the ratio of overview size to primary image display with the
            // assumption:
            //   The overview is uniformly scaled relative to the primary image.
            // Notes:
            //   If the assumption is violated the box graphic may not exactly cover
            //     displayed area (the more the assumption is violated the more the box will
            //     be off center).
            //   This assumption can be removed by resizing the overview or by using two scale
            //     factors, one for width and one for height dimensions. There are several
            //     design options and consequences to consider here.
            //   This demo ignores this problem.
            //
            double boxScaleFactor = (((double) overviewImg.getWidth() / (double) primaryImg.getWidth())
                            + ((double) overviewImg.getHeight() / (double) primaryImg.getHeight()))
                    / 2D;

            // Set up listeners for sending messages back and forth between
            // primary views and overviews.  The messages relay scroll position so that
            // the overview roi graphic can stay in sync with the primary view and
            // vice versa.
            primaryImgDisplay[pane].addBoxMoverListener(boxScaleFactor, "OverviewListener", overviewImgDisplay);
            overviewImgDisplay.addBoxMoverListener("PrimaryListener", primaryImgDisplay[pane]);
            if (overviewImage[pane] != null) {
                overviewImage[pane].setVisible(false);
                overviewImage[pane].removeAll();
            }
            overviewImage[pane] = new SimpleOverviewImage(overviewImgDisplay, ovwPath);
        } else {
            if (overviewImage[pane] != null) {
                overviewImage[pane].setVisible(false);
                overviewImage[pane].removeAll();
            }
            overviewImage[pane] = null;
        }
    } // loadImageToPane

    // getDefault layout returns a layout preferred by this demo.
    private ImageLayout getDefaultLayout() {
        ImageLayout il = new ImageLayout();
        il.setTileWidth(DEFAULT_TILE_SIZE);
        il.setTileHeight(DEFAULT_TILE_SIZE);
        il.setTileGridXOffset(0);
        il.setTileGridYOffset(0);
        return il;
    } // getDefaultLayout

    // Show the window leveling widget.  This widget is abreviated "mtw" for
    // mapping tool widget.
    private void showWinLevelWidget(int pane) {
        primaryImgDisplay[pane].mtw.setVisible(true);
    } // showWinLevelWidget

    private void hideWinLevelWidget(int pane) {
        primaryImgDisplay[pane].mtw.setVisible(false);
    } // hideWinLevelWidget

    private void showSharpWidget(int pane) {
        primaryImgDisplay[pane].sharpWidget.setVisible(true);
    } // showSharpWidget

    // Build a popup menu (follow simple pattern to add new items)
    private void buildPopup(int pane) {

        // Create a popup menu for a pane
        popupMenu[pane] = new JPopupMenu();

        // Build the panel label to be appended to each menu item
        String tail = new String(" (" + popupLabel[pane] + ")");

        JMenuItem menuItem = new JMenuItem(WIN_LEVEL_STRING + tail);
        menuItem.addActionListener(this);
        popupMenu[pane].add(menuItem);

        menuItem = new JMenuItem(SHARP_STRING + tail);
        menuItem.addActionListener(this);
        popupMenu[pane].add(menuItem);

        menuItem = new JMenuItem(TOGGLE_STRING + tail);
        menuItem.addActionListener(this);
        popupMenu[pane].add(menuItem);

        menuItem = new JMenuItem(OVERVIEW_STRING + tail);
        menuItem.addActionListener(this);
        popupMenu[pane].add(menuItem);

        menuItem = new JMenuItem(EXIT_STRING); // Exit option terminates app
        menuItem.addActionListener(this);
        popupMenu[pane].add(menuItem);
    }

    // parseAction is a convenience method to compute menu item numbers
    // so that we can use a switch to process menu cases.
    private int parseAction(String menuString) {
        if (menuString.startsWith(WIN_LEVEL_STRING)) return MENU_ITEM_WIN_LEVEL;
        if (menuString.startsWith(SHARP_STRING)) return MENU_ITEM_SHARP;
        if (menuString.startsWith(TOGGLE_STRING)) return MENU_ITEM_TOGGLE_DISPLAY;
        if (menuString.startsWith(OVERVIEW_STRING)) return MENU_ITEM_SHOW_OVERVIEW;
        if (menuString.startsWith(EXIT_STRING)) return MENU_ITEM_EXIT;
        return MENU_ITEM_INVALID;
    } // parseAction

    // parsePane figures out pane number from popupLabel
    private int parsePane(String menuString) {

        for (int pane = 0; pane < 4; pane++) if (menuString.indexOf(popupLabel[pane]) != -1) return pane;
        return MENU_ITEM_INVALID;
    } // parsePane

    // informUser is a simple MessageDialog generator
    private void informUser(String title, String message) {

        JFrame frame = new JFrame(title);
        JOptionPane.showMessageDialog(frame, message, title, JOptionPane.INFORMATION_MESSAGE);
    } // void informUser

    /**
     * Popup menu action event handler
     *
     * @param e contains the <code>ActionEvent</code> from a <code>JMenuItem</code>
     */
    public void actionPerformed(ActionEvent e) {
        JMenuItem source = (JMenuItem) (e.getSource());
        int currentPane = parsePane(source.getText());
        switch (parseAction(source.getText())) {
            case MENU_ITEM_WIN_LEVEL:

                // Display a win-level widget for current display
                showWinLevelWidget(currentPane);
                break;
            case MENU_ITEM_SHARP:

                // Display a win-level widget for current display
                showSharpWidget(currentPane);
                break;
            case MENU_ITEM_TOGGLE_DISPLAY:

                // Toggle the center + and image border (on to off or off to on)
                primaryImgDisplay[currentPane].toggleBorderAndPlus();
                break;

            case MENU_ITEM_SHOW_OVERVIEW:

                // If available, show the overview
                if (overviewImage[currentPane] != null) {
                    overviewImage[currentPane].setVisible(true);
                } else {
                    informUser("Overview unavailable", "Unable to load an overview");
                }
                break;

            case MENU_ITEM_EXIT:
                // Shutdown this application now
                System.exit(0);
        }
    } // actionPerformed

    /** Handler class for window events */
    class WindowHandler extends WindowAdapter {

        /**
         * Handler for window closing event
         *
         * @param e window closing event.
         */
        public void windowClosing(WindowEvent e) {

            // If desired, add closing tasks here
            System.exit(0);
        }
    } // class WindowHandler

    /** Listener class used to handle mouse events related to a pop-up menu. */
    class PopupListener extends MouseAdapter {

        /**
         * Handler for mouse events
         *
         * @param e possible popup event.
         */
        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }

        /**
         * Handler for mouse events
         *
         * @param e possible popup event.
         */
        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }

        // Right mouse clicks trigger popup menu. Note: there are four popup
        // menus, each labeled to correspond to four possible panes.
        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                int paneIndex = 0;
                PrimaryImageDisplayPane src = (PrimaryImageDisplayPane) e.getComponent();

                // determine which pane the event was triggered in
                if (src.equals(primaryImgDisplay[0])) // Upper left
                paneIndex = 0;
                else if (src.equals(primaryImgDisplay[1])) // Upper right
                paneIndex = 1;
                else if (src.equals(primaryImgDisplay[2])) // Lower left
                paneIndex = 2;
                else if (src.equals(primaryImgDisplay[3])) // Lower right
                paneIndex = 3;

                // show the popup at mouse location (pane location sensitive)
                popupMenu[paneIndex].show(e.getComponent(), e.getX(), e.getY());
            }
        }
    } // class PopupListener
}
