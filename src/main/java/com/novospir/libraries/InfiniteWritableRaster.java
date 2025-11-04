package com.novospir.libraries;

import java.awt.*;
import java.awt.image.*;
import java.util.List;

/**
 * A WritableRaster implementation that provides low-level pixel access for infinite space.
 * 
 * <p>InfiniteWritableRaster provides direct pixel-level read/write access to an
 * {@link InfiniteBufferedImage}. Unlike the higher-level Graphics2D drawing API,
 * this class allows you to manipulate individual pixels or pixel regions efficiently.
 * 
 * <h3>Purpose:</h3>
 * <p>While {@link QuadGraphics2D} provides shape-based drawing operations, InfiniteWritableRaster
 * provides direct pixel manipulation. It implements the {@link WritableRaster} interface
 * to allow pixel-level operations that work seamlessly across the quadtree tile structure.
 * 
 * <h3>Use Cases:</h3>
 * <ul>
 *   <li><b>Direct pixel manipulation</b> - Get or set individual pixel values
 *   <li><b>Batch pixel operations</b> - Read/write rectangular regions efficiently
 *   <li><b>Low-level image processing</b> - Algorithms that need direct raster access
 *   <li><b>Data transfer</b> - Copying data between images at the pixel level
 *   <li><b>Custom compositing</b> - Manual alpha blending or color space conversions
 * </ul>
 * 
 * @see InfiniteBufferedImage
 * @see AbstractWritableRaster
 * @see WritableRaster
 * @author Novospir, Adam
 * @since 1.0
 */
class InfiniteWritableRaster implements AbstractWritableRaster {

    private final InfiniteBufferedImage image;
    private final int bands;

    // todo: move band calculation to InfiniteBufferedImage.getType()
    protected InfiniteWritableRaster(InfiniteBufferedImage image) {
        this.image = image;
        this.bands = new BufferedImage(1, 1, image.getType()).getRaster().getNumBands();
    }

    /* ------ UTILITY ------ */

    // For single-pixel operations
    private Raster getLeafRaster(int x, int y) {
        QuadNode leaf = image.findOrCreateLeaf(x, y); // create if missing
        return leaf.image.getRaster();
    }

    private int[] globalToLocal(int x, int y, QuadNode leaf) {
        return new int[]{x - leaf.x, y - leaf.y};
    }

    @Override
    public Rectangle getBounds() {
        return image.getLogicalBounds();
    }

    @Override @Deprecated
    public void setDataElements(int x, int y, Object inData) {
        throw new UnsupportedOperationException("Todo: Implement function");
    }

    @Override @Deprecated
    public void setDataElements(int x, int y, Raster inRaster) {
        throw new UnsupportedOperationException("Todo: Implement function");
    }

    @Override @Deprecated
    public void setDataElements(int x, int y, int w, int h, Object inData) {
        throw new UnsupportedOperationException("Todo: Implement function");
    }

    @Override @Deprecated
    public void setRect(Raster srcRaster) {
        throw new UnsupportedOperationException("Todo: Implement function");
    }

    @Override @Deprecated
    public void setRect(int dx, int dy, Raster srcRaster) {
        throw new UnsupportedOperationException("Todo: Implement function");
    }

    // ---------- PIXELS ---------------
    /// @see java.awt.image.Raster#getPixel(int, int, int[])
    @Override public int[] getPixel(int x, int y, int[] iArray) {
        if (iArray == null) iArray = new int[bands];
        else if (iArray.length < bands) throw new ArrayIndexOutOfBoundsException(
                String.format("Allocated array was too small [%,d] < [%,d]", iArray.length, bands)
        );
        QuadNode leaf = image.findLeaf(x, y);
        if(leaf == null) throw new ArrayIndexOutOfBoundsException();
        int[] local = globalToLocal(x, y, leaf);
        return leaf.image.getRaster().getPixel(local[0], local[1], iArray);
    }

