package com.example.changshademo.service;

import com.example.changshademo.common.CurrentUserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Service 层基类，提供统一的日志记录方法
 * 所有 Service 实现类继承此类即可使用日志功能
 */
public abstract class BaseService {
    protected final Logger logger;

    @Autowired(required = false)
    private BusinessLogService businessLogService;

    public BaseService() {
        this.logger = LoggerFactory.getLogger(this.getClass());
    }

    /**
     * 记录查询日志（无详情）
     */
    protected void logQuery(String module) {
        logger.info("用户[{}] 查询了所有{}", getCurrentUser(), module);
        saveLog("查询", module, "查询了所有" + module);
    }

    /**
     * 记录查询日志（带详情）
     */
    protected void logQuery(String module, String detail) {
        logger.info("用户[{}] 查询了{}: {}", getCurrentUser(), module, detail);
        saveLog("查询", module, detail);
    }

    /**
     * 记录操作日志（新增/修改/删除）
     */
    protected void logOperation(String operation, String module, String detail) {
        logger.info("用户[{}] {}了{}: {}", getCurrentUser(), operation, module, detail);
        saveLog(operation, module, detail);
    }

    /**
     * 记录带结果的日志
     */
    protected void logWithResult(String module, String detail, String result) {
        logger.info("用户[{}] 查询了{}: {}，结果: {}", getCurrentUser(), module, detail, result);
        saveLog("查询", module, detail + "，结果: " + result);
    }

    private void saveLog(String operation, String module, String detail) {
        if (businessLogService != null) {
            try {
                businessLogService.addLog(
                    CurrentUserUtils.getCurrentUserId(),
                    CurrentUserUtils.getCurrentUserName(),
                    operation, module, detail.length() > 480 ? detail.substring(0, 480) : detail
                );
            } catch (Exception e) {
                logger.warn("保存业务日志失败: {}", e.getMessage());
            }
        }
    }

    /**
     * 获取当前用户（后续可集成 Spring Security 或 Session）
     */
    private String getCurrentUser() {
        return CurrentUserUtils.getCurrentUserName();
    }
}
