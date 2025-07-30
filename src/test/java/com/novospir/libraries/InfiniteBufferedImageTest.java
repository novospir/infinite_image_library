package com.novospir.libraries;


import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.Random;

/**
 * Benchmark/accuracy test for QuadImage vs BufferedImage.
 */
public class InfiniteBufferedImageTest {
    private static final int WRITES   = 100_000;  // random pixels to write
    private static final int CANVAS_W = 10_000;   // virtual canvas size
    private static final int CANVAS_H = 10_000;
    final int SIZE = 1024;


    @Test
    void comparison(){
        AbstractBufferedImage buf = new AbstractBufferedImage.BufferedImageWrapper(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
        AbstractBufferedImage inf = new InfiniteBufferedImage();

        Random rng  = new Random(42);

        // --- baseline memory ---
        long baselineMem = usedMem();

        // --- write identical random pixels into both canvases ---
        for (int i = 0; i < WRITES; i++) {
            int  x   = rng.nextInt(SIZE);
            int  y   = rng.nextInt(SIZE);
            int argb = rng.nextInt();
            inf.setRGB(x, y, argb);
            buf .setRGB(x, y, argb);
        }

        // --- memory after writes ---
        long afterWritesMem = usedMem();

        // --- pixel‑accuracy check ---
        rng.setSeed(42);                       // rewind RNG
        for (int i = 0; i < WRITES; i++) {
            int x = rng.nextInt(SIZE);
            int y = rng.nextInt(SIZE);
            assertEquals(buf.getRGB(x, y), inf.getRGB(x, y),
                    "Pixel mismatch at (" + x + "," + y + ")");
        }

        // --- extra stats for QuadImage ---
        QuadStats qs = gatherStats((InfiniteBufferedImage) inf);

        // --- print results to test log ---
        System.out.println("---------- QuadImage vs BufferedImage ----------");
        System.out.printf ("Random writes        : %,d pixels%n", WRITES);
        System.out.printf ("Canvas size          : %,d × %,d%n", SIZE, SIZE);
        System.out.printf ("Tiles allocated      : %,d%n", qs.tileCount);
        System.out.printf ("Tile memory (approx) : %,d KB%n", qs.tileBytes / 1024);
        System.out.printf ("Heap used baseline   : %,d KB%n", baselineMem / 1024);
        System.out.printf ("Heap used after op   : %,d KB%n", afterWritesMem / 1024);
        System.out.println("------------------------------------------------");
    }

    @Test
    void testSetAndGetRGB_accuracyAndMemory() {

        InfiniteBufferedImage quad;
        BufferedImage ref;
        Random rng;
        quad = new InfiniteBufferedImage();              // your wrapper
        ref  = new BufferedImage(SIZE, SIZE,
                BufferedImage.TYPE_INT_ARGB);            // monolithic reference
        rng  = new Random(42);

        // --- baseline memory ---
        long baselineMem = usedMem();

        // --- write identical random pixels into both canvases ---
        for (int i = 0; i < WRITES; i++) {
            int  x   = rng.nextInt(SIZE);
            int  y   = rng.nextInt(SIZE);
            int argb = rng.nextInt();
            quad.setRGB(x, y, argb);
            ref .setRGB(x, y, argb);
        }

        // --- memory after writes ---
        long afterWritesMem = usedMem();

        // --- pixel‑accuracy check ---
        rng.setSeed(42);                       // rewind RNG
        for (int i = 0; i < WRITES; i++) {
            int x = rng.nextInt(SIZE);
            int y = rng.nextInt(SIZE);
            assertEquals(ref.getRGB(x, y), quad.getRGB(x, y),
                    "Pixel mismatch at (" + x + "," + y + ")");
        }

        // --- extra stats for QuadImage ---
        QuadStats qs = gatherStats(quad);

        // --- print results to test log ---
        System.out.println("---------- QuadImage vs BufferedImage ----------");
        System.out.printf ("Random writes        : %,d pixels%n", WRITES);
        System.out.printf ("Canvas size          : %,d × %,d%n", SIZE, SIZE);
        System.out.printf ("Tiles allocated      : %,d%n", qs.tileCount);
        System.out.printf ("Tile memory (approx) : %,d KB%n", qs.tileBytes / 1024);
        System.out.printf ("Heap used baseline   : %,d KB%n", baselineMem / 1024);
        System.out.printf ("Heap used after op   : %,d KB%n", afterWritesMem / 1024);
        System.out.println("------------------------------------------------");

        // just to keep test green
        assertTrue(true);
    }

    /* ==== helpers ======================================================= */

    /** rough heap usage */
    private static long usedMem() {
        Runtime rt = Runtime.getRuntime();
        return rt.totalMemory() - rt.freeMemory();
    }

    /** collect tile statistics (leaf count and bytes) */
    private static QuadStats gatherStats(InfiniteBufferedImage qi) {
        QuadStats s = new QuadStats();
        QuadNode root;
        try {
            Field rootField = InfiniteBufferedImage.class.getDeclaredField("root");
            rootField.setAccessible(true);
            root = (QuadNode) rootField.get(qi);
            gather(root, s);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Could not access QuadImage.root", e);
        }
        return s;
    }

    private static void gather(QuadNode n, QuadStats s) {
        if (n == null) return;
        if (n.isLeaf()) {
            if (n.image != null) {
                s.tileCount++;
                s.tileBytes += n.image.getRaster().getDataBuffer().getSize() * 4L; // ARGB = 4 bytes
            }
        } else {
            for (QuadNode c : n.children) gather(c, s);
        }
    }

    private static class QuadStats {
        long tileCount;
        long tileBytes;
    }
}