    /// @see java.awt.image.Raster#getPixel(int, int, float[])
    @Override public float[] getPixel(int x, int y, float[] fArray) {
        if (fArray == null) fArray = new float[bands];
        else if (fArray.length < bands) throw new ArrayIndexOutOfBoundsException(
                String.format("Allocated array was too small [%,d] < [%,d]", fArray.length, bands)
        );
        QuadNode leaf = image.findLeaf(x, y);
        if(leaf == null) throw new ArrayIndexOutOfBoundsException();
        int[] local = globalToLocal(x, y, leaf);
        return leaf.image.getRaster().getPixel(local[0], local[1], fArray);
    }

    /// @see java.awt.image.Raster#getPixel(int, int, double[])
    @Override public double[] getPixel(int x, int y, double[] dArray) {
        if (dArray == null) dArray = new double[bands];
        else if (dArray.length < bands) throw new ArrayIndexOutOfBoundsException(
                String.format("Allocated array was too small [%,d] < [%,d]", dArray.length, bands)
        );
        QuadNode leaf = image.findLeaf(x, y);
        if(leaf == null) throw new ArrayIndexOutOfBoundsException();
        int[] local = globalToLocal(x, y, leaf);
        return leaf.image.getRaster().getPixel(local[0], local[1], dArray);
    }

    /// @see java.awt.image.Raster#getPixels(int, int, int, int, int[])
    @Override public int[] getPixels(int x, int y, int w, int h, int[] iArray) {
        if (iArray == null) iArray = new int[w * h * bands];
        else if (iArray.length <  w * h * bands) throw new ArrayIndexOutOfBoundsException(
                String.format("Allocated array was too small [%,d] < [%,d]", iArray.length, (w * h * bands))
        );

        // Iterate over tiles overlapping the requested rect
        List<QuadNode> tiles = image.findLeaves(x, y, w, h, false);
        if (tiles == null || tiles.isEmpty()) return iArray;

        for (QuadNode node : tiles) {
            if (node == null || node.image == null) continue;

            final Raster tileRaster = node.image.getRaster();
            final DataBuffer db = tileRaster.getDataBuffer();
            final DataBufferDecoder decoder = new DataBufferDecoder(tileRaster.getSampleModel(), db);

            final int tileX = node.x, tileY = node.y;
            final int tileW = node.image.getWidth(), tileH = node.image.getHeight();

            final int interX1 = Math.max(x, tileX);
            final int interY1 = Math.max(y, tileY);
            final int interX2 = Math.min(x + w, tileX + tileW);
            final int interY2 = Math.min(y + h, tileY + tileH);
            final int interW = interX2 - interX1;
            final int interH = interY2 - interY1;
            if (interW <= 0 || interH <= 0) continue;

            final int localX = interX1 - tileX;
            final int localY = interY1 - tileY;
            final int dstX = interX1 - x;
            final int dstY = interY1 - y;

            // Get direct access to data array based on DataBuffer type
            final int[] data = DataBufferDecoder.getDataArray(db);

            // Calculate base offset for this tile region
            final int rasterMinX = tileRaster.getMinX();
            final int rasterMinY = tileRaster.getMinY();
            final int dataBufferOffset = db.getOffset();
            final int baseOffset = dataBufferOffset + (localY + rasterMinY) * decoder.scanlineStride + (localX + rasterMinX) * decoder.pixelStride;

            // Copy pixels using decoder information
            for (int row = 0; row < interH; row++) {
                final int srcRowStart = baseOffset + row * decoder.scanlineStride;
                final int dstRowStart = ((dstY + row) * w + dstX) * bands;

                for (int col = 0; col < interW; col++) {
                    final int srcPixelStart = srcRowStart + col * decoder.pixelStride;
                    final int dstPixelStart = dstRowStart + col * bands;

                    if (decoder.needsBitUnpacking()) {
                        // Handle packed pixel formats
                        final int packedPixel = data[srcPixelStart + decoder.bandOffsets[0]];
                        for (int b = 0; b < Math.min(bands, decoder.numBands); b++) {
                            iArray[dstPixelStart + b] = decoder.unpackBand(packedPixel, b);
                        }
                    } else {
                        // Handle component pixel formats
                        for (int b = 0; b < Math.min(bands, decoder.numBands); b++) {
                            iArray[dstPixelStart + b] = data[srcPixelStart + decoder.bandOffsets[b]];
                        }
                    }

                    // Fill remaining bands with 0 if needed
                    for (int b = decoder.numBands; b < bands; b++) {
                        iArray[dstPixelStart + b] = 0;
                    }
                }
            }
        }

        return iArray;
    }

