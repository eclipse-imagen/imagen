/* Copyright 2009-2011 Michael Bedward, and others
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
 *  Copyright (c) 2009-2011, Michael Bedward. All rights reserved.
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
 */

package org.eclipse.imagen.media.kernel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import org.eclipse.imagen.KernelJAI;
import org.junit.Test;

/**
 * Unit tests of KernelFactory.
 *
 * @author Michael Bedward
 * @since 1.0
 * @version $Id$
 */
public class KernelFactoryTest {

    private static final float FTOL = 1.0e-4f;

    // raster circle, radius = 3
    private static final int[] CIRCLE3 = {
        0, 0, 0, 1, 0, 0, 0,
        0, 1, 1, 1, 1, 1, 0,
        0, 1, 1, 1, 1, 1, 0,
        1, 1, 1, 1, 1, 1, 1,
        0, 1, 1, 1, 1, 1, 0,
        0, 1, 1, 1, 1, 1, 0,
        0, 0, 0, 1, 0, 0, 0
    };

    private static final int RADIUS = 3;

    private static final int[] ANNULUS = {
        0, 0, 0, 0, 1, 0, 0, 0, 0,
        0, 0, 1, 1, 1, 1, 1, 0, 0,
        0, 1, 1, 1, 0, 1, 1, 1, 0,
        0, 1, 1, 0, 0, 0, 1, 1, 0,
        1, 1, 0, 0, 0, 0, 0, 1, 1,
        0, 1, 1, 0, 0, 0, 1, 1, 0,
        0, 1, 1, 1, 0, 1, 1, 1, 0,
        0, 0, 1, 1, 1, 1, 1, 0, 0,
        0, 0, 0, 0, 1, 0, 0, 0, 0
    };

    private static final int OUTER_RADIUS = 4;
    private static final int INNER_RADIUS = 2;

    private static final int[] RECT = {
        1, 1, 1, 1, 1,
        1, 1, 1, 1, 1,
        1, 1, 1, 1, 1
    };

    private static final int RECTW = 5;
    private static final int RECTH = 3;
    private static final int RECT_KEYX = 1;
    private static final int RECT_KEYY = 1;
    private static final double RECT_DMAX2 = (RECTW - 2) * (RECTW - 2) + (RECTH - 2) * (RECTH - 2);

    private interface Evaluator {
        float eval(int maskValue, double distanceToKeySquared);
    }

    private void assertKernel(KernelJAI kernel, int[] mask, Evaluator e) {
        final float[] data = kernel.getKernelData();
        assertEquals(mask.length, data.length);

        final int w = kernel.getWidth();
        Point p = new Point(kernel.getXOrigin(), kernel.getYOrigin());

        int x = 0, y = -1;
        for (int i = 0; i < data.length; i++) {
            if (i % w == 0) {
                x = 0;
                y++;
            }
            double dist2 = p.distanceSq(x, y);
            assertEquals(e.eval(mask[i], dist2), data[i], FTOL);
            x++;
        }
    }

    @Test
    public void defaultCircle() {
        // System.out.println("   circle");
        KernelJAI kernel = KernelFactory.createCircle(3);

        Evaluator e = new Evaluator() {
            public float eval(int maskValue, double ignored) {
                return maskValue;
            }
        };

        assertKernel(kernel, CIRCLE3, e);
    }

    @Test
    public void binaryCircle() {
        // System.out.println("   binary circle");

        KernelJAI kernel = KernelFactory.createCircle(RADIUS, KernelFactory.ValueType.BINARY);

        Evaluator e = new Evaluator() {
            public float eval(int maskValue, double ignored) {
                return maskValue;
            }
        };

        assertKernel(kernel, CIRCLE3, e);
    }

    @Test
    public void cosineCircle() {
        // System.out.println("   cosine circle");

        KernelJAI kernel = KernelFactory.createCircle(RADIUS, KernelFactory.ValueType.COSINE);

        Evaluator e = new Evaluator() {
            double PI4 = Math.PI / 4;
            double PI2 = Math.PI / 2;

            public float eval(int maskValue, double dist2) {
                if (maskValue == 0) {
                    return 0;
                } else {
                    return (float) (PI4 * Math.cos(PI2 * Math.sqrt(dist2) / RADIUS));
                }
            }
        };

        assertKernel(kernel, CIRCLE3, e);
    }

