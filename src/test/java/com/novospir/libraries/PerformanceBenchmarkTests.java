package com.novospir.libraries;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * Performance benchmark tests comparing InfiniteBufferedImage vs. standard BufferedImage
 * Test both speed and memory usage across various scenarios
 */
@TestMethodOrder(OrderAnnotation.class)
public class PerformanceBenchmarkTests {
    
    private static final int WARMUP_ITERATIONS = 100;
    private static final int BENCHMARK_ITERATIONS = 1000;
    private static final Random random = new Random(42); // Fixed seed for reproducible results
    
    @BeforeEach
    void setUp() {
        // Force garbage collection before each test
        System.gc();
        try {
            Thread.sleep(15000); // Allow GC to complete
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    @Test
    @Order(1)
    @DisplayName("Speed: Basic pixel operations (getRGB/setRGB)")
    void benchmarkBasicPixelOperations() {
        System.out.println("\n=== BASIC PIXEL OPERATIONS BENCHMARK ===");
        
        int size = 500;
        int numOperations = 10000;
        
        // Standard BufferedImage
        AbstractBufferedImage.BufferedImageAdapter standardWrapper =
            new AbstractBufferedImage.BufferedImageAdapter(size, size, BufferedImage.TYPE_INT_ARGB);
        
        // InfiniteBufferedImage
        InfiniteBufferedImage infiniteImage = new InfiniteBufferedImage();
        
        // Warmup
        warmupPixelOperations(standardWrapper, infiniteImage, numOperations / 10);
        
        // Benchmark setRGB operations
        long standardSetTime = benchmarkSetRGB(standardWrapper, numOperations, size);
        long infiniteSetTime = benchmarkSetRGB(infiniteImage, numOperations, size);
        
        // Benchmark getRGB operations  
        long standardGetTime = benchmarkGetRGB(standardWrapper, numOperations, size);
        long infiniteGetTime = benchmarkGetRGB(infiniteImage, numOperations, size);
        
        printSpeedComparison("setRGB", standardSetTime, infiniteSetTime, numOperations);
        printSpeedComparison("getRGB", standardGetTime, infiniteGetTime, numOperations);
    }
    
    @Test
    @Order(2)
    @DisplayName("Speed: Sequential vs Sparse pixel access patterns")
    void benchmarkAccessPatterns() {
        System.out.println("\n=== ACCESS PATTERNS BENCHMARK ===");
        
        int size = 1000;
        int numPixels = 5000;
        int numOperations = 10000;
        
        BufferedImage standardImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        AbstractBufferedImage.BufferedImageAdapter standardWrapper =
            new AbstractBufferedImage.BufferedImageAdapter(size, size, BufferedImage.TYPE_INT_ARGB);
        InfiniteBufferedImage infiniteImage = new InfiniteBufferedImage();


        // Warmup both access patterns
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            benchmarkSequentialAccess(standardWrapper, 100);
            benchmarkSequentialAccess(infiniteImage, 100);
            benchmarkSparseAccess(standardWrapper, 100, size);
            benchmarkSparseAccess(infiniteImage, 100, size);
        }
        
        // Sequential access pattern
        long standardSeqTime = benchmarkSequentialAccess(standardWrapper, numPixels);
        long infiniteSeqTime = benchmarkSequentialAccess(infiniteImage, numPixels);
        
        // Sparse/random access pattern
        long standardSparseTime = benchmarkSparseAccess(standardWrapper, numPixels, size);
        long infiniteSparseTime = benchmarkSparseAccess(infiniteImage, numPixels, size);
        
        printSpeedComparison("Sequential Access", standardSeqTime, infiniteSeqTime, numPixels);
        printSpeedComparison("Sparse Access", standardSparseTime, infiniteSparseTime, numPixels);
    }
    
    @Test
    @Order(3)
    @DisplayName("Memory: Sparse pixel storage efficiency")
    void benchmarkMemoryUsageSparse() {
        System.out.println("\n=== SPARSE MEMORY USAGE BENCHMARK ===");
        
        // Test memory usage with very sparse pixel patterns
        int[] sparseCounts = {100, 1000, 10000};
        int coordinateRange = 100000; // Very large coordinate space
        
        for (int pixelCount : sparseCounts) {
            System.out.println(String.format("\nTesting %d sparse pixels across %dx%d coordinate space:", 
                pixelCount, coordinateRange, coordinateRange));
            
            // Measure memory before
            long memBefore = getUsedMemory();
            
            // Standard approach would require massive BufferedImage
            // We'll simulate by calculating theoretical memory usage
            long theoreticalStandardMemory = (long) coordinateRange * coordinateRange * 4; // 4 bytes per ARGB pixel

            // InfiniteBufferedImage - actually create and populate
            InfiniteBufferedImage infiniteImage = new InfiniteBufferedImage();
            for (int i = 0; i < pixelCount; i++) {
                int x = random.nextInt(coordinateRange);
                int y = random.nextInt(coordinateRange);
                infiniteImage.setRGB(x, y, Color.RED.getRGB());
            }

            long memAfter = getUsedMemory();
            long actualInfiniteMemory = memAfter - memBefore;
            
            System.out.println(String.format("  Standard BufferedImage (theoretical): %.2f MB", 
                theoreticalStandardMemory / (1024.0 * 1024.0)));
            System.out.println(String.format("  InfiniteBufferedImage (actual): %.2f MB", 
                actualInfiniteMemory / (1024.0 * 1024.0)));
            System.out.println(String.format("  Memory efficiency: %.1f%% of standard", 
                (actualInfiniteMemory * 100.0) / theoreticalStandardMemory));
        }
    }
    
    @Test
    @Order(4)
    @DisplayName("Memory: Dense vs Sparse storage comparison")
    void benchmarkMemoryUsageDenseVsSparse() {
        System.out.println("\n=== DENSE VS SPARSE MEMORY BENCHMARK ===");
        
        int size = 2000;
        int[] fillPercentages = {1, 5, 25, 50, 100};
        
        for (int fillPercent : fillPercentages) {
            System.out.println(String.format("\nTesting %dx%d image with %d%% fill:", size, size, fillPercent));
            
            // Measure standard BufferedImage with stabilized GC
            long standardMemory = measureMemoryWithStabilization(() -> {
                BufferedImage standardImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = standardImage.createGraphics();
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, size, size);
                g.dispose();
                
                // Fill with pattern
                int pixelsToFill = (size * size * fillPercent) / 100;
                for (int i = 0; i < pixelsToFill; i++) {
                    int x = random.nextInt(size);
                    int y = random.nextInt(size);
                    standardImage.setRGB(x, y, Color.BLUE.getRGB());
                }
                
                return standardImage; // Keep reference to prevent GC
            });
            
            // Measure InfiniteBufferedImage with stabilized GC
            long infiniteMemory = measureMemoryWithStabilization(() -> {
                InfiniteBufferedImage infiniteImage = new InfiniteBufferedImage();
                int pixelsToFill = (size * size * fillPercent) / 100;
                
                for (int i = 0; i < pixelsToFill; i++) {
                    int x = random.nextInt(size);
                    int y = random.nextInt(size);
                    infiniteImage.setRGB(x, y, Color.BLUE.getRGB());
                }
                
                return infiniteImage; // Keep reference to prevent GC
            });
            
            // Display results with validation
            displayMemoryResults(standardMemory, infiniteMemory, "BufferedImage", "InfiniteBufferedImage");
        }
    }
    
