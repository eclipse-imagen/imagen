/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 *
 * This Example Content is intended to demonstrate usage of Eclipse technology. It is
 * provided to you under the terms and conditions of the Eclipse Distribution License
 * v1.0 which is available at http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */
package org.eclipse.imagen.tutorial.network;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DirectoryLister extends Remote {

    /**
     * Returns a list containing the names of all files in the specified directory. The file names returned are relative
     * to the directory path specified.
     */
    String[] getDirectoryListing(String pathToDirectory) throws RemoteException;

    /**
     * Returns a list containing the names of all files in the specified directory. The file names returned are absolute
     * path names.
     */
    String[] getPathDirectoryAbsoluteListing(String pathToDirectory) throws RemoteException;
}
