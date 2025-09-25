/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 *
 * This Example Content is intended to demonstrate usage of Eclipse technology. It is
 * provided to you under the terms and conditions of the Eclipse Distribution License
 * v1.0 which is available at http://www.eclipse.org/org/documents/edl-v10.php.
 */
package org.eclipse.imagen.tutorial.network.server;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.ParameterBlock;
import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import org.eclipse.imagen.NegotiableCapabilitySet;
import org.eclipse.imagen.RenderableOp;
import org.eclipse.imagen.RenderedOp;
import org.eclipse.imagen.media.rmi.ImageServer;
import org.eclipse.imagen.media.rmi.JAIRMIImageServer;
import org.eclipse.imagen.media.rmi.SerializableRenderableImage;
import org.eclipse.imagen.media.serialize.SerializableState;
import org.eclipse.imagen.remote.JAIRMIDescriptor;

/** GUI that displays the tile that was most recently processed on the server. */
class TilePanel extends JPanel {

    JLabel picture, opInfo;
    ImageIcon imageIcon;

    public TilePanel() {

        setLayout(new BorderLayout());

        imageIcon = new ImageIcon("the_nut.jpg");
        picture = new JLabel(imageIcon);
        Dimension size = new Dimension(imageIcon.getIconWidth(), imageIcon.getIconHeight());
        picture.setPreferredSize(size);

        // Put it in a scroll pane
        JScrollPane pictureScrollPane = new JScrollPane(
                picture,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        add(pictureScrollPane, BorderLayout.CENTER);
        add(pictureScrollPane);

        opInfo = new JLabel(" ");
        add(opInfo, BorderLayout.SOUTH);
    }

    public void updateImage(Raster tile, ColorModel colorModel, String text) {

        // Create a BufferedImage out of the tile
        WritableRaster wr = tile instanceof WritableRaster
                ? ((WritableRaster) tile).createWritableTranslatedChild(0, 0)
                : tile.createWritableRaster(tile.getSampleModel(), tile.getDataBuffer(), new Point(0, 0));

        BufferedImage bi = new BufferedImage(colorModel, wr, colorModel.isAlphaPremultiplied(), null);

        imageIcon = new ImageIcon(bi);
        picture.setIcon(imageIcon);

        Dimension iconSize = new Dimension(imageIcon.getIconWidth(), imageIcon.getIconHeight());
        picture.setPreferredSize(iconSize);

        opInfo.setText(text);
        picture.revalidate();
        opInfo.revalidate();
    }
}

/** GUI that displays the text messages regarding the last operation performed on the server. */
class TextPanel extends JPanel {

    JTextArea textArea;
    int textPosition = 0;
    JScrollPane textScrollPane;

    public TextPanel() {

        textArea = new JTextArea(25, 40);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);

        textScrollPane = new JScrollPane(
                textArea,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        // Add this text area to the Frame
        add(textScrollPane);
    }

    public void addText(String text) {
        textArea.insert(text, textPosition);
        textPosition += text.length();
    }
}

/**
 * A class that wraps the JAIRMIImageServer image server supplied with ImageN such that it intercepts all calls to
 * JAIRMIImageServer in order to display the results of these operations in a server side GUI, before forwarding the
 * call to the image server for image processing.
 */
public class JAIRMIServerWrapper extends UnicastRemoteObject implements ImageServer {

    public ImageServer server = null;
    public TilePanel imagePanel;
    public TextPanel textPanel;

    /** Constructs <code>JAIRMIServerWrapper</code>. */
    public JAIRMIServerWrapper(JFrame frame) throws RemoteException {

        this.server = new JAIRMIImageServer(0);

        this.imagePanel = new TilePanel();
        this.textPanel = new TextPanel();

        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new java.awt.BorderLayout());
        contentPane.add(imagePanel, java.awt.BorderLayout.NORTH);
        contentPane.add(textPanel, java.awt.BorderLayout.CENTER);
    }

    /**
     * Returns the identifier of the remote image. This method should be called to return an identifier before any other
     * methods are invoked. The same ID must be used in all subsequent references to the remote image.
     */
    public Long getRemoteID() throws RemoteException {
        return server.getRemoteID();
    }

