/*
 * Copyright (c) 2026 Eclipse ImageN contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution and is available at
 * http://www.opensource.org/licenses/apache2.0.php.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.eclipse.imagen;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.renderable.ParameterBlock;
import java.util.concurrent.TimeUnit;
import org.junit.Ignore;
import org.junit.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * JMH benchmark comparing a positional {@link ParameterBlock} with a named {@link ParameterBlockImageN}, on their own
 * and passed to {@link ImageN#create}, which builds the {@link RenderedOp} without computing any tile.
 *
 * <p>Run it with {@code mvn test -pl modules/core -Dtest=ParameterBlockImageNBenchmark#run} after commenting out the
 * {@link Ignore}. It runs single threaded and then with one thread per core, as rendering threads would share the
 * default {@link OperationRegistry}.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(1)
@State(Scope.Benchmark)
public class ParameterBlockImageNBenchmark {

    private static final BufferedImage IMAGE = TestSupport.createImage(DataBuffer.TYPE_BYTE, 4, 4);

    @Benchmark
    public ParameterBlock parameterBlock() {
        return new ParameterBlock().addSource(IMAGE).add(0.5).add(0.5);
    }

    @Benchmark
    public ParameterBlock parameterBlockImageN() {
        return new ParameterBlockImageN("SubsampleAverage")
                .addSource(IMAGE)
                .setParameter("scaleX", 0.5)
                .setParameter("scaleY", 0.5);
    }

    @Benchmark
    public RenderedOp createParameterBlock() {
        return ImageN.create("SubsampleAverage", parameterBlock());
    }

    @Benchmark
    public RenderedOp createParameterBlockImageN() {
        return ImageN.create("SubsampleAverage", parameterBlockImageN());
    }

    @Test
    @Ignore("manual benchmark, prints timings rather than asserting")
    public void run() throws RunnerException {
        int[] threads = {1, Runtime.getRuntime().availableProcessors()};
        for (int t : threads) {
            new Runner(new OptionsBuilder()
                            .include(getClass().getName())
                            .threads(t)
                            .build())
                    .run();
        }
    }
}
