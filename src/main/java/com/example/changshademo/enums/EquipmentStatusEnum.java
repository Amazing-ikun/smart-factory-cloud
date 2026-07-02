package com.example.changshademo.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum EquipmentStatusEnum {
    ENABLED(10, "启用"),
    DISABLED(20, "停用"),
    FAULT(30, "故障");

    private final Integer code;
    private final String description;

    EquipmentStatusEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() { return code; }
    public String getDescription() { return description; }

    public static EquipmentStatusEnum fromCode(Integer code) {
        for (EquipmentStatusEnum status : values()) {
            if (status.getCode().equals(code)) return status;
        }
        return null;
    }
}
