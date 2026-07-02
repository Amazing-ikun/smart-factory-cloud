package com.example.changshademo.mapper;

import com.example.changshademo.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {
    @Select("SELECT * FROM t_user WHERE flag = 0")
    List<User> findAll();

    @Select("SELECT * FROM t_user WHERE id = #{id} AND flag = 0")
    User findById(@Param("id") Integer id);

    @Select("SELECT * FROM t_user WHERE user_name = #{userName} AND flag = 0")
    User findByUserName(@Param("userName") String userName);

    @Select("SELECT * FROM t_user WHERE factory_id = #{factoryId} AND flag = 0")
    List<User> findByFactoryId(@Param("factoryId") Integer factoryId);

    @Select("SELECT * FROM t_user WHERE user_status = #{status} AND flag = 0")
    List<User> findByStatus(@Param("status") Integer status);

    @Insert("INSERT INTO t_user (flag, create_time, create_userid, user_name, user_real_name, user_passwd, " +
            "user_job_num, user_phone_num, user_email, role_id, factory_id, user_status) " +
            "VALUES (0, NOW(), #{createUserid}, #{userName}, #{userRealName}, #{userPasswd}, " +
            "#{userJobNum}, #{userPhoneNum}, #{userEmail}, #{roleId}, #{factoryId}, 0)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Update("UPDATE t_user SET user_passwd = #{userPasswd}, update_time = NOW(), update_userid = #{userId} WHERE id = #{id}")
    int updatePassword(@Param("id") Integer id, @Param("userPasswd") String userPasswd, @Param("userId") Integer userId);

    @Update("UPDATE t_user SET user_intro = #{userIntro}, update_time = NOW(), update_userid = #{userId} WHERE id = #{id}")
    int updateIntro(@Param("id") Integer id, @Param("userIntro") String userIntro, @Param("userId") Integer userId);

    @Update("UPDATE t_user SET user_avatar = #{userAvatar}, update_time = NOW(), update_userid = #{userId} WHERE id = #{id}")
    int updateAvatar(@Param("id") Integer id, @Param("userAvatar") String userAvatar, @Param("userId") Integer userId);

    @Update("UPDATE t_user SET user_name = #{userName}, user_real_name = #{userRealName}, role_id = #{roleId}, " +
            "user_status = #{userStatus}, user_job_num = #{userJobNum}, user_phone_num = #{userPhoneNum}, " +
            "user_email = #{userEmail}, factory_id = #{factoryId}, update_time = NOW(), update_userid = #{updateUserid} " +
            "WHERE id = #{id}")
    int updateUser(User user);

    @Update("UPDATE t_user SET flag = 1, update_time = NOW(), update_userid = #{operatorId} WHERE id = #{id}")
    int deleteById(@Param("id") Integer id, @Param("operatorId") Integer operatorId);

    @Update("UPDATE t_user SET role_id = #{roleId}, update_time = NOW(), update_userid = #{operatorId} WHERE id = #{id}")
    int updateRole(@Param("id") Integer id, @Param("roleId") Integer roleId, @Param("operatorId") Integer operatorId);

    @Select("SELECT * FROM t_user WHERE flag = 0 ORDER BY create_time DESC LIMIT #{offset}, #{size}")
    List<User> findPage(@Param("offset") int offset, @Param("size") int size);

    @Select("SELECT COUNT(*) FROM t_user WHERE flag = 0")
    long count();
}
