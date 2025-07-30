package main.java.com.pixel;

import org.jetbrains.annotations.NotNull;

public enum Quadrant {
    TOP_LEFT, TOP, TOP_RIGHT,
    LEFT, CORE, RIGHT,
    BOTTOM_LEFT, BOTTOM, BOTTOM_RIGHT;

    /**
     * Gets the horizontal counterpart of the diagonal quadrant.
     * @return The horizontal quadrant or null if not applicable.
     */
    public Quadrant getHorizontal() {
        switch (this) {
            case TOP_LEFT:
            case BOTTOM_LEFT:
            case LEFT:
                return LEFT;

            case TOP_RIGHT:
            case BOTTOM_RIGHT:
            case RIGHT:
                return RIGHT;

            default:
                return null;
        }
    }

    /**
     * Gets the vertical counterpart of the diagonal quadrant.
     * @return The vertical quadrant or null if not applicable.
     */
    public Quadrant getVertical() {
        switch (this) {
            case TOP_LEFT:
            case TOP_RIGHT:
            case TOP:
                return TOP;

            case BOTTOM_LEFT:
            case BOTTOM_RIGHT:
            case BOTTOM:
                return BOTTOM;

            default:
                return null;
        }
    }

    public boolean isHorizontal(){
        switch (this){
            case LEFT:
            case RIGHT:
                return true;
            default:
                return false;
        }
    }

    public boolean isDiagonal() {
        switch (this) {
            case TOP_LEFT:
            case TOP_RIGHT:
            case BOTTOM_LEFT:
            case BOTTOM_RIGHT:
                return true;
            case TOP:
            case LEFT:
            case RIGHT:
            case BOTTOM:
            case CORE:
            default:
                return false;
        }
    }

    /**
     * Gets the quadrant corresponding to the provided code (case-insensitive).
     *
     * @param code The code representing the quadrant.
     * @return The corresponding Quadrant.
     */
    public static Quadrant getByCode(@NotNull String code) {
        switch (code.toUpperCase()) {
            case "T":
                return TOP;
            case "B":
                return BOTTOM;
            case "L":
                return LEFT;
            case "R":
                return RIGHT;
            case "TL":
                return TOP_LEFT;
            case "TR":
                return TOP_RIGHT;
            case "BL":
                return BOTTOM_LEFT;
            case "BR":
                return BOTTOM_RIGHT;
            case "C":
                return CORE;
            default:
                return null;
        }
    }

    public String getCode(){
        switch (this) {
            case TOP_LEFT:  return "TL";
            case TOP:       return "T";
            case TOP_RIGHT: return "TR";
            case LEFT:      return "L";
            case CORE:      return "C";
            case RIGHT:     return "R";
            case BOTTOM_LEFT:   return "BL";
            case BOTTOM:        return "B";
            case BOTTOM_RIGHT:  return "BR";
            default:
                return null;
        }
    }
}
