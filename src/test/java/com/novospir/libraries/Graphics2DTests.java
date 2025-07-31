package com.novospir.libraries;


import com.github.romankh3.image.comparison.ImageComparison;
import com.github.romankh3.image.comparison.model.ImageComparisonResult;
import com.github.romankh3.image.comparison.model.ImageComparisonState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Graphics2DTests {
    private AbstractBufferedImage standardImage;
    private AbstractBufferedImage testImage;
    private Graphics2D standardGraphics;
    private Graphics2D testGraphics;
    private final int width = 300;
    private final int height = 300;


    @BeforeEach
    void setUp() {
        // Use the BufferedImageWrapper as the standard reference
        standardImage = new AbstractBufferedImage.BufferedImageWrapper(width, height, BufferedImage.TYPE_3BYTE_BGR);
        // Use InfiniteBufferedImage as the test implementation
        testImage = new InfiniteBufferedImage();
        
        standardGraphics = standardImage.createGraphics();
        testGraphics = testImage.createGraphics();
        
        // Set white background for both
        standardGraphics.setColor(Color.WHITE);
        testGraphics.setColor(Color.WHITE);
        standardGraphics.fillRect(0, 0, width, height);
        testGraphics.fillRect(0, 0, width, height);
    }

    @Test
    void testDrawLine() {
        // Test basic line drawing
        standardGraphics.setColor(Color.BLACK);
        testGraphics.setColor(Color.BLACK);
        
        standardGraphics.drawLine(10, 10, 100, 100);
        testGraphics.drawLine(10, 10, 100, 100);
        
        compareImages("drawLine - diagonal");
        clearImages();
        
        // Test horizontal line
        standardGraphics.setColor(Color.RED);
        testGraphics.setColor(Color.RED);
        
        standardGraphics.drawLine(20, 150, 200, 150);
        testGraphics.drawLine(20, 150, 200, 150);
        
        compareImages("drawLine - horizontal");
        clearImages();
        
        // Test vertical line
        standardGraphics.setColor(Color.BLUE);
        testGraphics.setColor(Color.BLUE);
        
        standardGraphics.drawLine(150, 20, 150, 200);
        testGraphics.drawLine(150, 20, 150, 200);
        
        compareImages("drawLine - vertical");
        clearImages();
        
        // Test edge cases - single point line
        standardGraphics.setColor(Color.GREEN);
        testGraphics.setColor(Color.GREEN);
        
        standardGraphics.drawLine(250, 250, 250, 250);
        testGraphics.drawLine(250, 250, 250, 250);
        
        compareImages("drawLine - single point");
    }

    @Test
    void testDrawRect() {
        // Test basic rectangle outline
        standardGraphics.setColor(Color.BLUE);
        testGraphics.setColor(Color.BLUE);
        
        standardGraphics.drawRect(50, 50, 100, 80);
        testGraphics.drawRect(50, 50, 100, 80);
        
        compareImages("drawRect - basic");
        clearImages();
        
        // Test rectangle with different stroke
        BasicStroke thickStroke = new BasicStroke(3.0f);
        standardGraphics.setStroke(thickStroke);
        testGraphics.setStroke(thickStroke);
        
        standardGraphics.setColor(Color.RED);
        testGraphics.setColor(Color.RED);
        
        standardGraphics.drawRect(200, 50, 80, 100);
        testGraphics.drawRect(200, 50, 80, 100);
        
        compareImages("drawRect - thick stroke");
        clearImages();
        
        // Test edge case - zero width/height
        standardGraphics.setColor(Color.MAGENTA);
        testGraphics.setColor(Color.MAGENTA);
        
        standardGraphics.drawRect(100, 200, 0, 50);
        testGraphics.drawRect(100, 200, 0, 50);
        
        standardGraphics.drawRect(150, 200, 50, 0);
        testGraphics.drawRect(150, 200, 50, 0);
        
        compareImages("drawRect - zero dimensions");
    }

    @Test
    void testFillRect() {
        // Test basic filled rectangle
        standardGraphics.setColor(Color.CYAN);
        testGraphics.setColor(Color.CYAN);
        
        standardGraphics.fillRect(25, 25, 80, 60);
        testGraphics.fillRect(25, 25, 80, 60);
        
        compareImages("fillRect - basic");
        clearImages();
        
        // Test overlapping rectangles with different colors
        standardGraphics.setColor(Color.CYAN);
        testGraphics.setColor(Color.CYAN);
        
        standardGraphics.fillRect(25, 25, 80, 60);
        testGraphics.fillRect(25, 25, 80, 60);
        
        standardGraphics.setColor(Color.YELLOW);
        testGraphics.setColor(Color.YELLOW);
        
        standardGraphics.fillRect(70, 50, 80, 60);
        testGraphics.fillRect(70, 50, 80, 60);
        
        compareImages("fillRect - overlapping");
        clearImages();
        
        // Test with transparency
        Color transparentRed = new Color(255, 0, 0, 128);
        standardGraphics.setColor(transparentRed);
        testGraphics.setColor(transparentRed);
        
        standardGraphics.fillRect(120, 75, 80, 60);
        testGraphics.fillRect(120, 75, 80, 60);
        
        compareImages("fillRect - transparent");
    }

    @Test
    void testDrawOval() {
        // Test basic oval/ellipse
        standardGraphics.setColor(Color.GREEN);
        testGraphics.setColor(Color.GREEN);
        
        standardGraphics.drawOval(50, 50, 100, 80);
        testGraphics.drawOval(50, 50, 100, 80);
        
        compareImages("drawOval - ellipse");
        clearImages();
        
        // Test perfect circle
        standardGraphics.setColor(Color.ORANGE);
        testGraphics.setColor(Color.ORANGE);
        
        standardGraphics.drawOval(200, 100, 80, 80);
        testGraphics.drawOval(200, 100, 80, 80);
        
        compareImages("drawOval - circle");
        clearImages();
        
        // Test with antialiasing
        standardGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        testGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        standardGraphics.setColor(Color.PINK);
        testGraphics.setColor(Color.PINK);
        
        standardGraphics.drawOval(100, 200, 90, 70);
        testGraphics.drawOval(100, 200, 90, 70);
        
        compareImages("drawOval - antialiased");
    }

    @Test
    void testFillOval() {
        // Test filled oval
        standardGraphics.setColor(Color.MAGENTA);
        testGraphics.setColor(Color.MAGENTA);
        
        standardGraphics.fillOval(40, 40, 120, 90);
        testGraphics.fillOval(40, 40, 120, 90);
        
        compareImages("fillOval - basic");
        
        // Test filled circle with gradient effect using multiple circles
        for (int i = 0; i < 5; i++) {
            int alpha = 255 - (i * 40);
            Color gradientColor = new Color(0, 0, 255, alpha);
            standardGraphics.setColor(gradientColor);
            testGraphics.setColor(gradientColor);
            
            int size = 80 - (i * 10);
            int offset = i * 5;
            standardGraphics.fillOval(200 + offset, 50 + offset, size, size);
            testGraphics.fillOval(200 + offset, 50 + offset, size, size);
        }
        
        compareImages("fillOval - gradient effect");
    }

    @Test
    void testDrawArc() {
        // Test quarter arc
        standardGraphics.setColor(Color.RED);
        testGraphics.setColor(Color.RED);
        
        standardGraphics.drawArc(50, 50, 100, 100, 0, 90);
        testGraphics.drawArc(50, 50, 100, 100, 0, 90);
        
        compareImages("drawArc - quarter arc");
        clearImages();
        
        // Test semicircle
        standardGraphics.setColor(Color.BLUE);
        testGraphics.setColor(Color.BLUE);
        
        standardGraphics.drawArc(200, 50, 80, 80, 0, 180);
        testGraphics.drawArc(200, 50, 80, 80, 0, 180);
        
        compareImages("drawArc - semicircle");
        clearImages();
        
        // Test full circle (360 degrees)
        standardGraphics.setColor(Color.GREEN);
        testGraphics.setColor(Color.GREEN);
        
        standardGraphics.drawArc(100, 200, 60, 60, 0, 360);
        testGraphics.drawArc(100, 200, 60, 60, 0, 360);
        
        compareImages("drawArc - full circle");
        clearImages();
        
        // Test negative angle
        standardGraphics.setColor(Color.ORANGE);
        testGraphics.setColor(Color.ORANGE);
        
        standardGraphics.drawArc(200, 200, 70, 70, 45, -90);
        testGraphics.drawArc(200, 200, 70, 70, 45, -90);
        
        compareImages("drawArc - negative angle");
    }

    @Test
    void testFillArc() {
        // Test filled arc (pie slice)
        standardGraphics.setColor(Color.YELLOW);
        testGraphics.setColor(Color.YELLOW);
        
        standardGraphics.fillArc(50, 50, 100, 100, 30, 120);
        testGraphics.fillArc(50, 50, 100, 100, 30, 120);
        
        compareImages("fillArc - pie slice");
        
        // Create a pie chart effect
        Color[] pieColors = {Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE, Color.PINK};
        int[] angles = {72, 72, 72, 72, 72}; // Equal slices
        int startAngle = 0;
        
        for (int i = 0; i < pieColors.length; i++) {
            standardGraphics.setColor(pieColors[i]);
            testGraphics.setColor(pieColors[i]);
            
            standardGraphics.fillArc(200, 100, 80, 80, startAngle, angles[i]);
            testGraphics.fillArc(200, 100, 80, 80, startAngle, angles[i]);
            
            startAngle += angles[i];
        }
        
        compareImages("fillArc - pie chart");
    }

    @Test
    void testDrawRoundRect() {
        // Test basic rounded rectangle
        standardGraphics.setColor(Color.BLUE);
        testGraphics.setColor(Color.BLUE);
        
        standardGraphics.drawRoundRect(50, 50, 120, 80, 20, 20);
        testGraphics.drawRoundRect(50, 50, 120, 80, 20, 20);
        
        compareImages("drawRoundRect - basic");
        clearImages();
        
        // Test with different arc dimensions
        standardGraphics.setColor(Color.RED);
        testGraphics.setColor(Color.RED);
        
        standardGraphics.drawRoundRect(200, 50, 80, 100, 40, 15);
        testGraphics.drawRoundRect(200, 50, 80, 100, 40, 15);
        
        compareImages("drawRoundRect - different arcs");
        clearImages();
        
        // Test extreme rounding (should look like oval)
        standardGraphics.setColor(Color.GREEN);
        testGraphics.setColor(Color.GREEN);
        
        standardGraphics.drawRoundRect(100, 200, 100, 60, 100, 60);
        testGraphics.drawRoundRect(100, 200, 100, 60, 100, 60);
        
        compareImages("drawRoundRect - extreme rounding");
    }

    @Test
    void testFillRoundRect() {
        // Test filled rounded rectangle
        standardGraphics.setColor(Color.CYAN);
        testGraphics.setColor(Color.CYAN);
        
        standardGraphics.fillRoundRect(40, 40, 100, 70, 15, 15);
        testGraphics.fillRoundRect(40, 40, 100, 70, 15, 15);
        
        compareImages("fillRoundRect - basic");
        clearImages();
        
        // Test overlapping rounded rectangles - need to draw both to show overlap
        standardGraphics.setColor(Color.CYAN);
        testGraphics.setColor(Color.CYAN);
        
        standardGraphics.fillRoundRect(40, 40, 100, 70, 15, 15);
        testGraphics.fillRoundRect(40, 40, 100, 70, 15, 15);
        
        standardGraphics.setColor(new Color(255, 0, 0, 128));
        testGraphics.setColor(new Color(255, 0, 0, 128));
        
        standardGraphics.fillRoundRect(80, 60, 100, 70, 25, 25);
        testGraphics.fillRoundRect(80, 60, 100, 70, 25, 25);
        
        compareImages("fillRoundRect - overlapping");
    }

    @Test
    void testDrawPolygon() {
        // Test triangle
        int[] xTriangle = {100, 150, 50};
        int[] yTriangle = {50, 120, 120};
        
        standardGraphics.setColor(Color.RED);
        testGraphics.setColor(Color.RED);
        
        standardGraphics.drawPolygon(xTriangle, yTriangle, 3);
        testGraphics.drawPolygon(xTriangle, yTriangle, 3);
        
        compareImages("drawPolygon - triangle");
        clearImages();
        
        // Test pentagon
        int[] xPentagon = new int[5];
        int[] yPentagon = new int[5];
        int centerX = 220, centerY = 100, radius = 40;
        
        for (int i = 0; i < 5; i++) {
            double angle = i * 2 * Math.PI / 5 - Math.PI / 2;
            xPentagon[i] = centerX + (int) (radius * Math.cos(angle));
            yPentagon[i] = centerY + (int) (radius * Math.sin(angle));
        }
        
        standardGraphics.setColor(Color.BLUE);
        testGraphics.setColor(Color.BLUE);
        
        standardGraphics.drawPolygon(xPentagon, yPentagon, 5);
        testGraphics.drawPolygon(xPentagon, yPentagon, 5);
        
        compareImages("drawPolygon - pentagon");
        clearImages();
        
        // Test star shape
        int[] xStar = {150, 160, 180, 165, 170, 150, 130, 135, 120, 140};
        int[] yStar = {200, 220, 220, 235, 255, 245, 255, 235, 220, 220};
        
        standardGraphics.setColor(Color.ORANGE);
        testGraphics.setColor(Color.ORANGE);
        
        standardGraphics.drawPolygon(xStar, yStar, 10);
        testGraphics.drawPolygon(xStar, yStar, 10);
        
        compareImages("drawPolygon - star");
    }

    @Test
    void testFillPolygon() {
        // Test filled triangle
        int[] xTriangle = {80, 120, 60};
        int[] yTriangle = {60, 110, 110};
        
        standardGraphics.setColor(Color.GREEN);
        testGraphics.setColor(Color.GREEN);
        
        standardGraphics.fillPolygon(xTriangle, yTriangle, 3);
        testGraphics.fillPolygon(xTriangle, yTriangle, 3);
        
        compareImages("fillPolygon - triangle");
        clearImages();
        
        // Test filled hexagon
        int[] xHexagon = new int[6];
        int[] yHexagon = new int[6];
        int centerX = 200, centerY = 120, radius = 35;
        
        for (int i = 0; i < 6; i++) {
            double angle = i * Math.PI / 3;
            xHexagon[i] = centerX + (int) (radius * Math.cos(angle));
            yHexagon[i] = centerY + (int) (radius * Math.sin(angle));
        }
        
        standardGraphics.setColor(Color.MAGENTA);
        testGraphics.setColor(Color.MAGENTA);
        
        standardGraphics.fillPolygon(xHexagon, yHexagon, 6);
        testGraphics.fillPolygon(xHexagon, yHexagon, 6);
        
        compareImages("fillPolygon - hexagon");
    }

    @Test
    void testDrawPolyline() {
        // Test zigzag line
        int[] xPoints = {30, 60, 90, 120, 150, 180};
        int[] yPoints = {100, 50, 100, 50, 100, 50};
        
        standardGraphics.setColor(Color.BLACK);
        testGraphics.setColor(Color.BLACK);
        
        standardGraphics.drawPolyline(xPoints, yPoints, 6);
        testGraphics.drawPolyline(xPoints, yPoints, 6);
        
        compareImages("drawPolyline - zigzag");
        clearImages();
        
        // Test curved path approximation
        int[] xCurve = new int[20];
        int[] yCurve = new int[20];
        
        for (int i = 0; i < 20; i++) {
            xCurve[i] = 50 + i * 10;
            yCurve[i] = 200 + (int) (30 * Math.sin(i * 0.5));
        }
        
        standardGraphics.setColor(Color.BLUE);
        testGraphics.setColor(Color.BLUE);
        standardGraphics.setStroke(new BasicStroke(2.0f));
        testGraphics.setStroke(new BasicStroke(2.0f));
        
        standardGraphics.drawPolyline(xCurve, yCurve, 20);
        testGraphics.drawPolyline(xCurve, yCurve, 20);
        
        compareImages("drawPolyline - sine wave");
    }

    @Test
    void testDrawString() {
        Font testFont = new Font("Arial", Font.PLAIN, 16);
        standardGraphics.setFont(testFont);
        testGraphics.setFont(testFont);
        
        // Test basic string drawing
        standardGraphics.setColor(Color.BLACK);
        testGraphics.setColor(Color.BLACK);
        
        standardGraphics.drawString("Hello World", 50, 50);
        testGraphics.drawString("Hello World", 50, 50);
        
        compareImages("drawString - basic");
        clearImages();
        
        // Test different fonts and styles
        Font boldFont = new Font("Arial", Font.BOLD, 20);
        standardGraphics.setFont(boldFont);
        testGraphics.setFont(boldFont);
        
        standardGraphics.setColor(Color.RED);
        testGraphics.setColor(Color.RED);
        
        standardGraphics.drawString("Bold Text", 50, 100);
        testGraphics.drawString("Bold Text", 50, 100);
        
        compareImages("drawString - bold");
        clearImages();
        
        // Test italic font
        Font italicFont = new Font("Arial", Font.ITALIC, 18);
        standardGraphics.setFont(italicFont);
        testGraphics.setFont(italicFont);
        
        standardGraphics.setColor(Color.BLUE);
        testGraphics.setColor(Color.BLUE);
        
        standardGraphics.drawString("Italic Text", 50, 150);
        testGraphics.drawString("Italic Text", 50, 150);
        
        compareImages("drawString - italic");
        clearImages();
        
        // Test float coordinates
        standardGraphics.setColor(Color.GREEN);
        testGraphics.setColor(Color.GREEN);
        
        standardGraphics.drawString("Float coords", 50.5f, 200.3f);
        testGraphics.drawString("Float coords", 50.5f, 200.3f);
        
        
        compareImages("drawString - float coordinates");
    }

    @Test
    void testTransformations() {
        // Test translation
        standardGraphics.setColor(Color.RED);
        testGraphics.setColor(Color.RED);
        
        standardGraphics.translate(50, 50);
        testGraphics.translate(50, 50);
        
        standardGraphics.fillRect(0, 0, 40, 30);
        testGraphics.fillRect(0, 0, 40, 30);
        
        compareImages("transform - translate");
        
        resetGraphics();
        
        // Test rotation
        standardGraphics.setColor(Color.BLUE);
        testGraphics.setColor(Color.BLUE);
        
        standardGraphics.translate(150, 100);
        testGraphics.translate(150, 100);
        
        standardGraphics.rotate(Math.PI / 4); // 45 degrees
        testGraphics.rotate(Math.PI / 4);
        
        standardGraphics.fillRect(-20, -15, 40, 30);
        testGraphics.fillRect(-20, -15, 40, 30);
        
        compareImages("transform - rotate");
        
        resetGraphics();
        
        // Test scaling
        standardGraphics.setColor(Color.GREEN);
        testGraphics.setColor(Color.GREEN);
        
        standardGraphics.translate(200, 150);
        testGraphics.translate(200, 150);
        
        standardGraphics.scale(2.0, 1.5);
        testGraphics.scale(2.0, 1.5);
        
        standardGraphics.fillOval(-15, -10, 30, 20);
        testGraphics.fillOval(-15, -10, 30, 20);
        
        compareImages("transform - scale");
    }

    @Test
    void testClipping() {
        // Test rectangular clipping
        standardGraphics.setClip(50, 50, 100, 80);
        testGraphics.setClip(50, 50, 100, 80);
        
        standardGraphics.setColor(Color.RED);
        testGraphics.setColor(Color.RED);
        
        // Draw a large rectangle that extends beyond clip
        standardGraphics.fillRect(0, 0, 200, 150);
        testGraphics.fillRect(0, 0, 200, 150);
        
        compareImages("clipping - rectangular");
        
        resetGraphics();
        
        // Test circular clipping
        Shape circleClip = new Ellipse2D.Float(100, 100, 80, 80);
        standardGraphics.setClip(circleClip);
        testGraphics.setClip(circleClip);
        
        standardGraphics.setColor(Color.BLUE);
        testGraphics.setColor(Color.BLUE);
        
        standardGraphics.fillRect(80, 80, 120, 120);
        testGraphics.fillRect(80, 80, 120, 120);
        
        compareImages("clipping - circular");
    }

    @Test
    void testStrokeProperties() {
        // Test different stroke widths - this is intentionally cumulative to show all widths together
        for (int i = 1; i <= 5; i++) {
            BasicStroke stroke = new BasicStroke(i);
            standardGraphics.setStroke(stroke);
            testGraphics.setStroke(stroke);
            
            standardGraphics.setColor(Color.BLACK);
            testGraphics.setColor(Color.BLACK);
            
            standardGraphics.drawLine(50, 30 + i * 20, 200, 30 + i * 20);
            testGraphics.drawLine(50, 30 + i * 20, 200, 30 + i * 20);
        }
        
        compareImages("stroke - different widths");
        clearImages();
        
        // Test dashed stroke
        float[] dashPattern = {10.0f, 5.0f, 3.0f, 5.0f};
        BasicStroke dashedStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, 
            BasicStroke.JOIN_MITER, 10.0f, dashPattern, 0.0f);
        
        standardGraphics.setStroke(dashedStroke);
        testGraphics.setStroke(dashedStroke);
        
        standardGraphics.setColor(Color.RED);
        testGraphics.setColor(Color.RED);
        
        standardGraphics.drawRect(50, 150, 100, 60);
        testGraphics.drawRect(50, 150, 100, 60);
        
        compareImages("stroke - dashed");
    }

    @Test
    void testColorAndPaint() {
        // Test basic colors - intentionally cumulative to show color palette
        Color[] colors = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.MAGENTA, Color.CYAN};
        
        for (int i = 0; i < colors.length; i++) {
            standardGraphics.setColor(colors[i]);
            testGraphics.setColor(colors[i]);
            
            standardGraphics.fillRect(50 + i * 35, 50, 30, 40);
            testGraphics.fillRect(50 + i * 35, 50, 30, 40);
        }
        
        compareImages("color - basic colors");
        clearImages();
        
        // Test transparency - intentionally cumulative to show transparency gradient
        for (int alpha = 255; alpha > 0; alpha -= 51) {
            Color transparentBlue = new Color(0, 0, 255, alpha);
            standardGraphics.setColor(transparentBlue);
            testGraphics.setColor(transparentBlue);
            
            int x = 50 + (255 - alpha) / 51 * 30;
            standardGraphics.fillOval(x, 150, 25, 25);
            testGraphics.fillOval(x, 150, 25, 25);
        }
        
        compareImages("color - transparency");
    }

    @Test
    void testRenderingHints() {
        // Test antialiasing for text - intentionally shows both together for comparison
        Font largeFont = new Font("Arial", Font.PLAIN, 24);
        standardGraphics.setFont(largeFont);
        testGraphics.setFont(largeFont);
        
        // Without antialiasing
        standardGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        testGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        
        standardGraphics.setColor(Color.BLACK);
        testGraphics.setColor(Color.BLACK);
        
        standardGraphics.drawString("No AA", 50, 50);
        testGraphics.drawString("No AA", 50, 50);
        
        // With antialiasing
        standardGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        testGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        standardGraphics.drawString("With AA", 50, 100);
        testGraphics.drawString("With AA", 50, 100);
        
        compareImages("rendering hints - antialiasing");
    }

    @Test
    void testCopyArea() {
        // Draw something to copy
        standardGraphics.setColor(Color.RED);
        testGraphics.setColor(Color.RED);
        
        standardGraphics.fillOval(50, 50, 60, 60);
        testGraphics.fillOval(50, 50, 60, 60);
        
        // Copy the area - intentionally cumulative to show multiple copies
        standardGraphics.copyArea(50, 50, 60, 60, 100, 0);
        testGraphics.copyArea(50, 50, 60, 60, 100, 0);
        
        // Copy to another location
        standardGraphics.copyArea(50, 50, 60, 60, 0, 100);
        testGraphics.copyArea(50, 50, 60, 60, 0, 100);
        
        compareImages("copyArea - multiple copies");
    }

    @Test
    void testClearRect() {
        // Fill background with color first
        standardGraphics.setColor(Color.BLUE);
        testGraphics.setColor(Color.BLUE);
        
        standardGraphics.fillRect(0, 0, width, height);
        testGraphics.fillRect(0, 0, width, height);
        
        // Clear rectangular areas
        standardGraphics.clearRect(50, 50, 80, 60);
        testGraphics.clearRect(50, 50, 80, 60);
        
        standardGraphics.clearRect(200, 100, 60, 80);
        testGraphics.clearRect(200, 100, 60, 80);
        
        compareImages("clearRect - cleared areas");
    }

    private void compareImages(String testName) {
        // Convert AbstractBufferedImages to BufferedImages for comparison
        Rectangle bounds = new Rectangle(0, 0, width, height);
        BufferedImage standardBufferedImage = standardImage.toBufferedImage(bounds);
        BufferedImage testBufferedImage = testImage.toBufferedImage(bounds);
        
        ImageComparisonResult result = new ImageComparison(standardBufferedImage, testBufferedImage).compareImages();
        
        // If comparison fails, show visual dialog before assertion
        if (result.getImageComparisonState() != ImageComparisonState.MATCH) {
            displayComparisonFailure(testName, standardBufferedImage, testBufferedImage, result);
        }
        
        assertEquals(ImageComparisonState.MATCH, result.getImageComparisonState(),
            "Images should match for test: " + testName);
    }
    
    private void displayComparisonFailure(String testName, BufferedImage expected, BufferedImage actual, ImageComparisonResult result) {
        System.out.println("TEST FAILURE: " + testName + " - Displaying images for visual inspection");
        
        JDialog dialog = new JDialog((Frame) null, "Test Failure: " + testName, true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setLayout(new BorderLayout());
        
        // Create main panel with labels
        JPanel mainPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Expected image
        JPanel expectedPanel = new JPanel(new BorderLayout());
        expectedPanel.add(new JLabel("Expected (Standard)", SwingConstants.CENTER), BorderLayout.NORTH);
        expectedPanel.add(new JLabel(new ImageIcon(expected)), BorderLayout.CENTER);
        
        // Actual image  
        JPanel actualPanel = new JPanel(new BorderLayout());
        actualPanel.add(new JLabel("Actual (InfiniteBufferedImage)", SwingConstants.CENTER), BorderLayout.NORTH);
        actualPanel.add(new JLabel(new ImageIcon(actual)), BorderLayout.CENTER);
        
        // Difference image (if available)
        JPanel diffPanel = new JPanel(new BorderLayout());
        diffPanel.add(new JLabel("Difference", SwingConstants.CENTER), BorderLayout.NORTH);
        try {
            BufferedImage diffImage = result.getResult();
            if (diffImage != null) {
                diffPanel.add(new JLabel(new ImageIcon(diffImage)), BorderLayout.CENTER);
            } else {
                diffPanel.add(new JLabel("No difference image available", SwingConstants.CENTER), BorderLayout.CENTER);
            }
        } catch (Exception e) {
            diffPanel.add(new JLabel("Difference image not available", SwingConstants.CENTER), BorderLayout.CENTER);
        }
        
        // Info panel
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.add(new JLabel("Test Info", SwingConstants.CENTER), BorderLayout.NORTH);
        JTextArea infoText = new JTextArea();
        infoText.setEditable(false);
        infoText.setText(
            "Test: " + testName + "\n" +
            "State: " + result.getImageComparisonState() + "\n" +
            "Expected Size: " + expected.getWidth() + "x" + expected.getHeight() + "\n" +
            "Actual Size: " + actual.getWidth() + "x" + actual.getHeight() + "\n" +
            "Difference %: " + String.format("%.2f%%", result.getDifferencePercent())
        );
        JScrollPane infoScroll = new JScrollPane(infoText);
        infoPanel.add(infoScroll, BorderLayout.CENTER);
        
        mainPanel.add(expectedPanel);
        mainPanel.add(actualPanel);
        mainPanel.add(diffPanel);
        mainPanel.add(infoPanel);
        
        dialog.add(mainPanel, BorderLayout.CENTER);
        
        // Add close button
        JButton closeButton = new JButton("Close and Continue Test");
        closeButton.addActionListener(e -> dialog.dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.pack();
        dialog.setLocationRelativeTo(null); // Center on screen
        dialog.setVisible(true); // This will block until dialog is closed
    }

    private void clearImages() {
        // Clear both images to white background
        standardGraphics.setColor(Color.WHITE);
        testGraphics.setColor(Color.WHITE);
        standardGraphics.fillRect(0, 0, width, height);
        testGraphics.fillRect(0, 0, width, height);
    }

    private void resetGraphics() {
        if (standardGraphics != null) {
            standardGraphics.dispose();
        }
        if (testGraphics != null) {
            testGraphics.dispose();
        }
        
        // Recreate images using AbstractBufferedImage interface
        standardImage = new AbstractBufferedImage.BufferedImageWrapper(width, height, BufferedImage.TYPE_3BYTE_BGR);
        testImage = new InfiniteBufferedImage();
        
        standardGraphics = standardImage.createGraphics();
        testGraphics = testImage.createGraphics();
        
        // Set white background for both
        standardGraphics.setColor(Color.WHITE);
        testGraphics.setColor(Color.WHITE);
        standardGraphics.fillRect(0, 0, width, height);
        testGraphics.fillRect(0, 0, width, height);
    }
}