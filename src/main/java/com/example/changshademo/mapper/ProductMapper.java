package com.example.changshademo.mapper;

import com.example.changshademo.entity.Product;
import org.apache.ibatis.annotations.*;

import java.util.*;

/**
 * 产品数据访问层
 */
@Mapper
public interface ProductMapper {
    /**
     * 查询所有有效产品
     */
    @Select("SELECT * FROM t_product WHERE flag = 0")
    List<Product> findAll();

    /**
     * 根据ID查询产品
     */
    @Select("SELECT * FROM t_product WHERE id = #{id} AND flag = 0")
    Product findById(@Param("id") Integer id);

    /**
     * 根据工厂ID查询产品列表
     */
    @Select("SELECT * FROM t_product WHERE factory_id = #{factoryId} AND flag = 0")
    List<Product> findByFactoryId(@Param("factoryId") Integer factoryId);

    /**
     * 根据产品名称模糊查询
     */
    @Select("SELECT * FROM t_product WHERE product_name LIKE CONCAT('%', #{productName}, '%') AND flag = 0")
    List<Product> findByProductName(@Param("productName") String productName);

    /**
     * 插入新产品
     */
    @Insert("INSERT INTO t_product (flag, create_time, create_userid, product_num, product_name, product_img_url, factory_id) " +
            "VALUES (0, NOW(), #{createUserid}, #{productNum}, #{productName}, #{productImgUrl}, #{factoryId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Product product);

    /**
     * 更新产品信息
     */
    @Update("UPDATE t_product SET " +
            "product_name = #{productName}, " +
            "product_img_url = #{productImgUrl}, " +
            "update_time = NOW(), " +
            "update_userid = #{updateUserid} " +
            "WHERE id = #{id}")
    int update(Product product);

    /**
     * 逻辑删除产品（将flag设为1）
     */
    @Update("UPDATE t_product SET flag = 1, update_time = NOW(), update_userid = #{updateUserid} " +
            "WHERE id = #{id}")
    int deleteById(@Param("id") Integer id, @Param("updateUserid") Integer updateUserid);

    @Select("SELECT * FROM t_product WHERE flag = 0 ORDER BY create_time DESC LIMIT #{offset}, #{size}")
    List<Product> findPage(@Param("offset") int offset, @Param("size") int size);

    @Select("SELECT COUNT(*) FROM t_product WHERE flag = 0")
    long count();
}
