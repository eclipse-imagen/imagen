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

package org.eclipse.imagen.media.codec;

import java.awt.color.ICC_Profile;
import java.awt.color.ICC_ProfileGray;
import java.awt.color.ICC_ProfileRGB;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.util.Date;
import java.util.Vector;

/**
 * An instance of <code>ImageEncodeParam</code> for encoding images in the PNG format.
 *
 * <p><b> This class is not a committed part of the JAI API. It may be removed or changed in future releases of JAI.</b>
 */
public abstract class PNGEncodeParam implements ImageEncodeParam {

    /** Constant for use with the sRGB chunk. */
    public static final int INTENT_PERCEPTUAL = 0;

    /** Constant for use with the sRGB chunk. */
    public static final int INTENT_RELATIVE = 1;

    /** Constant for use with the sRGB chunk. */
    public static final int INTENT_SATURATION = 2;

    /** Constant for use with the sRGB chunk. */
    public static final int INTENT_ABSOLUTE = 3;

    /** Constant for use in filtering. */
    public static final int PNG_FILTER_NONE = 0;

    /** Constant for use in filtering. */
    public static final int PNG_FILTER_SUB = 1;

    /** Constant for use in filtering. */
    public static final int PNG_FILTER_UP = 2;

    /** Constant for use in filtering. */
    public static final int PNG_FILTER_AVERAGE = 3;

    /** Constant for use in filtering. */
    public static final int PNG_FILTER_PAETH = 4;

    /**
     * Returns an instance of <code>PNGEncodeParam.Palette</code>, <code>PNGEncodeParam.Gray</code>, or <code>
     * PNGEncodeParam.RGB</code> appropriate for encoding the given image.
     *
     * <p>If the image has an <code>IndexColorModel</code>, an instance of <code>PNGEncodeParam.Palette</code> is
     * returned. Otherwise, if the image has 1 or 2 bands an instance of <code>PNGEncodeParam.Gray</code> is returned.
     * In all other cases an instance of <code>PNGEncodeParam.RGB</code> is returned.
     *
     * <p>Note that this method does not provide any guarantee that the given image will be successfully encoded by the
     * PNG encoder, as it only performs a very superficial analysis of the image structure.
     */
    public static PNGEncodeParam getDefaultEncodeParam(RenderedImage im) {
        ColorModel colorModel = im.getColorModel();
        if (colorModel instanceof IndexColorModel) {
            return new PNGEncodeParam.Palette();
        }

        SampleModel sampleModel = im.getSampleModel();
        int numBands = sampleModel.getNumBands();

        if (numBands == 1 || numBands == 2) {
            return new PNGEncodeParam.Gray();
        } else {
            return new PNGEncodeParam.RGB();
        }
    }

    public static class Palette extends PNGEncodeParam {

        /** Constructs an instance of <code>PNGEncodeParam.Palette</code>. */
        public Palette() {}

        // bKGD chunk

        private boolean backgroundSet = false;

        /** Suppresses the 'bKGD' chunk from being output. */
        public void unsetBackground() {
            backgroundSet = false;
        }

        /** Returns true if a 'bKGD' chunk will be output. */
        public boolean isBackgroundSet() {
            return backgroundSet;
        }

        /**
         * Sets the desired bit depth for a palette image. The bit depth must be one of 1, 2, 4, or 8, or else an <code>
         * IllegalArgumentException</code> will be thrown.
         */
        public void setBitDepth(int bitDepth) {
            if (bitDepth != 1 && bitDepth != 2 && bitDepth != 4 && bitDepth != 8) {
                throw new IllegalArgumentException(JaiI18N.getString("PNGEncodeParam2"));
            }
            this.bitDepth = bitDepth;
            bitDepthSet = true;
        }

        // PLTE chunk

        private int[] palette = null;
        private boolean paletteSet = false;

        /**
         * Sets the RGB palette of the image to be encoded. The <code>rgb</code> parameter contains alternating R, G, B
         * values for each color index used in the image. The number of elements must be a multiple of 3 between 3 and
         * 3*256.
         *
         * <p>The 'PLTE' chunk will encode this information.
         *
         * @param rgb An array of <code>int</code>s.
         */
        public void setPalette(int[] rgb) {
            if (rgb.length < 1 * 3 || rgb.length > 256 * 3) {
                throw new IllegalArgumentException(JaiI18N.getString("PNGEncodeParam0"));
            }
            if ((rgb.length % 3) != 0) {
                throw new IllegalArgumentException(JaiI18N.getString("PNGEncodeParam1"));
            }

            palette = (int[]) (rgb.clone());
            paletteSet = true;
        }

        /**
         * Returns the current RGB palette.
         *
         * <p>If the palette has not previously been set, or has been unset, an <code>IllegalStateException</code> will
         * be thrown.
         *
         * @throws IllegalStateException if the palette is not set.
         * @return An array of <code>int</code>s.
         */
        public int[] getPalette() {
            if (!paletteSet) {
                throw new IllegalStateException(JaiI18N.getString("PNGEncodeParam3"));
            }
            return (int[]) (palette.clone());
        }

        /** Suppresses the 'PLTE' chunk from being output. */
        public void unsetPalette() {
            palette = null;
            paletteSet = false;
        }

        /** Returns true if a 'PLTE' chunk will be output. */
        public boolean isPaletteSet() {
            return paletteSet;
        }

