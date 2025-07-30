import main.java.com.pixel.BufferFragment;
import main.java.com.pixel.util.Rect;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MainTest {

    /*static final int BUFFER_TYPE = BufferedImage.TYPE_INT_RGB;
    static final int ratio = 1;

    static Map<Integer, List<BufferFragment>> allNodes;
    static Rect sectionBounds;
    static int FRAGMENT_HEIGHT, ROOT_WIDTH;
    static boolean runningAllTests = true;

    //@BeforeAll
    static void init() {
        //System.out.println("Init");
        allNodes = new HashMap<>();
        sectionBounds = new Rect(0, 0, 11 * ratio, 10);
        FRAGMENT_HEIGHT = 2;
        int MIN_WIDTH = 2;
        int currentX = 0;

        BufferFragment created;
        created = Main.createFragment(sectionBounds,
                new Rect(currentX, 0, ratio, FRAGMENT_HEIGHT),
                (sectionBounds.width - 1) / 2,
                0,
                BUFFER_TYPE,
                MIN_WIDTH,
                allNodes
        );
        currentX += ratio;
        Main.fill(created, Color.CYAN.getRGB());

        created = Main.createFragment(sectionBounds,
                new Rect(currentX, 0, 2 * ratio, FRAGMENT_HEIGHT),
                (sectionBounds.width - 1) / 2,
                0,
                BUFFER_TYPE,
                MIN_WIDTH,
                allNodes
        );
        currentX += 2 * ratio;
        Main.fill(created, Color.ORANGE.getRGB());

        created = Main.createFragment(sectionBounds,
                new Rect(currentX, 0, 4 * ratio, FRAGMENT_HEIGHT),
                (sectionBounds.width - 1) / 2,
                0,
                BUFFER_TYPE,
                MIN_WIDTH,
                allNodes
        );
        currentX += 4 * ratio;
        Main.fill(created, Color.RED.getRGB());

        created = Main.createFragment(sectionBounds,
                new Rect(currentX, 0, 4 * ratio, FRAGMENT_HEIGHT),
                (sectionBounds.width - 1) / 2,
                0,
                BUFFER_TYPE,
                MIN_WIDTH,
                allNodes
        );
        Main.fill(created, Color.PINK.getRGB());

        created = Main.createFragment(sectionBounds,
                new Rect(0, FRAGMENT_HEIGHT, 2 * ratio, FRAGMENT_HEIGHT),
                ((sectionBounds.width - 1) / 2) / 2,
                1,
                BUFFER_TYPE,
                MIN_WIDTH,
                allNodes
        );
        Main.fill(created, Color.GREEN.getRGB());

        created = Main.createFragment(sectionBounds,
                new Rect(7 * ratio, FRAGMENT_HEIGHT, 2 * ratio, FRAGMENT_HEIGHT),
                ((sectionBounds.width - 1) / 2) / 2,
                1,
                BUFFER_TYPE,
                MIN_WIDTH,
                allNodes
        );
        Main.fill(created, Color.GREEN.getRGB());
    }

    @BeforeAll
    static void initV2() {
        allNodes = new HashMap<>();
        sectionBounds = new Rect(0, 0, 17 * ratio, 10);
        ROOT_WIDTH = (sectionBounds.width - 1) / 2;
        FRAGMENT_HEIGHT = 2;
        int MIN_WIDTH = 2;
        int currentX = 0;

        BufferFragment created;
        created = Main.createFragment(sectionBounds,
                new Rect(0, 0, 4, FRAGMENT_HEIGHT),
                (sectionBounds.width - 1) / 2,
                0,
                BUFFER_TYPE,
                MIN_WIDTH,
                allNodes
        );
        //currentX+=ratio;
        Main.fill(created, Color.red.getRGB());

        created = Main.createFragment(sectionBounds,
                new Rect(2, 2, 2, FRAGMENT_HEIGHT),
                (sectionBounds.width - 1) / 2,
                1,
                BUFFER_TYPE,
                MIN_WIDTH,
                allNodes
        );
        //currentX+=ratio;
        Main.fill(created, new Color(143, 137, 204).getRGB());

        created = Main.createFragment(sectionBounds,
                new Rect(2, 4, 2, FRAGMENT_HEIGHT),
                (sectionBounds.width - 1) / 2,
                2,
                BUFFER_TYPE,
                MIN_WIDTH,
                allNodes
        );
        //currentX+=ratio;
        Main.fill(created, Color.cyan.getRGB());

        created = Main.createFragment(sectionBounds,
                new Rect(4, 0, 8, FRAGMENT_HEIGHT),
                (sectionBounds.width - 1) / 2,
                0,
                BUFFER_TYPE,
                MIN_WIDTH,
                allNodes
        );
        currentX += 2 * ratio;
        Main.fill(created, Color.blue.getRGB());

        created = Main.createFragment(sectionBounds,
                new Rect(8, 2, 4 * ratio, FRAGMENT_HEIGHT),
                (sectionBounds.width - 1) / 2,
                1,
                BUFFER_TYPE,
                MIN_WIDTH,
                allNodes
        );
        currentX += 4 * ratio;
        Main.fill(created, Color.pink.getRGB());

        created = Main.createFragment(sectionBounds,
                new Rect(10, 4, 2, FRAGMENT_HEIGHT),
                (sectionBounds.width - 1) / 2,
                2,
                BUFFER_TYPE,
                MIN_WIDTH,
                allNodes
        );
        Main.fill(created, Color.green.getRGB());

        created = Main.createFragment(sectionBounds,
                new Rect(11, 6, 2, FRAGMENT_HEIGHT),
                ((sectionBounds.width - 1) / 2) / 2,
                3,
                BUFFER_TYPE,
                MIN_WIDTH,
                allNodes
        );
        Main.fill(created, Color.MAGENTA.getRGB());

        created = Main.createFragment(sectionBounds,
                new Rect(4, 2, 4, FRAGMENT_HEIGHT),
                ((sectionBounds.width - 1) / 2) / 2,
                1,
                BUFFER_TYPE,
                MIN_WIDTH,
                allNodes
        );
        Main.fill(created, Color.gray.getRGB());
    }

    @BeforeEach
    void setup() {
        //System.out.println("Set up");
    }

    @Test
    void overflowRight() {
        Assertions.assertEquals(new Rect(4, 4, 2, 2),
                Main.test(new Main.GlobalData(new HashMap<>(allNodes), sectionBounds, FRAGMENT_HEIGHT, BUFFER_TYPE), 4, 4));
    }

    @Test
    void overflowLeft() {
        Assertions.assertEquals(new Rect(8, 4, 2, 2),
                Main.test(new Main.GlobalData(new HashMap<>(allNodes), sectionBounds, FRAGMENT_HEIGHT, BUFFER_TYPE), 9, 4));
    }

    @Test
    void smartAllocateRight() {
         Assertions.assertEquals(new Rect(6, 4, 2, 2), // snap-left
                Main.test(new Main.GlobalData(new HashMap<>(allNodes), sectionBounds, FRAGMENT_HEIGHT, BUFFER_TYPE), 7, 4));
    }

    @Test
    void smartAllocateLeft() {
        Assertions.assertEquals(new Rect(6, 4, 2, 2),
                Main.test(new Main.GlobalData(new HashMap<>(allNodes), sectionBounds, FRAGMENT_HEIGHT, BUFFER_TYPE), 6, 4));
    }

    @Test
    void parentAlign() {
        Assertions.assertEquals(new Rect(8, 4, 2, 2), // parent-align
                Main.test(new Main.GlobalData(new HashMap<>(allNodes), sectionBounds, FRAGMENT_HEIGHT, BUFFER_TYPE), 8, 4));
    }


    // Additional tests for closestSiblings method

    @Test
    void closestSiblingsNoSiblings() {
        // Test when there are no siblings at the specified depth
        Map<Integer, List<BufferFragment>> emptyNodes = new HashMap<>();
        Main.ClosestSiblings closestSiblings = Main.findClosestSiblings(emptyNodes, 1, new Rect(5, 2, 2, 2));

        // Both siblings should be null
        Assertions.assertNull(closestSiblings.left);
        Assertions.assertNull(closestSiblings.right);
    }

    @Test
    void closestSiblingsOnlyLeftSibling() {
        // Test when there's only a left sibling
        Map<Integer, List<BufferFragment>> testNodes = new HashMap<>();
        List<BufferFragment> fragments = new ArrayList<>();

        // Create a left sibling
        BufferFragment leftFragment = Main.createFragment(sectionBounds,
                new Rect(0, 2, 2, 2),
                4,
                1,
                BUFFER_TYPE,
                2,
                testNodes
        );

        Main.ClosestSiblings closestSiblings = Main.findClosestSiblings(testNodes, 1, new Rect(5, 2, 2, 2));

        // Left sibling should be found, right should be null
        Assertions.assertEquals(new Rect(0, 2, 2, 2), closestSiblings.left.bounds());
        Assertions.assertEquals(3, closestSiblings.left.distance());
        Assertions.assertNull(closestSiblings.right);
    }

    @Test
    void closestSiblingsOnlyRightSibling() {
        // Test when there's only a right sibling
        Map<Integer, List<BufferFragment>> testNodes = new HashMap<>();

        // Create a right sibling
        BufferFragment rightFragment = Main.createFragment(sectionBounds,
                new Rect(7, 2, 2, 2),
                4,
                1,
                BUFFER_TYPE,
                2,
                testNodes
        );

        Main.ClosestSiblings closestSiblings = Main.findClosestSiblings(testNodes, 1, new Rect(3, 2, 2, 2));

        // Right sibling should be found, left should be null
        Assertions.assertNull(closestSiblings.left);
        Assertions.assertEquals(new Rect(7, 2, 2, 2), closestSiblings.right.bounds());
        Assertions.assertEquals(2, closestSiblings.right.distance());
    }

    @Test
    void closestSiblingsFarAway() {
        // Test when siblings are far away
        Map<Integer, List<BufferFragment>> testNodes = new HashMap<>();

        // Create distant siblings
        BufferFragment leftFragment = Main.createFragment(sectionBounds,
                new Rect(0, 2, 1, 2),
                4,
                1,
                BUFFER_TYPE,
                1,
                testNodes
        );

        BufferFragment rightFragment = Main.createFragment(sectionBounds,
                new Rect(15, 2, 1, 2),
                4,
                1,
                BUFFER_TYPE,
                1,
                testNodes
        );

        Main.ClosestSiblings closestSiblings = Main.findClosestSiblings(testNodes, 1, new Rect(7, 2, 2, 2));

        // Both siblings should be found with large distances
        Assertions.assertEquals(new Rect(0, 2, 1, 2), closestSiblings.left.bounds());
        Assertions.assertEquals(6, closestSiblings.left.distance());
        Assertions.assertEquals(new Rect(15, 2, 1, 2), closestSiblings.right.bounds());
        Assertions.assertEquals(6, closestSiblings.right.distance());
    }

    // Tests for getOrCreate method using Main.test()

    @Test
    void testGetOrCreateExistingFragment() {
        // Test finding an existing fragment
        Map<Integer, List<BufferFragment>> testNodes = new HashMap<>(allNodes);

        // The fragment at (2, 4) already exists from initV2
        Rect result = Main.test(new Main.GlobalData(testNodes, sectionBounds, FRAGMENT_HEIGHT, BUFFER_TYPE), 3, 4);

        // Should find the existing fragment
        assertNotNull(result);
        Assertions.assertEquals(new Rect(2, 4, 2, FRAGMENT_HEIGHT), result);
    }

    @Test
    void testGetOrCreateNewFragmentWithParent() {
        // Test creating a new fragment when a parent exists
        Map<Integer, List<BufferFragment>> testNodes = new HashMap<>(allNodes);

        // Create a new fragment at a position where a parent exists but no fragment exists yet
        // The fragment at (4, 0) with width 8 is a parent for fragments at depth 1
        Rect result = Main.test(new Main.GlobalData(testNodes, sectionBounds, FRAGMENT_HEIGHT, BUFFER_TYPE), 5, 2);

        // Should create a new fragment
        assertNotNull(result);
        // The bounds should be determined by the parent and siblings
        Assertions.assertEquals(FRAGMENT_HEIGHT, result.height);
    }

    @Test
    void testGetOrCreateOutOfBounds() {
        // Test creating a fragment outside the section bounds
        Map<Integer, List<BufferFragment>> testNodes = new HashMap<>(allNodes);

        // Make sure we have empty lists for all depths that might be accessed
        int depth = 20 / FRAGMENT_HEIGHT;
        for (int i = 0; i <= depth; i++) {
            if (!testNodes.containsKey(i)) {
                testNodes.put(i, new ArrayList<>());
            }
        }

        try {
            // Try to create a fragment outside the section bounds
            // This will throw an ArrayIndexOutOfBoundsException when trying to set RGB values
            // outside the bounds of the image, which is expected behavior
            Rect result = Main.test(new Main.GlobalData(testNodes, sectionBounds, FRAGMENT_HEIGHT, BUFFER_TYPE), 20, 20);

            // If we get here, the test should fail because we expected an exception
            Assertions.fail("Expected ArrayIndexOutOfBoundsException was not thrown");
        } catch (ArrayIndexOutOfBoundsException e) {
            // This is the expected behavior, so the test passes
            Assertions.assertTrue(e.getMessage().contains("Coordinate out of bounds"));
        }
    }

    @Test
    void testGetOrCreateNoParent() {
        // Test creating a fragment when no parent exists
        Map<Integer, List<BufferFragment>> testNodes = new HashMap<>();

        // Add empty lists for all depths that might be accessed
        int depth = 6 / FRAGMENT_HEIGHT;
        for (int i = 0; i <= depth; i++) {
            testNodes.put(i, new ArrayList<>());
        }

        // Try to create a fragment at depth > 0 with no parent
        Rect result = Main.test(new Main.GlobalData(testNodes, sectionBounds, FRAGMENT_HEIGHT, BUFFER_TYPE), 5, 6);

        // Should return null as there's no parent
        assertNull(result);
    }

    @Test
    void randomClosestSiblingsTests(){
        Main.ClosestSiblings closestSiblings = Main.findClosestSiblings(new HashMap<>(allNodes), 2, new Rect(5, 4, 2, 2));
        Assertions.assertEquals(new Rect(2, 4, 2, 2), closestSiblings.left.bounds());
        Assertions.assertEquals(new Rect(10, 4, 2, 2), closestSiblings.right.bounds());

        Assertions.assertEquals(1, closestSiblings.left.distance());
        Assertions.assertEquals(3, closestSiblings.right.distance());

        Main.ClosestSiblings closestSiblings2 = Main.findClosestSiblings(new HashMap<>(allNodes), 2, new Rect(7, 4, 2, 2));
        Assertions.assertEquals(new Rect(2, 4, 2, 2), closestSiblings2.left.bounds());
        Assertions.assertEquals(new Rect(10, 4, 2, 2), closestSiblings2.right.bounds());
        Assertions.assertEquals(3, closestSiblings2.left.distance());
        Assertions.assertEquals(1, closestSiblings2.right.distance());


    }*/
}