    @Test
    @Order(5)
    @DisplayName("Speed: Image expansion scenarios")
    void benchmarkImageExpansion() {
        System.out.println("\n=== IMAGE EXPANSION BENCHMARK ===");
        
        // Test scenarios where image needs to grow dynamically
        int[] expansionSizes = {500, 2000, 10000};
        
        // Warmup expansion operations
        for (int i = 0; i < WARMUP_ITERATIONS / 10; i++) {
            BufferedImage warmupStandard = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
            InfiniteBufferedImage warmupInfinite = new InfiniteBufferedImage();
            
            for (int j = 0; j < 10; j++) {
                warmupStandard.setRGB(j, j, Color.RED.getRGB());
                warmupInfinite.setRGB(j, j, Color.RED.getRGB());
            }
        }
        
        for (int maxCoord : expansionSizes) {
            System.out.println(String.format("\nExpanding to coordinate range [0, %d]:", maxCoord));
            
            // Standard BufferedImage - needs to be pre-allocated to max size
            long startTime = System.nanoTime();
            BufferedImage standardImage = new BufferedImage(maxCoord, maxCoord, BufferedImage.TYPE_INT_ARGB);
            
            // Draw expanding pattern
            for (int i = 0; i < 1000; i++) {
                int coord = (i * maxCoord) / 1000;
                standardImage.setRGB(coord, coord, Color.RED.getRGB());
            }
            long standardTime = System.nanoTime() - startTime;
            
            // InfiniteBufferedImage - grows dynamically
            startTime = System.nanoTime();
            InfiniteBufferedImage infiniteImage = new InfiniteBufferedImage();
            
            // Draw expanding pattern (same operations)
            for (int i = 0; i < 1000; i++) {
                int coord = (i * maxCoord) / 1000;
                infiniteImage.setRGB(coord, coord, Color.RED.getRGB());
            }
            long infiniteTime = System.nanoTime() - startTime;
            
            printSpeedComparison("Expansion to " + maxCoord, standardTime, infiniteTime, 1000);
        }
    }
    
