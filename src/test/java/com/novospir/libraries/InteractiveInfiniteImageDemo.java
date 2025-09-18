package com.novospir.libraries;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Simple interactive demo for InfiniteBufferedImage.
 *
 * Features:
 * - Left-drag: paint circles at world coordinates (demonstrates sparse growth)
 * - Right-drag: pan the viewport (demonstrates unbounded space)
 * - Mouse wheel: zoom in/out (discrete levels)
 * - Key 'E': export current viewport to PNG
 * - Key 'G': toggle tile grid overlay (128x128)
 * - Key 'B': draw random boxes across very large coordinates (stress sparsity)
 * - Key 'R': reset to origin and clear view (image data remains)
 *
 * Run from IDE or via: mvn -q -Dtest=com.novospir.libraries.InteractiveInfiniteImageDemo test
 * Or simply run this class' main() from test sources.
 */
public class InteractiveInfiniteImageDemo {

    // Optional: add a status bar legend for controls.

    public static void main(String[] args) {
        SwingUtilities.invokeLater(InteractiveInfiniteImageDemo::launch);
    }

    private static void launch() {
        JFrame frame = new JFrame("Infinite Image Demo");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        DemoPanel panel = new DemoPanel();
        frame.setContentPane(panel);
        frame.setSize(1000, 700);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static final class DemoPanel extends JPanel {
        private final InfiniteBufferedImage infinite;
        private Rectangle view; // world-space viewport
        private double zoom;
        private Point mouseDragStart;
        private Point viewStart;
        private boolean showGrid;
        private final Random random;

        DemoPanel() {
            this.infinite = new InfiniteBufferedImage(0, 0);
            this.view = new Rectangle(-300, -200, 1000, 700);
            this.zoom = 1.0;
            this.random = new Random(42);
            setBackground(new Color(0x1e1e1e));

            // Seed with a large diagonal and labels to illustrate negatives and far coords
            Graphics2D g = infinite.createGraphics();
            g.setColor(new Color(0x2e, 0xa0, 0x57));
            g.setStroke(new BasicStroke(2f));
            g.drawLine(-5000, -5000, 5000, 5000);
            g.setColor(Color.ORANGE);
            g.drawString("Origin (0,0)", 5, 15);
            g.setColor(Color.CYAN);
            g.drawString("(-2000, -2000)", -2000, -2000);
            g.dispose();

            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        mouseDragStart = e.getPoint();
                        viewStart = new Point(view.x, view.y);
                    } else if (SwingUtilities.isLeftMouseButton(e)) {
                        paintAt(e.getPoint());
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    mouseDragStart = null;
                    viewStart = null;
                }
            });

            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        if (mouseDragStart != null && viewStart != null) {
                            int dx = e.getX() - mouseDragStart.x;
                            int dy = e.getY() - mouseDragStart.y;
                            // Convert screen delta to world delta based on zoom
                            view.x = viewStart.x - (int) Math.round(dx / zoom);
                            view.y = viewStart.y - (int) Math.round(dy / zoom);
                            repaint();
                        }
                    } else if (SwingUtilities.isLeftMouseButton(e)) {
                        paintAt(e.getPoint());
                    }
                }
            });

            addMouseWheelListener(e -> {
                if (e.getWheelRotation() < 0) zoom = Math.min(8.0, zoom * 1.25);
                else zoom = Math.max(0.125, zoom / 1.25);

                // Zoom towards cursor: adjust view to keep cursor's world point stable
                Point p = e.getPoint();
                Point worldBefore = toWorld(p);
                int wx = worldBefore.x;
                int wy = worldBefore.y;
                // Recompute with new zoom
                double sx = p.x / zoom;
                double sy = p.y / zoom;
                view.x = wx - (int) Math.round(sx);
                view.y = wy - (int) Math.round(sy);
                repaint();
            });

            setFocusable(true);
            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_E:
                            exportViewport();
                            break;
                        case KeyEvent.VK_G:
                            showGrid = !showGrid;
                            repaint();
                            break;
                        case KeyEvent.VK_R:
                            view = new Rectangle(-300, -200, getWidth(), getHeight());
                            repaint();
                            break;
                        case KeyEvent.VK_B:
                            scatterBoxes();
                            repaint();
                            break;
                        case KeyEvent.VK_LEFT:
                            view.x -= 200;
                            repaint();
                            break;
                        case KeyEvent.VK_RIGHT:
                            view.x += 200;
                            repaint();
                            break;
                        case KeyEvent.VK_UP:
                            view.y -= 200;
                            repaint();
                            break;
                        case KeyEvent.VK_DOWN:
                            view.y += 200;
                            repaint();
                            break;
                        default:
                            break;
                    }
                }
            });

            new javax.swing.Timer(1000, e -> repaint()).start();
        }

        private void paintAt(Point screen) {
            Point world = toWorld(screen);
            Graphics2D g = infinite.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(new Color(255, 255, 0, 200));
            int r = 12;
            g.fill(new Rectangle2D.Double(world.x - r, world.y - r, r * 2.0, r * 2.0));
            g.setColor(Color.RED);
            g.drawOval(world.x - r, world.y - r, r * 2, r * 2);
            g.dispose();
            repaint();
        }

        private void scatterBoxes() {
            Graphics2D g = infinite.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            for (int i = 0; i < 200; i++) {
                int x = (random.nextBoolean() ? 1 : -1) * random.nextInt(20000);
                int y = (random.nextBoolean() ? 1 : -1) * random.nextInt(20000);
                int w = 50 + random.nextInt(200);
                int h = 50 + random.nextInt(200);
                g.setColor(new Color(random.nextInt(200) + 30, random.nextInt(200) + 30, random.nextInt(200) + 30, 180));
                g.fillRect(x, y, w, h);
                g.setColor(Color.BLACK);
                g.drawRect(x, y, w, h);
            }
            g.dispose();
        }

        private Point toWorld(Point screen) {
            int wx = view.x + (int) Math.round(screen.x / zoom);
            int wy = view.y + (int) Math.round(screen.y / zoom);
            return new Point(wx, wy);
        }

        private void exportViewport() {
            Rectangle worldRect = new Rectangle(view.x, view.y, (int) Math.round(getWidth() / zoom), (int) Math.round(getHeight() / zoom));
            BufferedImage out = infinite.toBufferedImage(worldRect);
            String ts = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File file = new File("export_" + ts + ".png");
            try {
                ImageIO.write(out, "png", file);
                System.out.println("Exported viewport to: " + file.getAbsolutePath());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        @Override
        protected void paintComponent(Graphics g0) {
            super.paintComponent(g0);
            Graphics2D g = (Graphics2D) g0;
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw current viewport from the infinite image
            Rectangle worldRect = new Rectangle(view.x, view.y, (int) Math.round(getWidth() / zoom), (int) Math.round(getHeight() / zoom));
            BufferedImage snapshot = infinite.toBufferedImage(worldRect);

            int drawW = (int) Math.round(snapshot.getWidth() * zoom);
            int drawH = (int) Math.round(snapshot.getHeight() * zoom);
            g.drawImage(snapshot, 0, 0, drawW, drawH, null);

            // Optional grid overlay to show 128x128 tiles
            if (showGrid) {
                g.setColor(new Color(255, 255, 255, 50));
                int tile = InfiniteBufferedImage.TILE_SIZE;
                int startX = (int) Math.floor((double) view.x / tile) * tile;
                int startY = (int) Math.floor((double) view.y / tile) * tile;
                int endX = view.x + worldRect.width;
                int endY = view.y + worldRect.height;
                for (int x = startX; x <= endX; x += tile) {
                    int sx = (int) Math.round((x - view.x) * zoom);
                    g.drawLine(sx, 0, sx, getHeight());
                }
                for (int y = startY; y <= endY; y += tile) {
                    int sy = (int) Math.round((y - view.y) * zoom);
                    g.drawLine(0, sy, getWidth(), sy);
                }
            }

            // HUD: view info and bounds
            g.setFont(getFont().deriveFont(Font.BOLD, 12f));
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRoundRect(10, getHeight() - 120, 420, 110, 12, 12);
            g.setColor(Color.WHITE);

            int leafCount = infinite.getAllocatedLeafCount();
            g.drawString("Allocated tiles: " + leafCount, 20, getHeight() - 100);

            g.drawString("View @ (" + view.x + ", " + view.y + ") z=" + String.format("%.2f", zoom), 20, getHeight() - 80);
            g.drawString("Viewport size (world): " + worldRect.width + " x " + worldRect.height, 20, getHeight() - 60);

            Rectangle logical = infinite.getLogicalBounds();
            g.drawString("Logical bounds: [x=" + logical.x + ", y=" + logical.y + ", w=" + logical.width + ", h=" + logical.height + "]", 20, getHeight() - 40);

            long used = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024);
            long total = Runtime.getRuntime().totalMemory() / (1024 * 1024);
            g.drawString("Memory: " + used + " MB / " + total + " MB", 20, getHeight() - 20);

            drawLegend(g);
        }

        /** Draws a simple legend of available controls */
        private void drawLegend(Graphics2D g) {
            String[] lines = {
                    "Controls:",
                    "Left-drag: paint circles",
                    "Right-drag: pan viewport",
                    "Mouse wheel: zoom in/out",
                    "E: export viewport to PNG",
                    "G: toggle tile grid",
                    "B: scatter random boxes",
                    "R: reset view to origin",
                    "Arrow keys: move viewport"
            };

            int desiredWidth = 175;
            int x = getWidth() - desiredWidth; // top-right corner
            int y = 20;
            int lineHeight = 16;

            // Background box
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRoundRect(x - 10, y - 10, desiredWidth, lines.length * lineHeight + 15, 12, 12);

            g.setColor(Color.WHITE);
            g.setFont(getFont().deriveFont(Font.PLAIN, 12f));
            for (int i = 0; i < lines.length; i++) {
                g.drawString(lines[i], x, y + i * lineHeight + 10);
            }
        }
    }
}


