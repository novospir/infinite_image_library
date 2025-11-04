package com.novospir.libraries;

import java.awt.*;
import java.awt.image.*;

/**
 * Abstraction layer for WritableRaster-like functionality.
 * 
 * <p>AbstractWritableRaster provides a common interface for low-level pixel manipulation
 * across different raster implementations. It abstracts over:
 * <ul>
 *   <li>Standard {@link WritableRaster} - Fixed-size image data
 *   <li>{@link InfiniteWritableRaster} - Infinite-space tiled raster
 * </ul>
 * 
 * <h3>Purpose:</h3>
 * <p>This interface enables polymorphic access to pixel-level operations:
 * <ul>
 *   <li><b>Unified API</b> - Same methods work for both standard and infinite rasters
 *   <li><b>Testing</b> - Compare implementations without code changes
 *   <li><b>Compatibility</b> - Bridge to Java's native imaging API
 * </ul>
 * 
 * @see AbstractBufferedImage
 * @see InfiniteWritableRaster
 * @see WritableRaster
 * @author Novospir, Adam
 * @since 1.0
 */
public interface AbstractWritableRaster {

    /**
     * Returns the number of data elements per pixel.
     *
     * @return The number of data elements per pixel
     * @see Raster#getNumDataElements()
     */
    int getNumDataElements();

    /**
     * Returns the number of color bands (channels) in this raster.
     *
     * @return The number of bands
     * @see Raster#getNumBands()
     */
    int getNumBands();

    /**
     * Returns the height of this raster.
     *
     * @return The height in pixels
     * @see Raster#getHeight()
     */
    int getHeight();

    /**
     * Returns the width of this raster.
     *
     * @return The width in pixels
     * @see Raster#getWidth()
     */
    int getWidth();

    /**
     * Returns the minimum y-coordinate of this raster.
     *
     * @return The minimum y-coordinate
     * @see Raster#getMinY()
     */
    int getMinY();

    /**
     * Returns the minimum x-coordinate of this raster.
     *
     * @return The minimum x-coordinate
     * @see Raster#getMinX()
     */
    int getMinX();

    /**
     * Returns the transfer type used to represent pixel data.
     *
     * @return The transfer type constant
     * @see Raster#getTransferType()
     */
    int getTransferType();

    /**
     * Returns the bounds of this raster.
     *
     * @return A Rectangle representing the bounds
     * @see Raster#getBounds()
     */
    Rectangle getBounds();

    /**
     * Sets pixel data from an Object array at the specified location.
     *
     * @param x The x-coordinate
     * @param y The y-coordinate
     * @param inData The pixel data
     * @see WritableRaster#setDataElements(int, int, Object)
     */
    void setDataElements(int x, int y, Object inData);

    /**
     * Sets pixel data from a Raster at the specified location.
     *
     * @param x The x-coordinate
     * @param y The y-coordinate
     * @param inRaster The source raster
     * @see WritableRaster#setDataElements(int, int, Raster)
     */
    void setDataElements(int x, int y, Raster inRaster);

    /**
     * Sets pixel data from an Object array for a rectangular region.
     *
     * @param x The x-coordinate of the region
     * @param y The y-coordinate of the region
     * @param w The width of the region
     * @param h The height of the region
     * @param inData The pixel data
     * @see WritableRaster#setDataElements(int, int, int, int, Object)
     */
    void setDataElements(int x, int y, int w, int h, Object inData);

    /**
     * Copies all pixels from the source raster into this raster.
     *
     * @param srcRaster The source raster
     * @see WritableRaster#setRect(Raster)
     */
    void setRect(Raster srcRaster);

    /**
     * Copies all pixels from the source raster into this raster at the specified offset.
     *
     * @param dx The destination x-coordinate
     * @param dy The destination y-coordinate
     * @param srcRaster The source raster
     * @see WritableRaster#setRect(int, int, Raster)
     */
    void setRect(int dx, int dy, Raster srcRaster);

    // ---------- PIXELS ---------------
    ///@see java.awt.image.Raster#getPixel(int, int, int[])

    /**
     * Gets pixel data as an int array.
     *
     * @param x The x-coordinate
     * @param y The y-coordinate
     * @param iArray Optional pre-allocated array for the result
     * @return An array containing the pixel data
     * @see Raster#getPixel(int, int, int[])
     */
    int[] getPixel(int x, int y, int[] iArray);

