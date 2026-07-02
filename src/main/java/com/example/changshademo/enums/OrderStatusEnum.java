package com.example.changshademo.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 订单状态枚举
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum OrderStatusEnum {

    UNACCEPTED(10, "未接单"),
    ACCEPTED(20, "已接单"),
    REJECTED(30, "已拒绝"),
    IN_PRODUCTION(40, "生产中"),
    COMPLETED(50, "订单完成");

    private final Integer code;
    private final String description;

    OrderStatusEnum(Integer code, String description) {
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
    public static OrderStatusEnum fromCode(Integer code) {
        for (OrderStatusEnum status : OrderStatusEnum.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 判断状态是否可转为目标状态
     */
    public boolean canTransitionTo(OrderStatusEnum targetStatus) {
        // 业务规则：已拒绝和已完成是终态，不能再变更
        if (this == REJECTED || this == COMPLETED) {
            return false;
        }
        // 未接单 -> 已接单/已拒绝
        if (this == UNACCEPTED) {
            return targetStatus == ACCEPTED || targetStatus == REJECTED;
        }
        // 已接单 -> 生产中
        if (this == ACCEPTED) {
            return targetStatus == IN_PRODUCTION;
        }
        // 生产中 -> 订单完成
        if (this == IN_PRODUCTION) {
            return targetStatus == COMPLETED;
        }
        return false;
    }
}
