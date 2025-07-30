package com.novospir.libraries;

import java.awt.*;
import java.awt.image.BufferedImage;

class QuadNode {
    final int x, y, size;
    BufferedImage image; // only used if leaf
    QuadNode[] children;

    public QuadNode(int x, int y, int size) {
        this.x = x;
        this.y = y;
        this.size = size;
    }

    boolean isLeaf() {
        return children == null;
    }

    boolean contains(int px, int py) {
        return px >= x && px < x + size && py >= y && py < y + size;
    }

    int getQuadrant(int px, int py) {
        int half = size / 2;
        boolean right = px >= x + half;
        boolean bottom = py >= y + half;
        return (bottom ? 2 : 0) + (right ? 1 : 0);
    }

    QuadNode createChild(int quadrant) {
        int half = size / 2;
        int childX = x + (quadrant % 2) * half;
        int childY = y + (quadrant / 2) * half;
        return new QuadNode(childX, childY, half);
    }

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

