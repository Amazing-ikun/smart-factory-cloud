package com.example.changshademo.mapper;

import com.example.changshademo.entity.ProductOrder;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 订单数据访问层
 */
@Mapper
public interface ProductOrderMapper {
    /**
     * 查询所有有效订单
     */
    @Select("SELECT * FROM t_product_order WHERE flag = 0")
    List<ProductOrder> findAll();

    /**
     * 根据ID查询订单
     */
    @Select("SELECT * FROM t_product_order WHERE id = #{id} AND flag = 0")
    ProductOrder findById(@Param("id") Integer id);

    /**
     * 根据订单编号查询订单
     */
    @Select("SELECT * FROM t_product_order WHERE order_seq = #{orderSeq} AND flag = 0")
    ProductOrder findByOrderSeq(@Param("orderSeq") String orderSeq);

    /**
     * 根据工厂ID查询订单列表
     */
    @Select("SELECT * FROM t_product_order WHERE factory_id = #{factoryId} AND flag = 0")
    List<ProductOrder> findByFactoryId(@Param("factoryId") Integer factoryId);

    /**
     * 根据订单状态查询订单列表
     */
    @Select("SELECT * FROM t_product_order WHERE order_status = #{orderStatus} AND flag = 0")
    List<ProductOrder> findByOrderStatus(@Param("orderStatus") Integer orderStatus);

    /**
     * 根据产品ID查询订单列表
     */
    @Select("SELECT * FROM t_product_order WHERE product_id = #{productId} AND flag = 0")
    List<ProductOrder> findByProductId(@Param("productId") Integer productId);

    /**
     * 查询指定日期范围内的订单
     */
    @Select("SELECT * FROM t_product_order WHERE end_date BETWEEN #{startDate} AND #{endDate} AND flag = 0")
    List<ProductOrder> findByEndDateRange(@Param("startDate") String startDate,
                                          @Param("endDate") String endDate);

    /**
     * 插入新订单
     */
    @Insert("INSERT INTO t_product_order (flag, create_time, create_userid, order_seq, order_source, " +
            "product_id, product_count, end_date, order_status, factory_id, factory_yield) " +
            "VALUES (0, NOW(), #{createUserid}, #{orderSeq}, #{orderSource}, " +
            "#{productId}, #{productCount}, #{endDate}, #{orderStatus}, #{factoryId}, #{factoryYield})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ProductOrder productOrder);

    /**
     * 更新订单信息
     */
    @Update("UPDATE t_product_order SET " +
            "order_source = #{orderSource}, " +
            "product_id = #{productId}, " +
            "product_count = #{productCount}, " +
            "end_date = #{endDate}, " +
            "order_status = #{orderStatus}, " +
            "factory_yield = #{factoryYield}, " +
            "update_time = NOW(), " +
            "update_userid = #{updateUserid} " +
            "WHERE id = #{id}")
    int update(ProductOrder productOrder);

    /**
     * 更新订单状态
     */
    @Update("UPDATE t_product_order SET " +
            "order_status = #{orderStatus}, " +
            "update_time = NOW(), " +
            "update_userid = #{updateUserid} " +
            "WHERE id = #{id}")
    int updateStatus(@Param("id") Integer id,
                     @Param("orderStatus") Integer orderStatus,
                     @Param("updateUserid") Integer updateUserid);

    @Select("SELECT * FROM t_product_order WHERE flag = 0 ORDER BY create_time DESC LIMIT #{offset}, #{size}")
    List<ProductOrder> findPage(@Param("offset") int offset, @Param("size") int size);

    @Select("SELECT COUNT(*) FROM t_product_order WHERE flag = 0")
    long count();

    /**
     * 逻辑删除订单（将 flag 设为 1）
     */
    @Update("UPDATE t_product_order SET flag = 1, update_time = NOW(), update_userid = #{updateUserid} " +
            "WHERE id = #{id}")
    int deleteById(@Param("id") Integer id, @Param("updateUserid") Integer updateUserid);
}
