package com.novospir.libraries;

import com.novospir.libraries.config.ConfigLoader;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.awt.image.Raster;

/**
 * An infinite-space BufferedImage implementation using a quadtree-based tiling system.
 * 
 * <p>This class provides a drop-in replacement for {@link BufferedImage} that supports
 * unlimited image dimensions by dynamically allocating memory tiles only where needed.
 * Unlike traditional BufferedImage, which requires fixed dimensions and allocates
 * memory for the entire image upfront, InfiniteBufferedImage creates tiles on-demand
 * as content is added.
 * 
 * <h3>Example Usage:</h3>
 * <pre>{@code
 * // Create an infinite canvas
 * InfiniteBufferedImage canvas = new InfiniteBufferedImage();
 * Graphics2D g = canvas.createGraphics();
 * 
 * // Draw anywhere in the infinite space
 * g.setColor(Color.RED);
 * g.fillRect(-5000, -3000, 1000, 500);
 * g.setColor(Color.BLUE);
 * g.fillOval(10000, 10000, 2000, 2000);
 * 
 * // Export a specific region
 * Rectangle region = new Rectangle(0, 0, 500, 500);
 * BufferedImage output = canvas.toBufferedImage(region);
 * 
 * g.dispose();
 * }</pre>
 * 
 * <h3>Implementation Notes:</h3>
 * <ul>
 *   <li>Tile size is configurable through config.properties
 *   <li>Accessing coordinates that haven't been written to returns transparent pixels
 * </ul>
 * 
 * @see QuadNode
 * @see AbstractBufferedImage
 * @see QuadGraphics2D
 * @author Novospir, Adam
 * @since 1.0
 */
public class InfiniteBufferedImage implements AbstractBufferedImage {

    private final int TILE_SIZE = ConfigLoader.getInstance().getInt("tile.size", 128);
    private QuadNode root;
    private final int type;
    private final InfiniteWritableRaster raster;

    private int minX = Integer.MAX_VALUE;
    private int minY = Integer.MAX_VALUE;
    private int maxX = Integer.MIN_VALUE;
    private int maxY = Integer.MIN_VALUE;
    private boolean boundsValid = false;

    /**
     * Creates a scalable buffered image, tiled space via quadtree
     * </br> Initializes with an origin of (0, 0)
     */
    public InfiniteBufferedImage() {
        this(0, 0);
    }

    /**
     * Creates a scalable buffered image, tiled space via quadtree
     * @param x The x-coordinate for the position of the starting image
     * @param y The y-coordinate for the position of the starting image
     */
    public InfiniteBufferedImage(int x, int y) {
        this.root = new QuadNode(x, y, TILE_SIZE);
        this.type = BufferedImage.TYPE_INT_ARGB;
        this.raster = new InfiniteWritableRaster(this);
    }

    /** Returns the image type. If it is not one of the known types, TYPE_CUSTOM is returned. <br>
     *
     * Currently, InfiniteBufferedImage is hard-coded to be TYPE_INT_ARGB
     * @return the image type of this BufferedImage.
     */
    protected int getType(){
        return type;
    }

    /** Gets the ARGB value at (x,y), or 0 if outside any filled tile */
    public int getRGB(int x, int y) {
        QuadNode node = findLeaf(x, y);
        if (node == null || node.image == null) return 0;
        return node.image.getRGB(x - node.x, y - node.y);
    }

    /** Sets the ARGB value at (x,y), expanding the tree as needed */
    public void setRGB(int x, int y, int argb) {
        QuadNode node = findOrCreateLeaf(x, y);
        node.image.setRGB(x - node.x, y - node.y, argb);
        this.markBoundsDirty();
    }

    /** Returns the full raster across all tiles (for export) */
    public BufferedImage toBufferedImage(Rectangle bounds) {
        BufferedImage out = new BufferedImage(bounds.width, bounds.height, type);
        Graphics g = out.getGraphics();
        paint(g, bounds.x, bounds.y);
        g.dispose();
        return out;
    }