    @Test
    @Order(7)
    @DisplayName("Speed: toBufferedImage extraction performance")
    void benchmarkImageExtraction() {
        System.out.println("\n=== IMAGE EXTRACTION BENCHMARK ===");
        
        // Create test images with some content
        int imageSize = 2000;
        int pixelCount = 5000;
        
        AbstractBufferedImage.BufferedImageAdapter standardWrapper =
            new AbstractBufferedImage.BufferedImageAdapter(imageSize, imageSize, BufferedImage.TYPE_INT_ARGB);
        InfiniteBufferedImage infiniteImage = new InfiniteBufferedImage();
        
        // Fill both with same random pattern
        Random testRandom = new Random(123);
        for (int i = 0; i < pixelCount; i++) {
            int x = testRandom.nextInt(imageSize);
            int y = testRandom.nextInt(imageSize);
            int color = Color.HSBtoRGB(testRandom.nextFloat(), 0.8f, 0.9f);
            standardWrapper.setRGB(x, y, color);
            infiniteImage.setRGB(x, y, color);
        }
        
        // Warmup extraction operations
        Rectangle warmupBounds = new Rectangle(0, 0, 50, 50);
        for (int i = 0; i < WARMUP_ITERATIONS / 10; i++) {
            standardWrapper.toBufferedImage(warmupBounds);
            infiniteImage.toBufferedImage(warmupBounds);
        }
        
        // Test different extraction sizes
        int[] extractSizes = {100, 500, 1000, 2000};
        
        for (int extractSize : extractSizes) {
            Rectangle bounds = new Rectangle(0, 0, extractSize, extractSize);
            
            // Benchmark standard
            long startTime = System.nanoTime();
            for (int i = 0; i < 100; i++) {
                BufferedImage extracted = standardWrapper.toBufferedImage(bounds);
            }
            long standardTime = System.nanoTime() - startTime;
            
            // Benchmark infinite
            startTime = System.nanoTime();
            for (int i = 0; i < 100; i++) {
                BufferedImage extracted = infiniteImage.toBufferedImage(bounds);
            }
            long infiniteTime = System.nanoTime() - startTime;
            
            printSpeedComparison("Extract " + extractSize + "x" + extractSize, 
                standardTime, infiniteTime, 100);
        }
    }
    
