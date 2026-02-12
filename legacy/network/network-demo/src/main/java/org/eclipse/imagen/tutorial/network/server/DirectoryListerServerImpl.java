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
import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import org.eclipse.imagen.tutorial.network.DirectoryLister;

/**
 * An implementation of the <code>DirectoryLister</code> interface. This implementation is specific to image file names,
 * and uses the <code>ImageFilenameFilter</code> as the <code>FilenameFilter</code> that decides which file names will
 * be returned from this class' methods.
 */
public class DirectoryListerServerImpl extends UnicastRemoteObject implements DirectoryLister {

    /** Construct an instance of <code>DirectoryListerServerImpl</code>. */
    public DirectoryListerServerImpl() throws RemoteException {
        System.out.println("DirectoryListerServerImpl constructed.");
    }

    /**
     * Returns a list containing the names of all files in the specified directory. The file names returned are relative
     * to the directory path specified. Only image file names are returned as identified by <code>
     * ImageFilenameFilterImpl.accept()</code>.
     */
    public String[] getDirectoryListing(String pathToDirectory) throws RemoteException {

        File directory = new File(pathToDirectory);

        if (directory.exists() == false) System.err.println("Specified path name does not exist.");
        if (directory.isDirectory() == false) System.err.println("Specified path name is not a directory");

        return directory.list(new ImageFilenameFilterImpl());
    }

    /**
     * Returns a list containing the names of all files in the specified directory. The file names returned are absolute
     * path names. Only image file names are returned as identified by <code>ImageFilenameFilterImpl.accept()</code>.
     */
    public String[] getPathDirectoryAbsoluteListing(String pathToDirectory) throws RemoteException {

        File directory = new File(pathToDirectory);
        if (directory.exists() == false) System.err.println("Specified path name does not exist.");
        if (directory.isDirectory() == false) System.err.println("Specified path name is not a directory");

        File[] files = directory.listFiles(new ImageFilenameFilterImpl());

        String absolutePaths[] = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            absolutePaths[i] = files[i].getAbsolutePath();
        }

        return absolutePaths;
    }

    public static void main(String args[]) {
        // Set the security manager.
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }

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

        DirectoryListerServerImpl dirLister = null;

        try {
            dirLister = new DirectoryListerServerImpl();

            String dirListingServerName = new String("rmi://" + host + ":" + port + "/" + "DirectoryListingServer");
            System.out.println("Registering image server as \"" + dirListingServerName + "\".");
            Naming.rebind(dirListingServerName, dirLister);
            System.out.println("Server: Bound DirectoryListingServer into " + "the registry.");
        } catch (Exception e) {
            System.err.println("Server construction error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
