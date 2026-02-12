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
 *
 * SPDX-License-Identifier: Apache-2.0
 */
/*
 *  Copyright (c) 2013, Michael Bedward. All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification,
 *  are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  SPDX-License-Identifier: BSD-2-Clause
 */
package org.eclipse.imagen.media.jiffle.parser;

import static org.eclipse.imagen.media.jiffle.parser.JiffleParser.AndExprContext;
import static org.eclipse.imagen.media.jiffle.parser.JiffleParser.AtomContext;
import static org.eclipse.imagen.media.jiffle.parser.JiffleParser.AtomExprContext;
import static org.eclipse.imagen.media.jiffle.parser.JiffleParser.BandSpecifierContext;
import static org.eclipse.imagen.media.jiffle.parser.JiffleParser.CompareExprContext;
import static org.eclipse.imagen.media.jiffle.parser.JiffleParser.ConCallContext;
import static org.eclipse.imagen.media.jiffle.parser.JiffleParser.EqExprContext;
import static org.eclipse.imagen.media.jiffle.parser.JiffleParser.ExpressionContext;
import static org.eclipse.imagen.media.jiffle.parser.JiffleParser.ExpressionListContext;
import static org.eclipse.imagen.media.jiffle.parser.JiffleParser.FunctionCallContext;
import static org.eclipse.imagen.media.jiffle.parser.JiffleParser.ImageCallContext;
import static org.eclipse.imagen.media.jiffle.parser.JiffleParser.ImagePosContext;
import static org.eclipse.imagen.media.jiffle.parser.JiffleParser.LiteralContext;
import static org.eclipse.imagen.media.jiffle.parser.JiffleParser.NotExprContext;
import static org.eclipse.imagen.media.jiffle.parser.JiffleParser.OrExprContext;
import static org.eclipse.imagen.media.jiffle.parser.JiffleParser.ParenExpressionContext;
import static org.eclipse.imagen.media.jiffle.parser.JiffleParser.PixelPosContext;
import static org.eclipse.imagen.media.jiffle.parser.JiffleParser.PixelSpecifierContext;
import static org.eclipse.imagen.media.jiffle.parser.JiffleParser.PlusMinusExprContext;
import static org.eclipse.imagen.media.jiffle.parser.JiffleParser.PostExprContext;
import static org.eclipse.imagen.media.jiffle.parser.JiffleParser.PowExprContext;
import static org.eclipse.imagen.media.jiffle.parser.JiffleParser.PreExprContext;
import static org.eclipse.imagen.media.jiffle.parser.JiffleParser.TernaryExprContext;
import static org.eclipse.imagen.media.jiffle.parser.JiffleParser.TimesDivModExprContext;
import static org.eclipse.imagen.media.jiffle.parser.JiffleParser.VarIDContext;
import static org.eclipse.imagen.media.jiffle.parser.JiffleParser.XorExprContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.eclipse.imagen.media.jiffle.parser.node.Band;
import org.eclipse.imagen.media.jiffle.parser.node.BinaryExpression;
import org.eclipse.imagen.media.jiffle.parser.node.ConFunction;
import org.eclipse.imagen.media.jiffle.parser.node.ConstantLiteral;
import org.eclipse.imagen.media.jiffle.parser.node.DoubleLiteral;
import org.eclipse.imagen.media.jiffle.parser.node.Expression;
import org.eclipse.imagen.media.jiffle.parser.node.FunctionCall;
import org.eclipse.imagen.media.jiffle.parser.node.GetSourceValue;
import org.eclipse.imagen.media.jiffle.parser.node.ImagePos;
import org.eclipse.imagen.media.jiffle.parser.node.ImageProperty;
import org.eclipse.imagen.media.jiffle.parser.node.IntLiteral;
import org.eclipse.imagen.media.jiffle.parser.node.ListLiteral;
import org.eclipse.imagen.media.jiffle.parser.node.Node;
import org.eclipse.imagen.media.jiffle.parser.node.NodeException;
import org.eclipse.imagen.media.jiffle.parser.node.ParenExpression;
import org.eclipse.imagen.media.jiffle.parser.node.Pixel;
import org.eclipse.imagen.media.jiffle.parser.node.PostfixUnaryExpression;
import org.eclipse.imagen.media.jiffle.parser.node.PrefixUnaryExpression;
import org.eclipse.imagen.media.jiffle.parser.node.Variable;

