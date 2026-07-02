package com.example.changshademo.captcha;

import java.awt.image.BufferedImage;

public class CaptchaData {
    private final String token;
    private final int targetX;
    private final int targetY;
    private final int pieceBaseSize;
    private final int maxAmplitude;
    private final int imageWidth;
    private final int imageHeight;
    private final String shapeType;
    private int attempts;
    private final long createTime;

    public CaptchaData(String token, int targetX, int targetY, int pieceBaseSize,
                       int maxAmplitude, int imageWidth, int imageHeight, String shapeType) {
        this.token = token;
        this.targetX = targetX;
        this.targetY = targetY;
        this.pieceBaseSize = pieceBaseSize;
        this.maxAmplitude = maxAmplitude;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.shapeType = shapeType;
        this.attempts = 0;
        this.createTime = System.currentTimeMillis();
    }

    public void incrementAttempts() { this.attempts++; }

    public String getToken() { return token; }
    public int getTargetX() { return targetX; }
    public int getTargetY() { return targetY; }
    public int getPieceBaseSize() { return pieceBaseSize; }
    public int getMaxAmplitude() { return maxAmplitude; }
    public int getImageWidth() { return imageWidth; }
    public int getImageHeight() { return imageHeight; }
    public String getShapeType() { return shapeType; }
    public int getAttempts() { return attempts; }
    public long getCreateTime() { return createTime; }
}
