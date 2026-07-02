package com.example.changshademo.mapper;

import com.example.changshademo.entity.EquipmentProduct;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface EquipmentProductMapper {
    @Select("SELECT * FROM t_equipment_product WHERE flag = 0")
    List<EquipmentProduct> findAll();

    @Select("SELECT * FROM t_equipment_product WHERE id = #{id} AND flag = 0")
    EquipmentProduct findById(@Param("id") Integer id);

    @Select("SELECT * FROM t_equipment_product WHERE equipment_id = #{equipmentId} AND flag = 0")
    List<EquipmentProduct> findByEquipmentId(@Param("equipmentId") Integer equipmentId);

    @Select("SELECT * FROM t_equipment_product WHERE product_id = #{productId} AND flag = 0")
    List<EquipmentProduct> findByProductId(@Param("productId") Integer productId);

    @Select("SELECT * FROM t_equipment_product WHERE factory_id = #{factoryId} AND flag = 0")
    List<EquipmentProduct> findByFactoryId(@Param("factoryId") Integer factoryId);

    @Insert("INSERT INTO t_equipment_product (flag, create_time, create_userid, equipment_id, product_id, yield, unit, factory_id) " +
            "VALUES (0, NOW(), #{createUserid}, #{equipmentId}, #{productId}, #{yield}, #{unit}, #{factoryId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(EquipmentProduct ep);

    @Update("UPDATE t_equipment_product SET flag = 1, update_time = NOW(), update_userid = #{updateUserid} WHERE id = #{id}")
    int deleteById(@Param("id") Integer id, @Param("updateUserid") Integer updateUserid);
}
