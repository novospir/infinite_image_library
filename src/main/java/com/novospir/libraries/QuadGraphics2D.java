package com.novospir.libraries;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.*;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Map;

import static com.novospir.libraries.InfiniteBufferedImage.TILE_SIZE;

class QuadGraphics2D extends Graphics2D {
    private final InfiniteBufferedImage image;
    private final Graphics2D delegate;

    /* ---------- state setters that store locally ---------- */
    @Override public void setPaint(Paint p)             { delegate.setPaint(p); }
    @Override public void setStroke(Stroke s)           { delegate.setStroke(s); }
    @Override public void setComposite(Composite c)     { delegate.setComposite(c); }
    @Override public void setRenderingHints(Map<?, ?> h){ delegate.setRenderingHints(h); }
    @Override public void addRenderingHints(Map<?, ?> h){ delegate.addRenderingHints(h); }
    @Override public void setFont(Font font)            { delegate.setFont(font); }
    @Override public void setColor(Color c)             { delegate.setColor(c); }
    @Override public void setPaintMode()                { delegate.setPaintMode(); }
    @Override public void setXORMode(Color c1)          { delegate.setXORMode(c1); }
    @Override public void setClip(Shape clip)           { delegate.setClip(clip); }
    @Override public void setBackground(Color color)    { delegate.setBackground(color); }
    @Override public void setTransform(AffineTransform t){ delegate.setTransform(t); }
    @Override public void setRenderingHint(RenderingHints.Key hintKey, Object hintValue) { delegate.setRenderingHint(hintKey, hintValue); }
    @Override public void setClip(int x, int y, int width, int height) { delegate.setClip(x, y, width, height); }

    /* ... getters just return the cached value ... */

    @Override public Paint getPaint()                   { return delegate.getPaint(); }
    @Override public Stroke getStroke()                 { return delegate.getStroke(); }
    @Override public Composite getComposite()           { return delegate.getComposite(); }
    @Override public RenderingHints getRenderingHints() { return delegate.getRenderingHints(); }
    @Override public Object getRenderingHint(RenderingHints.Key hintKey) { return delegate.getRenderingHint(hintKey); }
    @Override public Font getFont()                     { return delegate.getFont(); }
    @Override public Color getColor()                   { return delegate.getColor(); }
    @Override public Shape getClip()                    { return delegate.getClip(); }
    @Override public Rectangle getClipBounds()          { return delegate.getClipBounds(); }
    @Override public Color getBackground()              { return delegate.getBackground(); }
    @Override public AffineTransform getTransform()     { return delegate.getTransform(); }
    @Override public GraphicsConfiguration getDeviceConfiguration() { return delegate.getDeviceConfiguration(); }
    @Override public FontMetrics getFontMetrics(Font f) { return delegate.getFontMetrics(f); }
    @Override public FontRenderContext getFontRenderContext() { return delegate.getFontRenderContext(); }

    // ----------- End of Get/Set ---------------

    public QuadGraphics2D(InfiniteBufferedImage image) {
        this.image = image;
        this.delegate = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
                .createGraphics();
    }

    @Override public Graphics create() { return this.delegate.create(); }

    @Override public void dispose() {

    }

    @Override
    public void draw(Shape s) {
        /*Rectangle2D bounds = getTransform().createTransformedShape(s).getBounds2D();
        for (BufferedImage tile : image.tilesWithin(bounds)) {
            Graphics2D g = tile.createGraphics();

            // copy state
            g.setPaint(getPaint());
            g.setStroke(getStroke());
            g.setComposite(getComposite());
            g.setRenderingHints(getRenderingHints());

            // move to tile‑local space
            AffineTransform tx = new AffineTransform(getTransform());
            tx.translate(-tile.getMinX(), -tile.getMinY());
            g.setTransform(tx);

            // actual draw
            g.draw(s);
            g.dispose();
        }*/
    }

    @Override
    public void drawRenderedImage(RenderedImage img, AffineTransform xform) {

    }

    @Override
    public void drawRenderableImage(RenderableImage img, AffineTransform xform) {

    }

    @Override
    public void drawString(String str, int x, int y) {

    }

    @Override
    public void drawString(String str, float x, float y) {

    }

    @Override
    public void drawString(AttributedCharacterIterator iterator, int x, int y) {

    }

