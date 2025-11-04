package com.novospir.libraries;

import java.awt.image.*;

/**
 * Provides efficient decoding and interpretation of Java AWT DataBuffer structures.
 * 
 * <p>DataBufferDecoder analyzes and decodes the internal structure of Java's image data storage
 * systems. It extracts information about how pixels are stored in DataBuffer objects, including
 * pixel layout, color band organization, and bit packing schemes.
 * 
 * <h3>Purpose:</h3>
 * <p>Java's image APIs use multiple data structures to store pixel information:
 * <ul>
 *   <li><b>DataBuffer</b> - Raw pixel data storage
 *   <li><b>SampleModel</b> - Describes how data is organized
 *   <li><b>ColorModel</b> - Describes color space and interpretation
 * </ul>
 * 
 * <p>DataBufferDecoder bridges these systems to provide efficient, type-safe access
 * to pixel data regardless of the underlying storage format.
 *
 * @see InfiniteWritableRaster
 * @author Novospir, Adam
 * @since 1.0
 */
class DataBufferDecoder {
    // Basic properties
    public final int pixelStride;
    public final int scanlineStride;
    public final int dataElementSize;
    public final int numBands;
    public final int[] bandOffsets;

    // Bit operations (for packed formats)
    public final int[] bitMasks;
    public final int[] bitShifts;
    public final boolean isPacked;

    // Data type information
    public final Class<?> dataType;
    public final int elementSizeBytes;
    public final boolean isSigned;

    // Color space information
    public final boolean hasAlpha;
    public final boolean isPremultiplied;
    public final int alphaBand;

    // Constructor
    public DataBufferDecoder(SampleModel sm, DataBuffer db) {
        this.numBands = sm.getNumBands();
        this.scanlineStride = sm.getWidth();
        this.dataElementSize = sm.getNumDataElements();
        this.dataType = getDataType(db);
        this.elementSizeBytes = getElementSizeBytes(dataType);
        this.isSigned = isSignedType(dataType);

        // Handle different SampleModel types
        if (sm instanceof ComponentSampleModel) {
            ComponentSampleModel csm = (ComponentSampleModel) sm;
            this.pixelStride = csm.getPixelStride();
            this.bandOffsets = csm.getBandOffsets();
            this.isPacked = false;
            this.bitMasks = null;
            this.bitShifts = null;
        } else if (sm instanceof SinglePixelPackedSampleModel) {
            SinglePixelPackedSampleModel spsm = (SinglePixelPackedSampleModel) sm;
            this.pixelStride = 1;
            this.isPacked = true;
            this.bitMasks = spsm.getBitMasks();
            this.bitShifts = spsm.getBitOffsets();
            // All bands in same element
            this.bandOffsets = new int[numBands];
            for (int b = 0; b < numBands; b++) {
                bandOffsets[b] = 0;
            }
        } else if (sm instanceof MultiPixelPackedSampleModel) {
            MultiPixelPackedSampleModel mppsm = (MultiPixelPackedSampleModel) sm;
            this.pixelStride = 1;
            this.isPacked = true;

            // Calculate bit mask from pixel bit stride
            int pixelBitStride = mppsm.getPixelBitStride();
            int bitMask = (1 << pixelBitStride) - 1; // e.g., if 8 bits per pixel, mask is 0xFF
            this.bitMasks = new int[]{bitMask};

            this.bitShifts = new int[]{0};
            // Complex bit-based offsets
            this.bandOffsets = new int[numBands];
            for (int b = 0; b < numBands; b++) {
                bandOffsets[b] = 0; // All bands in same packed element
            }
        } else {
            // Default case
            this.pixelStride = numBands;
            this.isPacked = false;
            this.bitMasks = null;
            this.bitShifts = null;
            this.bandOffsets = new int[numBands];
            for (int b = 0; b < numBands; b++) {
                bandOffsets[b] = b; // Default: consecutive bands
            }
        }

        // Determine alpha channel
        this.alphaBand = findAlphaBand(sm);
        this.hasAlpha = alphaBand >= 0;
        this.isPremultiplied = false; // Would need ColorModel to determine this
    }

