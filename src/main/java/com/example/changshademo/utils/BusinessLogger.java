package com.example.changshademo.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BusinessLogger {
    public static void logQuery(Class<?> clazz, String module, String detail) {
        Logger logger = LoggerFactory.getLogger(clazz);
        logger.info("用户[admin] 查询了{}: {}", module, detail);
    }

    public static void logQuery(Class<?> clazz, String module) {
        Logger logger = LoggerFactory.getLogger(clazz);
        logger.info("用户[admin] 查询了所有{}", module);
    }

    public static void logOperation(Class<?> clazz, String module, String operation, String detail) {
        Logger logger = LoggerFactory.getLogger(clazz);
        logger.info("用户[admin] {}了{}: {}", operation, module, detail);
    }
}