        // bKGD chunk

        private int backgroundPaletteIndex;

        /**
         * Sets the palette index of the suggested background color.
         *
         * <p>The 'bKGD' chunk will encode this information.
         */
        public void setBackgroundPaletteIndex(int index) {
            backgroundPaletteIndex = index;
            backgroundSet = true;
        }

        /**
         * Returns the palette index of the suggested background color.
         *
         * <p>If the background palette index has not previously been set, or has been unset, an <code>
         * IllegalStateException</code> will be thrown.
         *
         * @throws IllegalStateException if the palette index is not set.
         */
        public int getBackgroundPaletteIndex() {
            if (!backgroundSet) {
                throw new IllegalStateException(JaiI18N.getString("PNGEncodeParam4"));
            }
            return backgroundPaletteIndex;
        }

        // tRNS chunk

        private int[] transparency;

        /**
         * Sets the alpha values associated with each palette entry. The <code>alpha</code> parameter should have as
         * many entries as there are RGB triples in the palette.
         *
         * <p>The 'tRNS' chunk will encode this information.
         */
        public void setPaletteTransparency(byte[] alpha) {
            transparency = new int[alpha.length];
            for (int i = 0; i < alpha.length; i++) {
                transparency[i] = alpha[i] & 0xff;
            }
            transparencySet = true;
        }

        /**
         * Returns the alpha values associated with each palette entry.
         *
         * <p>If the palette transparency has not previously been set, or has been unset, an <code>IllegalStateException
         * </code> will be thrown.
         *
         * @throws IllegalStateException if the palette transparency is not set.
         */
        public byte[] getPaletteTransparency() {
            if (!transparencySet) {
                throw new IllegalStateException(JaiI18N.getString("PNGEncodeParam5"));
            }
            byte[] alpha = new byte[transparency.length];
            for (int i = 0; i < alpha.length; i++) {
                alpha[i] = (byte) transparency[i];
            }
            return alpha;
        }
    }

    public static class Gray extends PNGEncodeParam {

        /** Constructs an instance of <code>PNGEncodeParam.Gray</code>. */
        public Gray() {}

        // bKGD chunk

        private boolean backgroundSet = false;

        /** Suppresses the 'bKGD' chunk from being output. */
        public void unsetBackground() {
            backgroundSet = false;
        }

        /** Returns true if a 'bKGD' chunk will be output. */
        public boolean isBackgroundSet() {
            return backgroundSet;
        }

        /**
         * Sets the desired bit depth for a grayscale image. The bit depth must be one of 1, 2, 4, 8, or 16.
         *
         * <p>When encoding a source image of a greater bit depth, pixel values will be clamped to the smaller range
         * after shifting by the value given by <code>getBitShift()</code>. When encoding a source image of a smaller
         * bit depth, pixel values will be shifted and left-filled with zeroes.
         */
        public void setBitDepth(int bitDepth) {
            if (bitDepth != 1 && bitDepth != 2 && bitDepth != 4 && bitDepth != 8 && bitDepth != 16) {
                throw new IllegalArgumentException();
            }
            this.bitDepth = bitDepth;
            bitDepthSet = true;
        }

        // bKGD chunk

        private int backgroundPaletteGray;

        /**
         * Sets the suggested gray level of the background.
         *
         * <p>The 'bKGD' chunk will encode this information.
         */
        public void setBackgroundGray(int gray) {
            backgroundPaletteGray = gray;
            backgroundSet = true;
        }

        /**
         * Returns the suggested gray level of the background.
         *
         * <p>If the background gray level has not previously been set, or has been unset, an <code>
         * IllegalStateException</code> will be thrown.
         *
         * @throws IllegalStateException if the background gray level is not set.
         */
        public int getBackgroundGray() {
            if (!backgroundSet) {
                throw new IllegalStateException(JaiI18N.getString("PNGEncodeParam6"));
            }
            return backgroundPaletteGray;
        }

        // tRNS chunk

        private int[] transparency;

        /**
         * Sets the gray value to be used to denote transparency.
         *
         * <p>Setting this attribute will cause the alpha channel of the input image to be ignored.
         *
         * <p>The 'tRNS' chunk will encode this information.
         */
        public void setTransparentGray(int transparentGray) {
            transparency = new int[1];
            transparency[0] = transparentGray;
            transparencySet = true;
        }

        /**
         * Returns the gray value to be used to denote transparency.
         *
         * <p>If the transparent gray value has not previously been set, or has been unset, an <code>
         * IllegalStateException</code> will be thrown.
         *
         * @throws IllegalStateException if the transparent gray value is not set.
         */
        public int getTransparentGray() {
            if (!transparencySet) {
                throw new IllegalStateException(JaiI18N.getString("PNGEncodeParam7"));
            }
            int gray = transparency[0];
            return gray;
        }

        private int bitShift;
        private boolean bitShiftSet = false;

        /**
         * Sets the desired bit shift for a grayscale image. Pixels in the source image will be shifted right by the
         * given amount prior to being clamped to the maximum value given by the encoded image's bit depth.
         */
        public void setBitShift(int bitShift) {
            if (bitShift < 0) {
                throw new RuntimeException();
            }
            this.bitShift = bitShift;
            bitShiftSet = true;
        }

