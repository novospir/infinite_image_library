package com.novospir.libraries;

import java.awt.*;
import java.awt.image.*;

public interface AbstractWritableRaster {

    // class tmp extends Raster {  }

    int getNumDataElements();
    int getNumBands();
    int getHeight();
    int getWidth();
    int getMinY();
    int getMinX();
    int getTransferType();
    Rectangle getBounds();
    void setDataElements(int x, int y, Object inData);
    void setDataElements(int x, int y, Raster inRaster);
    void setDataElements(int x, int y, int w, int h, Object inData);
    void setRect(Raster srcRaster);
    void setRect(int dx, int dy, Raster srcRaster);

    // ---------- PIXELS ---------------
    ///@see java.awt.image.Raster#getPixel(int, int, int[])
    int[] getPixel(int x, int y, int[] iArray);
    float[] getPixel(int x, int y, float[] fArray);
    double[] getPixel(int x, int y, double[] dArray);
    int[] getPixels(int x, int y, int w, int h, int[] iArray);
    float[] getPixels(int x, int y, int w, int h, float[] fArray);
    double[] getPixels(int x, int y, int w, int h, double[] dArray);
    void setPixel(int x, int y, int[] iArray);
    void setPixel(int x, int y, float[] fArray);
    void setPixel(int x, int y, double[] dArray);
    void setPixels(int x, int y, int w, int h, int[] iArray);
    void setPixels(int x, int y, int w, int h, float[] fArray);
    void setPixels(int x, int y, int w, int h, double[] dArray);

    // ---------- SAMPLES ---------------

    int getSample(int x, int y, int b);
    float getSampleFloat(int x, int y, int b);
    double getSampleDouble(int x, int y, int b);
    int[] getSamples(int x, int y, int w, int h, int b, int[] iArray);
    float[] getSamples(int x, int y, int w, int h, int b, float[] fArray);
    double[] getSamples(int x, int y, int w, int h, int b, double[] dArray);
    void setSample(int x, int y, int b, int s);
    void setSample(int x, int y, int b, float s);
    void setSample(int x, int y, int b, double s);
    void setSamples(int x, int y, int w, int h, int b, int[] iArray);
    void setSamples(int x, int y, int w, int h, int b, float[] fArray);
    void setSamples(int x, int y, int w, int h, int b, double[] dArray);

    class WritableRasterAdapter implements AbstractWritableRaster {
        private final WritableRaster wrapped;

        private WritableRasterAdapter(WritableRaster raster) {
            this.wrapped = raster;
        }

        public static WritableRasterAdapter toWritableRasterAdapter(WritableRaster raster){
            if (raster == null) return null;
            return new WritableRasterAdapter(raster);
        }

        @Override
        public int getNumDataElements() {
            return wrapped.getNumDataElements();
        }

        @Override
        public int getNumBands() {
            return wrapped.getNumBands();
        }

        @Override
        public int getHeight() {
            return wrapped.getHeight();
        }

        @Override
        public int getWidth() {
            return wrapped.getWidth();
        }

        @Override
        public int getMinY() {
            return wrapped.getMinY();
        }

        @Override
        public int getMinX() {
            return wrapped.getMinX();
        }

        @Override
        public int getTransferType() {
            return wrapped.getTransferType();
        }

        @Override
        public Rectangle getBounds() {
            return wrapped.getBounds();
        }

        @Override
        public void setDataElements(int x, int y, Object inData) {
            wrapped.setDataElements(x, y, inData);
        }

        @Override
        public void setDataElements(int x, int y, Raster inRaster) {
            wrapped.setDataElements(x, y, inRaster);
        }

        @Override
        public void setDataElements(int x, int y, int w, int h, Object inData) {
            wrapped.setDataElements(x, y, w, h, inData);
        }

        @Override
        public void setRect(Raster srcRaster) {
            wrapped.setRect(srcRaster);
        }

        @Override
        public void setRect(int dx, int dy, Raster srcRaster) {
            wrapped.setRect(dx, dy, srcRaster);
        }

        @Override
        public int[] getPixel(int x, int y, int[] iArray) {
            return wrapped.getPixel(x, y, iArray);
        }

        @Override
        public float[] getPixel(int x, int y, float[] fArray) {
            return wrapped.getPixel(x, y, fArray);
        }

        @Override
        public double[] getPixel(int x, int y, double[] dArray) {
            return wrapped.getPixel(x, y, dArray);
        }

        @Override
        public int[] getPixels(int x, int y, int w, int h, int[] iArray) {
            return wrapped.getPixels(x, y, w, h, iArray);
        }

        @Override
        public float[] getPixels(int x, int y, int w, int h, float[] fArray) {
            return wrapped.getPixels(x, y, w, h, fArray);
        }

        @Override
        public double[] getPixels(int x, int y, int w, int h, double[] dArray) {
            return wrapped.getPixels(x, y, w, h, dArray);
        }

        @Override
        public void setPixel(int x, int y, int[] iArray) {
            wrapped.setPixel(x, y, iArray);
        }

        @Override
        public void setPixel(int x, int y, float[] fArray) {
            wrapped.setPixel(x, y, fArray);
        }

        @Override
        public void setPixel(int x, int y, double[] dArray) {
            wrapped.setPixel(x, y, dArray);
        }

        @Override
        public void setPixels(int x, int y, int w, int h, int[] iArray) {
            wrapped.setPixels(x, y, w, h, iArray);
        }

        @Override
        public void setPixels(int x, int y, int w, int h, float[] fArray) {
            wrapped.setPixels(x, y, w, h, fArray);
        }

        @Override
        public void setPixels(int x, int y, int w, int h, double[] dArray) {
            wrapped.setPixels(x, y, w, h, dArray);
        }

        @Override
        public int getSample(int x, int y, int b) {
            return wrapped.getSample(x, y, b);
        }

        @Override
        public float getSampleFloat(int x, int y, int b) {
            return wrapped.getSampleFloat(x, y, b);
        }

        @Override
        public double getSampleDouble(int x, int y, int b) {
            return wrapped.getSampleDouble(x, y, b);
        }

        @Override
        public int[] getSamples(int x, int y, int w, int h, int b, int[] iArray) {
            return wrapped.getSamples(x, y, w, h, b, iArray);
        }

        @Override
        public float[] getSamples(int x, int y, int w, int h, int b, float[] fArray) {
            return wrapped.getSamples(x, y, w, h, b, fArray);
        }

        @Override
        public double[] getSamples(int x, int y, int w, int h, int b, double[] dArray) {
            return wrapped.getSamples(x, y, w, h, b, dArray);
        }

        @Override
        public void setSample(int x, int y, int b, int s) {
            wrapped.setSample(x, y, b, s);
        }

        @Override
        public void setSample(int x, int y, int b, float s) {
            wrapped.setSample(x, y, b, s);
        }

        @Override
        public void setSample(int x, int y, int b, double s) {
            wrapped.setSample(x, y, b, s);
        }

        @Override
        public void setSamples(int x, int y, int w, int h, int b, int[] iArray) {
            wrapped.setSamples(x, y, w, h, b, iArray);
        }

        @Override
        public void setSamples(int x, int y, int w, int h, int b, float[] fArray) {
            wrapped.setSamples(x, y, w, h, b, fArray);
        }

        @Override
        public void setSamples(int x, int y, int w, int h, int b, double[] dArray) {
            wrapped.setSamples(x, y, w, h, b, dArray);
        }
    }
}
