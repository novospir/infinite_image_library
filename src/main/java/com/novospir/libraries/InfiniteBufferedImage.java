package com.novospir.libraries;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;

public class InfiniteBufferedImage implements AbstractBufferedImage {

    public static final int TILE_SIZE = 128; // leaf tile size
    private QuadNode root;

    public static void main(String[] args){
        InfiniteBufferedImage infiniteBufferedImage = new InfiniteBufferedImage();
        BufferedImage res;

        infiniteBufferedImage.setRGB(1, 1, Color.BLUE.getRGB());
        infiniteBufferedImage.setRGB(-1, -1, Color.RED.getRGB());
        res = infiniteBufferedImage.toBufferedImage(new Rectangle(-2, -2, 5, 5));

        infiniteBufferedImage.setRGB(127, 127, Color.BLUE.getRGB());
        //infiniteBufferedImage.setRGB(129, 129, Color.RED.getRGB());
        infiniteBufferedImage.setRGB(745, 1, Color.BLACK.getRGB());
        infiniteBufferedImage.setRGB(745, 55, Color.RED.getRGB());

        res = infiniteBufferedImage.toBufferedImage(new Rectangle(126, 0, 630, 128));
        System.out.println("Fin.");
    }

    //todo: init to size (w,h) then run recursive until we have the space?

    /**
     * Creates a scalable buffered image, tiled space via quadtree
     * </br> Initializes with an origin of (0, 0)
     * </br> <code>TILE_SIZE</code> is set as a final static Todo: move to config
     */
    public InfiniteBufferedImage() {
        this.root = new QuadNode(0, 0, TILE_SIZE);
    }

    /**
     * Creates a scalable buffered image, tiled space via quadtree
     * @param x The x-coordinate for the position of the starting image
     * @param y The y-coordinate for the position of the starting image
     * </br> <code>TILE_SIZE</code> is set as a final static Todo: move to config
     */
    public InfiniteBufferedImage(int x, int y) {
        this.root = new QuadNode(x, y, TILE_SIZE);
    }

    /** Gets the ARGB value at (x,y), or 0 if outside any filled tile */
    public int getRGB(int x, int y) {
        QuadNode node = findLeaf(x, y);
        if (node == null || node.image == null) return 0;
        return node.image.getRGB(x - node.x, y - node.y);
    }

    /** Sets the ARGB value at (x,y), expanding the tree as needed */
    public void setRGB(int x, int y, int argb) {
        ensureContains(x, y);
        QuadNode node = findOrCreateLeaf(x, y);
        if (node.image == null)
            node.image = new BufferedImage(TILE_SIZE, TILE_SIZE, BufferedImage.TYPE_INT_ARGB); //!
        node.image.setRGB(x - node.x, y - node.y, argb);
    }

    /** Returns the full raster across all tiles (for export) */
    public BufferedImage toBufferedImage(Rectangle bounds) {
        BufferedImage out = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = out.getGraphics();
        paint(g, bounds.x, bounds.y);
        g.dispose();
        return out;
    }

    /** Returns a Graphics2D to draw onto this image via a wrapper */
    public Graphics2D createGraphics() {
        return new QuadGraphics2D(this);
    }

    /** Returns a synthetic raster across the quad image (read-only) */
    public Raster getRaster(Rectangle bounds) {
        return toBufferedImage(bounds).getRaster();
    }

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

    private QuadNode findLeaf(int x, int y) {
        QuadNode node = root;
        while (!node.isLeaf()) {
            int index = node.getQuadrant(x, y);
            if (node.children[index] == null) return null;
            node = node.children[index];
        }
        return node.contains(x, y) ? node : null;
    }

    private QuadNode findOrCreateLeaf(int x, int y) {
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

    private QuadNode findOrCreateLeaf(int globalX, int globalY, QuadNode current, int size){
        if (size == TILE_SIZE) {
            if (current.image == null)
                current.image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
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
        }

        return findOrCreateLeaf(globalX, globalY, current.children[childIndex], half);
    }

    /** Blit the quad tree into a graphics context */
    protected void paint(Graphics g, int offsetX, int offsetY) {
        root.paint(g, offsetX, offsetY);
    }
}
