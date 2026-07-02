package com.example.changshademo.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserAvatarHistory {
    private Integer id;
    private Integer userId;
    private String avatarUrl;
    private LocalDateTime createTime;
}
