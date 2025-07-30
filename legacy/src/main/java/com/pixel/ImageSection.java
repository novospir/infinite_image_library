package main.java.com.pixel;

import main.java.com.pixel.util.Logger;
import main.java.com.pixel.util.Rect;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static main.java.com.pixel.Config.*;

public class ImageSection {

    /// This is a temporary brute-force solution; use an actual algorithm later (see notes)
    ///
    /// key - depth, value - list of all fragments at that depth
    private final Map<Integer, List<BufferFragment>> allNodes;

    private final Neighbors neighbors;
    private final Quadrant quadrant;
    private final Rect bounds;

    public ImageSection(Quadrant quadrant, Rect bounds){
        this(new Neighbors(null, null), quadrant, bounds);
    }

    public ImageSection(Neighbors neighbors, Quadrant quadrant, Rect bounds){
        this.bounds = bounds;
        this.quadrant = quadrant;
        this.neighbors = neighbors;
        this.allNodes = new HashMap<>();
    }

    /**
     * Given global coordinates, set that pixel to given color
     * @param x Global x
     * @param y Global y
     * @param color desired color of global coordinates
     */
    public void set(int x, int y, int color){
        BufferFragment fragment = getOrCreate(x, y);
        fragment.set(x, y, color);
    }

    @NotNull
    public BufferFragment getOrCreate(int x, int y){
        BufferFragment result = this.get(x, y);
        if(result != null) return result;
        return this.create(x, y);
    }

    private BufferFragment get(int x, int y){
        Logger.log(String.format("Searching for fragment at (%d, %d)", x, y));
        int depth = y / FRAGMENT_HEIGHT;
        List<BufferFragment> bufferFragments = allNodes.get(depth);
        if(bufferFragments != null) {
            for (BufferFragment fragment : bufferFragments) {
                if (fragment.containsX(x)) {
                    Logger.log("Found fragment, returning it");
                    return fragment;
                }
            }
        }
        return null;
    }

    /**
     * @param x Global x
     * @param y Global y
     * @return A fragment created that contains the point x,y
     * @throws RuntimeException where no parent found - should be an unreachable situation
     */
    @NotNull
    private BufferFragment create(int x, int y){
        Logger.log("Did not find fragment, searching for location to create it at...");
        final int depth = y / FRAGMENT_HEIGHT;

        // Look through all the previous depth's fragments
        for (BufferFragment fragment : allNodes.get(depth - 1)) {
            if (fragment.containsX(x)) {
                Logger.log("Found possible parent, matching x (and y).");
                // found a possible parent-fragment;
                // skipping validation;

                final int maxWidth = fragment.getAllocatedWidth() / 2;

                // 0. default bounds are parent aligned, and must contain the given (x,y)
                Rect bounds = new Rect(fragment.getBounds());
                bounds.setSize(maxWidth, FRAGMENT_HEIGHT);
                bounds.translate((bounds.contains(x,y - FRAGMENT_HEIGHT)) ? 0 : bounds.width, FRAGMENT_HEIGHT);
                // assert bounds contains point now.

                //if(bounds.contains(x, y)); // what coords should we be looking for?

                // 1. find siblings
                Logger.log("Finding closest siblings...");

                ClosestSiblings closestSiblings = findClosestSiblings(depth, bounds);
                if(closestSiblings.left == null){
                    Logger.log("No closest sibling to the left");
                    closestSiblings.left = new Sibling(this.bounds, this.bounds.width);
                }
                if(closestSiblings.right == null){
                    Logger.log("No closest sibling to the right");
                    closestSiblings.right = new Sibling(this.bounds, this.bounds.width);
                }

                int distBetweenSiblings = Math.abs(
                        closestSiblings.left.bounds.getRightExclusive() -
                                closestSiblings.right.bounds.getLeft());

                // 2. Detect case: Overflow
                if(bounds.getLeft() < closestSiblings.left.bounds().getRightExclusive()
                        && bounds.getRightExclusive() > closestSiblings.left.bounds().getRightExclusive()
                ){
                    Logger.log("Overflow to the right\n");

                    bounds.translate(closestSiblings.left.distance, 0);

                    if(closestSiblings.right.distance < bounds.width){
                        // note: assuming setSize maintains the top-left coordinate
                        bounds.setSize(distBetweenSiblings, FRAGMENT_HEIGHT);
                    }

                    return createFragment(bounds, maxWidth, depth);
                }

                if(bounds.getRightExclusive() > closestSiblings.right.bounds().getLeft()
                        && bounds.getLeft() < closestSiblings.right.bounds().getLeft()
                ){
                    Logger.log("Overflow to the left\n");

                    bounds.translate(-closestSiblings.right.distance, 0);

                    if(closestSiblings.left.distance < bounds.width) {
                        // note: assuming setSize maintains the top-left coordinate
                        bounds.setSize(distBetweenSiblings, FRAGMENT_HEIGHT);
                    }

                    return createFragment(bounds, maxWidth, depth);
                }

                // 3. Smart Allocation

                // begin at current location
                // detect a multiple of "current expected width"

                // how much to allow another self.width on my left?
                int dx = bounds.width - (closestSiblings.left.distance % bounds.width);
                // does it lose my anchor
                if(bounds.contains(x + dx, y)){
                    // if it doesn't, success;
                    bounds.translate(-dx, 0);
                    Logger.log("smart aligning (left)\n");
                    return createFragment(bounds, maxWidth, depth);
                }

                // if it does, then try to align to right;
                dx = bounds.width - (closestSiblings.right.distance % bounds.width);
                if(bounds.contains(x + dx, y)){
                    // if it doesn't, success;
                    bounds.translate(dx, 0);
                    Logger.log("smart aligning (right)\n");
                    return createFragment(bounds, maxWidth, depth);
                }

                // 4. Default: Parent Aligned (if it's not contained, then leave it as is)
                Logger.log("parent aligned\n");
                return createFragment(bounds, maxWidth, depth);
            }
        }

        Logger.log("No parents found.", Logger.ERROR);
        throw new RuntimeException("Unreachable code?");
    }

