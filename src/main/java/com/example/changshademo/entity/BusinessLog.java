package com.example.changshademo.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BusinessLog {
    private Integer id;
    private Integer userId;
    private String userName;
    private String operation;
    private String module;
    private String detail;
    private LocalDateTime createTime;
}
