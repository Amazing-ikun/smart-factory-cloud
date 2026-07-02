package com.example.changshademo.captcha;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;
import java.util.UUID;

@Service
public class CaptchaService {

    private static final Logger log = LoggerFactory.getLogger(CaptchaService.class);

    private final CaptchaStore captchaStore;
    private final PathMatchingResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();

    private final SplittableRandom random = new SplittableRandom();

    private List<BufferedImage> imageCache = null;

    public CaptchaService(CaptchaStore captchaStore) {
        this.captchaStore = captchaStore;
    }

    private List<BufferedImage> loadImages() {
        if (imageCache != null) return imageCache;
        imageCache = new ArrayList<>();
        try {
            Resource[] resources = resourceResolver.getResources("classpath:captcha-images/*");
            log.info("Found {} captcha resource(s)", resources.length);
            for (Resource r : resources) {
                if (r.getFilename() == null) continue;
                String name = r.getFilename().toLowerCase();
                log.debug("Checking captcha resource: {}", name);
                if (name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png")) {
                    BufferedImage img = ImageIO.read(r.getInputStream());
                    if (img != null) {
                        imageCache.add(img);
                        log.info("Loaded captcha image: {} ({}x{})", name, img.getWidth(), img.getHeight());
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to load captcha images from classpath:captcha-images/", e);
            // Fall through to fallback
        }
        if (imageCache.isEmpty()) {
            // Fallback: generate a gradient image so captcha still works
            BufferedImage fallback = new BufferedImage(
                    CaptchaUtils.DISPLAY_WIDTH, CaptchaUtils.DISPLAY_HEIGHT,
                    BufferedImage.TYPE_INT_RGB);
            java.awt.Graphics2D g = fallback.createGraphics();
            java.awt.GradientPaint gp = new java.awt.GradientPaint(
                    0, 0, java.awt.Color.decode("#667eea"),
                    CaptchaUtils.DISPLAY_WIDTH, CaptchaUtils.DISPLAY_HEIGHT,
                    java.awt.Color.decode("#764ba2"));
            g.setPaint(gp);
            g.fillRect(0, 0, CaptchaUtils.DISPLAY_WIDTH, CaptchaUtils.DISPLAY_HEIGHT);
            g.dispose();
            imageCache.add(fallback);
        }
        return imageCache;
    }

    public static class GenerateResult {
        public final String token;
        public final CaptchaUtils.CaptchaResult captcha;

        public GenerateResult(String token, CaptchaUtils.CaptchaResult captcha) {
            this.token = token;
            this.captcha = captcha;
        }
    }

    public GenerateResult generate() {
        List<BufferedImage> images = loadImages();
        BufferedImage img = images.get(random.nextInt(images.size()));
        CaptchaUtils.CaptchaResult result = CaptchaUtils.generate(img);

        String token = UUID.randomUUID().toString();
        CaptchaData data = new CaptchaData(
                token, result.targetX, result.targetY,
                result.pieceBaseSize, result.maxAmplitude,
                result.imageWidth, result.imageHeight, result.shapeType
        );
        captchaStore.put(token, data);

        return new GenerateResult(token, result);
    }

    public static class VerifyResult {
        public final boolean success;
        public final int remainingAttempts;

        public VerifyResult(boolean success, int remainingAttempts) {
            this.success = success;
            this.remainingAttempts = remainingAttempts;
        }
    }

    public VerifyResult verify(String token, int userX) {
        CaptchaData data = captchaStore.getAndRemove(token);
        if (data == null) {
            return new VerifyResult(false, 0);
        }

        boolean ok = CaptchaUtils.verify(userX, data.getTargetX(), CaptchaUtils.getTolerance());
        if (ok) {
            return new VerifyResult(true, 3);
        }

        data.incrementAttempts();
        int remaining = 3 - data.getAttempts();

        if (remaining > 0) {
            // Put back for retry
            captchaStore.put(token, data);
        }

        return new VerifyResult(false, Math.max(0, remaining));
    }
}
