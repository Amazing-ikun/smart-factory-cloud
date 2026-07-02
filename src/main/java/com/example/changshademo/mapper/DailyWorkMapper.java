package com.example.changshademo.mapper;

import com.example.changshademo.entity.DailyWork;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface DailyWorkMapper {
    @Select("SELECT * FROM t_daily_work WHERE flag = 0")
    List<DailyWork> findAll();

    @Select("SELECT * FROM t_daily_work WHERE id = #{id} AND flag = 0")
    DailyWork findById(@Param("id") Integer id);

    @Select("SELECT * FROM t_daily_work WHERE schedule_id = #{scheduleId} AND flag = 0")
    List<DailyWork> findByScheduleId(@Param("scheduleId") Integer scheduleId);

    @Select("SELECT * FROM t_daily_work WHERE equipment_id = #{equipmentId} AND flag = 0")
    List<DailyWork> findByEquipmentId(@Param("equipmentId") Integer equipmentId);

    @Select("SELECT * FROM t_daily_work WHERE factory_id = #{factoryId} AND flag = 0")
    List<DailyWork> findByFactoryId(@Param("factoryId") Integer factoryId);

    @Select("SELECT * FROM t_daily_work WHERE complete_flag = #{completeFlag} AND flag = 0")
    List<DailyWork> findByCompleteFlag(@Param("completeFlag") Integer completeFlag);

    @Select("SELECT * FROM t_daily_work WHERE start_time BETWEEN #{start} AND #{end} AND flag = 0")
    List<DailyWork> findByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Select("SELECT * FROM t_daily_work WHERE create_time BETWEEN #{start} AND #{end} AND flag = 0")
    List<DailyWork> findByCreateTimeRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Select("SELECT * FROM t_daily_work WHERE flag = 0 ORDER BY create_time DESC LIMIT #{offset}, #{size}")
    List<DailyWork> findPage(@Param("offset") int offset, @Param("size") int size);

    @Select("SELECT COUNT(*) FROM t_daily_work WHERE flag = 0")
    long count();
}
