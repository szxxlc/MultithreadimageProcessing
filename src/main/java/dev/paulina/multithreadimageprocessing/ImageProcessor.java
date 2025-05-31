package dev.paulina.multithreadimageprocessing;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageProcessor {

    public static BufferedImage scale(BufferedImage img, int width, int height) {
        Image tmp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage scaled = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = scaled.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return scaled;
    }

    public static BufferedImage rotate90DegreesLeft(BufferedImage img) {
        int w = img.getWidth();
        int h = img.getHeight();
        BufferedImage rotated = new BufferedImage(h, w, img.getType());
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                rotated.setRGB(j, w - 1 - i, img.getRGB(i, j));
            }
        }
        return rotated;
    }

    public static BufferedImage rotate90DegreesRight(BufferedImage img) {
        int w = img.getWidth();
        int h = img.getHeight();
        BufferedImage rotated = new BufferedImage(h, w, img.getType());
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                rotated.setRGB(h - 1 - j, i, img.getRGB(i, j));
            }
        }
        return rotated;
    }

    public static BufferedImage negative(BufferedImage img) {
        BufferedImage negativeImg = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                int rgba = img.getRGB(x, y);
                int a = (rgba >> 24) & 0xff;
                int r = 255 - ((rgba >> 16) & 0xff);
                int g = 255 - ((rgba >> 8) & 0xff);
                int b = 255 - (rgba & 0xff);
                int newPixel = (a << 24) | (r << 16) | (g << 8) | b;
                negativeImg.setRGB(x, y, newPixel);
            }
        }
        return negativeImg;
    }

    public static BufferedImage threshold(BufferedImage img, int threshold) {
        BufferedImage threshImg = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                int rgba = img.getRGB(x, y);
                int a = (rgba >> 24) & 0xff;
                int r = (rgba >> 16) & 0xff;
                int g = (rgba >> 8) & 0xff;
                int b = rgba & 0xff;
                int gray = (r + g + b) / 3;
                int value = (gray >= threshold) ? 255 : 0;
                int newPixel = (a << 24) | (value << 16) | (value << 8) | value;
                threshImg.setRGB(x, y, newPixel);
            }
        }
        return threshImg;
    }

    public static BufferedImage edgeDetection(BufferedImage img) {
        int w = img.getWidth();
        int h = img.getHeight();
        BufferedImage edgeImg = new BufferedImage(w, h, img.getType());

        for (int x = 0; x < w - 1; x++) {
            for (int y = 0; y < h - 1; y++) {
                int rgb = img.getRGB(x, y);
                int rgbRight = img.getRGB(x + 1, y);
                int rgbDown = img.getRGB(x, y + 1);

                int diffR = Math.abs(((rgb >> 16) & 0xff) - ((rgbRight >> 16) & 0xff));
                int diffG = Math.abs(((rgb >> 8) & 0xff) - ((rgbRight >> 8) & 0xff));
                int diffB = Math.abs((rgb & 0xff) - (rgbRight & 0xff));
                int diffH = Math.abs(((rgb >> 16) & 0xff) - ((rgbDown >> 16) & 0xff));
                int diffS = Math.abs(((rgb >> 8) & 0xff) - ((rgbDown >> 8) & 0xff));
                int diffV = Math.abs((rgb & 0xff) - (rgbDown & 0xff));

                int diff = Math.max(diffR, Math.max(diffG, Math.max(diffB, Math.max(diffH, Math.max(diffS, diffV)))));
                int edgeColor = (0xff << 24) | (diff << 16) | (diff << 8) | diff;
                edgeImg.setRGB(x, y, edgeColor);
            }
        }
        return edgeImg;
    }
}