    @Test
    public void distanceCircle() {
        // System.out.println("   distance circle");

        KernelJAI kernel = KernelFactory.createCircle(RADIUS, KernelFactory.ValueType.DISTANCE);

        Evaluator e = new Evaluator() {
            public float eval(int maskValue, double dist2) {
                if (maskValue == 0) {
                    return 0;
                } else {
                    return (float) Math.sqrt(dist2);
                }
            }
        };

        assertKernel(kernel, CIRCLE3, e);
    }

    @Test
    public void epanechnikovCircle() {
        // System.out.println("   Epanenchnikov circle");

        KernelJAI kernel = KernelFactory.createCircle(RADIUS, KernelFactory.ValueType.EPANECHNIKOV);

        Evaluator e = new Evaluator() {
            double DMAX2 = RADIUS * RADIUS;

            public float eval(int maskValue, double dist2) {
                if (maskValue == 0) {
                    return 0;
                } else {
                    return (float) (3.0 * (1.0 - dist2 / DMAX2) / 4.0);
                }
            }
        };

        assertKernel(kernel, CIRCLE3, e);
    }

    @Test
    public void gaussianCircle() {
        // System.out.println("   Gaussian circle");

        KernelJAI kernel = KernelFactory.createCircle(RADIUS, KernelFactory.ValueType.GAUSSIAN);

        Evaluator e = new Evaluator() {
            double C = 1.0 / Math.sqrt(2 * Math.PI);
            double DMAX2 = RADIUS * RADIUS;

            public float eval(int maskValue, double dist2) {
                if (maskValue == 0) {
                    return 0;
                } else {
                    return (float) (C * Math.exp(-0.5 * dist2 / DMAX2));
                }
            }
        };

        assertKernel(kernel, CIRCLE3, e);
    }

    @Test
    public void inverseDistanceCircle() {
        // System.out.println("   Inverse distance circle");

        KernelJAI kernel = KernelFactory.createCircle(RADIUS, KernelFactory.ValueType.INVERSE_DISTANCE);

        Evaluator e = new Evaluator() {
            public float eval(int maskValue, double dist2) {
                if (maskValue == 0) {
                    return 0;
                } else if (dist2 < 0.5) {
                    return 1.0f;
                } else {
                    return (float) (1.0 / Math.sqrt(dist2));
                }
            }
        };

        assertKernel(kernel, CIRCLE3, e);
    }

    @Test
    public void quarticCircle() {
        // System.out.println("   quartic circle");

        KernelJAI kernel = KernelFactory.createCircle(RADIUS, KernelFactory.ValueType.QUARTIC);

        Evaluator e = new Evaluator() {
            double DMAX2 = RADIUS * RADIUS;

            public float eval(int maskValue, double dist2) {
                if (maskValue == 0) {
                    return 0;
                } else {
                    double term = (1.0 - dist2 / DMAX2);
                    return (float) (15.0 * term * term / 16.0);
                }
            }
        };

        assertKernel(kernel, CIRCLE3, e);
    }

    @Test
    public void triangularCircle() {
        // System.out.println("   triangular circle");

        KernelJAI kernel = KernelFactory.createCircle(RADIUS, KernelFactory.ValueType.TRIANGULAR);

        Evaluator e = new Evaluator() {
            public float eval(int maskValue, double dist2) {
                if (maskValue == 0) {
                    return 0;
                } else {
                    return (float) (1.0 - Math.sqrt(dist2) / RADIUS);
                }
            }
        };

        assertKernel(kernel, CIRCLE3, e);
    }

    @Test
    public void triweightCircle() {
        // System.out.println("   triweight circle");

        KernelJAI kernel = KernelFactory.createCircle(RADIUS, KernelFactory.ValueType.TRIWEIGHT);

        Evaluator e = new Evaluator() {
            double DMAX2 = RADIUS * RADIUS;

            public float eval(int maskValue, double dist2) {
                if (maskValue == 0) {
                    return 0;
                } else {
                    double term = (1.0 - dist2 / DMAX2);
                    return (float) (35.0 * term * term * term / 32.0);
                }
            }
        };

        assertKernel(kernel, CIRCLE3, e);
    }