    /**
     * Returns the calculated bounding Rectangle of this InfiniteBufferedImage.
     * @return the calculated bounding box of this InfiniteBufferedImage.
     */
    public Rectangle getLogicalBounds(){
        // ie. if destroy() is called, needs to be able to revert to previous "logical bounds"
        if (!boundsValid) {
            // Only recompute if we've never computed, grown or if we've shrunk
            recomputeBounds();
        }
        int x = Math.min(minX, maxX);
        int y = Math.min(minY, maxY);
        int w = Math.abs(maxX - minX);
        int h = Math.abs(maxY - minY);
        return new Rectangle(x, y, w, h);
    }

    protected void markBoundsDirty() {
        boundsValid = false;
    }

    private void recomputeBounds() {
        minX = Integer.MAX_VALUE;
        minY = Integer.MAX_VALUE;
        maxX = Integer.MIN_VALUE;
        maxY = Integer.MIN_VALUE;

        if (root != null) {
            walkQuadTreeForBounds(root);
        }

        // Handle case where no tiles exist
        if (minX == Integer.MAX_VALUE) {
            minX = minY = maxX = maxY = 0;
        }

        boundsValid = true;
    }

    private void walkQuadTreeForBounds(QuadNode node) {
        if (node == null) return;

        // If it's a leaf node, scan for actual data bounds
        if (node.image != null) {
            scanTileForDataBounds(node);
            return;
        }

        // If it's an internal node, recurse into children
        if (node.children != null) {
            for (QuadNode child : node.children) {
                if (child != null) {
                    walkQuadTreeForBounds(child);
                }
            }
        }
    }

    private void scanTileForDataBounds(QuadNode node) {
        Raster raster = node.image.getRaster();
        int tileX = node.x;
        int tileY = node.y;
        int tileWidth = node.size;
        int tileHeight = node.size;
        
        // Scan the tile to find actual pixel data bounds
        boolean foundData = false;
        int tileMinX = Integer.MAX_VALUE;
        int tileMinY = Integer.MAX_VALUE;
        int tileMaxX = Integer.MIN_VALUE;
        int tileMaxY = Integer.MIN_VALUE;
        
        // Scan for non-transparent pixels
        for (int y = 0; y < tileHeight; y++) {
            for (int x = 0; x < tileWidth; x++) {
                if (hasNonTransparentPixel(raster, x, y)) {
                    foundData = true;
                    tileMinX = Math.min(tileMinX, x);
                    tileMinY = Math.min(tileMinY, y);
                    tileMaxX = Math.max(tileMaxX, x + 1);
                    tileMaxY = Math.max(tileMaxY, y + 1);
                }
            }
        }
        
        // Update global bounds with actual data bounds
        if (foundData) {
            minX = Math.min(minX, tileX + tileMinX);
            minY = Math.min(minY, tileY + tileMinY);
            maxX = Math.max(maxX, tileX + tileMaxX);
            maxY = Math.max(maxY, tileY + tileMaxY);
        }
    }

    private boolean hasNonTransparentPixel(Raster raster, int x, int y) {
        // Check if pixel has non-transparent data
        // For RGB images, check if alpha channel is non-zero
        if (raster.getNumBands() >= 4) {
            return raster.getSample(x, y, 3) != 0; // Alpha channel
        }
        // For other formats, check if any band is non-zero
        for (int b = 0; b < raster.getNumBands(); b++) {
            if (raster.getSample(x, y, b) != 0) {
                return true;
            }
        }
        return false;
    }

    /** Returns a Graphics2D to draw onto this image via a wrapper */
    public Graphics2D createGraphics() {
        return new QuadGraphics2D(this);
    }

    /** Returns the aggregate/virtual raster - processes over all effected leaf on each read/write */
    public AbstractWritableRaster getRaster() {
        return this.raster;
    }

