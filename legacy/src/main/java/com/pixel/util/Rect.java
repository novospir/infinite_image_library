package main.java.com.pixel.util;

import java.awt.*;

/**
 * Wrapper class for {@link Rectangle} that provides additional methods
 * for convenience: {@code getRight()} and {@code getBottom()}.
 * <p>
 * The {@code main.java.com.pixel.util.Rect} class extends {@link Rectangle} and adds methods to
 * retrieve the right and bottom boundaries of the rectangle, in addition to the
 * left (x) and top (y) boundaries.
 * </p>
 */
public class Rect extends Rectangle {
     public Rect(int x, int y, int width, int height){
        super(x, y, width, height);
         if (this.width < 0 || this.height < 0) {
             throw new IllegalArgumentException("Width and height must be non-negative");
         }
    }

    public Rect(Rect rect){
        super(rect);
        if (this.width < 0 || this.height < 0) {
            throw new IllegalArgumentException("Width and height must be non-negative");
        }
    }

    /// @return The leftmost inclusive coordinate of the rectangle.
    public int getLeft(){
        return this.x;
    }

    /// @return The topmost inclusive coordinate of the rectangle.
    public int getTop(){
        return this.y;
    }

    /// @return The rightmost inclusive coordinate of the rectangle.
    public int getRight(){
        return this.getLeft() + this.width - 1;
    }

    /// @return The rightmost exclusive coordinate of the rectangle.
    public int getRightExclusive(){
        return this.getLeft() + this.width; // "industry standard", todo: switch to this
    }

    /// @return The bottommost inclusive coordinate of the rectangle.
    public int getBottom(){
        return this.getTop() + this.height - 1;
    }

    /// @return The bottommost exclusive coordinate of the rectangle.
    public int getBottomExclusive(){
        return this.getTop() + this.height; // "industry standard", todo: switch to this
    }

    @Override
    public Rect intersection(Rectangle r) {
        Rectangle rect = super.intersection(r);
        return new Rect(rect.x, rect.y, rect.width, rect.height);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(!(obj instanceof Rect other)) return false;
        return this.x == other.x && this.y == other.y && this.width == other.width && this.height == other.height;
    }
}