    @Test
    @Order(6)
    @DisplayName("Specialty: Negative coordinate handling")
    void benchmarkNegativeCoordinates() {
        System.out.println("\n=== NEGATIVE COORDINATES BENCHMARK ===");
        
        // Standard BufferedImage cannot handle negative coordinates
        // InfiniteBufferedImage specializes in this
        
        int numOperations = 5000;
        int coordinateRange = 10000;
        
        InfiniteBufferedImage infiniteImage = new InfiniteBufferedImage();
        
        // Warmup negative coordinate operations
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            int x = random.nextInt(200) - 100; // -100 to +100
            int y = random.nextInt(200) - 100;
            infiniteImage.setRGB(x, y, Color.CYAN.getRGB());
            infiniteImage.getRGB(x, y);
        }
        
        long startTime = System.nanoTime();
        
        // Test with negative coordinates
        for (int i = 0; i < numOperations; i++) {
            int x = random.nextInt(coordinateRange * 2) - coordinateRange; // -10000 to +10000
            int y = random.nextInt(coordinateRange * 2) - coordinateRange;
            infiniteImage.setRGB(x, y, Color.CYAN.getRGB());
        }
        
        // Test reading back
        for (int i = 0; i < numOperations; i++) {
            int x = random.nextInt(coordinateRange * 2) - coordinateRange;
            int y = random.nextInt(coordinateRange * 2) - coordinateRange;
            int color = infiniteImage.getRGB(x, y);
        }
        
        long totalTime = System.nanoTime() - startTime;
        