    /// @see java.awt.image.Raster#getPixels(int, int, int, int, float[])
    @Override
    public float[] getPixels(int x, int y, int w, int h, float[] fArray) {
        if (fArray == null) fArray = new float[w * h * bands];
        else if (fArray.length <  w * h * bands) throw new ArrayIndexOutOfBoundsException(
                String.format("Allocated array was too small [%,d] < [%,d]", fArray.length, (w * h * bands))
        );

        // Iterate over tiles overlapping the requested rect
        List<QuadNode> tiles = image.findLeaves(x, y, w, h, false);
        if (tiles == null || tiles.isEmpty()) return fArray;

        for (QuadNode node : tiles) {
            if (node == null || node.image == null) continue;

            final Raster tileRaster = node.image.getRaster();
            final DataBuffer db = tileRaster.getDataBuffer();
            final DataBufferDecoder decoder = new DataBufferDecoder(tileRaster.getSampleModel(), db);

            final int tileX = node.x, tileY = node.y;
            final int tileW = node.image.getWidth(), tileH = node.image.getHeight();

            final int interX1 = Math.max(x, tileX);
            final int interY1 = Math.max(y, tileY);
            final int interX2 = Math.min(x + w, tileX + tileW);
            final int interY2 = Math.min(y + h, tileY + tileH);
            final int interW = interX2 - interX1;
            final int interH = interY2 - interY1;
            if (interW <= 0 || interH <= 0) continue;

            final int localX = interX1 - tileX;
            final int localY = interY1 - tileY;
            final int dstX = interX1 - x;
            final int dstY = interY1 - y;

            // Get direct access to data array based on DataBuffer type
            final float[] data = DataBufferDecoder.getDataArrayFloat(db);

            // Calculate base offset for this tile region
            final int rasterMinX = tileRaster.getMinX();
            final int rasterMinY = tileRaster.getMinY();
            final int dataBufferOffset = db.getOffset();
            final int baseOffset = dataBufferOffset + (localY + rasterMinY) * decoder.scanlineStride + (localX + rasterMinX) * decoder.pixelStride;

            // Copy pixels using decoder information
            for (int row = 0; row < interH; row++) {
                final int srcRowStart = baseOffset + row * decoder.scanlineStride;
                final int dstRowStart = ((dstY + row) * w + dstX) * bands;

                for (int col = 0; col < interW; col++) {
                    final int srcPixelStart = srcRowStart + col * decoder.pixelStride;
                    final int dstPixelStart = dstRowStart + col * bands;

                    for (int b = 0; b < Math.min(bands, decoder.numBands); b++) {
                        fArray[dstPixelStart + b] = data[srcPixelStart + decoder.bandOffsets[b]];
                    }

                    // Fill remaining bands with 0 if needed
                    for (int b = decoder.numBands; b < bands; b++) {
                        fArray[dstPixelStart + b] = 0;
                    }
                }
            }
        }

        return fArray;
    }

