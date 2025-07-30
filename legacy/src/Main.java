import main.java.com.pixel.BufferFragment;
import main.java.com.pixel.util.Logger;
import main.java.com.pixel.util.Rect;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {

    /*public static final int BUFFER_TYPE = BufferedImage.TYPE_INT_RGB;

    static BufferedImage compiled;

    public static BufferedImage compile(GlobalData data){
        BufferedImage compiled = new BufferedImage(data.sectionBounds.width, data.sectionBounds.height, data.BUFFER_TYPE);
        for(List<BufferFragment> list : data.allNodes.values()){
            for(BufferFragment fragment : list){
                fragment.draw(compiled);
            }
        }
        return compiled;
    }

    public record GlobalData(Map<Integer, List<BufferFragment>> allNodes, Rect sectionBounds, int FRAGMENT_HEIGHT, int BUFFER_TYPE) {}

    public static Rect test(GlobalData data, int x, int y){
        compiled = compile(data);
        compiled.setRGB(x, y, Color.BLUE.getRGB());

        BufferFragment orCreate = getOrCreate(x, y, data.FRAGMENT_HEIGHT, data.allNodes, data.sectionBounds);
        fill(orCreate, Color.YELLOW.getRGB());

        compiled = compile(data);
        compiled.setRGB(x, y, Color.BLUE.getRGB());
        return orCreate == null ? null : orCreate.getBounds();
    }

    public static void fill(BufferFragment fragment, int color){
        if(fragment == null) return;
        for(int x = fragment.getBounds().getLeft(); x < fragment.getBounds().getLeft() + fragment.getBounds().width; x++)
            for(int y = fragment.getBounds().getTop(); y < fragment.getBounds().getTop() + fragment.getBounds().height; y++){
                fragment.set(x, y, color);
            }
    }

    public record Sibling(Rect bounds, int distance){}

    public static class ClosestSiblings {
        Sibling left, right;

        ClosestSiblings(Sibling left, Sibling right){
            this.left = left;
            this.right = right;
        }

    }

    *//**
     *
     * @param allNodes lists of fragments, sorted by depth
     * @param depth used to get the list of fragments that are within the same depth as self
     * @param selfBounds the current bounds of the new fragment
     * @return left for left sibling, right for right. left and right are null if no sibling was found in that direction.
     *//*
    public static ClosestSiblings findClosestSiblings(Map<Integer, List<BufferFragment>> allNodes, int depth, Rect selfBounds){
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

    public static BufferFragment getOrCreate(int x, int y, int FRAGMENT_HEIGHT, Map<Integer, List<BufferFragment>> allNodes, Rect sectionBounds){
        BufferFragment result = get(x, y, FRAGMENT_HEIGHT, allNodes, sectionBounds);
        if(result != null) return result;
        return create(x, y, FRAGMENT_HEIGHT, allNodes, sectionBounds);
    }

    private static BufferFragment get(int x, int y, int FRAGMENT_HEIGHT, Map<Integer, List<BufferFragment>> allNodes, Rect sectionBounds){
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

    private static BufferFragment create(int x, int y, int FRAGMENT_HEIGHT, Map<Integer, List<BufferFragment>> allNodes, Rect sectionBounds){
        Logger.log("Did not find fragment, searching for location to create it at...");
        int depth = y / FRAGMENT_HEIGHT;
        while(depth > 0) {
            // Look through all the previous depth's fragments
            for (BufferFragment fragment : allNodes.get(depth-1)) {
                if (fragment.containsX(x)) {
                    Logger.log("Found possible parent, matching x (and y).");
                    // found a possible parent-fragment;
                    // skipping validation;

                    *//*
                    3 forms of width:
                    max width = root_max_width / (2 * depth) .getMaxWidth()
                    rendered width = (assigned at initialization) returns for .getWidth()
                    allocated width = (assigned at initialization) how width can this fragment expand to render .getAllocatedWidth()
                     *//*


                    //final int MAX_WIDTH = (int) (fragment.getBounds().getWidth() / 2);
                    final int MAX_WIDTH = fragment.getAllocatedWidth() / 2;
                    // 0. default bounds are parent aligned, and must contain the given (x,y)
                    Rect bounds = new Rect(fragment.getBounds());
                    bounds.setSize(MAX_WIDTH, FRAGMENT_HEIGHT);
                    bounds.translate((bounds.contains(x,y - FRAGMENT_HEIGHT)) ? 0 : bounds.width, FRAGMENT_HEIGHT);
                    // assert bounds contains point now.

                    //if(bounds.contains(x, y)); // what coords should we be looking for?

                    // 1. find siblings
                    Logger.log("Finding closest siblings...");

                    ClosestSiblings closestSiblings = findClosestSiblings(allNodes, depth, bounds);
                    if(closestSiblings.left == null){
                        Logger.log("No closest sibling to the left");
                        closestSiblings.left = new Sibling(sectionBounds, sectionBounds.width);
                    }
                    if(closestSiblings.right == null){
                        Logger.log("No closest sibling to the right");
                        closestSiblings.right = new Sibling(sectionBounds, sectionBounds.width);
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

                        return createFragment(sectionBounds, bounds, MAX_WIDTH, depth, BUFFER_TYPE, FRAGMENT_HEIGHT, allNodes);
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

                        return createFragment(sectionBounds, bounds, MAX_WIDTH, depth, BUFFER_TYPE, FRAGMENT_HEIGHT, allNodes);
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
                        return createFragment(sectionBounds, bounds, MAX_WIDTH, depth, BUFFER_TYPE, FRAGMENT_HEIGHT, allNodes);
                    }

                    // if it does, then try to align to right;
                    dx = bounds.width - (closestSiblings.right.distance % bounds.width);
                    if(bounds.contains(x + dx, y)){
                        // if it doesn't, success;
                        bounds.translate(dx, 0);
                        Logger.log("smart aligning (right)\n");
                        return createFragment(sectionBounds, bounds, MAX_WIDTH, depth, BUFFER_TYPE, FRAGMENT_HEIGHT, allNodes);
                    }

                    // 4. Default: Parent Aligned (if it's not contained, then leave it as is)
                    Logger.log("parent aligned\n");
                    return createFragment(sectionBounds, bounds, MAX_WIDTH, depth, BUFFER_TYPE, FRAGMENT_HEIGHT, allNodes);
                }
            }
            // todo: currently only can create if parent already exists.
            //  should we a) create chains of parents, or b) throw a alert and don't create anything,
            //  or c) both.
            Logger.log("didn't find?\n");
            break;
        }

        // todo: throw illegal state, never should be reachable, and this should never return null;
        //  i take that back; if coords are out of bounds or would create a fragment without a parent (floating fragment)
        //  then either throw specific errors (preferred) or return null;
        Logger.log("somehow reached here.\n");
        return null;
    }

    public static BufferFragment createFragment(Rect sectionBounds, Rect bounds, int MAX_WIDTH, int depth,
                                                int IMAGE_TYPE, int MIN_WIDTH, Map<Integer, List<BufferFragment>> allNodes){
        BufferFragment found = new BufferFragment(sectionBounds, bounds, IMAGE_TYPE, MAX_WIDTH, MIN_WIDTH);
        List<BufferFragment> list = allNodes.getOrDefault(depth, new ArrayList<>());
        list.add(found);
        allNodes.put(depth, list);
        return found;
    }*/
}