    @Test
    public void binaryAnnulus() {
        // System.out.println("   binary annulua");

        KernelJAI kernel = KernelFactory.createAnnulus(OUTER_RADIUS, INNER_RADIUS, KernelFactory.ValueType.BINARY);

        Evaluator e = new Evaluator() {
            public float eval(int maskValue, double ignored) {
                return maskValue;
            }
        };

        assertKernel(kernel, ANNULUS, e);
    }

    @Test
    public void cosineAnnulus() {
        // System.out.println("   cosine annulus");

        KernelJAI kernel = KernelFactory.createAnnulus(OUTER_RADIUS, INNER_RADIUS, KernelFactory.ValueType.COSINE);

        Evaluator e = new Evaluator() {
            double PI4 = Math.PI / 4;
            double PI2 = Math.PI / 2;

            public float eval(int maskValue, double dist2) {
                if (maskValue == 0) {
                    return 0;
                } else {
                    return (float) (PI4 * Math.cos(PI2 * Math.sqrt(dist2) / OUTER_RADIUS));
                }
            }
        };

        assertKernel(kernel, ANNULUS, e);
    }

    @Test
    public void distanceAnnulus() {
        // System.out.println("   distance annulus");

        KernelJAI kernel = KernelFactory.createAnnulus(OUTER_RADIUS, INNER_RADIUS, KernelFactory.ValueType.DISTANCE);

        Evaluator e = new Evaluator() {
            public float eval(int maskValue, double dist2) {
                if (maskValue == 0) {
                    return 0;
                } else {
                    return (float) Math.sqrt(dist2);
                }
            }
        };

        assertKernel(kernel, ANNULUS, e);
    }

    @Test
    public void epanechnikovAnnulus() {
        // System.out.println("   Epanenchnikov annulus");

        KernelJAI kernel =
                KernelFactory.createAnnulus(OUTER_RADIUS, INNER_RADIUS, KernelFactory.ValueType.EPANECHNIKOV);

        Evaluator e = new Evaluator() {
            double DMAX2 = OUTER_RADIUS * OUTER_RADIUS;

            public float eval(int maskValue, double dist2) {
                if (maskValue == 0) {
                    return 0;
                } else {
                    return (float) (3.0 * (1.0 - dist2 / DMAX2) / 4.0);
                }
            }
        };

        assertKernel(kernel, ANNULUS, e);
    }

    @Test
    public void gaussianAnnulus() {
        // System.out.println("   Gaussian annulus");

        KernelJAI kernel = KernelFactory.createAnnulus(OUTER_RADIUS, INNER_RADIUS, KernelFactory.ValueType.GAUSSIAN);

        Evaluator e = new Evaluator() {
            double C = 1.0 / Math.sqrt(2 * Math.PI);
            double DMAX2 = OUTER_RADIUS * OUTER_RADIUS;

            public float eval(int maskValue, double dist2) {
                if (maskValue == 0) {
                    return 0;
                } else {
                    return (float) (C * Math.exp(-0.5 * dist2 / DMAX2));
                }
            }
        };

        assertKernel(kernel, ANNULUS, e);
    }

    @Test
    public void inverseDistanceAnnulus() {
        // System.out.println("   Inverse distance annulus");

        KernelJAI kernel =
                KernelFactory.createAnnulus(OUTER_RADIUS, INNER_RADIUS, KernelFactory.ValueType.INVERSE_DISTANCE);

        Evaluator e = new Evaluator() {
            public float eval(int maskValue, double dist2) {
                if (maskValue == 0) {
                    return 0;
                } else {
                    return (float) (1.0 / Math.sqrt(dist2));
                }
            }
        };

        assertKernel(kernel, ANNULUS, e);
    }

