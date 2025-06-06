/* JAI-Ext - OpenSource Java Advanced Image Extensions Library
 *    http://www.geo-solutions.it/
 *    Copyright 2018 GeoSolutions
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
 */

/*
 * Copyright (c) 2018, Michael Bedward. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice, this
 *   list of conditions and the following disclaimer in the documentation and/or
 *   other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.eclipse.imagen.media.jiffle.parser;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;

/** @author michael */
public abstract class PropertyWorker<T> extends BaseWorker {
    final Logger LOGGER = Logger.getLogger(getClass().getName());

    protected final TreeNodeProperties<T> properties;

    public PropertyWorker(ParseTree tree) {
        super(tree);
        this.properties = new TreeNodeProperties<T>();
    }

    public TreeNodeProperties<T> getProperties() {
        return new TreeNodeProperties<T>(properties);
    }

    protected T get(ParseTree ctx) {
        return properties.get(ctx);
    }

    protected T getOrElse(ParseTree ctx, T fallback) {
        if (ctx == null) {
            return fallback;
        }
        T prop = properties.get(ctx);
        return prop == null ? fallback : prop;
    }

    protected void set(ParseTree ctx, T node) {
        if (ctx instanceof ParserRuleContext && LOGGER.isLoggable(Level.FINE)) {
            ParserRuleContext prc = (ParserRuleContext) ctx;
            Token start = prc.getStart();
            String lineColumn = "(" + start.getLine() + ":" + start.getCharPositionInLine() + ")";
            LOGGER.fine("Token "
                    + start.getText()
                    + ", type "
                    + ctx.getClass().getSimpleName()
                    + " at "
                    + lineColumn
                    + " set to "
                    + node);
        }

        properties.put(ctx, node);
    }
}
