package com.novospir.libraries;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.TextLayout;
import java.awt.geom.*;
import java.awt.image.*;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Map;
import java.util.logging.Logger;

import static com.novospir.libraries.InfiniteBufferedImage.TILE_SIZE;

/*
Note: some pixel deviation; possibly anti-aliasing variations -> ignored

todo:
    optimize all functions
    overload function correctly
    refactor 'duplicate code' across functions
    implement 'dispose' ie correctly delete data freeing up space

Todo: The functions of this class, need to be optimized, specifically, if nodes can
        be clipped to only store a color (if they're only one color), then change all the functions
        to persevere the data.

Todo: need testing for each transformation, for each not tested yet - ie transform an copyArea
 */

class QuadGraphics2D extends Graphics2D {
    private final InfiniteBufferedImage image;
    private final Graphics2D delegate;
    private volatile boolean isDisposed;

    /* ------ SETTERS ------ */
    @Override
    public void setPaint(Paint p) {
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        delegate.setPaint(p);
    }

    @Override
    public void setStroke(Stroke s) {
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        delegate.setStroke(s);
    }

    @Override
    public void setComposite(Composite c) {
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        delegate.setComposite(c);
    }

    @Override
    public void setRenderingHints(Map<?, ?> h) {
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        delegate.setRenderingHints(h);
    }

    @Override
    public void addRenderingHints(Map<?, ?> h) {
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        delegate.addRenderingHints(h);
    }

    @Override
    public void setFont(Font font) {
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        delegate.setFont(font);
    }

    @Override
    public void setColor(Color c) {
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        delegate.setColor(c);
    }

    @Override
    public void setPaintMode() {
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        delegate.setPaintMode();
    }

    @Override
    public void setXORMode(Color c1) {
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        delegate.setXORMode(c1);
    }

    @Override
    public void setClip(Shape clip) {
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        delegate.setClip(clip);
    }

    @Override
    public void setBackground(Color color) {
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        delegate.setBackground(color);
    }

    @Override
    public void setTransform(AffineTransform t) {
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        delegate.setTransform(t);
    }

    @Override
    public void setRenderingHint(RenderingHints.Key hintKey, Object hintValue) {
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        delegate.setRenderingHint(hintKey, hintValue);
    }

    @Override
    public void setClip(int x, int y, int width, int height) {
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        delegate.setClip(x, y, width, height);
    }

    /* ------ GETTERS ------ */
    @Override
    public Paint getPaint() {
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        return delegate.getPaint();
    }

    @Override
    public Stroke getStroke() {
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        return delegate.getStroke();
    }

    @Override
    public Composite getComposite() {
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        return delegate.getComposite();
    }

    @Override
    public RenderingHints getRenderingHints() {
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        return delegate.getRenderingHints();
    }

    @Override
    public Object getRenderingHint(RenderingHints.Key hintKey) {
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        return delegate.getRenderingHint(hintKey);
    }

    @Override
    public Font getFont() {
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        return delegate.getFont();
    }

    @Override
    public Color getColor() {
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        return delegate.getColor();
    }

    @Override
    public Shape getClip() {
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        return delegate.getClip();
    }

    @Override
    public Rectangle getClipBounds() {
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        return delegate.getClipBounds();
    }

    @Override
    public Color getBackground() {
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        return delegate.getBackground();
    }

    @Override
    public AffineTransform getTransform() {
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        return delegate.getTransform();
    }

    @Override
    public GraphicsConfiguration getDeviceConfiguration() {
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        return delegate.getDeviceConfiguration();
    }

    @Override
    public FontMetrics getFontMetrics(Font f) {
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        return delegate.getFontMetrics(f);
    }

    @Override
    public FontRenderContext getFontRenderContext() {
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        return delegate.getFontRenderContext();
    }

    /* ------ END OF GETTERS/SETTERS ------ */

    public QuadGraphics2D(InfiniteBufferedImage image) {
        this.image = image;
        this.delegate = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
                .createGraphics();
    }

