package com.example.changshademo.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Feedback {
    private Integer id;
    private Integer userId;
    private String userName;
    private String content;
    private String attachments;
    private String feedbackType;
    private LocalDateTime createTime;
    private String replyContent;
    private LocalDateTime replyTime;
    private Integer replyUserId;
    private String replyUserName;
}
