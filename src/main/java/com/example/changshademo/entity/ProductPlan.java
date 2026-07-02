package com.example.changshademo.entity;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ProductPlan {
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
     * 创建人 ID
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
     * 计划编号
     */
    private String planSeq;

    /**
     * 订单 ID
     */
    private Integer orderId;

    /**
     * 产品 ID
     */
    private Integer productId;

    /**
     * 计划数量
     */
    private Integer planCount;

    /**
     * 交货日期
     */
    private LocalDate deliveryDate;

    /**
     * 计划开始日期
     */
    private LocalDate planStartDate;

    /**
     * 计划结束日期
     */
    private LocalDate planEndDate;

    /**
     * 计划状态
     * 10:未启动
     * 20:已启动
     * 30:已完成
     */
    private Integer planStatus;

    /**
     * 工厂 ID
     */
    private Integer factoryId;
}