    /* ------ UTILITY ------ */
    private AffineTransform buildLocalTransform(QuadNode leaf) {
        AffineTransform t = new AffineTransform(); // global/user transform
        t.translate(-leaf.x, -leaf.y);             // move into tile-local space
        t.concatenate(getTransform());
        return t;
    }

    private Graphics2D prepareTileGraphics(QuadNode leaf) {
        Graphics2D g = leaf.image.createGraphics();
        g.setRenderingHints(getRenderingHints());
        g.setComposite(getComposite());
        g.setPaint(getPaint());
        g.setStroke(getStroke());
        g.setFont(getFont());

        // Apply combined transform (global + tile shift)
        g.setTransform(buildLocalTransform(leaf));

        // Clip is specified in user space; set it AFTER transform
        Shape clip = getClip();
        if (clip != null) g.setClip(clip);

        return g;
    }

    /* ------ LIFECYCLE ------ */
    @Override
    public Graphics create() {
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        throw new UnsupportedOperationException("Todo: Implement function");
        // todo: copy state onto new object
        // return new QuadGraphics2D(image);
    }

    @Override
    public void dispose() {
        // todo: scan quadtree and remove empty leaves
        if(isDisposed) return;
        isDisposed = true;
        this.delegate.dispose();
    }

    /* ------ DRAWING ------ */

    @Override
    public void draw(Shape shape) {
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        if (shape == null) return;

        // 1. Get the bounding box of the shape
        Rectangle2D bounds = getTransformedBoundsRect(shape);

        // 2. Find all leaves that intersect the bounding box
        for (QuadNode leaf : image.findLeaves(bounds, true)) {
            if (leaf == null || leaf.image == null) continue;
            Graphics2D g2 = prepareTileGraphics(leaf);
            g2.draw(shape);
            g2.dispose();
        }

        this.image.markBoundsDirty();
    }

