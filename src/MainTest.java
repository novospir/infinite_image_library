import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

class MainTest {

    static final int BUFFER_TYPE = BufferedImage.TYPE_INT_RGB;
    static final int ratio = 1;

    static Map<Integer, List<BufferFragment>> allNodes;
    static Rect sectionBounds;
    static int FRAGMENT_HEIGHT;

    @BeforeAll
    static void init(){
        //System.out.println("Init");
        allNodes = new HashMap<>();
        sectionBounds = new Rect(0, 0, 11*ratio, 10);
        FRAGMENT_HEIGHT = 2;
        int MIN_WIDTH = 2;
        int currentX = 0;

        BufferFragment created;
        created = Main.createFragment(
                new Rect(currentX, 0, ratio, FRAGMENT_HEIGHT),
                (sectionBounds.width-1)/2,
                0,
                BUFFER_TYPE,
                MIN_WIDTH,
                allNodes
        );
        currentX+=ratio;
        Main.fill(created, Color.CYAN.getRGB());

        created = Main.createFragment(
                new Rect(currentX, 0, 2*ratio, FRAGMENT_HEIGHT),
                (sectionBounds.width-1)/2,
                0,
                BUFFER_TYPE,
                MIN_WIDTH,
                allNodes
        );
        currentX+=2*ratio;
        Main.fill(created, Color.ORANGE.getRGB());

        created = Main.createFragment(
                new Rect(currentX, 0, 4*ratio, FRAGMENT_HEIGHT),
                (sectionBounds.width-1)/2,
                0,
                BUFFER_TYPE,
                MIN_WIDTH,
                allNodes
        );
        currentX+=4*ratio;
        Main.fill(created, Color.RED.getRGB());

        created = Main.createFragment(
                new Rect(currentX, 0, 4*ratio, FRAGMENT_HEIGHT),
                (sectionBounds.width-1)/2,
                0,
                BUFFER_TYPE,
                MIN_WIDTH,
                allNodes
        );
        Main.fill(created, Color.PINK.getRGB());

        created = Main.createFragment(
                new Rect(0, FRAGMENT_HEIGHT, 2*ratio, FRAGMENT_HEIGHT),
                ((sectionBounds.width-1)/2)/2,
                1,
                BUFFER_TYPE,
                MIN_WIDTH,
                allNodes
        );
        Main.fill(created, Color.GREEN.getRGB());

        created = Main.createFragment(
                new Rect(7*ratio, FRAGMENT_HEIGHT, 2*ratio, FRAGMENT_HEIGHT),
                ((sectionBounds.width-1)/2)/2,
                1,
                BUFFER_TYPE,
                MIN_WIDTH,
                allNodes
        );
        Main.fill(created, Color.GREEN.getRGB());
    }

    @BeforeEach
    void setup(){
        //System.out.println("Set up");
    }

    @Test
    void overflowRight(){
        Main.ClosestSiblings closestSiblings = Main.findClosestSiblings(new HashMap<>(allNodes), 1, new Rect(1, 2, 2, 2));
        Assertions.assertEquals(new Rect(0,2,2,2), closestSiblings.left.bounds());
        Assertions.assertEquals(new Rect(7,2,2,2), closestSiblings.right.bounds());

        Assertions.assertEquals(1, closestSiblings.left.distance());
        Assertions.assertEquals(4, closestSiblings.right.distance());

        Assertions.assertEquals(new Rect(2,2,2,2),
                Main.test(new Main.GlobalData(new HashMap<>(allNodes), sectionBounds, FRAGMENT_HEIGHT, BUFFER_TYPE), 2*ratio, 2));
    }

    @Test
    void smartAllocateLeft(){
        Main.ClosestSiblings closestSiblings = Main.findClosestSiblings(new HashMap<>(allNodes), 1, new Rect(3, 2, 2, 2));
        Assertions.assertEquals(new Rect(0,2,2,2), closestSiblings.left.bounds());
        Assertions.assertEquals(new Rect(7,2,2,2), closestSiblings.right.bounds());

        Assertions.assertEquals(1, closestSiblings.left.distance());
        Assertions.assertEquals(2, closestSiblings.right.distance());

        Assertions.assertEquals(new Rect(2,2,2,2), // snap-left
                Main.test(new Main.GlobalData(new HashMap<>(allNodes), sectionBounds, FRAGMENT_HEIGHT, BUFFER_TYPE), 3*ratio, 2));

    }

    @Test
    void parentAlign(){
        Main.ClosestSiblings closestSiblings = Main.findClosestSiblings(new HashMap<>(allNodes), 1, new Rect(3, 2, 2, 2));
        Assertions.assertEquals(new Rect(0,2,2,2), closestSiblings.left.bounds());
        Assertions.assertEquals(new Rect(7,2,2,2), closestSiblings.right.bounds());

        Assertions.assertEquals(1, closestSiblings.left.distance());
        Assertions.assertEquals(2, closestSiblings.right.distance());

        Assertions.assertEquals(new Rect(3,2,2,2), // parent-align
                Main.test(new Main.GlobalData(new HashMap<>(allNodes), sectionBounds, FRAGMENT_HEIGHT, BUFFER_TYPE), 4*ratio, 2));

    }

    @Test
    void smartAllocateRight(){
        final Rect left = new Rect(0, 2, 2, 2);
        final Rect right = new Rect(7, 2, 2, 2);
        final Rect expected = new Rect(5, 2, 2, 2);

        Main.ClosestSiblings closestSiblings = Main.findClosestSiblings(new HashMap<>(allNodes), 1, expected);
        Assertions.assertEquals(left, closestSiblings.left.bounds());
        Assertions.assertEquals(right, closestSiblings.right.bounds());

        Assertions.assertEquals(expected.getLeft() - left.getRightExclusive(), closestSiblings.left.distance());
        Assertions.assertEquals(expected.getRightExclusive() - right.getLeft(), closestSiblings.right.distance());

        Assertions.assertEquals(expected, Main.test(new Main.GlobalData(// snap-right
               new HashMap<>(allNodes), sectionBounds, FRAGMENT_HEIGHT, BUFFER_TYPE), 5*ratio, 2));

    }

    @Test
    void overflowLeft(){
        Main.ClosestSiblings closestSiblings = Main.findClosestSiblings(new HashMap<>(allNodes), 1, new Rect(5, 2, 2, 2));
        Assertions.assertEquals(new Rect(0,2,2,2), closestSiblings.left.bounds());
        Assertions.assertEquals(new Rect(7,2,2,2), closestSiblings.right.bounds());

        Assertions.assertEquals(3, closestSiblings.left.distance());
        Assertions.assertEquals(0, closestSiblings.right.distance());

        Assertions.assertEquals(new Rect(5,2,2,2), // overflow-left
                Main.test(new Main.GlobalData(new HashMap<>(allNodes), sectionBounds, FRAGMENT_HEIGHT, BUFFER_TYPE), 6*ratio, 2));

    }
}