    /// @return the number of leaves currently allocated for this InfiniteBufferedImage
    public int getAllocatedLeafCount() {
        return countAllocatedLeaves(root);
    }

    private int countAllocatedLeaves(QuadNode node) {
        if (node == null) return 0;
        if (node.isLeaf()) return node.image != null ? 1 : 0;
        int sum = 0;
        if (node.children != null) {
            for (QuadNode c : node.children) sum += countAllocatedLeaves(c);
        }
        return sum;
    }

    QuadNode findLeaf(int x, int y) {
        QuadNode node = root;
        while (!node.isLeaf()) {
            int index = node.getQuadrant(x, y);
            if (node.children[index] == null) return null;
            node = node.children[index];
        }
        return node.contains(x, y) ? node : null;
    }

    /// Guarantees to return a QuadNode with a non-null image
    QuadNode findOrCreateLeaf(int x, int y) {
        // If tree is too small, grow tree to contain given coordinates
        while (!root.contains(x, y)) {
            root = root.growToFit(x, y);
        }
        // Recursively search for leaf at given coordinates
        QuadNode leaf = getOrCreateLeafAt(x, y, root, root.size);
        if (leaf.image == null) leaf.image = new BufferedImage(TILE_SIZE, TILE_SIZE, type);
        return leaf;
    }

    List<QuadNode> findLeaves(Rectangle2D bounds, boolean createIfMissing){
        return findLeaves((int) bounds.getX(), (int) bounds.getY(), (int) bounds.getWidth(), (int) bounds.getHeight(), createIfMissing);
    }

    List<QuadNode> findLeaves(int x, int y, int w, int h, boolean createIfMissing){
        List<QuadNode> nodes = new ArrayList<>();
        int endX = x + w - 1;
        int endY = y + h - 1;

        int startTileX = Math.floorDiv(x, TILE_SIZE) * TILE_SIZE;
        int endTileX   = Math.floorDiv(endX, TILE_SIZE) * TILE_SIZE;
        int startTileY = Math.floorDiv(y, TILE_SIZE) * TILE_SIZE;
        int endTileY   = Math.floorDiv(endY, TILE_SIZE) * TILE_SIZE;

        for (int i = startTileX; i <= endTileX; i += TILE_SIZE) {
            for (int j = startTileY; j <= endTileY; j += TILE_SIZE) {
                nodes.add(createIfMissing ? findOrCreateLeaf(i, j) : findLeaf(i, j));
            }
        }
        return nodes;
    }

    /**
     Recursively navigates the quadtree to locate the leaf node that contains the given global
     coordinates. If necessary, this method will create any missing intermediate nodes along
     the path to ensure the leaf exists.

     <p>This function assumes the quadtree subdivides space into quadrants until the specified
     {@code TILE_SIZE} is reached. Each recursive call descends one level deeper, halving the
     node size until the target leaf node is found.</p>

     @param globalX the global X coordinate in the full image space
     @param globalY the global Y coordinate in the full image space
     @param current the current node being examined
     @param size the size (width/height) of the current node in pixels

     @return the {@link QuadNode} leaf that corresponds to the given coordinates
     */
    private QuadNode getOrCreateLeafAt(int globalX, int globalY, QuadNode current, int size){
        if (size == TILE_SIZE) return current;

        int half = size / 2;
        int cx = (globalX >= current.x + half) ? 1 : 0;
        int cy = (globalY >= current.y + half) ? 1 : 0;
        int childIndex = cy * 2 + cx;

        if (current.children == null)
            current.children = new QuadNode[4];

        if (current.children[childIndex] == null) {
            int childX = current.x + (cx * half);
            int childY = current.y + (cy * half);
            current.children[childIndex] = new QuadNode(childX, childY, half);
        }

        return getOrCreateLeafAt(globalX, globalY, current.children[childIndex], half);
    }

    /** Blit the quad tree into a graphics context */
    protected void paint(Graphics g, int offsetX, int offsetY) {
        root.paint(g, offsetX, offsetY);
    }
}
