package com.example.changshademo.mapper;

import com.example.changshademo.entity.Factory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FactoryMapper {
    @Select("SELECT * FROM t_factory WHERE flag = 0")
    List<Factory> findAll();

    @Select("SELECT * FROM t_factory WHERE id = #{id} AND flag = 0")
    Factory findById(@Param("id") Integer id);

    @Select("SELECT * FROM t_factory WHERE factory_name LIKE CONCAT('%', #{name}, '%') AND flag = 0")
    List<Factory> findByName(@Param("name") String name);
}