/** Base class to generate a Java model representing the script, limited to expressions */
abstract class AbstractModelWorker extends PropertyWorker<Node> {

    protected RepeatedReadOptimizer readsOptimizer = new RepeatedReadOptimizer();

    /** Locates the source positions */
    public AbstractModelWorker(ParseTree tree) {
        super(tree);
    }

    @Override
    public void exitAtomExpr(AtomExprContext ctx) {
        set(ctx, get(ctx.atom()));
    }

    @Override
    public void exitPowExpr(PowExprContext ctx) {
        Expression a = getAsType(ctx.expression(0), Expression.class);
        Expression b = getAsType(ctx.expression(1), Expression.class);

        try {
            set(ctx, new BinaryExpression(JiffleParser.POW, a, b));
        } catch (NodeException ex) {
            throw new InternalCompilerException();
        }
    }

    @Override
    public void exitPostExpr(PostExprContext ctx) {
        String op = ctx.getChild(1).getText();
        Expression e = getAsType(ctx.expression(), Expression.class);
        set(ctx, new PostfixUnaryExpression(e, op));
    }

    @Override
    public void exitPreExpr(PreExprContext ctx) {
        String op = ctx.getChild(0).getText();
        Expression e = getAsType(ctx.expression(), Expression.class);
        set(ctx, new PrefixUnaryExpression(op, e));
    }

    @Override
    public void exitNotExpr(NotExprContext ctx) {
        setFunctionCall(ctx, "NOT", Collections.singletonList(ctx.expression()));
    }

    @Override
    public void exitTimesDivModExpr(TimesDivModExprContext ctx) {
        Expression left = getAsType(ctx.expression(0), Expression.class);
        Expression right = getAsType(ctx.expression(1), Expression.class);

        try {
            set(ctx, new BinaryExpression(ctx.op.getType(), left, right));
        } catch (NodeException ex) {
            throw new InternalCompilerException();
        }
    }

    @Override
    public void exitPlusMinusExpr(PlusMinusExprContext ctx) {
        Expression left = getAsType(ctx.expression(0), Expression.class);
        Expression right = getAsType(ctx.expression(1), Expression.class);

        try {
            set(ctx, new BinaryExpression(ctx.op.getType(), left, right));
        } catch (NodeException ex) {
            throw new InternalCompilerException();
        }
    }

    @Override
    public void exitCompareExpr(CompareExprContext ctx) {
        String op;

        switch (ctx.op.getType()) {
            case JiffleParser.LT:
                op = "LT";
                break;
            case JiffleParser.LE:
                op = "LE";
                break;
            case JiffleParser.GE:
                op = "GE";
                break;
            case JiffleParser.GT:
                op = "GT";
                break;
            default:
                throw new IllegalStateException("Unknown op: " + ctx.op.getText());
        }

        setFunctionCall(ctx, op, ctx.expression());
    }

    @Override
    public void exitEqExpr(EqExprContext ctx) {
        String op;

        switch (ctx.op.getType()) {
            case JiffleParser.EQ:
                op = "EQ";
                break;
            case JiffleParser.NE:
                op = "NE";
                break;
            default:
                throw new IllegalStateException("Unknown op: " + ctx.op.getText());
        }

        setFunctionCall(ctx, op, ctx.expression());
    }

    @Override
    public void exitAndExpr(AndExprContext ctx) {
        setFunctionCall(ctx, "AND", ctx.expression());
    }

    @Override
    public void exitOrExpr(OrExprContext ctx) {
        setFunctionCall(ctx, "OR", ctx.expression());
    }

    @Override
    public void exitXorExpr(XorExprContext ctx) {
        setFunctionCall(ctx, "XOR", ctx.expression());
    }

