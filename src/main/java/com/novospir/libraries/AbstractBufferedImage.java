package com.novospir.libraries;

import java.awt.*;
import java.awt.image.BufferedImage;

public interface AbstractBufferedImage {

    int getRGB(int x, int y);
    void setRGB(int x, int y, int rgb);
    Graphics2D createGraphics();
    AbstractWritableRaster getRaster();
    BufferedImage toBufferedImage(Rectangle bounds);

    class BufferedImageAdapter implements AbstractBufferedImage {
        private final BufferedImage bufferedImage;

        public BufferedImageAdapter(int width, int height, int imageType){
            this.bufferedImage = new BufferedImage(width, height, imageType);
        }

        @Override
        public int getRGB(int x, int y) {
            return bufferedImage.getRGB(x, y);
        }

        @Override
        public void setRGB(int x, int y, int rgb) {
            this.bufferedImage.setRGB(x, y, rgb);
        }

        @Override
        public Graphics2D createGraphics() {
            return this.bufferedImage.createGraphics();
        }

        @Override
        public AbstractWritableRaster getRaster() {
            return AbstractWritableRaster.WritableRasterAdapter.toWritableRasterAdapter(this.bufferedImage.getRaster());
        }

        @Override
        public BufferedImage toBufferedImage(Rectangle bounds) {
            return this.bufferedImage.getSubimage(bounds.x, bounds.y,
                    bounds.width, bounds.height);
        }
    }
}
