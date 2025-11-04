package com.novospir.libraries;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Abstraction layer for BufferedImage-like functionality.
 * 
 * <p>AbstractBufferedImage provides a common interface for working with different
 * image storage implementations. It abstracts over the differences between:
 * <ul>
 *   <li>Standard {@link BufferedImage} - Fixed size, monolithic storage
 *   <li>{@link InfiniteBufferedImage} - Infinite size, quadtree-based tiled storage
 * </ul>
 * 
 * <h3>Purpose:</h3>
 * <p>This interface enables:
 * <ul>
 *   <li><b>Polymorphism</b> - Write code that works with either BufferedImage or InfiniteBufferedImage
 *   <li><b>Testing</b> - Easily swap implementations for performance comparison
 *   <li><b>Encapsulation</b> - Hide implementation details behind a common API
 * </ul>
 *
 * @see InfiniteBufferedImage
 * @see BufferedImage
 * @author Novospir, Adam
 * @since 1.0
 */
public interface AbstractBufferedImage {
    /**
     * Gets the ARGB pixel value at the specified coordinates.
     *
     * @param x The x-coordinate of the pixel
     * @param y The y-coordinate of the pixel
     * @return The pixel value in ARGB format, or 0 (transparent black) if coordinates are out of bounds
     * @see BufferedImage#getRGB(int, int)
     */
    int getRGB(int x, int y);

    /**
     * Sets the ARGB pixel value at the specified coordinates.
     *
     * @param x The x-coordinate of the pixel
     * @param y The y-coordinate of the pixel
     * @param rgb The pixel value in ARGB format
     * @see BufferedImage#setRGB(int, int, int)
     */
    void setRGB(int x, int y, int rgb);

    /**
     * Creates a Graphics2D context for drawing on this image.
     *
     * @return A Graphics2D object that can be used to draw on this image
     * @see BufferedImage#createGraphics()
     */
    Graphics2D createGraphics();

    /**
     * Returns the raster for this image, providing direct pixel-level access.
     *
     * @return An AbstractWritableRaster object for low-level pixel manipulation
     * @see BufferedImage#getRaster()
     */
    AbstractWritableRaster getRaster();

    /**
     * Exports a rectangular region of this image as a standard BufferedImage.
     *
     * @param bounds The rectangular region to export
     * @return A BufferedImage containing the specified region's pixel data
     */
    BufferedImage toBufferedImage(Rectangle bounds);

    /**
     * Adapter class that wraps a standard BufferedImage to implement AbstractBufferedImage.
     *
     * <p>This allows standard BufferedImage instances to be used polymorphically
     * through the AbstractBufferedImage interface.
     */
    class BufferedImageAdapter implements AbstractBufferedImage {
        private final BufferedImage bufferedImage;

        /// @see BufferedImage#BufferedImage(int, int, int)
        public BufferedImageAdapter(int width, int height, int imageType){
            this.bufferedImage = new BufferedImage(width, height, imageType);
        }

        /// @see BufferedImage#getRGB(int, int)
        @Override
        public int getRGB(int x, int y) {
            return bufferedImage.getRGB(x, y);
        }

        /// @see BufferedImage#setRGB(int, int, int)
        @Override
        public void setRGB(int x, int y, int rgb) {
            this.bufferedImage.setRGB(x, y, rgb);
        }

        /// @see BufferedImage#createGraphics()
        @Override
        public Graphics2D createGraphics() {
            return this.bufferedImage.createGraphics();
        }

        /// @see BufferedImage#getRaster()
        @Override
        public AbstractWritableRaster getRaster() {
            return AbstractWritableRaster.WritableRasterAdapter.toWritableRasterAdapter(this.bufferedImage.getRaster());
        }

        /**
         * Exports a rectangular region of this image as a standard BufferedImage.
         *
         * @param bounds The rectangular region to export
         * @return A BufferedImage containing the specified region's pixel data
         */
        @Override
        public BufferedImage toBufferedImage(Rectangle bounds) {
            return this.bufferedImage.getSubimage(bounds.x, bounds.y,
                    bounds.width, bounds.height);
        }
    }
}
