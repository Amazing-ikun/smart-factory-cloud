package com.example.changshademo.mapper;

import com.example.changshademo.entity.BusinessLog;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BusinessLogMapper {

    @Select("SELECT * FROM t_business_log ORDER BY create_time DESC LIMIT #{offset}, #{size}")
    List<BusinessLog> findPage(@Param("offset") int offset, @Param("size") int size);

    @Select("SELECT COUNT(*) FROM t_business_log")
    long count();

    @Insert("INSERT INTO t_business_log (user_id, user_name, operation, module, detail, create_time) " +
            "VALUES (#{userId}, #{userName}, #{operation}, #{module}, #{detail}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(BusinessLog businessLog);
}