    /**
     * Gets pixel data as a float array.
     *
     * @param x The x-coordinate
     * @param y The y-coordinate
     * @param fArray Optional pre-allocated array for the result
     * @return An array containing the pixel data
     * @see Raster#getPixel(int, int, float[])
     */
    float[] getPixel(int x, int y, float[] fArray);

    /**
     * Gets pixel data as a double array.
     *
     * @param x The x-coordinate
     * @param y The y-coordinate
     * @param dArray Optional pre-allocated array for the result
     * @return An array containing the pixel data
     * @see Raster#getPixel(int, int, double[])
     */
    double[] getPixel(int x, int y, double[] dArray);

    /**
     * Gets pixel data for a rectangular region as an int array.
     *
     * @param x The x-coordinate of the region
     * @param y The y-coordinate of the region
     * @param w The width of the region
     * @param h The height of the region
     * @param iArray Optional pre-allocated array for the result
     * @return An array containing the pixel data
     * @see Raster#getPixels(int, int, int, int, int[])
     */
    int[] getPixels(int x, int y, int w, int h, int[] iArray);

    /**
     * Gets pixel data for a rectangular region as a float array.
     *
     * @param x The x-coordinate of the region
     * @param y The y-coordinate of the region
     * @param w The width of the region
     * @param h The height of the region
     * @param fArray Optional pre-allocated array for the result
     * @return An array containing the pixel data
     * @see Raster#getPixels(int, int, int, int, float[])
     */
    float[] getPixels(int x, int y, int w, int h, float[] fArray);

    /**
     * Gets pixel data for a rectangular region as a double array.
     *
     * @param x The x-coordinate of the region
     * @param y The y-coordinate of the region
     * @param w The width of the region
     * @param h The height of the region
     * @param dArray Optional pre-allocated array for the result
     * @return An array containing the pixel data
     * @see Raster#getPixels(int, int, int, int, double[])
     */
    double[] getPixels(int x, int y, int w, int h, double[] dArray);

    /**
     * Sets pixel data from an int array at the specified location.
     *
     * @param x The x-coordinate
     * @param y The y-coordinate
     * @param iArray The pixel data
     * @see WritableRaster#setPixel(int, int, int[])
     */
    void setPixel(int x, int y, int[] iArray);

    /**
     * Sets pixel data from a float array at the specified location.
     *
     * @param x The x-coordinate
     * @param y The y-coordinate
     * @param fArray The pixel data
     * @see WritableRaster#setPixel(int, int, float[])
     */
    void setPixel(int x, int y, float[] fArray);

    /**
     * Sets pixel data from a double array at the specified location.
     *
     * @param x The x-coordinate
     * @param y The y-coordinate
     * @param dArray The pixel data
     * @see WritableRaster#setPixel(int, int, double[])
     */
    void setPixel(int x, int y, double[] dArray);

    /**
     * Sets pixel data from an int array for a rectangular region.
     *
     * @param x The x-coordinate of the region
     * @param y The y-coordinate of the region
     * @param w The width of the region
     * @param h The height of the region
     * @param iArray The pixel data
     * @see WritableRaster#setPixels(int, int, int, int, int[])
     */
    void setPixels(int x, int y, int w, int h, int[] iArray);

    /**
     * Sets pixel data from a float array for a rectangular region.
     *
     * @param x The x-coordinate of the region
     * @param y The y-coordinate of the region
     * @param w The width of the region
     * @param h The height of the region
     * @param fArray The pixel data
     * @see WritableRaster#setPixels(int, int, int, int, float[])
     */
    void setPixels(int x, int y, int w, int h, float[] fArray);

    /**
     * Sets pixel data from a double array for a rectangular region.
     *
     * @param x The x-coordinate of the region
     * @param y The y-coordinate of the region
     * @param w The width of the region
     * @param h The height of the region
     * @param dArray The pixel data
     * @see WritableRaster#setPixels(int, int, int, int, double[])
     */
    void setPixels(int x, int y, int w, int h, double[] dArray);

    // ---------- SAMPLES ---------------

