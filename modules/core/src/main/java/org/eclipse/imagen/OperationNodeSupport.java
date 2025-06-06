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

package org.eclipse.imagen;

import java.awt.RenderingHints;
import java.awt.image.renderable.ParameterBlock;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

/**
 * This is a utility class that can be used by <code>OperationNode</code>s to consolidate common functionality. An
 * instance of this class may be used as a member field of the <code>OperationNode</code> and some of the <code>
 * OperationNode</code>'s work delegated to it.
 *
 * @since JAI 1.1
 */
public class OperationNodeSupport implements Serializable {

    // Constants supporting compare().
    private static final int PB_EQUAL = 0x0;
    private static final int PB_SOURCES_DIFFER = 0x1;
    private static final int PB_PARAMETERS_DIFFER = 0x2;
    private static final int PB_DIFFER = PB_SOURCES_DIFFER | PB_PARAMETERS_DIFFER;

    // The OperationRegistryMode.
    private String registryModeName;

    // Critical attributes.
    private String opName;
    private transient OperationRegistry registry;
    private transient ParameterBlock pb;
    private transient RenderingHints hints;

    // Event helper.
    private PropertyChangeSupportJAI eventManager;

    /**
     * This instance variable is lazily constructed only when one of the PropertySource methods or one of the local
     * property environment mutators is accessed. PropertyEnvironment is a package scope class.
     */
    private transient PropertyEnvironment propertySource = null;

    /**
     * Stores local property environment modifications sequentially as a PropertyGenerator, a String, or a CopyDirective
     * depending on which local property environment mutator method was invoked.
     */
    private Vector localPropEnv = new Vector();

    /**
     * <code>Map</code> of <code>ParamObserver</code>s of instances of <code>DeferredData</code> in the parameter <code>
     * Vector</code>.
     */
    private Hashtable paramObservers = new Hashtable();

    /** Compare the contents of two <code>ParameterBlock</code>s. */
    private static int compare(ParameterBlock pb1, ParameterBlock pb2) {
        if (pb1 == null && pb2 == null) {
            return PB_EQUAL;
        }

        if ((pb1 == null && pb2 != null) || (pb1 != null && pb2 == null)) {
            return PB_DIFFER;
        }

        int result = PB_EQUAL;
        if (!equals(pb1.getSources(), pb2.getSources())) {
            result |= PB_SOURCES_DIFFER;
        }
        if (!equals(pb1.getParameters(), pb2.getParameters())) {
            result |= PB_PARAMETERS_DIFFER;
        }

        return result;
    }

    private static boolean equals(ParameterBlock pb1, ParameterBlock pb2) {
        return pb1 == null
                ? pb2 == null
                : equals(pb1.getSources(), pb2.getSources()) && equals(pb1.getParameters(), pb2.getParameters());
    }

    private static boolean equals(Object o1, Object o2) {
        return o1 == null ? o2 == null : o1.equals(o2);
    }

    /**
     * Constructs an <code>OperationNodeSupport</code> instance. All parameters except <code>opName</code> may be <code>
     * null</code>. If non-<code>null</code> the <code>PropertyChangeSupportJAI</code> should have been created with the
     * node as its event source (note that this cannot be verified).
     *
     * <p>The <code>ParameterBlock</code> may include <code>DeferredData</code> parameters. These will not be evaluated
     * until their values are actually required, i.e., when the node is rendered. Any <code>Observable</code> events
     * generated by such <code>DeferredData</code> parameters will be trapped by the node, converted to a <code>
     * PropertyChangeEventJAI</code> named "parameters", and forwarded to any listeners registered with the supplied.
     * <code>eventManager</code>. The old and new values of the event object so generated will be the previous and
     * current values, respectively, of the data object wrapped by the <code>DeferredData</code> parameter, and thus
     * will be instances of the class returned by the <code>getDataClass()</code> method of the <code>DeferredData
     * </code> parameter.
     *
     * @param registryModeName The name of the registry mode concerned.
     * @param opName The operation name to set.
     * @param registry The <code>OperationRegistry</code> to set; it may be <code>null</code> in which case the registry
     *     will be set to the default JAI registry.
     * @param pb The <code>ParameterBlock</code> to set; it may be <code>null</code>.
     * @param hints The new <code>RenderingHints</code> to be set; it may be <code>null</code>.
     * @param eventManager The event helper object. The property change event source of this object should be the <code>
     *     OperationNode</code> which is using the constructed <code>OperationNodeSupport</code> instance. If <code>null
     *     </code> no events will be fired.
     * @throws IllegalArgumentException if <code>registryModeName</code> or <code>opName</code> is <code>null</code>.
     */
    public OperationNodeSupport(
            String registryModeName,
            String opName,
            OperationRegistry registry,
            ParameterBlock pb,
            RenderingHints hints,
            PropertyChangeSupportJAI eventManager) {
        if (registryModeName == null || opName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }

        // Set instance variables.
        this.registryModeName = registryModeName;
        this.opName = opName;
        if (registry == null) this.registry = JAI.getDefaultInstance().getOperationRegistry();
        else this.registry = registry;
        this.pb = pb;
        this.hints = hints;
        this.eventManager = eventManager;

        // Set any DeferredData Observers.
        if (pb != null) {
            updateObserverMap(pb.getParameters());
        }
    }