    @Override
    public void drawString(AttributedCharacterIterator iterator, float x, float y) {

    }

    @Override
    public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
        BufferedImage filtered = (op != null) ? op.filter(img, null) : img;
        drawImage(filtered, x, y, null);
    }

    @Override
    public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
        // Handle null image
        if (img == null) return true;

        // Check if image is fully loaded
        int width = img.getWidth(obs);
        int height = img.getHeight(obs);
        if (width < 0 || height < 0) {
            if (obs != null) {
                obs.imageUpdate(img, ImageObserver.ALLBITS, 0, 0, -1, -1);
            }
            return false; // Not yet fully loaded
        }

        // Prepare transformed image for drawing
        BufferedImage src;
        boolean srcIsBuffered = img instanceof BufferedImage;

        if (srcIsBuffered && xform.isIdentity()) {
            // Shortcut: draw directly if image is BufferedImage and no transform
            src = (BufferedImage) img;
        } else {
            // Convert image to a BufferedImage (if not already)
            src = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = src.createGraphics();
            g2d.setComposite(AlphaComposite.Src);
            g2d.drawImage(img, 0, 0, obs);
            g2d.dispose();
        }

        // Create transformed image
        AffineTransformOp xformOp = new AffineTransformOp(xform, AffineTransformOp.TYPE_BILINEAR);
        BufferedImage transformed = xformOp.createCompatibleDestImage(src, null);
        xformOp.filter(src, transformed);

        // Now, draw the transformed image to your internal tile system
        Rectangle bounds = xformOp.getBounds2D(src).getBounds();

        // Loop through affected tiles (based on your 128x128 quadtree)
        for (int tileY = bounds.y / 128; tileY <= (bounds.y + bounds.height) / 128; tileY++) {
            for (int tileX = bounds.x / 128; tileX <= (bounds.x + bounds.width) / 128; tileX++) {
                BufferedImage tile = image.getTileImage(tileX, tileY, true);
                if (tile == null) continue;

                Graphics2D g2d = tile.createGraphics();

                // Set clip so we don’t overdraw outside this tile
                g2d.setClip(0, 0, 128, 128);

                // Calculate destination offset
                int offsetX = tileX * 128;
                int offsetY = tileY * 128;

                // Translate graphics context so tile is correctly aligned
                g2d.translate(-offsetX, -offsetY);

                // Composite mode (if you have a composite state, set here)
                g2d.setComposite(AlphaComposite.SrcOver);

                // Actually draw the image
                g2d.drawImage(transformed, 0, 0, null);

                g2d.dispose();
            }
        }

        // Notify observer (we assume draw completed)
        if (obs != null) {
            obs.imageUpdate(img, ImageObserver.ALLBITS, 0, 0, width, height);
        }

        return true;
    }

    @Override
    public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
        return drawImage(img, x, y, img.getWidth(observer), img.getHeight(observer),
                null, observer);
    }

    @Override
    public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
        return drawImage(img,
                x, y, x + width, y + height,
                0, 0, img.getWidth(observer), img.getHeight(observer),
                null, observer);
    }

    @Override
    public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
        return drawImage(img, x, y, img.getWidth(observer), img.getHeight(observer),
                bgcolor, observer);
    }

    @Override
    public boolean drawImage(Image img,
                             int x, int y, int width, int height,
                             Color bgcolor, ImageObserver observer) {
        return drawImage(img,
                x, y, x + width, y + height,
                0, 0, img.getWidth(observer), img.getHeight(observer),
                bgcolor, observer);
    }

    @Override
    public boolean drawImage(Image img,
                             int dx1, int dy1, int dx2, int dy2,
                             int sx1, int sy1, int sx2, int sy2,
                             ImageObserver observer) {
        return drawImage(img,
                dx1, dy1, dx2, dy2,
                sx1, sy1, sx2, sy2,
                null, observer);
    }

    @Override
    public boolean drawImage(Image img,
                             int dx1, int dy1, int dx2, int dy2,
                             int sx1, int sy1, int sx2, int sy2,
                             Color bgcolor, ImageObserver observer) {

        // Ensure width/height are valid
        int dWidth = dx2 - dx1;
        int dHeight = dy2 - dy1;
        int sWidth = sx2 - sx1;
        int sHeight = sy2 - sy1;
        if (dWidth == 0 || dHeight == 0 || sWidth == 0 || sHeight == 0)
            return true;

        // Validate image readiness
        if (img == null || img.getWidth(observer) < 0 || img.getHeight(observer) < 0)
            return false;

        // Create a compatible BufferedImage for source (ensure we can access pixels)
        BufferedImage srcImage;
        if (img instanceof BufferedImage)
            srcImage = (BufferedImage) img;
        else {
            // Load full image
            srcImage = new BufferedImage(img.getWidth(observer), img.getHeight(observer), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = srcImage.createGraphics();
            g.drawImage(img, 0, 0, observer);
            g.dispose();
        }

        // Extract the region from source
        BufferedImage subImage = srcImage.getSubimage(
                Math.max(0, sx1),
                Math.max(0, sy1),
                Math.min(srcImage.getWidth(), sx2 - sx1),
                Math.min(srcImage.getHeight(), sy2 - sy1)
        );

        // Prepare destination transform: draw source onto destination box
        AffineTransform transform = new AffineTransform();
        transform.translate(dx1, dy1);
        transform.scale((double) dWidth / sWidth, (double) dHeight / sHeight);

        // Optionally fill background before painting if transparency present
        if (bgcolor != null && srcImage.getColorModel().hasAlpha()) {
            Shape originalClip = getClip();
            setClip(dx1, dy1, dWidth, dHeight);
            Color originalColor = getColor();
            setColor(bgcolor);
            fillRect(dx1, dy1, dWidth, dHeight);
            setColor(originalColor);
            setClip(originalClip);
        }

        // Draw into our quadtree structure: iterate through affected tiles
        Rectangle drawBounds = new Rectangle(dx1, dy1, dWidth, dHeight);
        for (int tileX = drawBounds.x / TILE_SIZE; tileX <= (drawBounds.x + drawBounds.width - 1) / TILE_SIZE; tileX++) {
            for (int tileY = drawBounds.y / TILE_SIZE; tileY <= (drawBounds.y + drawBounds.height - 1) / TILE_SIZE; tileY++) {

                BufferedImage tile = this.image.getTileImage(tileX, tileY, true); // You must implement this
                if (tile == null) continue;

                Graphics2D gTile = tile.createGraphics();
                gTile.setRenderingHints(getRenderingHints());

                // Translate graphics to match tile offset
                int tileOffsetX = tileX * TILE_SIZE;
                int tileOffsetY = tileY * TILE_SIZE;
                AffineTransform localTx = new AffineTransform(transform);
                localTx.translate(-tileOffsetX, -tileOffsetY);
                gTile.setTransform(localTx);

                // Draw subimage with same transform as original
                gTile.drawImage(subImage, 0, 0, null);
                gTile.dispose();
            }
        }

        return true;
    }

    @Override
    public void drawGlyphVector(GlyphVector g, float x, float y) {

    }

    @Override
    public void fill(Shape s) {

    }

    @Override
    public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
        return false;
    }

    @Override
    public void translate(int x, int y) {

    }

    @Override
    public void clipRect(int x, int y, int width, int height) {

    }

    @Override
    public void copyArea(int x, int y, int width, int height, int dx, int dy) {

    }

    @Override
    public void drawLine(int x1, int y1, int x2, int y2) {

    }

    @Override
    public void fillRect(int x, int y, int width, int height) {

    }

    @Override
    public void clearRect(int x, int y, int width, int height) {

    }

    @Override
    public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {

    }

    @Override
    public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {

    }

    @Override
    public void drawOval(int x, int y, int width, int height) {

    }

    ///
    @Override
    public void fillOval(int x, int y, int width, int height) {

    }

    @Override
    public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {

    }

    @Override
    public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {

    }

    @Override
    public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {

    }

    @Override
    public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {

    }

    @Override
    public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {

    }

    @Override
    public void translate(double tx, double ty) {

    }

    @Override
    public void rotate(double theta) {

    }

    @Override
    public void rotate(double theta, double x, double y) {

    }

    @Override
    public void scale(double sx, double sy) {

    }

    @Override
    public void shear(double shx, double shy) {

    }

    @Override
    public void transform(AffineTransform Tx) {

    }

    @Override
    public void clip(Shape s) {

    }
}