    @Test
    public void quarticAnnulus() {
        // System.out.println("   quartic annulus");

        KernelJAI kernel = KernelFactory.createAnnulus(OUTER_RADIUS, INNER_RADIUS, KernelFactory.ValueType.QUARTIC);

        Evaluator e = new Evaluator() {
            double DMAX2 = OUTER_RADIUS * OUTER_RADIUS;

            public float eval(int maskValue, double dist2) {
                if (maskValue == 0) {
                    return 0;
                } else {
                    double term = (1.0 - dist2 / DMAX2);
                    return (float) (15.0 * term * term / 16.0);
                }
            }
        };

        assertKernel(kernel, ANNULUS, e);
    }

    @Test
    public void triangularAnnulus() {
        // System.out.println("   triangular annulus");

        KernelJAI kernel = KernelFactory.createAnnulus(OUTER_RADIUS, INNER_RADIUS, KernelFactory.ValueType.TRIANGULAR);

        Evaluator e = new Evaluator() {
            public float eval(int maskValue, double dist2) {
                if (maskValue == 0) {
                    return 0;
                } else {
                    return (float) (1.0 - Math.sqrt(dist2) / OUTER_RADIUS);
                }
            }
        };

        assertKernel(kernel, ANNULUS, e);
    }

    @Test
    public void triweightAnnulus() {
        // System.out.println("   triweight annulus");

        KernelJAI kernel = KernelFactory.createAnnulus(OUTER_RADIUS, INNER_RADIUS, KernelFactory.ValueType.TRIWEIGHT);

        Evaluator e = new Evaluator() {
            double DMAX2 = OUTER_RADIUS * OUTER_RADIUS;

            public float eval(int maskValue, double dist2) {
                if (maskValue == 0) {
                    return 0;
                } else {
                    double term = (1.0 - dist2 / DMAX2);
                    return (float) (35.0 * term * term * term / 32.0);
                }
            }
        };

        assertKernel(kernel, ANNULUS, e);
    }

    @Test
    public void binaryRectangle() {
        // System.out.println("   binary rectangle");

        KernelJAI kernel =
                KernelFactory.createRectangle(RECTW, RECTH, KernelFactory.ValueType.BINARY, RECT_KEYX, RECT_KEYY);

        Evaluator e = new Evaluator() {
            public float eval(int maskValue, double ignored) {
                return maskValue;
            }
        };

        assertKernel(kernel, RECT, e);
    }

    @Test
    public void cosineRectangle() {
        // System.out.println("   cosine rectangle");

        KernelJAI kernel =
                KernelFactory.createRectangle(RECTW, RECTH, KernelFactory.ValueType.COSINE, RECT_KEYX, RECT_KEYY);

        Evaluator e = new Evaluator() {
            double PI4 = Math.PI / 4;
            double PI2 = Math.PI / 2;
            double DMAX = Math.sqrt(RECT_DMAX2);

            public float eval(int maskValue, double dist2) {
                if (maskValue == 0) {
                    return 0;
                } else {
                    return (float) (PI4 * Math.cos(PI2 * Math.sqrt(dist2) / DMAX));
                }
            }
        };

        assertKernel(kernel, RECT, e);
    }

    @Test
    public void distanceRectangle() {
        // System.out.println("   distance rectangle");

        KernelJAI kernel =
                KernelFactory.createRectangle(RECTW, RECTH, KernelFactory.ValueType.DISTANCE, RECT_KEYX, RECT_KEYY);

        Evaluator e = new Evaluator() {
            public float eval(int maskValue, double dist2) {
                if (maskValue == 0) {
                    return 0;
                } else {
                    return (float) Math.sqrt(dist2);
                }
            }
        };

        assertKernel(kernel, RECT, e);
    }

    @Test
    public void epanechnikovRectangle() {
        // System.out.println("   Epanenchnikov rectangle");

        KernelJAI kernel =
                KernelFactory.createRectangle(RECTW, RECTH, KernelFactory.ValueType.EPANECHNIKOV, RECT_KEYX, RECT_KEYY);

        Evaluator e = new Evaluator() {
            public float eval(int maskValue, double dist2) {
                if (maskValue == 0) {
                    return 0;
                } else {
                    return (float) (3.0 * (1.0 - dist2 / RECT_DMAX2) / 4.0);
                }
            }
        };

        assertKernel(kernel, RECT, e);
    }

