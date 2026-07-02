package com.example.changshademo.captcha;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.SplittableRandom;

public class CaptchaUtils {

    public static final int DISPLAY_WIDTH = 340;
    public static final int DISPLAY_HEIGHT = 200;
    public static final int PIECE_BASE_SIZE = 36;
    private static final int MAX_AMPLITUDE = 10;
    private static final int TOLERANCE = 5;

    public static class CaptchaResult {
        public final String bgImageBase64;
        public final String pieceImageBase64;
        public final int targetX;
        public final int targetY;
        public final int pieceBaseSize;
        public final int maxAmplitude;
        public final int imageWidth;
        public final int imageHeight;
        public final String shapeType;

        public CaptchaResult(String bgImageBase64, String pieceImageBase64,
                             int targetX, int targetY, int pieceBaseSize, int maxAmplitude,
                             int imageWidth, int imageHeight, String shapeType) {
            this.bgImageBase64 = bgImageBase64;
            this.pieceImageBase64 = pieceImageBase64;
            this.targetX = targetX;
            this.targetY = targetY;
            this.pieceBaseSize = pieceBaseSize;
            this.maxAmplitude = maxAmplitude;
            this.imageWidth = imageWidth;
            this.imageHeight = imageHeight;
            this.shapeType = shapeType;
        }
    }

