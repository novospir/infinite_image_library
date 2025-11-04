package com.novospir.libraries;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Represents a single node in the quadtree used to organize infinite image space.
 * 
 * <p>QuadNode is the fundamental building block of the quadtree spatial data structure
 * that enables infinite image dimensions. Each node represents a rectangular region
 * of the image space and either contains:
 * <ul>
 *   <li><b>Leaf nodes:</b> A tile (BufferedImage) with actual pixel data
 *   <li><b>Internal nodes:</b> References to 4 child nodes partitioning the space into quadrants
 * </ul>
 * 
 * <h3>Node States:</h3>
 * <ul>
 *   <li><b>Leaf node</b> ({@code children == null}): Contains a BufferedImage tile
 *   <li><b>Internal node</b> ({@code children != null}): Contains 0-4 child nodes
 *   <li><b>Unused regions</b>: Child array slots may be null if that quadrant is empty
 * </ul>
 *
 * <h3>Spatial Coordinates:</h3>
 * <p>Each node stores:
 * <ul>
 *   <li>{@code x, y} - Top-left corner position in global image space
 *   <li>{@code size} - Width and height of this node's region (always square)
 *   <li>Bounding rectangle: {@code [x, x+size) x [y, y+size)}
 * </ul>
 *
 * <h3>Example Traversal:</h3>
 * <pre>{@code
 * // Find which node contains pixel (500, 300)
 * QuadNode current = root;  // Start at root
 * while (!current.isLeaf()) {
 *     int quadrant = current.getQuadrant(500, 300);
 *     current = current.children[quadrant];
 * }
 * // Now current points to the 128x128 tile containing (500, 300)
 * BufferedImage tile = current.image;
 * }</pre>
 * 
 * @see InfiniteBufferedImage
 * @author Novospir, Adam
 * @since 1.0
 */
class QuadNode {
    final int x, y, size;
    BufferedImage image; // only used if leaf
    QuadNode[] children;

    /**
     * Creates a new quadtree node representing a spatial region.
     *
     * @param x The x-coordinate of the top-left corner in global image space
     * @param y The y-coordinate of the top-left corner in global image space
     * @param size The width and height of this node's region (always square)
     */
    public QuadNode(int x, int y, int size) {
        this.x = x;
        this.y = y;
        this.size = size;
    }

    /**
     * Determines if this node is a leaf node (contains pixel data).
     *
     * @return {@code true} if this is a leaf node (children == null), {@code false} if internal
     */
    boolean isLeaf() {
        return children == null;
    }

    /**
     * Checks if a point lies within this node's spatial bounds.
     *
     * @param px The x-coordinate of the point to test
     * @param py The y-coordinate of the point to test
     * @return {@code true} if the point is within bounds [x, x+size) x [y, y+size), {@code false} otherwise
     */
    boolean contains(int px, int py) {
        return px >= x && px < x + size && py >= y && py < y + size;
    }

    /**
     * Determines which of the four child quadrants contains a given point.
     *
     * <p>Quadrants are numbered as follows:
     * <ul>
     *   <li>0 = top-left (NW)
     *   <li>1 = top-right (NE)
     *   <li>2 = bottom-left (SW)
     *   <li>3 = bottom-right (SE)
     * </ul>
     *
     * @param px The x-coordinate of the point
     * @param py The y-coordinate of the point
     * @return An integer in [0, 3] representing the quadrant containing the point
     */
    int getQuadrant(int px, int py) {
        int half = size / 2;
        boolean right = px >= x + half;
        boolean bottom = py >= y + half;
        return (bottom ? 2 : 0) + (right ? 1 : 0);
    }

    /**
     * Creates a new parent node to accommodate a point outside this node's bounds.
     *
     * <p>This method is called when attempting to access coordinates beyond the current
     * tree's spatial extent. It creates a new root node that is 2x larger, then repositions
     * this node to be one of the four quadrants of the new root. The new root is positioned
     * to contain both the existing tree and the new point.
     *
     * <p>Example: If current root is [0, 128] x [0, 128] and we need to access point (200, 0),
     * the new root becomes [-64, 192] x [-64, 192] with the old root positioned appropriately.
     *
     * @param px The x-coordinate of the point that needs to be accommodated
     * @param py The y-coordinate of the point that needs to be accommodated
     * @return A new QuadNode that is the parent of this node and can contain both this tree and the point
     */
    QuadNode growToFit(int px, int py) {
        int newSize = size * 2;
        int newX = x, newY = y;
        int qx = px < x ? -1 : (px >= x + size ? 1 : 0);
        int qy = py < y ? -1 : (py >= y + size ? 1 : 0);

        if (qx < 0) newX -= size;
        if (qy < 0) newY -= size;

        QuadNode newRoot = new QuadNode(newX, newY, newSize);
        newRoot.children = new QuadNode[4];
        int oldQuadrant = (y >= newY + size ? 2 : 0) + (x >= newX + size ? 1 : 0);
        newRoot.children[oldQuadrant] = this;
        return newRoot;
    }

    /**
     * Recursively paints this node and its children to a Graphics context.
     *
     * <p>For leaf nodes, draws the BufferedImage tile at the correct position.
     * For internal nodes, recursively calls paint on all non-null children.
     *
     * @param g The Graphics context to draw to
     * @param offsetX The x-offset to apply to the drawing position
     * @param offsetY The y-offset to apply to the drawing position
     */
    void paint(Graphics g, int offsetX, int offsetY) {
        if (isLeaf()) {
            if (image != null)
                g.drawImage(image, x - offsetX, y - offsetY, null);
        } else {
            for (QuadNode child : children) {
                if (child != null) child.paint(g, offsetX, offsetY);
            }
        }
    }
}

