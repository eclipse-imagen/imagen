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

package org.eclipse.imagen.media.jiffle.demo;

import java.awt.image.RenderedImage;
import java.awt.image.WritableRenderedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.imagen.media.jiffle.Jiffle;
import org.eclipse.imagen.media.jiffle.runtime.JiffleDirectRuntime;
import org.eclipse.imagen.media.jiffle.runtime.JiffleEvent;
import org.eclipse.imagen.media.jiffle.runtime.JiffleEventListener;
import org.eclipse.imagen.media.jiffle.runtime.JiffleExecutor;
import org.eclipse.imagen.media.jiffle.runtime.JiffleExecutorResult;
import org.eclipse.imagen.media.jiffle.runtime.NullProgressListener;
import org.eclipse.imagen.media.swing.ImageFrame;
import org.eclipse.imagen.media.utilities.ImageUtilities;

/**
 * Demonstrates the use of {@link JiffleExecutor} to run a script. <br>
 * There are two options for running a Jiffle script...
 *
 * <ol type="1">
 *   <li>Directly, by getting a {@link org.eclipse.imagen.media.jiffle.runtime.JiffleRuntime} object from the compiled
 *       {@code Jiffle} object.
 *   <li>Indirectly, by submitting a Jiffle object to a {@link org.eclipse.imagen.media.jiffle.runtime.JiffleExecutor}.
 * </ol>
 *
 * The advantage of the second method for computationally demanding tasks is that execution is carried out in a separate
 * thread. The caller is informed about completion or failure via {@link JiffleEvent}s and can track progress using a
 * {@link org.eclipse.imagen.media.jiffle.runtime.JiffleProgressListener}.
 *
 * @author Michael Bedward
 * @since 1.1
 * @version $Id$
 */
public class JiffleExecutorDemo extends JiffleDemoBase {

    private JiffleExecutor executor;

    /**
     * Run the demonstration. The optional {@code arg} can be either the path to a user-supplied script or one of
     * "chessboard", "interference", "ripple" or "squircle".
     *
     * @param args (optional) the script to run
     * @throws Exception on problems compiling the script
     */
    public static void main(String[] args) throws Exception {
        JiffleExecutorDemo demo = new JiffleExecutorDemo();
        File f = JiffleDemoHelper.getScriptFile(args, ImageChoice.RIPPLES);
        demo.compileAndRun(f);
    }

    /** Constructor. Creates an instance of {@link JiffleExecutor} and sets up event handling. */
    public JiffleExecutorDemo() {
        executor = new JiffleExecutor(1);
        executor.addEventListener(new JiffleEventListener() {

            public void onCompletionEvent(JiffleEvent ev) {
                onCompletion(ev);
            }

            public void onFailureEvent(JiffleEvent ev) {
                onFailure(ev);
            }
        });
    }

    /**
     * Compiles a script read from a file and submits it for execution.
     *
     * @param scriptFile file containing the Jiffle script
     * @throws Exception on problems compiling the script
     */
    public void compileAndRun(File scriptFile) throws Exception {
        Map<String, Jiffle.ImageRole> imageParams = new HashMap<>();
        imageParams.put("result", Jiffle.ImageRole.DEST);

        Jiffle jiffle = new Jiffle(scriptFile, imageParams);
        JiffleDirectRuntime runtime = jiffle.getRuntimeInstance();

        WritableRenderedImage destImage = ImageUtilities.createConstantImage(WIDTH, HEIGHT, 0d);
        runtime.setDestinationImage("result", destImage);

        executor.submit(runtime, new NullProgressListener());
    }

    /**
     * Called when the Jiffle task has been completed successfully.
     *
     * @param ev the event containing the task results
     */
    private void onCompletion(JiffleEvent ev) {
        JiffleExecutorResult result = ev.getResult();
        RenderedImage img = result.getImages().get("result");

        ImageFrame frame = new ImageFrame(img, "Jiffle image demo");
        frame.setVisible(true);
    }

    /**
     * Called if the Jiffle task fails for some reason.
     *
     * @param ev the event
     */
    private void onFailure(JiffleEvent ev) {
        System.err.println("Bummer: script failed to run");
    }
}
