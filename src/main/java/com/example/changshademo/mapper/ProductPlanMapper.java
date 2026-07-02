package com.example.changshademo.mapper;

import com.example.changshademo.entity.ProductPlan;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface ProductPlanMapper {
    @Select("SELECT * FROM t_product_plan WHERE flag = 0")
    List<ProductPlan> findAll();

    @Select("SELECT * FROM t_product_plan WHERE id = #{id} AND flag = 0")
    ProductPlan findById(@Param("id") Integer id);

    @Select("SELECT * FROM t_product_plan WHERE plan_seq = #{planSeq} AND flag = 0")
    ProductPlan findByPlanSeq(@Param("planSeq") String planSeq);

    @Select("SELECT * FROM t_product_plan WHERE order_id = #{orderId} AND flag = 0")
    List<ProductPlan> findByOrderId(@Param("orderId") Integer orderId);

    @Select("SELECT * FROM t_product_plan WHERE product_id = #{productId} AND flag = 0")
    List<ProductPlan> findByProductId(@Param("productId") Integer productId);

    @Select("SELECT * FROM t_product_plan WHERE factory_id = #{factoryId} AND flag = 0")
    List<ProductPlan> findByFactoryId(@Param("factoryId") Integer factoryId);

    @Select("SELECT * FROM t_product_plan WHERE plan_status = #{status} AND flag = 0")
    List<ProductPlan> findByStatus(@Param("status") Integer status);

    @Select("SELECT * FROM t_product_plan WHERE plan_start_date <= #{end} AND plan_end_date >= #{start} AND flag = 0")
    List<ProductPlan> findByDateRange(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Insert("INSERT INTO t_product_plan (flag, create_time, create_userid, plan_seq, order_id, product_id, plan_count, delivery_date, plan_start_date, plan_end_date, plan_status, factory_id) " +
            "VALUES (0, NOW(), #{createUserid}, #{planSeq}, #{orderId}, #{productId}, #{planCount}, #{deliveryDate}, #{planStartDate}, #{planEndDate}, #{planStatus}, #{factoryId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ProductPlan plan);

    @Update("UPDATE t_product_plan SET plan_seq = #{planSeq}, order_id = #{orderId}, product_id = #{productId}, plan_count = #{planCount}, " +
            "delivery_date = #{deliveryDate}, plan_start_date = #{planStartDate}, plan_end_date = #{planEndDate}, " +
            "plan_status = #{planStatus}, factory_id = #{factoryId}, update_time = NOW(), update_userid = #{updateUserid} WHERE id = #{id}")
    int update(ProductPlan plan);

    @Update("UPDATE t_product_plan SET flag = 1, update_time = NOW(), update_userid = #{updateUserid} WHERE id = #{id}")
    int deleteById(@Param("id") Integer id, @Param("updateUserid") Integer updateUserid);
}
