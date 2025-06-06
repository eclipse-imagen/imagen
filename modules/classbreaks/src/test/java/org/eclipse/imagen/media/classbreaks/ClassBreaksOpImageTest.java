/* JAI-Ext - OpenSource Java Advanced Image Extensions Library
*    http://www.geo-solutions.it/
*    Copyright 2018 GeoSolutions
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at

* http://www.apache.org/licenses/LICENSE-2.0

* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2018, Open Source Geospatial Foundation (OSGeo)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.eclipse.imagen.media.classbreaks;

import static org.junit.Assert.*;

import java.awt.*;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import org.eclipse.imagen.JAI;
import org.eclipse.imagen.ParameterBlockJAI;
import org.eclipse.imagen.ParameterListDescriptor;
import org.eclipse.imagen.RegistryElementDescriptor;
import org.eclipse.imagen.RenderedOp;
import org.eclipse.imagen.media.testclasses.TestBase;
import org.eclipse.imagen.media.utilities.ImageUtilities;
import org.eclipse.imagen.operator.ExtremaDescriptor;
import org.junit.Test;

public class ClassBreaksOpImageTest extends TestBase {

    static final double EPS = 1e-3;

    static RenderedImage createImage() {
        return ImageUtilities.createImageFromArray(
                new Number[] {1, 1, 2, 3, 3, 8, 8, 9, 11, 14, 16, 24, 26, 26, 45, 53}, 4, 4);
    }

    @Test
    public void getMissingProperty() {
        RenderedImage image = createImage();

        ParameterBlockJAI pb = new ParameterBlockJAI(new ClassBreaksDescriptor());
        pb.addSource(image);
        pb.setParameter("method", ClassificationMethod.QUANTILE);
        pb.setParameter("numClasses", 5);
        // raw creation like in CoverageClassStats, otherwise the issue gets masked by JAI wrappers
        RenderedImage op = new ClassBreaksRIF().create(pb, null);

        // used to NPE here
        Object roi = op.getProperty("ROI");
        assertEquals(Image.UndefinedProperty, roi);
    }

    @Test
    public void testEqualInterval() throws Exception {
        RenderedImage image = createImage();

        ParameterBlockJAI pb = new ParameterBlockJAI(new ClassBreaksDescriptor());
        pb.addSource(image);
        pb.setParameter("method", ClassificationMethod.EQUAL_INTERVAL);
        pb.setParameter("numClasses", 4);
        RenderedImage op = JAI.create("ClassBreaks", pb, null);
        Classification classification = (Classification) op.getProperty(ClassBreaksDescriptor.CLASSIFICATION_PROPERTY);
        assertNotNull(classification);
        Number[] breaks = classification.getBreaks()[0];

        assertEquals(5, breaks.length);
        assertEquals(1, breaks[0].doubleValue(), EPS);
        assertEquals(14, breaks[1].doubleValue(), EPS);
        assertEquals(27, breaks[2].doubleValue(), EPS);
        assertEquals(40, breaks[3].doubleValue(), EPS);
        assertEquals(53, breaks[4].doubleValue(), EPS);
    }

    @Test
    public void testQuantileBreaks() throws Exception {
        RenderedImage image = createImage();

        ParameterBlockJAI pb = new ParameterBlockJAI(new ClassBreaksDescriptor());
        pb.addSource(image);
        pb.setParameter("method", ClassificationMethod.QUANTILE);
        pb.setParameter("numClasses", 4);
        RenderedImage op = JAI.create("ClassBreaks", pb, null);
        Classification classification = (Classification) op.getProperty(ClassBreaksDescriptor.CLASSIFICATION_PROPERTY);
        assertNotNull(classification);
        Number[] breaks = classification.getBreaks()[0];

        // 4 classes, 5 breaks
        // 1, 1, 2,
        // 3, 3, 8, 8, 9,
        // 11, 14, 16, 24,
        // 26, 26, 45, 53
        assertEquals(5, breaks.length);
        assertEquals(1, breaks[0].doubleValue(), EPS);
        assertEquals(3, breaks[1].doubleValue(), EPS);
        assertEquals(11, breaks[2].doubleValue(), EPS);
        assertEquals(26, breaks[3].doubleValue(), EPS);
        assertEquals(53, breaks[4].doubleValue(), EPS);
    }

    @Test
    public void testQuantileBreaksHistogram() throws Exception {
        RenderedImage image = createImage();

        ParameterBlockJAI pb = new ParameterBlockJAI(new ClassBreaksDescriptor());
        pb.addSource(image);
        pb.setParameter("method", ClassificationMethod.QUANTILE);
        pb.setParameter("numClasses", 4);
        pb.setParameter("extrema", getExtrema(image));
        pb.setParameter("histogram", true);
        pb.setParameter("histogramBins", 100);
        RenderedImage op = JAI.create("ClassBreaks", pb, null);
        Classification classification = (Classification) op.getProperty(ClassBreaksDescriptor.CLASSIFICATION_PROPERTY);
        assertNotNull(classification);
        Number[] breaks = classification.getBreaks()[0];

        // 4 classes, 5 breaks (not the same as the exact count, slightly different approach,
        // but still correct)
        // 1, 1, 2, 3, 3,
        // 8, 8, 9,
        // 11, 14, 16, 24,
        // 26, 26, 45, 53
        assertEquals(5, breaks.length);
        assertEquals(1, breaks[0].doubleValue(), EPS);
        assertEquals(8, breaks[1].doubleValue(), EPS);
        assertEquals(11, breaks[2].doubleValue(), EPS);
        assertEquals(26, breaks[3].doubleValue(), EPS);
        assertEquals(53, breaks[4].doubleValue(), EPS);
    }

    @Test
    public void testNaturalBreaks() throws Exception {
        RenderedImage image = createImage();

        ParameterBlockJAI pb = new ParameterBlockJAI(new ClassBreaksDescriptor());
        pb.addSource(image);
        pb.setParameter("method", ClassificationMethod.NATURAL_BREAKS);
        pb.setParameter("numClasses", 4);
        RenderedImage op = JAI.create("ClassBreaks", pb, null);
        Classification classification = (Classification) op.getProperty(ClassBreaksDescriptor.CLASSIFICATION_PROPERTY);
        assertNotNull(classification);
        Number[] breaks = classification.getBreaks()[0];

        // 4 classes, 5 breaks
        assertEquals(5, breaks.length);
        assertEquals(1, breaks[0].doubleValue(), EPS);
        assertEquals(3, breaks[1].doubleValue(), EPS);
        assertEquals(16, breaks[2].doubleValue(), EPS);
        assertEquals(26, breaks[3].doubleValue(), EPS);
        assertEquals(53, breaks[4].doubleValue(), EPS);
    }

    @Test
    public void testNaturalBreaksHistogram() throws Exception {
        RenderedImage image = createImage();

        ParameterBlockJAI pb = new ParameterBlockJAI(new ClassBreaksDescriptor());
        pb.addSource(image);
        pb.setParameter("method", ClassificationMethod.NATURAL_BREAKS);
        pb.setParameter("numClasses", 4);
        pb.setParameter("extrema", getExtrema(image));
        pb.setParameter("histogram", true);
        pb.setParameter("histogramBins", 100);
        RenderedImage op = JAI.create("ClassBreaks", pb, null);
        Classification classification = (Classification) op.getProperty(ClassBreaksDescriptor.CLASSIFICATION_PROPERTY);
        assertNotNull(classification);
        Number[] breaks = classification.getBreaks()[0];

        // 4 classes, 5 breaks
        assertEquals(5, breaks.length);
        assertEquals(1, breaks[0].doubleValue(), EPS);
        assertEquals(3, breaks[1].doubleValue(), EPS);
        assertEquals(16, breaks[2].doubleValue(), EPS);
        assertEquals(26, breaks[3].doubleValue(), EPS);
        assertEquals(53, breaks[4].doubleValue(), EPS);
    }

    @Test
    public void testNaturalBreaksWithPercentages() throws Exception {
        RenderedImage image = createImage();

        ParameterBlockJAI pb = new ParameterBlockJAI(new ClassBreaksDescriptor());
        pb.addSource(image);
        pb.setParameter("method", ClassificationMethod.NATURAL_BREAKS);
        pb.setParameter("numClasses", 4);
        pb.setParameter("percentages", true);
        RenderedImage op = JAI.create("ClassBreaks", pb, null);
        Classification classification = (Classification) op.getProperty(ClassBreaksDescriptor.CLASSIFICATION_PROPERTY);
        assertNotNull(classification);
        double[] percentages = classification.getPercentages();
        assertEquals(4, percentages.length);
        assertTrue(percentages[0] == 18.75);
        assertTrue(percentages[1] == 43.75);
        assertTrue(percentages[2] == 12.5);
        assertTrue(percentages[3] == 25.0);
    }

    @Test
    public void testNaturalBreaksHistogramWithPercentages() throws Exception {
        RenderedImage image = createImage();

        ParameterBlockJAI pb = new ParameterBlockJAI(new ClassBreaksDescriptor());
        pb.addSource(image);
        pb.setParameter("method", ClassificationMethod.NATURAL_BREAKS);
        pb.setParameter("numClasses", 4);
        pb.setParameter("extrema", getExtrema(image));
        pb.setParameter("histogram", true);
        pb.setParameter("histogramBins", 4);
        pb.setParameter("percentages", true);
        RenderedImage op = JAI.create("ClassBreaks", pb, null);
        Classification classification = (Classification) op.getProperty(ClassBreaksDescriptor.CLASSIFICATION_PROPERTY);
        assertNotNull(classification);
        double[] percentages = classification.getPercentages();
        assertEquals(3, percentages.length);
        assertTrue(Math.floor(percentages[0]) == 56.0);
        assertTrue(percentages[1] == 31.25);
        assertTrue(percentages[2] == 12.5);
    }

    @Test
    public void testNaturalBreaksWithPercentagesMoreClassesThanIntervals() throws Exception {
        RenderedImage image = createImage();

        ParameterBlockJAI pb = new ParameterBlockJAI(new ClassBreaksDescriptor());
        pb.addSource(image);
        pb.setParameter("method", ClassificationMethod.NATURAL_BREAKS);
        pb.setParameter("numClasses", 11);
        pb.setParameter("extrema", getExtrema(image));
        pb.setParameter("histogram", true);
        pb.setParameter("histogramBins", 100);
        pb.setParameter("percentages", true);
        RenderedImage op = JAI.create("ClassBreaks", pb, null);
        Classification classification = (Classification) op.getProperty(ClassBreaksDescriptor.CLASSIFICATION_PROPERTY);
        assertNotNull(classification);
        double[] percentages = classification.getPercentages();
        assertEquals(6, percentages.length);
        assertTrue(percentages[0] == 12.5);
        assertTrue(percentages[1] == 18.75);
        assertTrue(percentages[2] == 18.75);
        assertTrue(percentages[3] == 12.5);
        assertTrue(percentages[4] == 12.5);
        assertTrue(percentages[5] == 25.0);
    }

    @Test
    public void testNaturalBreaksHistogramWithPercentagesMoreClassesThanIntervals() throws Exception {
        RenderedImage image = createImage();

        ParameterBlockJAI pb = new ParameterBlockJAI(new ClassBreaksDescriptor());
        pb.addSource(image);
        pb.setParameter("method", ClassificationMethod.NATURAL_BREAKS);
        pb.setParameter("numClasses", 11);
        pb.setParameter("extrema", getExtrema(image));
        pb.setParameter("histogram", true);
        pb.setParameter("histogramBins", 100);
        pb.setParameter("percentages", true);
        RenderedImage op = JAI.create("ClassBreaks", pb, null);
        Classification classification = (Classification) op.getProperty(ClassBreaksDescriptor.CLASSIFICATION_PROPERTY);
        assertNotNull(classification);
        double[] percentages = classification.getPercentages();
        assertEquals(6, percentages.length);
        assertTrue(percentages[0] == 12.5);
        assertTrue(percentages[1] == 18.75);
        assertTrue(percentages[2] == 18.75);
        assertTrue(percentages[3] == 12.5);
        assertTrue(percentages[4] == 12.5);
        assertTrue(percentages[5] == 25.0);
    }

    @Test
    public void testQuantileBreaksWithPercentages() throws Exception {
        RenderedImage image = createImage();

        ParameterBlockJAI pb = new ParameterBlockJAI(new ClassBreaksDescriptor());
        pb.addSource(image);
        pb.setParameter("method", ClassificationMethod.QUANTILE);
        pb.setParameter("numClasses", 4);
        pb.setParameter("percentages", true);
        RenderedImage op = JAI.create("ClassBreaks", pb, null);
        Classification classification = (Classification) op.getProperty(ClassBreaksDescriptor.CLASSIFICATION_PROPERTY);
        assertNotNull(classification);
        double[] percentages = classification.getPercentages();
        assertEquals(percentages.length, 4);
        assertTrue(percentages[0] == 18.75);
        assertTrue(percentages[1] == 31.25);
        assertTrue(percentages[2] == 25.0);
        assertTrue(percentages[3] == 25.0);
    }

    @Test
    public void testQuantileBreaksPercentagesMoreClassesThaIntervals() {
        RenderedImage image = ImageUtilities.createImageFromArray(
                new Number[] {1, 1, 1, 1, 1, 1, 1, 1, 8, 8, 8, 8, 3, 3, 3, 3}, 4, 4);
        ParameterBlockJAI pb = new ParameterBlockJAI(new ClassBreaksDescriptor());
        pb.addSource(image);
        pb.setParameter("method", ClassificationMethod.QUANTILE);
        pb.setParameter("numClasses", 5);
        pb.setParameter("percentages", true);
        RenderedImage op = JAI.create("ClassBreaks", pb, null);
        Classification classification = (Classification) op.getProperty(ClassBreaksDescriptor.CLASSIFICATION_PROPERTY);
        assertNotNull(classification);
        double[] percentages = classification.getPercentages();
        assertEquals(percentages.length, 2);
        assertTrue(percentages[0] == 50.0);
        assertTrue(percentages[1] == 50.0);
    }

    @Test
    public void testQuantileBreaksHistogramWithPercentages() throws Exception {
        RenderedImage image = createImage();
        ParameterBlockJAI pb = new ParameterBlockJAI(new ClassBreaksDescriptor());
        pb.addSource(image);
        pb.setParameter("method", ClassificationMethod.QUANTILE);
        pb.setParameter("numClasses", 4);
        pb.setParameter("extrema", getExtrema(image));
        pb.setParameter("histogram", true);
        pb.setParameter("histogramBins", 100);
        pb.setParameter("percentages", true);
        RenderedImage op = JAI.create("ClassBreaks", pb, null);
        Classification classification = (Classification) op.getProperty(ClassBreaksDescriptor.CLASSIFICATION_PROPERTY);
        assertNotNull(classification);
        double[] percentages = classification.getPercentages();
        assertEquals(percentages.length, 4);
        assertTrue(percentages[0] == 31.25);
        assertTrue(percentages[1] == 18.75);
        assertTrue(percentages[2] == 25.0);
        assertTrue(percentages[3] == 25.0);
    }

    @Test
    public void testQuantileBreaksHistogramsPercentagesMoreClassesThaIntervals() {
        RenderedImage image2 = ImageUtilities.createImageFromArray(
                new Number[] {1, 1, 1, 1, 1, 1, 1, 1, 8, 8, 8, 8, 11, 11, 11, 16}, 4, 4);
        ParameterBlockJAI pb2 = new ParameterBlockJAI(new ClassBreaksDescriptor());
        pb2.addSource(image2);
        pb2.setParameter("method", ClassificationMethod.QUANTILE);
        pb2.setParameter("numClasses", 5);
        pb2.setParameter("extrema", getExtrema(image2));
        pb2.setParameter("histogram", true);
        pb2.setParameter("histogramBins", 100);
        pb2.setParameter("percentages", true);
        RenderedImage op2 = JAI.create("ClassBreaks", pb2, null);
        Classification classification2 =
                (Classification) op2.getProperty(ClassBreaksDescriptor.CLASSIFICATION_PROPERTY);
        assertNotNull(classification2);
        double[] percentages2 = classification2.getPercentages();
        assertEquals(percentages2.length, 3);
        assertTrue(percentages2[0] == 50.0);
        assertTrue(percentages2[1] == 25.0);
        assertTrue(percentages2[2] == 25.0);
    }

    @Test
    public void testEqualIntervalBreaksWithPercentages() throws Exception {
        RenderedImage image = createImage();

        ParameterBlockJAI pb = new ParameterBlockJAI(new ClassBreaksDescriptor());
        pb.addSource(image);
        pb.setParameter("method", ClassificationMethod.EQUAL_INTERVAL);
        pb.setParameter("numClasses", 4);
        pb.setParameter("percentages", true);
        RenderedImage op = JAI.create("ClassBreaks", pb, null);
        Classification classification = (Classification) op.getProperty(ClassBreaksDescriptor.CLASSIFICATION_PROPERTY);
        assertNotNull(classification);
        double[] percentages = classification.getPercentages();
        assertEquals(percentages.length, 4);
        assertTrue(percentages[0] == 56.25);
        assertTrue(percentages[1] == 31.25);
        assertTrue(percentages[2] == 0.0);
        assertTrue(percentages[3] == 12.5);
    }

    private Double[][] getExtrema(RenderedImage image) {
        RenderedOp extremaOp = ExtremaDescriptor.create(image, null, 1, 1, false, 1, null);
        double[][] extrema = (double[][]) extremaOp.getProperty("extrema");
        Double[][] result = new Double[2][];
        result[0] = new Double[] {extrema[0][0]};
        result[1] = new Double[] {extrema[1][0]};
        return result;
    }

    @Test
    public void testCreatesOperationDirectlyFromClassBreaksRIF() throws Exception {
        RenderedImage image = createImage();

        ParameterBlock pb = new ParameterBlock();
        pb.addSource(image);
        pb.set(4, 0);
        pb.set(ClassificationMethod.NATURAL_BREAKS, 1);
        pb.set(null, 2);
        pb.set(null, 3);
        pb.set(new Integer[] {0}, 4);
        pb.set(1, 5);
        pb.set(1, 6);
        pb.set(0.0, 7);
        RenderedImage op = new ClassBreaksRIF().create(pb, null);
        Classification classification = (Classification) op.getProperty(ClassBreaksDescriptor.CLASSIFICATION_PROPERTY);
        assertNotNull(classification);
        Number[] breaks = classification.getBreaks()[0];

        // 4 classes, 5 breaks
        assertEquals(5, breaks.length);
        assertEquals(1, breaks[0].doubleValue(), EPS);
        assertEquals(3, breaks[1].doubleValue(), EPS);
        assertEquals(16, breaks[2].doubleValue(), EPS);
        assertEquals(26, breaks[3].doubleValue(), EPS);
        assertEquals(53, breaks[4].doubleValue(), EPS);
    }

    @Test
    public void testRegistration() {
        RegistryElementDescriptor descriptor =
                JAI.getDefaultInstance().getOperationRegistry().getDescriptor("rendered", "ClassBreaks");
        assertNotNull(descriptor);
        assertEquals("ClassBreaks", descriptor.getName());
        ParameterListDescriptor parameters = descriptor.getParameterListDescriptor("rendered");
        assertArrayEquals(
                new String[] {
                    "numClasses",
                    "method",
                    "extrema",
                    "roi",
                    "band",
                    "xPeriod",
                    "yPeriod",
                    "noData",
                    "histogram",
                    "histogramBins",
                    "percentages"
                },
                parameters.getParamNames());
    }
}