        System.out.println(String.format("Negative coordinate operations: %.2f ms for %d operations", 
            totalTime / 1_000_000.0, numOperations * 2));
        System.out.println(String.format("Average per operation: %.3f microseconds", 
            totalTime / (1000.0 * numOperations * 2)));
        System.out.println("Note: Standard BufferedImage cannot handle negative coordinates at all");
    }
    
    // Helper methods
    
    private void warmupPixelOperations(AbstractBufferedImage standard, AbstractBufferedImage infinite, int operations) {
        for (int i = 0; i < operations; i++) {
            int x = random.nextInt(100);
            int y = random.nextInt(100);
            standard.setRGB(x, y, i);
            infinite.setRGB(x, y, i);
            standard.getRGB(x, y);
            infinite.getRGB(x, y);
        }
    }
    
    private long benchmarkSetRGB(AbstractBufferedImage image, int operations, int maxCoord) {
        long startTime = System.nanoTime();
        for (int i = 0; i < operations; i++) {
            int x = random.nextInt(maxCoord);
            int y = random.nextInt(maxCoord);
            image.setRGB(x, y, 0xFF000000 | i);
        }
        return System.nanoTime() - startTime;
    }
    
    private long benchmarkGetRGB(AbstractBufferedImage image, int operations, int maxCoord) {
        long startTime = System.nanoTime();
        for (int i = 0; i < operations; i++) {
            int x = random.nextInt(maxCoord);
            int y = random.nextInt(maxCoord);
            int color = image.getRGB(x, y);
        }
        return System.nanoTime() - startTime;
    }
    
    private long benchmarkSequentialAccess(AbstractBufferedImage image, int numPixels) {
        long startTime = System.nanoTime();
        for (int i = 0; i < numPixels; i++) {
            image.setRGB(i % 100, i / 100, i);
        }
        return System.nanoTime() - startTime;
    }
    
    private long benchmarkSparseAccess(AbstractBufferedImage image, int numPixels, int maxCoord) {
        long startTime = System.nanoTime();
        for (int i = 0; i < numPixels; i++) {
            int x = random.nextInt(maxCoord);
            int y = random.nextInt(maxCoord);
            image.setRGB(x, y, i);
        }
        return System.nanoTime() - startTime;
    }
    
    private long getUsedMemory() {
        // Force GC and take multiple measurements to get stable reading
        for (int i = 0; i < 5; i++) {
            System.gc();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        Runtime runtime = Runtime.getRuntime();
        long[] measurements = new long[5];
        
        // Take multiple measurements
        for (int i = 0; i < 5; i++) {
            measurements[i] = runtime.totalMemory() - runtime.freeMemory();
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        // Return the median to avoid outliers
        java.util.Arrays.sort(measurements);
        return measurements[2]; // median of 5 measurements
    }
    
    /**
     * Measure memory usage with GC stabilization and isolation
     */
    private <T> long measureMemoryWithStabilization(java.util.function.Supplier<T> allocator) {
        // Clear memory state multiple times to ensure stability
        for (int i = 0; i < 3; i++) {
            System.gc();
            System.runFinalization();
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        // Take baseline measurement
        long memoryBefore = getStableMemoryReading();
        
        // Allocate test object
        T testObject = allocator.get();
        
        // Force memory usage calculation
        Runtime.getRuntime().totalMemory(); // Touch memory system
        
        // Take final measurement
        long memoryAfter = getStableMemoryReading();
        
        // Keep reference to prevent GC during measurement
        testObject.hashCode(); // Use the object to prevent optimization
        
        return memoryAfter - memoryBefore;
    }
    
    /**
     * Get a stable memory reading by taking multiple samples
     */
    private long getStableMemoryReading() {
        Runtime runtime = Runtime.getRuntime();
        long[] readings = new long[7];
        
        for (int i = 0; i < readings.length; i++) {
            System.gc();
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            readings[i] = runtime.totalMemory() - runtime.freeMemory();
        }
        
        // Sort and return median
        java.util.Arrays.sort(readings);
        return readings[readings.length / 2];
    }
    
    /**
     * Display memory results with validation and error checking
     */
    private void displayMemoryResults(long standardMemory, long infiniteImageMemory, String name1, String name2) {
        if (standardMemory < 0 || infiniteImageMemory < 0) {
            System.out.println("  WARNING: Negative memory detected - measurement may be unreliable");
            System.out.println(String.format("  %s: %.2f MB %s", name1, 
                standardMemory / (1024.0 * 1024.0), standardMemory < 0 ? "(invalid)" : ""));
            System.out.println(String.format("  %s: %.2f MB %s", name2,
                    infiniteImageMemory / (1024.0 * 1024.0), infiniteImageMemory < 0 ? "(invalid)" : ""));
        } else {
            double mb1 = standardMemory / (1024.0 * 1024.0);
            double mb2 = infiniteImageMemory / (1024.0 * 1024.0);
            double ratio = (double) infiniteImageMemory / standardMemory;
            System.out.println(String.format("  %s: %.2f MB", name1, mb1));
            System.out.println(String.format("  %s: %.2f MB", name2, mb2));
            
            if (standardMemory > 0) {
                System.out.println(String.format("  Ratio: %.2fx %s",
                        ratio, ratio > 1 ? "(higher)" : "(lower)"));
            }
        }
    }
    
    private void printSpeedComparison(String operation, long standardTime, long infiniteTime, int operations) {
        double standardMs = standardTime / 1_000_000.0;
        double infiniteMs = infiniteTime / 1_000_000.0;
        double ratio = infiniteMs / standardMs;
        
        System.out.println(String.format("%s:", operation));
        System.out.println(String.format("  Standard: %.2f ms (%.3f μs per op)", 
            standardMs, standardTime / (1000.0 * operations)));
        System.out.println(String.format("  Infinite: %.2f ms (%.3f μs per op)", 
            infiniteMs, infiniteTime / (1000.0 * operations)));
        System.out.println(String.format("  Ratio: %.2fx %s", 
            ratio, ratio > 1 ? "(slower)" : "(faster)"));
    }
}