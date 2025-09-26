/* Copyright (c) 2025 Daniele Romagnoli and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution and is available at
 * http://www.opensource.org/licenses/apache2.0.php.
 */
package org.eclipse.imagen;

import static org.junit.Assert.*;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.geom.Ellipse2D;
import java.awt.image.*;
import java.io.*;
import org.eclipse.imagen.media.serialize.SerializableState;
import org.eclipse.imagen.media.serialize.SerializerFactory;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests for serialization of various AWT and ImageN classes using the classes in the org.eclipse.imagen.media.serialize
 * package.
 */
public class SerializationTest {

    private static IndexColorModel indexColorModel;
    private static ComponentColorModel componentGrayCM;
    private static SampleModel pixelInterleavedSM;
    private static SampleModel singlePixelPackedSM;
    private static Shape ellipse;
    private static RenderingHints hints;

    @BeforeClass
    public static void setup() {
        // ColorModels
        componentGrayCM = new ComponentColorModel(
                ColorSpace.getInstance(ColorSpace.CS_GRAY),
                new int[] {8},
                false,
                false,
                Transparency.OPAQUE,
                DataBuffer.TYPE_BYTE);

        byte[] r = {0, (byte) 128, (byte) 255};
        byte[] g = {0, (byte) 128, (byte) 255};
        byte[] b = {0, (byte) 128, (byte) 255};
        indexColorModel = new IndexColorModel(8, 3, r, g, b);

        // SampleModels
        pixelInterleavedSM = RasterFactory.createPixelInterleavedSampleModel(DataBuffer.TYPE_BYTE, 3, 3, 1);

        singlePixelPackedSM = new SinglePixelPackedSampleModel(
                DataBuffer.TYPE_INT, 2, 2, new int[] {0x00FF0000, 0x0000FF00, 0x000000FF});

        ellipse = new Ellipse2D.Float(1f, 2f, 5f, 7f);

        hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        hints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
    }

    private static Object serializeAndDeserialize(Object src) {
        SerializableState state = SerializerFactory.getState(src, null);
        assertNotNull("SerializerFactory should return a SerializableState for " + src.getClass(), state);
        Object restored = state.getObject();
        assertNotNull("getObject() should reconstruct a non-null object", restored);
        return restored;
    }

    @Test
    public void testComponentGrayColorModel() {
        Object obj = serializeAndDeserialize(componentGrayCM);
        assertTrue(obj instanceof ColorModel);
        ColorModel cm = (ColorModel) obj;

        assertEquals(componentGrayCM.getNumComponents(), cm.getNumComponents());
        assertEquals(componentGrayCM.getTransferType(), cm.getTransferType());
        assertEquals(
                componentGrayCM.getColorSpace().getType(), cm.getColorSpace().getType());
        assertEquals(componentGrayCM.hasAlpha(), cm.hasAlpha());
        assertEquals(componentGrayCM.isAlphaPremultiplied(), cm.isAlphaPremultiplied());
    }

    @Test
    public void testIndexColorModel() {
        Object obj = serializeAndDeserialize(indexColorModel);
        assertTrue(obj instanceof IndexColorModel);
        IndexColorModel cm = (IndexColorModel) obj;

        assertEquals(indexColorModel.getMapSize(), cm.getMapSize());
        for (int i = 0; i < indexColorModel.getMapSize(); i++) {
            assertEquals("red@" + i, indexColorModel.getRed(i), cm.getRed(i));
            assertEquals("green@" + i, indexColorModel.getGreen(i), cm.getGreen(i));
            assertEquals("blue@" + i, indexColorModel.getBlue(i), cm.getBlue(i));
        }
        assertEquals(indexColorModel.getPixelSize(), cm.getPixelSize());
    }

    @Test
    public void testPpixelInterleavedSampleModel() {
        Object obj = serializeAndDeserialize(pixelInterleavedSM);
        assertTrue(obj instanceof SampleModel);
        SampleModel sm = (SampleModel) obj;

        assertEquals(pixelInterleavedSM.getDataType(), sm.getDataType());
        assertEquals(pixelInterleavedSM.getNumBands(), sm.getNumBands());
        assertEquals(pixelInterleavedSM.getClass(), sm.getClass());
    }

    @Test
    public void testSinglePixelPackedSampleModel() {
        Object obj = serializeAndDeserialize(singlePixelPackedSM);
        assertTrue(obj instanceof SampleModel);
        SampleModel sm = (SampleModel) obj;

        assertEquals(singlePixelPackedSM.getDataType(), sm.getDataType());
        assertEquals(singlePixelPackedSM.getNumBands(), sm.getNumBands());
        assertEquals(singlePixelPackedSM.getClass(), sm.getClass());
    }

    @Test
    public void testShape() {
        Object obj = serializeAndDeserialize(ellipse);
        assertTrue(obj instanceof Shape);
        Shape s = (Shape) obj;

        Rectangle srcB = ellipse.getBounds();
        Rectangle dstB = s.getBounds();
        assertEquals(srcB.x, dstB.x);
        assertEquals(srcB.y, dstB.y);
        assertEquals(srcB.width, dstB.width);
        assertEquals(srcB.height, dstB.height);
    }

    @Test
    public void testRenderingHints() {
        Object obj = serializeAndDeserialize(hints);
        assertTrue(obj instanceof RenderingHints);
        RenderingHints rh = (RenderingHints) obj;

        assertEquals(hints.get(RenderingHints.KEY_ANTIALIASING), rh.get(RenderingHints.KEY_ANTIALIASING));
        assertEquals(hints.get(RenderingHints.KEY_INTERPOLATION), rh.get(RenderingHints.KEY_INTERPOLATION));
        assertEquals(hints.size(), rh.size());
    }

    private static class SMCMHolder implements Serializable {
        private static final long serialVersionUID = 1L;

        transient SampleModel sampleModel;
        transient ColorModel colorModel;

        SMCMHolder(SampleModel sm, ColorModel cm) {
            this.sampleModel = sm;
            this.colorModel = cm;
        }

        private void writeObject(ObjectOutputStream out) throws IOException {
            out.defaultWriteObject();
            out.writeObject(SerializerFactory.getState(sampleModel, null));
            out.writeObject(SerializerFactory.getState(colorModel, null));
        }

        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            in.defaultReadObject();
            SerializableState smState = (SerializableState) in.readObject();
            sampleModel = (SampleModel) smState.getObject();
            SerializableState cmState = (SerializableState) in.readObject();
            colorModel = (ColorModel) cmState.getObject();
        }
    }

    @Test
    public void testSampleModelColorModel() throws Exception {
        SMCMHolder holder = new SMCMHolder(pixelInterleavedSM, componentGrayCM);

        byte[] bytes;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(holder);
            oos.flush();
            bytes = bos.toByteArray();
        }

        SMCMHolder restored;
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                ObjectInputStream ois = new ObjectInputStream(bis)) {
            restored = (SMCMHolder) ois.readObject();
        }

        assertNotNull(restored.sampleModel);
        assertNotNull(restored.colorModel);
        assertEquals(holder.sampleModel.getClass(), restored.sampleModel.getClass());
        assertEquals(holder.colorModel.getNumComponents(), restored.colorModel.getNumComponents());
        assertEquals(holder.colorModel.getTransferType(), restored.colorModel.getTransferType());
    }
}
