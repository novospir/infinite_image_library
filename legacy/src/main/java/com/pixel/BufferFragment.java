package main.java.com.pixel;

import main.java.com.pixel.util.Logger;
import main.java.com.pixel.util.Rect;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import static main.java.com.pixel.Config.FRAGMENT_HEIGHT;

public class BufferFragment {

    /*
    As a fragment, I will never be changing my size.
    I will never be moved. only added to, copied from or deleted.
     */

    // todo: confirm global coords are accepted. not local buffer.

    protected BufferedImage image;

    /// The 'logically' available data within this fragment; global coordinates.
    private final Rect bounds;

    private final Rect crop;

    /// Can be pointed at the same fragment, but there's never more than two children.
    //public main.java.com.pixel.BufferFragment left, right;
    private final int MAX_WIDTH, MIN_WIDTH, RENDERED_WIDTH, ALLOCATED_WIDTH;

    /// @param bounds global bounds.
    public BufferFragment(Rect section, Rect bounds, int type, int maxWidth){
        //this.bounds = bounds; // can be "less" than actual size. crops illegal space
        this.MAX_WIDTH = maxWidth;
        this.MIN_WIDTH = FRAGMENT_HEIGHT; // note: w >= h
        /*
        3 forms of width:
        min width = the smallest width; equivalent to 1 wide (scaled), may actually be 256
        max width = root_max_width; this width can never exceed max_width
        width = root_max_width / (2 * depth) .getMaxWidth()
        rendered width = (assigned at initialization) returns for .getWidth()
        allocated width = (assigned at initialization) how width can this fragment expand to render
            .getAllocatedWidth() [always a power of 2] [child uses this to determine initial width]
         */

        // rect should be able to overflow left/right sides
        // if so, calculate & allocate appropriately


        // todo: do calculation of diagonal from expansion point
        this.crop = (Rect) bounds.clone();
        if(section.getRight() < bounds.getRight()){
            // overflow on right
            int diff = bounds.getRight() - section.getRight();
            crop.setSize(crop.width - diff, crop.height);
        }
        if(section.getLeft() < bounds.getLeft()){
            // overflow on left
            int diff = bounds.getLeft() - section.getLeft();
            crop.setLocation(crop.x + diff, crop.y);
            crop.setSize(crop.width - diff, crop.height);
        }
        this.ALLOCATED_WIDTH = bounds.width;
        this.RENDERED_WIDTH = (int) crop.getWidth();

        this.bounds = (Rect) bounds.clone();
        this.image = new BufferedImage(bounds.width, bounds.height, type);
    }

    public int getWidth(){
        return bounds.width;
    }

    public int getMaxWidth(){
        return MAX_WIDTH;
    }

    public int getAllocatedWidth(){
        return ALLOCATED_WIDTH;
    }

    public int getHeight(){
        return bounds.height;
    }

    public Rect getBounds(){
        return bounds;
    }

    /// Called when a perimeter expansion is in progress
    /// @param strip A vertical strip of pixels
    public BufferFragment add(int[] strip){
        // note: overlap with update()
        Logger.log("FUNCTION NOT USED", Logger.WARN);
        return null;
    }

    /// if out of bounds, throw an error
    public void set(int x, int y, int color){
        //main.java.com.pixel.util.Logger.log("FUNCTION NOT USED", main.java.com.pixel.util.Logger.WARN);
        this.image.setRGB(x - bounds.getLeft(), y - bounds.getTop(), color);
    }

    /// Translates the internal image; used for the corners
    ///
    /// Data at boundaries (overflow) is lost.
    public void shift(int dx, int dy){
        BufferedImage newImage = new BufferedImage(
                image.getWidth(),
                image.getHeight(),
                image.getType());
        WritableRaster newRaster = newImage.getRaster();
        WritableRaster targetRaster = image.getRaster();
        newRaster.setRect(dx, dy, targetRaster);
        targetRaster.setRect(newRaster);
        this.image = newImage;
    }

