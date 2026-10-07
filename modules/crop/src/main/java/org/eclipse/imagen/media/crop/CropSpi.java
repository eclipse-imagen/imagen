/* JAI-Ext - OpenSource Java Advanced Image Extensions Library
 *    http://www.geo-solutions.it/
 *    Copyright 2014 GeoSolutions
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.eclipse.imagen.media.crop;

import org.eclipse.imagen.OperationDescriptor;
import org.eclipse.imagen.OperationRegistry;
import org.eclipse.imagen.OperationRegistrySpi;
import org.eclipse.imagen.registry.RenderedRegistryMode;

/**
 * OperationRegistrySpi implementation to register the "Crop" operation and its associated image factories.
 *
 * @author Andrea Aime
 */
public class CropSpi implements OperationRegistrySpi {

    /** The name of the product to which these operations belong. */
    private String productName = "org.eclipse.imagen.media";

    /**
     * Registers the Crop operation and its associated image factories across all supported operation modes.
     *
     * @param registry The registry with which to register the operations and their factories.
     */
    public void updateRegistry(OperationRegistry registry) {
        OperationDescriptor op = new CropDescriptor();
        if (registry.getDescriptor(OperationDescriptor.class, op.getName()) == null) {
            registry.registerDescriptor(op);
            registry.registerFactory(RenderedRegistryMode.MODE_NAME, op.getName(), productName, new CropCRIF());
        }
    }
}
