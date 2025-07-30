package main.java.com.pixel;

import main.java.com.pixel.util.Logger;
import main.java.com.pixel.util.Rect;

import java.awt.*;
import java.awt.image.BufferedImage;

public class InfiniteImage extends BufferedImage {

    private int top, bottom, left, right;
    private final ImageSection[] sections;

    public InfiniteImage(){
        super();
        this.sections = new ImageSection[9];
        /*for(int i = 0; i < sections.length; i++){
            this.sections[i] = new ImageSection();
        }*/
        top =-10;
        bottom = 10;
        left = -10;
        right = 10;
        final Rect initialBounds = new Rect(-10, -10, 21, 21);
        final int corner_size = 8;

        // a single buffered image
        this.sections[0] = new ImageSection(Quadrant.TOP_LEFT, new Rect(initialBounds.getLeft(), initialBounds.getTop(), corner_size, corner_size));
        this.sections[2] = new ImageSection(Quadrant.TOP_RIGHT, new Rect(initialBounds.getRightExclusive() - corner_size, initialBounds.getTop(), corner_size, corner_size));
        this.sections[6] = new ImageSection(Quadrant.BOTTOM_LEFT, new Rect(initialBounds.getLeft(), initialBounds.getBottomExclusive() - corner_size, corner_size, corner_size));
        this.sections[8] = new ImageSection(Quadrant.BOTTOM_RIGHT, new Rect(initialBounds.getRightExclusive() - corner_size, initialBounds.getBottomExclusive() - corner_size, corner_size, corner_size));

        // within this library, the core never "grows" - ie stays a single buffered image
        this.sections[4] = new ImageSection(Quadrant.CORE, new Rect(
                this.sections[0].getBounds().getRightExclusive(),
                this.sections[0].getBounds().getBottomExclusive(),
                this.sections[8].getBounds().getLeft() - this.sections[0].getBounds().getRightExclusive(),
                this.sections[8].getBounds().getTop() - this.sections[0].getBounds().getBottomExclusive()
        ));

        final Rect coreBounds = this.sections[Quadrant.CORE.ordinal()].getBounds();

        this.sections[1] = new ImageSection(Quadrant.TOP, new Rect(
                coreBounds.getLeft(), initialBounds.getTop(),
                (int) coreBounds.getWidth(), corner_size
        ));
        this.sections[3] = new ImageSection(Quadrant.LEFT, new Rect(
                initialBounds.getLeft(), coreBounds.getTop(),
                corner_size, (int) coreBounds.getHeight()
        ));
        this.sections[5] = new ImageSection(Quadrant.RIGHT, new Rect(
                coreBounds.getRightExclusive(), coreBounds.getTop(),
                corner_size, (int) coreBounds.getHeight()
        ));
        this.sections[7] = new ImageSection(Quadrant.BOTTOM, new Rect(
                coreBounds.getLeft(), coreBounds.getBottomExclusive(),
                (int) coreBounds.getWidth(), corner_size
        ));
    }

    public static void main(String[] args){
        new InfiniteImage().colorSectionBounds();
    }

    /// test method
    private void colorSectionBounds(){
        Rect bounds = getBounds();
        BufferedImage compiled = new BufferedImage(bounds.width, bounds.height, Config.BUFFER_TYPE);

        int offsetX = -bounds.getLeft();
        int offsetY = -bounds.getTop();

        for(ImageSection section : sections){
            Rect sectionBounds = section.getBounds();
            int color = new Color((int) (Math.random() * 256), (int) (Math.random() * 256) , (int) (Math.random() * 256)).getRGB();

            for(int x = offsetX + sectionBounds.getLeft(); x < offsetX + sectionBounds.getLeft() + sectionBounds.getWidth(); x++)
                for(int y = offsetY + sectionBounds.getTop(); y < offsetY + sectionBounds.getTop() + sectionBounds.getHeight(); y++){
                    compiled.setRGB(x, y, color);
                }
        }

        Logger.log("Fin.");
    }

    public void set(int x, int y, int color){
        this.updateBounds(x, y);
    }

    public void draw(){
        new BufferedImage(0, 0 ,0).createGraphics().draw();
    }

    private void updateBounds(int x, int y) {
        if (x < 0) {
            left = Math.min(left, x);
        } else if (x > 0) {
            right = Math.max(right, x);
        }

        if (y < 0) {
            top = Math.min(top, y);
        } else if (y > 0) {
            bottom = Math.max(bottom, y);
        }
    }

    public Rect getBounds() {
        return new Rect(
                left, top,
                Math.abs(left) + right + 1,
                Math.abs(top) + bottom + 1
        );
    }
}