        /**
         * Returns the desired bit shift for a grayscale image.
         *
         * <p>If the bit shift has not previously been set, or has been unset, an <code>IllegalStateException</code>
         * will be thrown.
         *
         * @throws IllegalStateException if the bit shift is not set.
         */
        public int getBitShift() {
            if (!bitShiftSet) {
                throw new IllegalStateException(JaiI18N.getString("PNGEncodeParam8"));
            }
            return bitShift;
        }

        /**
         * Suppresses the setting of the bit shift of a grayscale image. Pixels in the source image will not be shifted
         * prior to encoding.
         */
        public void unsetBitShift() {
            bitShiftSet = false;
        }

        /** Returns true if the bit shift has been set. */
        public boolean isBitShiftSet() {
            return bitShiftSet;
        }

        /** Returns true if the bit depth has been set. */
        public boolean isBitDepthSet() {
            return bitDepthSet;
        }
    }

    public static class RGB extends PNGEncodeParam {

        /** Constructs an instance of <code>PNGEncodeParam.RGB</code>. */
        public RGB() {}

        // bKGD chunk

        private boolean backgroundSet = false;

        /** Suppresses the 'bKGD' chunk from being output. */
        public void unsetBackground() {
            backgroundSet = false;
        }

        /** Returns true if a 'bKGD' chunk will be output. */
        public boolean isBackgroundSet() {
            return backgroundSet;
        }

        /** Sets the desired bit depth for an RGB image. The bit depth must be 8 or 16. */
        public void setBitDepth(int bitDepth) {
            if (bitDepth != 8 && bitDepth != 16) {
                throw new RuntimeException();
            }
            this.bitDepth = bitDepth;
            bitDepthSet = true;
        }

        // bKGD chunk

        private int[] backgroundRGB;

        /**
         * Sets the RGB value of the suggested background color. The <code>rgb</code> parameter should have 3 entries.
         *
         * <p>The 'bKGD' chunk will encode this information.
         */
        public void setBackgroundRGB(int[] rgb) {
            if (rgb.length != 3) {
                throw new RuntimeException();
            }
            backgroundRGB = rgb;
            backgroundSet = true;
        }

        /**
         * Returns the RGB value of the suggested background color.
         *
         * <p>If the background color has not previously been set, or has been unset, an <code>IllegalStateException
         * </code> will be thrown.
         *
         * @throws IllegalStateException if the background color is not set.
         */
        public int[] getBackgroundRGB() {
            if (!backgroundSet) {
                throw new IllegalStateException(JaiI18N.getString("PNGEncodeParam9"));
            }
            return backgroundRGB;
        }

        // tRNS chunk

        private int[] transparency;

        /**
         * Sets the RGB value to be used to denote transparency.
         *
         * <p>Setting this attribute will cause the alpha channel of the input image to be ignored.
         *
         * <p>The 'tRNS' chunk will encode this information.
         */
        public void setTransparentRGB(int[] transparentRGB) {
            transparency = (int[]) (transparentRGB.clone());
            transparencySet = true;
        }

        /**
         * Returns the RGB value to be used to denote transparency.
         *
         * <p>If the transparent color has not previously been set, or has been unset, an <code>IllegalStateException
         * </code> will be thrown.
         *
         * @throws IllegalStateException if the transparent color is not set.
         */
        public int[] getTransparentRGB() {
            if (!transparencySet) {
                throw new IllegalStateException(JaiI18N.getString("PNGEncodeParam10"));
            }
            return (int[]) (transparency.clone());
        }
    }

    protected int bitDepth;
    protected boolean bitDepthSet = false;

    /** Sets the desired bit depth of an image. */
    public abstract void setBitDepth(int bitDepth);

    /**
     * Returns the desired bit depth for a grayscale image.
     *
     * <p>If the bit depth has not previously been set, or has been unset, an <code>IllegalStateException</code> will be
     * thrown.
     *
     * @throws IllegalStateException if the bit depth is not set.
     */
    public int getBitDepth() {
        if (!bitDepthSet) {
            throw new IllegalStateException(JaiI18N.getString("PNGEncodeParam11"));
        }
        return bitDepth;
    }

    /**
     * Suppresses the setting of the bit depth of a grayscale image. The depth of the encoded image will be inferred
     * from the source image bit depth, rounded up to the next power of 2 between 1 and 16.
     */
    public void unsetBitDepth() {
        bitDepthSet = false;
    }

    private boolean useInterlacing = false;

    /** Turns Adam7 interlacing on or off. */
    public void setInterlacing(boolean useInterlacing) {
        this.useInterlacing = useInterlacing;
    }

    /** Returns <code>true</code> if Adam7 interlacing will be used. */
    public boolean getInterlacing() {
        return useInterlacing;
    }

    // bKGD chunk - delegate to subclasses

    // In JAI 1.0, 'backgroundSet' was private.  The JDK 1.2 compiler
    // was lenient and incorrectly allowed this variable to be
    // accessed from the subclasses.  The JDK 1.3 compiler correctly
    // flags this as a use of a non-static variable in a static
    // context.  Changing 'backgroundSet' to protected would have
    // solved the problem, but would have introduced a visible API
    // change.  Thus we are forced to adopt the solution of placing a
    // separate private variable in each subclass and providing
    // separate implementations of 'unsetBackground' and
    // 'isBackgroundSet' in each concrete subclass.