    public static void update(BufferedImage src, BufferedImage dst, Rect copyFrom, Rect copyTo){
        WritableRaster srcRaster = src.getRaster();
        WritableRaster dstRaster = dst.getRaster();
        int[] strip = new int[copyFrom.height * copyFrom.width * ((src.getType() == BufferedImage.TYPE_4BYTE_ABGR) ? 4 : 1)];

        // todo: this assumes the same size regions.
        //  either its to wide or to short.
        //  either fill with zeros or cut away
        //  (the latter probably should never happen, throw a warning)
        srcRaster.getPixels(copyFrom.x, copyFrom.y, copyFrom.width, copyFrom.height, strip);
        dstRaster.setPixels(copyTo.x, copyTo.y, copyTo.width, copyTo.height, strip);
    }

    public <T extends BufferedImage> void draw(T canvas, Point to){
        WritableRaster srcRaster = image.getRaster();
        WritableRaster dstRaster = canvas.getRaster();
        int[] strip = new int[bounds.height * bounds.width * ((image.getType() == BufferedImage.TYPE_4BYTE_ABGR) ? 4 : 1)];
        srcRaster.getPixels(0, 0, bounds.width, bounds.height, strip);
        dstRaster.setPixels(to.x, to.y, bounds.width, bounds.height, strip);
    }

    public <T extends BufferedImage> void draw(T canvas){
        WritableRaster srcRaster = image.getRaster();
        WritableRaster dstRaster = canvas.getRaster();
        int bytesPerPixel = image.getColorModel().getPixelSize() / 8;
        int[] strip = new int[bounds.height * bounds.width * bytesPerPixel];
        srcRaster.getPixels(0, 0, bounds.width, bounds.height, strip);
        dstRaster.setPixels(bounds.getLeft(), bounds.getTop(), bounds.width, bounds.height, strip);
    }

    public <T extends BufferedImage> void draw(T canvas, Rect from, Rect to){
        WritableRaster srcRaster = image.getRaster();
        WritableRaster dstRaster = canvas.getRaster();

        // todo: this assumes @from and @to are the same size region.
        int[] strip = new int[from.height * from.width * ((image.getType() == BufferedImage.TYPE_4BYTE_ABGR) ? 4 : 1)];

        srcRaster.getPixels(from.x, from.y, from.width, from.height, strip);
        dstRaster.setPixels(to.x, to.y, to.width, to.height, strip);
    }


    public boolean contains(int x, int y){
        return this.bounds.contains(x, y);
    }

    public boolean containsX(int x){
        return this.bounds.getLeft() <= x && this.bounds.getRight() >= x;
    }

    public boolean containsY(int y){
        return this.bounds.getTop() < y && this.bounds.getBottom() >= y;
    }








    /*public void translate(int dx, int dy){

    }

    public void update(main.java.com.pixel.BufferFragment src, main.java.com.pixel.util.Rect copyFrom, main.java.com.pixel.util.Rect copyTo){
        update(src.image, image, copyFrom, copyTo);
    }

    public void update(main.java.com.pixel.BufferFragment src, main.java.com.pixel.util.Rect copyFrom, main.java.com.pixel.util.Rect copyTo, int dx, int dy){
        this.shift(dx, dy);
        update(src.image, image, copyFrom, copyTo);
    }

    private void update(BufferedImage src, BufferedImage dst, main.java.com.pixel.util.Rect copyFrom, main.java.com.pixel.util.Rect copyTo){
        WritableRaster srcRaster = src.getRaster();
        WritableRaster dstRaster = dst.getRaster();
        int[] strip = new int[copyFrom.height * copyFrom.width * ((src.getType() == BufferedImage.TYPE_4BYTE_ABGR) ? 4 : 1)];
        srcRaster.getPixels(copyFrom.x, copyFrom.y, copyFrom.width, copyFrom.height, strip);
        dstRaster.setPixels(copyTo.x, copyTo.y, copyTo.width, copyTo.height, strip);
    }

    private void shift(int dx, int dy){
        shift(image, dx, dy);
        this.bounds.translate(dx, dy);
    }

    private void shift(BufferedImage original, int dx, int dy){
        BufferedImage newImage = new BufferedImage(
                original.getWidth(),
                original.getHeight(),
                original.getType());
        WritableRaster newRaster = newImage.getRaster();
        WritableRaster targetRaster = original.getRaster();
        newRaster.setRect(dx, dy, targetRaster);
        targetRaster.setRect(newRaster);
    }

    public boolean contains(int x, int y){
        return bounds.contains(x, y);
    }

    public main.java.com.pixel.util.Rect getRect(){
        return bounds;
    }*/
}
