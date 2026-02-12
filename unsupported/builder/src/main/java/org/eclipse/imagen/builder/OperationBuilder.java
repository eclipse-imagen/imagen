/* Copyright (c) 2018 Jody Garnett and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution and is available at
 * http://www.opensource.org/licenses/apache2.0.php.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.eclipse.imagen.builder;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class OperationBuilder {
    /** Operation name. */
    final String name;

    int sourceCount = 0;

    /** Parameter map */
    Map<String, Object> parameters = new HashMap<String, Object>();

    /** Optional rendering hints */
    RenderingHints hints;

    public OperationBuilder(String name) {
        if (name == null) {
            throw new NullPointerException("OperationBuilder name is required");
        }
        this.name = name;
    }

    /**
     * Add sources (will be named "source1","source2", ...)
     *
     * @param renderedImage
     * @return this builder is returned for chaining
     */
    OperationBuilder source(RenderedImage... sources) {
        if (sources == null) {
            throw new NullPointerException("source is required");
        }
        for (RenderedImage source : sources) {
            source(source);
        }
        return this;
    }

    /**
     * Add source (will be named "source1","source2", ...)
     *
     * @param renderedImage
     * @return this builder is returned for chaining
     */
    OperationBuilder source(RenderedImage source) {
        if (source == null) {
            throw new NullPointerException("source is required");
        }
        String key = "source" + (sourceCount++);
        parameters.put(key, source);
        return this;
    }

    /**
     * Add named parameter with the provided value.
     *
     * @param name parameter name
     * @param value parameter value
     * @return this builder is returned for chaining
     */
    OperationBuilder parameter(String name, Object value) {
        parameters.put(name, value);
        return this;
    }
    /**
     * Add named hint with the provided value.
     *
     * @param hint Rendering hint
     * @param value hint value
     * @return this builder is returned for chaining
     */
    OperationBuilder hint(String key, Object value) {
        hints.put(key, value);
        return this;
    }

    OperationBuilder hint(KeyedHint hint) {
        hints.put(hint.key(), hint.value());
        return this;
    }

    RenderedImage build() {
        return OperationDispatch.getDefaultInstance().create(this);
    }

    public String getName() {
        return name;
    }

    public Map<String, Object> getParameters() {
        return Collections.unmodifiableMap(parameters);
    }

    public Object getSource(int i) {
        return parameters.get("source" + i);
    }
}