    private void setFunctionCall(ParseTree ctx, String fnName, List<ExpressionContext> ecs) {
        try {
            set(ctx, FunctionCall.of(fnName, asExpressions(ecs)));
        } catch (NodeException ex) {
            throw new InternalCompilerException();
        }
    }

    @Override
    public void exitTernaryExpr(TernaryExprContext ctx) {
        Expression[] args = {
            getAsType(ctx.expression(0), Expression.class),
            getAsType(ctx.expression(1), Expression.class),
            getAsType(ctx.expression(2), Expression.class)
        };

        try {
            set(ctx, new ConFunction(args));
        } catch (NodeException ex) {
            messages.error(ctx.getStart(), ex.getError());
        }
    }

    @Override
    public void exitAtom(AtomContext ctx) {
        set(ctx, get(ctx.getChild(0)));
    }

    @Override
    public void exitFunctionCall(FunctionCallContext ctx) {
        ExpressionListContext expressionList = ctx.argumentList().expressionList();
        if (expressionList == null) {
            setFunctionCall(ctx, ctx.start.getText(), new ArrayList<ExpressionContext>());
        } else {
            setFunctionCall(ctx, ctx.start.getText(), expressionList.expression());
        }
    }

    @Override
    public void exitConCall(ConCallContext ctx) {
        List<ExpressionContext> es = ctx.argumentList().expressionList().expression();
        Expression[] args = new Expression[es.size()];

        for (int i = 0; i < args.length; i++) {
            args[i] = getAsType(es.get(i), Expression.class);
        }

        try {
            set(ctx, new ConFunction(args));
        } catch (NodeException ex) {
            messages.error(ctx.getStart(), ex.getError());
        }
    }

    @Override
    public void exitParenExpression(ParenExpressionContext ctx) {
        Expression e = getAsType(ctx.expression(), Expression.class);
        set(ctx, new ParenExpression(e));
    }

    @Override
    public void exitImageCall(ImageCallContext ctx) {
        String name = ctx.ID().getText();
        ImagePos pos = getAsType(ctx.imagePos(), ImagePos.class);
        GetSourceValue node = new GetSourceValue(name, pos);
        readsOptimizer.add(node);
        set(ctx, node);
    }

    @Override
    public void exitImageProperty(JiffleParser.ImagePropertyContext ctx) {
        String varName = ctx.ID(0).getText();
        String property = ctx.ID(1).getText();
        ImageProperty node = new ImageProperty(varName, property);
        set(ctx, node);
    }

    @Override
    public void exitImagePos(ImagePosContext ctx) {
        BandSpecifierContext bandCtx = ctx.bandSpecifier();
        Band band = bandCtx == null ? Band.DEFAULT : getAsType(bandCtx, Band.class);

        PixelSpecifierContext pixelCtx = ctx.pixelSpecifier();
        Pixel pixel = pixelCtx == null ? Pixel.DEFAULT : getAsType(pixelCtx, Pixel.class);

        set(ctx, new ImagePos(band, pixel));
    }

    @Override
    public void exitBandSpecifier(BandSpecifierContext ctx) {
        Expression e = getAsType(ctx.expression(), Expression.class);
        set(ctx, new Band(e));
    }

    @Override
    public void exitPixelSpecifier(PixelSpecifierContext ctx) {

        final Expression x, y;
        Expression e;

        try {
            PixelPosContext xpos = ctx.pixelPos(0);
            e = getAsType(xpos.expression(), Expression.class);
            if (xpos.ABS_POS_PREFIX() == null) {
                // relative position
                x = new BinaryExpression(JiffleParser.PLUS, FunctionCall.of("x"), e);
            } else {
                // absolute position
                x = e;
            }

            PixelPosContext ypos = ctx.pixelPos(1);
            e = getAsType(ypos.expression(), Expression.class);
            if (ypos.ABS_POS_PREFIX() == null) {
                // relative position
                y = new BinaryExpression(JiffleParser.PLUS, FunctionCall.of("y"), e);
            } else {
                // absolute position
                y = e;
            }

            set(ctx, new Pixel(x, y));

        } catch (NodeException ex) {
            // definitely should not happen
            throw new InternalCompilerException(ex.getError().toString());
        }
    }

