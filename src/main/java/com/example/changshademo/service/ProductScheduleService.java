package com.example.changshademo.service;

import com.example.changshademo.entity.ProductSchedule;

import java.time.LocalDate;
import java.util.List;

/**
 * 生产调度业务逻辑接口
 */
public interface ProductScheduleService {
    /**
     * 查询所有调度
     */
    List<ProductSchedule> findAll();

    /**
     * 根据ID查询调度
     */
    ProductSchedule findById(Integer id);

    /**
     * 根据工单编号查询调度
     */
    ProductSchedule findByScheduleSeq(String scheduleSeq);

    /**
     * 根据计划ID查询调度列表
     */
    List<ProductSchedule> findByPlanId(Integer planId);

    /**
     * 根据产品ID查询调度列表
     */
    List<ProductSchedule> findByProductId(Integer productId);

    /**
     * 根据设备ID查询调度列表
     */
    List<ProductSchedule> findByEquipmentId(Integer equipmentId);

    /**
     * 根据工厂ID查询调度列表
     */
    List<ProductSchedule> findByFactoryId(Integer factoryId);

    /**
     * 根据调度状态查询调度列表
     */
    List<ProductSchedule> findByScheduleStatus(Integer scheduleStatus);

    /**
     * 查询指定日期范围内的调度
     */
    List<ProductSchedule> findByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * 新增调度
     */
    int insert(ProductSchedule productSchedule);

    /**
     * 更新调度
     */
    int update(ProductSchedule productSchedule);

    /**
     * 删除调度（逻辑删除）
     */
    int deleteById(Integer id, Integer updateUserid);
}
