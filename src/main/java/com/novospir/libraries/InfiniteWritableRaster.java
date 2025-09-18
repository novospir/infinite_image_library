package com.novospir.libraries;


import java.awt.*;
import java.awt.image.*;
import java.util.List;

/*
This class does the same functions as raster, but not as optimized

 */

class InfiniteWritableRaster implements AbstractWritableRaster {

    // getTransferType is final, and we must use the super
    //

    private final InfiniteBufferedImage image;
    private final int bands;

    protected InfiniteWritableRaster(InfiniteBufferedImage image) {
        this.image = image;
        // probably a better way to do this
        // yeah - when calc getType in InfiniteBufferedImage, calc bands from that
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
        // r.getSampleModel() instanceof SinglePixelPackedSampleModel && r.getDataBuffer() instanceof DataBufferInt

        // todo: use DataBuffer, so we only have to copy data once
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
            final int[] data = getDataArray(db);

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
            /*if (node == null || node.image == null) continue;

            final Raster tileRaster = node.image.getRaster();
            final int tileX = node.x;
            final int tileY = node.y;
            final int tileW = node.image.getWidth();
            final int tileH = node.image.getHeight();

            // Intersection in global coords
            final int interX1 = Math.max(x, tileX);
            final int interY1 = Math.max(y, tileY);
            final int interX2 = Math.min(x + w, tileX + tileW);
            final int interY2 = Math.min(y + h, tileY + tileH);
            final int interW = interX2 - interX1;
            final int interH = interY2 - interY1;
            if (interW <= 0 || interH <= 0) continue;

            // Local coords within the tile, and destination offsets within the request
            final int localX = interX1 - tileX;
            final int localY = interY1 - tileY;
            final int dstX = interX1 - x;
            final int dstY = interY1 - y;

            // Pull the block from the tile using the built-in getPixels
            final int[] block = tileRaster.getPixels(localX, localY, interW, interH, (int[]) null);

            // Copy row-by-row into destination (both arrays are interleaved by bands)
            final int srcRowStride = interW * bands;
            for (int row = 0; row < interH; row++) {
                final int srcOff = row * srcRowStride;
                final int dstOff = ((dstY + row) * w + dstX) * bands;
                System.arraycopy(block, srcOff, iArray, dstOff, srcRowStride);
            }*/
        }

        return iArray;
    }

    private int[] getDataArray(DataBuffer db) {
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

    /// @see java.awt.image.Raster#getPixels(int, int, int, int, float[])
    @Override public float[] getPixels(int x, int y, int w, int h, float[] fArray) {
        throw new UnsupportedOperationException("do this after integer is implemented.");
    }

    /// @see java.awt.image.Raster#getPixels(int, int, int, int, double[])
    @Override public double[] getPixels(int x, int y, int w, int h, double[] dArray) {
        throw new UnsupportedOperationException("do this after integer is implemented.");
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
        throw new UnsupportedOperationException("do this after integer is implemented.");
    }

    /// @see java.awt.image.WritableRaster#setPixel(int, int, double[])
    @Override public void setPixel(int x, int y, double[] dArray) {
        throw new UnsupportedOperationException("do this after integer is implemented.");
    }

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

    @Override
    public void setPixels(int x, int y, int w, int h, float[] fArray) {
        throw new UnsupportedOperationException("do this after integer is implemented.");
    }

    @Override
    public void setPixels(int x, int y, int w, int h, double[] dArray) {
        throw new UnsupportedOperationException("do this after integer is implemented.");
    }

    // ---------- SAMPLES ---------------

    // For getSample - delegate to leaf raster
    @Override
    public int getSample(int x, int y, int b) {
        // band = 0 = Red, 1 = Green, 2 = Blue, 3 = Alpha etc
        QuadNode leaf = image.findLeaf(x, y);
        int[] local = globalToLocal(x, y, leaf);
        return leaf.image.getRaster().getSample(local[0], local[1], b);
    }

    @Override
    public float getSampleFloat(int x, int y, int b) {
        throw new UnsupportedOperationException("do this after integer is implemented.");
    }

    @Override
    public double getSampleDouble(int x, int y, int b) {
        throw new UnsupportedOperationException("do this after integer is implemented.");
    }

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

    @Override
    public float[] getSamples(int x, int y, int w, int h, int b, float[] fArray) {
        throw new UnsupportedOperationException("do this after integer is implemented.");
    }

    @Override
    public double[] getSamples(int x, int y, int w, int h, int b, double[] dArray) {
        throw new UnsupportedOperationException("do this after integer is implemented.");
    }

    @Override @Deprecated
    public void setSample(int x, int y, int b, int s) {
        throw new UnsupportedOperationException("Todo: Implement function");
    }

    @Override
    public void setSample(int x, int y, int b, float s) {
        throw new UnsupportedOperationException("do this after integer is implemented.");
    }

    @Override
    public void setSample(int x, int y, int b, double s) {
        throw new UnsupportedOperationException("do this after integer is implemented.");
    }

    @Override @Deprecated
    public void setSamples(int x, int y, int w, int h, int b, int[] iArray) {

    }

    @Override
    public void setSamples(int x, int y, int w, int h, int b, float[] fArray) {
        throw new UnsupportedOperationException("do this after integer is implemented.");
    }

    @Override
    public void setSamples(int x, int y, int w, int h, int b, double[] dArray) {
        throw new UnsupportedOperationException("do this after integer is implemented.");
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

    @Override
    public int getNumBands() {
        return bands;
    }

    @Override
    public int getHeight() {
        return image.getLogicalBounds().height;
    }

    @Override
    public int getWidth() {
        return image.getLogicalBounds().width;
    }

    @Override
    public int getMinY() {
        return image.getLogicalBounds().y;
    }

    @Override
    public int getMinX() {
        return image.getLogicalBounds().x;
    }

    /*@Override
    public int[] getPixels(int x, int y, int w, int h, int[] iArray) {
        int bands = this.getSampleModel().getNumBands();
        if (iArray == null || iArray.length < w * h * bands) {
            iArray = new int[w * h * bands];
        }


        for each tile contained
            tile.getRaster.getPixels(x, y, w, h, iArray)
            // where x,y,w,h are recalculated within this tile appropriately
            // and, if possible, iArray is an offset array that the getPixels can write directly to -
                // offset so that it's writing to correct place calculated for this tile


        int outIndex = 0;
        for (int j = 0; j < h; j++) {
            int globalY = y + j;
            for (int i = 0; i < w; i++) {
                int globalX = x + i;

                BufferedImage tile = getTileContaining(globalX, globalY);
                Raster r = tile.getRaster().getPix;

                int localX = globalX % tileSize;
                int localY = globalY % tileSize;

                int[] pixel = new int[bands];
                r.getPixel(localX, localY, pixel);

                for (int b = 0; b < bands; b++) {
                    iArray[outIndex++] = pixel[b];
                }
            }
        }

        return iArray;
    }*/
}
