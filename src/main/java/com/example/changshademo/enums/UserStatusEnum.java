package com.example.changshademo.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum UserStatusEnum {
    NORMAL(0, "正常"),
    LOCKED(1, "锁定");

    private final Integer code;
    private final String description;

    UserStatusEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() { return code; }
    public String getDescription() { return description; }

    public static UserStatusEnum fromCode(Integer code) {
        for (UserStatusEnum status : values()) {
            if (status.getCode().equals(code)) return status;
        }
        return null;
    }
}
