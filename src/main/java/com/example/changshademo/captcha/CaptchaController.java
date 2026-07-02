package com.example.changshademo.captcha;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/captcha")
@RequiredArgsConstructor
public class CaptchaController {

    private static final Logger log = LoggerFactory.getLogger(CaptchaController.class);
    private final CaptchaService captchaService;

    @GetMapping("/generate")
    public Map<String, Object> generate() {
        try {
            CaptchaService.GenerateResult gen = captchaService.generate();
            CaptchaUtils.CaptchaResult r = gen.captcha;
            return Map.of(
                    "success", true,
                    "data", Map.of(
                            "token", gen.token,
                            "bgImage", r.bgImageBase64,
                            "pieceImage", r.pieceImageBase64,
                            "targetY", r.targetY,
                            "pieceBaseSize", r.pieceBaseSize,
                            "maxAmplitude", r.maxAmplitude,
                            "imageWidth", r.imageWidth,
                            "imageHeight", r.imageHeight,
                            "shapeType", r.shapeType
                    )
            );
        } catch (Exception e) {
            log.error("Captcha generation failed", e);
            return Map.of("success", false, "message", "验证码生成失败");
        }
    }

    @PostMapping("/verify")
    public Map<String, Object> verify(@RequestBody Map<String, Object> params) {
        String token = (String) params.get("token");
        int userX = ((Number) params.get("x")).intValue();

        CaptchaService.VerifyResult vr = captchaService.verify(token, userX);
        return Map.of(
                "success", vr.success,
                "message", vr.success ? "验证成功" : "验证失败",
                "data", Map.of("remainingAttempts", vr.remainingAttempts)
        );
    }
}
