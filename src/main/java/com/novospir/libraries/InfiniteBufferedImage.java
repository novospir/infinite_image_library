package com.novospir.libraries;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.awt.image.Raster;

/*
todo: Implement efficient storage; if a leaf is one color, then store that color.
        if on retrieval, the leaf is null &  color !null, create buffered image of that color
        note: this trades space for operation time - setting to flag on initialization
            exception is 0x0 tile ie fully transparent, then return null or rather, color will be null as well.
            the function above, dispose()/garbageCollect(), will call this function and iterate through tiles to check
            if any are able to be "optimized"
todo: thread safety
note: Add an internal method to prune empty tiles on dispose() of QuadGraphics2D or via explicit API (compact()).
note: Consider fast-path tracking of min/max when painting, to avoid full rescan
 */

public class InfiniteBufferedImage implements AbstractBufferedImage {

    public static final int TILE_SIZE = 128; // leaf tile size
    private QuadNode root;
    private final int type;
    private final InfiniteWritableRaster raster;

    private int minX = Integer.MAX_VALUE;
    private int minY = Integer.MAX_VALUE;
    private int maxX = Integer.MIN_VALUE;
    private int maxY = Integer.MIN_VALUE;
    private boolean boundsValid = false;


    protected int getType(){
        return type;
    }

    /**
     * Creates a scalable buffered image, tiled space via quadtree
     * </br> Initializes with an origin of (0, 0)
     * </br> <code>TILE_SIZE</code> is set as a final static Todo: move to config
     */
    public InfiniteBufferedImage() {
        this(0, 0);
    }

    /**
     * Creates a scalable buffered image, tiled space via quadtree
     * @param x The x-coordinate for the position of the starting image
     * @param y The y-coordinate for the position of the starting image
     * </br> <code>TILE_SIZE</code> is set as a final static Todo: move to config
     */
    public InfiniteBufferedImage(int x, int y) {
        this.root = new QuadNode(x, y, TILE_SIZE);
        this.type = BufferedImage.TYPE_INT_ARGB;
        this.raster = new InfiniteWritableRaster(this);
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
        if (node.image == null) {
            node.image = new BufferedImage(TILE_SIZE, TILE_SIZE, type);
            //if (delegateLeaf == null) delegateLeaf = node.image;
        }
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

    /*
    ie. if destroy() is called, needs to be able to revert to previous "logical bounds"
     */
    public Rectangle getLogicalBounds(){
        if (!boundsValid) {
            // Only recompute if we've never computed, grown or if we've shrunk
            recomputeBounds();
        }
        //return new Rectangle(minX, minY, maxX - minX, maxY - minY);
        //throw new UnsupportedOperationException("Todo: Implement function");
        int x = Math.min(minX, maxX);
        int y = Math.min(minY, maxY);
        int w = Math.abs(maxX - minX);
        int h = Math.abs(maxY - minY);
        return new Rectangle(x, y, w, h);
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

    // Call this when:
    // - Creating new tiles
    // - Removing tiles (if you implement that)
    // - Any operation that might change the bounds
    protected void markBoundsDirty() {
        boundsValid = false;
    }

    /** Returns a Graphics2D to draw onto this image via a wrapper */
    public Graphics2D createGraphics() {
        return new QuadGraphics2D(this);
    }

    /** Returns the aggregate/virtual raster - processes over all effected leaf on each read/write */
    public AbstractWritableRaster getRaster() {
        return this.raster;
    }

    /*
    Internally maps to multiple tiles
    Returns a composable or iterable view (not a full copy)
    BufferedImageView getRegion(Rectangle region);

    WritableRaster getTileRaster(int tileX, int tileY);
     */

    // ---- Internal helpers ----

    private void ensureContains(int x, int y) {
        while (!root.contains(x, y)) {
            root = root.growToFit(x, y);
        }
    }

    protected BufferedImage getTileImage(int x, int y, boolean createIfMissing){
        if(createIfMissing) return findOrCreateLeaf(x, y).image;
        QuadNode leaf = findLeaf(x, y);
        if(leaf == null) return null;
        return leaf.image;
    }

    List<QuadNode> findLeaves(Rectangle2D bounds, boolean createIfMissing){
        return findLeaves((int) bounds.getX(), (int) bounds.getY(), (int) bounds.getWidth(), (int) bounds.getHeight(), createIfMissing);
    }

    // todo: replace BufferedImage with interface - allow the needed functions to be
    //  passed on if buffered image, but allow a "tile" to contain only a single color - if
    //  that is the only color within (also, use that instead of null - color black/transparent etc.)
    List<QuadNode> findLeaves(int x, int y, int w, int h, boolean createIfMissing){
        // given bounds of affected region - return all tiles intersecting with bounds
        // use BufferedImage getTileImage(int x, int y, boolean createIfMissing=false)
        // jump TILE_SIZE when searching the space - insert even if null
        // return list
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

    public int getAllocatedLeafCount() {
        return countLeavesWithImage(root);
    }

    private int countLeavesWithImage(QuadNode node) {
        if (node == null) return 0;
        if (node.isLeaf()) return node.image != null ? 1 : 0;
        int sum = 0;
        if (node.children != null) {
            for (QuadNode c : node.children) sum += countLeavesWithImage(c);
        }
        return sum;
    }

    /// find, but don't create if missing - return null
    QuadNode findLeaf(int x, int y) {
        QuadNode node = root;
        while (!node.isLeaf()) {
            int index = node.getQuadrant(x, y);
            if (node.children[index] == null) return null;
            node = node.children[index];
        }
        return node.contains(x, y) ? node : null;
    }

    QuadNode findOrCreateLeaf(int x, int y) {
        ensureContains(x, y); // todo: simplify this interaction
        //note: ensureContains does part of the work. private overload findOrCreateLeaf doesn't actually do what it says
        return findOrCreateLeaf(x, y, root, root.size);
        /*QuadNode node = root;
        while (!node.isLeaf()) {
            int index = node.getQuadrant(x, y);
            if (node.children[index] == null)
                node.children[index] = node.createChild(index);
            node = node.children[index];
        }
        return node;*/
    }

    /// WARNING: does not do what it says. Does not expand image to find oob coordinates
    private QuadNode findOrCreateLeaf(int globalX, int globalY, QuadNode current, int size){
        if (size == TILE_SIZE) {
            if (current.image == null) {
                current.image = new BufferedImage(size, size, type);
                //System.out.printf("New image fragment created%n");
            }
            return current;
        }

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
            //System.out.printf("New children created%n");
        }

        return findOrCreateLeaf(globalX, globalY, current.children[childIndex], half);
    }

    /** Blit the quad tree into a graphics context */
    protected void paint(Graphics g, int offsetX, int offsetY) {
        root.paint(g, offsetX, offsetY);
    }
}