    /**
     * Suppresses the 'bKGD' chunk from being output. For API compatibility with JAI 1.0, the superclass defines this
     * method to throw a <code>RuntimeException</code>; accordingly, subclasses must provide their own implementations.
     */
    public void unsetBackground() {
        throw new RuntimeException(JaiI18N.getString("PNGEncodeParam23"));
    }

    /**
     * Returns true if a 'bKGD' chunk will be output. For API compatibility with JAI 1.0, the superclass defines this
     * method to throw a <code>RuntimeException</code>; accordingly, subclasses must provide their own implementations.
     */
    public boolean isBackgroundSet() {
        throw new RuntimeException(JaiI18N.getString("PNGEncodeParam24"));
    }

    // cHRM chunk

    private float[] chromaticity = null;
    private boolean chromaticitySet = false;

    /**
     * Sets the white point and primary chromaticities in CIE (x, y) space.
     *
     * <p>The <code>chromaticity</code> parameter should be a <code>float</code> array of length 8 containing the white
     * point X and Y, red X and Y, green X and Y, and blue X and Y values in order.
     *
     * <p>The 'cHRM' chunk will encode this information.
     */
    public void setChromaticity(float[] chromaticity) {
        if (chromaticity.length != 8) {
            throw new IllegalArgumentException();
        }
        this.chromaticity = (float[]) (chromaticity.clone());
        chromaticitySet = true;
    }

    /** A convenience method that calls the array version. */
    public void setChromaticity(
            float whitePointX,
            float whitePointY,
            float redX,
            float redY,
            float greenX,
            float greenY,
            float blueX,
            float blueY) {
        float[] chroma = new float[8];
        chroma[0] = whitePointX;
        chroma[1] = whitePointY;
        chroma[2] = redX;
        chroma[3] = redY;
        chroma[4] = greenX;
        chroma[5] = greenY;
        chroma[6] = blueX;
        chroma[7] = blueY;
        setChromaticity(chroma);
    }

    /**
     * Returns the white point and primary chromaticities in CIE (x, y) space.
     *
     * <p>See the documentation for the <code>setChromaticity</code> method for the format of the returned data.
     *
     * <p>If the chromaticity has not previously been set, or has been unset, an <code>IllegalStateException</code> will
     * be thrown.
     *
     * @throws IllegalStateException if the chromaticity is not set.
     */
    public float[] getChromaticity() {
        if (!chromaticitySet) {
            throw new IllegalStateException(JaiI18N.getString("PNGEncodeParam12"));
        }
        return (float[]) (chromaticity.clone());
    }

    /** Suppresses the 'cHRM' chunk from being output. */
    public void unsetChromaticity() {
        chromaticity = null;
        chromaticitySet = false;
    }

    /** Returns true if a 'cHRM' chunk will be output. */
    public boolean isChromaticitySet() {
        return chromaticitySet;
    }

    // gAMA chunk

    private float gamma;
    private boolean gammaSet = false;

    /**
     * Sets the file gamma value for the image.
     *
     * <p>The 'gAMA' chunk will encode this information.
     */
    public void setGamma(float gamma) {
        this.gamma = gamma;
        gammaSet = true;
    }

    /**
     * Returns the file gamma value for the image.
     *
     * <p>If the file gamma has not previously been set, or has been unset, an <code>IllegalStateException</code> will
     * be thrown.
     *
     * @throws IllegalStateException if the gamma is not set.
     */
    public float getGamma() {
        if (!gammaSet) {
            throw new IllegalStateException(JaiI18N.getString("PNGEncodeParam13"));
        }
        return gamma;
    }

    /** Suppresses the 'gAMA' chunk from being output. */
    public void unsetGamma() {
        gammaSet = false;
    }

    /** Returns true if a 'gAMA' chunk will be output. */
    public boolean isGammaSet() {
        return gammaSet;
    }

    // hIST chunk

    private int[] paletteHistogram = null;
    private boolean paletteHistogramSet = false;

    /**
     * Sets the palette histogram to be stored with this image. The histogram consists of an array of integers, one per
     * palette entry.
     *
     * <p>The 'hIST' chunk will encode this information.
     */
    public void setPaletteHistogram(int[] paletteHistogram) {
        this.paletteHistogram = (int[]) (paletteHistogram.clone());
        paletteHistogramSet = true;
    }

    /**
     * Returns the palette histogram to be stored with this image.
     *
     * <p>If the histogram has not previously been set, or has been unset, an <code>IllegalStateException</code> will be
     * thrown.
     *
     * @throws IllegalStateException if the histogram is not set.
     */
    public int[] getPaletteHistogram() {
        if (!paletteHistogramSet) {
            throw new IllegalStateException(JaiI18N.getString("PNGEncodeParam14"));
        }
        return paletteHistogram;
    }

    /** Suppresses the 'hIST' chunk from being output. */
    public void unsetPaletteHistogram() {
        paletteHistogram = null;
        paletteHistogramSet = false;
    }

    /** Returns true if a 'hIST' chunk will be output. */
    public boolean isPaletteHistogramSet() {
        return paletteHistogramSet;
    }

    // iCCP chunk

    private byte[] ICCProfileData = null;
    private boolean ICCProfileDataSet = false;
    private String ICCProfileName = null;