    /** Disposes of any resouces allocated to the client object with the specified ID. */
    public void dispose(Long id) throws RemoteException {
        server.dispose(id);
    }

    /**
     * Increments the reference count for this id, i.e. increments the number of RMIServerProxy objects that currently
     * reference this id.
     */
    public void incrementRefCount(Long id) throws RemoteException {
        server.incrementRefCount(id);
    }

    /// Methods Common To Rendered as well as Renderable modes.

    /**
     * Gets a property from the property set of this image. If the property name is not recognized,
     * java.awt.Image.UndefinedProperty will be returned.
     *
     * @param id An ID for the source which must be unique across all clients.
     * @param name the name of the property to get, as a String.
     * @return a reference to the property Object, or the value java.awt.Image.UndefinedProperty.
     */
    public Object getProperty(Long id, String name) throws RemoteException {

        guiPrint("Getting property named " + name + "\n");

        return server.getProperty(id, name);
    }

    /**
     * Returns a list of names recognized by getProperty(String).
     *
     * @return an array of Strings representing proeprty names.
     */
    public String[] getPropertyNames(Long id) throws RemoteException {

        guiPrint("Getting all propertyNames\n");
        return server.getPropertyNames(id);
    }

    /**
     * Returns a list of names recognized by getProperty().
     *
     * @return an array of Strings representing property names.
     */
    public String[] getPropertyNames(String opName) throws RemoteException {
        guiPrint("Getting property names for " + "opname = " + opName + "\n");
        return server.getPropertyNames(opName);
    }

    /// Rendered Mode Methods

    /** Returns the ColorModel associated with this image. */
    public SerializableState getColorModel(Long id) throws RemoteException {
        return server.getColorModel(id);
    }

    /** Returns the SampleModel associated with this image. */
    public SerializableState getSampleModel(Long id) throws RemoteException {
        return server.getSampleModel(id);
    }

    /** Returns the width of the image on the ImageServer. */
    public int getWidth(Long id) throws RemoteException {
        return server.getWidth(id);
    }

    /** Returns the height of the image on the ImageServer. */
    public int getHeight(Long id) throws RemoteException {
        return server.getHeight(id);
    }

    /** Returns the minimum X coordinate of the image on the ImageServer. */
    public int getMinX(Long id) throws RemoteException {
        return server.getMinX(id);
    }

    /** Returns the minimum Y coordinate of the image on the ImageServer. */
    public int getMinY(Long id) throws RemoteException {
        return server.getMinY(id);
    }

    /** Returns the number of tiles across the image. */
    public int getNumXTiles(Long id) throws RemoteException {
        return server.getNumXTiles(id);
    }

    /** Returns the number of tiles down the image. */
    public int getNumYTiles(Long id) throws RemoteException {
        return server.getNumYTiles(id);
    }

    /** Returns the index of the minimum tile in the X direction of the image. */
    public int getMinTileX(Long id) throws RemoteException {
        return server.getMinTileX(id);
    }

    /** Returns the index of the minimum tile in the Y direction of the image. */
    public int getMinTileY(Long id) throws RemoteException {
        return server.getMinTileY(id);
    }

    /** Returns the width of a tile in pixels. */
    public int getTileWidth(Long id) throws RemoteException {
        return server.getTileWidth(id);
    }

    /** Returns the height of a tile in pixels. */
    public int getTileHeight(Long id) throws RemoteException {
        return server.getTileHeight(id);
    }

    /** Returns the X offset of the tile grid relative to the origin. */
    public int getTileGridXOffset(Long id) throws RemoteException {
        return server.getTileGridXOffset(id);
    }

    /** Returns the Y offset of the tile grid relative to the origin. */
    public int getTileGridYOffset(Long id) throws RemoteException {
        return server.getTileGridYOffset(id);
    }

