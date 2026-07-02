package com.example.changshademo.captcha;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class CaptchaStore {
    private final ConcurrentHashMap<String, CaptchaData> store = new ConcurrentHashMap<>();

    public void put(String token, CaptchaData data) {
        store.put(token, data);
    }

    public CaptchaData getAndRemove(String token) {
        return store.remove(token);
    }

    @Scheduled(fixedRate = 300000)
    public void cleanExpired() {
        long now = System.currentTimeMillis();
        store.values().removeIf(d -> (now - d.getCreateTime()) > 300_000);
    }
}
