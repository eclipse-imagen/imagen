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

package org.eclipse.imagen.registry;

import java.lang.reflect.Method;
import org.eclipse.imagen.JAI;
import org.eclipse.imagen.RegistryMode;
import org.eclipse.imagen.util.ImagingListener;
import org.eclipse.imagen.remote.RemoteDescriptor;

/**
 * A class which provides information about the "remoteRendered" registry
 * mode.
 *
 * @since JAI 1.1
 */
public class RemoteRenderedRegistryMode extends RegistryMode {

    public static final String MODE_NAME = "remoteRendered";

    // The Method used to "create" objects from this factory.
    private static Method factoryMethod = null;

    private static Method getThisFactoryMethod() {

	if (factoryMethod != null)
	    return factoryMethod;

	// The factory Class that this registry mode represents.
	Class factoryClass =
		    org.eclipse.imagen.remote.RemoteRIF.class;

	try {
	    Class[] paramTypes = new Class[]
		    {java.lang.String.class,
		     java.lang.String.class,
		     java.awt.image.renderable.ParameterBlock.class,
		     java.awt.RenderingHints.class};

	    factoryMethod = factoryClass.getMethod("create", paramTypes);
	} catch (NoSuchMethodException e) {
            ImagingListener listener =
                JAI.getDefaultInstance().getImagingListener();
            String message = JaiI18N.getString("RegistryMode0") + " " +
                             factoryClass.getName() + ".";
            listener.errorOccurred(message, e,
                                   RemoteRenderedRegistryMode.class, false);
//	    e.printStackTrace();
	}

	return factoryMethod;
    }

    /**
     * Creates a <code>RemoteRenderedRegistryMode</code> for describing
     * the "remoteRendered" registry mode.
     */
    public RemoteRenderedRegistryMode() {
	super(MODE_NAME,
	      RemoteDescriptor.class,
	      getThisFactoryMethod().getReturnType(),
	      getThisFactoryMethod(),
	      false,
	      false);
    }
}