    @Override
    public void exitVarID(VarIDContext ctx) {
        String name = ctx.ID().getText();
        // variable or constant?
        if (ConstantLookup.isDefined(name)) {
            set(ctx, new DoubleLiteral(Double.toString(ConstantLookup.getValue(name))));
        } else {
            Symbol symbol = getScope(ctx).get(name);

            switch (symbol.getType()) {
                case SOURCE_IMAGE:
                    // Source image with default pixel / band positions
                    GetSourceValue sourceValue = new GetSourceValue(name, ImagePos.DEFAULT);
                    readsOptimizer.add(sourceValue);
                    set(ctx, sourceValue);
                    break;

                case LIST:
                    set(ctx, new Variable(name, JiffleType.LIST));
                    break;

                case LOOP_VAR:
                case SCALAR:
                    set(ctx, new Variable(name, JiffleType.D));
                    break;

                default:
                    // DEST_IMAGE and UNKNOWN
                    // This should have been picked up in earlier stages
                    throw new IllegalArgumentException("Compiler error: invalid type for variable '" + name + "'");
            }
        }
    }

    @Override
    public void exitLiteral(LiteralContext ctx) {
        Token tok = ctx.getStart();
        switch (tok.getType()) {
            case JiffleParser.INT_LITERAL:
                set(ctx, new IntLiteral(tok.getText()));
                break;

            case JiffleParser.FLOAT_LITERAL:
                set(ctx, new DoubleLiteral(tok.getText()));
                break;

            case JiffleParser.TRUE:
                set(ctx, ConstantLiteral.trueValue());
                break;

            case JiffleParser.FALSE:
                set(ctx, ConstantLiteral.falseValue());
                break;

            case JiffleParser.NULL:
                set(ctx, ConstantLiteral.nanValue());
                break;

            default:
                throw new JiffleParserException("Unrecognized literal type: " + tok.getText());
        }
    }

    @Override
    public void exitListLiteral(JiffleParser.ListLiteralContext ctx) {
        List<Expression> expressions = new ArrayList<>();
        if (ctx.expressionList() != null && ctx.expressionList().expression() != null) {
            for (ExpressionContext ec : ctx.expressionList().expression()) {
                Expression expression = getAsType(ec, Expression.class);
                expressions.add(expression);
            }
        }
        set(ctx, new ListLiteral(expressions));
    }

    /*
     * Looks up the inner-most scope for a rule node.
     */
    protected abstract SymbolScope getScope(ParseTree ctx);

    protected <N extends Node> N getAsType(ParseTree ctx, Class<N> clazz) {
        if (get(ctx) == null) {
            // bummer - node property should have been set but wasn't
            String lineColumn = "(unknown)";
            if (ctx instanceof ParserRuleContext) {
                ParserRuleContext prc = (ParserRuleContext) ctx;
                Token start = prc.getStart();
                lineColumn = "(" + start.getLine() + ":" + start.getCharPositionInLine() + ")";
            }
            throw new IllegalStateException("Internal compiler error: no property set for node of type "
                    + ctx.getClass().getSimpleName() + " at " + lineColumn);
        }

        try {
            // have to assign to a local variable to allow
            // for possible class cast exception
            return clazz.cast(get(ctx));

        } catch (ClassCastException ex) {
            // Bummer - internal error
            String msg = String.format(
                    "Internal compiler error: cannot cast %s to %s",
                    get(ctx).getClass().getSimpleName(), clazz.getSimpleName());

            throw new IllegalStateException(msg);
        }
    }

    protected Expression[] asExpressions(List<ExpressionContext> ctxs) {
        if (ctxs == null) {
            return new Expression[0];
        }

        Expression[] exprs = new Expression[ctxs.size()];
        for (int i = 0; i < exprs.length; i++) {
            exprs[i] = getAsType(ctxs.get(i), Expression.class);
        }

        return exprs;
    }
}