    /**
     * Sets the ICC profile data to be stored with this image. The profile is represented in raw binary form.
     *
     * <p>The 'iCCP' chunk will encode this information.
     */
    public void setICCProfileData(byte[] ICCProfileData) {
        this.ICCProfileData = (byte[]) (ICCProfileData.clone());
        ICCProfileDataSet = true;
        ICC_Profile profile = ICC_Profile.getInstance(this.ICCProfileData);
        if (!(profile instanceof ICC_ProfileRGB || profile instanceof ICC_ProfileGray)) return;

        // Set gamma
        try {
            if (profile instanceof ICC_ProfileRGB)
                setGamma(((ICC_ProfileRGB) profile).getGamma(ICC_ProfileRGB.REDCOMPONENT));
            else if (profile instanceof ICC_ProfileGray) setGamma(((ICC_ProfileGray) profile).getGamma());
        } catch (Exception e) {
            // Gamma is not defined.  TRC is defined.  So ignore.
        }

        if (profile instanceof ICC_ProfileGray) return;

        // Set cHRM
        float[] chrom = new float[8];
        float[] whitePoint = ((ICC_ProfileRGB) profile).getMediaWhitePoint();
        if (whitePoint == null) return;

        float sum = whitePoint[0] + whitePoint[1] + whitePoint[2];
        chrom[0] = whitePoint[0] / sum;
        chrom[1] = whitePoint[1] / sum;

        float[][] temp = ((ICC_ProfileRGB) profile).getMatrix();
        if (temp == null) return;

        for (int i = 0; i < 3; i++) {
            sum = temp[0][i] + temp[1][i] + temp[2][i];
            chrom[2 + (i << 1)] = temp[0][i] / sum;
            chrom[3 + (i << 1)] = temp[1][i] / sum;
        }

        setChromaticity(chrom);
    }

    /**
     * Returns the ICC profile data to be stored with this image.
     *
     * <p>If the ICC profile has not previously been set, or has been unset, an <code>IllegalStateException</code> will
     * be thrown.
     *
     * @throws IllegalStateException if the ICC profile is not set.
     */
    public byte[] getICCProfileData() {
        if (!ICCProfileDataSet) {
            throw new IllegalStateException(JaiI18N.getString("PNGEncodeParam15"));
        }
        return (byte[]) (ICCProfileData.clone());
    }

    /** Suppresses the 'iCCP' chunk from being output. */
    public void unsetICCProfileData() {
        ICCProfileData = null;
        ICCProfileDataSet = false;
        ICCProfileName = null;
    }

    /** Sets the ICC profile name. */
    public void setICCProfileName(String name) {
        if (!ICCProfileDataSet) {
            throw new IllegalStateException(JaiI18N.getString("PNGEncodeParam15"));
        }
        this.ICCProfileName = name;
    }

    /** Returns the ICC profile name. */
    public String getICCProfileName() {
        if (!ICCProfileDataSet) {
            throw new IllegalStateException(JaiI18N.getString("PNGEncodeParam15"));
        }
        return ICCProfileName;
    }

    /** Returns true if a 'iCCP' chunk will be output. */
    public boolean isICCProfileDataSet() {
        return ICCProfileDataSet;
    }

    // pHYS chunk

    private int[] physicalDimension = null;
    private boolean physicalDimensionSet = false;

    /**
     * Sets the physical dimension information to be stored with this image. The physicalDimension parameter should be a
     * 3-entry array containing the number of pixels per unit in the X direction, the number of pixels per unit in the Y
     * direction, and the unit specifier (0 = unknown, 1 = meters).
     *
     * <p>The 'pHYS' chunk will encode this information.
     */
    public void setPhysicalDimension(int[] physicalDimension) {
        this.physicalDimension = (int[]) (physicalDimension.clone());
        physicalDimensionSet = true;
    }

    /** A convenience method that calls the array version. */
    public void setPhysicalDimension(int xPixelsPerUnit, int yPixelsPerUnit, int unitSpecifier) {
        int[] pd = new int[3];
        pd[0] = xPixelsPerUnit;
        pd[1] = yPixelsPerUnit;
        pd[2] = unitSpecifier;

        setPhysicalDimension(pd);
    }

    /**
     * Returns the physical dimension information to be stored with this image.
     *
     * <p>If the physical dimension information has not previously been set, or has been unset, an <code>
     * IllegalStateException</code> will be thrown.
     *
     * @throws IllegalStateException if the physical dimension information is not set.
     */
    public int[] getPhysicalDimension() {
        if (!physicalDimensionSet) {
            throw new IllegalStateException(JaiI18N.getString("PNGEncodeParam16"));
        }
        return (int[]) (physicalDimension.clone());
    }

    /** Suppresses the 'pHYS' chunk from being output. */
    public void unsetPhysicalDimension() {
        physicalDimension = null;
        physicalDimensionSet = false;
    }

    /** Returns true if a 'pHYS' chunk will be output. */
    public boolean isPhysicalDimensionSet() {
        return physicalDimensionSet;
    }

    // sPLT chunk

    private PNGSuggestedPaletteEntry[] suggestedPalette = null;
    private boolean suggestedPaletteSet = false;

