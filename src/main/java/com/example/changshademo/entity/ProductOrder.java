package com.example.changshademo.entity;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 订单实体类，对应数据库表 t_product_order
 */
@Data
public class ProductOrder {
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
     * 创建人ID
     */
    private Integer createUserid;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;

    /**
     * 修改人ID
     */
    private Integer updateUserid;

    /**
     * 订单编号
     */
    private String orderSeq;

    /**
     * 订单来源
     */
    private Integer orderSource;

    /**
     * 产品ID
     */
    private Integer productId;

    /**
     * 产品数量
     */
    private Integer productCount;

    /**
     * 订单截止日期
     */
    private LocalDate endDate;

    /**
     * 订单状态
     * 10：未接单
     * 20：已接单
     * 30：已拒绝
     * 40：生产中
     * 50：订单完成
     */
    private Integer orderStatus;

    /**
     * 工厂ID
     */
    private Integer factoryId;

    /**
     * 工厂产能
     */
    private Integer factoryYield;
}
