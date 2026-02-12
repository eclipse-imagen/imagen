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

import java.awt.image.RenderedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

/** Mock Operation dispatch, registries are expected to register until service provider interface is defined. */
public class OperationDispatch {
    private static OperationDispatch defaultInstance = null;

    public static OperationDispatch getDefaultInstance() {
        if (defaultInstance == null) {
            defaultInstance = new OperationDispatch();
        }
        return defaultInstance;
    }

    Map<String, OperationSet> registry = new HashMap<String, OperationSet>();

    /** Set of operation implementations */
    class OperationSet {
        String operationName;

        List<Operation> factorySet = new ArrayList<Operation>();

        public OperationSet(String name) {
            this.operationName = name;
        }

        /** Candidates for the provided builder, may be empty */
        SortedSet<Operation> query(OperationBuilder builder) {
            return Collections.emptySortedSet();
        }

        public void add(Operation factory) {
            factorySet.add(factory);
        }

        public Operation resolve(OperationBuilder builder) {
            return factorySet.get(0); // first post!
        }
    }

    RenderedImage create(OperationBuilder builder) {
        if (builder == null) {
            throw new NullPointerException("OperationBuilder required");
        }
        String name = builder.getName();
        OperationSet candidates = registry.get(name);
        Operation operation = candidates.resolve(builder);

        return operation.create(builder);
    }

    public void register(Operation factory) {
        String name = factory.getName();
        if (!registry.containsKey(name)) {
            registry.put(name, new OperationSet(name));
        }
        OperationSet set = registry.get(name);
        set.add(factory);
    }
}
