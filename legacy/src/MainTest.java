import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class MainTest {

    static final int BUFFER_TYPE = BufferedImage.TYPE_INT_RGB;
    static final int ratio = 1;

    static Map<Integer, List<BufferFragment>> allNodes2;
    static Map<Integer, List<BufferFragment>> allNodes;
    static Rect sectionBounds;
    static Rect sectionBounds2;
    static int FRAGMENT_HEIGHT;
    static boolean runningAllTests = false;

    @BeforeAll
    static void init() {
        allNodes = new HashMap<>();
        sectionBounds = new Rect(0, 0, 11 * ratio, 10);
        FRAGMENT_HEIGHT = 2;
        int MIN_WIDTH = 2;
        int currentX = 0;

        BufferFragment created;
        created = Main.createFragment(
                new Rect(currentX, 0, ratio, FRAGMENT_HEIGHT),
                (sectionBounds.width - 1) / 2,
                0,
                BUFFER_TYPE,
                MIN_WIDTH,
                allNodes
        );
        currentX += ratio;
        Main.fill(created, Color.CYAN.getRGB());

        created = Main.createFragment(
                new Rect(currentX, 0, 2 * ratio, FRAGMENT_HEIGHT),
                (sectionBounds.width - 1) / 2,
                0,
                BUFFER_TYPE,
                MIN_WIDTH,
                allNodes
        );
        currentX += 2 * ratio;
        Main.fill(created, Color.ORANGE.getRGB());

        created = Main.createFragment(
                new Rect(currentX, 0, 4 * ratio, FRAGMENT_HEIGHT),
                (sectionBounds.width - 1) / 2,
                0,
                BUFFER_TYPE,
                MIN_WIDTH,
                allNodes
        );
        currentX += 4 * ratio;
        Main.fill(created, Color.RED.getRGB());

        created = Main.createFragment(
                new Rect(currentX, 0, 4 * ratio, FRAGMENT_HEIGHT),
                (sectionBounds.width - 1) / 2,
                0,
                BUFFER_TYPE,
                MIN_WIDTH,
                allNodes
        );
        Main.fill(created, Color.PINK.getRGB());

        created = Main.createFragment(
                new Rect(0, FRAGMENT_HEIGHT, 2 * ratio, FRAGMENT_HEIGHT),
                ((sectionBounds.width - 1) / 2) / 2,
                1,
                BUFFER_TYPE,
                MIN_WIDTH,
                allNodes
        );
        Main.fill(created, Color.GREEN.getRGB());

        created = Main.createFragment(
                new Rect(8 * ratio, FRAGMENT_HEIGHT, 2 * ratio, FRAGMENT_HEIGHT),
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
        allNodes2 = new HashMap<>();
        sectionBounds2 = new Rect(0, 0, 17 * ratio, 10);
        FRAGMENT_HEIGHT = 2;
        int MIN_WIDTH = 2;
        int currentX = 0;

        BufferFragment created;
        created = Main.createFragment(
                new Rect(0, 0, 4, FRAGMENT_HEIGHT),
                (sectionBounds2.width - 1) / 2,
                0,
                BUFFER_TYPE,
                MIN_WIDTH,
                allNodes2
        );
        Main.fill(created, Color.red.getRGB());

        created = Main.createFragment(
                new Rect(2, 2, 2, FRAGMENT_HEIGHT),
                (sectionBounds2.width - 1) / 2,
                1,
                BUFFER_TYPE,
                MIN_WIDTH,
                allNodes2
        );
        Main.fill(created, new Color(143, 137, 204).getRGB());

        created = Main.createFragment(
                new Rect(2, 4, 2, FRAGMENT_HEIGHT),
                (sectionBounds2.width - 1) / 2,
                2,
                BUFFER_TYPE,
                MIN_WIDTH,
                allNodes2
        );
        Main.fill(created, Color.cyan.getRGB());

        created = Main.createFragment(
                new Rect(4, 0, 8, FRAGMENT_HEIGHT),
                (sectionBounds2.width - 1) / 2,
                0,
                BUFFER_TYPE,
                MIN_WIDTH,
                allNodes2
        );
        Main.fill(created, Color.blue.getRGB());

        created = Main.createFragment(
                new Rect(8, 2, 4 * ratio, FRAGMENT_HEIGHT),
                (sectionBounds2.width - 1) / 2,
                1,
                BUFFER_TYPE,
                MIN_WIDTH,
                allNodes2
        );
        Main.fill(created, Color.pink.getRGB());

        created = Main.createFragment(
                new Rect(10, 4, 2, FRAGMENT_HEIGHT),
                (sectionBounds2.width - 1) / 2,
                2,
                BUFFER_TYPE,
                MIN_WIDTH,
                allNodes2
        );
        Main.fill(created, Color.green.getRGB());

        created = Main.createFragment(
                new Rect(11, 6, 2, FRAGMENT_HEIGHT),
                ((sectionBounds2.width - 1) / 2) / 2,
                3,
                BUFFER_TYPE,
                MIN_WIDTH,
                allNodes2
        );
        Main.fill(created, Color.MAGENTA.getRGB());

        created = Main.createFragment(
                new Rect(4, 2, 4, FRAGMENT_HEIGHT),
                ((sectionBounds2.width - 1) / 2) / 2,
                1,
                BUFFER_TYPE,
                MIN_WIDTH,
                allNodes2
        );
        Main.fill(created, Color.gray.getRGB());
    }

    @BeforeEach
    void setup() {
        //System.out.println("Set up");
    }

    //these tests all use initV2 as their setup

    @Test
    void overflowRight() {
        Assertions.assertEquals(new Rect(4, 4, 2, 2),
                Main.test(new Main.GlobalData(new HashMap<>(allNodes2), sectionBounds2, FRAGMENT_HEIGHT, BUFFER_TYPE), 4, 4));
    }

    @Test
    void overflowLeft() {
        Assertions.assertEquals(new Rect(8, 4, 2, 2),
                Main.test(new Main.GlobalData(new HashMap<>(allNodes2), sectionBounds2, FRAGMENT_HEIGHT, BUFFER_TYPE), 9, 4));
    }

    @Test
    void smartAllocateRight() {
        Assertions.assertEquals(new Rect(6, 4, 2, 2),
                Main.test(new Main.GlobalData(new HashMap<>(allNodes2), sectionBounds2, FRAGMENT_HEIGHT, BUFFER_TYPE), 7, 4));
    }

    @Test
    void smartAllocateLeft() {
        Assertions.assertEquals(new Rect(6, 4, 2, 2),
                Main.test(new Main.GlobalData(new HashMap<>(allNodes2), sectionBounds2, FRAGMENT_HEIGHT, BUFFER_TYPE), 6, 4));
    }


    @Test
    void parentAlign() {

        Assertions.assertEquals(new Rect(8, 4, 2, 2),
                Main.test(new Main.GlobalData(new HashMap<>(allNodes2), sectionBounds2, FRAGMENT_HEIGHT, BUFFER_TYPE), 8, 4));
    }

    @Test
    void placingOutOfBounds() {
        // Test for trying to insert a block out of bounds
        Map<Integer, List<BufferFragment>> testNodes = new HashMap<>(allNodes2);
        try {
            Main.test(new Main.GlobalData(testNodes, sectionBounds2, FRAGMENT_HEIGHT, BUFFER_TYPE), 20, 20);
            Assertions.fail("Expected ArrayIndexOutOfBoundsException was not thrown");
        } catch (ArrayIndexOutOfBoundsException e) {
            Assertions.assertTrue(e.getMessage().contains("Coordinate out of bounds"));
        }
    }

    //these corner case tests use init and therefore allNodes as their setup

    @Test
    void overflowExtendsOutOfBounds() {
        // Test for trying to insert a block that if the correct size would extend out of bounds
        Assertions.assertEquals(new Rect(10, 2, 2, 2),
                Main.test(new Main.GlobalData(allNodes, sectionBounds, FRAGMENT_HEIGHT, BUFFER_TYPE), 10, 2));
    }

    @Test
    void childOfParentWidthOfOne(){
        //Test making sure that when inserting a child of a parent that has a width of one, it still works and just
        //gives the child a width of one and doesn't crash
        BufferFragment created = Main.createFragment(
                new Rect(2, 2, 1, FRAGMENT_HEIGHT),
                (sectionBounds.width - 1) / 2,
                1,
                BUFFER_TYPE,
                1,
                allNodes
        );
        Main.fill(created, Color.gray.getRGB());
        Assertions.assertEquals(new Rect(2, 4, 1, 2),
                Main.test(new Main.GlobalData(allNodes, sectionBounds, FRAGMENT_HEIGHT, BUFFER_TYPE), 2, 4));
        //Cleanup added block
        allNodes.get(1).removeIf(fragment -> fragment.getBounds().equals(new Rect(2, 2, 1, FRAGMENT_HEIGHT)));
    }

    @Test
    void notEnoughRoomToPlace() {
        // Test for trying to insert a block where there is not enough room to place it (when getting the size from a parent)
        //Arranging init to only have a space 1 wide available at (5,2)
        BufferFragment created = Main.createFragment(
                new Rect(2, 2, 2, FRAGMENT_HEIGHT),
                (sectionBounds.width - 1) / 2,
                1,
                BUFFER_TYPE,
                2,
                allNodes
        );
        Main.fill(created, Color.CYAN.getRGB());
        created = Main.createFragment(
                new Rect(4, 2, 1, FRAGMENT_HEIGHT),
                (sectionBounds.width - 1) / 2,
                1,
                BUFFER_TYPE,
                1,
                allNodes
        );
        Main.fill(created, Color.gray.getRGB());
        created = Main.createFragment(
                new Rect(6, 2, 2, FRAGMENT_HEIGHT),
                (sectionBounds.width - 1) / 2,
                0,
                BUFFER_TYPE,
                2,
                allNodes
        );
        Main.fill(created, Color.orange.getRGB());
        // Act
        Assertions.assertEquals(new Rect(5, 2, 1, 2),
                Main.test(new Main.GlobalData(allNodes, sectionBounds, FRAGMENT_HEIGHT, BUFFER_TYPE), 5, 2));
        //Cleanup added blocks
        allNodes.get(1).removeIf(fragment -> fragment.getBounds().equals(new Rect(4, 2, 1, FRAGMENT_HEIGHT)));
        allNodes.get(0).removeIf(fragment -> fragment.getBounds().equals(new Rect(6, 2, 2, FRAGMENT_HEIGHT)));
        allNodes.get(1).removeIf(fragment -> fragment.getBounds().equals(new Rect(2, 2, 2, FRAGMENT_HEIGHT)));
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

        // Create a left sibling
        BufferFragment leftFragment = Main.createFragment(
                new Rect(0, 2, 2, 2),
                4,
                1,
                BUFFER_TYPE,
                2,
                testNodes
        );

        Main.ClosestSiblings closestSiblings = Main.findClosestSiblings(testNodes, 1, new Rect(5, 2, 2, 2));

        Assertions.assertEquals(new Rect(0, 2, 2, 2), closestSiblings.left.bounds());
        Assertions.assertEquals(3, closestSiblings.left.distance());
        Assertions.assertNull(closestSiblings.right);
    }

    @Test
    void closestSiblingsOnlyRightSibling() {
        // Test when there's only a right sibling
        Map<Integer, List<BufferFragment>> testNodes = new HashMap<>();

        // Create a right sibling
        BufferFragment rightFragment = Main.createFragment(
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
        BufferFragment leftFragment = Main.createFragment(
                new Rect(0, 2, 1, 2),
                4,
                1,
                BUFFER_TYPE,
                1,
                testNodes
        );

        BufferFragment rightFragment = Main.createFragment(
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
        // The fragment at (3, 0) already exists from init
        Rect result = Main.test(new Main.GlobalData(allNodes, sectionBounds, FRAGMENT_HEIGHT, BUFFER_TYPE), 4, 0);

        // Should find the existing fragment
        assertNotNull(result);
        Assertions.assertEquals(new Rect(3, 0, 4, FRAGMENT_HEIGHT), result);
    }

    @Test
    void testGetOrCreateOutOfBounds() {
        // Test creating a fragment outside the section bounds
        Map<Integer, List<BufferFragment>> testNodes = new HashMap<>(allNodes);

        // Make sure we have empty lists for all depths that might be accessed to avoid NullPointerException
        int depth = 20 / FRAGMENT_HEIGHT;
        for (int i = 0; i <= depth; i++) {
            if (!testNodes.containsKey(i)) {
                testNodes.put(i, new ArrayList<>());
            }
        }

        try {
            // Try to create a fragment outside the section bounds throwing an ArrayIndexOutOfBoundsException
            Rect result = Main.test(new Main.GlobalData(testNodes, sectionBounds, FRAGMENT_HEIGHT, BUFFER_TYPE), 20, 20);

            // If we get here, the test should fail because we expected an exception
            Assertions.fail("Expected ArrayIndexOutOfBoundsException was not thrown");
        } catch (ArrayIndexOutOfBoundsException e) {
            Assertions.assertTrue(e.getMessage().contains("Coordinate out of bounds"));
        }
    }

    @Test
    void testGetOrCreateNoParent() {
        // Test creating a fragment when no parent exists
        Map<Integer, List<BufferFragment>> testNodes = new HashMap<>();

        // Add empty lists for all depths that might be accessed to avoid NullPointerException
        int depth = 6 / FRAGMENT_HEIGHT;
        for (int i = 0; i <= depth; i++) {
            testNodes.put(i, new ArrayList<>());
        }

        // Try to create a fragment with no parent
        Rect result = Main.test(new Main.GlobalData(testNodes, sectionBounds, FRAGMENT_HEIGHT, BUFFER_TYPE), 5, 6);

        // Should return null as there's no parent
        Assertions.assertNull(result);
    }

    @Test
    void testRandomBlockGeneration() {
        //maybe add more seeds for other cases?
        Random random = new Random(12345);
        Map<Integer, List<BufferFragment>> randomBlocks = new HashMap<>();
        Rect testBounds = new Rect(0, 0, 17, 16);
        int testFragmentHeight = 2;

        // Generate random blocks at depth 0
        int currentX = 0;
        List<Rect> depthZeroBlocks = new ArrayList<>();

        while (currentX < testBounds.width - 2) {
            int width = random.nextInt(4) + 1;
            if (currentX + width > testBounds.width) {
                width = testBounds.width - currentX;
            }

            Rect blockBounds = new Rect(currentX, 0, width, testFragmentHeight);
            depthZeroBlocks.add(blockBounds);

            BufferFragment fragment = Main.createFragment(
                    blockBounds,
                    testBounds.width / 2,
                    0,
                    BUFFER_TYPE,
                    1,
                    randomBlocks
            );
            Main.fill(fragment, new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)).getRGB());

            currentX += width;
        }

        // Generate blocks at various depths under parents
        List<List<Rect>> blocksByDepth = new ArrayList<>();
        blocksByDepth.add(new ArrayList<>(depthZeroBlocks));

        for (int depth = 1; depth <= 3; depth++) {
            List<Rect> currentDepthBlocks = new ArrayList<>();
            List<Rect> parentBlocks = blocksByDepth.get(depth - 1);

            for (Rect parentBlock : parentBlocks) {
                if (random.nextBoolean()) { // 50% chance to create child
                    int childWidth = Math.max(1, parentBlock.width / 2);
                    int childX = parentBlock.getLeft() + random.nextInt(Math.max(1, parentBlock.width - childWidth + 1));

                    Rect childBounds = new Rect(childX, testFragmentHeight * depth, childWidth, testFragmentHeight);
                    currentDepthBlocks.add(childBounds);

                    BufferFragment childFragment = Main.createFragment(
                            childBounds,
                            parentBlock.width / 2,
                            depth,
                            BUFFER_TYPE,
                            1,
                            randomBlocks
                    );
                    Main.fill(childFragment, new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)).getRGB());
                }
            }
            blocksByDepth.add(currentDepthBlocks);
        }

        // Test random points that have parents but don't have another block there yet
        for (int testRun = 0; testRun < 15; testRun++) {
            // Pick a random depth (1-4) to test at
            int testDepth = random.nextInt(4) + 1;

            // Skip if no parent blocks exist
            if (testDepth - 1 >= blocksByDepth.size() || blocksByDepth.get(testDepth - 1).isEmpty()) {
                continue;
            }

            // Find all blocks at the parent depth
            List<Rect> parentBlocks = blocksByDepth.get(testDepth - 1);
            int parentIndex = random.nextInt(parentBlocks.size());
            Rect parentBlock = parentBlocks.get(parentIndex);

            // Generate test point within the parent's X range
            int testX = parentBlock.getLeft() + random.nextInt(parentBlock.width);
            int testY = testFragmentHeight * testDepth;

            // Check that this position doesn't overlap with existing fragments
            boolean overlaps = false;
            if (testDepth < blocksByDepth.size()) {
                for (Rect existingBlock : blocksByDepth.get(testDepth)) {
                    if (existingBlock.contains(testX, testY)) {
                        overlaps = true;
                        break;
                    }
                }
            }

            if (overlaps) {
                continue; // Skip this test point
            }

            try {
                Rect result = Main.test(new Main.GlobalData(new HashMap<>(randomBlocks), testBounds, testFragmentHeight, BUFFER_TYPE), testX, testY);

                // Validate the result
                if (result != null) {
                    // Should be at the correct depth
                    Assertions.assertEquals(testY, result.getTop(),
                            String.format("Fragment should be at y=%d, but was at y=%d", testY, result.getTop()));

                    // Should have the correct height
                    Assertions.assertEquals(testFragmentHeight, result.height,
                            "Fragment should have correct height");

                    // Should be within parent's X range
                    Assertions.assertTrue(testX >= parentBlock.getLeft() && testX < parentBlock.getRightExclusive(),
                            String.format("Test point X=%d should be within parent range [%d, %d)",
                                    testX, parentBlock.getLeft(), parentBlock.getRightExclusive()));

                    // Should not overlap with existing fragments at the same depth
                    List<BufferFragment> sameDepthFragments = randomBlocks.get(testDepth);
                    if (sameDepthFragments != null) {
                        for (BufferFragment existing : sameDepthFragments) {
                            if (!existing.getBounds().equals(result)) {
                                Assertions.assertFalse(existing.getBounds().intersects(result),
                                        String.format("New fragment %s should not overlap with existing %s", result, existing.getBounds()));
                            }
                        }
                    }
                    Rect testRect = new Rect(testX, testY, (randomBlocks.get(testDepth-1).get(parentIndex).getMaxWidth()/2), testFragmentHeight);
                    Main.ClosestSiblings closestSiblings = Main.findClosestSiblings(randomBlocks, testDepth, testRect);
                    if(closestSiblings.left == null){
                        closestSiblings.left = new Main.Sibling(sectionBounds, sectionBounds.width);
                    }
                    if(closestSiblings.right == null){
                        closestSiblings.right = new Main.Sibling(sectionBounds, sectionBounds.width);
                    }
                    int distBetweenSiblings = Math.abs(
                            closestSiblings.left.bounds().getRightExclusive() -
                                    closestSiblings.right.bounds().getLeft());

                    //case of overflow to the right
                    if(testRect.getLeft() < closestSiblings.left.bounds().getRightExclusive()
                            && testRect.getRightExclusive() >= closestSiblings.left.bounds().getRightExclusive()
                    ){
                        testRect.translate(-closestSiblings.left.distance(), 0);

                        if(closestSiblings.right.distance() < testRect.width){
                            // note: assuming setSize maintains the top-left coordinate
                            testRect.setSize(distBetweenSiblings, FRAGMENT_HEIGHT);
                        }
                        Assertions.assertEquals(new Rect(closestSiblings.left.bounds().getRightExclusive(), closestSiblings.left.bounds().y, testRect.width,
                                testRect.height), result);

                    } else //case of overflow to the left
                        if(testBounds.getRightExclusive() > closestSiblings.right.bounds().getLeft()
                            && testRect.getLeft() >= closestSiblings.right.bounds().getLeft()
                    ){
                        testRect.translate(-closestSiblings.right.distance(), 0);

                        if(closestSiblings.left.distance() < testRect.width){
                            // note: assuming setSize maintains the top-left coordinate
                            testRect.setSize(distBetweenSiblings, FRAGMENT_HEIGHT);
                        }
                        Assertions.assertEquals(new Rect(closestSiblings.right.bounds().getLeft() - testRect.width, closestSiblings.right.bounds().y, testRect.width,
                                testRect.height), result);
                    } else //case of smart allocating left
                      if(testRect.contains(testX + (testRect.width - closestSiblings.left.distance() % testRect.width), testY)){
                          testRect.translate(-(testRect.width - closestSiblings.left.distance() % testRect.width),0);
                          Assertions.assertEquals(testRect, result);

                    } else //case of smart allocate right
                        if(testRect.contains(testX + (testRect.width - closestSiblings.right.distance() % testRect.width), testY)) {
                            testRect.translate((testRect.width - closestSiblings.right.distance() % testRect.width), 0);
                            Assertions.assertEquals(testRect, result);

                        } else { //Parent alignment case
                            Assertions.assertTrue((parentBlock.getLeft() == result.getLeft()) || (parentBlock.getRightExclusive() == result.getRightExclusive()));
                        }


                    } else {
                    Assertions.fail(String.format("Expected fragment creation at (%d, %d) with parent %s",
                            testX, testY, parentBlock));
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                Assertions.fail(String.format("Unexpected exception at (%d, %d): %s", testX, testY, e.getMessage()));
            }
        }
    }
}