    /** Class representing a copy-from-source directive set via <code>copyPropertyFromSource()</code>. */
    private class CopyDirective implements Serializable {
        /** The name of the property. */
        private String name;

        /** The index of the source from which to copy the property. */
        private int index;

        /**
         * Constructor.
         *
         * @param name The name of the property.
         * @param index The index of the source from which to copy the property.
         */
        CopyDirective(String name, int index) {
            if (name == null) {
                throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
            }
            this.name = name;
            this.index = index;
        }

        String getName() {
            return name;
        }

        int getIndex() {
            return index;
        }
    }

    /** Class which is an <code>Observer</code> of a <code>DeferredData</code> parameter. */
    private class ParamObserver implements Observer {
        /** The index of the associated parameter. */
        final int paramIndex;

        /** The <code>DeferredData</code> object to observe. */
        final DeferredData dd;

        /**
         * Constructor.
         *
         * @param paramIndex The index of the associated parameter.
         * @param dd The <code>DeferredData</code> object to observe.
         */
        ParamObserver(int paramIndex, DeferredData dd) {
            if (dd == null) {
                throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
            } else if (paramIndex < 0 || (pb != null && (paramIndex >= ((ParameterBlock) pb).getNumParameters()))) {
                throw new ArrayIndexOutOfBoundsException();
            }

            this.paramIndex = paramIndex;
            this.dd = dd;

            // Add this object as an Observer of the Deferred Data.
            dd.addObserver(this);
        }

        /**
         * Implementation of <code>Observer</code>. An update from the observed <code>DeferredData</code> causes an
         * event to be fired if the <code>DeferredData</code> had been previously evaluated and there are event
         * listeners.
         */
        public synchronized void update(Observable o, Object arg) {
            if (!(o == dd)) {
                return;
            }

            // Do nothing unless the DeferredData was already evaluated.
            if (arg != null && eventManager != null) {
                Vector params = pb.getParameters();
                Vector oldParams = (Vector) params.clone();
                Vector newParams = (Vector) params.clone();

                oldParams.set(paramIndex, arg);
                newParams.set(paramIndex, dd.getData());

                fireEvent("Parameters", oldParams, newParams);
            }
        }
    }

    /**
     * Updates the <code>Map</code> of <code>Observer</code>s of <code>DeferredData</code> instances in the parameter
     * <code>Vector</code>.
     */
    private void updateObserverMap(Vector parameters) {
        if (parameters == null) {
            return;
        }

        int numParameters = parameters.size();
        for (int i = 0; i < numParameters; i++) {
            Object parameter = parameters.get(i);
            Integer index = new Integer(i);

            // Replace or remove ParamObserver as needed.
            Object oldObs;
            if (parameter instanceof DeferredData) {
                Observer obs = new ParamObserver(i, (DeferredData) parameter);
                oldObs = paramObservers.put(index, obs);
            } else {
                oldObs = paramObservers.remove(index);
            }

            // Unregister Observer from the associated DeferredData.
            if (oldObs != null) {
                ParamObserver obs = (ParamObserver) oldObs;
                obs.dd.deleteObserver(obs);
            }
        }
    }

