package com.example.changshademo.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderTrack {
    /**
     * ID 标识
     */
    private Integer id;

    /**
     * 有效标识
     * 0：有效
     * 1：无效
     */
    private Integer flag;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 创建者 ID
     */
    private Integer createUserid;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;

    /**
     * 修改人 ID
     */
    private Integer updateUserid;

    /**
     * 调度 ID
     */
    private Integer scheduleId;

    /**
     * 计划 ID
     */
    private Integer planId;

    /**
     * 产品 ID
     */
    private Integer productId;

    /**
     * 加工数量
     */
    private Integer workingCount;

    /**
     * 合格数
     */
    private Integer qualifiedCount;

    /**
     * 工厂 ID
     */
    private Integer factoryId;
}
