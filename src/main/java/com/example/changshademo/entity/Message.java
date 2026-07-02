package com.example.changshademo.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Message {
    private Integer id;
    private Integer fromUserId;
    private String fromUserName;
    private Integer toUserId;
    private String title;
    private String content;
    private String msgType;
    private Integer relatedId;
    private Integer isRead;
    private LocalDateTime createTime;
}