    /**
     * Gets a sample value for a specific band at the specified coordinates.
     *
     * @param x The x-coordinate
     * @param y The y-coordinate
     * @param b The band index (0=red, 1=green, 2=blue, 3=alpha)
     * @return The sample value
     * @see Raster#getSample(int, int, int)
     */
    int getSample(int x, int y, int b);

    /**
     * Gets a sample value as a float for a specific band at the specified coordinates.
     *
     * @param x The x-coordinate
     * @param y The y-coordinate
     * @param b The band index
     * @return The sample value
     * @see Raster#getSampleFloat(int, int, int)
     */
    float getSampleFloat(int x, int y, int b);

    /**
     * Gets a sample value as a double for a specific band at the specified coordinates.
     *
     * @param x The x-coordinate
     * @param y The y-coordinate
     * @param b The band index
     * @return The sample value
     * @see Raster#getSampleDouble(int, int, int)
     */
    double getSampleDouble(int x, int y, int b);

    /**
     * Gets sample values for a specific band over a rectangular region.
     *
     * @param x The x-coordinate of the region
     * @param y The y-coordinate of the region
     * @param w The width of the region
     * @param h The height of the region
     * @param b The band index
     * @param iArray Optional pre-allocated array for the result
     * @return An array containing the sample values
     * @see Raster#getSamples(int, int, int, int, int, int[])
     */
    int[] getSamples(int x, int y, int w, int h, int b, int[] iArray);

    /**
     * Gets sample values as floats for a specific band over a rectangular region.
     *
     * @param x The x-coordinate of the region
     * @param y The y-coordinate of the region
     * @param w The width of the region
     * @param h The height of the region
     * @param b The band index
     * @param fArray Optional pre-allocated array for the result
     * @return An array containing the sample values
     * @see Raster#getSamples(int, int, int, int, int, float[])
     */
    float[] getSamples(int x, int y, int w, int h, int b, float[] fArray);

    /**
     * Gets sample values as doubles for a specific band over a rectangular region.
     *
     * @param x The x-coordinate of the region
     * @param y The y-coordinate of the region
     * @param w The width of the region
     * @param h The height of the region
     * @param b The band index
     * @param dArray Optional pre-allocated array for the result
     * @return An array containing the sample values
     * @see Raster#getSamples(int, int, int, int, int, double[])
     */
    double[] getSamples(int x, int y, int w, int h, int b, double[] dArray);

    /**
     * Sets a sample value for a specific band at the specified coordinates.
     *
     * @param x The x-coordinate
     * @param y The y-coordinate
     * @param b The band index
     * @param s The sample value
     * @see WritableRaster#setSample(int, int, int, int)
     */
    void setSample(int x, int y, int b, int s);

    /**
     * Sets a sample value from a float for a specific band at the specified coordinates.
     *
     * @param x The x-coordinate
     * @param y The y-coordinate
     * @param b The band index
     * @param s The sample value
     * @see WritableRaster#setSample(int, int, int, float)
     */
    void setSample(int x, int y, int b, float s);

    /**
     * Sets a sample value from a double for a specific band at the specified coordinates.
     *
     * @param x The x-coordinate
     * @param y The y-coordinate
     * @param b The band index
     * @param s The sample value
     * @see WritableRaster#setSample(int, int, int, double)
     */
    void setSample(int x, int y, int b, double s);

    /**
     * Sets sample values from an int array for a specific band over a rectangular region.
     *
     * @param x The x-coordinate of the region
     * @param y The y-coordinate of the region
     * @param w The width of the region
     * @param h The height of the region
     * @param b The band index
     * @param iArray The sample values
     * @see WritableRaster#setSamples(int, int, int, int, int, int[])
     */
    void setSamples(int x, int y, int w, int h, int b, int[] iArray);

    /**
     * Sets sample values from a float array for a specific band over a rectangular region.
     *
     * @param x The x-coordinate of the region
     * @param y The y-coordinate of the region
     * @param w The width of the region
     * @param h The height of the region
     * @param b The band index
     * @param fArray The sample values
     * @see WritableRaster#setSamples(int, int, int, int, int, float[])
     */
    void setSamples(int x, int y, int w, int h, int b, float[] fArray);