    /// @see java.awt.image.Raster#getPixels(int, int, int, int, double[])
    @Override
    public double[] getPixels(int x, int y, int w, int h, double[] dArray) {
        if (dArray == null) dArray = new double[w * h * bands];
        else if (dArray.length <  w * h * bands) throw new ArrayIndexOutOfBoundsException(
                String.format("Allocated array was too small [%,d] < [%,d]", dArray.length, (w * h * bands))
        );

        // Iterate over tiles overlapping the requested rect
        List<QuadNode> tiles = image.findLeaves(x, y, w, h, false);
        if (tiles == null || tiles.isEmpty()) return dArray;

        for (QuadNode node : tiles) {
            if (node == null || node.image == null) continue;

            final Raster tileRaster = node.image.getRaster();
            final DataBuffer db = tileRaster.getDataBuffer();
            final DataBufferDecoder decoder = new DataBufferDecoder(tileRaster.getSampleModel(), db);

            final int tileX = node.x, tileY = node.y;
            final int tileW = node.image.getWidth(), tileH = node.image.getHeight();

            final int interX1 = Math.max(x, tileX);
            final int interY1 = Math.max(y, tileY);
            final int interX2 = Math.min(x + w, tileX + tileW);
            final int interY2 = Math.min(y + h, tileY + tileH);
            final int interW = interX2 - interX1;
            final int interH = interY2 - interY1;
            if (interW <= 0 || interH <= 0) continue;

            final int localX = interX1 - tileX;
            final int localY = interY1 - tileY;
            final int dstX = interX1 - x;
            final int dstY = interY1 - y;

            // Get direct access to data array based on DataBuffer type
            final double[] data = DataBufferDecoder.getDataArrayDouble(db);

            // Calculate base offset for this tile region
            final int rasterMinX = tileRaster.getMinX();
            final int rasterMinY = tileRaster.getMinY();
            final int dataBufferOffset = db.getOffset();
            final int baseOffset = dataBufferOffset + (localY + rasterMinY) * decoder.scanlineStride + (localX + rasterMinX) * decoder.pixelStride;

            // Copy pixels using decoder information
            for (int row = 0; row < interH; row++) {
                final int srcRowStart = baseOffset + row * decoder.scanlineStride;
                final int dstRowStart = ((dstY + row) * w + dstX) * bands;

                for (int col = 0; col < interW; col++) {
                    final int srcPixelStart = srcRowStart + col * decoder.pixelStride;
                    final int dstPixelStart = dstRowStart + col * bands;

                    for (int b = 0; b < Math.min(bands, decoder.numBands); b++) {
                        dArray[dstPixelStart + b] = data[srcPixelStart + decoder.bandOffsets[b]];
                    }

                    // Fill remaining bands with 0 if needed
                    for (int b = decoder.numBands; b < bands; b++) {
                        dArray[dstPixelStart + b] = 0;
                    }
                }
            }
        }

        return dArray;
    }

    /// @see java.awt.image.WritableRaster#setPixel(int, int, int[])
    @Override public void setPixel(int x, int y, int[] iArray) {
        if (iArray == null) throw new NullPointerException("Provided array-data is null");
        else if (iArray.length < bands) throw new ArrayIndexOutOfBoundsException(
                String.format("Allocated array was too small [%,d] < [%,d]", iArray.length, bands)
        );
        QuadNode leaf = this.image.findOrCreateLeaf(x, y);
        leaf.image.getRaster().setPixel(x - leaf.x, y - leaf.y, iArray);
        this.image.markBoundsDirty();
    }

    /// @see java.awt.image.WritableRaster#setPixel(int, int, float[])
    @Override public void setPixel(int x, int y, float[] fArray) {
        if (fArray == null) throw new NullPointerException("Provided array-data is null");
        else if (fArray.length < bands) throw new ArrayIndexOutOfBoundsException(
                String.format("Allocated array was too small [%,d] < [%,d]", fArray.length, bands)
        );
        QuadNode leaf = this.image.findOrCreateLeaf(x, y);
        leaf.image.getRaster().setPixel(x - leaf.x, y - leaf.y, fArray);
        this.image.markBoundsDirty();
    }

    /// @see java.awt.image.WritableRaster#setPixel(int, int, double[])
    @Override public void setPixel(int x, int y, double[] dArray) {
        if (dArray == null) throw new NullPointerException("Provided array-data is null");
        else if (dArray.length < bands) throw new ArrayIndexOutOfBoundsException(
                String.format("Allocated array was too small [%,d] < [%,d]", dArray.length, bands)
        );
        QuadNode leaf = this.image.findOrCreateLeaf(x, y);
        leaf.image.getRaster().setPixel(x - leaf.x, y - leaf.y, dArray);
        this.image.markBoundsDirty();
    }

