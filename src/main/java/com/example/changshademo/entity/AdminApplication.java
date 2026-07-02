package com.example.changshademo.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AdminApplication {
    private Integer id;
    private Integer userId;
    private String userName;
    private String userRealName;
    private String reason;
    private String commitment;
    private String phone;
    private String email;
    private Integer status;
    private LocalDateTime createTime;
    private String replyContent;
    private LocalDateTime replyTime;
    private Integer replyUserId;
    private String replyUserName;
}
