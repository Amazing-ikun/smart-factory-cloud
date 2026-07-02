package com.example.changshademo.mapper;

import com.example.changshademo.entity.ProductSchedule;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 生产调度数据访问层
 */
@Mapper
public interface ProductScheduleMapper {
    /**
     * 查询所有有效调度
     */
    @Select("SELECT * FROM t_product_schedule WHERE flag = 0")
    List<ProductSchedule> findAll();

    /**
     * 根据ID查询调度
     */
    @Select("SELECT * FROM t_product_schedule WHERE id = #{id} AND flag = 0")
    ProductSchedule findById(@Param("id") Integer id);

    /**
     * 根据工单编号查询调度
     */
    @Select("SELECT * FROM t_product_schedule WHERE schedule_seq = #{scheduleSeq} AND flag = 0")
    ProductSchedule findByScheduleSeq(@Param("scheduleSeq") String scheduleSeq);

    /**
     * 根据计划ID查询调度列表
     */
    @Select("SELECT * FROM t_product_schedule WHERE plan_id = #{planId} AND flag = 0")
    List<ProductSchedule> findByPlanId(@Param("planId") Integer planId);

    /**
     * 根据产品ID查询调度列表
     */
    @Select("SELECT * FROM t_product_schedule WHERE product_id = #{productId} AND flag = 0")
    List<ProductSchedule> findByProductId(@Param("productId") Integer productId);

    /**
     * 根据设备ID查询调度列表
     */
    @Select("SELECT * FROM t_product_schedule WHERE equipment_id = #{equipmentId} AND flag = 0")
    List<ProductSchedule> findByEquipmentId(@Param("equipmentId") Integer equipmentId);

    /**
     * 根据工厂ID查询调度列表
     */
    @Select("SELECT * FROM t_product_schedule WHERE factory_id = #{factoryId} AND flag = 0")
    List<ProductSchedule> findByFactoryId(@Param("factoryId") Integer factoryId);

    /**
     * 根据调度状态查询调度列表
     */
    @Select("SELECT * FROM t_product_schedule WHERE schedule_status = #{scheduleStatus} AND flag = 0")
    List<ProductSchedule> findByScheduleStatus(@Param("scheduleStatus") Integer scheduleStatus);

    /**
     * 查询指定日期范围内的调度
     */
    @Select("SELECT * FROM t_product_schedule WHERE start_date >= #{startDate} AND end_date <= #{endDate} AND flag = 0")
    List<ProductSchedule> findByDateRange(@Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate);

    /**
     * 插入新调度
     */
    @Insert("INSERT INTO t_product_schedule (flag, create_time, create_userid, schedule_seq, " +
            "schedule_count, schedule_status, plan_id, product_id, equipment_id, " +
            "start_date, end_date, factory_id) " +
            "VALUES (0, NOW(), #{createUserid}, #{scheduleSeq}, " +
            "#{scheduleCount}, #{scheduleStatus}, #{planId}, #{productId}, #{equipmentId}, " +
            "#{startDate}, #{endDate}, #{factoryId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ProductSchedule productSchedule);

    /**
     * 更新调度信息
     */
    @Update("UPDATE t_product_schedule SET " +
            "schedule_count = #{scheduleCount}, " +
            "schedule_status = #{scheduleStatus}, " +
            "plan_id = #{planId}, " +
            "product_id = #{productId}, " +
            "equipment_id = #{equipmentId}, " +
            "start_date = #{startDate}, " +
            "end_date = #{endDate}, " +
            "update_time = NOW(), " +
            "update_userid = #{updateUserid} " +
            "WHERE id = #{id}")
    int update(ProductSchedule productSchedule);

    /**
     * 逻辑删除调度（将flag设为1）
     */
    @Update("UPDATE t_product_schedule SET flag = 1, update_time = NOW(), update_userid = #{updateUserid} " +
            "WHERE id = #{id}")
    int deleteById(@Param("id") Integer id, @Param("updateUserid") Integer updateUserid);
}