    // Helper methods
    protected static float[] getDataArrayFloat(DataBuffer db) {
        return ((DataBufferFloat) db).getData();
    }

    protected static double[] getDataArrayDouble(DataBuffer db) {
        return ((DataBufferDouble) db).getData();
    }

    protected static int[] getDataArray(DataBuffer db) {
        if (db instanceof DataBufferInt) {
            return ((DataBufferInt) db).getData();
        } else if (db instanceof DataBufferByte) {
            // Convert byte array to int array
            byte[] byteData = ((DataBufferByte) db).getData();
            int[] intData = new int[byteData.length];
            for (int i = 0; i < byteData.length; i++) {
                intData[i] = byteData[i] & 0xFF; // Convert to unsigned
            }
            return intData;
        } else if (db instanceof DataBufferUShort) {
            // Convert short array to int array
            short[] shortData = ((DataBufferUShort) db).getData();
            int[] intData = new int[shortData.length];
            for (int i = 0; i < shortData.length; i++) {
                intData[i] = shortData[i] & 0xFFFF; // Convert to unsigned
            }
            return intData;
        } else if (db instanceof DataBufferShort) {
            // Convert short array to int array
            short[] shortData = ((DataBufferShort) db).getData();
            int[] intData = new int[shortData.length];
            for (int i = 0; i < shortData.length; i++) {
                intData[i] = shortData[i];
            }
            return intData;
        } else if (db instanceof DataBufferFloat) {
            // Convert float array to int array
            float[] floatData = ((DataBufferFloat) db).getData();
            int[] intData = new int[floatData.length];
            for (int i = 0; i < floatData.length; i++) {
                intData[i] = (int) floatData[i];
            }
            return intData;
        } else if (db instanceof DataBufferDouble) {
            // Convert double array to int array
            double[] doubleData = ((DataBufferDouble) db).getData();
            int[] intData = new int[doubleData.length];
            for (int i = 0; i < doubleData.length; i++) {
                intData[i] = (int) doubleData[i];
            }
            return intData;
        } else {
            throw new UnsupportedOperationException("Unsupported DataBuffer type: " + db.getClass().getSimpleName());
        }
    }

    private static Class<?> getDataType(DataBuffer db) {
        if (db instanceof DataBufferInt) return int.class;
        if (db instanceof DataBufferByte) return byte.class;
        if (db instanceof DataBufferUShort) return short.class;
        if (db instanceof DataBufferShort) return short.class;
        if (db instanceof DataBufferFloat) return float.class;
        if (db instanceof DataBufferDouble) return double.class;
        return int.class; // Default
    }

    private static int getElementSizeBytes(Class<?> dataType) {
        if (dataType == int.class || dataType == float.class) return 4;
        if (dataType == short.class) return 2;
        if (dataType == byte.class) return 1;
        if (dataType == double.class) return 8;
        return 4; // Default
    }

    private static boolean isSignedType(Class<?> dataType) {
        return dataType != byte.class && dataType != short.class; // UShort is unsigned
    }

    private static int findAlphaBand(SampleModel sm) {
        // This would need ColorModel to determine accurately
        // For now, assume last band is alpha if it exists
        return sm.getNumBands() > 3 ? sm.getNumBands() - 1 : -1;
    }

    // Utility methods for data access
    public int getPixelOffset(int x, int y) {
        return y * scanlineStride + x * pixelStride;
    }

    public int getBandOffset(int pixelOffset, int band) {
        return pixelOffset + bandOffsets[band];
    }

    public boolean needsBitUnpacking() {
        return isPacked && bitMasks != null;
    }

    public int unpackBand(int packedValue, int band) {
        if (!needsBitUnpacking()) return packedValue;
        return ((packedValue & bitMasks[band]) >>> bitShifts[band]);
    }
}
