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
 * SPDX-License-Identifier: Apache-2.0
 */
package org.eclipse.imagen;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A wrapper class on <code>OperationRegistry</code> which is thread safe. Every method is wrapped with an appropriate
 * read or a write lock.
 */
final class ThreadSafeOperationRegistry extends OperationRegistry {

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public String toString() {
        lock.readLock().lock();
        try {
            return super.toString();
        } finally {
            lock.readLock().unlock();
        }
    }

    public void writeToStream(OutputStream out) throws IOException {
        lock.readLock().lock();
        try {
            super.writeToStream(out);
        } finally {
            lock.readLock().unlock();
        }
    }

    public void initializeFromStream(InputStream in) throws IOException {
        lock.writeLock().lock();
        try {
            super.initializeFromStream(in);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void updateFromStream(InputStream in) throws IOException {
        lock.writeLock().lock();
        try {
            super.updateFromStream(in);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

        lock.writeLock().lock();
        try {
            super.readExternal(in);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        lock.readLock().lock();
        try {
            super.writeExternal(out);
        } finally {
            lock.readLock().unlock();
        }
    }

    /** ******************** NEW ImageN methods ************************ */
    public void removeRegistryMode(String modeName) {
        lock.writeLock().lock();
        try {
            super.removeRegistryMode(modeName);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public String[] getRegistryModes() {
        lock.readLock().lock();
        try {
            return super.getRegistryModes();
        } finally {
            lock.readLock().unlock();
        }
    }

    public void registerDescriptor(RegistryElementDescriptor descriptor) {
        lock.writeLock().lock();
        try {
            super.registerDescriptor(descriptor);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void unregisterDescriptor(RegistryElementDescriptor descriptor) {
        lock.writeLock().lock();
        try {
            super.unregisterDescriptor(descriptor);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public RegistryElementDescriptor getDescriptor(Class descriptorClass, String descriptorName) {
        lock.readLock().lock();
        try {
            return super.getDescriptor(descriptorClass, descriptorName);
        } finally {
            lock.readLock().unlock();
        }
    }

    public RegistryElementDescriptor getDescriptor(String modeName, String descriptorName) {
        lock.readLock().lock();
        try {
            return super.getDescriptor(modeName, descriptorName);
        } finally {
            lock.readLock().unlock();
        }
    }

    public List getDescriptors(Class descriptorClass) {
        lock.readLock().lock();
        try {
            return super.getDescriptors(descriptorClass);
        } finally {
            lock.readLock().unlock();
        }
    }

    public String[] getDescriptorNames(Class descriptorClass) {
        lock.readLock().lock();
        try {
            return super.getDescriptorNames(descriptorClass);
        } finally {
            lock.readLock().unlock();
        }
    }

    public List getDescriptors(String modeName) {

        lock.readLock().lock();
        try {
            return super.getDescriptors(modeName);
        } finally {
            lock.readLock().unlock();
        }
    }

    public String[] getDescriptorNames(String modeName) {
        lock.readLock().lock();
        try {
            return super.getDescriptorNames(modeName);
        } finally {
            lock.readLock().unlock();
        }
    }

    public void setProductPreference(
            String modeName, String descriptorName, String preferredProductName, String otherProductName) {
        lock.writeLock().lock();
        try {
            super.setProductPreference(modeName, descriptorName, preferredProductName, otherProductName);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void unsetProductPreference(
            String modeName, String descriptorName, String preferredProductName, String otherProductName) {
        lock.writeLock().lock();
        try {
            super.unsetProductPreference(modeName, descriptorName, preferredProductName, otherProductName);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void clearProductPreferences(String modeName, String descriptorName) {
        lock.writeLock().lock();
        try {
            super.clearProductPreferences(modeName, descriptorName);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public String[][] getProductPreferences(String modeName, String descriptorName) {
        lock.readLock().lock();
        try {
            return super.getProductPreferences(modeName, descriptorName);
        } finally {
            lock.readLock().unlock();
        }
    }

    public Vector getOrderedProductList(String modeName, String descriptorName) {
        lock.readLock().lock();
        try {
            return super.getOrderedProductList(modeName, descriptorName);
        } finally {
            lock.readLock().unlock();
        }
    }

    public void registerFactory(String modeName, String descriptorName, String productName, Object factory) {
        lock.writeLock().lock();
        try {
            super.registerFactory(modeName, descriptorName, productName, factory);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void unregisterFactory(String modeName, String descriptorName, String productName, Object factory) {
        lock.writeLock().lock();
        try {
            super.unregisterFactory(modeName, descriptorName, productName, factory);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void setFactoryPreference(
            String modeName, String descriptorName, String productName, Object preferredOp, Object otherOp) {
        lock.writeLock().lock();
        try {
            super.setFactoryPreference(modeName, descriptorName, productName, preferredOp, otherOp);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void unsetFactoryPreference(
            String modeName, String descriptorName, String productName, Object preferredOp, Object otherOp) {
        lock.writeLock().lock();
        try {
            super.unsetFactoryPreference(modeName, descriptorName, productName, preferredOp, otherOp);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void clearFactoryPreferences(String modeName, String descriptorName, String productName) {
        lock.writeLock().lock();
        try {
            super.clearFactoryPreferences(modeName, descriptorName, productName);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Object[][] getFactoryPreferences(String modeName, String descriptorName, String productName) {
        lock.readLock().lock();
        try {
            return super.getFactoryPreferences(modeName, descriptorName, productName);
        } finally {
            lock.readLock().unlock();
        }
    }

    public List getOrderedFactoryList(String modeName, String descriptorName, String productName) {
        lock.readLock().lock();
        try {
            return super.getOrderedFactoryList(modeName, descriptorName, productName);
        } finally {
            lock.readLock().unlock();
        }
    }

    public Iterator getFactoryIterator(String modeName, String descriptorName) {
        lock.readLock().lock();
        try {
            return super.getFactoryIterator(modeName, descriptorName);
        } finally {
            lock.readLock().unlock();
        }
    }

    public Object getFactory(String modeName, String descriptorName) {
        lock.readLock().lock();
        try {
            return super.getFactory(modeName, descriptorName);
        } finally {
            lock.readLock().unlock();
        }
    }

    public Object invokeFactory(String modeName, String descriptorName, Object[] args) {
        lock.readLock().lock();
        try {
            return super.invokeFactory(modeName, descriptorName, args);
        } finally {
            lock.readLock().unlock();
        }
    }

    public void addPropertyGenerator(String modeName, String descriptorName, PropertyGenerator generator) {
        lock.writeLock().lock();
        try {
            super.addPropertyGenerator(modeName, descriptorName, generator);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void removePropertyGenerator(String modeName, String descriptorName, PropertyGenerator generator) {
        lock.writeLock().lock();
        try {
            super.removePropertyGenerator(modeName, descriptorName, generator);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void copyPropertyFromSource(String modeName, String descriptorName, String propertyName, int sourceIndex) {
        lock.writeLock().lock();
        try {
            super.copyPropertyFromSource(modeName, descriptorName, propertyName, sourceIndex);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void suppressProperty(String modeName, String descriptorName, String propertyName) {
        lock.writeLock().lock();
        try {
            super.suppressProperty(modeName, descriptorName, propertyName);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void suppressAllProperties(String modeName, String descriptorName) {
        lock.writeLock().lock();
        try {
            super.suppressAllProperties(modeName, descriptorName);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void clearPropertyState(String modeName) {
        lock.writeLock().lock();
        try {
            super.clearPropertyState(modeName);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public String[] getGeneratedPropertyNames(String modeName, String descriptorName) {
        lock.readLock().lock();
        try {
            return super.getGeneratedPropertyNames(modeName, descriptorName);
        } finally {
            lock.readLock().unlock();
        }
    }

    public PropertySource getPropertySource(String modeName, String descriptorName, Object op, Vector sources) {
        lock.readLock().lock();
        try {
            return super.getPropertySource(modeName, descriptorName, op, sources);
        } finally {
            lock.readLock().unlock();
        }
    }

    public PropertySource getPropertySource(OperationNode op) {
        lock.readLock().lock();
        try {
            return super.getPropertySource(op);
        } finally {
            lock.readLock().unlock();
        }
    }

    public void registerServices(ClassLoader cl) throws IOException {
        lock.writeLock().lock();
        try {
            super.registerServices(cl);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /** ******************** DEPRECATED METHODS ************************ */
    public void unregisterOperationDescriptor(String operationName) {
        lock.writeLock().lock();
        try {
            super.unregisterOperationDescriptor(operationName);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void clearOperationPreferences(String operationName, String productName) {
        lock.writeLock().lock();
        try {
            super.clearOperationPreferences(operationName, productName);
        } finally {
            lock.writeLock().unlock();
        }
    }
}
