package com.example.changshademo.mapper;

import com.example.changshademo.entity.Equipment;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface EquipmentMapper {
    @Select("SELECT * FROM t_equipment WHERE flag = 0")
    List<Equipment> findAll();

    @Select("SELECT * FROM t_equipment WHERE id = #{id} AND flag = 0")
    Equipment findById(@Param("id") Integer id);

    @Select("SELECT * FROM t_equipment WHERE factory_id = #{factoryId} AND flag = 0")
    List<Equipment> findByFactoryId(@Param("factoryId") Integer factoryId);

    @Select("SELECT * FROM t_equipment WHERE equipment_status = #{status} AND flag = 0")
    List<Equipment> findByStatus(@Param("status") Integer status);

    @Insert("INSERT INTO t_equipment (flag, create_time, create_userid, equipment_seq, equipment_name, equipment_img_url, equipment_status, factory_id) " +
            "VALUES (0, NOW(), #{createUserid}, #{equipmentSeq}, #{equipmentName}, #{equipmentImgUrl}, #{equipmentStatus}, #{factoryId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Equipment equipment);

    @Update("UPDATE t_equipment SET equipment_seq = #{equipmentSeq}, equipment_name = #{equipmentName}, equipment_status = #{equipmentStatus}, " +
            "factory_id = #{factoryId}, update_time = NOW(), update_userid = #{updateUserid} WHERE id = #{id}")
    int update(Equipment equipment);

    @Update("UPDATE t_equipment SET equipment_img_url = #{equipmentImgUrl}, update_time = NOW(), update_userid = #{updateUserid} WHERE id = #{id}")
    int updateImage(@Param("id") Integer id, @Param("equipmentImgUrl") String equipmentImgUrl, @Param("updateUserid") Integer updateUserid);

    @Update("UPDATE t_equipment SET flag = 1, update_time = NOW(), update_userid = #{updateUserid} WHERE id = #{id}")
    int deleteById(@Param("id") Integer id, @Param("updateUserid") Integer updateUserid);

    @Select("SELECT * FROM t_equipment WHERE flag = 0 ORDER BY create_time DESC LIMIT #{offset}, #{size}")
    List<Equipment> findPage(@Param("offset") int offset, @Param("size") int size);

    @Select("SELECT COUNT(*) FROM t_equipment WHERE flag = 0")
    long count();
}