    /**
     * Returns the name of <code>RegistryMode</code> corresponding to this <code>OperationNode</code>. This value shoud
     * be immutable for a given node. The value is returned by reference.
     */
    public String getRegistryModeName() {
        return registryModeName;
    }

    /** Returns the name of the operation the associated node represents. The value is returned by reference. */
    public String getOperationName() {
        return opName;
    }

    /**
     * Sets the name of the operation the associated node represents.
     * The value is set by reference.
     *
     * <p> If the operation name changes as a result of calling this
     * method according to a case-insensitive
     * comparison by <code>equals()</code> of the old and new names,
     * a <code>PropertyChangeEventJAI<code> named "OperationName"
     * will be fired by the event helper object with old and new values
     * set to the old and new values of the operation name, respectively.
     *
     * @param opName The new operation name to be set.
     *
     * @throws IllegalArgumentException if <code>opName</code> is
     * <code>null</code>.
     */
    public void setOperationName(String opName) {
        if (opName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }

        if (opName.equalsIgnoreCase(this.opName)) return;

        String oldOpName = this.opName;
        this.opName = opName;
        fireEvent("OperationName", oldOpName, opName);
        resetPropertyEnvironment(false);
    }

    /** Returns the <code>OperationRegistry</code> used by the associated node. The value is returned by reference. */
    public OperationRegistry getRegistry() {
        return registry;
    }

    /**
     * Sets the <code>OperationRegistry</code> that is used by the associated
     * node.  If the specified registry is <code>null</code>, the
     * registry will be set to the default JAI registry.  The value is
     * set by reference.
     *
     * <p> If the registry changes according to a direct comparison
     * of the old and new registry references,
     * a <code>PropertyChangeEventJAI<code> named "OperationRegistry"
     * will be fired by the event helper object with old and new values
     * set to the old and new values of the registry, respectively.
     *
     * @param registry  The new <code>OperationRegistry</code> to be set;
     *        it may be <code>null</code>.
     */
    public void setRegistry(OperationRegistry registry) {
        if (registry == null) {
            registry = JAI.getDefaultInstance().getOperationRegistry();
        }
        if (registry != this.registry) {
            OperationRegistry oldRegistry = this.registry;
            this.registry = registry;
            fireEvent("OperationRegistry", oldRegistry, registry);
            resetPropertyEnvironment(false);
        }
    }

    /**
     * Returns the <code>ParameterBlock</code> of the associated node by reference. Nodes desirous of maintaining a
     * consistent state for their <code>ParameterBlock</code> may prefer to clone the value returned by this method.
     */
    public ParameterBlock getParameterBlock() {
        return pb;
    }

    /**
     * Sets the <code>ParameterBlock</code> of the associated node by
     * reference.  If the specified <code>ParameterBlock</code> is
     * <code>null</code>, it is assumed that the associated node has
     * neither input sources nor parameters.  Nodes desirous of maintaining
     * a consistent state for their <code>ParameterBlock</code> may prefer
     * to clone any user-supplied <code>ParameterBlock</code> before passing
     * it to this method.
     *
     * <p> This method does not validate the content of the supplied
     * <code>ParameterBlock</code>.  The caller should ensure that
     * the sources and parameters in the <code>ParameterBlock</code>
     * are suitable for the operation the associated node represents; otherwise
     * some form of error or exception may occur at the time of rendering.
     *
     * <p> If the <code>ParameterBlock</code> changes according to a
     * comparison of the sources and parameters <code>Vector</code>s of the
     * old and new <code>ParameterBlock</code>s using <code>equals()</code>,
     * a <code>PropertyChangeEventJAI<code> named "ParameterBlock"
     * will be fired by the event helper object with old and new values
     * set to the old and new values of the <code>ParameterBlock</code>,
     * respectively.  A <code>PropertyChangeEventJAI<code> named "Sources" or
     * "Parameters" will instead be fired if it can be determined that the
     * <code>ParameterBlock</code> modification has affected only the sources
     * or parameters of the node, respectively.
     *
     * <p> The <code>ParameterBlock</code> may include
     * <code>DeferredData</code> parameters.  These will not be evaluated
     * until their values are actually required, i.e., when the node is
     * rendered.  Any <code>Observable</code> events generated by such
     * <code>DeferredData</code> parameters will be trapped by the node,
     * converted to a <code>PropertyChangeEventJAI</code> named "parameters",
     * and forwarded to any listeners registered with the supplied.
     * <code>eventManager</code>.  The old and new values of the event object
     * so generated will be the previous and current values, respectively, of
     * the data object wrapped by the <code>DeferredData</code> parameter,
     * and thus will be instances of the class returned by the
     * <code>getDataClass()</code> method of the <code>DeferredData</code>
     * parameter.
     *
     * @param pb  The new <code>ParameterBlock</code> to be set;
     *        it may be <code>null</code>.
     */
    public void setParameterBlock(ParameterBlock pb) {
        int comparison = compare(this.pb, pb);
        if (comparison == PB_EQUAL) {
            return;
        }

        ParameterBlock oldPB = this.pb;
        this.pb = pb;

        // Set any DeferredData Observers.
        if (pb != null) {
            updateObserverMap(pb.getParameters());
        }

        if (comparison == PB_SOURCES_DIFFER) {
            // Sources have changed.
            fireEvent("Sources", oldPB.getSources(), pb.getSources());
        } else if (comparison == PB_PARAMETERS_DIFFER) {
            // Parameters have changed.
            fireEvent("Parameters", oldPB.getParameters(), pb.getParameters());
        } else {
            // Sources and parameters have changed.
            fireEvent("ParameterBlock", oldPB, pb);
        }

        resetPropertyEnvironment(false);
    }