    /// @see java.awt.image.WritableRaster#setPixels(int, int, int, int, int[])
    @Override
    public void setPixels(int x, int y, int w, int h, int[] iArray) {
        if (iArray == null) return;
        if (w <= 0 || h <= 0) return;

        // Validate array size
        int expectedSize = w * h * bands;
        if (iArray.length < expectedSize) {
            throw new ArrayIndexOutOfBoundsException(
                    String.format("Array too small: %d < %d", iArray.length, expectedSize));
        }

        // Find all leaves that intersect the region
        List<QuadNode> tiles = image.findLeaves(x, y, w, h, true);

        for (QuadNode node : tiles) {
            if (node == null || node.image == null) continue;

            // Calculate intersection between requested region and tile
            int tileX = node.x;
            int tileY = node.y;
            int tileWidth = node.size;
            int tileHeight = node.size;

            int interX1 = Math.max(x, tileX);
            int interY1 = Math.max(y, tileY);
            int interX2 = Math.min(x + w, tileX + tileWidth);
            int interY2 = Math.min(y + h, tileY + tileHeight);

            int interW = interX2 - interX1;
            int interH = interY2 - interY1;

            if (interW <= 0 || interH <= 0) continue;

            // Convert to tile-local coordinates
            int localX = interX1 - tileX;
            int localY = interY1 - tileY;
            int srcX = interX1 - x;
            int srcY = interY1 - y;

            // Get the tile's writable raster
            WritableRaster tileRaster = node.image.getRaster();

            // Copy pixels from source array to tile
            for (int row = 0; row < interH; row++) {
                int srcOffset = ((srcY + row) * w + srcX) * bands;
                int dstOffset = ((localY + row) * tileWidth + localX) * bands;

                System.arraycopy(
                        iArray, srcOffset,
                        ((DataBufferInt) tileRaster.getDataBuffer()).getData(), dstOffset,
                        interW * bands
                );
            }
        }
    }

    /// @see java.awt.image.WritableRaster#setPixels(int, int, int, int, float[])
    @Override @Deprecated
    public void setPixels(int x, int y, int w, int h, float[] fArray) {
        throw new UnsupportedOperationException("Support this after integer is implemented, properly.");
    }

    /// @see java.awt.image.WritableRaster#setPixels(int, int, int, int, double[])
    @Override @Deprecated
    public void setPixels(int x, int y, int w, int h, double[] dArray) {
        throw new UnsupportedOperationException("Support this after integer is implemented, properly.");
    }

    // ---------- SAMPLES ---------------

    /// @see java.awt.image.WritableRaster#getSample(int, int, int)
    @Override
    public int getSample(int x, int y, int b) {
        // Bands = 0 = Red, 1 = Green, 2 = Blue, 3 = Alpha
        QuadNode leaf = image.findLeaf(x, y);
        int[] local = globalToLocal(x, y, leaf);
        return leaf.image.getRaster().getSample(local[0], local[1], b);
    }

    /// @see java.awt.image.WritableRaster#getSampleFloat(int, int, int)
    @Override
    public float getSampleFloat(int x, int y, int b) {
        QuadNode leaf = image.findLeaf(x, y);
        int[] local = globalToLocal(x, y, leaf);
        return leaf.image.getRaster().getSampleFloat(local[0], local[1], b);
    }

    /// @see java.awt.image.WritableRaster#getSampleDouble(int, int, int)
    @Override
    public double getSampleDouble(int x, int y, int b) {
        QuadNode leaf = image.findLeaf(x, y);
        int[] local = globalToLocal(x, y, leaf);
        return leaf.image.getRaster().getSampleDouble(local[0], local[1], b);
    }