    @Override @Deprecated
    public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        throw new UnsupportedOperationException("Todo: Implement function");
    }

    @Override @Deprecated
    public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        throw new UnsupportedOperationException("Todo: Implement function");
    }

    @Override
    public void drawString(String str, int x, int y) {
        this.drawString(str, (float) x, (float) y); // actions are the same
    }

    @Override
    public void drawString(String str, float x, float y) {
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        if (str == null || str.isEmpty()) return;

        // 1. Create a GlyphVector for the string
        Font font = getFont();
        FontRenderContext frc = getFontRenderContext();
        GlyphVector gv = font.createGlyphVector(frc, str);

        // 2. Get the visual bounds of the string
        Rectangle2D shape = gv.getVisualBounds();
        Rectangle bounds = getTransformedBoundsRect(shape);

        // 3. Find all leaves that intersect the bounding box
        for (QuadNode leaf : image.findLeaves(bounds, true)) {
            if (leaf == null || leaf.image == null) continue;
            Graphics2D g = prepareTileGraphics(leaf);
            g.drawString(str, x, y);
            g.dispose();
        }

        this.image.markBoundsDirty();
    }

    @Override
    public void drawString(AttributedCharacterIterator iterator, int x, int y) {
        this.drawString(iterator, (float) x, (float) y); // actions are the same
    }

    @Override
    public void drawString(AttributedCharacterIterator iterator, float x, float y) {
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        if (iterator == null) return;

        // 1. Create a TextLayout for the attributed string
        FontRenderContext frc = getFontRenderContext();
        TextLayout layout = new TextLayout(iterator, frc);

        // 2. Get the visual bounds of the layout
        Rectangle2D shape = layout.getBounds();
        Rectangle bounds = getTransformedBoundsRect(shape);

        // 3. Find all leaves that intersect the bounding box
        for (QuadNode leaf : image.findLeaves(bounds, true)) {
            if (leaf == null || leaf.image == null) continue;
            Graphics2D g = prepareTileGraphics(leaf);
            layout.draw(g, x, y);
            g.dispose();
        }

        this.image.markBoundsDirty();
    }

    @Override
    public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        BufferedImage filtered = (op != null) ? op.filter(img, null) : img;
        drawImage(filtered, x, y, null);
    }

    @Override
    public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
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
        for (int tileY = bounds.y / TILE_SIZE; tileY <= (bounds.y + bounds.height) / TILE_SIZE; tileY++) {
            for (int tileX = bounds.x / TILE_SIZE; tileX <= (bounds.x + bounds.width) / TILE_SIZE; tileX++) {
                BufferedImage tile = image.getTileImage(tileX * TILE_SIZE, tileY * TILE_SIZE, true);
                if (tile == null) continue;

                Graphics2D g2d = tile.createGraphics();

                // Set clip so we don’t overdraw outside this tile
                g2d.setClip(0, 0, TILE_SIZE, TILE_SIZE);

                // Calculate destination offset
                int offsetX = tileX * TILE_SIZE;
                int offsetY = tileY * TILE_SIZE;

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

        this.image.markBoundsDirty();
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
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        if (img == null) return true;

        int dWidth = dx2 - dx1;
        int dHeight = dy2 - dy1;
        int sWidth = sx2 - sx1;
        int sHeight = sy2 - sy1;
        if (dWidth == 0 || dHeight == 0 || sWidth == 0 || sHeight == 0) return true;

        int iw = img.getWidth(observer);
        int ih = img.getHeight(observer);
        if (iw < 0 || ih < 0) return false;

        int minDx = Math.min(dx1, dx2);
        int minDy = Math.min(dy1, dy2);
        int w = Math.abs(dWidth);
        int h = Math.abs(dHeight);
        Rectangle dstBounds = new Rectangle(minDx, minDy, w, h);

        for (QuadNode leaf : image.findLeaves(dstBounds.x, dstBounds.y, dstBounds.width, dstBounds.height, true)) {
            if (leaf == null || leaf.image == null) continue;

            Graphics2D g = leaf.image.createGraphics();
            applyLocalClip(g, leaf);
            g.setRenderingHints(getRenderingHints());
            g.setComposite(getComposite());

            // Optional background fill (only inside dst rect for this tile)
            if (bgcolor != null) {
                Rectangle tileRect = new Rectangle(leaf.x, leaf.y, leaf.size, leaf.size);
                Rectangle r = dstBounds.intersection(tileRect);
                if (!r.isEmpty()) {
                    Color oc = g.getColor();
                    g.setColor(bgcolor);
                    g.fillRect(r.x - leaf.x, r.y - leaf.y, r.width, r.height);
                    g.setColor(oc);
                }
            }

            g.drawImage(img,
                    dx1 - leaf.x, dy1 - leaf.y, dx2 - leaf.x, dy2 - leaf.y,
                    sx1, sy1, sx2, sy2,
                    observer);

            g.dispose();
        }

        this.image.markBoundsDirty();
        return true;
    }

    @Override
    public void drawGlyphVector(GlyphVector g, float x, float y) {
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        if (g == null) return;

        // 1. Get the visual bounds of the glyph vector
        Rectangle2D bounds = g.getVisualBounds();
        float minX = (float) (x + bounds.getX());
        float minY = (float) (y + bounds.getY());
        float width = (float) bounds.getWidth();
        float height = (float) bounds.getHeight();

        // 2. Find all leaves that intersect the bounding box
        int searchMinX = (int) Math.floor(minX);
        int searchMinY = (int) Math.floor(minY);
        int searchWidth = (int) Math.ceil(width);
        int searchHeight = (int) Math.ceil(height);

        for (QuadNode leaf : image.findLeaves(searchMinX, searchMinY, searchWidth, searchHeight, true)) {
            if (leaf == null || leaf.image == null) continue;
            Graphics2D g2 = prepareTileGraphics(leaf);
            g2.drawGlyphVector(g, x, y);
            g2.dispose();
        }

        this.image.markBoundsDirty();
    }

    @Override
    public void fill(Shape s) {
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        if (s == null) return;

        // 1. Get the bounding box of the shape
        Rectangle2D bounds = s.getBounds2D();
        int minX = (int) Math.floor(bounds.getX());
        int minY = (int) Math.floor(bounds.getY());
        int width = (int) Math.ceil(bounds.getWidth());
        int height = (int) Math.ceil(bounds.getHeight());

        // 2. Find all leaves that intersect the bounding box
        for (QuadNode leaf : image.findLeaves(minX, minY, width, height, true)) {
            if (leaf == null || leaf.image == null) continue;
            Graphics2D g = prepareTileGraphics(leaf);
            g.fill(s);
            g.dispose();
        }

        this.image.markBoundsDirty();
    }

    @Override
    public void drawLine(int x1, int y1, int x2, int y2) {
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        Shape shape = new Line2D.Double(x1, y1, x2, y2);
        Rectangle bounds = getTransformedBoundsRect(shape);

        // 2. Find all leaves in the bounding box
        for (QuadNode leaf : image.findLeaves(bounds, true)) {
            if (leaf == null || leaf.image == null) continue;
            Graphics2D g = prepareTileGraphics(leaf);
            g.drawLine(x1, y1, x2, y2);
            g.dispose();
        }

        this.image.markBoundsDirty();
    }

    @Override
    public void drawRect(int x, int y, int width, int height) {
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        Shape shape = new Rectangle2D.Double(x, y, width, height);
        Rectangle bounds = getTransformedBoundsRect(shape);

        for (QuadNode leaf : image.findLeaves(bounds, true)) {
            if (leaf == null || leaf.image == null) continue;
            Graphics2D g = prepareTileGraphics(leaf);
            g.drawRect(x, y, width, height);
            g.dispose();
        }

        this.image.markBoundsDirty();
    }

    // getClip returns global coordinates, applies local coordinates to local graphics2d
    private void applyLocalClip(Graphics2D g, QuadNode leaf) {
        Shape clip = getClip();
        if (clip == null) return;
        AffineTransform toLocal = AffineTransform.getTranslateInstance(-leaf.x, -leaf.y);
        g.setClip(toLocal.createTransformedShape(clip));
    }

    private Rectangle getTransformedBoundsRect(Shape s) {
        if (s == null) return new Rectangle();

        AffineTransform transform = getTransform();
        if (transform.isIdentity()) {
            Rectangle2D bounds = s.getBounds2D();
            return new Rectangle(
                    (int) Math.floor(bounds.getX()),
                    (int) Math.floor(bounds.getY()),
                    (int) Math.ceil(bounds.getWidth()),
                    (int) Math.ceil(bounds.getHeight())
            );
        }

        // Transform the shape and get its bounds
        Shape transformedShape = transform.createTransformedShape(s);
        Rectangle2D bounds = transformedShape.getBounds2D();
        return new Rectangle(
                (int) Math.floor(bounds.getX()),
                (int) Math.floor(bounds.getY()),
                (int) Math.ceil(bounds.getWidth()),
                (int) Math.ceil(bounds.getHeight())
        );
    }

    @Override
    public void fillRect(int x, int y, int width, int height) {
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        Shape shape = new Rectangle2D.Double(x, y, width, height);
        Rectangle bounds = getTransformedBoundsRect(shape);

        for (QuadNode leaf : image.findLeaves(bounds, true)) {
            if (leaf == null || leaf.image == null) continue;
            Graphics2D g = prepareTileGraphics(leaf);
            g.fillRect(x, y, width, height);
            g.dispose();
        }

        this.image.markBoundsDirty();
    }

    @Override
    public void clearRect(int x, int y, int width, int height) {
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        Shape shape = new Rectangle2D.Double(x, y, width, height);
        Rectangle bounds = getTransformedBoundsRect(shape);

        for (QuadNode leaf : image.findLeaves(bounds, true)) {
        // todo: not sure - should we deleted leaf node?
            if (leaf == null || leaf.image == null) continue;
            Graphics2D g = prepareTileGraphics(leaf);
            g.clearRect(x, y, width, height);
            g.dispose();
        }

        this.image.markBoundsDirty();
    }

    @Override
    public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        Shape shape = new Rectangle2D.Double(x, y, width, height);
        Rectangle bounds = getTransformedBoundsRect(shape);

        for (QuadNode leaf : image.findLeaves(bounds, true)) {
            if (leaf == null || leaf.image == null) continue;
            Graphics2D g = prepareTileGraphics(leaf);
            g.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
            g.dispose();
        }

        this.image.markBoundsDirty();
    }

    @Override
    public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        Shape shape = new Rectangle2D.Double(x, y, width, height);
        Rectangle bounds = getTransformedBoundsRect(shape);

        for (QuadNode leaf : image.findLeaves(bounds, true)) {
            if (leaf == null || leaf.image == null) continue;
            Graphics2D g = prepareTileGraphics(leaf);
            g.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
            g.dispose();
        }

        this.image.markBoundsDirty();
    }

    @Override
    public void drawOval(int x, int y, int width, int height) {
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        Shape shape = new Ellipse2D.Double(x, y, width, height);
        Rectangle bounds = getTransformedBoundsRect(shape);

        for (QuadNode leaf : image.findLeaves(bounds, true)) {
            if (leaf == null || leaf.image == null) continue;
            Graphics2D g = prepareTileGraphics(leaf);
            g.drawOval(x, y, width, height);
            g.dispose();
        }

        this.image.markBoundsDirty();
    }

    @Override
    public void fillOval(int x, int y, int width, int height) {
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        Shape shape = new Ellipse2D.Double(x, y, width, height);
        Rectangle bounds = getTransformedBoundsRect(shape);
        
        for (QuadNode leaf : image.findLeaves(bounds, true)) {
            if (leaf == null || leaf.image == null) continue;
            Graphics2D g = prepareTileGraphics(leaf);
            g.fillOval(x, y, width, height);
            g.dispose();
        }

        this.image.markBoundsDirty();
    }

    @Override
    public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        Shape shape = new Arc2D.Double(x, y, width, height, startAngle, arcAngle, Arc2D.OPEN);
        Rectangle bounds = getTransformedBoundsRect(shape);

        // 1. Find all leaves that intersect the bounding box
        for (QuadNode leaf : image.findLeaves(bounds, true)) {
            if (leaf == null || leaf.image == null) continue;
            Graphics2D g = prepareTileGraphics(leaf);
            g.drawArc(x, y, width, height, startAngle, arcAngle);
            g.dispose();
        }

        this.image.markBoundsDirty();
    }

    @Override
    public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        Shape shape = new Arc2D.Double(x, y, width, height, startAngle, arcAngle, Arc2D.PIE);
        Rectangle bounds = getTransformedBoundsRect(shape);

        // 1. Find all leaves that intersect the bounding box
        for (QuadNode leaf : image.findLeaves(bounds, true)) {
            if (leaf == null || leaf.image == null) continue;
            Graphics2D g2 = prepareTileGraphics(leaf);
            g2.fillArc(x, y, width, height, startAngle, arcAngle);
            g2.dispose();
        }

        this.image.markBoundsDirty();
    }

    @Override
    public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        if (nPoints < 2) return;

        // Create a GeneralPath from the points
        GeneralPath path = new GeneralPath();
        path.moveTo(xPoints[0], yPoints[0]);
        for (int i = 1; i < nPoints; i++) {
            path.lineTo(xPoints[i], yPoints[i]);
        }

        Rectangle bounds = getTransformedBoundsRect(path);

        // 2. Find all leaves that intersect the bounding box
        for (QuadNode leaf : image.findLeaves(bounds, true)) {
            if (leaf == null || leaf.image == null) continue;
            Graphics2D g2 = prepareTileGraphics(leaf);
            g2.drawPolyline(xPoints, yPoints, nPoints);
            g2.dispose();
        }

        this.image.markBoundsDirty();
    }

    @Override
    public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        if (nPoints < 2) return;

        // Create a GeneralPath from the points
        GeneralPath path = new GeneralPath();
        path.moveTo(xPoints[0], yPoints[0]);
        for (int i = 1; i < nPoints; i++) {
            path.lineTo(xPoints[i], yPoints[i]);
        }
        path.closePath(); // Close the polygon

        Rectangle bounds = getTransformedBoundsRect(path);

        // 2. Find all leaves that intersect the bounding box
        for (QuadNode leaf : image.findLeaves(bounds, true)) {
            if (leaf == null || leaf.image == null) continue;
            Graphics2D g2 = prepareTileGraphics(leaf);
            g2.drawPolygon(xPoints, yPoints, nPoints);
            g2.dispose();
        }

        this.image.markBoundsDirty();
    }

    @Override
    public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        if (nPoints < 2) return;

        // Create a GeneralPath from the points
        GeneralPath path = new GeneralPath();
        path.moveTo(xPoints[0], yPoints[0]);
        for (int i = 1; i < nPoints; i++) {
            path.lineTo(xPoints[i], yPoints[i]);
        }
        path.closePath(); // Close the polygon

        Rectangle bounds = getTransformedBoundsRect(path);

        // 2. Find all leaves that intersect the bounding box
        for (QuadNode leaf : image.findLeaves(bounds, true)) {
            if (leaf == null || leaf.image == null) continue;
            Graphics2D g2 = prepareTileGraphics(leaf);
            g2.fillPolygon(xPoints, yPoints, nPoints);
            g2.dispose();
        }

        this.image.markBoundsDirty();
    }

    @Override
    public void drawPolygon(Polygon p) { // Note: Untested
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        if (p == null || p.npoints < 2) return;

        Rectangle bounds = getTransformedBoundsRect(p);
        for (QuadNode leaf : image.findLeaves(bounds, true)) {
            if (leaf == null || leaf.image == null) continue;
            Graphics2D g2 = prepareTileGraphics(leaf);
            g2.drawPolygon(p);
            g2.dispose();
        }

        this.image.markBoundsDirty();
    }

    @Override
    public void fillPolygon(Polygon p) { // Note: Untested
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        if (p == null || p.npoints < 3) return;

        Rectangle bounds = getTransformedBoundsRect(p);
        for (QuadNode leaf : image.findLeaves(bounds, true)) {
            if (leaf == null || leaf.image == null) continue;
            Graphics2D g2 = prepareTileGraphics(leaf);
            g2.fillPolygon(p);
            g2.dispose();
        }

        this.image.markBoundsDirty();
    }

    @Override
    public void drawChars(char[] data, int offset, int length, int x, int y) { // Note: Untested; Optimize
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        if (data == null || length <= 0) return;
        drawString(new String(data, offset, length), x, y);
    }

    @Override
    public void drawBytes(byte[] data, int offset, int length, int x, int y) { // Note: Untested; Optimize
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        if (data == null || length <= 0) return;
        drawString(new String(data, offset, length), x, y);
    }

    @Override
    public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        if (onStroke) {
            Stroke stroke = getStroke();
            s = stroke.createStrokedShape(s);
        }
        return s.intersects(rect);
    }

    /* ------ TRANSFORMS ------ */

    @Override
    public void translate(int x, int y) {
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        delegate.translate(x, y);
    }

    @Override
    public void translate(double tx, double ty) {
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        delegate.translate(tx, ty);
    }

    @Override
    public void clip(Shape s) { // Note: Untested
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        if (s == null) return;
        Shape current = getClip();
        if (current == null) {
            setClip(s);
        } else {
            Area a = new Area(current);
            a.intersect(new Area(s));
            setClip(a);
        }
    }

    @Override
    public void clipRect(int x, int y, int width, int height) { // Note: Untested
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        if (width <= 0 || height <= 0) {
            setClip(new Rectangle());
            return;
        }
        clip(new Rectangle(x, y, width, height));
    }

    @Override
    public Rectangle getClipBounds(Rectangle r) { // Note: Untested
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        return getClipBounds();
    }

    @Override
    public void copyArea(int x, int y, int width, int height, int dx, int dy) {
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        if (width <= 0 || height <= 0) return;
        
        // Calculate source and destination bounds
        Rectangle srcBounds = new Rectangle(x, y, width, height);
        Rectangle dstBounds = new Rectangle(x + dx, y + dy, width, height);
        
        // Find all leaves that intersect either source or destination
        Rectangle combinedBounds = srcBounds.union(dstBounds);
        
        // First, read all source pixels into a temporary buffer
        BufferedImage srcBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D srcGraphics = srcBuffer.createGraphics();
        
        // Copy source pixels from all tiles
        for (QuadNode leaf : image.findLeaves(srcBounds, false)) {
            if (leaf == null || leaf.image == null) continue;
            
            Rectangle tileBounds = new Rectangle(leaf.x, leaf.y, leaf.size, leaf.size);
            Rectangle intersection = srcBounds.intersection(tileBounds);
            
            if (!intersection.isEmpty()) {
                // Calculate source region in the buffer
                int bufferX = intersection.x - srcBounds.x;
                int bufferY = intersection.y - srcBounds.y;
                
                // Calculate source region in the tile
                int tileX = intersection.x - leaf.x;
                int tileY = intersection.y - leaf.y;
                
                // Copy from tile to buffer
                srcGraphics.drawImage(leaf.image,
                    bufferX, bufferY, bufferX + intersection.width, bufferY + intersection.height,
                    tileX, tileY, tileX + intersection.width, tileY + intersection.height,
                    null);
            }
        }
        srcGraphics.dispose();
        
        // Now draw the source buffer to all destination tiles
        for (QuadNode leaf : image.findLeaves(dstBounds, true)) {
            if (leaf == null || leaf.image == null) continue;
            
            Graphics2D g = leaf.image.createGraphics();
            g.setRenderingHints(getRenderingHints());
            g.setComposite(getComposite());
            
            Rectangle tileBounds = new Rectangle(leaf.x, leaf.y, leaf.size, leaf.size);
            Rectangle intersection = dstBounds.intersection(tileBounds);
            
            if (!intersection.isEmpty()) {
                // Calculate destination region in the tile
                int tileX = intersection.x - leaf.x;
                int tileY = intersection.y - leaf.y;
                
                // Calculate source region in the buffer
                int bufferX = intersection.x - dstBounds.x;
                int bufferY = intersection.y - dstBounds.y;
                
                // Draw from buffer to tile
                g.drawImage(srcBuffer,
                    tileX, tileY, tileX + intersection.width, tileY + intersection.height,
                    bufferX, bufferY, bufferX + intersection.width, bufferY + intersection.height,
                    null);
            }
            
            g.dispose();
        }

        this.image.markBoundsDirty();
    }

    @Override
    public void rotate(double theta) {
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        delegate.rotate(theta);
    }

    @Override
    public void rotate(double theta, double x, double y) {
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        delegate.rotate(theta, x, y);
    }

    @Override
    public void scale(double sx, double sy) {
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        delegate.scale(sx, sy);
    }

    @Override
    public void shear(double shx, double shy) { // Note: Untested
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        delegate.shear(shx, shy);
    }

    @Override
    public void transform(AffineTransform Tx) { // Note: Untested
        if (isDisposed) throw new IllegalStateException("Dispose was called on this object");
        delegate.transform(Tx);
    }

}

