package com.example.changshademo.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.AbstractRequestLoggingFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.BufferedReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final Pattern SENSITIVE_FIELD = Pattern.compile(
            "(?i)(\"password\"\\s*:\\s*)\"[^\"]*\""
    );

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadPath = Paths.get(System.getProperty("user.dir"), "uploads").toAbsolutePath().normalize();
        registry.addResourceHandler("/upload/**")
                .addResourceLocations(uploadPath.toUri().toString())
                .setCachePeriod(3600);
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public AbstractRequestLoggingFilter requestLoggingFilter() {
        return new AbstractRequestLoggingFilter() {

            @Override
            protected boolean shouldLog(HttpServletRequest request) {
                return logger.isInfoEnabled();
            }

            @Override
            protected void beforeRequest(HttpServletRequest request, String message) {
            }

            @Override
            protected void afterRequest(HttpServletRequest request, String message) {
                logger.info("HTTP请求: " + request.getMethod() + " " + request.getRequestURI()
                        + " | 参数: " + maskSensitive(sanitizeQueryString(request.getQueryString()))
                        + " | 来源: " + request.getRemoteAddr()
                        + (message.isEmpty() ? "" : " | 请求体: " + maskSensitive(message)));
            }

            @Override
            protected String getMessagePayload(HttpServletRequest request) {
                try {
                    BufferedReader reader = request.getReader();
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    return sb.toString();
                } catch (Exception e) {
                    return "";
                }
            }
        };
    }

    private static String maskSensitive(String input) {
        if (input == null) return null;
        Matcher m = SENSITIVE_FIELD.matcher(input);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, m.group(1) + "\"******\"");
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private static String sanitizeQueryString(String qs) {
        if (qs == null) return null;
        return qs.replaceAll("(?i)(password|pwd|passwd)=[^&]*", "$1=******");
    }
}
