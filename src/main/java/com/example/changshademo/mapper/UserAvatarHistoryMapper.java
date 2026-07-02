package com.example.changshademo.mapper;

import com.example.changshademo.entity.UserAvatarHistory;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserAvatarHistoryMapper {

    @Select("SELECT * FROM t_user_avatar_history WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<UserAvatarHistory> findByUserId(@Param("userId") Integer userId);

    @Insert("INSERT INTO t_user_avatar_history (user_id, avatar_url) VALUES (#{userId}, #{avatarUrl})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserAvatarHistory history);

    @Delete("DELETE FROM t_user_avatar_history WHERE id = #{id}")
    int deleteById(@Param("id") Integer id);
}