    /**
     * Sets the suggested palette information to be stored with this image. The information is passed to this method as
     * an array of <code>PNGSuggestedPaletteEntry</code> objects.
     *
     * <p>The 'sPLT' chunk will encode this information.
     */
    public void setSuggestedPalette(PNGSuggestedPaletteEntry[] palette) {
        suggestedPalette = (PNGSuggestedPaletteEntry[]) (palette.clone());
        suggestedPaletteSet = true;
    }

    /**
     * Returns the suggested palette information to be stored with this image.
     *
     * <p>If the suggested palette information has not previously been set, or has been unset, an <code>
     * IllegalStateException</code> will be thrown.
     *
     * @throws IllegalStateException if the suggested palette information is not set.
     */
    public PNGSuggestedPaletteEntry[] getSuggestedPalette() {
        if (!suggestedPaletteSet) {
            throw new IllegalStateException(JaiI18N.getString("PNGEncodeParam17"));
        }
        return (PNGSuggestedPaletteEntry[]) (suggestedPalette.clone());
    }

    /** Suppresses the 'sPLT' chunk from being output. */
    public void unsetSuggestedPalette() {
        suggestedPalette = null;
        suggestedPaletteSet = false;
    }

    /** Returns true if a 'sPLT' chunk will be output. */
    public boolean isSuggestedPaletteSet() {
        return suggestedPaletteSet;
    }

    // sBIT chunk

    private int[] significantBits = null;
    private boolean significantBitsSet = false;

    /**
     * Sets the number of significant bits for each band of the image.
     *
     * <p>The number of entries in the <code>significantBits</code> array must be equal to the number of output bands in
     * the image: 1 for a gray image, 2 for gray+alpha, 3 for index or truecolor, and 4 for truecolor+alpha.
     *
     * <p>The 'sBIT' chunk will encode this information.
     */
    public void setSignificantBits(int[] significantBits) {
        this.significantBits = (int[]) (significantBits.clone());
        significantBitsSet = true;
    }

    /**
     * Returns the number of significant bits for each band of the image.
     *
     * <p>If the significant bits values have not previously been set, or have been unset, an <code>
     * IllegalStateException</code> will be thrown.
     *
     * @throws IllegalStateException if the significant bits values are not set.
     */
    public int[] getSignificantBits() {
        if (!significantBitsSet) {
            throw new IllegalStateException(JaiI18N.getString("PNGEncodeParam18"));
        }
        return (int[]) significantBits.clone();
    }

    /** Suppresses the 'sBIT' chunk from being output. */
    public void unsetSignificantBits() {
        significantBits = null;
        significantBitsSet = false;
    }

    /** Returns true if an 'sBIT' chunk will be output. */
    public boolean isSignificantBitsSet() {
        return significantBitsSet;
    }

    // sRGB chunk

    private int SRGBIntent;
    private boolean SRGBIntentSet = false;

    /**
     * Sets the sRGB rendering intent to be stored with this image. The legal values are 0 = Perceptual, 1 = Relative
     * Colorimetric, 2 = Saturation, and 3 = Absolute Colorimetric. Refer to the PNG specification for information on
     * these values.
     *
     * <p>The 'sRGB' chunk will encode this information.
     */
    public void setSRGBIntent(int SRGBIntent) {
        this.SRGBIntent = SRGBIntent;
        SRGBIntentSet = true;
    }

    /**
     * Returns the sRGB rendering intent to be stored with this image.
     *
     * <p>If the sRGB intent has not previously been set, or has been unset, an <code>IllegalStateException</code> will
     * be thrown.
     *
     * @throws IllegalStateException if the sRGB intent is not set.
     */
    public int getSRGBIntent() {
        if (!SRGBIntentSet) {
            throw new IllegalStateException(JaiI18N.getString("PNGEncodeParam19"));
        }
        return SRGBIntent;
    }

    /** Suppresses the 'sRGB' chunk from being output. */
    public void unsetSRGBIntent() {
        SRGBIntentSet = false;
    }

    /** Returns true if an 'sRGB' chunk will be output. */
    public boolean isSRGBIntentSet() {
        return SRGBIntentSet;
    }

    // tEXt chunk

    private String[] text = null;
    private boolean textSet = false;

    /**
     * Sets the textual data to be stored in uncompressed form with this image. The data is passed to this method as an
     * array of <code>String</code>s.
     *
     * <p>The 'tEXt' chunk will encode this information.
     */
    public void setText(String[] text) {
        this.text = text;
        textSet = true;
    }

    /**
     * Returns the text strings to be stored in uncompressed form with this image as an array of <code>String</code>s.
     *
     * <p>If the text strings have not previously been set, or have been unset, an <code>IllegalStateException</code>
     * will be thrown.
     *
     * @throws IllegalStateException if the text strings are not set.
     */
    public String[] getText() {
        if (!textSet) {
            throw new IllegalStateException(JaiI18N.getString("PNGEncodeParam20"));
        }
        return text;
    }

    /** Suppresses the 'tEXt' chunk from being output. */
    public void unsetText() {
        text = null;
        textSet = false;
    }

    /** Returns true if a 'tEXt' chunk will be output. */
    public boolean isTextSet() {
        return textSet;
    }

    // tIME chunk

    private Date modificationTime;
    private boolean modificationTimeSet = false;