    /**
     * Returns the <code>RenderingHints</code> of the associated node by reference. Nodes desirous of maintaining a
     * consistent state for their <code>RenderingHints</code> may prefer to clone the value returned by this method.
     */
    public RenderingHints getRenderingHints() {
        return hints;
    }

    /**
     * Sets the <code>RenderingHints</code> of the associated node.  It is
     * legal for nodes to ignore <code>RenderingHints</code> set on them by
     * this mechanism.  Nodes desirous of maintaining
     * a consistent state for their <code>RenderingHints</code> may prefer
     * to clone any user-supplied <code>RenderingHints</code> before passing
     * it to this method.
     *
     * <p> If the <code>RenderingHints</code> changes according to a
     * comparison by <code>equals()</code> of the old and new hints,
     * a <code>PropertyChangeEventJAI<code> named "RenderingHints"
     * will be fired by the event helper object with old and new values
     * set to the old and new values of the <code>RenderingHints</code>,
     * respectively.
     *
     * @param hints The new <code>RenderingHints</code> to be set;
     *        it may be <code>null</code>.
     */
    public void setRenderingHints(RenderingHints hints) {
        if (equals(this.hints, hints)) {
            return;
        }
        RenderingHints oldHints = this.hints;
        this.hints = hints;
        fireEvent("RenderingHints", oldHints, hints);
        resetPropertyEnvironment(false);
    }