    /**
     * Sets sample values from a double array for a specific band over a rectangular region.
     *
     * @param x The x-coordinate of the region
     * @param y The y-coordinate of the region
     * @param w The width of the region
     * @param h The height of the region
     * @param b The band index
     * @param dArray The sample values
     * @see WritableRaster#setSamples(int, int, int, int, int, double[])
     */
    void setSamples(int x, int y, int w, int h, int b, double[] dArray);

    /**
     * Adapter class that wraps a standard WritableRaster to implement AbstractWritableRaster.
     *
     * <p>This allows standard WritableRaster instances to be used polymorphically
     * through the AbstractWritableRaster interface.
     */
    class WritableRasterAdapter implements AbstractWritableRaster {
        private final WritableRaster wrapped;

        /**
         * Private constructor. Use {@link #toWritableRasterAdapter(WritableRaster)} to create instances.
         * 
         * @param raster The WritableRaster to wrap
         */
        private WritableRasterAdapter(WritableRaster raster) {
            this.wrapped = raster;
        }

        /**
         * Creates a WritableRasterAdapter for the given WritableRaster.
         * 
         * @param raster The WritableRaster to wrap, or null
         * @return A new WritableRasterAdapter, or null if input is null
         */
        public static WritableRasterAdapter toWritableRasterAdapter(WritableRaster raster){
            if (raster == null) return null;
            return new WritableRasterAdapter(raster);
        }

        /// @see WritableRaster#getNumDataElements()
        @Override
        public int getNumDataElements() {
            return wrapped.getNumDataElements();
        }

        /// @see WritableRaster#getNumBands()
        @Override
        public int getNumBands() {
            return wrapped.getNumBands();
        }

        /// @see WritableRaster#getHeight()
        @Override
        public int getHeight() {
            return wrapped.getHeight();
        }

        /// @see WritableRaster#getWidth()
        @Override
        public int getWidth() {
            return wrapped.getWidth();
        }

        /// @see WritableRaster#getMinY()
        @Override
        public int getMinY() {
            return wrapped.getMinY();
        }

        /// @see WritableRaster#getMinX()
        @Override
        public int getMinX() {
            return wrapped.getMinX();
        }

        /// @see WritableRaster#getTransferType()
        @Override
        public int getTransferType() {
            return wrapped.getTransferType();
        }

        /// @see WritableRaster#getBounds()
        @Override
        public Rectangle getBounds() {
            return wrapped.getBounds();
        }

        /// @see WritableRaster#setDataElements(int, int, Object)
        @Override
        public void setDataElements(int x, int y, Object inData) {
            wrapped.setDataElements(x, y, inData);
        }

        /// @see WritableRaster#setDataElements(int, int, Raster)
        @Override
        public void setDataElements(int x, int y, Raster inRaster) {
            wrapped.setDataElements(x, y, inRaster);
        }

        /// @see WritableRaster#setDataElements(int, int, int, int, Object)
        @Override
        public void setDataElements(int x, int y, int w, int h, Object inData) {
            wrapped.setDataElements(x, y, w, h, inData);
        }

        /// @see WritableRaster#setRect(Raster)
        @Override
        public void setRect(Raster srcRaster) {
            wrapped.setRect(srcRaster);
        }

        /// @see WritableRaster#setRect(int, int, Raster)
        @Override
        public void setRect(int dx, int dy, Raster srcRaster) {
            wrapped.setRect(dx, dy, srcRaster);
        }

        /// @see WritableRaster#getPixel(int, int, int[])
        @Override
        public int[] getPixel(int x, int y, int[] iArray) {
            return wrapped.getPixel(x, y, iArray);
        }

        /// @see WritableRaster#getPixel(int, int, float[])
        @Override
        public float[] getPixel(int x, int y, float[] fArray) {
            return wrapped.getPixel(x, y, fArray);
        }

        /// @see WritableRaster#getPixel(int, int, double[])
        @Override
        public double[] getPixel(int x, int y, double[] dArray) {
            return wrapped.getPixel(x, y, dArray);
        }

        /// @see WritableRaster#getPixels(int, int, int, int, int[])
        @Override
        public int[] getPixels(int x, int y, int w, int h, int[] iArray) {
            return wrapped.getPixels(x, y, w, h, iArray);
        }

