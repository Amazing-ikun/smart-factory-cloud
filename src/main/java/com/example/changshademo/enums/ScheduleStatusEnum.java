package com.example.changshademo.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 调度工单状态枚举
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ScheduleStatusEnum {
    NOT_STARTED(10, "未开始"),
    IN_PRODUCTION(20, "生产中"),
    COMPLETED(30, "已完成");

    private final Integer code;
    private final String description;

    ScheduleStatusEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据code获取枚举
     */
    public static ScheduleStatusEnum fromCode(Integer code) {
        for (ScheduleStatusEnum status : ScheduleStatusEnum.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 判断状态是否可转为目标状态
     */
    public boolean canTransitionTo(ScheduleStatusEnum targetStatus) {
        // 已完成是终态，不能再变更
        if (this == COMPLETED) {
            return false;
        }
        // 未开始 -> 生产中
        if (this == NOT_STARTED) {
            return targetStatus == IN_PRODUCTION;
        }
        // 生产中 -> 已完成
        if (this == IN_PRODUCTION) {
            return targetStatus == COMPLETED;
        }
        return false;
    }
}