    @Test
    public void gaussianRectangle() {
        // System.out.println("   Gaussian rectangle");

        KernelJAI kernel =
                KernelFactory.createRectangle(RECTW, RECTH, KernelFactory.ValueType.GAUSSIAN, RECT_KEYX, RECT_KEYY);

        Evaluator e = new Evaluator() {
            double C = 1.0 / Math.sqrt(2 * Math.PI);

            public float eval(int maskValue, double dist2) {
                if (maskValue == 0) {
                    return 0;
                } else {
                    return (float) (C * Math.exp(-0.5 * dist2 / RECT_DMAX2));
                }
            }
        };

        assertKernel(kernel, RECT, e);
    }

    @Test
    public void inverseDistanceRectangle() {
        // System.out.println("   Inverse distance rectangle");

        KernelJAI kernel = KernelFactory.createRectangle(
                RECTW, RECTH, KernelFactory.ValueType.INVERSE_DISTANCE, RECT_KEYX, RECT_KEYY);

        Evaluator e = new Evaluator() {
            public float eval(int maskValue, double dist2) {
                if (maskValue == 0) {
                    return 0;
                } else if (dist2 < 0.5) {
                    return 1.0f;
                } else {
                    return (float) (1.0 / Math.sqrt(dist2));
                }
            }
        };

        assertKernel(kernel, RECT, e);
    }

    @Test
    public void quarticRectangle() {
        // System.out.println("   quartic rectangle");

        KernelJAI kernel =
                KernelFactory.createRectangle(RECTW, RECTH, KernelFactory.ValueType.QUARTIC, RECT_KEYX, RECT_KEYY);

        Evaluator e = new Evaluator() {
            public float eval(int maskValue, double dist2) {
                if (maskValue == 0) {
                    return 0;
                } else {
                    double term = (1.0 - dist2 / RECT_DMAX2);
                    return (float) (15.0 * term * term / 16.0);
                }
            }
        };

        assertKernel(kernel, RECT, e);
    }

    @Test
    public void triangularRectangle() {
        // System.out.println("   triangular rectangle");

        KernelJAI kernel =
                KernelFactory.createRectangle(RECTW, RECTH, KernelFactory.ValueType.TRIANGULAR, RECT_KEYX, RECT_KEYY);

        Evaluator e = new Evaluator() {
            double DMAX = Math.sqrt(RECT_DMAX2);

            public float eval(int maskValue, double dist2) {
                if (maskValue == 0) {
                    return 0;
                } else {
                    return (float) (1.0 - Math.sqrt(dist2) / DMAX);
                }
            }
        };

        assertKernel(kernel, RECT, e);
    }

    @Test
    public void triweightRectangle() {
        // System.out.println("   triweight rectangle");

        KernelJAI kernel =
                KernelFactory.createRectangle(RECTW, RECTH, KernelFactory.ValueType.TRIWEIGHT, RECT_KEYX, RECT_KEYY);

        Evaluator e = new Evaluator() {
            public float eval(int maskValue, double dist2) {
                if (maskValue == 0) {
                    return 0;
                } else {
                    double term = (1.0 - dist2 / RECT_DMAX2);
                    return (float) (35.0 * term * term * term / 32.0);
                }
            }
        };

        assertKernel(kernel, RECT, e);
    }

    @Test
    public void testCreateFromShape() {
        // System.out.println("   create from shape");

        final int width = 2 * RADIUS;
        Shape shape = new Ellipse2D.Float(100, 200, width, width);
        KernelJAI shpKernel =
                KernelFactory.createFromShape(shape, null, KernelFactory.ValueType.BINARY, RADIUS, RADIUS, 1.0f);
        KernelJAI circleKernel = KernelFactory.createCircle(RADIUS);

        float[] shpData = shpKernel.getKernelData();
        float[] circleData = circleKernel.getKernelData();

        assertTrue(shpData.length == circleData.length);

        for (int i = 0; i < shpData.length; i++) {
            assertEquals(shpData[i], circleData[i], FTOL);
        }
    }
}
