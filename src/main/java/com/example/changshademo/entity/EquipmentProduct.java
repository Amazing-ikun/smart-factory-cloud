package com.example.changshademo.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EquipmentProduct {
    /**
     * ID 标识
     */
    private Integer id;

    /**
     * 有效标识
     * 0：有效  1：无效
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
     * 设备 ID
     */
    private Integer equipmentId;

    /**
     * 产品 ID
     */
    private Integer productId;

    /**
     * 产能
     */
    private Integer yield;

    /**
     * 产能单位
     * 10:天  20:月  30:年  40:小时
     */
    private Integer unit;

    /**
     * 工厂 ID
     */
    private Integer factoryId;
}