    /**
     * Sets the modification time, as a <code>Date</code>, to be stored with this image. The internal storage format
     * will use UTC regardless of how the <code>modificationTime</code> parameter was created.
     *
     * <p>The 'tIME' chunk will encode this information.
     */
    public void setModificationTime(Date modificationTime) {
        this.modificationTime = modificationTime;
        modificationTimeSet = true;
    }

    /**
     * Returns the modification time to be stored with this image.
     *
     * <p>If the bit depth has not previously been set, or has been unset, an <code>IllegalStateException</code> will be
     * thrown.
     *
     * @throws IllegalStateException if the bit depth is not set.
     */
    public Date getModificationTime() {
        if (!modificationTimeSet) {
            throw new IllegalStateException(JaiI18N.getString("PNGEncodeParam21"));
        }
        return modificationTime;
    }

    /** Suppresses the 'tIME' chunk from being output. */
    public void unsetModificationTime() {
        modificationTime = null;
        modificationTimeSet = false;
    }

    /** Returns true if a 'tIME' chunk will be output. */
    public boolean isModificationTimeSet() {
        return modificationTimeSet;
    }

    // tRNS chunk

    boolean transparencySet = false;

    /** Suppresses the 'tRNS' chunk from being output. */
    public void unsetTransparency() {
        transparencySet = false;
    }

    /** Returns true if a 'tRNS' chunk will be output. */
    public boolean isTransparencySet() {
        return transparencySet;
    }

    // zTXT chunk

    private String[] zText = null;
    private boolean zTextSet = false;

    /**
     * Sets the text strings to be stored in compressed form with this image. The data is passed to this method as an
     * array of <code>String</code>s.
     *
     * <p>The 'zTXt' chunk will encode this information.
     */
    public void setCompressedText(String[] text) {
        this.zText = text;
        zTextSet = true;
    }

    /**
     * Returns the text strings to be stored in compressed form with this image as an array of <code>String</code>s.
     *
     * <p>If the compressed text strings have not previously been set, or have been unset, an <code>
     * IllegalStateException</code> will be thrown.
     *
     * @throws IllegalStateException if the compressed text strings are not set.
     */
    public String[] getCompressedText() {
        if (!zTextSet) {
            throw new IllegalStateException(JaiI18N.getString("PNGEncodeParam22"));
        }
        return zText;
    }

    /** Suppresses the 'zTXt' chunk from being output. */
    public void unsetCompressedText() {
        zText = null;
        zTextSet = false;
    }

    /** Returns true if a 'zTXT' chunk will be output. */
    public boolean isCompressedTextSet() {
        return zTextSet;
    }

    // Other chunk types

    Vector chunkType = new Vector();
    Vector chunkData = new Vector();

    /**
     * Adds a private chunk, in binary form, to the list of chunks to be stored with this image.
     *
     * @param type a 4-character String giving the chunk type name.
     * @param data an array of <code>byte</code>s containing the chunk data.
     */
    public synchronized void addPrivateChunk(String type, byte[] data) {
        chunkType.add(type);
        chunkData.add((byte[]) data.clone());
    }

    /** Returns the number of private chunks to be written to the output file. */
    public synchronized int getNumPrivateChunks() {
        return chunkType.size();
    }

    /**
     * Returns the type of the private chunk at a given index, as a 4-character <code>String</code>. The index must be
     * smaller than the return value of <code>getNumPrivateChunks</code>.
     */
    public synchronized String getPrivateChunkType(int index) {
        return (String) chunkType.elementAt(index);
    }

    /**
     * Returns the data associated of the private chunk at a given index, as an array of <code>byte</code>s. The index
     * must be smaller than the return value of <code>getNumPrivateChunks</code>.
     */
    public synchronized byte[] getPrivateChunkData(int index) {
        return (byte[]) chunkData.elementAt(index);
    }

    /**
     * Remove all private chunks associated with this parameter instance whose 'safe-to-copy' bit is not set. This may
     * be advisable when transcoding PNG images.
     */
    public synchronized void removeUnsafeToCopyPrivateChunks() {
        Vector newChunkType = new Vector();
        Vector newChunkData = new Vector();

        int len = getNumPrivateChunks();
        for (int i = 0; i < len; i++) {
            String type = getPrivateChunkType(i);
            char lastChar = type.charAt(3);
            if (lastChar >= 'a' && lastChar <= 'z') {
                newChunkType.add(type);
                newChunkData.add(getPrivateChunkData(i));
            }
        }

        chunkType = newChunkType;
        chunkData = newChunkData;
    }

    /** Remove all private chunks associated with this parameter instance. */
    public synchronized void removeAllPrivateChunks() {
        chunkType = new Vector();
        chunkData = new Vector();
    }

    /** An abs() function for use by the Paeth predictor. */
    private static final int abs(int x) {
        return (x < 0) ? -x : x;
    }

    /**
     * The Paeth predictor routine used in PNG encoding. This routine is included as a convenience to subclasses that
     * override the <code>filterRow</code> method.
     */
    public static final int paethPredictor(int a, int b, int c) {
        int p = a + b - c;
        int pa = abs(p - a);
        int pb = abs(p - b);
        int pc = abs(p - c);

        if ((pa <= pb) && (pa <= pc)) {
            return a;
        } else if (pb <= pc) {
            return b;
        } else {
            return c;
        }
    }