    /**
     * Returns tile (x, y). Note that x and y are indices into the tile array, not pixel locations. Unlike in the true
     * RenderedImage interface, the Raster that is returned should be considered a copy.
     *
     * @param id An ID for the source which must be unique across all clients.
     * @param x the x index of the requested tile in the tile array
     * @param y the y index of the requested tile in the tile array
     * @return a copy of the tile as a Raster.
     */
    public SerializableState getTile(Long id, int x, int y) throws RemoteException {

        RenderedOp op = getNode(id);
        String opName = op.getOperationName();

        guiPrint("Tile(" + x + ", " + y + ") from " + opName + " \n");

        SerializableState ss = server.getTile(id, x, y);
        Raster tile = (Raster) ss.getObject();

        imagePanel.updateImage(
                tile, (ColorModel) (getColorModel(id).getObject()), opName + ": tile (" + x + ", " + y + ")");

        return ss;
    }

    /**
     * Compresses tile (x, y) and returns the compressed tile's contents as a byte array. Note that x and y are indices
     * into the tile array, not pixel locations.
     *
     * @param id An ID for the source which must be unique across all clients.
     * @param x the x index of the requested tile in the tile array
     * @param y the y index of the requested tile in the tile array
     * @return a byte array containing the compressed tile contents.
     */
    public byte[] getCompressedTile(Long id, int x, int y) throws RemoteException {

        guiPrint("Compressed Tile(" + x + ", " + y + ")\n");
        return server.getCompressedTile(id, x, y);
    }

    /**
     * Returns the entire image as a single Raster.
     *
     * @return a SerializableState containing a copy of this image's data.
     */
    public SerializableState getData(Long id) throws RemoteException {
        guiPrint("getData\n");
        return server.getData(id);
    }

    /**
     * Returns an arbitrary rectangular region of the RenderedImage in a Raster. The rectangle of interest will be
     * clipped against the image bounds.
     *
     * @param id An ID for the source which must be unique across all clients.
     * @param bounds the region of the RenderedImage to be returned.
     * @return a SerializableState containing a copy of the desired data.
     */
    public SerializableState getData(Long id, Rectangle bounds) throws RemoteException {
        guiPrint("getData(" + bounds + ")\n");
        return server.getData(id, bounds);
    }

    /** Returns the same result as getData(Rectangle) would for the same rectangular region. */
    public SerializableState copyData(Long id, Rectangle bounds) throws RemoteException {
        guiPrint("copyData(" + bounds + ")\n");
        return server.copyData(id, bounds);
    }

    /**
     * Creates a RenderedOp on the server side with a parameter block empty of sources. The sources are set by separate
     * calls depending upon the type and serializabilty of the source.
     */
    public void createRenderedOp(Long id, String opName, ParameterBlock pb, SerializableState hints)
            throws RemoteException {

        guiPrint("Created " + opName + " op.\n");
        server.createRenderedOp(id, opName, pb, hints);
    }

    /**
     * Method that prints text messages to the Text GUI window in order to inform the viewer of server side processing
     * just taken place.
     */
    private void guiPrint(String s) {
        textPanel.addText(s);
    }

    /** Calls for Rendering of the Op and returns true if the RenderedOp could be rendered else false */
    public boolean getRendering(Long id) throws RemoteException {
        return server.getRendering(id);
    }

    /** Retrieve a node from the hashtable. */
    public RenderedOp getNode(Long id) throws RemoteException {
        return server.getNode(id);
    }

    /** Sets the source of the image as a RenderedImage on the server side */
    public void setRenderedSource(Long id, RenderedImage source, int index) throws RemoteException {
        server.setRenderedSource(id, source, index);
    }

    /** Sets the source of the image as a RenderedOp on the server side */
    public void setRenderedSource(Long id, RenderedOp source, int index) throws RemoteException {
        server.setRenderedSource(id, source, index);
    }

    /** Sets the source of the image which is on the same server */
    public void setRenderedSource(Long id, Long sourceId, int index) throws RemoteException {
        server.setRenderedSource(id, sourceId, index);
    }

    /** Sets the source of the image which is on a different server */
    public void setRenderedSource(Long id, Long sourceId, String serverName, String opName, int index)
            throws RemoteException {
        server.setRenderedSource(id, sourceId, serverName, opName, index);
    }

    /// Renderable mode methods

    /**
     * Gets the minimum X coordinate of the rendering-independent image stored against the given ID.
     *
     * @return the minimum X coordinate of the rendering-independent image data.
     */
    public float getRenderableMinX(Long id) throws RemoteException {
        return server.getRenderableMinX(id);
    }

