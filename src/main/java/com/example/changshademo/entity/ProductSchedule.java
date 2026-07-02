package com.example.changshademo.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 生产调度实体类，对应数据库表 t_product_schedule
 */
@Data
public class ProductSchedule {
    /**
     * ID标识
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
     * 工单编号
     */
    @NotBlank(message = "工单编号不能为空")
    private String scheduleSeq;

    /**
     * 工单数量
     */
    @Positive(message = "工单数量必须大于0")
    private Integer scheduleCount;

    /**
     * 工单状态
     * 10：未开始  20：生产中  30：已完成
     */
    private Integer scheduleStatus;

    /**
     * 计划ID
     */
    @NotNull(message = "计划ID不能为空")
    private Integer planId;

    /**
     * 产品ID
     */
    @NotNull(message = "产品ID不能为空")
    private Integer productId;

    /**
     * 设备ID
     */
    private Integer equipmentId;

    /**
     * 开始日期
     */
    @NotNull(message = "开始日期不能为空")
    private LocalDate startDate;

    /**
     * 结束日期
     */
    @NotNull(message = "结束日期不能为空")
    private LocalDate endDate;

    /**
     * 工厂ID
     */
    private Integer factoryId;
}
