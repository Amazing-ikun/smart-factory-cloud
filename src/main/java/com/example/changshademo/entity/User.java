package com.example.changshademo.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    /**
     * 主键 ID
     */
    private Integer id;

    /**
     * 有效标识
     * 0：有效
     * 1：无效
     */
    private Integer flag;

    /**
     * 注册时间
     */
    private LocalDateTime createTime;

    /**
     * 创建人 ID
     */
    private Integer createUserid;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;

    /**
     * 修改人 ID
     */
    private Integer updateUserid;

    /**
     * 用户状态
     * 0：正常
     * 1：锁定
     */
    private Integer userStatus;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户姓名
     */
    private String userRealName;

    /**
     * 用户密码
     */
    private String userPasswd;

    /**
     * 用户工号
     */
    private String userJobNum;

    /**
     * 用户手机号
     */
    private String userPhoneNum;

    /**
     * 用户邮箱
     */
    private String userEmail;

    /**
     * 角色 ID
     */
    private Integer roleId;

    /**
     * 工厂 ID
     */
    private Integer factoryId;

    /**
     * 用户简介
     */
    private String userIntro;

    /**
     * 用户头像 URL
     */
    private String userAvatar;
}
