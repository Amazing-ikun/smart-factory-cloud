package com.example.changshademo.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum PlanStatusEnum {
    NOT_STARTED(10, "未启动"),
    STARTED(20, "已启动"),
    COMPLETED(30, "已完成");

    private final Integer code;
    private final String description;

    PlanStatusEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() { return code; }
    public String getDescription() { return description; }

    public static PlanStatusEnum fromCode(Integer code) {
        for (PlanStatusEnum status : values()) {
            if (status.getCode().equals(code)) return status;
        }
        return null;
    }
}