    public static CaptchaResult generate(BufferedImage original) {
        SplittableRandom random = new SplittableRandom();

        BufferedImage scaled = scaleImage(original, DISPLAY_WIDTH, DISPLAY_HEIGHT);
        int shapeChoice = random.nextInt(3);
        String[] shapeNames = {"triangle", "star", "puzzle"};
        String shapeType = shapeNames[shapeChoice];

        int amplitude = 8 + random.nextInt(MAX_AMPLITUDE - 8 + 1);
        int[] edgeProfile = generateEdgeProfile(PIECE_BASE_SIZE, shapeChoice, amplitude, random);

        int maxOffset = 0;
        for (int v : edgeProfile) if (v > maxOffset) maxOffset = v;

        int targetX = 30 + random.nextInt(DISPLAY_WIDTH - PIECE_BASE_SIZE - maxOffset - 60);
        int targetY = 10 + random.nextInt(DISPLAY_HEIGHT - PIECE_BASE_SIZE - 20);
        int actualWidth = PIECE_BASE_SIZE + maxOffset;

        // ---- Extract piece image ----
        BufferedImage pieceImg = new BufferedImage(actualWidth, PIECE_BASE_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D pg = pieceImg.createGraphics();
        pg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (int row = 0; row < PIECE_BASE_SIZE; row++) {
            int rightOffset = edgeProfile[row];
            for (int col = 0; col < PIECE_BASE_SIZE + rightOffset; col++) {
                int srcX = targetX + col;
                int srcY = targetY + row;
                if (srcX < scaled.getWidth() && srcY < scaled.getHeight()) {
                    int rgb = scaled.getRGB(srcX, srcY);
                    pieceImg.setRGB(col, row, rgb);
                }
            }
        }
        pg.dispose();

        // ---- Create background with cutout ----
        BufferedImage bgImg = new BufferedImage(DISPLAY_WIDTH, DISPLAY_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D bgG = bgImg.createGraphics();
        bgG.drawImage(scaled, 0, 0, null);

        // Draw cutout: first draw a dark overlay, then a dashed border
        for (int row = 0; row < PIECE_BASE_SIZE; row++) {
            int rightOffset = edgeProfile[row];
            for (int col = 0; col < PIECE_BASE_SIZE + rightOffset; col++) {
                int px = targetX + col;
                int py = targetY + row;
                if (px < bgImg.getWidth() && py < bgImg.getHeight()) {
                    int orig = scaled.getRGB(px, py);
                    bgImg.setRGB(px, py, blendWithOverlay(orig));
                }
            }
        }

        // Draw dashed border around cutout
        drawCutoutBorder(bgG, targetX, targetY, PIECE_BASE_SIZE, edgeProfile);

        // Draw a hint arrow on the background
        drawHintArrow(bgG, targetX, targetY, PIECE_BASE_SIZE + maxOffset, actualWidth);

        bgG.dispose();

        // ---- Encode to Base64 ----
        String bgB64 = encodeBase64(bgImg);
        String pieceB64 = encodeBase64(pieceImg);

        return new CaptchaResult(bgB64, pieceB64, targetX, targetY,
                PIECE_BASE_SIZE, maxOffset, DISPLAY_WIDTH, DISPLAY_HEIGHT, shapeType);
    }

    public static boolean verify(int userX, int targetX, int tolerance) {
        return Math.abs(userX - targetX) <= tolerance;
    }

    public static int getTolerance() {
        return TOLERANCE;
    }

    // ================== Shape Generators ==================

    private static int[] generateEdgeProfile(int size, int shape, int amplitude, SplittableRandom random) {
        int[] profile = new int[size];
        double phase = random.nextDouble() * 2 * Math.PI;

        for (int y = 0; y < size; y++) {
            double t = (double) y / size;
            double shapeVal = 0;

            switch (shape) {
                case 0: // Triangle — central peak
                    shapeVal = amplitude * (1 - Math.abs(2 * t - 1));
                    break;
                case 1: // Star — multiple sharp peaks
                    int peaks = 2 + random.nextInt(3); // 2-4 peaks
                    shapeVal = amplitude * Math.pow(Math.abs(Math.sin(t * peaks * Math.PI)), 0.6);
                    break;
                case 2: // Puzzle — rounded knob
                    double center = 0.5;
                    double radius = 0.35;
                    double dist = Math.abs(t - center) / radius;
                    if (dist < 1) {
                        shapeVal = amplitude * Math.sqrt(1 - dist * dist) * 1.3;
                    }
                    break;
            }

            // Add random perturbation
            double perturb = 2.0 * Math.sin(y * 0.5 + phase)
                    + 1.5 * Math.sin(y * 0.3 + phase * 1.7)
                    + 1.0 * Math.sin(y * 0.7 + phase * 0.5);
            shapeVal = Math.max(0, shapeVal + perturb);
            profile[y] = (int) Math.round(shapeVal);
        }

        // Ensure edges are at 0
        profile[0] = 0;
        profile[size - 1] = 0;

        // Smooth transition at edges
        for (int i = 1; i < 4 && i < size; i++) {
            if (profile[i] > profile[i - 1] + 2)
                profile[i] = profile[i - 1] + 2;
            if (profile[size - 1 - i] > profile[size - i] + 2)
                profile[size - 1 - i] = profile[size - i] + 2;
        }

        return profile;
    }

    // ================== Image Helpers ==================

    private static BufferedImage scaleImage(BufferedImage src, int targetW, int targetH) {
        Image tmp = src.getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
        BufferedImage result = new BufferedImage(targetW, targetH, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = result.createGraphics();
        g.drawImage(tmp, 0, 0, null);
        g.dispose();
        return result;
    }

    private static int blendWithOverlay(int rgb) {
        int a = 140;
        int r = ((rgb >> 16) & 0xFF) * (255 - a) / 255 + 255 * a / 255;
        int g = ((rgb >> 8) & 0xFF) * (255 - a) / 255 + 255 * a / 255;
        int b = (rgb & 0xFF) * (255 - a) / 255 + 255 * a / 255;
        return (0xFF << 24) | (r << 16) | (g << 8) | b;
    }

    private static BufferedImage addDropShadow(BufferedImage src, int maxOffset) {
        int shadowSize = 4;
        int w = src.getWidth() + shadowSize;
        int h = src.getHeight() + shadowSize;
        BufferedImage result = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = result.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw shadow
        for (int dy = 1; dy <= shadowSize; dy++) {
            float alpha = 0.15f * (1 - (float) dy / shadowSize);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            int offsetX = dy;
            int offsetY = dy / 2;
            g.drawImage(src, offsetX, offsetY, null);
        }

        // Draw original piece
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        g.drawImage(src, 0, 0, null);
        g.dispose();

        return result;
    }

    private static void drawCutoutBorder(Graphics2D g, int x, int y, int size, int[] edgeProfile) {
        g.setColor(new Color(0xFFFFFF, true));
        g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                0, new float[]{4, 3}, 0));
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));

        // Top edge
        g.drawLine(x, y, x + size, y);
        // Left edge
        g.drawLine(x, y, x, y + size);
        // Bottom edge
        g.drawLine(x, y + size, x + size, y + size);
        // Right edge with shape
        for (int row = 0; row < size; row++) {
            int rOff = edgeProfile[row];
            if (row > 0) {
                int prevOff = edgeProfile[row - 1];
                g.drawLine(x + size + prevOff, y + row - 1, x + size + rOff, y + row);
            }
        }
    }

    private static void drawHintArrow(Graphics2D g, int targetX, int targetY, int pieceTotalWidth, int imgWidth) {
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(2f));

        int arrowY = targetY + PIECE_BASE_SIZE / 2 - 1;
        int arrowX = targetX + pieceTotalWidth + 8;

        // Draw small arrow pointing right to indicate direction
        g.drawLine(arrowX, arrowY, arrowX + 20, arrowY);
        g.drawLine(arrowX + 16, arrowY - 4, arrowX + 20, arrowY);
        g.drawLine(arrowX + 16, arrowY + 4, arrowX + 20, arrowY);
    }

    private static String encodeBase64(BufferedImage img) {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(img, "png", os);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(os.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Failed to encode image", e);
        }
    }
}