    /// @see java.awt.image.WritableRaster#getSamples(int, int, int, int, int, int[])
    @Override
    public int[] getSamples(int x, int y, int w, int h, int b, int[] iArray) {
        if (w <= 0 || h <= 0) return new int[0];
        if (b < 0 || b >= bands) {
            throw new ArrayIndexOutOfBoundsException("Band index out of bounds: " + b);
        }

        if (iArray == null) {
            iArray = new int[w * h];
        } else if (iArray.length < w * h) {
            throw new ArrayIndexOutOfBoundsException(
                    String.format("Array too small: %d < %d", iArray.length, w * h));
        }

        // Find all leaves that intersect the region
        List<QuadNode> overlappingTiles = image.findLeaves(x, y, w, h, false);

        for (QuadNode node : overlappingTiles) {
            if (node == null || node.image == null) continue;

            // Calculate intersection between requested region and tile
            int tileX = node.x;
            int tileY = node.y;
            int tileWidth = node.size;
            int tileHeight = node.size;

            int interX1 = Math.max(x, tileX);
            int interY1 = Math.max(y, tileY);
            int interX2 = Math.min(x + w, tileX + tileWidth);
            int interY2 = Math.min(y + h, tileY + tileHeight);

            int interW = interX2 - interX1;
            int interH = interY2 - interY1;

            if (interW <= 0 || interH <= 0) continue;

            // Convert to tile-local coordinates
            int localX = interX1 - tileX;
            int localY = interY1 - tileY;
            int dstX = interX1 - x;
            int dstY = interY1 - y;

            // Get the tile's raster
            Raster tileRaster = node.image.getRaster();

            // Extract samples for the specific band
            for (int row = 0; row < interH; row++) {
                for (int col = 0; col < interW; col++) {
                    int srcX = localX + col;
                    int srcY = localY + row;
                    int dstIndex = (dstY + row) * w + (dstX + col);

                    iArray[dstIndex] = tileRaster.getSample(srcX, srcY, b);
                }
            }
        }

        return iArray;
    }

    /// @see java.awt.image.WritableRaster#getSamples(int, int, int, int, int, float[])
    @Override @Deprecated
    public float[] getSamples(int x, int y, int w, int h, int b, float[] fArray) {
        throw new UnsupportedOperationException("Support this after integer is implemented, properly.");
    }

    /// @see java.awt.image.WritableRaster#getSamples(int, int, int, int, int, double[])
    @Override @Deprecated
    public double[] getSamples(int x, int y, int w, int h, int b, double[] dArray) {
        throw new UnsupportedOperationException("Support this after integer is implemented, properly.");
    }

    /// @see java.awt.image.WritableRaster#setSample(int, int, int, int)
    @Override @Deprecated
    public void setSample(int x, int y, int b, int s) {
        throw new UnsupportedOperationException("Todo: Implement function");
    }

    /// @see java.awt.image.WritableRaster#setSample(int, int, int, float)
    @Override @Deprecated
    public void setSample(int x, int y, int b, float s) {
        throw new UnsupportedOperationException("Support this after integer is implemented, properly.");
    }

    /// @see java.awt.image.WritableRaster#setSample(int, int, int, double)
    @Override @Deprecated
    public void setSample(int x, int y, int b, double s) {
        throw new UnsupportedOperationException("Support this after integer is implemented, properly.");
    }

    /// @see java.awt.image.WritableRaster#setSamples(int, int, int, int, int, int[])
    @Override @Deprecated
    public void setSamples(int x, int y, int w, int h, int b, int[] iArray) {
        throw new UnsupportedOperationException("Todo: Implement function");
    }

    /// @see java.awt.image.WritableRaster#setSamples(int, int, int, int, int, float[])
    @Override @Deprecated
    public void setSamples(int x, int y, int w, int h, int b, float[] fArray) {
        throw new UnsupportedOperationException("Support this after integer is implemented, properly.");
    }

    /// @see java.awt.image.WritableRaster#setSamples(int, int, int, int, int, double[])
    @Override @Deprecated
    public void setSamples(int x, int y, int w, int h, int b, double[] dArray) {
        throw new UnsupportedOperationException("Support this after integer is implemented, properly.");
    }

    /// @see Raster#getNumDataElements()
    @Override @Deprecated
    public int getNumDataElements() {
        throw new UnsupportedOperationException("Todo: Implement function");
    }

    /// @see Raster#getTransferType()
    @Override @Deprecated
    public int getTransferType() {
        throw new UnsupportedOperationException("Todo: Implement function");
    }

    /// @see Raster#getNumBands()
    @Override
    public int getNumBands() {
        return bands;
    }

    /// @see Raster#getHeight()
    @Override
    public int getHeight() {
        return image.getLogicalBounds().height;
    }

    /// @see Raster#getWidth()
    @Override
    public int getWidth() {
        return image.getLogicalBounds().width;
    }

    /// @see Raster#getMinY()
    @Override
    public int getMinY() {
        return image.getLogicalBounds().y;
    }

    /// @see Raster#getMinX()
    @Override
    public int getMinX() {
        return image.getLogicalBounds().x;
    }
}
