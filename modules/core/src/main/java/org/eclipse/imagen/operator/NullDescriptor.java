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

package org.eclipse.imagen.operator;

import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderableImage;
import java.util.Vector;
import org.eclipse.imagen.JAI;
import org.eclipse.imagen.OperationDescriptorImpl;
import org.eclipse.imagen.OperationNode;
import org.eclipse.imagen.ParameterBlockJAI;
import org.eclipse.imagen.RenderableOp;
import org.eclipse.imagen.RenderedOp;
import org.eclipse.imagen.registry.RenderableRegistryMode;
import org.eclipse.imagen.registry.RenderedRegistryMode;

/**
 * An <code>OperationDescriptor</code> describing the "Null" operation.
 *
 * <p>The "Null" operation performs no processing. It merely propagates its first source along the operation chain
 * unmodified. There may be an arbitrary number of sources but only the first one is passed along so it must have the
 * appropriate class type for the operation mode.
 *
 * <p>This operation may be useful as a placeholder in operation chains and in creating nodes to which <code>
 * PropertyGenerator</code>s may be attached. This would enable non-image data nodes to be present in chains without
 * requiring that specific <code>OperationDescriptor</code>s be implemented for these operations. The <code>
 * PropertyGenerator</code>s required would in this case be added locally to the nodes using the <code>
 * addPropertyGenerator()</code> method of the node.
 *
 * <p>
 *
 * <table border=1>
 * <caption>Resource List</caption>
 * <tr><th>Name</th>        <th>Value</th></tr>
 * <tr><td>GlobalName</td>  <td>Null</td></tr>
 * <tr><td>LocalName</td>   <td>Null</td></tr>
 * <tr><td>Vendor</td>      <td>org.eclipse.imagen.media</td></tr>
 * <tr><td>Description</td> <td>An operation which does no processing.</td></tr>
 * <tr><td>DocURL</td>      <td>http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/NullDescriptor.html</td></tr>
 * <tr><td>Version</td>     <td>1.0</td></tr>
 * </table>
 *
 * <p>No parameters are needed for this operation.
 *
 * @see org.eclipse.imagen.OperationDescriptor
 * @since JAI 1.1
 */
public class NullDescriptor extends OperationDescriptorImpl {

    /**
     * The resource strings that provide the general documentation and specify the parameter list for this operation.
     */
    private static final String[][] resources = {
        {"GlobalName", "Null"},
        {"LocalName", "Null"},
        {"Vendor", "org.eclipse.imagen.media"},
        {"Description", JaiI18N.getString("NullDescriptor0")},
        {
            "DocURL",
            "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/NullDescriptor.html"
        },
        {"Version", JaiI18N.getString("DescriptorVersion")},
    };

    private static final String[] supportedModes = {"rendered", "renderable"};

    /** Constructor. */
    public NullDescriptor() {
        super(resources, supportedModes, 1, null, null, null, null);
    }

    /**
     * If the PB has more than one source, replace it with a new PB which has only one source equal to the first source
     * of the input. We want to support an arbitrary number of sources but only care that there is at least one of the
     * appropriate class.
     */
    private static ParameterBlock foolSourceValidation(ParameterBlock args) {
        if (args.getNumSources() > 1) {
            Vector singleSource = new Vector();
            singleSource.add(args.getSource(0));
            args = new ParameterBlock(singleSource, args.getParameters());
        }
        return args;
    }

    /**
     * Returns <code>true</code> if there is at least one source and the first source is a <code>RenderedImage</code> or
     * <code>RenderableImage</code>.
     *
     * @throws IllegalArgumentException if <code>args</code> is <code>null</code>.
     * @throws IllegalArgumentException if <code>msg</code> is <code>null</code> and the validation fails.
     */
    protected boolean validateSources(String modeName, ParameterBlock args, StringBuffer msg) {

        if (args == null || msg == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }

        return super.validateSources(modeName, foolSourceValidation(args), msg);
    }
    /**
     * Calculates the region over which two distinct renderings of the "Null" operation may be expected to differ.
     *
     * <p>The operation returns an empty <code>Shape</code> if the first source in each of the two <code>ParameterBlock
     * </code>s are equal according to the <code>equals()</code> method of the old source or <code>null</code> for all
     * other cases.
     *
     * @param modeName The name of the mode.
     * @param oldParamBlock The previous sources and parameters.
     * @param oldHints The previous hints.
     * @param newParamBlock The current sources and parameters.
     * @param newHints The current hints.
     * @param node The affected node in the processing chain (ignored).
     * @return The region over which the data of two renderings of this operation may be expected to be invalid or
     *     <code>null</code> if there is no common region of validity. A non-<code>null</code> empty region indicates
     *     that the operation would produce identical data over the bounds of the old rendering although perhaps not
     *     over the area occupied by the <i>tiles</i> of the old rendering.
     * @throws IllegalArgumentException if <code>modeName</code> is <code>null</code> or if either <code>oldParamBlock
     *     </code> or <code>newParamBlock</code> is <code>null</code>.
     * @throws IllegalArgumentException if <code>oldParamBlock</code> or <code>newParamBlock</code> does not contain at
     *     least one source.
     */
    public Object getInvalidRegion(
            String modeName,
            ParameterBlock oldParamBlock,
            RenderingHints oldHints,
            ParameterBlock newParamBlock,
            RenderingHints newHints,
            OperationNode node) {
        if (modeName == null || oldParamBlock == null || newParamBlock == null) {
            throw new IllegalArgumentException(JaiI18N.getString("NullDescriptor1"));
        }

        if (oldParamBlock.getNumSources() < 1 || newParamBlock.getNumSources() < 1) {

            throw new IllegalArgumentException(JaiI18N.getString("NullDescriptor2"));
        }

        return oldParamBlock.getSource(0).equals(newParamBlock.getSource(0)) ? new Rectangle() : null;
    }

    /**
     * An operation which does no processing.
     *
     * <p>Creates a <code>ParameterBlockJAI</code> from all supplied arguments except <code>hints</code> and invokes
     * {@link JAI#create(String,ParameterBlock,RenderingHints)}.
     *
     * @see JAI
     * @see ParameterBlockJAI
     * @see RenderedOp
     * @param source0 <code>RenderedImage</code> source 0.
     * @param hints The <code>RenderingHints</code> to use. May be <code>null</code>.
     * @return The <code>RenderedOp</code> destination.
     * @throws IllegalArgumentException if <code>source0</code> is <code>null</code>.
     */
    public static RenderedOp create(RenderedImage source0, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("Null", RenderedRegistryMode.MODE_NAME);

        pb.setSource("source0", source0);

        return JAI.create("Null", pb, hints);
    }

    /**
     * An operation which does no processing.
     *
     * <p>Creates a <code>ParameterBlockJAI</code> from all supplied arguments except <code>hints</code> and invokes
     * {@link JAI#createRenderable(String,ParameterBlock,RenderingHints)}.
     *
     * @see JAI
     * @see ParameterBlockJAI
     * @see RenderableOp
     * @param source0 <code>RenderableImage</code> source 0.
     * @param hints The <code>RenderingHints</code> to use. May be <code>null</code>.
     * @return The <code>RenderableOp</code> destination.
     * @throws IllegalArgumentException if <code>source0</code> is <code>null</code>.
     */
    public static RenderableOp createRenderable(RenderableImage source0, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("Null", RenderableRegistryMode.MODE_NAME);

        pb.setSource("source0", source0);

        return JAI.createRenderable("Null", pb, hints);
    }
}
