package test.java.com.novospir.libraries;

import com.github.romankh3.image.comparison.ImageComparison;
import com.github.romankh3.image.comparison.model.ImageComparisonResult;
import com.github.romankh3.image.comparison.model.ImageComparisonState;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;


public class InfiniteBufferedImageTest2 {
    private static BufferedImage standardBufferedImage;
    private static BufferedImage newBufferedImage;
    private static int width = 200;
    private static int height = 200;
    private static int circleX = 50;
    private static int circleY = 50;
    private static int circleDiameter = 100;
    private static int circleX2 = 50;
    private static int circleY2 = 50;
    private static int circleDiameter2 = 100;

    @BeforeAll
    public static void setUp() {
        // Create a BufferedImage with a white background

        standardBufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);

        // Get graphics context and set up drawing
        Graphics2D g2d = standardBufferedImage.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);

        // Draw a blue circle
        g2d.setColor(Color.BLUE);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.fillOval(circleX, circleY, circleDiameter, circleDiameter);

        g2d.dispose();


        //set up a new typ of BufferedImage to compare with the standard one
        newBufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);

        // Get graphics context and set up drawing
        Graphics2D g2d2 = newBufferedImage.createGraphics();
        g2d2.setColor(Color.WHITE);
        g2d2.fillRect(0, 0, width, height);

        // Draw a blue circle
        g2d2.setColor(Color.BLUE);
        g2d2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d2.fillOval(circleX2, circleY2, circleDiameter2, circleDiameter2);

        g2d2.dispose();
    }


    @Test
    public void testCircleDrawing() {
         // Display the images
        JDialog dialog = new JDialog((Frame) null, "Circle Test Visualization", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JLabel label = new JLabel(new ImageIcon(standardBufferedImage));
        dialog.add(label);

        JLabel label2 = new JLabel(new ImageIcon(newBufferedImage));
        dialog.add(label2, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setVisible(true);
        //load images to be compared:
        BufferedImage expectedImage = standardBufferedImage;
        BufferedImage actualImage = newBufferedImage;

        //Create ImageComparison object and compare the images.
        ImageComparisonResult imageComparisonResult = new ImageComparison(expectedImage, actualImage).compareImages();

        //Check the result
        assertEquals(ImageComparisonState.MATCH, imageComparisonResult.getImageComparisonState());

    }

    @Test
    void rasterTest() throws IOException {
        byte[] byteArray = ((DataBufferByte) standardBufferedImage.getData().getDataBuffer()).getData();
        byte[] byteArray2 = ((DataBufferByte) newBufferedImage.getData().getDataBuffer()).getData();
        assertArrayEquals(byteArray2,byteArray);
    }

    @Test
    void basicDrawingTests(){
        //these are basic tests of the regular BufferedImage maybe useful for debugging purposes
        // Verify the image was created
        assertNotNull(standardBufferedImage);

        // Test specific pixels to verify circle was drawn
        // Center of circle should be blue
        int centerX = circleX + circleDiameter / 2;
        int centerY = circleY + circleDiameter / 2;
        int centerColor = standardBufferedImage.getRGB(centerX, centerY);
        assertEquals(Color.BLUE.getRGB(), centerColor);

        // Corner should be white (background)
        int cornerColor = standardBufferedImage.getRGB(10, 10);
        assertEquals(Color.WHITE.getRGB(), cornerColor);

        // Point outside circle but inside image should be white
        int outsideColor = standardBufferedImage.getRGB(180, 180);
        assertEquals(Color.WHITE.getRGB(), outsideColor);

        // Verify the image was created
        assertNotNull(newBufferedImage);

        // Test specific pixels to verify circle was drawn
        // Center of circle should be blue
        int centerX2 = circleX + circleDiameter / 2;
        int centerY2 = circleY + circleDiameter / 2;
        int centerColor2 = standardBufferedImage.getRGB(centerX2, centerY2);
        assertEquals(Color.BLUE.getRGB(), centerColor2);

        // Corner should be white (background)
        int cornerColor2 = newBufferedImage.getRGB(10, 10);
        assertEquals(Color.WHITE.getRGB(), cornerColor2);

        // Point outside circle but inside image should be white
        int outsideColor2 = newBufferedImage.getRGB(180, 180);
        assertEquals(Color.WHITE.getRGB(), outsideColor2);



    }

    @Test
    void testGetRGB() {
        // Test getRGB method with known pixel values
        
        // Test center of blue circle
        int centerX = circleX + circleDiameter / 2;
        int centerY = circleY + circleDiameter / 2;
        int standardRGB = standardBufferedImage.getRGB(centerX, centerY);
        int newRGB = newBufferedImage.getRGB(centerX, centerY);
        assertEquals(standardRGB, newRGB, "getRGB should return identical values for blue circle center");
        assertEquals(Color.BLUE.getRGB(), standardRGB, "Center pixel should be blue");
        
        // Test white background pixel
        int bgRGB1 = standardBufferedImage.getRGB(10, 10);
        int bgRGB2 = newBufferedImage.getRGB(10, 10);
        assertEquals(bgRGB1, bgRGB2, "getRGB should return identical values for background");
        assertEquals(Color.WHITE.getRGB(), bgRGB1, "Background pixel should be white");
        
        // Test edge cases - corners
        int corner1 = standardBufferedImage.getRGB(0, 0);
        int corner2 = newBufferedImage.getRGB(0, 0);
        assertEquals(corner1, corner2, "Corner pixels should match");
        
        int corner3 = standardBufferedImage.getRGB(width-1, height-1);
        int corner4 = newBufferedImage.getRGB(width-1, height-1);
        assertEquals(corner3, corner4, "Bottom-right corner pixels should match");
        
        // Test multiple points along circle edge to verify anti-aliasing consistency
        for (int angle = 0; angle < 360; angle += 45) {
            double radians = Math.toRadians(angle);
            int edgeX = (int) (centerX + (circleDiameter / 2.0) * Math.cos(radians));
            int edgeY = (int) (centerY + (circleDiameter / 2.0) * Math.sin(radians));
            
            if (edgeX >= 0 && edgeX < width && edgeY >= 0 && edgeY < height) {
                int rgb1 = standardBufferedImage.getRGB(edgeX, edgeY);
                int rgb2 = newBufferedImage.getRGB(edgeX, edgeY);
                assertEquals(rgb1, rgb2, "Circle edge pixels should match at angle " + angle);
            }
        }
    }
    
    @Test
    void testSetRGB() {
        // Create fresh test images for setRGB testing
        BufferedImage testStandard = new BufferedImage(50, 50, BufferedImage.TYPE_3BYTE_BGR);
        BufferedImage testNew = new BufferedImage(50, 50, BufferedImage.TYPE_3BYTE_BGR);
        
        // Fill both with white background
        Graphics2D g1 = testStandard.createGraphics();
        Graphics2D g2 = testNew.createGraphics();
        g1.setColor(Color.WHITE);
        g2.setColor(Color.WHITE);
        g1.fillRect(0, 0, 50, 50);
        g2.fillRect(0, 0, 50, 50);
        g1.dispose();
        g2.dispose();
        
        // Test setting individual pixels with different colors
        int[] testColors = {
            Color.RED.getRGB(),
            Color.GREEN.getRGB(), 
            Color.BLUE.getRGB(),
            Color.BLACK.getRGB(),
            0xFF808080, // Gray
            0xFFFF00FF  // Magenta
        };
        
        for (int i = 0; i < testColors.length; i++) {
            int x = i * 7;
            int y = i * 6;
            
            testStandard.setRGB(x, y, testColors[i]);
            testNew.setRGB(x, y, testColors[i]);
            
            // Verify the pixel was set correctly
            assertEquals(testColors[i], testStandard.getRGB(x, y), "Standard image should have correct RGB at (" + x + "," + y + ")");
            assertEquals(testColors[i], testNew.getRGB(x, y), "New image should have correct RGB at (" + x + "," + y + ")");
            assertEquals(testStandard.getRGB(x, y), testNew.getRGB(x, y), "Both images should have identical RGB at (" + x + "," + y + ")");
        }
        
        // Test setting a rectangular pattern
        for (int x = 10; x < 20; x++) {
            for (int y = 10; y < 20; y++) {
                int color = (x * y) << 8; // Create a pattern based on coordinates
                testStandard.setRGB(x, y, color);
                testNew.setRGB(x, y, color);
                
                assertEquals(testStandard.getRGB(x, y), testNew.getRGB(x, y), 
                    "Pattern pixels should match at (" + x + "," + y + ")");
            }
        }
        
        // Test edge cases - setting pixels at boundaries
        testStandard.setRGB(0, 0, Color.YELLOW.getRGB());
        testNew.setRGB(0, 0, Color.YELLOW.getRGB());
        assertEquals(testStandard.getRGB(0, 0), testNew.getRGB(0, 0), "Top-left corner should match");
        
        testStandard.setRGB(49, 49, Color.CYAN.getRGB());
        testNew.setRGB(49, 49, Color.CYAN.getRGB());
        assertEquals(testStandard.getRGB(49, 49), testNew.getRGB(49, 49), "Bottom-right corner should match");
    }
    
    @Test 
    void testToBufferedImage() {
        // Test toBufferedImage with different rectangle bounds

        Rectangle fullBounds = new Rectangle(0, 0, width, height);
        Rectangle partialBounds = new Rectangle(25, 25, 100, 100);
        Rectangle circleBounds = new Rectangle(circleX, circleY, circleDiameter, circleDiameter);
        
        // Test full image bounds
        BufferedImage fullStandard = standardBufferedImage.getSubimage(fullBounds.x, fullBounds.y, 
            fullBounds.width, fullBounds.height);
        BufferedImage fullNew = newBufferedImage.getSubimage(fullBounds.x, fullBounds.y, 
            fullBounds.width, fullBounds.height);
        
        assertEquals(fullStandard.getWidth(), fullNew.getWidth(), "Full bounds width should match");
        assertEquals(fullStandard.getHeight(), fullNew.getHeight(), "Full bounds height should match");
        
        // Compare pixels in full bounds
        for (int x = 0; x < fullStandard.getWidth(); x += 10) {
            for (int y = 0; y < fullStandard.getHeight(); y += 10) {
                assertEquals(fullStandard.getRGB(x, y), fullNew.getRGB(x, y), 
                    "Full bounds pixel should match at (" + x + "," + y + ")");
            }
        }
        
        // Test partial bounds
        BufferedImage partialStandard = standardBufferedImage.getSubimage(partialBounds.x, partialBounds.y,
            partialBounds.width, partialBounds.height);
        BufferedImage partialNew = newBufferedImage.getSubimage(partialBounds.x, partialBounds.y,
            partialBounds.width, partialBounds.height);
        
        assertEquals(partialBounds.width, partialStandard.getWidth(), "Partial bounds width should be correct");
        assertEquals(partialBounds.height, partialStandard.getHeight(), "Partial bounds height should be correct");
        assertEquals(partialStandard.getWidth(), partialNew.getWidth(), "Partial bounds width should match");
        assertEquals(partialStandard.getHeight(), partialNew.getHeight(), "Partial bounds height should match");
        
        // Compare pixels in partial bounds
        for (int x = 0; x < partialStandard.getWidth(); x += 5) {
            for (int y = 0; y < partialStandard.getHeight(); y += 5) {
                assertEquals(partialStandard.getRGB(x, y), partialNew.getRGB(x, y),
                    "Partial bounds pixel should match at (" + x + "," + y + ")");
            }
        }
        
        // Test circle bounds (should contain most of the blue circle)
        BufferedImage circleStandard = standardBufferedImage.getSubimage(circleBounds.x, circleBounds.y,
            circleBounds.width, circleBounds.height);
        BufferedImage circleNew = newBufferedImage.getSubimage(circleBounds.x, circleBounds.y,
            circleBounds.width, circleBounds.height);
        
        assertEquals(circleBounds.width, circleStandard.getWidth(), "Circle bounds width should be correct");
        assertEquals(circleBounds.height, circleStandard.getHeight(), "Circle bounds height should be correct");
        
        // Verify circle center is blue in both subimages
        int centerInSubimage = circleDiameter / 2;
        assertEquals(Color.BLUE.getRGB(), circleStandard.getRGB(centerInSubimage, centerInSubimage),
            "Circle center should be blue in standard subimage");
        assertEquals(Color.BLUE.getRGB(), circleNew.getRGB(centerInSubimage, centerInSubimage),
            "Circle center should be blue in new subimage");
        assertEquals(circleStandard.getRGB(centerInSubimage, centerInSubimage), 
            circleNew.getRGB(centerInSubimage, centerInSubimage),
            "Circle centers should match in both subimages");
    }
    
    @Test
    void testCreateGraphics() {
        // Test that createGraphics() returns functional Graphics2D objects
        BufferedImage testStandard = new BufferedImage(100, 100, BufferedImage.TYPE_3BYTE_BGR);
        BufferedImage testNew = new BufferedImage(100, 100, BufferedImage.TYPE_3BYTE_BGR);
        
        Graphics2D g1 = testStandard.createGraphics();
        Graphics2D g2 = testNew.createGraphics();
        
        assertNotNull(g1, "Standard createGraphics should return non-null Graphics2D");
        assertNotNull(g2, "New createGraphics should return non-null Graphics2D");
        
        // Test basic drawing operations
        g1.setColor(Color.RED);
        g2.setColor(Color.RED);
        g1.fillRect(10, 10, 30, 30);
        g2.fillRect(10, 10, 30, 30);
        
        // Verify the drawing worked
        assertEquals(Color.RED.getRGB(), testStandard.getRGB(20, 20), "Standard graphics should draw red rectangle");
        assertEquals(Color.RED.getRGB(), testNew.getRGB(20, 20), "New graphics should draw red rectangle");
        assertEquals(testStandard.getRGB(20, 20), testNew.getRGB(20, 20), "Both graphics should produce identical results");
        
        // Test line drawing
        g1.setColor(Color.BLUE);
        g2.setColor(Color.BLUE);
        g1.drawLine(0, 0, 50, 50);
        g2.drawLine(0, 0, 50, 50);
        
        // Check a point on the line
        assertEquals(testStandard.getRGB(25, 25), testNew.getRGB(25, 25), "Line drawing should produce identical results");
        
        // Test text rendering
        g1.setColor(Color.GREEN);
        g2.setColor(Color.GREEN);
        g1.drawString("Test", 60, 60);
        g2.drawString("Test", 60, 60);
        
        // Check that text was rendered (pixel should not be black/transparent)
        int textPixel1 = testStandard.getRGB(65, 55);
        int textPixel2 = testNew.getRGB(65, 55);
        assertEquals(textPixel1, textPixel2, "Text rendering should produce identical results");
        
        // Test antialiasing settings
        g1.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g1.setColor(Color.YELLOW);
        g2.setColor(Color.YELLOW);
        g1.fillOval(70, 70, 20, 20);
        g2.fillOval(70, 70, 20, 20);
        
        // Check antialiased circle
        assertEquals(testStandard.getRGB(80, 80), testNew.getRGB(80, 80), "Antialiased drawing should produce identical results");
        
        g1.dispose();
        g2.dispose();
        
        // Test that graphics can be created multiple times
        Graphics2D g3 = testStandard.createGraphics();
        Graphics2D g4 = testNew.createGraphics();
        assertNotNull(g3, "Should be able to create graphics multiple times");
        assertNotNull(g4, "Should be able to create graphics multiple times");
        g3.dispose();
        g4.dispose();
    }
    
    @Test
    void testGetRaster() {
        // Test getRaster with different bounds
        Rectangle fullBounds = new Rectangle(0, 0, width, height);
        Rectangle partialBounds = new Rectangle(50, 50, 100, 100);
        Rectangle smallBounds = new Rectangle(75, 75, 50, 50);
        
        // Test full raster
        Raster fullRaster1 = standardBufferedImage.getRaster();
        Raster fullRaster2 = newBufferedImage.getRaster();
        
        assertNotNull(fullRaster1, "Standard getRaster should return non-null");
        assertNotNull(fullRaster2, "New getRaster should return non-null");
        assertEquals(fullRaster1.getWidth(), fullRaster2.getWidth(), "both raster widths should match");
        assertEquals(fullRaster1.getHeight(), fullRaster2.getHeight(), "both raster heights should match");
        assertEquals(fullRaster1.getNumBands(), fullRaster2.getNumBands(), "Number of bands should match");
        
        // Test data extraction from rasters
        int[] pixel1 = new int[fullRaster1.getNumBands()];
        int[] pixel2 = new int[fullRaster2.getNumBands()];
        
        // Test center of circle
        int centerX = circleX + circleDiameter / 2;
        int centerY = circleY + circleDiameter / 2;
        fullRaster1.getPixel(centerX, centerY, pixel1);
        fullRaster2.getPixel(centerX, centerY, pixel2);
        assertArrayEquals(pixel1, pixel2, "Center pixel raster data should match");
        
        // Test background pixel
        fullRaster1.getPixel(10, 10, pixel1);
        fullRaster2.getPixel(10, 10, pixel2);
        assertArrayEquals(pixel1, pixel2, "Background pixel raster data should match");
        
        // Test partial raster using getData
        Raster partialRaster1 = standardBufferedImage.getData(partialBounds);
        Raster partialRaster2 = newBufferedImage.getData(partialBounds);
        
        assertNotNull(partialRaster1, "Partial raster 1 should not be null");
        assertNotNull(partialRaster2, "Partial raster 2 should not be null");
        assertEquals(partialBounds.width, partialRaster1.getWidth(), "Partial raster width should match bounds");
        assertEquals(partialBounds.height, partialRaster1.getHeight(), "Partial raster height should match bounds");
        assertEquals(partialRaster1.getWidth(), partialRaster2.getWidth(), "Partial raster widths should match");
        assertEquals(partialRaster1.getHeight(), partialRaster2.getHeight(), "Partial raster heights should match");
        
        // Compare sample points in partial raster
        for (int x = 0; x < partialRaster1.getWidth(); x += 10) {
            for (int y = 0; y < partialRaster1.getHeight(); y += 10) {
                partialRaster1.getPixel(x, y, pixel1);
                partialRaster2.getPixel(x, y, pixel2);
                assertArrayEquals(pixel1, pixel2, "Partial raster pixels should match at (" + x + "," + y + ")");
            }
        }
        
        // Test small bounds raster
        Raster smallRaster1 = standardBufferedImage.getData(smallBounds);
        Raster smallRaster2 = newBufferedImage.getData(smallBounds);
        
        assertEquals(smallBounds.width, smallRaster1.getWidth(), "Small raster should have correct width");
        assertEquals(smallBounds.height, smallRaster1.getHeight(), "Small raster should have correct height");
        assertEquals(smallRaster1.getWidth(), smallRaster2.getWidth(), "Small raster widths should match");
        assertEquals(smallRaster1.getHeight(), smallRaster2.getHeight(), "Small raster heights should match");
        
        // Test corner pixels of small raster
        smallRaster1.getPixel(0, 0, pixel1);
        smallRaster2.getPixel(0, 0, pixel2);
        assertArrayEquals(pixel1, pixel2, "Small raster corner pixels should match");
        
        smallRaster1.getPixel(smallRaster1.getWidth()-1, smallRaster1.getHeight()-1, pixel1);
        smallRaster2.getPixel(smallRaster2.getWidth()-1, smallRaster2.getHeight()-1, pixel2);
        assertArrayEquals(pixel1, pixel2, "Small raster opposite corner pixels should match");
    }
}