    @NotNull
    private BufferFragment createFragment(Rect bounds, int maxWidth, int depth){
        BufferFragment fragment = new BufferFragment(this.bounds, bounds, BUFFER_TYPE, maxWidth);
        List<BufferFragment> list = allNodes.getOrDefault(depth, new ArrayList<>());
        list.add(fragment);
        allNodes.put(depth, list);
        return fragment;
    }

    private ClosestSiblings findClosestSiblings(int depth, Rect selfBounds){
        final List<BufferFragment> siblings = allNodes.get(depth);
        ClosestSiblings closestSiblings = new ClosestSiblings(null, null);

        if(siblings == null) return closestSiblings;

        final int left = selfBounds.getLeft();
        final int right = selfBounds.getRightExclusive();

        for (BufferFragment sibling : siblings) {
            if (right > sibling.getBounds().getLeft() && right > sibling.getBounds().getRightExclusive()) {
                int distance = Math.abs(sibling.getBounds().getRightExclusive() - left);
                if (closestSiblings.left == null || distance < closestSiblings.left.distance) {
                    closestSiblings.left = new Sibling(sibling.getBounds(), distance);
                    continue;
                }
            }
            if (left < sibling.getBounds().getRightExclusive() && left < sibling.getBounds().getLeft()) {
                int distance = Math.abs(sibling.getBounds().getLeft() - right);
                if (closestSiblings.right == null || distance < closestSiblings.right.distance) {
                    closestSiblings.right = new Sibling(sibling.getBounds(), distance);
                }
            }
        }
        return closestSiblings;
    }

    private record Sibling(Rect bounds, int distance){}

    private static class ClosestSiblings {
        Sibling left, right;

        ClosestSiblings(Sibling left, Sibling right){
            this.left = left;
            this.right = right;
        }

    }

    /// who to copy from when perimeter expands
    public record Neighbors(BufferedImage left, BufferedImage right){}

    /// Returns the Quadrant of this section
    public Quadrant getQuadrant() {
        return quadrant;
    }

    public boolean isCore() {
        return getQuadrant() == Quadrant.CORE;
    }

    public Rect getBounds(){
        return bounds;
    }

    public String toString() {
        return String.format("Section [%s], Bounds[%s]",
                this.getQuadrant(), this.getBounds()
        );
    }
}