    /**
     * Performs filtering on a row of an image. This method may be overridden in order to provide a custom algorithm for
     * choosing the filter type for a given row.
     *
     * <p>The method is supplied with the current and previous rows of the image. For the first row of the image, or of
     * an interlacing pass, the previous row array will be filled with zeros as required by the PNG specification.
     *
     * <p>The method is also supplied with five scratch arrays. These arrays may be used within the method for any
     * purpose. At method exit, the array at the index given by the return value of the method should contain the
     * filtered data. The return value will also be used as the filter type.
     *
     * <p>The default implementation of the method performs a trial encoding with each of the filter types, and computes
     * the sum of absolute values of the differences between the raw bytes of the current row and the predicted values.
     * The index of the filter producing the smallest result is returned.
     *
     * <p>As an example, to perform only 'sub' filtering, this method could be implemented (non-optimally) as follows:
     *
     * <pre>
     * for (int i = bytesPerPixel; i < bytesPerRow + bytesPerPixel; i++) {
     *     int curr = currRow[i] & 0xff;
     *     int left = currRow[i - bytesPerPixel] & 0xff;
     *     scratchRow[PNG_FILTER_SUB][i] = (byte)(curr - left);
     * }
     * return PNG_FILTER_SUB;
     * </pre>
     *
     * @param currRow The current row as an array of <code>byte</code>s of length at least <code>
     *     bytesPerRow + bytesPerPixel</code>. The pixel data starts at index <code>bytesPerPixel</code>; the initial
     *     <code>bytesPerPixel</code> bytes are zero.
     * @param prevRow The current row as an array of <code>byte</code>s The pixel data starts at index <code>
     *     bytesPerPixel</code>; the initial <code>bytesPerPixel</code> bytes are zero.
     * @param scratchRows An array of 5 <code>byte</code> arrays of length at least <code>bytesPerRow +
     *        bytesPerPixel</code>, useable to hold temporary results. The filtered row will be returned as one of the
     *     entries of this array. The returned filtered data should start at index <code>bytesPerPixel</code>; The
     *     initial <code>bytesPerPixel</code> bytes are not used.
     * @param bytesPerRow The number of bytes in the image row. This value will always be greater than 0.
     * @param bytesPerPixel The number of bytes representing a single pixel, rounded up to an integer. This is the 'bpp'
     *     parameter described in the PNG specification.
     * @return The filter type to be used. The entry of <code>scratchRows[]</code> at this index holds the filtered
     *     data.
     */
    public int filterRow(byte[] currRow, byte[] prevRow, byte[][] scratchRows, int bytesPerRow, int bytesPerPixel) {
        int[] filterBadness = new int[5];
        for (int i = 0; i < 5; i++) {
            filterBadness[i] = Integer.MAX_VALUE;
        }

        {
            int badness = 0;

            for (int i = bytesPerPixel; i < bytesPerRow + bytesPerPixel; i++) {
                int curr = currRow[i] & 0xff;
                badness += curr;
            }

            filterBadness[0] = badness;
        }

        {
            byte[] subFilteredRow = scratchRows[1];
            int badness = 0;

            for (int i = bytesPerPixel; i < bytesPerRow + bytesPerPixel; i++) {
                int curr = currRow[i] & 0xff;
                int left = currRow[i - bytesPerPixel] & 0xff;
                int difference = curr - left;
                subFilteredRow[i] = (byte) difference;

                badness += abs(difference);
            }

            filterBadness[1] = badness;
        }

        {
            byte[] upFilteredRow = scratchRows[2];
            int badness = 0;

            for (int i = bytesPerPixel; i < bytesPerRow + bytesPerPixel; i++) {
                int curr = currRow[i] & 0xff;
                int up = prevRow[i] & 0xff;
                int difference = curr - up;
                upFilteredRow[i] = (byte) difference;

                badness += abs(difference);
            }

            filterBadness[2] = badness;
        }

        {
            byte[] averageFilteredRow = scratchRows[3];
            int badness = 0;

            for (int i = bytesPerPixel; i < bytesPerRow + bytesPerPixel; i++) {
                int curr = currRow[i] & 0xff;
                int left = currRow[i - bytesPerPixel] & 0xff;
                int up = prevRow[i] & 0xff;
                int difference = curr - (left + up) / 2;
                ;
                averageFilteredRow[i] = (byte) difference;

                badness += abs(difference);
            }

            filterBadness[3] = badness;
        }

        {
            byte[] paethFilteredRow = scratchRows[4];
            int badness = 0;

            for (int i = bytesPerPixel; i < bytesPerRow + bytesPerPixel; i++) {
                int curr = currRow[i] & 0xff;
                int left = currRow[i - bytesPerPixel] & 0xff;
                int up = prevRow[i] & 0xff;
                int upleft = prevRow[i - bytesPerPixel] & 0xff;
                int predictor = paethPredictor(left, up, upleft);
                int difference = curr - predictor;
                paethFilteredRow[i] = (byte) difference;

                badness += abs(difference);
            }

            filterBadness[4] = badness;
        }

        int filterType = 0;
        int minBadness = filterBadness[0];

        for (int i = 1; i < 5; i++) {
            if (filterBadness[i] < minBadness) {
                minBadness = filterBadness[i];
                filterType = i;
            }
        }

        if (filterType == 0) {
            System.arraycopy(currRow, bytesPerPixel, scratchRows[0], bytesPerPixel, bytesPerRow);
        }

        return filterType;
    }
}