    /**
     * Gets the minimum Y coordinate of the rendering-independent image stored against the given ID.
     *
     * @return the minimum X coordinate of the rendering-independent image data.
     */
    public float getRenderableMinY(Long id) throws RemoteException {
        return server.getRenderableMinY(id);
    }

    /**
     * Gets the width (in user coordinate space) of the <code>RenderableImage</code> stored against the given ID.
     *
     * @return the width of the renderable image in user coordinates.
     */
    public float getRenderableWidth(Long id) throws RemoteException {
        return server.getRenderableWidth(id);
    }

    /**
     * Gets the height (in user coordinate space) of the <code>RenderableImage</code> stored against the given ID.
     *
     * @return the height of the renderable image in user coordinates.
     */
    public float getRenderableHeight(Long id) throws RemoteException {
        return server.getRenderableHeight(id);
    }

    /**
     * Creates a RenderedImage instance of this image with width w, and height h in pixels. The RenderContext is built
     * automatically with an appropriate usr2dev transform and an area of interest of the full image. All the rendering
     * hints come from hints passed in.
     *
     * <p>If w == 0, it will be taken to equal Math.round(h*(getWidth()/getHeight())). Similarly, if h == 0, it will be
     * taken to equal Math.round(w*(getHeight()/getWidth())). One of w or h must be non-zero or else an
     * IllegalArgumentException will be thrown.
     *
     * <p>The created RenderedImage may have a property identified by the String HINTS_OBSERVED to indicate which
     * RenderingHints were used to create the image. In addition any RenderedImages that are obtained via the
     * getSources() method on the created RenderedImage may have such a property.
     *
     * @param w the width of rendered image in pixels, or 0.
     * @param h the height of rendered image in pixels, or 0.
     * @param hints a RenderingHints object containg hints.
     * @return a RenderedImage containing the rendered data.
     */
    public RenderedImage createScaledRendering(Long id, int w, int h, SerializableState hintsState)
            throws RemoteException {

        guiPrint("createScaledRendering\n");
        return server.createScaledRendering(id, w, h, hintsState);
    }

    /**
     * Returnd a RenderedImage instance of this image with a default width and height in pixels. The RenderContext is
     * built automatically with an appropriate usr2dev transform and an area of interest of the full image. The
     * rendering hints are empty. createDefaultRendering may make use of a stored rendering for speed.
     *
     * @return a RenderedImage containing the rendered data.
     */
    public RenderedImage createDefaultRendering(Long id) throws RemoteException {

        guiPrint("createDefaultRendering\n");
        return server.createDefaultRendering(id);
    }

    /**
     * Creates a RenderedImage that represented a rendering of this image using a given RenderContext. This is the most
     * general way to obtain a rendering of a RenderableImage.
     *
     * <p>The created RenderedImage may have a property identified by the String HINTS_OBSERVED to indicate which
     * RenderingHints (from the RenderContext) were used to create the image. In addition any RenderedImages that are
     * obtained via the getSources() method on the created RenderedImage may have such a property.
     *
     * @param renderContext the RenderContext to use to produce the rendering.
     * @return a RenderedImage containing the rendered data.
     */
    public RenderedImage createRendering(Long id, SerializableState renderContextState) throws RemoteException {

        guiPrint("createRendering\n");
        return server.createRendering(id, renderContextState);
    }

    /**
     * Creates a RenderableOp on the server side with a parameter block empty of sources. The sources are set by
     * separate calls depending upon the type and serializabilty of the source.
     */
    public void createRenderableOp(Long id, String opName, ParameterBlock pb) throws RemoteException {

        guiPrint("createRenderableOp\n");
        server.createRenderableOp(id, opName, pb);
    }

    /** Calls for rendering of a RenderableOp with the given SerializableState which should be a RenderContextState. */
    public Long getRendering(Long id, SerializableState rcs) throws RemoteException {
        return server.getRendering(id, rcs);
    }

    /** Sets the source of the image which is on the same server */
    public void setRenderableSource(Long id, Long sourceId, int index) throws RemoteException {
        server.setRenderableSource(id, sourceId, index);
    }

    /** Sets the source of the image which is on a different server */
    public void setRenderableSource(Long id, Long sourceId, String serverName, String opName, int index)
            throws RemoteException {
        server.setRenderableSource(id, sourceId, serverName, opName, index);
    }