        /// @see WritableRaster#getPixels(int, int, int, int, float[])
        @Override
        public float[] getPixels(int x, int y, int w, int h, float[] fArray) {
            return wrapped.getPixels(x, y, w, h, fArray);
        }

        /// @see WritableRaster#getPixels(int, int, int, int, double[])
        @Override
        public double[] getPixels(int x, int y, int w, int h, double[] dArray) {
            return wrapped.getPixels(x, y, w, h, dArray);
        }

        /// @see WritableRaster#setPixel(int, int, int[])
        @Override
        public void setPixel(int x, int y, int[] iArray) {
            wrapped.setPixel(x, y, iArray);
        }

        /// @see WritableRaster#setPixel(int, int, float[])
        @Override
        public void setPixel(int x, int y, float[] fArray) {
            wrapped.setPixel(x, y, fArray);
        }

        /// @see WritableRaster#setPixel(int, int, double[])
        @Override
        public void setPixel(int x, int y, double[] dArray) {
            wrapped.setPixel(x, y, dArray);
        }

        /// @see WritableRaster#setPixels(int, int, int, int, int[])
        @Override
        public void setPixels(int x, int y, int w, int h, int[] iArray) {
            wrapped.setPixels(x, y, w, h, iArray);
        }

        /// @see WritableRaster#setPixels(int, int, int, int, float[])
        @Override
        public void setPixels(int x, int y, int w, int h, float[] fArray) {
            wrapped.setPixels(x, y, w, h, fArray);
        }

        /// @see WritableRaster#setPixels(int, int, int, int, double[])
        @Override
        public void setPixels(int x, int y, int w, int h, double[] dArray) {
            wrapped.setPixels(x, y, w, h, dArray);
        }

        /// @see WritableRaster#getSample(int, int, int)
        @Override
        public int getSample(int x, int y, int b) {
            return wrapped.getSample(x, y, b);
        }

        /// @see WritableRaster#getSampleFloat(int, int, int)
        @Override
        public float getSampleFloat(int x, int y, int b) {
            return wrapped.getSampleFloat(x, y, b);
        }

        /// @see WritableRaster#getSampleDouble(int, int, int)
        @Override
        public double getSampleDouble(int x, int y, int b) {
            return wrapped.getSampleDouble(x, y, b);
        }

        /// @see WritableRaster#getSamples(int, int, int, int, int, double[])
        @Override
        public int[] getSamples(int x, int y, int w, int h, int b, int[] iArray) {
            return wrapped.getSamples(x, y, w, h, b, iArray);
        }

        /// @see WritableRaster#getSamples(int, int, int, int, int, double[])
        @Override
        public float[] getSamples(int x, int y, int w, int h, int b, float[] fArray) {
            return wrapped.getSamples(x, y, w, h, b, fArray);
        }

        /// @see WritableRaster#getSamples(int, int, int, int, int, double[])
        @Override
        public double[] getSamples(int x, int y, int w, int h, int b, double[] dArray) {
            return wrapped.getSamples(x, y, w, h, b, dArray);
        }

        /// @see WritableRaster#setSample(int, int, int, int)
        @Override
        public void setSample(int x, int y, int b, int s) {
            wrapped.setSample(x, y, b, s);
        }

        /// @see WritableRaster#setSample(int, int, int, float)
        @Override
        public void setSample(int x, int y, int b, float s) {
            wrapped.setSample(x, y, b, s);
        }

        /// @see WritableRaster#setSample(int, int, int, double)
        @Override
        public void setSample(int x, int y, int b, double s) {
            wrapped.setSample(x, y, b, s);
        }

        /// @see WritableRaster#setSamples(int, int, int, int, int, int[])
        @Override
        public void setSamples(int x, int y, int w, int h, int b, int[] iArray) {
            wrapped.setSamples(x, y, w, h, b, iArray);
        }

        /// @see WritableRaster#setSamples(int, int, int, int, int, float[])
        @Override
        public void setSamples(int x, int y, int w, int h, int b, float[] fArray) {
            wrapped.setSamples(x, y, w, h, b, fArray);
        }

        /// @see WritableRaster#setSamples(int, int, int, int, int, double[])
        @Override
        public void setSamples(int x, int y, int w, int h, int b, double[] dArray) {
            wrapped.setSamples(x, y, w, h, b, dArray);
        }
    }
}
