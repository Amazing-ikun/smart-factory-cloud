package com.example.changshademo.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DailyWork {
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
     * 调度 ID
     */
    private Integer scheduleId;

    /**
     * 设备 ID
     */
    private Integer equipmentId;

    /**
     * 设备序号
     */
    private String equipmentSeq;

    /**
     * 加工开始时间
     */
    private LocalDateTime startTime;

    /**
     * 加工结束时间
     */
    private LocalDateTime endTime;

    /**
     * 加工数量
     */
    private Integer workingCount;

    /**
     * 合格数量
     */
    private Integer qualifiedCount;

    /**
     * 不合格数量
     */
    private Integer unqualifiedCount;

    /**
     * 结束报工标识
     * 0:是
     * 1:否
     */
    private Integer completeFlag;

    /**
     * 工厂 ID
     */
    private Integer factoryId;

    /**
     * 备注
     */
    private String bak;
}