    /**
     * Sets the source of the operation refered to by the supplied <code>id</code> to the <code>RenderableRMIServerProxy
     * </code> that exists on the supplied <code>serverName</code> under the supplied <code>sourceId</code>.
     */
    public void setRenderableRMIServerProxyAsSource(Long id, Long sourceId, String serverName, String opName, int index)
            throws RemoteException {
        server.setRenderableRMIServerProxyAsSource(id, sourceId, serverName, opName, index);
    }

    /** Sets the source of the image as a RenderableOp on the server side. */
    public void setRenderableSource(Long id, RenderableOp source, int index) throws RemoteException {
        server.setRenderableSource(id, source, index);
    }

    /** Sets the source of the image as a RenderableImage on the server side. */
    public void setRenderableSource(Long id, SerializableRenderableImage source, int index) throws RemoteException {
        server.setRenderableSource(id, source, index);
    }

    /** Sets the source of the image as a RenderedImage on the server side */
    public void setRenderableSource(Long id, RenderedImage source, int index) throws RemoteException {
        server.setRenderableSource(id, source, index);
    }

    /** Maps the RenderContext for the remote Image */
    public SerializableState mapRenderContext(int id, Long nodeId, String operationName, SerializableState rcs)
            throws RemoteException {

        guiPrint("mapRenderContext\n");
        return server.mapRenderContext(id, nodeId, operationName, rcs);
    }

    /** Gets the Bounds2D of the specified Remote Image */
    public SerializableState getBounds2D(Long nodeId, String operationName) throws RemoteException {

        guiPrint("getBounds2D for id = " + nodeId + "\n");
        return server.getBounds2D(nodeId, operationName);
    }

    /**
     * Returns <code>true</code> if successive renderings with the same arguments may produce different results for this
     * opName
     *
     * @return <code>false</code> indicating that the rendering is static.
     */
    public boolean isDynamic(String opName) throws RemoteException {
        return server.isDynamic(opName);
    }

    /**
     * Returns <code>true</code> if successive renderings with the same arguments may produce different results for this
     * opName
     *
     * @return <code>false</code> indicating that the rendering is static.
     */
    public boolean isDynamic(Long id) throws RemoteException {
        return server.isDynamic(id);
    }

    /** Gets the operation names supported on the Server */
    public String[] getServerSupportedOperationNames() throws RemoteException {

        guiPrint("getServerSupportedOperationNames\n");
        return server.getServerSupportedOperationNames();
    }

    /** Gets the <code>OperationDescriptor</code>s of the operations supported on this server. */
    public List getOperationDescriptors() throws RemoteException {

        guiPrint("Getting list of all OperationDescriptors\n");
        return server.getOperationDescriptors();
    }

    /**
     * Calculates the region over which two distinct renderings of an operation may be expected to differ.
     *
     * <p>The class of the returned object will vary as a function of the nature of the operation. For rendered and
     * renderable two- dimensional images this should be an instance of a class which implements <code>java.awt.Shape
     * </code>.
     *
     * @return The region over which the data of two renderings of this operation may be expected to be invalid or
     *     <code>null</code> if there is no common region of validity.
     */
    public SerializableState getInvalidRegion(
            Long id,
            ParameterBlock oldParamBlock,
            SerializableState oldHints,
            ParameterBlock newParamBlock,
            SerializableState newHints)
            throws RemoteException {
        return server.getInvalidRegion(id, oldParamBlock, oldHints, newParamBlock, newHints);
    }

    /**
     * Returns a conservative estimate of the destination region that can potentially be affected by the pixels of a
     * rectangle of a given source.
     *
     * @param id A <code>Long</code> identifying the node for whom the destination region needs to be calculated .
     * @param sourceRect The <code>Rectangle</code> in source coordinates.
     * @param sourceIndex The index of the source image.
     * @return A <code>Rectangle</code> indicating the potentially affected destination region, or <code>null</code> if
     *     the region is unknown.
     */
    public Rectangle mapSourceRect(Long id, Rectangle sourceRect, int sourceIndex) throws RemoteException {

        guiPrint("mapSourceRect\n");
        return server.mapSourceRect(id, sourceRect, sourceIndex);
    }