    /**
     * Adds a <code>PropertyGenerator</code> to the node. The property values emitted by this property generator
     * override any previous definitions.
     *
     * @param pg A <code>PropertyGenerator</code> to be added to the associated node's property environment.
     * @throws IllegalArgumentException if <code>pg</code> is <code>null</code>.
     */
    public void addPropertyGenerator(PropertyGenerator pg) {
        if (pg == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        localPropEnv.add(pg);
        if (propertySource != null) {
            propertySource.addPropertyGenerator(pg);
        }
    }

    /**
     * Forces a property to be copied from the specified source node. By default, a property is copied from the first
     * source node that that emits it. The result of specifying an invalid source is undefined.
     *
     * @param propertyName the name of the property to be copied.
     * @param sourceIndex the index of the source to copy the property from.
     * @throws IllegalArgumentException if propertyName is null.
     */
    public void copyPropertyFromSource(String propertyName, int sourceIndex) {
        if (propertyName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        localPropEnv.add(new CopyDirective(propertyName, sourceIndex));
        if (propertySource != null) {
            propertySource.copyPropertyFromSource(propertyName, sourceIndex);
        }
    }

    /**
     * Removes a named property from the property environment of the associated node. Unless the property is stored
     * locally either due to having been set explicitly or to having been cached for property synchronization purposes,
     * subsequent calls to <code>getProperty(name)</code> will return <code>java.awt.Image.UndefinedProperty</code>, and
     * <code>name</code> will not appear on the list of properties emitted by <code>getPropertyNames()</code>.
     *
     * @param name A <code>String</code> naming the property to be suppressed.
     * @throws IllegalArgumentException if <code>name</code> is <code>null</code>.
     */
    public void suppressProperty(String name) {
        if (name == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        localPropEnv.add(name);
        if (propertySource != null) {
            propertySource.suppressProperty(name);
        }
    }

    /**
     * Constructs and returns a <code>PropertySource</code> suitable for use by the specified <code>OperationNode</code>
     * . If the registry mode identified by <code>getRegistryModeName()</code> supports properties, i.e., the statement
     *
     * <pre>
     * Registry.getMode(getRegistryModeName()).arePropertiesSupported()
     * </pre>
     *
     * evaluates to <code>true</code>, then the <code>PropertySource</code> will include the global property environment
     * as managed by the <code>OperationRegistry</code> for the corresponding operation. Prior and subsequent
     * modifications to the local property environment made via this object will be reflected in the returned <code>
     * PropertySource</code>.
     *
     * @param opNode the <code>OperationNode</code> requesting its <code>PropertySource</code>.
     * @param defaultPS a <code>PropertySource</code> to be used to derive property values if and only if they would
     *     otherwise be derived by inheritance from a source rather than from a a <code>PropertyGenerator</code> or a
     *     copy-from-source directive.
     * @throws IllegalArgumentException if opNode is null.
     * @return A <code>PropertySource</code> including the local and, if applicable, the global property environment for
     *     the operation.
     * @see RegistryMode
     * @see OperationRegistry#getPropertySource(OperationNode op)
     */
    public PropertySource getPropertySource(OperationNode opNode, PropertySource defaultPS) {

        if (opNode == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }

        if (propertySource == null) {
            synchronized (this) {
                RegistryMode regMode = RegistryMode.getMode(registryModeName);
                if (regMode != null && regMode.arePropertiesSupported()) {
                    // Get the global property environment.
                    propertySource = (PropertyEnvironment) registry.getPropertySource(opNode);
                } else {
                    // This mode does not support properties so we create
                    // a default environment to permit property inheritance
                    // from the sources. The PropertyGenerators,
                    // copy-from-source directives, and suppressed properties
                    // are null.
                    propertySource =
                            new PropertyEnvironment(pb != null ? pb.getSources() : null, null, null, null, opNode);
                }

                // Update from the local environment.
                updatePropertyEnvironment(propertySource);
            }
        }

        // Add the specified default source.
        propertySource.setDefaultPropertySource(defaultPS);

        return propertySource;
    }

    /**
     * Resets the property environment. The list of local property environment modifications made directly on this
     * object is reset if and only if the parameter is <code>true</code>.
     *
     * @param resetLocalEnvironment Whether to clear the list of property environment changes made directly on this
     *     object.
     */
    public void resetPropertyEnvironment(boolean resetLocalEnvironment) {
        propertySource = null;
        if (resetLocalEnvironment) {
            localPropEnv.clear();
        }
    }

    // Add items from local environment cache.
    private void updatePropertyEnvironment(PropertyEnvironment pe) {
        if (pe != null) { // "pe" should never null but check anyway.
            synchronized (this) {
                // Add items from the local environment.
                int size = localPropEnv.size();
                for (int i = 0; i < size; i++) {
                    Object element = localPropEnv.get(i);
                    if (element instanceof String) { // suppressed property
                        pe.suppressProperty((String) element);
                    } else if (element instanceof CopyDirective) {
                        CopyDirective cd = (CopyDirective) element;
                        pe.copyPropertyFromSource(cd.getName(), cd.getIndex());
                    } else if (element instanceof PropertyGenerator) {
                        pe.addPropertyGenerator((PropertyGenerator) element);
                    }
                }
            }
        }
    }

    private void fireEvent(String propName, Object oldVal, Object newVal) {
        if (eventManager != null) {
            Object eventSource = eventManager.getPropertyChangeEventSource();
            PropertyChangeEventJAI evt = new PropertyChangeEventJAI(eventSource, propName, oldVal, newVal);
            eventManager.firePropertyChange(evt);
        }
    }

    // Note that at present in RenderedOp and RenderableOp the only
    // non-serializable classes handled are RenderedImage, Raster, and
    // RenderingHints. How should this best be handled? Should an OpNode
    // be forced to implement for example
    //
    //  void writePB(ParameterBlock pb, ObjectOutputStream out)
    //  void ParameterBlock readPB(ObjectInputStream in)
    //
    // perhaps in a SerializableOperationNode?
    // Or does this require a more generic approach using Proxy?

    /** Serializes the <code>OperationNodeSupport</code>. */
    /* TODO check serialization
        private void writeObject(ObjectOutputStream out) throws IOException {
            ParameterBlock pbClone = pb;
            boolean pbCloned = false;

            // Wrap RenderedImage sources in RenderedImageStates.
            for (int index = 0; index < pbClone.getNumSources(); index++) {
                Object source = pbClone.getSource(index);
                if (source != null && !(source instanceof Serializable)) {
                    if (!pbCloned) {
                        pbClone = (ParameterBlock) pb.clone();
                        pbCloned = true;
                    }
                    if (source instanceof RenderedImage) {
                        SerializableState serializableImage = SerializerFactory.getState(source, null);
                        pbClone.setSource(serializableImage, index);
                    } else {
                        throw new RuntimeException(
                                source.getClass().getName() + JaiI18N.getString("OperationNodeSupport0"));
                    }
                }
            }

            // Wrap RenderedImage parameters in RenderedImageState objects;
            // wrap Raster parameters in RasterState objects;
            // check other parameters for serializability.
            for (int index = 0; index < pbClone.getNumParameters(); index++) {
                Object parameter = pbClone.getObjectParameter(index);
                if (parameter != null && !(parameter instanceof Serializable)) {
                    if (!pbCloned) {
                        pbClone = (ParameterBlock) pb.clone();
                        pbCloned = true;
                    }
                    if (parameter instanceof Raster) {
                        pbClone.set(SerializerFactory.getState(parameter, null), index);
                    } else if (parameter instanceof RenderedImage) {
                        RenderedImage ri = (RenderedImage) parameter;
                        RenderingHints hints = new RenderingHints(null);
                        hints.put(JAI.KEY_SERIALIZE_DEEP_COPY, new Boolean(true));
                        pbClone.set(SerializerFactory.getState(ri, hints), index);
                    } else {
                        throw new RuntimeException(
                                parameter.getClass().getName() + JaiI18N.getString("OperationNodeSupport1"));
                    }
                }
            }

            // Serialize the object.
            // Write non-static and non-transient fields.
            out.defaultWriteObject();
            // Write ParameterBlock.
            out.writeObject(pbClone);
            // Write RenderingHints.
            out.writeObject(SerializerFactory.getState(hints, null));
        }
    */

    /** Deserializes the <code>OperationNodeSupport</code>. */
    /* TODO check deserialization
     private synchronized void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {

         // Read non-static and non-transient fields.
         in.defaultReadObject();
         // Read ParameterBlock.
         pb = (ParameterBlock) in.readObject();
         // Read RenderingHints.
         SerializableState ss = (SerializableState) in.readObject();
         hints = (RenderingHints) ss.getObject();

         // Wrap any RenderedImageState sources in PlanarImage objects.
         for (int index = 0; index < pb.getNumSources(); index++) {
             Object source = pb.getSource(index);
             if (source instanceof SerializableState) {
                 ss = (SerializableState) source;
                 PlanarImage pi = PlanarImage.wrapRenderedImage((RenderedImage) ss.getObject());
                 pb.setSource(pi, index);
             }
         }

         // Extract Raster and PlanarImage parameters from RasterState and
         // RenderedImageState wrappers, respectively.
         for (int index = 0; index < pb.getNumParameters(); index++) {
             Object parameter = pb.getObjectParameter(index);
             if (parameter instanceof SerializableState) {
                 Object object = ((SerializableState) parameter).getObject();
                 if (object instanceof Raster) pb.set(object, index);
                 else if (object instanceof RenderedImage)
                     pb.set(PlanarImage.wrapRenderedImage((RenderedImage) object), index);
                 else pb.set(object, index);
             }
         }

         registry = JAI.getDefaultInstance().getOperationRegistry();
     }
    */
}
