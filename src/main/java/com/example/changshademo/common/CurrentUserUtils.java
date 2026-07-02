package com.example.changshademo.common;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class CurrentUserUtils {

    private static final Logger log = LoggerFactory.getLogger(CurrentUserUtils.class);
    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USER_NAME_HEADER = "X-User-Name";

    private CurrentUserUtils() {}

    public static Integer getCurrentUserId() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            log.warn("无法获取当前请求上下文，默认使用 admin(1)");
            return 1;
        }
        HttpServletRequest request = attrs.getRequest();
        String userIdStr = request.getHeader(USER_ID_HEADER);
        if (userIdStr != null && !userIdStr.isEmpty()) {
            try {
                return Integer.parseInt(userIdStr.trim());
            } catch (NumberFormatException e) {
                log.warn("X-User-Id 格式无效: {}", userIdStr);
            }
        }
        return 1;
    }

    public static String getCurrentUserName() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            return "admin";
        }
        HttpServletRequest request = attrs.getRequest();
        String name = request.getHeader(USER_NAME_HEADER);
        return (name != null && !name.isEmpty()) ? name.trim() : "admin";
    }
}