    /**
     * Returns a conservative estimate of the region of a specified source that is required in order to compute the
     * pixels of a given destination rectangle.
     *
     * @param id A <code>Long</code> identifying the node for whom the source region needs to be calculated .
     * @param destRect The <code>Rectangle</code> in destination coordinates.
     * @param sourceIndex The index of the source image.
     * @return A <code>Rectangle</code> indicating the required source region.
     */
    public Rectangle mapDestRect(Long id, Rectangle destRect, int sourceIndex) throws RemoteException {

        guiPrint("mapDestRect\n");
        return server.mapDestRect(id, destRect, sourceIndex);
    }

    /** A method that handles a change in some critical parameter. */
    public Long handleEvent(Long renderedOpID, String propName, Object oldValue, Object newValue)
            throws RemoteException {
        return server.handleEvent(renderedOpID, propName, oldValue, newValue);
    }

    /**
     * A method that handles a change in one of it's source's rendering, i.e. a change that would be signalled by
     * RenderingChangeEvent.
     */
    public Long handleEvent(Long renderedOpID, int srcIndex, SerializableState srcInvalidRegion, Object oldRendering)
            throws RemoteException {

        return server.handleEvent(renderedOpID, srcIndex, srcInvalidRegion, oldRendering);
    }

    /**
     * Returns the server's capabilities as a <code>NegotiableCapabilitySet</code>. Currently the only capabilities that
     * are returned are those of TileCodecs.
     */
    public NegotiableCapabilitySet getServerCapabilities() throws RemoteException {
        return server.getServerCapabilities();
    }

    /**
     * Informs the server of the negotiated values that are the result of a successful negotiation.
     *
     * @param id An ID for the node which must be unique across all clients.
     * @param negotiatedValues The result of the negotiation.
     */
    public void setServerNegotiatedValues(Long id, NegotiableCapabilitySet negotiatedValues) throws RemoteException {
        server.setServerNegotiatedValues(id, negotiatedValues);
    }

    /**
     * Starts a server on a given port. The RMI registry must be running on the server host.
     *
     * <p>The usage of this class is
     *
     * <pre>
     * java -Djava.rmi.server.codebase=file:$ImageN/lib/jai.jar \
     * -Djava.rmi.server.useCodebaseOnly=false \
     * -Djava.security.policy=\
     * file:`pwd`/policy com.sun.media.jai.rmi.JAIRMIImageServer \
     * [-host hostName] [-port portNumber]
     * </pre>
     *
     * The default host is the local host and the default port is 1099.
     *
     * @param args the port number as a command-line argument.
     */
    public static void main(String[] args) {

        JFrame frame;

        // Set the security manager.
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }

        frame = new JFrame("ImageN Network Imaging Server");

        // Set the host name and port number.
        String host = null;
        int port = 1099; // default port is 1099
        for (int i = 0; i < args.length; i++) {
            if (args[i].equalsIgnoreCase("-host")) {
                host = args[++i];
            } else if (args[i].equalsIgnoreCase("-port")) {
                port = Integer.parseInt(args[++i]);
            }
        }

        // Default to the local host if the host was not specified.
        if (host == null) {
            try {
                host = InetAddress.getLocalHost().getHostAddress();
            } catch (Exception e) {
                System.err.println("Server: Error:" + e.getMessage());
                e.printStackTrace();
            }
        }

        System.out.println("Server: using host/port " + host + ":" + port);

        JAIRMIServerWrapper im = null;

        try {
            im = new JAIRMIServerWrapper(frame);

            String serverName =
                    new String("rmi://" + host + ":" + port + "/" + JAIRMIDescriptor.IMAGE_SERVER_BIND_NAME);
            System.out.println("Registering image server as \"" + serverName + "\".");
            Naming.rebind(serverName, im);
            System.out.println("Server: Bound RemoteImageServer into the registry.");
        } catch (Exception e) {
            System.err.println("Server construction error: " + e.getMessage());
            e.printStackTrace();
        }

        // Pack the frame and make it visible.
        frame.pack();
        Dimension d = frame.getSize();
        frame.setSize(d.width, d.height + 20);
        frame.setVisible(true);
    }
}
