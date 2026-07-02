package com.example.changshademo.mapper;

import com.example.changshademo.entity.OrderTrack;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface OrderTrackMapper {
    @Select("SELECT * FROM t_order_track WHERE flag = 0")
    List<OrderTrack> findAll();

    @Select("SELECT * FROM t_order_track WHERE id = #{id} AND flag = 0")
    OrderTrack findById(@Param("id") Integer id);

    @Select("SELECT * FROM t_order_track WHERE schedule_id = #{scheduleId} AND flag = 0")
    List<OrderTrack> findByScheduleId(@Param("scheduleId") Integer scheduleId);

    @Select("SELECT * FROM t_order_track WHERE plan_id = #{planId} AND flag = 0")
    List<OrderTrack> findByPlanId(@Param("planId") Integer planId);

    @Select("SELECT * FROM t_order_track WHERE product_id = #{productId} AND flag = 0")
    List<OrderTrack> findByProductId(@Param("productId") Integer productId);

    @Select("SELECT * FROM t_order_track WHERE factory_id = #{factoryId} AND flag = 0")
    List<OrderTrack> findByFactoryId(@Param("factoryId") Integer factoryId);

    @SelectProvider(type = OrderTrackSqlProvider.class, method = "findPage")
    List<OrderTrack> findPage(Map<String, Object> params);

    @SelectProvider(type = OrderTrackSqlProvider.class, method = "count")
    long count(Map<String, Object> params);
}
