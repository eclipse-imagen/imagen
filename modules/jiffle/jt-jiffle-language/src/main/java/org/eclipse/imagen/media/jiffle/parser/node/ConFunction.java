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

package org.eclipse.imagen.media.jiffle.parser.node;

import java.util.Arrays;
import org.eclipse.imagen.media.jiffle.parser.DirectSources;
import org.eclipse.imagen.media.jiffle.parser.Errors;
import org.eclipse.imagen.media.jiffle.parser.JiffleType;

/**
 * Separate node type for con functions which are implemented as directly injected source fragments in the runtime class
 * rather than by the function lookup mechanism.
 *
 * @author michael
 */
public class ConFunction extends Expression {
    private final Expression[] args;

    public ConFunction(Expression... args) throws NodeException {
        super(args[0].getType());

        // first arg (condition) must be scalar
        if (args[0].getType() != JiffleType.D) {
            throw new NodeException(Errors.CON_CONDITION_MUST_BE_SCALAR);
        }

        if (args.length > 2) {
            for (int i = 2; i < args.length; i++) {
                if (args[1].getType() != args[i].getType()) {
                    throw new NodeException(Errors.CON_RESULTS_MUST_BE_SAME_TYPE);
                }
            }
        }

        this.args = args;
    }

    @Override
    public String toString() {
        String[] argStrs = new String[args.length];

        int i = 0;
        for (Expression arg : args) {
            argStrs[i++] = arg.toString();
        }

        return DirectSources.conCall(argStrs);
    }

    public void write(SourceWriter w) {
        String[] argStrs = new String[args.length];

        int i = 0;
        for (Expression arg : args) {
            argStrs[i++] = w.writeToString(arg);
        }

        w.append(DirectSources.conCall(argStrs));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConFunction that = (ConFunction) o;
        return Arrays.equals(args, that.args);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(args);
    }

    public Expression[] getArgs() {
        return args;
    }
}
