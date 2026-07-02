package com.example.changshademo.mapper;

import com.example.changshademo.entity.Feedback;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FeedbackMapper {

    @Select("SELECT * FROM t_feedback ORDER BY create_time DESC LIMIT #{offset}, #{size}")
    List<Feedback> findPage(@Param("offset") int offset, @Param("size") int size);

    @Select("SELECT COUNT(*) FROM t_feedback")
    long count();

    @Select("SELECT * FROM t_feedback WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<Feedback> findByUserId(@Param("userId") Integer userId);

    @Insert("INSERT INTO t_feedback (user_id, user_name, content, attachments, feedback_type, create_time) " +
            "VALUES (#{userId}, #{userName}, #{content}, #{attachments}, #{feedbackType}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Feedback feedback);

    @Update("UPDATE t_feedback SET reply_content = #{replyContent}, reply_time = NOW(), " +
            "reply_user_id = #{replyUserId}, reply_user_name = #{replyUserName} WHERE id = #{id}")
    int updateReply(Feedback feedback);

    @Select("SELECT * FROM t_feedback WHERE id = #{id}")
    Feedback findById(@Param("id") Integer id);
}
