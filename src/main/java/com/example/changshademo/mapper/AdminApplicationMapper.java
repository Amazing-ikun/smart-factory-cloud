package com.example.changshademo.mapper;

import com.example.changshademo.entity.AdminApplication;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AdminApplicationMapper {

    @Select("SELECT * FROM t_admin_application ORDER BY create_time DESC LIMIT #{offset}, #{size}")
    List<AdminApplication> findPage(@Param("offset") int offset, @Param("size") int size);

    @Select("SELECT COUNT(*) FROM t_admin_application")
    long count();

    @Select("SELECT * FROM t_admin_application WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<AdminApplication> findByUserId(@Param("userId") Integer userId);

    @Insert("INSERT INTO t_admin_application (user_id, user_name, user_real_name, reason, commitment, " +
            "phone, email, status, create_time) " +
            "VALUES (#{userId}, #{userName}, #{userRealName}, #{reason}, #{commitment}, " +
            "#{phone}, #{email}, 0, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(AdminApplication application);

    @Update("UPDATE t_admin_application SET status = #{status}, reply_content = #{replyContent}, " +
            "reply_time = NOW(), reply_user_id = #{replyUserId}, reply_user_name = #{replyUserName} WHERE id = #{id}")
    int updateReply(AdminApplication application);

    @Select("SELECT * FROM t_admin_application WHERE id = #{id}")
    AdminApplication findById(@Param("id") Integer id);
}
