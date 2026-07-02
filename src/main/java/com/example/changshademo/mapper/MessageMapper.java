package com.example.changshademo.mapper;

import com.example.changshademo.entity.Message;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MessageMapper {

    @Select("SELECT * FROM t_message WHERE to_user_id = #{userId} ORDER BY create_time DESC LIMIT #{offset}, #{size}")
    List<Message> findByUserId(@Param("userId") Integer userId, @Param("offset") int offset, @Param("size") int size);

    @Select("SELECT COUNT(*) FROM t_message WHERE to_user_id = #{userId}")
    long countByUserId(@Param("userId") Integer userId);

    @Select("SELECT COUNT(*) FROM t_message WHERE to_user_id = #{userId} AND is_read = 0")
    long countUnread(@Param("userId") Integer userId);

    @Insert("INSERT INTO t_message (from_user_id, from_user_name, to_user_id, title, content, msg_type, related_id, is_read, create_time) " +
            "VALUES (#{fromUserId}, #{fromUserName}, #{toUserId}, #{title}, #{content}, #{msgType}, #{relatedId}, 0, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Message message);

    @Update("UPDATE t_message SET is_read = 1 WHERE id = #{id}")
    int markAsRead(@Param("id") Integer id);

    @Select("SELECT * FROM t_message WHERE id = #{id}")
    Message findById(@Param("id") Integer id);
}
