package sk.upjs.zirro.fpm10sensor;

import java.awt.Color;
import java.awt.image.BufferedImage;

public final class FingerprintUtils {

    public static BufferedImage fingerprintScanToImage(int[][] fingerprintScan) {
        int height = fingerprintScan.length;
        int width = fingerprintScan[0].length;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = fingerprintScan[y][x];
                Color c = new Color(pixel % 256, pixel % 256, pixel % 256);
                image.setRGB(x, y, c.getRGB());
            }
        }

        return image;
    }

    public static int[][] imageToFingerprintScan(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[][] fingerprintScan = new int[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color c = new Color(image.getRGB(x,y));
                fingerprintScan[y][x] = c.getBlue();
            }
        }
        return fingerprintScan;
    }
}